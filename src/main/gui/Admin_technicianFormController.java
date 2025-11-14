package main.gui;

import dao.TechnicianDAO;
import model.Technician;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

import javax.swing.text.html.HTMLDocument;
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
                success = technicianDAO.updateTechnician(technician);
            } else {
                technician.setStatus("Active");
                success = technicianDAO.insertTechnician(technician);
            }

            if (success) {
                showAlert(Alert.AlertType.INFORMATION, "Success", "Technician record saved.");
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
        return true;
    }

    private void showAlert(Alert.AlertType type, String title, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}
