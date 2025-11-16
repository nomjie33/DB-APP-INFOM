package main.gui;

import dao.MaintenanceChequeDAO;
import dao.MaintenanceDAO;
import dao.PartDAO;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import model.MaintenanceCheque;
import model.MaintenanceTransaction;
import model.Part;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.util.StringConverter;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.URL;
import java.util.ResourceBundle;

public class Admin_maintenanceChequeFormController implements Initializable {

    @FXML private Label formHeaderLabel;
    @FXML private ComboBox<MaintenanceTransaction> maintenanceComboBox;
    @FXML private ComboBox<Part> partComboBox;
    @FXML private TextField quantityField;

    private Admin_dashboardController mainController;
    private MaintenanceChequeDAO chequeDAO = new MaintenanceChequeDAO();
    private MaintenanceDAO maintenanceDAO = new MaintenanceDAO();
    private PartDAO partDAO = new PartDAO();
    private boolean isUpdatingRecord = false;
    private MaintenanceCheque currentCheque;

    @Override
    public void initialize(java.net.URL url, java.util.ResourceBundle rb) {
        loadComboBoxes();
    }

    public void setMainController(Admin_dashboardController mainController){
        this.mainController = mainController;
    }

    public void setMaintenanceChequeData(MaintenanceCheque cheque){
        if (cheque != null){
            isUpdatingRecord = true;
            currentCheque = cheque;
            formHeaderLabel.setText("Update Cheque");

            maintenanceComboBox.setValue(findMaintenanceInList(cheque.getMaintenanceID()));
            partComboBox.setValue(findPartInList(cheque.getPartID()));

            quantityField.setText(cheque.getQuantityUsed().setScale(2, RoundingMode.HALF_UP).toString());

            maintenanceComboBox.setDisable(true);
            partComboBox.setDisable(true);
        }
    }

    @FXML private void handleSave(){
        if (!validateFields()){
            return;
        }

        try {
            String maintenanceID = maintenanceComboBox.getValue().getMaintenanceID();
            String partID = partComboBox.getValue().getPartId();

            BigDecimal quantity = new BigDecimal(quantityField.getText().trim()).setScale(2, RoundingMode.HALF_UP);
            Part selectedPart = partComboBox.getValue();

            if (!isUpdatingRecord) {

                if (chequeDAO.getMaintenanceChequeById(maintenanceID, partID) != null) {
                    showAlert(Alert.AlertType.ERROR, "Duplicate Record", "This Part has already been added to this Maintenance record. Please edit the existing entry or enter a new Part ID.");
                    return;
                }

                if (selectedPart.getQuantity() < quantity.intValue()) {
                    showAlert(Alert.AlertType.ERROR, "Inventory Error",
                            String.format("Insufficient stock for Part ID %s. Available: %d, Requested: %.2f.",
                                    partID, selectedPart.getQuantity(), quantity));
                }

            }

            MaintenanceCheque cheque = isUpdatingRecord ? currentCheque : new MaintenanceCheque();
            cheque.setMaintenanceID(maintenanceID);
            cheque.setPartID(partID);
            cheque.setQuantityUsed(quantity);

            boolean isSuccessful;
            if (isUpdatingRecord){
                isSuccessful = chequeDAO.updateMaintenanceCheque(cheque);
            } else {
                cheque.setStatus("Active");
                isSuccessful = chequeDAO.insertMaintenanceCheque(cheque);
            }

            if (isSuccessful){
                if (isUpdatingRecord){
                    showAlert(Alert.AlertType.INFORMATION, "Save Successful", "Maintenance Cheque record saved successfully.");
                } else {
                    String title = "New Cheque Item Added!";
                    String content = "A new part has been successfully added to the maintenance record.\n\n" +
                            "Maintenance ID:\n" + cheque.getMaintenanceID() + "\n\n" +
                            "Part ID:\n" + cheque.getPartID() + "\n\n" +
                            "Quantity Used:\n" + String.format("%.2f", cheque.getQuantityUsed());

                    showConfirmationDialog(title, content);
                }
                mainController.loadPage("Admin-maintenanceChequeRecords.fxml");
            } else {
                showAlert(Alert.AlertType.ERROR, "Error", "Failed to save Maintenance Cheque record. Check logs for details.");
            }

        } catch (NumberFormatException e){
            showAlert(Alert.AlertType.ERROR, "Invalid Input", "Quantity must be a valid number (e.g., 1.00).");
        } catch (Exception e){
            showAlert(Alert.AlertType.ERROR, "Error", "An unexpected error occurred during save.");
            e.printStackTrace();
        }
    }

    @FXML private void handleCancel(){
        mainController.loadPage("Admin-maintenanceChequeRecords.fxml");
    }

    private boolean validateFields(){
        String quantityText = quantityField.getText().trim();
        if (maintenanceComboBox.getValue() == null || partComboBox.getValue() == null || quantityText.isEmpty()){
            showAlert(Alert.AlertType.WARNING, "Validation Error", "Please fill in all required fields.");
            return false;
        }

        try {
            new BigDecimal(quantityText);
        } catch (NumberFormatException e){
            showAlert(Alert.AlertType.ERROR, "Invalid Input", "Quantity Used must be a valid number.");
            return false;
        }

        if (new BigDecimal(quantityText).compareTo(BigDecimal.ZERO) <= 0) {
            showAlert(Alert.AlertType.ERROR, "Invalid Input", "Quantity Used must be greater than zero.");
            return false;
        }

        return true;
    }

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

    private MaintenanceTransaction findMaintenanceInList(String id) {
        if (id == null || maintenanceComboBox.getItems() == null) return null;
        return maintenanceComboBox.getItems().stream()
                .filter(m -> m.getMaintenanceID().equals(id))
                .findFirst().orElse(maintenanceDAO.getMaintenanceById(id));
    }

    private Part findPartInList(String id) {
        if (id == null || partComboBox.getItems() == null) return null;
        return partComboBox.getItems().stream()
                .filter(p -> p.getPartId().equals(id))
                .findFirst().orElse(partDAO.getPartById(id));
    }


    private void loadComboBoxes() {

        try {
            ObservableList<MaintenanceTransaction> maintenanceList = FXCollections.observableArrayList(maintenanceDAO.getAllMaintenance());
            maintenanceComboBox.setItems(maintenanceList);

            maintenanceComboBox.setConverter(new StringConverter<MaintenanceTransaction>() {
                @Override public String toString(MaintenanceTransaction m) {
                    if (m == null) return null;

                    return String.format("%s (Vehicle: %s)", m.getMaintenanceID(), m.getPlateID());
                }
                @Override public MaintenanceTransaction fromString(String string) { return null; }
            });
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Load Error", "Failed to load maintenance records.");
        }

        try {
            ObservableList<Part> partList = FXCollections.observableArrayList(partDAO.getAllParts());
            partComboBox.setItems(partList);

            partComboBox.setConverter(new StringConverter<Part>() {
                @Override public String toString(Part p) {
                    if (p == null) return null;

                    return String.format("%s (%s) - Stock: %d", p.getPartId(), p.getPartName(), p.getQuantity());
                }
                @Override public Part fromString(String string) { return null; }
            });
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Load Error", "Failed to load parts inventory.");
        }
    }
}