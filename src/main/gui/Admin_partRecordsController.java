package main.gui;

import dao.PartDAO;
import model.Part;
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

public class Admin_partRecordsController implements Initializable {

    @FXML
    private Label countLabel;
    @FXML
    private TableView<Part> table;
    @FXML
    private TableColumn<Part, String> idColumn;
    @FXML
    private TableColumn<Part, String> nameColumn;
    @FXML
    private TableColumn<Part, Integer> stockColumn;
    @FXML
    private TableColumn<Part, BigDecimal> priceColumn;
    @FXML
    private TableColumn<Part, String> statusColumn;
    @FXML
    private TableColumn<Part, Void> editColumn;

    @FXML
    private TableColumn<Part, Void> actionColumn;
    @FXML
    private ComboBox<String> statusFilterComboBox;

    private PartDAO dao = new PartDAO();
    private Admin_dashboardController mainController;

    @Override
    public void initialize(URL url, ResourceBundle rb) {

        idColumn.setCellValueFactory(new PropertyValueFactory<>("partId"));
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("partName"));
        stockColumn.setCellValueFactory(new PropertyValueFactory<>("quantity"));
        priceColumn.setCellValueFactory(new PropertyValueFactory<>("price"));
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

    private void loadData() {
        table.getItems().clear();
        String statusFilter = statusFilterComboBox.getValue();
        List<Part> list;

        if ("All".equals(statusFilter)) {
            list = dao.getAllPartsIncludingInactive();
        } else {
            list = dao.getPartsByStatus(statusFilter);
        }

        ObservableList<Part> data = FXCollections.observableArrayList(list);
        table.setItems(data);
        countLabel.setText("(" + data.size() + ") PARTS:");
    }

    @FXML
    private void handleAdd(ActionEvent event) {
        mainController.loadPartForm(null);
    }

    private void handleEdit(Part part) {
        mainController.loadPartForm(part);
    }

    private void setupEditButton() {
        Callback<TableColumn<Part, Void>, TableCell<Part, Void>> cellFactory = new Callback<>() {
            @Override
            public TableCell<Part, Void> call(final TableColumn<Part, Void> param) {
                final TableCell<Part, Void> cell = new TableCell<>() {
                    @Override
                    public void updateItem(Void item, boolean empty) {
                        super.updateItem(item, empty);
                        if (empty || getTableRow() == null || getTableRow().getItem() == null) {
                            setGraphic(null);
                        } else {
                            Part part = getTableRow().getItem();
                            // Only show Edit button for Active parts
                            if ("Active".equals(part.getStatus())) {
                                Button btn = new Button("Edit");
                                btn.getStyleClass().add("edit-button");
                                btn.setOnAction((ActionEvent event) -> {
                                    Part p = getTableRow().getItem();
                                    if (p != null) {
                                        handleEdit(p);
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
        Callback<TableColumn<Part, Void>, TableCell<Part, Void>> cellFactory = new Callback<>() {
            @Override
            public TableCell<Part, Void> call(final TableColumn<Part, Void> param) {
                final TableCell<Part, Void> cell = new TableCell<>() {
                    @Override
                    public void updateItem(Void item, boolean empty) {
                        super.updateItem(item, empty);
                        if (empty || getTableRow() == null || getTableRow().getItem() == null) {
                            setGraphic(null);
                        } else {
                            Part part = getTableRow().getItem();
                            Button btn = new Button();

                            if ("Active".equals(part.getStatus())) {
                                btn.setText("Deactivate");
                                btn.getStyleClass().add("deactivate-button");
                            } else {
                                btn.setText("Reactivate");
                                btn.getStyleClass().add("edit-button");
                            }

                            btn.setOnAction((ActionEvent event) -> {
                                Part currentPart = getTableRow().getItem();
                                if (currentPart != null) {
                                    handleDeactivateReactivate(currentPart);
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

    private void handleDeactivateReactivate(Part part) {
        String action = "Active".equals(part.getStatus()) ? "deactivate" : "reactivate";

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmation");
        alert.setHeaderText("Confirm " + action);
        alert.setContentText("Are you sure you want to " + action + " part: " + part.getPartName() + "?");

        if (alert.showAndWait().get() == ButtonType.OK) {
            boolean success;
            if ("Active".equals(part.getStatus())) {
                success = dao.deactivatePart(part.getPartId());
            } else {
                success = dao.reactivatePart(part.getPartId());
            }

            if (success) {
                showAlert(Alert.AlertType.INFORMATION, "Success", "Part has been " + action + "d.");
                loadData();
            } else {
                showAlert(Alert.AlertType.ERROR, "Error", "Failed to update part status.");
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