package main.gui;

import dao.PenaltyDAO;
import dao.RentalDAO;
import model.Customer;
import model.PenaltyTransaction;
import model.RentalTransaction;
import service.PenaltyService;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;

import java.math.BigDecimal;
import java.sql.Date;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class Client_resolveController {

    private Client_dashboardController mainController;

    @FXML private Label penaltyIDLabel;
    @FXML private Label rentalIDLabel;
    @FXML private Label dateIssuedLabel;
    @FXML private Label penaltyAmountLabel;

    @FXML private Button confirmButton;
    @FXML private Button cancelButton;

    private Customer loggedInCustomer;
    private PenaltyTransaction currentPenalty;
    private PenaltyService penaltyService = new PenaltyService();
    private PenaltyDAO penaltyDAO = new PenaltyDAO();
    private RentalDAO rentalDAO = new RentalDAO();

    public void setMainController(Client_dashboardController mainController) {
        this.mainController = mainController;
    }

    public void initData(Customer customer){

        this.loggedInCustomer = customer;

        List<PenaltyTransaction> myUnpaidPenalties = penaltyDAO.getUnpaidPenaltiesByCustomer(customer.getCustomerID());

        if (myUnpaidPenalties.isEmpty()){

            penaltyIDLabel.setText("N/A");
            rentalIDLabel.setText("No unpaid penalties found.");
            dateIssuedLabel.setText("N/A");
            penaltyAmountLabel.setText("₱0.00");

            confirmButton.setDisable(false);
            confirmButton.setText("Back to Home");

            confirmButton.setOnAction(event -> handleBackToHome());
            cancelButton.setVisible(false);
            return;
        }

        this.currentPenalty = myUnpaidPenalties.get(0);
        loadPenaltyDetails(this.currentPenalty);
    }

    private void loadPenaltyDetails(PenaltyTransaction penalty){

        if(penalty == null) return;

        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("MMMM d, yyyy");
        String dateString;
        Date sqlDate = penalty.getDateIssued();

        if (sqlDate != null){
            dateString = sqlDate.toLocalDate().format(dateFormatter);
        } else {
            dateString = "Date Missing";
        }

        penaltyIDLabel.setText(penalty.getPenaltyID() != null ? penalty.getPenaltyID(): "N/A");
        rentalIDLabel.setText(penalty.getRentalID() != null ? penalty.getRentalID(): "N/A");
        dateIssuedLabel.setText(dateString);

        BigDecimal amount = penalty.getTotalPenalty();
        penaltyAmountLabel.setText("₱" + String.format("%.2f", amount));
        confirmButton.setDisable(false);

        confirmButton.setText("Pay Penalty");
        confirmButton.setOnAction(event -> handleConfirm());
        cancelButton.setVisible(true);
    }

    @FXML
    void handleBackToHome() {
        if (mainController != null) {
            mainController.loadPage("Client-home.fxml");
        } else {
            System.err.println("Client_resolveController: Main controller is null!");
        }
    }

    @FXML
    void handleConfirm(){

        if (currentPenalty == null){
            System.err.println("No penalty selected to resolve.");
            return;
        }

        boolean success = penaltyService.updatePenaltyPayment(currentPenalty.getPenaltyID(), "PAID");

        if (success){

            System.out.println("Penalty " + currentPenalty.getPenaltyID() + " successfully resolved.");

            if (mainController != null){
                mainController.loadPage("Client-history.fxml");
            }

        } else {
            System.err.println("Failed to update penalty status.");
        }

    }

    @FXML
    void handleCancel(){
        System.out.println("Penalty resolution cancelled.");
        if (mainController != null){
            mainController.loadPage("Client-home.fxml");
        }
    }

}
