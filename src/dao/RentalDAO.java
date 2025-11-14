package dao;

import model.RentalTransaction;
import util.DBConnection;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object for RENTAL TRANSACTION table operations.
 */
public class RentalDAO {
    
    // ==================== CREATE ====================
    
    public boolean insertRental(RentalTransaction rental) {
        String sql = "INSERT INTO rentals (rentalID, customerID, plateID, locationID, " +
                     "pickUpDateTime, startDateTime, endDateTime, status) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, rental.getRentalID());
            stmt.setString(2, rental.getCustomerID());
            stmt.setString(3, rental.getPlateID());
            stmt.setString(4, rental.getLocationID());
            stmt.setTimestamp(5, rental.getPickUpDateTime());
            stmt.setTimestamp(6, rental.getStartDateTime());
            stmt.setTimestamp(7, rental.getEndDateTime());
            stmt.setString(8, "Active");
            
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
    
    public RentalTransaction getRentalById(String rentalID) {
        String sql = "SELECT * FROM rentals WHERE rentalID = ?";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, rentalID);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                return extractRentalFromResultSet(rs);
            }
            
        } catch (SQLException e) {
            System.err.println("Error getting rental: " + e.getMessage());
            e.printStackTrace();
        }
        
        return null;
    }
    
    /**
     * Get all ACTIVE rentals (excludes cancelled)
     */
    public List<RentalTransaction> getAllRentals() {
        List<RentalTransaction> rentals = new ArrayList<>();
        String sql = "SELECT * FROM rentals WHERE status != 'Cancelled' ORDER BY startDateTime DESC";
        
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
     * Get ALL rentals including cancelled ones.
     * For reporting purposes.
     */
    public List<RentalTransaction> getAllRentalsIncludingCancelled() {
        List<RentalTransaction> rentals = new ArrayList<>();
        String sql = "SELECT * FROM rentals ORDER BY startDateTime DESC";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            
            while (rs.next()) {
                rentals.add(extractRentalFromResultSet(rs));
            }
            
        } catch (SQLException e) {
            System.err.println("Error getting all rentals: " + e.getMessage());
            e.printStackTrace();
        }
        
        return rentals;
    }
    
    public List<RentalTransaction> getActiveRentals() {
        List<RentalTransaction> rentals = new ArrayList<>();
        String sql = "SELECT * FROM rentals WHERE endDateTime IS NULL AND status = 'Active' ORDER BY startDateTime DESC";
        
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
    
    public List<RentalTransaction> getCompletedRentals() {
        List<RentalTransaction> rentals = new ArrayList<>();
        String sql = "SELECT * FROM rentals WHERE endDateTime IS NOT NULL AND status = 'Completed' ORDER BY endDateTime DESC";
        
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
    
    public List<RentalTransaction> getRentalsByCustomer(String customerID) {
        List<RentalTransaction> rentals = new ArrayList<>();
        String sql = "SELECT * FROM rentals WHERE customerID = ? AND status != 'Cancelled' ORDER BY startDateTime DESC";
        
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
    
    public List<RentalTransaction> getRentalsByVehicle(String plateID) {
        List<RentalTransaction> rentals = new ArrayList<>();
        String sql = "SELECT * FROM rentals WHERE plateID = ? AND status != 'Cancelled' ORDER BY startDateTime DESC";
        
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
    
    public List<RentalTransaction> getRentalsByLocation(String locationID) {
        List<RentalTransaction> rentals = new ArrayList<>();
        String sql = "SELECT * FROM rentals WHERE locationID = ? AND status != 'Cancelled' ORDER BY startDateTime DESC";
        
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
    
    public boolean hasActiveRental(String plateID) {
        String sql = "SELECT COUNT(*) FROM rentals WHERE plateID = ? AND endDateTime IS NULL AND status = 'Active'";
        
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
    
    public RentalTransaction getActiveRentalByVehicle(String plateID) {
        String sql = "SELECT * FROM rentals WHERE plateID = ? AND endDateTime IS NULL AND status = 'Active'";
        
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
    
    public boolean updateRental(RentalTransaction rental) {
        String sql = "UPDATE rentals SET customerID = ?, plateID = ?, locationID = ?, " +
                     "pickUpDateTime = ?, startDateTime = ?, endDateTime = ?, status = ? WHERE rentalID = ?";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, rental.getCustomerID());
            stmt.setString(2, rental.getPlateID());
            stmt.setString(3, rental.getLocationID());
            stmt.setTimestamp(4, rental.getPickUpDateTime());
            stmt.setTimestamp(5, rental.getStartDateTime());
            stmt.setTimestamp(6, rental.getEndDateTime());
            stmt.setString(7, rental.getStatus());
            stmt.setString(8, rental.getRentalID());
            
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
    
    public boolean completeRental(String rentalID, Timestamp endDateTime) {
        String sql = "UPDATE rentals SET endDateTime = ?, status = 'Completed' WHERE rentalID = ?";
        
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
     * SOFT DELETE: Mark a rental as cancelled instead of physically deleting.
     * This preserves historical data and maintains referential integrity.
     * Cancel a rental (mark as Cancelled).
     * 
     * @param rentalID Rental ID to cancel
     * @return true if cancellation successful, false otherwise
     */
    public boolean cancelRental(String rentalID) {
        String sql = "UPDATE rentals SET status = 'Cancelled' WHERE rentalID = ?";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, rentalID);
            int rowsAffected = stmt.executeUpdate();
            
            if (rowsAffected > 0) {
                System.out.println("Rental " + rentalID + " has been marked as Cancelled (soft deleted)");
                return true;
            }
            
        } catch (SQLException e) {
            System.err.println("Error cancelling rental: " + e.getMessage());
            e.printStackTrace();
        }
        
        return false;
    }
    
    // ==================== HELPER ====================
    
    /**
     * Helper method to convert database row to Java object
     */
    private RentalTransaction extractRentalFromResultSet(ResultSet rs) throws SQLException {
        RentalTransaction rental = new RentalTransaction();
        rental.setRentalID(rs.getString("rentalID"));
        rental.setCustomerID(rs.getString("customerID"));
        rental.setPlateID(rs.getString("plateID"));
        rental.setLocationID(rs.getString("locationID"));
        rental.setPickUpDateTime(rs.getTimestamp("pickUpDateTime"));
        rental.setStartDateTime(rs.getTimestamp("startDateTime"));
        rental.setEndDateTime(rs.getTimestamp("endDateTime"));
        rental.setStatus(rs.getString("status"));
        return rental;
    }
}