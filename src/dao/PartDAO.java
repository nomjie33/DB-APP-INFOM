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
 * Uses SOFT DELETE pattern - records are marked inactive instead of being deleted.
 * 
 * SCHEMA ALIGNMENT:
 * This DAO assumes the parts table has columns:
 * - part_id    VARCHAR(11) PRIMARY KEY
 * - part_name  VARCHAR(25)
 * - quantity   INT(3)
 * - price      DECIMAL(10,2)
 * - status     VARCHAR(15) DEFAULT 'Active'
 * 
 * SOFT DELETE IMPLEMENTATION:
 * - deletePart() now sets status to 'Inactive' instead of DELETE
 * - All retrieval methods filter WHERE status = 'Active' by default
 * - New methods: deactivatePart(), reactivatePart(), getAllPartsIncludingInactive()
 * 
 * METHODS IMPLEMENTED:
 * 1. insertPart()           - INSERT new part (status defaults to 'Active')
 * 2. updatePart()           - UPDATE existing part record
 * 3. deletePart()           - SOFT DELETE (sets status to 'Inactive')
 * 4. deactivatePart()       - Alias for deletePart() - marks as inactive
 * 5. reactivatePart()       - Sets status back to 'Active'
 * 6. getPartById()          - SELECT active part by ID
 * 7. getAllParts()          - SELECT all active parts
 * 8. getAllPartsIncludingInactive() - SELECT all parts regardless of status
 * 9. updatePartQuantity()   - UPDATE quantity (for restocking)
 * 10. decrementPartQuantity() - Reduce quantity when part is used (with validation)
 * 11. incrementPartQuantity() - Add to quantity when restocking
 * 12. getLowStockParts()    - SELECT active parts with quantity below threshold
 * 13. extractPartFromResultSet() - Helper to map ResultSet to Part object
 * 
 * COLLABORATOR NOTES:
 * - Track inventory carefully
 * - Validate quantities before decrementing (prevent negative stock)
 * - Use decrementPartQuantity() during maintenance operations
 * - Use incrementPartQuantity() when restocking
 * - All queries default to active parts only unless explicitly requesting inactive
 */
public class PartDAO {
    
