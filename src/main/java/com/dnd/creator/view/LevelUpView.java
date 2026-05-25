package com.dnd.creator.view;

import com.dnd.creator.data.DbManager;
import com.dnd.creator.model.CharacterModel;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.Window;

import java.io.IOException;
import java.util.*;

/**
 * Vollständiger Level Up Dialog. Zeigt dynamisch je nach Klasse, Rasse und Level:
 *   - Neue Klassen-Features
 *   - Subklassen-Wahl (oder neue Subklassen-Features wenn bereits gewählt)
 *   - Rassen-spezifische Features auf bestimmten Leveln (Drow, Tiefling)
 *   - HP-Steigerung (Würfeln oder Durchschnitt, Hill Dwarf bekommt +1)
 *   - ASI ODER Feat (auf ASI-Leveln)
 *   - Neue Cantrips (klassenabhängig)
 *   - Neue Spells (Known Casters)
 *   - Spell Slots Übersicht
 *   - Proficiency Bonus Steigerung
 */
public class LevelUpView {

    /** Auf welchem Level wählt eine Klasse ihre Subklasse? */
    private static final Map<String, Integer> SUBCLASS_LEVEL = Map.ofEntries(
            Map.entry("Barbarian", 3), Map.entry("Bard", 3),
            Map.entry("Cleric", 1),   Map.entry("Druid", 2),
            Map.entry("Fighter", 3),  Map.entry("Monk", 3),
            Map.entry("Paladin", 3),  Map.entry("Ranger", 3),
            Map.entry("Rogue", 3),    Map.entry("Sorcerer", 1),
            Map.entry("Warlock", 1),  Map.entry("Wizard", 2)
    );

    /** Klassen die ihre Spells explizit kennen (vs. Prepared Casters wie Cleric/Wizard). */
    private static final Set<String> KNOWN_CASTERS = Set.of("Bard", "Sorcerer", "Warlock", "Ranger");

    /** Rassen-Features die auf bestimmten Leveln freigeschaltet werden. */
    private static final Map<String, Map<Integer, String[]>> RACE_LEVEL_FEATURES = Map.of(
            "Drow", Map.of(
                    3, new String[]{"Faerie Fire",     "Du kannst Faerie Fire 1x pro Tag wirken (Drow Magic)."},
                    5, new String[]{"Darkness",        "Du kannst Darkness 1x pro Tag wirken (Drow Magic)."}
            ),
            "Tiefling", Map.of(
                    3, new String[]{"Hellish Rebuke",  "Du kannst Hellish Rebuke (2nd-level) 1x pro Tag wirken (Infernal Legacy)."},
                    5, new String[]{"Darkness",        "Du kannst Darkness 1x pro Tag wirken (Infernal Legacy)."}
            )
    );

    /** Auswahl an Standard-Feats. */
    private static final String[][] FEATS = {
            {"Alert",              "+5 Initiative. Du wirst nie überrascht. Unsichtbare Angreifer haben keinen Vorteil gegen dich."},
            {"Athlete",            "+1 STR oder DEX. Aufstehen kostet nur 5 ft. Klettergeschwindigkeit. Sprung aus dem Stand."},
            {"Great Weapon Master","Bonus-Angriff nach Critical/Kill. -5 Angriff / +10 Schaden mit Heavy-Waffen."},
            {"Healer",             "Heiler-Kit heilt 1d6+4 HP. Stabilisieren bringt 1 HP zurück."},
            {"Inspiring Leader",   "10 Min Rede gibt bis zu 6 Verbündeten temporäre HP = Stufe + CHA-Mod."},
            {"Lucky",              "3 Glückspunkte pro Long Rest. Würfle einen Wurf neu."},
            {"Mage Slayer",        "Opportunity Attack gegen Caster. Vorteil vs. Zauber von Gegnern in 5 ft."},
            {"Mobile",             "+10 ft Geschwindigkeit. Keine Opportunity Attacks von Zielen die du im Nahkampf getroffen hast."},
            {"Observant",          "+1 INT oder WIS. +5 auf passive Perception und Investigation."},
            {"Resilient",          "+1 auf einen Stat. Proficiency in dessen Saving Throw."},
            {"Sentinel",           "Treffer bei OA stoppt Bewegung. Reaction-Angriff wenn Verbündeter angegriffen wird."},
            {"Sharpshooter",       "Keine Cover-/Range-Penalties. -5 Angriff / +10 Schaden mit Fernkampf."},
            {"Tough",              "+2 HP pro Stufe (rückwirkend)."},
            {"War Caster",         "Vorteil auf Konzentrations-Saves. Somatische Komponenten mit Waffe. OAs können Zauber sein."}
    };

