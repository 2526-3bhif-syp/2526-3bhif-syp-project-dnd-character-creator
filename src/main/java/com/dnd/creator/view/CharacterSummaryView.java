package com.dnd.creator.view;

import com.dnd.creator.data.DbManager;
import com.dnd.creator.model.CharacterModel;
import com.dnd.creator.model.CharacterSession;
import com.dnd.creator.model.Race;
import com.dnd.creator.presenter.MainPresenter;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.scene.layout.HBox;
import javafx.scene.control.CheckBox;
import javafx.geometry.Pos;
import javafx.stage.Stage;
import javafx.scene.layout.GridPane;
import javafx.scene.control.TextArea;
import javafx.scene.layout.Priority;
import javafx.geometry.Insets;

import java.io.IOException;
import java.util.Map;

public class CharacterSummaryView {
    private Parent root;

    @FXML
    private Label lblCharacterName;

    @FXML
    private Label lblClassLevel;

    @FXML
    private Label lblBackground;

    @FXML
    private Label lblPlayerName;

    @FXML
    private Label lblRace;

    @FXML
    private Label lblAlignment;

    @FXML
    private Label lblExp;

    @FXML
    private Label lblPassiveWisdom;

    @FXML
    private Button btnBack;

    @FXML
    private Button btnSave;

    @FXML
    private VBox leftColumn;

    @FXML
    private VBox middleColumn;

    @FXML
    private VBox rightColumn;

    private DbManager dbManager = new DbManager();

    public CharacterSummaryView() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/dnd/creator/view/CharacterSummaryView.fxml"));
            loader.setController(this);
            root = loader.load();

