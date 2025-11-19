package main.gui;

import dao.RentalDAO;
import model.Customer;
import model.RentalTransaction;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;

import java.io.IOException;
import java.sql.Timestamp;
import java.time.LocalDateTime;

public class Client_returnVehicleController {

    private Client_dashboardController mainController;
    private Customer loggedInCustomer;
    private RentalTransaction activeRental;
    private RentalDAO rentalDAO = new RentalDAO();

    @FXML private Label rentalIdLabel;
    @FXML private Label customerIdLabel;
    @FXML private Label vehiclePlateLabel;
    @FXML private Label startTimeLabel;
    @FXML private Button confirmReturnButton;

    public void initData(Customer customer, RentalTransaction rental) {
        this.loggedInCustomer = customer;
        this.activeRental = rental;

        if (activeRental != null) {
            rentalIdLabel.setText(rental.getRentalID());
            vehiclePlateLabel.setText(rental.getPlateID());

            if (rental.getStartDateTime() != null) {
                startTimeLabel.setText(rental.getStartDateTime().toString());
                confirmReturnButton.setDisable(false);
            } else {

                startTimeLabel.setText("PENDING (Admin must start)");
                confirmReturnButton.setDisable(true);
            }

            System.out.println("Loaded Active Rental: " + activeRental.getRentalID());
        }
    }

    public void setMainController(Client_dashboardController mainController) {
        this.mainController = mainController;
    }

    @FXML
    void handleConfirmReturn() {

        if (activeRental == null) {
            System.err.println("No active rental found to complete.");
            return;
        }

        Timestamp returnTimestamp = new Timestamp(System.currentTimeMillis());
        activeRental.setEndDateTime(returnTimestamp);
        activeRental.setStatus("Completed");

        boolean success = rentalDAO.updateRental(activeRental);

        if (success) {
            System.out.println("Rental successfully finalized in DB: " + activeRental.getRentalID());

            loadFinalPaymentScene(activeRental);
        } else {
            System.err.println("Error finalizing rental return in DB. Check DAO.");
        }
    }

    private void loadFinalPaymentScene(RentalTransaction rental) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("Client-payment.fxml"));
            Parent page = loader.load();

            Client_paymentController paymentController = loader.getController();

            paymentController.initData(loggedInCustomer, rental);
            paymentController.setMainController(mainController);

            mainController.loadPageFromSub(page);

        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("Failed to load Client-payment.fxml.");
            mainController.handleHome(null);
        }
    }

    @FXML
    void handleCancel() {
        if (mainController != null) {
            mainController.handleHome(null);
        }
    }
}