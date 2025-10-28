package dao;

import model.Technician;
import util.DBConnection;
import java.math.BigDecimal;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object for TECHNICIAN table operations.
 * 
 * PURPOSE: Handles all database CRUD operations for technicians table.
 * 
 * SCHEMA ALIGNMENT:
 * This DAO assumes the technicians table has columns:
 * - technician_id     VARCHAR(11) PRIMARY KEY
 * - last_name         VARCHAR(25)
 * - first_name        VARCHAR(25)
 * - specialization_id VARCHAR(15)
 * - rate              DECIMAL(10,2)
 * - contact_number    VARCHAR(15)
 * 
 * METHODS IMPLEMENTED:
 * 1. insertTechnician()        - INSERT new technician
 * 2. updateTechnician()        - UPDATE technician record
 * 3. deleteTechnician()        - DELETE technician by ID
 * 4. getTechnicianById()       - SELECT technician by ID
 * 5. getAllTechnicians()       - SELECT all technicians
 * 6. getTechniciansBySpecialization() - SELECT by specialization
 * 
 * COLLABORATOR NOTES:
 * - Used by MaintenanceService to assign repairs
 * - Filter by specialization when assigning specific repair types
 * - Always use PreparedStatement for SQL safety
 */
public class TechnicianDAO {
    
