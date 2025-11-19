package main.gui;

import javafx.scene.layout.AnchorPane;
import dao.MaintenanceChequeDAO;
import model.MaintenanceCheque;
import service.MaintenanceService;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.util.Callback;

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

    @FXML private TableColumn<MaintenanceCheque, Void> actionColumn;
    @FXML private ComboBox<String> statusFilterComboBox;

    private final MaintenanceChequeDAO chequeDAO = new MaintenanceChequeDAO();
    private final MaintenanceService maintenanceService = new MaintenanceService();
    private Admin_dashboardController mainController;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        maintenanceIDColumn.setCellValueFactory(new PropertyValueFactory<>("maintenanceID"));
        partIDColumn.setCellValueFactory(new PropertyValueFactory<>("partID"));
        quantityUsedColumn.setCellValueFactory(new PropertyValueFactory<>("quantityUsed"));
        statusColumn.setCellValueFactory(new PropertyValueFactory<>("status"));

        statusFilterComboBox.setItems(FXCollections.observableArrayList("Active", "Inactive", "All"));
        statusFilterComboBox.setValue("Active");
        statusFilterComboBox.setOnAction(e -> loadChequeData());

        loadChequeData();
        setupEditButtonColumn();
        setupActionColumn();
    }

    public void setMainController(Admin_dashboardController mainController) {
        this.mainController = mainController;
    }

    private void loadChequeData() {
        try {
            chequeTable.getItems().clear();
            String statusFilter = statusFilterComboBox.getValue();
            List<MaintenanceCheque> chequeList;

            if ("All".equals(statusFilter)) {
                chequeList = chequeDAO.getAllMaintenanceChequesIncludingInactive();
            } else {
                chequeList = chequeDAO.getMaintenanceChequesByStatus(statusFilter);
            }

            ObservableList<MaintenanceCheque> observableList = FXCollections.observableArrayList(chequeList);
            chequeTable.setItems(observableList);
            chequeCountLabel.setText("(" + observableList.size() + ") MAINTENANCE CHEQUES:");

        } catch (Exception e) {
            System.err.println("Failed to load maintenance cheque data:");
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Load Error", "Could not load data.");
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
            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || getTableRow() == null || getTableRow().getItem() == null) {
                    setGraphic(null);
                } else {
                    Button btn = new Button("Edit");
                    btn.getStyleClass().add("edit-button");
                    btn.setOnAction(event -> {
                        MaintenanceCheque cheque = getTableRow().getItem();
                        if (cheque != null) {
                            handleEditCheque(cheque);
                        }
                    });
                    setGraphic(btn);
                }
            }
        };
        editColumn.setCellFactory(cellFactory);
    }

    private void setupActionColumn() {
        Callback<TableColumn<MaintenanceCheque, Void>, TableCell<MaintenanceCheque, Void>> cellFactory = new Callback<>() {
            @Override
            public TableCell<MaintenanceCheque, Void> call(final TableColumn<MaintenanceCheque, Void> param) {
                final TableCell<MaintenanceCheque, Void> cell = new TableCell<>() {
                    @Override
                    public void updateItem(Void item, boolean empty) {
                        super.updateItem(item, empty);
                        if (empty || getTableRow() == null || getTableRow().getItem() == null) {
                            setGraphic(null);
                        } else {
                            MaintenanceCheque cheque = getTableRow().getItem();
                            Button btn = new Button();

                            if ("Active".equals(cheque.getStatus())) {
                                btn.setText("Cancel");
                                btn.getStyleClass().add("deactivate-button");
                            } else {
                                btn.setText("Reactivate");
                                btn.getStyleClass().add("edit-button");
                            }

                            btn.setOnAction((ActionEvent event) -> {
                                MaintenanceCheque currentCheque = getTableRow().getItem();
                                if (currentCheque != null) {
                                    handleDeactivateReactivate(currentCheque);
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

    private void handleDeactivateReactivate(MaintenanceCheque cheque) {
        String action = "Active".equals(cheque.getStatus()) ? "Cancellation" : "Revert Cancellation";
        
        String inventoryMessage = "";
        if ("Active".equals(cheque.getStatus())) {
            inventoryMessage = "\n\nNote: All " + cheque.getQuantityUsed() + " units of this part will be returned to inventory.";
        } else {
            inventoryMessage = "\n\nNote: " + cheque.getQuantityUsed() + " units will be deducted from inventory.";
        }

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmation");
        alert.setHeaderText("Confirm " + action);
        alert.setContentText("Are you sure you want to " + action + " this record?\nMaintenance ID: " + cheque.getMaintenanceID() + "\nPart ID: " + cheque.getPartID() + inventoryMessage);

        if (alert.showAndWait().get() == ButtonType.OK) {
            boolean success;
            if ("Active".equals(cheque.getStatus())) {
                // Use service method to handle inventory return automatically
                success = maintenanceService.deactivateMaintenanceChequeWithInventory(cheque.getMaintenanceID(), cheque.getPartID());
            } else {
                // Use service method to handle inventory deduction automatically
                success = maintenanceService.reactivateMaintenanceChequeWithInventory(cheque.getMaintenanceID(), cheque.getPartID());
            }

            if (success) {
                showAlert(Alert.AlertType.INFORMATION, "Success", "Maintenance cheque status has been updated.");
                loadChequeData();
            } else {
                showAlert(Alert.AlertType.ERROR, "Error", "Failed to update status. Check console for details.");
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