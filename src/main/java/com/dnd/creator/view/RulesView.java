package com.dnd.creator.view;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.TreeView;
import javafx.scene.layout.VBox;

import java.io.IOException;

public class RulesView {
    @FXML
    private TreeView<String> rulesTree;
    @FXML
    private VBox contentPane;

    private Parent root;
    private Runnable onBackRequested;

    public RulesView() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/dnd/creator/view/RulesView.fxml"));
            loader.setController(this);
            root = loader.load();
            CreationStyles.attach(root);
        } catch (IOException e) {
            throw new RuntimeException("Failed to load RulesView.fxml", e);
        }
    }

    @FXML
    private void handleBack() {
        if (onBackRequested != null) {
            onBackRequested.run();
        }
    }

    public void setOnBackRequested(Runnable onBackRequested) {
        this.onBackRequested = onBackRequested;
    }

    public Parent getRoot() {
        return root;
    }

    public TreeView<String> getRulesTree() {
        return rulesTree;
    }

    public VBox getContentPane() {
        return contentPane;
    }
}
