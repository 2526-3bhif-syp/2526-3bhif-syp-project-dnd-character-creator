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

    @FXML private HBox stepperContainer;
    @FXML private VBox previewContainer;
    @FXML private VBox alignmentGrid;
    @FXML private Button btnBack;
    @FXML private Button btnNext;

    private Parent root;
    private final DbManager dbManager = new DbManager();
    private final CharacterPreviewPanel preview = new CharacterPreviewPanel();
    private String selectedAlignment = null;
    private Button selectedButton = null;

    public AlignmentView() {
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/com/dnd/creator/view/AlignmentView.fxml"));
            loader.setController(this);
            root = loader.load();
            CreationStyles.attach(root);

            dbManager.connect();

            stepperContainer.getChildren().setAll(new StepperBar(6).getRoot());
            previewContainer.getChildren().setAll(preview.getRoot());

            buildGrid();
            restoreSaved();
            setupButtons();
        } catch (IOException e) {
            throw new RuntimeException("Failed to load AlignmentView.fxml", e);
        }
    }

    private void buildGrid() {
        List<String> alignments = dbManager.getAlignments();
        for (int row = 0; row < 3; row++) {
            HBox hbox = new HBox(20);
            hbox.setAlignment(Pos.CENTER);
            for (int col = 0; col < 3; col++) {
                int idx = row * 3 + col;
                if (idx >= alignments.size()) break;
                String name = alignments.get(idx);
                Button btn = new Button(name);
                btn.getStyleClass().add("alignment-btn");
                btn.setWrapText(true);
                btn.setTextAlignment(javafx.scene.text.TextAlignment.CENTER);
                btn.setOnAction(e -> selectAlignment(name, btn));
                hbox.getChildren().add(btn);
            }
            alignmentGrid.getChildren().add(hbox);
        }
    }

    private void selectAlignment(String name, Button btn) {
        if (selectedButton != null) {
            selectedButton.getStyleClass().remove("alignment-btn-selected");
        }
        selectedAlignment = name;
        selectedButton = btn;
        btn.getStyleClass().add("alignment-btn-selected");
    }

    private void restoreSaved() {
        String saved = CharacterSession.getInstance().getCurrentCharacter().getAlignment();
        if (saved == null || saved.isBlank()) return;
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
            stage.setScene(new Scene(new EquipmentView().getRoot(),
                    stage.getScene().getWidth(), stage.getScene().getHeight()));
        });

        btnNext.setOnAction(e -> {
            if (selectedAlignment == null) {
                Alert alert = new Alert(Alert.AlertType.WARNING);
                alert.setHeaderText(null);
                alert.setContentText("Bitte wähle eine Gesinnung aus!");
                alert.showAndWait();
                return;
            }
            CharacterSession.getInstance().getCurrentCharacter().setAlignment(selectedAlignment);
            Stage stage = (Stage) root.getScene().getWindow();
            String classIdx = CharacterSession.getInstance().getCurrentCharacter().getClassIndex();
            if (SpellSelectionView.isSpellcaster(classIdx)) {
                stage.setScene(new Scene(new SpellSelectionView().getRoot(),
                        stage.getScene().getWidth(), stage.getScene().getHeight()));
            } else {
                stage.setScene(new Scene(new CharacterSummaryView().getRoot(),
                        stage.getScene().getWidth(), stage.getScene().getHeight()));
            }
        });
    }

    public Parent getRoot() { return root; }
}
