package com.dnd.creator.presenter;

import com.dnd.creator.model.CharacterModel;
import com.dnd.creator.view.CharacterCardView;
import com.dnd.creator.view.CharactersOverviewView;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class CharactersOverviewPresenter {
    private List<CharacterModel> characters;
    private CharactersOverviewView view;

    public CharactersOverviewPresenter(CharactersOverviewView view) {
        this.view = view;
        this.characters = loadCharacters();
        updateView();
    }

    private List<CharacterModel> loadCharacters() {
        try (var is = getClass().getResourceAsStream("/com/dnd/creator/model/characters.json")) {
            if (is == null) return new ArrayList<>();
            try (var reader = new InputStreamReader(is)) {
                return new Gson().fromJson(reader, new TypeToken<List<CharacterModel>>() {}.getType());
            }
        } catch (Exception e) {
            return new ArrayList<>();
        }
    }

    private void updateView() {
        if (characters == null || characters.isEmpty()) {
            view.setEmptyMessageVisible(true);
        } else {
            view.setEmptyMessageVisible(false);
            view.getCardsPane().getChildren().clear();
            for (CharacterModel character : characters) {
                CharacterCardView cardView = new CharacterCardView(character);
                view.getCardsPane().getChildren().add(cardView.getRoot());
            }
        }
    }
}
