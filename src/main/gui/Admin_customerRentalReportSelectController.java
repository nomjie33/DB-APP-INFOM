package main.gui;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import reports.CustomerRentalReport;
import reports.CustomerRentalReport.CustomerRentalData;

import reports.CustomerRentalReport.CustomerDemographicsData;
import reports.CustomerRentalReport.CustomerPenaltyRiskData;
import reports.CustomerRentalReport.SummaryStatistics;

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
        yearComboBox.setValue(currentYear);

        monthComboBox.getItems().add("Yearly (All Months)");
        for (Month month : Month.values()) {
            String monthName = month.toString();
            String formattedName = monthName.substring(0, 1).toUpperCase() + monthName.substring(1).toLowerCase();
            monthComboBox.getItems().add(formattedName);
        }
        monthComboBox.setValue("Yearly (All Months)");
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
        List<CustomerDemographicsData> demographicsData;
        List<CustomerPenaltyRiskData> penaltyRiskData;
        SummaryStatistics summaryStats;

        int monthInt = 0;

        if (monthName == null || monthName.isEmpty() || monthName.equals("Yearly (All Months)")) {

            System.out.println("Generating Yearly Report for " + year + "...");

            // --- Generate all 4 data components for the YEAR ---
            // 1. Rental Summary
            rentalData = report.generateYearlySummary(year, "Revenue"); // <-- Corrected method name
            // 2. Demographics (not time-based)
            demographicsData = report.generateDemographics();
            // 3. Penalty Risk
            penaltyRiskData = report.generateYearlyPenaltyRiskAnalysis(year);
            // 4. Summary Statistics
            summaryStats = report.generateSummaryStatistics(rentalData, penaltyRiskData);

        } else {

            monthInt = Month.valueOf(monthName.toUpperCase()).getValue();
            System.out.println("Generating Monthly Report for " + monthName + " " + year + "...");

            // --- Generate all 4 data components for the MONTH ---
            // 1. Rental Summary
            rentalData = report.generateRentalSummary(year, monthInt, "Revenue"); // <-- Corrected method name
            // 2. Demographics (not time-based)
            demographicsData = report.generateDemographics();
            // 3. Penalty Risk
            penaltyRiskData = report.generatePenaltyRiskAnalysis(year, monthInt);
            // 4. Summary Statistics
            summaryStats = report.generateSummaryStatistics(rentalData, penaltyRiskData);
        }

        if (rentalData.isEmpty()) {
            showAlert(Alert.AlertType.INFORMATION, "No Data", "No customer rental data found for the specified period.");
        } else {
            // --- Pass all 6 arguments to the display controller ---
            mainController.loadCustomerReportDisplay(
                    rentalData,
                    demographicsData,
                    penaltyRiskData,
                    summaryStats,
                    year,
                    monthInt
            );
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