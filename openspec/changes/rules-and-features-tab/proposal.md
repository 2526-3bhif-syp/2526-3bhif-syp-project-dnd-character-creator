## Why

The D&D Character Creator requires a central reference manual where players can view core mechanics, rules, ability score definitions, skills, class features, and racial traits. Offering this section directly from the main menu ensures that players can read and learn the rules of Dungeons & Dragons 5th Edition (5e) at any time without having to start or complete the character creation process.

## What Changes

- **Rules Button in Main Menu**: Wire the existing, unused `✦ Rules ✦` button in `MainView.fxml` to transition to a new Rules & Codex screen.
- **Rules & Codex Screen (MVP Pattern)**: Create a new view `RulesView` (`RulesView.fxml` / `RulesView.java`) and a corresponding presenter `RulesPresenter.java`.
- **Structured Categories**: Lay out the rules screen with a categorical navigation panel (e.g., TreeView or list-based menu on the left) and a rich parchment-themed scrollable text area on the right. Categories include:
  - **Core Rules**: General d20 mechanics, advantage/disadvantage, ability modifiers, resting, and combat basics.
  - **Ability Scores**: STR, DEX, CON, INT, WIS, CHA definitions and descriptions loaded from the `ability` database table.
  - **Skills**: All 18 skills, their associated abilities, and full descriptions loaded from the `skill` database table.
  - **Classes & Features**: Full list of classes with their hit die, primary abilities, proficiencies, and class features (level-by-level) loaded from `class` and `class_feature` database tables.
  - **Races & Traits**: Full list of races with descriptions and race traits loaded from `race` and `race_trait` database tables.
- **Back Navigation**: Add a navigation button to return to the main menu screen.

## Capabilities

### New Capabilities
- `rules-codex`: Allows the player to browse D&D 5e rules, ability scores, skills, class features, and racial traits from the main menu in a categorized, clearly formatted interface.

### Modified Capabilities
<!-- None -->

## Impact

- **UI Screens**: Adds `RulesView` (FXML/Java) and wires up action listener to `MainView.fxml` / `MainPresenter.java`.
- **Database Access**: Adds query methods in `DbManager.java` to fetch:
  - All records from `ability` (name, description).
  - All records from `skill` (name, ability, description).
  - Class details, including starting saving throws and level-by-level features from `class_feature`.
  - Race traits (trait_name, description) from `race_trait`.
- **Styles**: Updates or references existing CSS files to preserve the medieval parchment theme (`character-creation.css`).
