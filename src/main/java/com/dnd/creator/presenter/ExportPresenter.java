package com.dnd.creator.presenter;

import com.dnd.creator.export.CharacterPdfExporter;
import com.dnd.creator.model.CharacterModel;
import javafx.scene.control.Alert;
import javafx.stage.FileChooser;
import javafx.stage.Window;

import java.io.File;

/**
 * Drives the "export character to PDF" use case: asks the user where to save the
 * file, runs the {@link CharacterPdfExporter}, and reports success or failure.
 * Views stay passive and delegate their Export button to this presenter.
 */
public class ExportPresenter {

    private final CharacterPdfExporter exporter = new CharacterPdfExporter();

    /**
     * Prompt for a destination and export {@code character} to a PDF there.
     *
     * @param character the character to export (must be non-null)
     * @param owner     the window that owns the save dialog (may be null)
     */
    public void exportToPdf(CharacterModel character, Window owner) {
        if (character == null) {
            showError("Kein Charakter zum Exportieren ausgewählt.");
            return;
        }

        FileChooser chooser = new FileChooser();
        chooser.setTitle("Charakter als PDF exportieren");
        chooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("PDF-Datei (*.pdf)", "*.pdf"));
        chooser.setInitialFileName(suggestedFileName(character));

        File target = chooser.showSaveDialog(owner);
        if (target == null) {
            // User cancelled — write nothing, say nothing.
            return;
        }
        if (!target.getName().toLowerCase().endsWith(".pdf")) {
            target = new File(target.getParentFile(), target.getName() + ".pdf");
        }

        try {
            exporter.export(character, target);
            showInfo("Charakter wurde als PDF gespeichert:\n" + target.getAbsolutePath());
        } catch (Exception ex) {
            // Leave no half-written file behind.
            if (target.exists() && target.length() == 0) {
                target.delete();
            }
            showError("Der Charakter konnte nicht exportiert werden.\n"
                    + ex.getClass().getSimpleName() + ": " + ex.getMessage());
        }
    }

    private String suggestedFileName(CharacterModel character) {
        String name = character.getName();
        if (name == null || name.isBlank()) name = "character";
        String safe = name.replaceAll("[^a-zA-Z0-9-_ ]", "").trim();
        if (safe.isBlank()) safe = "character";
        return safe + ".pdf";
    }

    private void showInfo(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Export erfolgreich");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Export fehlgeschlagen");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
