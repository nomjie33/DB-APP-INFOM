package main.gui;

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

    @FXML private TableColumn<RentalTransaction, Void> editColumn;
    @FXML private TableColumn<RentalTransaction, Void> actionColumn;
    @FXML private ComboBox<String> statusFilterComboBox;

    private RentalDAO rentalDAO = new RentalDAO();
    private Admin_dashboardController mainController;

    private final DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    public void setMainController(Admin_dashboardController mainController) {
        this.mainController = mainController;
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {

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
            String text = (ts != null) ? ts.toLocalDateTime().format(dtf) : "Not Started";
            return new SimpleStringProperty(text);
        });
        endColumn.setCellValueFactory(cellData -> {
            Timestamp ts = cellData.getValue().getEndDateTime();
            String text = (ts != null) ? ts.toLocalDateTime().format(dtf) : "Not Ended";
            return new SimpleStringProperty(text);
        });

        statusColumn.setCellValueFactory(new PropertyValueFactory<>("status"));

        statusFilterComboBox.setItems(FXCollections.observableArrayList("Active", "Completed", "Cancelled", "All"));
        statusFilterComboBox.setValue("Active");
        statusFilterComboBox.setOnAction(e -> loadRentalRecords());

        setupEditButtonColumn();
        setupActionColumn();
        loadRentalRecords();
    }

    private void handleEditRental(RentalTransaction rental) {
        mainController.loadRentalForm(rental);
    }

    /**
     * Shows the "Edit" button for ALL records, including "Completed".
     */
    private void setupEditButtonColumn() {
        Callback<TableColumn<RentalTransaction, Void>, TableCell<RentalTransaction, Void>> cellFactory = col -> new TableCell<>() {
            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || getTableRow() == null || getTableRow().getItem() == null) {
                    setGraphic(null);
                } else {
                    Button btn = new Button("Edit");
                    btn.getStyleClass().add("edit-button");
                    btn.setOnAction(event -> {
                        RentalTransaction rentalOnClick = getTableRow().getItem();
                        if (rentalOnClick != null) {
                            handleEditRental(rentalOnClick);
                        }
                    });
                    setGraphic(btn);
                }
            }
        };
        editColumn.setCellFactory(cellFactory);
    }

    /**
     * Loads rentals based on the filter.
     */
    public void loadRentalRecords() {
        try {
            rentalTable.getItems().clear();
            String statusFilter = statusFilterComboBox.getValue();
            List<RentalTransaction> rentals;

            if ("All".equals(statusFilter)) {
                rentals = rentalDAO.getAllRentalsIncludingCancelled();
            } else if ("Active".equals(statusFilter)) {
                rentals = rentalDAO.getRentalsByStatus("Active");
            } else {
                rentals = rentalDAO.getRentalsByStatus(statusFilter);
            }

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

    /**
     * Sets up the action buttons for "Cancel" and "Reactivate".
     */
    private void setupActionColumn() {
        Callback<TableColumn<RentalTransaction, Void>, TableCell<RentalTransaction, Void>> cellFactory = new Callback<>() {
            @Override
            public TableCell<RentalTransaction, Void> call(final TableColumn<RentalTransaction, Void> param) {
                final TableCell<RentalTransaction, Void> cell = new TableCell<>() {
                    @Override
                    public void updateItem(Void item, boolean empty) {
                        super.updateItem(item, empty);
                        if (empty || getTableRow() == null || getTableRow().getItem() == null) {
                            setGraphic(null);
                        } else {
                            RentalTransaction rental = getTableRow().getItem();
                            Button btn = new Button();

                            if ("Active".equals(rental.getStatus())) {
                                btn.setText("Cancel");
                                btn.getStyleClass().add("deactivate-button");
                                btn.setOnAction(e -> handleCancelReactivate(rental));
                                setGraphic(btn);
                            } else if ("Cancelled".equals(rental.getStatus())) {
                                btn.setText("Reactivate");
                                btn.getStyleClass().add("edit-button");
                                btn.setOnAction(e -> handleCancelReactivate(rental));
                                setGraphic(btn);
                            } else {
                                setGraphic(null);
                            }
                        }
                    }
                };
                return cell;
            }
        };
        actionColumn.setCellFactory(cellFactory);
    }

    /**
     * Handler for the "Cancel" / "Reactivate" buttons.
     */
    private void handleCancelReactivate(RentalTransaction rental) {
        String action = "Active".equals(rental.getStatus()) ? "Cancellation" : "Revert Cancellation";

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmation");
        alert.setHeaderText("Confirm " + action);
        alert.setContentText("Are you sure you want to " + action + " rental: " + rental.getRentalID() + "?");

        if (alert.showAndWait().get() == ButtonType.OK) {
            boolean success;
            if ("Active".equals(rental.getStatus())) {
                success = rentalDAO.cancelRental(rental.getRentalID());
            } else {
                success = rentalDAO.reactivateRental(rental.getRentalID());
            }

            if (success) {
                showAlert(Alert.AlertType.INFORMATION, "Success", "Rental status has been updated.");
                loadRentalRecords();
            } else {
                showAlert(Alert.AlertType.ERROR, "Error", "Failed to update rental status.");
            }
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