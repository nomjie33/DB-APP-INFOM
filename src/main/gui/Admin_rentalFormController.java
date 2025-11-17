package main.gui;

import dao.CustomerDAO;
import dao.LocationDAO;
import dao.RentalDAO;
import dao.VehicleDAO;
import java.time.format.DateTimeFormatter;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.StringConverter;
import model.Customer;
import model.Location;
import model.RentalTransaction;
import model.Vehicle; // <-- Make sure you have 'import model.Vehicle;'
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

public class Admin_rentalFormController {

    @FXML private Label formHeaderLabel;
    @FXML private TextField rentalIDField;

    // --- ALL 3 ARE NOW COMBOBOXES ---
    @FXML private ComboBox<Customer> customerComboBox;
    @FXML private ComboBox<Vehicle> vehicleComboBox;
    @FXML private ComboBox<Location> locationComboBox;

    @FXML private DatePicker pickUpDatePicker;
    @FXML private TextField pickUpTimeField;
    @FXML private DatePicker startDatePicker;
    @FXML private TextField startTimeField;
    @FXML private DatePicker endDatePicker;
    @FXML private TextField endTimeField;
    @FXML private Label endDateLabel;
    @FXML private Label endTimeLabel;

    private Admin_dashboardController mainController;

    // --- ALL 4 DAOs ARE NEEDED ---
    private RentalDAO rentalDAO = new RentalDAO();
    private CustomerDAO customerDAO = new CustomerDAO();
    private VehicleDAO vehicleDAO = new VehicleDAO();
    private LocationDAO locationDAO = new LocationDAO();

    private boolean isUpdatingRecord = false;
    private RentalTransaction currentRental;

    public void setMainController(Admin_dashboardController mainController) {
        this.mainController = mainController;
    }

