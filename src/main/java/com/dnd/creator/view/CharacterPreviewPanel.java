package com.dnd.creator.view;

import com.dnd.creator.model.CharacterModel;
import com.dnd.creator.model.CharacterSession;
import com.dnd.creator.model.Race;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

import java.io.InputStream;
import java.util.LinkedHashMap;
import java.util.Map;

public class CharacterPreviewPanel {

    private static final int BAR_WIDTH = 110; // fixed px — avoids binding-to-unrendered-node bug

    private static final Map<String, String> RACE_ICONS = Map.ofEntries(
        Map.entry("Dwarf",     "⛏"),  Map.entry("Elf",      "🏹"),
        Map.entry("Halfling",  "🍃"),  Map.entry("Human",    "🛡"),
        Map.entry("Dragonborn","🐉"),  Map.entry("Gnome",    "🔮"),
        Map.entry("Half-Elf",  "✨"),  Map.entry("Half-Orc", "⚔"),
        Map.entry("Tiefling",  "😈")
    );

    private static final Map<String, String> CLASS_ICONS = Map.ofEntries(
        Map.entry("Barbarian","🪓"), Map.entry("Bard",    "🎵"),
        Map.entry("Cleric",  "✝"),  Map.entry("Druid",   "🌿"),
        Map.entry("Fighter", "⚔"),  Map.entry("Monk",    "👊"),
        Map.entry("Paladin", "🛡"),  Map.entry("Ranger",  "🏹"),
        Map.entry("Rogue",   "🗡"),  Map.entry("Sorcerer","✨"),
        Map.entry("Warlock", "👁"),  Map.entry("Wizard",  "🔮")
    );

    /** Race names whose image file doesn't match raceName.toLowerCase(). */
    private static final Map<String, String> RACE_IMAGE_FILE = Map.of(
        "Half-Orc",          "orc",
        "Hill Dwarf",        "dwarf",
        "Mountain Dwarf",    "dwarf",
        "High Elf",          "elf",
        "Wood Elf",          "elf",
        "Drow",              "elf",
        "Lightfoot Halfling","halfling",
        "Stout Halfling",    "halfling",
        "Forest Gnome",      "gnome",
        "Rock Gnome",        "gnome"
    );

    private static final String[][] STAT_DEFS = {
        {"STR", "ST"}, {"DEX", "GE"}, {"CON", "KO"},
        {"INT", "IN"}, {"WIS", "WE"}, {"CHA", "CH"}
    };

    private final VBox root;
    private final StackPane portraitBox;
    private final Label nameLabel;
    private final Label raceLabel;
    private final Label classLabel;
    private final Label backgroundLabel;
    private final VBox statsBox;

    public CharacterPreviewPanel() {
        root = new VBox(10);
        root.getStyleClass().add("preview-panel");
        root.setMinWidth(280);
        root.setPrefWidth(300);
        root.setMaxWidth(320);

        Label heading = new Label("Vorschau");
        heading.getStyleClass().add("section-title");
        heading.setStyle("-fx-font-size: 18px;");

        portraitBox = new StackPane();
        portraitBox.getStyleClass().add("preview-portrait");
        portraitBox.setMinHeight(110);
        portraitBox.setPrefHeight(110);
        portraitBox.setMaxWidth(Double.MAX_VALUE);
        portraitBox.setAlignment(Pos.CENTER);

        nameLabel = new Label("Neuer Charakter");
        nameLabel.getStyleClass().add("preview-name");
        nameLabel.setWrapText(true);

        raceLabel   = new Label("Rasse: —");  raceLabel.getStyleClass().add("preview-line");
        classLabel  = new Label("Klasse: —"); classLabel.getStyleClass().add("preview-line");
        backgroundLabel = new Label("Hintergrund: —"); backgroundLabel.getStyleClass().add("preview-line");

        Region divider = new Region();
        divider.getStyleClass().add("preview-divider");
        divider.setMaxWidth(Double.MAX_VALUE);

        Label statsTitle = new Label("Werte");
        statsTitle.getStyleClass().add("section-title");
        statsTitle.setStyle("-fx-font-size: 14px;");

        statsBox = new VBox(5);

        root.getChildren().addAll(
            heading, portraitBox, nameLabel,
            raceLabel, classLabel, backgroundLabel,
            divider, statsTitle, statsBox
        );

        refresh();
    }

    public VBox getRoot() { return root; }

