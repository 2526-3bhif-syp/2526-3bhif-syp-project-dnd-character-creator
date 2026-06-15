# character-editing Specification

## Purpose

Let the user modify an already-saved character. Serves Pflichtenheft use case
"Charakter bearbeiten" (1.4.1, via "Charakter verwalten"). Editing reuses the
guided creation flow rather than a separate UI.

This spec describes the BASELINE behavior already implemented.

## Requirements

### Requirement: Load existing character into the creation flow

The system SHALL load the selected character (all stored fields and selections)
into the creation session and open the creation flow so the user can change any
step.

#### Scenario: Editing from the sheet

- **WHEN** the user chooses Edit on a character's sheet
- **THEN** the creation flow opens pre-populated with that character's data

### Requirement: Preselect existing choices on each step

The system SHALL pre-select the character's existing choices when editing: race,
class, ability scores, skills, equipment (including resolved "choose one"
options), alignment, and selected cantrips/spells.

#### Scenario: Skills preselected

- **WHEN** editing a character that has skill proficiencies
- **THEN** those skills are shown as already selected on the skills step

#### Scenario: Equipment and cantrips preserved

- **WHEN** editing reaches the equipment and spell steps
- **THEN** previously chosen gear and cantrips are restored and not reset

### Requirement: Update in place on save

The system SHALL update the existing database record (identified by its id) on
save rather than creating a duplicate, replacing the character's related stats,
skills, equipment, and spells with the edited selections.

#### Scenario: Saving an edited character

- **WHEN** the user saves a character that already has a database id
- **THEN** the existing record is updated and its related stats, skills, equipment, and spells are replaced with the new selections

#### Scenario: No duplicate created

- **WHEN** an existing character is edited and saved
- **THEN** the overview shows the same character updated, not a second copy
