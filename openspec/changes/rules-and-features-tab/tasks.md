## 1. Database Extensions

- [x] 1.1 Add database helper method `getAbilities()` in `DbManager.java` to fetch name and description from the `ability` table.
- [x] 1.2 Add database helper method `getSkillsWithAbilities()` in `DbManager.java` to fetch name, associated ability, and description from the `skill` table.
- [x] 1.3 Add database helper method `getClassFeatures(String className)` in `DbManager.java` to fetch level, feature_name, and description from the `class_feature` table.
- [x] 1.4 Add database helper method `getRaceTraits(String raceName)` in `DbManager.java` to fetch trait_name and description from the `race_trait` table.

## 2. UI Layout & View Creation

- [x] 2.1 Create the FXML structure `RulesView.fxml` in `src/main/resources/com/dnd/creator/view/` with a SplitPane containing a TreeView on the left and a ScrollPane wrapping a VBox/TextFlow on the right.
- [x] 2.2 Create `RulesView.java` in `src/main/java/com/dnd/creator/view/` to load `RulesView.fxml`, bind UI components, and expose event callbacks (e.g., node selection, back button action).
- [x] 2.3 Add parchment-themed styles for the Rules TreeView, titles, and text rendering inside `character-creation.css`.

## 3. Presenter & Navigation Wiring

- [ ] 3.1 Create `RulesPresenter.java` in `src/main/java/com/dnd/creator/presenter/` to manage TreeView node selection, query data from `DbManager` on demand, and format the detailed rules text dynamically.
- [ ] 3.2 Wire the `✦ Rules ✦` button's `onAction` attribute in `MainView.fxml` to a new handler `handleRules()` in `MainView.java`.
- [ ] 3.3 Add navigation listeners in `MainPresenter.java` to handle showRules transition and wire the back button from `RulesView` to showMain.