    public void refresh() {
        CharacterModel c = CharacterSession.getInstance().getCurrentCharacter();

        // Name
        String name = c.getName();
        nameLabel.setText(
            name == null || name.isBlank() || "New Character".equals(name) ? "Neuer Charakter" : name
        );

        // Race & class text
        Race race = c.getRace();
        String raceName  = race != null ? race.getName() : null;
        String className = c.getCharacterClass();

        raceLabel.setText("Rasse: "  + (raceName  != null ? raceName  : "—"));
        classLabel.setText("Klasse: " + (className != null && !className.isBlank() ? className : "—"));

        String bg = c.getSelectedBackground();
        backgroundLabel.setText("Hintergrund: " + (bg != null && !bg.isBlank() ? bg : "—"));

        // Portrait — try real image (class first, then race), emoji fallback
        refreshPortrait(className, raceName);

        // Stat bars
        rebuildStats(c);
    }

    // ── Portrait ──────────────────────────────────────────────────────────────

    private void refreshPortrait(String className, String raceName) {
        portraitBox.getChildren().clear();

        // 1. Try class image
        if (className != null && !className.isBlank()) {
            ImageView iv = tryImage(className.toLowerCase());
            if (iv != null) { portraitBox.getChildren().add(iv); return; }
        }

        // 2. Try race image
        if (raceName != null) {
            String key = RACE_IMAGE_FILE.getOrDefault(raceName,
                raceName.toLowerCase().replace(" ", "-").replace("'", ""));
            ImageView iv = tryImage(key);
            if (iv != null) { portraitBox.getChildren().add(iv); return; }
        }

        // 3. Emoji fallback
        String emoji = null;
        if (className != null) emoji = CLASS_ICONS.get(className);
        if (emoji == null && raceName != null) emoji = RACE_ICONS.get(raceName);
        if (emoji == null) emoji = "⚔";
        Label l = new Label(emoji);
        l.setStyle("-fx-font-size: 52px;");
        portraitBox.getChildren().add(l);
    }

    private ImageView tryImage(String fileKey) {
        InputStream is = CharacterPreviewPanel.class.getResourceAsStream(
            "/com/dnd/creator/pics/" + fileKey + ".png");
        if (is == null) return null;
        ImageView iv = new ImageView(new Image(is));
        iv.setFitWidth(90);
        iv.setFitHeight(90);
        iv.setPreserveRatio(true);
        return iv;
    }

    // ── Stats ─────────────────────────────────────────────────────────────────

    private void rebuildStats(CharacterModel c) {
        statsBox.getChildren().clear();

        Map<String, Integer> base = new LinkedHashMap<>();
        base.put("STR", c.getStrength());
        base.put("DEX", c.getDexterity());
        base.put("CON", c.getConstitution());
        base.put("INT", c.getIntelligence());
        base.put("WIS", c.getWisdom());
        base.put("CHA", c.getCharisma());

        boolean anyAssigned = base.values().stream().anyMatch(v -> v != 0);
        if (!anyAssigned) {
            Label hint = new Label("Noch keine Werte verteilt");
            hint.getStyleClass().add("muted");
            statsBox.getChildren().add(hint);
            return;
        }

        Race race = c.getRace();
        for (String[] def : STAT_DEFS) {
            String key   = def[0];
            String abbr  = def[1];
            int bv = base.getOrDefault(key, 0);
            if (bv == 0) continue; // skip unassigned — don't show confusing "—" rows

            int bonus = race != null ? race.getAbilityBonuses().getOrDefault(key, 0) : 0;
            int total = bv + bonus;

            HBox row = new HBox(6);
            row.setAlignment(Pos.CENTER_LEFT);

            Label statName = new Label(key);
            statName.getStyleClass().add("preview-stat-name");
            statName.setMinWidth(34);

            Region barBg = new Region();
            barBg.getStyleClass().add("preview-mini-bar-bg");
            barBg.setPrefWidth(BAR_WIDTH);
            barBg.setMinWidth(BAR_WIDTH);
            barBg.setMaxWidth(BAR_WIDTH);
            barBg.setPrefHeight(6);

            double pct = Math.max(0, Math.min(1.0, total / 20.0));
            Region barFill = new Region();
            barFill.getStyleClass().add("preview-mini-bar-fill");
            barFill.setPrefWidth(BAR_WIDTH * pct);
            barFill.setMinWidth(0);
            barFill.setMaxWidth(BAR_WIDTH * pct);
            barFill.setPrefHeight(6);

            StackPane bar = new StackPane(barBg, barFill);
            StackPane.setAlignment(barFill, Pos.CENTER_LEFT);

            String valueText = String.valueOf(total);
            if (bonus != 0) valueText = total + " (" + (bonus > 0 ? "+" : "") + bonus + ")";
            Label valLabel = new Label(valueText);
            valLabel.getStyleClass().add("preview-stat-value");
            valLabel.setMinWidth(44);

            row.getChildren().addAll(statName, bar, valLabel);
            statsBox.getChildren().add(row);
        }
    }
}
