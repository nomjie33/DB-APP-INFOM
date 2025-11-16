package main.gui;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import reports.RentalRevenueReport;
import reports.RentalRevenueReport.RevenueData; // Import the inner class

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class Report_RevenueDisplayController implements Initializable {

    @FXML private Label subHeaderLabel;
    @FXML private TableView<RevenueData> reportTable;
    @FXML private TableColumn<RevenueData, String> colTimePeriod;
    @FXML private TableColumn<RevenueData, String> colVehicleType;
    @FXML private TableColumn<RevenueData, Double> colTotalRevenue;
    @FXML private TableColumn<RevenueData, Double> colAvgRevenue;
    @FXML private TableColumn<RevenueData, Integer> colRentals;

    private Admin_dashboardController mainController;

    private List<RevenueData> currentReportData;
    private String reportTypeTitle;
    private String vehicleType;

    public void setMainController(Admin_dashboardController mainController) {
        this.mainController = mainController;
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        colTimePeriod.setCellValueFactory(new PropertyValueFactory<>("timePeriod"));
        colVehicleType.setCellValueFactory(new PropertyValueFactory<>("vehicleType"));
        colTotalRevenue.setCellValueFactory(new PropertyValueFactory<>("totalRevenue"));
        colAvgRevenue.setCellValueFactory(new PropertyValueFactory<>("averageRevenue"));
        colRentals.setCellValueFactory(new PropertyValueFactory<>("numberOfRentals"));
    }

    public void setData(List<RevenueData> data, String reportTypeTitle, String vehicleType) {
        this.currentReportData = data;
        this.reportTypeTitle = reportTypeTitle;
        this.vehicleType = vehicleType;

        subHeaderLabel.setText("Showing: " + reportTypeTitle + " (" + vehicleType + ")");

        reportTable.setItems(FXCollections.observableArrayList(data));
    }

    @FXML
    private void handleExportToPDF() {
        System.out.println("Exporting revenue report to PDF...");
        try {
            RentalRevenueReport report = new RentalRevenueReport();

            String fileName = "Revenue_Report_" + reportTypeTitle.replace(" ", "_") + ".pdf";

            report.exportToPDF(currentReportData, fileName, reportTypeTitle, vehicleType);

            showAlert(Alert.AlertType.INFORMATION, "Export Successful",
                    "Report has been saved to the 'reports_output' folder as " + fileName);

        } catch (Exception e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Export Failed", "Could not save PDF file.");
        }
    }

    @FXML private void handleBack() {

        mainController.loadPage("Admin-RevenueSelect.fxml");
    }

    private void showAlert(Alert.AlertType type, String title, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}