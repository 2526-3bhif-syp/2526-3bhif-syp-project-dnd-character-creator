package com.dnd.creator.view;

import com.dnd.creator.data.DbManager;
import com.dnd.creator.model.CharacterModel;
import com.dnd.creator.model.Race;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.Window;

import java.io.IOException;
import java.util.Map;

public class LevelUpView {

    // D&D 5e multiclassing prerequisites: class -> {STR, DEX, CON, INT, WIS, CHA} minimum scores
    private static final Map<String, int[]> PREREQS = Map.ofEntries(
        Map.entry("Barbarian", new int[]{13,  0,  0,  0,  0,  0}),
        Map.entry("Bard",      new int[]{ 0,  0,  0,  0,  0, 13}),
        Map.entry("Cleric",    new int[]{ 0,  0,  0,  0, 13,  0}),
        Map.entry("Druid",     new int[]{ 0,  0,  0,  0, 13,  0}),
        Map.entry("Fighter",   new int[]{13,  0,  0,  0,  0,  0}), // STR 13 OR DEX 13 — handled in meetsPrereq()
        Map.entry("Monk",      new int[]{ 0, 13,  0,  0, 13,  0}),
        Map.entry("Paladin",   new int[]{13,  0,  0,  0,  0, 13}),
        Map.entry("Ranger",    new int[]{ 0, 13,  0,  0, 13,  0}),
        Map.entry("Rogue",     new int[]{ 0, 13,  0,  0,  0,  0}),
        Map.entry("Sorcerer",  new int[]{ 0,  0,  0,  0,  0, 13}),
        Map.entry("Warlock",   new int[]{ 0,  0,  0,  0,  0, 13}),
        Map.entry("Wizard",    new int[]{ 0,  0,  0, 13,  0,  0})
    );

    private static final String[] ALL_CLASSES = {
        "Barbarian", "Bard", "Cleric", "Druid", "Fighter", "Monk",
        "Paladin", "Ranger", "Rogue", "Sorcerer", "Warlock", "Wizard"
    };

    private static final String SELECTED_STYLE =
        "-fx-background-color: #8B0000; -fx-border-color: #8B0000; -fx-border-width: 2; " +
        "-fx-border-radius: 8; -fx-background-radius: 8; -fx-text-fill: #F5F5DC; " +
        "-fx-font-size: 13px; -fx-font-weight: bold; -fx-padding: 8 16; -fx-cursor: hand;";
    private static final String EXISTING_STYLE =
        "-fx-background-color: #FFFBF0; -fx-border-color: #C6A664; -fx-border-width: 2; " +
        "-fx-border-radius: 8; -fx-background-radius: 8; -fx-text-fill: #8B0000; " +
        "-fx-font-size: 13px; -fx-font-weight: bold; -fx-padding: 8 16; -fx-cursor: hand;";
    private static final String NEW_CLASS_STYLE =
        "-fx-background-color: #FFFBF0; -fx-border-color: #C6A664; -fx-border-width: 2; " +
        "-fx-border-radius: 8; -fx-background-radius: 8; -fx-text-fill: #1A1A1A; " +
        "-fx-font-size: 12px; -fx-cursor: hand;";

    @FXML private Label lblCharName;
    @FXML private Label lblCurrentLevel;
    @FXML private HBox currentClassesBox;
    @FXML private HBox existingClassesBox;
    @FXML private FlowPane newClassesPane;
    @FXML private VBox hpPreviewBox;
    @FXML private Label lblHpPreview;
    @FXML private Label lblHpFormula;
    @FXML private Button btnCancel;
    @FXML private Button btnConfirm;

    private Parent root;
    private final CharacterModel character;
    private final DbManager dbManager;
    private String selectedClass = null;
    private int hpGain = 0;

