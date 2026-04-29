package com.dnd.creator.model;
import java.util.*;

public class CharacterModel {
    private long dbId;
    private String name = "New Character";
    private String imagePath = "placeholder.png";
    private int strength;
    private int dexterity;
    private int constitution;
    private int intelligence;
    private int wisdom;
    private int charisma;
    private Race race;
    private String characterClass;
    private String classIndex;
    private int classHitDie;
    private String spellcastingAbility;
    private List<String> classProficiencies = new ArrayList<>();
    private List<String> selectedSpells = new ArrayList<>();
    private List<String> selectedCantrips = new ArrayList<>();
    private List<String> selectedEquipmentOptions = new ArrayList<>();
    private List<String> selectedSkills = new ArrayList<>();
    private String selectedBackground;
    private List<String[]> weaponAttacks = new ArrayList<>();
    private String alignment;



    public CharacterModel() {}

    public CharacterModel(String name, String imagePath, int strength, int dexterity, int constitution, int intelligence, int wisdom, int charisma) {
        this.name = name;
        this.imagePath = imagePath;
        this.strength = strength;
        this.dexterity = dexterity;
        this.constitution = constitution;
        this.intelligence = intelligence;
        this.wisdom = wisdom;
        this.charisma = charisma;
    }

    public long getDbId() { return dbId; }
    public void setDbId(long dbId) { this.dbId = dbId; }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImagePath() {
        return imagePath;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }

    public int getStrength() {
        return strength;
    }

    public void setStrength(int strength) {
        this.strength = strength;
    }

    public int getDexterity() {
        return dexterity;
    }

    public void setDexterity(int dexterity) {
        this.dexterity = dexterity;
    }

    public int getConstitution() {
        return constitution;
    }

    public void setConstitution(int constitution) {
        this.constitution = constitution;
    }

    public int getIntelligence() {
        return intelligence;
    }

    public void setIntelligence(int intelligence) {
        this.intelligence = intelligence;
    }

    public int getWisdom() {
        return wisdom;
    }

    public void setWisdom(int wisdom) {
        this.wisdom = wisdom;
    }

    public int getCharisma() {
        return charisma;
    }

    public void setCharisma(int charisma) {
        this.charisma = charisma;
    }

    public Race getRace() {
        return race;
    }

    public void setRace(Race race) {
        this.race = race;
    }

    public String getCharacterClass() {
        return characterClass;
    }

    public void setCharacterClass(String characterClass) {
        this.characterClass = characterClass;
    }

    public String getClassIndex() {
        return classIndex;
    }

    public void setClassIndex(String classIndex) {
        this.classIndex = classIndex;
    }

    public int getClassHitDie() {
        return classHitDie;
    }

    public void setClassHitDie(int classHitDie) {
        this.classHitDie = classHitDie;
    }

    public String getSpellcastingAbility() {
        return spellcastingAbility;
    }

    public void setSpellcastingAbility(String spellcastingAbility) {
        this.spellcastingAbility = spellcastingAbility;
    }

    public List<String> getClassProficiencies() {
        return classProficiencies;
    }

    public void setClassProficiencies(List<String> classProficiencies) {
        this.classProficiencies = classProficiencies;
    }

    public void addClassProficiency(String proficiency) {
        this.classProficiencies.add(proficiency);
    }

    public void setSelectedCantrips(List<String> selectedCantrips) {
        this.selectedCantrips = selectedCantrips;
    }

    public void setSelectedEquipmentOptions(List<String> selectedEquipmentOptions) {
        this.selectedEquipmentOptions = selectedEquipmentOptions;
    }

    public List<String> getSelectedCantrips() {
        return selectedCantrips;
    }

    public List<String> getSelectedEquipmentOptions() {
        return selectedEquipmentOptions;
    }

    public List<String> getSelectedSpells() {
        return selectedSpells;
    }

    public void setSelectedSpells(List<String> selectedSpells) {
        this.selectedSpells = selectedSpells;
    }

    public void addSelectedSpell(String spell) {
        this.selectedSpells.add(spell);
    }

    public List<String> getSelectedEquipment() {
        return selectedEquipmentOptions;
    }

    public void setSelectedEquipment(List<String> selectedEquipment) {
        this.selectedEquipmentOptions = selectedEquipment;
    }

    public void addSelectedEquipment(String equipment) {
        this.selectedEquipmentOptions.add(equipment);
    }

    public List<String> getSelectedSkills() {
        return selectedSkills;
    }

    public void setSelectedSkills(List<String> selectedSkills) {
        this.selectedSkills = selectedSkills;
    }

    public void addSelectedSkill(String skill) {
        this.selectedSkills.add(skill);
    }

    public String getSelectedBackground() {
        return selectedBackground;
    }

    public void setSelectedBackground(String selectedBackground) {
        this.selectedBackground = selectedBackground;
    }

    public String getAlignment() { return alignment; }
    public void setAlignment(String alignment) { this.alignment = alignment; }

    /** Each entry is {name, atkBonus, damageAndType} — e.g. {"Longsword", "+4", "1d8 Slashing"} */
    public List<String[]> getWeaponAttacks() { return weaponAttacks; }
    public void setWeaponAttacks(List<String[]> weaponAttacks) { this.weaponAttacks = weaponAttacks; }
}

