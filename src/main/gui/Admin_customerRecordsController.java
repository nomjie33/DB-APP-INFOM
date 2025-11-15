package main.gui;

import javafx.scene.control.Tooltip;
import javafx.scene.input.MouseEvent;
import javafx.scene.control.Tooltip;
import javafx.scene.input.MouseEvent;
import javafx.stage.Popup;
import javafx.scene.control.Label;
import javafx.geometry.Bounds;

import dao.CustomerDAO;
import javafx.scene.control.*;
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

    private CustomerDAO customerDAO = new CustomerDAO();
    private Admin_dashboardController mainController;

    private static Popup textDisplayPopup = new Popup();
    private static Label popupLabel = new Label();
    private static TableCell<?, ?> currentlyOpenCell = null;

    static {
        popupLabel.setStyle(
                "-fx-background-color: white;" +
                        "-fx-border-color: #33a398;" +
                        "-fx-border-width: 2;" +
                        "-fx-padding: 8px;" +
                        "-fx-background-radius: 5;" +
                        "-fx-border-radius: 5;" +
                        "-fx-text-fill: black;" +
                        "-fx-font-size: 13px;"
        );
        textDisplayPopup.getContent().add(popupLabel);
        textDisplayPopup.setAutoHide(true);
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {

        customerIDColumn.setCellValueFactory(new PropertyValueFactory<>("customerID"));
        lastNameColumn.setCellValueFactory(new PropertyValueFactory<>("lastName"));
        firstNameColumn.setCellValueFactory(new PropertyValueFactory<>("firstName"));
        contactColumn.setCellValueFactory(new PropertyValueFactory<>("contactNumber"));

        addressColumn.setCellValueFactory(new PropertyValueFactory<>("fullAddressString"));
        addressColumn.setCellFactory(column -> {
            return new TableCell<Customer, String>() {
                @Override
                protected void updateItem(String item, boolean empty) {
                    super.updateItem(item, empty);

                    if (item == null || empty) {
                        setText(null);
                        setOnMouseClicked(null);
                    } else {

                        setText(item);
                        setOnMouseClicked(event -> {

                            if (textDisplayPopup.isShowing() && currentlyOpenCell == this) {
                                textDisplayPopup.hide();
                                currentlyOpenCell = null;
                            } else {
                                popupLabel.setText(item);
                                Bounds bounds = this.localToScreen(this.getBoundsInLocal());
                                textDisplayPopup.show(this.getScene().getWindow(), bounds.getMinX(), bounds.getMinY());
                                currentlyOpenCell = this;
                            }
                        });
                    }
                }
            };
        });

        emailColumn.setCellValueFactory(new PropertyValueFactory<>("emailAddress"));
        emailColumn.setCellFactory(column -> {
            return new TableCell<Customer, String>() {
                @Override
                protected void updateItem(String item, boolean empty) {
                    super.updateItem(item, empty);

                    if (item == null || empty) {
                        setText(null);
                        setOnMouseClicked(null);
                    } else {

                        setText(item);
                        setOnMouseClicked(event -> {

                            if (textDisplayPopup.isShowing() && currentlyOpenCell == this) {
                                textDisplayPopup.hide();
                                currentlyOpenCell = null;
                            } else {
                                popupLabel.setText(item);
                                Bounds bounds = this.localToScreen(this.getBoundsInLocal());
                                textDisplayPopup.show(this.getScene().getWindow(), bounds.getMinX(), bounds.getMinY());
                                currentlyOpenCell = this;
                            }
                        });
                    }
                }
            };
        });

        loadCustomerData();
        setupEditButtonColumn();
    }

    public void setMainController(Admin_dashboardController mainController) {
        this.mainController = mainController;
    }

    private void loadCustomerData() {

        // --- FIX 2: Use the DAO method that loads the full address ---
        List<Customer> customerList = customerDAO.getAllCustomersWithAddress();

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