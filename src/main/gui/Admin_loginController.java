package main.gui;

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

    @FXML private VBox adminLoginRoot;
    @FXML private TextField usernameField;
    @FXML private PasswordField passwordField;
    @FXML private Button loginButton;
    @FXML private Button backButton;
    @FXML private Label errorLabel;

    @FXML void handleLoginButton(ActionEvent e){

        String username = usernameField.getText();
        String password = passwordField.getText();


        if (username.equals("CCINFOM") && password.equals("S22-07")){

        } else {
            System.out.println("Admin login failed.");
            errorLabel.setText("Invalid username or password.");
            errorLabel.setVisible(true);
        }
    }

    @FXML
    void handleBackButton(ActionEvent e){
        System.out.println("Going back to main menu...");
        loadScene("Main-login.fxml", "UVR! - Select Role");
    }

    private void loadScene(String fxmlFile, String title){
        try {

            Parent nextSceneRoot = FXMLLoader.load(getClass().getResource(fxmlFile));
            Scene nextScene = new Scene(nextSceneRoot);

            Stage currentStage = (Stage) adminLoginRoot.getScene().getWindow();

            currentStage.setScene(nextScene);
            currentStage.setTitle(title);
            currentStage.show();

        } catch (IOException e) {
            e.printStackTrace();
            errorLabel.setText("Error: Could not load page.");
            errorLabel.setVisible(true);
        }
    }
}
