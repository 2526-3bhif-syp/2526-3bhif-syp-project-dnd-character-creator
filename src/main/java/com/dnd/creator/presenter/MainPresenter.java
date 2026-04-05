package com.dnd.creator.presenter;

import com.dnd.creator.view.CharactersOverviewView;
import com.dnd.creator.view.MainView;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class MainPresenter {
    private MainView mainView;
    private Stage stage;

    public MainPresenter(MainView mainView, Stage stage) {
        this.mainView = mainView;
        this.stage = stage;
        
        this.mainView.setOnShowOverviewRequested(this::showOverview);
    }

    private void showOverview() {
        CharactersOverviewView overviewView = new CharactersOverviewView();
        new CharactersOverviewPresenter(overviewView);
        
        Scene scene = new Scene(overviewView.getRoot(), 800, 600);
        stage.setScene(scene);
    }
}
