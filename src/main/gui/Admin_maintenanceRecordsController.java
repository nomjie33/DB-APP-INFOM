package main.gui;

import dao.MaintenanceDAO;
import model.MaintenanceTransaction;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Region;
import javafx.stage.Popup;
import javafx.geometry.Bounds;
import javafx.util.Callback;

import java.net.URL;
import java.sql.Timestamp;
import java.util.List;
import java.util.ResourceBundle;

public class Admin_maintenanceRecordsController implements Initializable {

    @FXML private Label maintenanceCountLabel;
    @FXML private TableView<MaintenanceTransaction> maintenanceTable;
    @FXML private TableColumn<MaintenanceTransaction, String> maintenanceIDColumn;
    @FXML private TableColumn<MaintenanceTransaction, String> plateIDColumn;
    @FXML private TableColumn<MaintenanceTransaction, String> technicianIDColumn;
    @FXML private TableColumn<MaintenanceTransaction, Timestamp> startDateTimeColumn;
    @FXML private TableColumn<MaintenanceTransaction, Timestamp> endDateTimeColumn;
    @FXML private TableColumn<MaintenanceTransaction, String> notesColumn;
    @FXML private TableColumn<MaintenanceTransaction, String> statusColumn;
    @FXML private TableColumn<MaintenanceTransaction, Void> editColumn;

    @FXML private TableColumn<MaintenanceTransaction, Void> actionColumn;
    @FXML private ComboBox<String> statusFilterComboBox;

    private MaintenanceDAO maintenanceDAO = new MaintenanceDAO();
    private service.MaintenanceService maintenanceService = new service.MaintenanceService();
    private Admin_dashboardController mainController;

    private static Popup textDisplayPopup = new Popup();
    private static Label popupLabel = new Label();
    private static TableCell<?, ?> currentlyOpenCell = null;
    static {
        popupLabel.setStyle(
                "-fx-background-color: white;" +
                        "-fx-border-color: #33a398;" +
                        "-fx-border-width: 2;" +
                        "-fx-padding: 8;" +
                        "-fx-background-radius: 5;" +
                        "-fx-border-radius: 5;" +
                        "-fx-text-fill: black;" +
                        "-fx-font-size: 13px;"
        );
        textDisplayPopup.getContent().add(popupLabel);
        textDisplayPopup.setAutoHide(true);
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {

        maintenanceIDColumn.setCellValueFactory(new PropertyValueFactory<>("maintenanceID"));
        plateIDColumn.setCellValueFactory(new PropertyValueFactory<>("plateID"));
        technicianIDColumn.setCellValueFactory(new PropertyValueFactory<>("technicianID"));
        startDateTimeColumn.setCellValueFactory(new PropertyValueFactory<>("startDateTime"));
        endDateTimeColumn.setCellValueFactory(new PropertyValueFactory<>("endDateTime"));
        notesColumn.setCellValueFactory(new PropertyValueFactory<>("notes"));
        statusColumn.setCellValueFactory(new PropertyValueFactory<>("status"));

        notesColumn.setCellFactory(column -> new TableCell<MaintenanceTransaction, String>() {

            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (item == null || empty) {
                    setText(null);
                    setOnMouseClicked(null);
                } else {
                    setText(item);
                    setOnMouseClicked(event -> {
                        if (textDisplayPopup.isShowing() && currentlyOpenCell == this) {
                            textDisplayPopup.hide();
                            currentlyOpenCell = null;
                        } else {
                            popupLabel.setText(item);
                            Bounds bounds = this.localToScreen(this.getBoundsInLocal());
                            textDisplayPopup.show(this.getScene().getWindow(), bounds.getMinX(), bounds.getMinY());
                            currentlyOpenCell = this;
                        }
                    });
                }
            }
        });

        statusFilterComboBox.setItems(FXCollections.observableArrayList("Active", "Inactive", "All"));
        statusFilterComboBox.setValue("Active");
        statusFilterComboBox.setOnAction(e -> loadMaintenanceData());

        loadMaintenanceData();
        setupEditButtonColumn();
        setupActionColumn();
    }

    public void setMainController(Admin_dashboardController mainController) {
        this.mainController = mainController;
    }

    private void loadMaintenanceData() {
        maintenanceTable.getItems().clear();
        String statusFilter = statusFilterComboBox.getValue();
        List<MaintenanceTransaction> maintenanceList;

        if ("All".equals(statusFilter)) {
            maintenanceList = maintenanceDAO.getAllMaintenanceIncludingInactive();
        } else {

            maintenanceList = maintenanceDAO.getMaintenanceByStatus(statusFilter);
        }

        ObservableList<MaintenanceTransaction> maintenances = FXCollections.observableArrayList(maintenanceList);
        maintenanceTable.setItems(maintenances);
        maintenanceCountLabel.setText("(" + maintenances.size() + ") MAINTENANCES:");
    }

    @FXML
    private void handleAddMaintenance() {
        mainController.loadMaintenanceForm(null);
    }

    private void handleEditMaintenance(MaintenanceTransaction maintenance) {
        mainController.loadMaintenanceForm(maintenance);
    }