    public LevelUpView(CharacterModel character, DbManager dbManager) {
        this.character = character;
        this.dbManager = dbManager;
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/dnd/creator/view/LevelUpView.fxml"));
            loader.setController(this);
            root = loader.load();
            populate();
        } catch (IOException e) {
            throw new RuntimeException("Failed to load LevelUpView.fxml", e);
        }
    }

    public void showAsPopup(Window owner, Runnable onComplete) {
        Stage stage = new Stage();
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.initOwner(owner);
        stage.setTitle("Level Up — " + character.getName());
        stage.setScene(new Scene(root, 560, 620));
        stage.setMinWidth(480);
        stage.setMinHeight(500);

        btnCancel.setOnAction(e -> stage.close());
        btnConfirm.setOnAction(e -> {
            if (confirm()) {
                stage.close();
                if (onComplete != null) onComplete.run();
            }
        });

        stage.show();
    }

    private void populate() {
        String raceName = character.getRace() != null ? character.getRace().getName() : "Unknown";
        lblCharName.setText(character.getName() + "  •  " + raceName);
        lblCurrentLevel.setText("Level " + character.getTotalLevel() + "  —  " + character.getClassLevelDisplay());

        buildCurrentClassBadges();
        buildExistingClassButtons();
        buildNewClassOptions();
    }

    private void buildCurrentClassBadges() {
        currentClassesBox.getChildren().clear();
        character.getClassLevels().forEach((cls, lvl) -> {
            Label badge = new Label(cls + " Lv." + lvl);
            badge.setStyle("-fx-background-color: #8B0000; -fx-text-fill: #F5F5DC; " +
                "-fx-font-size: 12px; -fx-font-weight: bold; -fx-padding: 4 10; -fx-background-radius: 10;");
            currentClassesBox.getChildren().add(badge);
        });
    }

    private void buildExistingClassButtons() {
        existingClassesBox.getChildren().clear();
        character.getClassLevels().forEach((cls, currentLevel) -> {
            if (currentLevel >= 20) return;
            int hitDie = dbManager.getClassHitDie(cls);
            Button btn = new Button(cls + "  (Lv." + currentLevel + " → " + (currentLevel + 1) + ")");
            btn.setStyle(EXISTING_STYLE);
            btn.setOnAction(e -> selectClass(cls, hitDie, currentLevel + 1, btn, false));
            existingClassesBox.getChildren().add(btn);
        });
    }

    private void buildNewClassOptions() {
        newClassesPane.getChildren().clear();
        Race race = character.getRace();
        int str   = character.getStrength()     + bonus(race, "STR");
        int dex   = character.getDexterity()    + bonus(race, "DEX");
        int con   = character.getConstitution() + bonus(race, "CON");
        int intel = character.getIntelligence() + bonus(race, "INT");
        int wis   = character.getWisdom()       + bonus(race, "WIS");
        int cha   = character.getCharisma()     + bonus(race, "CHA");
        int[] scores = {str, dex, con, intel, wis, cha};

        for (String cls : ALL_CLASSES) {
            if (character.getClassLevels().containsKey(cls)) continue;

            int hitDie = dbManager.getClassHitDie(cls);
            boolean eligible = meetsPrereq(cls, scores);

            Button btn = new Button(cls + "\n(d" + hitDie + ")");
            btn.setWrapText(true);
            btn.setPrefWidth(108);
            btn.setPrefHeight(56);

            if (eligible) {
                btn.setStyle(NEW_CLASS_STYLE);
                btn.setOnAction(e -> selectClass(cls, hitDie, 1, btn, true));
            } else {
                btn.setStyle("-fx-background-color: #EEEEEE; -fx-border-color: #BBBBBB; -fx-border-width: 1; " +
                    "-fx-border-radius: 8; -fx-background-radius: 8; -fx-text-fill: #AAAAAA; -fx-font-size: 12px;");
                btn.setDisable(true);
                Tooltip.install(btn, new Tooltip("Voraussetzung: " + prereqText(cls)));
            }
            newClassesPane.getChildren().add(btn);
        }
    }

    private void selectClass(String className, int hitDie, int newLevel, Button sourceBtn, boolean isNewClass) {
        // Deselect all
        existingClassesBox.getChildren().forEach(n -> { if (n instanceof Button b) b.setStyle(EXISTING_STYLE); });
        newClassesPane.getChildren().forEach(n -> {
            if (n instanceof Button b && !b.isDisabled()) b.setStyle(NEW_CLASS_STYLE);
        });

        sourceBtn.setStyle(SELECTED_STYLE);
        selectedClass = className;

        Race race = character.getRace();
        int conTotal = character.getConstitution() + bonus(race, "CON");
        int conMod = (conTotal - 10) / 2;
        hpGain = Math.max(1, (hitDie / 2) + 1 + conMod);

        lblHpPreview.setText("+" + hpGain + " HP  (Gesamt: " + (resolvedCurrentHp() + hpGain) + ")");
        lblHpFormula.setText("d" + hitDie + " Durchschnitt (" + ((hitDie / 2) + 1) + ") + KON-Mod (" +
            (conMod >= 0 ? "+" : "") + conMod + ")" +
            (isNewClass ? "  —  Neue Klasse: Startet auf Lv.1" : ""));
        hpPreviewBox.setVisible(true);
        hpPreviewBox.setManaged(true);
        btnConfirm.setDisable(false);
    }

    private boolean confirm() {
        if (selectedClass == null) return false;
        int currentClassLevel = character.getClassLevels().getOrDefault(selectedClass, 0);
        int newClassLevel = currentClassLevel + 1;
        int newMaxHp = resolvedCurrentHp() + hpGain;

        boolean saved = dbManager.levelUpCharacter(character.getDbId(), selectedClass, newClassLevel, newMaxHp);
        if (saved) {
            character.getClassLevels().put(selectedClass, newClassLevel);
            character.setMaxHp(newMaxHp);
        }
        return saved;
    }

    /** Returns stored max HP or computes level-1 HP as fallback for old characters. */
    private int resolvedCurrentHp() {
        if (character.getMaxHp() > 0) return character.getMaxHp();
        Race race = character.getRace();
        int conMod = (character.getConstitution() + bonus(race, "CON") - 10) / 2;
        int hitDie = character.getClassHitDie() == 0 ? 8 : character.getClassHitDie();
        return Math.max(1, hitDie + conMod);
    }

    private boolean meetsPrereq(String className, int[] scores) {
        if ("Fighter".equals(className)) return scores[0] >= 13 || scores[1] >= 13;
        int[] req = PREREQS.getOrDefault(className, new int[6]);
        for (int i = 0; i < 6; i++) {
            if (scores[i] < req[i]) return false;
        }
        return true;
    }

    private String prereqText(String className) {
        if ("Fighter".equals(className)) return "STR 13 oder DEX 13";
        int[] req = PREREQS.getOrDefault(className, new int[6]);
        String[] names = {"STR", "DEX", "KON", "INT", "WIS", "CHA"};
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 6; i++) {
            if (req[i] > 0) {
                if (sb.length() > 0) sb.append(", ");
                sb.append(names[i]).append(" ").append(req[i]);
            }
        }
        return sb.toString();
    }

    private int bonus(Race race, String ability) {
        return race != null ? race.getAbilityBonuses().getOrDefault(ability, 0) : 0;
    }
}
