## 1. Dependency & module setup

- [x] 1.1 Add Apache PDFBox 3.x to `pom.xml` dependencies
- [x] 1.2 Add the matching `requires org.apache.pdfbox;` (and any transitive module) to `module-info.java`
- [x] 1.3 Verify `mvn clean package` and `mvn exec:java -Dexec.mainClass="com.dnd.creator.Main"` both still run with the module path intact

## 2. Sheet drawing primitives

- [x] 2.1 Add PDFBox helper methods in the exporter for drawing a labeled box, a section title, a line of text, and an ability column (reused across sections)
- [x] 2.2 Define page geometry (A4, margins, three-column grid) as constants in one place
- [x] 2.3 Use PDFBox Standard-14 fonts (Helvetica/Times) so no external font asset is needed

## 3. Layout map

- [x] 3.1 Lay out the header (name, race, class+subclass, level, alignment, background, portrait box) and the three columns (abilities/saves/skills, combat/attacks/spells, equipment/languages/personality) using the geometry constants
- [x] 3.2 Define per-field max width / wrap behavior and which lists (equipment, spells, skills) flow downward or to a second page

## 4. Exporter (com.dnd.creator.export)

- [x] 4.1 Create `CharacterPdfExporter` with `export(CharacterModel, File)` that creates a PDDocument, adds an A4 page, and opens a content stream
- [x] 4.2 Stamp all scalar fields (name, race, class, subclass, level, alignment, background) using the coordinate map
- [x] 4.3 Stamp ability scores with derived modifiers and proficiency bonus, reusing the same derivation the sheet uses (`getProficiencyBonus()` etc.)
- [x] 4.4 Stamp skills, equipment, and weapon attacks (attack bonus + damage), wrapping/overflowing per the layout rules
- [x] 4.5 Stamp known spells and cantrips; render an empty/omitted spell section for non-spellcasters (no invented data)
- [x] 4.6 Stamp the four free-text background fields (personality traits, ideals, bonds, flaws)
- [x] 4.7 Embed the portrait as a `PDImageXObject`; skip the image gracefully if missing or the default placeholder
- [x] 4.8 Save the PDF to the target file and close all PDFBox resources

## 5. Presenter & save flow

- [x] 5.1 Create `ExportPresenter` that opens a JavaFX `FileChooser` defaulting to `<character name>.pdf`
- [x] 5.2 On a chosen path, call `CharacterPdfExporter` and show a plain-language success confirmation
- [x] 5.3 On cancel, write nothing and return to the previous state
- [x] 5.4 On write/generation failure, show a clear plain-language error and leave no partial file

## 6. Wire the trigger points (passive views)

- [x] 6.1 Add an Export button to `CharacterSheetPopup.fxml` next to Edit / Level Up / Delete and an `@FXML` field in `CharacterSheetPopupView`
- [x] 6.2 Wire the sheet's Export button to `ExportPresenter` with the open `CharacterModel`; confirm the sheet stays open and the character is unchanged
- [x] 6.3 Add an Export button to `CharacterSummaryView.fxml` and wire it in `CharacterSummaryView` to export the just-saved character via `ExportPresenter`

## 7. Verification

- [x] 7.1 Export a fully-populated spellcaster and confirm every value from the on-screen sheet appears in the correct PDF section
- [x] 7.2 Export a non-spellcaster and a character with no portrait; confirm graceful handling (no spells, no image, no crash)
- [x] 7.3 Export a max-content character (long equipment/spell lists) and confirm no text overflows its box illegibly
- [x] 7.4 Confirm the save dialog cancel path and a write-failure path behave per spec
