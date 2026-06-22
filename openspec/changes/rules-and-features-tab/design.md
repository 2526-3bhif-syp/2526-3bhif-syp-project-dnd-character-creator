## Context

The D&D Character Creator application has an MVP architecture where FXML-based views are coupled with Java presenters. The database (`data.db`) already includes rich relational schema containing static D&D 5e content (classes, features, races, traits, abilities, and skills). The main menu view (`MainView.fxml`) contains a stub for a "✦ Rules ✦" button that currently does nothing. We need to implement a dedicated screen for displaying categorized, readable rules using the existing styling conventions.

## Goals / Non-Goals

**Goals:**
- Connect the main menu's `✦ Rules ✦` button to open a new Rules & Codex screen.
- Provide a clean, categorized, book-like interface with navigation on the left (TreeView) and content reading pane on the right (ScrollPane).
- Support browsing core rules (d20, Rest, Combat), Ability Scores, Skills, Classes/Features, and Races/Traits.
- Fetch data dynamically from SQLite tables (`ability`, `skill`, `class_feature`, `race_trait`) using `DbManager`.
- Keep rules accessible without any character state (no character creation needed).

**Non-Goals:**
- Creating or editing rules (read-only reference codex).
- Comprehensive search/filtering engine for rule text (navigation via tree structure is sufficient).
- Dynamic rule modifications (all database tables for rules are read-only).

## Decisions

### 1. Navigation Panel using TreeView
- **Choice**: Use a JavaFX `TreeView` in the left panel of a `SplitPane`.
- **Rationale**: A `TreeView` allows for clear hierarchical grouping of categories (e.g., "Classes" -> "Barbarian", "Fighter") and allows the user to expand/collapse sections. Clicking a child node will update the right-side detail pane.
- **Alternatives Considered**: `ListView` or multiple buttons. `ListView` lacks hierarchical layout (we'd have to mix main categories with items or have nested lists).

### 2. Styling Layout (Parchment & Serif Fonts)
- **Choice**: Match the existing UI styles in `character-creation.css` (cream backgrounds, dark red headers, serif typography, soft drop shadows).
- **Rationale**: Visual consistency makes the codex look like a premium built-in game manual.

### 3. DbManager Extensions
- **Choice**: Add lightweight query methods in `DbManager.java` using direct SQL statements and return standard collections like `List<Map<String, String>>` or custom model data.
- **Rationale**: Keeps database access aligned with existing methods, avoiding ORM complexity while keeping data access clean.
- **New Methods**:
  - `public List<Map<String, String>> getAbilities()`
  - `public List<Map<String, String>> getSkillsWithAbilities()`
  - `public List<Map<String, Object>> getClassFeatures(String className)`
  - `public List<Map<String, String>> getRaceTraits(String raceName)`

## Risks / Trade-offs

- **[Risk] Long description text formatting in JavaFX Label/TextFlow**  
  *Mitigation*: Wrap text in a `TextFlow` or a `Label` with `setWrapText(true)` inside a scrollable pane (`ScrollPane`). Add CSS padding and spacing to ensure clean spacing between paragraphs and headings.
- **[Risk] Large SQLite database connection overhead**  
  *Mitigation*: Reuse the single SQLite connection via `DbManager` instance. Load data on demand when a leaf node in the TreeView is clicked rather than loading all features/traits upfront.
