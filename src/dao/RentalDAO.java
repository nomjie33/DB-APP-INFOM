package dao;

import model.RentalTransaction;
import util.DBConnection;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
/**
 * Data Access Object for RENTAL TRANSACTION table operations.
 * 
 * PURPOSE: Handles all database CRUD operations for rentals table.
 * 
 * METHODS TO IMPLEMENT:
 * 
 * 1. insertRental(RentalTransaction rental)
 *    - INSERT new rental record
 *    - Return generated rental ID
 * 
 * 2. updateRental(RentalTransaction rental)
 *    - UPDATE existing rental (e.g., when returned)
 * 
 * 3. deleteRental(int rentalId)
 *    - DELETE rental record (rarely used)
 * 
 * 4. getRentalById(int rentalId)
 *    - SELECT rental by ID
 * 
 * 5. getAllRentals()
 *    - SELECT all rental records
 * 
 * 6. getActiveRentals()
 *    - SELECT rentals with status = "Active"
 *    - Currently ongoing rentals
 * 
 * 7. getRentalsByCustomer(int customerId)
 *    - SELECT all rentals for a customer
 *    - Rental history
 * 
 * 8. getRentalsByVehicle(int vehicleId)
 *    - SELECT all rentals for a vehicle
 *    - Vehicle usage history
 * 
 * 9. getOverdueRentals()
 *    - SELECT rentals where actualReturnDate is null AND endDate < NOW()
 *    - For penalty processing
 * 
 * 10. completeRental(int rentalId, Timestamp returnDate)
 *     - UPDATE rental with return date and change status to "Completed"
 * 
 * COLLABORATOR NOTES:
 * - Core transaction table - handle with care
 * - Use transactions for atomicity when creating rentals
 * - Link with VehicleDAO to update vehicle status
 */
public class RentalDAO {
    
    // ==================== CREATE ====================
    
    /**
     * Insert a new rental into database
     * 
     * @param rental The rental object to insert
     * @return true if successful, false otherwise
     */
    public boolean insertRental(RentalTransaction rental) {
        String sql = "INSERT INTO rentals (rentalID, customerID, plateID, locationID, " +
                     "startDateTime, endDateTime) VALUES (?, ?, ?, ?, ?, ?)";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, rental.getRentalID());
            stmt.setString(2, rental.getCustomerID());
            stmt.setString(3, rental.getPlateID());
            stmt.setString(4, rental.getLocationID());
            stmt.setTimestamp(5, rental.getStartDateTime());
            stmt.setTimestamp(6, rental.getEndDateTime());
            
            int rowsAffected = stmt.executeUpdate();
            
