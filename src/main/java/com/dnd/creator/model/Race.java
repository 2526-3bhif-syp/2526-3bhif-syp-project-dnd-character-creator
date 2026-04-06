package com.dnd.creator.model;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Race {
    private String index;
    private String name;
    private int speed;
    private String alignment;
    private String age;
    private String size;
    private String sizeDescription;
    private String languageDesc;
    private Map<String, Integer> abilityBonuses; // STR, DEX, CON, INT, WIS, CHA
    private List<String> languages;
    private List<Trait> traits;

    public Race() {
        this.abilityBonuses = new HashMap<>();
        this.languages = new java.util.ArrayList<>();
        this.traits = new java.util.ArrayList<>();
    }

    public Race(String index, String name, int speed) {
        this();
        this.index = index;
        this.name = name;
        this.speed = speed;
    }

    // Getters and Setters
    public String getIndex() { return index; }
    public void setIndex(String index) { this.index = index; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public int getSpeed() { return speed; }
    public void setSpeed(int speed) { this.speed = speed; }

    public String getAlignment() { return alignment; }
    public void setAlignment(String alignment) { this.alignment = alignment; }

    public String getAge() { return age; }
    public void setAge(String age) { this.age = age; }

    public String getSize() { return size; }
    public void setSize(String size) { this.size = size; }

    public String getSizeDescription() { return sizeDescription; }
    public void setSizeDescription(String sizeDescription) { this.sizeDescription = sizeDescription; }

    public String getLanguageDesc() { return languageDesc; }
    public void setLanguageDesc(String languageDesc) { this.languageDesc = languageDesc; }

    public Map<String, Integer> getAbilityBonuses() { return abilityBonuses; }
    public void setAbilityBonuses(Map<String, Integer> abilityBonuses) { this.abilityBonuses = abilityBonuses; }

    public void addAbilityBonus(String ability, int bonus) {
        this.abilityBonuses.put(ability, bonus);
    }

    public List<String> getLanguages() { return languages; }
    public void setLanguages(List<String> languages) { this.languages = languages; }
    public void addLanguage(String language) {
        if (!this.languages.contains(language)) {
            this.languages.add(language);
        }
    }

    public List<Trait> getTraits() { return traits; }
    public void setTraits(List<Trait> traits) { this.traits = traits; }
    public void addTrait(Trait trait) {
        if (!this.traits.stream().anyMatch(t -> t.getIndex().equals(trait.getIndex()))) {
            this.traits.add(trait);
        }
    }

    @Override
    public String toString() {
        return name;
    }

    // Inner class for Traits
    public static class Trait {
        private String index;
        private String name;
        private String description;

        public Trait() {}

        public Trait(String index, String name, String description) {
            this.index = index;
            this.name = name;
            this.description = description;
        }

        public String getIndex() { return index; }
        public void setIndex(String index) { this.index = index; }

        public String getName() { return name; }
        public void setName(String name) { this.name = name; }

        public String getDescription() { return description; }
        public void setDescription(String description) { this.description = description; }

        @Override
        public String toString() {
            return name;
        }
    }
}


