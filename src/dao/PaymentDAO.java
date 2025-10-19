package dao;

import model.PaymentTransaction;
import java.sql.*;
import java.util.List;

/**
 * Data Access Object for PAYMENT TRANSACTION table operations.
 * 
 * PURPOSE: Handles all database CRUD operations for payments table.
 * 
 * METHODS TO IMPLEMENT:
 * 
 * 1. insertPayment(PaymentTransaction payment)
 *    - INSERT new payment record
 *    - Generate receipt number
 * 
 * 2. updatePayment(PaymentTransaction payment)
 *    - UPDATE payment record (e.g., refunds)
 * 
 * 3. deletePayment(int paymentId)
 *    - DELETE payment (rarely used)
 * 
 * 4. getPaymentById(int paymentId)
 *    - SELECT payment by ID
 * 
 * 5. getAllPayments()
 *    - SELECT all payments
 * 
 * 6. getPaymentsByRental(int rentalId)
 *    - SELECT all payments for a specific rental
 *    - May have multiple (deposit + final payment)
 * 
 * 7. getPaymentsByDateRange(Date startDate, Date endDate)
 *    - SELECT payments within date range
 *    - For revenue reports
 * 
 * 8. getPaymentsByMethod(String paymentMethod)
 *    - SELECT payments by method
 *    - For payment analytics
 * 
 * 9. getTotalRevenueByDateRange(Date startDate, Date endDate)
 *    - SUM all payments in date range
 *    - For financial reports
 * 
 * COLLABORATOR NOTES:
 * - Link to RentalTransaction for payment tracking
 * - Generate unique receipt numbers
 * - Used heavily in revenue reports
 */
public class PaymentDAO {
    
    // TODO: Implement insertPayment(PaymentTransaction payment)
    
    // TODO: Implement updatePayment(PaymentTransaction payment)
    
    // TODO: Implement deletePayment(int paymentId)
    
    // TODO: Implement getPaymentById(int paymentId)
    
    // TODO: Implement getAllPayments()
    
    // TODO: Implement getPaymentsByRental(int rentalId)
    
    // TODO: Implement getPaymentsByDateRange(Date startDate, Date endDate)
    
    // TODO: Implement getPaymentsByMethod(String paymentMethod)
    
    // TODO: Implement getTotalRevenueByDateRange(Date startDate, Date endDate)
}
