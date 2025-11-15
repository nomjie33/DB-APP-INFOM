package main.gui;

import dao.DashboardDAO;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;

import java.net.URL;
import java.util.Map;
import java.util.ResourceBundle;

public class Admin_homeController implements Initializable {

    @FXML private Label adminNameLabel;
    @FXML private TableView<RecordSummary> recordsTable;
    @FXML private TableColumn<RecordSummary, String> infoColumn;
    @FXML private TableColumn<RecordSummary, Number> countColumn;
    @FXML private TableColumn<RecordSummary, String> categoryColumn;

    private Admin_dashboardController mainController;
    private DashboardDAO dashboardDAO = new DashboardDAO();

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        infoColumn.setCellValueFactory(cellData -> cellData.getValue().infoProperty());
        countColumn.setCellValueFactory(cellData -> cellData.getValue().countProperty());
        categoryColumn.setCellValueFactory(cellData -> cellData.getValue().categoryProperty());

        loadRecordData();

        recordsTable.setOnMouseClicked(event -> {
            RecordSummary selectedRecord = recordsTable.getSelectionModel().getSelectedItem();
            if (selectedRecord != null){
                handleRecordClick(selectedRecord);
            }
        });
    }

    public void setMainController(Admin_dashboardController mainController) {
        this.mainController = mainController;
    }

    private void loadRecordData() {
        Map<String, Integer> counts = dashboardDAO.getRecordCounts();
        ObservableList<RecordSummary> records = FXCollections.observableArrayList();

        records.add(new RecordSummary("Customer records", counts.getOrDefault("Customers", 0), "Core"));
        records.add(new RecordSummary("Vehicle records", counts.getOrDefault("Vehicles", 0), "Core"));
        records.add(new RecordSummary("Location records", counts.getOrDefault("Locations", 0), "Core"));
        records.add(new RecordSummary("Technician records", counts.getOrDefault("Technicians", 0), "Core"));
        records.add(new RecordSummary("Part records", counts.getOrDefault("Parts", 0), "Core"));
        records.add(new RecordSummary("Rental records", counts.getOrDefault("Rentals", 0), "Transaction"));
        records.add(new RecordSummary("Payment records", counts.getOrDefault("Payments", 0), "Transaction"));
        records.add(new RecordSummary("Maintenance records", counts.getOrDefault("Maintenance", 0), "Transaction"));
        records.add(new RecordSummary("Maintenance cheques", counts.getOrDefault("Maintenance_Cheque", 0), "Transaction"));
        records.add(new RecordSummary("Penalty records", counts.getOrDefault("Penalties", 0), "Transaction"));

        recordsTable.setItems(records);
    }

    private void handleRecordClick(RecordSummary record){

        String recordName = record.getInfo();
        String fxmlFile = null;

        switch(recordName){
            case "Customer records":
                fxmlFile = "Admin-customerRecords.fxml";
                break;
            case "Vehicle records":
                fxmlFile = "Admin-vehicleRecords.fxml";
                break;
            case "Location records":
                fxmlFile = "Admin-locationRecords.fxml";
                break;
            case "Technician records":
                fxmlFile = "Admin-technicianRecords.fxml";
                break;
            case "Part records":
                fxmlFile = "Admin-partRecords.fxml";
                break;
            case "Rental records":
                fxmlFile = "Admin-rentalTransactions.fxml";
                break;
            case "Payment records":
                fxmlFile = "Admin-paymentTransactions.fxml";
                break;
            case "Maintenance records":
                fxmlFile = "Admin-maintenanceTransactions.fxml";
                break;
            case "Maintenance cheques":
                fxmlFile = "Admin-maintenanceCheques.fxml";
                break;
            case "Penalty records":
                fxmlFile = "Admin-penaltyRecords.fxml";
        }

        if (fxmlFile != null && mainController != null){
            mainController.loadPage(fxmlFile);
        } else {
            System.out.println("Link not found for: " + recordName);
        }
    }

    public static class RecordSummary {
        private final SimpleStringProperty info;
        private final SimpleIntegerProperty count;
        private final SimpleStringProperty category;

        public RecordSummary(String info, int count, String category) {
            this.info = new SimpleStringProperty(info);
            this.count = new SimpleIntegerProperty(count);
            this.category = new SimpleStringProperty(category);
        }

        public String getInfo() { return info.get(); }
        public SimpleStringProperty infoProperty() { return info; }
        public int getCount() { return count.get(); }
        public SimpleIntegerProperty countProperty() { return count; }
        public String getCategory() { return category.get(); }
        public SimpleStringProperty categoryProperty() { return category; }
    }
}