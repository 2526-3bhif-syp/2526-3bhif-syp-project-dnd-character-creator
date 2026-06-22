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

    // Statische Kernregeln
    private static final Map<String, String[]> CORE_RULES = new LinkedHashMap<>();
    static {
        CORE_RULES.put("Würfeln mit dem d20", new String[]{
            "🎲 Das Herzstück des Spiels",
            "Wenn der Ausgang einer Aktion unsicher ist, würfelst du in der Regel einen d20 und addierst passende Boni oder Mali. Der Spielleiter sagt dir, welche Probe nötig ist und welche Schwelle du erreichen musst.",
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
            "💡 Der Übungsbonus wird zusätzlich addiert, wenn dein Charakter mit einer Waffe, Fertigkeit, einem Rettungswurf oder einem Werkzeug geübt ist. Auf Stufe 1 beträgt er +2."
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
        TreeItem<String> rootItem = new TreeItem<>("Regelbuch");
        rootItem.setExpanded(true);

        // Kernregeln
        TreeItem<String> coreRulesItem = new TreeItem<>("Grundregeln");
        coreRulesItem.setExpanded(true);
        for (String key : CORE_RULES.keySet()) {
            coreRulesItem.getChildren().add(new TreeItem<>(key));
        }
        rootItem.getChildren().add(coreRulesItem);

        // Attribute aus der Datenbank
        TreeItem<String> abilitiesItem = new TreeItem<>("Attribute");
        for (String ability : dbManager.getAllAbilities()) {
            abilitiesItem.getChildren().add(new TreeItem<>(abilityDisplayName(ability)));
        }
        rootItem.getChildren().add(abilitiesItem);

        // Fertigkeiten aus der Datenbank
        TreeItem<String> skillsItem = new TreeItem<>("Fertigkeiten");
        for (String skill : dbManager.getAllSkills()) {
            skillsItem.getChildren().add(new TreeItem<>(skillDisplayName(skill)));
        }
        rootItem.getChildren().add(skillsItem);

        // Klassen aus der Datenbank
        TreeItem<String> classesItem = new TreeItem<>("Klassen");
        for (String clazz : dbManager.getAllClasses()) {
            classesItem.getChildren().add(new TreeItem<>(classDisplayName(clazz)));
        }
        rootItem.getChildren().add(classesItem);

        // Völker aus der Datenbank
        TreeItem<String> racesItem = new TreeItem<>("Völker");
        for (String race : dbManager.getAllRaces()) {
            racesItem.getChildren().add(new TreeItem<>(raceDisplayName(race)));
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
                String name = toEnglishName(category, newSel.getValue());
                showContent(category, name);
            }
        });
    }

    private void showWelcomeMessage() {
        VBox container = view.getContentPane();
        container.getChildren().clear();

        VBox introCard = createCard();
        introCard.getChildren().addAll(
            createCardHeader("📖", "Willkommen im Regelbuch"),
            createBodyLabel(
                "Hier findest du die wichtigsten Regeln, Attribute, Fertigkeiten, Klassen und Völker für Dungeons & Dragons, fünfte Edition."
            ),
            createBodyLabel("Wähle links ein Kapitel oder einen Eintrag aus, um die Details rechts zu lesen.")
        );

        VBox categoriesCard = createCard();
        categoriesCard.getChildren().add(createCardHeader("🗂", "Kategorien"));
        categoriesCard.getChildren().add(createChipRow(List.of("Grundregeln", "Attribute", "Fertigkeiten", "Klassen", "Völker")));
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
            case "Attribute":
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

        addTitle(container, abilityDisplayName(name), abilityIcon(name));
        container.getChildren().add(makeSeparator());

        VBox summary = createCard();
        summary.getChildren().add(createCardHeader("📊", "Attributsübersicht"));
        summary.getChildren().add(createChipRow(List.of("Attribut", "Modifikator", "Übungsbonus")));
        if (!measures.isBlank()) {
            addLabeledBlock(summary, "Bedeutung", localizeDatabaseText(measures));
        }
        if (!importantFor.isBlank()) {
            addLabeledBlock(summary, "Wichtig für", localizeDatabaseText(importantFor));
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

        addTitle(container, skillDisplayName(name), "🎯");
        container.getChildren().add(makeSeparator());

        VBox summary = createCard();
        summary.getChildren().add(createCardHeader("🎯", "Fertigkeit"));
        summary.getChildren().add(createChipRow(List.of("Attribut: " + abilityDisplayName(ability), "Proben: d20 + Modifikator + ggf. Übungsbonus")));
        summary.getChildren().add(createBodyLabel(description.isBlank() ? "Keine Beschreibung verfügbar." : localizeSkillDescription(name, description)));
        container.getChildren().add(summary);
    }

    private void showClass(String name) {
        VBox container = view.getContentPane();
        Map<String, Object> classInfo = dbManager.getClassByName(name);

        addTitle(container, classDisplayName(name), classIcon(name));
        container.getChildren().add(makeSeparator());

        if (classInfo != null) {
            VBox summary = createCard();
            summary.getChildren().add(createCardHeader("⚔️", "Klassenprofil"));
            summary.getChildren().add(createChipRow(classSummaryChips(classInfo)));

            List<String> savingThrows = castStringList(classInfo.get("saving_throws"));
            if (!savingThrows.isEmpty()) {
                addLabeledBlock(summary, "Rettungswürfe", String.join(", ", savingThrows.stream().map(this::abilityDisplayName).toList()));
            }

            @SuppressWarnings("unchecked")
            List<String> profs = (List<String>) classInfo.get("proficiencies");
            if (profs != null && !profs.isEmpty()) {
                addLabeledBlock(summary, "Übungen", localizeDatabaseText(String.join(", ", profs)));
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
                    Label featDesc = createBodyLabel(desc != null && !desc.isBlank() ? localizeDatabaseText(desc) : "Keine Beschreibung.");
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

        addTitle(container, raceDisplayName(name), "🧝");
        container.getChildren().add(makeSeparator());

        if (race != null) {
            VBox summary = createCard();
            summary.getChildren().add(createCardHeader("🧬", "Volksprofil"));
            summary.getChildren().add(createChipRow(List.of(
                "Größe: " + raceSizeDisplayName(race.getSize()),
                "Bewegung: " + race.getSpeed() + " Fuß"
            )));

            if (!race.getAbilityBonuses().isEmpty()) {
                FlowPane abilityChips = createChipRow(race.getAbilityBonuses().entrySet().stream()
                    .map(entry -> abilityDisplayName(entry.getKey()) + " +" + entry.getValue())
                    .toList());
                summary.getChildren().add(addLabeledHeader("Attributsboni", "💪"));
                summary.getChildren().add(abilityChips);
            }

            if (!race.getLanguages().isEmpty()) {
                addLabeledBlock(summary, "Sprachen", String.join(", ", race.getLanguages().stream().map(this::languageDisplayName).toList()));
            }
            container.getChildren().add(summary);

            List<Map<String, String>> traits = dbManager.getRaceTraits(name);
            if (!traits.isEmpty()) {
                VBox traitsCard = createCard();
                traitsCard.getChildren().add(createCardHeader("✦", "Volksmerkmale"));
                for (Map<String, String> trait : traits) {
                    VBox traitBox = new VBox(6);
                    traitBox.getStyleClass().add("rules-feature-card");
                    Label traitTitle = new Label(trait.get("trait_name"));
                    traitTitle.getStyleClass().add("rules-feature-title");
                    String desc = trait.get("description");
                    Label traitDesc = createBodyLabel(desc != null && !desc.isBlank() ? localizeDatabaseText(desc) : "Keine Beschreibung.");
                    traitBox.getChildren().addAll(traitTitle, traitDesc);
                    traitsCard.getChildren().add(traitBox);
                }
                container.getChildren().add(traitsCard);
            }
        } else {
            addBody(container, "Keine Informationen zum Volk verfügbar.");
        }
    }

    // ===== HILFSMETHODEN =====

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
            chips.add("Primär: " + abilityDisplayName(String.valueOf(primary)));
        }
        if (spell != null) {
            chips.add("Zauberattribut: " + abilityDisplayName(String.valueOf(spell)));
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

    private String abilityDisplayName(String englishName) {
        return switch (englishName) {
            case "STR" -> "Stärke";
            case "Strength" -> "Stärke";
            case "DEX" -> "Geschicklichkeit";
            case "Dexterity" -> "Geschicklichkeit";
            case "CON" -> "Konstitution";
            case "Constitution" -> "Konstitution";
            case "INT" -> "Intelligenz";
            case "Intelligence" -> "Intelligenz";
            case "WIS" -> "Weisheit";
            case "Wisdom" -> "Weisheit";
            case "CHA" -> "Charisma";
            case "Charisma" -> "Charisma";
            default -> englishName;
        };
    }

    private String localizeSkillDescription(String skillName, String rawDescription) {
        return switch (skillName) {
            case "Acrobatics" -> "Deine Fähigkeit, in schwierigen Situationen auf den Beinen zu bleiben und akrobatische Kunststücke auszuführen.";
            case "Animal Handling" -> "Deine Fähigkeit, domestizierte Tiere zu beruhigen, Reittiere zu kontrollieren und die Absichten eines Tieres zu erahnen.";
            case "Arcana" -> "Deine Fähigkeit, Wissen über Zauber, magische Gegenstände, arkane Symbole und magische Traditionen abzurufen.";
            case "Athletics" -> "Deine Fähigkeit, zu klettern, zu springen, zu schwimmen und andere körperliche Leistungen zu vollbringen.";
            case "Deception" -> "Deine Fähigkeit, die Wahrheit überzeugend zu verbergen, ob durch Täuschung oder offenes Lügen.";
            case "History" -> "Deine Fähigkeit, Wissen über historische Ereignisse, legendäre Personen, alte Königreiche und Kriege abzurufen.";
            case "Insight" -> "Deine Fähigkeit, die wahren Absichten einer Kreatur zu erkennen, zum Beispiel wenn du eine Lüge aufdeckst.";
            case "Intimidation" -> "Deine Fähigkeit, andere durch Drohungen, feindseliges Verhalten und körperliche Gewalt zu beeinflussen.";
            case "Investigation" -> "Deine Fähigkeit, nach Hinweisen zu suchen und aus ihnen logische Schlüsse zu ziehen.";
            case "Medicine" -> "Deine Fähigkeit, einen sterbenden Gefährten zu stabilisieren oder eine Krankheit zu diagnostizieren.";
            case "Nature" -> "Deine Fähigkeit, Wissen über Tiere, Pflanzen, Jahreszeiten, Wetter und natürliche Kreisläufe abzurufen.";
            case "Perception" -> "Deine Fähigkeit, deine Umgebung aufmerksam wahrzunehmen und versteckte Dinge zu bemerken.";
            case "Performance" -> "Deine Fähigkeit, andere mit Musik, Schauspiel, Tanz oder anderen Darbietungen zu unterhalten.";
            case "Persuasion" -> "Deine Fähigkeit, andere mit Diplomatie, Charme und überzeugenden Worten zu bewegen.";
            case "Religion" -> "Deine Fähigkeit, Wissen über Götter, Rituale, heilige Symbole und religiöse Traditionen abzurufen.";
            case "Sleight of Hand" -> "Deine Fingerfertigkeit für Taschenspielertricks, das Verstecken von Gegenständen und geschickte Handarbeit.";
            case "Stealth" -> "Deine Fähigkeit, dich leise zu bewegen und unbemerkt zu bleiben.";
            case "Survival" -> "Deine Fähigkeit, in der Wildnis zu überleben, Spuren zu lesen und dich in der Natur zurechtzufinden.";
            default -> rawDescription;
        };
    }

    private String localizeDatabaseText(String rawText) {
        if (rawText == null || rawText.isBlank()) return rawText;
        String text = rawText;
        text = text.replace("Measures:", "Bedeutet:");
        text = text.replace("Important for:", "Wichtig für:");
        text = text.replace("Your ability to", "Deine Fähigkeit,");
        text = text.replace("your ability to", "deine Fähigkeit,");
        text = text.replace("You can", "Du kannst");
        text = text.replace("you can", "du kannst");
        text = text.replace("You have", "Du hast");
        text = text.replace("you have", "du hast");
        text = text.replace("When ", "Wenn ");
        text = text.replace("when ", "wenn ");
        text = text.replace("While ", "Solange ");
        text = text.replace("while ", "solange ");
        text = text.replace("short rest", "kurze Rast");
        text = text.replace("long rest", "lange Rast");
        text = text.replace("hit points", "Trefferpunkte");
        text = text.replace("saving throw", "Rettungswurf");
        text = text.replace("skill", "Fertigkeit");
        text = text.replace("attack", "Angriff");
        text = text.replace("damage", "Schaden");
        text = text.replace("spell", "Zauber");
        text = text.replace("weapon", "Waffe");
        text = text.replace("tool", "Werkzeug");
        text = text.replace("feet", "Fuß");
        text = text.replace("Light Armor", "Leichte Rüstung");
        text = text.replace("Medium Armor", "Mittlere Rüstung");
        text = text.replace("Heavy Armor", "Schwere Rüstung");
        text = text.replace("Shields", "Schilde");
        text = text.replace("Simple Melee", "Einfache Nahkampfwaffen");
        text = text.replace("Simple Ranged", "Einfache Fernkampfwaffen");
        text = text.replace("Martial Melee", "Kriegsnahkampfwaffen");
        text = text.replace("Martial Ranged", "Kriegsfernkampfwaffen");
        return text;
    }

    private String abilityDisplayName(String englishName) {
        return switch (englishName) {
            case "Strength" -> "Stärke";
            case "Dexterity" -> "Geschicklichkeit";
            case "Constitution" -> "Konstitution";
            case "Intelligence" -> "Intelligenz";
            case "Wisdom" -> "Weisheit";
            case "Charisma" -> "Charisma";
            default -> englishName;
        };
    }

    private String skillDisplayName(String englishName) {
        return switch (englishName) {
            case "Acrobatics" -> "Akrobatik";
            case "Animal Handling" -> "Umgang mit Tieren";
            case "Arcana" -> "Arkane Kunde";
            case "Athletics" -> "Athletik";
            case "Deception" -> "Täuschung";
            case "History" -> "Geschichte";
            case "Insight" -> "Motiv erkennen";
            case "Intimidation" -> "Einschüchtern";
            case "Investigation" -> "Nachforschung";
            case "Medicine" -> "Heilkunde";
            case "Nature" -> "Naturkunde";
            case "Perception" -> "Wahrnehmung";
            case "Performance" -> "Auftreten";
            case "Persuasion" -> "Überzeugen";
            case "Religion" -> "Religion";
            case "Sleight of Hand" -> "Fingerfertigkeit";
            case "Stealth" -> "Heimlichkeit";
            case "Survival" -> "Überleben";
            default -> englishName;
        };
    }

    private String classDisplayName(String englishName) {
        return switch (englishName) {
            case "Barbarian" -> "Barbar";
            case "Bard" -> "Barde";
            case "Cleric" -> "Kleriker";
            case "Druid" -> "Druide";
            case "Fighter" -> "Kämpfer";
            case "Monk" -> "Mönch";
            case "Paladin" -> "Paladin";
            case "Ranger" -> "Waldläufer";
            case "Rogue" -> "Schurke";
            case "Sorcerer" -> "Zauberer";
            case "Warlock" -> "Hexenmeister";
            case "Wizard" -> "Magier";
            default -> englishName;
        };
    }

    private String raceDisplayName(String englishName) {
        return switch (englishName) {
            case "Human" -> "Mensch";
            case "Hill Dwarf" -> "Hügelzwerg";
            case "Mountain Dwarf" -> "Bergzwerg";
            case "High Elf" -> "Hochelf";
            case "Wood Elf" -> "Waldelf";
            case "Dark Elf" -> "Dunkelelf";
            case "Lightfoot Halfling" -> "Leichtfuß-Halbling";
            case "Stout Halfling" -> "Robust-Halbling";
            case "Dragonborn" -> "Drachengeborener";
            case "Gnome" -> "Gnom";
            case "Half-Elf" -> "Halbelf";
            case "Half-Orc" -> "Halbork";
            case "Tiefling" -> "Tiefling";
            default -> englishName;
        };
    }

    private String raceSizeDisplayName(String size) {
        return switch (size) {
            case "Small" -> "Klein";
            case "Medium" -> "Mittel";
            case "Large" -> "Groß";
            case "Tiny" -> "Winzig";
            default -> size != null && !size.isBlank() ? size : "—";
        };
    }

    private String languageDisplayName(String language) {
        return switch (language) {
            case "Common" -> "Gemeinsprache";
            case "Dwarvish" -> "Zwergisch";
            case "Elvish" -> "Elfisch";
            case "Giant" -> "Riesisch";
            case "Gnomish" -> "Gnomisch";
            case "Goblin" -> "Goblinisch";
            case "Halfling" -> "Halblingssprache";
            case "Orc" -> "Orkisch";
            case "Draconic" -> "Drachisch";
            case "Deep Speech" -> "Tiefensprache";
            case "Infernal" -> "Infernalisch";
            case "Celestial" -> "Himmlisch";
            case "Primordial" -> "Ursprache";
            default -> language;
        };
    }

    private String toEnglishName(String category, String displayName) {
        return switch (category) {
            case "Attribute" -> switch (displayName) {
                case "Stärke" -> "Strength";
                case "Geschicklichkeit" -> "Dexterity";
                case "Konstitution" -> "Constitution";
                case "Intelligenz" -> "Intelligence";
                case "Weisheit" -> "Wisdom";
                case "Charisma" -> "Charisma";
                default -> displayName;
            };
            case "Fertigkeiten" -> switch (displayName) {
                case "Akrobatik" -> "Acrobatics";
                case "Umgang mit Tieren" -> "Animal Handling";
                case "Arkane Kunde" -> "Arcana";
                case "Athletik" -> "Athletics";
                case "Täuschung" -> "Deception";
                case "Geschichte" -> "History";
                case "Motiv erkennen" -> "Insight";
                case "Einschüchtern" -> "Intimidation";
                case "Nachforschung" -> "Investigation";
                case "Heilkunde" -> "Medicine";
                case "Naturkunde" -> "Nature";
                case "Wahrnehmung" -> "Perception";
                case "Auftreten" -> "Performance";
                case "Überzeugen" -> "Persuasion";
                case "Religion" -> "Religion";
                case "Fingerfertigkeit" -> "Sleight of Hand";
                case "Heimlichkeit" -> "Stealth";
                case "Überleben" -> "Survival";
                default -> displayName;
            };
            case "Klassen" -> switch (displayName) {
                case "Barbar" -> "Barbarian";
                case "Barde" -> "Bard";
                case "Kleriker" -> "Cleric";
                case "Druide" -> "Druid";
                case "Kämpfer" -> "Fighter";
                case "Mönch" -> "Monk";
                case "Paladin" -> "Paladin";
                case "Waldläufer" -> "Ranger";
                case "Schurke" -> "Rogue";
                case "Zauberer" -> "Sorcerer";
                case "Hexenmeister" -> "Warlock";
                case "Magier" -> "Wizard";
                default -> displayName;
            };
            case "Völker" -> switch (displayName) {
                case "Mensch" -> "Human";
                case "Hügelzwerg" -> "Hill Dwarf";
                case "Bergzwerg" -> "Mountain Dwarf";
                case "Hochelf" -> "High Elf";
                case "Waldelf" -> "Wood Elf";
                case "Dunkelelf" -> "Dark Elf";
                case "Leichtfuß-Halbling" -> "Lightfoot Halfling";
                case "Robust-Halbling" -> "Stout Halfling";
                case "Drachengeborener" -> "Dragonborn";
                case "Gnom" -> "Gnome";
                case "Halbelf" -> "Half-Elf";
                case "Halbork" -> "Half-Orc";
                case "Tiefling" -> "Tiefling";
                default -> displayName;
            };
            default -> displayName;
        };
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
