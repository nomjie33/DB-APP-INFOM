package main.gui;

import javafx.scene.image.WritableImage;
import javafx.embed.swing.SwingFXUtils;
import javax.imageio.ImageIO;
import java.io.File;
import java.io.IOException;

import javafx.scene.chart.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
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
    @FXML private BarChart<String, Number> locationBarChart;
    @FXML private CategoryAxis xAxis;
    @FXML private NumberAxis yAxis;

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

        if (locationBarChart != null) {
            locationBarChart.setAnimated(false);
            locationBarChart.setLegendVisible(false); // Optional: hides the "Rentals" legend if you don't want it
        }
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

        populateChart(data);
    }

    private void populateChart(List<LocationFrequencyData> data) {
        if (locationBarChart == null) {
            System.out.println("Error: BarChart is not injected properly.");
            return;
        }

        // Clear old data
        locationBarChart.getData().clear();

        // Create a new Data Series
        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName("Rentals");

        // Loop through data and add to graph
        for (LocationFrequencyData item : data) {
            // Safety check: ensure location name isn't null
            String location = item.getLocationName() != null ? item.getLocationName() : "Unknown";
            int rentals = item.getNumberOfRentals();

            series.getData().add(new XYChart.Data<>(location, rentals));
        }

        // Add the series to the chart
        locationBarChart.getData().add(series);
    }

    @FXML
    private void handleExportToPDF() {
        System.out.println("Exporting location report to PDF...");
        try {

            String chartImagePath = null;
            if (locationBarChart != null) {
                try {

                    WritableImage image = locationBarChart.snapshot(new javafx.scene.SnapshotParameters(), null);

                    File tempFile = new File("temp_location_chart.png");

                    ImageIO.write(SwingFXUtils.fromFXImage(image, null), "png", tempFile);

                    chartImagePath = tempFile.getAbsolutePath();
                    System.out.println("Chart captured at: " + chartImagePath);
                } catch (IOException ex) {
                    System.err.println("Failed to save chart image: " + ex.getMessage());
                }
            }
            LocationRentalFrequencyReport report = new LocationRentalFrequencyReport();

            String[] months = {"", "Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"};
            String fileName;

            if (reportMonth > 0) {
                fileName = String.format("Location_Report_%d_%s.pdf", reportYear, months[reportMonth]);
            } else {
                fileName = String.format("Location_Report_%d_Yearly.pdf", reportYear);
            }

            report.exportToPDF(currentReportData, fileName, reportYear, reportMonth, chartImagePath);

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