            dbManager.connect();
            loadCharacterData();
            setupButtonHandlers();

        } catch (IOException e) {
            throw new RuntimeException("Failed to load CharacterSummaryView.fxml", e);
        }
    }

    public Parent getRoot() {
        return root;
    }

    private void loadCharacterData() {
        var character = CharacterSession.getInstance().getCurrentCharacter();

        // Character name
        if (character.getName() != null && !character.getName().isEmpty()) {
            lblCharacterName.setText(character.getName());
        } else {
            lblCharacterName.setText("Neuer Charakter");
        }

        // Top Fields
        if (character.getCharacterClass() != null && !character.getCharacterClass().isEmpty()) {
            lblClassLevel.setText(character.getCharacterClass() + " 1");
        } else {
            lblClassLevel.setText("Keine Klasse");
        }

        if (character.getSelectedBackground() != null && !character.getSelectedBackground().isBlank()) {
            lblBackground.setText(character.getSelectedBackground());
        } else {
            lblBackground.setText("Kein Hintergrund");
        }

        lblPlayerName.setText("Spieler");

        if (character.getRace() != null) {
            lblRace.setText(character.getRace().getName());
        } else {
            lblRace.setText("Keine Rasse");
        }

        lblAlignment.setText("Neutral");
        lblExp.setText("0");

        // Passive Wisdom
        int wisBonus = character.getRace() != null ? character.getRace().getAbilityBonuses().getOrDefault("WIS", 0) : 0;
        int wis = character.getWisdom() + wisBonus;
        int wisMod = (wis - 10) / 2;
        boolean proficientInPerception = character.getSelectedSkills() != null && character.getSelectedSkills().contains("Perception");
        int passiveWisdom = 10 + wisMod + (proficientInPerception ? 2 : 0);
        lblPassiveWisdom.setText(String.valueOf(passiveWisdom));

        // We will build columns step by step, for now just clear them
        leftColumn.getChildren().clear();
        middleColumn.getChildren().clear();
        rightColumn.getChildren().clear();

        populateLeftColumn(character);
        populateMiddleColumn(character);
        populateRightColumn(character);
    }

    private void populateRightColumn(CharacterModel character) {
        rightColumn.getChildren().add(createTextBox("PERSONALITY TRAITS", true));
        rightColumn.getChildren().add(createTextBox("IDEALS", true));
        rightColumn.getChildren().add(createTextBox("BONDS", true));
        rightColumn.getChildren().add(createTextBox("FLAWS", true));
    }

    private VBox createTextBox(String title, boolean grow) {
        VBox box = new VBox(2);
        box.setStyle("-fx-border-color: #1A1A1A; -fx-padding: 5; -fx-background-color: white; -fx-background-radius: 5; -fx-border-radius: 5;");
        if(grow) {
            VBox.setVgrow(box, Priority.ALWAYS);
        }

        TextArea textArea = new TextArea();
        textArea.setWrapText(true);
        textArea.setStyle("-fx-background-color: transparent; -fx-border-color: transparent; -fx-control-inner-background: white; -fx-font-size: 11px;");
        if(grow) {
            VBox.setVgrow(textArea, Priority.ALWAYS);
        } else {
            textArea.setPrefRowCount(2);
        }

        Label lblTitle = new Label(title);
        lblTitle.setStyle("-fx-font-size: 10px; -fx-font-weight: bold; -fx-alignment: center; -fx-pref-width: 200;");

        box.getChildren().addAll(textArea, lblTitle);
        return box;
    }

    private void populateMiddleColumn(CharacterModel character) {
        int dexMod = (character.getDexterity() - 10) / 2;
        int ac = 10 + dexMod;
        String raceName = character.getRace() != null ? character.getRace().getName() : "Human";
        int speed = getRaceSpeed(raceName);

        HBox topStatsBox = new HBox(10);
        topStatsBox.setAlignment(Pos.CENTER);
        topStatsBox.getChildren().addAll(
            createShieldBox("ARMOR\nCLASS", String.valueOf(ac)),
            createStatBox("INITIATIVE", (dexMod >= 0 ? "+" : "") + dexMod),
            createStatBox("SPEED", String.valueOf(speed))
        );
        middleColumn.getChildren().add(topStatsBox);

        // HPC Row
        HBox hpHBox = new HBox(10);

        // HP Section
        int maxHp = calculateHP(character);
        VBox hpBox = new VBox(5);
        hpBox.setStyle("-fx-border-color: #1A1A1A; -fx-padding: 5; -fx-background-color: white; -fx-background-radius: 5; -fx-border-radius: 5;");
        HBox.setHgrow(hpBox, Priority.ALWAYS);

        HBox hpLabels = new HBox(10);
        Label lblHpMax = new Label("Hit Point Maximum: " + maxHp);
        lblHpMax.setStyle("-fx-font-size: 10px; -fx-text-fill: #555;");
        hpLabels.getChildren().add(lblHpMax);

        Label lblHpValue = new Label(String.valueOf(maxHp));
        lblHpValue.setStyle("-fx-font-size: 18px; -fx-padding: 5; -fx-alignment: center;");

        Label lblHpCurrent = new Label("CURRENT HIT POINTS");
        lblHpCurrent.setStyle("-fx-font-size: 10px; -fx-font-weight: bold;");

        hpBox.getChildren().addAll(hpLabels, lblHpValue, lblHpCurrent);

        VBox tempHpBox = new VBox(5);
        tempHpBox.setStyle("-fx-border-color: #1A1A1A; -fx-padding: 5; -fx-background-color: white; -fx-background-radius: 5; -fx-border-radius: 5;");
        HBox.setHgrow(tempHpBox, Priority.ALWAYS);
        Label lblTempHpValue = new Label("");
        lblTempHpValue.setStyle("-fx-font-size: 18px; -fx-padding: 5; -fx-alignment: center;");
        Label lblTempHp = new Label("TEMP HIT POINTS");
        lblTempHp.setStyle("-fx-font-size: 10px; -fx-font-weight: bold;");
        tempHpBox.getChildren().addAll(lblTempHpValue, lblTempHp);

        hpHBox.getChildren().addAll(hpBox, tempHpBox);

        // Hit Dice & Death Saves
        HBox diceSavesBox = new HBox(10);

        VBox hitDiceBox = new VBox(5);
        hitDiceBox.setStyle("-fx-border-color: #1A1A1A; -fx-padding: 5; -fx-background-color: white; -fx-background-radius: 5; -fx-border-radius: 5;");
        HBox.setHgrow(hitDiceBox, Priority.ALWAYS);
        Label lblHdTotal = new Label("Total: 1d" + (character.getClassHitDie() == 0 ? 6 : character.getClassHitDie()));
        lblHdTotal.setStyle("-fx-font-size: 10px; -fx-text-fill: #555;");
        Label lblHdValue = new Label("1d" + (character.getClassHitDie() == 0 ? 6 : character.getClassHitDie()));
        lblHdValue.setStyle("-fx-font-size: 14px; -fx-alignment: center;");
        Label lblHdText = new Label("HIT DICE");
        lblHdText.setStyle("-fx-font-size: 10px; -fx-font-weight: bold;");
        hitDiceBox.getChildren().addAll(lblHdTotal, lblHdValue, lblHdText);

        VBox deathBox = new VBox(5);
        deathBox.setStyle("-fx-border-color: #1A1A1A; -fx-padding: 5; -fx-background-color: white; -fx-background-radius: 5; -fx-border-radius: 5;");
        HBox.setHgrow(deathBox, Priority.ALWAYS);

        HBox successes = new HBox(5);
        successes.getChildren().addAll(new Label("SUCCESSES"), new CheckBox(), new CheckBox(), new CheckBox());
        successes.setStyle("-fx-font-size: 9px;");

        HBox failures = new HBox(5);
        failures.getChildren().addAll(new Label("FAILURES  "), new CheckBox(), new CheckBox(), new CheckBox());
        failures.setStyle("-fx-font-size: 9px;");

        Label lblDeathText = new Label("DEATH SAVES");
        lblDeathText.setStyle("-fx-font-size: 10px; -fx-font-weight: bold;");
        deathBox.getChildren().addAll(successes, failures, lblDeathText);

        diceSavesBox.getChildren().addAll(hitDiceBox, deathBox);

        // Attacks & Spellcasting
        VBox attacksBox = new VBox(5);
        attacksBox.setStyle("-fx-border-color: #1A1A1A; -fx-padding: 5; -fx-background-color: white; -fx-background-radius: 5; -fx-border-radius: 5; -fx-min-height: 150;");

        GridPane atkGrid = new GridPane();
        atkGrid.setHgap(10);
        atkGrid.setVgap(5);

        Label lN = new Label("NAME"); lN.setStyle("-fx-font-size: 9px; -fx-font-weight: bold; -fx-text-fill: #555;");
        Label lA = new Label("ATK BONUS"); lA.setStyle("-fx-font-size: 9px; -fx-font-weight: bold; -fx-text-fill: #555;");
        Label lD = new Label("DAMAGE/TYPE"); lD.setStyle("-fx-font-size: 9px; -fx-font-weight: bold; -fx-text-fill: #555;");

        atkGrid.add(lN, 0, 0);
        atkGrid.add(lA, 1, 0);
        atkGrid.add(lD, 2, 0);

        Label atkPlaceholder = new Label("Unarmed Strike"); atkPlaceholder.setStyle("-fx-font-size: 11px;");
        int strModForAttack = (character.getStrength() - 10) / 2;
        Label AtkBonusPlaceholder = new Label((strModForAttack >= 0 ? "+" : "") + strModForAttack); AtkBonusPlaceholder.setStyle("-fx-font-size: 11px;");
        Label dmgPlaceholder = new Label("1 Bludgeoning"); dmgPlaceholder.setStyle("-fx-font-size: 11px;");

        atkGrid.add(atkPlaceholder, 0, 1);
        atkGrid.add(AtkBonusPlaceholder, 1, 1);
        atkGrid.add(dmgPlaceholder, 2, 1);

        Label lblAttacksText = new Label("ATTACKS & SPELLCASTING");
        lblAttacksText.setStyle("-fx-font-size: 10px; -fx-font-weight: bold; -fx-padding: 5 0 0 0;");

        attacksBox.getChildren().addAll(atkGrid, lblAttacksText);

        middleColumn.getChildren().addAll(hpHBox, diceSavesBox, attacksBox);
    }

    private VBox createStatBox(String title, String value) {
        VBox box = new VBox(2);
        box.setStyle("-fx-border-color: #1A1A1A; -fx-border-radius: 5; -fx-background-color: white; -fx-background-radius: 5; -fx-alignment: center; -fx-pref-width: 80; -fx-pref-height: 80;");
        Label valLbl = new Label(value); valLbl.setStyle("-fx-font-size: 24px;");
        Label titleLbl = new Label(title); titleLbl.setStyle("-fx-font-size: 9px; -fx-font-weight: bold;");
        box.getChildren().addAll(valLbl, titleLbl);
        return box;
    }

    private VBox createShieldBox(String title, String value) {
        VBox box = new VBox(2);
        box.setStyle("-fx-border-color: #1A1A1A; -fx-border-radius: 10 10 30 30; -fx-background-color: white; -fx-background-radius: 10 10 30 30; -fx-alignment: center; -fx-pref-width: 80; -fx-pref-height: 80;");
        Label valLbl = new Label(value); valLbl.setStyle("-fx-font-size: 24px; -fx-font-weight: bold;");
        Label titleLbl = new Label(title); titleLbl.setStyle("-fx-font-size: 9px; -fx-font-weight: bold; -fx-text-alignment: center;");
        box.getChildren().addAll(valLbl, titleLbl);
        return box;
    }

    private void populateLeftColumn(CharacterModel character) {
        // 1. Ability Scores
        Race race = character.getRace();
        int strBonus = race != null ? race.getAbilityBonuses().getOrDefault("STR", 0) : 0;
        int dexBonus = race != null ? race.getAbilityBonuses().getOrDefault("DEX", 0) : 0;
        int conBonus = race != null ? race.getAbilityBonuses().getOrDefault("CON", 0) : 0;
        int intBonus = race != null ? race.getAbilityBonuses().getOrDefault("INT", 0) : 0;
        int wisBonus = race != null ? race.getAbilityBonuses().getOrDefault("WIS", 0) : 0;
        int chaBonus = race != null ? race.getAbilityBonuses().getOrDefault("CHA", 0) : 0;

        int str = character.getStrength() + strBonus;
        int dex = character.getDexterity() + dexBonus;
        int con = character.getConstitution() + conBonus;
        int intel = character.getIntelligence() + intBonus;
        int wis = character.getWisdom() + wisBonus;
        int cha = character.getCharisma() + chaBonus;

        GridPane abilityBox = new GridPane();
        abilityBox.setHgap(5);
        abilityBox.setVgap(5);
        abilityBox.add(createAbilityBlock("STRENGTH", str), 0, 0);
        abilityBox.add(createAbilityBlock("DEXTERITY", dex), 1, 0);
        abilityBox.add(createAbilityBlock("CONSTITUTION", con), 2, 0);
        abilityBox.add(createAbilityBlock("INTELLIGENCE", intel), 0, 1);
        abilityBox.add(createAbilityBlock("WISDOM", wis), 1, 1);
        abilityBox.add(createAbilityBlock("CHARISMA", cha), 2, 1);

        // Inspiration & Proficiency Bonus
        VBox inspProfBox = new VBox(5);
        inspProfBox.getChildren().add(createLabeledBox("INSPIRATION", ""));

        int profBonus = 2; // Default for level 1
        inspProfBox.getChildren().add(createLabeledBox("PROFICIENCY BONUS", "+" + profBonus));

        // Saving Throws
        VBox savesBox = new VBox(2);
        savesBox.setStyle("-fx-border-color: #1A1A1A; -fx-padding: 5; -fx-background-color: white; -fx-background-radius: 5; -fx-border-radius: 5;");
        Label lblSaves = new Label("SAVING THROWS");
        lblSaves.setStyle("-fx-font-size: 10px; -fx-font-weight: bold;");
        savesBox.getChildren().add(lblSaves);

        GridPane savesGrid = new GridPane();
        savesGrid.setHgap(5);
        savesGrid.setVgap(2);
        savesGrid.add(createSaveRow("Strength", (str - 10) / 2), 0, 0);
        savesGrid.add(createSaveRow("Dexterity", (dex - 10) / 2), 1, 0);
        savesGrid.add(createSaveRow("Constitution", (con - 10) / 2), 0, 1);
        savesGrid.add(createSaveRow("Intelligence", (intel - 10) / 2), 1, 1);
        savesGrid.add(createSaveRow("Wisdom", (wis - 10) / 2), 0, 2);
        savesGrid.add(createSaveRow("Charisma", (cha - 10) / 2), 1, 2);
        savesBox.getChildren().add(savesGrid);

        // Skills List
        VBox skillsBox = new VBox(2);
        skillsBox.setStyle("-fx-border-color: #1A1A1A; -fx-padding: 5; -fx-background-color: white; -fx-background-radius: 5; -fx-border-radius: 5;");
        Label lblSkillsTitle = new Label("SKILLS");
        lblSkillsTitle.setStyle("-fx-font-size: 10px; -fx-font-weight: bold;");
        skillsBox.getChildren().add(lblSkillsTitle);

        int strMod = (str - 10) / 2;
        int dexMod = (dex - 10) / 2;
        int conMod = (con - 10) / 2;
        int intMod = (intel - 10) / 2;
        int wisMod = (wis - 10) / 2;
        int chaMod = (cha - 10) / 2;

        GridPane skillsGrid = new GridPane();
        skillsGrid.setHgap(5);
        skillsGrid.setVgap(1);

        skillsGrid.add(createSkillRow("Acrobatics", "Dex", dexMod, character), 0, 0);
        skillsGrid.add(createSkillRow("Animal Hand.", "Wis", wisMod, character), 0, 1);
        skillsGrid.add(createSkillRow("Arcana", "Int", intMod, character), 0, 2);
        skillsGrid.add(createSkillRow("Athletics", "Str", strMod, character), 0, 3);
        skillsGrid.add(createSkillRow("Deception", "Cha", chaMod, character), 0, 4);
        skillsGrid.add(createSkillRow("History", "Int", intMod, character), 0, 5);

        skillsGrid.add(createSkillRow("Insight", "Wis", wisMod, character), 1, 0);
        skillsGrid.add(createSkillRow("Intimidation", "Cha", chaMod, character), 1, 1);
        skillsGrid.add(createSkillRow("Investigation", "Int", intMod, character), 1, 2);
        skillsGrid.add(createSkillRow("Medicine", "Wis", wisMod, character), 1, 3);
        skillsGrid.add(createSkillRow("Nature", "Int", intMod, character), 1, 4);
        skillsGrid.add(createSkillRow("Perception", "Wis", wisMod, character), 1, 5);

        skillsGrid.add(createSkillRow("Performance", "Cha", chaMod, character), 2, 0);
        skillsGrid.add(createSkillRow("Persuasion", "Cha", chaMod, character), 2, 1);
        skillsGrid.add(createSkillRow("Religion", "Int", intMod, character), 2, 2);
        skillsGrid.add(createSkillRow("Sleight Of Hand.", "Dex", dexMod, character), 2, 3);
        skillsGrid.add(createSkillRow("Stealth", "Dex", dexMod, character), 2, 4);
        skillsGrid.add(createSkillRow("Survival", "Wis", wisMod, character), 2, 5);

        skillsBox.getChildren().add(skillsGrid);

        leftColumn.getChildren().addAll(abilityBox, inspProfBox, savesBox, skillsBox);
    }

    private HBox createSkillRow(String name, String ability, int score, CharacterModel character) {
        int mod = score;
        boolean isProficient = character.getSelectedSkills() != null && character.getSelectedSkills().contains(name);
        if (isProficient) mod += 2; // Add proficiency bonus

        String modStr = (mod >= 0 ? "+" : "") + mod;

        HBox row = new HBox(5);
        row.setAlignment(Pos.CENTER_LEFT);

        CheckBox cb = new CheckBox();
        cb.setSelected(isProficient);
        cb.setDisable(true);

        Label lblMod = new Label(modStr);
        lblMod.setStyle("-fx-border-color: transparent transparent black transparent; -fx-pref-width: 25; -fx-alignment: bottom-center;");

        Label lblName = new Label(name + " (" + ability + ")");
        lblName.setStyle("-fx-font-size: 10px;");

        row.getChildren().addAll(cb, lblMod, lblName);
        return row;
    }

    private HBox createLabeledBox(String label, String value) {
        HBox box = new HBox(10);
        box.setAlignment(Pos.CENTER_LEFT);

        Label valLbl = new Label(value);
        valLbl.setPrefWidth(30);
        valLbl.setStyle("-fx-border-color: black; -fx-padding: 5; -fx-alignment: center; -fx-background-color: white;");

        Label nameLbl = new Label(label);
        nameLbl.setStyle("-fx-font-size: 10px; -fx-font-weight: bold;");

        box.getChildren().addAll(valLbl, nameLbl);
        return box;
    }

    private HBox createSaveRow(String name, int score) {
        if (score < 0) score++; // Adjust for negative truncation if needed, simplicity here
        int mod = score;
        String modStr = (mod >= 0 ? "+" : "") + mod;

        HBox row = new HBox(5);
        row.setAlignment(Pos.CENTER_LEFT);

        CheckBox cb = new CheckBox();
        cb.setDisable(true); // read-only preview

        Label lblMod = new Label(modStr);
        lblMod.setStyle("-fx-border-color: transparent transparent black transparent; -fx-pref-width: 25; -fx-alignment: bottom-center;");

        Label lblName = new Label(name);
        lblName.setStyle("-fx-font-size: 10px;");

        row.getChildren().addAll(cb, lblMod, lblName);
        return row;
    }

    private VBox createAbilityBlock(String name, int score) {
        VBox box = new VBox(0); // reduce spacing
        box.setStyle("-fx-border-color: #C6A664; -fx-border-width: 2; -fx-alignment: center; -fx-padding: 2; -fx-background-color: white; -fx-background-radius: 10; -fx-border-radius: 10;");

        Label lblName = new Label(name);
        lblName.setStyle("-fx-font-size: 8px; -fx-font-weight: bold;"); // smaller font

        int mod = (score - 10) / 2;
        if (score < 10 && score % 2 != 0) mod--;
        String modStr = (mod >= 0 ? "+" : "") + mod;

        Label lblScore = new Label(String.valueOf(score));
        lblScore.setStyle("-fx-font-size: 18px;"); // smaller font

        Label lblMod = new Label(modStr);
        lblMod.setStyle("-fx-font-size: 12px; -fx-border-color: black; -fx-border-radius: 15; -fx-padding: 2 4; -fx-background-color: #eee; -fx-background-radius: 15;");

        box.getChildren().addAll(lblName, lblScore, lblMod);
        return box;
    }

    private void setupButtonHandlers() {
        btnBack.setOnAction(e -> navigateBack());
        btnSave.setOnAction(e -> saveCharacter());
    }

    private void navigateBack() {
        try {
            Stage stage = (Stage) btnBack.getScene().getWindow();
            EquipmentView previousView = new EquipmentView();
            stage.setScene(new Scene(previousView.getRoot(), stage.getScene().getWidth(), stage.getScene().getHeight()));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void saveCharacter() {
        var character = CharacterSession.getInstance().getCurrentCharacter();
        
        if (dbManager.saveCharacter(character)) {
            showSuccess("Charakter erfolgreich gespeichert!");
            
            // Reset the session so the next character starts fresh
            CharacterSession.getInstance().reset();

            MainView mainView = new MainView();
            Stage stage = (Stage) btnSave.getScene().getWindow();
            new MainPresenter(mainView, stage);
            stage.getScene().setRoot(mainView.getRoot());
        } else {
            javafx.scene.control.Alert alert = new javafx.scene.control.Alert(javafx.scene.control.Alert.AlertType.ERROR);
            alert.setTitle("Fehler");
            alert.setHeaderText(null);
            alert.setContentText("Fehler beim Speichern des Charakters!");
            alert.showAndWait();
        }
    }

    private void showSuccess(String message) {
        javafx.scene.control.Alert alert = new javafx.scene.control.Alert(javafx.scene.control.Alert.AlertType.INFORMATION);
        alert.setTitle("Erfolg");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }


    private int calculateHP(CharacterModel character) {
        int hitDie = character.getClassHitDie();
        if (hitDie == 0) hitDie = 6;
        int conMod = (character.getConstitution() - 10) / 2;
        return hitDie + conMod;
    }

    private int getRaceSpeed(String raceName) {
        if (raceName == null || raceName.isEmpty()) return 30;
        switch (raceName) {
            case "Dwarf": return 25;
            case "Elf": return 30;
            case "Halfling": return 25;
            case "Human": return 30;
            case "Dragonborn": return 30;
            case "Gnome": return 25;
            case "Half-Elf": return 30;
            case "Half-Orc": return 30;
            case "Tiefling": return 30;
            default: return 30;
        }
    }
}
