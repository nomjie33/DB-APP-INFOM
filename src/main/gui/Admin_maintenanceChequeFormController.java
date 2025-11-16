package main.gui;

import dao.MaintenanceChequeDAO;
import model.MaintenanceCheque;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import java.math.BigDecimal;
import java.math.RoundingMode;

public class Admin_maintenanceChequeFormController {

    @FXML private Label formHeaderLabel;
    @FXML private TextField maintenanceIDField;
    @FXML private TextField partIDField;
    @FXML private TextField quantityField;

    private Admin_dashboardController mainController;
    private MaintenanceChequeDAO chequeDAO = new MaintenanceChequeDAO();
    private boolean isUpdatingRecord = false;
    private MaintenanceCheque currentCheque;

    public void setMainController(Admin_dashboardController mainController){
        this.mainController = mainController;
    }

    public void setMaintenanceChequeData(MaintenanceCheque cheque){
        if (cheque != null){
            isUpdatingRecord = true;
            currentCheque = cheque;
            formHeaderLabel.setText("Update Cheque");

            maintenanceIDField.setText(cheque.getMaintenanceID());
            partIDField.setText(cheque.getPartID());
            quantityField.setText(cheque.getQuantityUsed().setScale(2, RoundingMode.HALF_UP).toString());

            maintenanceIDField.setDisable(true);
            partIDField.setDisable(true);
        }
    }

    @FXML private void handleSave(){
        if (!validateFields()){
            return;
        }

        try {
            String maintenanceID = maintenanceIDField.getText().trim();
            String partID = partIDField.getText().trim();
            BigDecimal quantity = new BigDecimal(quantityField.getText().trim()).setScale(2, RoundingMode.HALF_UP);

            MaintenanceCheque cheque = isUpdatingRecord ? currentCheque : new MaintenanceCheque();
            cheque.setMaintenanceID(maintenanceID);
            cheque.setPartID(partID);
            cheque.setQuantityUsed(quantity);

            boolean isSuccessful;
            if (isUpdatingRecord){
                isSuccessful = chequeDAO.updateMaintenanceCheque(cheque);
            } else {
                isSuccessful = chequeDAO.insertMaintenanceCheque(cheque);
            }

            if (isSuccessful){
                showAlert(Alert.AlertType.INFORMATION, "Save Successful", "Maintenance Cheque record saved successfully.");
                mainController.loadPage("Admin-maintenanceCheques.fxml");
            } else {
                showAlert(Alert.AlertType.ERROR, "Error", "Failed to save Maintenance Cheque record. Check logs for details.");
            }

        } catch (NumberFormatException e){
            showAlert(Alert.AlertType.ERROR, "Invalid Input", "Quantity must be a valid number (e.g., 1.00).");
        } catch (Exception e){
            showAlert(Alert.AlertType.ERROR, "Error", "An unexpected error occurred during save.");
            e.printStackTrace();
        }
    }

    @FXML private void handleCancel(){
        mainController.loadPage("Admin-maintenanceCheques.fxml");
    }

    private boolean validateFields(){
        String quantityText = quantityField.getText().trim();
        if (maintenanceIDField.getText().trim().isEmpty() || partIDField.getText().trim().isEmpty() || quantityText.isEmpty()){
            showAlert(Alert.AlertType.WARNING, "Validation Error", "Please fill in all required fields.");
            return false;
        }

        try {
            new BigDecimal(quantityText);
        } catch (NumberFormatException e){
            showAlert(Alert.AlertType.ERROR, "Invalid Input", "Quantity Used must be a valid number.");
            return false;
        }

        if (new BigDecimal(quantityText).compareTo(BigDecimal.ZERO) <= 0) {
            showAlert(Alert.AlertType.ERROR, "Invalid Input", "Quantity Used must be greater than zero.");
            return false;
        }

        return true;
    }

    private void showAlert(Alert.AlertType type, String title, String content){
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}
