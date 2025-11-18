package main.gui;

import main.gui.Client_homeController.Transaction;
import dao.PenaltyDAO;
import dao.RentalDAO;
import model.Customer;
import model.PenaltyTransaction;
import model.RentalTransaction;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class Client_historyController implements Initializable {

    private Client_dashboardController mainController;
    private Customer loggedInCustomer;

    private RentalDAO rentalDAO = new RentalDAO();
    private PenaltyDAO penaltyDAO = new PenaltyDAO();

    @FXML private TableView<Transaction> transactionsTable;
    @FXML private TableColumn<Transaction, String> dateColumn;
    @FXML private TableColumn<Transaction, String> idColumn;
    @FXML private TableColumn<Transaction, String> categoryColumn;
    @FXML private Button backToHomeButton;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        setupTransactionTable();
    }

    public void initData(Customer customer){
        this.loggedInCustomer = customer;
        System.out.println("History Controller initialized for: " + customer.getCustomerID());
        loadAllTransactions();
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

        System.out.println("Fetching all transactions for Customer ID: " + customerID);

        List<RentalTransaction> rentals = rentalDAO.getRentalsByCustomer(customerID);

        if (rentals != null){
            for (RentalTransaction rental: rentals){
                String id = rental.isCompleted() ? rental.getRentalID() : "ONGOING - " + rental.getRentalID();

                String date;
                if (rental.getStartDateTime() != null) {
                    date = rental.getStartDateTime().toString().substring(0, 10);
                } else if (rental.getPickUpDateTime() != null) {
                    date = rental.getPickUpDateTime().toString().substring(0, 10);
                } else {
                    date = "Pending";
                }

                allTransactions.add(new Transaction(date, id, "Vehicle Rental"));

                List<PenaltyTransaction> penalties = penaltyDAO.getPenaltiesByRental(rental.getRentalID());
                if (penalties != null) {
                    for (PenaltyTransaction penalty : penalties) {
                        allTransactions.add(new Transaction(
                                penalty.getDateIssued().toString(),
                                penalty.getPenaltyID(),
                                "Penalty (" + penalty.getPenaltyStatus() + ")"
                        ));
                    }
                }
            }
        }

        System.out.println("Total transactions loaded: " + allTransactions.size());
        return allTransactions;
    }

    public void setMainController(Client_dashboardController mainController) {
        this.mainController = mainController;
    }

    @FXML
    void handleBackToHome() {
        if (mainController != null) {
            mainController.loadPage("Client-home.fxml");
        } else {
            System.err.println("Client_historyController: Main controller is null!");
        }
    }
}