package com.dnd.creator;

import com.dnd.creator.presenter.MainPresenter;
import com.dnd.creator.view.MainView;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {
    @Override
    public void start(Stage stage) {
        MainView mainView = new MainView();
        new MainPresenter(mainView, stage);

        Scene scene = new Scene(mainView.getRoot(), 400, 300);
        stage.setTitle("D&D Character Creator");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
