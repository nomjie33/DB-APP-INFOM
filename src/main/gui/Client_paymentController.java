package main.gui;

import dao.*;
import model.Customer;
import model.PaymentTransaction;
import model.RentalTransaction;
import model.Vehicle;
import service.DeploymentService;
import service.PaymentService;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Date;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Client_paymentController {

    @FXML private Label plateNumberLabel;
    @FXML private Label rentingDateLabel;
    @FXML private Label paymentDateLabel;
    @FXML private Label timeLabel;
    @FXML private Label priceLabel;
    @FXML private Button confirmButton;

    // Your DAOs and Services are perfect
    private VehicleDAO vehicleDAO = new VehicleDAO();
    private RentalDAO rentalDAO = new RentalDAO();
    private PaymentService paymentService = new PaymentService();
    private DeploymentService deploymentService = new DeploymentService(new DeploymentDAO(), new VehicleDAO(), new LocationDAO());
    private PaymentDAO paymentDAO = new PaymentDAO();

    private Client_dashboardController mainController;
    private Customer loggedInCustomer;
    private RentalTransaction finalRental;
    private Vehicle foundVehicle;
    private BigDecimal totalCost;

    public void setMainController(Client_dashboardController mainController) {
        this.mainController = mainController;
    }

    public void initData(Customer customer, RentalTransaction completedRental) {
        this.loggedInCustomer = customer;
        this.finalRental = completedRental;

        this.foundVehicle = vehicleDAO.getVehicleById(completedRental.getPlateID());

        if (foundVehicle == null) {
            plateNumberLabel.setText("Error: Could not retrieve vehicle data.");
            confirmButton.setDisable(true);
            return;
        }

        try {

            this.totalCost = paymentService.calculateRentalFee(completedRental.getRentalID());

            if (this.totalCost.compareTo(BigDecimal.ZERO) <= 0) {
                plateNumberLabel.setText("Error: Invalid rental duration or cost.");
                confirmButton.setDisable(true);
                return;
            }

            LocalDateTime startDateTime = finalRental.getStartDateTime().toLocalDateTime();
            LocalDateTime endDateTime = finalRental.getEndDateTime().toLocalDateTime();

            DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("MMM dd, yyyy");
            DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("h:mm a");

            plateNumberLabel.setText(foundVehicle.getPlateID() + " (FINAL SETTLEMENT)");

            String actualPeriod = startDateTime.toLocalDate().format(dateFormatter) + " - " + endDateTime.toLocalDate().format(dateFormatter);
            rentingDateLabel.setText("Actual Rental: " + actualPeriod);

            timeLabel.setText(startDateTime.toLocalTime().format(timeFormatter) + " - " + endDateTime.toLocalTime().format(timeFormatter));

            paymentDateLabel.setText(LocalDate.now().format(dateFormatter));
            priceLabel.setText("TOTAL DUE: ₱" + String.format("%,.2f", totalCost));

            confirmButton.setText("Confirm Final Payment");
            confirmButton.setDisable(false);

        } catch (Exception e) {
            e.printStackTrace();
            plateNumberLabel.setText("Error during final cost calculation.");
            confirmButton.setDisable(true);
        }
    }

    @FXML
    void handleConfirmPayment() {
        if (finalRental == null) {
            System.err.println("No completed rental data found for payment.");
            return;
        }

        try {
            String rentalID = finalRental.getRentalID();
            Date paymentDate = Date.valueOf(LocalDate.now());

            boolean paymentSuccess = paymentService.finalizePaymentForRental(rentalID, totalCost, paymentDate);

            if (paymentSuccess) {
                System.out.println("Final Payment recorded for Rental ID: " + rentalID);
                mainController.handleHome(null);
            } else {
                System.err.println("Database error: Failed to record final payment.");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    void handleCancel() {
        if (mainController != null) {
            mainController.handleHome(null);
        }
    }
}