package main.gui;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import reports.RentalRevenueReport;
import reports.RentalRevenueReport.RevenueData;

import java.net.URL;
import java.time.Month;
import java.time.Year;
import java.util.List;
import java.util.ResourceBundle;

public class Admin_revenueSelectController implements Initializable{

    @FXML private ComboBox<String> vehicleTypeComboBox;
    @FXML private ComboBox<Integer> yearComboBox;
    @FXML private ComboBox<String> monthComboBox;

    private Admin_dashboardController mainController;

    public void setMainController(Admin_dashboardController mainController) {
        this.mainController = mainController;
    }

    @Override
    public void initialize(URL url, ResourceBundle rb){

        vehicleTypeComboBox.getItems().addAll("All", "E-Scooter", "E-Bike", "E-Trike");
        vehicleTypeComboBox.setValue("All");

        int currentYear = Year.now().getValue();
        for (int i = currentYear; i >= 2020; i--) {
            yearComboBox.getItems().add(i);
        }

        for (Month month : Month.values()) {
            String monthName = month.toString();
            String formattedName = monthName.substring(0, 1).toUpperCase() + monthName.substring(1).toLowerCase();
            monthComboBox.getItems().add(formattedName);
        }
    }

    @FXML private void handleGenerateReport() {
        String vehicleType = vehicleTypeComboBox.getValue();
        Integer year = yearComboBox.getValue();
        String monthName = monthComboBox.getValue();

        if (year == null) {
            showAlert(Alert.AlertType.WARNING, "Validation Error", "Please select a year.");
            return;
        }

        RentalRevenueReport report = new RentalRevenueReport();
        List<RevenueData> reportData;
        String reportTypeTitle;
        int monthInt = 0;

        if (monthName == null || monthName.isEmpty()) {

            System.out.println("Generating Yearly Report for " + year + "...");
            reportData = report.generateYearlyReport(vehicleType, year, year);
            reportTypeTitle = "Yearly Report for " + year;

        } else {

            monthInt = Month.valueOf(monthName.toUpperCase()).getValue();
            System.out.println("Generating Monthly Report for " + monthName + " " + year + "...");
            reportData = report.generateMonthlyReport(vehicleType, year, monthInt, monthInt);
            reportTypeTitle = "Monthly Report for " + monthName + " " + year;
        }

        if (reportData.isEmpty()) {
            showAlert(Alert.AlertType.INFORMATION, "No Data", "No revenue data found for the specified period.");
        } else {
            mainController.loadRevenueReportDisplay(reportData, reportTypeTitle, vehicleType);
        }
    }

    @FXML private void handleCancel() {
        mainController.loadPage("Admin-home.fxml");
    }

    private void showAlert(Alert.AlertType type, String title, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}
