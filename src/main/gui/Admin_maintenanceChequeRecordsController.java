package main.gui;

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

    /**
     * Load maintenance cheques into the table.
     * Uses getPartsByMaintenance() from DAO.
     */
    private void loadChequeData() {
        List<MaintenanceCheque> chequeList = new ArrayList<>();

        // TODO: Replace with actual maintenanceID list if you want multiple
        // For now, using a placeholder ID
        String placeholderMaintenanceID = "MAINT-0001"; // change as needed
        chequeList = chequeDAO.getPartsByMaintenance(placeholderMaintenanceID);

        ObservableList<MaintenanceCheque> observableList = FXCollections.observableArrayList(chequeList);
        chequeTable.setItems(observableList);
        chequeCountLabel.setText("(" + observableList.size() + ") MAINTENANCE CHEQUES:");
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
