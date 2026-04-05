package com.dnd.creator.view;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.layout.FlowPane;

import java.io.IOException;

public class CharactersOverviewView {
    @FXML
    private FlowPane cardsPane;
    @FXML
    private Label emptyLabel;

    private Parent root;

    public CharactersOverviewView() {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/dnd/creator/view/CharactersOverview.fxml"));
        loader.setController(this);
        try {
            root = loader.load();
        } catch (IOException e) {
            throw new RuntimeException("Failed to load CharactersOverview.fxml", e);
        }
    }

    public Parent getRoot() {
        return root;
    }

    public FlowPane getCardsPane() {
        return cardsPane;
    }

    public void setEmptyMessageVisible(boolean visible) {
        emptyLabel.setVisible(visible);
        emptyLabel.setManaged(visible);
    }
}
