package dao;

import util.DBConnection;
import model.Location;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object for LOCATION table operations.
 * 
 * PURPOSE: Handles all database CRUD operations for locations/branches table.
 * 
 * METHODS TO IMPLEMENT:
 * 
 * 1. insertLocation(Location location)
 *    - INSERT new branch/location 
 * 
 * 2. updateLocation(Location location)
 *    - UPDATE existing location record
 * 
 * 3. deleteLocation(int locationId)
 *    - DELETE location by ID
 * 
 * 4. getLocationById(int locationId)
 *    - SELECT location by ID
 * 
 * 5. getAllLocations()
 *    - SELECT all locations
 * 
 * 6. getLocationsByCity(String city)
 *    - SELECT locations in a specific city
 *    - For regional filtering
 * 
 * COLLABORATOR NOTES:
 * - Locations are central to vehicle deployment
 * - Used in reports for location-based analytics
 */
public class LocationDAO {
    
    // TODO: Implement insertLocation(Location location)
    public boolean insertLocation(Location location) {
        String sql = "INSERT INTO locations (locationID, name) VALUES (?, ?)";
        
            // Connect to database and prepare statement
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            // Set values for placeholders
            stmt.setString(1, location.getLocationID());  // 1st ? = locationID
            stmt.setString(2, location.getName());        // 2nd ? = name
            
            // Execute INSERT
            int rowsAffected = stmt.executeUpdate();
            
            // Return true if at least 1 row was inserted
            return rowsAffected > 0;
            
        } catch (SQLException e) {
            // Handle errors
            System.err.println("Error inserting location: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
    
    // TODO: Implement updateLocation(Location location)
    public boolean updateLocation(Location location) {

        String sql = "UPDATE locations SET name = ? WHERE locationID = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, location.getName());        // 1st ? = new name
            stmt.setString(2, location.getLocationID());  // 2nd ? = which location to update

            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
            
        } catch (SQLException e) {
            System.err.println("Error updating location: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    // TODO: Implement deleteLocation(int locationId)
    public boolean deleteLocation(String locationID)
    {
        String sql = "DELETE FROM locations WHERE locationID = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, locationID);  

            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
            
        } catch (SQLException e) {
            System.err.println("Error deleting location: " + e.getMessage());
            e.printStackTrace();
            return false;
        }

    }
    
    // TODO: Implement getLocationById(int locationId)
    public Location getLocationById(String locationID)
    {
        String sql = "SELECT * FROM locations WHERE locationID = ?";
            try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, locationID);
            
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                Location location = new Location();
                location.setLocationID(rs.getString("locationID"));
                location.setName(rs.getString("name"));
                                return location;
            }
            
        } catch (SQLException e) {
            System.err.println("Error getting location by ID: " + e.getMessage());
            e.printStackTrace();
        }
        
        return null;
    }


    // TODO: Implement getAllLocations()
    public List<Location> getAllLocations(){
        List<Location> locations = new ArrayList<>();

        String sql = "SELECT * FROM locations ORDER BY name";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            
            // Loop through all rows
            while (rs.next()) {  // Keep going while there are rows
                
                // Build Location object from each row
                Location location = new Location();
                location.setLocationID(rs.getString("locationID"));
                location.setName(rs.getString("name"));
                locations.add(location);
            }
            
        } catch (SQLException e) {
            System.err.println("Error getting all locations: " + e.getMessage());
            e.printStackTrace();
        }
        
        return locations;
    }
    
}
