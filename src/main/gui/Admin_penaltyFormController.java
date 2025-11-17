package main.gui;

// --- 1. ADD THESE IMPORTS ---
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import java.io.IOException;
// --- END OF IMPORTS ---

import dao.PenaltyDAO;
import dao.RentalDAO;
import dao.MaintenanceDAO;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.Alert.AlertType;
import javafx.util.StringConverter;
import model.PenaltyTransaction;
import model.RentalTransaction;
import model.MaintenanceTransaction;
import java.math.BigDecimal;
import java.net.URL;
import java.time.LocalDate;
import java.util.ResourceBundle;

public class Admin_penaltyFormController implements Initializable{

    @FXML private Label formHeaderLabel;
    @FXML private TextField idField;

    @FXML private ComboBox<RentalTransaction> rentalComboBox;
    @FXML private ComboBox<MaintenanceTransaction> maintenanceComboBox;

    @FXML private TextField totalPenaltyField;
    @FXML private DatePicker dateIssuedPicker;

    private Admin_dashboardController mainController;
    private PenaltyDAO penaltyDAO = new PenaltyDAO();

    private boolean isUpdatingRecord = false;
    private PenaltyTransaction currentPenalty;
    private RentalDAO rentalDAO = new RentalDAO();
    private MaintenanceDAO maintenanceDAO = new MaintenanceDAO();

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        loadComboBoxes();
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
            totalPenaltyField.setText(penalty.getTotalPenalty().toPlainString());
            if (penalty.getDateIssued() != null) {
                dateIssuedPicker.setValue(penalty.getDateIssued().toLocalDate());
            }

            rentalComboBox.setValue(findRentalInList(penalty.getRentalID()));
            maintenanceComboBox.setValue(findMaintenanceInList(penalty.getMaintenanceID()));

