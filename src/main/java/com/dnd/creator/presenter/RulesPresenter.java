package com.dnd.creator.presenter;

import com.dnd.creator.data.DbManager;
import com.dnd.creator.view.RulesView;
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
        showWelcomeMessage();
    }

    private void initTreeView() {
        TreeItem<String> rootItem = new TreeItem<>("DnD Codex");
        rootItem.setExpanded(true);

        TreeItem<String> coreRulesItem = new TreeItem<>("Grundregeln");
        coreRulesItem.getChildren().addAll(
                new TreeItem<>("Wie man spielt / Der d20"),
                new TreeItem<>("Vorteil & Nachteil"),
                new TreeItem<>("Attributsmodifikatoren"),
                new TreeItem<>("Kampf & Erholung")
        );
        rootItem.getChildren().add(coreRulesItem);

        view.getRulesTree().setRoot(rootItem);
        view.getRulesTree().setShowRoot(false);
    }

    private void showWelcomeMessage() {
        VBox container = view.getContentPane();
        container.getChildren().clear();

        Label title = new Label("Willkommen im Codex");
        title.getStyleClass().add("rules-title");

        Label intro = new Label("Wähle links ein Thema aus den Kategorien aus, um die Regeln, Attribute, Fertigkeiten, Klassen und Völker nachzuschlagen.");
        intro.getStyleClass().add("rules-body");
        intro.setWrapText(true);

        container.getChildren().addAll(title, intro);
    }
}