    /**
     * Insert a new part into the inventory.
     * Status defaults to 'Active' in the model constructor.
     * 
     * @param part Part object to insert
     * @return true if insert successful, false otherwise
     */
    public boolean insertPart(Part part) {
        String sql = "INSERT INTO parts (part_id, part_name, quantity, price, status) VALUES (?, ?, ?, ?, ?)";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, part.getPartId());
            stmt.setString(2, part.getPartName());
            stmt.setInt(3, part.getQuantity());
            stmt.setBigDecimal(4, part.getPrice());
            stmt.setString(5, part.getStatus() != null ? part.getStatus() : "Active");
            
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
     * Note: Does not update status field (use deactivatePart/reactivatePart for that)
     * 
     * @param part Part object with updated data
     * @return true if update successful, false otherwise
     */
    public boolean updatePart(Part part) {
        String sql = "UPDATE parts SET part_name = ?, quantity = ?, price = ? WHERE part_id = ? AND status = 'Active'";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, part.getPartName());
            stmt.setInt(2, part.getQuantity());
            stmt.setBigDecimal(3, part.getPrice());
            stmt.setString(4, part.getPartId());
            
            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
            
        } catch (SQLException e) {
            System.err.println("Error updating part: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
    
    /**
     * SOFT DELETE: Mark a part as inactive instead of physically deleting it.
     * This preserves historical data and maintains referential integrity.
     * 
     * @param partId Part ID to mark as inactive
     * @return true if soft delete successful, false otherwise
     */
    public boolean deletePart(String partId) {
        return deactivatePart(partId);
    }
    
    /**
     * Deactivate a part (mark as Inactive).
     * Same as deletePart() - soft delete implementation.
     * 
     * @param partId Part ID to deactivate
     * @return true if deactivation successful, false otherwise
     */
    public boolean deactivatePart(String partId) {
        String sql = "UPDATE parts SET status = 'Inactive' WHERE part_id = ?";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, partId);
            int rowsAffected = stmt.executeUpdate();
            
            if (rowsAffected > 0) {
                System.out.println("Part " + partId + " has been marked as Inactive (soft deleted)");
                return true;
            }
            
        } catch (SQLException e) {
            System.err.println("Error deactivating part: " + e.getMessage());
            e.printStackTrace();
        }
        
        return false;
    }
    
    /**
     * Reactivate a previously deactivated part.
     * Sets status back to 'Active'.
     * 
     * @param partId Part ID to reactivate
     * @return true if reactivation successful, false otherwise
     */
    public boolean reactivatePart(String partId) {
        String sql = "UPDATE parts SET status = 'Active' WHERE part_id = ?";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, partId);
            int rowsAffected = stmt.executeUpdate();
            
            if (rowsAffected > 0) {
                System.out.println("Part " + partId + " has been reactivated");
                return true;
            }
            
        } catch (SQLException e) {
            System.err.println("Error reactivating part: " + e.getMessage());
            e.printStackTrace();
        }
        
        return false;
    }
    
    /**
     * Get an active part by ID.
     * Only returns parts with status = 'Active'.
     * 
     * @param partId Part ID to retrieve
     * @return Part object or null if not found or inactive
     */
    public Part getPartById(String partId) {
        String sql = "SELECT * FROM parts WHERE part_id = ? AND status = 'Active'";
        
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
     * Get a part by ID regardless of status (Active or Inactive).
     * Used for historical lookups, such as calculating costs for past maintenance.
     * 
     * @param partId Part ID to retrieve
     * @return Part object or null if not found
     */
    public Part getPartByIdIncludingInactive(String partId) {
        String sql = "SELECT * FROM parts WHERE part_id = ?";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, partId);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                return extractPartFromResultSet(rs);
            }
            
        } catch (SQLException e) {
            System.err.println("Error retrieving part (including inactive): " + e.getMessage());
            e.printStackTrace();
        }
        
        return null;
    }
    
    /**
     * Get all active parts in inventory.
     * Only returns parts with status = 'Active'.
     * 
     * @return List of all active Part objects
     */
    public List<Part> getAllParts() {
        List<Part> partList = new ArrayList<>();
        String sql = "SELECT * FROM parts WHERE status = 'Active' ORDER BY part_name";
        
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
     * Get all parts including inactive ones.
     * Returns both Active and Inactive parts.
     * 
     * @return List of all Part objects regardless of status
     */
    public List<Part> getAllPartsIncludingInactive() {
        List<Part> partList = new ArrayList<>();
        String sql = "SELECT * FROM parts ORDER BY status DESC, part_name";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            
            while (rs.next()) {
                partList.add(extractPartFromResultSet(rs));
            }
            
        } catch (SQLException e) {
            System.err.println("Error retrieving all parts (including inactive): " + e.getMessage());
            e.printStackTrace();
        }
        
        return partList;
    }
    
    /**
     * Update part quantity directly (for restocking or manual adjustments).
     * Only updates active parts.
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
        
        String sql = "UPDATE parts SET quantity = ? WHERE part_id = ? AND status = 'Active'";
        
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
     * Only works with active parts.
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
        
        // First, check current quantity (only for active parts)
        Part part = getPartById(partId);
        if (part == null) {
            System.err.println("Error: Active part not found with ID: " + partId);
            return false;
        }
        
        int currentQuantity = part.getQuantity();
        if (currentQuantity < usedQuantity) {
            System.err.println("Error: Insufficient stock. Available: " + currentQuantity + 
                             ", Requested: " + usedQuantity);
            return false;
        }
        
        // Perform the decrement (only for active parts)
        String sql = "UPDATE parts SET quantity = quantity - ? WHERE part_id = ? AND status = 'Active'";
        
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
     * Only works with active parts.
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
        
        String sql = "UPDATE parts SET quantity = quantity + ? WHERE part_id = ? AND status = 'Active'";
        
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
     * Get active parts with low stock (quantity below threshold).
     * Useful for inventory alerts and reorder notifications.
     * Only returns active parts.
     * 
     * @param threshold Minimum quantity threshold (e.g., 10)
     * @return List of active parts with quantity <= threshold
     */
    public List<Part> getLowStockParts(int threshold) {
        List<Part> lowStockParts = new ArrayList<>();
        String sql = "SELECT * FROM parts WHERE quantity <= ? AND status = 'Active' ORDER BY quantity ASC";
        
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
     * @return Part object with all fields including status
     * @throws SQLException if column access fails
     */
    private Part extractPartFromResultSet(ResultSet rs) throws SQLException {
        return new Part(
            rs.getString("part_id"),
            rs.getString("part_name"),
            rs.getInt("quantity"),
            rs.getBigDecimal("price"),
            rs.getString("status")
        );
    }
    
}
