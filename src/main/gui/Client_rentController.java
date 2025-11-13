/*
=====================================
This code facilitates the rent
sequence of the code.

Version: 1.3
Latest edit: November 12, 2025
- Added active rental check to initData()
- This immediately redirects to the return scene

Authors:
Airon Matthew Bantillo | S22-07
Alexandra Gayle Gonzales | S22-07
Naomi Isabel Reyes | S22-07
Roberta Netanya Tan | S22-07
=====================================
*/

package main.gui;

// Core JavaFX

import dao.LocationDAO;
import dao.RentalDAO;
import dao.VehicleDAO;
import model.Customer;
import model.Location;
import model.RentalTransaction;
import model.Vehicle;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;

import java.io.IOException;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Locale;

public class Client_rentController {

    private Client_dashboardController mainController;
    private Customer loggedInCustomer;
    private RentalDAO rentalDAO = new RentalDAO();
    private VehicleDAO vehicleDAO = new VehicleDAO();
    private LocationDAO locationDAO = new LocationDAO();

    private String selectedVehicleType = null;
    private RentalTransaction activeRental;

    @FXML private Pane scooterPane;
    @FXML private Pane bikePane;
    @FXML private Pane trikePane;
    @FXML private VBox rentalFormPane;
    @FXML private ComboBox<Location> locationComboBox;
    @FXML private DatePicker datePicker;
    @FXML private DatePicker returnDatePicker;
    @FXML private TextField startTimeField;
    @FXML private TextField endTimeField;
    @FXML private Label errorLabel;
    @FXML private Button confirmButton;
    @FXML private Button cancelButton;

    /**
     =========================================================
     This function will prompt the user to return the vehicle
     instead they have an active rent at the moment.
     =========================================================
     */
    private void loadReturnVehicleScene(RentalTransaction activeRental){
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("Client-returnVehicle.fxml"));
            Parent page = loader.load();

            Client_returnVehicleController returnVehicle = loader.getController();
            returnVehicle.initData(loggedInCustomer, activeRental);
            returnVehicle.setMainController(mainController);

