package main.gui;

import dao.AddressDAO;
import javafx.scene.control.Tooltip;
import javafx.scene.input.MouseEvent;
import javafx.scene.control.Tooltip;
import javafx.scene.input.MouseEvent;
import javafx.stage.Popup;
import javafx.scene.control.Label;
import javafx.geometry.Bounds;

import dao.CustomerDAO;
import javafx.scene.control.*;
import model.Address;
import model.Customer;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.util.Callback;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class Admin_customerRecordsController implements Initializable {

    @FXML private Label customerCountLabel;
    @FXML private TableView<Customer> customerTable;
    @FXML private TableColumn<Customer, String> customerIDColumn;
    @FXML private TableColumn<Customer, String> lastNameColumn;
    @FXML private TableColumn<Customer, String> firstNameColumn;
    @FXML private TableColumn<Customer, String> contactColumn;
    @FXML private TableColumn<Customer, String> addressColumn;
    @FXML private TableColumn<Customer, String> emailColumn;
    @FXML private TableColumn<Customer, Void> editColumn;

    @FXML private TableColumn<Customer, Void> actionColumn;
    @FXML private ComboBox<String> statusFilterComboBox;

    private CustomerDAO customerDAO = new CustomerDAO();
    private Admin_dashboardController mainController;
    private AddressDAO addressDAO = new AddressDAO();

    @Override
    public void initialize(URL url, ResourceBundle rb) {

        customerIDColumn.setCellValueFactory(new PropertyValueFactory<>("customerID"));
        lastNameColumn.setCellValueFactory(new PropertyValueFactory<>("lastName"));
        firstNameColumn.setCellValueFactory(new PropertyValueFactory<>("firstName"));
        contactColumn.setCellValueFactory(new PropertyValueFactory<>("contactNumber"));
        addressColumn.setCellValueFactory(new PropertyValueFactory<>("fullAddressString"));
        emailColumn.setCellValueFactory(new PropertyValueFactory<>("emailAddress"));

        addressColumn.setCellFactory(column -> createPopupCellFactory());
        emailColumn.setCellFactory(column -> createPopupCellFactory());

        statusFilterComboBox.setItems(FXCollections.observableArrayList("Active", "Inactive", "All"));
        statusFilterComboBox.setValue("Active");

        statusFilterComboBox.setOnAction(e -> loadCustomerData());

        loadCustomerData();
        setupEditButtonColumn();
        setupActionColumn();
    }

    public void setMainController(Admin_dashboardController mainController) {
        this.mainController = mainController;
    }

    private void loadCustomerData() {

        customerTable.getItems().clear();

        String statusFilter = statusFilterComboBox.getValue();
        List<Customer> customerList;

        if ("All".equals(statusFilter)) {
            customerList = customerDAO.getAllCustomersIncludingInactive();
        } else {
            customerList = customerDAO.getCustomersByStatus(statusFilter);
        }

        for (Customer customer : customerList) {
            try {
                if (customer != null && customer.getAddressID() != null) {
                    Address address = addressDAO.getAddressWithFullDetails(customer.getAddressID());
                    if (address != null) {
                        customer.setAddress(address);
                    }
                }
            } catch (Exception e) {

                System.err.println("Error loading address for customer " + customer.getCustomerID() + ": " + e.getMessage());

            }
        }

        ObservableList<Customer> customers = FXCollections.observableArrayList(customerList);
        customerTable.setItems(customers);
        customerCountLabel.setText("(" + customers.size() + ") CUSTOMERS: ");
    }

    @FXML
    private void handleAddCustomer() {
        System.out.println("Add button clicked. Loading new customer form...");
        mainController.loadCustomerForm(null);
    }

    private void handleEditCustomer(Customer customer) {
        System.out.println("Edit button clicked. Loading updated customer form...");
        mainController.loadCustomerForm(customer);
    }

    private void setupEditButtonColumn() {
        Callback<TableColumn<Customer, Void>, TableCell<Customer, Void>> cellFactory = new Callback<>() {
            @Override
            public TableCell<Customer, Void> call(final TableColumn<Customer, Void> param) {

                final TableCell<Customer, Void> cell = new TableCell<>() {

                    @Override
                    public void updateItem(Void item, boolean empty) {
                        super.updateItem(item, empty);

                        if (empty || getTableRow() == null || getTableRow().getItem() == null) {

                            setGraphic(null);
                        } else {

                            Button btn = new Button("Edit");
                            btn.getStyleClass().add("edit-button");

                            btn.setOnAction((ActionEvent event) -> {
                                Customer customer = getTableRow().getItem();
                                if (customer != null) {
                                    handleEditCustomer(customer);
                                }
                            });

                            setGraphic(btn);
                        }
                    }
                };
                return cell;
            }
        };
        editColumn.setCellFactory(cellFactory);
    }

    private void setupActionColumn() {
        Callback<TableColumn<Customer, Void>, TableCell<Customer, Void>> cellFactory = new Callback<>() {
            @Override
            public TableCell<Customer, Void> call(final TableColumn<Customer, Void> param) {

                final TableCell<Customer, Void> cell = new TableCell<>() {

                    @Override
                    public void updateItem(Void item, boolean empty) {
                        super.updateItem(item, empty);

                        if (empty || getTableRow() == null || getTableRow().getItem() == null) {

                            setGraphic(null);
                        } else {

                            Button btn = new Button();
                            Customer customer = getTableRow().getItem();

                            if ("Active".equals(customer.getStatus())) {
                                btn.setText("Deactivate");
                                btn.getStyleClass().add("deactivate-button");
                            } else {
                                btn.setText("Reactivate");
                                btn.getStyleClass().add("edit-button");
                            }

                            btn.setOnAction((ActionEvent event) -> {

                                if (customer != null) {
                                    handleDeactivateReactivate(customer);
                                }
                            });

                            setGraphic(btn);
                        }
                    }
                };
                return cell;
            }
        };
        actionColumn.setCellFactory(cellFactory);
    }

    private void handleDeactivateReactivate(Customer customer) {
        String newStatus = "Active".equals(customer.getStatus()) ? "Inactive" : "Active";
        String action = "Active".equals(customer.getStatus()) ? "deactivate" : "reactivate";

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmation");
        alert.setHeaderText("Confirm " + action);
        alert.setContentText("Are you sure you want to " + action + " customer: " + customer.getFullName() + "?");

        if (alert.showAndWait().get() == ButtonType.OK) {
            boolean success;

            if ("Active".equals(customer.getStatus())) {
                success = customerDAO.deactivateCustomer(customer.getCustomerID());
            } else {
                success = customerDAO.reactivateCustomer(customer.getCustomerID());
            }

            if (success) {

                showAlert(Alert.AlertType.INFORMATION, "Success", "Customer has been " + action + "d.");
                loadCustomerData();
            } else {
                showAlert(Alert.AlertType.ERROR, "Error", "Failed to update customer status.");
            }
        }
    }

    private TableCell<Customer, String> createPopupCellFactory() {
        return new TableCell<Customer, String>() {

            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);

                if (item == null || empty) {
                    setText(null);
                    setTooltip(null);
                } else {
                    setText(item);

                    Tooltip tooltip = new Tooltip();
                    tooltip.setText(item);
                    setTooltip(tooltip);
                }
            }
        };
    }

    private void showAlert(Alert.AlertType type, String title, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}