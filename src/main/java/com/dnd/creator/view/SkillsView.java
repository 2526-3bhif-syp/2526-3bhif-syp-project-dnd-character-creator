package com.dnd.creator.view;

import com.dnd.creator.data.DbManager;
import com.dnd.creator.model.CharacterSession;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class SkillsView {

    private static final Map<String, String> SKILL_ABILITY = new LinkedHashMap<>() {{
        put("Athletics",      "STR");
        put("Acrobatics",     "DEX");
        put("Sleight of Hand","DEX");
        put("Stealth",        "DEX");
        put("Arcana",         "INT");
        put("History",        "INT");
        put("Investigation",  "INT");
        put("Nature",         "INT");
        put("Religion",       "INT");
        put("Animal Handling","WIS");
        put("Insight",        "WIS");
        put("Medicine",       "WIS");
        put("Perception",     "WIS");
        put("Survival",       "WIS");
        put("Deception",      "CHA");
        put("Intimidation",   "CHA");
        put("Performance",    "CHA");
        put("Persuasion",     "CHA");
    }};

    private static final Map<String, String> ABILITY_GROUP_LABEL = Map.of(
        "STR", "Stärke",
        "DEX", "Geschicklichkeit",
        "INT", "Intelligenz",
        "WIS", "Weisheit",
        "CHA", "Charisma"
    );

    private static final Map<String, List<String>> BACKGROUND_CLASS_FIT = new LinkedHashMap<>() {{
        put("Soldier",       List.of("Fighter", "Paladin", "Barbarian"));
        put("Sage",          List.of("Wizard", "Cleric", "Druid"));
        put("Criminal",      List.of("Rogue", "Warlock"));
        put("Acolyte",       List.of("Cleric", "Paladin", "Druid"));
        put("Outlander",     List.of("Barbarian", "Ranger", "Druid"));
        put("Sailor",        List.of("Fighter", "Rogue", "Ranger"));
        put("Noble",         List.of("Paladin", "Bard", "Warlock"));
        put("Entertainer",   List.of("Bard", "Rogue"));
        put("Hermit",        List.of("Druid", "Monk", "Cleric"));
        put("Guild Artisan", List.of("Rogue", "Wizard", "Bard"));
        put("Folk Hero",     List.of("Fighter", "Barbarian", "Paladin"));
        put("Charlatan",     List.of("Bard", "Rogue", "Sorcerer"));
    }};

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
    @FXML private HBox statsReminderBar;
    @FXML private Label lblSkillsCounter;
    @FXML private Label lblSkillsHint;
    @FXML private VBox proficiencyBox;
    @FXML private FlowPane proficiencyPane;
    @FXML private Button btnBack;
    @FXML private Button btnNext;

    private Parent root;
    private final DbManager dbManager = new DbManager();
    private final CharacterPreviewPanel preview = new CharacterPreviewPanel();
    private final Map<String, VBox> backgroundCards = new LinkedHashMap<>();
    private final List<CheckBox> skillCheckboxes = new ArrayList<>();
    private final VBox backgroundSkillsSection = new VBox(6);
    private List<String> backgroundSkills = new ArrayList<>();
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
            buildStatsReminderBar();
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

    // ── Backgrounds ───────────────────────────────────────────────────────────

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

        String currentClass = CharacterSession.getInstance().getCurrentCharacter().getCharacterClass();
        List<String> fits = BACKGROUND_CLASS_FIT.getOrDefault(name, List.of());

        HBox headerRow = new HBox(8);
        headerRow.setAlignment(Pos.CENTER_LEFT);
        headerRow.getChildren().add(nameLabel);
        if (currentClass != null && fits.contains(currentClass)) {
            Label badge = new Label("★ Empfohlen");
            badge.getStyleClass().add("recommended-badge");
            headerRow.getChildren().add(badge);
        }

        // Show which skills this background grants
        List<String> bgSkills = dbManager.getBackgroundSkills(name);
        card.getChildren().addAll(headerRow, tagline);
        if (!bgSkills.isEmpty()) {
            HBox chips = new HBox(6);
            chips.setAlignment(Pos.CENTER_LEFT);
            for (String skill : bgSkills) {
                Label chip = new Label("✓ " + skill);
                chip.getStyleClass().add("skill-auto-chip");
                chips.getChildren().add(chip);
            }
            card.getChildren().add(chips);
        }

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

        backgroundSkills = new ArrayList<>(dbManager.getBackgroundSkills(name));
        refreshBackgroundSection();

        // Re-evaluate every class-skill checkbox: disable those covered by the background
        for (CheckBox cb : skillCheckboxes) {
            if (backgroundSkills.contains(cb.getText())) {
                cb.setSelected(false);
                cb.setDisable(true);
                Tooltip.install(cb, new Tooltip(cb.getText() + " — bereits vom Hintergrund erteilt"));
            } else {
                cb.setDisable(false);
                // tooltip is already set at build time
            }
        }
        enforceSkillLimit();
        saveSkills();
        updateCounter(); // also calls updateProficiencyDisplay
        preview.refresh();
        updateNextButton();
    }

    private void refreshBackgroundSection() {
        skillsContainer.getChildren().remove(backgroundSkillsSection);
        backgroundSkillsSection.getChildren().clear();

        if (backgroundSkills.isEmpty()) return;

        Label title = new Label("✓ Vom Hintergrund — automatisch erteilt:");
        title.getStyleClass().add("skill-group-title");
        title.setStyle("-fx-text-fill: #2E8B57;");

        FlowPane chips = new FlowPane(8, 4);
        for (String skill : backgroundSkills) {
            Label chip = new Label("✓ " + skill);
            chip.getStyleClass().add("skill-auto-chip");
            chips.getChildren().add(chip);
        }

        backgroundSkillsSection.getStyleClass().setAll("skill-group");
        backgroundSkillsSection.setStyle("-fx-border-color: #2E8B57;");
        backgroundSkillsSection.getChildren().addAll(title, chips);
        skillsContainer.getChildren().add(0, backgroundSkillsSection);
    }

    // ── Class skills ──────────────────────────────────────────────────────────

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

        lblSkillsHint.setText("Wähle " + maxSkills + " zusätzliche Fähigkeiten deiner Klasse.");

        Map<String, List<String>> grouped = new LinkedHashMap<>();
        for (String skill : options) {
            grouped.computeIfAbsent(SKILL_ABILITY.getOrDefault(skill, "INT"), k -> new ArrayList<>()).add(skill);
        }

        String className = character.getCharacterClass();
        Set<String> primaryStats = AbilityScoresView.getPrimaryStats(className);

        String[] order = {"STR", "DEX", "INT", "WIS", "CHA"};
        for (String ability : order) {
            List<String> skills = grouped.get(ability);
            if (skills == null || skills.isEmpty()) continue;
            skillsContainer.getChildren().add(buildSkillGroup(ability, skills, primaryStats));
        }
    }

    private VBox buildSkillGroup(String ability, List<String> skills, Set<String> primaryStats) {
        VBox box = new VBox(6);
        box.getStyleClass().add("skill-group");

        boolean isPrimary = primaryStats.contains(ability);
        Label title = new Label(ABILITY_GROUP_LABEL.getOrDefault(ability, ability) + " (" + ability + ")");
        title.getStyleClass().add("skill-group-title");
        if (isPrimary) title.getStyleClass().add("skill-group-title-primary");

        VBox checks = new VBox(4);
        for (String skill : skills) {
            boolean fromBg = backgroundSkills.contains(skill);

            CheckBox cb = new CheckBox(skill);
            cb.setStyle("-fx-text-fill: #1A1A1A; -fx-font-size: 13px;"); // ← NEU

            if (isPrimary && !fromBg) cb.getStyleClass().add("skill-primary");
            if (fromBg) {
                cb.setDisable(true);
                Tooltip.install(cb, new Tooltip(skill + " — bereits vom Hintergrund erteilt"));
            } else {
                Tooltip.install(cb, makeSkillTooltip(skill, ability));
            }
            cb.setOnAction(e -> {
                enforceSkillLimit();
                saveSkills();
                updateCounter();
                updateNextButton();
            });
            skillCheckboxes.add(cb);

            HBox row = new HBox(6);
            row.setAlignment(Pos.CENTER_LEFT);
            row.getChildren().add(cb);
            if (isPrimary && !fromBg) {
                Label star = new Label("★");
                star.getStyleClass().add("skill-primary-star");
                row.getChildren().add(star);
            }
            if (fromBg) {
                Label tag = new Label("HG");
                tag.getStyleClass().add("skill-from-bg-tag");
                row.getChildren().add(tag);
            }
            checks.getChildren().add(row);
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
        boolean atLimit = selected >= maxSkills;
        for (CheckBox cb : skillCheckboxes) {
            if (backgroundSkills.contains(cb.getText())) {
                cb.setDisable(true);
            } else if (!cb.isSelected()) {
                cb.setDisable(atLimit);
            }
        }
    }

    private void saveSkills() {
        List<String> chosen = skillCheckboxes.stream()
            .filter(CheckBox::isSelected)
            .map(CheckBox::getText)
            .toList();
        List<String> all = new ArrayList<>(backgroundSkills);
        all.addAll(chosen);
        CharacterSession.getInstance().getCurrentCharacter().setSelectedSkills(all);
    }

    private void updateCounter() {
        long selected = skillCheckboxes.stream().filter(CheckBox::isSelected).count();
        lblSkillsCounter.setText(selected + " / " + maxSkills + " gewählt");
        updateProficiencyDisplay();
    }

    private void updateProficiencyDisplay() {
        proficiencyPane.getChildren().clear();

        // Background skills (green chips)
        for (String skill : backgroundSkills) {
            Label chip = new Label("✓ " + skill);
            chip.getStyleClass().add("skill-auto-chip");
            proficiencyPane.getChildren().add(chip);
        }

        // Class-chosen skills (dark red chips)
        skillCheckboxes.stream()
            .filter(CheckBox::isSelected)
            .map(CheckBox::getText)
            .forEach(skill -> {
                Label chip = new Label("✓ " + skill);
                chip.getStyleClass().add("skill-chosen-chip");
                proficiencyPane.getChildren().add(chip);
            });

        // Show placeholder when nothing selected yet
        if (proficiencyPane.getChildren().isEmpty()) {
            Label hint = new Label("Noch keine Fähigkeiten ausgewählt");
            hint.getStyleClass().add("muted");
            proficiencyPane.getChildren().add(hint);
        }
    }

    // ── Session restore ───────────────────────────────────────────────────────

    private void restoreSession() {
        var character = CharacterSession.getInstance().getCurrentCharacter();

        String savedBg = character.getSelectedBackground();
        if (savedBg != null && backgroundCards.containsKey(savedBg)) {
            selectBackground(savedBg); // loads backgroundSkills + refreshes section
        }

        List<String> savedSkills = character.getSelectedSkills();
        if (savedSkills != null && !savedSkills.isEmpty()) {
            for (CheckBox cb : skillCheckboxes) {
                if (!backgroundSkills.contains(cb.getText())) {
                    cb.setSelected(savedSkills.contains(cb.getText()));
                }
            }
            enforceSkillLimit();
            updateCounter();
        }
    }

    private void updateNextButton() {
        long selected = skillCheckboxes.stream().filter(CheckBox::isSelected).count();
        boolean valid = selectedBackground != null && selected == maxSkills;
        btnNext.setDisable(!valid);
        if (!valid) {
            String reason;
            if (selectedBackground == null) reason = "Bitte wähle einen Hintergrund.";
            else if (selected < maxSkills)  reason = "Bitte wähle " + maxSkills + " Fähigkeiten.";
            else                            reason = "Du hast zu viele Fähigkeiten gewählt.";
            Tooltip.install(btnNext, new Tooltip(reason));
        } else {
            Tooltip.uninstall(btnNext, btnNext.getTooltip());
        }
    }

    // ── Stats reminder ────────────────────────────────────────────────────────

    private void buildStatsReminderBar() {
        var character = CharacterSession.getInstance().getCurrentCharacter();
        int[] vals = {
            character.getStrength(), character.getDexterity(), character.getConstitution(),
            character.getIntelligence(), character.getWisdom(), character.getCharisma()
        };
        boolean anyAssigned = false;
        for (int v : vals) if (v != 0) { anyAssigned = true; break; }
        if (!anyAssigned) return;

        var race = character.getRace();
        String[] keys   = {"STR", "DEX", "CON", "INT", "WIS", "CHA"};
        String[] labels = {"STR", "GES", "KON", "INT", "WEI", "CHA"};

        for (int i = 0; i < keys.length; i++) {
            int base  = vals[i];
            int bonus = race != null ? race.getAbilityBonuses().getOrDefault(keys[i], 0) : 0;
            int total = base + bonus;

            VBox pill = new VBox(2);
            pill.getStyleClass().add("stat-reminder-pill");
            pill.setAlignment(Pos.CENTER);

            Label lblKey = new Label(labels[i]);
            lblKey.getStyleClass().add("stat-reminder-key");

            Label lblVal = new Label(base == 0 ? "—" : String.valueOf(total));
            lblVal.getStyleClass().add("stat-reminder-val");

            int mod = total == 0 ? 0 : Math.floorDiv(total - 10, 2);
            Label lblMod = new Label(base == 0 ? "" : (mod >= 0 ? "+" : "") + mod);
            lblMod.getStyleClass().add("stat-reminder-mod");

            pill.getChildren().addAll(lblKey, lblVal, lblMod);
            statsReminderBar.getChildren().add(pill);
        }
    }

    // ── Navigation ────────────────────────────────────────────────────────────

    private void wireNavigation() {
        btnBack.setOnAction(e -> {
            Stage stage = (Stage) root.getScene().getWindow();
            stage.getScene().setRoot(new AbilityScoresView().getRoot());
        });
        btnNext.setOnAction(e -> {
            saveSkills();
            Stage stage = (Stage) root.getScene().getWindow();
            stage.getScene().setRoot(new EquipmentView().getRoot());
        });
    }
}