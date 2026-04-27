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

        // We will build columns step by step, for now just clear them
        leftColumn.getChildren().clear();
        middleColumn.getChildren().clear();
        rightColumn.getChildren().clear();

        populateLeftColumn(character);
        populateMiddleColumn(character);
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

        // HP Section
        int maxHp = calculateHP(character);
        VBox hpBox = new VBox(5);
        hpBox.setStyle("-fx-border-color: #1A1A1A; -fx-padding: 10; -fx-background-color: white; -fx-background-radius: 5; -fx-border-radius: 5;");

        HBox hpLabels = new HBox(10);
        Label lblHpMax = new Label("Hit Point Maximum: " + maxHp);
        lblHpMax.setStyle("-fx-font-size: 10px; -fx-text-fill: #555;");
        hpLabels.getChildren().add(lblHpMax);

        Label lblHpValue = new Label(String.valueOf(maxHp));
        lblHpValue.setStyle("-fx-font-size: 24px; -fx-padding: 10; -fx-alignment: center; -fx-pref-width: 200;");

        Label lblHpCurrent = new Label("CURRENT HIT POINTS");
        lblHpCurrent.setStyle("-fx-font-size: 12px; -fx-font-weight: bold;");

        hpBox.getChildren().addAll(hpLabels, lblHpValue, lblHpCurrent);

        VBox tempHpBox = new VBox(5);
        tempHpBox.setStyle("-fx-border-color: #1A1A1A; -fx-padding: 10; -fx-background-color: white; -fx-background-radius: 5; -fx-border-radius: 5;");
        Label lblTempHpValue = new Label("");
        lblTempHpValue.setStyle("-fx-font-size: 24px; -fx-padding: 10; -fx-alignment: center; -fx-pref-width: 200;");
        Label lblTempHp = new Label("TEMPORARY HIT POINTS");
        lblTempHp.setStyle("-fx-font-size: 12px; -fx-font-weight: bold;");
        tempHpBox.getChildren().addAll(lblTempHpValue, lblTempHp);

        // Hit Dice & Death Saves
        HBox diceSavesBox = new HBox(10);

        VBox hitDiceBox = new VBox(5);
        hitDiceBox.setStyle("-fx-border-color: #1A1A1A; -fx-padding: 10; -fx-background-color: white; -fx-background-radius: 5; -fx-border-radius: 5; -fx-pref-width: 140;");
        Label lblHdTotal = new Label("Total: 1d" + (character.getClassHitDie() == 0 ? 6 : character.getClassHitDie()));
        lblHdTotal.setStyle("-fx-font-size: 10px; -fx-text-fill: #555;");
        Label lblHdValue = new Label("1d" + (character.getClassHitDie() == 0 ? 6 : character.getClassHitDie()));
        lblHdValue.setStyle("-fx-font-size: 18px; -fx-alignment: center;");
        Label lblHdText = new Label("HIT DICE");
        lblHdText.setStyle("-fx-font-size: 10px; -fx-font-weight: bold;");
        hitDiceBox.getChildren().addAll(lblHdTotal, lblHdValue, lblHdText);

        VBox deathBox = new VBox(5);
        deathBox.setStyle("-fx-border-color: #1A1A1A; -fx-padding: 10; -fx-background-color: white; -fx-background-radius: 5; -fx-border-radius: 5; -fx-pref-width: 140;");

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

        middleColumn.getChildren().addAll(hpBox, tempHpBox, diceSavesBox);
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

        VBox abilityBox = new VBox(10);
        abilityBox.getChildren().add(createAbilityBlock("STRENGTH", str));
        abilityBox.getChildren().add(createAbilityBlock("DEXTERITY", dex));
        abilityBox.getChildren().add(createAbilityBlock("CONSTITUTION", con));
        abilityBox.getChildren().add(createAbilityBlock("INTELLIGENCE", intel));
        abilityBox.getChildren().add(createAbilityBlock("WISDOM", wis));
        abilityBox.getChildren().add(createAbilityBlock("CHARISMA", cha));

        // Inspiration & Proficiency Bonus
        VBox inspProfBox = new VBox(5);
        inspProfBox.getChildren().add(createLabeledBox("INSPIRATION", ""));

        int profBonus = 2; // Default for level 1
        inspProfBox.getChildren().add(createLabeledBox("PROFICIENCY BONUS", "+" + profBonus));

        // Saving Throws
        VBox savesBox = new VBox(5);
        savesBox.setStyle("-fx-border-color: #1A1A1A; -fx-padding: 10; -fx-background-color: white; -fx-background-radius: 5; -fx-border-radius: 5;");
        Label lblSaves = new Label("SAVING THROWS");
        lblSaves.setStyle("-fx-font-size: 10px; -fx-font-weight: bold;");
        savesBox.getChildren().add(lblSaves);

        savesBox.getChildren().add(createSaveRow("Strength", (str - 10) / 2));
        savesBox.getChildren().add(createSaveRow("Dexterity", (dex - 10) / 2));
        savesBox.getChildren().add(createSaveRow("Constitution", (con - 10) / 2));
        savesBox.getChildren().add(createSaveRow("Intelligence", (intel - 10) / 2));
        savesBox.getChildren().add(createSaveRow("Wisdom", (wis - 10) / 2));
        savesBox.getChildren().add(createSaveRow("Charisma", (cha - 10) / 2));

        // Skills List
        VBox skillsBox = new VBox(5);
        skillsBox.setStyle("-fx-border-color: #1A1A1A; -fx-padding: 10; -fx-background-color: white; -fx-background-radius: 5; -fx-border-radius: 5;");
        Label lblSkillsTitle = new Label("SKILLS");
        lblSkillsTitle.setStyle("-fx-font-size: 10px; -fx-font-weight: bold;");
        skillsBox.getChildren().add(lblSkillsTitle);

        int strMod = (str - 10) / 2;
        int dexMod = (dex - 10) / 2;
        int conMod = (con - 10) / 2;
        int intMod = (intel - 10) / 2;
        int wisMod = (wis - 10) / 2;
        int chaMod = (cha - 10) / 2;

        skillsBox.getChildren().add(createSkillRow("Acrobatics", "Dex", dexMod, character));
        skillsBox.getChildren().add(createSkillRow("Animal Handling", "Wis", wisMod, character));
        skillsBox.getChildren().add(createSkillRow("Arcana", "Int", intMod, character));
        skillsBox.getChildren().add(createSkillRow("Athletics", "Str", strMod, character));
        skillsBox.getChildren().add(createSkillRow("Deception", "Cha", chaMod, character));
        skillsBox.getChildren().add(createSkillRow("History", "Int", intMod, character));
        skillsBox.getChildren().add(createSkillRow("Insight", "Wis", wisMod, character));
        skillsBox.getChildren().add(createSkillRow("Intimidation", "Cha", chaMod, character));
        skillsBox.getChildren().add(createSkillRow("Investigation", "Int", intMod, character));
        skillsBox.getChildren().add(createSkillRow("Medicine", "Wis", wisMod, character));
        skillsBox.getChildren().add(createSkillRow("Nature", "Int", intMod, character));
        skillsBox.getChildren().add(createSkillRow("Perception", "Wis", wisMod, character));
        skillsBox.getChildren().add(createSkillRow("Performance", "Cha", chaMod, character));
        skillsBox.getChildren().add(createSkillRow("Persuasion", "Cha", chaMod, character));
        skillsBox.getChildren().add(createSkillRow("Religion", "Int", intMod, character));
        skillsBox.getChildren().add(createSkillRow("Sleight of Hand", "Dex", dexMod, character));
        skillsBox.getChildren().add(createSkillRow("Stealth", "Dex", dexMod, character));
        skillsBox.getChildren().add(createSkillRow("Survival", "Wis", wisMod, character));

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
        VBox box = new VBox(2);
        box.setStyle("-fx-border-color: #C6A664; -fx-border-width: 2; -fx-alignment: center; -fx-padding: 5; -fx-background-color: white; -fx-background-radius: 10; -fx-border-radius: 10;");

        Label lblName = new Label(name);
        lblName.setStyle("-fx-font-size: 10px; -fx-font-weight: bold;");

        int mod = (score - 10) / 2;
        if (score < 10 && score % 2 != 0) mod--; // Java integer division truncates towards zero
        String modStr = (mod >= 0 ? "+" : "") + mod;

        Label lblScore = new Label(String.valueOf(score));
        lblScore.setStyle("-fx-font-size: 24px;");

        Label lblMod = new Label(modStr);
        lblMod.setStyle("-fx-font-size: 14px; -fx-border-color: black; -fx-border-radius: 15; -fx-padding: 2 5; -fx-background-color: #eee; -fx-background-radius: 15;");

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

    private void addCombatStats() {
        var character = CharacterSession.getInstance().getCurrentCharacter();

        Label statsTitle = new Label("✓ Kampfwerte:");
        statsTitle.setStyle("-fx-font-size: 12px; -fx-font-weight: bold; -fx-text-fill: #D32F2F; -fx-padding: 10 0 5 0;");
        leftColumn.getChildren().add(statsTitle);

        // Leben (HP)
        int hp = calculateHP(character);
        Label hpLabel = new Label("  • Leben: " + hp);
        hpLabel.setStyle("-fx-font-size: 11px; -fx-text-fill: #D32F2F;");
        leftColumn.getChildren().add(hpLabel);

        // Speed
        String raceName = character.getRace() != null ? character.getRace().getName() : "Human";
        int speed = getRaceSpeed(raceName);
        Label speedLabel = new Label("  • Geschwindigkeit: " + speed + " ft/round");
        speedLabel.setStyle("-fx-font-size: 11px; -fx-text-fill: #D32F2F;");
        leftColumn.getChildren().add(speedLabel);

        // Initiative
        int dexMod = (character.getDexterity() - 10) / 2;
        Label initiativeLabel = new Label("  • Initiative: " + (dexMod >= 0 ? "+" : "") + dexMod);
        initiativeLabel.setStyle("-fx-font-size: 11px; -fx-text-fill: #D32F2F;");
        leftColumn.getChildren().add(initiativeLabel);

        // Armor Class (vereinfacht: 10 + DEX)
        int ac = 10 + dexMod;
        Label acLabel = new Label("  • Rüstungsklasse: " + ac);
        acLabel.setStyle("-fx-font-size: 11px; -fx-text-fill: #D32F2F;");
        leftColumn.getChildren().add(acLabel);

        // Skills
        Label skillsTitle = new Label("✓ Fertigkeiten:");
        skillsTitle.setStyle("-fx-font-size: 12px; -fx-font-weight: bold; -fx-text-fill: #F57C00; -fx-padding: 10 0 5 0;");
        middleColumn.getChildren().add(skillsTitle);

        int strMod = (character.getStrength() - 10) / 2;
        int conMod = (character.getConstitution() - 10) / 2;
        int intMod = (character.getIntelligence() - 10) / 2;
        int wisMod = (character.getWisdom() - 10) / 2;
        int chaMod = (character.getCharisma() - 10) / 2;

        String[][] skills = {
            {"Akrobatik", String.valueOf(dexMod)},
            {"Tierhandhabung", String.valueOf(wisMod)},
            {"Arkanwissen", String.valueOf(intMod)},
            {"Athletik", String.valueOf(strMod)},
            {"Betrug", String.valueOf(chaMod)},
            {"Geschichte", String.valueOf(intMod)},
            {"Einsicht", String.valueOf(wisMod)},
            {"Einschüchterung", String.valueOf(chaMod)},
            {"Untersuchung", String.valueOf(intMod)},
            {"Heilkunde", String.valueOf(wisMod)},
            {"Naturkunde", String.valueOf(intMod)},
            {"Wahrnehmung", String.valueOf(wisMod)},
            {"Aufführung", String.valueOf(chaMod)},
            {"Überzeugung", String.valueOf(chaMod)},
            {"Religion", String.valueOf(intMod)},
            {"Fingerfertigkeit", String.valueOf(dexMod)},
            {"Heimlichkeit", String.valueOf(dexMod)},
            {"Überleben", String.valueOf(wisMod)}
        };

        for (String[] skill : skills) {
            int mod = Integer.parseInt(skill[1]);
            Label skillLabel = new Label("  • " + skill[0] + ": " + (mod >= 0 ? "+" : "") + mod);
            skillLabel.setStyle("-fx-font-size: 10px; -fx-text-fill: #F57C00;");
            middleColumn.getChildren().add(skillLabel);
        }
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
