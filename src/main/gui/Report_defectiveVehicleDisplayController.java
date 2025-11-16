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
import reports.DefectiveVehiclesReport;
import reports.DefectiveVehiclesReport.DefectiveVehicleData;

import java.net.URL;
import java.util.Date;
import java.util.List;
import java.util.ResourceBundle;

// THIS IS THE FIX: Class name matches your file
public class Report_defectiveVehicleDisplayController implements Initializable {

    @FXML private Label subHeaderLabel;
    @FXML private TableView<DefectiveVehicleData> reportTable;
    @FXML private TableColumn<DefectiveVehicleData, String> colPlateID;
    @FXML private TableColumn<DefectiveVehicleData, String> colVehicleType;
    @FXML private TableColumn<DefectiveVehicleData, Integer> colTimesMaint;
    @FXML private TableColumn<DefectiveVehicleData, Double> colTotalCost;
    @FXML private TableColumn<DefectiveVehicleData, Double> colDaysMaint;
    @FXML private TableColumn<DefectiveVehicleData, Date> colLastMaint;
    @FXML private TableColumn<DefectiveVehicleData, Integer> colTotalRentals;
    @FXML private TableColumn<DefectiveVehicleData, Double> colRatio;

    private Admin_dashboardController mainController;

    private List<DefectiveVehicleData> currentReportData;
    private int reportYear;
    private int reportMonth; // 0 = Yearly

    public void setMainController(Admin_dashboardController mainController) {
        this.mainController = mainController;
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        colPlateID.setCellValueFactory(new PropertyValueFactory<>("plateID"));
        colVehicleType.setCellValueFactory(new PropertyValueFactory<>("vehicleType"));
        colTimesMaint.setCellValueFactory(new PropertyValueFactory<>("timesMaintained"));
        colTotalCost.setCellValueFactory(new PropertyValueFactory<>("totalMaintenanceCost"));
        colDaysMaint.setCellValueFactory(new PropertyValueFactory<>("totalDaysInMaintenance"));
        colLastMaint.setCellValueFactory(new PropertyValueFactory<>("lastMaintenanceDate"));
        colTotalRentals.setCellValueFactory(new PropertyValueFactory<>("totalRentalsLifetime"));
        colRatio.setCellValueFactory(new PropertyValueFactory<>("costToRevenueRatio"));
    }

    public void setData(List<DefectiveVehicleData> data, int year, int month) {
        this.currentReportData = data;
        this.reportYear = year;
        this.reportMonth = month;

        String[] months = {"", "January", "February", "March", "April", "May", "June",
                "July", "August", "September", "October", "November", "December"};

        if (month > 0) {
            subHeaderLabel.setText("Maintenance Activity for " + months[month] + " " + year);
        } else {
            subHeaderLabel.setText("Maintenance Activity for Year " + year);
        }

        reportTable.setItems(FXCollections.observableArrayList(data));
    }

    @FXML
    private void handleExportToPDF() {
        System.out.println("Exporting defective report to PDF...");
        try {
            DefectiveVehiclesReport report = new DefectiveVehiclesReport();
            String[] months = {"", "Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"};
            String fileName;

            if (reportMonth > 0) {
                fileName = String.format("Defective_Report_%d_%s.pdf", reportYear, months[reportMonth]);
            } else {
                fileName = String.format("Defective_Report_%d_Yearly.pdf", reportYear);
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

        mainController.loadPage("Admin_defectiveVehicleSelect.fxml");
    }

    private void showAlert(Alert.AlertType type, String title, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}