package com.dnd.creator.view;

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
    private Label currentlySelectedLabel = null;

    public AbilityScoresView() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/dnd/creator/view/AbilityScoresView.fxml"));
            loader.setController(this);
            root = loader.load();

            availableValues = new ArrayList<>(Arrays.asList(15, 14, 13, 12, 10, 8));
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
    }

    private void handleValueSelection(Integer value) {
        if (currentlySelectedLabel != null) {
            assignValueToAttribute(currentlySelectedLabel, value);
            availableValues.remove(value);
            initializeValuePool();
            currentlySelectedLabel = null;
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
        strValue.setOnMouseClicked(e -> selectLabel(strValue));
        dexValue.setOnMouseClicked(e -> selectLabel(dexValue));
        conValue.setOnMouseClicked(e -> selectLabel(conValue));
        intValue.setOnMouseClicked(e -> selectLabel(intValue));
        wisValue.setOnMouseClicked(e -> selectLabel(wisValue));
        chaValue.setOnMouseClicked(e -> selectLabel(chaValue));
    }

    private void selectLabel(Label label) {
        if (currentlySelectedLabel != null) {
            currentlySelectedLabel.getStyleClass().remove("selected");
            currentlySelectedLabel.setStyle(currentlySelectedLabel.getStyle().replace("-fx-border-color: #C6A664;", "-fx-border-color: transparent;"));
        }
        currentlySelectedLabel = label;
        label.getStyleClass().add("selected");
        label.setStyle(label.getStyle() + "-fx-border-color: #C6A664; -fx-background-color: #FFF8DC;");
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
