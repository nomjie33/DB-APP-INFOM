package main.gui;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.stage.Stage;

public class Client_penaltyDialogController {

    private Stage dialogStage;
    private boolean resolveClicked = false;
    private Client_dashboardController mainController;

    @FXML private Button resolveButton;
    @FXML private Button closeButton;

    public void setMainController(Client_dashboardController mainController) {
        this.mainController = mainController;
    }

    public void setDialogStage(Stage dialogStage){
        this.dialogStage = dialogStage;
    }

    @FXML private void handleResolve() {
        if (dialogStage != null) {
            dialogStage.close();
        }

        if (mainController != null) {
            System.out.println("Redirecting to Penalty/Resolve screen...");
            mainController.handleResolvePenalty(null);
        }
    }

    @FXML private void handleClose() {
        dialogStage.close();
    }

}