package com.dnd.creator.view;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.RadioButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class SkillsView {
    private Parent root;

    @FXML
    private VBox backgroundContainer;

    @FXML
    private VBox skillsContainer;

    @FXML
    private Button btnBack;

    @FXML
    private Button btnNext;

    private ToggleGroup backgroundGroup;
    private String selectedBackground = null;
    private List<String> selectedSkills = new ArrayList<>();
    private static final int MAX_SKILLS = 2;

    private List<String> backgrounds = Arrays.asList(
        "Acolyte", "Criminal", "Folk Hero", "Noble", "Sage", "Soldier"
    );

    private List<String> skills = Arrays.asList(
        "Acrobatics", "Animal Handling", "Arcana", "Athletics",
        "Deception", "History", "Insight", "Intimidation",
        "Investigation", "Medicine", "Nature", "Perception",
        "Performance", "Persuasion", "Religion", "Sleight of Hand",
        "Stealth", "Survival"
    );

    public SkillsView() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/dnd/creator/view/SkillsView.fxml"));
            loader.setController(this);
            root = loader.load();

            initializeBackgrounds();
            initializeSkills();
            setupButtonHandlers();

        } catch (IOException e) {
            throw new RuntimeException("Failed to load SkillsView.fxml", e);
        }
    }

    public Parent getRoot() {
        return root;
    }
