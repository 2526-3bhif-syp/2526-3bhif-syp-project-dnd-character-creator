package com.dnd.creator.view;
import com.dnd.creator.data.DbManager;
import com.dnd.creator.model.CharacterSession;
import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import java.util.Map;
public class SelectClassView {
    private Parent root;
    private final DbManager dbManager = new DbManager();
    private String selectedClass = null;
    private VBox selectedCard = null;
    private final java.util.Map<String, VBox> classCardMap = new java.util.HashMap<>();

    private static final String[][] CLASSES = {
        {"Barbarian", "d12", "STR"},
        {"Bard", "d8", "CHA"},
        {"Cleric", "d8", "WIS"},
        {"Druid", "d8", "WIS"},
        {"Fighter", "d10", "STR/DEX"},
        {"Monk", "d8", "DEX/WIS"},
        {"Paladin", "d10", "STR/CHA"},
        {"Ranger", "d10", "DEX/WIS"},
        {"Rogue", "d8", "DEX"},
        {"Sorcerer", "d6", "CHA"},
        {"Warlock", "d8", "CHA"},
        {"Wizard", "d6", "INT"}
    };
    public SelectClassView() {
        dbManager.connect();
        VBox mainBox = new VBox(20);
        mainBox.setPadding(new Insets(20));
        mainBox.setStyle("-fx-background-color: #F5F5DC;");
        javafx.scene.control.Label title = new javafx.scene.control.Label("Schritt 2: Klasse");
        title.setStyle("-fx-font-size: 32px; -fx-font-weight: bold; -fx-text-fill: #8B0000;");
        javafx.scene.control.Label subtitle = new javafx.scene.control.Label("Wähle deine Klasse.");
        subtitle.setStyle("-fx-font-size: 16px; -fx-text-fill: #1A1A1A;");
        VBox headerBox = new VBox(10);
        headerBox.getChildren().addAll(title, subtitle);
        headerBox.setAlignment(javafx.geometry.Pos.CENTER);
        VBox classGrid = createClassGrid();
        HBox buttonBox = new HBox(50);
        buttonBox.setPadding(new Insets(20));
        buttonBox.setAlignment(javafx.geometry.Pos.CENTER);
        Button btnBack = new Button("Zurück");
        btnBack.setPrefWidth(150);
        btnBack.setPrefHeight(50);
        btnBack.setStyle("-fx-background-color: #F5F5DC; -fx-border-color: #C6A664; -fx-border-width: 2; -fx-font-size: 16px;");
        btnBack.setOnAction(e -> {
            CreateCharacterView prevView = new CreateCharacterView();
            Stage stage = (Stage) root.getScene().getWindow();
            stage.setScene(new Scene(prevView.getRoot(), stage.getScene().getWidth(), stage.getScene().getHeight()));
        });
        Button btnNext = new Button("Weiter");
        btnNext.setPrefWidth(150);
        btnNext.setPrefHeight(50);
        btnNext.setStyle("-fx-background-color: #8B0000; -fx-text-fill: #F5F5DC; -fx-font-size: 16px; -fx-font-weight: bold;");
        btnNext.setOnAction(e -> {
            if (selectedClass != null) {
                AbilityScoresView nextView = new AbilityScoresView();
                Stage stage = (Stage) root.getScene().getWindow();
                stage.setScene(new Scene(nextView.getRoot(), stage.getScene().getWidth(), stage.getScene().getHeight()));
            } else {
                javafx.scene.control.Alert alert = new javafx.scene.control.Alert(javafx.scene.control.Alert.AlertType.WARNING);
                alert.setTitle("Warnung");
                alert.setHeaderText(null);
                alert.setContentText("Bitte wähle eine Klasse!");
                alert.showAndWait();
            }
        });
        buttonBox.getChildren().addAll(btnBack, btnNext);
        ScrollPane scrollPane = new ScrollPane(classGrid);
        scrollPane.setFitToWidth(true);
        scrollPane.setStyle("-fx-background-color: #F5F5DC;");
        mainBox.getChildren().addAll(headerBox, scrollPane, buttonBox);
        VBox.setVgrow(scrollPane, javafx.scene.layout.Priority.ALWAYS);
        root = mainBox;
    }
    private VBox createClassGrid() {
        VBox grid = new VBox(20);
        grid.setPadding(new Insets(20, 40, 20, 40));
        grid.setAlignment(javafx.geometry.Pos.TOP_CENTER);

        HBox row1 = new HBox(20);
        row1.setAlignment(javafx.geometry.Pos.CENTER);
        for (int i = 0; i < 5; i++) {
            VBox card = createClassButton(CLASSES[i]);
            classCardMap.put(CLASSES[i][0], card);
            row1.getChildren().add(card);
        }

        HBox row2 = new HBox(20);
        row2.setAlignment(javafx.geometry.Pos.CENTER);
        for (int i = 5; i < 10; i++) {
            VBox card = createClassButton(CLASSES[i]);
            classCardMap.put(CLASSES[i][0], card);
            row2.getChildren().add(card);
        }

        HBox row3 = new HBox(20);
        row3.setAlignment(javafx.geometry.Pos.CENTER);
        for (int i = 10; i < 12; i++) {
            VBox card = createClassButton(CLASSES[i]);
            classCardMap.put(CLASSES[i][0], card);
            row3.getChildren().add(card);
        }

        grid.getChildren().addAll(row1, row2, row3);
        
        // Wenn bereits eine Klasse gespeichert ist, markiere sie
        var character = CharacterSession.getInstance().getCurrentCharacter();
        String savedClass = character.getCharacterClass();
        if (savedClass != null && !savedClass.isEmpty() && classCardMap.containsKey(savedClass)) {
            VBox savedCard = classCardMap.get(savedClass);
            selectedClass = savedClass;
            selectedCard = savedCard;
            savedCard.setStyle(savedCard.getStyle().replace("transparent", "#4CAF50"));
        }
        
        return grid;
    }
    private VBox createClassButton(String[] classInfo) {
        String className = classInfo[0];
        String hitDie = classInfo[1];
        String mainAbility = classInfo[2];
        VBox card = new VBox(5);
        card.setPrefWidth(200);
        card.setPrefHeight(180);
        card.setAlignment(javafx.geometry.Pos.CENTER);
        card.setStyle("-fx-border-color: #C6A664; -fx-border-width: 3; -fx-border-radius: 10; " +
                      "-fx-background-color: transparent; -fx-background-radius: 10; -fx-cursor: hand; -fx-padding: 10;");
        javafx.scene.control.Label nameLabel = new javafx.scene.control.Label(className);
        nameLabel.setStyle("-fx-font-size: 22px; -fx-font-weight: bold; -fx-text-fill: #8B0000;");
        nameLabel.setWrapText(true);
        nameLabel.setTextAlignment(javafx.scene.text.TextAlignment.CENTER);
        javafx.scene.control.Label mainLabel = new javafx.scene.control.Label("Main: " + mainAbility);
        mainLabel.setStyle("-fx-font-size: 13px; -fx-text-fill: #1A1A1A;");
        mainLabel.setWrapText(true);
        mainLabel.setTextAlignment(javafx.scene.text.TextAlignment.CENTER);
        javafx.scene.control.Label dieLabel = new javafx.scene.control.Label("Hit Die: " + hitDie);
        dieLabel.setStyle("-fx-font-size: 11px; -fx-text-fill: #8B0000;");
        dieLabel.setWrapText(true);
        dieLabel.setTextAlignment(javafx.scene.text.TextAlignment.CENTER);
        card.getChildren().addAll(nameLabel, mainLabel, dieLabel);
        card.setOnMouseClicked(e -> selectClass(className, card));
        return card;
    }
    private void selectClass(String className, VBox card) {
        System.out.println("Klasse: " + className);
        if (selectedCard != null) {
            String style = selectedCard.getStyle();
            if (style.contains("#4CAF50")) {
                style = style.replace("-fx-background-color: #4CAF50;", "-fx-background-color: transparent;");
                selectedCard.setStyle(style);
            }
        }
        selectedClass = className;
        selectedCard = card;
        card.setStyle(card.getStyle().replace("transparent", "#4CAF50"));

        // Alte Ausrüstungs-Auswahlen löschen bei neuer Klassenwahl
        CharacterSession.getInstance().getCurrentCharacter().setSelectedEquipment(new java.util.ArrayList<>());

        Map<String, Object> classData = dbManager.getClassByName(className);
        if (classData != null) {
            CharacterSession.getInstance().getCurrentCharacter().setCharacterClass(className);
            CharacterSession.getInstance().getCurrentCharacter().setClassIndex((String) classData.get("index"));
            CharacterSession.getInstance().getCurrentCharacter().setClassHitDie((Integer) classData.get("hit_die"));
            CharacterSession.getInstance().getCurrentCharacter().setSpellcastingAbility((String) classData.get("spellcasting_ability"));
            @SuppressWarnings("unchecked")
            java.util.List<String> profs = (java.util.List<String>) classData.get("proficiencies");
            CharacterSession.getInstance().getCurrentCharacter().setClassProficiencies(profs);
            System.out.println("OK");
        }
    }
    public Parent getRoot() {
        return root;
    }
}