    @FXML private Label lblTitle;
    @FXML private Label lblSubtitle;
    @FXML private VBox contentBox;
    @FXML private Button btnCancel;
    @FXML private Button btnConfirm;

    private final CharacterModel character;
    private final Window ownerWindow;
    private final Runnable onCompletionCallback;
    private final DbManager dbManager = new DbManager();
    private final int newLevel;
    private final String cls;
    private final List<String> replacementNewSpells = new ArrayList<>();



    // ── State (gewählte Werte) ─────────────────────────────────────────
    private int chosenHp = 0;
    private String chosenSubclass = null;
    private boolean usingFeat = false;
    private String asiAbility1 = null;
    private int asiBonus1 = 2;
    private String asiAbility2 = null;
    private Integer asiBonus2 = null;
    private String chosenFeat = null;
    private final List<String> newSpellsChosen = new ArrayList<>();
    private final List<String> newCantripsChosen = new ArrayList<>();
    private final List<String> replacedSpells = new ArrayList<>();

    // ── Validation flags ───────────────────────────────────────────────
    private boolean needsAsiOrFeat = false;
    private boolean needsSubclass = false;
    private int neededNewCantrips = 0;
    private int neededNewSpells = 0;

    public LevelUpView(CharacterModel character, Window ownerWindow, Runnable onCompletion) {
        this.character = character;
        this.ownerWindow = ownerWindow;
        this.onCompletionCallback = onCompletion;
        this.newLevel = character.getLevel() + 1;
        this.cls = character.getCharacterClass();
    }

    public void show() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/dnd/creator/view/LevelUpView.fxml"));
            loader.setController(this);
            Parent root = loader.load();
            dbManager.connect();
            buildContent();
            setupButtons();

