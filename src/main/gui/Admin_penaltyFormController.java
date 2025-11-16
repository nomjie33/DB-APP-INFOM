package main.gui;

import dao.PenaltyDAO;
import model.PenaltyTransaction;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.Alert.AlertType;

import java.math.BigDecimal;
import java.time.LocalDate;

public class Admin_penaltyFormController {

    @FXML private Label formHeaderLabel;
    @FXML private TextField idField;
    @FXML private TextField rentalIDField;
    @FXML private TextField maintenanceIDField;
    @FXML private TextField totalPenaltyField;
    @FXML private DatePicker dateIssuedPicker;

    private Admin_dashboardController mainController;
    private PenaltyDAO penaltyDAO = new PenaltyDAO();

    private boolean isUpdatingRecord = false;
    private PenaltyTransaction currentPenalty;

    public void setMainController(Admin_dashboardController mainController) {
        this.mainController = mainController;
    }

    public void setPenaltyData(PenaltyTransaction penalty) {
        if (penalty != null) {
            isUpdatingRecord = true;
            currentPenalty = penalty;

            formHeaderLabel.setText("Update Penalty");

            idField.setText(penalty.getPenaltyID());
            rentalIDField.setText(penalty.getRentalID());
            maintenanceIDField.setText(penalty.getMaintenanceID());
            totalPenaltyField.setText(penalty.getTotalPenalty().toPlainString());
            if (penalty.getDateIssued() != null) {
                dateIssuedPicker.setValue(penalty.getDateIssued().toLocalDate());
            }

            idField.setDisable(true);
        }
    }

    @FXML
    private void handleSave() {
        if (!validateFields()) return;

        try {
            String penaltyID = idField.getText().trim();
            String rentalID = rentalIDField.getText().trim();
            String maintenanceID = maintenanceIDField.getText().trim();
            BigDecimal totalPenalty = new BigDecimal(totalPenaltyField.getText().trim());
            LocalDate dateIssued = dateIssuedPicker.getValue();

            PenaltyTransaction penalty = isUpdatingRecord ? currentPenalty : new PenaltyTransaction();
            penalty.setPenaltyID(penaltyID);
            penalty.setRentalID(rentalID);
            penalty.setMaintenanceID(maintenanceID);
            penalty.setTotalPenalty(totalPenalty);
            penalty.setDateIssued(dateIssued != null ? java.sql.Date.valueOf(dateIssued) : null);

            boolean success = isUpdatingRecord
                    ? penaltyDAO.updatePenalty(penalty)
                    : penaltyDAO.insertPenalty(penalty);

            if (success) {
                showAlert(AlertType.INFORMATION, "Save Successful", "Penalty record saved successfully.");
                mainController.loadPage("Admin_penaltyRecords.fxml");
            } else {
                showAlert(AlertType.ERROR, "Error", "Failed to save penalty record.");
            }

        } catch (NumberFormatException e) {
            showAlert(AlertType.ERROR, "Invalid Amount", "Total penalty must be a valid number (e.g., 500.00).");
        } catch (Exception e) {
            showAlert(AlertType.ERROR, "Error", "An unexpected error occurred while saving the record.");
            e.printStackTrace();
        }
    }

    @FXML
    private void handleCancel() {
        mainController.loadPage("Admin-penaltyRecords.fxml");
    }

    private boolean validateFields() {
        if (idField.getText().trim().isEmpty() ||
                rentalIDField.getText().trim().isEmpty() ||
                maintenanceIDField.getText().trim().isEmpty() ||
                totalPenaltyField.getText().trim().isEmpty() ||
                dateIssuedPicker.getValue() == null) {
            showAlert(AlertType.WARNING, "Validation Error", "Please fill in all required fields.");
            return false;
        }

        try {
            new BigDecimal(totalPenaltyField.getText().trim());
        } catch (NumberFormatException e) {
            showAlert(AlertType.ERROR, "Invalid Amount", "Total penalty must be a valid number (e.g., 500.00).");
            return false;
        }

        return true;
    }

    private void showAlert(AlertType type, String title, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}