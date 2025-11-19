package main.gui;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import model.PenaltyTransaction;
import model.RentalTransaction;
import model.MaintenanceTransaction;
import dao.PenaltyDAO;
import dao.RentalDAO;
import dao.MaintenanceDAO;
import service.MaintenanceService;

import java.io.IOException;
import java.math.BigDecimal;
import java.net.URL;
import java.time.LocalDate;
import java.util.Objects;
import java.util.ResourceBundle;
import java.math.RoundingMode;

public class Admin_penaltyFormController implements Initializable {

    @FXML private Label formHeaderLabel;
    @FXML private TextField idField;
    @FXML private ComboBox<RentalTransaction> rentalComboBox;
    @FXML private ComboBox<MaintenanceTransaction> maintenanceComboBox;
    @FXML private ComboBox<String> statusComboBox;
    @FXML private TextField totalPenaltyField;
    @FXML private DatePicker dateIssuedPicker;

    private Admin_dashboardController mainController;
    private final PenaltyDAO penaltyDAO = new PenaltyDAO();
    private final RentalDAO rentalDAO = new RentalDAO();
    private final MaintenanceDAO maintenanceDAO = new MaintenanceDAO();
    private final MaintenanceService maintenanceService = new MaintenanceService();

    private boolean isUpdatingRecord = false;
    private PenaltyTransaction currentPenalty;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        setupStatusComboBox();
        loadComboBoxes();
        totalPenaltyField.setDisable(true);
        setupMaintenanceListener();
    }

    public void setMainController(Admin_dashboardController mainController) {
        this.mainController = mainController;
    }

    public void setPenaltyData(PenaltyTransaction penalty) {
        if (penalty != null) {
            isUpdatingRecord = true;
            currentPenalty = penalty;

            formHeaderLabel.setText("Update Penalty");
            idField.setText(penalty.getPenaltyID());
            totalPenaltyField.setText(penalty.getTotalPenalty().setScale(2, RoundingMode.HALF_UP).toPlainString());
            dateIssuedPicker.setValue(penalty.getDateIssued() != null ? penalty.getDateIssued().toLocalDate() : null);

            rentalComboBox.setValue(findRental(penalty.getRentalID()));
            maintenanceComboBox.setValue(findMaintenance(penalty.getMaintenanceID()));
            statusComboBox.setValue(penalty.getPenaltyStatus());

            idField.setDisable(true);
            idField.getStyleClass().add("form-text-field-disabled");
        } else {
            isUpdatingRecord = false;
            formHeaderLabel.setText("New Penalty");
            statusComboBox.setValue("UNPAID");
            totalPenaltyField.setText("0.00");
        }
    }

    @FXML
    private void handleSave() {
        if (!validateFields()) return;

        try {
            String penaltyID = idField.getText().trim();
            RentalTransaction rental = rentalComboBox.getValue();
            MaintenanceTransaction maintenance = maintenanceComboBox.getValue();

            if (rental == null) {
                showAlert(Alert.AlertType.ERROR, "Missing Data", "Please select a Rental ID.");
                return;
            }

            if (maintenance == null || maintenance.getMaintenanceID() == null) {
                showAlert(Alert.AlertType.ERROR, "Missing Data", "Please select a Maintenance record.");
                return;
            }

            // Always calculate penalty using the service
            BigDecimal totalPenalty = maintenanceService.calculateMaintenanceCost(maintenance.getMaintenanceID())
                    .setScale(2, RoundingMode.HALF_UP);
            totalPenaltyField.setText(totalPenalty.toPlainString());

            LocalDate dateIssued = dateIssuedPicker.getValue();
            String penaltyStatus = statusComboBox.getValue();

            if (!isUpdatingRecord && penaltyDAO.getPenaltyById(penaltyID) != null) {
                showAlert(Alert.AlertType.ERROR, "Duplicate ID", "Penalty ID already exists.");
                return;
            }

            if (isUpdatingRecord && !hasChanges(rental, maintenance, totalPenalty, penaltyStatus, dateIssued)) {
                mainController.loadPage("Admin-penaltyRecords.fxml");
                return;
            }

            PenaltyTransaction penalty = isUpdatingRecord ? currentPenalty : new PenaltyTransaction();
            penalty.setPenaltyID(penaltyID);
            penalty.setRentalID(rental.getRentalID());
            penalty.setMaintenanceID(maintenance.getMaintenanceID());
            penalty.setTotalPenalty(totalPenalty);
            penalty.setPenaltyStatus(penaltyStatus);
            penalty.setDateIssued(dateIssued != null ? java.sql.Date.valueOf(dateIssued) : null);
            if (!isUpdatingRecord) penalty.setStatus("Active");

            boolean success = isUpdatingRecord
                    ? penaltyDAO.updatePenalty(penalty)
                    : penaltyDAO.insertPenalty(penalty);

            if (success) {
                showConfirmationDialog(
                        isUpdatingRecord ? "Penalty Updated!" : "New Penalty Added!",
                        "Penalty ID:\n" + penalty.getPenaltyID() +
                                "\n\nAmount:\n₱" + totalPenalty +
                                "\n\nStatus:\n" + penalty.getPenaltyStatus()
                );
                mainController.loadPage("Admin-penaltyRecords.fxml");
            } else {
                showAlert(Alert.AlertType.ERROR, "Error", "Failed to save penalty record.");
            }

        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Error", "An unexpected error occurred.");
            e.printStackTrace();
        }
    }

    private boolean hasChanges(RentalTransaction rental, MaintenanceTransaction maintenance,
                               BigDecimal total, String status, LocalDate dateIssued) {
        return !(Objects.equals(currentPenalty.getRentalID(), rental.getRentalID()) &&
                Objects.equals(currentPenalty.getMaintenanceID(), maintenance.getMaintenanceID()) &&
                currentPenalty.getTotalPenalty().compareTo(total) == 0 &&
                Objects.equals(currentPenalty.getPenaltyStatus(), status) &&
                ((currentPenalty.getDateIssued() == null && dateIssued == null) ||
                        (currentPenalty.getDateIssued() != null && currentPenalty.getDateIssued().toLocalDate().equals(dateIssued))));
    }

    private void setupStatusComboBox() {
        statusComboBox.setItems(FXCollections.observableArrayList("UNPAID", "PAID", "WAIVED"));
        statusComboBox.setValue("UNPAID");
    }

    private void setupMaintenanceListener() {
        maintenanceComboBox.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> calculateTotalPenalty());
    }

    private void calculateTotalPenalty() {
        MaintenanceTransaction selected = maintenanceComboBox.getValue();
        if (selected != null && selected.getMaintenanceID() != null) {
            BigDecimal total = maintenanceService.calculateMaintenanceCost(selected.getMaintenanceID())
                    .setScale(2, RoundingMode.HALF_UP);
            totalPenaltyField.setText(total.toPlainString());

            if (selected.getPlateID() != null) autoSelectRentalByVehicle(selected.getPlateID());
        } else {
            totalPenaltyField.setText("0.00");
        }
    }

    private void autoSelectRentalByVehicle(String plateID) {
        if (plateID == null) return;

        RentalTransaction rental = rentalComboBox.getItems().stream()
                .filter(r -> r != null && plateID.equals(r.getPlateID()))
                .findFirst()
                .orElseGet(() -> rentalDAO.getActiveRentalByVehicle(plateID));

        if (rental != null) rentalComboBox.setValue(rental);
    }

    @FXML private void handleCancel() {
        mainController.loadPage("Admin-penaltyRecords.fxml");
    }

    private boolean validateFields() {
        if (idField.getText().trim().isEmpty() ||
                rentalComboBox.getValue() == null ||
                totalPenaltyField.getText().trim().isEmpty() ||
                dateIssuedPicker.getValue() == null) {
            showAlert(Alert.AlertType.WARNING, "Validation Error", "Please fill in all required fields.");
            return false;
        }

        try { new BigDecimal(totalPenaltyField.getText().trim()); }
        catch (NumberFormatException e) {
            showAlert(Alert.AlertType.ERROR, "Invalid Amount", "Penalty amount must be a number.");
            return false;
        }

        return true;
    }

    private RentalTransaction findRental(String id) {
        if (id == null) return null;
        return rentalComboBox.getItems().stream()
                .filter(r -> r != null && id.equals(r.getRentalID()))
                .findFirst()
                .orElse(rentalDAO.getRentalById(id));
    }

    private MaintenanceTransaction findMaintenance(String id) {
        if (id == null) return null;
        return maintenanceComboBox.getItems().stream()
                .filter(m -> m != null && id.equals(m.getMaintenanceID()))
                .findFirst()
                .orElse(maintenanceDAO.getMaintenanceById(id));
    }

    private void loadComboBoxes() {
        ObservableList<RentalTransaction> rentals = FXCollections.observableArrayList(rentalDAO.getAllRentals());
        rentalComboBox.setItems(rentals);
        rentalComboBox.setConverter(new javafx.util.StringConverter<>() {
            @Override public String toString(RentalTransaction r) { return r == null ? "" : r.getRentalID() + " (" + r.getPlateID() + ")"; }
            @Override public RentalTransaction fromString(String string) { return null; }
        });

        ObservableList<MaintenanceTransaction> maints = FXCollections.observableArrayList(maintenanceDAO.getAllMaintenance());
        MaintenanceTransaction none = new MaintenanceTransaction(); // None placeholder
        maints.add(0, none);
        maintenanceComboBox.setItems(maints);
        maintenanceComboBox.setConverter(new javafx.util.StringConverter<>() {
            @Override public String toString(MaintenanceTransaction m) {
                return (m == null || m.getMaintenanceID() == null) ? "None (Not Linked)" : m.getMaintenanceID() + " (" + m.getPlateID() + ")";
            }
            @Override public MaintenanceTransaction fromString(String string) { return null; }
        });
    }

    private void showAlert(Alert.AlertType type, String title, String content) {
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
            if (mainController != null) dialogStage.initOwner(mainController.getPrimaryStage());
            dialogStage.setScene(new Scene(page));
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
