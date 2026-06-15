## ADDED Requirements

### Requirement: Reflect deletions in the Deck

The system SHALL refresh the overview so that a character deleted from its sheet no
longer appears among the cards, without requiring the user to manually navigate
away and back. (Pflichtenheft 1.4.1 "Charakter löschen".)

#### Scenario: Deleted character disappears from the Deck

- **WHEN** a character is deleted from its sheet popup
- **THEN** the popup closes and the overview no longer shows that character's card

#### Scenario: Deleting the last character shows the empty state

- **WHEN** the only saved character is deleted
- **THEN** the overview shows the empty-state message and no cards
