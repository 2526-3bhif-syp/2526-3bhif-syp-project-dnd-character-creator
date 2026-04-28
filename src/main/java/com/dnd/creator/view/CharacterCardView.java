package com.dnd.creator.view;

import com.dnd.creator.model.CharacterModel;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;

import java.io.IOException;
import java.util.function.Consumer;

public class CharacterCardView {
    @FXML
    private ImageView imageView;
    @FXML
    private Label nameLabel;
    @FXML
    private Label strLabel;
    @FXML
    private Label dexLabel;
    @FXML
    private Label conLabel;
    @FXML
    private Label intLabel;
    @FXML
    private Label wisLabel;
    @FXML
    private Label chaLabel;

    private Parent root;
    private Consumer<CharacterModel> onCardClicked;

    private static final String STYLE_DEFAULT =
            "-fx-border-color: #D4AF37; -fx-border-width: 3; -fx-padding: 15; " +
            "-fx-background-color: #FDF5E6; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.5), 10, 0, 4, 4); " +
            "-fx-min-width: 220; -fx-max-width: 220; -fx-min-height: 320; -fx-background-radius: 5; -fx-border-radius: 5;";

    private static final String STYLE_HOVER =
            "-fx-border-color: #D4AF37; -fx-border-width: 3; -fx-padding: 15; " +
            "-fx-background-color: #FDF5E6; -fx-effect: dropshadow(three-pass-box, rgba(139,0,0,0.85), 22, 0.6, 0, 0); " +
            "-fx-min-width: 220; -fx-max-width: 220; -fx-min-height: 320; -fx-background-radius: 5; -fx-border-radius: 5;";

    public CharacterCardView(CharacterModel character) {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/dnd/creator/view/CharacterCard.fxml"));
        loader.setController(this);
        try {
            root = loader.load();
            updateUI(character);
            root.setOnMouseEntered(e -> root.setStyle(STYLE_HOVER));
            root.setOnMouseExited(e -> root.setStyle(STYLE_DEFAULT));
            root.setOnMouseClicked(e -> {
                if (onCardClicked != null) onCardClicked.accept(character);
            });
        } catch (IOException e) {
            throw new RuntimeException("Failed to load CharacterCard.fxml", e);
        }
    }

    private void updateUI(CharacterModel character) {
        if (character == null) return;
        
        nameLabel.setText(character.getName());
        strLabel.setText(String.valueOf(character.getStrength()));
        dexLabel.setText(String.valueOf(character.getDexterity()));
        conLabel.setText(String.valueOf(character.getConstitution()));
        intLabel.setText(String.valueOf(character.getIntelligence()));
        wisLabel.setText(String.valueOf(character.getWisdom()));
        chaLabel.setText(String.valueOf(character.getCharisma()));
        
        // Visual debug for ImageView: set a colored style to see it
        imageView.setStyle("-fx-background-color: #cccccc;");
        
        // Debug output to see what's being loaded
        System.out.println("Card loaded for: " + character.getName() + " STR: " + character.getStrength());
    }

    public Parent getRoot() {
        return root;
    }

    public void setOnCardClicked(Consumer<CharacterModel> callback) {
        this.onCardClicked = callback;
    }
}
