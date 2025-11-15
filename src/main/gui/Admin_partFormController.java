package main.gui;

import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import java.io.IOException;

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
    private Part currentPart;

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
                Part oldPart = dao.getPartById(part.getPartId());
                if (oldPart != null){
                    part.setStatus(oldPart.getStatus());
                } else {
                    part.setStatus(part.getQuantity() > 0 ? "Active" : "Out of Stock");
                }
                success = dao.updatePart(part);
            } else {
                part.setStatus(part.getQuantity() > 0 ? "Active" : "Out of Stock");
                success = dao.insertPart(part);
            }

            if (success) {
                if (isEditMode){
                    showAlert(Alert.AlertType.INFORMATION, "Success", "Part record saved.");
                } else {
                    String title = "New Part Added!";
                    String content = "A new part has been successfully added.\n\n" +
                            "Part ID:\n" + part.getPartId() + "\n\n" +
                            "Part Name:\n" + part.getPartName() + "\n\n" +
                            "Stock Quantity:\n" + part.getQuantity() + "\n\n" +
                            "Price:\n₱" + String.format("%.2f", part.getPrice());

                    showConfirmationDialog(title, content);
                }
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