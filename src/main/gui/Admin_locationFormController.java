package main.gui;

import dao.LocationDAO;
import model.Location;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

class Admin_locationFormController {

    @FXML private Label formHeaderLabel;
    @FXML private TextField idField;
    @FXML private TextField nameField;

    private Admin_dashboardController mainController;
    private LocationDAO locationDAO = new LocationDAO();
    private boolean isEditMode = false;

    public void setMainController(Admin_dashboardController mainController) {
        this.mainController = mainController;
    }

    public void setLocationData(Location loc){
        if (loc != null){
            isEditMode = true;
            formHeaderLabel.setText("Update Location");

            idField.setText(loc.getLocationID());
            nameField.setText(loc.getName());

            idField.setDisable(true);
            idField.getStyleClass().add("form-text-field-disabled");
        }
    }

    @FXML private void handleSave(){
        if (idField.getText().isEmpty() || nameField.getText().isEmpty()){
            showAlert(Alert.AlertType.WARNING, "Validation Error!!!", "Please fill in all required Fields.");
            return;
        }

        Location loc = new Location();
        loc.setLocationID(idField.getText());
        loc.setName(nameField.getText());

        boolean success;
        if (isEditMode){
            Location oldLoc = locationDAO.getLocationById(loc.getLocationID());
            loc.setStatus(oldLoc.getStatus());
            success = locationDAO.updateLocation(loc);
        } else {
            loc.setStatus("Active");
            success = locationDAO.insertLocation(loc);
        }

        if (success){
            showAlert(Alert.AlertType.INFORMATION, "Success", "Location record saved.");
            mainController.loadPage("Admin-locationRecords.fxml");
        } else {
            showAlert(Alert.AlertType.ERROR, "Error", "Failed to save location.");
        }
    }

    @FXML private void handleCancel(){
        mainController.loadPage("Admin-locationRecords.fxml");
    }

    private void showAlert(Alert.AlertType type, String title, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }

}