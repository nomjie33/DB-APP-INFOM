package main.gui;

import dao.MaintenanceDAO;
import dao.TechnicianDAO;
import dao.VehicleDAO;
import javafx.fxml.Initializable;
import model.MaintenanceTransaction;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.scene.control.*;
import javafx.scene.control.Alert.AlertType;
import model.Technician;
import model.Vehicle;

import javafx.util.StringConverter;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.io.IOException;
import java.net.URL;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ResourceBundle;

public class Admin_maintenanceFormController implements Initializable {

    @FXML private Label formHeaderLabel;
    @FXML private TextField maintenanceIDField;
    @FXML private ComboBox<Vehicle> plateComboBox;
    @FXML private ComboBox<Technician> technicianComboBox;
    @FXML private DatePicker startDatePicker;
    @FXML private TextField startTimeField;
    @FXML private DatePicker endDatePicker;
    @FXML private TextField endTimeField;
    @FXML private TextArea notesArea;

    private Admin_dashboardController mainController;
    private MaintenanceDAO maintenanceDAO = new MaintenanceDAO();
    private VehicleDAO vehicleDAO = new VehicleDAO();      // <-- NEW
    private TechnicianDAO technicianDAO = new TechnicianDAO();

    private boolean isUpdatingRecord = false;
    private MaintenanceTransaction currentMaintenance;

    private final DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm:ss");

    public void setMainController(Admin_dashboardController mainController) {
        this.mainController = mainController;
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        loadComboBoxes();
    }

    public void setMaintenanceData(MaintenanceTransaction maintenance) {
        if (maintenance != null) {
            isUpdatingRecord = true;
            currentMaintenance = maintenance;

            formHeaderLabel.setText("Update Maintenance");

            maintenanceIDField.setText(maintenance.getMaintenanceID());

            plateComboBox.setValue(findVehicleInList(maintenance.getPlateID()));
            technicianComboBox.setValue(findTechnicianInList(maintenance.getTechnicianID()));

            if (maintenance.getStartDateTime() != null) {
                startDatePicker.setValue(maintenance.getStartDateTime().toLocalDateTime().toLocalDate());
                startTimeField.setText(maintenance.getStartDateTime().toLocalDateTime().toLocalTime().format(timeFormatter));
            }

            if (maintenance.getEndDateTime() != null) {
                endDatePicker.setValue(maintenance.getEndDateTime().toLocalDateTime().toLocalDate());
                endTimeField.setText(maintenance.getEndDateTime().toLocalDateTime().toLocalTime().format(timeFormatter));
            }

            notesArea.setText(maintenance.getNotes());

            maintenanceIDField.setDisable(true);
        }
    }

    @FXML
    private void handleSave() {
        if (!validateFields()) return;

        try {
            // ... (Data extraction logic remains the same)
            String maintenanceID = maintenanceIDField.getText().trim();
            String plateID = plateComboBox.getValue().getPlateID();
            String technicianID = technicianComboBox.getValue().getTechnicianId();
            String notes = notesArea.getText().trim();

            // ... (DateTime parsing logic remains the same)
            LocalDateTime startDateTime = LocalDateTime.of(startDatePicker.getValue(),
                    LocalTime.parse(startTimeField.getText().trim(), timeFormatter));
            LocalDateTime endDateTime = null;
            if (endDatePicker.getValue() != null && !endTimeField.getText().trim().isEmpty()) {
                endDateTime = LocalDateTime.of(endDatePicker.getValue(),
                        LocalTime.parse(endTimeField.getText().trim(), timeFormatter));
            }

            // ... (Maintenance object creation and DAO call remains the same)
            MaintenanceTransaction maintenance = isUpdatingRecord ? currentMaintenance : new MaintenanceTransaction();
            maintenance.setMaintenanceID(maintenanceID);
            maintenance.setPlateID(plateID);
            maintenance.setTechnicianID(technicianID);
            maintenance.setStartDateTime(java.sql.Timestamp.valueOf(startDateTime));
            maintenance.setEndDateTime(endDateTime != null ? java.sql.Timestamp.valueOf(endDateTime) : null);
            maintenance.setNotes(notes);

            boolean isSuccessful = isUpdatingRecord
                    ? maintenanceDAO.updateMaintenance(maintenance)
                    : maintenanceDAO.insertMaintenance(maintenance);

            if (isSuccessful) {
                if (isUpdatingRecord) {
                    showAlert(AlertType.INFORMATION, "Maintenance Saved", "Maintenance record saved successfully. ID: " + maintenance.getMaintenanceID());
                } else {
                    // FIX: Show confirmation dialog for NEW record
                    String title = "New Maintenance Added!";
                    String content = "A new vehicle maintenance record has been saved.\n\n" +
                            "Maintenance ID:\n" + maintenance.getMaintenanceID() + "\n\n" +
                            "Vehicle Plate:\n" + maintenance.getPlateID() + "\n\n" +
                            "Technician ID:\n" + maintenance.getTechnicianID() + "\n\n" +
                            "Start Time:\n" + maintenance.getStartDateTime().toLocalDateTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
                    showConfirmationDialog(title, content);
                }
                mainController.loadPage("Admin-maintenanceRecords.fxml"); // go back to records table
            } else {
                showAlert(AlertType.ERROR, "Error", "Failed to save maintenance record.");
            }

        } catch (NullPointerException e) {
            showAlert(AlertType.ERROR, "Selection Error", "Please ensure Vehicle and Technician are selected.");
        } catch (Exception e) {
            showAlert(AlertType.ERROR, "Error", "An unexpected error occurred during save. Check console for stack trace.");
            e.printStackTrace();
        }
    }

