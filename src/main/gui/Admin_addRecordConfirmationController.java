package main.gui;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.stage.Stage;

public class Admin_addRecordConfirmationController {

    @FXML private Label titleLabel;
    @FXML private Label contentLabel;

    private Stage dialogStage;

    /**
     * Call this to set the stage for this dialog
     */
    public void setDialogStage(Stage dialogStage) {
        this.dialogStage = dialogStage;
    }

    /**
     * Call this from your form controllers to set the text
     */
    public void setData(String title, String content) {
        titleLabel.setText(title);
        contentLabel.setText(content);
    }

    /**
     * Handles the "Got It!" button click
     */
    @FXML
    private void handleClose() {
        if (dialogStage != null) {
            dialogStage.close();
        }
    }
}