# Proposal Backlog — Open Issues Needing Implementation

Open GitHub issues that still need work, to be turned into OpenSpec change
proposals (`/opsx:propose`). Status is assessed against the baseline specs in
`openspec/specs/`. Generated 2026-06-15. Last updated 2026-06-22.

Ordered by recommended implementation priority (effort vs. value, gaps first).

---

## 1. Rules and features tab — #4

- **Issue:** [#4 rules and features tab](../../../issues/4) · Priority: Low · Effort: 1
- **Status:** ◐ Partial — trait/spell/feature descriptions are shown inline during
  creation, but there is no dedicated, browsable rules section.
- **Pflichtenheft:** 1.4.1 "Regeln und Features lesen" (supports the NFA goal of
  beginner-friendliness)
- **Affects spec:** new capability `rules-reference` (reads from `reference-data`)
- **Acceptance criteria:**
  - [ ] The system provides a section for rules
  - [ ] Rules are structured by category
  - [ ] Content is readable and clearly formatted
  - [ ] The section is accessible without creating a character (entry from main menu)
- **Notes:** Data largely already in the DB. Mostly a new read-only view +
  navigation entry. Low effort, high beginner value.

---

## 2. Character Level Up — #17 (partially built)

- **Issue:** [#17 Character Level Up](../../../issues/17) · Priority: High · Effort: 8
- **Status:** ◐ Partially built — covered by the `character-leveling` baseline spec
  (`LevelUpView` + `DbManager.saveLevelUp`). Several acceptance criteria remain.
- **Pflichtenheft:** extends "Charakter verwalten"
- **Affects spec:** `character-leveling` (extend)
- **Acceptance criteria:**
  - [x] Choose between feats or ability score improvement
  - [x] Subclass options
  - [x] HP always increases
  - [x] Some classes get new spells and spell slots at different levels
  - [ ] Some races gain new abilities at different levels (racial level features)
  - [ ] Multiclassing — user can choose multiple classes per level, each new class
        starting at level 1
- **Notes:** Remaining work is the hard part. **Multiclassing** is a large change
  touching the data model (a character has one class today), creation, sheet, and
  level-up — recommend its own proposal, separate from racial level features.

---

## Already implemented (no proposal needed)

- **#6 delete character** — ✅ Built & archived
  (`add-character-delete`, 2026-06-15). `DbManager.deleteCharacter(id)` cascades to
  related tables; the sheet has a confirm dialog and the overview refreshes after
  delete. Covered by `character-sheet` / `character-persistence` /
  `character-overview` specs.
- **#3 character export (PDF)** — ✅ Built & archived
  (`add-character-export`, 2026-06-22). `CharacterPdfExporter` renders an
  official-style 5e sheet via Apache PDFBox (drawn programmatically, no template
  asset); `ExportPresenter` handles the save dialog and success/error reporting.
  Export is wired both on the character-sheet popup and on the creation Summary
  step. Covered by the new `character-export` capability plus Export requirements
  added to `character-sheet` and `character-creation`.
  - [x] User can select a saved character for export (sheet popup)
  - [x] Export can be triggered immediately after creation via a button (Summary step)
  - [x] Export generates a PDF file
  - [x] The PDF contains all character data
  - [x] The file is saved via a chooser after export
- **#5 edit character** — ✅ Built. Covered by the `character-editing` baseline
  spec (load into creation flow, preselect, update-in-place).

---

## Suggested proposal order

1. `add-rules-reference` (#4) — small, high beginner value
2. `add-racial-level-features` (part of #17)
3. `add-multiclassing` (part of #17) — largest, schema-level change

_Done: `add-character-delete` (#6), `add-character-export` (#3)._
