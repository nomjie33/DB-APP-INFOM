package dao;

import model.City;
import util.DBConnection;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object for CITY table operations.
 */
public class CityDAO {
    
    /**
     * Insert a new city into the database.
     * 
     * @param city City object to insert
     * @return true if insertion successful, false otherwise
     */
    public boolean insertCity(City city) {
        String sql = "INSERT INTO cities (name) VALUES (?)";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            stmt.setString(1, city.getName());
            
            int rowsAffected = stmt.executeUpdate();
            
            if (rowsAffected > 0) {
                // Retrieve the auto-generated cityID
                try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        city.setCityID(generatedKeys.getInt(1));
                    }
                }
                return true;
            }
            
        } catch (SQLException e) {
            System.err.println("Error inserting city: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }
    
    /**
     * Update an existing city.
     * 
     * @param city City object with updated information
     * @return true if update successful, false otherwise
     */
    public boolean updateCity(City city) {
        String sql = "UPDATE cities SET name = ? WHERE cityID = ?";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, city.getName());
            stmt.setInt(2, city.getCityID());
            
            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
            
        } catch (SQLException e) {
            System.err.println("Error updating city: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }
    
    /**
     * Delete a city from the database.
     * Note: This will fail if there are barangays referencing this city.
     * 
     * @param cityID City ID to delete
     * @return true if deletion successful, false otherwise
     */
    public boolean deleteCity(Integer cityID) {
        String sql = "DELETE FROM cities WHERE cityID = ?";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, cityID);
            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
            
        } catch (SQLException e) {
            System.err.println("Error deleting city: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }
    
    /**
     * Get a city by its ID.
     * 
     * @param cityID City ID to retrieve
     * @return City object if found, null otherwise
     */
    public City getCityById(Integer cityID) {
        String sql = "SELECT * FROM cities WHERE cityID = ?";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, cityID);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return extractCityFromResultSet(rs);
                }
            }
            
        } catch (SQLException e) {
            System.err.println("Error getting city by ID: " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }
    
    /**
     * Get a city by its name.
     * 
     * @param name City name to search for
     * @return City object if found, null otherwise
     */
    public City getCityByName(String name) {
        String sql = "SELECT * FROM cities WHERE name = ?";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, name);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return extractCityFromResultSet(rs);
                }
            }
            
        } catch (SQLException e) {
            System.err.println("Error getting city by name: " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }
    
    /**
     * Get all cities.
     * 
     * @return List of all cities
     */
    public List<City> getAllCities() {
        String sql = "SELECT * FROM cities ORDER BY name";
        List<City> cities = new ArrayList<>();
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            
            while (rs.next()) {
                cities.add(extractCityFromResultSet(rs));
            }
            
        } catch (SQLException e) {
            System.err.println("Error getting all cities: " + e.getMessage());
            e.printStackTrace();
        }
        return cities;
    }
    
    /**
     * Search cities by name pattern.
     * 
     * @param namePattern Name pattern to search for (supports wildcards)
     * @return List of matching cities
     */
    public List<City> searchCitiesByName(String namePattern) {
        String sql = "SELECT * FROM cities WHERE name LIKE ? ORDER BY name";
        List<City> cities = new ArrayList<>();
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, "%" + namePattern + "%");
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    cities.add(extractCityFromResultSet(rs));
                }
            }
            
        } catch (SQLException e) {
            System.err.println("Error searching cities: " + e.getMessage());
            e.printStackTrace();
        }
        return cities;
    }
    
    /**
     * Helper method to extract City object from ResultSet.
     */
    private City extractCityFromResultSet(ResultSet rs) throws SQLException {
        City city = new City();
        city.setCityID(rs.getInt("cityID"));
        city.setName(rs.getString("name"));
        return city;
    }
}
