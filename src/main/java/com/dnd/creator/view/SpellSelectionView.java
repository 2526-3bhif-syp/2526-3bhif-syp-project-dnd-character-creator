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
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

public class SpellSelectionView {

    private static final Set<String> SPELLCASTING_CLASSES =
            Set.of("bard", "cleric", "druid", "sorcerer", "warlock", "wizard");

    @FXML private HBox stepperContainer;
    @FXML private VBox previewContainer;
    @FXML private VBox cantripSection;
    @FXML private VBox spellSection;
    @FXML private FlowPane cantripPane;
    @FXML private FlowPane spellPane;
    @FXML private Label lblCantripTitle;
    @FXML private Label lblSpellTitle;
    @FXML private Label lblCantripCounter;
    @FXML private Label lblSpellCounter;
    @FXML private Button btnBack;
    @FXML private Button btnNext;

    private Parent root;
    private final DbManager dbManager = new DbManager();
    private final CharacterPreviewPanel preview = new CharacterPreviewPanel();

    private final List<VBox> cantripCards = new ArrayList<>();
    private final List<VBox> spellCards   = new ArrayList<>();
    private final Set<String> selectedCantrips = new LinkedHashSet<>();
    private final Set<String> selectedSpells   = new LinkedHashSet<>();

    private int maxCantrips = 0;
    private int maxSpells   = 0;