            mainController.loadPageFromSub(page);

        } catch (IOException e){
            e.printStackTrace();
            showError(("Could not load return prompt page."));
        }
    }

    /**
     ========================================================
     This method passes the logged-in customer object.
     [NEW] It now also checks for an active rental immediately.
     ========================================================
     */
    public void initData(Customer customer) {
        this.loggedInCustomer = customer;
    }

    /**
     ========================================================
     This method enables the dashboard to display the rent
     sequence as the main scene AND populates the location ComboBox.
     ========================================================
     */
    public void setMainController(Client_dashboardController mainController) {
        this.mainController = mainController;

        try {
            List<Location> locations = locationDAO.getAllLocations();
            locationComboBox.getItems().addAll(locations);

            locationComboBox.setConverter(new javafx.util.StringConverter<Location>() {
                @Override
                public String toString(Location location) {
                    return location == null ? null : location.getName() + " (" + location.getLocationID() + ")";
                }

                @Override
                public Location fromString(String string) {
                    return null;
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
            showError("Could not load locations.");
        }
    }

    /**
     =======================================================
     This function controls the type of vehicle selected
     =======================================================
     */
    @FXML void handleTypeSelected(MouseEvent event) {
        // ... (This method is correct, no changes needed)
        Pane selectedPane = (Pane) event.getSource();
        scooterPane.getStyleClass().remove("vehicle-pane-selected");
        bikePane.getStyleClass().remove("vehicle-pane-selected");
        trikePane.getStyleClass().remove("vehicle-pane-selected");
        if (selectedPane == scooterPane) {
            selectedVehicleType = "E-Scooter";
            scooterPane.getStyleClass().add("vehicle-pane-selected");
        } else if (selectedPane == bikePane) {
            selectedVehicleType = "E-Bike";
            bikePane.getStyleClass().add("vehicle-pane-selected");
        } else if (selectedPane == trikePane) {
            selectedVehicleType = "E-Trike";
            trikePane.getStyleClass().add("vehicle-pane-selected");
        }
        rentalFormPane.setVisible(true);
        errorLabel.setVisible(false);
    }

    /**
     ===========================================================
     This function validates details, finds a matching vehicle,
     and creates the rental.
     ===========================================================
     */
    @FXML
    void handleConfirmRent() {

        if (mainController == null) {
            System.err.println("Client_rentController: Main controller is null!");
            return;
        }

        LocalDate startDate = datePicker.getValue();
        LocalDate endDate = returnDatePicker.getValue();
        Location selectedLocation = locationComboBox.getValue(); // Get Location object
        String startTime = startTimeField.getText();
        String endTime = endTimeField.getText();

        if (selectedVehicleType == null || startDate == null || endDate == null || selectedLocation == null || startTime.isEmpty() || endTime.isEmpty()) {
            showError("Please fill in all fields (Type, Location, Start Date, Return Date, Start Time, and End Time).");
            return;
        }

        String locationID = selectedLocation.getLocationID();
        LocalDate today = LocalDate.now();

        if (startDate.isBefore(today)) {
            showError("The Start Date cannot be a past date.");
            return;
        }
        if (endDate.isBefore(startDate)) {
            showError("The Return Date cannot be before the Start Date.");
            return;
        }

        LocalTime startLocalTime;
        LocalTime endLocalTime;

        try {

            DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("h:mm a", Locale.ENGLISH);
            startLocalTime = LocalTime.parse(startTime, timeFormatter);
            endLocalTime = LocalTime.parse(endTime, timeFormatter);

            if (startDate.isEqual(endDate) && (endLocalTime.isBefore(startLocalTime) || endLocalTime.equals(startLocalTime))) {
                showError("For a single-day rental, the End Time must be after the Start Time.");
                return;
            }

        } catch (DateTimeParseException e) {
            showError("Invalid time format. Please use 'h:mm a' (e.g., '8:00 AM' or '1:30 PM').");
            return;
        }

        LocalDateTime startDateTime = LocalDateTime.of(startDate, startLocalTime);
        LocalDateTime endDateTime = LocalDateTime.of(endDate, endLocalTime);

        if (startDateTime.isBefore(LocalDateTime.now())) {
            showError("The selected start date and time have already passed. Please select a future time.");
            return;
        }

        try {

            List<Vehicle> vehicleOfType = vehicleDAO.getVehiclesByType(selectedVehicleType);
            Vehicle vehicleToRent = null;
            for (Vehicle v: vehicleOfType){
                if (v.getStatus().equalsIgnoreCase("Available")){
                    vehicleToRent = v;
                    break;
                }
            }

            if (vehicleToRent == null){
                showError("Sorry, no " + selectedVehicleType + " vehicles are available at this time.");
                return;
            }

            String newRentalID = "R" + (System.currentTimeMillis() % 1000000);
            String newDeploymentID = "D" + newRentalID;

            RentalTransaction newRental = new RentalTransaction();
            newRental.setRentalID(newRentalID);
            newRental.setCustomerID(loggedInCustomer.getCustomerID());
            newRental.setPlateID(vehicleToRent.getPlateID());
            newRental.setLocationID(locationID);
            newRental.setStartDateTime(Timestamp.valueOf(startDateTime));
            newRental.setEndDateTime(Timestamp.valueOf(endDateTime));

            boolean rentalCreated = rentalDAO.insertRental(newRental);
            if (!rentalCreated) {
                if (rentalDAO.getRentalById(newRentalID) != null) {
                    showError("A temporary ID conflict occurred. Please try again.");
                } else {
                    showError("A database error occurred. Could not create the rental.");
                }
                return;
            }

            vehicleDAO.updateVehicleStatus(vehicleToRent.getPlateID(), "In Use");

            FXMLLoader loader = new FXMLLoader(getClass().getResource("Client-deployment.fxml"));
            Parent page = loader.load();
            Client_deploymentController deploymentController = loader.getController();
            deploymentController.initData(newRental, vehicleToRent, newDeploymentID);
            deploymentController.setMainController(mainController);
            mainController.loadPageFromSub(page);

        } catch (IOException e) {
            e.printStackTrace();
            showError("Could not load deployment page.");
        } catch (Exception e) {
            e.printStackTrace();
            showError("An unexpected error occurred: " + e.getMessage());
        }
    }

    @FXML void handleCancel() {
        if (mainController != null) {
            mainController.handleHome(null);
        }
    }

    private void showError(String message) {
        errorLabel.setText(message);
        errorLabel.setVisible(true);
    }
}