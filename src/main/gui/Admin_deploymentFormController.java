package main.gui;

import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import java.io.IOException;

import dao.DeploymentDAO;
import model.DeploymentTransaction;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import java.sql.Date;
import java.time.LocalDate;

public class Admin_deploymentFormController {

    @FXML private Label formHeaderLabel;
    @FXML private TextField deploymentIDField;
    @FXML private TextField plateIDField;
    @FXML private TextField locationIDField;
    @FXML private DatePicker startDatePicker;
    @FXML private DatePicker endDatePicker;

    private Admin_dashboardController mainController;
    private DeploymentDAO deploymentDAO = new DeploymentDAO();
    private boolean isUpdatingRecord = false;

    public void setMainController(Admin_dashboardController mainController){
        this.mainController = mainController;
        deploymentIDField.setDisable(true);
    }

    public void setDeploymentData(DeploymentTransaction deployment){
        if (deployment != null){
            isUpdatingRecord = true;
            formHeaderLabel.setText("Update Deployment");

            deploymentIDField.setText(deployment.getDeploymentID());
            plateIDField.setText(deployment.getPlateID());
            locationIDField.setText(deployment.getLocationID());

            if (deployment.getStartDate() != null) {
                startDatePicker.setValue(deployment.getStartDate().toLocalDate());
            }
            if (deployment.getEndDate() != null) {
                endDatePicker.setValue(deployment.getEndDate().toLocalDate());
            }

            plateIDField.setDisable(true);
            plateIDField.getStyleClass().add("form-text-field-disabled");
            locationIDField.setDisable(true);
            locationIDField.getStyleClass().add("form-text-field-disabled");
            startDatePicker.setDisable(true);
            startDatePicker.getStyleClass().add("form-text-field-disabled");
        }
    }

    @FXML
    private void handleSave(){
        if (!validateFields()){
            return;
        }

        DeploymentTransaction deployment = new DeploymentTransaction();

        deployment.setDeploymentID(deploymentIDField.getText());
        deployment.setPlateID(plateIDField.getText());
        deployment.setLocationID(locationIDField.getText());

        LocalDate startDate = startDatePicker.getValue();
        deployment.setStartDate(Date.valueOf(startDate));
        LocalDate endDate = endDatePicker.getValue();

        if (endDate != null) {
            deployment.setEndDate(Date.valueOf(endDate));
            deployment.setStatus("Completed");
        } else {
            deployment.setEndDate(null);
            deployment.setStatus("Active");
        }

        boolean isSuccessful;
        if (isUpdatingRecord){
            isSuccessful = deploymentDAO.updateDeployment(deployment);
        } else {
            isSuccessful = deploymentDAO.insertDeployment(deployment);
        }

        if (isSuccessful){
            if (isUpdatingRecord){
                showAlert(Alert.AlertType.INFORMATION, "Deployment Saved",
                        "Deployment record saved successfully. ID: " + deployment.getDeploymentID());
            } else {
                String title = "New Deployment Added!";
                String content = "A new vehicle deployment has been recorded.\n\n" +
                        "Deployment ID:\n" + deployment.getDeploymentID() + "\n\n" +
                        "Plate ID:\n" + deployment.getPlateID() + "\n\n" +
                        "Location ID:\n" + deployment.getLocationID() + "\n\n" +
                        "Start Date:\n" + deployment.getStartDate().toString();
                showConfirmationDialog(title, content);
            }
            mainController.loadPage("Admin-deploymentRecords.fxml");
        } else {
            showAlert(Alert.AlertType.ERROR, "Error", "Failed to save deployment record.");
        }
    }

    @FXML
    private void handleCancel(){
        System.out.println("Cancel button clicked. Returning to deployment list.");
        mainController.loadPage("Admin-deploymentRecords.fxml");
    }

    private boolean validateFields(){
        LocalDate startDate = startDatePicker.getValue();
        LocalDate endDate = endDatePicker.getValue();

        if (plateIDField.getText().isEmpty() || locationIDField.getText().isEmpty() || startDate == null){
            showAlert(Alert.AlertType.WARNING, "Validation Error", "Please ensure Plate ID, Location ID, and Start Date are filled.");
            return false;
        }

        if (endDate != null && endDate.isBefore(startDate)) {
            showAlert(Alert.AlertType.WARNING, "Validation Error", "End Date cannot be before the Start Date.");
            return false;
        }

        return true;
    }

    private String generateDeploymentID() {
        return "DEP-" + System.currentTimeMillis() % 10000;
    }

    private void showAlert(Alert.AlertType type, String title, String content){
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }

    private void showConfirmationDialog(String title, String content) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("Admin_addRecordConfirmation.fxml"));
            AnchorPane page = loader.load();
            Stage dialogStage = new Stage();
            dialogStage.setTitle("Confirmation");
            dialogStage.initModality(Modality.WINDOW_MODAL);
            if (mainController != null) {
                dialogStage.initOwner(mainController.getPrimaryStage());
            }
            Scene scene = new Scene(page);
            dialogStage.setScene(scene);
            Admin_addRecordConfirmationController controller = loader.getController();
            controller.setDialogStage(dialogStage);
            controller.setData(title, content);
            dialogStage.showAndWait();
        } catch (IOException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Error", "Could not load confirmation dialog.");
        }
    }
}