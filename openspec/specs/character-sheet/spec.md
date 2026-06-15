# character-sheet Specification

## Purpose

Present a saved character as a complete D&D character sheet — the final output
promised in Pflichtenheft 1.4 ("am Ende soll der Spieler den Charakter in Form
eines Charakter-Sheets erhalten mit allen Attributen, Fähigkeiten und Zaubern").
The sheet is the hub from which the user can edit, level up, and (planned) delete
a character, and is also where free-text background fields are captured.

This spec describes the BASELINE behavior already implemented.

## Requirements

### Requirement: Show full character sheet

The system SHALL display a character's complete data in a popup sheet: name,
portrait, race, class, level, alignment, background, ability scores (with derived
modifiers and proficiency bonus), skills, equipment, weapon attacks, and known
spells/cantrips.

#### Scenario: Opening a saved character's sheet

- **WHEN** a character is opened from the overview
- **THEN** the sheet shows the character's attributes, skills, equipment, weapon attacks, and spells

#### Scenario: Derived combat values

- **WHEN** the sheet renders weapon attacks
- **THEN** each attack shows an attack bonus (ability modifier + proficiency) and damage, derived from the character's stats and race bonuses

### Requirement: Editable background fields

The system SHALL let the user view and edit the four free-text background fields —
personality traits, ideals, bonds, and flaws — directly on the sheet, and SHALL
persist changes to these fields.

#### Scenario: Editing a background field

- **WHEN** the user edits a background field and the field loses focus or the popup is closed
- **THEN** the updated personality traits, ideals, bonds, and flaws are saved to the database for that character

### Requirement: Edit action

The system SHALL provide an "Edit" action on the sheet that loads the character
into the creation flow for modification (see `character-editing`).

#### Scenario: Starting an edit

- **WHEN** the user clicks Edit on the sheet
- **THEN** the character is loaded into the session and the creation flow opens for editing

### Requirement: Level-up action

The system SHALL provide a "Level Up" action on the sheet that opens the level-up
flow for the character (see `character-leveling`).

#### Scenario: Starting a level-up

- **WHEN** the user clicks Level Up on the sheet
- **THEN** the level-up flow opens for that character

### Requirement: Delete action present but not yet functional

The sheet SHALL expose a delete control. NOTE (baseline gap): deletion is
currently a stub and does NOT remove the character from storage; full deletion is
out of scope for this baseline and tracked separately (Pflichtenheft use case
"Charakter löschen").

#### Scenario: Delete control is a stub

- **WHEN** the user activates the delete control
- **THEN** no character is removed from the database (behavior is a placeholder)
