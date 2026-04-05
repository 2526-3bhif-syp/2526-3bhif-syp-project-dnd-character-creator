package com.dnd.creator.view;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.scene.control.Button;
import javafx.fxml.FXML;
import java.io.IOException;
public class MainView {
    private Parent root;
    @FXML private Button btnCreate;
    public MainView() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/dnd/creator/view/MainView.fxml"));
            loader.setController(this);
            root = loader.load();
            if (btnCreate != null) {
                btnCreate.setOnAction(e -> {
                    CreateCharacterView createView = new CreateCharacterView();
                    Stage stage = (Stage) root.getScene().getWindow();
                    stage.setScene(new Scene(createView.getRoot(), stage.getScene().getWidth(), stage.getScene().getHeight()));
                });
            }
        } catch (IOException e) {
            throw new RuntimeException("Failed to load MainView.fxml", e);
        }
    }
    public Parent getRoot() { return root; }
}
