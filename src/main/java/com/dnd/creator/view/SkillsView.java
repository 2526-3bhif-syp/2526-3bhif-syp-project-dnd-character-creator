package com.dnd.creator.view;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.RadioButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class SkillsView {
    private Parent root;

    @FXML
    private VBox backgroundContainer;

    @FXML
    private VBox skillsContainer;

    @FXML
    private Button btnBack;

    @FXML
    private Button btnNext;

    private ToggleGroup backgroundGroup;
    private String selectedBackground = null;
    private List<String> selectedSkills = new ArrayList<>();
    private static final int MAX_SKILLS = 2;

    private List<String> backgrounds = Arrays.asList(
        "Acolyte", "Criminal", "Folk Hero", "Noble", "Sage", "Soldier"
    );

    private List<String> skills = Arrays.asList(
        "Acrobatics", "Animal Handling", "Arcana", "Athletics",
        "Deception", "History", "Insight", "Intimidation",
        "Investigation", "Medicine", "Nature", "Perception",
        "Performance", "Persuasion", "Religion", "Sleight of Hand",
        "Stealth", "Survival"
    );

    public SkillsView() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/dnd/creator/view/SkillsView.fxml"));
            loader.setController(this);
            root = loader.load();

            initializeBackgrounds();
            initializeSkills();
            setupButtonHandlers();

        } catch (IOException e) {
            throw new RuntimeException("Failed to load SkillsView.fxml", e);
        }
    }

    public Parent getRoot() {
        return root;
    }

    private void initializeBackgrounds() {
        backgroundGroup = new ToggleGroup();
        
        for (String background : backgrounds) {
            RadioButton radioButton = new RadioButton(background);
            radioButton.setToggleGroup(backgroundGroup);
            radioButton.setStyle("-fx-font-size: 16px; -fx-text-fill: #1A1A1A;");
            radioButton.setOnAction(e -> selectedBackground = background);
            backgroundContainer.getChildren().add(radioButton);
        }
    }

    private void initializeSkills() {
        for (String skill : skills) {
            CheckBox checkBox = new CheckBox(skill);
            checkBox.setStyle("-fx-font-size: 14px; -fx-text-fill: #1A1A1A;");
            checkBox.setOnAction(e -> handleSkillSelection(checkBox, skill));
            skillsContainer.getChildren().add(checkBox);
        }
    }

    private void handleSkillSelection(CheckBox checkBox, String skill) {
        if (checkBox.isSelected()) {
            if (selectedSkills.size() >= MAX_SKILLS) {
                checkBox.setSelected(false);
                showError("Du kannst maximal " + MAX_SKILLS + " Fertigkeiten auswählen.");
            } else {
                selectedSkills.add(skill);
            }
        } else {
            selectedSkills.remove(skill);
        }
    }

    private void setupButtonHandlers() {
        btnBack.setOnAction(e -> navigateBack());
        btnNext.setOnAction(e -> navigateNext());
    }

    private void navigateBack() {
        try {
            Stage stage = (Stage) btnBack.getScene().getWindow();
            // TODO: Load previous view (AbilityScoresView)
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void navigateNext() {
        if (selectedBackground == null) {
            showError("Bitte wähle einen Hintergrund aus.");
            return;
        }
        if (selectedSkills.size() != MAX_SKILLS) {
            showError("Bitte wähle genau " + MAX_SKILLS + " Fertigkeiten aus.");
            return;
        }
        
        try {
            Stage stage = (Stage) btnNext.getScene().getWindow();
            // TODO: Load next view (EquipmentView)
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void showError(String message) {
        javafx.scene.control.Alert alert = new javafx.scene.control.Alert(javafx.scene.control.Alert.AlertType.WARNING);
        alert.setTitle("Ungültige Auswahl");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
