package main.gui;

import dao.*;
import model.*;
import service.PaymentService;
import service.RentalService;

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
import java.util.List;

public class Client_rentController {

    private Client_dashboardController mainController;
    private Customer loggedInCustomer;

    // DAOs and Services
    private RentalDAO rentalDAO = new RentalDAO();
    private LocationDAO locationDAO = new LocationDAO();
    private VehicleDAO vehicleDAO = new VehicleDAO();
    private PaymentDAO paymentDAO = new PaymentDAO();
    private PaymentService paymentService = new PaymentService();
    private RentalService rentalService;

    private String selectedVehicleType = null;
    private RentalTransaction activeRental;

    // FXML Fields
    @FXML private Pane scooterPane;
    @FXML private Pane bikePane;
    @FXML private Pane trikePane;
    @FXML private VBox rentalFormPane;
    @FXML private ComboBox<Location> locationComboBox;
    @FXML private DatePicker pickUpDatePicker;
    @FXML private TextField startTimeField;
    @FXML private Label errorLabel;
    @FXML private Button confirmButton;
    @FXML private Button cancelButton;

    public void setMainController(Client_dashboardController mainController){
        this.mainController = mainController;

        this.rentalService = new RentalService(
                new CustomerDAO(),
                vehicleDAO,
                locationDAO,
                rentalDAO,
                paymentDAO,
                paymentService
        );

        try {
            List<Location> locations = locationDAO.getAllLocations();
            locationComboBox.getItems().addAll(locations);
            locationComboBox.setConverter(new javafx.util.StringConverter<>() {
                @Override
                public String toString(Location location) {
                    return location == null ? null : location.getName() + " (" + location.getLocationID() + ")";
                }
                @Override
                public Location fromString(String string) { return null; }
            });
        } catch (Exception e) {
            e.printStackTrace();
            showError("Could not load locations.");
        }
    }

    public void initData(Customer customer) {
        this.loggedInCustomer = customer;

    }

    @FXML
    void handleTypeSelected(MouseEvent event) {
        Pane selectedPane = (Pane) event.getSource();
        scooterPane.getStyleClass().remove("vehicle-pane-selected");
        bikePane.getStyleClass().remove("vehicle-pane-selected");
        trikePane.getStyleClass().remove("vehicle-pane-selected");

        if (selectedPane == scooterPane) selectedVehicleType = "E-Scooter";
        else if (selectedPane == bikePane) selectedVehicleType = "E-Bike";
        else if (selectedPane == trikePane) selectedVehicleType = "E-Trike";

        selectedPane.getStyleClass().add("vehicle-pane-selected");
        rentalFormPane.setVisible(true);
        errorLabel.setVisible(false);
    }

    @FXML
    void handleConfirmRent() {
        Location location = locationComboBox.getValue();
        LocalDate startDate = pickUpDatePicker.getValue();
        String startTimeStr = startTimeField.getText();

        if (selectedVehicleType == null || location == null || startDate == null || startTimeStr.isEmpty()) {
            showError("Please select vehicle type, location, date, and time.");
            return;
        }

        Timestamp pickUpDateTime;
        try {
            LocalTime startTime = LocalTime.parse(startTimeStr.trim());
            pickUpDateTime = Timestamp.valueOf(LocalDateTime.of(startDate, startTime));
        } catch (java.time.format.DateTimeParseException e) {
            showError("Invalid time format. Please use HH:mm (e.g., 09:30).");
            return;
        }

        List<Vehicle> vehiclesAtLocation = vehicleDAO.getVehiclesByLocation(location.getLocationID());
        String availablePlateID = null;

        for (Vehicle v : vehiclesAtLocation) {
            if (v.getVehicleType().equals(selectedVehicleType) && v.getStatus().equals("Available")) {
                availablePlateID = v.getPlateID();
                break;
            }
        }

        if (availablePlateID == null) {
            showError("Sorry, no " + selectedVehicleType + "s are available at " + location.getName() + " right now.");
            return;
        }

        String newRentalID = rentalService.bookRental(
                loggedInCustomer.getCustomerID(),
                availablePlateID,
                location.getLocationID(),
                pickUpDateTime
        );

        if (newRentalID == null) {
            showError("Failed to create rental. Please try again.");
            return;
        }

        mainController.handleRentVehicle(null);
    }

    @FXML
    void handleCancel() {
        if (mainController != null) mainController.handleHome(null);
    }

    private void showError(String message) {
        errorLabel.setText(message);
        errorLabel.setVisible(true);
    }
}