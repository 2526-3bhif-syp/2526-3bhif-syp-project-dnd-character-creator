## ADDED Requirements

### Requirement: Export from the Summary step

The system SHALL provide an Export action on the creation Summary step so a
just-created character can be exported to PDF immediately after it is saved (see
`character-export`). (Pflichtenheft 1.4.1 "Charakter exportieren".) This lets a
first-time player walk away from the creation flow with a finished, printable sheet
in one sitting.

#### Scenario: Exporting a freshly created character

- **GIVEN** the user has completed creation and saved the character on the Summary step
- **WHEN** the user clicks Export on that step
- **THEN** the export flow runs for the newly saved character and, on success, a PDF is saved to the user's chosen location
