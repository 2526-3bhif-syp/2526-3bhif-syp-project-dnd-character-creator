package com.dnd.creator.presenter;
import com.dnd.creator.model.CharacterModel;
import com.dnd.creator.view.MainView;
public class MainPresenter {
    private CharacterModel model;
    private MainView view;
    public MainPresenter(CharacterModel model, MainView view) {
        this.model = model;
        this.view = view;
    }
}
