package main.gui;

import dao.LocationDAO;
import model.Location;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.util.Callback;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class Admin_locationRecordsController implements  Initializable{

    @FXML private Label countLabel;
    @FXML private TableView<Location> table;
    @FXML private TableColumn<Location, String> idColumn;
    @FXML private TableColumn<Location, String> nameColumn;
    @FXML private TableColumn<Location, String> statusColumn;
    @FXML private TableColumn<Location, Void> editColumn;
    @FXML private TableColumn<Location, Void> actionColumn;
    @FXML private ComboBox<String> statusFilterComboBox;

    private LocationDAO dao = new LocationDAO();
    private Admin_dashboardController mainController;

    @Override
    public void initialize(URL url, ResourceBundle rb){
        idColumn.setCellValueFactory(new PropertyValueFactory<>("locationID"));
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        statusColumn.setCellValueFactory(new PropertyValueFactory<>("status"));

        statusFilterComboBox.setItems(FXCollections.observableArrayList("Active", "Inactive", "All"));
        statusFilterComboBox.setValue("Active");
        statusFilterComboBox.setOnAction(e -> loadData());

        loadData();
        setupEditButton();
        setupActionColumn();
    }

    public void setMainController(Admin_dashboardController mainController) {
        this.mainController = mainController;
    }

    private  void loadData(){
        table.getItems().clear();
        String statusFilter = statusFilterComboBox.getValue();
        List<Location> list;

        if ("All".equals(statusFilter)) {
            list = dao.getAllLocationsIncludingInactive();
        } else {
            list = dao.getAllLocationsByStatus(statusFilter);
        }

        ObservableList<Location> data = FXCollections.observableArrayList(list);
        table.setItems(data);
        countLabel.setText("(" + data.size() + ") LOCATIONS:");
    }

    @FXML private void handleAdd(){
        mainController.loadLocationForm(null);
    }

    private void handleEdit(Location loc){
        mainController.loadLocationForm(loc);
    }

    private void setupEditButton() {
        Callback<TableColumn<Location, Void>, TableCell<Location, Void>> cellFactory = new Callback<>() {
            @Override
            public TableCell<Location, Void> call(final TableColumn<Location, Void> param) {
                final TableCell<Location, Void> cell = new TableCell<>() {
                    @Override
                    public void updateItem(Void item, boolean empty) {
                        super.updateItem(item, empty);
                        if (empty || getTableRow() == null || getTableRow().getItem() == null) {
                            setGraphic(null);
                        } else {

                            Button btn = new Button("Edit");
                            btn.getStyleClass().add("edit-button");
                            btn.setOnAction((ActionEvent event) -> {
                                Location loc = getTableRow().getItem();
                                if (loc != null) {
                                    handleEdit(loc);
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
        Callback<TableColumn<Location, Void>, TableCell<Location, Void>> cellFactory = new Callback<>() {
            @Override
            public TableCell<Location, Void> call(final TableColumn<Location, Void> param) {
                final TableCell<Location, Void> cell = new TableCell<>() {
                    @Override
                    public void updateItem(Void item, boolean empty) {
                        super.updateItem(item, empty);
                        if (empty || getTableRow() == null || getTableRow().getItem() == null) {
                            setGraphic(null);
                        } else {
                            Location loc = getTableRow().getItem();
                            Button btn = new Button();

                            if ("Active".equals(loc.getStatus())) {
                                btn.setText("Deactivate");
                                btn.getStyleClass().add("deactivate-button");
                            } else {
                                btn.setText("Reactivate");
                                btn.getStyleClass().add("edit-button");
                            }

                            btn.setOnAction((ActionEvent event) -> {
                                Location currentLoc = getTableRow().getItem();
                                if (currentLoc != null) {
                                    handleDeactivateReactivate(currentLoc);
                                }
                            });
                            setGraphic(btn);
                        }
                    }
                };
                return cell;
            }
        };
        actionColumn.setCellFactory(cellFactory);
    }

    private void handleDeactivateReactivate(Location loc) {
        String action = "Active".equals(loc.getStatus()) ? "deactivate" : "reactivate";

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmation");
        alert.setHeaderText("Confirm " + action);
        alert.setContentText("Are you sure you want to " + action + " location: " + loc.getName() + "?");

        if (alert.showAndWait().get() == ButtonType.OK) {
            boolean success;
            if ("Active".equals(loc.getStatus())) {
                success = dao.deactivateLocation(loc.getLocationID());
            } else {
                success = dao.reactivateLocation(loc.getLocationID());
            }

            if (success) {
                showAlert(Alert.AlertType.INFORMATION, "Success", "Location has been " + action + "d.");
                loadData();
            } else {
                showAlert(Alert.AlertType.ERROR, "Error", "Failed to update location status.");
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


