package main.gui;

import dao.CustomerDAO;
import dao.AddressDAO;
import dao.BarangayDAO;
import dao.CityDAO;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import model.Customer;
import model.Address;
import model.Barangay;
import model.City;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class Client_signupController implements Initializable {

    // DAOs
    private CustomerDAO customerDAO = new CustomerDAO();
    private AddressDAO addressDAO = new AddressDAO();
    private CityDAO cityDAO = new CityDAO();
    private BarangayDAO barangayDAO = new BarangayDAO();

    // FXML Fields
    @FXML private TextField lastNameField;
    @FXML private TextField firstNameField;
    @FXML private TextField contactNumberField;
    @FXML private TextField emailField;
    @FXML private ImageView bgImage;
    @FXML private Pane orangeOverlay;

    @FXML private ComboBox<City> cityComboBox;
    @FXML private ComboBox<Barangay> barangayComboBox;
    @FXML private TextField streetField;

    @FXML private Label errorLabel;
    @FXML private Button confirmButton;
    @FXML private Button cancelButton;

    /**
     * This method is called when the FXML is loaded.
     */
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        orangeOverlay.prefWidthProperty().bind(bgImage.fitWidthProperty());
        orangeOverlay.prefHeightProperty().bind(bgImage.fitHeightProperty());
        // 1. Load all cities into the first dropdown
        cityComboBox.getItems().setAll(cityDAO.getAllCities());

        // 2. Add a listener to the city ComboBox
        cityComboBox.getSelectionModel().selectedItemProperty().addListener((options, oldVal, newVal) -> {
            onCitySelected(newVal); // Call helper method
        });
    }

    /**
     * Called when a user selects a city from the dropdown.
     * This populates the barangay dropdown.
     */
    private void onCitySelected(City selectedCity) {
        if (selectedCity != null) {
            // A city is selected:
            // 1. Enable the barangay dropdown
            barangayComboBox.setDisable(false);
            barangayComboBox.setPromptText("Select a Barangay");
            // 2. Load all barangays for that city
            barangayComboBox.getItems().setAll(barangayDAO.getBarangaysByCity(selectedCity.getCityID()));
        } else {
            // No city is selected:
            // 1. Disable and clear the barangay dropdown
            barangayComboBox.setDisable(true);
            barangayComboBox.setPromptText("Select a City first");
            barangayComboBox.getItems().clear();
        }
    }

    /**
     * Called when the "Confirm" button is clicked.
     */
    @FXML
    void handleConfirmSignup() {
        // --- 1. Get Data ---
        String lastName = lastNameField.getText().trim();
        String firstName = firstNameField.getText().trim();
        String contactNumber = contactNumberField.getText().trim();
        String email = emailField.getText().trim();
        Barangay selectedBarangay = barangayComboBox.getValue();
        String street = streetField.getText().trim();

        // --- 2. Validation ---
        if (!validateFields(lastName, firstName, contactNumber, email, selectedBarangay)) {
            return; // showError is called inside validateFields
        }

        errorLabel.setVisible(false);

        // --- 3. Process Signup ---
        try {
            // --- A. Create the Address ---
            Address newAddress = new Address();
            newAddress.setBarangayID(selectedBarangay.getBarangayID());
            newAddress.setStreet(street);

            // Insert address. Your DAO is smart and sets the new ID on the object.
            boolean addressSuccess = addressDAO.insertAddress(newAddress);

            if (!addressSuccess || newAddress.getAddressID() == null) {
                showError("Registration failed: Could not save address.");
                return;
            }

            // --- B. Create the Customer ---
            String newCustomerID = "CUST-" + (System.currentTimeMillis() % 100000);
            Customer newCustomer = new Customer();
            newCustomer.setCustomerID(newCustomerID);
            newCustomer.setLastName(lastName);
            newCustomer.setFirstName(firstName);
            newCustomer.setContactNumber(contactNumber);
            newCustomer.setEmailAddress(email);
            newCustomer.setAddressID(newAddress.getAddressID()); // Link to the new address
            // newCustomer.setStatus("Active"); // Your Customer constructor does this

            // --- C. Insert the Customer ---
            boolean customerSuccess = customerDAO.insertCustomer(newCustomer);

            if (customerSuccess) {
                System.out.println("New customer registered: " + newCustomerID);
                showAlert(
                        "Registration Successful!",
                        "Welcome, " + newCustomer.getFirstName() + "!",
                        "Your Customer ID is: " + newCustomerID +
                                "\nPlease use your Contact Number as your password to log in!"
                );
                navigateToMain();
            } else {
                showError("Registration failed. A unique constraint or data error likely occurred.");
            }
        } catch (Exception e) {
            e.printStackTrace();
            showError("System Error: Could not process registration.");
        }
    }

    /**
     * Validates all input fields for the signup form.
     * @return true if all fields are valid, false otherwise.
     */
    private boolean validateFields(String last, String first, String contact, String email, Barangay barangay) {
        if (last.isEmpty() || first.isEmpty() || contact.isEmpty() || email.isEmpty()) {
            showError("Please fill out all personal information fields.");
            return false;
        }
        if (barangay == null) {
            showError("Please select a City and Barangay.");
            return false;
        }
        if (customerDAO.getCustomerByEmail(email) != null){
            showError("Registration failed: An account with this Email already exists.");
            return false;
        }
        return true;
    }

    private void showError(String message) {
        errorLabel.setText(message);
        errorLabel.setVisible(true);
    }

    private void showAlert(String title, String header, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(content);
        alert.showAndWait();
    }

    /**
     * Called when the "Cancel" button is clicked.
     */
    @FXML
    void handleCancelSignup() {
        System.out.println("Signup cancelled. Returning to main menu.");
        navigateToMain();
    }

    /**
     * Navigates the user back to the Main-login.fxml scene.
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
}