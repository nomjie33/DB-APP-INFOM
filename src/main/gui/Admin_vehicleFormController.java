package main.gui;

import dao.VehicleDAO;
import model.Vehicle;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

public class Admin_vehicleFormController {

    @FXML private Label formHeaderLabel;
    @FXML private TextField plateField;
    @FXML private TextField typeField;
    @FXML private TextField priceField;

    private Admin_dashboardController mainController;
    private VehicleDAO vehicleDAO = new VehicleDAO();
    private boolean isEditMode = false;

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
        }
    }

    @FXML private void handleSave(){
        if(!validateFields()) return;

        try {
            Vehicle v = new Vehicle();
            v.setPlateID(plateField.getText());
            //v.setVehicleModel(modelField.getText());
            v.setVehicleType(typeField.getText());
            v.setRentalPrice(Double.parseDouble(priceField.getText()));

            boolean success;
            if (isEditMode){

                Vehicle target = vehicleDAO.getVehicleById(v.getPlateID());
                if(target != null) v.setStatus(target.getStatus());

                success = vehicleDAO.updateVehicle(v);

            } else {
                success = vehicleDAO.insertVehicle(v);
            }

            if (success){
                showAlert(Alert.AlertType.INFORMATION, "Success", "Vehicle record saved.");
                mainController.loadPage("Admin-vehicleRecords.fxml");
            } else {
                showAlert(Alert.AlertType.ERROR, "Error", "Failed to save vehicle.");
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
        return true;
    }

    private void showAlert(Alert.AlertType type, String title, String content){
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}
