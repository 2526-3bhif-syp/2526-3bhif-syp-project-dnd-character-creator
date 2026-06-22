## Why

Pflichtenheft 1.4.1 "Charakter exportieren" requires that a finished character can
be taken out of the app as a shareable, printable file. Today a character only
lives inside the app's SQLite database and the on-screen sheet popup — there is no
way to print it, bring it to a table-top session, or hand it to a Dungeon Master.
For a first-time player, a real D&D-style character sheet on paper is also the most
recognizable, reassuring final artifact the app can produce.

## What Changes

- Add a new **character-export** capability that renders a saved character to a
  **PDF formatted like the official D&D 5e character sheet** and writes it to a
  user-chosen location on the local filesystem.
- The PDF includes all character data already shown on the sheet: name, race,
  class, subclass, level, alignment, background, ability scores (with derived
  modifiers and proficiency bonus), skills, equipment, weapon attacks, known
  spells/cantrips, and the four free-text background fields (personality traits,
  ideals, bonds, flaws). The chosen portrait is embedded where the sheet has a
  character image.
- Add an **Export** action to the **character sheet popup**, next to Edit / Level
  Up / Delete. (No export button exists in the code today despite earlier
  assumptions — it is added by this change.)
- Add an **Export** action to the **creation Summary step**, so a brand-new
  character can be exported immediately after it is saved.
- Add a PDF-generation library dependency to `pom.xml` and declare it in
  `module-info.java`.

## Capabilities

### New Capabilities
- `character-export`: Render a saved character to an official-style D&D 5e PDF
  sheet containing all of its data, and save the file to a user-chosen location.

### Modified Capabilities
- `character-sheet`: Add an Export action on the sheet that exports the open
  character to PDF.
- `character-creation`: Add an Export action on the Summary step so a
  just-created character can be exported right after saving.

## Impact

- **New dependency:** a PDF library in `pom.xml` (library choice decided in
  design.md) plus a corresponding `requires` in `module-info.java`.
- **New code:** an exporter in a new package (e.g. `com.dnd.creator.export`)
  driven by a presenter; a bundled official-style sheet template asset under
  `src/main/resources`.
- **Modified views:** `CharacterSheetPopupView` (+ its FXML) and
  `CharacterSummaryView` (+ its FXML) gain an Export button and a file-save dialog.
- **No schema or persistence change:** export is read-only over the existing
  `CharacterModel` / `DbManager` data; no new tables or columns.
- **No new game data** is hardcoded — all character values come from the existing
  model loaded from SQLite.