    private void setupEditButtonColumn() {
        Callback<TableColumn<MaintenanceTransaction, Void>, TableCell<MaintenanceTransaction, Void>> cellFactory = new Callback<>() {
            @Override
            public TableCell<MaintenanceTransaction, Void> call(final TableColumn<MaintenanceTransaction, Void> param) {
                final TableCell<MaintenanceTransaction, Void> cell = new TableCell<>() {
                    @Override
                    public void updateItem(Void item, boolean empty) {
                        super.updateItem(item, empty);
                        if (empty || getTableRow() == null || getTableRow().getItem() == null) {
                            setGraphic(null);
                        } else {
                            MaintenanceTransaction maintenance = getTableRow().getItem();
                            // REQUIREMENT: Only show Edit button for Active maintenance records
                            if ("Active".equals(maintenance.getStatus())) {
                                Button btn = new Button("Edit");
                                btn.getStyleClass().add("edit-button");
                                btn.setOnAction(event -> {
                                    MaintenanceTransaction maint = getTableRow().getItem();
                                    if (maint != null) {
                                        handleEditMaintenance(maint);
                                    }
                                });
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
        editColumn.setCellFactory(cellFactory);
    }

    private void setupActionColumn() {
        Callback<TableColumn<MaintenanceTransaction, Void>, TableCell<MaintenanceTransaction, Void>> cellFactory = new Callback<>() {
            @Override
            public TableCell<MaintenanceTransaction, Void> call(final TableColumn<MaintenanceTransaction, Void> param) {
                final TableCell<MaintenanceTransaction, Void> cell = new TableCell<>() {
                    @Override
                    public void updateItem(Void item, boolean empty) {
                        super.updateItem(item, empty);
                        if (empty || getTableRow() == null || getTableRow().getItem() == null) {
                            setGraphic(null);
                        } else {
                            MaintenanceTransaction maintenance = getTableRow().getItem();
                            Button btn = new Button();

                            if ("Active".equals(maintenance.getStatus())) {
                                btn.setText("Deactivate");
                                btn.getStyleClass().add("deactivate-button");
                            } else {
                                btn.setText("Reactivate");
                                btn.getStyleClass().add("edit-button");
                            }

                            btn.setOnAction((ActionEvent event) -> {
                                MaintenanceTransaction currentMaint = getTableRow().getItem();
                                if (currentMaint != null) {
                                    handleDeactivateReactivate(currentMaint);
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

    private void handleDeactivateReactivate(MaintenanceTransaction maintenance) {
        String action = "Active".equals(maintenance.getStatus()) ? "deactivate" : "reactivate";
        
        // Build detailed confirmation message
        String message = "Are you sure you want to " + action + " maintenance: " + maintenance.getMaintenanceID() + "?";
        
        if ("Active".equals(maintenance.getStatus())) {
            // DEACTIVATION: Explain that this is a full cancellation
            message += "\n\n⚠️ IMPORTANT: Deactivation means FULL CANCELLATION";
            message += "\nThe maintenance will be treated as if it never occurred.";
            message += "\n\nThis will:";
            message += "\n• Deactivate all associated parts records";
            message += "\n• Return ALL parts to inventory";
            message += "\n• Update vehicle status to 'Available'";
        } else {
            // REACTIVATION: Explain that inventory will be checked
            message += "\n\nThis will:";
            message += "\n• Reactivate the maintenance transaction";
            message += "\n• Reactivate all associated parts records";
            message += "\n• Deduct parts from inventory again";
            message += "\n• Update vehicle status to 'Maintenance'";
            message += "\n\n⚠️ NOTE: Reactivation will fail if insufficient inventory.";
        }

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmation");
        alert.setHeaderText("Confirm " + action);
        alert.setContentText(message);

        if (alert.showAndWait().get() == ButtonType.OK) {
            boolean success;
            if ("Active".equals(maintenance.getStatus())) {
                // Use SERVICE layer for cascade deactivation with inventory rollback
                success = maintenanceService.deactivateMaintenance(maintenance.getMaintenanceID());
            } else {
                // Use SERVICE layer for validation and cascade reactivation
                success = maintenanceService.reactivateMaintenance(maintenance.getMaintenanceID());
            }

            if (success) {
                String successMessage = "Maintenance has been " + action + "d successfully.";
                if ("Active".equals(maintenance.getStatus())) {
                    successMessage += "\n\nAll parts have been returned to inventory.";
                    successMessage += "\nVehicle status updated to 'Available'.";
                } else {
                    successMessage += "\n\nAll parts have been deducted from inventory.";
                    successMessage += "\nVehicle status updated.";
                }
                showAlert(Alert.AlertType.INFORMATION, "Success", successMessage);
                loadMaintenanceData();
            } else {
                String errorMessage = "Failed to " + action + " maintenance.";
                if (!"Active".equals(maintenance.getStatus())) {
                    errorMessage += "\n\nPossible reasons:";
                    errorMessage += "\n• Insufficient inventory for required parts";
                    errorMessage += "\n• Database connection error";
                    errorMessage += "\n\nCheck console for detailed error messages.";
                }
                showAlert(Alert.AlertType.ERROR, "Error", errorMessage);
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