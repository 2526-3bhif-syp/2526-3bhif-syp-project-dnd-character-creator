package com.dnd.creator.view;

import com.dnd.creator.model.CharacterSession;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class AbilityScoresView {
    private Parent root;

    @FXML
    private HBox valuePool;

    @FXML
    private Label strValue;

    @FXML
    private Label dexValue;

    @FXML
    private Label conValue;

    @FXML
    private Label intValue;

    @FXML
    private Label wisValue;

    @FXML
    private Label chaValue;

    @FXML
    private Button btnBack;

    @FXML
    private Button btnNext;

    private List<Integer> availableValues;
    private Integer selectedSTR = null;
    private Integer selectedDEX = null;
    private Integer selectedCON = null;
    private Integer selectedINT = null;
    private Integer selectedWIS = null;
    private Integer selectedCHA = null;

    public AbilityScoresView() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/dnd/creator/view/AbilityScoresView.fxml"));
            loader.setController(this);
            root = loader.load();

            availableValues = new ArrayList<>(Arrays.asList(15, 14, 13, 12, 10, 8));

            // Lade gespeicherte Werte aus der Session
            loadSavedAbilityScores();

            initializeValuePool();
            setupLabelHandlers();
            setupButtonHandlers();

        } catch (IOException e) {
            throw new RuntimeException("Failed to load AbilityScoresView.fxml", e);
        }
    }

    public Parent getRoot() {
        return root;
    }

    private void loadSavedAbilityScores() {
        var character = CharacterSession.getInstance().getCurrentCharacter();

        // Lade alle gespeicherten Werte
        int str = character.getStrength();
        int dex = character.getDexterity();
        int con = character.getConstitution();
        int intel = character.getIntelligence();
        int wis = character.getWisdom();
        int cha = character.getCharisma();

        // Wenn Werte vorhanden sind, stelle sie wieder her
        if (str != 0) {
            selectedSTR = str;
            availableValues.remove(Integer.valueOf(str));
        }
        if (dex != 0) {
            selectedDEX = dex;
            availableValues.remove(Integer.valueOf(dex));
        }
        if (con != 0) {
            selectedCON = con;
            availableValues.remove(Integer.valueOf(con));
        }
        if (intel != 0) {
            selectedINT = intel;
            availableValues.remove(Integer.valueOf(intel));
        }
        if (wis != 0) {
            selectedWIS = wis;
            availableValues.remove(Integer.valueOf(wis));
        }
        if (cha != 0) {
            selectedCHA = cha;
            availableValues.remove(Integer.valueOf(cha));
        }
    }

    private void initializeValuePool() {
        valuePool.getChildren().clear();
        
        for (Integer value : availableValues) {
            Button valueBtn = new Button(value.toString());
            valueBtn.getStyleClass().add("value-button");
            valueBtn.setStyle("-fx-background-color: #8B0000; -fx-text-fill: #F5F5DC; " +
                            "-fx-font-size: 18px; -fx-font-weight: bold; " +
                            "-fx-min-width: 60px; -fx-min-height: 60px; " +
                            "-fx-background-radius: 10; -fx-cursor: hand;");
            valueBtn.setOnAction(e -> handleValueSelection(value));
            valuePool.getChildren().add(valueBtn);
        }

        // Aktualisiere alle Label mit gespeicherten Werten
        strValue.setText(selectedSTR != null ? selectedSTR.toString() : "-");
        dexValue.setText(selectedDEX != null ? selectedDEX.toString() : "-");
        conValue.setText(selectedCON != null ? selectedCON.toString() : "-");
        intValue.setText(selectedINT != null ? selectedINT.toString() : "-");
        wisValue.setText(selectedWIS != null ? selectedWIS.toString() : "-");
        chaValue.setText(selectedCHA != null ? selectedCHA.toString() : "-");
    }

    private void handleValueSelection(Integer value) {
        Label targetLabel = null;
        if (selectedSTR == null) targetLabel = strValue;
        else if (selectedDEX == null) targetLabel = dexValue;
        else if (selectedCON == null) targetLabel = conValue;
        else if (selectedINT == null) targetLabel = intValue;
        else if (selectedWIS == null) targetLabel = wisValue;
        else if (selectedCHA == null) targetLabel = chaValue;

        if (targetLabel != null) {
            assignValueToAttribute(targetLabel, value);
            availableValues.remove(value);
            initializeValuePool();
        }
    }

    private void assignValueToAttribute(Label label, Integer value) {
        label.setText(value.toString());
        
        if (label == strValue) selectedSTR = value;
        else if (label == dexValue) selectedDEX = value;
        else if (label == conValue) selectedCON = value;
        else if (label == intValue) selectedINT = value;
        else if (label == wisValue) selectedWIS = value;
        else if (label == chaValue) selectedCHA = value;
    }

    private void setupLabelHandlers() {
        strValue.setOnMouseClicked(e -> unassignAttribute(strValue));
        dexValue.setOnMouseClicked(e -> unassignAttribute(dexValue));
        conValue.setOnMouseClicked(e -> unassignAttribute(conValue));
        intValue.setOnMouseClicked(e -> unassignAttribute(intValue));
        wisValue.setOnMouseClicked(e -> unassignAttribute(wisValue));
        chaValue.setOnMouseClicked(e -> unassignAttribute(chaValue));
    }

    private void unassignAttribute(Label label) {
        Integer val = null;
        if (label == strValue) { val = selectedSTR; selectedSTR = null; }
        else if (label == dexValue) { val = selectedDEX; selectedDEX = null; }
        else if (label == conValue) { val = selectedCON; selectedCON = null; }
        else if (label == intValue) { val = selectedINT; selectedINT = null; }
        else if (label == wisValue) { val = selectedWIS; selectedWIS = null; }
        else if (label == chaValue) { val = selectedCHA; selectedCHA = null; }

        if (val != null) {
            label.setText("-");
            availableValues.add(val);
            availableValues.sort((a, b) -> b.compareTo(a));
            initializeValuePool();
        }
    }

    private void setupButtonHandlers() {
        btnBack.setOnAction(e -> navigateBack());
        btnNext.setOnAction(e -> navigateNext());
    }

    private void navigateBack() {
        try {
            Stage stage = (Stage) btnBack.getScene().getWindow();
            SelectClassView previousView = new SelectClassView();
            stage.setScene(new Scene(previousView.getRoot(), stage.getScene().getWidth(), stage.getScene().getHeight()));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void navigateNext() {
        if (!allValuesAssigned()) {
            showError("Please assign all ability scores before continuing.");
            return;
        }

        // Save ability scores to session
        CharacterSession.getInstance().getCurrentCharacter().setStrength(selectedSTR);
        CharacterSession.getInstance().getCurrentCharacter().setDexterity(selectedDEX);
        CharacterSession.getInstance().getCurrentCharacter().setConstitution(selectedCON);
        CharacterSession.getInstance().getCurrentCharacter().setIntelligence(selectedINT);
        CharacterSession.getInstance().getCurrentCharacter().setWisdom(selectedWIS);
        CharacterSession.getInstance().getCurrentCharacter().setCharisma(selectedCHA);

        try {
            Stage stage = (Stage) btnNext.getScene().getWindow();
            SkillsView nextView = new SkillsView();
            stage.setScene(new Scene(nextView.getRoot(), stage.getScene().getWidth(), stage.getScene().getHeight()));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private boolean allValuesAssigned() {
        return selectedSTR != null && selectedDEX != null && 
               selectedCON != null && selectedINT != null && 
               selectedWIS != null && selectedCHA != null;
    }

    private void showError(String message) {
        javafx.scene.control.Alert alert = new javafx.scene.control.Alert(javafx.scene.control.Alert.AlertType.WARNING);
        alert.setTitle("Incomplete Selection");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
