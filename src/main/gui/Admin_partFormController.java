package main.gui;

import dao.PartDAO;
import model.Part;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import java.math.BigDecimal;

public class Admin_partFormController {

    @FXML private Label formHeaderLabel;
    @FXML private TextField idField;
    @FXML private TextField nameField;
    @FXML private TextField stockField;
    @FXML private TextField priceField;

    private Admin_dashboardController mainController;
    private PartDAO dao = new PartDAO();
    private boolean isEditMode = false;
    private Part currentPart; // To hold the part being edited

    public void setMainController(Admin_dashboardController mainController) {
        this.mainController = mainController;
    }

    public void setPartData(Part part) {
        if (part != null) {
            isEditMode = true;
            currentPart = part;
            formHeaderLabel.setText("Update Part");

            idField.setText(part.getPartId());
            nameField.setText(part.getPartName());
            stockField.setText(String.valueOf(part.getQuantity()));
            priceField.setText(part.getPrice().toString());

            idField.setDisable(true);
            idField.getStyleClass().add("form-text-field-disabled");
        }
    }

    @FXML
    private void handleSave() {
        if (!validateFields()) return;

        try {
            Part part = new Part();
            part.setPartId(idField.getText());
            part.setPartName(nameField.getText());
            part.setQuantity(Integer.parseInt(stockField.getText()));
            part.setPrice(new BigDecimal(priceField.getText()));

            boolean success;
            if (isEditMode) {
                success = dao.updatePart(part);
            } else {

                part.setStatus(part.getQuantity() > 0 ? "Active" : "Out of Stock");
                success = dao.insertPart(part);
            }

            if (success) {
                showAlert(Alert.AlertType.INFORMATION, "Success", "Part record saved.");
                mainController.loadPage("Admin-partRecords.fxml");
            } else {
                showAlert(Alert.AlertType.ERROR, "Error", "Failed to save part.");
            }
        } catch (NumberFormatException e) {
            showAlert(Alert.AlertType.ERROR, "Invalid Input", "Stock (e.g., 10) and Price (e.g., 1500.00) must be valid numbers.");
        }
    }

    @FXML
    private void handleCancel() {
        mainController.loadPage("Admin-partRecords.fxml");
    }

    private boolean validateFields() {
        if (idField.getText().isEmpty() || nameField.getText().isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Validation Error", "ID and Name are required.");
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