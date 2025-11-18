package dao;

import model.MaintenanceTransaction;
import util.DBConnection;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object for MAINTENANCE table operations.
 * 
 * PURPOSE: Handles all database CRUD operations for maintenance table.
 * Uses SOFT DELETE pattern - records are marked inactive instead of being deleted.
 * 
 * SCHEMA ALIGNMENT:
 * This DAO assumes the maintenance table has columns:
 * - maintenanceID   VARCHAR(11) (primary key)
 * - startDateTime   TIMESTAMP
 * - endDateTime     TIMESTAMP
 * - notes           VARCHAR(125)
 * - technicianID    VARCHAR(11) (foreign key)
 * - plateID         VARCHAR(11) (foreign key)
 * - status          VARCHAR(15) DEFAULT 'Active'
 * 
 * SOFT DELETE IMPLEMENTATION:
 * - deactivateMaintenance() sets status to 'Inactive' instead of DELETE
 * - All retrieval methods filter WHERE status = 'Active' by default
 * - IncludingInactive methods available for historical data access
 * - NO HARD DELETE METHODS - only soft delete (deactivate) is supported
 * 
 * METHODS IMPLEMENTED:
 * 1. insertMaintenance()           - INSERT new maintenance record (status defaults to 'Active')
 * 2. updateMaintenance()           - UPDATE maintenance record (only active)
 * 3. deactivateMaintenance()       - SOFT DELETE (sets status to 'Inactive')
 * 4. reactivateMaintenance()       - Sets status back to 'Active'
 * 5. getMaintenanceById()          - SELECT active maintenance by ID
 * 6. getMaintenanceByIdIncludingInactive() - SELECT regardless of status (for historical lookups)
 * 7. getAllMaintenance()           - SELECT all active maintenance records
 * 8. getAllMaintenanceIncludingInactive() - SELECT all regardless of status
 * 9. getMaintenanceByVehicle()     - SELECT active maintenance history for a vehicle
 * 10. getMaintenanceByTechnician() - SELECT active work assigned to a technician
 * 
 */
public class MaintenanceDAO {
    
