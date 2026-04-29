package com.dnd.creator.view;

import com.dnd.creator.data.DbManager;
import com.dnd.creator.model.CharacterSession;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class SkillsView {

    /** Skill -> ability that governs it (D&D 5e). */
    private static final Map<String, String> SKILL_ABILITY = new LinkedHashMap<>() {{
        put("Athletics", "STR");
        put("Acrobatics", "DEX");
        put("Sleight of Hand", "DEX");
        put("Stealth", "DEX");
        put("Arcana", "INT");
        put("History", "INT");
        put("Investigation", "INT");
        put("Nature", "INT");
        put("Religion", "INT");
        put("Animal Handling", "WIS");
        put("Insight", "WIS");
        put("Medicine", "WIS");
        put("Perception", "WIS");
        put("Survival", "WIS");
        put("Deception", "CHA");
        put("Intimidation", "CHA");
        put("Performance", "CHA");
        put("Persuasion", "CHA");
    }};

    private static final Map<String, String> ABILITY_GROUP_LABEL = Map.of(
        "STR", "Stärke",
        "DEX", "Geschicklichkeit",
        "INT", "Intelligenz",
        "WIS", "Weisheit",
        "CHA", "Charisma"
    );

    /** Background -> short German tagline. */
    private static final Map<String, String> BACKGROUND_TAGLINES = new LinkedHashMap<>() {{
        put("Acolyte",       "Tempeldiener. Kennt Religion und heilige Stätten.");
        put("Charlatan",     "Schwindler. Täuscht Leute geschickt.");
        put("Criminal",      "Verbrecher. Kennt die Unterwelt.");
        put("Entertainer",   "Künstler. Lieder, Geschichten, Auftritte.");
        put("Folk Hero",     "Volksheld. Das einfache Volk mag dich.");
        put("Guild Artisan", "Zunfthandwerker. Geschickt mit Werkzeugen.");
        put("Hermit",        "Einsiedler. Lange in Abgeschiedenheit gelebt.");
        put("Noble",         "Adliger. Bildung, Manieren, Privilegien.");
        put("Outlander",     "Wildnisbewohner. Überlebt in der Wildnis.");
        put("Sage",          "Gelehrter. Tiefes Wissen über die Welt.");
        put("Sailor",        "Seemann. Schiffe, Knoten, Stürme.");
        put("Soldier",       "Soldat. Militärische Ausbildung und Taktik.");
    }};

    @FXML private HBox stepperContainer;
    @FXML private VBox previewContainer;
    @FXML private VBox backgroundContainer;
    @FXML private VBox skillsContainer;
    @FXML private Label lblSkillsCounter;
    @FXML private Label lblSkillsHint;
    @FXML private Button btnBack;
    @FXML private Button btnNext;

    private Parent root;
    private final DbManager dbManager = new DbManager();
    private final CharacterPreviewPanel preview = new CharacterPreviewPanel();
    private final Map<String, VBox> backgroundCards = new LinkedHashMap<>();
    private final List<CheckBox> skillCheckboxes = new ArrayList<>();
    private String selectedBackground = null;
    private int maxSkills = 2;

    public SkillsView() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/dnd/creator/view/SkillsView.fxml"));
            loader.setController(this);
            root = loader.load();
            CreationStyles.attach(root);

            dbManager.connect();

            stepperContainer.getChildren().setAll(new StepperBar(4).getRoot());
            previewContainer.getChildren().setAll(preview.getRoot());

            buildBackgrounds();
            buildSkills();
            restoreSession();
            updateCounter();
            wireNavigation();
            updateNextButton();

        } catch (IOException e) {
            throw new RuntimeException("Failed to load SkillsView.fxml", e);
        }
    }

    public Parent getRoot() {
        return root;
    }

    private void buildBackgrounds() {
        List<String> backgrounds = dbManager.getAllBackgrounds();
        if (backgrounds.isEmpty()) backgrounds = new ArrayList<>(BACKGROUND_TAGLINES.keySet());
        for (String bg : backgrounds) {
            VBox card = buildBackgroundCard(bg);
            backgroundCards.put(bg, card);
            backgroundContainer.getChildren().add(card);
        }
    }

    private VBox buildBackgroundCard(String name) {
        VBox card = new VBox(4);
        card.getStyleClass().add("selection-card");
        card.setAlignment(Pos.TOP_LEFT);

        Label nameLabel = new Label(name);
        nameLabel.getStyleClass().add("card-title");
        nameLabel.setStyle("-fx-font-size: 14px;");

        Label tagline = new Label(BACKGROUND_TAGLINES.getOrDefault(name, ""));
        tagline.getStyleClass().add("card-tagline");
        tagline.setWrapText(true);

        card.getChildren().addAll(nameLabel, tagline);
        card.setOnMouseClicked(e -> selectBackground(name));
        return card;
    }

    private void selectBackground(String name) {
        selectedBackground = name;
        for (Map.Entry<String, VBox> entry : backgroundCards.entrySet()) {
            VBox c = entry.getValue();
            c.getStyleClass().remove("selection-card-selected");
            if (entry.getKey().equals(name)) c.getStyleClass().add("selection-card-selected");
        }
        CharacterSession.getInstance().getCurrentCharacter().setSelectedBackground(name);
        preview.refresh();
        updateNextButton();
    }

    @SuppressWarnings("unchecked")
    private void buildSkills() {
        var character = CharacterSession.getInstance().getCurrentCharacter();
        String classIndex = character.getClassIndex();

        List<String> options;
        if (classIndex != null && !classIndex.isBlank()) {
            Map<String, Object> config = dbManager.getClassSkillSelectionConfig(classIndex);
            maxSkills = (int) config.getOrDefault("choose", 2);
            options = (List<String>) config.getOrDefault("options", dbManager.getAllSkills());
        } else {
            maxSkills = 2;
            options = dbManager.getAllSkills();
        }

        lblSkillsHint.setText("Wähle " + maxSkills + " Fähigkeiten, in denen dein Held besonders gut ist.");

        // Group by ability
        Map<String, List<String>> grouped = new LinkedHashMap<>();
        for (String skill : options) {
            String ability = SKILL_ABILITY.getOrDefault(skill, "INT");
            grouped.computeIfAbsent(ability, k -> new ArrayList<>()).add(skill);
        }

        // Render each group in a fixed ability order
        String[] order = {"STR", "DEX", "INT", "WIS", "CHA"};
        for (String ability : order) {
            List<String> skills = grouped.get(ability);
            if (skills == null || skills.isEmpty()) continue;
            skillsContainer.getChildren().add(buildSkillGroup(ability, skills));
        }
    }

    private VBox buildSkillGroup(String ability, List<String> skills) {
        VBox box = new VBox(6);
        box.getStyleClass().add("skill-group");

        Label title = new Label(ABILITY_GROUP_LABEL.getOrDefault(ability, ability) + " (" + ability + ")");
        title.getStyleClass().add("skill-group-title");

        VBox checks = new VBox(4);
        for (String skill : skills) {
            CheckBox cb = new CheckBox(skill);
            Tooltip.install(cb, makeSkillTooltip(skill, ability));
            cb.setOnAction(e -> {
                enforceSkillLimit();
                saveSkills();
                updateCounter();
                updateNextButton();
            });
            skillCheckboxes.add(cb);
            checks.getChildren().add(cb);
        }

        box.getChildren().addAll(title, checks);
        return box;
    }

    private Tooltip makeSkillTooltip(String skill, String ability) {
        Tooltip t = new Tooltip(skill + " — basiert auf "
            + ABILITY_GROUP_LABEL.getOrDefault(ability, ability) + " (" + ability + ")");
        t.setShowDelay(Duration.millis(300));
        return t;
    }

    private void enforceSkillLimit() {
        long selected = skillCheckboxes.stream().filter(CheckBox::isSelected).count();
        boolean limit = selected >= maxSkills;
        for (CheckBox cb : skillCheckboxes) {
            if (!cb.isSelected()) cb.setDisable(limit);
        }
    }

    private void saveSkills() {
        List<String> chosen = skillCheckboxes.stream()
            .filter(CheckBox::isSelected)
            .map(CheckBox::getText)
            .toList();
        CharacterSession.getInstance().getCurrentCharacter().setSelectedSkills(new ArrayList<>(chosen));
    }

    private void updateCounter() {
        long selected = skillCheckboxes.stream().filter(CheckBox::isSelected).count();
        lblSkillsCounter.setText(selected + " / " + maxSkills + " gewählt");
    }

    private void restoreSession() {
        var character = CharacterSession.getInstance().getCurrentCharacter();

        String savedBg = character.getSelectedBackground();
        if (savedBg != null && backgroundCards.containsKey(savedBg)) {
            selectBackground(savedBg);
        }

        List<String> savedSkills = character.getSelectedSkills();
        if (savedSkills != null && !savedSkills.isEmpty()) {
            for (CheckBox cb : skillCheckboxes) {
                cb.setSelected(savedSkills.contains(cb.getText()));
            }
            enforceSkillLimit();
        }
    }

    private void updateNextButton() {
        long selected = skillCheckboxes.stream().filter(CheckBox::isSelected).count();
        boolean valid = selectedBackground != null && selected == maxSkills;
        btnNext.setDisable(!valid);
        if (!valid) {
            String reason;
            if (selectedBackground == null) reason = "Bitte wähle einen Hintergrund.";
            else if (selected < maxSkills) reason = "Bitte wähle " + maxSkills + " Fähigkeiten.";
            else reason = "Du hast zu viele Fähigkeiten gewählt.";
            Tooltip.install(btnNext, new Tooltip(reason));
        } else {
            Tooltip.uninstall(btnNext, btnNext.getTooltip());
        }
    }

    private void wireNavigation() {
        btnBack.setOnAction(e -> {
            AbilityScoresView prev = new AbilityScoresView();
            Stage stage = (Stage) root.getScene().getWindow();
            stage.setScene(new Scene(prev.getRoot(), stage.getScene().getWidth(), stage.getScene().getHeight()));
        });
        btnNext.setOnAction(e -> {
            saveSkills();
            EquipmentView next = new EquipmentView();
            Stage stage = (Stage) root.getScene().getWindow();
            stage.setScene(new Scene(next.getRoot(), stage.getScene().getWidth(), stage.getScene().getHeight()));
        });
    }
}
