package main.gui;

import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import java.io.IOException;

import model.Part;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import java.math.BigDecimal;
import java.util.Objects;

public class Admin_partFormController {

    @FXML private Label formHeaderLabel;
    @FXML private TextField idField;
    @FXML private TextField nameField;
    @FXML private TextField stockField;
    @FXML private TextField priceField;

    private Admin_dashboardController mainController;
    private service.PartService partService = new service.PartService();
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
        System.out.println("DEBUG: handleSave called");
        if (!validateFields()) return;

        try {

            String newName = nameField.getText();
            int newQuantity = Integer.parseInt(stockField.getText());
            BigDecimal newPrice = new BigDecimal(priceField.getText());

            if (isEditMode) {

                boolean nameChanged = !Objects.equals(currentPart.getPartName(), newName);
                boolean qtyChanged = currentPart.getQuantity() != newQuantity;
                boolean priceChanged = currentPart.getPrice().compareTo(newPrice) != 0;

                if (!nameChanged && !qtyChanged && !priceChanged) {
                    showAlert(Alert.AlertType.INFORMATION, "No Changes", "No changes were detected.");
                    return;
                }
            }

            Part part = new Part();
            part.setPartId(idField.getText());
            part.setPartName(newName);
            part.setQuantity(newQuantity);
            part.setPrice(newPrice);

            System.out.println("DEBUG: Part object created, ID: " + part.getPartId());
            System.out.println("DEBUG: isEditMode: " + isEditMode);

            boolean success;
            if (isEditMode) {
                part.setStatus(currentPart.getStatus());

                // Preserve existing status if present
                model.Part oldPart = new dao.PartDAO().getPartByIdIncludingInactive(part.getPartId());
                if (oldPart != null) {
                    part.setStatus(oldPart.getStatus());
                } else {
                    part.setStatus("Active");
                }

                success = partService.updatePart(part);
            } else {
                part.setStatus("Active");
                success = partService.addPart(part);
            }
            
            System.out.println("DEBUG: Service call completed, success: " + success);

            if (success) {
                if (isEditMode) {
                    showAlert(Alert.AlertType.INFORMATION, "Success", "Part record saved.");
                    mainController.loadPage("Admin-partRecords.fxml");
                } else {
                    // For new parts, show confirmation dialog THEN navigate
                    String title = "New Part Added!";
                    String content = "A new part has been successfully added.\n\n" +
                            "Part ID:\n" + part.getPartId() + "\n\n" +
                            "Part Name:\n" + part.getPartName() + "\n\n" +
                            "Stock Quantity:\n" + part.getQuantity() + "\n\n" +
                            "Price:\n₱" + String.format("%.2f", part.getPrice());

                    // Show dialog and wait for user to close it
                    showConfirmationDialog(title, content);
                    
                    // Only navigate after dialog is closed
                    mainController.loadPage("Admin-partRecords.fxml");
                }
            } else {
                showAlert(Alert.AlertType.ERROR, "Error", "Failed to save part. The ID might already exist.");
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
        if (stockField.getText().isEmpty() || priceField.getText().isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Validation Error", "Stock and Price are required.");
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
            System.out.println("DEBUG: Attempting to load confirmation dialog...");
            FXMLLoader loader = new FXMLLoader(getClass().getResource("Admin-addRecordConfirmation.fxml"));
            AnchorPane page = loader.load();
            System.out.println("DEBUG: FXML loaded successfully");

            Stage dialogStage = new Stage();
            dialogStage.setTitle("Confirmation");
            dialogStage.initModality(Modality.APPLICATION_MODAL);

            if (mainController != null){
                dialogStage.initOwner(mainController.getPrimaryStage());
                System.out.println("DEBUG: Dialog owner set");
            }

            Scene scene = new Scene(page);
            dialogStage.setScene(scene);

            Admin_addRecordConfirmationController controller = loader.getController();
            controller.setDialogStage(dialogStage);
            controller.setData(title, content);
            System.out.println("DEBUG: Dialog data set, about to show");

            dialogStage.showAndWait();
            System.out.println("DEBUG: Dialog closed");

        } catch (IOException e) {
            System.err.println("ERROR: Failed to load confirmation dialog FXML");
            e.printStackTrace();
            // Fallback: if FXML dialog fails to load, show a simple Alert so the user still receives feedback
            showAlert(Alert.AlertType.INFORMATION, title, content);
        } catch (Exception e) {
            System.err.println("ERROR: Unexpected error showing confirmation dialog");
            e.printStackTrace();
            showAlert(Alert.AlertType.INFORMATION, title, content);
        }
    }

}