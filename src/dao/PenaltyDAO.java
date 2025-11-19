package dao;

import model.MaintenanceCheque;
import model.PenaltyTransaction;
import util.DBConnection;
import java.sql.*;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object for PENALTY table operations.
 * 
 * PURPOSE: Handles all database CRUD operations for penalty table.
 * 
 * SCHEMA ALIGNMENT:
 * This DAO assumes the penalty table has columns:
 * - penaltyID      VARCHAR(11) (primary key)
 * - rentalID       VARCHAR(25) (foreign key)
 * - totalPenalty   DECIMAL(10,2)
 * - penaltyStatus  VARCHAR(15) (payment status: PAID/UNPAID/WAIVED)
 * - maintenanceID  VARCHAR(11) (foreign key)
 * - dateIssued     DATE
 * - status         VARCHAR(15) (record status: Active/Inactive for soft delete)
 * 
 * METHODS IMPLEMENTED:
 * 1. insertPenalty()       - INSERT new penalty record
 * 2. updatePenalty()       - UPDATE penalty record
 * 3. deactivatePenalty()   - SOFT DELETE penalty record (sets status to Inactive)
 * 4. reactivatePenalty()   - RESTORE soft deleted penalty (sets status to Active)
 * 5. getPenaltyById()      - SELECT active penalty by ID
 * 6. getAllPenalties()     - SELECT all active penalties
 * 7. getPenaltiesByRental() - SELECT active penalties for a rental
 * 8. getPenaltiesByPaymentStatus() - SELECT penalties by payment status
 * 9. getPenaltiesByMaintenance() - SELECT active penalties linked to maintenance
 * 10. getTotalPenaltiesByRental() - SUM active penalties for a rental
 * 11. getPenaltiesByDateRange() - SELECT active penalties within date range
 * 12. getPenaltyByIdIncludingInactive() - SELECT penalty including inactive records
 * 
 * SOFT DELETE APPROACH:
 * - All query methods filter by status='Active' by default
 * - Use deactivatePenalty() instead of deletePenalty() for normal operations
 * - Use *IncludingInactive() methods for historical/reporting purposes
 * 
 * COLLABORATOR NOTES:
 * - Always use PreparedStatement to prevent SQL injection
 * - Close resources in try-with-resources for automatic cleanup
 * - Return null when record not found
 * - Handle SQLException by printing stack trace (or log in production)
 */
public class PenaltyDAO {
    
