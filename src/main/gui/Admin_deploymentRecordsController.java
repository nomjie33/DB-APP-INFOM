package main.gui;

import dao.DeploymentDAO;
import dao.VehicleDAO;
import dao.LocationDAO;
import javafx.scene.control.*;
import model.DeploymentTransaction;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.util.Callback;

import java.net.URL;
import java.sql.Date;
import java.util.List;
import java.util.ResourceBundle;

public class Admin_deploymentRecordsController implements Initializable {

    @FXML private Label deploymentCountLabel;
    @FXML private TableView<DeploymentTransaction> deploymentTable;
    @FXML private TableColumn<DeploymentTransaction, String> deploymentIDColumn;
    @FXML private TableColumn<DeploymentTransaction, String> plateIDColumn;
    @FXML private TableColumn<DeploymentTransaction, String> locationIDColumn;
    @FXML private TableColumn<DeploymentTransaction, Date> startDateColumn;
    @FXML private TableColumn<DeploymentTransaction, Date> endDateColumn;
    @FXML private TableColumn<DeploymentTransaction, String> statusColumn;
    @FXML private TableColumn<DeploymentTransaction, Void> editColumn;

    @FXML private TableColumn<DeploymentTransaction, Void> actionColumn;
    @FXML private ComboBox<String> statusFilterComboBox;

    private final DeploymentDAO deploymentDAO = new DeploymentDAO();
    private Admin_dashboardController mainController;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        deploymentIDColumn.setCellValueFactory(new PropertyValueFactory<>("deploymentID"));
        plateIDColumn.setCellValueFactory(new PropertyValueFactory<>("plateID"));
        locationIDColumn.setCellValueFactory(new PropertyValueFactory<>("locationID"));
        startDateColumn.setCellValueFactory(new PropertyValueFactory<>("startDate"));
        endDateColumn.setCellValueFactory(new PropertyValueFactory<>("endDate"));

        endDateColumn.setCellFactory(column -> {
            return new TableCell<DeploymentTransaction, Date>() {
                @Override
                protected void updateItem(Date item, boolean empty) {
                    super.updateItem(item, empty);

                    if (empty) {
                        setText(null);
                        setStyle("");
                    } else if (item == null) {
                        setText("N/A"); // Show "N/A" if date is NULL
                        setStyle("-fx-text-fill: grey;");
                    } else {
                        setText(item.toString());
                        setStyle("");
                    }
                }
            };
        });
        statusColumn.setCellValueFactory(new PropertyValueFactory<>("status"));

        statusFilterComboBox.setItems(FXCollections.observableArrayList("Active/Completed", "Cancelled", "All"));
        statusFilterComboBox.setValue("Active/Completed");
        statusFilterComboBox.setOnAction(e -> loadDeploymentData());

        loadDeploymentData();
        setupEditButtonColumn();
        setupActionColumn();
    }

    public void setMainController(Admin_dashboardController mainController) {
        this.mainController = mainController;
    }

    private void loadDeploymentData() {
        deploymentTable.getItems().clear();
        String statusFilter = statusFilterComboBox.getValue();
        List<DeploymentTransaction> deployments;

        if ("All".equals(statusFilter)) {
            deployments = deploymentDAO.getAllDeploymentsIncludingCancelled();
        } else if ("Cancelled".equals(statusFilter)) {
            deployments = deploymentDAO.getDeploymentsByStatus("Cancelled");
        } else {
            deployments = deploymentDAO.getAllDeployments(); // This is your original method
        }

        ObservableList<DeploymentTransaction> data = FXCollections.observableArrayList(deployments);
        deploymentTable.setItems(data);
        deploymentCountLabel.setText("(" + deployments.size() + ") DEPLOYMENTS:");
    }

    @FXML
    private void handleAddDeployment() {
        mainController.loadDeploymentForm(null);
    }

    private void handleEditDeployment(DeploymentTransaction deployment) {
        mainController.loadDeploymentForm(deployment);
    }

    private void setupEditButtonColumn() {
        Callback<TableColumn<DeploymentTransaction, Void>, TableCell<DeploymentTransaction, Void>> cellFactory = col -> new TableCell<>() {
            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || getTableRow() == null || getTableRow().getItem() == null) {
                    setGraphic(null);
                } else {
                    DeploymentTransaction deployment = getTableRow().getItem();
                    if ("Active".equalsIgnoreCase(deployment.getStatus())) {
                        Button btn = new Button("Edit");
                        btn.getStyleClass().add("edit-button");
                        btn.setOnAction(event -> {
                            handleEditDeployment(deployment);
                        });
                        setGraphic(btn);
                    } else {
                        setGraphic(null);
                    }
                }
            }
        };
        editColumn.setCellFactory(cellFactory);
    }

    private void setupActionColumn() {
        Callback<TableColumn<DeploymentTransaction, Void>, TableCell<DeploymentTransaction, Void>> cellFactory = new Callback<>() {
            @Override
            public TableCell<DeploymentTransaction, Void> call(final TableColumn<DeploymentTransaction, Void> param) {
                final TableCell<DeploymentTransaction, Void> cell = new TableCell<>() {
                    @Override
                    public void updateItem(Void item, boolean empty) {
                        super.updateItem(item, empty);
                        if (empty || getTableRow() == null || getTableRow().getItem() == null) {
                            setGraphic(null);
                        } else {
                            DeploymentTransaction deployment = getTableRow().getItem();
                            Button btn = new Button();

                            if ("Active".equals(deployment.getStatus())) {
                                btn.setText("Cancel");
                                btn.getStyleClass().add("deactivate-button");
                            } else if ("Cancelled".equals(deployment.getStatus())) {
                                btn.setText("Reactivate");
                                btn.getStyleClass().add("edit-button");
                            } else {
                                setGraphic(null);
                                return;
                            }

                            btn.setOnAction((ActionEvent event) -> {
                                DeploymentTransaction currentDep = getTableRow().getItem();
                                if (currentDep != null) {
                                    handleCancelReactivate(currentDep);
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

    private void handleCancelReactivate(DeploymentTransaction deployment) {
        String action = "Active".equals(deployment.getStatus()) ? "cancel" : "reactivate";

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmation");
        alert.setHeaderText("Confirm " + action);
        alert.setContentText("Are you sure you want to " + action + " deployment: " + deployment.getDeploymentID() + "?");

        if (alert.showAndWait().get() == ButtonType.OK) {
            boolean success;
            if ("Active".equals(deployment.getStatus())) {
                success = deploymentDAO.cancelDeployment(deployment.getDeploymentID());
            } else {
                success = deploymentDAO.reactivateDeployment(deployment.getDeploymentID());
            }

            if (success) {
                showAlert(Alert.AlertType.INFORMATION, "Success", "Deployment has been " + action + "ed.");
                loadDeploymentData();
            } else {
                showAlert(Alert.AlertType.ERROR, "Error", "Failed to update deployment status.");
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