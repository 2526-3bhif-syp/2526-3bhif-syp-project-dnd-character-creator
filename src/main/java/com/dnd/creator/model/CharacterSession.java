package com.dnd.creator.model;

/**
 * Global session singleton to hold character data during creation
 * This allows passing data between different views/steps
 */
public class CharacterSession {
    private static CharacterSession instance;
    private CharacterModel currentCharacter;

    private CharacterSession() {
        this.currentCharacter = new CharacterModel();
    }

    public static synchronized CharacterSession getInstance() {
        if (instance == null) {
            instance = new CharacterSession();
        }
        return instance;
    }

    public CharacterModel getCurrentCharacter() {
        return currentCharacter;
    }

    public void setCurrentCharacter(CharacterModel character) {
        this.currentCharacter = character;
    }

    public void reset() {
        this.currentCharacter = new CharacterModel();
    }
}

