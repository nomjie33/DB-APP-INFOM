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
 * Uses SOFT DELETE pattern - records are marked inactive instead of being deleted.
 * 
 * SCHEMA ALIGNMENT:
 * This DAO assumes the maintenance_cheque table has columns:
 * - maintenanceID   VARCHAR(11) (primary key, foreign key)
 * - partID          VARCHAR(11) (primary key, foreign key)
 * - quantityUsed    DECIMAL(10,2)
 * - status          VARCHAR(15) DEFAULT 'Active'
 * 
 * SOFT DELETE IMPLEMENTATION:
 * - deactivateMaintenanceCheque() sets status to 'Inactive' instead of DELETE
 * - All retrieval methods filter WHERE status = 'Active' by default
 * - IncludingInactive methods available for historical data access
 * - NO HARD DELETE METHODS - only soft delete (deactivate) is supported
 * 
 * METHODS IMPLEMENTED:
 * 1. insertMaintenanceCheque()         - INSERT new maintenance part usage record (status defaults to 'Active')
 * 2. updateMaintenanceCheque()         - UPDATE quantity used (only active records)
 * 3. deactivateMaintenanceCheque()     - SOFT DELETE a specific part usage
 * 4. reactivateMaintenanceCheque()     - Sets status back to 'Active'
 * 5. deactivateAllByMaintenance()      - SOFT DELETE all parts for a maintenance record
 * 6. reactivateAllByMaintenance()      - Sets status back to 'Active' for all parts
 * 7. getMaintenanceChequeById()        - SELECT active part usage
 * 8. getMaintenanceChequeByIdIncludingInactive() - SELECT regardless of status
 * 9. getPartsByMaintenance()           - SELECT all active parts used in maintenance
 * 10. getPartsByMaintenanceIncludingInactive() - SELECT all parts regardless of status
 * 11. getMaintenancesByPart()          - SELECT all maintenance that used a part
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
     * Status defaults to 'Active' in the model constructor.
     * 
     * @param cheque MaintenanceCheque object to insert
     * @return true if insert successful, false otherwise
     */
    public boolean insertMaintenanceCheque(MaintenanceCheque cheque) {
        String sql = "INSERT INTO maintenance_cheque (maintenanceID, partID, quantityUsed, status) " +
                     "VALUES (?, ?, ?, ?)";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, cheque.getMaintenanceID());
            stmt.setString(2, cheque.getPartID());
            stmt.setBigDecimal(3, cheque.getQuantityUsed());
            stmt.setString(4, cheque.getStatus() != null ? cheque.getStatus() : "Active");
            
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
     * Only updates active records (status = 'Active').
     * 
     * @param cheque MaintenanceCheque object with updated data
     * @return true if update successful, false otherwise
     */
    public boolean updateMaintenanceCheque(MaintenanceCheque cheque) {
        String sql = "UPDATE maintenance_cheque SET quantityUsed = ? " +
                     "WHERE maintenanceID = ? AND partID = ? AND status = 'Active'";
        
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
     * SOFT DELETE a specific part usage from a maintenance record.
     * Sets status to 'Inactive' instead of deleting.
     * 
     * @param maintenanceID Maintenance ID
     * @param partID Part ID
     * @return true if deactivation successful, false otherwise
     */
    public boolean deactivateMaintenanceCheque(String maintenanceID, String partID) {
        String sql = "UPDATE maintenance_cheque SET status = 'Inactive' " +
                     "WHERE maintenanceID = ? AND partID = ?";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, maintenanceID);
            stmt.setString(2, partID);
            
            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
            
        } catch (SQLException e) {
            System.err.println("Error deactivating maintenance cheque record: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
    
    /**
     * Reactivate a previously deactivated part usage record.
     * Sets status back to 'Active'.
     * 
     * @param maintenanceID Maintenance ID
     * @param partID Part ID
     * @return true if reactivation successful, false otherwise
     */
    public boolean reactivateMaintenanceCheque(String maintenanceID, String partID) {
        String sql = "UPDATE maintenance_cheque SET status = 'Active' " +
                     "WHERE maintenanceID = ? AND partID = ?";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, maintenanceID);
            stmt.setString(2, partID);
            
            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
            
        } catch (SQLException e) {
            System.err.println("Error reactivating maintenance cheque record: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
    
    /**
     * SOFT DELETE all part usage records for a maintenance record.
     * Used when deactivating a maintenance to cascade soft delete to related parts.
     * 
     * @param maintenanceID Maintenance ID
     * @return true if deactivation successful, false otherwise
     */
    public boolean deactivateAllByMaintenance(String maintenanceID) {
        String sql = "UPDATE maintenance_cheque SET status = 'Inactive' " +
                     "WHERE maintenanceID = ?";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, maintenanceID);
            
            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
            
        } catch (SQLException e) {
            System.err.println("Error deactivating all maintenance cheque records: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
    
    /**
     * Reactivate all part usage records for a maintenance record.
     * Used when reactivating a maintenance to restore related parts.
     * 
     * @param maintenanceID Maintenance ID
     * @return true if reactivation successful, false otherwise
     */
    public boolean reactivateAllByMaintenance(String maintenanceID) {
        String sql = "UPDATE maintenance_cheque SET status = 'Active' " +
                     "WHERE maintenanceID = ?";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, maintenanceID);
            
            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
            
        } catch (SQLException e) {
            System.err.println("Error reactivating all maintenance cheque records: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
    
    /**
     * Get a specific maintenance part usage record (active only).
     * 
     * @param maintenanceID Maintenance ID
     * @param partID Part ID
     * @return MaintenanceCheque object or null if not found
     */
    public MaintenanceCheque getMaintenanceChequeById(String maintenanceID, String partID) {
        String sql = "SELECT * FROM maintenance_cheque " +
                     "WHERE maintenanceID = ? AND partID = ? AND status = 'Active'";
        
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
     * Get a specific maintenance part usage record including inactive records.
     * Used for historical lookups (e.g., penalty cost calculations).
     * 
     * @param maintenanceID Maintenance ID
     * @param partID Part ID
     * @return MaintenanceCheque object or null if not found
     */
    public MaintenanceCheque getMaintenanceChequeByIdIncludingInactive(String maintenanceID, String partID) {
        String sql = "SELECT * FROM maintenance_cheque " +
                     "WHERE maintenanceID = ? AND partID = ?";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, maintenanceID);
            stmt.setString(2, partID);
            
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                return extractMaintenanceChequeFromResultSet(rs);
            }
            
        } catch (SQLException e) {
            System.err.println("Error retrieving maintenance cheque record (including inactive): " + e.getMessage());
            e.printStackTrace();
        }
        
        return null;
    }
    
    /**
     * Get all active parts used in a specific maintenance record.
     * 
     * @param maintenanceID Maintenance ID to filter by
     * @return List of MaintenanceCheque objects for the maintenance
     */
    public List<MaintenanceCheque> getPartsByMaintenance(String maintenanceID) {
        List<MaintenanceCheque> chequeList = new ArrayList<>();
        String sql = "SELECT * FROM maintenance_cheque " +
                     "WHERE maintenanceID = ? AND status = 'Active'";
        
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
     * Get all parts used in a specific maintenance record including inactive.
     * Used for historical lookups (e.g., penalty cost calculations for old maintenance).
     * 
     * @param maintenanceID Maintenance ID to filter by
     * @return List of all MaintenanceCheque objects for the maintenance
     */
    public List<MaintenanceCheque> getPartsByMaintenanceIncludingInactive(String maintenanceID) {
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
            System.err.println("Error retrieving parts by maintenance (including inactive): " + e.getMessage());
            e.printStackTrace();
        }
        
        return chequeList;
    }
    
    /**
     * Get all active maintenance records that used a specific part.
     * 
     * @param partID Part ID to filter by
     * @return List of MaintenanceCheque objects for the part
     */
    public List<MaintenanceCheque> getMaintenancesByPart(String partID) {
        List<MaintenanceCheque> chequeList = new ArrayList<>();
        String sql = "SELECT * FROM maintenance_cheque " +
                     "WHERE partID = ? AND status = 'Active'";
        
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
            rs.getBigDecimal("quantityUsed"),
            rs.getString("status")
        );
    }

    public List<MaintenanceCheque> getAllActiveMaintenanceCheques() {
        List<MaintenanceCheque> chequeList = new ArrayList<>();
        String sql = "SELECT * FROM maintenance_cheque WHERE status = 'Active'";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                chequeList.add(extractMaintenanceChequeFromResultSet(rs));
            }

        } catch (SQLException e) {
            System.err.println("Error retrieving all active maintenance cheques: " + e.getMessage());
            e.printStackTrace();
        }

        return chequeList;
    }
}
