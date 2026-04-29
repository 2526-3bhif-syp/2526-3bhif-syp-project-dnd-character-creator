package com.dnd.creator.view;

import com.dnd.creator.data.DbManager;
import com.dnd.creator.model.CharacterModel;
import com.dnd.creator.model.CharacterSession;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

public class SpellSelectionView {

    private static final Set<String> SPELLCASTING_CLASSES =
            Set.of("bard", "cleric", "druid", "sorcerer", "warlock", "wizard");

    @FXML private VBox cantripSection;
    @FXML private VBox spellSection;
    @FXML private FlowPane cantripPane;
    @FXML private FlowPane spellPane;
    @FXML private Label lblCantripTitle;
    @FXML private Label lblSpellTitle;
    @FXML private Button btnBack;
    @FXML private Button btnNext;

    private Parent root;
    private final DbManager dbManager = new DbManager();
    private final List<CheckBox> cantripBoxes = new ArrayList<>();
    private final List<CheckBox> spellBoxes   = new ArrayList<>();
    private int maxCantrips = 0;
    private int maxSpells   = 0;

    public SpellSelectionView() {
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/com/dnd/creator/view/SpellSelectionView.fxml"));
            loader.setController(this);
            root = loader.load();
            dbManager.connect();
            initialize();
        } catch (IOException e) {
            throw new RuntimeException("Failed to load SpellSelectionView.fxml", e);
        }
    }

    private void initialize() {
        CharacterModel character = CharacterSession.getInstance().getCurrentCharacter();
        String classIdx = character.getClassIndex() != null
                ? character.getClassIndex().toLowerCase() : "";

        Map<String, Integer> slots = dbManager.getSpellSlotsAtLevel(classIdx, 1);
        maxCantrips = slots.getOrDefault("cantrips_known", 0);
        maxSpells   = slots.getOrDefault("spells_known", 0);
        if (maxSpells == 0) maxSpells = 4; // Prepared casters (cleric, druid, wizard)

        // Cantrips
        if (maxCantrips > 0) {
            lblCantripTitle.setText("Cantrips wählen (" + maxCantrips + "):");
            List<Map<String, String>> cantrips = dbManager.getSpellsForClass(classIdx, 0);
            for (Map<String, String> spell : cantrips) {
                CheckBox cb = new CheckBox(spell.get("name"));
                cb.setStyle("-fx-font-size: 13px;");
                cb.setTooltip(new Tooltip(
                        "Schule: " + capitalize(spell.get("school")) +
                                "\nWirkzeit: " + spell.get("casting_time") +
                                "\nReichweite: " + spell.get("range")));
                cb.setOnAction(e -> enforceLimit(cantripBoxes, maxCantrips));
                cantripBoxes.add(cb);
                cantripPane.getChildren().add(cb);
            }
            cantripSection.setVisible(true);
            cantripSection.setManaged(true);
        }

        // Level 1 Spells
        if (maxSpells > 0) {
            lblSpellTitle.setText("Level-1-Zauber wählen (" + maxSpells + "):");
            List<Map<String, String>> spells = dbManager.getSpellsForClass(classIdx, 1);
            for (Map<String, String> spell : spells) {
                CheckBox cb = new CheckBox(spell.get("name"));
                cb.setStyle("-fx-font-size: 13px;");
                cb.setTooltip(new Tooltip(
                        "Schule: " + capitalize(spell.get("school")) +
                                "\nWirkzeit: " + spell.get("casting_time") +
                                "\nReichweite: " + spell.get("range") +
                                "\nDauer: " + spell.get("duration")));
                cb.setOnAction(e -> enforceLimit(spellBoxes, maxSpells));
                spellBoxes.add(cb);
                spellPane.getChildren().add(cb);
            }
            spellSection.setVisible(true);
            spellSection.setManaged(true);
        }

        setupButtons();
        restoreSaved();
    }

    private void setupButtons() {
        btnBack.setOnAction(e -> {
            Stage stage = (Stage) root.getScene().getWindow();
            stage.setScene(new Scene(new AlignmentView().getRoot(),
                    stage.getScene().getWidth(), stage.getScene().getHeight()));
        });

        btnNext.setOnAction(e -> {
            long chosenCantrips = cantripBoxes.stream().filter(CheckBox::isSelected).count();
            long chosenSpells   = spellBoxes.stream().filter(CheckBox::isSelected).count();

            if (maxCantrips > 0 && chosenCantrips != maxCantrips) {
                showWarning("Bitte wähle genau " + maxCantrips + " Cantrips aus.");
                return;
            }
            if (maxSpells > 0 && chosenSpells != maxSpells) {
                showWarning("Bitte wähle genau " + maxSpells + " Zauber aus.");
                return;
            }

            CharacterModel ch = CharacterSession.getInstance().getCurrentCharacter();
            ch.setSelectedCantrips(cantripBoxes.stream()
                    .filter(CheckBox::isSelected).map(CheckBox::getText)
                    .collect(Collectors.toList()));
            ch.setSelectedSpells(spellBoxes.stream()
                    .filter(CheckBox::isSelected).map(CheckBox::getText)
                    .collect(Collectors.toList()));

            Stage stage = (Stage) root.getScene().getWindow();
            stage.setScene(new Scene(new CharacterSummaryView().getRoot(),
                    stage.getScene().getWidth(), stage.getScene().getHeight()));
        });
    }

    private void enforceLimit(List<CheckBox> boxes, int max) {
        long selected = boxes.stream().filter(CheckBox::isSelected).count();
        if (selected > max) {
            boxes.stream().filter(CheckBox::isSelected)
                    .reduce((a, b) -> b)
                    .ifPresent(last -> last.setSelected(false));
        }
    }

    private void restoreSaved() {
        CharacterModel ch = CharacterSession.getInstance().getCurrentCharacter();
        if (ch.getSelectedCantrips() != null)
            cantripBoxes.forEach(cb -> cb.setSelected(
                    ch.getSelectedCantrips().contains(cb.getText())));
        if (ch.getSelectedSpells() != null)
            spellBoxes.forEach(cb -> cb.setSelected(
                    ch.getSelectedSpells().contains(cb.getText())));
    }

    private void showWarning(String msg) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setHeaderText(null);
        alert.setContentText(msg);
        alert.showAndWait();
    }

    private String capitalize(String s) {
        if (s == null || s.isEmpty()) return "";
        return s.substring(0, 1).toUpperCase() + s.substring(1);
    }

    public static boolean isSpellcaster(String classIndex) {
        if (classIndex == null) return false;
        return SPELLCASTING_CLASSES.contains(classIndex.toLowerCase());
    }

    public Parent getRoot() { return root; }
}