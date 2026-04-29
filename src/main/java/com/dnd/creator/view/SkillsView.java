package com.dnd.creator.view;
import com.dnd.creator.data.DbManager;
import com.dnd.creator.model.CharacterSession;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.io.IOException;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.FlowPane;

public class SkillsView {
    private Parent root;

    @FXML private Button btnBack;
    @FXML private Button btnNext;
    @FXML private FlowPane backgroundContainer;
    @FXML private FlowPane skillsContainer;
    @FXML private Label lblSkillsTitle;
    @FXML private VBox backgroundSkillContainer;
    @FXML private FlowPane backgroundSkillDisplay;

    private final DbManager dbManager = new DbManager();
    private final ToggleGroup backgroundToggleGroup = new ToggleGroup();
    private final List<CheckBox> skillCheckboxes = new ArrayList<>();
    private int maxSkillsToChoose = 2;
    private List<String> backgroundGrantedSkills = new ArrayList<>();


    public SkillsView() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/dnd/creator/view/SkillsView.fxml"));
            loader.setController(this);
            root = loader.load();

            dbManager.connect();
            setupNavigationButtons();
            loadStepData();

        } catch (IOException e) {
            throw new RuntimeException("Failed to load SkillsView.fxml", e);
        }
    }

    private void setupNavigationButtons() {
        if (btnBack != null) {
            btnBack.setOnAction(e -> navigateBack());
        }

        if (btnNext != null) {
            btnNext.setOnAction(e -> navigateNext());
        }
    }

    private void loadStepData() {
        loadBackgroundOptions();
        loadSkillOptions();
        restoreSavedSelections();
    }

    private void loadBackgroundOptions() {
        backgroundContainer.getChildren().clear();
        List<String> backgrounds = dbManager.getAllBackgrounds();

        if (backgrounds.isEmpty()) {
            backgrounds = List.of("Acolyte");
        }

        for (String background : backgrounds) {
            RadioButton radio = new RadioButton(background);
            radio.setToggleGroup(backgroundToggleGroup);
            radio.setStyle("-fx-font-size: 14px; -fx-text-fill: #1A1A1A;");
            radio.setOnAction(e -> updateBackgroundSkills(background));
            backgroundContainer.getChildren().add(radio);
        }
    }

    @SuppressWarnings("unchecked")
    private void loadSkillOptions() {
        loadSkillOptionsFiltered(backgroundGrantedSkills);
    }

    @SuppressWarnings("unchecked")
    private void loadSkillOptionsFiltered(List<String> exclude) {
        skillsContainer.getChildren().clear();
        skillCheckboxes.clear();

        var character = CharacterSession.getInstance().getCurrentCharacter();
        String classIndex = character.getClassIndex();

        List<String> skillOptions;
        if (classIndex != null && !classIndex.isBlank()) {
            Map<String, Object> config = dbManager.getClassSkillSelectionConfig(classIndex);
            maxSkillsToChoose = (int) config.getOrDefault("choose", 2);
            skillOptions = (List<String>) config.getOrDefault("options", dbManager.getAllSkills());
        } else {
            maxSkillsToChoose = 2;
            skillOptions = dbManager.getAllSkills();
        }

        if (lblSkillsTitle != null) {
            lblSkillsTitle.setText("Fertigkeiten wählen (" + maxSkillsToChoose + "):");
        }

        for (String skill : skillOptions) {
            if (exclude.contains(skill)) continue;
            CheckBox checkBox = new CheckBox(skill);
            checkBox.setStyle("-fx-font-size: 14px; -fx-text-fill: #1A1A1A;");
            checkBox.setOnAction(e -> enforceSkillLimit());
            skillCheckboxes.add(checkBox);
            skillsContainer.getChildren().add(checkBox);
        }
    }

    private void restoreSavedSelections() {
        var character = CharacterSession.getInstance().getCurrentCharacter();

        if (character.getSelectedBackground() != null) {
            for (var node : backgroundContainer.getChildren()) {
                if (node instanceof RadioButton radio && character.getSelectedBackground().equals(radio.getText())) {
                    radio.setSelected(true);
                    break;
                }
            }
        }

        List<String> selectedSkills = character.getSelectedSkills();
        if (selectedSkills == null || selectedSkills.isEmpty()) {
            return;
        }

        for (CheckBox checkBox : skillCheckboxes) {
            checkBox.setSelected(selectedSkills.contains(checkBox.getText()));
        }
        enforceSkillLimit();

        if (character.getSelectedBackground() != null) {
            updateBackgroundSkills(character.getSelectedBackground());
        }
    }

    private void enforceSkillLimit() {
        long selectedCount = skillCheckboxes.stream().filter(CheckBox::isSelected).count();
        boolean limitReached = selectedCount >= maxSkillsToChoose;

        for (CheckBox checkBox : skillCheckboxes) {
            if (!checkBox.isSelected()) {
                checkBox.setDisable(limitReached);
            }
        }
    }

    private void navigateBack() {
        AbilityScoresView prevView = new AbilityScoresView();
        Stage stage = (Stage) root.getScene().getWindow();
        stage.setScene(new Scene(prevView.getRoot(), stage.getScene().getWidth(), stage.getScene().getHeight()));
    }

    private void navigateNext() {
        RadioButton selectedBackground = (RadioButton) backgroundToggleGroup.getSelectedToggle();
        if (selectedBackground == null) {
            showError("Bitte wähle einen Hintergrund aus.");
            return;
        }

        List<String> chosenSkills = skillCheckboxes.stream()
                .filter(CheckBox::isSelected)
                .map(CheckBox::getText)
                .toList();

        if (chosenSkills.size() != maxSkillsToChoose) {
            showError("Bitte wähle genau " + maxSkillsToChoose + " Fertigkeiten aus.");
            return;
        }

        List<String> allSkills = new ArrayList<>(backgroundGrantedSkills);
        allSkills.addAll(chosenSkills);

        var character = CharacterSession.getInstance().getCurrentCharacter();
        character.setSelectedBackground(selectedBackground.getText());
        character.setSelectedSkills(allSkills);

        EquipmentView nextView = new EquipmentView();
        Stage stage = (Stage) root.getScene().getWindow();
        stage.setScene(new Scene(nextView.getRoot(), stage.getScene().getWidth(), stage.getScene().getHeight()));
    }

    private void showError(String message) {
        javafx.scene.control.Alert alert = new javafx.scene.control.Alert(javafx.scene.control.Alert.AlertType.WARNING);
        alert.setTitle("Unvollständige Auswahl");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    public Parent getRoot() { return root; }

    private void updateBackgroundSkills(String backgroundName) {
        backgroundGrantedSkills = dbManager.getBackgroundSkills(backgroundName);

        backgroundSkillDisplay.getChildren().clear();
        for (String skill : backgroundGrantedSkills) {
            CheckBox cb = new CheckBox(skill);
            cb.setSelected(true);
            cb.setDisable(true);
            cb.setStyle("-fx-font-size: 13px; -fx-text-fill: #2d5a3d; -fx-opacity: 1;");
            backgroundSkillDisplay.getChildren().add(cb);
        }
        backgroundSkillContainer.setVisible(!backgroundGrantedSkills.isEmpty());
        backgroundSkillContainer.setManaged(!backgroundGrantedSkills.isEmpty());

        loadSkillOptionsFiltered(backgroundGrantedSkills);
        restoreChosenClassSkills();
    }

    private void restoreChosenClassSkills() {
        List<String> saved = CharacterSession.getInstance()
                .getCurrentCharacter().getSelectedSkills();
        if (saved == null) return;
        for (CheckBox cb : skillCheckboxes) {
            if (saved.contains(cb.getText())) cb.setSelected(true);
        }
        enforceSkillLimit();
    }
}
