package main.gui;

import model.RentalTransaction;
import model.Vehicle;
import javafx.fxml.FXML;
import javafx.scene.control.Label;

import java.sql.Timestamp;
import java.time.format.DateTimeFormatter;


public class Client_deploymentController {

    @FXML private Label rentalIdLabel;
    @FXML private Label deploymentIdLabel;
    @FXML private Label plateNumberLabel;
    @FXML private Label locationIdLabel;
    @FXML private Label dateLabel;
    @FXML private Label startTimeLabel;
    @FXML private Label endTimeLabel;

    private Client_dashboardController mainController;

    public void initData(RentalTransaction rental, Vehicle vehicle, String deploymentID){
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("MMMM d, yyyy");
        DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("h:mm a");

        rentalIdLabel.setText(rental.getRentalID());
        deploymentIdLabel.setText(deploymentID);
        plateNumberLabel.setText(vehicle.getPlateID() + " (" + vehicle.getVehicleModel() + ")");
        locationIdLabel.setText(rental.getLocationID());

        Timestamp startTS = rental.getStartDateTime();
        Timestamp endTS = rental.getEndDateTime();

        if (startTS != null) {
            dateLabel.setText(startTS.toLocalDateTime().format(dateFormatter));
            startTimeLabel.setText(startTS.toLocalDateTime().toLocalTime().format(timeFormatter));
        }
        if (endTS != null) {
            endTimeLabel.setText(endTS.toLocalDateTime().toLocalTime().format(timeFormatter));
        } else {
            endTimeLabel.setText("N/A (Open-ended Rental)");
        }

    }

    public void setMainController(Client_dashboardController mainController) {
        this.mainController = mainController;
    }

    @FXML
    void handleBackToHome() {
        if (mainController != null) {

            mainController.loadPage("Client-home.fxml");
        } else {
            System.err.println("Client_deploymentController: Main controller is null!");
        }
    }

}