## Why

The character sheet already shows a Delete button, but it is a stub (`// Delete is stub`) — clicking it does nothing, so users cannot remove characters they no longer need. This implements Pflichtenheft use case 1.4.1 "Charakter löschen". For a first-time user with no prior knowledge, a non-working button is confusing; a guarded delete (with confirmation) lets them clean up the Deck safely without fear of accidental loss.

## What Changes

- Wire the existing `btnDelete` on the character sheet popup to a real delete action.
- Show a confirmation dialog before deleting; deletion only proceeds on explicit confirmation.
- Add `DbManager.deleteCharacter(long id)` that removes the character and all its related rows (stats, skills, equipment, spells) in a single transaction.
- After a confirmed delete, close the popup and refresh the overview grid so the character disappears from the Deck immediately.
- The copied portrait image file is intentionally left on disk (DB rows only); the placeholder is unaffected.

## Capabilities

### New Capabilities

<!-- None. Delete is part of existing capabilities. -->

### Modified Capabilities

- `character-sheet`: the Delete control changes from a non-functional stub to a working action that asks for confirmation and removes the character.
- `character-persistence`: add a requirement to permanently delete a character and all its related data atomically.
- `character-overview`: the Deck refreshes after a delete so the removed character no longer appears.

## Impact

- **Code:**
  - `DbManager` — new `deleteCharacter(long id)` (manual cascade in a transaction, mirroring the existing delete-then-reinsert pattern in `updateCharacter`).
  - `CharacterSheetPopupView` — wire `btnDelete`, add a confirmation `Alert` and an `onDeleteCallback` (parallel to the existing `onEditCallback`).
  - `CharactersOverviewPresenter` — handle the delete callback: close the popup and reload/refresh the cards grid.
- **Database:** no schema change. Child tables already declare `ON DELETE CASCADE`, but SQLite FK enforcement is off on this connection, so deletion is done explicitly per table.
- **Dependencies:** none.
- **Scope:** single-user, local; no new concurrency or networking concerns.
