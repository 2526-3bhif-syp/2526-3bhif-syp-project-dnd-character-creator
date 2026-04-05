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

