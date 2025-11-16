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

        loadPaymentData();
        setupEditButtonColumn();
    }

    public void setMainController(Admin_dashboardController mainController) {
        this.mainController = mainController;
    }

    private void loadPaymentData() {
        List<PaymentTransaction> paymentList = paymentDAO.getAllPayments();
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
                    private final Button btn = new Button("Edit");
                    {
                        btn.getStyleClass().add("edit-button");
                        btn.setOnAction((ActionEvent event) -> {
                            PaymentTransaction payment = getTableView().getItems().get(getIndex());
                            handleEditPayment(payment);
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