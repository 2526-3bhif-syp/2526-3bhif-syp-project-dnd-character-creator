package com.dnd.creator;
import com.dnd.creator.data.DbManager;
import com.dnd.creator.model.CharacterModel;
import com.dnd.creator.presenter.MainPresenter;
import com.dnd.creator.view.MainView;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.util.List;

public class Main extends Application {
    @Override
    public void start(Stage stage) {
        MainView view = new MainView();
        new MainPresenter(view, stage);

        Scene scene = new Scene(view.getRoot(), 800, 600);
        stage.setTitle("D&D Character Creator");
        stage.setScene(scene);
        stage.setMaximized(true);
        stage.show();
    }
        public static void main(String[] args) {

        Application.launch(args);




        }



}
