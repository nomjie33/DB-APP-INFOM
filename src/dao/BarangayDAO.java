package dao;

import model.Barangay;
import model.City;
import util.DBConnection;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object for BARANGAY table operations.
 */
public class BarangayDAO {
    
    private CityDAO cityDAO = new CityDAO();
    
    /**
     * Insert a new barangay into the database.
     * 
     * @param barangay Barangay object to insert
     * @return true if insertion successful, false otherwise
     */
    public boolean insertBarangay(Barangay barangay) {
        String sql = "INSERT INTO barangays (cityID, name) VALUES (?, ?)";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            stmt.setInt(1, barangay.getCityID());
            stmt.setString(2, barangay.getName());
            
            int rowsAffected = stmt.executeUpdate();
            
            if (rowsAffected > 0) {
                // Retrieve the auto-generated barangayID
                try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        barangay.setBarangayID(generatedKeys.getInt(1));
                    }
                }
                return true;
            }
            
        } catch (SQLException e) {
            System.err.println("Error inserting barangay: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }
    
    /**
     * Update an existing barangay.
     * 
     * @param barangay Barangay object with updated information
     * @return true if update successful, false otherwise
     */
    public boolean updateBarangay(Barangay barangay) {
        String sql = "UPDATE barangays SET cityID = ?, name = ? WHERE barangayID = ?";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, barangay.getCityID());
            stmt.setString(2, barangay.getName());
            stmt.setInt(3, barangay.getBarangayID());
            
            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
            
        } catch (SQLException e) {
            System.err.println("Error updating barangay: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }
    
    /**
     * Delete a barangay from the database.
     * Note: This will fail if there are addresses referencing this barangay.
     * 
     * @param barangayID Barangay ID to delete
     * @return true if deletion successful, false otherwise
     */
    public boolean deleteBarangay(Integer barangayID) {
        String sql = "DELETE FROM barangays WHERE barangayID = ?";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, barangayID);
            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
            
        } catch (SQLException e) {
            System.err.println("Error deleting barangay: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }
    
    /**
     * Get a barangay by its ID.
     * 
     * @param barangayID Barangay ID to retrieve
     * @return Barangay object if found, null otherwise
     */
    public Barangay getBarangayById(Integer barangayID) {
        String sql = "SELECT * FROM barangays WHERE barangayID = ?";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, barangayID);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return extractBarangayFromResultSet(rs, true);
                }
            }
            
        } catch (SQLException e) {
            System.err.println("Error getting barangay by ID: " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }
    
    /**
     * Get all barangays.
     * 
     * @return List of all barangays
     */
    public List<Barangay> getAllBarangays() {
        String sql = "SELECT * FROM barangays ORDER BY name";
        List<Barangay> barangays = new ArrayList<>();
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            
            while (rs.next()) {
                barangays.add(extractBarangayFromResultSet(rs, false));
            }
            
        } catch (SQLException e) {
            System.err.println("Error getting all barangays: " + e.getMessage());
            e.printStackTrace();
        }
        return barangays;
    }
    
    /**
     * Get all barangays in a specific city.
     * 
     * @param cityID City ID to filter by
     * @return List of barangays in the specified city
     */
    public List<Barangay> getBarangaysByCity(Integer cityID) {
        String sql = "SELECT * FROM barangays WHERE cityID = ? ORDER BY name";
        List<Barangay> barangays = new ArrayList<>();
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, cityID);
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    barangays.add(extractBarangayFromResultSet(rs, true));
                }
            }
            
        } catch (SQLException e) {
            System.err.println("Error getting barangays by city: " + e.getMessage());
            e.printStackTrace();
        }
        return barangays;
    }
    
    /**
     * Search barangays by name pattern.
     * 
     * @param namePattern Name pattern to search for (supports wildcards)
     * @return List of matching barangays
     */
    public List<Barangay> searchBarangaysByName(String namePattern) {
        String sql = "SELECT * FROM barangays WHERE name LIKE ? ORDER BY name";
        List<Barangay> barangays = new ArrayList<>();
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, "%" + namePattern + "%");
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    barangays.add(extractBarangayFromResultSet(rs, false));
                }
            }
            
        } catch (SQLException e) {
            System.err.println("Error searching barangays: " + e.getMessage());
            e.printStackTrace();
        }
        return barangays;
    }
    
    /**
     * Get barangay with its associated city information (JOIN query).
     * 
     * @param barangayID Barangay ID to retrieve
     * @return Barangay object with City information, null if not found
     */
    public Barangay getBarangayWithCity(Integer barangayID) {
        String sql = "SELECT b.*, c.cityID, c.name as cityName " +
                     "FROM barangays b " +
                     "JOIN cities c ON b.cityID = c.cityID " +
                     "WHERE b.barangayID = ?";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, barangayID);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    Barangay barangay = extractBarangayFromResultSet(rs, false);
                    
                    // Extract city information
                    City city = new City();
                    city.setCityID(rs.getInt("cityID"));
                    city.setName(rs.getString("cityName"));
                    barangay.setCity(city);
                    
                    return barangay;
                }
            }
            
        } catch (SQLException e) {
            System.err.println("Error getting barangay with city: " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }
    
    /**
     * Helper method to extract Barangay object from ResultSet.
     * 
     * @param rs ResultSet to extract from
     * @param loadCity Whether to load the associated City object
     */
    private Barangay extractBarangayFromResultSet(ResultSet rs, boolean loadCity) throws SQLException {
        Barangay barangay = new Barangay();
        barangay.setBarangayID(rs.getInt("barangayID"));
        barangay.setCityID(rs.getInt("cityID"));
        barangay.setName(rs.getString("name"));
        
        // Optionally load the city object
        if (loadCity) {
            City city = cityDAO.getCityById(barangay.getCityID());
            barangay.setCity(city);
        }
        
        return barangay;
    }
}
