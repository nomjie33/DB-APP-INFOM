package main.gui;

import dao.PaymentDAO;
import dao.RentalDAO;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.StringConverter;
import model.PaymentTransaction;
import model.RentalTransaction;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Objects;

public class Admin_paymentFormController {

    @FXML private Label formHeaderLabel;
    @FXML private TextField idField;
    @FXML private ComboBox<RentalTransaction> rentalComboBox;
    @FXML private TextField amountField;
    @FXML private DatePicker paymentDatePicker;

    private Admin_dashboardController mainController;
    private PaymentDAO paymentDAO = new PaymentDAO();
    private RentalDAO rentalDAO = new RentalDAO();

    private boolean isUpdatingRecord = false;
    private PaymentTransaction currentPayment;

    @FXML
    public void initialize() {

        loadRentalComboBox();
    }

    public void setMainController(Admin_dashboardController mainController) {
        this.mainController = mainController;
    }

    public void setPaymentData(PaymentTransaction payment) {



        idField.setDisable(false);
        rentalComboBox.setDisable(false);
        amountField.setDisable(false);
        paymentDatePicker.setDisable(false);
        idField.getStyleClass().remove("form-text-field-disabled");
        rentalComboBox.getStyleClass().remove("form-text-field-disabled");

        if (payment != null) {

            isUpdatingRecord = true;
            currentPayment = payment;
            formHeaderLabel.setText("Update Payment");

            idField.setText(payment.getPaymentID());
            amountField.setText(payment.getAmount().toPlainString());
            if (payment.getPaymentDate() != null) {
                paymentDatePicker.setValue(payment.getPaymentDate().toLocalDate());
            }

            RentalTransaction paymentRental = findRentalInList(payment.getRentalID());
            rentalComboBox.setValue(paymentRental);

            idField.setDisable(true);
            idField.getStyleClass().add("form-text-field-disabled");
            rentalComboBox.setDisable(true);
            rentalComboBox.getStyleClass().add("form-text-field-disabled");

        } else {
            isUpdatingRecord = false;
            formHeaderLabel.setText("New Payment");

            idField.clear();
            rentalComboBox.setValue(null);
            amountField.clear();
            paymentDatePicker.setValue(null);
        }
    }

    @FXML private void handleSave() {
        if (!validateFields()) return;

        if (isUpdatingRecord) {

            BigDecimal newAmount = new BigDecimal(amountField.getText().trim());
            LocalDate newDate = paymentDatePicker.getValue();

            BigDecimal oldAmount = currentPayment.getAmount();
            LocalDate oldDate = (currentPayment.getPaymentDate() != null)
                    ? currentPayment.getPaymentDate().toLocalDate()
                    : null;

            boolean amountChanged = oldAmount.compareTo(newAmount) != 0;
            boolean dateChanged = !Objects.equals(oldDate, newDate);

            if (!amountChanged && !dateChanged) {
                showAlert(AlertType.INFORMATION, "No Changes", "No changes were detected.");
                return;
            }
        }

        try {
            String paymentID = idField.getText().trim();

            RentalTransaction selectedRental = rentalComboBox.getValue();
            String rentalID = selectedRental.getRentalID();

            BigDecimal amount = new BigDecimal(amountField.getText().trim());
            LocalDate paymentDate = paymentDatePicker.getValue();

            PaymentTransaction payment = isUpdatingRecord ? currentPayment : new PaymentTransaction();
            payment.setPaymentID(paymentID);
            payment.setRentalID(rentalID);
            payment.setAmount(amount);
            payment.setPaymentDate(paymentDate != null ? java.sql.Date.valueOf(paymentDate) : null);

            if (!isUpdatingRecord) {
                payment.setStatus("Active"); // Or "Completed", "Paid", etc.
            }

            boolean isSuccessful = isUpdatingRecord
                    ? paymentDAO.updatePayment(payment)
                    : paymentDAO.insertPayment(payment);

            if (isSuccessful) {
                if (isUpdatingRecord){
                    showAlert(AlertType.INFORMATION, "Save Successful", "Payment record saved successfully.");
                } else {
                    String title = "Payment Recorded!";
                    String content = "A new payment has been successfully recorded.\n\n" +
                            "Payment ID:\n" + payment.getPaymentID() + "\n\n" +
                            "Rental ID:\n" + payment.getRentalID() + "\n\n" +
                            "Amount:\n₱" + String.format("%.2f", payment.getAmount()) + "\n\n" +
                            "Payment Date:\n" + payment.getPaymentDate().toString();

                    showConfirmationDialog(title, content);
                }
                mainController.loadPage("Admin-paymentRecords.fxml"); // go back to records
            } else {
                showAlert(AlertType.ERROR, "Error", "Failed to save payment record.");
            }

        } catch (NumberFormatException e) {
            showAlert(AlertType.ERROR, "Invalid Amount", "Amount must be a valid number (e.g., 1500.00).");
        } catch (NullPointerException e) {
            showAlert(AlertType.ERROR, "Validation Error", "Please select a rental from the dropdown.");
            e.printStackTrace();
        } catch (Exception e) {
            showAlert(AlertType.ERROR, "Error", "An unexpected error occurred during save.");
            e.printStackTrace();
        }
    }

    @FXML private void handleCancel() {
        mainController.loadPage("Admin-paymentRecords.fxml");
    }

    private boolean validateFields() {
        if (idField.getText().trim().isEmpty() ||
                rentalComboBox.getValue() == null ||
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

    private void loadRentalComboBox() {
        try {

            List<RentalTransaction> rentals = rentalDAO.getAllRentals();
            ObservableList<RentalTransaction> rentalList = FXCollections.observableArrayList(rentals);
            rentalComboBox.setItems(rentalList);

            rentalComboBox.setConverter(new StringConverter<RentalTransaction>() {
                @Override
                public String toString(RentalTransaction rental) {
                    if (rental == null) {
                        return null;
                    }

                    return String.format("%s (Customer: %s, Vehicle: %s)",
                            rental.getRentalID(),
                            rental.getCustomerID(),
                            rental.getPlateID()
                    );
                }

                @Override
                public RentalTransaction fromString(String string) {
                    return null;
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
            showAlert(AlertType.ERROR, "Load Error", "Failed to load rental list.");
        }
    }

    private RentalTransaction findRentalInList(String rentalID) {
        if (rentalID == null) return null;
        for (RentalTransaction r : rentalComboBox.getItems()) {
            if (r.getRentalID().equals(rentalID)) {
                return r;
            }
        }
        return rentalDAO.getRentalById(rentalID);
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
}