    @FXML
    private void handleCancel() {
        mainController.loadPage("Admin-maintenanceRecords.fxml");
    }

    private boolean validateFields() {
        LocalDate startDate = startDatePicker.getValue();
        LocalTime startTime = null;
        LocalDate endDate = endDatePicker.getValue();
        LocalTime endTime = null;

        if (maintenanceIDField.getText().trim().isEmpty() ||
                plateComboBox.getValue() == null ||
                technicianComboBox.getValue() == null ||
                startDatePicker.getValue() == null ||
                startTimeField.getText().trim().isEmpty()) {
            showAlert(AlertType.WARNING, "Validation Error", "Please fill in all required fields.");
            return false;
        }

        if (!isUpdatingRecord) {
            if (maintenanceDAO.getMaintenanceById(maintenanceIDField.getText().trim()) != null) {
                showAlert(AlertType.ERROR, "Validation Error", "The Maintenance ID '" + maintenanceIDField.getText().trim() + "' already exists. Please enter a unique ID.");
                return false;
            }
        }

        try {
            startTime = LocalTime.parse(startTimeField.getText().trim(), timeFormatter);

            boolean hasEndDate = endDate != null;
            boolean hasEndTime = !endTimeField.getText().trim().isEmpty();

            if (hasEndDate && !hasEndTime) {
                showAlert(AlertType.ERROR, "Invalid Input", "End Date is set, but End Time is missing.");
                return false;
            }
            if (!hasEndDate && hasEndTime) {
                showAlert(AlertType.ERROR, "Invalid Input", "End Time is set, but End Date is missing.");
                return false;
            }
            if (hasEndDate && hasEndTime) {
                endTime = LocalTime.parse(endTimeField.getText().trim(), timeFormatter);
            }
        } catch (Exception e) {
            showAlert(AlertType.ERROR, "Invalid Time Format", "Time must be in HH:MM:SS format (e.g., 10:30:00).");
            return false;
        }
        if (endDate != null && endTime != null) {
            LocalDateTime startDateTime = LocalDateTime.of(startDate, startTime);
            LocalDateTime endDateTime = LocalDateTime.of(endDate, endTime);

            if (endDateTime.isBefore(startDateTime)) {
                showAlert(AlertType.WARNING, "Date/Time Error", "End Date/Time cannot be before the Start Date/Time.");
                return false;
            }
        }

        return true;
    }

    private void showAlert(AlertType type, String title, String content) {
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

    private void loadComboBoxes() {
        // Load Vehicles
        try {
            ObservableList<Vehicle> vehicleList = FXCollections.observableArrayList(vehicleDAO.getAllVehicles()); // Assuming getAllVehicles() exists
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

        // Load Technicians
        try {
            ObservableList<Technician> technicianList = FXCollections.observableArrayList(technicianDAO.getAllTechnicians()); // Assuming getAllTechnicians() exists
            technicianComboBox.setItems(technicianList);

            technicianComboBox.setConverter(new StringConverter<Technician>() {
                @Override
                public String toString(Technician tech) {
                    if (tech == null) return null;
                    return String.format("%s (%s)", tech.getTechnicianId(), tech.getFullName());
                }
                @Override public Technician fromString(String string) { return null; }
            });
        } catch (Exception e) {
            showAlert(AlertType.ERROR, "Load Error", "Failed to load technician list.");
            e.printStackTrace();
        }
    }

    private Vehicle findVehicleInList(String plateID) {
        if (plateID == null || plateComboBox.getItems() == null) return null;
        for (Vehicle v : plateComboBox.getItems()) {
            if (v.getPlateID().equals(plateID)) {
                return v;
            }
        }
        return vehicleDAO.getVehicleById(plateID); // Fallback lookup if not in the initial list
    }

    private Technician findTechnicianInList(String technicianID) {
        if (technicianID == null || technicianComboBox.getItems() == null) return null;
        for (Technician t : technicianComboBox.getItems()) {
            if (t.getTechnicianId().equals(technicianID)) {
                return t;
            }
        }
        return technicianDAO.getTechnicianById(technicianID); // Fallback lookup
    }

}