            Stage stage = new Stage();
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.initOwner(ownerWindow);
            stage.setTitle("Level Up — " + character.getName());
            stage.setScene(new Scene(root));
            stage.setMinWidth(720);
            stage.setMinHeight(600);
            stage.show();
        } catch (IOException e) {
            throw new RuntimeException("Failed to load LevelUpView.fxml", e);
        }
    }

    // ════════════════════════════════════════════════════════════════════════
    // CONTENT BUILDER — baut alle Sections in der richtigen Reihenfolge
    // ════════════════════════════════════════════════════════════════════════

    private void buildContent() {
        lblTitle.setText("LEVEL " + character.getLevel() + " → " + newLevel);
        lblSubtitle.setText(character.getName() + " — " + cls
                + (character.getRace() != null ? " (" + character.getRace().getName() + ")" : ""));

        buildClassFeaturesSection();
        buildSubclassSection();
        buildRaceFeaturesSection();
        buildHpSection();

        if (dbManager.isAsiLevel(cls, newLevel)) {
            needsAsiOrFeat = true;
            buildAsiOrFeatSection();
        }

        buildNewCantripsSection();
        buildNewSpellsSection();
        buildSpellSlotsOverview();
        buildProficiencyBonusSection();
    }

    private void buildClassFeaturesSection() {
        List<String[]> features = dbManager.getClassFeaturesAtLevel(cls, newLevel);
        // Filter ASI heraus, da wir das in einer eigenen Section haben
        features.removeIf(f -> f[0].toLowerCase().contains("ability score improvement"));
        if (features.isEmpty()) return;

        VBox section = sectionBox("✨ Neue Klassen-Fähigkeiten");
        for (String[] f : features) section.getChildren().add(featureCard(f[0], f[1]));
        contentBox.getChildren().add(section);
    }

    private void buildSubclassSection() {
        int subclassLevel = SUBCLASS_LEVEL.getOrDefault(cls, -1);
        boolean hasSubclass = character.getSubclassName() != null && !character.getSubclassName().isBlank();

        // Fall A: noch keine Subklasse + neues Level ≥ Subklassen-Level → muss wählen
        if (!hasSubclass && newLevel >= subclassLevel) {
            needsSubclass = true;
            List<String[]> options = dbManager.getSubclassesForClass(cls);
            if (options.isEmpty()) return;

            VBox section = sectionBox("🏛️ Subklasse wählen");
            Label info = new Label("Wähle eine Subklasse für deinen " + cls + ":");
            info.setStyle("-fx-font-size: 12px; -fx-text-fill: #5a3a1a;");
            section.getChildren().add(info);

            ToggleGroup group = new ToggleGroup();
            for (String[] sc : options) {
                VBox card = new VBox(4);
                card.setStyle("-fx-background-color: white; -fx-border-color: #C6A664; -fx-border-width: 2; -fx-border-radius: 8; -fx-background-radius: 8; -fx-padding: 10;");
                RadioButton rb = new RadioButton(sc[0]);
                rb.setStyle("-fx-font-size: 14px; -fx-font-weight: bold; -fx-text-fill: #6B0000;");
                rb.setToggleGroup(group);
                rb.setUserData(sc[0]);
                Label desc = new Label(sc[1] != null ? sc[1] : "");
                desc.setStyle("-fx-font-size: 11px; -fx-text-fill: #333;");
                desc.setWrapText(true);
                card.getChildren().addAll(rb, desc);
                section.getChildren().add(card);
            }
            group.selectedToggleProperty().addListener((obs, o, n) -> {
                chosenSubclass = (n != null) ? (String) n.getUserData() : null;
            });
            contentBox.getChildren().add(section);
        }
        // Fall B: hat bereits Subklasse → zeige neue Features für dieses Level
        else if (hasSubclass) {
            List<String[]> features = dbManager.getSubclassFeaturesAtLevel(character.getSubclassName(), newLevel);
            if (features.isEmpty()) return;
            VBox section = sectionBox("🏛️ " + character.getSubclassName() + " — neue Fähigkeiten");
            for (String[] f : features) section.getChildren().add(featureCard(f[0], f[1]));
            contentBox.getChildren().add(section);
        }
    }

    private void buildRaceFeaturesSection() {
        if (character.getRace() == null) return;
        String raceName = character.getRace().getName();
        Map<Integer, String[]> levelFeats = RACE_LEVEL_FEATURES.get(raceName);
        if (levelFeats == null) return;
        String[] feature = levelFeats.get(newLevel);
        if (feature == null) return;

        VBox section = sectionBox("🌟 " + raceName + " — Rassen-Fähigkeit");
        section.getChildren().add(featureCard(feature[0], feature[1]));
        contentBox.getChildren().add(section);
    }

    private void buildHpSection() {
        int hitDie = character.getClassHitDie() == 0 ? 8 : character.getClassHitDie();
        int conMod = (character.getConstitution() - 10) / 2;
        int average = (hitDie / 2 + 1) + conMod;
        int dwarfBonus = isHillDwarf() ? 1 : 0;
        int finalAverage = Math.max(1, average + dwarfBonus);

        VBox section = sectionBox("❤️ Trefferpunkte");
        String info = "Hit Die: d" + hitDie + "  •  CON-Modifikator: " + (conMod >= 0 ? "+" : "") + conMod
                + (dwarfBonus > 0 ? "  •  Dwarven Toughness: +1" : "");
        Label infoLbl = new Label(info);
        infoLbl.setStyle("-fx-font-size: 12px; -fx-text-fill: #5a3a1a;");
        section.getChildren().add(infoLbl);

        HBox choices = new HBox(14);
        choices.setAlignment(Pos.CENTER_LEFT);
        Button btnAvg = new Button("Durchschnitt: +" + finalAverage + " HP");
        Button btnRoll = new Button("Würfeln (d" + hitDie + ")");
        Label resultLbl = new Label("");
        resultLbl.setStyle("-fx-font-size: 14px; -fx-font-weight: bold; -fx-text-fill: #2d5a3d;");

        btnAvg.setStyle(styleBtnDefault());
        btnRoll.setStyle(styleBtnDefault());

        btnAvg.setOnAction(e -> {
            chosenHp = finalAverage;
            btnAvg.setStyle(styleBtnSelected());
            btnRoll.setStyle(styleBtnDefault());
            resultLbl.setText("✓ +" + chosenHp + " HP gewählt");
        });
        btnRoll.setOnAction(e -> {
            int roll = (int)(Math.random() * hitDie) + 1;
            chosenHp = Math.max(1, roll + conMod + dwarfBonus);
            btnRoll.setStyle(styleBtnSelected());
            btnAvg.setStyle(styleBtnDefault());
            resultLbl.setText("🎲 Gewürfelt " + roll + " + " + (conMod + dwarfBonus) + " = +" + chosenHp + " HP");
        });

        // Default: Average
        chosenHp = finalAverage;
        btnAvg.setStyle(styleBtnSelected());
        resultLbl.setText("✓ +" + chosenHp + " HP gewählt");

        choices.getChildren().addAll(btnAvg, btnRoll, resultLbl);
        section.getChildren().add(choices);
        contentBox.getChildren().add(section);
    }

    private void buildAsiOrFeatSection() {
        VBox section = sectionBox("📈 Attributsteigerung oder Talent");
        Label info = new Label("Wähle zwischen einer Attributssteigerung (ASI) oder einem Talent:");
        info.setStyle("-fx-font-size: 12px; -fx-text-fill: #5a3a1a;");
        section.getChildren().add(info);

        ToggleGroup mainGroup = new ToggleGroup();
        RadioButton rbAsi = new RadioButton("Attributssteigerung (ASI)");
        RadioButton rbFeat = new RadioButton("Talent (Feat) wählen");
        rbAsi.setToggleGroup(mainGroup);
        rbFeat.setToggleGroup(mainGroup);
        rbAsi.setStyle("-fx-font-size: 13px; -fx-font-weight: bold; -fx-text-fill: #1A1A1A;");
        rbFeat.setStyle("-fx-font-size: 13px; -fx-font-weight: bold; -fx-text-fill: #1A1A1A;");
        rbAsi.setSelected(true);
        usingFeat = false;

        HBox modeBox = new HBox(24, rbAsi, rbFeat);
        section.getChildren().add(modeBox);

        VBox asiBox = buildAsiSubsection();
        VBox featBox = buildFeatSubsection();
        featBox.setVisible(false);
        featBox.setManaged(false);

        section.getChildren().addAll(asiBox, featBox);

        mainGroup.selectedToggleProperty().addListener((obs, o, n) -> {
            usingFeat = (n == rbFeat);
            asiBox.setVisible(!usingFeat);  asiBox.setManaged(!usingFeat);
            featBox.setVisible(usingFeat);  featBox.setManaged(usingFeat);
        });

        contentBox.getChildren().add(section);
    }

    private VBox buildAsiSubsection() {
        VBox box = new VBox(8);
        box.setPadding(new Insets(8, 0, 0, 0));
        List<String> abilities = List.of("Strength", "Dexterity", "Constitution",
                "Intelligence", "Wisdom", "Charisma");

        ToggleGroup modeGroup = new ToggleGroup();
        RadioButton rbTwo = new RadioButton("+2 auf ein Attribut");
        RadioButton rbOne = new RadioButton("+1 / +1 auf zwei Attribute");
        rbTwo.setStyle("-fx-text-fill: #1A1A1A; -fx-font-size: 13px;");
        rbOne.setStyle("-fx-text-fill: #1A1A1A; -fx-font-size: 13px;");
        rbTwo.setToggleGroup(modeGroup);
        rbOne.setToggleGroup(modeGroup);
        rbTwo.setSelected(true);
        asiBonus1 = 2; asiBonus2 = null;

        HBox modeRow = new HBox(24, rbTwo, rbOne);

        ComboBox<String> combo1 = new ComboBox<>();
        combo1.getItems().addAll(abilities);
        combo1.setPromptText("Attribut wählen…");
        combo1.setOnAction(e -> asiAbility1 = combo1.getValue());

        ComboBox<String> combo2 = new ComboBox<>();
        combo2.getItems().addAll(abilities);
        combo2.setPromptText("Zweites Attribut…");
        combo2.setVisible(false);
        combo2.setManaged(false);
        combo2.setOnAction(e -> asiAbility2 = combo2.getValue());

        HBox comboRow = new HBox(12, combo1, combo2);

        modeGroup.selectedToggleProperty().addListener((obs, o, n) -> {
            if (n == rbTwo) {
                asiBonus1 = 2; asiBonus2 = null; asiAbility2 = null;
                combo2.setVisible(false); combo2.setManaged(false);
                combo2.getSelectionModel().clearSelection();
            } else {
                asiBonus1 = 1; asiBonus2 = 1;
                combo2.setVisible(true); combo2.setManaged(true);
            }
        });
        box.getChildren().addAll(modeRow, comboRow);
        return box;
    }

    private VBox buildFeatSubsection() {
        VBox box = new VBox(6);
        box.setPadding(new Insets(8, 0, 0, 0));

        ComboBox<String> combo = new ComboBox<>();
        for (String[] f : FEATS) combo.getItems().add(f[0]);
        combo.setPromptText("Talent wählen…");

        Label descLbl = new Label("");
        descLbl.setStyle("-fx-font-size: 12px; -fx-text-fill: #333; -fx-padding: 6 0 0 0;");
        descLbl.setWrapText(true);

        combo.setOnAction(e -> {
            chosenFeat = combo.getValue();
            for (String[] f : FEATS) if (f[0].equals(chosenFeat)) descLbl.setText(f[1]);
        });
        box.getChildren().addAll(combo, descLbl);
        return box;
    }

    private void buildNewCantripsSection() {
        Integer oldC = dbManager.getCantripsKnownAtLevel(cls, character.getLevel());
        Integer newC = dbManager.getCantripsKnownAtLevel(cls, newLevel);
        if (oldC == null || newC == null) return;
        int diff = newC - oldC;
        if (diff <= 0) return;

        neededNewCantrips = diff;
        VBox section = sectionBox("🪄 Neuer Cantrip (" + diff + " wählen)");

        List<Map<String,String>> cantrips = dbManager.getSpellsForClassAndLevel(cls, 0);
        List<String> known = character.getSelectedCantrips() != null ? character.getSelectedCantrips() : List.of();

        FlowPane pane = new FlowPane(10, 8);
        for (Map<String,String> sp : cantrips) {
            if (known.contains(sp.get("name"))) continue;
            CheckBox cb = new CheckBox(sp.get("name"));
            cb.setStyle("-fx-text-fill: #1A1A1A; -fx-font-size: 12px;");
            cb.setTooltip(new Tooltip(
                    "Schule: " + sp.get("school") + "\nWirkzeit: " + sp.get("casting_time") +
                            "\nReichweite: " + sp.get("range")));
            cb.setOnAction(e -> {
                if (cb.isSelected()) {
                    if (newCantripsChosen.size() >= diff) { cb.setSelected(false); return; }
                    newCantripsChosen.add(cb.getText());
                } else newCantripsChosen.remove(cb.getText());
            });
            pane.getChildren().add(cb);
        }
        section.getChildren().add(pane);
        contentBox.getChildren().add(section);
    }

    private void buildNewSpellsSection() {
        if (!KNOWN_CASTERS.contains(cls)) return;
        Integer oldS = dbManager.getSpellsKnownAtLevel(cls, character.getLevel());
        Integer newS = dbManager.getSpellsKnownAtLevel(cls, newLevel);
        if (oldS == null || newS == null) return;
        int diff = newS - oldS;

        Map<Integer, Integer> slots = dbManager.getAllSpellSlotsAtLevel(cls, newLevel);
        int maxSpellLevel = slots.keySet().stream().max(Integer::compareTo).orElse(1);

        List<String> known = character.getSelectedSpells() != null
                ? character.getSelectedSpells() : new ArrayList<>();
        List<String> knownCantrips = character.getSelectedCantrips() != null
                ? character.getSelectedCantrips() : new ArrayList<>();

        // Cantrips aus den "known spells" rausfiltern, falls sie versehentlich drinstecken
        List<String> knownSpellsOnly = new ArrayList<>();
        for (String s : known) {
            if (!knownCantrips.contains(s)) knownSpellsOnly.add(s);
        }

        // ─── Neue Zauber wählen (KEINE Cantrips, nur Grad 1+) ───────────────
        if (diff > 0) {
            neededNewSpells = diff;
            VBox section = sectionBox("📜 Neue Zauber (" + diff + " wählen)");
            Label info = new Label("Wähle " + diff + " neue Zauber bis Zaubergrad " + maxSpellLevel + ":");
            info.setStyle("-fx-font-size: 12px; -fx-text-fill: #5a3a1a;");
            section.getChildren().add(info);

            for (int lvl = 1; lvl <= maxSpellLevel; lvl++) {
                List<Map<String,String>> spells = dbManager.getSpellsForClassAndLevel(cls, lvl);
                if (spells.isEmpty()) continue;
                Label lvlLabel = new Label("Zaubergrad " + lvl);
                lvlLabel.setStyle("-fx-font-size: 13px; -fx-font-weight: bold; -fx-text-fill: #8B0000; -fx-padding: 6 0 2 0;");
                section.getChildren().add(lvlLabel);

                FlowPane pane = new FlowPane(10, 8);
                for (Map<String,String> sp : spells) {
                    if (knownSpellsOnly.contains(sp.get("name"))) continue;
                    CheckBox cb = new CheckBox(sp.get("name"));
                    cb.setStyle("-fx-text-fill: #1A1A1A; -fx-font-size: 12px;");
                    cb.setTooltip(new Tooltip(
                            "Schule: " + sp.get("school") + "\nWirkzeit: " + sp.get("casting_time") +
                                    "\nReichweite: " + sp.get("range") + "\nDauer: " + sp.get("duration")));
                    cb.setOnAction(e -> {
                        if (cb.isSelected()) {
                            if (newSpellsChosen.size() >= diff) { cb.setSelected(false); return; }
                            newSpellsChosen.add(cb.getText());
                        } else newSpellsChosen.remove(cb.getText());
                    });
                    pane.getChildren().add(cb);
                }
                section.getChildren().add(pane);
            }
            contentBox.getChildren().add(section);
        }

        // ─── Spell-Replacement: nur ECHTE Zauber (Grad 1+), keine Cantrips ───
        if (!knownSpellsOnly.isEmpty()) {
            VBox section = sectionBox("🔄 Zauber tauschen (optional)");
            Label info = new Label("Du darfst EINEN bekannten Zauber durch einen anderen ersetzen:");
            info.setStyle("-fx-font-size: 12px; -fx-text-fill: #5a3a1a;");
            section.getChildren().add(info);

            Label lbl1 = new Label("Alten Zauber entfernen:");
            lbl1.setStyle("-fx-font-size: 12px; -fx-font-weight: bold; -fx-text-fill: #5a3a1a;");
            ComboBox<String> comboOld = new ComboBox<>();
            comboOld.getItems().add("— Keinen ersetzen —");
            comboOld.getItems().addAll(knownSpellsOnly);
            comboOld.getSelectionModel().selectFirst();
            comboOld.setPrefWidth(340);

            Label lbl2 = new Label("Durch neuen Zauber ersetzen:");
            lbl2.setStyle("-fx-font-size: 12px; -fx-font-weight: bold; -fx-text-fill: #5a3a1a;");
            ComboBox<String> comboNew = new ComboBox<>();
            comboNew.setPrefWidth(340);
            comboNew.setVisible(false);
            comboNew.setManaged(false);
            lbl2.setVisible(false);
            lbl2.setManaged(false);

            // Alle wählbaren Ersatz-Zauber (NUR Grad 1+, keine Cantrips)
            Set<String> allKnownSet = new HashSet<>(knownSpellsOnly);
            for (int lvl = 1; lvl <= maxSpellLevel; lvl++) {
                for (Map<String,String> sp : dbManager.getSpellsForClassAndLevel(cls, lvl)) {
                    if (!allKnownSet.contains(sp.get("name"))) {
                        comboNew.getItems().add("[Grad " + lvl + "] " + sp.get("name"));
                    }
                }
            }

            comboOld.setOnAction(e -> {
                replacedSpells.clear();
                replacementNewSpells.clear();
                comboNew.getSelectionModel().clearSelection();
                String v = comboOld.getValue();
                if (v != null && !v.startsWith("—")) {
                    replacedSpells.add(v);
                    comboNew.setVisible(true); comboNew.setManaged(true);
                    lbl2.setVisible(true); lbl2.setManaged(true);
                } else {
                    comboNew.setVisible(false); comboNew.setManaged(false);
                    lbl2.setVisible(false); lbl2.setManaged(false);
                }
            });

            comboNew.setOnAction(e -> {
                replacementNewSpells.clear();
                String v = comboNew.getValue();
                if (v != null) {
                    int idx = v.indexOf("] ");
                    replacementNewSpells.add(idx >= 0 ? v.substring(idx + 2) : v);
                }
            });

            section.getChildren().addAll(lbl1, comboOld, lbl2, comboNew);
            contentBox.getChildren().add(section);
        }
    }

    private void buildSpellSlotsOverview() {
        Map<Integer, Integer> slots = dbManager.getAllSpellSlotsAtLevel(cls, newLevel);
        if (slots.isEmpty()) return;

        VBox section = sectionBox("🔮 Zauberplätze auf Charakterstufe " + newLevel);
        HBox row = new HBox(10);
        row.setAlignment(Pos.CENTER_LEFT);
        slots.forEach((lvl, count) -> {
            VBox box = new VBox(2);
            box.setAlignment(Pos.CENTER);
            box.setStyle("-fx-background-color: white; -fx-border-color: #8B0000; -fx-border-width: 2; -fx-border-radius: 8; -fx-background-radius: 8; -fx-padding: 6 12; -fx-min-width: 60;");
            Label l1 = new Label("Grad " + lvl);
            l1.setStyle("-fx-font-size: 10px; -fx-text-fill: #555;");
            Label l2 = new Label(count + "x");
            l2.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: #8B0000;");
            box.getChildren().addAll(l1, l2);
            row.getChildren().add(box);
        });
        section.getChildren().add(row);
        contentBox.getChildren().add(section);
    }

    private void buildProficiencyBonusSection() {
        int oldPb = 2 + (character.getLevel() - 1) / 4;
        int newPb = 2 + (newLevel - 1) / 4;
        if (oldPb == newPb) return;

        VBox section = sectionBox("⚔️ Proficiency Bonus steigt!");
        Label lbl = new Label("Dein Proficiency Bonus erhöht sich von +" + oldPb + " auf +" + newPb + ".");
        lbl.setStyle("-fx-font-size: 13px; -fx-text-fill: #2d5a3d; -fx-font-weight: bold;");
        section.getChildren().add(lbl);
        contentBox.getChildren().add(section);
    }

    // ════════════════════════════════════════════════════════════════════════
    // CONFIRM / SAVE
    // ════════════════════════════════════════════════════════════════════════

    private void setupButtons() {
        btnCancel.setOnAction(e -> ((Stage) btnCancel.getScene().getWindow()).close());
        btnConfirm.setOnAction(e -> tryConfirm());
    }

    private void tryConfirm() {
        if (chosenHp == 0) { showWarn("Bitte HP-Option wählen."); return; }

        if (needsSubclass && chosenSubclass == null) {
            showWarn("Bitte wähle eine Subklasse."); return;
        }

        if (needsAsiOrFeat) {
            if (!usingFeat) {
                if (asiAbility1 == null) { showWarn("Bitte Attribut für ASI wählen."); return; }
                if (asiBonus2 != null && asiAbility2 == null) {
                    showWarn("Bitte zweites Attribut wählen."); return;
                }
                if (asiBonus2 != null && asiAbility1.equals(asiAbility2)) {
                    showWarn("Wähle zwei verschiedene Attribute."); return;
                }
            } else {
                if (chosenFeat == null) { showWarn("Bitte ein Talent wählen."); return; }
            }
        }

        if (newCantripsChosen.size() != neededNewCantrips) {
            showWarn("Bitte " + neededNewCantrips + " Cantrip(s) wählen."); return;
        }
        if (newSpellsChosen.size() != neededNewSpells) {
            showWarn("Bitte " + neededNewSpells + " Zauber wählen."); return;
        }
        // Replacement validation: wenn alter Zauber gewählt aber kein neuer → Fehler
        if (!replacedSpells.isEmpty() && replacementNewSpells.isEmpty()) {
            showWarn("Bitte wähle einen Ersatz-Zauber für '" + replacedSpells.get(0) + "'."); return;
        }

        // Neues max HP berechnen
        int oldMaxHp = character.getMaxHp() > 0 ? character.getMaxHp()
                : dbManager.getCharacterMaxHp(character.getDbId());
        if (oldMaxHp == 0) {
            int hitDie = character.getClassHitDie() == 0 ? 8 : character.getClassHitDie();
            int conMod = (character.getConstitution() - 10) / 2;
            oldMaxHp = hitDie + conMod + (character.getLevel() - 1) * ((hitDie / 2 + 1) + conMod);
        }
        int newMaxHp = oldMaxHp + chosenHp;

        // Alle neuen Zauber (Level-Up + Replacement) zusammenführen
        List<String> allNewSpells = new ArrayList<>(newSpellsChosen);
        allNewSpells.addAll(replacementNewSpells);

        dbManager.saveLevelUp(
                character.getDbId(), newLevel, newMaxHp,
                chosenSubclass,
                usingFeat ? null : asiAbility1,
                usingFeat ? 0 : asiBonus1,
                usingFeat ? null : asiAbility2,
                usingFeat ? null : asiBonus2,
                usingFeat ? chosenFeat : null,
                allNewSpells, newCantripsChosen, replacedSpells
        );

        // Model aktualisieren
        character.setLevel(newLevel);
        character.setMaxHp(newMaxHp);
        if (chosenSubclass != null) character.setSubclassName(chosenSubclass);
        if (usingFeat && chosenFeat != null) character.addFeat(chosenFeat);

        Alert ok = new Alert(Alert.AlertType.INFORMATION);
        ok.setHeaderText("Level Up erfolgreich!");
        ok.setContentText(character.getName() + " ist jetzt Level " + newLevel + ".");
        ok.showAndWait();

        ((Stage) btnConfirm.getScene().getWindow()).close();
        if (onCompletionCallback != null) onCompletionCallback.run();
    }

    // ════════════════════════════════════════════════════════════════════════
    // HELPERS
    // ════════════════════════════════════════════════════════════════════════

    private boolean isHillDwarf() {
        return character.getRace() != null && "Hill Dwarf".equals(character.getRace().getName());
    }

    private VBox sectionBox(String title) {
        VBox box = new VBox(10);
        box.setStyle("-fx-background-color: #FFFBED; -fx-border-color: #D4AF37 #8A6A20 #8A6A20 #D4AF37; -fx-border-width: 2; -fx-border-radius: 10; -fx-background-radius: 10; -fx-padding: 14; -fx-effect: dropshadow(three-pass-box, rgba(60,40,10,0.2), 6, 0, 0, 2);");
        Label lbl = new Label(title);
        lbl.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: #6B0000; -fx-font-family: 'Serif';");
        box.getChildren().add(lbl);
        return box;
    }

    private VBox featureCard(String name, String desc) {
        VBox card = new VBox(4);
        card.setStyle("-fx-background-color: white; -fx-border-color: #C6A664; -fx-border-radius: 6; -fx-background-radius: 6; -fx-padding: 8 10;");
        Label n = new Label(name);
        n.setStyle("-fx-font-size: 13px; -fx-font-weight: bold; -fx-text-fill: #8B0000;");
        Label d = new Label(desc != null ? desc : "");
        d.setStyle("-fx-font-size: 11px; -fx-text-fill: #333;");
        d.setWrapText(true);
        card.getChildren().addAll(n, d);
        return card;
    }

    private void showWarn(String msg) {
        Alert a = new Alert(Alert.AlertType.WARNING);
        a.setHeaderText(null);
        a.setContentText(msg);
        a.showAndWait();
    }

    private String styleBtnDefault() {
        return "-fx-background-color: #FFFBED; -fx-border-color: #C6A664; -fx-border-width: 2; -fx-border-radius: 8; -fx-background-radius: 8; -fx-text-fill: #5a3a1a; -fx-font-size: 13px; -fx-cursor: hand; -fx-padding: 6 14;";
    }

    private String styleBtnSelected() {
        return "-fx-background-color: #2d5a3d; -fx-border-color: #1d3d28; -fx-border-width: 2; -fx-border-radius: 8; -fx-background-radius: 8; -fx-text-fill: #FFF8E2; -fx-font-size: 13px; -fx-font-weight: bold; -fx-cursor: hand; -fx-padding: 6 14;";
    }
}