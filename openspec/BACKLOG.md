# Proposal Backlog — Open Issues Needing Implementation

Open GitHub issues that still need work, to be turned into OpenSpec change
proposals (`/opsx:propose`). Status is assessed against the baseline specs in
`openspec/specs/`. Generated 2026-06-15.

Ordered by recommended implementation priority (effort vs. value, gaps first).

---

## 1. Delete character — #6

- **Issue:** [#6 delete character](../../../issues/6) · Priority: Low · Effort: 2
- **Status:** ❌ Not built — `btnDelete` exists on the sheet but is a stub
  (`// Delete is stub`); no DB delete runs.
- **Pflichtenheft:** 1.4.1 "Charakter löschen"
- **Affects spec:** `character-sheet` (replace the stub requirement),
  `character-persistence` (add delete), `character-overview` (refresh after delete)
- **Acceptance criteria:**
  - [ ] User can select a character for deletion
  - [ ] System asks for confirmation before deletion
  - [ ] After confirmation, the character is permanently removed
  - [ ] Deleted characters no longer appear in the Deck
- **Notes:** Smallest gap, well-scoped. Needs a `DbManager.deleteCharacter(id)`
  cascading to character_stats/skill/equipment/spell, plus a confirm dialog and
  overview refresh. Good first proposal.

---

## 2. Rules and features tab — #4

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

## 3. Character export (PDF) — #3

- **Issue:** [#3 character export](../../../issues/3) · Priority: Medium · Effort: 5
- **Status:** ❌ Not built — no export/PDF code exists.
- **Pflichtenheft:** 1.4.1 "Charakter exportieren"
- **Affects spec:** new capability `character-export`
- **Acceptance criteria:**
  - [ ] User can select a saved character for export
  - [ ] Export can be triggered immediately after creation via a button
  - [ ] Export generates a PDF file
  - [ ] The PDF contains all character data
  - [ ] The file is saved/offered automatically after export
- **Notes:** Needs a PDF library decision (no PDF dep in pom.xml yet — e.g.
  OpenPDF/PDFBox). Map the full character sheet to a printable layout. Largest of
  the unbuilt features.

---

## 4. Character Level Up — #17 (partially built)

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

- **#5 edit character** — ✅ Built. Covered by the `character-editing` baseline
  spec (load into creation flow, preselect, update-in-place).

---

## Suggested proposal order

1. `add-character-delete` (#6) — small, closes a known stub
2. `add-rules-reference` (#4) — small, high beginner value
3. `add-character-export` (#3) — medium, new PDF dependency
4. `add-racial-level-features` (part of #17)
5. `add-multiclassing` (part of #17) — largest, schema-level change
