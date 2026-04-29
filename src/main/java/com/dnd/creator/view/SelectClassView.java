package com.dnd.creator.view;

import com.dnd.creator.data.DbManager;
import com.dnd.creator.model.CharacterSession;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Tooltip;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.TilePane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.InputStream;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class SelectClassView {

    private enum Role {
        TANK("Tank", "role-tank"),
        DPS("Schaden", "role-dps"),
        CASTER("Magie", "role-caster"),
        STEALTH("Heimlich", "role-stealth"),
        SUPPORT("Unterstützung", "role-support");

        final String label;
        final String cssClass;

        Role(String label, String cssClass) {
            this.label = label;
            this.cssClass = cssClass;
        }
    }

    private static final class ClassDef {
        final String name;
        final String icon;
        final Role role;
        final int difficulty; // 1-3
        final String tagline;
        final Set<String> tags;

        ClassDef(String name, String icon, Role role, int difficulty, String tagline, Set<String> tags) {
            this.name = name;
            this.icon = icon;
            this.role = role;
            this.difficulty = difficulty;
            this.tagline = tagline;
            this.tags = tags;
        }
    }

    private static final String TAG_BEGINNER = "Anfängerfreundlich";
    private static final String TAG_MAGIC = "Magie";
    private static final String TAG_MELEE = "Nahkampf";
    private static final String TAG_RANGED = "Fernkampf";

    private static final List<ClassDef> CLASSES = List.of(
        new ClassDef("Barbarian", "🪓", Role.DPS,    1, "Wilder Krieger. Schlägt hart, hält viel aus.",        Set.of(TAG_MELEE, TAG_BEGINNER)),
        new ClassDef("Bard",      "🎵", Role.SUPPORT,2, "Inspiriert Verbündete mit Musik. Vielseitig.",        Set.of(TAG_MAGIC, TAG_RANGED)),
        new ClassDef("Cleric",    "✝",  Role.SUPPORT,2, "Heiliger Heiler. Schild und göttliche Magie.",        Set.of(TAG_MAGIC, TAG_BEGINNER)),
        new ClassDef("Druid",     "🌿", Role.CASTER, 3, "Naturmagier. Verwandelt sich in Tiere.",              Set.of(TAG_MAGIC)),
        new ClassDef("Fighter",   "⚔",  Role.DPS,    1, "Verlässlicher Kämpfer. Einfach zu spielen.",          Set.of(TAG_MELEE, TAG_RANGED, TAG_BEGINNER)),
        new ClassDef("Monk",      "👊", Role.DPS,    3, "Mönch der Faust. Schnell und beweglich.",             Set.of(TAG_MELEE)),
        new ClassDef("Paladin",   "🛡", Role.TANK,   2, "Heiliger Ritter. Schwert, Schild und Glaube.",        Set.of(TAG_MELEE, TAG_MAGIC)),
        new ClassDef("Ranger",    "🏹", Role.DPS,    2, "Spurenleser und Bogenschütze.",                       Set.of(TAG_RANGED, TAG_MAGIC)),
        new ClassDef("Rogue",     "🗡", Role.STEALTH,2, "Hinterhältig und schnell. Trifft, wo es weh tut.",    Set.of(TAG_MELEE, TAG_RANGED)),
        new ClassDef("Sorcerer",  "✨", Role.CASTER, 2, "Geborener Magier. Kraft aus den Adern.",              Set.of(TAG_MAGIC)),
        new ClassDef("Warlock",   "👁", Role.CASTER, 2, "Pakt mit dunklen Mächten.",                           Set.of(TAG_MAGIC)),
        new ClassDef("Wizard",    "🔮", Role.CASTER, 3, "Studierter Zauberer. Wissen ist Macht.",              Set.of(TAG_MAGIC))
    );

    @FXML private HBox stepperContainer;
    @FXML private VBox previewContainer;
    @FXML private HBox filterBar;
    @FXML private TilePane classGrid;
    @FXML private Button btnBack;
    @FXML private Button btnNext;

    private Parent root;
    private final DbManager dbManager = new DbManager();
    private final Map<String, VBox> classCards = new HashMap<>();
    private final CharacterPreviewPanel preview = new CharacterPreviewPanel();
    private String selectedClass = null;
    private String activeFilter = "Alle";
    private final List<Button> filterButtons = new ArrayList<>();

    public SelectClassView() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/dnd/creator/view/SelectClassView.fxml"));
            loader.setController(this);
            root = loader.load();
            CreationStyles.attach(root);

            dbManager.connect();

            stepperContainer.getChildren().setAll(new StepperBar(2).getRoot());
            previewContainer.getChildren().setAll(preview.getRoot());

            buildFilterBar();
            buildClassCards();
            restoreSession();
            wireNavigation();
            updateNextButton();

        } catch (IOException e) {
            throw new RuntimeException("Failed to load SelectClassView.fxml", e);
        }
    }

    public Parent getRoot() {
        return root;
    }

    private void buildFilterBar() {
        String[] filters = {"Alle", TAG_BEGINNER, TAG_MAGIC, TAG_MELEE, TAG_RANGED};
        for (String f : filters) {
            Button b = new Button(f);
            b.getStyleClass().add("filter-chip");
            if (f.equals(activeFilter)) b.getStyleClass().add("filter-chip-active");
            b.setOnAction(e -> {
                activeFilter = f;
                refreshFilterButtonStyles();
                applyFilter();
            });
            filterButtons.add(b);
            filterBar.getChildren().add(b);
        }
    }

    private void refreshFilterButtonStyles() {
        for (Button b : filterButtons) {
            b.getStyleClass().remove("filter-chip-active");
            if (b.getText().equals(activeFilter)) {
                b.getStyleClass().add("filter-chip-active");
            }
        }
    }

    private void buildClassCards() {
        for (ClassDef def : CLASSES) {
            VBox card = buildClassCard(def);
            classCards.put(def.name, card);
            classGrid.getChildren().add(card);
        }
    }

    private void applyFilter() {
        classGrid.getChildren().clear();
        for (ClassDef def : CLASSES) {
            boolean matches = "Alle".equals(activeFilter) || def.tags.contains(activeFilter);
            if (matches) {
                classGrid.getChildren().add(classCards.get(def.name));
            }
        }
    }

    private VBox buildClassCard(ClassDef def) {
        VBox card = new VBox(4);
        card.getStyleClass().add("selection-card");
        card.setAlignment(Pos.TOP_CENTER);

        HBox topRow = new HBox();
        topRow.setAlignment(Pos.CENTER_LEFT);
        Node icon = classIcon(def.name, def.icon);
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        Label stars = new Label(stars(def.difficulty));
        stars.getStyleClass().add("difficulty-stars");
        Tooltip.install(stars, new Tooltip(difficultyTip(def.difficulty)));
        topRow.getChildren().addAll(icon, spacer, stars);

        Label name = new Label(def.name);
        name.getStyleClass().add("card-title");

        Label roleTag = new Label(def.role.label);
        roleTag.getStyleClass().addAll("role-tag", def.role.cssClass);

        Label tagline = new Label(def.tagline);
        tagline.getStyleClass().add("card-tagline");
        tagline.setWrapText(true);
        tagline.setMaxWidth(200);

        // Tooltip with class details from DB
        Map<String, Object> classData = dbManager.getClassByName(def.name);
        if (classData != null) {
            StringBuilder tip = new StringBuilder();
            tip.append("Hit Die: d").append(classData.get("hit_die")).append(" (Lebenswürfel)\n");
            Object primary = classData.get("primary_ability");
            if (primary != null) tip.append("Hauptwert: ").append(primary).append("\n");
            Object spellAbility = classData.get("spellcasting_ability");
            if (spellAbility != null) tip.append("Magiewert: ").append(spellAbility).append("\n");
            Tooltip tooltip = new Tooltip(tip.toString().trim());
            tooltip.setShowDelay(Duration.millis(300));
            Tooltip.install(card, tooltip);
        }

        card.getChildren().addAll(topRow, name, roleTag, tagline);
        card.setOnMouseClicked(e -> selectClass(def));
        return card;
    }

    private String stars(int difficulty) {
        return "★".repeat(difficulty) + "☆".repeat(3 - difficulty);
    }

    private String difficultyTip(int difficulty) {
        return switch (difficulty) {
            case 1 -> "Einfach — gut für Einsteiger";
            case 2 -> "Mittel — etwas Erfahrung hilft";
            default -> "Komplex — viele Regeln zu lernen";
        };
    }

    private void selectClass(ClassDef def) {
        selectedClass = def.name;
        for (Map.Entry<String, VBox> entry : classCards.entrySet()) {
            VBox c = entry.getValue();
            c.getStyleClass().remove("selection-card-selected");
            if (entry.getKey().equals(def.name)) {
                c.getStyleClass().add("selection-card-selected");
            }
        }

        Map<String, Object> classData = dbManager.getClassByName(def.name);
        var character = CharacterSession.getInstance().getCurrentCharacter();
        character.setCharacterClass(def.name);
        if (classData != null) {
            character.setClassIndex((String) classData.get("index"));
            Object hitDie = classData.get("hit_die");
            if (hitDie instanceof Integer i) character.setClassHitDie(i);
            character.setSpellcastingAbility((String) classData.get("spellcasting_ability"));
            @SuppressWarnings("unchecked")
            List<String> profs = (List<String>) classData.get("proficiencies");
            if (profs != null) character.setClassProficiencies(profs);
        }
        // Reset equipment selection — different class, different gear
        character.setSelectedEquipment(new ArrayList<>());

        preview.refresh();
        updateNextButton();
    }

    private void restoreSession() {
        String saved = CharacterSession.getInstance().getCurrentCharacter().getCharacterClass();
        if (saved != null && !saved.isBlank()) {
            for (ClassDef def : CLASSES) {
                if (def.name.equals(saved)) {
                    selectClass(def);
                    break;
                }
            }
        }
    }

    private void updateNextButton() {
        boolean valid = selectedClass != null;
        btnNext.setDisable(!valid);
        if (!valid) {
            Tooltip.install(btnNext, new Tooltip("Bitte wähle eine Klasse."));
        } else {
            Tooltip.uninstall(btnNext, btnNext.getTooltip());
        }
    }

    private static Node classIcon(String className, String fallbackEmoji) {
        String path = "/com/dnd/creator/pics/" + className.toLowerCase() + ".png";
        InputStream is = SelectClassView.class.getResourceAsStream(path);
        if (is != null) {
            ImageView iv = new ImageView(new Image(is));
            iv.setFitWidth(48);
            iv.setFitHeight(48);
            iv.setPreserveRatio(true);
            return iv;
        }
        Label l = new Label(fallbackEmoji);
        l.getStyleClass().add("card-icon");
        l.setStyle("-fx-font-size: 28px;");
        return l;
    }

    private void wireNavigation() {
        btnBack.setOnAction(e -> {
            CreateCharacterView prev = new CreateCharacterView();
            Stage stage = (Stage) root.getScene().getWindow();
            stage.setScene(new Scene(prev.getRoot(), stage.getScene().getWidth(), stage.getScene().getHeight()));
        });
        btnNext.setOnAction(e -> {
            AbilityScoresView next = new AbilityScoresView();
            Stage stage = (Stage) root.getScene().getWindow();
            stage.setScene(new Scene(next.getRoot(), stage.getScene().getWidth(), stage.getScene().getHeight()));
        });
    }
}
