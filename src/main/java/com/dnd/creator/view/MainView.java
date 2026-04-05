package com.dnd.creator.view;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import java.io.IOException;

public class MainView {
    private Parent root;
    private Runnable onShowOverviewRequested;

    public MainView() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/dnd/creator/view/MainView.fxml"));
            loader.setController(this);
            root = loader.load();
        } catch (IOException e) {
            throw new RuntimeException("Failed to load MainView.fxml", e);
        }
    }

    @FXML
    private void handleShowOverview() {
        if (onShowOverviewRequested != null) {
            onShowOverviewRequested.run();
        }
    }

    public void setOnShowOverviewRequested(Runnable callback) {
        this.onShowOverviewRequested = callback;
    }

    public Parent getRoot() {
        return root;
    }
}
