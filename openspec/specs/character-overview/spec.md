# character-overview Specification

## Purpose

Let the user see all previously saved characters at a glance so they can manage
them. Serves Pflichtenheft use case "Charakterübersicht anzeigen" (1.4.1) and is
the entry point for viewing, editing, and (planned) deleting characters.

This spec describes the BASELINE behavior already implemented.

## Requirements

### Requirement: Display saved characters as cards

The system SHALL load all saved characters from the database and display each as a
card in a grid on the overview screen. Each card SHALL show the character's key
data: name, portrait, and base ability scores.

#### Scenario: Characters exist

- **WHEN** the overview is opened and at least one character is saved
- **THEN** one card per character is shown with name, portrait, and ability scores

#### Scenario: Loading from storage

- **WHEN** the overview is opened
- **THEN** characters are read fresh from the database (including stats, race, class, alignment, skills, equipment, spells, background fields)

### Requirement: Empty state

The system SHALL show an empty-state message instead of cards when no characters
are saved.

#### Scenario: No characters saved

- **WHEN** the overview is opened and no characters exist
- **THEN** an empty-state message is shown and no cards are displayed

### Requirement: Open character sheet from a card

The system SHALL open the full character sheet (see `character-sheet`) as a popup
when the user clicks a card.

#### Scenario: Clicking a card

- **WHEN** the user clicks a character card
- **THEN** a popup with that character's full sheet is shown over the overview

### Requirement: Navigate back to main menu

The system SHALL provide a way to return from the overview to the main menu.

#### Scenario: Back to main

- **WHEN** the user activates the back control on the overview
- **THEN** the main menu is shown
