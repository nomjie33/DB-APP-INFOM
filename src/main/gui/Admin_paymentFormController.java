package main.gui;

import dao.PaymentDAO;
import model.PaymentTransaction;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.Alert.AlertType;

import java.math.BigDecimal;
import java.time.LocalDate;

public class Admin_paymentFormController {

    @FXML private Label formHeaderLabel;
    @FXML private TextField idField;
    @FXML private TextField rentalIDField;
    @FXML private TextField amountField;
    @FXML private DatePicker paymentDatePicker;

    private Admin_dashboardController mainController;
    private PaymentDAO paymentDAO = new PaymentDAO();

    private boolean isUpdatingRecord = false;
    private PaymentTransaction currentPayment;

    public void setMainController(Admin_dashboardController mainController) {
        this.mainController = mainController;
    }

    public void setPaymentData(PaymentTransaction payment) {
        if (payment != null) {
            isUpdatingRecord = true;
            currentPayment = payment;

            formHeaderLabel.setText("Update Payment");

            idField.setText(payment.getPaymentID());
            rentalIDField.setText(payment.getRentalID());
            amountField.setText(payment.getAmount().toPlainString());
            if (payment.getPaymentDate() != null) {
                paymentDatePicker.setValue(payment.getPaymentDate().toLocalDate());
            }

            idField.setDisable(true);
        }
    }

    @FXML
    private void handleSave() {
        if (!validateFields()) return;

        try {
            String paymentID = idField.getText().trim();
            String rentalID = rentalIDField.getText().trim();
            BigDecimal amount = new BigDecimal(amountField.getText().trim());
            LocalDate paymentDate = paymentDatePicker.getValue();

            PaymentTransaction payment = isUpdatingRecord ? currentPayment : new PaymentTransaction();
            payment.setPaymentID(paymentID);
            payment.setRentalID(rentalID);
            payment.setAmount(amount);
            payment.setPaymentDate(paymentDate != null ? java.sql.Date.valueOf(paymentDate) : null);

            boolean isSuccessful = isUpdatingRecord
                    ? paymentDAO.updatePayment(payment)
                    : paymentDAO.insertPayment(payment);

            if (isSuccessful) {
                showAlert(AlertType.INFORMATION, "Save Successful", "Payment record saved successfully.");
                mainController.loadPage("Admin_paymentRecords.fxml"); // go back to records
            } else {
                showAlert(AlertType.ERROR, "Error", "Failed to save payment record.");
            }

        } catch (NumberFormatException e) {
            showAlert(AlertType.ERROR, "Invalid Amount", "Amount must be a valid number (e.g., 1500.00).");
        } catch (Exception e) {
            showAlert(AlertType.ERROR, "Error", "An unexpected error occurred during save.");
            e.printStackTrace();
        }
    }

    @FXML
    private void handleCancel() {
        mainController.loadPage("Admin_paymentRecords.fxml");
    }

    private boolean validateFields() {
        if (idField.getText().trim().isEmpty() ||
                rentalIDField.getText().trim().isEmpty() ||
                amountField.getText().trim().isEmpty() ||
                paymentDatePicker.getValue() == null) {
            showAlert(AlertType.WARNING, "Validation Error", "Please fill in all required fields.");
            return false;
        }

        try {
            new BigDecimal(amountField.getText().trim());
        } catch (NumberFormatException e) {
            showAlert(AlertType.ERROR, "Invalid Amount", "Amount must be a valid number (e.g., 1500.00).");
            return false;
        }

        return true;
    }

    private void showAlert(AlertType type, String title, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}
