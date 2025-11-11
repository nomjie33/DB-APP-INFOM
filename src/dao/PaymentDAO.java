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
 * Uses SOFT DELETE pattern - records are marked inactive instead of being deleted.
 * 
 * SCHEMA ALIGNMENT:
 * This DAO assumes the payment table has columns:
 * - paymentID   VARCHAR(11) (primary key)
 * - amount      DECIMAL(10,2)
 * - rentalID    VARCHAR(11) (foreign key)
 * - paymentDate DATE
 * - status      VARCHAR(15) DEFAULT 'Active'
 * 
 * SOFT DELETE IMPLEMENTATION:
 * - deactivatePayment() sets status to 'Inactive' instead of DELETE
 * - All retrieval methods filter WHERE status = 'Active' by default
 * - IncludingInactive methods available for historical data access
 * - NO HARD DELETE METHODS - only soft delete (deactivate) is supported
 * 
 * METHODS IMPLEMENTED:
 * 1. insertPayment()       - INSERT new payment record (status defaults to 'Active')
 * 2. updatePayment()       - UPDATE payment record (only active)
 * 3. deactivatePayment()   - SOFT DELETE (sets status to 'Inactive')
 * 4. reactivatePayment()   - Sets status back to 'Active'
 * 5. getPaymentById()      - SELECT active payment by ID
 * 6. getPaymentByIdIncludingInactive() - SELECT regardless of status
 * 7. getAllPayments()      - SELECT all active payments
 * 8. getAllPaymentsIncludingInactive() - SELECT all regardless of status
 * 9. getPaymentsByRental() - SELECT active payments for a rental
 * 10. getPaymentsByRentalIncludingInactive() - SELECT all payments for rental
 * 11. getPaymentsByDateRange() - SELECT active payments within date range
 * 12. getTotalRevenueByDateRange() - SUM active payments in date range
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
     * Status defaults to 'Active' in the model constructor.
     * 
     * @param payment PaymentTransaction object to insert
     * @return true if insert successful, false otherwise
     */
    public boolean insertPayment(PaymentTransaction payment) {
        String sql = "INSERT INTO payments (paymentID, amount, rentalID, paymentDate, status) VALUES (?, ?, ?, ?, ?)";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, payment.getPaymentID());
            stmt.setBigDecimal(2, payment.getAmount());
            stmt.setString(3, payment.getRentalID());
            stmt.setDate(4, payment.getPaymentDate());
            stmt.setString(5, payment.getStatus() != null ? payment.getStatus() : "Active");
            
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
     * Only updates active records (status = 'Active').
     * 
     * @param payment PaymentTransaction object with updated data
     * @return true if update successful, false otherwise
     */
    public boolean updatePayment(PaymentTransaction payment) {
        String sql = "UPDATE payments SET amount = ?, rentalID = ?, paymentDate = ? " +
                     "WHERE paymentID = ? AND status = 'Active'";
        
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
     * SOFT DELETE: Deactivate a payment record by setting status to 'Inactive'.
     * The record remains in the database for historical and audit purposes.
     * 
     * @param paymentID Payment ID to deactivate
     * @return true if deactivation successful, false otherwise
     */
    public boolean deactivatePayment(String paymentID) {
        String sql = "UPDATE payments SET status = 'Inactive' WHERE paymentID = ?";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, paymentID);
            int rowsAffected = stmt.executeUpdate();
            
            if (rowsAffected > 0) {
                System.out.println("Payment " + paymentID + " has been deactivated");
                return true;
            }
            
        } catch (SQLException e) {
            System.err.println("Error deactivating payment record: " + e.getMessage());
            e.printStackTrace();
        }
        
        return false;
    }
    
    /**
     * Reactivate a previously deactivated payment record.
     * Sets status back to 'Active'.
     * 
     * @param paymentID Payment ID to reactivate
     * @return true if reactivation successful, false otherwise
     */
    public boolean reactivatePayment(String paymentID) {
        String sql = "UPDATE payments SET status = 'Active' WHERE paymentID = ?";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, paymentID);
            int rowsAffected = stmt.executeUpdate();
            
            if (rowsAffected > 0) {
                System.out.println("Payment " + paymentID + " has been reactivated");
                return true;
            }
            
        } catch (SQLException e) {
            System.err.println("Error reactivating payment record: " + e.getMessage());
            e.printStackTrace();
        }
        
        return false;
    }
    
    /**
     * Get an active payment record by ID.
     * Only returns payments with status = 'Active'.
     * 
     * @param paymentID Payment ID to retrieve
     * @return PaymentTransaction object or null if not found or inactive
     */
    public PaymentTransaction getPaymentById(String paymentID) {
        String sql = "SELECT * FROM payments WHERE paymentID = ? AND status = 'Active'";
        
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
     * Get a payment record by ID including inactive records.
     * Used for historical lookups and audit purposes.
     * 
     * @param paymentID Payment ID to retrieve
     * @return PaymentTransaction object or null if not found
     */
    public PaymentTransaction getPaymentByIdIncludingInactive(String paymentID) {
        String sql = "SELECT * FROM payments WHERE paymentID = ?";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, paymentID);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                return extractPaymentFromResultSet(rs);
            }
            
        } catch (SQLException e) {
            System.err.println("Error retrieving payment record (including inactive): " + e.getMessage());
            e.printStackTrace();
        }
        
        return null;
    }
    
    /**
     * Get all active payment records.
     * Only returns payments with status = 'Active'.
     * 
     * @return List of active PaymentTransaction objects
     */
    public List<PaymentTransaction> getAllPayments() {
        List<PaymentTransaction> paymentList = new ArrayList<>();
        String sql = "SELECT * FROM payments WHERE status = 'Active' ORDER BY paymentDate DESC";
        
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
     * Get all payment records including inactive ones.
     * Used for historical analysis and reporting.
     * 
     * @return List of all PaymentTransaction objects regardless of status
     */
    public List<PaymentTransaction> getAllPaymentsIncludingInactive() {
        List<PaymentTransaction> paymentList = new ArrayList<>();
        String sql = "SELECT * FROM payments ORDER BY paymentDate DESC";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            
            while (rs.next()) {
                paymentList.add(extractPaymentFromResultSet(rs));
            }
            
        } catch (SQLException e) {
            System.err.println("Error retrieving all payment records (including inactive): " + e.getMessage());
            e.printStackTrace();
        }
        
        return paymentList;
    }
    
    /**
     * Get all active payments for a specific rental.
     * Multiple payments may exist per rental (e.g., deposit + final payment).
     * Only returns payments with status = 'Active'.
     * 
     * @param rentalID Rental ID to filter by
     * @return List of active PaymentTransaction objects for the rental
     */
    public List<PaymentTransaction> getPaymentsByRental(String rentalID) {
        List<PaymentTransaction> paymentList = new ArrayList<>();
        String sql = "SELECT * FROM payments WHERE rentalID = ? AND status = 'Active' ORDER BY paymentDate ASC";
        
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
     * Get all payments for a specific rental including inactive ones.
     * Used for historical payment tracking and auditing.
     * 
     * @param rentalID Rental ID to filter by
     * @return List of all PaymentTransaction objects for the rental
     */
    public List<PaymentTransaction> getPaymentsByRentalIncludingInactive(String rentalID) {
        List<PaymentTransaction> paymentList = new ArrayList<>();
        String sql = "SELECT * FROM payments WHERE rentalID = ? ORDER BY paymentDate ASC";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, rentalID);
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                paymentList.add(extractPaymentFromResultSet(rs));
            }
            
        } catch (SQLException e) {
            System.err.println("Error retrieving payments by rental (including inactive): " + e.getMessage());
            e.printStackTrace();
        }
        
        return paymentList;
    }
    
    /**
     * Get active payments within a date range.
     * Useful for revenue reports and financial analysis.
     * Only returns payments with status = 'Active'.
     * 
     * @param startDate Start date (inclusive)
     * @param endDate End date (inclusive)
     * @return List of active PaymentTransaction objects within the date range
     */
    public List<PaymentTransaction> getPaymentsByDateRange(Date startDate, Date endDate) {
        List<PaymentTransaction> paymentList = new ArrayList<>();
        String sql = "SELECT * FROM payments WHERE paymentDate BETWEEN ? AND ? AND status = 'Active' ORDER BY paymentDate ASC";
        
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
     * Get total revenue within a date range from active payments.
     * Sums all payment amounts for financial reporting.
     * Only includes payments with status = 'Active'.
     * 
     * @param startDate Start date (inclusive)
     * @param endDate End date (inclusive)
     * @return Total revenue as BigDecimal, or BigDecimal.ZERO if no payments
     */
    public BigDecimal getTotalRevenueByDateRange(Date startDate, Date endDate) {
        String sql = "SELECT SUM(amount) as total_revenue FROM payments " +
                     "WHERE paymentDate BETWEEN ? AND ? AND status = 'Active'";
        
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
            rs.getDate("paymentDate"),
            rs.getString("status")
        );
    }
}
