package main.gui;

import dao.RentalDAO;
import model.RentalTransaction;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.Alert.AlertType;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

public class Admin_rentalFormController {

    @FXML private Label formHeaderLabel;
    @FXML private TextField rentalIDField;
    @FXML private TextField customerIDField;
    @FXML private TextField plateIDField;
    @FXML private TextField locationIDField;
    @FXML private DatePicker pickUpDatePicker;
    @FXML private TextField pickUpTimeField;
    @FXML private DatePicker startDatePicker;
    @FXML private TextField startTimeField;
    @FXML private DatePicker endDatePicker;
    @FXML private TextField endTimeField;

    private Admin_dashboardController mainController;
    private RentalDAO rentalDAO = new RentalDAO();

    private boolean isUpdatingRecord = false;
    private RentalTransaction currentRental;

    public void setMainController(Admin_dashboardController mainController) {
        this.mainController = mainController;
    }

    public void setRentalData(RentalTransaction rental) {
        if (rental != null) {
            isUpdatingRecord = true;
            currentRental = rental;

            formHeaderLabel.setText("Update Rental");

            rentalIDField.setText(rental.getRentalID());
            customerIDField.setText(rental.getCustomerID());
            plateIDField.setText(rental.getPlateID());
            locationIDField.setText(rental.getLocationID());

            if (rental.getPickUpDateTime() != null) {
                pickUpDatePicker.setValue(rental.getPickUpDateTime().toLocalDateTime().toLocalDate());
                pickUpTimeField.setText(rental.getPickUpDateTime().toLocalDateTime().toLocalTime().toString());
            }

            if (rental.getStartDateTime() != null) {
                startDatePicker.setValue(rental.getStartDateTime().toLocalDateTime().toLocalDate());
                startTimeField.setText(rental.getStartDateTime().toLocalDateTime().toLocalTime().toString());
            }

            if (rental.getEndDateTime() != null) {
                endDatePicker.setValue(rental.getEndDateTime().toLocalDateTime().toLocalDate());
                endTimeField.setText(rental.getEndDateTime().toLocalDateTime().toLocalTime().toString());
            }

            rentalIDField.setDisable(true);
        } else {
            formHeaderLabel.setText("New Rental");
        }
    }

    @FXML
    private void handleSave() {
        if (!validateFields()) return;

        try {
            String rentalID = rentalIDField.getText().trim();
            String customerID = customerIDField.getText().trim();
            String plateID = plateIDField.getText().trim();
            String locationID = locationIDField.getText().trim();

            LocalDate pickUpDate = pickUpDatePicker.getValue();
            LocalTime pickUpTime = LocalTime.parse(pickUpTimeField.getText().trim());
            LocalDateTime pickUpDateTime = LocalDateTime.of(pickUpDate, pickUpTime);

            LocalDate startDate = startDatePicker.getValue();
            LocalTime startTime = LocalTime.parse(startTimeField.getText().trim());
            LocalDateTime startDateTime = LocalDateTime.of(startDate, startTime);

            LocalDateTime endDateTime = null;
            if (endDatePicker.getValue() != null && !endTimeField.getText().trim().isEmpty()) {
                LocalDate endDate = endDatePicker.getValue();
                LocalTime endTime = LocalTime.parse(endTimeField.getText().trim());
                endDateTime = LocalDateTime.of(endDate, endTime);
            }

            RentalTransaction rental = isUpdatingRecord ? currentRental : new RentalTransaction();
            rental.setRentalID(rentalID);
            rental.setCustomerID(customerID);
            rental.setPlateID(plateID);
            rental.setLocationID(locationID);
            rental.setPickUpDateTime(java.sql.Timestamp.valueOf(pickUpDateTime));
            rental.setStartDateTime(java.sql.Timestamp.valueOf(startDateTime));
            rental.setEndDateTime(endDateTime != null ? java.sql.Timestamp.valueOf(endDateTime) : null);
            String status = isUpdatingRecord ? currentRental.getStatus() : "Pending";
            rental.setStatus(status);

            boolean success = isUpdatingRecord
                    ? rentalDAO.updateRental(rental)
                    : rentalDAO.insertRental(rental);

            if (success) {
                showAlert(AlertType.INFORMATION, "Save Successful", "Rental record saved successfully.");
                mainController.loadPage("Admin-rentalRecords.fxml");
            } else {
                showAlert(AlertType.ERROR, "Error", "Failed to save rental record.");
            }

        } catch (Exception e) {
            showAlert(AlertType.ERROR, "Error", "Invalid date/time or unexpected error occurred.");
            e.printStackTrace();
        }
    }

    @FXML
    private void handleCancel() {
        mainController.loadPage("Admin-rentalRecords.fxml");
    }

    private boolean validateFields() {
        if (rentalIDField.getText().trim().isEmpty() ||
                customerIDField.getText().trim().isEmpty() ||
                plateIDField.getText().trim().isEmpty() ||
                locationIDField.getText().trim().isEmpty() ||
                pickUpDatePicker.getValue() == null ||
                pickUpTimeField.getText().trim().isEmpty() ||
                startDatePicker.getValue() == null ||
                startTimeField.getText().trim().isEmpty()) {

            showAlert(Alert.AlertType.WARNING, "Validation Error", "Please fill in all required fields.");
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

