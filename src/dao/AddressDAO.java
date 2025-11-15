package dao;

import model.Address;
import model.Barangay;
import model.City;
import util.DBConnection;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object for ADDRESS table operations.
 */
public class AddressDAO {
    
    private BarangayDAO barangayDAO = new BarangayDAO();
    
    /**
     * Insert a new address into the database.
     * 
     * @param address Address object to insert
     * @return true if insertion successful, false otherwise
     */
    public boolean insertAddress(Address address) {
        String sql = "INSERT INTO addresses (barangayID, street) VALUES (?, ?)";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            stmt.setInt(1, address.getBarangayID());
            stmt.setString(2, address.getStreet());
            
            int rowsAffected = stmt.executeUpdate();
            
            if (rowsAffected > 0) {
                // Retrieve the auto-generated addressID
                try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        address.setAddressID(generatedKeys.getInt(1));
                    }
                }
                return true;
            }
            
        } catch (SQLException e) {
            System.err.println("Error inserting address: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }
    
    /**
     * Update an existing address.
     * 
     * @param address Address object with updated information
     * @return true if update successful, false otherwise
     */
    public boolean updateAddress(Address address) {
        String sql = "UPDATE addresses SET barangayID = ?, street = ? WHERE addressID = ?";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, address.getBarangayID());
            stmt.setString(2, address.getStreet());
            stmt.setInt(3, address.getAddressID());
            
            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
            
        } catch (SQLException e) {
            System.err.println("Error updating address: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }
    
    /**
     * Delete an address from the database.
     * Note: This will fail if there are customers referencing this address.
     * 
     * @param addressID Address ID to delete
     * @return true if deletion successful, false otherwise
     */
    public boolean deleteAddress(Integer addressID) {
        String sql = "DELETE FROM addresses WHERE addressID = ?";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, addressID);
            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
            
        } catch (SQLException e) {
            System.err.println("Error deleting address: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }
    
    /**
     * Get an address by its ID.
     * 
     * @param addressID Address ID to retrieve
     * @return Address object if found, null otherwise
     */
    public Address getAddressById(Integer addressID) {
        String sql = "SELECT * FROM addresses WHERE addressID = ?";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, addressID);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return extractAddressFromResultSet(rs, true);
                }
            }
            
        } catch (SQLException e) {
            System.err.println("Error getting address by ID: " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }
    
    /**
     * Get all addresses.
     * 
     * @return List of all addresses
     */
    public List<Address> getAllAddresses() {
        String sql = "SELECT * FROM addresses";
        List<Address> addresses = new ArrayList<>();
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            
            while (rs.next()) {
                addresses.add(extractAddressFromResultSet(rs, false));
            }
            
        } catch (SQLException e) {
            System.err.println("Error getting all addresses: " + e.getMessage());
            e.printStackTrace();
        }
        return addresses;
    }
    
    /**
     * Get all addresses in a specific barangay.
     * 
     * @param barangayID Barangay ID to filter by
     * @return List of addresses in the specified barangay
     */
    public List<Address> getAddressesByBarangay(Integer barangayID) {
        String sql = "SELECT * FROM addresses WHERE barangayID = ?";
        List<Address> addresses = new ArrayList<>();
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, barangayID);
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    addresses.add(extractAddressFromResultSet(rs, true));
                }
            }
            
        } catch (SQLException e) {
            System.err.println("Error getting addresses by barangay: " + e.getMessage());
            e.printStackTrace();
        }
        return addresses;
    }
    
    /**
     * Get all addresses in a specific city.
     * 
     * @param cityID City ID to filter by
     * @return List of addresses in the specified city
     */
    public List<Address> getAddressesByCity(Integer cityID) {
        String sql = "SELECT a.* FROM addresses a " +
                     "JOIN barangays b ON a.barangayID = b.barangayID " +
                     "WHERE b.cityID = ?";
        List<Address> addresses = new ArrayList<>();
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, cityID);
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    addresses.add(extractAddressFromResultSet(rs, true));
                }
            }
            
        } catch (SQLException e) {
            System.err.println("Error getting addresses by city: " + e.getMessage());
            e.printStackTrace();
        }
        return addresses;
    }
    
    /**
     * Get address with full hierarchical information (Address -> Barangay -> City).
     * Uses JOIN query for efficiency.
     * 
     * @param addressID Address ID to retrieve
     * @return Address object with Barangay and City information, null if not found
     */
    public Address getAddressWithFullDetails(Integer addressID) {
        String sql = "SELECT a.*, b.barangayID, b.name as barangayName, " +
                     "c.cityID, c.name as cityName " +
                     "FROM addresses a " +
                     "JOIN barangays b ON a.barangayID = b.barangayID " +
                     "JOIN cities c ON b.cityID = c.cityID " +
                     "WHERE a.addressID = ?";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, addressID);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    Address address = extractAddressFromResultSet(rs, false);
                    
                    // Extract barangay information
                    Barangay barangay = new Barangay();
                    barangay.setBarangayID(rs.getInt("barangayID"));
                    barangay.setName(rs.getString("barangayName"));
                    
                    // Extract city information
                    City city = new City();
                    city.setCityID(rs.getInt("cityID"));
                    city.setName(rs.getString("cityName"));
                    
                    barangay.setCity(city);
                    address.setBarangay(barangay);
                    
                    return address;
                }
            }
            
        } catch (SQLException e) {
            System.err.println("Error getting address with full details: " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }
    
    /**
     * Search addresses by street name pattern.
     * 
     * @param streetPattern Street name pattern to search for (supports wildcards)
     * @return List of matching addresses
     */
    public List<Address> searchAddressesByStreet(String streetPattern) {
        String sql = "SELECT * FROM addresses WHERE street LIKE ?";
        List<Address> addresses = new ArrayList<>();
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, "%" + streetPattern + "%");
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    addresses.add(extractAddressFromResultSet(rs, false));
                }
            }
            
        } catch (SQLException e) {
            System.err.println("Error searching addresses: " + e.getMessage());
            e.printStackTrace();
        }
        return addresses;
    }
    
    /**
     * Find or create an address. If the exact address exists, return it.
     * Otherwise, create a new one.
     * 
     * @param barangayID Barangay ID
     * @param street Street name
     * @return Address object (existing or newly created)
     */
    public Address findOrCreateAddress(Integer barangayID, String street) {
        // First, try to find existing address
        String findSql = "SELECT * FROM addresses WHERE barangayID = ? AND street = ?";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(findSql)) {
            
            stmt.setInt(1, barangayID);
            stmt.setString(2, street);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return extractAddressFromResultSet(rs, true);
                }
            }
            
        } catch (SQLException e) {
            System.err.println("Error finding address: " + e.getMessage());
            e.printStackTrace();
        }
        
        // If not found, create new address
        Address newAddress = new Address(null, barangayID, street);
        if (insertAddress(newAddress)) {
            return newAddress;
        }
        
        return null;
    }
    
    /**
     * Helper method to extract Address object from ResultSet.
     * 
     * @param rs ResultSet to extract from
     * @param loadBarangay Whether to load the associated Barangay object
     */
    private Address extractAddressFromResultSet(ResultSet rs, boolean loadBarangay) throws SQLException {
        Address address = new Address();
        address.setAddressID(rs.getInt("addressID"));
        address.setBarangayID(rs.getInt("barangayID"));
        address.setStreet(rs.getString("street"));
        
        // Optionally load the barangay object with city
        if (loadBarangay) {
            Barangay barangay = barangayDAO.getBarangayWithCity(address.getBarangayID());
            address.setBarangay(barangay);
        }
        
        return address;
    }
}
