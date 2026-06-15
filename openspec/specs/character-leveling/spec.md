# character-leveling Specification

## Purpose

Let the user advance a saved character to the next level and apply the level-up
choices (hit points, subclass, ability score improvements or feats, and new
spells). This is not an explicit Pflichtenheft 1.4.1 use case but extends
"Charakter verwalten" and the goal of a complete, beginner-guided character.

This spec describes the BASELINE behavior already implemented.

## Requirements

### Requirement: Open level-up for a character

The system SHALL open a level-up flow for a specific saved character, showing the
new level and the choices that apply at that level for the character's class.

#### Scenario: Starting a level-up

- **WHEN** the user starts Level Up from a character's sheet
- **THEN** the level-up flow opens targeting the next level for that character

### Requirement: Hit point increase

The system SHALL determine the new maximum hit points for the gained level and
persist the updated max HP.

#### Scenario: HP updated on level-up

- **WHEN** a level-up is confirmed
- **THEN** the character's max HP is updated in storage

### Requirement: Subclass choice at the appropriate level

The system SHALL let the user choose a subclass from the class's available
subclasses when the new level grants one, and persist the choice.

#### Scenario: Choosing a subclass

- **WHEN** the new level grants a subclass and the user selects one
- **THEN** the subclass is stored on the character

### Requirement: Ability Score Improvement or feat at ASI levels

The system SHALL, at ASI levels for the class, let the user either raise ability
scores or take a feat (mutually exclusive). Applied ability increases SHALL be
capped at 20 and persisted to the character's stats.

#### Scenario: Applying an ASI

- **WHEN** the user chooses ability increases at an ASI level
- **THEN** the chosen abilities are increased (capped at 20) and persisted

#### Scenario: Feat instead of ASI

- **WHEN** the user chooses a feat at an ASI level
- **THEN** no ability score increase is applied for that level

### Requirement: New spells and cantrips for casters

The system SHALL let spellcasting characters learn new spells/cantrips allowed at
the new level and, where the class permits, replace a known spell. New selections
SHALL be persisted and replaced spells removed.

#### Scenario: Learning new spells

- **WHEN** a caster confirms new spell/cantrip selections at level-up
- **THEN** the new spells are added to the character and any replaced spell is removed

### Requirement: Persist and log the level-up

The system SHALL persist the new level and record the level-up event for the
character.

#### Scenario: Level recorded

- **WHEN** a level-up is confirmed
- **THEN** the character's level is updated and a level-up entry is recorded
