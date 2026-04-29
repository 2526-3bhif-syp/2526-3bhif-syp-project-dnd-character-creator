package com.dnd.creator.view;

import com.dnd.creator.data.DbManager;
import com.dnd.creator.model.CharacterSession;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class EquipmentView {

    @FXML private HBox stepperContainer;
    @FXML private VBox previewContainer;
    @FXML private VBox mandatoryContainer;
    @FXML private VBox choicesContainer;
    @FXML private Button btnBack;
    @FXML private Button btnNext;

    private Parent root;
    private final DbManager dbManager = new DbManager();
    private final CharacterPreviewPanel preview = new CharacterPreviewPanel();
    /** orderNum -> selected entry (e.g. "A) chain mail") */
    private final Map<Integer, String> selectedChoices = new LinkedHashMap<>();
    /** orderNum -> list of bundle card nodes for visual update */
    private final Map<Integer, List<VBox>> bundleCardsByOrder = new LinkedHashMap<>();
    private int totalChoiceBlocks = 0;

    public EquipmentView() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/dnd/creator/view/EquipmentView.fxml"));
            loader.setController(this);
            root = loader.load();
            CreationStyles.attach(root);

            dbManager.connect();

            stepperContainer.getChildren().setAll(new StepperBar(5).getRoot());
            previewContainer.getChildren().setAll(preview.getRoot());

            buildContent();
            wireNavigation();
            updateNextButton();

        } catch (IOException e) {
            throw new RuntimeException("Failed to load EquipmentView.fxml", e);
        }
    }

    public Parent getRoot() {
        return root;
    }

    private void buildContent() {
        var character = CharacterSession.getInstance().getCurrentCharacter();
        String classIndex = character.getClassIndex();

        if (classIndex == null || classIndex.isBlank()) {
            mandatoryContainer.getChildren().add(makeMutedLine("Keine Klasse gewählt — bitte gehe zurück zu Schritt 2."));
            return;
        }

        // Mandatory items
        List<String> mandatory = dbManager.getStartingEquipment(classIndex);
        java.util.Set<String> uniqueMandatory = new java.util.LinkedHashSet<>(mandatory);
        if (uniqueMandatory.isEmpty()) {
            mandatoryContainer.getChildren().add(makeMutedLine("Keine Pflicht-Ausrüstung."));
        } else {
            for (String item : uniqueMandatory) {
                if (item == null || item.isBlank()) continue;
                mandatoryContainer.getChildren().add(makeMandatoryRow(item));
            }
        }

        // Choices
        List<Map<String, Object>> options = dbManager.getEquipmentOptions(classIndex);
        Map<Integer, String> uniqueDescriptions = new LinkedHashMap<>();
        for (Map<String, Object> option : options) {
            int orderNum = (Integer) option.get("order_num");
            uniqueDescriptions.putIfAbsent(orderNum, (String) option.get("description"));
        }
        totalChoiceBlocks = uniqueDescriptions.size();

        // Restore prior selections (matched by saved entry strings)
        List<String> previouslySelected = character.getSelectedEquipment();

        int blockIndex = 0;
        for (Map.Entry<Integer, String> entry : uniqueDescriptions.entrySet()) {
            int orderNum = entry.getKey();
            String description = entry.getValue();
            List<String> bundles = parseBundles(description);

            VBox block = buildChoiceBlock(blockIndex + 1, orderNum, bundles, previouslySelected);
            choicesContainer.getChildren().add(block);
            blockIndex++;
        }
    }

    private Label makeMutedLine(String text) {
        Label l = new Label(text);
        l.getStyleClass().add("muted");
        return l;
    }

    private HBox makeMandatoryRow(String item) {
        Label bullet = new Label("✓");
        bullet.setStyle("-fx-text-fill: #4CAF50; -fx-font-weight: bold; -fx-font-size: 13px;");
        Label text = new Label(item);
        text.getStyleClass().add("mandatory-item");
        text.setWrapText(true);
        HBox row = new HBox(8, bullet, text);
        row.setAlignment(Pos.CENTER_LEFT);
        return row;
    }

    private List<String> parseBundles(String description) {
        // Description is "option_a or option_b or option_c" from DbManager.
        // Each option may have a leading "(a)" prefix in source data — strip it.
        List<String> result = new ArrayList<>();
        if (description == null) return result;
        for (String part : description.split(" or ")) {
            String cleaned = part.trim();
            cleaned = cleaned.replaceFirst("^\\([a-z]\\)\\s*", "");
            if (!cleaned.isEmpty()) result.add(cleaned);
        }
        return result;
    }

    private VBox buildChoiceBlock(int blockNumber, int orderNum, List<String> bundles, List<String> previouslySelected) {
        VBox block = new VBox(8);

        Label title = new Label("Wahl " + blockNumber + " — wähle eines:");
        title.getStyleClass().add("choice-block-title");

        HBox cardsRow = new HBox(12);
        cardsRow.setAlignment(Pos.TOP_LEFT);

        List<VBox> cards = new ArrayList<>();
        for (int i = 0; i < bundles.size(); i++) {
            String letter = String.valueOf((char) ('A' + i));
            String bundleText = bundles.get(i);
            String entry = letter + ") " + bundleText;

            VBox card = buildBundleCard(letter, bundleText, orderNum, entry);
            cards.add(card);
            HBox.setHgrow(card, Priority.ALWAYS);
            cardsRow.getChildren().add(card);

            if (previouslySelected != null && previouslySelected.contains(entry)) {
                applyBundleSelection(orderNum, entry, cards);
            }
        }
        bundleCardsByOrder.put(orderNum, cards);

        block.getChildren().addAll(title, cardsRow);
        return block;
    }

    private VBox buildBundleCard(String letter, String bundleText, int orderNum, String entry) {
        VBox card = new VBox(6);
        card.getStyleClass().add("bundle-card");
        card.setAlignment(Pos.TOP_LEFT);
        card.setMinWidth(200);

        Label letterLabel = new Label(letter);
        letterLabel.getStyleClass().add("bundle-letter");

        Label content = new Label(bundleText);
        content.getStyleClass().add("bundle-text");
        content.setWrapText(true);

        Tooltip.install(card, new Tooltip(letter + ") " + bundleText));

        card.getChildren().addAll(letterLabel, content);
        card.setOnMouseClicked(e -> {
            applyBundleSelection(orderNum, entry, bundleCardsByOrder.get(orderNum));
            saveToSession();
            preview.refresh();
            updateNextButton();
        });
        return card;
    }

    private void applyBundleSelection(int orderNum, String entry, List<VBox> cardsForBlock) {
        selectedChoices.put(orderNum, entry);
        if (cardsForBlock == null) return;
        char chosenLetter = entry.charAt(0);
        for (int i = 0; i < cardsForBlock.size(); i++) {
            VBox c = cardsForBlock.get(i);
            c.getStyleClass().remove("bundle-card-selected");
            if ((char) ('A' + i) == chosenLetter) {
                c.getStyleClass().add("bundle-card-selected");
            }
        }
    }

    private void saveToSession() {
        // Persist selected choices in orderNum order so Step 6 sees stable ordering.
        List<String> entries = new ArrayList<>(selectedChoices.values());
        CharacterSession.getInstance().getCurrentCharacter().setSelectedEquipment(entries);
    }

    private void updateNextButton() {
        boolean valid = totalChoiceBlocks == 0 || selectedChoices.size() == totalChoiceBlocks;
        btnNext.setDisable(!valid);
        if (!valid) {
            int missing = totalChoiceBlocks - selectedChoices.size();
            Tooltip.install(btnNext, new Tooltip("Bitte triff noch " + missing + " Auswahl(en)."));
        } else {
            Tooltip.uninstall(btnNext, btnNext.getTooltip());
        }
    }

    private void wireNavigation() {
        btnBack.setOnAction(e -> {
            SkillsView prev = new SkillsView();
            Stage stage = (Stage) root.getScene().getWindow();
            stage.setScene(new Scene(prev.getRoot(), stage.getScene().getWidth(), stage.getScene().getHeight()));
        });
        btnNext.setOnAction(e -> {
            saveToSession();
            AlignmentView next = new AlignmentView();
            Stage stage = (Stage) root.getScene().getWindow();
            stage.setScene(new Scene(next.getRoot(), stage.getScene().getWidth(), stage.getScene().getHeight()));
        });
    }
}
