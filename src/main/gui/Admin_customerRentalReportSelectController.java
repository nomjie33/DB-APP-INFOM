package main.gui;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import reports.CustomerRentalReport;
import reports.CustomerRentalReport.CustomerRentalData;

import java.net.URL;
import java.time.Month;
import java.time.Year;
import java.util.List;
import java.util.ResourceBundle;

public class Admin_customerRentalReportSelectController implements Initializable {

    @FXML private ComboBox<Integer> yearComboBox;
    @FXML private ComboBox<String> monthComboBox;

    private Admin_dashboardController mainController;

    public void setMainController(Admin_dashboardController mainController) {
        this.mainController = mainController;
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

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
        Integer year = yearComboBox.getValue();
        String monthName = monthComboBox.getValue();

        if (year == null) {
            showAlert(Alert.AlertType.WARNING, "Validation Error", "Please select a year.");
            return;
        }

        CustomerRentalReport report = new CustomerRentalReport();

        List<CustomerRentalData> rentalData;
        List<CustomerRentalReport.CustomerDemographicsData> demoData;
        List<CustomerRentalReport.CustomerPenaltyRiskData> riskData;
        CustomerRentalReport.SummaryStatistics stats;

        int monthInt = 0;
        if (monthName == null || monthName.isEmpty()) {

            System.out.println("Generating Yearly Report for " + year + "...");

            rentalData = report.generateYearlySummary(year, "Revenue");
            demoData = report.generateDemographics(); // This one has no filter
            riskData = report.generateYearlyPenaltyRiskAnalysis(year);
            stats = report.generateSummaryStatistics(rentalData, riskData); // Takes 2 args

        } else {

            monthInt = Month.valueOf(monthName.toUpperCase()).getValue();
            System.out.println("Generating Monthly Report for " + monthName + " " + year + "...");

            rentalData = report.generateRentalSummary(year, monthInt, "Revenue");
            demoData = report.generateDemographics();
            riskData = report.generatePenaltyRiskAnalysis(year, monthInt);
            stats = report.generateSummaryStatistics(rentalData, riskData); // Takes 2 args
        }

        if (rentalData.isEmpty()) {
            showAlert(Alert.AlertType.INFORMATION, "No Data", "No customer data found for the specified period.");
        } else {
            mainController.loadCustomerReportDisplay(rentalData, demoData, riskData, stats, year, monthInt);
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