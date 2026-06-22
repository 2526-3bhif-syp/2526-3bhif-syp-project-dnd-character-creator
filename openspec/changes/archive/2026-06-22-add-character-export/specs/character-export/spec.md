## ADDED Requirements

### Requirement: Export a character to a PDF sheet

The system SHALL render a saved character to a PDF document laid out like the
official D&D 5e character sheet. (Pflichtenheft 1.4.1 "Charakter exportieren".)
The user triggers the export for one selected character and the system produces a
single PDF file. For a first-time player, this yields a familiar, printable sheet
they can take to a table-top session without needing the app.

#### Scenario: Exporting a saved character

- **GIVEN** a saved character is open or selected
- **WHEN** the user triggers Export
- **THEN** the system generates a PDF formatted like the official 5e character sheet for that character

### Requirement: PDF contains all character data

The exported PDF SHALL contain all of the character's data: name, race, class,
subclass, level, alignment, background, ability scores with their derived modifiers
and proficiency bonus, skill proficiencies, equipment, weapon attacks (with attack
bonus and damage), known spells and cantrips, and the four free-text background
fields (personality traits, ideals, bonds, flaws). The chosen portrait SHALL be
embedded where the sheet shows a character image. All values SHALL come from the
character's stored data, never hardcoded.

#### Scenario: Complete data on the sheet

- **GIVEN** a character with stats, skills, equipment, weapon attacks, spells, and background text
- **WHEN** the PDF is generated
- **THEN** every one of those values appears on the PDF in the corresponding section of the sheet

#### Scenario: Spellcaster shows spells, non-caster omits them

- **GIVEN** a non-spellcasting character with no known spells
- **WHEN** the PDF is generated
- **THEN** the spell section is empty or omitted and no spell data is invented

### Requirement: Save the exported file to the filesystem

The system SHALL let the user choose where to save the exported PDF on the local
filesystem and SHALL write the file to that location. The system SHALL confirm
success or report a clear, plain-language error if writing fails. (Single-user,
local-desktop scope — no upload, sharing service, or network involved.)

#### Scenario: Choosing a save location

- **GIVEN** the user has triggered an export
- **WHEN** the system prompts for a destination and the user picks a folder and file name
- **THEN** the PDF is written there and the system confirms the file was saved

#### Scenario: Cancelling the save dialog

- **GIVEN** the save-location prompt is open
- **WHEN** the user cancels
- **THEN** no file is written and the app returns to its previous state

#### Scenario: Write failure is reported

- **GIVEN** the chosen location cannot be written (e.g. no permission)
- **WHEN** the system attempts to save the PDF
- **THEN** a clear plain-language error is shown and no partial file is left behind
