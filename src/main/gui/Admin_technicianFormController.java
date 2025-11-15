package main.gui;

import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import java.io.IOException;

import dao.TechnicianDAO;
import model.Technician;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

import java.math.BigDecimal;

public class Admin_technicianFormController {

    @FXML private Label formHeaderLabel;
    @FXML private TextField idField;
    @FXML private TextField lastNameField;
    @FXML private TextField firstNameField;
    @FXML private TextField specIdField;
    @FXML private TextField rateField;
    @FXML private TextField contactField;

    private Admin_dashboardController mainController;
    private TechnicianDAO technicianDAO = new TechnicianDAO();
    private boolean isEditMode = false;

    public void setMainController(Admin_dashboardController mainController) {
        this.mainController = mainController;
    }

    public void setTechnicianData(Technician technician){
        if (technician != null){
            isEditMode = true;
            formHeaderLabel.setText("Update Technician");

            idField.setText(technician.getTechnicianId());
            lastNameField.setText(technician.getLastName());
            firstNameField.setText(technician.getFirstName());
            specIdField.setText(technician.getSpecializationId());
            rateField.setText(technician.getRate().toString());
            contactField.setText(technician.getContactNumber());

            idField.setDisable(true);
            idField.getStyleClass().add("form-text-field-disabled");
        }
    }

    @FXML private void handleSave(){
        if(!validateFields()) return;

        try {
            Technician technician = new Technician();
            technician.setTechnicianId(idField.getText());
            technician.setLastName(lastNameField.getText());
            technician.setFirstName(firstNameField.getText());
            technician.setSpecializationId(specIdField.getText());
            technician.setRate(new BigDecimal(rateField.getText()));
            technician.setContactNumber(contactField.getText());

            boolean success;
            if (isEditMode){
                Technician oldTech = technicianDAO.getTechnicianById(technician.getTechnicianId());
                if (oldTech != null){
                    technician.setStatus(oldTech.getStatus());
                } else {
                    technician.setStatus("Active");
                }
                success = technicianDAO.updateTechnician(technician);
            } else {
                technician.setStatus("Active");
                success = technicianDAO.insertTechnician(technician);
            }

            if (success) {
                if (isEditMode){
                    showAlert(Alert.AlertType.INFORMATION, "Success", "Technician record saved.");
                } else {
                    String title = "New Technician Added!";
                    String content = "A new technician has been successfully added.\n\n" +
                            "Technician ID:\n" + technician.getTechnicianId() + "\n\n" +
                            "Name:\n" + technician.getFirstName() + " " + technician.getLastName() + "\n\n" +
                            "Specialization:\n" + technician.getSpecializationId() + "\n\n" +
                            "Rate:\n₱" + String.format("%.2f", technician.getRate());

                    showConfirmationDialog(title, content);
                }
                mainController.loadPage("Admin-technicianRecords.fxml");
            } else {
                showAlert(Alert.AlertType.ERROR, "Error", "Failed to save technician.");
            }
        } catch (NumberFormatException e){
            showAlert(Alert.AlertType.ERROR, "Invalid Input", "Rate must be a valid number (e.g., 1500.00).");
        }
    }

    @FXML private void handleCancel() {
        mainController.loadPage("Admin-technicianRecords.fxml");
    }

    private boolean validateFields() {
        if (idField.getText().isEmpty() || lastNameField.getText().isEmpty() || firstNameField.getText().isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Validation Error", "ID, Last Name, and First Name are required.");
            return false;
        }
        if (rateField.getText().isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Validation Error", "Rate is required.");
            return false;
        }
        return true;
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
