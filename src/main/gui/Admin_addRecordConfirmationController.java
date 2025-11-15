package main.gui;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.stage.Stage;

public class Admin_addRecordConfirmationController {

    @FXML private Label titleLabel;
    @FXML private Label contentLabel;

    private Stage dialogStage;

    public void setDialogStage(Stage dialogStage) {
        this.dialogStage = dialogStage;
    }

    public void setData(String title, String content) {
        titleLabel.setText(title);
        contentLabel.setText(content);
    }

    @FXML private void handleClose() {
        if (dialogStage != null) {
            dialogStage.close();
        }
    }
}