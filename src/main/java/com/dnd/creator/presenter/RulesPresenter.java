package com.dnd.creator.presenter;

import com.dnd.creator.data.DbManager;
import com.dnd.creator.model.Race;
import com.dnd.creator.view.RulesView;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.Separator;
import javafx.scene.control.TreeCell;
import javafx.scene.control.TreeItem;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class RulesPresenter {
    private final RulesView view;
    private final DbManager dbManager;

    // Static D&D core rules content
    private static final Map<String, String[]> CORE_RULES = new LinkedHashMap<>();
    static {
        CORE_RULES.put("Würfeln mit dem d20", new String[]{
            "🎲 Das Herzstück von D&D",
            "Wenn der Ausgang einer Aktion unsicher ist, wirfst du in der Regel einen d20 und addierst passende Boni oder Mali. Der Spielleiter sagt dir, welche Probe nötig ist und welche Schwelle du erreichen musst.",
            "Typische Würfe:\n" +
            "• Angriff: d20 + Angriffsbonus gegen die Rüstungsklasse (RK)\n" +
            "• Attributsprobe: d20 + Attributsmodifikator + ggf. Übungsbonus gegen den Schwierigkeitsgrad (SG)\n" +
            "• Rettungswurf: d20 + Rettungswurf-Bonus gegen den SG eines Effekts",
            "Schwierigkeitsgrade (SG):\n" +
            "• 5 – sehr leicht\n" +
            "• 10 – leicht\n" +
            "• 15 – mittel\n" +
            "• 20 – schwer\n" +
            "• 25 – sehr schwer\n" +
            "• 30 – nahezu unmöglich"
        });
        CORE_RULES.put("Vorteil & Nachteil", new String[]{
            "⚖️ Vorteil & Nachteil",
            "Manche Situationen geben dir Vorteil oder Nachteil. Dann würfelst du zwei d20 und nimmst das bessere oder schlechtere Ergebnis.",
            "✅ Vorteil: Nimm das höhere Ergebnis. Beispiele sind ein überraschter Gegner, gute Position oder Hilfe von einem Verbündeten.",
            "❌ Nachteil: Nimm das niedrigere Ergebnis. Beispiele sind Dunkelheit, Blenden, Vergiftung oder ein Angriff auf kurze Distanz mit einer Fernwaffe.",
            "⚠️ Wichtig: Mehrfacher Vorteil oder Nachteil stapelt sich nicht. Haben sich Vorteil und Nachteil gegenseitig aufgehoben, würfelst du normal."
        });
        CORE_RULES.put("Attributswerte & Übungsbonus", new String[]{
            "📊 Die 6 Attribute",
            "Jeder Charakter besitzt Stärke, Geschick, Konstitution, Intelligenz, Weisheit und Charisma. Der Attributswert bestimmt, wie gut dein Charakter in einem Bereich ist.",
            "Der Attributsmodifikator wird berechnet als: (Attributswert − 10) ÷ 2, abgerundet. Übliche Werte sind zum Beispiel:\n" +
            "• 8–9 = −1\n" +
            "• 10–11 = +0\n" +
            "• 12–13 = +1\n" +
            "• 14–15 = +2\n" +
            "• 16–17 = +3\n" +
            "• 18–19 = +4\n" +
            "• 20 = +5",
            "💡 Der Übungsbonus wird zusätzlich addiert, wenn dein Charakter mit einer Waffe, Fertigkeit, einem Rettungswurf oder einem Tool geübt ist. Auf Stufe 1 beträgt er +2."
        });
        CORE_RULES.put("Kampf & Erholung", new String[]{
            "⚔️ Kampf & Erholung",
            "Kampf ist rundenbasiert. Die Reihenfolge wird über Initiative festgelegt: d20 + Geschicklichkeitsmodifikator. Eine Runde steht für ungefähr 6 Sekunden.",
            "In deinem Zug kannst du normalerweise folgende Dinge tun:\n" +
            "• Eine Aktion: angreifen, zaubern, dashen, helfen, verstecken, suchen usw.\n" +
            "• Eine Bonusaktion: falls eine Fähigkeit oder ein Zauber sie erlaubt\n" +
            "• Bewegung: bis zu deiner Bewegungsrate\n" +
            "• Eine Reaktion: einmal pro Runde bei einem passenden Auslöser",
            "❤️ Trefferpunkte und Todeswürfe:\n" +
            "Fällt dein Charakter auf 0 Trefferpunkte, ist er bewusstlos. Am Anfang seines Zuges wirft er Todesrettungswürfe. Drei Erfolge stabilisieren, drei Fehlschläge töten.",
            "😴 Erholung:\n" +
            "• Kurze Rast: mindestens 1 Stunde, Trefferwürfel können eingesetzt werden\n" +
            "• Lange Rast: mindestens 8 Stunden, alle Trefferpunkte werden wiederhergestellt, Trefferwürfel teilweise erholt und Zauberplätze vollständig zurückgesetzt"
        });
    }

    public RulesPresenter(RulesView view) {
        this.view = view;
        this.dbManager = new DbManager();
        this.dbManager.connect();

        initTreeView();
        Platform.runLater(this::showWelcomeMessage);
    }

    private void initTreeView() {
        TreeItem<String> rootItem = new TreeItem<>("DnD Codex");
        rootItem.setExpanded(true);

        // Core Rules
        TreeItem<String> coreRulesItem = new TreeItem<>("Grundregeln");
        coreRulesItem.setExpanded(true);
        for (String key : CORE_RULES.keySet()) {
            coreRulesItem.getChildren().add(new TreeItem<>(key));
        }
        rootItem.getChildren().add(coreRulesItem);

        // Ability Scores from DB
        TreeItem<String> abilitiesItem = new TreeItem<>("Fähigkeiten");
        for (String ability : dbManager.getAllAbilities()) {
            abilitiesItem.getChildren().add(new TreeItem<>(ability));
        }
        rootItem.getChildren().add(abilitiesItem);

        // Skills from DB
        TreeItem<String> skillsItem = new TreeItem<>("Fertigkeiten");
        for (String skill : dbManager.getAllSkills()) {
            skillsItem.getChildren().add(new TreeItem<>(skill));
        }
        rootItem.getChildren().add(skillsItem);

        // Classes from DB
        TreeItem<String> classesItem = new TreeItem<>("Klassen");
        for (String clazz : dbManager.getAllClasses()) {
            classesItem.getChildren().add(new TreeItem<>(clazz));
        }
        rootItem.getChildren().add(classesItem);

        // Races from DB
        TreeItem<String> racesItem = new TreeItem<>("Völker");
        for (String race : dbManager.getAllRaces()) {
            racesItem.getChildren().add(new TreeItem<>(race));
        }
        rootItem.getChildren().add(racesItem);

        view.getRulesTree().setRoot(rootItem);
        view.getRulesTree().setShowRoot(false);
        view.getRulesTree().setCellFactory(tree -> new TreeCell<>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                getStyleClass().removeAll("rules-tree-category", "rules-tree-leaf");
                if (empty || item == null) {
                    setText(null);
                    setGraphic(null);
                    return;
                }

                setText(item);
                TreeItem<String> treeItem = getTreeItem();
                boolean isCategory = treeItem != null && treeItem.getParent() != null && !treeItem.isLeaf();
                if (isCategory) {
                    setText("✦ " + item);
                    getStyleClass().add("rules-tree-category");
                    setGraphic(null);
                } else {
                    setText("  • " + item);
                    getStyleClass().add("rules-tree-leaf");
                }
            }
        });

        view.getRulesTree().getSelectionModel().selectedItemProperty().addListener((obs, oldSel, newSel) -> {
            if (newSel != null && newSel.isLeaf() && newSel.getParent() != null) {
                String category = newSel.getParent().getValue();
                String name = newSel.getValue();
                showContent(category, name);
            }
        });
    }

    private void showWelcomeMessage() {
        VBox container = view.getContentPane();
        container.getChildren().clear();

        VBox introCard = createCard();
        introCard.getChildren().addAll(
            createCardHeader("📖", "Willkommen im DnD Codex"),
            createBodyLabel(
                "Hier findest du die wichtigsten Regeln, Attribute, Fertigkeiten, Klassen und Völker für Dungeons & Dragons 5e."
            ),
            createBodyLabel("Wähle links ein Kapitel oder einen Eintrag aus, um die Details rechts zu lesen.")
        );

        VBox categoriesCard = createCard();
        categoriesCard.getChildren().add(createCardHeader("🗂", "Kategorien"));
        categoriesCard.getChildren().add(createChipRow(List.of("Grundregeln", "Fähigkeiten", "Fertigkeiten", "Klassen", "Völker")));
        categoriesCard.getChildren().add(createBodyLabel("Die Inhalte werden direkt aus der Datenbank geladen und sauber im Lesebereich dargestellt."));

        container.getChildren().addAll(introCard, categoriesCard);
    }

    private void showContent(String category, String name) {
        VBox container = view.getContentPane();
        container.getChildren().clear();

        switch (category) {
            case "Grundregeln":
                showCoreRule(name);
                break;
            case "Fähigkeiten":
                showAbility(name);
                break;
            case "Fertigkeiten":
                showSkill(name);
                break;
            case "Klassen":
                showClass(name);
                break;
            case "Völker":
                showRace(name);
                break;
            default:
                addTitle(container, name, "📄");
                addBody(container, "Kein Inhalt verfügbar.");
        }
    }

    private void showCoreRule(String name) {
        VBox container = view.getContentPane();
        String[] content = CORE_RULES.get(name);
        if (content == null) {
            addTitle(container, name, "📜");
            container.getChildren().add(makeSeparator());
            addBody(container, "Kein Inhalt gefunden.");
            return;
        }

        addTitle(container, content[0], "📜");
        container.getChildren().add(makeSeparator());

        VBox card = createCard();
        card.getChildren().add(createCardHeader("📘", "Regelübersicht"));
        for (int i = 1; i < content.length; i++) {
            card.getChildren().add(createBodyLabel(content[i]));
        }
        container.getChildren().add(card);
    }

    private void showAbility(String name) {
        VBox container = view.getContentPane();
        String description = dbManager.getAbilityDescription(name);
        String importantFor = extractImportantFor(description);
        String measures = extractMeasures(description);

        addTitle(container, name, abilityIcon(name));
        container.getChildren().add(makeSeparator());

        VBox summary = createCard();
        summary.getChildren().add(createCardHeader("📊", "Attributsübersicht"));
        summary.getChildren().add(createChipRow(List.of("Attributswert", "Modifikator", "Übungsbonus")));
        if (!measures.isBlank()) {
            addLabeledBlock(summary, "Wofür steht es?", measures);
        }
        if (!importantFor.isBlank()) {
            addLabeledBlock(summary, "Wichtig für", importantFor);
        }
        container.getChildren().add(summary);

        VBox formula = createCard();
        formula.getChildren().add(createCardHeader("🧮", "Modifikator"));
        formula.getChildren().add(createBodyLabel("Modifikator = (Attributswert − 10) ÷ 2, abgerundet"));
        formula.getChildren().add(createBodyLabel("Beispiel: 14 ergibt +2, 8 ergibt −1."));
        container.getChildren().add(formula);
    }

    private void showSkill(String name) {
        VBox container = view.getContentPane();
        String description = dbManager.getSkillDescription(name);
        List<Map<String, String>> allSkills = dbManager.getSkillsWithAbilities();
        String ability = allSkills.stream()
            .filter(s -> name.equals(s.get("name")))
            .map(s -> s.get("ability"))
            .findFirst().orElse("—");

        addTitle(container, name, "🎯");
        container.getChildren().add(makeSeparator());

        VBox summary = createCard();
        summary.getChildren().add(createCardHeader("🎯", "Fertigkeit"));
        summary.getChildren().add(createChipRow(List.of("Attribut: " + ability, "Proben: d20 + Modifikator + ggf. Übungsbonus")));
        summary.getChildren().add(createBodyLabel(description.isBlank() ? "Keine Beschreibung verfügbar." : description));
        container.getChildren().add(summary);
    }

    private void showClass(String name) {
        VBox container = view.getContentPane();
        Map<String, Object> classInfo = dbManager.getClassByName(name);

        addTitle(container, name, classIcon(name));
        container.getChildren().add(makeSeparator());

        if (classInfo != null) {
            VBox summary = createCard();
            summary.getChildren().add(createCardHeader("⚔️", "Klassenprofil"));
            summary.getChildren().add(createChipRow(classSummaryChips(classInfo)));

            List<String> savingThrows = castStringList(classInfo.get("saving_throws"));
            if (!savingThrows.isEmpty()) {
                addLabeledBlock(summary, "Rettungswürfe", String.join(", ", savingThrows));
            }

            @SuppressWarnings("unchecked")
            List<String> profs = (List<String>) classInfo.get("proficiencies");
            if (profs != null && !profs.isEmpty()) {
                addLabeledBlock(summary, "Übungen", String.join(", ", profs));
            }
            container.getChildren().add(summary);

            List<Map<String, Object>> features = dbManager.getClassFeatures(name);
            if (!features.isEmpty()) {
                VBox featuresCard = createCard();
                featuresCard.getChildren().add(createCardHeader("✨", "Klassenmerkmale"));
                for (Map<String, Object> feature : features) {
                    VBox featureBox = new VBox(6);
                    featureBox.getStyleClass().add("rules-feature-card");
                    Label featTitle = new Label("Stufe " + feature.get("level") + " — " + feature.get("feature_name"));
                    featTitle.getStyleClass().add("rules-feature-title");
                    String desc = (String) feature.get("description");
                    Label featDesc = createBodyLabel(desc != null && !desc.isBlank() ? desc : "Keine Beschreibung.");
                    featureBox.getChildren().addAll(featTitle, featDesc);
                    featuresCard.getChildren().add(featureBox);
                }
                container.getChildren().add(featuresCard);
            }
        } else {
            addBody(container, "Keine Informationen zur Klasse verfügbar.");
        }
    }

    private void showRace(String name) {
        VBox container = view.getContentPane();
        Race race = dbManager.getRaceByName(name);

        addTitle(container, name, "🧝");
        container.getChildren().add(makeSeparator());

        if (race != null) {
            VBox summary = createCard();
            summary.getChildren().add(createCardHeader("🧬", "Volksprofil"));
            summary.getChildren().add(createChipRow(List.of(
                "Größe: " + (race.getSize() != null ? race.getSize() : "—"),
                "Bewegung: " + race.getSpeed() + " Fuß"
            )));

            if (!race.getAbilityBonuses().isEmpty()) {
                FlowPane abilityChips = createChipRow(race.getAbilityBonuses().entrySet().stream()
                    .map(entry -> entry.getKey() + " +" + entry.getValue())
                    .toList());
                summary.getChildren().add(addLabeledHeader("Attributsboni", "💪"));
                summary.getChildren().add(abilityChips);
            }

            if (!race.getLanguages().isEmpty()) {
                addLabeledBlock(summary, "Sprachen", String.join(", ", race.getLanguages()));
            }
            container.getChildren().add(summary);

            List<Map<String, String>> traits = dbManager.getRaceTraits(name);
            if (!traits.isEmpty()) {
                VBox traitsCard = createCard();
                traitsCard.getChildren().add(createCardHeader("✦", "Rassenmerkmale"));
                for (Map<String, String> trait : traits) {
                    VBox traitBox = new VBox(6);
                    traitBox.getStyleClass().add("rules-feature-card");
                    Label traitTitle = new Label(trait.get("trait_name"));
                    traitTitle.getStyleClass().add("rules-feature-title");
                    String desc = trait.get("description");
                    Label traitDesc = createBodyLabel(desc != null && !desc.isBlank() ? desc : "Keine Beschreibung.");
                    traitBox.getChildren().addAll(traitTitle, traitDesc);
                    traitsCard.getChildren().add(traitBox);
                }
                container.getChildren().add(traitsCard);
            }
        } else {
            addBody(container, "Keine Informationen zum Volk verfügbar.");
        }
    }

    // ===== HELPER METHODS =====

    private void addTitle(VBox container, String text, String icon) {
        HBox titleBox = new HBox(10);
        titleBox.setAlignment(Pos.CENTER_LEFT);
        Label iconLabel = new Label(icon);
        iconLabel.setStyle("-fx-font-size: 28px;");
        Label titleLabel = new Label(text);
        titleLabel.getStyleClass().add("rules-title");
        titleLabel.setWrapText(true);
        titleBox.getChildren().addAll(iconLabel, titleLabel);
        container.getChildren().add(titleBox);
    }

    private void addSectionTitle(VBox container, String text) {
        Label label = new Label(text);
        label.getStyleClass().add("rules-section-title");
        VBox.setMargin(label, new Insets(6, 0, 2, 0));
        container.getChildren().add(label);
    }

    private void addBody(VBox container, String text) {
        container.getChildren().add(createBodyLabel(text));
    }

    private void addTag(VBox container, String text) {
        Label tag = new Label(text);
        tag.setStyle("-fx-background-color: rgba(107,0,0,0.12); -fx-text-fill: #6B0000; " +
            "-fx-font-size: 12px; -fx-font-weight: bold; -fx-padding: 3 10 3 10; " +
            "-fx-background-radius: 12;");
        VBox.setMargin(tag, new Insets(0, 0, 4, 0));
        container.getChildren().add(tag);
    }

    private Separator makeSeparator() {
        Separator sep = new Separator();
        sep.setStyle("-fx-background-color: #D4AF37; -fx-opacity: 0.4;");
        VBox.setMargin(sep, new Insets(6, 0, 6, 0));
        return sep;
    }

    private VBox createCard() {
        VBox card = new VBox(10);
        card.getStyleClass().add("rules-card");
        return card;
    }

    private HBox createCardHeader(String icon, String title) {
        HBox header = new HBox(10);
        header.setAlignment(Pos.CENTER_LEFT);
        Label iconLabel = new Label(icon);
        iconLabel.setStyle("-fx-font-size: 20px;");
        Label titleLabel = new Label(title);
        titleLabel.getStyleClass().add("rules-subtitle");
        header.getChildren().addAll(iconLabel, titleLabel);
        return header;
    }

    private Label createBodyLabel(String text) {
        Label label = new Label(text == null || text.isBlank() ? "Keine Beschreibung verfügbar." : text);
        label.getStyleClass().add("rules-body");
        label.setWrapText(true);
        return label;
    }

    private HBox addLabeledHeader(String text, String icon) {
        HBox row = new HBox(8);
        row.setAlignment(Pos.CENTER_LEFT);
        Label iconLabel = new Label(icon);
        iconLabel.setStyle("-fx-font-size: 16px;");
        Label label = new Label(text);
        label.getStyleClass().add("rules-subtitle");
        row.getChildren().addAll(iconLabel, label);
        return row;
    }

    private void addLabeledBlock(VBox container, String heading, String text) {
        container.getChildren().add(addLabeledHeader(heading, "✦"));
        container.getChildren().add(createBodyLabel(text));
    }

    private FlowPane createChipRow(List<String> chips) {
        FlowPane row = new FlowPane();
        row.setHgap(8);
        row.setVgap(8);
        row.getStyleClass().add("rules-chip-row");
        for (String chip : chips) {
            if (chip == null || chip.isBlank()) continue;
            Label label = new Label(chip);
            label.getStyleClass().add("rules-chip");
            row.getChildren().add(label);
        }
        return row;
    }

    private List<String> classSummaryChips(Map<String, Object> classInfo) {
        List<String> chips = new java.util.ArrayList<>();
        Object hitDie = classInfo.get("hit_die");
        Object primary = classInfo.get("primary_ability");
        Object spell = classInfo.get("spellcasting_ability");
        chips.add("Trefferwürfel: d" + (hitDie != null ? hitDie : "?"));
        if (primary != null) {
            chips.add("Primär: " + primary);
        }
        if (spell != null) {
            chips.add("Zauberattribut: " + spell);
        }
        return chips;
    }

    @SuppressWarnings("unchecked")
    private List<String> castStringList(Object value) {
        if (value instanceof List<?> list) {
            List<String> result = new java.util.ArrayList<>();
            for (Object item : list) {
                if (item != null) {
                    result.add(String.valueOf(item));
                }
            }
            return result;
        }
        return List.of();
    }

    private String extractImportantFor(String description) {
        if (description == null || description.isBlank()) return "";
        String marker = "Important for:";
        int index = description.indexOf(marker);
        if (index < 0) return "";
        return description.substring(index + marker.length()).trim().replaceAll("\\.$", "");
    }

    private String extractMeasures(String description) {
        if (description == null || description.isBlank()) return "";
        String marker = "Measures:";
        int start = description.indexOf(marker);
        if (start < 0) return description.trim();
        int end = description.indexOf("Important for:");
        String text = end > start ? description.substring(start + marker.length(), end) : description.substring(start + marker.length());
        return text.trim().replaceAll("\\.$", "");
    }

    private String abilityIcon(String name) {
        return switch (name) {
            case "Strength" -> "💪";
            case "Dexterity" -> "🏃";
            case "Constitution" -> "❤️";
            case "Intelligence" -> "🧠";
            case "Wisdom" -> "🦉";
            case "Charisma" -> "✨";
            default -> "📊";
        };
    }

    private String classIcon(String name) {
        return switch (name) {
            case "Barbarian" -> "🪓";
            case "Bard" -> "🎵";
            case "Cleric" -> "✝️";
            case "Druid" -> "🌿";
            case "Fighter" -> "⚔️";
            case "Monk" -> "🥋";
            case "Paladin" -> "🛡️";
            case "Ranger" -> "🏹";
            case "Rogue" -> "🗡️";
            case "Sorcerer" -> "🔮";
            case "Warlock" -> "👁️";
            case "Wizard" -> "🧙";
            default -> "⚔️";
        };
    }
}
