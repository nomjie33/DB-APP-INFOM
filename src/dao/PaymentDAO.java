package dao;

import model.PaymentTransaction;
import util.DBConnection;
import java.sql.*;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object for PAYMENT table operations.
 * 
 * PURPOSE: Handles all database CRUD operations for payment table.
 * 
 * SCHEMA ALIGNMENT:
 * This DAO assumes the payment table has columns:
 * - paymentID   VARCHAR(11) (primary key)
 * - amount      DECIMAL(10,2)
 * - rentalID    VARCHAR(11) (foreign key)
 * - paymentDate DATE
 * 
 * METHODS IMPLEMENTED:
 * 1. insertPayment()       - INSERT new payment record
 * 2. updatePayment()       - UPDATE payment record
 * 3. deletePayment()       - DELETE payment record
 * 4. getPaymentById()      - SELECT payment by ID
 * 5. getAllPayments()      - SELECT all payments
 * 6. getPaymentsByRental() - SELECT all payments for a rental
 * 7. getPaymentsByDateRange() - SELECT payments within date range
 * 8. getTotalRevenueByDateRange() - SUM payments in date range
 * 
 * COLLABORATOR NOTES:
 * - Always use PreparedStatement to prevent SQL injection
 * - Close resources in try-with-resources for automatic cleanup
 * - Return null when record not found
 * - Handle SQLException by printing stack trace (or log in production)
 */
public class PaymentDAO {
    
    /**
     * Insert a new payment record into the database.
     * 
     * @param payment PaymentTransaction object to insert
     * @return true if insert successful, false otherwise
     */
    public boolean insertPayment(PaymentTransaction payment) {
        String sql = "INSERT INTO payment (paymentID, amount, rentalID, paymentDate) VALUES (?, ?, ?, ?)";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, payment.getPaymentID());
            stmt.setBigDecimal(2, payment.getAmount());
            stmt.setString(3, payment.getRentalID());
            stmt.setDate(4, payment.getPaymentDate());
            
            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
            
        } catch (SQLException e) {
            System.err.println("Error inserting payment record: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
    
    /**
     * Update an existing payment record.
     * 
     * @param payment PaymentTransaction object with updated data
     * @return true if update successful, false otherwise
     */
    public boolean updatePayment(PaymentTransaction payment) {
        String sql = "UPDATE payment SET amount = ?, rentalID = ?, paymentDate = ? WHERE paymentID = ?";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setBigDecimal(1, payment.getAmount());
            stmt.setString(2, payment.getRentalID());
            stmt.setDate(3, payment.getPaymentDate());
            stmt.setString(4, payment.getPaymentID());
            
            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
            
        } catch (SQLException e) {
            System.err.println("Error updating payment record: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
    
    /**
     * Delete a payment record by ID.
     * 
     * @param paymentID Payment ID to delete
     * @return true if delete successful, false otherwise
     */
    public boolean deletePayment(String paymentID) {
        String sql = "DELETE FROM payment WHERE paymentID = ?";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, paymentID);
            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
            
        } catch (SQLException e) {
            System.err.println("Error deleting payment record: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
    
    /**
     * Get a payment record by ID.
     * 
     * @param paymentID Payment ID to retrieve
     * @return PaymentTransaction object or null if not found
     */
    public PaymentTransaction getPaymentById(String paymentID) {
        String sql = "SELECT * FROM payment WHERE paymentID = ?";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, paymentID);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                return extractPaymentFromResultSet(rs);
            }
            
        } catch (SQLException e) {
            System.err.println("Error retrieving payment record: " + e.getMessage());
            e.printStackTrace();
        }
        
        return null;
    }
    
    /**
     * Get all payment records.
     * 
     * @return List of all PaymentTransaction objects
     */
    public List<PaymentTransaction> getAllPayments() {
        List<PaymentTransaction> paymentList = new ArrayList<>();
        String sql = "SELECT * FROM payment ORDER BY paymentDate DESC";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            
            while (rs.next()) {
                paymentList.add(extractPaymentFromResultSet(rs));
            }
            
        } catch (SQLException e) {
            System.err.println("Error retrieving all payment records: " + e.getMessage());
            e.printStackTrace();
        }
        
        return paymentList;
    }
    
    /**
     * Get all payments for a specific rental.
     * Multiple payments may exist per rental (e.g., deposit + final payment).
     * 
     * @param rentalID Rental ID to filter by
     * @return List of PaymentTransaction objects for the rental
     */
    public List<PaymentTransaction> getPaymentsByRental(String rentalID) {
        List<PaymentTransaction> paymentList = new ArrayList<>();
        String sql = "SELECT * FROM payment WHERE rentalID = ? ORDER BY paymentDate ASC";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, rentalID);
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                paymentList.add(extractPaymentFromResultSet(rs));
            }
            
        } catch (SQLException e) {
            System.err.println("Error retrieving payments by rental: " + e.getMessage());
            e.printStackTrace();
        }
        
        return paymentList;
    }
    
    /**
     * Get payments within a date range.
     * Useful for revenue reports and financial analysis.
     * 
     * @param startDate Start date (inclusive)
     * @param endDate End date (inclusive)
     * @return List of PaymentTransaction objects within the date range
     */
    public List<PaymentTransaction> getPaymentsByDateRange(Date startDate, Date endDate) {
        List<PaymentTransaction> paymentList = new ArrayList<>();
        String sql = "SELECT * FROM payment WHERE paymentDate BETWEEN ? AND ? ORDER BY paymentDate ASC";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setDate(1, startDate);
            stmt.setDate(2, endDate);
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                paymentList.add(extractPaymentFromResultSet(rs));
            }
            
        } catch (SQLException e) {
            System.err.println("Error retrieving payments by date range: " + e.getMessage());
            e.printStackTrace();
        }
        
        return paymentList;
    }
    
    /**
     * Get total revenue within a date range.
     * Sums all payment amounts for financial reporting.
     * 
     * @param startDate Start date (inclusive)
     * @param endDate End date (inclusive)
     * @return Total revenue as BigDecimal, or BigDecimal.ZERO if no payments
     */
    public BigDecimal getTotalRevenueByDateRange(Date startDate, Date endDate) {
        String sql = "SELECT SUM(amount) as total_revenue FROM payment WHERE paymentDate BETWEEN ? AND ?";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setDate(1, startDate);
            stmt.setDate(2, endDate);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                BigDecimal revenue = rs.getBigDecimal("total_revenue");
                return revenue != null ? revenue : BigDecimal.ZERO;
            }
            
        } catch (SQLException e) {
            System.err.println("Error calculating total revenue: " + e.getMessage());
            e.printStackTrace();
        }
        
        return BigDecimal.ZERO;
    }
    
    /**
     * Helper method to extract PaymentTransaction object from ResultSet.
     * 
     * @param rs ResultSet positioned at a payment record row
     * @return PaymentTransaction object
     * @throws SQLException if column access fails
     */
    private PaymentTransaction extractPaymentFromResultSet(ResultSet rs) throws SQLException {
        return new PaymentTransaction(
            rs.getString("paymentID"),
            rs.getBigDecimal("amount"),
            rs.getString("rentalID"),
            rs.getDate("paymentDate")
        );
    }
}
