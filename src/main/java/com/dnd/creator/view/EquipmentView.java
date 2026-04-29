package com.dnd.creator.view;
import com.dnd.creator.data.DbManager;
import com.dnd.creator.model.CharacterSession;
import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
public class EquipmentView {
    private Parent root;
    private final DbManager dbManager = new DbManager();
    private final Map<Integer, String> selectedChoices = new LinkedHashMap<>();
    private int totalOptions = 0;
    private java.util.List<String> savedEquipment;
    public EquipmentView() {
        dbManager.connect();

        // Lade bereits gespeicherte Equipment-Auswahl (nicht löschen!)
        var character = CharacterSession.getInstance().getCurrentCharacter();
        java.util.List<String> existingEquipment = character.getSelectedEquipment();
        if (existingEquipment != null) {
            for (String equipment : existingEquipment) {
                // Parse equipment string to get the choice letter and update selectedChoices
                if (equipment.contains(")")) {
                    String[] parts = equipment.split("\\)", 2);
                    if (parts.length == 2) {
                        String choiceStr = parts[0].trim(); // e.g., "A"
                        String description = parts[1].trim();
                        // Versuche die order_num zu rekonstruieren (wird später durchlaufen)
                    }
                }
            }
        }

        VBox mainBox = new VBox(20);
        mainBox.setPadding(new Insets(20));
        mainBox.setStyle("-fx-background-color: #F5F5DC;");
        Label title = new Label("Schritt 5: Ausrüstung");
        title.setStyle("-fx-font-size: 32px; -fx-font-weight: bold; -fx-text-fill: #8B0000;");
        Label subtitle = new Label("Wähle deine Startausrüstung");
        subtitle.setStyle("-fx-font-size: 14px; -fx-text-fill: #666666;");
        VBox headerBox = new VBox(8);
        headerBox.getChildren().addAll(title, subtitle);
        headerBox.setAlignment(javafx.geometry.Pos.CENTER);
        VBox equipmentContent = createEquipmentSelection();
        HBox buttonBox = new HBox(50);
        buttonBox.setPadding(new Insets(20));
        buttonBox.setAlignment(javafx.geometry.Pos.CENTER);
        Button btnBack = new Button("Zurück");
        btnBack.setPrefWidth(150);
        btnBack.setPrefHeight(50);
        btnBack.setStyle("-fx-background-color: #F5F5DC; -fx-border-color: #C6A664; -fx-border-width: 2; -fx-font-size: 16px;");
        btnBack.setOnAction(e -> {
            SkillsView prevView = new SkillsView();
            Stage stage = (Stage) root.getScene().getWindow();
            stage.setScene(new Scene(prevView.getRoot(), stage.getScene().getWidth(), stage.getScene().getHeight()));
        });
        Button btnNext = new Button("Weiter");
        btnNext.setPrefWidth(150);
        btnNext.setPrefHeight(50);
        btnNext.setStyle("-fx-background-color: #8B0000; -fx-text-fill: #F5F5DC; -fx-font-size: 16px; -fx-font-weight: bold;");
        btnNext.setOnAction(e -> {
            if (selectedChoices.size() < totalOptions) {
                Alert alert = new Alert(Alert.AlertType.WARNING);
                alert.setTitle("Unvollständige Auswahl");
                alert.setHeaderText(null);
                alert.setContentText("Bitte wähle alle Ausrüstungs-Optionen aus bevor du fortfährst!");
                alert.showAndWait();
                return;
            }

            var figure= CharacterSession.getInstance().getCurrentCharacter();
            String classIdx = figure.getClassIndex();

            // Komplett neu aufbauen um Duplikate zu vermeiden
            List<String> allEquipment = new ArrayList<>();

            // 1. Pflichtausrüstung
            List<String> mandatory = dbManager.getStartingEquipment(classIdx);
            new java.util.LinkedHashSet<>(mandatory).forEach(allEquipment::add);

            // 2. Holy Symbol für divine Klassen
            if (classIdx != null) {
                String lower = classIdx.toLowerCase();
                if (lower.equals("cleric") || lower.equals("paladin")) {
                    allEquipment.add("Holy Symbol");
                }
            }

            // 3. Gewählte Optionen
            allEquipment.addAll(selectedChoices.values());

            character.setSelectedEquipment(allEquipment);

            CharacterSummaryView nextView = new CharacterSummaryView();
            Stage stage = (Stage) root.getScene().getWindow();
            stage.setScene(new Scene(nextView.getRoot(), stage.getScene().getWidth(), stage.getScene().getHeight()));
        });
        buttonBox.getChildren().addAll(btnBack, btnNext);
        ScrollPane scrollPane = new ScrollPane(equipmentContent);
        scrollPane.setFitToWidth(true);
        scrollPane.setStyle("-fx-background-color: #F5F5DC;");
        mainBox.getChildren().addAll(headerBox, scrollPane, buttonBox);
        VBox.setVgrow(scrollPane, javafx.scene.layout.Priority.ALWAYS);
        root = mainBox;
    }
    private VBox createEquipmentSelection() {
        VBox container = new VBox(15);
        container.setPadding(new Insets(20, 40, 20, 40));
        container.setAlignment(javafx.geometry.Pos.TOP_LEFT);
        var character = CharacterSession.getInstance().getCurrentCharacter();
        String classIndex = character.getClassIndex();
        if (classIndex == null || classIndex.isEmpty()) {
            Label error = new Label("Keine Klasse ausgewählt!");
            container.getChildren().add(error);
            return container;
        }
        // Lade bereits gespeicherte Equipment-Auswahl und rekonstruiere selectedChoices
        java.util.List<String> savedEquipment = character.getSelectedEquipment();

        // Wahl-Ausrüstung
        List<Map<String, Object>> options = dbManager.getEquipmentOptions(classIndex);
        Map<Integer, Map<String, Object>> uniqueOptions = new LinkedHashMap<>();
        for (Map<String, Object> option : options) {
            int orderNum = (Integer) option.get("order_num");
            if (!uniqueOptions.containsKey(orderNum)) {
                uniqueOptions.put(orderNum, option);
            }
        }

        // Rekonstruiere selectedChoices mit den richtigen orderNum keys
        selectedChoices.clear();
        int index = 0;
        for (Integer orderNum : uniqueOptions.keySet()) {
            if (index < savedEquipment.size() && savedEquipment.get(index) != null) {
                String equipment = savedEquipment.get(index);
                if (!equipment.isEmpty()) {
                    selectedChoices.put(orderNum, equipment);
                }
            }
            index++;
        }
        // Pflicht-Ausrüstung (de-dupliziert)
        List<String> mandatory = dbManager.getStartingEquipment(classIndex);
        java.util.Set<String> uniqueMandatory = new java.util.LinkedHashSet<>(mandatory);
        if (!uniqueMandatory.isEmpty()) {
            Label mandatoryTitle = new Label("Pflicht-Ausrüstung:");
            mandatoryTitle.setStyle("-fx-font-size: 13px; -fx-font-weight: bold; -fx-text-fill: #8B0000; -fx-padding: 10 0 5 0;");
            container.getChildren().add(mandatoryTitle);
            for (String item : uniqueMandatory) {
                // Überspringe leere Items
                if (item == null || item.trim().isEmpty()) {
                    continue;
                }
                HBox itemBox = new HBox(10);
                itemBox.setStyle("-fx-padding: 5;");
                Label bullet = new Label("✓");
                bullet.setStyle("-fx-font-size: 12px; -fx-text-fill: #4CAF50; -fx-font-weight: bold;");
                Label itemLabel = new Label(item);
                itemLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: #1A1A1A;");
                itemLabel.setWrapText(true);
                itemBox.getChildren().addAll(bullet, itemLabel);
                container.getChildren().add(itemBox);
            }
            Label spacing = new Label("");
            container.getChildren().add(spacing);
        }
        // Wahl-Ausrüstung bereits definiert oben
        totalOptions = uniqueOptions.size();
        if (!uniqueOptions.isEmpty()) {
            Label optionsTitle = new Label("Wähle deine Ausrüstung:");
            optionsTitle.setStyle("-fx-font-size: 13px; -fx-font-weight: bold; -fx-text-fill: #8B0000; -fx-padding: 10 0 10 0;");
            container.getChildren().add(optionsTitle);
            for (Map.Entry<Integer, Map<String, Object>> entry : uniqueOptions.entrySet()) {
                int orderNum = entry.getKey();
                String desc = (String) entry.getValue().get("description");
                List<String> optionChoices = parseEquipmentOptions(desc);
                VBox optionBox = createOptionBox(orderNum, desc, optionChoices);
                container.getChildren().add(optionBox);
            }
        }
        return container;
    }
    private VBox createOptionBox(int orderNum, String fullDescription, List<String> optionChoices) {
        VBox optionBox = new VBox(8);
        optionBox.setStyle("-fx-border-color: #D4A574; -fx-border-width: 1; -fx-padding: 12; " +
                          "-fx-background-color: #FFFBF5; -fx-border-radius: 5;");
        // Große Überschrift mit vollständiger Beschreibung
        Label descLabel = new Label(fullDescription);
        descLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: #333333; -fx-wrap-text: true;");
        descLabel.setWrapText(true);
        optionBox.getChildren().add(descLabel);
        Label spacer = new Label("");
        optionBox.getChildren().add(spacer);
        // RadioButtons nur mit A, B, C, etc.
        ToggleGroup group = new ToggleGroup();

        // Überprüfe, ob für diesen orderNum bereits eine Auswahl gespeichert ist
        String savedSelection = selectedChoices.get(orderNum);

        for (int i = 0; i < optionChoices.size(); i++) {
            String choice = optionChoices.get(i);
            String letter = (char) ('A' + i) + "";
            HBox choiceBox = new HBox(15);
            choiceBox.setStyle("-fx-padding: 6;");
            choiceBox.setAlignment(javafx.geometry.Pos.CENTER_LEFT);
            RadioButton radio = new RadioButton(letter);
            radio.setToggleGroup(group);
            radio.setStyle("-fx-font-size: 12px;");
            radio.setPrefWidth(40);
            Label choiceLabel = new Label(choice);
            choiceLabel.setStyle("-fx-font-size: 11px; -fx-text-fill: #444444;");
            choiceLabel.setWrapText(true);

            // Wenn diese Option bereits ausgewählt war, markiere sie
            String choiceStr = letter + ") " + choice;
            if (savedSelection != null && savedSelection.equals(choiceStr)) {
                radio.setSelected(true);
            }

            radio.setOnAction(e -> {
                if (radio.isSelected()) {
                    selectedChoices.put(orderNum, letter + ") " + choice);
                    CharacterSession.getInstance().getCurrentCharacter().addSelectedEquipment(letter + ") " + choice);
                }
            });
            choiceBox.getChildren().addAll(radio, choiceLabel);
            optionBox.getChildren().add(choiceBox);
        }
        return optionBox;
    }
    private List<String> parseEquipmentOptions(String description) {
        List<String> choices = new ArrayList<>();
        String[] parts = description.split(" or ");
        for (String part : parts) {
            String cleaned = part.trim();
            if (cleaned.matches("^\\([a-z]\\)\\s*.*")) {
                cleaned = cleaned.replaceFirst("^\\([a-z]\\)\\s*", "");
            }
            if (!cleaned.isEmpty()) {
                choices.add(cleaned);
            }
        }
        return choices;
    }
    public Parent getRoot() {
        return root;
    }
}
