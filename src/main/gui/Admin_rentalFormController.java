package main.gui;

import dao.CustomerDAO;
import dao.LocationDAO;
import dao.PaymentDAO;
import dao.RentalDAO;
import dao.VehicleDAO;
import service.PaymentService;
import service.RentalService;
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
import model.Vehicle;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Objects;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.Initializable;

public class Admin_rentalFormController implements Initializable{

    @FXML private Label formHeaderLabel;
    @FXML private TextField rentalIDField;

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

    private RentalDAO rentalDAO = new RentalDAO();
    private CustomerDAO customerDAO = new CustomerDAO();
    private VehicleDAO vehicleDAO = new VehicleDAO();
    private LocationDAO locationDAO = new LocationDAO();
    private PaymentDAO paymentDAO = new PaymentDAO();
    private PaymentService paymentService = new PaymentService();
    private RentalService rentalService;

    private boolean isUpdatingRecord = false;
    private RentalTransaction currentRental;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        this.rentalService = new RentalService(
                customerDAO,
                vehicleDAO,
                locationDAO,
                rentalDAO,
                paymentDAO,
                paymentService
        );

        loadCustomerComboBox();
        loadVehicleComboBox();
        loadLocationComboBox();

        // Auto-generate ID for new rentals
        // ID field is always disabled (for display only)
        if (!isUpdatingRecord) {
            String nextID = rentalService.generateNextRentalID();
            rentalIDField.setText(nextID);
            rentalIDField.setDisable(true);
            rentalIDField.getStyleClass().add("form-text-field-disabled");
        }
    }

    public void setMainController(Admin_dashboardController mainController) {
        this.mainController = mainController;
    }

    public void setRentalData(RentalTransaction rental) {
        if (rental != null) {
            isUpdatingRecord = true;
            currentRental = rental;
            formHeaderLabel.setText("Update Rental");

            // Display existing rental ID (already disabled in initialize)
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

            // Show end date/time fields for editing
            endDateLabel.setVisible(true);
            endDatePicker.setVisible(true);
            endTimeLabel.setVisible(true);
            endTimeField.setVisible(true);

            // Ensure ID field is disabled for editing
            rentalIDField.setDisable(true);
            if (!rentalIDField.getStyleClass().contains("form-text-field-disabled")) {
                rentalIDField.getStyleClass().add("form-text-field-disabled");
            }

        } else {
            // NEW RENTAL - initialize() already set the ID
            isUpdatingRecord = false;
            formHeaderLabel.setText("New Rental");

            customerComboBox.setValue(null);
            vehicleComboBox.setValue(null);
            locationComboBox.setValue(null);
            pickUpDatePicker.setValue(null);
            pickUpTimeField.clear();
            startDatePicker.setValue(null);
            startTimeField.clear();

            // Hide end date/time fields for new rentals
            endDateLabel.setVisible(false);
            endDatePicker.setVisible(false);
            endTimeLabel.setVisible(false);
            endTimeField.setVisible(false);
        }
    }

    @FXML
    private void handleSave() {
        if (!validateFields()) return;

        LocalDateTime newPickUpDateTime, newStartDateTime, newEndDateTime = null;
        try {
            newPickUpDateTime = LocalDateTime.of(pickUpDatePicker.getValue(), LocalTime.parse(pickUpTimeField.getText().trim()));
            newStartDateTime = LocalDateTime.of(startDatePicker.getValue(), LocalTime.parse(startTimeField.getText().trim()));
            if (endDatePicker.getValue() != null && !endTimeField.getText().trim().isEmpty()) {
                newEndDateTime = LocalDateTime.of(endDatePicker.getValue(), LocalTime.parse(endTimeField.getText().trim()));
            }
        } catch (java.time.format.DateTimeParseException e) {
            showAlert(AlertType.ERROR, "Invalid Time", "Time must be in 24-hour HH:MM format (e.g., 09:30 or 14:00).");
            return;
        }

        Customer newCustomer = customerComboBox.getValue();
        Vehicle newVehicle = vehicleComboBox.getValue();
        Location newLocation = locationComboBox.getValue();

        if (newPickUpDateTime.isAfter(newStartDateTime)) {
            showAlert(AlertType.ERROR, "Invalid Dates", "Pick-Up time must be before or the same as the Start time.");
            return;
        }
        if (newEndDateTime != null && newEndDateTime.isBefore(newStartDateTime)) {
            showAlert(AlertType.ERROR, "Invalid Dates", "End time must be after the Start time.");
            return;
        }

        if (isUpdatingRecord) {
            LocalDateTime oldPickUp = currentRental.getPickUpDateTime().toLocalDateTime();
            LocalDateTime oldStart = (currentRental.getStartDateTime() == null) ? null : currentRental.getStartDateTime().toLocalDateTime();
            LocalDateTime oldEnd = (currentRental.getEndDateTime() == null) ? null : currentRental.getEndDateTime().toLocalDateTime();

            boolean noChange = Objects.equals(currentRental.getCustomerID(), newCustomer.getCustomerID()) &&
                    Objects.equals(currentRental.getPlateID(), newVehicle.getPlateID()) &&
                    Objects.equals(currentRental.getLocationID(), newLocation.getLocationID()) &&
                    Objects.equals(oldPickUp, newPickUpDateTime) &&
                    Objects.equals(oldStart, newStartDateTime) &&
                    Objects.equals(oldEnd, newEndDateTime);

            if (noChange) {
                showAlert(AlertType.INFORMATION, "No Changes", "No changes were detected.");
                return;
            }
        }

        try {
            RentalTransaction rental = isUpdatingRecord ? currentRental : new RentalTransaction();

            rental.setCustomerID(newCustomer.getCustomerID());
            rental.setPlateID(newVehicle.getPlateID());
            rental.setLocationID(newLocation.getLocationID());
            rental.setPickUpDateTime(java.sql.Timestamp.valueOf(newPickUpDateTime));
            rental.setStartDateTime(java.sql.Timestamp.valueOf(newStartDateTime));

            boolean success;
            if (isUpdatingRecord) {
                // UPDATE EXISTING RENTAL
                
                // Check if transitioning to completed status
                if (!currentRental.isCompleted() && newEndDateTime != null) {
                    // Complete the rental using service
                    double rentalCost = rentalService.completeRental(rental.getRentalID());
                    success = (rentalCost > 0);
                    
                    if (success) {
                        rental.setEndDateTime(java.sql.Timestamp.valueOf(newEndDateTime));
                        rental.setStatus("Completed");
                    }
                } 
                // Check if transitioning from not-picked-up to picked-up
                else if (!currentRental.isPickedUp() && newStartDateTime != null) {
                    // Start the rental using service with custom timestamp
                    success = rentalService.startRental(rental.getRentalID(), java.sql.Timestamp.valueOf(newStartDateTime));
                    
                    if (success) {
                        rental.setStartDateTime(java.sql.Timestamp.valueOf(newStartDateTime));
                        rental.setStatus("Active");
                    }
                }
                // Otherwise, update rental data directly with proper status management
                else {
                    // Determine the correct status based on timestamps
                    if (newEndDateTime != null) {
                        rental.setEndDateTime(java.sql.Timestamp.valueOf(newEndDateTime));
                        rental.setStatus("Completed");
                    } else if (newStartDateTime != null) {
                        rental.setEndDateTime(null);
                        rental.setStatus("Active");
                    } else {
                        rental.setEndDateTime(null);
                        rental.setStatus("Active");
                    }
                    
                    success = rentalDAO.updateRental(rental);
                    
                    if (success) {
                        // Update vehicle status based on rental state
                        if (rental.isCompleted() || rental.isCancelled()) {
                            vehicleDAO.updateVehicleStatus(rental.getPlateID(), "Available");
                        } else if (rental.isPickedUp()) {
                            vehicleDAO.updateVehicleStatus(rental.getPlateID(), "In Use");
                        } else {
                            // Rental is booked but not picked up yet
                            vehicleDAO.updateVehicleStatus(rental.getPlateID(), "Available");
                        }
                    }
                }

            } else {
                // NEW RENTAL: Use service layer to create booking
                String newRentalID = rentalService.bookRental(
                    newCustomer.getCustomerID(),
                    newVehicle.getPlateID(),
                    newLocation.getLocationID(),
                    java.sql.Timestamp.valueOf(newPickUpDateTime)
                );
                
                success = (newRentalID != null);
                
                if (success) {
                    // If startDateTime is set, also mark as picked up
                    if (newStartDateTime != null) {
                        boolean pickupSuccess = rentalService.startRental(newRentalID);
                        if (!pickupSuccess) {
                            showAlert(AlertType.WARNING, "Partial Success", 
                                "Rental created but failed to mark as picked up. Please update manually.");
                        }
                    }
                    
                    // Update rental object for confirmation display
                    rental.setRentalID(newRentalID);
                    rental.setEndDateTime(null);
                    rental.setStatus("Active");
                }
            }

            if (success) {
                if (isUpdatingRecord) {
                    showAlert(AlertType.INFORMATION, "Save Successful", "Rental record has been updated.");
                } else {

                    DateTimeFormatter dtf = DateTimeFormatter.ofPattern("MMM dd, yyyy 'at' hh:mm a");
                    String pickUpStr = rental.getPickUpDateTime().toLocalDateTime().format(dtf);
                    String startStr = rental.getStartDateTime().toLocalDateTime().format(dtf);
                    String title = "New Rental Created!";
                    String content = "A new rental (check-out) has been recorded.\n\n" +
                            "Rental ID:\n" + rental.getRentalID() + "\n\n" +
                            "Customer:\n" + String.format("%s %s (%s)\n\n",
                            newCustomer.getFirstName(), newCustomer.getLastName(), newCustomer.getCustomerID()) +
                            "Vehicle:\n" + String.format("%s (%s)\n\n",
                            newVehicle.getPlateID(), newVehicle.getVehicleType()) +
                            "Pick-Up:\n" + String.format("%s\n%s\n\n",
                            newLocation.getName(), pickUpStr) +
                            "Rental Start:\n" + startStr;
                    showConfirmationDialog(title, content);
                }
                mainController.loadPage("Admin-rentalRecords.fxml");
            } else {
                showAlert(AlertType.ERROR, "Error", "Failed to save rental record.");
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

        try {
            LocalTime.parse(pickUpTimeField.getText().trim());
            LocalTime.parse(startTimeField.getText().trim());
            if (isUpdatingRecord && !endTimeField.getText().trim().isEmpty() && endDatePicker.getValue() != null) {
                LocalTime.parse(endTimeField.getText().trim());
            }
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Invalid Time", "Time must be in 24-hour HH:MM format (e.g., 09:30 or 14:00).");
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

    private void loadVehicleComboBox() {
        try {
            List<Vehicle> vehicles = vehicleDAO.getAvailableVehicles();
            vehicleComboBox.setItems(FXCollections.observableArrayList(vehicles));

            vehicleComboBox.setConverter(new StringConverter<Vehicle>() {
                @Override
                public String toString(Vehicle v) {
                    if (v == null) return null;
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
                            loc.getName()
                    );
                }
                @Override
                public Location fromString(String string) { return null; }
            });
        } catch (Exception e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Load Error", "Failed to load location list.");
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