            if (rowsAffected > 0) {
                System.out.println("Rental inserted: " + rental.getRentalID());
                return true;
            }
            
        } catch (SQLException e) {
            System.err.println("Error inserting rental: " + e.getMessage());
            e.printStackTrace();
        }
        
        return false;
    }
    
    // ==================== READ ====================
    
    /**
     * Get rental by ID
     * 
     * @param rentalID The rental ID to search for
     * @return Rental object or null if not found
     */
    public RentalTransaction getRentalById(String rentalID) {
        String sql = "SELECT * FROM rentals WHERE rentalID = ?";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, rentalID);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                // Convert database row to Java object
                return extractRentalFromResultSet(rs);
            }
            
        } catch (SQLException e) {
            System.err.println("Error getting rental: " + e.getMessage());
            e.printStackTrace();
        }
        
        return null;
    }
    
    /**
     * Get all rentals
     * 
     * @return List of all rentals
     */
    public List<RentalTransaction> getAllRentals() {
        List<RentalTransaction> rentals = new ArrayList<>();
        String sql = "SELECT * FROM rentals ORDER BY startDateTime DESC";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            
            while (rs.next()) {
                rentals.add(extractRentalFromResultSet(rs));
            }
            
            System.out.println("Retrieved " + rentals.size() + " rental(s)");
            
        } catch (SQLException e) {
            System.err.println("Error getting all rentals: " + e.getMessage());
            e.printStackTrace();
        }
        
        return rentals;
    }
    
    /**
     * Get active rentals (endDateTime is NULL)
     * 
     * @return List of active rentals
     */
    public List<RentalTransaction> getActiveRentals() {
        List<RentalTransaction> rentals = new ArrayList<>();
        String sql = "SELECT * FROM rentals WHERE endDateTime IS NULL ORDER BY startDateTime DESC";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            
            while (rs.next()) {
                rentals.add(extractRentalFromResultSet(rs));
            }
            
            System.out.println("Found " + rentals.size() + " active rental(s)");
            
        } catch (SQLException e) {
            System.err.println("Error getting active rentals: " + e.getMessage());
            e.printStackTrace();
        }
        
        return rentals;
    }
    
    /**
     * Get completed rentals (endDateTime is NOT NULL)
     * 
     * @return List of completed rentals
     */
    public List<RentalTransaction> getCompletedRentals() {
        List<RentalTransaction> rentals = new ArrayList<>();
        String sql = "SELECT * FROM rentals WHERE endDateTime IS NOT NULL ORDER BY endDateTime DESC";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            
            while (rs.next()) {
                rentals.add(extractRentalFromResultSet(rs));
            }
            
        } catch (SQLException e) {
            System.err.println("Error getting completed rentals: " + e.getMessage());
            e.printStackTrace();
        }
        
        return rentals;
    }
    
    /**
     * Get rental history for a customer
     * 
     * @param customerID The customer's ID
     * @return List of customer's rentals
     */
    public List<RentalTransaction> getRentalsByCustomer(String customerID) {
        List<RentalTransaction> rentals = new ArrayList<>();
        String sql = "SELECT * FROM rentals WHERE customerID = ? ORDER BY startDateTime DESC";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, customerID);
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                rentals.add(extractRentalFromResultSet(rs));
            }
            
        } catch (SQLException e) {
            System.err.println("Error getting customer rentals: " + e.getMessage());
            e.printStackTrace();
        }
        
        return rentals;
    }
    
    /**
     * Get rentals for a specific vehicle
     * 
     * @param plateID The vehicle's plate ID
     * @return List of vehicle's rentals
     */
    public List<RentalTransaction> getRentalsByVehicle(String plateID) {
        List<RentalTransaction> rentals = new ArrayList<>();
        String sql = "SELECT * FROM rentals WHERE plateID = ? ORDER BY startDateTime DESC";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, plateID);
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                rentals.add(extractRentalFromResultSet(rs));
            }
            
        } catch (SQLException e) {
            System.err.println("Error getting vehicle rentals: " + e.getMessage());
            e.printStackTrace();
        }
        
        return rentals;
    }
    
    /**
     * Get rentals at a specific location
     * 
     * @param locationID The location ID
     * @return List of rentals at this location
     */
    public List<RentalTransaction> getRentalsByLocation(String locationID) {
        List<RentalTransaction> rentals = new ArrayList<>();
        String sql = "SELECT * FROM rentals WHERE locationID = ? ORDER BY startDateTime DESC";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, locationID);
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                rentals.add(extractRentalFromResultSet(rs));
            }
            
        } catch (SQLException e) {
            System.err.println("Error getting location rentals: " + e.getMessage());
            e.printStackTrace();
        }
        
        return rentals;
    }
    
    /**
     * Check if a vehicle has an active rental
     * 
     * @param plateID The vehicle's plate ID
     * @return true if vehicle is currently rented, false otherwise
     */
    public boolean hasActiveRental(String plateID) {
        String sql = "SELECT COUNT(*) FROM rentals WHERE plateID = ? AND endDateTime IS NULL";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, plateID);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
            
        } catch (SQLException e) {
            System.err.println("Error checking active rental: " + e.getMessage());
            e.printStackTrace();
        }
        
        return false;
    }
    
    /**
     * Get active rental for a specific vehicle
     * 
     * @param plateID The vehicle's plate ID
     * @return Active rental or null if none
     */
    public RentalTransaction getActiveRentalByVehicle(String plateID) {
        String sql = "SELECT * FROM rentals WHERE plateID = ? AND endDateTime IS NULL";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, plateID);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                return extractRentalFromResultSet(rs);
            }
            
        } catch (SQLException e) {
            System.err.println("Error getting active rental: " + e.getMessage());
            e.printStackTrace();
        }
        
        return null;
    }
    
    // ==================== UPDATE ====================
    
    /**
     * Update existing rental
     * 
     * @param rental The rental object with updated data
     * @return true if successful, false otherwise
     */
    public boolean updateRental(RentalTransaction rental) {
        String sql = "UPDATE rentals SET customerID = ?, plateID = ?, locationID = ?, " +
                     "startDateTime = ?, endDateTime = ? WHERE rentalID = ?";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, rental.getCustomerID());
            stmt.setString(2, rental.getPlateID());
            stmt.setString(3, rental.getLocationID());
            stmt.setTimestamp(4, rental.getStartDateTime());
            stmt.setTimestamp(5, rental.getEndDateTime());
            stmt.setString(6, rental.getRentalID());
            
            int rowsAffected = stmt.executeUpdate();
            
            if (rowsAffected > 0) {
                System.out.println("Rental updated: " + rental.getRentalID());
                return true;
            }
            
        } catch (SQLException e) {
            System.err.println("Error updating rental: " + e.getMessage());
            e.printStackTrace();
        }
        
        return false;
    }
    
    /**
     * Complete a rental by setting the end time
     * 
     * @param rentalID The rental ID
     * @param endDateTime The return time
     * @return true if successful, false otherwise
     */
    public boolean completeRental(String rentalID, Timestamp endDateTime) {
        String sql = "UPDATE rentals SET endDateTime = ? WHERE rentalID = ?";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setTimestamp(1, endDateTime);
            stmt.setString(2, rentalID);
            
            int rowsAffected = stmt.executeUpdate();
            
            if (rowsAffected > 0) {
                System.out.println("Rental completed: " + rentalID);
                return true;
            }
            
        } catch (SQLException e) {
            System.err.println("Error completing rental: " + e.getMessage());
            e.printStackTrace();
        }
        
        return false;
    }
    
    // ==================== DELETE ====================
    
    /**
     * Delete rental record
     * Use carefully - may violate referential integrity
     * 
     * @param rentalID The rental ID to delete
     * @return true if successful, false otherwise
     */
    public boolean deleteRental(String rentalID) {
        String sql = "DELETE FROM rentals WHERE rentalID = ?";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, rentalID);
            
            int rowsAffected = stmt.executeUpdate();
            
            if (rowsAffected > 0) {
                System.out.println("Rental deleted: " + rentalID);
                return true;
            } else {
                System.err.println("Rental not found: " + rentalID);
            }
            
        } catch (SQLException e) {
            System.err.println("Error deleting rental: " + e.getMessage());
            System.err.println("Note: Cannot delete if referenced by payments/penalties");
            e.printStackTrace();
        }
        
        return false;
    }
    
    // ==================== HELPER/S ====================
    
    /**
     * Helper method to convert database row to Java object
     * Eliminates code duplication
     */
    private RentalTransaction extractRentalFromResultSet(ResultSet rs) throws SQLException {
        RentalTransaction rental = new RentalTransaction();
        rental.setRentalID(rs.getString("rentalID"));
        rental.setCustomerID(rs.getString("customerID"));
        rental.setPlateID(rs.getString("plateID"));
        rental.setLocationID(rs.getString("locationID"));
        rental.setStartDateTime(rs.getTimestamp("startDateTime"));
        rental.setEndDateTime(rs.getTimestamp("endDateTime"));
        return rental;
    }
}