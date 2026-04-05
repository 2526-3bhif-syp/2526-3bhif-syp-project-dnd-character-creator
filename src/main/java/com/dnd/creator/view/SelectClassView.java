package com.dnd.creator.view;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.stage.Stage;

import java.io.IOException;

public class SelectClassView {
    private Parent root;

    @FXML
    private Button btnBack;

    public SelectClassView() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/dnd/creator/view/SelectClassView.fxml"));
            loader.setController(this);
            root = loader.load();

            if (btnBack != null) {
                btnBack.setOnAction(e -> {
                    // Navigate back to Step 1
                    CreateCharacterView wizardView = new CreateCharacterView();
                    Stage stage = (Stage) root.getScene().getWindow();
                    stage.setScene(new Scene(wizardView.getRoot(), stage.getScene().getWidth(), stage.getScene().getHeight()));
                });
            }
        } catch (IOException e) {
            throw new RuntimeException("Failed to load SelectClassView.fxml", e);
        }
    }

    public Parent getRoot() {
        return root;
    }
}