    public void setRentalData(RentalTransaction rental) {

        // --- 1. LOAD ALL DROPDOWNS ---
        loadCustomerComboBox();
        loadVehicleComboBox();
        loadLocationComboBox();

        // --- 2. RESET ALL FIELDS ---
        rentalIDField.setDisable(false);
        customerComboBox.setDisable(false);
        vehicleComboBox.setDisable(false);
        locationComboBox.setDisable(false);
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

        // --- 3. REMOVE ALL STYLES ---
        rentalIDField.getStyleClass().remove("form-text-field-disabled");
        customerComboBox.getStyleClass().remove("form-text-field-disabled");
        vehicleComboBox.getStyleClass().remove("form-text-field-disabled");
        locationComboBox.getStyleClass().remove("form-text-field-disabled");
        pickUpDatePicker.getStyleClass().remove("form-text-field-disabled");
        pickUpTimeField.getStyleClass().remove("form-text-field-disabled");
        startDatePicker.getStyleClass().remove("form-text-field-disabled");
        startTimeField.getStyleClass().remove("form-text-field-disabled");
        endDatePicker.getStyleClass().remove("form-text-field-disabled");
        endTimeField.getStyleClass().remove("form-text-field-disabled");

        if (rental != null) {
            // --- 4. EDIT MODE LOGIC ---
            isUpdatingRecord = true;
            currentRental = rental;
            formHeaderLabel.setText("Update Rental");

            rentalIDField.setText(rental.getRentalID());
            customerComboBox.setValue(findCustomerInList(rental.getCustomerID()));
            vehicleComboBox.setValue(findVehicleInList(rental.getPlateID()));
            locationComboBox.setValue(findLocationInList(rental.getLocationID()));

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

            // --- DISABLE RENTAL ID ONLY ---
            rentalIDField.setDisable(true);
            rentalIDField.getStyleClass().add("form-text-field-disabled");

        } else {
            // --- 5. NEW RENTAL MODE LOGIC ---
            isUpdatingRecord = false;
            formHeaderLabel.setText("New Rental");

            rentalIDField.clear();
            customerComboBox.setValue(null);
            vehicleComboBox.setValue(null);
            locationComboBox.setValue(null);
            pickUpDatePicker.setValue(null);
            pickUpTimeField.clear();
            startDatePicker.setValue(null);
            startTimeField.clear();
            endDatePicker.setValue(null);
            endTimeField.clear();

            // --- HIDE AND DISABLE End Date/Time fields ---
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
        // --- 1. Run basic field validation ---
        if (!validateFields()) {
            return; // Stops if any required field is empty or format is wrong
        }

        // --- 2. Prep and validate date logic ---
        LocalDateTime pickUpDateTime;
        LocalDateTime startDateTime;
        LocalDateTime endDateTime = null;

        try {
            pickUpDateTime = LocalDateTime.of(pickUpDatePicker.getValue(), LocalTime.parse(pickUpTimeField.getText().trim()));
            startDateTime = LocalDateTime.of(startDatePicker.getValue(), LocalTime.parse(startTimeField.getText().trim()));

            if (isUpdatingRecord && endDatePicker.getValue() != null && !endTimeField.getText().trim().isEmpty()) {
                endDateTime = LocalDateTime.of(endDatePicker.getValue(), LocalTime.parse(endTimeField.getText().trim()));
            }

        } catch (java.time.format.DateTimeParseException e) {
            showAlert(AlertType.ERROR, "Invalid Time", "Time must be in 24-hour HH:MM format (e.g., 09:30 or 14:00).");
            return;
        }

        // --- HERE IS YOUR NEW DATE LOGIC VALIDATION ---
        if (pickUpDateTime.isAfter(startDateTime)) {
            showAlert(AlertType.ERROR, "Invalid Dates", "Pick-Up time must be before or the same as the Start time.");
            return;
        }

        if (endDateTime != null && endDateTime.isBefore(startDateTime)) {
            showAlert(AlertType.ERROR, "Invalid Dates", "End time must be after the Start time.");
            return;
        }

        // --- 3. All validation passed, proceed with save ---
        try {
            RentalTransaction rental = isUpdatingRecord ? currentRental : new RentalTransaction();

            // --- Set common fields ---
            rental.setCustomerID(customerComboBox.getValue().getCustomerID());
            rental.setPlateID(vehicleComboBox.getValue().getPlateID());
            rental.setLocationID(locationComboBox.getValue().getLocationID());
            rental.setPickUpDateTime(java.sql.Timestamp.valueOf(pickUpDateTime));
            rental.setStartDateTime(java.sql.Timestamp.valueOf(startDateTime));

            boolean success;
            if (isUpdatingRecord) {
                // --- UPDATE LOGIC ---
                if (endDateTime != null) {
                    rental.setEndDateTime(java.sql.Timestamp.valueOf(endDateTime));
                    rental.setStatus("Completed");
                }
                success = rentalDAO.updateRental(rental);
            } else {
                // --- NEW RENTAL LOGIC ---
                rental.setRentalID(rentalIDField.getText().trim());
                rental.setEndDateTime(null);
                rental.setStatus("Active");

                // --- THIS IS CRITICAL: UPDATE VEHICLE STATUS ---
                success = rentalDAO.insertRental(rental);
                if (success) {
                    // After inserting, mark the vehicle as "In Use"
                    vehicleDAO.updateVehicleStatus(rental.getPlateID(), "In Use");
                }
            }

            // --- Show Confirmation ---
            if (success) {
                if (isUpdatingRecord) {
                    showAlert(AlertType.INFORMATION, "Save Successful", "Rental record has been updated.");
                } else {
                    // --- THIS IS THE FIX ---

                    // Get the full objects from the ComboBoxes
                    Customer customer = customerComboBox.getValue();
                    Vehicle vehicle = vehicleComboBox.getValue();
                    Location location = locationComboBox.getValue();

                    // Format the dates/times
                    DateTimeFormatter dtf = DateTimeFormatter.ofPattern("MMM dd, yyyy 'at' hh:mm a");
                    String pickUpStr = rental.getPickUpDateTime().toLocalDateTime().format(dtf);
                    String startStr = rental.getStartDateTime().toLocalDateTime().format(dtf);

                    String title = "New Rental Created!";
                    String content = "A new rental (check-out) has been recorded.\n\n" +

                            "Rental ID:\n" + rental.getRentalID() + "\n\n" +

                            "Customer:\n" + String.format("%s %s (%s)\n\n",
                            customer.getFirstName(),
                            customer.getLastName(),
                            customer.getCustomerID()) +

                            "Vehicle:\n" + String.format("%s (%s)\n\n",
                            vehicle.getPlateID(),
                            vehicle.getVehicleType()) + // Uses your VehicleDAO's field

                            "Pick-Up:\n" + String.format("%s\n%s\n\n",
                            location.getName(), // Uses your LocationDAO's field
                            pickUpStr) +

                            "Rental Start:\n" + startStr;

                    showConfirmationDialog(title, content);
                    // --- END OF FIX ---
                }
                mainController.loadPage("Admin-rentalRecords.fxml");
            } else {
                showAlert(AlertType.ERROR, "Error", "Failed to save rental record. Check foreign keys.");
            }
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
        // --- NOW CHECKS ALL 3 COMBOBOXES ---
        if (rentalIDField.getText().trim().isEmpty() ||
                customerComboBox.getValue() == null ||
                vehicleComboBox.getValue() == null ||
                locationComboBox.getValue() == null ||
                pickUpDatePicker.getValue() == null ||
                pickUpTimeField.getText().trim().isEmpty() ||
                startDatePicker.getValue() == null ||
                startTimeField.getText().trim().isEmpty()) {

            showAlert(Alert.AlertType.WARNING, "Validation Error", "Please fill in all required fields.");
            return false;
        }

        // Check time formats (parsing is handled in handleSave)
        try {
            LocalTime.parse(pickUpTimeField.getText().trim());
            LocalTime.parse(startTimeField.getText().trim());
            if (isUpdatingRecord && !endTimeField.getText().trim().isEmpty() && endDatePicker.getValue() != null) {
                LocalTime.parse(endTimeField.getText().trim());
            }
        } catch (Exception e) {
            showAlert(AlertType.ERROR, "Invalid Time", "Time must be in 24-hour HH:MM format (e.g., 09:30 or 14:00).");
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

    // --- (Keep your showConfirmationDialog method) ---
    private void showConfirmationDialog(String title, String content) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("Admin-addRecordConfirmation.fxml"));
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

    // --- (Keep your Customer helper methods) ---
    private void loadCustomerComboBox() {
        try {
            List<Customer> customers = customerDAO.getAllCustomers();
            customerComboBox.setItems(FXCollections.observableArrayList(customers));
            customerComboBox.setConverter(new StringConverter<Customer>() {
                @Override
                public String toString(Customer c) {
                    return (c == null) ? null : String.format("%s: %s %s (%s)",
                            c.getCustomerID(), c.getFirstName(), c.getLastName(), c.getEmailAddress());
                }
                @Override
                public Customer fromString(String s) { return null; }
            });
        } catch (Exception e) { e.printStackTrace(); }
    }

    private Customer findCustomerInList(String customerID) {
        if (customerID == null) return null;
        for (Customer c : customerComboBox.getItems()) {
            if (c.getCustomerID().equals(customerID)) return c;
        }
        return customerDAO.getCustomerById(customerID);
    }

    // --- ADD THESE NEW HELPER METHODS FOR VEHICLE AND LOCATION ---

    private void loadVehicleComboBox() {
        try {
            // Use getAvailableVehicles() so user can't rent a vehicle
            // that is already 'In Use' or in 'Maintenance'
            List<Vehicle> vehicles = vehicleDAO.getAvailableVehicles();
            vehicleComboBox.setItems(FXCollections.observableArrayList(vehicles));

            vehicleComboBox.setConverter(new StringConverter<Vehicle>() {
                @Override
                public String toString(Vehicle v) {
                    if (v == null) return null;
                    // Assumes Vehicle model has getPlateID, getVehicleType
                    // and getRentalPrice. Adjust if needed.
                    return String.format("%s: %s (₱%.2f/day)",
                            v.getPlateID(),
                            v.getVehicleType(),
                            v.getRentalPrice()
                    );
                }
                @Override
                public Vehicle fromString(String string) { return null; }
            });
        } catch (Exception e) {
            e.printStackTrace();
            showAlert(AlertType.ERROR, "Load Error", "Failed to load vehicle list.");
        }
    }

    private Vehicle findVehicleInList(String plateID) {
        if (plateID == null) return null;
        for (Vehicle v : vehicleComboBox.getItems()) {
            if (v.getPlateID().equals(plateID)) return v;
        }
        // If the vehicle is 'In Use' (i.e., we are editing),
        // it won't be in the 'Available' list, so fetch it directly.
        return vehicleDAO.getVehicleById(plateID);
    }

    private void loadLocationComboBox() {
        try {
            List<Location> locations = locationDAO.getAllLocations();
            locationComboBox.setItems(FXCollections.observableArrayList(locations));

            locationComboBox.setConverter(new StringConverter<Location>() {
                @Override
                public String toString(Location loc) {
                    if (loc == null) return null;
                    return String.format("%s: %s",
                            loc.getLocationID(),
                            loc.getName() // Uses 'getName' from your DAO
                    );
                }
                @Override
                public Location fromString(String string) { return null; }
            });
        } catch (Exception e) {
            e.printStackTrace();
            showAlert(AlertType.ERROR, "Load Error", "Failed to load location list.");
        }
    }

    private Location findLocationInList(String locationID) {
        if (locationID == null) return null;
        for (Location loc : locationComboBox.getItems()) {
            if (loc.getLocationID().equals(locationID)) return loc;
        }
        return locationDAO.getLocationById(locationID);
    }
}