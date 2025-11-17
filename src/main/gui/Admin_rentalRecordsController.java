package main.gui;

import dao.CustomerDAO;
import model.Customer;
import javafx.collections.FXCollections;
import javafx.util.StringConverter;
import java.util.List;

import dao.RentalDAO;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.util.Callback;
import model.RentalTransaction;

import java.net.URL;
import java.sql.Timestamp;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.ResourceBundle;

import javafx.beans.property.SimpleStringProperty;

public class Admin_rentalRecordsController implements Initializable {

    @FXML private Label rentalCountLabel;

    @FXML private TableView<RentalTransaction> rentalTable;
    @FXML private TableColumn<RentalTransaction, String> rentalIDColumn;
    @FXML private TableColumn<RentalTransaction, String> customerIDColumn;
    @FXML private TableColumn<RentalTransaction, String> plateIDColumn;
    @FXML private TableColumn<RentalTransaction, String> locationIDColumn;
    @FXML private TableColumn<RentalTransaction, String> pickUpColumn;
    @FXML private TableColumn<RentalTransaction, String> startColumn;
    @FXML private TableColumn<RentalTransaction, String> endColumn;
    @FXML private TableColumn<RentalTransaction, String> statusColumn;
    @FXML private TableColumn<RentalTransaction, Void> actionColumn;

    private RentalDAO rentalDAO = new RentalDAO();
    private Admin_dashboardController mainController;

    private final DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    public void setMainController(Admin_dashboardController mainController) {
        this.mainController = mainController;
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // Setup columns
        rentalIDColumn.setCellValueFactory(new PropertyValueFactory<>("rentalID"));
        customerIDColumn.setCellValueFactory(new PropertyValueFactory<>("customerID"));
        plateIDColumn.setCellValueFactory(new PropertyValueFactory<>("plateID"));
        locationIDColumn.setCellValueFactory(new PropertyValueFactory<>("locationID"));

        pickUpColumn.setCellValueFactory(cellData -> {
            Timestamp ts = cellData.getValue().getPickUpDateTime();
            String text = (ts != null) ? ts.toLocalDateTime().format(dtf) : "";
            return new SimpleStringProperty(text);
        });

        startColumn.setCellValueFactory(cellData -> {
            Timestamp ts = cellData.getValue().getStartDateTime();
            String text = (ts != null) ? ts.toLocalDateTime().format(dtf) : "";
            return new SimpleStringProperty(text);
        });

        endColumn.setCellValueFactory(cellData -> {
            Timestamp ts = cellData.getValue().getEndDateTime();
            String text = (ts != null) ? ts.toLocalDateTime().format(dtf) : "";
            return new SimpleStringProperty(text);
        });

        statusColumn.setCellValueFactory(new PropertyValueFactory<>("status"));

        // Setup Edit button and load data
        setupEditButtonColumn();
        loadRentalRecords();
    }

    private void handleEditRental(RentalTransaction rental) {
        System.out.println("Edit clicked for rental: " + rental.getRentalID());
        mainController.loadRentalForm(rental);
    }

    private void setupEditButtonColumn() {
        Callback<TableColumn<RentalTransaction, Void>, TableCell<RentalTransaction, Void>> cellFactory = col -> new TableCell<>() {
            private final Button btn = new Button("Edit");
            {
                btn.getStyleClass().add("edit-button");
                btn.setOnAction(event -> {
                    // Get the correct item using getIndex(), like the working Penalty table
                    RentalTransaction rental = getTableView().getItems().get(getIndex());
                    handleEditRental(rental);
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : btn);
            }
        };

        actionColumn.setCellFactory(cellFactory);
    }

    public void loadRentalRecords() {
        try {
            List<RentalTransaction> rentals = rentalDAO.getAllRentals();
            ObservableList<RentalTransaction> obsList = FXCollections.observableArrayList(rentals);
            rentalTable.setItems(obsList);
            rentalCountLabel.setText("(" + rentals.size() + ") RENTALS:");
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Error", "Failed to load rental records.");
            e.printStackTrace();
        }
    }

    @FXML
    private void handleAddRental(ActionEvent event) {
        if (mainController != null) {
            mainController.loadRentalForm(null);
        }
    }

    private void showAlert(Alert.AlertType type, String title, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}
