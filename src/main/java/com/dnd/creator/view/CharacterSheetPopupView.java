package com.dnd.creator.view;

import com.dnd.creator.model.CharacterModel;
import com.dnd.creator.model.Race;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.Window;

import java.io.IOException;
import java.util.List;

public class CharacterSheetPopupView {

    @FXML private Label lblCharacterName;
    @FXML private Label lblClassLevel;
    @FXML private Label lblBackground;
    @FXML private Label lblRace;
    @FXML private ImageView imgPortrait;
    @FXML private Button btnDelete;
    @FXML private Button btnEdit;
    @FXML private Button btnClose;
    @FXML private VBox leftColumn;
    @FXML private VBox middleColumn;
    @FXML private VBox rightColumn;

    private Parent root;
    private final CharacterModel character;

    public CharacterSheetPopupView(CharacterModel character) {
        this.character = character;
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/dnd/creator/view/CharacterSheetPopup.fxml"));
            loader.setController(this);
            root = loader.load();
            populate();
            setupButtons();
        } catch (IOException e) {
            throw new RuntimeException("Failed to load CharacterSheetPopup.fxml", e);
        }
    }

    public void showAsPopup(Window owner) {
        Stage stage = new Stage();
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.initOwner(owner);
        stage.setTitle(character.getName() + " — Character Sheet");
        Scene scene = new Scene(root, 900, 720);
        scene.getStylesheets().add(getClass().getResource("/com/dnd/creator/view/character-sheet.css").toExternalForm());
        stage.setScene(scene);
        stage.setMinWidth(800);
        stage.setMinHeight(600);
        stage.show();
    }

    private void populate() {
        lblCharacterName.setText(character.getName() != null ? character.getName() : "Unknown");
        String cls = character.getCharacterClass() != null ? character.getCharacterClass() : "Unknown";
        lblClassLevel.setText(cls + " | Level 1");
        lblBackground.setText(character.getSelectedBackground() != null ? character.getSelectedBackground() : "—");
        lblRace.setText(character.getRace() != null ? character.getRace().getName() : "Unknown");
        loadPortrait();

        populateLeft();
        populateMiddle();
        populateRight();
    }

    private void populateLeft() {
        Race race = character.getRace();
        int str = character.getStrength() + bonus(race, "STR");
        int dex = character.getDexterity() + bonus(race, "DEX");
        int con = character.getConstitution() + bonus(race, "CON");
        int intel = character.getIntelligence() + bonus(race, "INT");
        int wis = character.getWisdom() + bonus(race, "WIS");
        int cha = character.getCharisma() + bonus(race, "CHA");

        GridPane abilityGrid = new GridPane();
        abilityGrid.setHgap(6);
        abilityGrid.setVgap(6);
        abilityGrid.add(abilityBlock("STR", str), 0, 0);
        abilityGrid.add(abilityBlock("DEX", dex), 1, 0);
        abilityGrid.add(abilityBlock("CON", con), 2, 0);
        abilityGrid.add(abilityBlock("INT", intel), 0, 1);
        abilityGrid.add(abilityBlock("WIS", wis), 1, 1);
        abilityGrid.add(abilityBlock("CHA", cha), 2, 1);

        HBox profBox = new HBox(8);
        profBox.setAlignment(Pos.CENTER_LEFT);
        Label profVal = new Label("+2");
        profVal.setStyle("-fx-font-size: 14px; -fx-font-weight: bold; -fx-border-color: #C6A664; -fx-border-radius: 5; -fx-background-color: white; -fx-background-radius: 5; -fx-padding: 4 8;");
        Label profLabel = new Label("PROFICIENCY BONUS");
        profLabel.setStyle("-fx-font-size: 10px; -fx-font-weight: bold;");
        profBox.getChildren().addAll(profVal, profLabel);

        VBox savesBox = titledSection("SAVING THROWS");
        savesBox.getChildren().add(saveRow("Strength", mod(str)));
        savesBox.getChildren().add(saveRow("Dexterity", mod(dex)));
        savesBox.getChildren().add(saveRow("Constitution", mod(con)));
        savesBox.getChildren().add(saveRow("Intelligence", mod(intel)));
        savesBox.getChildren().add(saveRow("Wisdom", mod(wis)));
        savesBox.getChildren().add(saveRow("Charisma", mod(cha)));

        VBox skillsBox = titledSection("SKILLS");
        addSkillRow(skillsBox, "Acrobatics", "Dex", dex);
        addSkillRow(skillsBox, "Animal Handling", "Wis", wis);
        addSkillRow(skillsBox, "Arcana", "Int", intel);
        addSkillRow(skillsBox, "Athletics", "Str", str);
        addSkillRow(skillsBox, "Deception", "Cha", cha);
        addSkillRow(skillsBox, "History", "Int", intel);
        addSkillRow(skillsBox, "Insight", "Wis", wis);
        addSkillRow(skillsBox, "Intimidation", "Cha", cha);
        addSkillRow(skillsBox, "Investigation", "Int", intel);
        addSkillRow(skillsBox, "Medicine", "Wis", wis);
        addSkillRow(skillsBox, "Nature", "Int", intel);
        addSkillRow(skillsBox, "Perception", "Wis", wis);
        addSkillRow(skillsBox, "Performance", "Cha", cha);
        addSkillRow(skillsBox, "Persuasion", "Cha", cha);
        addSkillRow(skillsBox, "Religion", "Int", intel);
        addSkillRow(skillsBox, "Sleight of Hand", "Dex", dex);
        addSkillRow(skillsBox, "Stealth", "Dex", dex);
        addSkillRow(skillsBox, "Survival", "Wis", wis);

        boolean percProf = isProficient("Perception");
        int passWis = 10 + mod(wis) + (percProf ? 2 : 0);
        HBox passiveBox = new HBox(8);
        passiveBox.setAlignment(Pos.CENTER_LEFT);
        Label passVal = new Label(String.valueOf(passWis));
        passVal.setStyle("-fx-font-size: 13px; -fx-border-color: #1A1A1A; -fx-padding: 3 7; -fx-background-color: white;");
        Label passLabel = new Label("PASSIVE WISDOM (PERCEPTION)");
        passLabel.setStyle("-fx-font-size: 9px; -fx-font-weight: bold;");
        passiveBox.getChildren().addAll(passVal, passLabel);

        leftColumn.getChildren().addAll(abilityGrid, profBox, savesBox, skillsBox, passiveBox);
    }

    private void populateMiddle() {
        Race race = character.getRace();
        int dex = character.getDexterity() + bonus(race, "DEX");
        int con = character.getConstitution() + bonus(race, "CON");
        int str = character.getStrength() + bonus(race, "STR");
        int ac = 10 + mod(dex);
        int speed = race != null ? race.getSpeed() : 30;
        int hitDie = character.getClassHitDie() == 0 ? 6 : character.getClassHitDie();
        int maxHp = hitDie + mod(con);

        HBox topRow = new HBox(10);
        topRow.setAlignment(Pos.CENTER);
        topRow.getChildren().addAll(
            shieldBox("ARMOR\nCLASS", String.valueOf(ac)),
            statBox("INITIATIVE", modStr(mod(dex))),
            statBox("SPEED", speed + " ft")
        );

        VBox hpBox = new VBox(4);
        hpBox.setStyle(sectionStyle());
        Label hpMax = new Label("HP Maximum: " + maxHp);
        hpMax.setStyle("-fx-font-size: 10px; -fx-text-fill: #555;");
        Label hpVal = new Label(String.valueOf(maxHp));
        hpVal.setStyle("-fx-font-size: 26px; -fx-font-weight: bold;");
        Label hpLabel = new Label("CURRENT HIT POINTS");
        hpLabel.setStyle("-fx-font-size: 10px; -fx-font-weight: bold;");
        hpBox.getChildren().addAll(hpMax, hpVal, hpLabel);

        HBox diceRow = new HBox(10);

        VBox hitDiceBox = new VBox(4);
        hitDiceBox.setStyle(sectionStyle());
        hitDiceBox.setPadding(new Insets(6));
        HBox.setHgrow(hitDiceBox, Priority.ALWAYS);
        Label hdTotal = new Label("Total: 1d" + hitDie);
        hdTotal.setStyle("-fx-font-size: 10px; -fx-text-fill: #555;");
        Label hdVal = new Label("1d" + hitDie);
        hdVal.setStyle("-fx-font-size: 18px;");
        Label hdLabel = new Label("HIT DICE");
        hdLabel.setStyle("-fx-font-size: 10px; -fx-font-weight: bold;");
        hitDiceBox.getChildren().addAll(hdTotal, hdVal, hdLabel);

        VBox deathBox = new VBox(5);
        deathBox.setStyle(sectionStyle());
        deathBox.setPadding(new Insets(6));
        HBox.setHgrow(deathBox, Priority.ALWAYS);
        HBox succRow = new HBox(4);
        succRow.setAlignment(Pos.CENTER_LEFT);
        Label succLbl = new Label("SUCCESSES");
        succLbl.setStyle("-fx-font-size: 9px;");
        succRow.getChildren().addAll(succLbl, disabledCheckBox(), disabledCheckBox(), disabledCheckBox());
        HBox failRow = new HBox(4);
        failRow.setAlignment(Pos.CENTER_LEFT);
        Label failLbl = new Label("FAILURES   ");
        failLbl.setStyle("-fx-font-size: 9px;");
        failRow.getChildren().addAll(failLbl, disabledCheckBox(), disabledCheckBox(), disabledCheckBox());
        Label dsLabel = new Label("DEATH SAVES");
        dsLabel.setStyle("-fx-font-size: 10px; -fx-font-weight: bold;");
        deathBox.getChildren().addAll(succRow, failRow, dsLabel);

        diceRow.getChildren().addAll(hitDiceBox, deathBox);

        VBox attacksBox = new VBox(5);
        attacksBox.setStyle(sectionStyle());
        attacksBox.setPadding(new Insets(6));
        VBox.setVgrow(attacksBox, Priority.ALWAYS);

        GridPane atkGrid = new GridPane();
        atkGrid.setHgap(12);
        atkGrid.setVgap(3);
        atkGrid.add(headerLabel("NAME"), 0, 0);
        atkGrid.add(headerLabel("ATK BONUS"), 1, 0);
        atkGrid.add(headerLabel("DAMAGE / TYPE"), 2, 0);

        int row = 1;
        for (String[] weapon : character.getWeaponAttacks()) {
            atkGrid.add(new Label(weapon[0]), 0, row);
            atkGrid.add(new Label(weapon[1]), 1, row);
            atkGrid.add(new Label(weapon[2]), 2, row);
            row++;
        }
        if (row == 1) {
            // No weapons — show unarmed as fallback
            int strMod = mod(str);
            atkGrid.add(new Label("Unarmed Strike"), 0, 1);
            atkGrid.add(new Label(modStr(strMod)), 1, 1);
            atkGrid.add(new Label("1 Bludgeoning"), 2, 1);
        }

        Label atkLabel = new Label("ATTACKS & SPELLCASTING");
        atkLabel.setStyle("-fx-font-size: 10px; -fx-font-weight: bold;");
        atkLabel.setPadding(new Insets(4, 0, 0, 0));
        attacksBox.getChildren().addAll(atkGrid, atkLabel);

        middleColumn.getChildren().addAll(topRow, hpBox, diceRow, attacksBox);
    }

    private void populateRight() {
        VBox equipBox = titledSection("EQUIPMENT");
        VBox.setVgrow(equipBox, Priority.ALWAYS);
        List<String> equipment = character.getSelectedEquipment();
        if (equipment == null || equipment.isEmpty()) {
            Label none = new Label("—");
            none.setStyle("-fx-font-size: 11px; -fx-text-fill: #888;");
            equipBox.getChildren().add(none);
        } else {
            for (String item : equipment) {
                Label lbl = new Label("• " + item);
                lbl.setStyle("-fx-font-size: 11px;");
                lbl.setWrapText(true);
                equipBox.getChildren().add(lbl);
            }
        }

        VBox spellsBox = titledSection("SPELLS");
        VBox.setVgrow(spellsBox, Priority.SOMETIMES);
        List<String> spells = character.getSelectedSpells();
        if (spells == null || spells.isEmpty()) {
            Label none = new Label("—");
            none.setStyle("-fx-font-size: 11px; -fx-text-fill: #888;");
            spellsBox.getChildren().add(none);
        } else {
            for (String spell : spells) {
                Label lbl = new Label("• " + spell);
                lbl.setStyle("-fx-font-size: 11px;");
                lbl.setWrapText(true);
                spellsBox.getChildren().add(lbl);
            }
        }

        rightColumn.getChildren().addAll(equipBox, spellsBox);
    }

    private void loadPortrait() {
        String path = character.getImagePath();
        if (path == null || path.isBlank()) return;
        try {
            java.io.File file = new java.io.File(path);
            if (file.exists()) {
                imgPortrait.setImage(new Image(file.toURI().toString()));
            }
        } catch (Exception ignored) {}
    }

    private void setupButtons() {
        btnClose.setOnAction(e -> ((Stage) btnClose.getScene().getWindow()).close());
        // Delete and Edit are stubs — implemented in stories #6 and #5
    }

    // ---- Helpers ----

    private int bonus(Race race, String ability) {
        return race != null ? race.getAbilityBonuses().getOrDefault(ability, 0) : 0;
    }

    private int mod(int score) {
        return (score - 10) / 2;
    }

    private String modStr(int mod) {
        return (mod >= 0 ? "+" : "") + mod;
    }

    private boolean isProficient(String skillName) {
        return character.getSelectedSkills() != null && character.getSelectedSkills().contains(skillName);
    }

    private String sectionStyle() {
        return "-fx-border-color: #1A1A1A; -fx-padding: 5; -fx-background-color: white; -fx-background-radius: 5; -fx-border-radius: 5;";
    }

    private VBox abilityBlock(String label, int score) {
        VBox box = new VBox(1);
        box.setStyle("-fx-border-color: #C6A664; -fx-border-width: 2; -fx-alignment: center; -fx-padding: 4; -fx-background-color: white; -fx-background-radius: 10; -fx-border-radius: 10; -fx-min-width: 62;");
        Label lbl = new Label(label);
        lbl.setStyle("-fx-font-size: 8px; -fx-font-weight: bold;");
        Label scoreLbl = new Label(String.valueOf(score));
        scoreLbl.setStyle("-fx-font-size: 20px;");
        Label modLbl = new Label(modStr(mod(score)));
        modLbl.setStyle("-fx-font-size: 11px; -fx-border-color: black; -fx-border-radius: 10; -fx-padding: 1 4; -fx-background-color: #eee; -fx-background-radius: 10;");
        box.getChildren().addAll(lbl, scoreLbl, modLbl);
        return box;
    }

    private VBox titledSection(String title) {
        VBox box = new VBox(4);
        box.setStyle(sectionStyle());
        Label lbl = new Label(title);
        lbl.setStyle("-fx-font-size: 10px; -fx-font-weight: bold;");
        box.getChildren().add(lbl);
        return box;
    }

    private HBox saveRow(String name, int modifier) {
        HBox row = new HBox(5);
        row.setAlignment(Pos.CENTER_LEFT);
        CheckBox cb = new CheckBox();
        cb.setDisable(true);
        Label modLbl = new Label(modStr(modifier));
        modLbl.setStyle("-fx-pref-width: 28; -fx-alignment: center;");
        Label nameLbl = new Label(name);
        nameLbl.setStyle("-fx-font-size: 10px;");
        row.getChildren().addAll(cb, modLbl, nameLbl);
        return row;
    }

    private void addSkillRow(VBox parent, String skillName, String ability, int abilityScore) {
        boolean prof = isProficient(skillName);
        int skillMod = mod(abilityScore) + (prof ? 2 : 0);
        HBox row = new HBox(5);
        row.setAlignment(Pos.CENTER_LEFT);
        CheckBox cb = new CheckBox();
        cb.setSelected(prof);
        cb.setDisable(true);
        Label modLbl = new Label(modStr(skillMod));
        modLbl.setStyle("-fx-pref-width: 28; -fx-alignment: center;");
        Label nameLbl = new Label(skillName + " (" + ability + ")");
        nameLbl.setStyle("-fx-font-size: 10px;");
        row.getChildren().addAll(cb, modLbl, nameLbl);
        parent.getChildren().add(row);
    }

    private VBox statBox(String title, String value) {
        VBox box = new VBox(2);
        box.setStyle("-fx-border-color: #1A1A1A; -fx-border-radius: 5; -fx-background-color: white; -fx-background-radius: 5; -fx-alignment: center; -fx-pref-width: 85; -fx-pref-height: 80;");
        Label val = new Label(value);
        val.setStyle("-fx-font-size: 22px;");
        Label lbl = new Label(title);
        lbl.setStyle("-fx-font-size: 9px; -fx-font-weight: bold;");
        box.getChildren().addAll(val, lbl);
        return box;
    }

    private VBox shieldBox(String title, String value) {
        VBox box = new VBox(2);
        box.setStyle("-fx-border-color: #1A1A1A; -fx-border-radius: 10 10 30 30; -fx-background-color: white; -fx-background-radius: 10 10 30 30; -fx-alignment: center; -fx-pref-width: 85; -fx-pref-height: 80;");
        Label val = new Label(value);
        val.setStyle("-fx-font-size: 22px; -fx-font-weight: bold;");
        Label lbl = new Label(title);
        lbl.setStyle("-fx-font-size: 9px; -fx-font-weight: bold; -fx-text-alignment: center;");
        box.getChildren().addAll(val, lbl);
        return box;
    }

    private Label headerLabel(String text) {
        Label l = new Label(text);
        l.setStyle("-fx-font-size: 9px; -fx-font-weight: bold; -fx-text-fill: #555;");
        return l;
    }

    private CheckBox disabledCheckBox() {
        CheckBox cb = new CheckBox();
        cb.setDisable(true);
        return cb;
    }
}