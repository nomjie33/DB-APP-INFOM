package main.gui;

import javafx.collections.FXCollections;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import java.io.IOException;
import java.util.Objects;
import javafx.scene.control.DialogPane;
import dao.AddressDAO;
import dao.BarangayDAO;
import dao.CityDAO;
import dao.CustomerDAO;
import model.Address;
import model.Barangay;
import model.City;
import model.Customer;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

import java.net.URL;
import java.util.ResourceBundle;

public class Admin_customerFormController implements Initializable {

    // FXML Fields
    @FXML private Label formHeaderLabel;
    @FXML private TextField idField;
    @FXML private TextField firstNameField;
    @FXML private TextField lastNameField;
    @FXML private TextField contactField;
    @FXML private TextField emailField;
    @FXML private ComboBox<City> cityComboBox;
    @FXML private ComboBox<Barangay> barangayComboBox;
    @FXML private TextField streetField;

    // DAOs
    private CustomerDAO customerDAO = new CustomerDAO();
    private AddressDAO addressDAO = new AddressDAO();
    private CityDAO cityDAO = new CityDAO();
    private BarangayDAO barangayDAO = new BarangayDAO();

    private Admin_dashboardController mainController;
    private boolean isUpdatingRecord = false;
    private Customer currentCustomer;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        cityComboBox.getItems().setAll(cityDAO.getAllCities());
        cityComboBox.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                barangayComboBox.setDisable(false);
                barangayComboBox.getItems().setAll(barangayDAO.getBarangaysByCity(newVal.getCityID()));
            } else {
                barangayComboBox.setDisable(true);
                barangayComboBox.getItems().clear();
            }
        });

    }

    public void setMainController(Admin_dashboardController mainController){
        this.mainController = mainController;
    }

    public void setCustomerData(Customer customer){
        if (customer == null) return;
        isUpdatingRecord = true;
        currentCustomer = customer;
        formHeaderLabel.setText("Update Customer");
        idField.setDisable(true);
        idField.getStyleClass().add("form-text-field-disabled");
        idField.setText(customer.getCustomerID());
        firstNameField.setText(customer.getFirstName());
        lastNameField.setText(customer.getLastName());
        contactField.setText(customer.getContactNumber());
        emailField.setText(customer.getEmailAddress());

        if (customer.getAddressID() != null) {
            Address fullAddress = addressDAO.getAddressWithFullDetails(customer.getAddressID());
            if (fullAddress != null && fullAddress.getBarangay() != null && fullAddress.getBarangay().getCity() != null) {
                City city = fullAddress.getBarangay().getCity();
                Barangay barangay = fullAddress.getBarangay();
                cityComboBox.setValue(city);
                barangayComboBox.setValue(barangay);
                streetField.setText(fullAddress.getStreet());
            }
        }
    }

    @FXML private void handleSave() {

        if (!validateFields()) return;

        Barangay selectedBarangay = barangayComboBox.getValue();
        String street = streetField.getText();

        if (isUpdatingRecord) {
            Address currentAddress = (currentCustomer.getAddressID() != null)
                    ? addressDAO.getAddressWithFullDetails(currentCustomer.getAddressID())
                    : new Address();
            Barangay currentBarangay = (currentAddress.getBarangay() != null) ? currentAddress.getBarangay() : new Barangay();
            String currentStreet = (currentAddress.getStreet() != null) ? currentAddress.getStreet() : "";

            boolean fNameChanged = !Objects.equals(currentCustomer.getFirstName(), firstNameField.getText());
            boolean lNameChanged = !Objects.equals(currentCustomer.getLastName(), lastNameField.getText());
            boolean contactChanged = !Objects.equals(currentCustomer.getContactNumber(), contactField.getText());
            boolean emailChanged = !Objects.equals(currentCustomer.getEmailAddress(), emailField.getText());
            boolean streetChanged = !Objects.equals(currentStreet, street);
            boolean barangayChanged = !Objects.equals(currentBarangay.getBarangayID(), selectedBarangay.getBarangayID());

            if (!fNameChanged && !lNameChanged && !contactChanged && !emailChanged && !streetChanged && !barangayChanged) {
                System.out.println("No changes detected. Returning to list.");
                mainController.loadPage("Admin-customerRecords.fxml"); // Just go back
                return;
            }
        }

        try {
            Address address = addressDAO.findOrCreateAddress(selectedBarangay.getBarangayID(), street);
            if (address == null || address.getAddressID() == null) {
                showAlert(Alert.AlertType.ERROR, "Error", "Failed to save address record.");
                return;
            }

            Customer customer;
            boolean isSuccessful;
            if (isUpdatingRecord){
                customer = currentCustomer;
                customer.setFirstName(firstNameField.getText());
                customer.setLastName(lastNameField.getText());
                customer.setContactNumber(contactField.getText());
                customer.setEmailAddress(emailField.getText());
                customer.setAddressID(address.getAddressID());
                isSuccessful = customerDAO.updateCustomer(customer);
            } else {
                customer = new Customer();
                customer.setCustomerID(idField.getText());

                customer.setFirstName(firstNameField.getText());
                customer.setLastName(lastNameField.getText());
                customer.setContactNumber(contactField.getText());
                customer.setEmailAddress(emailField.getText());
                customer.setAddressID(address.getAddressID());
                customer.setStatus("Active");
                isSuccessful = customerDAO.insertCustomer(customer);
            }

            if (isSuccessful){
                String title;
                String content;
                String name = customer.getFirstName() + " " + customer.getLastName();
                String fullAddress = street + ", " + selectedBarangay.getName() + ", " + cityComboBox.getValue().getName();

                if (isUpdatingRecord) {
                    title = "Customer Record Updated!";
                    content = "The record has been successfully updated.\n\n" +
                            "Customer ID:\n" + customer.getCustomerID() + "\n\n" +
                            "Name:\n" + name + "\n\n" +
                            "Contact:\n" + customer.getContactNumber() + "\n\n" +
                            "Address:\n" + fullAddress;

                    showAlert(Alert.AlertType.INFORMATION, "Update Successful", "Customer record has been updated.");
                } else {
                    title = "New Customer Created!";
                    content = "A new customer has been successfully added.\n\n" +
                            "Customer ID:\n" + customer.getCustomerID() + "\n\n" +
                            "Name:\n" + name + "\n\n" +
                            "Contact:\n" + customer.getContactNumber() + "\n\n" +
                            "Address:\n" + fullAddress;
                }
                showConfirmationDialog(title, content);
                mainController.loadPage("Admin-customerRecords.fxml");
            } else {
                showAlert(Alert.AlertType.ERROR, "Error", "Failed to save customer record.");
            }
        } catch (Exception e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "System Error", "An unexpected error occurred: " + e.getMessage());
        }
    }

    @FXML private void handleCancel(){
        mainController.loadPage("Admin-customerRecords.fxml");
    }

    private boolean validateFields(){

        String customerID = idField.getText().trim();

        if (firstNameField.getText().isEmpty() || lastNameField.getText().isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Validation Error", "First Name and Last Name are required.");
            return false;
        }

        if (cityComboBox.getValue() == null || barangayComboBox.getValue() == null) {
            showAlert(Alert.AlertType.WARNING, "Validation Error", "City and Barangay are required.");
            return false;
        }

        if (!isUpdatingRecord) {
            if (customerID.isEmpty()) {
                showAlert(Alert.AlertType.WARNING, "Validation Error", "Customer ID is required.");
                return false;
            }

            if (customerDAO.getCustomerById(customerID) != null) {
                showAlert(Alert.AlertType.ERROR, "Duplicate ID", "The Customer ID '" + customerID + "' already exists. Please enter a unique ID.");
                return false;
            }
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