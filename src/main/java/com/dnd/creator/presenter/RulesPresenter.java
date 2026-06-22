package com.dnd.creator.presenter;

import com.dnd.creator.data.DbManager;
import com.dnd.creator.view.RulesView;
import javafx.application.Platform;
import javafx.beans.value.ObservableValue;
import javafx.scene.control.Label;
import javafx.scene.control.TreeItem;
import javafx.scene.layout.VBox;

import java.util.List;
import java.util.Map;

public class RulesPresenter {
    private final RulesView view;
    private final DbManager dbManager;

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

        // Core Rules (static)
        TreeItem<String> coreRulesItem = new TreeItem<>("Grundregeln");
        coreRulesItem.getChildren().addAll(
                new TreeItem<>("Wie man spielt / Der d20"),
                new TreeItem<>("Vorteil & Nachteil"),
                new TreeItem<>("Attributsmodifikatoren"),
                new TreeItem<>("Kampf & Erholung")
        );
        rootItem.getChildren().add(coreRulesItem);

        // Ability Scores
        TreeItem<String> abilitiesItem = new TreeItem<>("Fähigkeiten");
        for (String ability : dbManager.getAllAbilities()) {
            abilitiesItem.getChildren().add(new TreeItem<>(ability));
        }
        rootItem.getChildren().add(abilitiesItem);

        // Skills
        TreeItem<String> skillsItem = new TreeItem<>("Fertigkeiten");
        for (String skill : dbManager.getAllSkills()) {
            skillsItem.getChildren().add(new TreeItem<>(skill));
        }
        rootItem.getChildren().add(skillsItem);

        // Classes
        TreeItem<String> classesItem = new TreeItem<>("Klassen");
        for (String clazz : dbManager.getAllClasses()) {
            classesItem.getChildren().add(new TreeItem<>(clazz));
        }
        rootItem.getChildren().add(classesItem);

        // Races
        TreeItem<String> racesItem = new TreeItem<>("Völker");
        for (String race : dbManager.getAllRaces()) {
            racesItem.getChildren().add(new TreeItem<>(race));
        }
        rootItem.getChildren().add(racesItem);

        view.getRulesTree().setRoot(rootItem);
        view.getRulesTree().setShowRoot(false);

        // Selection listener
        view.getRulesTree().getSelectionModel().selectedItemProperty().addListener((obs, oldSel, newSel) -> {
            if (newSel != null && newSel.getParent() != null) {
                String category = newSel.getParent().getValue();
                String name = newSel.getValue();
                showContent(category, name);
            }
        });    }

    private void showWelcomeMessage() {
        VBox container = view.getContentPane();
        container.getChildren().clear();
        Label title = new Label("Willkommen");
        title.getStyleClass().add("rules-title");
        Label body = new Label("Willkommen im DnD Codex. Wählen Sie einen Eintrag im Baum, um die Details anzuzeigen.");
        body.getStyleClass().add("rules-body");
        body.setWrapText(true);
        container.getChildren().addAll(title, body);
    }

    private void showContent(String category, String name) {
        VBox container = view.getContentPane();
        container.getChildren().clear();
        Label title = new Label(name);
        title.getStyleClass().add("rules-title");
        Label body = new Label();
        body.getStyleClass().add("rules-body");
        body.setWrapText(true);
        switch (category) {
            case "Fähigkeiten":
                body.setText(dbManager.getAbilityDescription(name));
                break;
            case "Fertigkeiten":
                body.setText(dbManager.getSkillDescription(name));
                break;
            case "Klassen":
                var classInfo = dbManager.getClassByName(name);
                if (classInfo != null) {
                    body.setText("Hit Die: " + classInfo.get("hit_die") + "\n" + "Proficiency: " + classInfo.get("proficiencies"));
                }
                break;
            case "Völker":
                var race = dbManager.getRaceByName(name);
                if (race != null) {
                    StringBuilder sb = new StringBuilder();
                    sb.append("Size: ").append(race.getSize()).append("\n");
                    sb.append("Speed: ").append(race.getSpeed()).append("\n");
                    sb.append("Abilities: ").append(race.getAbilityBonuses()).append("\n");
                    sb.append("Languages: ").append(race.getLanguages()).append("\n");
                    sb.append("Traits: ").append(race.getTraits()).append("\n");
                    body.setText(sb.toString());
                }
                break;
            default:
                body.setText("Information not available.");
        }
        container.getChildren().addAll(title, body);
    }
}
