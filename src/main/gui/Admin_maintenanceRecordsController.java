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
    @FXML private TableColumn<MaintenanceTransaction, Void> editColumn; // Action column

    private MaintenanceDAO maintenanceDAO = new MaintenanceDAO();
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

        loadMaintenanceData();
        setupEditButtonColumn();
    }

    public void setMainController(Admin_dashboardController mainController) {
        this.mainController = mainController;
    }

    private void loadMaintenanceData() {
        List<MaintenanceTransaction> maintenanceList = maintenanceDAO.getAllMaintenance();
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
                    private final Button btn = new Button("Edit");
                    {
                        btn.getStyleClass().add("edit-button");
                        btn.setOnAction(event -> {
                            MaintenanceTransaction maintenance = getTableView().getItems().get(getIndex());
                            handleEditMaintenance(maintenance);
                        });
                    }

                    @Override
                    public void updateItem(Void item, boolean empty) {
                        super.updateItem(item, empty);
                        setGraphic(empty ? null : btn);
                    }
                };
                return cell;
            }
        };
        editColumn.setCellFactory(cellFactory);
    }
}

