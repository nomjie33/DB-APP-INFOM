package main.gui;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import model.*;
import reports.*;
import reports.CustomerRentalReport.CustomerDemographicsData;
import reports.CustomerRentalReport.CustomerPenaltyRiskData;
import reports.CustomerRentalReport.SummaryStatistics;
import reports.DefectiveVehiclesReport;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class Admin_dashboardController implements Initializable {

    // --- FXML Declarations ---
    @FXML private BorderPane adminDashboardRoot;
    @FXML private AnchorPane centerContentPane;
    @FXML private Button quitButton;

    // Main Nav
    @FXML private HBox homeButton;
    @FXML private HBox settingsButton;

    // Core Records
    @FXML private HBox customerRecordsButton;
    @FXML private HBox vehicleRecordsButton;
    @FXML private HBox locationRecordsButton;
    @FXML private HBox technicianRecordsButton;
    @FXML private HBox partRecordsButton;

    // Transaction Records
    @FXML private HBox rentalTransactionsButton;
    @FXML private HBox paymentTransactionsButton;
    @FXML private HBox deploymentTransactionsButton;
    @FXML private HBox maintenanceTransactionsButton;
    @FXML private HBox maintenanceChequesButton;
    @FXML private HBox penaltyTransactionsButton;

    // Reports
    @FXML private HBox reportRentalRevenueButton;
    @FXML private HBox reportDefectiveVehiclesButton;
    @FXML private HBox reportLocationFrequencyButton;
    @FXML private HBox reportCustomerRentalButton;

    // List to manage nav button styling
    private List<HBox> navButtons;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        navButtons = List.of(
                homeButton, settingsButton, customerRecordsButton, vehicleRecordsButton,
                locationRecordsButton, technicianRecordsButton, partRecordsButton,
                rentalTransactionsButton, paymentTransactionsButton, deploymentTransactionsButton,
                maintenanceTransactionsButton, maintenanceChequesButton, penaltyTransactionsButton,
                reportRentalRevenueButton, reportDefectiveVehiclesButton, reportLocationFrequencyButton, reportCustomerRentalButton
        );

        handleHome(null);
    }

    @FXML void handleHome(MouseEvent event) {
        setActiveNav(homeButton);
        System.out.println("Home clicked.");
        loadPage("Admin-home.fxml");
    }

    @FXML void handleSettings(MouseEvent event) {
        setActiveNav(settingsButton);
        System.out.println("Settings clicked.");
        loadPage("Admin-settings.fxml");
    }

    @FXML void handleLogout(ActionEvent event) {
        try {
            Parent loginRoot = FXMLLoader.load(getClass().getResource("Main-login.fxml"));
            Scene loginScene = new Scene(loginRoot);
            Stage currentStage = (Stage) quitButton.getScene().getWindow();
            currentStage.setScene(loginScene);
            currentStage.setTitle("UVR!");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML void handleCustomerRecords(MouseEvent event) {
        setActiveNav(customerRecordsButton);
        System.out.println("Customer Records clicked");
        loadPage("Admin-customerRecords.fxml");
    }

    @FXML void handleVehicleRecords(MouseEvent event) {
        setActiveNav(vehicleRecordsButton);
        System.out.println("Vehicle Records clicked");
        loadPage("Admin-vehicleRecords.fxml");
    }

    @FXML void handleLocationRecords(MouseEvent event) {
        setActiveNav(locationRecordsButton);
        System.out.println("Location Records clicked");
        loadPage("Admin-locationRecords.fxml");
    }

    @FXML void handleTechnicianRecords(MouseEvent event) {
        setActiveNav(technicianRecordsButton);
        System.out.println("Technician Records clicked");
        loadPage("Admin-technicianRecords.fxml");
    }

    @FXML void handlePartRecords(MouseEvent event) {
        setActiveNav(partRecordsButton);
        System.out.println("Part Records clicked");
        loadPage("Admin-partRecords.fxml");
    }

    @FXML void handleRentalTransactions(MouseEvent event) {
        setActiveNav(rentalTransactionsButton);
        System.out.println("Rental Transactions clicked");
        loadPage("Admin-rentalTransactions.fxml");
    }

    @FXML void handlePaymentTransactions(MouseEvent event) {
        setActiveNav(paymentTransactionsButton);
        System.out.println("Payment Transactions clicked");
        loadPage("Admin-paymentRecords.fxml");
    }

    @FXML void handleDeploymentTransactions(MouseEvent event) {
        setActiveNav(deploymentTransactionsButton);
        System.out.println("Deployment Transactions clicked");
        loadPage("Admin-deploymentRecords.fxml");
    }

    @FXML void handleMaintenanceTransactions(MouseEvent event) {
        setActiveNav(maintenanceTransactionsButton);
        System.out.println("Maintenance Transactions clicked");
        loadPage("Admin-maintenanceTransactions.fxml");
    }

    @FXML void handleMaintenanceCheques(MouseEvent event) {
        setActiveNav(maintenanceChequesButton);
        System.out.println("Maintenance Cheques clicked");
        loadPage("Admin-maintenanceCheques.fxml");
    }

    @FXML void handlePenaltyTransactions(MouseEvent event) {
        setActiveNav(penaltyTransactionsButton);
        System.out.println("Penalty Transactions clicked");
        loadPage("Admin-penaltyRecords.fxml");
    }

    @FXML void handleReportRentalRevenue(MouseEvent event) {
        setActiveNav(reportRentalRevenueButton);
        System.out.println("Rental Revenue Report clicked");
        loadPage("Admin-revenueSelect.fxml");
    }

    @FXML
    void handleReportDefectiveVehicles(MouseEvent event) {
        setActiveNav(reportDefectiveVehiclesButton);
        System.out.println("Defective Vehicles Report clicked");
        loadPage("Admin-defectiveVehicleSelect.fxml");
    }

    @FXML void handleReportLocationFrequency(MouseEvent event) {
        setActiveNav(reportLocationFrequencyButton);
        System.out.println("Location Frequency Report clicked");
        loadPage("Admin-locationFrequencySelect.fxml");
    }

    @FXML void handleReportCustomerRental(MouseEvent event) {
        setActiveNav(reportCustomerRentalButton);
        System.out.println("Customer Rental Report clicked");
        loadPage("Admin-customerRentalReportSelect.fxml");
    }

    private void setActiveNav(HBox activeButton) {
        if (navButtons == null) return;
        for (HBox button : navButtons) {
            button.getStyleClass().remove("nav-button-active");
            button.getStyleClass().remove("nav-button-sub-active");
        }

        if (activeButton.getStyleClass().contains("nav-button-sub")) {
            activeButton.getStyleClass().add("nav-button-sub-active");
        } else {
            activeButton.getStyleClass().add("nav-button-active");
        }
    }

    public void loadPage(String fxmlFile) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlFile));
            Parent page = loader.load();

            Object controller = loader.getController();

            if (controller instanceof Admin_homeController){
                ((Admin_homeController) controller).setMainController(this);
            } else if (controller instanceof  Admin_customerRecordsController){
                ((Admin_customerRecordsController) controller).setMainController(this);
            } else if (controller instanceof Admin_vehicleRecordsController){
                ((Admin_vehicleRecordsController) controller).setMainController(this);
            } else if (controller instanceof Admin_locationRecordsController){
                ((Admin_locationRecordsController) controller).setMainController(this);
            } else if (controller instanceof Admin_technicianRecordsController) {
                ((Admin_technicianRecordsController) controller).setMainController(this);
            } else if (controller instanceof Admin_partRecordsController){
                ((Admin_partRecordsController) controller).setMainController(this);
            } else if (controller instanceof Admin_paymentRecordsController) {
                ((Admin_paymentRecordsController) controller).setMainController(this);
            } else if (controller instanceof Admin_deploymentRecordsController) {
                ((Admin_deploymentRecordsController) controller).setMainController(this);
            } else if (controller instanceof Admin_customerRentalReportSelectController) {
                ((Admin_customerRentalReportSelectController) controller).setMainController(this);
            } else if (controller instanceof Admin_defectiveVehicleSelectController) {
                ((Admin_defectiveVehicleSelectController) controller).setMainController(this);
            } else if (controller instanceof Admin_revenueSelectController) {
                ((Admin_revenueSelectController) controller).setMainController(this);
            } else if (controller instanceof Admin_locationFrequencyController) {
                ((Admin_locationFrequencyController) controller).setMainController(this);
            }


            loadPageFromSub(page);

        } catch (IOException e) {
            System.err.println("Failed to load page: " + fxmlFile);
            e.printStackTrace();
        }
    }

    public void loadPageFromSub(Parent page) {
        centerContentPane.getChildren().clear();
        centerContentPane.getChildren().add(page);

        AnchorPane.setTopAnchor(page, 0.0);
        AnchorPane.setBottomAnchor(page, 0.0);
        AnchorPane.setLeftAnchor(page, 0.0);
        AnchorPane.setRightAnchor(page, 0.0);
    }

    public void loadCustomerForm(Customer customerToEdit){
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("Admin-customerForm.fxml"));
            Parent page = loader.load();

            Admin_customerFormController controller = loader.getController();
            controller.setMainController(this);

            if (customerToEdit != null){
                controller.setCustomerData(customerToEdit);
            }
            loadPageFromSub(page);

        } catch (IOException e){
            e.printStackTrace();
        }
    }

    public void loadVehicleForm(Vehicle vehicleToEdit){
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("Admin-vehicleForm.fxml"));
            Parent page = loader.load();

            Admin_vehicleFormController controller = loader.getController();
            controller.setMainController(this);

            if (vehicleToEdit != null){
                controller.setVehicleData(vehicleToEdit);
            }
            loadPageFromSub(page);

        } catch (IOException e){
            e.printStackTrace();
        }
    }

    public void loadLocationForm(Location locationToEdit){
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("Admin-locationForm.fxml"));
            Parent page = loader.load();

            Admin_locationFormController controller = loader.getController();
            controller.setMainController(this);

            if (locationToEdit != null){
                controller.setLocationData(locationToEdit);
            }
            loadPageFromSub(page);

        } catch (IOException e){
            e.printStackTrace();
        }
    }

    public void loadTechnicianForm(Technician techToEdit){
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("Admin-technicianForm.fxml"));
            Parent page = loader.load();

            Admin_technicianFormController controller = loader.getController();
            controller.setMainController(this);

            if (techToEdit != null){
                controller.setTechnicianData(techToEdit);
            }

            loadPageFromSub(page);

        } catch (IOException e){
            System.err.println("Failed to load technician form.");
            e.printStackTrace();
        }
    }

    public void loadPartForm(Part partToEdit){
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("Admin-partForm.fxml"));
            Parent page = loader.load();

            Admin_partFormController controller = loader.getController();
            controller.setMainController(this);

            if (partToEdit != null){
                controller.setPartData(partToEdit);
            }

            loadPageFromSub(page);

        } catch (IOException e){
            System.err.println("Failed to load part form.");
            e.printStackTrace();
        }
    }

    public void loadPaymentForm(PaymentTransaction payment) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/main/gui/Admin-paymentForm.fxml"));
            AnchorPane root = loader.load();

            Admin_paymentFormController controller = loader.getController();
            controller.setMainController(this);

            if (payment != null) {
                controller.setPaymentData(payment);
            }

            centerContentPane.getChildren().setAll(root);

        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("Failed to load Payment Form FXML.");
        }
    }

    public void loadDeploymentForm(DeploymentTransaction deployment) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("Admin-deploymentForm.fxml"));
            Parent page = loader.load();

            Admin_deploymentFormController controller = loader.getController();
            controller.setMainController(this);

            controller.setDeploymentData(deployment);

            loadPageFromSub(page);

        } catch (IOException e) {
            System.err.println("Failed to load deployment form.");
            e.printStackTrace();
        }
    }

    public void loadCustomerReportDisplay(
            List<CustomerRentalReport.CustomerRentalData> rentalData,
            List<CustomerRentalReport.CustomerDemographicsData> demographicsData,
            List<CustomerRentalReport.CustomerPenaltyRiskData> penaltyRiskData,
            CustomerRentalReport.SummaryStatistics summaryStats,
            int year,
            int month) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("Report-CustomerRentalDisplay.fxml"));
            Parent page = loader.load();

            Report_CustomerRentalController controller = loader.getController();
            controller.setMainController(this);

            // Now pass all 6 arguments forward
            controller.setData(
                    rentalData,
                    demographicsData,
                    penaltyRiskData,
                    summaryStats,
                    year,
                    month
            );

            loadPageFromSub(page);
        } catch (Exception e){
            System.err.println("Failed to load page: Report-CustomerRentalDisplay.fxml");
            e.printStackTrace();
        }
    }

    public void loadDefectiveReportDisplay(List<DefectiveVehiclesReport.DefectiveVehicleData> data, int year, int month) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("Report-defectiveVehicleDisplay.fxml"));
            Parent page = loader.load();

            Report_defectiveVehicleDisplayController controller = loader.getController();
            controller.setMainController(this);

            controller.setData(data, year, month);

            loadPageFromSub(page);

        } catch (IOException e) {
            System.err.println("Failed to load page: Report-defectiveVehicleDisplay.fxml");
            e.printStackTrace();
        }
    }

    public void loadLocationReportDisplay(List<LocationRentalFrequencyReport.LocationFrequencyData> data, int year, int month) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("Report-locationFrequencyDisplay.fxml"));
            Parent page = loader.load();

            Report_locationFrequencyController controller = loader.getController();
            controller.setMainController(this);

            controller.setData(data, year, month);

            loadPageFromSub(page);

        } catch (IOException e){
            System.err.println("Failed to load page: Report-locationFrequencyDisplay.fxml");
            e.printStackTrace();
        }
    }
    public void loadRevenueReportDisplay(List<RentalRevenueReport.RevenueData> data, String reportTypeTitle, String vehicleType) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("Report-RevenueDisplay.fxml"));
            Parent page = loader.load();

            Report_RevenueDisplayController controller = loader.getController();
            controller.setMainController(this);

            controller.setData(data, reportTypeTitle, vehicleType);

            loadPageFromSub(page);

        } catch (IOException e){
            System.err.println("Failed to load page: Report-RevenueDisplay.fxml");
            e.printStackTrace();
        }
    }

    public Stage getPrimaryStage(){
        return (Stage) adminDashboardRoot.getScene().getWindow();
    }

}