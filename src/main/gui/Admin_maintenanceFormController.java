package main.gui;

import dao.MaintenanceDAO;
import model.MaintenanceTransaction;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.Alert.AlertType;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

public class Admin_maintenanceFormController {

    @FXML private Label formHeaderLabel;
    @FXML private TextField maintenanceIDField;
    @FXML private TextField plateIDField;
    @FXML private TextField technicianIDField;
    @FXML private DatePicker startDatePicker;
    @FXML private TextField startTimeField;
    @FXML private DatePicker endDatePicker;
    @FXML private TextField endTimeField;
    @FXML private TextArea notesArea;

    private Admin_dashboardController mainController;
    private MaintenanceDAO maintenanceDAO = new MaintenanceDAO();

    private boolean isUpdatingRecord = false;
    private MaintenanceTransaction currentMaintenance;

    private final DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm:ss");

    public void setMainController(Admin_dashboardController mainController) {
        this.mainController = mainController;
    }

    public void setMaintenanceData(MaintenanceTransaction maintenance) {
        if (maintenance != null) {
            isUpdatingRecord = true;
            currentMaintenance = maintenance;

            formHeaderLabel.setText("Update Maintenance");

            maintenanceIDField.setText(maintenance.getMaintenanceID());
            plateIDField.setText(maintenance.getPlateID());
            technicianIDField.setText(maintenance.getTechnicianID());

            if (maintenance.getStartDateTime() != null) {
                startDatePicker.setValue(maintenance.getStartDateTime().toLocalDateTime().toLocalDate());
                startTimeField.setText(maintenance.getStartDateTime().toLocalDateTime().toLocalTime().format(timeFormatter));
            }

            if (maintenance.getEndDateTime() != null) {
                endDatePicker.setValue(maintenance.getEndDateTime().toLocalDateTime().toLocalDate());
                endTimeField.setText(maintenance.getEndDateTime().toLocalDateTime().toLocalTime().format(timeFormatter));
            }

            notesArea.setText(maintenance.getNotes());

            maintenanceIDField.setDisable(true);
        }
    }

    @FXML
    private void handleSave() {
        if (!validateFields()) return;

        try {
            String maintenanceID = maintenanceIDField.getText().trim();
            String plateID = plateIDField.getText().trim();
            String technicianID = technicianIDField.getText().trim();
            String notes = notesArea.getText().trim();

            LocalDateTime startDateTime = LocalDateTime.of(startDatePicker.getValue(),
                    LocalTime.parse(startTimeField.getText().trim(), timeFormatter));

            LocalDateTime endDateTime = null;
            if (endDatePicker.getValue() != null && !endTimeField.getText().trim().isEmpty()) {
                endDateTime = LocalDateTime.of(endDatePicker.getValue(),
                        LocalTime.parse(endTimeField.getText().trim(), timeFormatter));
            }

            MaintenanceTransaction maintenance = isUpdatingRecord ? currentMaintenance : new MaintenanceTransaction();
            maintenance.setMaintenanceID(maintenanceID);
            maintenance.setPlateID(plateID);
            maintenance.setTechnicianID(technicianID);
            maintenance.setStartDateTime(java.sql.Timestamp.valueOf(startDateTime));
            maintenance.setEndDateTime(endDateTime != null ? java.sql.Timestamp.valueOf(endDateTime) : null);
            maintenance.setNotes(notes);

            boolean isSuccessful = isUpdatingRecord
                    ? maintenanceDAO.updateMaintenance(maintenance)
                    : maintenanceDAO.insertMaintenance(maintenance);

            if (isSuccessful) {
                showAlert(AlertType.INFORMATION, "Save Successful", "Maintenance record saved successfully.");
                mainController.loadPage("Admin-maintenanceRecords.fxml"); // go back to records table
            } else {
                showAlert(AlertType.ERROR, "Error", "Failed to save maintenance record.");
            }

        } catch (Exception e) {
            showAlert(AlertType.ERROR, "Error", "Invalid date/time format or unexpected error.");
            e.printStackTrace();
        }
    }

    @FXML
    private void handleCancel() {
        mainController.loadPage("Admin-maintenanceRecords.fxml");
    }

    private boolean validateFields() {
        if (maintenanceIDField.getText().trim().isEmpty() ||
                plateIDField.getText().trim().isEmpty() ||
                technicianIDField.getText().trim().isEmpty() ||
                startDatePicker.getValue() == null ||
                startTimeField.getText().trim().isEmpty()) {
            showAlert(AlertType.WARNING, "Validation Error", "Please fill in all required fields.");
            return false;
        }

        try {
            LocalTime.parse(startTimeField.getText().trim(), timeFormatter);
            if (!endTimeField.getText().trim().isEmpty() && endDatePicker.getValue() != null) {
                LocalTime.parse(endTimeField.getText().trim(), timeFormatter);
            }
        } catch (Exception e) {
            showAlert(AlertType.ERROR, "Invalid Time", "Time must be in HH:MM:SS format.");
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
