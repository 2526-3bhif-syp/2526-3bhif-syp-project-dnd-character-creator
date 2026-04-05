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

    private void initializeEquipment() {
        int row = 0;
        int col = 0;
        
        for (String item : equipment) {
            CheckBox checkBox = new CheckBox(item);
            checkBox.setStyle("-fx-font-size: 14px; -fx-text-fill: #1A1A1A;");
            checkBox.setOnAction(e -> {
                if (checkBox.isSelected()) {
                    selectedEquipment.add(item);
                } else {
                    selectedEquipment.remove(item);
                }
            });
            equipmentGrid.add(checkBox, col, row);
            
            col++;
            if (col > 1) {
                col = 0;
                row++;
            }
        }
    }

    private void initializeSpells() {
        for (String spell : spells) {
            CheckBox checkBox = new CheckBox(spell);
            checkBox.setStyle("-fx-font-size: 14px; -fx-text-fill: #1A1A1A;");
            checkBox.setOnAction(e -> handleSpellSelection(checkBox, spell));
            spellsContainer.getChildren().add(checkBox);
        }
    }

    private void handleSpellSelection(CheckBox checkBox, String spell) {
        if (checkBox.isSelected()) {
            if (selectedSpells.size() >= MAX_SPELLS) {
                checkBox.setSelected(false);
                showError("Du kannst maximal " + MAX_SPELLS + " Zauber auswählen.");
            } else {
                selectedSpells.add(spell);
            }
        } else {
            selectedSpells.remove(spell);
        }
    }

    private void setupButtonHandlers() {
        btnBack.setOnAction(e -> navigateBack());
        btnNext.setOnAction(e -> navigateNext());
    }

    private void navigateBack() {
        try {
            Stage stage = (Stage) btnBack.getScene().getWindow();
            // TODO: Load previous view (SkillsView)
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void navigateNext() {
        if (selectedEquipment.isEmpty()) {
            showError("Bitte wähle mindestens ein Ausrüstungsgegenstand aus.");
            return;
        }
        
        try {
            Stage stage = (Stage) btnNext.getScene().getWindow();
            // TODO: Load next view (CharacterSummaryView)
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
