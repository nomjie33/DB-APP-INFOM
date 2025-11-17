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

import dao.LocationDAO;
import model.Location;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

public class Admin_locationFormController implements Initializable{

    @FXML private Label formHeaderLabel;
    @FXML private TextField idField;
    @FXML private TextField nameField;

    @FXML private Label statusLabel;
    @FXML private ComboBox<String> statusComboBox;

    private Admin_dashboardController mainController;
    private LocationDAO locationDAO = new LocationDAO();
    private boolean isEditMode = false;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        statusComboBox.setItems(FXCollections.observableArrayList("Active", "Inactive"));
    }

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

            statusLabel.setVisible(true);
            statusComboBox.setVisible(true);
            statusComboBox.setValue(loc.getStatus());
        }
    }

    @FXML private void handleSave(){

        if (idField.getText().isEmpty() || nameField.getText().isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Validation Error!!!", "Please fill in all required Fields.");
            return;
        }
        if (isEditMode && statusComboBox.getValue() == null) {
            showAlert(Alert.AlertType.WARNING, "Validation Error!!!", "Please select a status.");
            return;
        }

        Location loc = new Location();
        loc.setLocationID(idField.getText());
        loc.setName(nameField.getText());

        boolean success;
        if (isEditMode){
            Location oldLoc = locationDAO.getLocationById(loc.getLocationID());
            boolean nameChanged = !oldLoc.getName().equals(loc.getName());
            boolean statusChanged = !oldLoc.getStatus().equals(statusComboBox.getValue());

            if (!nameChanged && !statusChanged) {
                showAlert(Alert.AlertType.INFORMATION, "No Changes", "No changes were detected.");
                return;
            }

            loc.setStatus(statusComboBox.getValue());
            success = locationDAO.updateLocation(loc);
        } else {
            loc.setStatus("Active");
            success = locationDAO.insertLocation(loc);
        }

        if (success){
            if (isEditMode) {
                showAlert(Alert.AlertType.INFORMATION, "Success", "Location record saved.");
            } else {
                // Show custom dialog for new records
                String title = "New Location Added!";
                String content = "A new location has been successfully added.\n\n" +
                        "Location ID:\n" + loc.getLocationID() + "\n\n" +
                        "Location Name:\n" + loc.getName() + "\n\n" +
                        "Status:\nActive";

                showConfirmationDialog(title, content);
            }
            mainController.loadPage("Admin-locationRecords.fxml");
        } else {
            showAlert(Alert.AlertType.ERROR, "Error", "Failed to save location. The ID might already exist.");
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