package main.gui;

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
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.input.MouseEvent;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class Client_homeController implements Initializable {

    // --- All the TableView code is now here ---

    private RentalDAO rentalDAO = new RentalDAO();
    private PenaltyDAO penaltyDAO = new PenaltyDAO();

    private Client_dashboardController mainController;

    @FXML
    private TableView<Transaction> transactionsTable;

    @FXML
    private TableColumn<Transaction, String> dateColumn;

    @FXML
    private TableColumn<Transaction, String> idColumn;

    @FXML
    private TableColumn<Transaction, String> categoryColumn;

    @FXML private Label clientNameLabel;

    @FXML
    void handleRentShortcut(MouseEvent e){
        if (mainController != null){
            mainController.handleRentVehicle(null);
        } else {
            System.err.println("ERROR: Cannot execute the action.");
        }
    }

    @FXML
    void handlePenaltyShortcut(MouseEvent e){
        if (mainController != null){
            mainController.handleResolvePenalty(null);
        } else {
            System.err.println("ERROR: Cannot execute the action.");
        }
    }

    @FXML
    void handleHistoryShortcut(MouseEvent e){
        if (mainController != null){
            mainController.handleTransactionHistory(null);
        } else {
            System.err.println("ERROR: Cannot execute the action.");
        }
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

    }

    public void initData(Customer loggedInCustomer){
        if (loggedInCustomer == null){
            return;
        }

        if (clientNameLabel != null){
            clientNameLabel.setText(loggedInCustomer.getFirstName() + " " + loggedInCustomer.getLastName());
        }

        loadRecentTransaction(loggedInCustomer.getCustomerID());
    }

    public void updateUserInfo(String fullName) {
        if (clientNameLabel != null) {
            clientNameLabel.setText(fullName);
        }
    }

    public void setMainController(Client_dashboardController mainController) {
        this.mainController = mainController;
    }

    private void loadRecentTransaction(String customerId){
        dateColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getDate()));
        idColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getTransactionId()));
        categoryColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getCategory()));

        ObservableList<Transaction> recentTransactions = getTableData(customerId);
        transactionsTable.setItems(recentTransactions);
    }

    private ObservableList<Transaction> getTableData(String customerId){

        List<Transaction> allTransactions = new ArrayList<>();

        try {
            List<RentalTransaction> rentals = rentalDAO.getRentalsByCustomer(customerId);

            for (RentalTransaction rental: rentals){
                String date = rental.getStartDateTime().toString().substring(0, 10);
                allTransactions.add(new Transaction(date, rental.getRentalID(), "Vehicle Rental"));

                List<PenaltyTransaction> penalties = penaltyDAO.getPenaltiesByRental(rental.getRentalID());
                for (PenaltyTransaction penalty: penalties){
                    String penaltyDate = penalty.getDateIssued().toString();
                    allTransactions.add(new Transaction(penaltyDate, penalty.getPenaltyID(), "Penalty Issue"));
                }
            }
        } catch (Exception ex){
            ex.printStackTrace();
        }

        return FXCollections.observableArrayList((allTransactions));
    }

    public static class Transaction {
        private final String date;
        private final String transactionId;
        private final String category;

        public Transaction(String date, String transactionId, String category) {
            this.date = date;
            this.transactionId = transactionId;
            this.category = category;
        }

        public String getDate() { return date; }
        public String getTransactionId() { return transactionId; }
        public String getCategory() { return category; }
    }
}