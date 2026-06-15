## Context

The character sheet popup (`CharacterSheetPopupView`) already renders a Delete
button (`@FXML private Button btnDelete`) but never wires it — the constructor ends
with the comment `// Delete is stub`. The other two action buttons on the same
popup (`btnEdit`, `btnLevelUp`) are fully wired, and `btnEdit` already demonstrates
the callback pattern this change will follow (`onEditCallback`, handled by
`CharactersOverviewPresenter`).

Persistence lives in `DbManager` (SQLite via sqlite-jdbc). The child tables
(`character_stats`, `character_skill`, `character_equipment`, `character_spell`)
declare `FOREIGN KEY (character_id) REFERENCES character(id) ON DELETE CASCADE`,
but `DbManager.connect()` does not run `PRAGMA foreign_keys = ON`, so SQLite does
**not** enforce cascade on this connection. `updateCharacter` already works around
this by explicitly deleting the four child tables before re-inserting.

## Goals / Non-Goals

**Goals:**
- Make the existing Delete button permanently remove a character after confirmation.
- Remove all related rows atomically, leaving no orphans.
- Refresh the Deck so the character disappears immediately.

**Non-Goals:**
- Enabling global SQLite foreign-key enforcement.
- Deleting portrait image files from disk.
- A delete control on the overview cards themselves (sheet popup only).
- Undo / soft-delete / trash.
- Touching `character_asi` / `character_level_up` (those tables are not defined in
  the current schema; see Open Questions).

## Decisions

### Decision 1: Manual cascade in a transaction (not PRAGMA foreign_keys)

`DbManager.deleteCharacter(long id)` deletes from the child tables, then the
`character` row, inside one transaction (`setAutoCommit(false)` → commit, rollback
on failure).

- **Why:** Mirrors the existing `updateCharacter` delete-then-reinsert pattern, is
  self-contained, and carries zero risk to other code paths.
- **Alternative considered:** `PRAGMA foreign_keys = ON` + rely on declared
  `ON DELETE CASCADE`. Rejected — it would turn on FK enforcement for the entire
  application at once, risking regressions in existing insert/update paths for a
  feature that should own a much smaller blast radius.

### Decision 2: Confirmation via JavaFX Alert(CONFIRMATION)

`btnDelete` opens a confirmation `Alert` naming the character; deletion proceeds
only on the OK/confirm button.

- **Why:** Matches the issue's acceptance criteria and the beginner-friendly NFA
  (guard against accidental, irreversible loss). Consistent with the error `Alert`
  already used by `btnLevelUp`.

### Decision 3: Callback + presenter refresh (reuse the edit pattern)

Add `onDeleteCallback` (a `Consumer<CharacterModel>` or `Runnable`) on
`CharacterSheetPopupView`, set by `CharactersOverviewPresenter` when it builds the
popup — exactly parallel to `onEditCallback`. On confirmed delete: call
`DbManager.deleteCharacter`, fire the callback, close the popup stage. The
presenter re-queries `getAllSavedCharacters()` and rebuilds the cards grid (the
same `updateView` logic that already handles the empty state).

- **Why:** Keeps the View passive and the Presenter in charge of navigation/refresh
  (MVP), reusing an established pattern.
- **Alternative considered:** removing just the one card node in place. Rejected —
  re-querying is simpler, already exists, and correctly drives the empty-state label.

## Risks / Trade-offs

- **Forgetting a child table leaves orphans** → enumerate the four child tables
  explicitly in `deleteCharacter`, matching the set `updateCharacter` already
  deletes; cover with the "removes all related data" scenario.
- **Partial delete on mid-operation failure** → wrap all deletes in one transaction
  and roll back on any error (atomicity scenario).
- **Orphaned portrait files accumulate** → accepted trade-off (Non-Goal); files are
  small and single-user scale is tiny.

## Migration Plan

No data migration or schema change. Purely additive code: one new `DbManager`
method, one wired button + callback, one presenter refresh. Rollback = revert the
commit; no persistent state is altered by deploying the change itself.

## Open Questions

- `DbManager.saveLevelUp` inserts into `character_asi` and `character_level_up`,
  but those tables are not defined in `dnd5e.sql` (inserts fail silently today).
  This change does not need them, but it is a latent bug to track separately under
  issue #17 (Level Up).
