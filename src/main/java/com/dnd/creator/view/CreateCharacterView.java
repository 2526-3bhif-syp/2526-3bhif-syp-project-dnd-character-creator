package com.dnd.creator.view;

import com.dnd.creator.data.DbManager;
import com.dnd.creator.model.CharacterSession;
import com.dnd.creator.presenter.MainPresenter;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;

public class CreateCharacterView {
    private Parent root;
    private DbManager dbManager;

    @FXML
    private Button btnBack;

    @FXML
    private Button btnNext;

    @FXML
    private Button btnDwarf;

    @FXML
    private Button btnElf;

    @FXML
    private Button btnHalfling;

    @FXML
    private Button btnHuman;

    @FXML
    private Button btnDragonborn;

    @FXML
    private Button btnGnome;

    @FXML
    private Button btnHalfElf;

    @FXML
    private Button btnHalfOrc;

    @FXML
    private Button btnTiefling;

    @FXML
    private TextField txtCharacterName;

    private String selectedRaceName = null;

    public CreateCharacterView() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/dnd/creator/view/CreateCharacterView.fxml"));
            loader.setController(this);
            root = loader.load();

            dbManager = new DbManager();
            dbManager.connect();

            setupRaceButtons();
            setupNavigationButtons();
            loadSavedStepData();

        } catch (IOException e) {
            throw new RuntimeException("Failed to load CreateCharacterView.fxml", e);
        }
    }

    public Parent getRoot() {
        return root;
    }

    private void loadSavedStepData() {
        var character = CharacterSession.getInstance().getCurrentCharacter();

        if (txtCharacterName != null && character.getName() != null && !character.getName().isBlank()
            && !"New Character".equals(character.getName())) {
            txtCharacterName.setText(character.getName());
        }

        var savedRace = character.getRace();

        if (savedRace != null && savedRace.getName() != null) {
            selectedRaceName = savedRace.getName();

            // Hebe die bereits ausgewählte Rasse hervor
            Button raceButton = null;
            switch(selectedRaceName) {
                case "Dwarf" -> raceButton = btnDwarf;
                case "Elf" -> raceButton = btnElf;
                case "Halfling" -> raceButton = btnHalfling;
                case "Human" -> raceButton = btnHuman;
                case "Dragonborn" -> raceButton = btnDragonborn;
                case "Gnome" -> raceButton = btnGnome;
                case "Half-Elf" -> raceButton = btnHalfElf;
                case "Half-Orc" -> raceButton = btnHalfOrc;
                case "Tiefling" -> raceButton = btnTiefling;
            }

            if (raceButton != null) {
                resetRaceButtonStyles();
                raceButton.setStyle(raceButton.getStyle() + "; -fx-background-color: #8B0000; -fx-text-fill: #F5F5DC;");
            }
        }
    }

    private void setupRaceButtons() {
        btnDwarf.setOnAction(e -> selectRace(btnDwarf, "Dwarf"));
        btnElf.setOnAction(e -> selectRace(btnElf, "Elf"));
        btnHalfling.setOnAction(e -> selectRace(btnHalfling, "Halfling"));
        btnHuman.setOnAction(e -> selectRace(btnHuman, "Human"));
        btnDragonborn.setOnAction(e -> selectRace(btnDragonborn, "Dragonborn"));
        btnGnome.setOnAction(e -> selectRace(btnGnome, "Gnome"));
        btnHalfElf.setOnAction(e -> selectRace(btnHalfElf, "Half-Elf"));
        btnHalfOrc.setOnAction(e -> selectRace(btnHalfOrc, "Half-Orc"));
        btnTiefling.setOnAction(e -> selectRace(btnTiefling, "Tiefling"));
    }

    private void selectRace(Button button, String raceName) {
        selectedRaceName = raceName;

        // Reset all buttons
        resetRaceButtonStyles();

        // Highlight selected button
        button.setStyle(button.getStyle() + "; -fx-background-color: #8B0000; -fx-text-fill: #F5F5DC;");
    }

    private void resetRaceButtonStyles() {
        btnDwarf.setStyle("-fx-padding: 20; -fx-border-color: #C6A664; -fx-border-width: 3; -fx-border-radius: 10; -fx-background-radius: 10; -fx-background-color: transparent; -fx-cursor: hand; -fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: #8B0000;");
        btnElf.setStyle("-fx-padding: 20; -fx-border-color: #C6A664; -fx-border-width: 3; -fx-border-radius: 10; -fx-background-radius: 10; -fx-background-color: transparent; -fx-cursor: hand; -fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: #8B0000;");
        btnHalfling.setStyle("-fx-padding: 20; -fx-border-color: #C6A664; -fx-border-width: 3; -fx-border-radius: 10; -fx-background-radius: 10; -fx-background-color: transparent; -fx-cursor: hand; -fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: #8B0000;");
        btnHuman.setStyle("-fx-padding: 20; -fx-border-color: #C6A664; -fx-border-width: 3; -fx-border-radius: 10; -fx-background-radius: 10; -fx-background-color: transparent; -fx-cursor: hand; -fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: #8B0000;");
        btnDragonborn.setStyle("-fx-padding: 20; -fx-border-color: #C6A664; -fx-border-width: 3; -fx-border-radius: 10; -fx-background-radius: 10; -fx-background-color: transparent; -fx-cursor: hand; -fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: #8B0000;");
        btnGnome.setStyle("-fx-padding: 20; -fx-border-color: #C6A664; -fx-border-width: 3; -fx-border-radius: 10; -fx-background-radius: 10; -fx-background-color: transparent; -fx-cursor: hand; -fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: #8B0000;");
        btnHalfElf.setStyle("-fx-padding: 20; -fx-border-color: #C6A664; -fx-border-width: 3; -fx-border-radius: 10; -fx-background-radius: 10; -fx-background-color: transparent; -fx-cursor: hand; -fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: #8B0000;");
        btnHalfOrc.setStyle("-fx-padding: 20; -fx-border-color: #C6A664; -fx-border-width: 3; -fx-border-radius: 10; -fx-background-radius: 10; -fx-background-color: transparent; -fx-cursor: hand; -fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: #8B0000;");
        btnTiefling.setStyle("-fx-padding: 20; -fx-border-color: #C6A664; -fx-border-width: 3; -fx-border-radius: 10; -fx-background-radius: 10; -fx-background-color: transparent; -fx-cursor: hand; -fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: #8B0000;");
    }

    private void setupNavigationButtons() {
        if (btnBack != null) {
            btnBack.setOnAction(e -> navigateBack());
        }

        if (btnNext != null) {
            btnNext.setOnAction(e -> navigateNext());
        }
    }

    private void navigateBack() {
        try {
            MainView mainView = new MainView();
            Stage stage = (Stage) root.getScene().getWindow();
            new MainPresenter(mainView, stage);
            stage.setScene(new Scene(mainView.getRoot(), stage.getScene().getWidth(), stage.getScene().getHeight()));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void navigateNext() {
        String characterName = txtCharacterName != null ? txtCharacterName.getText().trim() : "";
        if (characterName.isEmpty()) {
            showError("Fehlender Name", "Bitte gib einen Charakternamen ein!");
            return;
        }

        if (selectedRaceName == null) {
            showError("Keine Rasse gewählt", "Bitte wähle eine Rasse!");
            return;
        }

        CharacterSession.getInstance().getCurrentCharacter().setName(characterName);

        // Load race from database and save to session
        var race = dbManager.getRaceByName(selectedRaceName);
        if (race != null) {
            CharacterSession.getInstance().getCurrentCharacter().setRace(race);
        }

        try {
            Stage stage = (Stage) btnNext.getScene().getWindow();
            SelectClassView nextView = new SelectClassView();
            stage.setScene(new Scene(nextView.getRoot(), stage.getScene().getWidth(), stage.getScene().getHeight()));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void showError(String title, String message) {
        javafx.scene.control.Alert alert = new javafx.scene.control.Alert(javafx.scene.control.Alert.AlertType.WARNING);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
