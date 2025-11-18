package main.gui;

import dao.PenaltyDAO;
import model.PenaltyTransaction;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.util.Callback;

import java.net.URL;
import java.sql.Date;
import java.util.List;
import java.util.ResourceBundle;

public class Admin_penaltyRecordsController implements Initializable {

    @FXML
    private Label penaltyCountLabel;
    @FXML
    private TableView<PenaltyTransaction> penaltyTable;
    @FXML
    private TableColumn<PenaltyTransaction, String> penaltyIDColumn;
    @FXML
    private TableColumn<PenaltyTransaction, String> rentalIDColumn;
    @FXML
    private TableColumn<PenaltyTransaction, String> maintenanceIDColumn;
    @FXML
    private TableColumn<PenaltyTransaction, String> totalPenaltyColumn;
    @FXML
    private TableColumn<PenaltyTransaction, String> statusColumn;
    @FXML
    private TableColumn<PenaltyTransaction, Date> dateIssuedColumn;
    @FXML
    private TableColumn<PenaltyTransaction, Void> editColumn;

    @FXML
    private TableColumn<PenaltyTransaction, Void> actionColumn;
    @FXML
    private ComboBox<String> statusFilterComboBox;

    private final PenaltyDAO penaltyDAO = new PenaltyDAO();
    private Admin_dashboardController mainController;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        penaltyIDColumn.setCellValueFactory(new PropertyValueFactory<>("penaltyID"));
        rentalIDColumn.setCellValueFactory(new PropertyValueFactory<>("rentalID"));
        maintenanceIDColumn.setCellValueFactory(new PropertyValueFactory<>("maintenanceID"));
        totalPenaltyColumn.setCellValueFactory(new PropertyValueFactory<>("totalPenalty"));
        statusColumn.setCellValueFactory(new PropertyValueFactory<>("status"));
        dateIssuedColumn.setCellValueFactory(new PropertyValueFactory<>("dateIssued"));

        statusFilterComboBox.setItems(FXCollections.observableArrayList("Active", "Inactive", "All"));
        statusFilterComboBox.setValue("Active");
        statusFilterComboBox.setOnAction(e -> loadPenaltyData());

        loadPenaltyData();
        setupEditButtonColumn();
        setupActionColumn();
    }

    public void setMainController(Admin_dashboardController mainController) {
        this.mainController = mainController;
    }

    private void loadPenaltyData() {
        try {
            penaltyTable.getItems().clear();
            String statusFilter = statusFilterComboBox.getValue();
            List<PenaltyTransaction> penalties;

            if ("All".equals(statusFilter)) {
                penalties = penaltyDAO.getAllPenaltiesIncludingInactive();
            } else {
                penalties = penaltyDAO.getPenaltiesByStatus(statusFilter);
            }

            ObservableList<PenaltyTransaction> data = FXCollections.observableArrayList(penalties);
            penaltyTable.setItems(data);
            penaltyCountLabel.setText("(" + data.size() + ") PENALTIES:");

        } catch (Exception e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Load Error", "Failed to load penalty data.");
        }
    }

    @FXML
    private void handleAddPenalty(ActionEvent event) {
        mainController.loadPenaltyForm(null);
    }

    private void handleEditPenalty(PenaltyTransaction penalty) {
        mainController.loadPenaltyForm(penalty);
    }

    private void setupEditButtonColumn() {
        Callback<TableColumn<PenaltyTransaction, Void>, TableCell<PenaltyTransaction, Void>> cellFactory = col -> new TableCell<>() {
            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || getTableRow() == null || getTableRow().getItem() == null) {
                    setGraphic(null);
                } else {
                    Button btn = new Button("Edit");
                    btn.getStyleClass().add("edit-button");
                    btn.setOnAction(event -> {
                        PenaltyTransaction penalty = getTableRow().getItem();
                        if (penalty != null) {
                            handleEditPenalty(penalty);
                        }
                    });
                    setGraphic(btn);
                }
            }
        };
        editColumn.setCellFactory(cellFactory);
    }

    private void setupActionColumn() {
        Callback<TableColumn<PenaltyTransaction, Void>, TableCell<PenaltyTransaction, Void>> cellFactory = new Callback<>() {
            @Override
            public TableCell<PenaltyTransaction, Void> call(final TableColumn<PenaltyTransaction, Void> param) {
                final TableCell<PenaltyTransaction, Void> cell = new TableCell<>() {
                    @Override
                    public void updateItem(Void item, boolean empty) {
                        super.updateItem(item, empty);
                        if (empty || getTableRow() == null || getTableRow().getItem() == null) {
                            setGraphic(null);
                        } else {
                            PenaltyTransaction penalty = getTableRow().getItem();
                            Button btn = new Button();

                            if ("Active".equals(penalty.getStatus())) {
                                btn.setText("Deactivate");
                                btn.getStyleClass().add("deactivate-button");
                            } else {
                                btn.setText("Reactivate");
                                btn.getStyleClass().add("edit-button");
                            }

                            btn.setOnAction((ActionEvent event) -> {
                                PenaltyTransaction currentPenalty = getTableRow().getItem();
                                if (currentPenalty != null) {
                                    handleDeactivateReactivate(currentPenalty);
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

    private void handleDeactivateReactivate(PenaltyTransaction penalty) {
        String action = "Active".equals(penalty.getStatus()) ? "deactivate" : "reactivate";

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmation");
        alert.setHeaderText("Confirm " + action);
        alert.setContentText("Are you sure you want to " + action + " penalty: " + penalty.getPenaltyID() + "?");

        if (alert.showAndWait().get() == ButtonType.OK) {
            boolean success;
            if ("Active".equals(penalty.getStatus())) {
                success = penaltyDAO.deactivatePenalty(penalty.getPenaltyID());
            } else {
                success = penaltyDAO.reactivatePenalty(penalty.getPenaltyID());
            }

            if (success) {
                showAlert(Alert.AlertType.INFORMATION, "Success", "Penalty has been " + action + "d.");
                loadPenaltyData();
            } else {
                showAlert(Alert.AlertType.ERROR, "Error", "Failed to update status.");
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