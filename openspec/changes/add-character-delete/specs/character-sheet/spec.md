## REMOVED Requirements

### Requirement: Delete action present but not yet functional

**Reason**: Deletion is now implemented; the stub is replaced by a working delete action.
**Migration**: None — the same Delete control is reused; it now performs a confirmed deletion instead of doing nothing.

## ADDED Requirements

### Requirement: Delete a character from the sheet

The system SHALL provide a Delete action on the character sheet that permanently
removes the character after the user explicitly confirms. (Pflichtenheft 1.4.1
"Charakter löschen".) For a first-time user this prevents accidental loss by
requiring confirmation before anything is removed.

#### Scenario: Confirming a deletion

- **WHEN** the user clicks Delete on a character's sheet and confirms the prompt
- **THEN** the character and all its related data are permanently removed and the sheet closes

#### Scenario: Cancelling a deletion

- **WHEN** the user clicks Delete but cancels the confirmation prompt
- **THEN** nothing is deleted and the sheet remains open
