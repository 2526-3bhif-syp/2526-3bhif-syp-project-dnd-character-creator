package com.dnd.creator.presenter;

import com.dnd.creator.view.CharactersOverviewView;
import com.dnd.creator.view.CreateCharacterView;
import com.dnd.creator.view.MainView;
import javafx.stage.Stage;

public class MainPresenter {
    private MainView mainView;
    private Stage stage;

    public MainPresenter(MainView mainView, Stage stage) {
        this.mainView = mainView;
        this.stage = stage;

        this.mainView.setOnCreateRequested(this::showCreateStep);
        this.mainView.setOnShowOverviewRequested(this::showOverview);
    }

    private void showCreateStep() {
        com.dnd.creator.model.CharacterSession.getInstance().reset();
        CreateCharacterView createCharacterView = new CreateCharacterView();
        stage.getScene().setRoot(createCharacterView.getRoot());
    }

    private void showOverview() {
        CharactersOverviewView overviewView = new CharactersOverviewView();
        overviewView.setOnBackRequested(this::showMain);
        new CharactersOverviewPresenter(overviewView);

        stage.getScene().setRoot(overviewView.getRoot());
    }

    private void showMain() {
        MainView newMainView = new MainView();
        new MainPresenter(newMainView, stage);
        stage.getScene().setRoot(newMainView.getRoot());
    }
}
