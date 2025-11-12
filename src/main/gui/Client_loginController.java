/*
=====================================
This code facilitates the client
login option.

Version: 1.0
Latest edit: November 11, 2025

Authors:
Airon Matthew Bantillo | S22-07
Alexandra Gayle Gonzales | S22-07
Naomi Isabel Reyes | S22-07
Roberta Netanya Tan | S22-07
=====================================
*/

package main.gui;

import dao.CustomerDAO;
import model.Customer;
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

public class Client_loginController {

    private CustomerDAO customerDAO = new CustomerDAO();

    @FXML private VBox clientLoginRoot;
    @FXML private TextField usernameField;
    @FXML private PasswordField passwordField;
    @FXML private Button loginButton;
    @FXML private Button backButton;
    @FXML private Label errorLabel;

    /**
     * =========================================================
     * This function prompts the user to enter the following:
     * 1. Registered customer ID
     * 2. Contact Number
     * =========================================================
     * @param e listens for user input at the login button.
     */
    @FXML void handleLoginButton(ActionEvent e){

        String customerID = usernameField.getText();
        String password = passwordField.getText();

        if (customerID.isEmpty() || password.isEmpty()){
            errorLabel.setText("Please enter your Customer ID and Contact Number.");
            errorLabel.setVisible(true);
            return;
        }

        try {

            Customer customer = customerDAO.getCustomerById(customerID);

            if (customer != null && customer.getContactNumber().equals(password)){
                System.out.println("Client login successful for: " + customer.getFullName());
                errorLabel.setVisible(false);

                try {

                    FXMLLoader loader = new FXMLLoader(getClass().getResource("Client-dashboard.fxml"));
                    Parent root = loader.load();

                    Client_dashboardController dashboardController = loader.getController();
                    dashboardController.initData(customer);

                    Stage currentStage = (Stage) clientLoginRoot.getScene().getWindow();
                    Scene nextScene = new Scene(root);
                    currentStage.setScene(nextScene);
                    currentStage.setTitle("UVR!");
                    currentStage.show();

                } catch (Exception ex){
                    ex.printStackTrace();
                    errorLabel.setText("Error: Could not load dashboard page.");
                    errorLabel.setVisible(true);
                }

            }

        } catch (Exception ex){
            ex.printStackTrace();
            errorLabel.setText("Error connecting to database. Please try again.");
            errorLabel.setVisible(true);
        }
    }

    /**
     =======================================================
     this function returns the user to the main menu
     @param e listens for user input at the back button
     =======================================================
     */
    @FXML void handleBackButton(ActionEvent e){
        System.out.println("Going back to main menu...");
        loadScene("Main-login.fxml", "UVR!");
    }

    /**
     ==================================================================
     This function loads a new FXML file depending on what is clicked.
     @param fxmlFile The name of the .fxml file to load
     @param title The title will be UVR! regardless
     ==================================================================
     */
    private void loadScene(String fxmlFile, String title){

        try {

            Parent nextSceneRoot = FXMLLoader.load(getClass().getResource(fxmlFile));
            Scene nextScene = new Scene(nextSceneRoot);
            Stage currentStage = (Stage) clientLoginRoot.getScene().getWindow();

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
