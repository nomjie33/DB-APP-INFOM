package model;

/**
 * Entity class representing a PAYMENT TRANSACTION in the database.
 * 
 * PURPOSE: Maps to the 'payment' table in MySQL database.
 * 
 * SCHEMA:
 * - paymentID   : String (primary key, VARCHAR(11))
 * - amount      : java.math.BigDecimal (payment amount, DECIMAL(10,2))
 * - rentalID    : String (foreign key to Rental, VARCHAR(11))
 * - paymentDate : java.sql.Date (when payment was made)
 * - status      : String (Active/Inactive for soft delete, VARCHAR(15))
 * 
 * RELATIONSHIP:
 * - Many-to-one with RentalTransaction
 */
import java.math.BigDecimal;
import java.sql.Date;

public class PaymentTransaction {
    private String paymentID;
    private BigDecimal amount;
    private String rentalID;
    private Date paymentDate;
    private String status;

    // Default constructor
    public PaymentTransaction() {
        this.status = "Active";
    }

    // Constructor without status (defaults to Active)
    public PaymentTransaction(String paymentID, BigDecimal amount, String rentalID, Date paymentDate) {
        this.paymentID = paymentID;
        this.amount = amount;
        this.rentalID = rentalID;
        this.paymentDate = paymentDate;
        this.status = "Active";
    }

    // Constructor with status
    public PaymentTransaction(String paymentID, BigDecimal amount, String rentalID, Date paymentDate, String status) {
        this.paymentID = paymentID;
        this.amount = amount;
        this.rentalID = rentalID;
        this.paymentDate = paymentDate;
        this.status = status;
    }

    // Getters and setters
    public String getPaymentID() {
        return paymentID;
    }

    public void setPaymentID(String paymentID) {
        this.paymentID = paymentID;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public String getRentalID() {
        return rentalID;
    }

    public void setRentalID(String rentalID) {
        this.rentalID = rentalID;
    }

    public Date getPaymentDate() {
        return paymentDate;
    }

    public void setPaymentDate(Date paymentDate) {
        this.paymentDate = paymentDate;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public boolean isActive() {
        return "Active".equals(status);
    }

    public boolean isInactive() {
        return "Inactive".equals(status);
    }

    @Override
    public String toString() {
        return "PaymentTransaction{" +
                "paymentID='" + paymentID + '\'' +
                ", amount=" + amount +
                ", rentalID='" + rentalID + '\'' +
                ", paymentDate=" + paymentDate +
                ", status='" + status + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PaymentTransaction that = (PaymentTransaction) o;
        return paymentID != null && paymentID.equals(that.paymentID);
    }

    @Override
    public int hashCode() {
        return paymentID != null ? paymentID.hashCode() : 0;
    }
}
