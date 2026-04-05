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

    private void setupButtonHandlers() {
        btnBack.setOnAction(e -> navigateBack());
        btnNext.setOnAction(e -> navigateNext());
    }

    private void navigateBack() {
        try {
            Stage stage = (Stage) btnBack.getScene().getWindow();
            // TODO: Load previous view (RaceSelectionView)
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
