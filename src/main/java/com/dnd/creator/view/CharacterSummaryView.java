package com.dnd.creator.view;

import com.dnd.creator.data.DbManager;
import com.dnd.creator.model.CharacterModel;
import com.dnd.creator.model.CharacterSession;
import com.dnd.creator.model.Race;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Map;

public class CharacterSummaryView {
    private Parent root;

    @FXML
    private Label lblCharacterName;

    @FXML
    private Label lblClass;

    @FXML
    private Label lblRace;

    @FXML
    private VBox abilityScoresContainer;

    @FXML
    private Label lblBackground;

    @FXML
    private Label lblSkills;

    @FXML
    private Label lblEquipment;

    @FXML
    private Label lblSpells;

    @FXML
    private Button btnBack;

    @FXML
    private Button btnSave;

    private DbManager dbManager = new DbManager();

    public CharacterSummaryView() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/dnd/creator/view/CharacterSummaryView.fxml"));
            loader.setController(this);
            root = loader.load();

            dbManager.connect();
            loadCharacterData();
            setupButtonHandlers();

        } catch (IOException e) {
            throw new RuntimeException("Failed to load CharacterSummaryView.fxml", e);
        }
    }

    public Parent getRoot() {
        return root;
    }

    private void loadCharacterData() {
        var character = CharacterSession.getInstance().getCurrentCharacter();

        // Character name
        if (character.getName() != null && !character.getName().isEmpty()) {
            lblCharacterName.setText(character.getName());
        } else {
            lblCharacterName.setText("Neuer Charakter");
        }

        // Race - mit allen Details
        if (character.getRace() != null) {
            Race race = character.getRace();
            lblRace.setText(race.getName());

            // Clear existing ability scores
            abilityScoresContainer.getChildren().clear();

            // Debug: Print bonuses to console
            System.out.println("=== Race: " + race.getName() + " ===");
            System.out.println("Ability Bonuses: " + race.getAbilityBonuses());

            // Add ability scores from character WITH bonuses added
            // Note: Database keys are "STR", "DEX", "CON", "INT", "WIS", "CHA"
            int strBonus = race.getAbilityBonuses().getOrDefault("STR", 0);
            int dexBonus = race.getAbilityBonuses().getOrDefault("DEX", 0);
            int conBonus = race.getAbilityBonuses().getOrDefault("CON", 0);
            int intBonus = race.getAbilityBonuses().getOrDefault("INT", 0);
            int wisBonus = race.getAbilityBonuses().getOrDefault("WIS", 0);
            int chaBonus = race.getAbilityBonuses().getOrDefault("CHA", 0);

            int strWithBonus = character.getStrength() + strBonus;
            int dexWithBonus = character.getDexterity() + dexBonus;
            int conWithBonus = character.getConstitution() + conBonus;
            int intWithBonus = character.getIntelligence() + intBonus;
            int wisWithBonus = character.getWisdom() + wisBonus;
            int chaWithBonus = character.getCharisma() + chaBonus;

            // Display only final values (no calculations shown)
            String[] abilities = {
                "STR: " + strWithBonus,
                "DEX: " + dexWithBonus,
                "CON: " + conWithBonus,
                "INT: " + intWithBonus,
                "WIS: " + wisWithBonus,
                "CHA: " + chaWithBonus
            };

            for (String ability : abilities) {
                Label label = new Label(ability);
                label.setStyle("-fx-font-size: 14px; -fx-text-fill: #1A1A1A;");
                abilityScoresContainer.getChildren().add(label);
            }

            // Add race information
            addRaceInformation(race);

            // Add combat stats
            addCombatStats();
        } else {
            lblRace.setText("Keine Rasse gewählt");
        }

        // Class
        if (character.getCharacterClass() != null && !character.getCharacterClass().isEmpty()) {
            lblClass.setText(character.getCharacterClass());
            addClassInformation(character);
        } else {
            lblClass.setText("Keine Klasse gewählt");
        }

        // Default values for now (can be extended later)
        lblBackground.setText("Keine Hintergrund gewählt");
        lblSkills.setText("Keine Fähigkeiten gewählt");
        lblEquipment.setText("Keine Ausrüstung gewählt");
        lblSpells.setText("Keine Zauber");
    }

    private void addClassInformation(com.dnd.creator.model.CharacterModel character) {
        // Get class data from database
        Map<String, Object> classData = dbManager.getClassByName(character.getCharacterClass());

        if (classData != null) {
            Label classTitle = new Label("✓ Klassenmerkmale:");
            classTitle.setStyle("-fx-font-size: 12px; -fx-font-weight: bold; -fx-text-fill: #1565C0; -fx-padding: 10 0 5 0;");
            abilityScoresContainer.getChildren().add(classTitle);

            VBox classInfoBox = new VBox(3);
            classInfoBox.setStyle("-fx-border-color: #E0E0E0; -fx-border-width: 0 0 1 0; -fx-padding: 8;");

            // Hit Die
            Label hitDieLabel = new Label("• Hit Die: d" + classData.get("hit_die"));
            hitDieLabel.setStyle("-fx-font-size: 11px; -fx-font-weight: bold; -fx-text-fill: #1A1A1A;");
            classInfoBox.getChildren().add(hitDieLabel);

            // Spellcasting info
            if ((Boolean) classData.get("has_spells")) {
                String spellAbility = (String) classData.get("spellcasting_ability");
                Label spellLabel = new Label("• Zauber: Hauptfähigkeit = " + (spellAbility != null ? spellAbility : "Unbekannt"));
                spellLabel.setStyle("-fx-font-size: 11px; -fx-text-fill: #8B0000;");
                classInfoBox.getChildren().add(spellLabel);
            }

            // Proficiencies
            @SuppressWarnings("unchecked")
            java.util.List<String> profs = (java.util.List<String>) classData.get("proficiencies");
            if (profs != null && !profs.isEmpty()) {
                Label profTitle = new Label("• Profizienzen:");
                profTitle.setStyle("-fx-font-size: 10px; -fx-font-weight: bold; -fx-text-fill: #555555;");
                classInfoBox.getChildren().add(profTitle);

                for (String prof : profs) {
                    Label profLabel = new Label("  - " + prof);
                    profLabel.setStyle("-fx-font-size: 10px; -fx-text-fill: #555555;");
                    classInfoBox.getChildren().add(profLabel);
                }
            }

            abilityScoresContainer.getChildren().add(classInfoBox);

            // Starting Equipment
            java.util.List<String> equipment = dbManager.getClassStartingEquipment((String) classData.get("index"));
            if (equipment != null && !equipment.isEmpty()) {
                Label equipTitle = new Label("✓ Startausrüstung:");
                equipTitle.setStyle("-fx-font-size: 12px; -fx-font-weight: bold; -fx-text-fill: #6A4C93; -fx-padding: 10 0 5 0;");
                abilityScoresContainer.getChildren().add(equipTitle);

                for (String equip : equipment) {
                    Label equipLabel = new Label("  • " + equip);
                    equipLabel.setStyle("-fx-font-size: 11px; -fx-text-fill: #6A4C93;");
                    abilityScoresContainer.getChildren().add(equipLabel);
                }
            }

            // Selected Equipment Choices
            java.util.List<String> selectedEquip = character.getSelectedEquipment();
            if (selectedEquip != null && !selectedEquip.isEmpty()) {
                Label selectedTitle = new Label("✓ Ausgewählte Ausrüstung:");
                selectedTitle.setStyle("-fx-font-size: 12px; -fx-font-weight: bold; -fx-text-fill: #1565C0; -fx-padding: 10 0 5 0;");
                abilityScoresContainer.getChildren().add(selectedTitle);

                for (String choice : selectedEquip) {
                    Label choiceLabel = new Label("  • " + choice);
                    choiceLabel.setStyle("-fx-font-size: 11px; -fx-text-fill: #1565C0;");
                    abilityScoresContainer.getChildren().add(choiceLabel);
                }
            }
        }
    }

    private void addRaceInformation(Race race) {
        // Add traits section
        if (race.getTraits() != null && !race.getTraits().isEmpty()) {
            Label traitsTitle = new Label("✓ Fähigkeiten:");
            traitsTitle.setStyle("-fx-font-size: 12px; -fx-font-weight: bold; -fx-text-fill: #1565C0; -fx-padding: 10 0 5 0;");
            abilityScoresContainer.getChildren().add(traitsTitle);

            for (Race.Trait trait : race.getTraits()) {
                VBox traitBox = new VBox(3);
                traitBox.setStyle("-fx-border-color: #E0E0E0; -fx-border-width: 0 0 1 0; -fx-padding: 8;");

                Label traitName = new Label("• " + trait.getName());
                traitName.setStyle("-fx-font-size: 11px; -fx-font-weight: bold; -fx-text-fill: #1A1A1A;");
                traitBox.getChildren().add(traitName);

                if (trait.getDescription() != null && !trait.getDescription().isEmpty()) {
                    Label traitDesc = new Label(trait.getDescription());
                    traitDesc.setStyle("-fx-font-size: 10px; -fx-text-fill: #555555; -fx-wrap-text: true;");
                    traitDesc.setWrapText(true);
                    traitBox.getChildren().add(traitDesc);
                }

                abilityScoresContainer.getChildren().add(traitBox);
            }
        }

        // Add languages section
        if (race.getLanguages() != null && !race.getLanguages().isEmpty()) {
            Label langTitle = new Label("✓ Sprachen:");
            langTitle.setStyle("-fx-font-size: 12px; -fx-font-weight: bold; -fx-text-fill: #6A4C93; -fx-padding: 10 0 5 0;");
            abilityScoresContainer.getChildren().add(langTitle);

            for (String language : race.getLanguages()) {
                Label langLabel = new Label("  • " + language);
                langLabel.setStyle("-fx-font-size: 11px; -fx-text-fill: #6A4C93;");
                abilityScoresContainer.getChildren().add(langLabel);
            }
        }
    }

    private void setupButtonHandlers() {
        btnBack.setOnAction(e -> navigateBack());
        btnSave.setOnAction(e -> saveCharacter());
    }

    private void navigateBack() {
        try {
            Stage stage = (Stage) btnBack.getScene().getWindow();
            EquipmentView previousView = new EquipmentView();
            stage.setScene(new Scene(previousView.getRoot(), stage.getScene().getWidth(), stage.getScene().getHeight()));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void saveCharacter() {
        // TODO: Save character to database
        showSuccess("Charakter erfolgreich gespeichert!");
        MainView mainView = new MainView();
        Stage stage = (Stage) btnSave.getScene().getWindow();
        stage.setScene(new Scene(mainView.getRoot(), stage.getScene().getWidth(), stage.getScene().getHeight()));
    }

    private void showSuccess(String message) {
        javafx.scene.control.Alert alert = new javafx.scene.control.Alert(javafx.scene.control.Alert.AlertType.INFORMATION);
        alert.setTitle("Erfolg");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void addCombatStats() {
        var character = CharacterSession.getInstance().getCurrentCharacter();

        Label statsTitle = new Label("✓ Kampfwerte:");
        statsTitle.setStyle("-fx-font-size: 12px; -fx-font-weight: bold; -fx-text-fill: #D32F2F; -fx-padding: 10 0 5 0;");
        abilityScoresContainer.getChildren().add(statsTitle);

        // Leben (HP)
        int hp = calculateHP(character);
        Label hpLabel = new Label("  • Leben: " + hp);
        hpLabel.setStyle("-fx-font-size: 11px; -fx-text-fill: #D32F2F;");
        abilityScoresContainer.getChildren().add(hpLabel);

        // Speed
        String raceName = character.getRace() != null ? character.getRace().getName() : "Human";
        int speed = getRaceSpeed(raceName);
        Label speedLabel = new Label("  • Geschwindigkeit: " + speed + " ft/round");
        speedLabel.setStyle("-fx-font-size: 11px; -fx-text-fill: #D32F2F;");
        abilityScoresContainer.getChildren().add(speedLabel);

        // Initiative
        int dexMod = (character.getDexterity() - 10) / 2;
        Label initiativeLabel = new Label("  • Initiative: " + (dexMod >= 0 ? "+" : "") + dexMod);
        initiativeLabel.setStyle("-fx-font-size: 11px; -fx-text-fill: #D32F2F;");
        abilityScoresContainer.getChildren().add(initiativeLabel);

        // Armor Class (vereinfacht: 10 + DEX)
        int ac = 10 + dexMod;
        Label acLabel = new Label("  • Rüstungsklasse: " + ac);
        acLabel.setStyle("-fx-font-size: 11px; -fx-text-fill: #D32F2F;");
        abilityScoresContainer.getChildren().add(acLabel);

        // Skills
        Label skillsTitle = new Label("✓ Fertigkeiten:");
        skillsTitle.setStyle("-fx-font-size: 12px; -fx-font-weight: bold; -fx-text-fill: #F57C00; -fx-padding: 10 0 5 0;");
        abilityScoresContainer.getChildren().add(skillsTitle);

        int strMod = (character.getStrength() - 10) / 2;
        int conMod = (character.getConstitution() - 10) / 2;
        int intMod = (character.getIntelligence() - 10) / 2;
        int wisMod = (character.getWisdom() - 10) / 2;
        int chaMod = (character.getCharisma() - 10) / 2;

        String[][] skills = {
            {"Akrobatik", String.valueOf(dexMod)},
            {"Tierhandhabung", String.valueOf(wisMod)},
            {"Arkanwissen", String.valueOf(intMod)},
            {"Athletik", String.valueOf(strMod)},
            {"Betrug", String.valueOf(chaMod)},
            {"Geschichte", String.valueOf(intMod)},
            {"Einsicht", String.valueOf(wisMod)},
            {"Einschüchterung", String.valueOf(chaMod)},
            {"Untersuchung", String.valueOf(intMod)},
            {"Heilkunde", String.valueOf(wisMod)},
            {"Naturkunde", String.valueOf(intMod)},
            {"Wahrnehmung", String.valueOf(wisMod)},
            {"Aufführung", String.valueOf(chaMod)},
            {"Überzeugung", String.valueOf(chaMod)},
            {"Religion", String.valueOf(intMod)},
            {"Fingerfertigkeit", String.valueOf(dexMod)},
            {"Heimlichkeit", String.valueOf(dexMod)},
            {"Überleben", String.valueOf(wisMod)}
        };

        for (String[] skill : skills) {
            int mod = Integer.parseInt(skill[1]);
            Label skillLabel = new Label("  • " + skill[0] + ": " + (mod >= 0 ? "+" : "") + mod);
            skillLabel.setStyle("-fx-font-size: 10px; -fx-text-fill: #F57C00;");
            abilityScoresContainer.getChildren().add(skillLabel);
        }
    }

    private int calculateHP(CharacterModel character) {
        int hitDie = character.getClassHitDie();
        if (hitDie == 0) hitDie = 6;
        int conMod = (character.getConstitution() - 10) / 2;
        return hitDie + conMod;
    }

    private int getRaceSpeed(String raceName) {
        if (raceName == null || raceName.isEmpty()) return 30;
        switch (raceName) {
            case "Dwarf": return 25;
            case "Elf": return 30;
            case "Halfling": return 25;
            case "Human": return 30;
            case "Dragonborn": return 30;
            case "Gnome": return 25;
            case "Half-Elf": return 30;
            case "Half-Orc": return 30;
            case "Tiefling": return 30;
            default: return 30;
        }
    }
}
