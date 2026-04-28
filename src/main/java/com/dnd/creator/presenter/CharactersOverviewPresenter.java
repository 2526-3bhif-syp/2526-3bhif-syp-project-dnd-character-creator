package com.dnd.creator.presenter;

import com.dnd.creator.data.DbManager;
import com.dnd.creator.model.CharacterModel;
import com.dnd.creator.view.CharacterCardView;
import com.dnd.creator.view.CharacterSheetPopupView;
import com.dnd.creator.view.CharactersOverviewView;

import java.util.List;

public class CharactersOverviewPresenter {
    private List<CharacterModel> characters;
    private CharactersOverviewView view;
    private DbManager dbManager;

    public CharactersOverviewPresenter(CharactersOverviewView view) {
        this.view = view;
        this.dbManager = new DbManager();
        this.dbManager.connect();
        this.characters = dbManager.getAllSavedCharacters();
        updateView();
    }

    private void updateView() {
        if (characters == null || characters.isEmpty()) {
            view.setEmptyMessageVisible(true);
        } else {
            view.setEmptyMessageVisible(false);
            view.getCardsPane().getChildren().clear();
            for (CharacterModel character : characters) {
                CharacterCardView cardView = new CharacterCardView(character);
                cardView.setOnCardClicked(c -> {
                    CharacterSheetPopupView popup = new CharacterSheetPopupView(c);
                    popup.showAsPopup(view.getCardsPane().getScene().getWindow());
                });
                view.getCardsPane().getChildren().add(cardView.getRoot());
            }
        }
    }
}
