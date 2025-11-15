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

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class Admin_vehicleRecordsController implements  Initializable{

    @FXML private Label vehicleCountLabel;
    @FXML private TableView<Vehicle> vehicleTable;
    @FXML private TableColumn<Vehicle, String> plateColumn;
    @FXML private TableColumn<Vehicle, String> modelColumn;
    @FXML private TableColumn<Vehicle, String> typeColumn;
    @FXML private TableColumn<Vehicle, Double> priceColumn;
    @FXML private TableColumn<Vehicle, String> statusColumn;
    @FXML private TableColumn<Vehicle, Void> editColumn;

    private VehicleDAO vehicleDAO = new VehicleDAO();
    private Admin_dashboardController mainController;

    @Override
    public void initialize(URL url, ResourceBundle rb){
        plateColumn.setCellValueFactory(new PropertyValueFactory<>("plateID"));
        modelColumn.setCellValueFactory(new PropertyValueFactory<>("vehicleModel"));
        typeColumn.setCellValueFactory(new PropertyValueFactory<>("vehicleType"));
        priceColumn.setCellValueFactory(new PropertyValueFactory<>("rentalPrice"));
        statusColumn.setCellValueFactory(new PropertyValueFactory<>("status"));

        loadVehicleData();
        setupEditButtonColumn();
    }

    public void setMainController(Admin_dashboardController mainController){
        this.mainController = mainController;
    }

    private void loadVehicleData(){
        List<Vehicle> list = vehicleDAO.getAllVehicles();
        ObservableList<Vehicle> vehicles = FXCollections.observableArrayList(list);
        vehicleTable.setItems(vehicles);
        vehicleCountLabel.setText("(" + vehicles.size() + ") VEHICLES:");
    }

    @FXML private void handleAddVehicle(){
        mainController.loadVehicleForm(null);
    }

    private void handleEditVehicle(Vehicle vehicle){
        mainController.loadVehicleForm(vehicle);
    }

    private void setupEditButtonColumn() {
        Callback<TableColumn<Vehicle, Void>, TableCell<Vehicle, Void>> cellFactory = new Callback<>() {
            @Override
            public TableCell<Vehicle, Void> call(final TableColumn<Vehicle, Void> param) {
                return new TableCell<>() {
                    private final Button btn = new Button("Edit");
                    {
                        btn.getStyleClass().add("edit-button");
                        btn.setOnAction((ActionEvent event) -> {
                            Vehicle vehicle = getTableView().getItems().get(getIndex());
                            handleEditVehicle(vehicle);
                        });
                    }
                    @Override
                    public void updateItem(Void item, boolean empty) {
                        super.updateItem(item, empty);
                        if (empty) { setGraphic(null); } else { setGraphic(btn); }
                    }
                };
            }
        };
        editColumn.setCellFactory(cellFactory);
    }
}

