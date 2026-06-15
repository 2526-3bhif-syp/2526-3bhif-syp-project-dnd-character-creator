## 1. Data layer — DbManager.deleteCharacter

- [x] 1.1 Add `public boolean deleteCharacter(long id)` to `DbManager`
- [x] 1.2 Inside a transaction (`setAutoCommit(false)`), delete child rows by `character_id` from `character_stats`, `character_skill`, `character_equipment`, `character_spell`, then delete the `character` row by `id`
- [x] 1.3 Commit on success; on any `SQLException` roll back, restore autocommit, and return `false`
- [x] 1.4 Return `true` only when the character row was actually removed (rows affected > 0)

## 2. View — wire btnDelete in CharacterSheetPopupView

- [x] 2.1 Add an `onDeleteCallback` field (parallel to `onEditCallback`) with a setter
- [x] 2.2 Replace the `// Delete is stub` comment by wiring `btnDelete` to an action handler
- [x] 2.3 In the handler, show an `Alert(AlertType.CONFIRMATION)` naming the character; proceed only if the user confirms
- [x] 2.4 On confirm, call `DbManager.deleteCharacter(character.getDbId())`; on success fire `onDeleteCallback`, then close the popup stage
- [x] 2.5 On delete failure, show an error `Alert` and keep the popup open

## 3. Presenter — refresh the Deck

- [x] 3.1 In `CharactersOverviewPresenter`, set `onDeleteCallback` when constructing the popup
- [x] 3.2 In the callback, re-query `getAllSavedCharacters()` and rebuild the cards grid (reuse existing `updateView`, which also drives the empty-state label)

## 4. Verification

- [x] 4.1 Build and run; delete a character from its sheet, confirm, and verify its card disappears from the Deck
- [x] 4.2 Cancel a delete and verify nothing is removed and the sheet stays open
- [x] 4.3 Delete the last remaining character and verify the empty-state message appears
- [x] 4.4 Query the DB to confirm no orphaned rows remain in `character_stats`, `character_skill`, `character_equipment`, `character_spell` for the deleted id
