package main.gui;

import dao.CustomerDAO;
import model.Customer;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

public class Admin_customerFormController {

    @FXML private Label formHeaderLabel;
    @FXML private TextField idField;
    @FXML private TextField firstNameField;
    @FXML private TextField lastNameField;
    @FXML private TextField contactField;
    @FXML private TextField emailField;
    @FXML private TextField addressField;

    private Admin_dashboardController mainController;
    private CustomerDAO customerDAO = new CustomerDAO();
    private boolean isUpdatingRecord = false;

    public void setMainController(Admin_dashboardController mainController){
        this.mainController = mainController;
    }

    public void setCustomerData(Customer customer){
        if (customer != null){
            isUpdatingRecord = true;
            formHeaderLabel.setText("Update Customer");

            idField.setText(customer.getCustomerID());
            firstNameField.setText(customer.getFirstName());
            lastNameField.setText(customer.getLastName());
            contactField.setText(customer.getContactNumber());
            emailField.setText(customer.getEmailAddress());
            addressField.setText(customer.getAddress());

            //This should disable the ID field (idk that's just my basis for edit mode haha)
            idField.setDisable(true);
            idField.getStyleClass().add("form-text-field-disabled");
        }
    }

    @FXML private void handleSave(){
        if (!validateFields()) return;

        Customer customer = new Customer();
        customer.setCustomerID(idField.getText());
        customer.setFirstName(firstNameField.getText());
        customer.setLastName(lastNameField.getText());
        customer.setContactNumber(contactField.getText());
        customer.setAddress(addressField.getText());
        customer.setStatus("Active");

        boolean isSuccessful;
        if (isUpdatingRecord){
            isSuccessful = customerDAO.updateCustomer(customer);
        } else {
            isSuccessful = customerDAO.insertCustomer(customer);
        }

        if (isSuccessful){
            showAlert(Alert.AlertType.INFORMATION, "Customer update successful",
                    "Customer record saved successfully.");
            mainController.loadPage("Admin-customerRecords.fxml");
        } else {
            showAlert(Alert.AlertType.ERROR, "Error", "Failed to save customer record.");
        }
    }

    @FXML private void handleCancel(){
        System.out.println("Cancel button clicked.");
        mainController.loadPage("Admin-customerRecords.fxml");
    }

    private boolean validateFields(){
        if (idField.getText().isEmpty() || firstNameField.getText().isEmpty() ||
        lastNameField.getText().isEmpty()){
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
