package com.dnd.creator.view;

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
        // TODO: Load data from model/session
        lblCharacterName.setText("Beispiel Held");
        lblClass.setText("Warrior");
        lblRace.setText("Human");
        lblBackground.setText("Soldier");
        lblSkills.setText("Athletics, Perception");
        lblEquipment.setText("Longsword, Shield, Leather Armor");
        lblSpells.setText("Keine");
        
        // Add ability scores
        String[] abilities = {"STR: 15", "DEX: 14", "CON: 13", "INT: 12", "WIS: 10", "CHA: 8"};
        for (String ability : abilities) {
            Label label = new Label(ability);
            label.setStyle("-fx-font-size: 14px; -fx-text-fill: #1A1A1A;");
            abilityScoresContainer.getChildren().add(label);
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
