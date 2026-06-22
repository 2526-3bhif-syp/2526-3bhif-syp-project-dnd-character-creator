## ADDED Requirements

### Requirement: Accessibility from Main Menu
The system SHALL display a Rules & Codex section when the player selects the "Rules" option on the main menu, without requiring the player to create or select a character.

#### Scenario: Navigating to Rules Screen
- **WHEN** the user clicks the "✦ Rules ✦" button in MainView
- **THEN** the system transitions the scene to the Rules & Codex screen

#### Scenario: Returning to Main Menu
- **WHEN** the user clicks the "Back" button in the Rules & Codex screen
- **THEN** the system returns the user to the MainView main menu

### Requirement: Categorized Rules Navigation
The Rules & Codex screen SHALL present a list of rule categories for navigation on the left, and detail contents on the right. The categories SHALL include Core Rules, Ability Scores, Skills, Classes, and Races.

#### Scenario: Selecting a Category
- **WHEN** the user selects a category from the navigation list
- **THEN** the detail pane updates to show the corresponding information for that category

### Requirement: Dynamic Content Retrieval
The system SHALL query the SQLite database (`ability`, `skill`, `class_feature`, `race_trait` tables) to load rules content dynamically.

#### Scenario: Reading Ability Scores
- **WHEN** the user selects the "Ability Scores" category and clicks "Strength"
- **THEN** the system queries the `ability` table for "Strength" and displays its description and important classes in a scrollable, clearly formatted text pane

#### Scenario: Reading Skills
- **WHEN** the user selects the "Skills" category and clicks "Athletics"
- **THEN** the system queries the `skill` table for "Athletics" and displays its associated ability ("Strength") and description

#### Scenario: Reading Class Features
- **WHEN** the user selects the "Classes" category and clicks "Barbarian"
- **THEN** the system displays the class's hit die, primary ability, saving throws, and a level-by-level list of features queried from `class_feature`

#### Scenario: Reading Race Traits
- **WHEN** the user selects the "Races" category and clicks "Elf"
- **THEN** the system displays the race's description and all associated racial traits queried from `race_trait`
