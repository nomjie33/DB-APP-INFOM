package main.gui;

import javafx.collections.FXCollections;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.scene.control.DialogPane;
import dao.VehicleDAO;
import model.Vehicle;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

public class Admin_vehicleFormController implements Initializable{

    @FXML private Label formHeaderLabel;
    @FXML private TextField plateField;
    @FXML private TextField typeField;
    @FXML private TextField priceField;

    @FXML private Label statusLabel;
    @FXML private ComboBox<String> statusComboBox;

    private Admin_dashboardController mainController;
    private VehicleDAO vehicleDAO = new VehicleDAO();
    private boolean isEditMode = false;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
    }

    public void setMainController(Admin_dashboardController mainController){
        this.mainController = mainController;
    }

    public void setVehicleData(Vehicle vehicle){
        if (vehicle != null){
            isEditMode = true;
            formHeaderLabel.setText("Update Vehicle");

            plateField.setText(vehicle.getPlateID());
            typeField.setText(vehicle.getVehicleType());
            priceField.setText(String.valueOf(vehicle.getRentalPrice()));

            plateField.setDisable(true);
            plateField.getStyleClass().add("form-text-field-disabled");

            statusLabel.setVisible(true);
            statusComboBox.setVisible(true);

            String currentStatus = vehicle.getStatus();
            /*
            if ("In Use".equals(currentStatus)) {

                statusComboBox.setItems(FXCollections.observableArrayList("In Use"));
                statusComboBox.setValue("In Use");
                statusComboBox.setDisable(true);
            } else {

                statusComboBox.setItems(FXCollections.observableArrayList(
                        "Available", "Maintenance", "Inactive"
                ));
                statusComboBox.setValue(currentStatus);
                statusComboBox.setDisable(false);
            } */

            statusComboBox.setItems(FXCollections.observableArrayList(
                    "Available", "In Use", "Maintenance", "Inactive"
            ));

            statusComboBox.setValue(currentStatus);
            statusComboBox.setDisable(false);
        }
    }

    @FXML private void handleSave(){
        if(!validateFields()) return;

        try {

            Vehicle v = new Vehicle();
            v.setPlateID(plateField.getText());
            v.setVehicleType(typeField.getText());
            v.setRentalPrice(Double.parseDouble(priceField.getText()));

            boolean success;
            if (isEditMode){
                Vehicle target = vehicleDAO.getVehicleById(v.getPlateID());

                boolean typeChanged = !target.getVehicleType().equals(v.getVehicleType());
                boolean priceChanged = (Double.compare(target.getRentalPrice(), v.getRentalPrice()) != 0);
                boolean statusChanged = !target.getStatus().equals(statusComboBox.getValue());

                if (!typeChanged && !priceChanged && !statusChanged) {
                    showAlert(Alert.AlertType.INFORMATION, "No Changes", "No changes were detected.");
                    return; // Stop without saving
                }

                v.setStatus(statusComboBox.getValue());
                success = vehicleDAO.updateVehicle(v);
            } else {
                v.setStatus("Available");
                success = vehicleDAO.insertVehicle(v);
            }

            if (success){
                if (isEditMode) {
                    showAlert(Alert.AlertType.INFORMATION, "Update Successful", "Vehicle record has been updated.");
                } else {

                    String title = "New Vehicle Added!";
                    String content = "A new vehicle has been successfully added.\n\n" +
                            "Plate ID:\n" + v.getPlateID() + "\n\n" +
                            "Vehicle Type:\n" + v.getVehicleType() + "\n\n" +
                            "Price per Day:\n₱" + String.format("%.2f", v.getRentalPrice()) + "\n\n" +
                            "Status:\nAvailable";

                    showConfirmationDialog(title, content);
                }

                mainController.loadPage("Admin-vehicleRecords.fxml");

            } else {
                showAlert(Alert.AlertType.ERROR, "Error", "Failed to save vehicle. The Plate ID might already exist.");
            }

        } catch (NumberFormatException e){
            showAlert(Alert.AlertType.ERROR, "Invalid Input", "Price must be a valid number.");
        }
    }

    @FXML private void handleCancel(){
        mainController.loadPage("Admin-vehicleRecords.fxml");
    }

    private boolean validateFields(){
        if (plateField.getText().isEmpty() ||
                typeField.getText().isEmpty() || priceField.getText().isEmpty()){
            showAlert(Alert.AlertType.WARNING, "Validation Error!!!", "Please fill in all required Fields.");
            return false;
        }

        if (statusComboBox.isVisible() && statusComboBox.getValue() == null) {
            showAlert(Alert.AlertType.WARNING, "Validation Error!!!", "Please select a status.");
            return false;
        }

        return true;
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

    private void showAlert(Alert.AlertType type, String title, String content){
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}