    /**
     * Insert a new maintenance record into the database.
     * Status defaults to 'Active' in the model constructor.
     * 
     * @param maintenance MaintenanceTransaction object to insert
     * @return true if insert successful, false otherwise
     */
    public boolean insertMaintenance(MaintenanceTransaction maintenance) {
        String sql = "INSERT INTO maintenance (maintenanceID, startDateTime, endDateTime, totalCost, " +
                     "notes, technicianID, plateID, status) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, maintenance.getMaintenanceID());
            stmt.setTimestamp(2, maintenance.getStartDateTime());
            stmt.setTimestamp(3, maintenance.getEndDateTime());
            stmt.setBigDecimal(4, maintenance.getTotalCost());
            stmt.setString(5, maintenance.getNotes());
            stmt.setString(6, maintenance.getTechnicianID());
            stmt.setString(7, maintenance.getPlateID());
            stmt.setString(8, maintenance.getStatus() != null ? maintenance.getStatus() : "Active");
            
            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
            
        } catch (SQLException e) {
            System.err.println("Error inserting maintenance record: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
    
    /**
     * Update an existing maintenance record.
     * Note: Does not update status field (use deactivateMaintenance/reactivateMaintenance for that)
     * Only updates active maintenance records.
     * 
     * @param maintenance MaintenanceTransaction object with updated data
     * @return true if update successful, false otherwise
     */
    public boolean updateMaintenance(MaintenanceTransaction maintenance) {
        String sql = "UPDATE maintenance SET startDateTime = ?, endDateTime = ?, totalCost = ?, notes = ?, " +
                     "technicianID = ?, plateID = ? WHERE maintenanceID = ? AND status = 'Active'";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setTimestamp(1, maintenance.getStartDateTime());
            stmt.setTimestamp(2, maintenance.getEndDateTime());
            stmt.setBigDecimal(3, maintenance.getTotalCost());
            stmt.setString(4, maintenance.getNotes());
            stmt.setString(5, maintenance.getTechnicianID());
            stmt.setString(6, maintenance.getPlateID());
            stmt.setString(7, maintenance.getMaintenanceID());
            
            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
            
        } catch (SQLException e) {
            System.err.println("Error updating maintenance record: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
    
    /**
     * SOFT DELETE: Mark a maintenance record as inactive instead of physically deleting.
     * This preserves historical data and maintains referential integrity.
     * Deactivate a maintenance record (mark as Inactive).
     * 
     * @param maintenanceID Maintenance ID to deactivate
     * @return true if deactivation successful, false otherwise
     */
    public boolean deactivateMaintenance(String maintenanceID) {
        String sql = "UPDATE maintenance SET status = 'Inactive' WHERE maintenanceID = ?";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, maintenanceID);
            int rowsAffected = stmt.executeUpdate();
            
            if (rowsAffected > 0) {
                System.out.println("Maintenance " + maintenanceID + " has been marked as Inactive (soft deleted)");
                return true;
            }
            
        } catch (SQLException e) {
            System.err.println("Error deactivating maintenance record: " + e.getMessage());
            e.printStackTrace();
        }
        
        return false;
    }
    
    /**
     * Reactivate a previously deactivated maintenance record.
     * Sets status back to 'Active'.
     * 
     * @param maintenanceID Maintenance ID to reactivate
     * @return true if reactivation successful, false otherwise
     */
    public boolean reactivateMaintenance(String maintenanceID) {
        String sql = "UPDATE maintenance SET status = 'Active' WHERE maintenanceID = ?";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, maintenanceID);
            int rowsAffected = stmt.executeUpdate();
            
            if (rowsAffected > 0) {
                System.out.println("Maintenance " + maintenanceID + " has been reactivated");
                return true;
            }
            
        } catch (SQLException e) {
            System.err.println("Error reactivating maintenance record: " + e.getMessage());
            e.printStackTrace();
        }
        
        return false;
    }
    
