package main.gui;

import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import java.io.IOException;

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
    @FXML private Label endDateLabel;
    @FXML private Label endTimeLabel;

    private Admin_dashboardController mainController;
    private RentalDAO rentalDAO = new RentalDAO();

    private boolean isUpdatingRecord = false;
    private RentalTransaction currentRental;

    public void setMainController(Admin_dashboardController mainController) {
        this.mainController = mainController;
    }

    public void setRentalData(RentalTransaction rental) {

        rentalIDField.setDisable(false);
        customerIDField.setDisable(false);
        plateIDField.setDisable(false);
        locationIDField.setDisable(false);
        pickUpDatePicker.setDisable(false);
        pickUpTimeField.setDisable(false);
        startDatePicker.setDisable(false);
        startTimeField.setDisable(false);

        endDateLabel.setVisible(true);
        endDatePicker.setVisible(true);
        endDatePicker.setDisable(false);
        endTimeLabel.setVisible(true);
        endTimeField.setVisible(true);
        endTimeField.setDisable(false);

        rentalIDField.getStyleClass().remove("form-text-field-disabled");
        customerIDField.getStyleClass().remove("form-text-field-disabled");
        plateIDField.getStyleClass().remove("form-text-field-disabled");
        locationIDField.getStyleClass().remove("form-text-field-disabled");
        pickUpDatePicker.getStyleClass().remove("form-text-field-disabled");
        pickUpTimeField.getStyleClass().remove("form-text-field-disabled");
        startDatePicker.getStyleClass().remove("form-text-field-disabled");
        startTimeField.getStyleClass().remove("form-text-field-disabled");
        endDatePicker.getStyleClass().remove("form-text-field-disabled");
        endTimeField.getStyleClass().remove("form-text-field-disabled");


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
            rentalIDField.getStyleClass().add("form-text-field-disabled");

        } else {

            isUpdatingRecord = false;
            formHeaderLabel.setText("New Rental");

            rentalIDField.clear();
            customerIDField.clear();
            plateIDField.clear();
            locationIDField.clear();
            pickUpDatePicker.setValue(null);
            pickUpTimeField.clear();
            startDatePicker.setValue(null);
            startTimeField.clear();
            endDatePicker.setValue(null);
            endTimeField.clear();

            endDateLabel.setVisible(false);
            endDatePicker.setVisible(false);
            endDatePicker.setDisable(true);

            endTimeLabel.setVisible(false);
            endTimeField.setVisible(false);
            endTimeField.setDisable(true);
        }
    }

    @FXML
    private void handleSave() {
        if (!validateFields()) return;

        try {
            RentalTransaction rental = isUpdatingRecord ? currentRental : new RentalTransaction();

            // Only set these if it's a NEW rental
            if (!isUpdatingRecord) {
                rental.setRentalID(rentalIDField.getText().trim());
                rental.setCustomerID(customerIDField.getText().trim());
                rental.setPlateID(plateIDField.getText().trim());
                rental.setLocationID(locationIDField.getText().trim());

                LocalDate pickUpDate = pickUpDatePicker.getValue();
                LocalTime pickUpTime = LocalTime.parse(pickUpTimeField.getText().trim());
                LocalDateTime pickUpDateTime = LocalDateTime.of(pickUpDate, pickUpTime);
                rental.setPickUpDateTime(java.sql.Timestamp.valueOf(pickUpDateTime));

                LocalDate startDate = startDatePicker.getValue();
                LocalTime startTime = LocalTime.parse(startTimeField.getText().trim());
                LocalDateTime startDateTime = LocalDateTime.of(startDate, startTime);
                rental.setStartDateTime(java.sql.Timestamp.valueOf(startDateTime));

                rental.setStatus("Active"); // New rentals are "Active"
            }

            // This logic is for BOTH new and update
            LocalDateTime endDateTime = null;
            if (endDatePicker.getValue() != null && !endTimeField.getText().trim().isEmpty()) {
                LocalDate endDate = endDatePicker.getValue();
                LocalTime endTime = LocalTime.parse(endTimeField.getText().trim());
                endDateTime = LocalDateTime.of(endDate, endTime);

                rental.setEndDateTime(java.sql.Timestamp.valueOf(endDateTime));
                rental.setStatus("Completed"); // If end date is set, it's "Completed"
            }

            boolean success = isUpdatingRecord
                    ? rentalDAO.updateRental(rental)
                    : rentalDAO.insertRental(rental);

            // --- 2. UPDATE HANDLESAVE LOGIC ---
            if (success) {
                if (isUpdatingRecord) {
                    showAlert(AlertType.INFORMATION, "Save Successful", "Rental record has been updated.");
                } else {
                    String title = "New Rental Created!";
                    String content = "A new rental (check-out) has been recorded.\n\n" +
                            "Rental ID:\n" + rental.getRentalID() + "\n\n" +
                            "Customer ID:\n" + rental.getCustomerID() + "\n\n" +
                            "Plate ID:\n" + rental.getPlateID() + "\n\n" +
                            "Start Time:\n" + rental.getStartDateTime().toString();

                    showConfirmationDialog(title, content); // <-- USE RECEIPT
                }
                mainController.loadPage("Admin-rentalRecords.fxml"); // <-- This needs a records page
            } else {
                showAlert(AlertType.ERROR, "Error", "Failed to save rental record.");
            }
        } catch (java.time.format.DateTimeParseException e) {
            showAlert(AlertType.ERROR, "Invalid Time", "Time must be in 24-hour HH:mm format (e.g., 09:30 or 14:00).");
        } catch (Exception e) {
            showAlert(AlertType.ERROR, "Error", "An unexpected error occurred.");
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
