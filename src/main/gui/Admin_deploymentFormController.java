package main.gui;

import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import java.io.IOException;

import dao.DeploymentDAO;
import dao.VehicleDAO;
import dao.LocationDAO;
import model.DeploymentTransaction;
import model.Vehicle; // Corrected model name
import model.Location; // Corrected model name

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.Alert.AlertType;
import javafx.util.StringConverter;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.net.URL;
import java.sql.Date;
import java.time.LocalDate;
import java.util.List;
import java.util.ResourceBundle;

public class Admin_deploymentFormController implements Initializable {

    @FXML private Label formHeaderLabel;
    @FXML private TextField deploymentIDField;

    @FXML private ComboBox<Location> locationComboBox; // FIX: Using ComboBox
    @FXML private ComboBox<Vehicle> plateComboBox;    // FIX: Using ComboBox

    @FXML private DatePicker startDatePicker;
    @FXML private DatePicker endDatePicker;
    @FXML private Label endDateLabel;

    private Admin_dashboardController mainController;
    private DeploymentDAO deploymentDAO = new DeploymentDAO();
    private VehicleDAO vehicleDAO = new VehicleDAO();
    private LocationDAO locationDAO = new LocationDAO();

    private DeploymentTransaction currentDeployment;
    private boolean isUpdatingRecord = false;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // FIX: Load ComboBoxes immediately after FXML injection
        loadComboBoxes();
    }

    public void setMainController(Admin_dashboardController mainController){
        this.mainController = mainController;
    }

    public void setDeploymentData(DeploymentTransaction deployment){
        if (deployment != null){
            isUpdatingRecord = true;
            currentDeployment = deployment; // Store for update
            formHeaderLabel.setText("Update Deployment");

            deploymentIDField.setDisable(true);
            deploymentIDField.getStyleClass().add("form-text-field-disabled");

            deploymentIDField.setText(deployment.getDeploymentID());

            endDateLabel.setVisible(true);
            endDatePicker.setVisible(true);

            Vehicle deployedVehicle = findVehicleInList(deployment.getPlateID());
            plateComboBox.setValue(deployedVehicle);
            Location deployedLocation = findLocationInList(deployment.getLocationID());
            locationComboBox.setValue(deployedLocation);

            if (deployment.getStartDate() != null) {
                startDatePicker.setValue(deployment.getStartDate().toLocalDate());
            }
            if (deployment.getEndDate() != null) {
                endDatePicker.setValue(deployment.getEndDate().toLocalDate());
            } else {
                endDatePicker.setValue(null);
            }

            // Disable foreign key fields on update
            plateComboBox.setDisable(true);
            plateComboBox.getStyleClass().add("form-text-field-disabled");
            locationComboBox.setDisable(true);
            locationComboBox.getStyleClass().add("form-text-field-disabled");
            startDatePicker.setDisable(true);
            startDatePicker.getStyleClass().add("form-text-field-disabled");

            if ("Completed".equalsIgnoreCase(deployment.getStatus())) {
                endDatePicker.setDisable(true);
                endDatePicker.getStyleClass().add("form-text-field-disabled");
            } else {
                endDatePicker.setDisable(false);
                endDatePicker.getStyleClass().remove("form-text-field-disabled");
            }

        } else {
            isUpdatingRecord = false;
            formHeaderLabel.setText("New Deployment");

            deploymentIDField.setDisable(false);
            deploymentIDField.getStyleClass().remove("form-text-field-disabled");
            deploymentIDField.setText("");

            endDateLabel.setVisible(false);
            endDatePicker.setVisible(false);

            plateComboBox.setDisable(false);
            locationComboBox.setDisable(false);
            startDatePicker.setDisable(false);

            plateComboBox.getStyleClass().remove("form-text-field-disabled");
            locationComboBox.getStyleClass().remove("form-text-field-disabled");
            startDatePicker.getStyleClass().remove("form-text-field-disabled");

            endDatePicker.setValue(null);
            plateComboBox.setValue(null);
            locationComboBox.setValue(null);
        }
    }

    @FXML
    private void handleSave(){
        if (!validateFields()){
            return;
        }

        DeploymentTransaction deployment = isUpdatingRecord ? currentDeployment : new DeploymentTransaction();

        try {

            deployment.setDeploymentID(deploymentIDField.getText());
            deployment.setPlateID(plateComboBox.getValue().getPlateID());
            deployment.setLocationID(locationComboBox.getValue().getLocationID());

            LocalDate startDate = startDatePicker.getValue();
            deployment.setStartDate(Date.valueOf(startDate));
            LocalDate endDate = endDatePicker.getValue();

            if (endDate != null) {
                deployment.setEndDate(Date.valueOf(endDate));
                deployment.setStatus("Completed");
            } else {
                deployment.setEndDate(null);
                deployment.setStatus("Active");
            }

            // 2. Execute DAO operation
            boolean isSuccessful = isUpdatingRecord
                    ? deploymentDAO.updateDeployment(deployment)
                    : deploymentDAO.insertDeployment(deployment);

            // 3. Handle result
            if (isSuccessful) {
                if (isUpdatingRecord){
                    showAlert(AlertType.INFORMATION, "Deployment Saved",
                            "Deployment record saved successfully. ID: " + deployment.getDeploymentID());
                } else {
                    String title = "New Deployment Added!";
                    String content = "A new vehicle deployment has been recorded.\n\n" +
                            "Deployment ID:\n" + deployment.getDeploymentID() + "\n\n" +
                            "Plate ID:\n" + deployment.getPlateID() + "\n\n" +
                            "Location ID:\n" + deployment.getLocationID() + "\n\n" +
                            "Start Date:\n" + deployment.getStartDate().toString();
                    showConfirmationDialog(title, content);
                }
                mainController.loadPage("Admin-deploymentRecords.fxml");
            } else {
                showAlert(Alert.AlertType.ERROR, "Error", "Failed to save deployment record. Check database for details.");
            }
        } catch (NullPointerException e) {
            showAlert(AlertType.ERROR, "Selection Error", "Please ensure both Vehicle and Location are selected from the dropdowns.");
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Unexpected Error", "An unexpected error occurred during save.");
            e.printStackTrace();
        }
    }

    @FXML
    private void handleCancel(){
        System.out.println("Cancel button clicked. Returning to deployment list.");
        mainController.loadPage("Admin-deploymentRecords.fxml");
    }

    private boolean validateFields(){
        LocalDate startDate = startDatePicker.getValue();
        LocalDate endDate = endDatePicker.getValue();
        String deploymentID = deploymentIDField.getText();

        if (deploymentID.isEmpty() || plateComboBox.getValue() == null || locationComboBox.getValue() == null || startDate == null){
            showAlert(Alert.AlertType.WARNING, "Validation Error", "Please ensure Deployment ID, Vehicle Plate, Location, and Start Date are filled.");
            return false;
        }

        if (!isUpdatingRecord) {
            if (deploymentDAO.getDeploymentById(deploymentID) != null) {
                showAlert(Alert.AlertType.ERROR, "Validation Error", "The Deployment ID '" + deploymentID + "' already exists. Please enter a unique ID.");
                return false;
            }
        }

        if (endDate != null && endDate.isBefore(startDate)) {
            showAlert(Alert.AlertType.WARNING, "Validation Error", "End Date cannot be before the Start Date.");
            return false;
        }

        return true;
    }

    private void loadComboBoxes() {

        try {
            List<Vehicle> vehicles = vehicleDAO.getAvailableVehicles();
            ObservableList<Vehicle> vehicleList = FXCollections.observableArrayList(vehicles);
            plateComboBox.setItems(vehicleList);

            plateComboBox.setConverter(new StringConverter<Vehicle>() {
                @Override
                public String toString(Vehicle vehicle) {
                    if (vehicle == null) return null;
                    return String.format("%s (%s)", vehicle.getPlateID(), vehicle.getVehicleType());
                }
                @Override public Vehicle fromString(String string) { return null; }
            });
        } catch (Exception e) {
            showAlert(AlertType.ERROR, "Load Error", "Failed to load vehicle list.");
            e.printStackTrace();
        }

        // Load Locations
        try {
            List<Location> locations = locationDAO.getAllLocations();
            ObservableList<Location> locationList = FXCollections.observableArrayList(locations);
            locationComboBox.setItems(locationList);

            locationComboBox.setConverter(new StringConverter<Location>() {
                @Override
                public String toString(Location location) {
                    if (location == null) return null;
                    return String.format("%s (%s)", location.getLocationID(), location.getName());
                }
                @Override public Location fromString(String string) { return null; }
            });
        } catch (Exception e) {
            showAlert(AlertType.ERROR, "Load Error", "Failed to load location list.");
            e.printStackTrace();
        }
    }

    private Vehicle findVehicleInList(String plateID) {
        if (plateID == null) return null;
        if (plateComboBox.getItems() == null) return vehicleDAO.getVehicleById(plateID);
        for (Vehicle v : plateComboBox.getItems()) {
            if (v.getPlateID().equals(plateID)) {
                return v;
            }
        }
        return vehicleDAO.getVehicleById(plateID);
    }

    private Location findLocationInList(String locationID) {
        if (locationID == null) return null;
        if (locationComboBox.getItems() == null) return locationDAO.getLocationById(locationID);
        for (Location l : locationComboBox.getItems()) {
            if (l.getLocationID().equals(locationID)) {
                return l;
            }
        }
        return locationDAO.getLocationById(locationID);
    }

    // ==================== ALERT/CONFIRMATION ====================

    private void showAlert(Alert.AlertType type, String title, String content){
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }

    private void showConfirmationDialog(String title, String content) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("Admin-addRecordConfirmation.fxml"));
            AnchorPane page = loader.load();
            Stage dialogStage = new Stage();
            dialogStage.setTitle("Confirmation");
            dialogStage.initModality(Modality.WINDOW_MODAL);
            if (mainController != null) {
                dialogStage.initOwner(mainController.getPrimaryStage());
            }
            Scene scene = new Scene(page);
            dialogStage.setScene(scene);
            Admin_addRecordConfirmationController controller = loader.getController();
            controller.setDialogStage(dialogStage);
            controller.setData(title, content);
            dialogStage.showAndWait();
        } catch (IOException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Error", "Could not load confirmation dialog.");
        }
    }
}