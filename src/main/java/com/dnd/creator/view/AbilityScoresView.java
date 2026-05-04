package com.dnd.creator.view;

import com.dnd.creator.model.CharacterModel;
import com.dnd.creator.model.CharacterSession;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.Tooltip;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.StringConverter;

import java.io.IOException;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class AbilityScoresView {

    private static final int UNASSIGNED = 0;
    private static final List<Integer> STANDARD_ARRAY = List.of(15, 14, 13, 12, 10, 8);

    private static final String[][] STATS = {
        {"STR", "Stärke",          "biceps.png",  "Wie hart du zuschlägst und wie viel du tragen kannst."},
        {"DEX", "Geschicklichkeit", "target.png",  "Schnelligkeit, Zielen und Reflexe."},
        {"CON", "Konstitution",     "heart.png",   "Wie viele Lebenspunkte du hast — wie viel du aushältst."},
        {"INT", "Intelligenz",      "book.png",    "Buchwissen und logisches Denken — wichtig für Magier."},
        {"WIS", "Weisheit",         "weisheit.png","Wahrnehmung, Intuition und Naturverbundenheit."},
        {"CHA", "Charisma",         "speak.png",   "Reden, Auftreten und Überzeugungskraft."}
    };

    /** Recommended distributions by class — STR,DEX,CON,INT,WIS,CHA */
    private static final Map<String, int[]> RECOMMENDED = new LinkedHashMap<>() {{
        put("Barbarian", new int[]{15, 13, 14, 8, 12, 10});
        put("Bard",      new int[]{8,  14, 13, 12, 10, 15});
        put("Cleric",    new int[]{13, 8,  14, 10, 15, 12});
        put("Druid",     new int[]{8,  13, 14, 12, 15, 10});
        put("Fighter",   new int[]{15, 13, 14, 10, 12, 8});
        put("Monk",      new int[]{12, 15, 13, 10, 14, 8});
        put("Paladin",   new int[]{15, 10, 13, 8,  12, 14});
        put("Ranger",    new int[]{10, 15, 13, 12, 14, 8});
        put("Rogue",     new int[]{8,  15, 14, 13, 12, 10});
        put("Sorcerer",  new int[]{8,  13, 14, 12, 10, 15});
        put("Warlock",   new int[]{8,  13, 14, 10, 12, 15});
        put("Wizard",    new int[]{8,  14, 13, 15, 12, 10});
    }};

    @FXML private HBox stepperContainer;
    @FXML private VBox previewContainer;
    @FXML private VBox statsContainer;
    @FXML private HBox poolContainer;
    @FXML private Button btnAutoAssign;
    @FXML private Button btnReset;
    @FXML private Button btnBack;
    @FXML private Button btnNext;

    private Parent root;
    private final CharacterPreviewPanel preview = new CharacterPreviewPanel();
    private final Map<String, Integer> assignments = new LinkedHashMap<>();
    private final Map<String, ComboBox<Integer>> combos = new LinkedHashMap<>();
    private final Map<String, Label> modifierLabels = new LinkedHashMap<>();
    private final Map<String, Label> finalLabels = new LinkedHashMap<>();
    private final Map<String, Region> barFills = new LinkedHashMap<>();
    private final Map<String, StackPane> barContainers = new LinkedHashMap<>();
    private boolean suppressEvents = false;

    public AbilityScoresView() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/dnd/creator/view/AbilityScoresView.fxml"));
            loader.setController(this);
            root = loader.load();
            CreationStyles.attach(root);

            stepperContainer.getChildren().setAll(new StepperBar(3).getRoot());
            previewContainer.getChildren().setAll(preview.getRoot());

            for (String[] s : STATS) assignments.put(s[0], UNASSIGNED);
            loadFromSession();
            buildStatRows();
            refreshAllCombos();
            refreshPool();
            wireActions();
            wireNavigation();
            updateNextButton();

        } catch (IOException e) {
            throw new RuntimeException("Failed to load AbilityScoresView.fxml", e);
        }
    }

    public Parent getRoot() {
        return root;
    }

    private void loadFromSession() {
        CharacterModel c = CharacterSession.getInstance().getCurrentCharacter();
        if (c.getStrength()     != 0) assignments.put("STR", c.getStrength());
        if (c.getDexterity()    != 0) assignments.put("DEX", c.getDexterity());
        if (c.getConstitution() != 0) assignments.put("CON", c.getConstitution());
        if (c.getIntelligence() != 0) assignments.put("INT", c.getIntelligence());
        if (c.getWisdom()       != 0) assignments.put("WIS", c.getWisdom());
        if (c.getCharisma()     != 0) assignments.put("CHA", c.getCharisma());
    }

    private boolean isPrimary(String stat) {
        String className = CharacterSession.getInstance().getCurrentCharacter().getCharacterClass();
        if (className == null) return false;
        int[] arr = RECOMMENDED.get(className);
        if (arr == null) return false;
        int idx = indexOf(stat);
        if (idx < 0) return false;
        int[] sorted = arr.clone();
        Arrays.sort(sorted);
        int secondHighest = sorted[sorted.length - 2];
        return arr[idx] >= secondHighest;
    }

    /** Returns the set of stat keys (e.g. "STR", "DEX") that are primary for the given class. */
    static Set<String> getPrimaryStats(String className) {
        if (className == null) return Set.of();
        int[] arr = RECOMMENDED.get(className);
        if (arr == null) return Set.of();
        int[] sorted = arr.clone();
        Arrays.sort(sorted);
        int secondHighest = sorted[sorted.length - 2];
        String[] keys = {"STR", "DEX", "CON", "INT", "WIS", "CHA"};
        Set<String> result = new java.util.HashSet<>();
        for (int i = 0; i < keys.length; i++) {
            if (arr[i] >= secondHighest) result.add(keys[i]);
        }
        return result;
    }

    private static int indexOf(String stat) {
        return switch (stat) {
            case "STR" -> 0;
            case "DEX" -> 1;
            case "CON" -> 2;
            case "INT" -> 3;
            case "WIS" -> 4;
            case "CHA" -> 5;
            default -> -1;
        };
    }

    private static String nameFromShort(String stat) {
        return switch (stat) {
            case "STR" -> "Strength";
            case "DEX" -> "Dexterity";
            case "CON" -> "Constitution";
            case "INT" -> "Intelligence";
            case "WIS" -> "Wisdom";
            case "CHA" -> "Charisma";
            default -> stat;
        };
    }

    private void buildStatRows() {
        for (String[] def : STATS) {
            statsContainer.getChildren().add(buildStatRow(def[0], def[1], def[2], def[3]));
        }
    }

    private HBox buildStatRow(String key, String german, String icon, String desc) {
        HBox row = new HBox(14);
        row.getStyleClass().add("stat-row");
        row.setAlignment(Pos.CENTER_LEFT);
        if (isPrimary(key)) row.getStyleClass().add("stat-row-primary");

        ImageView iconLabel = new ImageView(new Image(
            getClass().getResourceAsStream("/com/dnd/creator/pics/" + icon)));
        iconLabel.setFitWidth(32);
        iconLabel.setFitHeight(32);
        iconLabel.setPreserveRatio(true);
        iconLabel.getStyleClass().add("stat-icon");

        VBox middle = new VBox(2);
        HBox.setHgrow(middle, Priority.ALWAYS);

        HBox titleRow = new HBox(8);
        titleRow.setAlignment(Pos.CENTER_LEFT);
        Label nameLabel = new Label(key + " — " + german);
        nameLabel.getStyleClass().add("stat-name");
        titleRow.getChildren().add(nameLabel);
        if (isPrimary(key)) {
            Label primary = new Label("Hauptwert");
            primary.getStyleClass().add("primary-badge");
            Tooltip.install(primary, new Tooltip(
                "Dies ist der wichtigste Wert für deine Klasse. Hier solltest du die höchste Zahl (15) hinpacken."
            ));
            titleRow.getChildren().add(primary);
        }
        var race = CharacterSession.getInstance().getCurrentCharacter().getRace();
        if (race != null) {
            int bonus = race.getAbilityBonuses().getOrDefault(key, 0);
            if (bonus > 0) {
                Label raceChip = new Label("+" + bonus + " Rasse");
                raceChip.getStyleClass().add("race-bonus-chip");
                Tooltip.install(raceChip, new Tooltip("Rassenbonus wird zum Endwert addiert."));
                titleRow.getChildren().add(raceChip);
            }
        }

        Label descLabel = new Label(desc);
        descLabel.getStyleClass().add("stat-desc");
        descLabel.setWrapText(true);

        StackPane bar = makeBar(key);
        barContainers.put(key, bar);

        middle.getChildren().addAll(titleRow, descLabel, bar);

        ComboBox<Integer> combo = new ComboBox<>();
        combo.getStyleClass().add("value-combo");
        combo.setConverter(new StringConverter<>() {
            @Override public String toString(Integer v) {
                return v == null || v == UNASSIGNED ? "—" : v.toString();
            }
            @Override public Integer fromString(String s) {
                if (s == null || s.equals("—")) return UNASSIGNED;
                try { return Integer.parseInt(s); } catch (NumberFormatException e) { return UNASSIGNED; }
            }
        });
        combo.valueProperty().addListener((obs, oldV, newV) -> {
            if (suppressEvents) return;
            int newVal = newV == null ? UNASSIGNED : newV;
            assignments.put(key, newVal);
            refreshAllCombos();
            refreshPool();
            refreshModifier(key);
            refreshBar(key);
            saveToSession();
            preview.refresh();
            updateNextButton();
        });
        combos.put(key, combo);

        Label modifier = new Label("");
        modifier.getStyleClass().add("stat-modifier");
        modifier.setMinWidth(40);
        Tooltip.install(modifier, new Tooltip(
            "Modifikator — wird bei jedem Wurf addiert. +2 = recht gut, +3 = sehr gut."
        ));
        modifierLabels.put(key, modifier);

        Label finalLabel = new Label("—");
        finalLabel.getStyleClass().add("stat-final");
        finalLabel.setMinWidth(40);
        Tooltip.install(finalLabel, new Tooltip("Endwert inkl. Rassenbonus"));
        finalLabels.put(key, finalLabel);

        row.getChildren().addAll(iconLabel, middle, combo, modifier, finalLabel);
        return row;
    }

    private StackPane makeBar(String key) {
        Region bg = new Region();
        bg.getStyleClass().add("stat-bar-bg");
        bg.setMaxWidth(Double.MAX_VALUE);

        Region fill = new Region();
        fill.getStyleClass().add("stat-bar-fill");
        if (isPrimary(key)) fill.getStyleClass().add("stat-bar-fill-primary");
        fill.setMaxWidth(Region.USE_PREF_SIZE);

        StackPane sp = new StackPane(bg, fill);
        StackPane.setAlignment(fill, Pos.CENTER_LEFT);
        sp.setMaxWidth(Double.MAX_VALUE);

        barFills.put(key, fill);
        return sp;
    }

    private void refreshAllCombos() {
        suppressEvents = true;
        for (Map.Entry<String, ComboBox<Integer>> entry : combos.entrySet()) {
            String key = entry.getKey();
            ComboBox<Integer> combo = entry.getValue();
            int currentVal = assignments.getOrDefault(key, UNASSIGNED);

            // values used by other stats
            java.util.Set<Integer> usedByOthers = new java.util.HashSet<>();
            for (Map.Entry<String, Integer> a : assignments.entrySet()) {
                if (!a.getKey().equals(key) && a.getValue() != UNASSIGNED) {
                    usedByOthers.add(a.getValue());
                }
            }

            java.util.List<Integer> items = new java.util.ArrayList<>();
            items.add(UNASSIGNED);
            for (Integer v : STANDARD_ARRAY) {
                if (!usedByOthers.contains(v)) items.add(v);
            }

            combo.getItems().setAll(items);
            combo.setValue(currentVal);
            refreshModifier(key);
            refreshBar(key);
        }
        suppressEvents = false;
    }

    private void refreshModifier(String key) {
        int v = assignments.getOrDefault(key, UNASSIGNED);
        Label l = modifierLabels.get(key);
        Label fl = finalLabels.get(key);
        if (l == null) return;

        var race = CharacterSession.getInstance().getCurrentCharacter().getRace();
        int raceBonus = race != null ? race.getAbilityBonuses().getOrDefault(key, 0) : 0;

        if (v == UNASSIGNED) {
            l.setText("—");
            if (fl != null) fl.setText(raceBonus > 0 ? "+" + raceBonus : "—");
        } else {
            int mod = (v - 10) / 2;
            l.setText((mod >= 0 ? "+" : "") + mod);
            if (fl != null) fl.setText("= " + (v + raceBonus));
        }
    }

    private void refreshBar(String key) {
        int v = assignments.getOrDefault(key, UNASSIGNED);
        Region fill = barFills.get(key);
        StackPane container = barContainers.get(key);
        if (fill == null || container == null) return;
        double pct = v == UNASSIGNED ? 0 : Math.max(0, Math.min(1.0, v / 18.0));
        fill.prefWidthProperty().unbind();
        fill.prefWidthProperty().bind(container.widthProperty().multiply(pct));
    }

    private void refreshPool() {
        poolContainer.getChildren().clear();
        java.util.Set<Integer> used = new java.util.HashSet<>();
        for (int v : assignments.values()) if (v != UNASSIGNED) used.add(v);

        for (Integer v : STANDARD_ARRAY) {
            Label pill = new Label(String.valueOf(v));
            pill.getStyleClass().add("value-pool-pill");
            if (used.contains(v)) {
                pill.getStyleClass().add("value-pool-pill-used");
                pill.setText(v + " ✓");
            }
            poolContainer.getChildren().add(pill);
        }
    }

    private void saveToSession() {
        CharacterModel c = CharacterSession.getInstance().getCurrentCharacter();
        c.setStrength(    assignments.getOrDefault("STR", 0));
        c.setDexterity(   assignments.getOrDefault("DEX", 0));
        c.setConstitution(assignments.getOrDefault("CON", 0));
        c.setIntelligence(assignments.getOrDefault("INT", 0));
        c.setWisdom(      assignments.getOrDefault("WIS", 0));
        c.setCharisma(    assignments.getOrDefault("CHA", 0));
    }

    private void updateNextButton() {
        boolean allSet = assignments.values().stream().noneMatch(v -> v == UNASSIGNED);
        btnNext.setDisable(!allSet);
        if (!allSet) {
            Tooltip.install(btnNext, new Tooltip("Bitte verteile alle sechs Werte."));
        } else {
            Tooltip.uninstall(btnNext, btnNext.getTooltip());
        }
    }

    private void wireActions() {
        btnAutoAssign.setOnAction(e -> autoAssign());
        btnReset.setOnAction(e -> {
            for (String[] s : STATS) assignments.put(s[0], UNASSIGNED);
            refreshAllCombos();
            refreshPool();
            saveToSession();
            preview.refresh();
            updateNextButton();
        });
    }

    private void autoAssign() {
        String className = CharacterSession.getInstance().getCurrentCharacter().getCharacterClass();
        int[] arr = className == null ? null : RECOMMENDED.get(className);
        if (arr == null) {
            // default: 15,14,13,12,10,8 in stat order
            arr = new int[]{15, 14, 13, 12, 10, 8};
        }
        assignments.put("STR", arr[0]);
        assignments.put("DEX", arr[1]);
        assignments.put("CON", arr[2]);
        assignments.put("INT", arr[3]);
        assignments.put("WIS", arr[4]);
        assignments.put("CHA", arr[5]);
        refreshAllCombos();
        refreshPool();
        saveToSession();
        preview.refresh();
        updateNextButton();
    }

    private void wireNavigation() {
        btnBack.setOnAction(e -> {
            SelectClassView prev = new SelectClassView();
            Stage stage = (Stage) root.getScene().getWindow();
            stage.setScene(new Scene(prev.getRoot(), stage.getScene().getWidth(), stage.getScene().getHeight()));
        });
        btnNext.setOnAction(e -> {
            SkillsView next = new SkillsView();
            Stage stage = (Stage) root.getScene().getWindow();
            stage.setScene(new Scene(next.getRoot(), stage.getScene().getWidth(), stage.getScene().getHeight()));
        });
    }
}
