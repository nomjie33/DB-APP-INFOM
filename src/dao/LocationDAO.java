package dao;

import util.DBConnection;
import model.Location;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object for LOCATION table operations.
 */
public class LocationDAO {
    
    public boolean insertLocation(Location location) {
        String sql = "INSERT INTO locations (locationID, name, status) VALUES (?, ?, ?)";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, location.getLocationID());
            stmt.setString(2, location.getName());
            stmt.setString(3, "Active");
            
            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
            
        } catch (SQLException e) {
            System.err.println("Error inserting location: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
    
    public boolean updateLocation(Location location) {
        String sql = "UPDATE locations SET name = ?, status = ? WHERE locationID = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, location.getName());
            stmt.setString(2, location.getStatus());
            stmt.setString(3, location.getLocationID());

            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
            
        } catch (SQLException e) {
            System.err.println("Error updating location: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    /**
     * SOFT DELETE: Mark a location as inactive instead of physically deleting.
     * This preserves historical data and maintains referential integrity.
     * Deactivate a location (mark as Inactive).
     * 
     * @param locationID Location ID to deactivate
     * @return true if deactivation successful, false otherwise
     */
    public boolean deactivateLocation(String locationID) {
        String sql = "UPDATE locations SET status = 'Inactive' WHERE locationID = ?";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, locationID);
            int rowsAffected = stmt.executeUpdate();
            
            if (rowsAffected > 0) {
                System.out.println("Location " + locationID + " has been marked as Inactive (soft deleted)");
                return true;
            }
            
        } catch (SQLException e) {
            System.err.println("Error deactivating location: " + e.getMessage());
            e.printStackTrace();
        }
        
        return false;
    }

    /**
     * Reactivate a previously deactivated location.
     * 
     * @param locationID Location ID to reactivate
     * @return true if reactivation successful, false otherwise
     */
    public boolean reactivateLocation(String locationID) {
        String sql = "UPDATE locations SET status = 'Active' WHERE locationID = ?";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, locationID);
            int rowsAffected = stmt.executeUpdate();
            
            if (rowsAffected > 0) {
                System.out.println("Location " + locationID + " has been reactivated");
                return true;
            }
            
        } catch (SQLException e) {
            System.err.println("Error reactivating location: " + e.getMessage());
            e.printStackTrace();
        }
        
        return false;
    }
    
    public Location getLocationById(String locationID) {
        String sql = "SELECT * FROM locations WHERE locationID = ?";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, locationID);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                Location location = new Location();
                location.setLocationID(rs.getString("locationID"));
                location.setName(rs.getString("name"));
                location.setStatus(rs.getString("status"));
                return location;
            }
            
        } catch (SQLException e) {
            System.err.println("Error getting location by ID: " + e.getMessage());
            e.printStackTrace();
        }
        
        return null;
    }

    /**
     * Get all ACTIVE locations (excludes inactive)
     */
    public List<Location> getAllLocations() {
        List<Location> locations = new ArrayList<>();
        String sql = "SELECT * FROM locations WHERE status = 'Active' ORDER BY name";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            
            while (rs.next()) {
                Location location = new Location();
                location.setLocationID(rs.getString("locationID"));
                location.setName(rs.getString("name"));
                location.setStatus(rs.getString("status"));
                locations.add(location);
            }
            
        } catch (SQLException e) {
            System.err.println("Error getting all locations: " + e.getMessage());
            e.printStackTrace();
        }
        
        return locations;
    }
    
    /**
     * Get ALL locations including inactive ones.
     * For reporting purposes.
     */
    public List<Location> getAllLocationsIncludingInactive() {
        List<Location> locations = new ArrayList<>();
        String sql = "SELECT * FROM locations ORDER BY name";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            
            while (rs.next()) {
                Location location = new Location();
                location.setLocationID(rs.getString("locationID"));
                location.setName(rs.getString("name"));
                location.setStatus(rs.getString("status"));
                locations.add(location);
            }
            
        } catch (SQLException e) {
            System.err.println("Error getting all locations: " + e.getMessage());
            e.printStackTrace();
        }
        
        return locations;
    }

    public List<Location> getAllLocationsByStatus(String status) {
        List<Location> locations = new ArrayList<>();

        String sql = "SELECT * FROM locations WHERE status = ? ORDER BY name";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, status);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                Location location = new Location();
                location.setLocationID(rs.getString("locationID"));
                location.setName(rs.getString("name"));
                location.setStatus(rs.getString("status"));
                locations.add(location);
            }

        } catch (SQLException e) {
            System.err.println("Error getting locations by status: " + e.getMessage());
            e.printStackTrace();
        }

        return locations;
    }
    
}