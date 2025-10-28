package dao;

import model.MaintenanceCheque;
import util.DBConnection;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object for MAINTENANCE_CHEQUE table operations.
 * 
 * PURPOSE: Handles all database CRUD operations for maintenance_cheque table.
 * This table tracks which parts were used in each maintenance record and their quantities.
 * 
 * SCHEMA ALIGNMENT:
 * This DAO assumes the maintenance_cheque table has columns:
 * - maintenanceID   VARCHAR(11) (primary key, foreign key)
 * - partID          VARCHAR(11) (primary key, foreign key)
 * - quantityUsed    DECIMAL(10,2)
 * 
 * METHODS IMPLEMENTED:
 * 1. insertMaintenanceCheque()       - INSERT new maintenance part usage record
 * 2. updateMaintenanceCheque()       - UPDATE quantity used for a part in maintenance
 * 3. deleteMaintenanceCheque()       - DELETE a specific part from maintenance record
 * 4. deleteAllByMaintenance()        - DELETE all parts for a maintenance record
 * 5. getMaintenanceChequeById()      - SELECT specific part usage in maintenance
 * 6. getPartsByMaintenance()         - SELECT all parts used in a maintenance record
 * 7. getMaintenancesByPart()         - SELECT all maintenance records that used a part
 * 
 * COLLABORATOR NOTES:
 * - Composite primary key (maintenanceID, partID)
 * - Always use PreparedStatement to prevent SQL injection
 * - Close resources in try-with-resources for automatic cleanup
 * - Return null/empty list when records not found
 * - Handle SQLException by printing stack trace (or log in production)
 */
public class MaintenanceChequeDAO {
    
    /**
     * Insert a new maintenance part usage record.
     * 
     * @param cheque MaintenanceCheque object to insert
     * @return true if insert successful, false otherwise
     */
    public boolean insertMaintenanceCheque(MaintenanceCheque cheque) {
        String sql = "INSERT INTO maintenance_cheque (maintenanceID, partID, quantityUsed) " +
                     "VALUES (?, ?, ?)";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, cheque.getMaintenanceID());
            stmt.setString(2, cheque.getPartID());
            stmt.setBigDecimal(3, cheque.getQuantityUsed());
            
            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
            
        } catch (SQLException e) {
            System.err.println("Error inserting maintenance cheque record: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
    
    /**
     * Update quantity used for a part in a maintenance record.
     * 
     * @param cheque MaintenanceCheque object with updated data
     * @return true if update successful, false otherwise
     */
    public boolean updateMaintenanceCheque(MaintenanceCheque cheque) {
        String sql = "UPDATE maintenance_cheque SET quantityUsed = ? " +
                     "WHERE maintenanceID = ? AND partID = ?";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setBigDecimal(1, cheque.getQuantityUsed());
            stmt.setString(2, cheque.getMaintenanceID());
            stmt.setString(3, cheque.getPartID());
            
            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
            
        } catch (SQLException e) {
            System.err.println("Error updating maintenance cheque record: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
    
    /**
     * Delete a specific part usage from a maintenance record.
     * 
     * @param maintenanceID Maintenance ID
     * @param partID Part ID
     * @return true if delete successful, false otherwise
     */
    public boolean deleteMaintenanceCheque(String maintenanceID, String partID) {
        String sql = "DELETE FROM maintenance_cheque WHERE maintenanceID = ? AND partID = ?";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, maintenanceID);
            stmt.setString(2, partID);
            
            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
            
        } catch (SQLException e) {
            System.err.println("Error deleting maintenance cheque record: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
    
    /**
     * Delete all part usage records for a maintenance record.
     * Useful when deleting a maintenance or clearing all parts to re-add them.
     * 
     * @param maintenanceID Maintenance ID
     * @return true if delete successful, false otherwise
     */
    public boolean deleteAllByMaintenance(String maintenanceID) {
        String sql = "DELETE FROM maintenance_cheque WHERE maintenanceID = ?";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, maintenanceID);
            
            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
            
        } catch (SQLException e) {
            System.err.println("Error deleting all maintenance cheque records: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
    
    /**
     * Get a specific maintenance part usage record.
     * 
     * @param maintenanceID Maintenance ID
     * @param partID Part ID
     * @return MaintenanceCheque object or null if not found
     */
    public MaintenanceCheque getMaintenanceChequeById(String maintenanceID, String partID) {
        String sql = "SELECT * FROM maintenance_cheque WHERE maintenanceID = ? AND partID = ?";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, maintenanceID);
            stmt.setString(2, partID);
            
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                return extractMaintenanceChequeFromResultSet(rs);
            }
            
        } catch (SQLException e) {
            System.err.println("Error retrieving maintenance cheque record: " + e.getMessage());
            e.printStackTrace();
        }
        
        return null;
    }
    
    /**
     * Get all parts used in a specific maintenance record.
     * 
     * @param maintenanceID Maintenance ID to filter by
     * @return List of MaintenanceCheque objects for the maintenance
     */
    public List<MaintenanceCheque> getPartsByMaintenance(String maintenanceID) {
        List<MaintenanceCheque> chequeList = new ArrayList<>();
        String sql = "SELECT * FROM maintenance_cheque WHERE maintenanceID = ?";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, maintenanceID);
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                chequeList.add(extractMaintenanceChequeFromResultSet(rs));
            }
            
        } catch (SQLException e) {
            System.err.println("Error retrieving parts by maintenance: " + e.getMessage());
            e.printStackTrace();
        }
        
        return chequeList;
    }
    
    /**
     * Get all maintenance records that used a specific part.
     * 
     * @param partID Part ID to filter by
     * @return List of MaintenanceCheque objects for the part
     */
    public List<MaintenanceCheque> getMaintenancesByPart(String partID) {
        List<MaintenanceCheque> chequeList = new ArrayList<>();
        String sql = "SELECT * FROM maintenance_cheque WHERE partID = ?";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, partID);
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                chequeList.add(extractMaintenanceChequeFromResultSet(rs));
            }
            
        } catch (SQLException e) {
            System.err.println("Error retrieving maintenance by part: " + e.getMessage());
            e.printStackTrace();
        }
        
        return chequeList;
    }
    
    /**
     * Helper method to extract MaintenanceCheque object from ResultSet.
     * 
     * @param rs ResultSet positioned at a maintenance_cheque record row
     * @return MaintenanceCheque object
     * @throws SQLException if column access fails
     */
    private MaintenanceCheque extractMaintenanceChequeFromResultSet(ResultSet rs) throws SQLException {
        return new MaintenanceCheque(
            rs.getString("maintenanceID"),
            rs.getString("partID"),
            rs.getBigDecimal("quantityUsed")
        );
    }
}
