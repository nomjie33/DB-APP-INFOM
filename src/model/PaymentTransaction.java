package model;

/**
 * Entity class representing a PAYMENT TRANSACTION in the database.
 * 
 * PURPOSE: Maps to the 'payments' table in MySQL database.
 * 
 * FIELDS TO IMPLEMENT:
 * - paymentId (int/String) - Primary key
 * - rentalId (int/String) - Foreign key to RentalTransaction
 * - paymentDate (Date/Timestamp) - When payment was made
 * - amount (double) - Payment amount
 * - paymentMethod (String) - "Cash", "Credit Card", "Debit Card", "Online"
 * - status (String) - "Paid", "Pending", "Refunded"
 * - receiptNumber (String) - Receipt/transaction reference
 * 
 * METHODS TO IMPLEMENT:
 * - Constructor(s)
 * - Getters and Setters for all fields
 * - toString() for debugging
 * - equals() and hashCode()
 * 
 * COLLABORATOR NOTES:
 * - Links to RentalTransaction for payment tracking
 * - Multiple payments possible per rental (deposit + final payment)
 */
public class PaymentTransaction {
    // TODO: Add private fields for payment transaction attributes
    
    // TODO: Add constructors (default and parameterized)
    
    // TODO: Add getters and setters
    
    // TODO: Add toString(), equals(), hashCode()
}
