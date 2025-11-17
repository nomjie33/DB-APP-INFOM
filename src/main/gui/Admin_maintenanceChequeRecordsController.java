package main.gui;

import javafx.scene.layout.AnchorPane;
import dao.MaintenanceChequeDAO;
import model.MaintenanceCheque;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.util.Callback;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.Button;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class Admin_maintenanceChequeRecordsController implements Initializable {

    @FXML private Label chequeCountLabel;
    @FXML private TableView<MaintenanceCheque> chequeTable;
    @FXML private TableColumn<MaintenanceCheque, String> maintenanceIDColumn;
    @FXML private TableColumn<MaintenanceCheque, String> partIDColumn;
    @FXML private TableColumn<MaintenanceCheque, String> quantityUsedColumn;
    @FXML private TableColumn<MaintenanceCheque, String> statusColumn;
    @FXML private TableColumn<MaintenanceCheque, Void> editColumn;

    private final MaintenanceChequeDAO chequeDAO = new MaintenanceChequeDAO();
    private Admin_dashboardController mainController;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // Bind table columns
        maintenanceIDColumn.setCellValueFactory(new PropertyValueFactory<>("maintenanceID"));
        partIDColumn.setCellValueFactory(new PropertyValueFactory<>("partID"));
        quantityUsedColumn.setCellValueFactory(new PropertyValueFactory<>("quantityUsed"));
        statusColumn.setCellValueFactory(new PropertyValueFactory<>("status"));

        // Load data
        loadChequeData();

        // Setup edit column
        setupEditButtonColumn();
    }

    public void setMainController(Admin_dashboardController mainController) {
        this.mainController = mainController;
    }

    private void loadChequeData() {
        try {
            List<MaintenanceCheque> chequeList = chequeDAO.getAllActiveMaintenanceCheques();

            ObservableList<MaintenanceCheque> observableList = FXCollections.observableArrayList(chequeList);
            chequeTable.setItems(observableList);
            chequeCountLabel.setText("(" + observableList.size() + ") MAINTENANCE CHEQUES:");

        } catch (Exception e) {
            System.err.println("Failed to load maintenance cheque data:");
            e.printStackTrace();
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Load Error");
            alert.setHeaderText("Could not load maintenance cheque data.");
            alert.setContentText("An error occurred while loading the data. Please check the logs.");
            alert.showAndWait();
        }
    }

    @FXML
    private void handleAddCheque(ActionEvent event) {
        mainController.loadMaintenanceChequeForm(null);
    }

    private void handleEditCheque(MaintenanceCheque cheque) {
        mainController.loadMaintenanceChequeForm(cheque);
    }

    private void setupEditButtonColumn() {
        Callback<TableColumn<MaintenanceCheque, Void>, TableCell<MaintenanceCheque, Void>> cellFactory = col -> new TableCell<>() {

            private final Button btn = new Button("Edit");

            {
                btn.getStyleClass().add("edit-button");
                btn.setOnAction(event -> {
                    MaintenanceCheque cheque = getTableView().getItems().get(getIndex());
                    handleEditCheque(cheque);
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
}