package main.gui;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import reports.LocationRentalFrequencyReport;
import reports.LocationRentalFrequencyReport.LocationFrequencyData;

import java.net.URL;
import java.time.Month;
import java.time.Year;
import java.util.List;
import java.util.ResourceBundle;

public class Admin_locationFrequencyController implements Initializable {

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

    @FXML
    private void handleGenerateReport() {
        Integer year = yearComboBox.getValue();
        String monthName = monthComboBox.getValue();

        if (year == null) {
            showAlert(Alert.AlertType.WARNING, "Validation Error", "Please select a year.");
            return;
        }

        LocationRentalFrequencyReport report = new LocationRentalFrequencyReport();
        List<LocationFrequencyData> reportData;
        int monthInt = 0; // 0 = Yearly

        if (monthName == null || monthName.isEmpty()) {

            System.out.println("Generating Yearly Location Report for " + year + "...");
            reportData = report.generateYearlyReport(year);

        } else {

            monthInt = Month.valueOf(monthName.toUpperCase()).getValue();
            System.out.println("Generating Monthly Location Report for " + monthName + " " + year + "...");
            reportData = report.generateMonthlyReport(year, monthInt);
        }

        if (reportData.isEmpty()) {
            showAlert(Alert.AlertType.INFORMATION, "No Data", "No location rental data found for the specified period.");
        } else {
            mainController.loadLocationReportDisplay(reportData, year, monthInt);
        }
    }

    @FXML
    private void handleCancel() {
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