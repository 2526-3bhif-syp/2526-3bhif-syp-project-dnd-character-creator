package com.dnd.creator.view;

import com.dnd.creator.model.CharacterModel;
import com.dnd.creator.model.CharacterSession;
import com.dnd.creator.model.Race;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.paint.ImagePattern;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Rectangle;

import java.io.File;
import java.io.InputStream;
import java.util.LinkedHashMap;
import java.util.Map;

public class CharacterPreviewPanel {

    private static final int BAR_WIDTH = 110; // fixed px — avoids binding-to-unrendered-node bug


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
        portraitBox.setPrefHeight(130);
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

        refreshPortrait(className, raceName); // args unused but kept for signature compat

        // Stat bars
        rebuildStats(c);
    }

    // ── Portrait ──────────────────────────────────────────────────────────────

    private void refreshPortrait(String className, String raceName) {
        portraitBox.getChildren().clear();

        CharacterModel c = CharacterSession.getInstance().getCurrentCharacter();
        String imgPath = c.getImagePath();

        if (imgPath != null && !imgPath.equals("placeholder.png")) {
            File file = new File(imgPath);
            if (file.exists()) {
                // panel prefWidth=300 minus 20px padding each side = 260, box height = 128 (130 - 2px border)
                double w = 256, h = 126;
                Image image = new Image(file.toURI().toString());

                // cover: scale so the image fills the box, crop overflow
                double iw = image.getWidth(), ih = image.getHeight();
                double scale = Math.max(w / iw, h / ih);
                double sw = iw * scale, sh = ih * scale;
                double ox = (w - sw) / 2, oy = (h - sh) / 2;

                Rectangle rect = new Rectangle(w, h);
                rect.setArcWidth(24);
                rect.setArcHeight(24);
                rect.setFill(new ImagePattern(image, ox, oy, sw, sh, false));
                portraitBox.getChildren().add(rect);
                return;
            }
        }

        Label noImg = new Label("Kein Bild\nhochgeladen");
        noImg.setStyle("-fx-font-size: 13px; -fx-text-alignment: center; -fx-text-fill: #888;");
        noImg.setWrapText(true);
        portraitBox.getChildren().add(noImg);
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
