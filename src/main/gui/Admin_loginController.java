package main.gui;

import dao.StaffDAO;
import model.Staff;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.IOException;

public class Admin_loginController {

    private StaffDAO staffDAO = new StaffDAO();

    @FXML private VBox adminLoginRoot;
    @FXML private TextField usernameField;
    @FXML private PasswordField passwordField;
    @FXML private Button loginButton;
    @FXML private Button backButton;
    @FXML private Label errorLabel;

    @FXML void handleLoginButton(ActionEvent e){

        String username = usernameField.getText();
        String password = passwordField.getText();

        if (username.isEmpty() || password.isEmpty()){
            showError("Please enter both username and password.");
            return;
        }

        try {
            Staff staff = staffDAO.getStaffByUsername(username);

            if (staff != null && staff.getPassword().equals(password)){

                System.out.println("Admin login successful for: " + staff.getUsername());
                errorLabel.setVisible(false);

                loadScene("Admin-dashboard.fxml", "UVR!");
            } else {
                System.out.println("Admin login failed.");
                showError("Invalid username or password.");
            }

        } catch (Exception ex){
            ex.printStackTrace();
            showError("A database error occured. Please try again.");
        }
    }

    @FXML
    void handleBackButton(ActionEvent e){
        System.out.println("Going back to main menu...");
        loadScene("Main-login.fxml", "UVR!");
    }

    private void loadScene(String fxmlFile, String title){
        try {

            Parent nextSceneRoot = FXMLLoader.load(getClass().getResource(fxmlFile));
            Scene nextScene = new Scene(nextSceneRoot);

            Stage currentStage = (Stage) adminLoginRoot.getScene().getWindow();

            currentStage.setScene(nextScene);
            currentStage.setTitle(title);
            currentStage.show();

        } catch (IOException ex) {
            ex.printStackTrace();
            errorLabel.setText("Error: Could not load page.");
            errorLabel.setVisible(true);
        }
    }

    private void showError(String message){
        errorLabel.setText(message);
        errorLabel.setVisible(true);
    }
}
