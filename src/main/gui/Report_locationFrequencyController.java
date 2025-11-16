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
import reports.LocationRentalFrequencyReport;
import reports.LocationRentalFrequencyReport.LocationFrequencyData;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class Report_locationFrequencyController implements Initializable {

    @FXML private Label subHeaderLabel;
    @FXML private TableView<LocationFrequencyData> reportTable;
    @FXML private TableColumn<LocationFrequencyData, String> colLocationName;
    @FXML private TableColumn<LocationFrequencyData, Integer> colRentals;
    @FXML private TableColumn<LocationFrequencyData, Double> colAvgDuration;
    @FXML private TableColumn<LocationFrequencyData, Double> colTotalRevenue;
    @FXML private TableColumn<LocationFrequencyData, String> colTopVehicle;
    @FXML private TableColumn<LocationFrequencyData, Integer> colDeployments;

    private Admin_dashboardController mainController;

    private List<LocationFrequencyData> currentReportData;
    private int reportYear;
    private int reportMonth; // 0 = Yearly

    public void setMainController(Admin_dashboardController mainController) {
        this.mainController = mainController;
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        colLocationName.setCellValueFactory(new PropertyValueFactory<>("locationName"));
        colRentals.setCellValueFactory(new PropertyValueFactory<>("numberOfRentals"));
        colAvgDuration.setCellValueFactory(new PropertyValueFactory<>("averageRentalDuration"));
        colTotalRevenue.setCellValueFactory(new PropertyValueFactory<>("totalRevenue"));
        colTopVehicle.setCellValueFactory(new PropertyValueFactory<>("mostRentedVehicleType"));
        colDeployments.setCellValueFactory(new PropertyValueFactory<>("vehicleDeploymentCount"));
    }

    public void setData(List<LocationFrequencyData> data, int year, int month) {
        this.currentReportData = data;
        this.reportYear = year;
        this.reportMonth = month;

        String[] months = {"", "January", "February", "March", "April", "May", "June",
                "July", "August", "September", "October", "November", "December"};

        if (month > 0) {
            subHeaderLabel.setText("Rental Frequency for " + months[month] + " " + year);
        } else {
            subHeaderLabel.setText("Rental Frequency for Year " + year);
        }

        reportTable.setItems(FXCollections.observableArrayList(data));
    }

    @FXML
    private void handleExportToPDF() {
        System.out.println("Exporting location report to PDF...");
        try {
            LocationRentalFrequencyReport report = new LocationRentalFrequencyReport();

            String[] months = {"", "Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"};
            String fileName;

            if (reportMonth > 0) {
                fileName = String.format("Location_Report_%d_%s.pdf", reportYear, months[reportMonth]);
            } else {
                fileName = String.format("Location_Report_%d_Yearly.pdf", reportYear);
            }

            report.exportToPDF(currentReportData, fileName, reportYear, reportMonth);

            showAlert(Alert.AlertType.INFORMATION, "Export Successful",
                    "Report has been saved to the 'reports_output' folder as " + fileName);

        } catch (Exception e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Export Failed", "Could not save PDF file.");
        }
    }

    @FXML
    private void handleBack() {

        mainController.loadPage("Admin-locationFrequencySelect.fxml");
    }

    private void showAlert(Alert.AlertType type, String title, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}