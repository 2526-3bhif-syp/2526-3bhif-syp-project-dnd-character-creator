package com.dnd.creator.view;

import com.dnd.creator.presenter.MainPresenter;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.stage.Stage;

import java.io.IOException;

public class CreateCharacterView {
    private Parent root;

    @FXML
    private Button btnBack;

    @FXML
    private Button btnNext;

    public CreateCharacterView() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/dnd/creator/view/CreateCharacterView.fxml"));
            loader.setController(this);
            root = loader.load();

            if (btnBack != null) {
                btnBack.setOnAction(e -> {
                    MainView mainView = new MainView();
                    Stage stage = (Stage) root.getScene().getWindow();
                    new MainPresenter(mainView, stage);
                    stage.setScene(new Scene(mainView.getRoot(), stage.getScene().getWidth(), stage.getScene().getHeight()));
                });
            }

            if (btnNext != null) {
                btnNext.setOnAction(e -> {
                    SelectClassView nextView = new SelectClassView();
                    Stage stage = (Stage) root.getScene().getWindow();
                    stage.setScene(new Scene(nextView.getRoot(), stage.getScene().getWidth(), stage.getScene().getHeight()));
                });
            }
        } catch (IOException e) {
            throw new RuntimeException("Failed to load CreateCharacterView.fxml", e);
        }
    }

    public Parent getRoot() {
        return root;
    }
}
