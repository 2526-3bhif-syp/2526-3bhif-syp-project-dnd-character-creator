## ADDED Requirements

### Requirement: Permanently delete a character

The system SHALL permanently delete a character (identified by its id) together
with all of its related data — ability stats, skill proficiencies, equipment, and
spells — as a single atomic operation, so no orphaned rows remain. (Pflichtenheft
1.4.1 "Charakter löschen".)

#### Scenario: Deleting a character removes all related data

- **WHEN** a character is deleted by its id
- **THEN** the character record and its stats, skills, equipment, and spells are all removed

#### Scenario: Deletion is atomic

- **WHEN** deletion of any related table fails
- **THEN** no rows are deleted and the character remains intact

### Requirement: Portrait file retained on deletion

When a character is deleted, the system SHALL remove only database rows; any copied
portrait image file on disk SHALL be left in place.

#### Scenario: Portrait file is not removed

- **WHEN** a character with a copied portrait is deleted
- **THEN** its database rows are removed and the portrait image file remains on disk
