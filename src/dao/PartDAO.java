package dao;

import model.Part;
import util.DBConnection;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object for PART table operations.
 * 
 * PURPOSE: Handles all database CRUD operations for parts/inventory table.
 * 
 *  SCHEMA ALIGNMENT:
 * This DAO assumes the parts table has columns:
 * - part_id    VARCHAR(11) PRIMARY KEY
 * - part_name  VARCHAR(25)
 * - quantity   INT(3)
 * 
 * METHODS IMPLEMENTED:
 * 1. insertPart()           - INSERT new part into inventory
 * 2. updatePart()           - UPDATE existing part record
 * 3. deletePart()           - DELETE part by ID
 * 4. getPartById()          - SELECT part by ID
 * 5. getAllParts()          - SELECT all parts in inventory
 * 6. updatePartQuantity()   - UPDATE quantity (for restocking)
 * 7. decrementPartQuantity() - Reduce quantity when part is used (with validation)
 * 8. incrementPartQuantity() - Add to quantity when restocking
 * 9. getLowStockParts()      - SELECT parts with quantity below threshold
 * 10. extractPartFromResultSet() - Helper to map ResultSet to Part object
 * 
 * COLLABORATOR NOTES:
 * - Track inventory carefully
 * - Validate quantities before decrementing (prevent negative stock)
 * - Use decrementPartQuantity() during maintenance operations
 * - Use incrementPartQuantity() when restocking
 */
public class PartDAO {
    
