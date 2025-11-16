package main.gui;

import dao.DeploymentDAO;
import model.DeploymentTransaction;
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

    private final DeploymentDAO deploymentDAO = new DeploymentDAO();
    private Admin_dashboardController mainController;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        deploymentIDColumn.setCellValueFactory(new PropertyValueFactory<>("deploymentID"));
        plateIDColumn.setCellValueFactory(new PropertyValueFactory<>("plateID"));
        locationIDColumn.setCellValueFactory(new PropertyValueFactory<>("locationID"));
        startDateColumn.setCellValueFactory(new PropertyValueFactory<>("startDate"));
        endDateColumn.setCellValueFactory(new PropertyValueFactory<>("endDate"));
        statusColumn.setCellValueFactory(new PropertyValueFactory<>("status"));

        loadDeploymentData();
        setupEditButtonColumn();
    }

    public void setMainController(Admin_dashboardController mainController) {
        this.mainController = mainController;
    }

    private void loadDeploymentData() {
        List<DeploymentTransaction> deployments = deploymentDAO.getAllDeployments();
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
            private final Button btn = new Button("Edit");
            {
                btn.getStyleClass().add("edit-button");
                btn.setOnAction(event -> {
                    DeploymentTransaction deployment = getTableView().getItems().get(getIndex());
                    handleEditDeployment(deployment);
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    DeploymentTransaction deployment = getTableView().getItems().get(getIndex());
                    if ("Active".equalsIgnoreCase(deployment.getStatus())) {
                        setGraphic(btn);
                    } else {
                        setGraphic(null);
                    }
                }
            }
        };

        editColumn.setCellFactory(cellFactory);
    }
}
