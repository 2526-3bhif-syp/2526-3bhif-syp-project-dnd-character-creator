# character-creation Specification

## Purpose

Provide a beginner-friendly, guided multi-step flow that lets a user with no prior
D&D knowledge build a complete level-1 character. Serves Pflichtenheft use case
"Charakter erstellen" (1.4.1) and the project goal of a guided creation that needs
little prior knowledge (1.5).

This spec describes the BASELINE behavior already implemented. Creation state is
held in a single in-memory session (`CharacterSession`) and committed to storage
only on the final save step (see `character-persistence`).

## Requirements

### Requirement: Guided step sequence

The system SHALL guide the user through ordered creation steps with a visible
stepper showing the current step, completed steps, and upcoming steps. The step
order SHALL be: Identität, Klasse, Werte, Fähigkeiten, Ausrüstung, Gesinnung,
Zauber, followed by a summary/save screen.

#### Scenario: Stepper reflects progress

- **WHEN** the user is on a creation step
- **THEN** the stepper marks earlier steps as done, the current step as active, and later steps as future

#### Scenario: Forward and backward navigation

- **WHEN** the user clicks "Next" on a step with valid input, or "Back" on any step
- **THEN** the application advances to the following step, or returns to the previous step, preserving previously entered data in the session

### Requirement: Step 1 — Identity (name, race, background, portrait)

The system SHALL let the user enter a character name, choose a race and a
background from the reference database, and optionally pick a portrait image from
the local filesystem. Race and background lists SHALL come from the database, not
hardcoded values. (Pflichtenheft 1.4: data from the self-provided DB.)

#### Scenario: Name is required to proceed

- **WHEN** the character name is empty
- **THEN** the "Next" button is disabled and a reason is shown

#### Scenario: Selecting a race loads its data

- **WHEN** the user selects a race
- **THEN** the chosen race (with ability bonuses, languages, traits, size, speed) is stored on the session character

#### Scenario: Optional portrait

- **WHEN** the user chooses an image file
- **THEN** the file path is stored as the character's portrait; if none is chosen a placeholder is used

### Requirement: Step 2 — Class selection

The system SHALL present the available classes from the database and store the
chosen class together with its hit die, primary ability, saving throws,
proficiencies, and whether it is a spellcasting class.

#### Scenario: Class drives later steps

- **WHEN** the user selects a class
- **THEN** the class data is stored and used to determine starting equipment, skill choices, and whether the Zauber step applies

### Requirement: Step 3 — Ability scores

The system SHALL let the user assign a fixed standard-array pool of values
([15, 14, 13, 12, 10, 8]) to the six abilities (Strength, Dexterity, Constitution,
Intelligence, Wisdom, Charisma), each value used once.

#### Scenario: All abilities must be assigned

- **WHEN** at least one ability has no assigned value
- **THEN** the user cannot proceed to the next step

#### Scenario: Assigned values persist

- **WHEN** the user assigns values and continues
- **THEN** the six ability scores are stored on the session character

### Requirement: Step 4 — Skill proficiencies

The system SHALL let the user choose skill proficiencies, limited to the number
and option set defined for the chosen class in the database.

#### Scenario: Skill choice respects class limit

- **WHEN** the class allows choosing N skills from a list
- **THEN** the user may select up to N skills from that list, and the selection is stored

### Requirement: Step 5 — Equipment

The system SHALL include the class's mandatory starting equipment automatically
and let the user resolve each "choose one" equipment option defined for the class.

#### Scenario: Resolving an equipment choice

- **WHEN** the class defines an "A or B" starting-equipment option
- **THEN** the user selects exactly one alternative, and the selection plus all mandatory items are stored

### Requirement: Step 6 — Alignment

The system SHALL let the user pick one alignment from the nine standard D&D
alignments loaded from the database.

#### Scenario: Alignment stored

- **WHEN** the user selects an alignment
- **THEN** the alignment is stored on the session character

### Requirement: Step 7 — Spell selection for spellcasters

The system SHALL show a spell/cantrip selection step ONLY when the chosen class is
a spellcasting class. The number of cantrips and spells selectable SHALL follow
the class's known-counts; non-spellcasting classes SHALL skip directly to the
summary.

#### Scenario: Spellcaster selects spells

- **WHEN** the chosen class has a spellcasting ability
- **THEN** the Zauber step is shown and the user selects the allowed number of cantrips and level-1 spells

#### Scenario: Non-caster skips spells

- **WHEN** the chosen class has no spellcasting ability
- **THEN** the Zauber step is skipped and the flow goes from Alignment to the summary

### Requirement: Summary and save

The system SHALL present a read-only summary of all chosen options (name, class,
race, background, alignment, ability scores, skills, equipment, spells) and a Save
button. On save the character SHALL be persisted and the creation session reset.

#### Scenario: Saving a complete character

- **WHEN** the user clicks Save on the summary with race, class, and background set
- **THEN** the character is persisted, the session is reset, and the user returns to the main menu

#### Scenario: Incomplete character is not saved

- **WHEN** race, class, or background is missing
- **THEN** the save is rejected and the character is not persisted

### Requirement: Fresh session per creation

The system SHALL reset the creation session when a new character creation is
started from the main menu, so no data leaks from a previous character.

#### Scenario: Starting a new creation

- **WHEN** the user starts "Create" from the main menu
- **THEN** the session is reset to an empty character before step 1 is shown
