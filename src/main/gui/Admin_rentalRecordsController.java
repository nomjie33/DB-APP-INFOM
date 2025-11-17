package main.gui;

import dao.*;
import model.RentalTransaction;
import service.PaymentService;
import service.RentalService;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.util.Callback;
import javafx.beans.property.SimpleStringProperty;

import java.net.URL;
import java.sql.Timestamp;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.ResourceBundle;

public class Admin_rentalRecordsController implements Initializable {

    @FXML private Label rentalCountLabel;
    @FXML private ComboBox<String> statusFilterComboBox;
    @FXML private TableView<RentalTransaction> rentalTable;
    @FXML private TableColumn<RentalTransaction, String> rentalIDColumn;
    @FXML private TableColumn<RentalTransaction, String> customerIDColumn;
    @FXML private TableColumn<RentalTransaction, String> plateIDColumn;
    @FXML private TableColumn<RentalTransaction, String> locationIDColumn;
    @FXML private TableColumn<RentalTransaction, String> pickUpColumn;
    @FXML private TableColumn<RentalTransaction, String> startColumn;
    @FXML private TableColumn<RentalTransaction, String> endColumn;
    @FXML private TableColumn<RentalTransaction, String> statusColumn;
    @FXML private TableColumn<RentalTransaction, Void> editColumn; // Correctly mapped
    @FXML private TableColumn<RentalTransaction, Void> actionColumn; // Correctly mapped

    private RentalDAO rentalDAO = new RentalDAO();
    private RentalService rentalService;

    private Admin_dashboardController mainController;
    private final DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    public void setMainController(Admin_dashboardController mainController) {
        this.mainController = mainController;


        this.rentalService = new RentalService(
                new CustomerDAO(),
                new VehicleDAO(),
                new LocationDAO(),
                rentalDAO,
                new PaymentDAO(),
                new PaymentService()
        );
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {

        rentalIDColumn.setCellValueFactory(new PropertyValueFactory<>("rentalID"));
        customerIDColumn.setCellValueFactory(new PropertyValueFactory<>("customerID"));
        plateIDColumn.setCellValueFactory(new PropertyValueFactory<>("plateID"));
        locationIDColumn.setCellValueFactory(new PropertyValueFactory<>("locationID"));
        statusColumn.setCellValueFactory(new PropertyValueFactory<>("status"));

        pickUpColumn.setCellValueFactory(cellData -> new SimpleStringProperty(formatTimestamp(cellData.getValue().getPickUpDateTime())));
        startColumn.setCellValueFactory(cellData -> new SimpleStringProperty(formatTimestamp(cellData.getValue().getStartDateTime())));
        endColumn.setCellValueFactory(cellData -> new SimpleStringProperty(formatTimestamp(cellData.getValue().getEndDateTime())));

        statusFilterComboBox.setItems(FXCollections.observableArrayList("Active", "Completed", "Cancelled", "All"));
        statusFilterComboBox.setOnAction(e -> loadRentalData());
        statusFilterComboBox.setValue("Active");

        setupEditButtonColumn();
        setupActionColumn();

        loadRentalData();
    }

    private String formatTimestamp(Timestamp ts) {
        return (ts != null) ? ts.toLocalDateTime().format(dtf) : "---";
    }

    public void loadRentalData() {
        String statusFilter = statusFilterComboBox.getValue();
        List<RentalTransaction> rentalList;

        try {
            if ("All".equals(statusFilter)) {
                rentalList = rentalDAO.getAllRentalsIncludingCancelled();
            } else if ("Completed".equals(statusFilter)) {
                rentalList = rentalDAO.getCompletedRentals();
            } else if ("Cancelled".equals(statusFilter)) {
                rentalList = rentalDAO.getAllRentalsIncludingCancelled().stream()
                        .filter(r -> "Cancelled".equals(r.getStatus()))
                        .toList();
            } else {

                rentalList = rentalDAO.getActiveRentals();
            }

            ObservableList<RentalTransaction> obsList = FXCollections.observableArrayList(rentalList);
            rentalTable.setItems(obsList);
            rentalCountLabel.setText("(" + obsList.size() + ") RENTALS:");
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Error", "Failed to load rental records.");
            e.printStackTrace();
        }
    }

    private void setupEditButtonColumn() {
        Callback<TableColumn<RentalTransaction, Void>, TableCell<RentalTransaction, Void>> cellFactory = col -> new TableCell<>() {
            private final Button btn = new Button("Edit");
            {
                btn.getStyleClass().add("edit-button");
                btn.setOnAction(event -> {
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

        editColumn.setCellFactory(cellFactory);
    }

    private void setupActionColumn() {
        Callback<TableColumn<RentalTransaction, Void>, TableCell<RentalTransaction, Void>> cellFactory = col -> new TableCell<>() {
            private final Button btn = new Button();
            {
                btn.setOnAction(event -> {
                    RentalTransaction rental = getTableView().getItems().get(getIndex());
                    if (rental.getStartDateTime() == null) {
                        handleStartRental(rental);
                    } else {
                        handleEndRental(rental);
                    }
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    RentalTransaction rental = getTableView().getItems().get(getIndex());

                    if ("Active".equals(rental.getStatus()) && rental.getStartDateTime() == null) {
                        btn.setText("Start");
                        btn.getStyleClass().clear();
                        btn.getStyleClass().add("edit-button");
                        setGraphic(btn);
                    } else if ("Active".equals(rental.getStatus()) && rental.getStartDateTime() != null) {
                        btn.setText("End");
                        btn.getStyleClass().clear();
                        btn.getStyleClass().add("deactivate-button");
                        setGraphic(btn);
                    } else {
                        setGraphic(null);
                    }
                }
            }
        };

        actionColumn.setCellFactory(cellFactory);
    }

    private void handleStartRental(RentalTransaction rental) {
        System.out.println("PHASE 2: Starting rental: " + rental.getRentalID());

        boolean success = rentalService.startRental(rental.getRentalID());

        if (success) {
            showAlert(Alert.AlertType.INFORMATION, "Rental Started", "Rental " + rental.getRentalID() + " has been started.");
            loadRentalData();
        } else {
            showAlert(Alert.AlertType.ERROR, "Error", "Failed to start rental. Check console/service logs.");
        }
    }

    private void handleEndRental(RentalTransaction rental) {
        System.out.println("PHASE 3A: Ending rental: " + rental.getRentalID());

        rental.setEndDateTime(new Timestamp(System.currentTimeMillis()));
        rental.setStatus("Completed");

        boolean success = rentalDAO.updateRental(rental);

        if (success) {
            showAlert(Alert.AlertType.INFORMATION, "Rental Ended", "Rental " + rental.getRentalID() + " has been completed.");
            loadRentalData(); // Refresh the table
        } else {
            showAlert(Alert.AlertType.ERROR, "Error", "Failed to end rental.");
        }
    }

    private void handleEditRental(RentalTransaction rental) {
        System.out.println("Edit clicked for rental: " + rental.getRentalID());
        mainController.loadRentalForm(rental);
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