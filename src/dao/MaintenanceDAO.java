package dao;

import model.MaintenanceTransaction;
import util.DBConnection;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object for MAINTENANCE TRANSACTION table operations.
 * 
 * PURPOSE: Handles all database CRUD operations for maintenance table.
 * 
 * SCHEMA ALIGNMENT:
 * This DAO assumes the maintenance table has columns:
 * - maintenance_id   VARCHAR (primary key, e.g. "M001")
 * - vehicle_id       VARCHAR (foreign key)
 * - technician_id    VARCHAR (foreign key)
 * - part_id          VARCHAR
 * - report_date      TIMESTAMP
 * - repair_date      TIMESTAMP
 * - notes            TEXT
 * - vehicle_status   VARCHAR
 * 
 * METHODS IMPLEMENTED:
 * 1. insertMaintenance()       - INSERT new maintenance record
 * 2. updateMaintenance()       - UPDATE maintenance record
 * 3. deleteMaintenance()       - DELETE maintenance record
 * 4. getMaintenanceById()      - SELECT maintenance by ID
 * 5. getAllMaintenance()       - SELECT all maintenance records
 * 6. getMaintenanceByVehicle() - SELECT maintenance history for a vehicle
 * 7. getMaintenanceByTechnician() - SELECT work assigned to a technician
 * 
 * COLLABORATOR NOTES:
 * - Always use PreparedStatement to prevent SQL injection
 * - Close resources in try-with-resources for automatic cleanup
 * - Return null when record not found
 * - Handle SQLException by printing stack trace (or log in production)
 */
public class MaintenanceDAO {
    
    /**
     * Insert a new maintenance record into the database.
     * 
     * @param maintenance MaintenanceTransaction object to insert
     * @return true if insert successful, false otherwise
     */
    public boolean insertMaintenance(MaintenanceTransaction maintenance) {
        String sql = "INSERT INTO maintenance (maintenance_id, vehicle_id, technician_id, part_id, " +
                     "report_date, repair_date, notes, vehicle_status) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, maintenance.getMaintenanceId());
            stmt.setString(2, maintenance.getVehicleId());
            stmt.setString(3, maintenance.getTechnicianId());
            stmt.setString(4, maintenance.getPartId());
            stmt.setTimestamp(5, maintenance.getReportDate());
            stmt.setTimestamp(6, maintenance.getRepairDate());
            stmt.setString(7, maintenance.getNotes());
            stmt.setString(8, maintenance.getVehicleStatus());
            
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
     * 
     * @param maintenance MaintenanceTransaction object with updated data
     * @return true if update successful, false otherwise
     */
    public boolean updateMaintenance(MaintenanceTransaction maintenance) {
        String sql = "UPDATE maintenance SET vehicle_id = ?, technician_id = ?, part_id = ?, " +
                     "report_date = ?, repair_date = ?, notes = ?, vehicle_status = ? " +
                     "WHERE maintenance_id = ?";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, maintenance.getVehicleId());
            stmt.setString(2, maintenance.getTechnicianId());
            stmt.setString(3, maintenance.getPartId());
            stmt.setTimestamp(4, maintenance.getReportDate());
            stmt.setTimestamp(5, maintenance.getRepairDate());
            stmt.setString(6, maintenance.getNotes());
            stmt.setString(7, maintenance.getVehicleStatus());
            stmt.setString(8, maintenance.getMaintenanceId());
            
            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
            
        } catch (SQLException e) {
            System.err.println("Error updating maintenance record: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
    
    /**
     * Delete a maintenance record by ID.
     * 
     * @param maintenanceId Maintenance ID to delete
     * @return true if delete successful, false otherwise
     */
    public boolean deleteMaintenance(String maintenanceId) {
        String sql = "DELETE FROM maintenance WHERE maintenance_id = ?";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, maintenanceId);
            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
            
        } catch (SQLException e) {
            System.err.println("Error deleting maintenance record: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
    
    /**
     * Get a maintenance record by ID.
     * 
     * @param maintenanceId Maintenance ID to retrieve
     * @return MaintenanceTransaction object or null if not found
     */
    public MaintenanceTransaction getMaintenanceById(String maintenanceId) {
        String sql = "SELECT * FROM maintenance WHERE maintenance_id = ?";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, maintenanceId);
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
     * Get all maintenance records.
     * 
     * @return List of all MaintenanceTransaction objects
     */
    public List<MaintenanceTransaction> getAllMaintenance() {
        List<MaintenanceTransaction> maintenanceList = new ArrayList<>();
        String sql = "SELECT * FROM maintenance ORDER BY report_date DESC";
        
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
     * Get maintenance history for a specific vehicle.
     * 
     * @param vehicleId Vehicle ID to filter by
     * @return List of MaintenanceTransaction objects for the vehicle
     */
    public List<MaintenanceTransaction> getMaintenanceByVehicle(String vehicleId) {
        List<MaintenanceTransaction> maintenanceList = new ArrayList<>();
        String sql = "SELECT * FROM maintenance WHERE vehicle_id = ? ORDER BY report_date DESC";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, vehicleId);
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
     * Get maintenance work assigned to a specific technician.
     * 
     * @param technicianId Technician ID to filter by
     * @return List of MaintenanceTransaction objects assigned to the technician
     */
    public List<MaintenanceTransaction> getMaintenanceByTechnician(String technicianId) {
        List<MaintenanceTransaction> maintenanceList = new ArrayList<>();
        String sql = "SELECT * FROM maintenance WHERE technician_id = ? ORDER BY report_date DESC";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, technicianId);
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
     * Get maintenance records by vehicle status.
     * Useful for finding vehicles that are "Available", "Under Repair", etc. after maintenance.
     * 
     * @param vehicleStatus Vehicle status to filter by
     * @return List of MaintenanceTransaction objects with matching vehicle status
     */
    public List<MaintenanceTransaction> getMaintenanceByVehicleStatus(String vehicleStatus) {
        List<MaintenanceTransaction> maintenanceList = new ArrayList<>();
        String sql = "SELECT * FROM maintenance WHERE vehicle_status = ? ORDER BY report_date DESC";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, vehicleStatus);
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                maintenanceList.add(extractMaintenanceFromResultSet(rs));
            }
            
        } catch (SQLException e) {
            System.err.println("Error retrieving maintenance by status: " + e.getMessage());
            e.printStackTrace();
        }
        
        return maintenanceList;
    }
    
    /**
     * Helper method to extract MaintenanceTransaction object from ResultSet.
     * 
     * @param rs ResultSet positioned at a maintenance record row
     * @return MaintenanceTransaction object
     * @throws SQLException if column access fails
     */
    private MaintenanceTransaction extractMaintenanceFromResultSet(ResultSet rs) throws SQLException {
        return new MaintenanceTransaction(
            rs.getString("maintenance_id"),
            rs.getString("vehicle_id"),
            rs.getString("technician_id"),
            rs.getString("part_id"),
            rs.getTimestamp("report_date"),
            rs.getTimestamp("repair_date"),
            rs.getString("notes"),
            rs.getString("vehicle_status")
        );
    }
    
}
