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
 * 
 * SCHEMA ALIGNMENT:
 * This DAO assumes the maintenance table has columns:
 * - maintenanceID   VARCHAR(11) (primary key)
 * - dateReported    DATE
 * - dateRepaired    DATE
 * - notes           VARCHAR(125)
 * - technicianID    VARCHAR(11) (foreign key)
 * - plateID         VARCHAR(11) (foreign key)
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
 */
public class MaintenanceDAO {
    
    /**
     * Insert a new maintenance record into the database.
     * 
     * @param maintenance MaintenanceTransaction object to insert
     * @return true if insert successful, false otherwise
     */
    public boolean insertMaintenance(MaintenanceTransaction maintenance) {
        String sql = "INSERT INTO maintenance (maintenanceID, dateReported, dateRepaired, " +
                     "notes, technicianID, plateID, hoursWorked) VALUES (?, ?, ?, ?, ?, ?, ?)";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, maintenance.getMaintenanceID());
            stmt.setDate(2, maintenance.getDateReported());
            stmt.setDate(3, maintenance.getDateRepaired());
            stmt.setString(4, maintenance.getNotes());
            stmt.setString(5, maintenance.getTechnicianID());
            stmt.setString(6, maintenance.getPlateID());
            
            // Handle hoursWorked - default to 0 if null
            if (maintenance.getHoursWorked() != null) {
                stmt.setBigDecimal(7, maintenance.getHoursWorked());
            } else {
                stmt.setBigDecimal(7, java.math.BigDecimal.ZERO);
            }
            
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
        String sql = "UPDATE maintenance SET dateReported = ?, dateRepaired = ?, notes = ?, " +
                     "technicianID = ?, plateID = ?, hoursWorked = ? WHERE maintenanceID = ?";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setDate(1, maintenance.getDateReported());
            stmt.setDate(2, maintenance.getDateRepaired());
            stmt.setString(3, maintenance.getNotes());
            stmt.setString(4, maintenance.getTechnicianID());
            stmt.setString(5, maintenance.getPlateID());
            
            // Handle hoursWorked - default to 0 if null
            if (maintenance.getHoursWorked() != null) {
                stmt.setBigDecimal(6, maintenance.getHoursWorked());
            } else {
                stmt.setBigDecimal(6, java.math.BigDecimal.ZERO);
            }
            
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
     * Delete a maintenance record by ID.
     * Note: This will also delete related maintenance_cheque records due to CASCADE.
     * 
     * @param maintenanceID Maintenance ID to delete
     * @return true if delete successful, false otherwise
     */
    public boolean deleteMaintenance(String maintenanceID) {
        String sql = "DELETE FROM maintenance WHERE maintenanceID = ?";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, maintenanceID);
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
     * @param maintenanceID Maintenance ID to retrieve
     * @return MaintenanceTransaction object or null if not found
     */
    public MaintenanceTransaction getMaintenanceById(String maintenanceID) {
        String sql = "SELECT * FROM maintenance WHERE maintenanceID = ?";
        
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
     * Get all maintenance records.
     * 
     * @return List of all MaintenanceTransaction objects
     */
    public List<MaintenanceTransaction> getAllMaintenance() {
        List<MaintenanceTransaction> maintenanceList = new ArrayList<>();
        String sql = "SELECT * FROM maintenance ORDER BY dateReported DESC";
        
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
     * @param plateID Vehicle plate ID to filter by
     * @return List of MaintenanceTransaction objects for the vehicle
     */
    public List<MaintenanceTransaction> getMaintenanceByVehicle(String plateID) {
        List<MaintenanceTransaction> maintenanceList = new ArrayList<>();
        String sql = "SELECT * FROM maintenance WHERE plateID = ? ORDER BY dateReported DESC";
        
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
     * Get maintenance work assigned to a specific technician.
     * 
     * @param technicianID Technician ID to filter by
     * @return List of MaintenanceTransaction objects assigned to the technician
     */
    public List<MaintenanceTransaction> getMaintenanceByTechnician(String technicianID) {
        List<MaintenanceTransaction> maintenanceList = new ArrayList<>();
        String sql = "SELECT * FROM maintenance WHERE technicianID = ? ORDER BY dateReported DESC";
        
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
     * Helper method to extract MaintenanceTransaction object from ResultSet.
     * 
     * @param rs ResultSet positioned at a maintenance record row
     * @return MaintenanceTransaction object
     * @throws SQLException if column access fails
     */
    private MaintenanceTransaction extractMaintenanceFromResultSet(ResultSet rs) throws SQLException {
        return new MaintenanceTransaction(
            rs.getString("maintenanceID"),
            rs.getDate("dateReported"),
            rs.getDate("dateRepaired"),
            rs.getString("notes"),
            rs.getString("technicianID"),
            rs.getString("plateID"),
            rs.getBigDecimal("hoursWorked")
        );
    }
    
}
