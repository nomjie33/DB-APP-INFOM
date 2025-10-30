package dao;

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
 * - penaltyStatus  VARCHAR(15)
 * - maintenanceID  VARCHAR(11) (foreign key)
 * - dateIssued     DATE
 * 
 * METHODS IMPLEMENTED:
 * 1. insertPenalty()       - INSERT new penalty record
 * 2. updatePenalty()       - UPDATE penalty record
 * 3. deletePenalty()       - DELETE penalty record
 * 4. getPenaltyById()      - SELECT penalty by ID
 * 5. getAllPenalties()     - SELECT all penalties
 * 6. getPenaltiesByRental() - SELECT penalties for a rental
 * 7. getPenaltiesByStatus() - SELECT penalties by status
 * 8. getPenaltiesByMaintenance() - SELECT penalties linked to maintenance
 * 9. getTotalPenaltiesByRental() - SUM penalties for a rental
 * 10. getPenaltiesByDateRange() - SELECT penalties within date range
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
        String sql = "INSERT INTO penalty (penaltyID, rentalID, totalPenalty, penaltyStatus, maintenanceID, dateIssued) " +
                     "VALUES (?, ?, ?, ?, ?, ?)";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, penalty.getPenaltyID());
            stmt.setString(2, penalty.getRentalID());
            stmt.setBigDecimal(3, penalty.getTotalPenalty());
            stmt.setString(4, penalty.getPenaltyStatus());
            stmt.setString(5, penalty.getMaintenanceID());
            stmt.setDate(6, penalty.getDateIssued());
            
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
                     "maintenanceID = ?, dateIssued = ? WHERE penaltyID = ?";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, penalty.getRentalID());
            stmt.setBigDecimal(2, penalty.getTotalPenalty());
            stmt.setString(3, penalty.getPenaltyStatus());
            stmt.setString(4, penalty.getMaintenanceID());
            stmt.setDate(5, penalty.getDateIssued());
            stmt.setString(6, penalty.getPenaltyID());
            
            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
            
        } catch (SQLException e) {
            System.err.println("Error updating penalty record: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
    
    /**
     * Delete a penalty record by ID.
     * 
     * @param penaltyID Penalty ID to delete
     * @return true if delete successful, false otherwise
     */
    public boolean deletePenalty(String penaltyID) {
        String sql = "DELETE FROM penalty WHERE penaltyID = ?";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, penaltyID);
            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
            
        } catch (SQLException e) {
            System.err.println("Error deleting penalty record: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
    
    /**
     * Get a penalty record by ID.
     * 
     * @param penaltyID Penalty ID to retrieve
     * @return PenaltyTransaction object or null if not found
     */
    public PenaltyTransaction getPenaltyById(String penaltyID) {
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
     * Get all penalty records.
     * 
     * @return List of all PenaltyTransaction objects
     */
    public List<PenaltyTransaction> getAllPenalties() {
        List<PenaltyTransaction> penaltyList = new ArrayList<>();
        String sql = "SELECT * FROM penalty ORDER BY dateIssued DESC";
        
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
     * Get all penalties for a specific rental.
     * 
     * @param rentalID Rental ID to filter by
     * @return List of PenaltyTransaction objects for the rental
     */
    public List<PenaltyTransaction> getPenaltiesByRental(String rentalID) {
        List<PenaltyTransaction> penaltyList = new ArrayList<>();
        String sql = "SELECT * FROM penalty WHERE rentalID = ? ORDER BY dateIssued DESC";
        
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
     * Get penalties by status.
     * Useful for filtering Paid, Unpaid, or Waived penalties.
     * 
     * @param penaltyStatus Status to filter by (e.g., "Paid", "Unpaid", "Waived")
     * @return List of PenaltyTransaction objects with matching status
     */
    public List<PenaltyTransaction> getPenaltiesByStatus(String penaltyStatus) {
        List<PenaltyTransaction> penaltyList = new ArrayList<>();
        String sql = "SELECT * FROM penalty WHERE penaltyStatus = ? ORDER BY dateIssued DESC";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, penaltyStatus);
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
     * Get penalties linked to a specific maintenance record.
     * Useful for tracking damage-related penalties.
     * 
     * @param maintenanceID Maintenance ID to filter by
     * @return List of PenaltyTransaction objects linked to the maintenance
     */
    public List<PenaltyTransaction> getPenaltiesByMaintenance(String maintenanceID) {
        List<PenaltyTransaction> penaltyList = new ArrayList<>();
        String sql = "SELECT * FROM penalty WHERE maintenanceID = ? ORDER BY dateIssued DESC";
        
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
     * Get total penalties for a specific rental.
     * Sums all penalty amounts for the rental.
     * 
     * @param rentalID Rental ID to calculate total for
     * @return Total penalty amount as BigDecimal, or BigDecimal.ZERO if no penalties
     */
    public BigDecimal getTotalPenaltiesByRental(String rentalID) {
        String sql = "SELECT SUM(totalPenalty) as total FROM penalty WHERE rentalID = ?";
        
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
    
    /**
     * Get penalties within a date range.
     * Useful for penalty reports and analysis.
     * 
     * @param startDate Start date (inclusive)
     * @param endDate End date (inclusive)
     * @return List of PenaltyTransaction objects within the date range
     */
    public List<PenaltyTransaction> getPenaltiesByDateRange(Date startDate, Date endDate) {
        List<PenaltyTransaction> penaltyList = new ArrayList<>();
        String sql = "SELECT * FROM penalty WHERE dateIssued BETWEEN ? AND ? ORDER BY dateIssued ASC";
        
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
            rs.getDate("dateIssued")
        );
    }
}
