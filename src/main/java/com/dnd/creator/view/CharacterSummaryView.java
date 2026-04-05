package com.dnd.creator.view;

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

    public CharacterSummaryView() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/dnd/creator/view/CharacterSummaryView.fxml"));
            loader.setController(this);
            root = loader.load();

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
        } else {
            lblRace.setText("Keine Rasse gewählt");
        }

        // Class
        lblClass.setText(character.getCharacterClass() != null ? character.getCharacterClass() : "Keine Klasse gewählt");

        // Default values for now (can be extended later)
        lblBackground.setText("Keine Hintergrund gewählt");
        lblSkills.setText("Keine Fähigkeiten gewählt");
        lblEquipment.setText("Keine Ausrüstung gewählt");
        lblSpells.setText("Keine Zauber");
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
}