    public SpellSelectionView() {
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/com/dnd/creator/view/SpellSelectionView.fxml"));
            loader.setController(this);
            root = loader.load();
            CreationStyles.attach(root);

            dbManager.connect();

            stepperContainer.getChildren().setAll(new StepperBar(7).getRoot());
            previewContainer.getChildren().setAll(preview.getRoot());

            initialize();
        } catch (IOException e) {
            throw new RuntimeException("Failed to load SpellSelectionView.fxml", e);
        }
    }

    private void initialize() {
        CharacterModel character = CharacterSession.getInstance().getCurrentCharacter();
        String classIdx = character.getClassIndex() != null ? character.getClassIndex() : "";

        Map<String, Integer> slots = dbManager.getSpellSlotsAtLevel(classIdx, 1);
        maxCantrips = slots.getOrDefault("cantrips_known", 0);
        maxSpells   = slots.getOrDefault("spells_known", 0);
        if (maxSpells == 0) maxSpells = 4;

        if (maxCantrips > 0) {
            lblCantripTitle.setText("Cantrips wählen");
            updateCounter(lblCantripCounter, 0, maxCantrips);
            List<Map<String, String>> cantrips = dbManager.getSpellsForClass(classIdx, 0);
            for (Map<String, String> spell : cantrips) {
                VBox card = buildSpellCard(spell, cantripCards, selectedCantrips, maxCantrips, lblCantripCounter);
                cantripCards.add(card);
                cantripPane.getChildren().add(card);
            }
            cantripSection.setVisible(true);
            cantripSection.setManaged(true);
        }

        if (maxSpells > 0) {
            lblSpellTitle.setText("Level-1-Zauber wählen");
            updateCounter(lblSpellCounter, 0, maxSpells);
            List<Map<String, String>> spells = dbManager.getSpellsForClass(classIdx, 1);
            for (Map<String, String> spell : spells) {
                VBox card = buildSpellCard(spell, spellCards, selectedSpells, maxSpells, lblSpellCounter);
                spellCards.add(card);
                spellPane.getChildren().add(card);
            }
            spellSection.setVisible(true);
            spellSection.setManaged(true);
        }

        restoreSaved();
        setupButtons();
    }

    private VBox buildSpellCard(Map<String, String> spell,
                                List<VBox> cardList,
                                Set<String> selected,
                                int max,
                                Label counter) {
        String name   = spell.getOrDefault("name", "");
        String school = capitalize(spell.getOrDefault("school", ""));
        String time   = spell.getOrDefault("casting_time", "");
        String range  = spell.getOrDefault("range", "");
        String dur    = spell.getOrDefault("duration", "");

        VBox card = new VBox(4);
        card.getStyleClass().add("spell-card");
        card.setPrefWidth(170);

        Label lblName = new Label(name);
        lblName.getStyleClass().add("spell-card-name");
        lblName.setWrapText(true);

        Label lblSchool = new Label(school);
        lblSchool.getStyleClass().add("spell-card-school");

        Label lblMeta = new Label("⏱ " + time + "  ·  ↔ " + range);
        lblMeta.getStyleClass().add("spell-card-meta");
        lblMeta.setWrapText(true);

        card.getChildren().addAll(lblName, lblSchool, lblMeta);

        String tooltip = "Schule: " + school + "\nWirkzeit: " + time +
                         "\nReichweite: " + range + (dur.isBlank() ? "" : "\nDauer: " + dur);
        Tooltip.install(card, new Tooltip(tooltip));

        card.setOnMouseClicked(e -> {
            if (selected.contains(name)) {
                selected.remove(name);
                card.getStyleClass().remove("spell-card-selected");
            } else if (selected.size() < max) {
                selected.add(name);
                card.getStyleClass().add("spell-card-selected");
            }
            updateCounter(counter, selected.size(), max);
        });

        return card;
    }

    private void updateCounter(Label lbl, int current, int max) {
        lbl.setText(current + " / " + max + " gewählt");
        if (current == max) {
            lbl.getStyleClass().remove("skill-counter");
            lbl.getStyleClass().add("skill-counter-done");
        } else {
            lbl.getStyleClass().remove("skill-counter-done");
            lbl.getStyleClass().add("skill-counter");
        }
    }

    private void restoreSaved() {
        CharacterModel ch = CharacterSession.getInstance().getCurrentCharacter();

        if (ch.getSelectedCantrips() != null) {
            selectedCantrips.addAll(ch.getSelectedCantrips());
            cantripCards.forEach(card -> {
                Label lbl = (Label) card.getChildren().get(0);
                if (selectedCantrips.contains(lbl.getText()))
                    card.getStyleClass().add("spell-card-selected");
            });
            updateCounter(lblCantripCounter, selectedCantrips.size(), maxCantrips);
        }

        if (ch.getSelectedSpells() != null) {
            selectedSpells.addAll(ch.getSelectedSpells());
            spellCards.forEach(card -> {
                Label lbl = (Label) card.getChildren().get(0);
                if (selectedSpells.contains(lbl.getText()))
                    card.getStyleClass().add("spell-card-selected");
            });
            updateCounter(lblSpellCounter, selectedSpells.size(), maxSpells);
        }
    }

    private void setupButtons() {
        btnBack.setOnAction(e -> {
            Stage stage = (Stage) root.getScene().getWindow();
            stage.setScene(new Scene(new AlignmentView().getRoot(),
                    stage.getScene().getWidth(), stage.getScene().getHeight()));
        });

        btnNext.setOnAction(e -> {
            if (maxCantrips > 0 && selectedCantrips.size() != maxCantrips) {
                showWarning("Bitte wähle genau " + maxCantrips + " Cantrips aus.");
                return;
            }
            if (maxSpells > 0 && selectedSpells.size() != maxSpells) {
                showWarning("Bitte wähle genau " + maxSpells + " Zauber aus.");
                return;
            }

            CharacterModel ch = CharacterSession.getInstance().getCurrentCharacter();
            ch.setSelectedCantrips(new ArrayList<>(selectedCantrips));
            ch.setSelectedSpells(new ArrayList<>(selectedSpells));

            Stage stage = (Stage) root.getScene().getWindow();
            stage.setScene(new Scene(new CharacterSummaryView().getRoot(),
                    stage.getScene().getWidth(), stage.getScene().getHeight()));
        });
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
        return SPELLCASTING_CLASSES.stream().anyMatch(c -> c.equalsIgnoreCase(classIndex));
    }

    public Parent getRoot() { return root; }
}
