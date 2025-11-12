/*
=====================================
This code displays four options:
1. Admin login
2. Client login
4. Client signup
3. Quit

Version: 1.0
Latest edit: November 10, 2025

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
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.stream.Collectors;

public class Client_signupController {

    private CustomerDAO customerDAO = new CustomerDAO();

    @FXML private TextField lastNameField;
    @FXML private TextField firstNameField;
    @FXML private TextField contactNumberField;
    @FXML private TextField streetField;
    @FXML private TextField barangayField;
    @FXML private TextField cityField;
    @FXML private TextField provinceField;
    @FXML private TextField emailField;

    @FXML private Label errorLabel;
    @FXML private Button confirmButton;
    @FXML private Button cancelButton;

    /**
     =========================================
     This function brings the user back to the
     main login scene if the cancel button is
     clicked.
     ========================================
     */
    private void navigateToMain() {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("Main-login.fxml"));
            Stage stage = (Stage) confirmButton.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("UVR!");
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     ============================================
     This is just a sanity check for cancel.
     ============================================
     */
    @FXML void handleCancelSignup() {
        System.out.println("Signup cancelled. Returning to main menu.");
        navigateToMain();
    }

    /**
     =============================================================
     This function handles the Confirm button action, performing
     validation, ID generation, and inserting of the new customer
     record into the database.
     =============================================================
     */
    @FXML void handleConfirmSignup() {

        // Customer record attribute related variables
        String lastName = lastNameField.getText().trim();
        String firstName = firstNameField.getText().trim();
        String contactNumber = contactNumberField.getText().trim();
        String street = streetField.getText().trim();
        String barangay = barangayField.getText().trim();
        String city = cityField.getText().trim();
        String province = provinceField.getText().trim();
        String email = emailField.getText().trim();

        if (lastName.isEmpty() || firstName.isEmpty() || contactNumber.isEmpty() || city.isEmpty()) {
            errorLabel.setText("Please fill out all required fields.");
            errorLabel.setVisible(true);
            return;
        }

        if (customerDAO.getCustomerByEmail(email) != null){
            errorLabel.setText("Registration failed: An account with this Email already exists.");
            errorLabel.setVisible(true);
            return;
        }

        errorLabel.setVisible(false);

        try {

            String newCustomerID = "CUST-" + (System.currentTimeMillis() % 100000);

            String fullAddress = java.util.Arrays.asList(street, barangay, city, province).stream().filter(s-> !s.isEmpty()).collect(Collectors.joining(", "));

            if (fullAddress.isEmpty()){ fullAddress = city; }

            Customer newCustomer = new Customer();
            newCustomer.setCustomerID(newCustomerID);
            newCustomer.setLastName(lastName);
            newCustomer.setFirstName(firstName);
            newCustomer.setContactNumber(contactNumber);
            newCustomer.setAddress(fullAddress);
            newCustomer.setEmailAddress(email);

            //Auto-generated ID
            boolean success = customerDAO.insertCustomer(newCustomer);

            //Alex can you study how this can be costumized? But I'm sure it's costumized.
            if (success) {
                System.out.println("New customer registered: " + newCustomerID);

                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Registration Successful!");

                alert.setHeaderText("Welcome, " + newCustomer.getFirstName() + "!");
                alert.setContentText("Your Customer ID is: " + newCustomerID +
                        "\nPlease use your Contact Number as your password to log in!");
                alert.showAndWait();

                navigateToMain();

            } else {
                errorLabel.setText("Registration failed. A unique constraint or data error likely occurred.");
                errorLabel.setVisible(true);
            }
        } catch (Exception e) {
            e.printStackTrace();
            errorLabel.setText("System Error: Could not process registration.");
            errorLabel.setVisible(true);
        }
    }
}