    /**
     * Insert a new technician into the database.
     * 
     * @param technician Technician object to insert
     * @return true if insert successful, false otherwise
     */
    public boolean insertTechnician(Technician technician) {
        String sql = "INSERT INTO technicians (technician_id, last_name, first_name, " +
                     "specialization_id, rate, contact_number) VALUES (?, ?, ?, ?, ?, ?)";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, technician.getTechnicianId());
            stmt.setString(2, technician.getLastName());
            stmt.setString(3, technician.getFirstName());
            stmt.setString(4, technician.getSpecializationId());
            stmt.setBigDecimal(5, technician.getRate());
            stmt.setString(6, technician.getContactNumber());
            
            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
            
        } catch (SQLException e) {
            System.err.println("Error inserting technician: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
    
    /**
     * Update an existing technician record.
     * 
     * @param technician Technician object with updated data
     * @return true if update successful, false otherwise
     */
    public boolean updateTechnician(Technician technician) {
        String sql = "UPDATE technicians SET last_name = ?, first_name = ?, " +
                     "specialization_id = ?, rate = ?, contact_number = ? " +
                     "WHERE technician_id = ?";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, technician.getLastName());
            stmt.setString(2, technician.getFirstName());
            stmt.setString(3, technician.getSpecializationId());
            stmt.setBigDecimal(4, technician.getRate());
            stmt.setString(5, technician.getContactNumber());
            stmt.setString(6, technician.getTechnicianId());
            
            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
            
        } catch (SQLException e) {
            System.err.println("Error updating technician: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
    
    /**
     * Delete a technician by ID.
     * 
     * @param technicianId Technician ID to delete
     * @return true if delete successful, false otherwise
     */
    public boolean deleteTechnician(String technicianId) {
        String sql = "DELETE FROM technicians WHERE technician_id = ?";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, technicianId);
            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
            
        } catch (SQLException e) {
            System.err.println("Error deleting technician: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
    
    /**
     * Get a technician by ID.
     * 
     * @param technicianId Technician ID to retrieve
     * @return Technician object or null if not found
     */
    public Technician getTechnicianById(String technicianId) {
        String sql = "SELECT * FROM technicians WHERE technician_id = ?";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, technicianId);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                return extractTechnicianFromResultSet(rs);
            }
            
        } catch (SQLException e) {
            System.err.println("Error retrieving technician: " + e.getMessage());
            e.printStackTrace();
        }
        
        return null;
    }
    
    /**
     * Get all technicians.
     * 
     * @return List of all Technician objects
     */
    public List<Technician> getAllTechnicians() {
        List<Technician> technicianList = new ArrayList<>();
        String sql = "SELECT * FROM technicians ORDER BY last_name, first_name";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            
            while (rs.next()) {
                technicianList.add(extractTechnicianFromResultSet(rs));
            }
            
        } catch (SQLException e) {
            System.err.println("Error retrieving all technicians: " + e.getMessage());
            e.printStackTrace();
        }
        
        return technicianList;
    }
    
    /**
     * Get technicians by specialization.
     * Useful for assigning maintenance tasks to technicians with specific expertise.
     * 
     * @param specializationId Specialization ID to filter by
     * @return List of Technician objects with matching specialization
     */
    public List<Technician> getTechniciansBySpecialization(String specializationId) {
        List<Technician> technicianList = new ArrayList<>();
        String sql = "SELECT * FROM technicians WHERE specialization_id = ? " +
                    "ORDER BY last_name, first_name";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, specializationId);
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                technicianList.add(extractTechnicianFromResultSet(rs));
            }
            
        } catch (SQLException e) {
            System.err.println("Error retrieving technicians by specialization: " + e.getMessage());
            e.printStackTrace();
        }
        
        return technicianList;
    }
    
    /**
     * Get technicians with rate within a specific range.
     * Useful for budget-based technician assignment.
     * 
     * @param minRate Minimum rate (inclusive)
     * @param maxRate Maximum rate (inclusive)
     * @return List of Technician objects within rate range
     */
    public List<Technician> getTechniciansByRateRange(BigDecimal minRate, BigDecimal maxRate) {
        List<Technician> technicianList = new ArrayList<>();
        String sql = "SELECT * FROM technicians WHERE rate BETWEEN ? AND ? " +
                    "ORDER BY rate ASC";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setBigDecimal(1, minRate);
            stmt.setBigDecimal(2, maxRate);
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                technicianList.add(extractTechnicianFromResultSet(rs));
            }
            
        } catch (SQLException e) {
            System.err.println("Error retrieving technicians by rate range: " + e.getMessage());
            e.printStackTrace();
        }
        
        return technicianList;
    }
    
    /**
     * Search technicians by name (partial match).
     * Searches both first name and last name.
     * 
     * @param searchTerm Search term for name matching
     * @return List of Technician objects with matching names
     */
    public List<Technician> searchTechniciansByName(String searchTerm) {
        List<Technician> technicianList = new ArrayList<>();
        String sql = "SELECT * FROM technicians WHERE " +
                    "first_name LIKE ? OR last_name LIKE ? " +
                    "ORDER BY last_name, first_name";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            String searchPattern = "%" + searchTerm + "%";
            stmt.setString(1, searchPattern);
            stmt.setString(2, searchPattern);
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                technicianList.add(extractTechnicianFromResultSet(rs));
            }
            
        } catch (SQLException e) {
            System.err.println("Error searching technicians by name: " + e.getMessage());
            e.printStackTrace();
        }
        
        return technicianList;
    }
    
    /**
     * Helper method to extract Technician object from ResultSet.
     * 
     * @param rs ResultSet positioned at a technician record row
     * @return Technician object
     * @throws SQLException if column access fails
     */
    private Technician extractTechnicianFromResultSet(ResultSet rs) throws SQLException {
        return new Technician(
            rs.getString("technician_id"),
            rs.getString("last_name"),
            rs.getString("first_name"),
            rs.getString("specialization_id"),
            rs.getBigDecimal("rate"),
            rs.getString("contact_number")
        );
    }
    
    /**
     * Test method - demonstrates basic DAO usage.
     * Run this to verify your database connection and table structure.
     */
    public static void main(String[] args) {
        System.out.println("=== TechnicianDAO Test ===\n");
        
        TechnicianDAO dao = new TechnicianDAO();
        
        // Test: Get all technicians
        System.out.println("Fetching all technicians...");
        List<Technician> allTechnicians = dao.getAllTechnicians();
        System.out.println("Found " + allTechnicians.size() + " technician(s).");
        
        for (Technician tech : allTechnicians) {
            System.out.println(tech);
        }
        
        // Test: Get by ID (if you have data)
        if (!allTechnicians.isEmpty()) {
            String testId = allTechnicians.get(0).getTechnicianId();
            System.out.println("\nFetching technician by ID: " + testId);
            Technician tech = dao.getTechnicianById(testId);
            System.out.println(tech);
        }
        
        System.out.println("\n=== Test Complete ===");
    }
}