            idField.setDisable(true);
            idField.getStyleClass().add("form-text-field-disabled");
        }
    }

    @FXML
    private void handleSave() {
        if (!validateFields()) return;

        try {
            String penaltyID = idField.getText().trim();
            String rentalID = rentalComboBox.getValue() != null ? rentalComboBox.getValue().getRentalID() : null;
            String maintenanceID = (maintenanceComboBox.getValue() != null && maintenanceComboBox.getValue().getMaintenanceID() != null)
                    ? maintenanceComboBox.getValue().getMaintenanceID()
                    : null; 

            if (rentalID == null) {
                showAlert(AlertType.WARNING, "Validation Error", "Rental ID is required.");
                return;
            }

            BigDecimal totalPenalty = new BigDecimal(totalPenaltyField.getText().trim());
            LocalDate dateIssued = dateIssuedPicker.getValue();

            if (!isUpdatingRecord && penaltyDAO.getPenaltyById(penaltyID) != null) {
                showAlert(AlertType.ERROR, "Duplicate ID", "Penalty ID already exists. Please choose a unique ID.");
                return;
            }

            PenaltyTransaction penalty = isUpdatingRecord ? currentPenalty : new PenaltyTransaction();
            penalty.setPenaltyID(penaltyID);
            penalty.setRentalID(rentalID);
            penalty.setMaintenanceID(maintenanceID);
            penalty.setTotalPenalty(totalPenalty);
            penalty.setDateIssued(dateIssued != null ? java.sql.Date.valueOf(dateIssued) : null);

            if (!isUpdatingRecord) {
                penalty.setPenaltyStatus("UNPAID"); // Default for new penalties
                penalty.setStatus("Active");
            }

            boolean success = isUpdatingRecord
                    ? penaltyDAO.updatePenalty(penalty)
                    : penaltyDAO.insertPenalty(penalty);

            if (success) {
                String title;
                String content;
                if (isUpdatingRecord) {
                    title = "Penalty Updated!";
                    content = "The penalty record has been successfully updated.\n\n" +
                            "Penalty ID:\n" + penalty.getPenaltyID() + "\n\n" +
                            "Rental ID:\n" + penalty.getRentalID() + "\n\n" +
                            "Amount:\n₱" + String.format("%.2f", penalty.getTotalPenalty()) + "\n\n" +
                            "Status:\n" + penalty.getPenaltyStatus();
                } else {
                    title = "New Penalty Added!";
                    content = "A new penalty has been recorded.\n\n" +
                            "Penalty ID:\n" + penalty.getPenaltyID() + "\n\n" +
                            "Rental ID:\n" + penalty.getRentalID() + "\n\n" +
                            "Amount:\n₱" + String.format("%.2f", penalty.getTotalPenalty()) + "\n\n" +
                            "Status:\n" + penalty.getPenaltyStatus();
                }

                showConfirmationDialog(title, content); // Call the FXML dialog
                mainController.loadPage("Admin-penaltyRecords.fxml"); // Go back to list

            } else {
                showAlert(AlertType.ERROR, "Error", "Failed to save penalty record.");
            }

        } catch (NumberFormatException e) {
            showAlert(AlertType.ERROR, "Invalid Amount", "Total penalty must be a valid number (e.g., 500.00).");
        } catch (Exception e) {
            showAlert(AlertType.ERROR, "Error", "An unexpected error occurred while saving the record.");
            e.printStackTrace();
        }
    }

    @FXML
    private void handleCancel() {
        mainController.loadPage("Admin-penaltyRecords.fxml");
    }

    private boolean validateFields() {
        if (idField.getText().trim().isEmpty() ||
                rentalComboBox.getValue() == null||
                totalPenaltyField.getText().trim().isEmpty() ||
                dateIssuedPicker.getValue() == null) {
            showAlert(AlertType.WARNING, "Validation Error", "Please fill in all required fields.");
            return false;
        }

        try {
            BigDecimal penaltyAmount = new BigDecimal(totalPenaltyField.getText().trim());
            if (penaltyAmount.scale() > 2){
                showAlert(AlertType.ERROR, "Invalid Amount", "Total penalty must have at most two decimal places.");
                return false;
            }
            if (penaltyAmount.compareTo(BigDecimal.ZERO) <= 0) {
                showAlert(AlertType.ERROR, "Invalid Amount", "Total penalty must be greater than zero.");
                return false;
            }
        } catch (NumberFormatException e) {
            showAlert(AlertType.ERROR, "Invalid Amount", "Total penalty must be a valid number (e.g., 500.00).");
            return false;
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

    private RentalTransaction findRentalInList(String id) {
        if (id == null) return null;
        return rentalComboBox.getItems().stream()
                .filter(r -> r != null && id.equals(r.getRentalID()))
                .findFirst().orElse(rentalDAO.getRentalById(id));
    }

    private MaintenanceTransaction findMaintenanceInList(String id) {
        if (id == null) return null;
        return maintenanceComboBox.getItems().stream()
                .filter(m -> m != null && id.equals(m.getMaintenanceID()))
                .findFirst().orElse(maintenanceDAO.getMaintenanceById(id));
    }

    private void loadComboBoxes() {
        try {
            ObservableList<RentalTransaction> rentalList = FXCollections.observableArrayList(rentalDAO.getAllRentals());
            rentalComboBox.setItems(rentalList);

            rentalComboBox.setConverter(new StringConverter<RentalTransaction>() {
                @Override public String toString(RentalTransaction r) {
                    if (r == null) return null;
                    return String.format("%s (Vehicle: %s)", r.getRentalID(), r.getPlateID());
                }
                @Override public RentalTransaction fromString(String string) { return null; }
            });
        } catch (Exception e) {
            System.err.println("Failed to load rental records: " + e.getMessage());
        }

        try {
            ObservableList<MaintenanceTransaction> maintenanceList = FXCollections.observableArrayList(maintenanceDAO.getAllMaintenance());

            MaintenanceTransaction optionalNone = new MaintenanceTransaction();
            maintenanceList.add(0, optionalNone);
            maintenanceComboBox.setItems(maintenanceList);

            maintenanceComboBox.setConverter(new StringConverter<MaintenanceTransaction>() {
                @Override public String toString(MaintenanceTransaction m) {
                    if (m == null || m.getMaintenanceID() == null) return "None (Not Linked to Maintenance)";
                    return String.format("%s (Vehicle: %s)", m.getMaintenanceID(), m.getPlateID());
                }
                @Override public MaintenanceTransaction fromString(String string) { return null; }
            });
        } catch (Exception e) {
            System.err.println("Failed to load maintenance records: " + e.getMessage());
        }
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