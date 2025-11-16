package main.gui;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import reports.CustomerRentalReport;

import reports.CustomerRentalReport.CustomerRentalData;
import reports.CustomerRentalReport.CustomerDemographicsData;
import reports.CustomerRentalReport.CustomerPenaltyRiskData;
import reports.CustomerRentalReport.SummaryStatistics;

import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.ResourceBundle;

public class Report_CustomerRentalController implements Initializable{

    @FXML private Label headerLabel;
    @FXML private Label subHeaderLabel;
    @FXML private Button backButton;
    @FXML private Button exportButton;
    @FXML private TableView<CustomerRentalData> reportTable;
    @FXML private TableColumn<CustomerRentalData, String> colCustomerID;
    @FXML private TableColumn<CustomerRentalData, String> colName;
    @FXML private TableColumn<CustomerRentalData, Integer> colRentals;
    @FXML private TableColumn<CustomerRentalData, Double> colTotalCost;
    @FXML private TableColumn<CustomerRentalData, Double> colAvgCost;
    @FXML private TableColumn<CustomerRentalData, Double> colTotalHours;
    @FXML private TableColumn<CustomerRentalData, Date> colLastRental;

    private Admin_dashboardController mainController;

    private List<CustomerRentalData> currentReportData;
    private List<CustomerDemographicsData> currentDemoData;
    private List<CustomerPenaltyRiskData> currentPenaltyData;
    private SummaryStatistics currentStats;
    private int reportYear;
    private int reportMonth;

    public void setMainController(Admin_dashboardController mainController) {
        this.mainController = mainController;
    }

    @Override
    public void initialize(URL url, ResourceBundle rb){
        colCustomerID.setCellValueFactory(new PropertyValueFactory<>("customerID"));
        colName.setCellValueFactory(new PropertyValueFactory<>("fullName"));
        colRentals.setCellValueFactory(new PropertyValueFactory<>("numberOfRentals"));
        colTotalCost.setCellValueFactory(new PropertyValueFactory<>("totalRentalCost"));
        colAvgCost.setCellValueFactory(new PropertyValueFactory<>("averageRentalCost"));
        colTotalHours.setCellValueFactory(new PropertyValueFactory<>("totalRentalDuration"));
        colLastRental.setCellValueFactory(new PropertyValueFactory<>("mostRecentRentalDate"));
    }

    // --- 3. UPDATE SETDATA TO RECEIVE EVERYTHING ---
    public void setData(
            List<CustomerRentalData> rentalData,
            List<CustomerDemographicsData> demoData,
            List<CustomerPenaltyRiskData> penaltyData,
            SummaryStatistics stats,
            int year, int month){

        this.currentReportData = rentalData;
        this.currentDemoData = demoData;
        this.currentPenaltyData = penaltyData;
        this.currentStats = stats;
        this.reportYear = year;
        this.reportMonth = month; //0 = yearly

        String[] months = {"", "January", "February", "March", "April", "May", "June",
                "July", "August", "September", "October", "November", "December"};

        if (month > 0) {
            subHeaderLabel.setText("Report for " + months[month] + " " + year);
        } else {
            subHeaderLabel.setText("Report for Year " + year);
        }

        // The table still only shows the main rental data
        ObservableList<CustomerRentalData> observableData = FXCollections.observableArrayList(rentalData);
        reportTable.setItems(observableData);
    }

    @FXML private void handleExportToPDF(){
        System.out.println("Exporting report to PDF...");

        try{
            CustomerRentalReport report = new CustomerRentalReport();
            String[] months = {"", "Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"};
            String fileName;

            if (reportMonth > 0) {

                fileName = String.format("Customer_Comprehensive_Report_%d_%s.pdf", reportYear, months[reportMonth]);
                report.exportToPDF(
                        currentReportData,
                        currentDemoData,
                        currentPenaltyData,
                        currentStats,
                        fileName,
                        reportYear,
                        reportMonth,
                        "Revenue" // sortBy
                );
            } else {

                fileName = String.format("Customer_Comprehensive_Report_%d.pdf", reportYear);
                report.exportYearlyToPDF(
                        currentReportData,
                        currentDemoData,
                        currentPenaltyData,
                        currentStats,
                        fileName,
                        reportYear,
                        "Revenue" // sortBy
                );
            }

            showAlert(Alert.AlertType.INFORMATION, "Export Successful",
                    "Report has been saved to the 'reports_output' folder as " + fileName);

        } catch (Exception e){
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Export Failed", "Could not save PDF file.");
        }
    }

    @FXML private void handleBack() {
        mainController.loadPage("Admin-customerRentalReportSelect.fxml");
    }

    private void showAlert(Alert.AlertType type, String title, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}