package com.dnd.creator.view;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class EquipmentView {
    private Parent root;

    @FXML
    private GridPane equipmentGrid;

    @FXML
    private VBox spellsSection;

    @FXML
    private VBox spellsContainer;

    @FXML
    private Button btnBack;

    @FXML
    private Button btnNext;

    private List<String> selectedEquipment = new ArrayList<>();
    private List<String> selectedSpells = new ArrayList<>();
    private static final int MAX_SPELLS = 3;

    private List<String> equipment = Arrays.asList(
        "Longsword", "Shield", "Leather Armor", "Backpack",
        "Rope (50ft)", "Torch (10)", "Rations (10 days)", "Waterskin"
    );

    private List<String> spells = Arrays.asList(
        "Fire Bolt", "Mage Hand", "Light", "Shield",
        "Magic Missile", "Healing Word", "Cure Wounds", "Bless"
    );

    public EquipmentView() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/dnd/creator/view/EquipmentView.fxml"));
            loader.setController(this);
            root = loader.load();

            initializeEquipment();
            initializeSpells();
            setupButtonHandlers();

        } catch (IOException e) {
            throw new RuntimeException("Failed to load EquipmentView.fxml", e);
        }
    }

    public Parent getRoot() {
        return root;
    }
