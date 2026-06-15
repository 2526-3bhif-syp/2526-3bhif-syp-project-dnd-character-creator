# reference-data Specification

## Purpose

Provide the authoritative D&D 5e rules data that drives the whole application —
races, classes, backgrounds, skills, alignments, equipment, spells, and class
features — reproduced from the official Player's Handbook (Pflichtenheft 1.4).
Every game choice offered to the user SHALL come from this data, never from
hardcoded values (CLAUDE.md convention). Supports the partial "Regeln und Features
lesen" use case by exposing trait/spell/feature descriptions for inline display.

NOTE: The Pflichtenheft names an Oracle database; the baseline implementation uses
a local SQLite database (`DbManager`), consistent with the single-user, local
desktop scope. The data-access contract below is what matters for this spec.

## Requirements

### Requirement: Self-initializing local database

The system SHALL connect to a local database on startup and, if it is empty,
initialize it from the bundled SQL script. The system SHALL also apply lightweight
schema migrations needed by the application (e.g. adding the background free-text
columns if missing).

#### Scenario: First run initializes data

- **WHEN** the application starts and the database has no race data
- **THEN** the database is initialized from the bundled SQL script before use

#### Scenario: Missing columns are added

- **WHEN** the character table lacks the background free-text columns
- **THEN** the columns are added so background fields can be stored

### Requirement: Provide selectable lists

The system SHALL provide the lists used by the creation flow: races (collapsing
sub-races appropriately), classes, backgrounds, skills, and alignments.

#### Scenario: Listing races

- **WHEN** the creation flow requests the race list
- **THEN** a de-duplicated, ordered list of selectable races is returned

#### Scenario: Listing classes, backgrounds, skills, alignments

- **WHEN** a creation step requests one of these lists
- **THEN** an ordered list from the database is returned

### Requirement: Provide detail data per selection

The system SHALL provide detailed data for a chosen race (ability bonuses,
languages, traits, size, speed) and class (hit die, primary ability, spellcasting
ability, proficiencies, saving throws, skill choices and count, starting equipment
and equipment options).

#### Scenario: Race detail

- **WHEN** a race is selected
- **THEN** its ability bonuses, languages, traits, size, and speed are available

#### Scenario: Class detail

- **WHEN** a class is selected
- **THEN** its hit die, abilities, proficiencies, saving throws, skill options, and equipment data are available

### Requirement: Provide spell data

The system SHALL provide spells available to a class up to a given level, spell
detail records, per-level known spell/cantrip counts, and spell slots per level.

#### Scenario: Spells for a class

- **WHEN** a spellcasting class needs its selectable spells
- **THEN** the spells available to that class at or below the relevant level are returned

### Requirement: Provide class progression data

The system SHALL provide class and subclass features by level, ASI levels, and
available subclasses for a class, to support the level-up flow.

#### Scenario: Features at a level

- **WHEN** the level-up flow needs the features granted at a level
- **THEN** the class (and subclass, if any) features for that level are returned

#### Scenario: ASI level check

- **WHEN** the level-up flow checks whether a level grants an ability score improvement
- **THEN** the system reports whether that level is an ASI level for the class