    /**
     * Get an active maintenance record by ID.
     * Only returns maintenance records with status = 'Active'.
     * 
     * @param maintenanceID Maintenance ID to retrieve
     * @return MaintenanceTransaction object or null if not found or inactive
     */
    public MaintenanceTransaction getMaintenanceById(String maintenanceID) {
        String sql = "SELECT * FROM maintenance WHERE maintenanceID = ? AND status = 'Active'";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, maintenanceID);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                return extractMaintenanceFromResultSet(rs);
            }
            
        } catch (SQLException e) {
            System.err.println("Error retrieving maintenance record: " + e.getMessage());
            e.printStackTrace();
        }
        
        return null;
    }
    
    /**
     * Get a maintenance record by ID regardless of status (Active or Inactive).
     * Used for historical lookups, such as calculating penalty costs for past maintenance.
     * 
     * @param maintenanceID Maintenance ID to retrieve
     * @return MaintenanceTransaction object or null if not found
     */
    public MaintenanceTransaction getMaintenanceByIdIncludingInactive(String maintenanceID) {
        String sql = "SELECT * FROM maintenance WHERE maintenanceID = ?";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, maintenanceID);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                return extractMaintenanceFromResultSet(rs);
            }
            
        } catch (SQLException e) {
            System.err.println("Error retrieving maintenance record (including inactive): " + e.getMessage());
            e.printStackTrace();
        }
        
        return null;
    }
    
    /**
     * Get all active maintenance records.
     * Only returns maintenance records with status = 'Active'.
     * 
     * @return List of all active MaintenanceTransaction objects
     */
    public List<MaintenanceTransaction> getAllMaintenance() {
        List<MaintenanceTransaction> maintenanceList = new ArrayList<>();
        String sql = "SELECT * FROM maintenance WHERE status = 'Active' ORDER BY startDateTime DESC";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            
            while (rs.next()) {
                maintenanceList.add(extractMaintenanceFromResultSet(rs));
            }
            
        } catch (SQLException e) {
            System.err.println("Error retrieving all maintenance records: " + e.getMessage());
            e.printStackTrace();
        }
        
        return maintenanceList;
    }
    
    /**
     * Get all maintenance records including inactive ones.
     * Returns both Active and Inactive maintenance records.
     * 
     * @return List of all MaintenanceTransaction objects regardless of status
     */
    public List<MaintenanceTransaction> getAllMaintenanceIncludingInactive() {
        List<MaintenanceTransaction> maintenanceList = new ArrayList<>();
        String sql = "SELECT * FROM maintenance ORDER BY status DESC, startDateTime DESC";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            
            while (rs.next()) {
                maintenanceList.add(extractMaintenanceFromResultSet(rs));
            }
            
        } catch (SQLException e) {
            System.err.println("Error retrieving all maintenance records (including inactive): " + e.getMessage());
            e.printStackTrace();
        }
        
        return maintenanceList;
    }
    
    /**
     * Get active maintenance history for a specific vehicle.
     * Only returns active maintenance records.
     * 
     * @param plateID Vehicle plate ID to filter by
     * @return List of active MaintenanceTransaction objects for the vehicle
     */
    public List<MaintenanceTransaction> getMaintenanceByVehicle(String plateID) {
        List<MaintenanceTransaction> maintenanceList = new ArrayList<>();
        String sql = "SELECT * FROM maintenance WHERE plateID = ? AND status = 'Active' ORDER BY startDateTime DESC";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, plateID);
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                maintenanceList.add(extractMaintenanceFromResultSet(rs));
            }
            
        } catch (SQLException e) {
            System.err.println("Error retrieving maintenance by vehicle: " + e.getMessage());
            e.printStackTrace();
        }
        
        return maintenanceList;
    }
    
    /**
     * Get active maintenance work assigned to a specific technician.
     * Only returns active maintenance records.
     * 
     * @param technicianID Technician ID to filter by
     * @return List of active MaintenanceTransaction objects assigned to the technician
     */
    public List<MaintenanceTransaction> getMaintenanceByTechnician(String technicianID) {
        List<MaintenanceTransaction> maintenanceList = new ArrayList<>();
        String sql = "SELECT * FROM maintenance WHERE technicianID = ? AND status = 'Active' ORDER BY startDateTime DESC";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, technicianID);
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                maintenanceList.add(extractMaintenanceFromResultSet(rs));
            }
            
        } catch (SQLException e) {
            System.err.println("Error retrieving maintenance by technician: " + e.getMessage());
            e.printStackTrace();
        }
        
        return maintenanceList;
    }

    /**
     * Get all maintenance records by a specific status.
     * @param status The status to filter by (e.g., "Active" or "Inactive")
     * @return List of MaintenanceTransaction objects
     */
    public List<MaintenanceTransaction> getMaintenanceByStatus(String status) {
        List<MaintenanceTransaction> maintenanceList = new ArrayList<>();
        String sql = "SELECT * FROM maintenance WHERE status = ? ORDER BY startDateTime DESC";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, status);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                maintenanceList.add(extractMaintenanceFromResultSet(rs));
            }

        } catch (SQLException e) {
            System.err.println("Error retrieving maintenance records by status: " + e.getMessage());
            e.printStackTrace();
        }

        return maintenanceList;
    }
    
    /**
     * Helper method to extract MaintenanceTransaction object from ResultSet.
     * 
     * @param rs ResultSet positioned at a maintenance record row
     * @return MaintenanceTransaction object with all fields including status
     * @throws SQLException if column access fails
     */
    private MaintenanceTransaction extractMaintenanceFromResultSet(ResultSet rs) throws SQLException {
        return new MaintenanceTransaction(
            rs.getString("maintenanceID"),
            rs.getTimestamp("startDateTime"),
            rs.getTimestamp("endDateTime"),
            rs.getBigDecimal("totalCost"),
            rs.getString("notes"),
            rs.getString("technicianID"),
            rs.getString("plateID"),
            rs.getString("status")
        );
    }
    
}
