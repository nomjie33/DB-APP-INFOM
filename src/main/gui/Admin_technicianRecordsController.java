package main.gui;

import dao.TechnicianDAO;
import model.Technician;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.util.Callback;

import java.math.BigDecimal;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class Admin_technicianRecordsController implements Initializable {

    @FXML
    private Label countLabel;
    @FXML
    private TableView<Technician> table;
    @FXML
    private TableColumn<Technician, String> idColumn;
    @FXML
    private TableColumn<Technician, String> lastNameColumn;
    @FXML
    private TableColumn<Technician, String> firstNameColumn;
    @FXML
    private TableColumn<Technician, String> specIdColumn;
    @FXML
    private TableColumn<Technician, BigDecimal> rateColumn;
    @FXML
    private TableColumn<Technician, String> contactColumn;
    @FXML
    private TableColumn<Technician, Void> editColumn;

    @FXML
    private TableColumn<Technician, String> statusColumn;
    @FXML
    private TableColumn<Technician, Void> actionColumn;
    @FXML
    private ComboBox<String> statusFilterComboBox;

    private TechnicianDAO technicianDAO = new TechnicianDAO();
    private Admin_dashboardController mainController;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        idColumn.setCellValueFactory(new PropertyValueFactory<>("technicianId"));
        lastNameColumn.setCellValueFactory(new PropertyValueFactory<>("lastName"));
        firstNameColumn.setCellValueFactory(new PropertyValueFactory<>("firstName"));
        specIdColumn.setCellValueFactory(new PropertyValueFactory<>("specializationId"));
        rateColumn.setCellValueFactory(new PropertyValueFactory<>("rate"));
        contactColumn.setCellValueFactory(new PropertyValueFactory<>("contactNumber"));
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

    private void handleEditTechnician(Technician tech) {
        mainController.loadTechnicianForm(tech);
    }

    @FXML
    private void handleAdd(ActionEvent event) {
        mainController.loadTechnicianForm(null);
    }

    private void loadData() {
        table.getItems().clear();
        String statusFilter = statusFilterComboBox.getValue();
        List<Technician> list;

        if ("All".equals(statusFilter)) {
            list = technicianDAO.getAllTechniciansIncludingInactive();
        } else {
            list = technicianDAO.getTechniciansByStatus(statusFilter);
        }

        ObservableList<Technician> data = FXCollections.observableArrayList(list);
        table.setItems(data);
        countLabel.setText("(" + data.size() + ") TECHNICIANS:");
    }

    private void setupEditButton() {
        Callback<TableColumn<Technician, Void>, TableCell<Technician, Void>> cellFactory = new Callback<>() {
            @Override
            public TableCell<Technician, Void> call(final TableColumn<Technician, Void> param) {
                final TableCell<Technician, Void> cell = new TableCell<>() {
                    @Override
                    public void updateItem(Void item, boolean empty) {
                        super.updateItem(item, empty);
                        if (empty || getTableRow() == null || getTableRow().getItem() == null) {
                            setGraphic(null);
                        } else {
                            Technician technician = getTableRow().getItem();
                            // Only show Edit button for Active technicians
                            if ("Active".equals(technician.getStatus())) {
                                Button btn = new Button("Edit");
                                btn.getStyleClass().add("edit-button");
                                btn.setOnAction((ActionEvent event) -> {
                                    // Get fresh item on click
                                    Technician tech = getTableRow().getItem();
                                    if (tech != null) {
                                        handleEditTechnician(tech);
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
        Callback<TableColumn<Technician, Void>, TableCell<Technician, Void>> cellFactory = new Callback<>() {
            @Override
            public TableCell<Technician, Void> call(final TableColumn<Technician, Void> param) {
                final TableCell<Technician, Void> cell = new TableCell<>() {
                    @Override
                    public void updateItem(Void item, boolean empty) {
                        super.updateItem(item, empty);
                        if (empty || getTableRow() == null || getTableRow().getItem() == null) {
                            setGraphic(null);
                        } else {
                            Technician tech = getTableRow().getItem();
                            Button btn = new Button();

                            if ("Active".equals(tech.getStatus())) {
                                btn.setText("Deactivate");
                                btn.getStyleClass().add("deactivate-button");
                            } else {
                                btn.setText("Reactivate");
                                btn.getStyleClass().add("edit-button");
                            }

                            btn.setOnAction((ActionEvent event) -> {
                                Technician currentTech = getTableRow().getItem();
                                if (currentTech != null) {
                                    handleDeactivateReactivate(currentTech);
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

    private void handleDeactivateReactivate(Technician tech) {
        String action = "Active".equals(tech.getStatus()) ? "deactivate" : "reactivate";

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmation");
        alert.setHeaderText("Confirm " + action);
        alert.setContentText("Are you sure you want to " + action + " technician: " + tech.getFullName() + "?");

        if (alert.showAndWait().get() == ButtonType.OK) {
            boolean success;
            if ("Active".equals(tech.getStatus())) {
                success = technicianDAO.deactivateTechnician(tech.getTechnicianId());
            } else {
                success = technicianDAO.reactivateTechnician(tech.getTechnicianId());
            }

            if (success) {
                showAlert(Alert.AlertType.INFORMATION, "Success", "Technician status has been updated.");
                loadData();
            } else {
                showAlert(Alert.AlertType.ERROR, "Error", "Failed to update technician status.");
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
