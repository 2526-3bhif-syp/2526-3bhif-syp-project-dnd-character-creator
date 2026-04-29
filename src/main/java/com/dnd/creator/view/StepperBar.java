package com.dnd.creator.view;

import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

public class StepperBar {

    private static final String[] STEP_LABELS = {
        "Identität", "Klasse", "Werte", "Fähigkeiten", "Ausrüstung", "Gesinnung", "Zauber"
    };

    private final HBox root;

    public StepperBar(int currentStep) {
        root = new HBox(0);
        root.getStyleClass().add("stepper-bar");
        root.setAlignment(Pos.CENTER);

        for (int i = 0; i < STEP_LABELS.length; i++) {
            int stepNumber = i + 1;
            root.getChildren().add(buildStep(stepNumber, STEP_LABELS[i], stepNumber, currentStep));
            if (i < STEP_LABELS.length - 1) {
                root.getChildren().add(buildConnector(stepNumber, currentStep));
            }
        }
    }

    public HBox getRoot() {
        return root;
    }

    private VBox buildStep(int number, String text, int stepIndex, int currentStep) {
        StackPane circle = new StackPane();
        circle.getStyleClass().add("stepper-circle");
        Label numberLabel;

        if (stepIndex < currentStep) {
            circle.getStyleClass().add("stepper-circle-done");
            numberLabel = new Label("✓");
        } else if (stepIndex == currentStep) {
            circle.getStyleClass().add("stepper-circle-active");
            numberLabel = new Label(String.valueOf(number));
        } else {
            circle.getStyleClass().add("stepper-circle-future");
            numberLabel = new Label(String.valueOf(number));
        }
        numberLabel.setStyle("-fx-text-fill: inherit; -fx-font-size: 14px; -fx-font-weight: bold;");
        circle.getChildren().add(numberLabel);

        Label nameLabel = new Label(text);
        if (stepIndex == currentStep) {
            nameLabel.getStyleClass().add("stepper-label-active");
        } else if (stepIndex < currentStep) {
            nameLabel.getStyleClass().add("stepper-label-done");
        } else {
            nameLabel.getStyleClass().add("stepper-label");
        }

        VBox box = new VBox(4, circle, nameLabel);
        box.getStyleClass().add("stepper-step");
        box.setAlignment(Pos.CENTER);
        return box;
    }

    private StackPane buildConnector(int stepIndex, int currentStep) {
        Label arrow = new Label("➜");
        arrow.getStyleClass().add("stepper-arrow");
        if (stepIndex < currentStep) {
            arrow.getStyleClass().add("stepper-arrow-done");
        }
        StackPane wrapper = new StackPane(arrow);
        wrapper.getStyleClass().add("stepper-arrow-wrap");
        return wrapper;
    }
}
