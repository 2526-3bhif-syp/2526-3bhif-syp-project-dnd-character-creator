package com.dnd.creator.view;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.stage.Stage;
import java.io.IOException;
public class CharacterSummaryView {
    private Parent root;
    @FXML private Button btnBack;
    @FXML private Button btnSave;
    public CharacterSummaryView() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/dnd/creator/view/CharacterSummaryView.fxml"));
            loader.setController(this);
            root = loader.load();
            if (btnBack != null) {
                btnBack.setOnAction(e -> {
                    EquipmentView prevView = new EquipmentView();
                    Stage stage = (Stage) root.getScene().getWindow();
                    stage.setScene(new Scene(prevView.getRoot(), stage.getScene().getWidth(), stage.getScene().getHeight()));
                });
            }
            if (btnSave != null) {
                btnSave.setOnAction(e -> {
                    MainView mainView = new MainView();
                    Stage stage = (Stage) root.getScene().getWindow();
                    stage.setScene(new Scene(mainView.getRoot(), stage.getScene().getWidth(), stage.getScene().getHeight()));
                });
            }
        } catch (IOException e) {
            throw new RuntimeException("Failed to load CharacterSummaryView.fxml", e);
        }
    }
    public Parent getRoot() { return root; }
}
