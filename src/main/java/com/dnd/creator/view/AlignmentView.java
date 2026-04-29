package com.dnd.creator.view;

import com.dnd.creator.data.DbManager;
import com.dnd.creator.model.CharacterSession;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.List;

public class AlignmentView {

    @FXML private VBox alignmentGrid;
    @FXML private Button btnBack;
    @FXML private Button btnNext;

    private Parent root;
    private final DbManager dbManager = new DbManager();
    private String selectedAlignment = null;
    private Button selectedButton = null;

    private static final String BTN_DEFAULT =
            "-fx-pref-width: 200; -fx-pref-height: 80; " +
                    "-fx-border-color: #C6A664; -fx-border-width: 2; -fx-border-radius: 10; " +
                    "-fx-background-radius: 10; -fx-background-color: transparent; " +
                    "-fx-font-size: 14px; -fx-font-weight: bold; -fx-text-fill: #8B0000; -fx-cursor: hand;";

    private static final String BTN_SELECTED =
            "-fx-pref-width: 200; -fx-pref-height: 80; " +
                    "-fx-border-color: #C6A664; -fx-border-width: 2; -fx-border-radius: 10; " +
                    "-fx-background-radius: 10; -fx-background-color: #8B0000; " +
                    "-fx-font-size: 14px; -fx-font-weight: bold; -fx-text-fill: #F5F5DC; -fx-cursor: hand;";

    public AlignmentView() {
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/com/dnd/creator/view/AlignmentView.fxml"));
            loader.setController(this);
            root = loader.load();

            dbManager.connect();
            buildGrid();
            restoreSaved();
            setupButtons();
        } catch (IOException e) {
            throw new RuntimeException("Failed to load AlignmentView.fxml", e);
        }
    }

    private void buildGrid() {
        List<String> alignments = dbManager.getAlignments();
        // 3x3 Grid
        for (int row = 0; row < 3; row++) {
            HBox hbox = new HBox(20);
            hbox.setAlignment(Pos.CENTER);
            for (int col = 0; col < 3; col++) {
                int idx = row * 3 + col;
                if (idx >= alignments.size()) break;
                String name = alignments.get(idx);
                Button btn = new Button(name);
                btn.setStyle(BTN_DEFAULT);
                btn.setWrapText(true);
                btn.setTextAlignment(javafx.scene.text.TextAlignment.CENTER);
                btn.setOnAction(e -> selectAlignment(name, btn));
                hbox.getChildren().add(btn);
            }
            alignmentGrid.getChildren().add(hbox);
        }
    }

    private void selectAlignment(String name, Button btn) {
        if (selectedButton != null) selectedButton.setStyle(BTN_DEFAULT);
        selectedAlignment = name;
        selectedButton = btn;
        btn.setStyle(BTN_SELECTED);
    }

    private void restoreSaved() {
        String saved = CharacterSession.getInstance()
                .getCurrentCharacter().getAlignment();
        if (saved == null || saved.equals("Neutral")) return;
        alignmentGrid.getChildren().forEach(row -> {
            if (row instanceof HBox hbox) {
                hbox.getChildren().forEach(node -> {
                    if (node instanceof Button btn && btn.getText().equals(saved)) {
                        selectAlignment(saved, btn);
                    }
                });
            }
        });
    }

    private void setupButtons() {
        btnBack.setOnAction(e -> {
            Stage stage = (Stage) root.getScene().getWindow();
            EquipmentView prev = new EquipmentView();
            stage.setScene(new Scene(prev.getRoot(),
                    stage.getScene().getWidth(), stage.getScene().getHeight()));
        });

        btnNext.setOnAction(e -> {
            if (selectedAlignment == null) {
                Alert alert = new Alert(Alert.AlertType.WARNING);
                alert.setTitle("Keine Gesinnung");
                alert.setHeaderText(null);
                alert.setContentText("Bitte wähle eine Gesinnung aus!");
                alert.showAndWait();
                return;
            }
            CharacterSession.getInstance().getCurrentCharacter()
                    .setAlignment(selectedAlignment);
            Stage stage = (Stage) root.getScene().getWindow();
            CharacterSummaryView next = new CharacterSummaryView();
            stage.setScene(new Scene(next.getRoot(),
                    stage.getScene().getWidth(), stage.getScene().getHeight()));
        });
    }

    public Parent getRoot() { return root; }
}