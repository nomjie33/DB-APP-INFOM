package main.gui;

import javafx.scene.control.Tooltip;
import javafx.scene.input.MouseEvent;
import javafx.stage.Popup;
import javafx.scene.control.Label;
import javafx.geometry.Bounds;

import dao.PaymentDAO;
import javafx.scene.control.*;
import model.PaymentTransaction;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.util.Callback;

import java.net.URL;
import java.sql.Date;
import java.util.List;
import java.util.ResourceBundle;

public class Admin_paymentRecordsController implements Initializable {

    @FXML private Label paymentCountLabel;
    @FXML private TableView<PaymentTransaction> paymentTable;
    @FXML private TableColumn<PaymentTransaction, String> paymentIDColumn;
    @FXML private TableColumn<PaymentTransaction, String> rentalIDColumn;
    @FXML private TableColumn<PaymentTransaction, String> amountColumn;
    @FXML private TableColumn<PaymentTransaction, Date> paymentDateColumn;
    @FXML private TableColumn<PaymentTransaction, String> statusColumn;
    @FXML private TableColumn<PaymentTransaction, Void> editColumn;

    @FXML private TableColumn<PaymentTransaction, Void> actionColumn;
    @FXML private ComboBox<String> statusFilterComboBox;

    private PaymentDAO paymentDAO = new PaymentDAO();
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
        paymentIDColumn.setCellValueFactory(new PropertyValueFactory<>("paymentID"));
        rentalIDColumn.setCellValueFactory(new PropertyValueFactory<>("rentalID"));
        amountColumn.setCellValueFactory(new PropertyValueFactory<>("amount"));
        paymentDateColumn.setCellValueFactory(new PropertyValueFactory<>("paymentDate"));
        statusColumn.setCellValueFactory(new PropertyValueFactory<>("status"));

        statusFilterComboBox.setItems(FXCollections.observableArrayList("Active", "Inactive", "All"));
        statusFilterComboBox.setValue("Active");
        statusFilterComboBox.setOnAction(e -> loadPaymentData());

        loadPaymentData();
        setupEditButtonColumn();
        setupActionColumn();
    }

    public void setMainController(Admin_dashboardController mainController) {
        this.mainController = mainController;
    }

    private void loadPaymentData() {
        paymentTable.getItems().clear();
        String statusFilter = statusFilterComboBox.getValue();
        List<PaymentTransaction> paymentList;

        if ("All".equals(statusFilter)) {
            paymentList = paymentDAO.getAllPaymentsIncludingInactive();
        } else {
            paymentList = paymentDAO.getPaymentsByStatus(statusFilter);
        }

        ObservableList<PaymentTransaction> payments = FXCollections.observableArrayList(paymentList);
        paymentTable.setItems(payments);
        paymentCountLabel.setText("(" + payments.size() + ") PAYMENTS:");
    }

    @FXML
    private void handleAddPayment() {
        mainController.loadPaymentForm(null);
    }

    private void handleEditPayment(PaymentTransaction payment) {
        mainController.loadPaymentForm(payment);
    }

    private void setupEditButtonColumn() {
        Callback<TableColumn<PaymentTransaction, Void>, TableCell<PaymentTransaction, Void>> cellFactory = new Callback<>() {
            @Override
            public TableCell<PaymentTransaction, Void> call(final TableColumn<PaymentTransaction, Void> param) {
                final TableCell<PaymentTransaction, Void> cell = new TableCell<>() {
                    @Override
                    public void updateItem(Void item, boolean empty) {
                        super.updateItem(item, empty);
                        PaymentTransaction payment = getTableRow() != null ? getTableRow().getItem() : null;

                        if (empty || payment == null || !"Active".equals(payment.getStatus())) {
                            setGraphic(null);
                        } else {
                            Button btn = new Button("Edit");
                            btn.getStyleClass().add("edit-button");
                            btn.setOnAction((ActionEvent event) -> {
                                handleEditPayment(payment);
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
        Callback<TableColumn<PaymentTransaction, Void>, TableCell<PaymentTransaction, Void>> cellFactory = new Callback<>() {
            @Override
            public TableCell<PaymentTransaction, Void> call(final TableColumn<PaymentTransaction, Void> param) {
                final TableCell<PaymentTransaction, Void> cell = new TableCell<>() {
                    @Override
                    public void updateItem(Void item, boolean empty) {
                        super.updateItem(item, empty);
                        if (empty || getTableRow() == null || getTableRow().getItem() == null) {
                            setGraphic(null);
                        } else {
                            PaymentTransaction payment = getTableRow().getItem();
                            Button btn = new Button();

                            if ("Active".equals(payment.getStatus())) {
                                btn.setText("Deactivate");
                                btn.getStyleClass().add("deactivate-button");
                            } else {
                                btn.setText("Reactivate");
                                btn.getStyleClass().add("edit-button");
                            }

                            btn.setOnAction((ActionEvent event) -> {
                                PaymentTransaction currentPayment = getTableRow().getItem();
                                if (currentPayment != null) {
                                    handleDeactivateReactivate(currentPayment);
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

    private void handleDeactivateReactivate(PaymentTransaction payment) {
        String action = "Active".equals(payment.getStatus()) ? "deactivate" : "reactivate";

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmation");
        alert.setHeaderText("Confirm " + action);
        alert.setContentText("Are you sure you want to " + action + " payment: " + payment.getPaymentID() + "?");

        if (alert.showAndWait().get() == ButtonType.OK) {
            boolean success;
            if ("Active".equals(payment.getStatus())) {
                success = paymentDAO.deactivatePayment(payment.getPaymentID());
            } else {
                success = paymentDAO.reactivatePayment(payment.getPaymentID());
            }

            if (success) {
                showAlert(Alert.AlertType.INFORMATION, "Success", "Payment has been " + action + "d.");
                loadPaymentData();
            } else {
                showAlert(Alert.AlertType.ERROR, "Error", "Failed to update payment status.");
            }
        }
    }

    private void showAlert(Alert.AlertType type, String title, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}