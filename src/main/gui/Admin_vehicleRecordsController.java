package main.gui;

import dao.VehicleDAO;
import model.Vehicle;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.util.Callback;

import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class Admin_vehicleRecordsController implements  Initializable {

    @FXML private Label vehicleCountLabel;
    @FXML private TableView<Vehicle> vehicleTable;
    @FXML private TableColumn<Vehicle, String> plateColumn;
    @FXML private TableColumn<Vehicle, String> typeColumn;
    @FXML private TableColumn<Vehicle, Double> priceColumn;
    @FXML private TableColumn<Vehicle, String> statusColumn;
    @FXML private TableColumn<Vehicle, Void> editColumn;

    @FXML private TableColumn<Vehicle, Void> actionColumn;
    @FXML private ComboBox<String> statusFilterComboBox;

    private VehicleDAO vehicleDAO = new VehicleDAO();
    private Admin_dashboardController mainController;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        plateColumn.setCellValueFactory(new PropertyValueFactory<>("plateID"));
        typeColumn.setCellValueFactory(new PropertyValueFactory<>("vehicleType"));
        priceColumn.setCellValueFactory(new PropertyValueFactory<>("rentalPrice"));
        statusColumn.setCellValueFactory(new PropertyValueFactory<>("status"));

        statusFilterComboBox.setItems(FXCollections.observableArrayList("Available", "Inactive", "All"));
        statusFilterComboBox.setValue("Available");
        statusFilterComboBox.setOnAction(e -> loadVehicleData());

        loadVehicleData();
        setupEditButtonColumn();

        setupActionColumn();
    }

    public void setMainController(Admin_dashboardController mainController) {
        this.mainController = mainController;
    }

    private void loadVehicleData() {
        vehicleTable.getItems().clear();
        String statusFilter = statusFilterComboBox.getValue();
        List<Vehicle> list;

        if ("All".equals(statusFilter)) {
            list = vehicleDAO.getAllVehiclesIncludingInactive();
        } else {
            list = vehicleDAO.getVehiclesByStatus(statusFilter);
        }

        ObservableList<Vehicle> vehicles = FXCollections.observableArrayList(list);
        vehicleTable.setItems(vehicles);
        vehicleCountLabel.setText("(" + vehicles.size() + ") VEHICLES:");
    }

    @FXML
    private void handleAddVehicle() {
        mainController.loadVehicleForm(null);
    }

    private void handleEditVehicle(Vehicle vehicle) {
        mainController.loadVehicleForm(vehicle);
    }

    private void setupEditButtonColumn() {
        Callback<TableColumn<Vehicle, Void>, TableCell<Vehicle, Void>> cellFactory = new Callback<>() {
            @Override
            public TableCell<Vehicle, Void> call(final TableColumn<Vehicle, Void> param) {

                final TableCell<Vehicle, Void> cell = new TableCell<>() {

                    @Override
                    public void updateItem(Void item, boolean empty) {
                        super.updateItem(item, empty);

                        if (empty || getTableRow() == null || getTableRow().getItem() == null) {
                            setGraphic(null);
                        } else {

                            Button btn = new Button("Edit");
                            btn.getStyleClass().add("edit-button");

                            btn.setOnAction((ActionEvent event) -> {
                                Vehicle v = getTableRow().getItem(); // Get fresh item
                                if (v != null) {
                                    handleEditVehicle(v);
                                }
                            });
                            setGraphic(btn);
                        }
                    }
                };
                return cell;
            }
        };
        editColumn.setCellFactory(cellFactory);
    }

    private void setupActionColumn() {
        Callback<TableColumn<Vehicle, Void>, TableCell<Vehicle, Void>> cellFactory = new Callback<>() {
            @Override
            public TableCell<Vehicle, Void> call(final TableColumn<Vehicle, Void> param) {

                final TableCell<Vehicle, Void> cell = new TableCell<>() {

                    @Override
                    public void updateItem(Void item, boolean empty) {
                        super.updateItem(item, empty);

                        if (empty || getTableRow() == null || getTableRow().getItem() == null) {
                            setGraphic(null);
                        } else {

                            Vehicle vehicle = getTableRow().getItem();
                            String status = vehicle.getStatus();

                            if ("Available".equals(status)) {
                                Button btn = new Button("Deactivate"); // Create button INSIDE
                                btn.getStyleClass().add("deactivate-button");

                                btn.setOnAction((ActionEvent event) -> {
                                    Vehicle v = getTableRow().getItem(); // Get fresh item on click
                                    if (v != null) {
                                        handleDeactivateReactivate(v);
                                    }
                                });
                                setGraphic(btn);

                            } else if ("Inactive".equals(status)) {
                                Button btn = new Button("Reactivate"); // Create button INSIDE
                                btn.getStyleClass().add("edit-button");

                                btn.setOnAction((ActionEvent event) -> {
                                    Vehicle v = getTableRow().getItem(); // Get fresh item on click
                                    if (v != null) {
                                        handleDeactivateReactivate(v);
                                    }
                                });
                                setGraphic(btn);

                            } else {
                                // This handles "In Use" and "Maintenance"
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

    private void handleDeactivateReactivate(Vehicle vehicle) {

        String newStatus = "Available".equals(vehicle.getStatus()) ? "Inactive" : "Available";
        String action = "Available".equals(vehicle.getStatus()) ? "deactivate" : "reactivate";

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmation");
        alert.setHeaderText("Confirm " + action);
        alert.setContentText("Are you sure you want to " + action + " vehicle: " + vehicle.getPlateID() + "?");

        if (alert.showAndWait().get() == ButtonType.OK) {
            boolean success;

            if ("Available".equals(vehicle.getStatus())) {
                success = vehicleDAO.deactivateVehicle(vehicle.getPlateID());
            } else {
                success = vehicleDAO.reactivateVehicle(vehicle.getPlateID());
            }

            if (success) {
                showAlert(Alert.AlertType.INFORMATION, "Success", "Vehicle has been " + action + "d.");
                loadVehicleData();
            } else {
                showAlert(Alert.AlertType.ERROR, "Error", "Failed to update vehicle status.");
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


