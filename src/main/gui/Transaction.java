// File: Transaction.java (in your Client Repo: com.uvr.launch.uvr)
package main.gui;
import javafx.beans.property.SimpleStringProperty;

public class Transaction {
    private final SimpleStringProperty date;
    private final SimpleStringProperty transactionId;
    private final SimpleStringProperty category;

    public Transaction(String date, String transactionId, String category) {
        this.date = new SimpleStringProperty(date);
        this.transactionId = new SimpleStringProperty(transactionId);
        this.category = new SimpleStringProperty(category);
    }

    // Getters
    public String getDate() { return date.get(); }
    public String getTransactionId() { return transactionId.get(); }
    public String getCategory() { return category.get(); }
}