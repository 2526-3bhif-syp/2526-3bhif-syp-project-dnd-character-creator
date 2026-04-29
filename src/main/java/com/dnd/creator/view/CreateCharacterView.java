package com.dnd.creator.view;

import com.dnd.creator.data.DbManager;
import com.dnd.creator.model.CharacterSession;
import com.dnd.creator.model.Race;
import com.dnd.creator.presenter.MainPresenter;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.Tooltip;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.TilePane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class CreateCharacterView {

    // ── Focus categories ──────────────────────────────────────────────────────
    private enum Focus {
        FIGHTER  ("Kämpfer",    "Stärke & Zähigkeit", "role-dps"),
        AGILE    ("Geschick",   "Schnelligkeit & DEX", "role-stealth"),
        MAGIC    ("Magie",      "INT & CHA Magie",     "role-caster"),
        VERSATILE("Vielseitig", "Passt zu allem",      "role-support");

        final String label, tooltip, cssClass;
        Focus(String label, String tooltip, String cssClass) {
            this.label = label; this.tooltip = tooltip; this.cssClass = cssClass;
        }
    }

    // ── Static data ───────────────────────────────────────────────────────────

    /** Image file key — override only where raceName.toLowerCase() isn't the filename. */
    private static final Map<String, String> RACE_IMAGE_FILE = new HashMap<>() {{
        put("Half-Orc",         "orc");
        // subraces → parent image
        put("Hill Dwarf",       "dwarf");
        put("Mountain Dwarf",   "dwarf");
        put("High Elf",         "elf");
        put("Wood Elf",         "elf");
        put("Drow",             "elf");
        put("Lightfoot Halfling","halfling");
        put("Stout Halfling",   "halfling");
        put("Forest Gnome",     "gnome");
        put("Rock Gnome",       "gnome");
    }};

    private static final Map<String, String> RACE_EMOJI_FALLBACK = new HashMap<>() {{
        put("Dwarf",             "⛏"); put("Hill Dwarf",        "⛏");
        put("Mountain Dwarf",   "⛏"); put("Elf",               "🏹");
        put("High Elf",         "🏹"); put("Wood Elf",          "🏹");
        put("Drow",             "🌑"); put("Halfling",          "🍃");
        put("Lightfoot Halfling","🍃");put("Stout Halfling",    "🍃");
        put("Human",            "🛡"); put("Dragonborn",        "🐉");
        put("Gnome",            "🔮"); put("Forest Gnome",      "🔮");
        put("Rock Gnome",       "🔮"); put("Half-Elf",          "✨");
        put("Half-Orc",         "⚔"); put("Tiefling",          "😈");
    }};

    private static final Map<String, String> RACE_TAGLINES = new LinkedHashMap<>() {{
        put("Dwarf",             "Zäh und ausdauernd. Schwer zu Boden zu bekommen.");
        put("Hill Dwarf",        "Weiser Zwerg. Viele Lebenspunkte, starker Wille.");
        put("Mountain Dwarf",    "Krieger-Zwerg. Rüstungsprofi und Frontkämpfer.");
        put("Elf",               "Schnell, präzise und scharfsinnig.");
        put("High Elf",          "Magisch begabt. Kennt einen Zauberspruch von Geburt.");
        put("Wood Elf",          "Waldläufer. Schnell, leise, naturverbunden.");
        put("Drow",              "Dunkelelf aus den Tiefen. Magie und Heimlichkeit.");
        put("Halfling",          "Klein, flink und ungewöhnlich glücklich.");
        put("Lightfoot Halfling","Besonders leise und charmant. Meister des Versteckens.");
        put("Stout Halfling",    "Zäher Halbling. Widerstandsfähig gegen Gift.");
        put("Human",             "Wandelbar und vielseitig — passt zu allem.");
        put("Dragonborn",        "Drachenblut. Mächtig im Nahkampf.");
        put("Gnome",             "Klug und neugierig — Magie im Blut.");
        put("Forest Gnome",      "Naturverbundener Gnom. Spricht mit kleinen Tieren.");
        put("Rock Gnome",        "Erfinder-Gnom. Technik und Magie vereint.");
        put("Half-Elf",          "Charmant und vielseitig. Gut mit Menschen.");
        put("Half-Orc",          "Wild und unbändig. Lebt von purer Stärke.");
        put("Tiefling",          "Höllische Abstammung. Geheimnisvoll und magisch.");
    }};

    private static final Map<String, Focus> RACE_FOCUS = new HashMap<>() {{
        put("Dwarf",             Focus.FIGHTER);
        put("Hill Dwarf",        Focus.FIGHTER);
        put("Mountain Dwarf",    Focus.FIGHTER);
        put("Elf",               Focus.AGILE);
        put("High Elf",          Focus.MAGIC);
        put("Wood Elf",          Focus.AGILE);
        put("Drow",              Focus.MAGIC);
        put("Halfling",          Focus.AGILE);
        put("Lightfoot Halfling",Focus.AGILE);
        put("Stout Halfling",    Focus.AGILE);
        put("Human",             Focus.VERSATILE);
        put("Dragonborn",        Focus.FIGHTER);
        put("Gnome",             Focus.MAGIC);
        put("Forest Gnome",      Focus.MAGIC);
        put("Rock Gnome",        Focus.MAGIC);
        put("Half-Elf",          Focus.VERSATILE);
        put("Half-Orc",          Focus.FIGHTER);
        put("Tiefling",          Focus.MAGIC);
    }};

    private static final Map<String, Set<String>> RACE_TAGS = new HashMap<>() {{
        put("Dwarf",             Set.of("Kämpfer"));
        put("Hill Dwarf",        Set.of("Kämpfer"));
        put("Mountain Dwarf",    Set.of("Kämpfer"));
        put("Elf",               Set.of("Geschick", "Magie"));
        put("High Elf",          Set.of("Geschick", "Magie"));
        put("Wood Elf",          Set.of("Geschick"));
        put("Drow",              Set.of("Geschick", "Magie"));
        put("Halfling",          Set.of("Geschick"));
        put("Lightfoot Halfling",Set.of("Geschick"));
        put("Stout Halfling",    Set.of("Geschick"));
        put("Human",             Set.of("Vielseitig"));
        put("Dragonborn",        Set.of("Kämpfer"));
        put("Gnome",             Set.of("Magie"));
        put("Forest Gnome",      Set.of("Magie"));
        put("Rock Gnome",        Set.of("Magie"));
        put("Half-Elf",          Set.of("Vielseitig", "Magie"));
        put("Half-Orc",          Set.of("Kämpfer"));
        put("Tiefling",          Set.of("Magie"));
    }};

    @FXML private HBox stepperContainer;
    @FXML private VBox previewContainer;
    @FXML private HBox filterBar;
    @FXML private TextField txtCharacterName;
    @FXML private TilePane raceGrid;
    @FXML private Button btnBack;
    @FXML private Button btnNext;

    private Parent root;
    private final DbManager dbManager = new DbManager();
    private final Map<String, VBox> raceTiles = new LinkedHashMap<>();
    private final CharacterPreviewPanel preview = new CharacterPreviewPanel();
    private final List<Button> filterButtons = new ArrayList<>();
    private String selectedRaceName = null;
    private String activeFilter = "Alle";

    public CreateCharacterView() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/dnd/creator/view/CreateCharacterView.fxml"));
            loader.setController(this);
            root = loader.load();
            CreationStyles.attach(root);

            dbManager.connect();

            stepperContainer.getChildren().setAll(new StepperBar(1).getRoot());
            previewContainer.getChildren().setAll(preview.getRoot());

            buildRaceGrid();
            buildFilterBar();
            restoreSession();
            wireNameField();
            wireNavigation();
            updateNextButton();

        } catch (IOException e) {
            throw new RuntimeException("Failed to load CreateCharacterView.fxml", e);
        }
    }

    public Parent getRoot() { return root; }

    // ── Race image helper ─────────────────────────────────────────────────────

    private Node raceIcon(String raceName) {
        String fileKey = RACE_IMAGE_FILE.getOrDefault(
            raceName, raceName.toLowerCase().replace(" ", "-").replace("'", ""));
        InputStream is = getClass().getResourceAsStream("/com/dnd/creator/pics/" + fileKey + ".png");
        if (is != null) {
            ImageView iv = new ImageView(new Image(is));
            iv.setFitWidth(56);
            iv.setFitHeight(56);
            iv.setPreserveRatio(true);
            return iv;
        }
        Label l = new Label(RACE_EMOJI_FALLBACK.getOrDefault(raceName, "⚔"));
        l.getStyleClass().add("card-icon");
        return l;
    }

    // ── Race grid ─────────────────────────────────────────────────────────────

    private void buildRaceGrid() {
        for (String raceName : dbManager.getAllRaces()) {
            Race race = dbManager.getRaceByName(raceName);
            VBox tile = buildRaceTile(raceName, race);
            raceTiles.put(raceName, tile);
            raceGrid.getChildren().add(tile);
        }
    }

    private VBox buildRaceTile(String raceName, Race race) {
        VBox tile = new VBox(5);
        tile.getStyleClass().add("selection-card");
        tile.setAlignment(Pos.TOP_CENTER);

        Node icon = raceIcon(raceName);

        // Name + focus tag
        HBox nameRow = new HBox(6);
        nameRow.setAlignment(Pos.CENTER);

        Label name = new Label(raceName);
        name.getStyleClass().add("card-title");
        name.setWrapText(true);
        name.setMaxWidth(130);
        name.setStyle("-fx-text-alignment: center; -fx-font-size: 14px;");

        Focus focus = RACE_FOCUS.getOrDefault(raceName, Focus.VERSATILE);
        Label focusTag = new Label(focus.label);
        focusTag.getStyleClass().addAll("role-tag", focus.cssClass);
        Tooltip.install(focusTag, new Tooltip(focus.tooltip));

        nameRow.getChildren().addAll(name, focusTag);

        // Tagline
        Label tagline = new Label(RACE_TAGLINES.getOrDefault(raceName, ""));
        tagline.getStyleClass().add("card-tagline");
        tagline.setWrapText(true);
        tagline.setMaxWidth(210);
        tagline.setAlignment(Pos.CENTER);
        tagline.setStyle("-fx-text-alignment: center;");

        // Ability bonus chips — sorted highest first
        HBox chips = new HBox(4);
        chips.setAlignment(Pos.CENTER);
        if (race != null) {
            race.getAbilityBonuses().entrySet().stream()
                .sorted(Map.Entry.<String, Integer>comparingByValue().reversed())
                .forEach(e -> {
                    Label chip = new Label("+" + e.getValue() + " " + e.getKey());
                    chip.getStyleClass().add("ability-chip");
                    chips.getChildren().add(chip);
                });
        }

        // Tooltip with traits
        if (race != null) {
            StringBuilder tip = new StringBuilder();
            tip.append("Geschwindigkeit: ").append(race.getSpeed()).append(" ft.\n");
            if (race.getSize() != null) tip.append("Größe: ").append(race.getSize()).append("\n");
            if (!race.getTraits().isEmpty()) {
                tip.append("\nMerkmale:\n");
                int count = 0;
                for (Race.Trait t : race.getTraits()) {
                    if (count++ >= 4) break;
                    tip.append("• ").append(t.getName()).append("\n");
                }
            }
            Tooltip tooltip = new Tooltip(tip.toString().trim());
            tooltip.setShowDelay(Duration.millis(300));
            Tooltip.install(tile, tooltip);
        }

        tile.getChildren().addAll(icon, nameRow, tagline, chips);
        tile.setOnMouseClicked(e -> selectRace(raceName, race));
        return tile;
    }

    // ── Filter bar ────────────────────────────────────────────────────────────

    private void buildFilterBar() {
        String[] filters = {"Alle", "Kämpfer", "Geschick", "Magie", "Vielseitig"};
        for (String f : filters) {
            Button btn = new Button(f);
            btn.getStyleClass().add("filter-chip");
            if (f.equals(activeFilter)) btn.getStyleClass().add("filter-chip-active");
            btn.setOnAction(e -> {
                activeFilter = f;
                refreshFilterStyles();
                applyFilter();
            });
            filterButtons.add(btn);
            filterBar.getChildren().add(btn);
        }
    }

    private void refreshFilterStyles() {
        for (Button b : filterButtons) {
            b.getStyleClass().remove("filter-chip-active");
            if (b.getText().equals(activeFilter)) b.getStyleClass().add("filter-chip-active");
        }
    }

    private void applyFilter() {
        raceGrid.getChildren().clear();
        for (Map.Entry<String, VBox> entry : raceTiles.entrySet()) {
            boolean match = "Alle".equals(activeFilter)
                || RACE_TAGS.getOrDefault(entry.getKey(), Set.of()).contains(activeFilter);
            if (match) raceGrid.getChildren().add(entry.getValue());
        }
    }

    // ── Selection & session ───────────────────────────────────────────────────

    private void selectRace(String raceName, Race race) {
        selectedRaceName = raceName;
        for (Map.Entry<String, VBox> entry : raceTiles.entrySet()) {
            VBox tile = entry.getValue();
            tile.getStyleClass().remove("selection-card-selected");
            if (entry.getKey().equals(raceName)) tile.getStyleClass().add("selection-card-selected");
        }
        if (race != null) CharacterSession.getInstance().getCurrentCharacter().setRace(race);
        preview.refresh();
        updateNextButton();
    }

    private void wireNameField() {
        txtCharacterName.textProperty().addListener((obs, oldV, newV) -> {
            String n = newV != null ? newV.trim() : "";
            if (!n.isEmpty()) CharacterSession.getInstance().getCurrentCharacter().setName(n);
            preview.refresh();
            updateNextButton();
        });
    }

    private void restoreSession() {
        var character = CharacterSession.getInstance().getCurrentCharacter();
        String savedName = character.getName();
        if (savedName != null && !savedName.isBlank() && !"New Character".equals(savedName))
            txtCharacterName.setText(savedName);
        Race savedRace = character.getRace();
        if (savedRace != null && savedRace.getName() != null)
            selectRace(savedRace.getName(), savedRace);
    }

    private void updateNextButton() {
        boolean nameOk = txtCharacterName.getText() != null && !txtCharacterName.getText().trim().isEmpty();
        boolean raceOk = selectedRaceName != null;
        boolean valid  = nameOk && raceOk;
        btnNext.setDisable(!valid);
        if (!valid) {
            String reason = !nameOk ? "Bitte gib einen Charakternamen ein." : "Bitte wähle eine Rasse.";
            Tooltip.install(btnNext, new Tooltip(reason));
        } else {
            Tooltip.uninstall(btnNext, btnNext.getTooltip());
        }
    }

    private void wireNavigation() {
        btnBack.setOnAction(e -> {
            MainView mainView = new MainView();
            Stage stage = (Stage) root.getScene().getWindow();
            new MainPresenter(mainView, stage);
            stage.setScene(new Scene(mainView.getRoot(), stage.getScene().getWidth(), stage.getScene().getHeight()));
        });
        btnNext.setOnAction(e -> {
            CharacterSession.getInstance().getCurrentCharacter().setName(txtCharacterName.getText().trim());
            SelectClassView next = new SelectClassView();
            Stage stage = (Stage) root.getScene().getWindow();
            stage.setScene(new Scene(next.getRoot(), stage.getScene().getWidth(), stage.getScene().getHeight()));
        });
    }
}
