package com.dnd.creator.view;

import com.dnd.creator.model.CharacterModel;
import javafx.animation.ScaleTransition;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.util.Duration;

import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class CharacterCardView {

    @FXML private ImageView imageView;
    @FXML private Label nameLabel;
    @FXML private Label classLabel;
    @FXML private Label raceLabel;
    @FXML private Label hpLabel;
    @FXML private Label starsLabel;
    @FXML private Label bgLabel;
    @FXML private Label strLabel;
    @FXML private Label dexLabel;
    @FXML private Label conLabel;
    @FXML private Label intLabel;
    @FXML private Label wisLabel;
    @FXML private Label chaLabel;
    @FXML private HBox headerBox;
    @FXML private VBox cardRoot;

    private Parent root;
    private Consumer<CharacterModel> onCardClicked;

    // [headerStart, headerEnd, glowColor, classBadgeBg, classBadgeBorder]
    private static final Map<String, String[]> CLASS_COLORS = Map.ofEntries(
        Map.entry("barbarian", new String[]{"#6B0F0F", "#AA2222", "#FF5533", "#7A0A0A", "#FF8060"}),
        Map.entry("bard",      new String[]{"#4B1080", "#8533BB", "#DD88FF", "#600E9A", "#CC77EE"}),
        Map.entry("cleric",    new String[]{"#7A5A00", "#BF8F00", "#FFD700", "#8B6600", "#FFE566"}),
        Map.entry("druid",     new String[]{"#1A4A1A", "#2A7A38", "#66CC66", "#1A5E1A", "#88DD88"}),
        Map.entry("fighter",   new String[]{"#152A5C", "#2255A8", "#77AAEE", "#1A3880", "#88BBFF"}),
        Map.entry("monk",      new String[]{"#7A3A00", "#BB6600", "#FF9933", "#8B4200", "#FFBB55"}),
        Map.entry("paladin",   new String[]{"#103A60", "#1A6699", "#66BBEE", "#155580", "#88CCFF"}),
        Map.entry("ranger",    new String[]{"#1A4228", "#256635", "#55AA66", "#1A5530", "#77CC88"}),
        Map.entry("rogue",     new String[]{"#151528", "#222255", "#8888CC", "#1A1A40", "#AAAADD"}),
        Map.entry("sorcerer",  new String[]{"#5C0A0A", "#991515", "#FF4444", "#770000", "#FF7777"}),
        Map.entry("warlock",   new String[]{"#220A40", "#550F88", "#AA55DD", "#380A60", "#CC88EE"}),
        Map.entry("wizard",    new String[]{"#0A1840", "#1535AA", "#4477DD", "#0D2060", "#6699FF"})
    );

    private static final String[] DEFAULT_COLORS =
        {"#5A2800", "#8B4513", "#D4AF37", "#6B3000", "#FFCC66"};

    public CharacterCardView(CharacterModel character) {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/dnd/creator/view/CharacterCard.fxml"));
        loader.setController(this);
        try {
            root = loader.load();
            updateUI(character);
            setupHoverAnimation(character);
            root.setOnMouseClicked(e -> {
                if (onCardClicked != null) onCardClicked.accept(character);
            });
        } catch (IOException e) {
            throw new RuntimeException("Failed to load CharacterCard.fxml", e);
        }
    }

    private String[] colorsFor(CharacterModel character) {
        if (character.getCharacterClass() == null) return DEFAULT_COLORS;
        return CLASS_COLORS.getOrDefault(
            character.getCharacterClass().toLowerCase().trim(),
            DEFAULT_COLORS
        );
    }

    private void updateUI(CharacterModel character) {
        if (character == null) return;

        String[] c = colorsFor(character);

        // ── Basic stats ──
        nameLabel.setText(character.getName() != null ? character.getName() : "Unnamed");
        strLabel.setText(String.valueOf(character.getStrength()));
        dexLabel.setText(String.valueOf(character.getDexterity()));
        conLabel.setText(String.valueOf(character.getConstitution()));
        intLabel.setText(String.valueOf(character.getIntelligence()));
        wisLabel.setText(String.valueOf(character.getWisdom()));
        chaLabel.setText(String.valueOf(character.getCharisma()));

        // ── Stat badge color matches class ──
        String statBadgeStyle =
            "-fx-background-color: " + c[0] + "; -fx-text-fill: white;" +
            "-fx-font-weight: bold; -fx-font-size: 11;" +
            "-fx-background-radius: 3; -fx-padding: 1 4 1 4;";
        for (Label stat : new Label[]{strLabel, dexLabel, conLabel, intLabel, wisLabel, chaLabel}) {
            stat.setStyle(statBadgeStyle);
        }

        // ── Header gradient ──
        headerBox.setStyle(
            "-fx-padding: 8 10 8 10; -fx-background-radius: 6 6 0 0;" +
            "-fx-background-color: linear-gradient(to right, " + c[0] + ", " + c[1] + ");"
        );

        // ── Class badge ──
        String cls = character.getCharacterClass() != null
            ? capitalize(character.getCharacterClass()) : "—";
        classLabel.setText(cls);
        classLabel.setStyle(
            "-fx-background-color: " + c[3] + "; -fx-text-fill: #FFEEAA;" +
            "-fx-font-size: 10; -fx-font-weight: bold;" +
            "-fx-padding: 2 9 2 9; -fx-background-radius: 10;" +
            "-fx-border-color: " + c[4] + "; -fx-border-radius: 10; -fx-border-width: 1;"
        );

        // ── Race badge ──
        String race = (character.getRace() != null && character.getRace().getName() != null)
            ? character.getRace().getName() : "—";
        raceLabel.setText(race);

        // ── Hit die ──
        int hitDie = character.getClassHitDie();
        hpLabel.setText(hitDie > 0 ? "d" + hitDie : "—");

        // ── Stars: d6=3, d8=4, d10=5, d12=6 ──
        int stars = hitDie >= 6 ? hitDie / 2 : 3;
        starsLabel.setText("★".repeat(Math.min(stars, 6)));

        // ── Flavor text footer ──
        String bg = character.getSelectedBackground();
        String alignment = character.getAlignment();
        String flavor = Stream.of(bg, alignment)
            .filter(s -> s != null && !s.isBlank())
            .collect(Collectors.joining(" · "));
        bgLabel.setText(flavor.isBlank() ? "Unknown Background" : flavor);

        // ── Image ──
        String path = character.getImagePath();
        if (path != null && !path.isBlank()) {
            File file = new File(path);
            if (file.exists()) {
                imageView.setImage(new Image(file.toURI().toString()));
            }
        }
    }

    private void setupHoverAnimation(CharacterModel character) {
        String[] c = colorsFor(character);

        ScaleTransition scaleIn = new ScaleTransition(Duration.millis(130), root);
        scaleIn.setToX(1.07);
        scaleIn.setToY(1.07);

        ScaleTransition scaleOut = new ScaleTransition(Duration.millis(130), root);
        scaleOut.setToX(1.0);
        scaleOut.setToY(1.0);

        String defaultStyle = buildCardStyle("rgba(0,0,0,0.75)", 16);
        String hoverStyle   = buildCardStyle(c[2], 28);

        root.setOnMouseEntered(e -> {
            cardRoot.setStyle(hoverStyle);
            scaleIn.playFromStart();
        });
        root.setOnMouseExited(e -> {
            cardRoot.setStyle(defaultStyle);
            scaleOut.playFromStart();
        });
    }

    private String buildCardStyle(String glowColor, int glowRadius) {
        return "-fx-min-width: 252; -fx-max-width: 252; -fx-min-height: 395;" +
               "-fx-background-color: #FDF5E6;" +
               "-fx-border-color: #FFD700 #B8960C #B8960C #FFD700; -fx-border-width: 4;" +
               "-fx-background-radius: 10; -fx-border-radius: 10;" +
               "-fx-effect: dropshadow(three-pass-box, " + glowColor + ", " + glowRadius + ", 0.4, 0, 0);";
    }

    private String capitalize(String s) {
        if (s == null || s.isEmpty()) return s;
        return Character.toUpperCase(s.charAt(0)) + s.substring(1).toLowerCase();
    }

    public Parent getRoot() {
        return root;
    }

    public void setOnCardClicked(Consumer<CharacterModel> callback) {
        this.onCardClicked = callback;
    }
}
