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

    @FXML private Label penaltyCountLabel;
    @FXML private TableView<PenaltyTransaction> penaltyTable;
    @FXML private TableColumn<PenaltyTransaction, String> penaltyIDColumn;
    @FXML private TableColumn<PenaltyTransaction, String> rentalIDColumn;
    @FXML private TableColumn<PenaltyTransaction, String> maintenanceIDColumn;
    @FXML private TableColumn<PenaltyTransaction, String> totalPenaltyColumn;
    @FXML private TableColumn<PenaltyTransaction, String> statusColumn;
    @FXML private TableColumn<PenaltyTransaction, Date> dateIssuedColumn;
    @FXML private TableColumn<PenaltyTransaction, Void> editColumn;

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

        loadPenaltyData();
        setupEditButtonColumn();
    }

    public void setMainController(Admin_dashboardController mainController) {
        this.mainController = mainController;
    }

    private void loadPenaltyData() {
        List<PenaltyTransaction> penalties = penaltyDAO.getAllPenalties();
        ObservableList<PenaltyTransaction> data = FXCollections.observableArrayList(penalties);
        penaltyTable.setItems(data);
        penaltyCountLabel.setText("(" + data.size() + ") PENALTIES:");
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
            private final Button btn = new Button("Edit");
            {
                btn.getStyleClass().add("edit-button");
                btn.setOnAction(event -> {
                    PenaltyTransaction penalty = getTableView().getItems().get(getIndex());
                    handleEditPenalty(penalty);
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
