package com.dnd.creator.view;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.stage.Stage;
import java.io.IOException;
public class EquipmentView {
    private Parent root;
    @FXML private Button btnBack;
    @FXML private Button btnNext;
    public EquipmentView() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/dnd/creator/view/EquipmentView.fxml"));
            loader.setController(this);
            root = loader.load();
            if (btnBack != null) {
                btnBack.setOnAction(e -> {
                    SkillsView prevView = new SkillsView();
                    Stage stage = (Stage) root.getScene().getWindow();
                    stage.setScene(new Scene(prevView.getRoot(), stage.getScene().getWidth(), stage.getScene().getHeight()));
                });
            }
            if (btnNext != null) {
                btnNext.setOnAction(e -> {
                    CharacterSummaryView nextView = new CharacterSummaryView();
                    Stage stage = (Stage) root.getScene().getWindow();
                    stage.setScene(new Scene(nextView.getRoot(), stage.getScene().getWidth(), stage.getScene().getHeight()));
                });
            }
        } catch (IOException e) {
            throw new RuntimeException("Failed to load EquipmentView.fxml", e);
        }
    }
    public Parent getRoot() { return root; }
}
