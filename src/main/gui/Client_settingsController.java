package main.gui;

import dao.CustomerDAO;
import dao.PenaltyDAO;
import dao.RentalDAO;
import model.Customer;
import model.PenaltyTransaction;
import model.RentalTransaction;
import main.gui.Client_homeController.Transaction;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class Client_settingsController implements Initializable {

    private Client_dashboardController mainController;
    private Customer loggedInCustomer;

    private PenaltyDAO penaltyDAO = new PenaltyDAO();
    private CustomerDAO customerDAO = new CustomerDAO();
    private RentalDAO rentalDAO = new RentalDAO();

    // --- Profile FXML Elements ---
    @FXML private Label fullNameLabel;
    @FXML private Label penaltyStatusLabel;

    // --- History Table FXML Elements ---
    @FXML private TableView<Transaction> transactionsTable;
    @FXML private TableColumn<Transaction, String> dateColumn;
    @FXML private TableColumn<Transaction, String> idColumn;
    @FXML private TableColumn<Transaction, String> categoryColumn;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
    }

    public void setMainController(Client_dashboardController mainController) {
        this.mainController = mainController;
    }

    public void initData(Customer customer){
        this.loggedInCustomer = customer;
        System.out.println("Settings Controller initialized for CustomerL " + customer.getCustomerID());
        loadProfileData();

        setupTransactionTable();
        loadAllTransactions();
    }

    private void loadProfileData(){

        if(this.loggedInCustomer == null) return;
        String fullname = this.loggedInCustomer.getFirstName() + " " + this.loggedInCustomer.getLastName();

        List<PenaltyTransaction> unpaidPenalties = penaltyDAO.getPenaltiesByStatus("UNPAID");

        String penaltyStatus = "None";
        for (PenaltyTransaction penalty : unpaidPenalties){
            penaltyStatus = "ACTIVE: ID - " + penalty.getPenaltyID() + " (₱" + penalty.getTotalPenalty() + ")";
            break;
        }

        fullNameLabel.setText(fullname);
        penaltyStatusLabel.setText(penaltyStatus);

    }

    public void updateProfileInfo(String fullName, String penaltyStatus) {
        if (fullNameLabel != null) {
            fullNameLabel.setText(fullName);
        }
        if (penaltyStatusLabel != null) {
            penaltyStatusLabel.setText(penaltyStatus);
        }
    }

    private void setupTransactionTable() {
        dateColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getDate()));
        idColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getTransactionId()));
        categoryColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getCategory()));
    }

    private void loadAllTransactions(){
        ObservableList<Transaction> allTransactions = getFullHistoryData();
        transactionsTable.setItems(allTransactions);
    }

    private ObservableList<Transaction> getFullHistoryData(){

        if (this.loggedInCustomer == null){
            System.err.println("Error: Customer data not initialized");
            return FXCollections.observableArrayList();
        }

        ObservableList<Transaction> allTransactions = FXCollections.observableArrayList();
        String customerID = this.loggedInCustomer.getCustomerID();

        List<RentalTransaction> rentals = rentalDAO.getRentalsByCustomer(customerID);

        if (rentals != null){
            for (RentalTransaction rental: rentals){
                String id = rental.isCompleted() ? rental.getRentalID() : "ONGOING - " + rental.getRentalID();

                String dateStr = "Pending/N/A";
                if (rental.getStartDateTime() != null) {
                    dateStr = rental.getStartDateTime().toString();
                }

                allTransactions.add(new Transaction(dateStr, id, "Vehicle Rental"));

                List<PenaltyTransaction> penalties = penaltyDAO.getPenaltiesByRental(rental.getRentalID());
                if (penalties != null){
                    for (PenaltyTransaction penalty: penalties){

                        String penaltyDateStr = "N/A";
                        if (penalty.getDateIssued() != null) {
                            penaltyDateStr = penalty.getDateIssued().toString();
                        }

                        allTransactions.add(new Transaction(
                           penalty.getDateIssued().toString(),
                           penalty.getPenaltyID(),
                           "Penalty (" + penalty.getPenaltyStatus() + ")"
                        ));
                    }
                }

            }
        }

        return allTransactions;
    }
}