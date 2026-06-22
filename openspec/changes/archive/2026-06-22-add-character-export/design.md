## Context

The app is a single-user JavaFX desktop tool (Java 21, JavaFX 21, SQLite via
sqlite-jdbc) following MVP. A character is held in `CharacterModel` and loaded from
SQLite through `DbManager` (`getAllSavedCharacters()` already returns fully
populated models, including stats, skills, equipment, weapon attacks, spells, and
the four background fields). The character sheet popup (`CharacterSheetPopupView`)
already renders all of this on screen and hosts Edit / Level Up / Delete actions;
the creation Summary step (`CharacterSummaryView`) renders the same data for a
just-saved character.

There is currently **no PDF code and no PDF dependency** in `pom.xml`, and
`module-info.java` (JPMS) is in use, so any library must resolve as a Java module.
The requirement is an **official-style 5e sheet**, which means precise placement of
many values into a fixed layout — not free-flowing text.

## Goals / Non-Goals

**Goals:**
- Produce a PDF that visually resembles the official D&D 5e character sheet and
  contains every value the on-screen sheet shows.
- Trigger export from two places: the character sheet popup and the creation
  Summary step.
- Let the user pick the save location; confirm success, report failures clearly.
- Keep export read-only — no schema, persistence, or game-data changes.

**Non-Goals:**
- No multi-character / batch export, no printing dialog, no cloud upload or sharing.
- No editable/interactive PDF form fields (export is a flat, printable sheet).
- No new character data — only what already exists in `CharacterModel`.
- No spell-description appendix or rules text in v1 (sheet data only).

## Decisions

### Decision: Use Apache PDFBox and draw the official-style sheet programmatically

Render the sheet entirely in code with Apache PDFBox — draw the boxes, section
labels, ability columns, and stamp values at computed coordinates onto a blank A4
page. PDFBox 3.x is well suited: `PDPageContentStream` draws rectangles, lines and
text, embeds the portrait (`PDImageXObject`), and ships the Standard-14 fonts
(Helvetica/Times) so no external font asset is needed. It is a proper named JPMS
module (`org.apache.pdfbox`) and Apache-2.0 (no copyleft concerns).

- **Alternative — overlay onto a bundled template PDF:** originally preferred, but
  it requires shipping a *license-clean* official-style sheet PDF, which is not
  available in this environment (the official WotC sheet is copyrighted and no
  vetted community asset is on hand). Drawing programmatically removes the asset
  dependency and the licensing risk entirely, at the cost of more drawing code.
- **Alternative — OpenPDF (high-level tables/paragraphs):** great for a *custom*
  flowing layout, but reproducing the official sheet's fixed grid with its
  paragraph/table model is awkward and fragile, plus LGPL/MPL + automatic-module
  friction under JPMS. PDFBox's coordinate drawing fits the fixed-grid goal better.

### Decision: Centralize layout in a coordinate/box helper

Keep all geometry (page margins, the three-column grid, each box's x/y/width/height
and label) in one place inside the exporter so layout tweaks stay isolated and the
field-stamping logic stays data-driven. No external template resource is bundled.

### Decision: New `com.dnd.creator.export` package, driven by a presenter (MVP)

- `CharacterPdfExporter` (export package): pure function
  `export(CharacterModel, OutputStream/File)` — no UI, no DB. Reuses the same
  derived-value helpers the sheet uses (ability modifiers,
  `getProficiencyBonus()`, weapon attack bonus/damage) so the PDF and the on-screen
  sheet stay consistent.
- `ExportPresenter` (presenter package): handles the action — opens a JavaFX
  `FileChooser` (default name `<character name>.pdf`), calls the exporter, and
  reports success/failure via an alert. Views stay passive: the sheet popup and
  Summary view just expose an Export button and delegate to the presenter.
- Both trigger points (`CharacterSheetPopupView`, `CharacterSummaryView`) call the
  same presenter with the relevant `CharacterModel`, so there is one export path.

### Decision: Data source is the in-memory `CharacterModel`

Both trigger points already hold a fully populated `CharacterModel` (the sheet
loaded it via `getAllSavedCharacters()`; the Summary step has the session model
just saved). No new `DbManager` query is required for v1. If a future caller only
has an id, a thin `DbManager.getCharacterById(...)` lookup can be added, but it is
out of scope now.

## Risks / Trade-offs

- **Coordinate drift / overflow** (long equipment or spell lists overrunning their
  box) → cap/auto-shrink text per field, wrap within the box, and overflow long
  lists to a continuation area or second page; verify with a max-content character.
- **JPMS resolution of PDFBox and its FontBox/commons dependency** → add the correct
  `requires` entries; verify `mvn clean package` and `mvn exec:java` both run with
  the module path intact. PDFBox 3.x font subsystem may need its bundled fonts —
  test glyph rendering for the chosen sheet font.
- **Portrait formats** → portraits are PNG/JPEG on the local FS; guard against a
  missing/old `imagePath` (e.g. the default `placeholder.png`) by skipping the image
  rather than failing the whole export.
- **Effort** (this is the largest unbuilt feature, BACKLOG #3, effort 5) → the
  coordinate map is the bulk of the work; keep it data-driven and test incrementally
  field-by-field.

## Migration Plan

Additive only — new package, new dependency, two new buttons. No DB migration, no
change to existing saved characters. Rollback = remove the buttons, the export
package, the template resource, and the `pom.xml` / `module-info.java` additions.

## Open Questions

- One page or two? The official sheet is multi-page; v1 can target page 1 (core
  sheet) and add the spellcasting page only if the character is a caster.
- Exact template source/license to bundle (community sheet vs. self-drawn).
- Should export be disabled for the default/placeholder portrait, or just skip the
  image? (Leaning: skip the image, still export.)
