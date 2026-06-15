# character-persistence Specification

## Purpose

Save created characters and load them back so they survive across sessions —
underpinning Pflichtenheft 1.4 ("einen erstellten Charakter speichern") and the
overview/sheet/edit/level-up capabilities. This is the cross-cutting storage
contract for user-created characters (distinct from `reference-data`, which holds
the read-only rules data). Scope stays single-user and local (Pflichtenheft 1.6).

This spec describes the BASELINE behavior already implemented.

## Requirements

### Requirement: Persist a complete character

The system SHALL persist a character together with its related data: ability
stats, skill proficiencies, equipment (mandatory plus chosen options), and
spells/cantrips. A character SHALL be persisted only if it has a race, class, and
background.

#### Scenario: Saving a valid character

- **WHEN** a character with race, class, and background is saved
- **THEN** the character and its stats, skills, equipment, and spells are written to storage

#### Scenario: Rejecting an incomplete character

- **WHEN** a character is missing race, class, or background
- **THEN** it is not written to storage and the save reports failure

### Requirement: Persist portrait images locally

The system SHALL copy a chosen portrait image into local application storage so it
remains available independent of the original file location; the placeholder
portrait SHALL be left as-is.

#### Scenario: Saving a portrait

- **WHEN** a character with a chosen image file is saved
- **THEN** the image is copied into local storage and the stored path points to the copy

### Requirement: Load all saved characters

The system SHALL load all saved characters with their full related data (stats,
race, class, alignment, level, subclass, background free-text fields, skills,
equipment, spells, cantrips, and derived weapon attacks), ordered most-recent
first.

#### Scenario: Loading characters

- **WHEN** the overview requests saved characters
- **THEN** every saved character is returned fully populated, newest first

### Requirement: Update an existing character

The system SHALL update an existing character in place (identified by its id),
replacing its related stats, skills, equipment, and spells, rather than inserting a
duplicate.

#### Scenario: Updating in place

- **WHEN** a character that already has an id is saved
- **THEN** its record and related data are updated, not duplicated

### Requirement: Persist background free-text fields independently

The system SHALL allow the four background free-text fields (personality traits,
ideals, bonds, flaws) to be updated for an existing character without going through
the full creation flow.

#### Scenario: Updating background fields from the sheet

- **WHEN** background fields are edited on the character sheet
- **THEN** only those fields are updated for that character in storage

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