    /**
     * Insert a new penalty record into the database.
     * 
     * @param penalty PenaltyTransaction object to insert
     * @return true if insert successful, false otherwise
     */
    public boolean insertPenalty(PenaltyTransaction penalty) {
        String sql = "INSERT INTO penalty (penaltyID, rentalID, totalPenalty, penaltyStatus, maintenanceID, dateIssued, status) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?)";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, penalty.getPenaltyID());
            stmt.setString(2, penalty.getRentalID());
            stmt.setBigDecimal(3, penalty.getTotalPenalty());
            stmt.setString(4, penalty.getPenaltyStatus());
            stmt.setString(5, penalty.getMaintenanceID());
            stmt.setDate(6, penalty.getDateIssued());
            stmt.setString(7, penalty.getStatus() != null ? penalty.getStatus() : "Active");
            
            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
            
        } catch (SQLException e) {
            System.err.println("Error inserting penalty record: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
    
    /**
     * Update an existing penalty record.
     * 
     * @param penalty PenaltyTransaction object with updated data
     * @return true if update successful, false otherwise
     */
    public boolean updatePenalty(PenaltyTransaction penalty) {
        String sql = "UPDATE penalty SET rentalID = ?, totalPenalty = ?, penaltyStatus = ?, " +
                     "maintenanceID = ?, dateIssued = ?, status = ? WHERE penaltyID = ?";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, penalty.getRentalID());
            stmt.setBigDecimal(2, penalty.getTotalPenalty());
            stmt.setString(3, penalty.getPenaltyStatus());
            stmt.setString(4, penalty.getMaintenanceID());
            stmt.setDate(5, penalty.getDateIssued());
            stmt.setString(6, penalty.getStatus() != null ? penalty.getStatus() : "Active");
            stmt.setString(7, penalty.getPenaltyID());
            
            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
            
        } catch (SQLException e) {
            System.err.println("Error updating penalty record: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
    
    /**
     * Deactivate a penalty record (SOFT DELETE).
     * Sets the status to 'Inactive' without removing from database.
     * This is the ONLY method for removing penalties - hard deletes are not permitted.
     * 
     * @param penaltyID Penalty ID to deactivate
     * @return true if deactivation successful, false otherwise
     */
    public boolean deactivatePenalty(String penaltyID) {
        String sql = "UPDATE penalty SET status = 'Inactive' WHERE penaltyID = ?";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, penaltyID);
            int rowsAffected = stmt.executeUpdate();
            
            if (rowsAffected > 0) {
                System.out.println("Penalty " + penaltyID + " has been deactivated (soft deleted)");
            }
            
            return rowsAffected > 0;
            
        } catch (SQLException e) {
            System.err.println("Error deactivating penalty record: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Reactivate a penalty record (RESTORE SOFT DELETE).
     * Sets the status back to 'Active' to restore a previously deactivated penalty.
     * Useful for correcting mistakes or reinstating voided penalties.
     * 
     * @param penaltyID Penalty ID to reactivate
     * @return true if reactivation successful, false otherwise
     */
    public boolean reactivatePenalty(String penaltyID) {
        String sql = "UPDATE penalty SET status = 'Active' WHERE penaltyID = ?";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, penaltyID);
            int rowsAffected = stmt.executeUpdate();
            
            if (rowsAffected > 0) {
                System.out.println("Penalty " + penaltyID + " has been reactivated");
            }
            
            return rowsAffected > 0;
            
        } catch (SQLException e) {
            System.err.println("Error reactivating penalty record: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
    
    /**
     * Get an active penalty record by ID.
     * Only returns penalties with status='Active'.
     * 
     * @param penaltyID Penalty ID to retrieve
     * @return PenaltyTransaction object or null if not found or inactive
     */
    public PenaltyTransaction getPenaltyById(String penaltyID) {
        String sql = "SELECT * FROM penalty WHERE penaltyID = ? AND status = 'Active'";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, penaltyID);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                return extractPenaltyFromResultSet(rs);
            }
            
        } catch (SQLException e) {
            System.err.println("Error retrieving penalty record: " + e.getMessage());
            e.printStackTrace();
        }
        
        return null;
    }

    /**
     * Get a penalty record by ID including inactive records.
     * Used for historical/reporting purposes.
     * 
     * @param penaltyID Penalty ID to retrieve
     * @return PenaltyTransaction object or null if not found
     */
    public PenaltyTransaction getPenaltyByIdIncludingInactive(String penaltyID) {
        String sql = "SELECT * FROM penalty WHERE penaltyID = ?";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, penaltyID);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                return extractPenaltyFromResultSet(rs);
            }
            
        } catch (SQLException e) {
            System.err.println("Error retrieving penalty record: " + e.getMessage());
            e.printStackTrace();
        }
        
        return null;
    }
    
    /**
     * Get all active penalty records.
     * Only returns penalties with status='Active'.
     * 
     * @return List of all active PenaltyTransaction objects
     */
    public List<PenaltyTransaction> getAllPenalties() {
        List<PenaltyTransaction> penaltyList = new ArrayList<>();
        String sql = "SELECT * FROM penalty WHERE status = 'Active' ORDER BY dateIssued DESC";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            
            while (rs.next()) {
                penaltyList.add(extractPenaltyFromResultSet(rs));
            }
            
        } catch (SQLException e) {
            System.err.println("Error retrieving all penalty records: " + e.getMessage());
            e.printStackTrace();
        }
        
        return penaltyList;
    }
    
    /**
     * Get all active penalties for a specific rental.
     * Only returns penalties with status='Active'.
     * 
     * @param rentalID Rental ID to filter by
     * @return List of active PenaltyTransaction objects for the rental
     */
    public List<PenaltyTransaction> getPenaltiesByRental(String rentalID) {
        List<PenaltyTransaction> penaltyList = new ArrayList<>();
        String sql = "SELECT * FROM penalty WHERE rentalID = ? AND status = 'Active' ORDER BY dateIssued DESC";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, rentalID);
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                penaltyList.add(extractPenaltyFromResultSet(rs));
            }
            
        } catch (SQLException e) {
            System.err.println("Error retrieving penalties by rental: " + e.getMessage());
            e.printStackTrace();
        }
        
        return penaltyList;
    }
    
    /**
     * Get active penalties by payment status.
     * Useful for filtering Paid, Unpaid, or Waived penalties.
     * Only returns penalties with status='Active'.
     * 
     * @param penaltyStatus Payment status to filter by (e.g., "PAID", "UNPAID", "WAIVED")
     * @return List of active PenaltyTransaction objects with matching payment status
     */
    public List<PenaltyTransaction> getPenaltiesByPaymentStatus(String penaltyStatus) {
        List<PenaltyTransaction> penaltyList = new ArrayList<>();
        String sql = "SELECT * FROM penalty WHERE penaltyStatus = ? AND status = 'Active' ORDER BY dateIssued DESC";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, penaltyStatus);
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                penaltyList.add(extractPenaltyFromResultSet(rs));
            }
            
        } catch (SQLException e) {
            System.err.println("Error retrieving penalties by payment status: " + e.getMessage());
            e.printStackTrace();
        }
        
        return penaltyList;
    }
    
    /**
     * Get active penalties linked to a specific maintenance record.
     * Useful for tracking damage-related penalties.
     * Only returns penalties with status='Active'.
     * 
     * @param maintenanceID Maintenance ID to filter by
     * @return List of active PenaltyTransaction objects linked to the maintenance
     */
    public List<PenaltyTransaction> getPenaltiesByMaintenance(String maintenanceID) {
        List<PenaltyTransaction> penaltyList = new ArrayList<>();
        String sql = "SELECT * FROM penalty WHERE maintenanceID = ? AND status = 'Active' ORDER BY dateIssued DESC";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, maintenanceID);
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                penaltyList.add(extractPenaltyFromResultSet(rs));
            }
            
        } catch (SQLException e) {
            System.err.println("Error retrieving penalties by maintenance: " + e.getMessage());
            e.printStackTrace();
        }
        
        return penaltyList;
    }
    
    /**
     * Get total active penalties for a specific rental.
     * Sums all active penalty amounts for the rental.
     * Only includes penalties with status='Active'.
     * 
     * @param rentalID Rental ID to calculate total for
     * @return Total penalty amount as BigDecimal, or BigDecimal.ZERO if no penalties
     */
    public BigDecimal getTotalPenaltiesByRental(String rentalID) {
        String sql = "SELECT SUM(totalPenalty) as total FROM penalty WHERE rentalID = ? AND status = 'Active'";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, rentalID);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                BigDecimal total = rs.getBigDecimal("total");
                return total != null ? total : BigDecimal.ZERO;
            }
            
        } catch (SQLException e) {
            System.err.println("Error calculating total penalties: " + e.getMessage());
            e.printStackTrace();
        }
        
        return BigDecimal.ZERO;
    }

    public List<PenaltyTransaction> getAllPenaltiesIncludingInactive(){
        List<PenaltyTransaction> penaltyList = new ArrayList<>();
        String sql = "SELECT * FROM penalty ORDER BY dateIssued DESC";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                penaltyList.add(extractPenaltyFromResultSet(rs));
            }

        } catch (SQLException e) {
            System.err.println("Error retrieving all penalties (including inactive): " + e.getMessage());
            e.printStackTrace();
        }

        return penaltyList;
    }
    
    /**
     * Get active penalties within a date range.
     * Useful for penalty reports and analysis.
     * Only returns penalties with status='Active'.
     * 
     * @param startDate Start date (inclusive)
     * @param endDate End date (inclusive)
     * @return List of active PenaltyTransaction objects within the date range
     */
    public List<PenaltyTransaction> getPenaltiesByDateRange(Date startDate, Date endDate) {
        List<PenaltyTransaction> penaltyList = new ArrayList<>();
        String sql = "SELECT * FROM penalty WHERE dateIssued BETWEEN ? AND ? AND status = 'Active' ORDER BY dateIssued ASC";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setDate(1, startDate);
            stmt.setDate(2, endDate);
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                penaltyList.add(extractPenaltyFromResultSet(rs));
            }
            
        } catch (SQLException e) {
            System.err.println("Error retrieving penalties by date range: " + e.getMessage());
            e.printStackTrace();
        }
        
        return penaltyList;
    }

    public List<PenaltyTransaction> getPenaltiesByStatus(String status) {
        List<PenaltyTransaction> penaltyList = new ArrayList<>();
        String sql = "SELECT * FROM penalty WHERE status = ? ORDER BY dateIssued DESC";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, status);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                penaltyList.add(extractPenaltyFromResultSet(rs));
            }

        } catch (SQLException e) {
            System.err.println("Error retrieving penalties by status: " + e.getMessage());
            e.printStackTrace();
        }

        return penaltyList;
    }


    
    /**
     * Helper method to extract PenaltyTransaction object from ResultSet.
     * 
     * @param rs ResultSet positioned at a penalty record row
     * @return PenaltyTransaction object
     * @throws SQLException if column access fails
     */
    private PenaltyTransaction extractPenaltyFromResultSet(ResultSet rs) throws SQLException {
        return new PenaltyTransaction(
            rs.getString("penaltyID"),
            rs.getString("rentalID"),
            rs.getBigDecimal("totalPenalty"),
            rs.getString("penaltyStatus"),
            rs.getString("maintenanceID"),
            rs.getDate("dateIssued"),
            rs.getString("status")
        );
    }

    /**
     * Check if a customer has any unpaid active penalties.
     * Used to block rentals if the customer has outstanding debts.
     * @param customerID The customer to check
     * @return true if they have unpaid penalties, false otherwise
     */
    public boolean hasUnpaidPenalties(String customerID) {
        String sql = "SELECT COUNT(*) FROM penalty p " +
                "JOIN rentals r ON p.rentalID = r.rentalID " +
                "WHERE r.customerID = ? " +
                "AND p.penaltyStatus = 'UNPAID' " +
                "AND p.status = 'Active'"; // Only count Active records

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, customerID);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return rs.getInt(1) > 0; // Returns true if count > 0
            }

        } catch (SQLException e) {
            System.err.println("Error checking unpaid penalties: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Get unpaid penalties for a specific customer.
     * @param customerID The customer in question
     * @return List of unpaid PenaltyTransactions
     */
    public List<PenaltyTransaction> getUnpaidPenaltiesByCustomer(String customerID){
        List<PenaltyTransaction> penaltyList = new ArrayList<>();

        String sql = "SELECT p.* " +
                "FROM penalty p " +
                "JOIN rentals r ON p.rentalID = r.rentalID " +
                "WHERE r.customerID = ? " +
                "  AND p.penaltyStatus = 'UNPAID' " +
                "  AND p.status = 'Active' " +
                "ORDER BY p.dateIssued ASC";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, customerID);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                penaltyList.add(extractPenaltyFromResultSet(rs));
            }

        } catch (SQLException e) {
            System.err.println("Error retrieving customer penalties: " + e.getMessage());
            e.printStackTrace();
        }

        return penaltyList;
    }

    public boolean isMaintenanceLinked(String maintenanceID) {
        String sql = "SELECT COUNT(*) FROM penalty WHERE maintenanceID = ? AND status = 'Active'";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, maintenanceID);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public String generateNewPenaltyID() {
        String sql = "SELECT penaltyID FROM penalty ORDER BY LENGTH(penaltyID) DESC, penaltyID DESC LIMIT 1";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            if (rs.next()) {
                String lastID = rs.getString("penaltyID");
                if (lastID.startsWith("PEN-")) {
                    String numberPart = lastID.substring(4);
                    int nextID = Integer.parseInt(numberPart) + 1;
                    return String.format("PEN-%03d", nextID);
                }
            }

        } catch (SQLException | NumberFormatException e) {
            System.err.println("Error generating new ID: " + e.getMessage());
        }

        return "PEN-001";
    }
}
