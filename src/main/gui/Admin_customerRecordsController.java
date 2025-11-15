package main.gui;

import dao.CustomerDAO;import model.Customer;import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.util.Callback;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class Admin_customerRecordsController implements Initializable{

    @FXML private Label customerCountLabel;
    @FXML private TableView<Customer> customerTable;
    @FXML private TableColumn<Customer, String> customerIDColumn;
    @FXML private TableColumn<Customer, String> lastNameColumn;
    @FXML private TableColumn<Customer, String> firstNameColumn;
    @FXML private TableColumn<Customer, String> contactColumn;
    @FXML private TableColumn<Customer, String> addressColumn;
    @FXML private TableColumn<Customer, String> emailColumn;
    @FXML private TableColumn<Customer, Void> editColumn;

    private CustomerDAO customerDAO = new CustomerDAO();
    private Admin_dashboardController mainController;

    @Override
    public void initialize(URL url, ResourceBundle rb){

        customerIDColumn.setCellValueFactory(new PropertyValueFactory<>("customerID"));
        lastNameColumn.setCellValueFactory(new PropertyValueFactory<>("lastName"));
        firstNameColumn.setCellValueFactory(new PropertyValueFactory<>("firstName"));
        contactColumn.setCellValueFactory(new PropertyValueFactory<>("contactNumber"));
        addressColumn.setCellValueFactory(new PropertyValueFactory<>("address"));
        emailColumn.setCellValueFactory(new PropertyValueFactory<>("emailAddress"));

        loadCustomerData();

        setupEditButtonColumn();
    }

    public void setMainController(Admin_dashboardController mainController){
        this.mainController = mainController;
    }

    private void loadCustomerData() {

        List<Customer> customerList = customerDAO.getAllCustomers();
        ObservableList<Customer> customers = FXCollections.observableArrayList(customerList);

        customerTable.setItems(customers);
        customerCountLabel.setText("(" + customers.size() + ") CUSTOMERS: ");
    }

    @FXML private void handleAddCustomer(){
        System.out.println("Add button clicked. Loading new customer form...");
        mainController.loadCustomerForm(null);
    }

    private void handleEditCustomer(Customer customer){
        System.out.println("Edit button clicked. Loading updated customer form...");
        mainController.loadCustomerForm(customer);
    }

    private void setupEditButtonColumn() {
        Callback<TableColumn<Customer, Void>, TableCell<Customer, Void>> cellFactory = new Callback<>() {
            @Override
            public TableCell<Customer, Void> call(final TableColumn<Customer, Void> param) {
                final TableCell<Customer, Void> cell = new TableCell<>() {
                    private final Button btn = new Button("Edit");
                    {
                        btn.getStyleClass().add("edit-button");
                        btn.setOnAction((ActionEvent event) -> {
                            Customer customer = getTableView().getItems().get(getIndex());
                            handleEditCustomer(customer);
                        });
                    }

                    @Override
                    public void updateItem(Void item, boolean empty) {
                        super.updateItem(item, empty);
                        if (empty) {
                            setGraphic(null);
                        } else {
                            setGraphic(btn);
                        }
                    }
                };
                return cell;
            }
        };
        editColumn.setCellFactory(cellFactory);
    }

}
