## ADDED Requirements

### Requirement: Export action

The system SHALL provide an "Export" action on the character sheet that exports the
currently open character to a PDF sheet (see `character-export`). (Pflichtenheft
1.4.1 "Charakter exportieren".) The action sits alongside the existing Edit, Level
Up, and Delete actions.

#### Scenario: Exporting from the sheet

- **GIVEN** a character's sheet is open
- **WHEN** the user clicks Export
- **THEN** the export flow runs for that character and, on success, a PDF is saved to the user's chosen location

#### Scenario: Export does not alter the character

- **GIVEN** a character's sheet is open
- **WHEN** the user clicks Export and the PDF is generated
- **THEN** the character's stored data is unchanged and the sheet remains open