    /**
     * Insert a new part into the inventory.
     * 
     * @param part Part object to insert
     * @return true if insert successful, false otherwise
     */
    public boolean insertPart(Part part) {
        String sql = "INSERT INTO parts (part_id, part_name, quantity) VALUES (?, ?, ?)";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, part.getPartId());
            stmt.setString(2, part.getPartName());
            stmt.setInt(3, part.getQuantity());
            
            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
            
        } catch (SQLException e) {
            System.err.println("Error inserting part: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
    
    /**
     * Update an existing part record.
     * 
     * @param part Part object with updated data
     * @return true if update successful, false otherwise
     */
    public boolean updatePart(Part part) {
        String sql = "UPDATE parts SET part_name = ?, quantity = ? WHERE part_id = ?";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, part.getPartName());
            stmt.setInt(2, part.getQuantity());
            stmt.setString(3, part.getPartId());
            
            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
            
        } catch (SQLException e) {
            System.err.println("Error updating part: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
    
    /**
     * Delete a part by ID.
     * 
     * @param partId Part ID to delete
     * @return true if delete successful, false otherwise
     */
    public boolean deletePart(String partId) {
        String sql = "DELETE FROM parts WHERE part_id = ?";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, partId);
            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
            
        } catch (SQLException e) {
            System.err.println("Error deleting part: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
    
    /**
     * Get a part by ID.
     * 
     * @param partId Part ID to retrieve
     * @return Part object or null if not found
     */
    public Part getPartById(String partId) {
        String sql = "SELECT * FROM parts WHERE part_id = ?";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, partId);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                return extractPartFromResultSet(rs);
            }
            
        } catch (SQLException e) {
            System.err.println("Error retrieving part: " + e.getMessage());
            e.printStackTrace();
        }
        
        return null;
    }
    
    /**
     * Get all parts in inventory.
     * 
     * @return List of all Part objects
     */
    public List<Part> getAllParts() {
        List<Part> partList = new ArrayList<>();
        String sql = "SELECT * FROM parts ORDER BY part_name";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            
            while (rs.next()) {
                partList.add(extractPartFromResultSet(rs));
            }
            
        } catch (SQLException e) {
            System.err.println("Error retrieving all parts: " + e.getMessage());
            e.printStackTrace();
        }
        
        return partList;
    }
    
    /**
     * Update part quantity directly (for restocking or manual adjustments).
     * 
     * @param partId Part ID to update
     * @param newQuantity New quantity value
     * @return true if update successful, false otherwise
     */
    public boolean updatePartQuantity(String partId, int newQuantity) {
        if (newQuantity < 0) {
            System.err.println("Error: Quantity cannot be negative.");
            return false;
        }
        
        String sql = "UPDATE parts SET quantity = ? WHERE part_id = ?";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, newQuantity);
            stmt.setString(2, partId);
            
            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
            
        } catch (SQLException e) {
            System.err.println("Error updating part quantity: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
    
    /**
     * Decrement part quantity when part is used in maintenance.
     * Validates that sufficient quantity exists before decrementing.
     * 
     * @param partId Part ID to decrement
     * @param usedQuantity Quantity to subtract
     * @return true if decrement successful, false if insufficient stock or error
     */
    public boolean decrementPartQuantity(String partId, int usedQuantity) {
        if (usedQuantity <= 0) {
            System.err.println("Error: Used quantity must be positive.");
            return false;
        }
        
        // First, check current quantity
        Part part = getPartById(partId);
        if (part == null) {
            System.err.println("Error: Part not found with ID: " + partId);
            return false;
        }
        
        int currentQuantity = part.getQuantity();
        if (currentQuantity < usedQuantity) {
            System.err.println("Error: Insufficient stock. Available: " + currentQuantity + 
                             ", Requested: " + usedQuantity);
            return false;
        }
        
        // Perform the decrement
        String sql = "UPDATE parts SET quantity = quantity - ? WHERE part_id = ?";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, usedQuantity);
            stmt.setString(2, partId);
            
            int rowsAffected = stmt.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("Part " + partId + " quantity decremented by " + usedQuantity + 
                                 ". New quantity: " + (currentQuantity - usedQuantity));
                return true;
            }
            
        } catch (SQLException e) {
            System.err.println("Error decrementing part quantity: " + e.getMessage());
            e.printStackTrace();
        }
        
        return false;
    }
    
    /**
     * Increment part quantity when restocking.
     * 
     * @param partId Part ID to increment
     * @param addedQuantity Quantity to add
     * @return true if increment successful, false otherwise
     */
    public boolean incrementPartQuantity(String partId, int addedQuantity) {
        if (addedQuantity <= 0) {
            System.err.println("Error: Added quantity must be positive.");
            return false;
        }
        
        String sql = "UPDATE parts SET quantity = quantity + ? WHERE part_id = ?";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, addedQuantity);
            stmt.setString(2, partId);
            
            int rowsAffected = stmt.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("Part " + partId + " quantity incremented by " + addedQuantity);
                return true;
            }
            
        } catch (SQLException e) {
            System.err.println("Error incrementing part quantity: " + e.getMessage());
            e.printStackTrace();
        }
        
        return false;
    }
    
    /**
     * Get parts with low stock (quantity below threshold).
     * Useful for inventory alerts and reorder notifications.
     * 
     * @param threshold Minimum quantity threshold (e.g., 10)
     * @return List of parts with quantity <= threshold
     */
    public List<Part> getLowStockParts(int threshold) {
        List<Part> lowStockParts = new ArrayList<>();
        String sql = "SELECT * FROM parts WHERE quantity <= ? ORDER BY quantity ASC";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, threshold);
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                lowStockParts.add(extractPartFromResultSet(rs));
            }
            
        } catch (SQLException e) {
            System.err.println("Error retrieving low stock parts: " + e.getMessage());
            e.printStackTrace();
        }
        
        return lowStockParts;
    }
    
    /**
     * Helper method to extract Part object from ResultSet.
     * 
     * @param rs ResultSet positioned at a part record row
     * @return Part object
     * @throws SQLException if column access fails
     */
    private Part extractPartFromResultSet(ResultSet rs) throws SQLException {
        return new Part(
            rs.getString("part_id"),
            rs.getString("part_name"),
            rs.getInt("quantity"),
            rs.getBigDecimal("price")
        );
    }
    
}
