package com.dnd.creator.view;

import javafx.scene.Parent;

final class CreationStyles {
    private static final String SHEET = "/com/dnd/creator/view/character-creation.css";

    private CreationStyles() {}

    static void attach(Parent root) {
        var url = CreationStyles.class.getResource(SHEET);
        if (url == null) return;
        String href = url.toExternalForm();
        if (!root.getStylesheets().contains(href)) {
            root.getStylesheets().add(href);
        }
    }
}
