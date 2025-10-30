package dao;

import model.Vehicle;
import util.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object for VEHICLE table operations.
 * 
 * PURPOSE: Handles all database CRUD operations for vehicles table.
 * 
 * METHODS TO IMPLEMENT:
 * 
 * 1. insertVehicle(Vehicle vehicle)
 *    - INSERT new vehicle into database
 *    - Return generated vehicle ID or boolean success
 * 
 * 2. updateVehicle(Vehicle vehicle)
 *    - UPDATE existing vehicle record
 *    - Match by vehicle ID
 * 
 * 3. deleteVehicle(int vehicleId)
 *    - DELETE vehicle by ID
 *    - Consider dependencies (rentals, maintenance)
 * 
 * 4. getVehicleById(int vehicleId)
 *    - SELECT vehicle by ID
 *    - Return Vehicle object or null
 * 
 * 5. getAllVehicles()
 *    - SELECT all vehicles
 *    - Return List<Vehicle>
 * 
 * 6. getAvailableVehicles()
 *    - SELECT vehicles with status = "Available"
 *    - Critical for rental operations
 * 
 * 7. getVehiclesByLocation(int locationId)
 *    - SELECT vehicles at a specific branch
 *    - For location-based searches
 * 
 * 8. updateVehicleStatus(int vehicleId, String newStatus)
 *    - UPDATE only the status field
 *    - Used frequently during rentals and maintenance
 * 
 * 9. getVehiclesByStatus(String status)
 *    - SELECT vehicles with specific status
 *    - For reports and filtering
 * 
 * COLLABORATOR NOTES:
 * - Status changes are critical - log them if needed
 * - Validate status values before updating
 * - Use PreparedStatement for all queries
 */
public class VehicleDAO {
    private static final String STATUS_AVAILABLE = "Available";
    private static final String STATUS_IN_USE = "In Use";
    private static final String STATUS_MAINTENANCE = "Maintenance";
    
    // TODO: Implement insertVehicle(Vehicle vehicle)
    public boolean insertVehicle(Vehicle vehicle) {
        String sql = "INSERT INTO vehicles (plateID, vehicleType, vehicleModel, status, rentalPrice) " +
                "VALUES (?, ?, ?, ?, ?)";
        
        try (Connection conn = DBConnection.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, vehicle.getPlateID());
            stmt.setString(2, vehicle.getVehicleType());
            stmt.setString(3, vehicle.getVehicleModel());
            stmt.setString(4, vehicle.getStatus());
            stmt.setDouble(5, vehicle.getRentalPrice());
            
            int rowsAffected = stmt.executeUpdate();
            
            if (rowsAffected > 0) {
                System.out.println("✓ Vehicle inserted: " + vehicle.getPlateID());
                return true;
            }
            
        } catch (SQLException e) {
            System.err.println("Error inserting vehicle: " + e.getMessage());
            e.printStackTrace();
        }
        
        return false;
    }

    // TODO: Implement updateVehicle(Vehicle vehicle)
    public boolean updateVehicle(Vehicle vehicle) {
        String sql = "UPDATE vehicles SET vehicleType = ?, vehicleModel = ?, " +
                    "status = ?, rentalPrice = ? WHERE plateID = ?";
        
        try (Connection conn = DBConnection.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, vehicle.getVehicleType());
            stmt.setString(2, vehicle.getVehicleModel());
            stmt.setString(3, vehicle.getStatus());
            stmt.setDouble(4, vehicle.getRentalPrice());
            stmt.setString(5, vehicle.getPlateID());
            
            int rowsAffected = stmt.executeUpdate();
            
            if (rowsAffected > 0) {
                System.out.println("✓ Vehicle updated: " + vehicle.getPlateID());
                return true;
            }
            
        } catch (SQLException e) {
            System.err.println("Error updating vehicle: " + e.getMessage());
            e.printStackTrace();
        }
        
        return false;
    }
    
    // TODO: Implement deleteVehicle(int vehicleId)
    public boolean deleteVehicle(String plateID) {
        String sql = "DELETE FROM vehicles WHERE plateID = ?";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, plateID);
            
            int rowsAffected = stmt.executeUpdate();
            
            if (rowsAffected > 0) {
                System.out.println("✓ Vehicle deleted: " + plateID);
                return true;
            } else {
                System.err.println("✗ Vehicle not found: " + plateID);
            }
            
        } catch (SQLException e) {
            System.err.println("Error deleting vehicle: " + e.getMessage());
            System.err.println("Note: Cannot delete vehicle if referenced in rentals/maintenance");
            e.printStackTrace();
        }
        
        return false;
    }

    // TODO: Implement getVehicleById(int vehicleId)
    public Vehicle getVehicleById(String plateID) {
        String sql = "SELECT * FROM vehicles WHERE plateID = ?";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, plateID);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                return extractVehicleFromResultSet(rs);
            }
            
        } catch (SQLException e) {
            System.err.println("Error getting vehicle by ID: " + e.getMessage());
            e.printStackTrace();
        }
        
        return null;
    }
    
    // TODO: Implement getAllVehicles()
    public List<Vehicle> getAllVehicles() {
        List<Vehicle> vehicles = new ArrayList<>();
        String sql = "SELECT * FROM vehicles ORDER BY plateID";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            
            while (rs.next()) {
                vehicles.add(extractVehicleFromResultSet(rs));
            }
            
            System.out.println("✓ Retrieved " + vehicles.size() + " vehicle(s)");
            
        } catch (SQLException e) {
            System.err.println("Error getting all vehicles: " + e.getMessage());
            e.printStackTrace();
        }
        
        return vehicles;
    }
    
    // TODO: Implement getAvailableVehicles()
    public List<Vehicle> getAvailableVehicles() {
        List<Vehicle> vehicles = new ArrayList<>();
        String sql = "SELECT * FROM vehicles WHERE status = ? ORDER BY vehicleType, rentalPrice";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, STATUS_AVAILABLE);
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                vehicles.add(extractVehicleFromResultSet(rs));
            }
            
            System.out.println("✓ Found " + vehicles.size() + " available vehicle(s)");
            
        } catch (SQLException e) {
            System.err.println("Error getting available vehicles: " + e.getMessage());
            e.printStackTrace();
        }
        
        return vehicles;
    }
    // TODO: Implement getVehiclesByLocation(int locationId)
        public List<Vehicle> getVehiclesByLocation(String locationID) {
        List<Vehicle> vehicles = new ArrayList<>();
        
        // TODO: Update this query based on your actual schema
        // If vehicles table has locationID field:
        String sql = "SELECT * FROM vehicles WHERE locationID = ? ORDER BY vehicleType";
        
        // If you have a separate deployments table:
        // String sql = "SELECT v.* FROM vehicles v " +
        //              "JOIN deployments d ON v.plateID = d.plateID " +
        //              "WHERE d.locationID = ? AND d.endDate IS NULL";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, locationID);
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                vehicles.add(extractVehicleFromResultSet(rs));
            }
            
            System.out.println("✓ Found " + vehicles.size() + " vehicle(s) at location " + locationID);
            
        } catch (SQLException e) {
            System.err.println("Error getting vehicles by location: " + e.getMessage());
            System.err.println("Note: Ensure vehicles table has locationID or adjust query for deployments table");
            e.printStackTrace();
        }
        
        return vehicles;
    }
    
    // TODO: Implement updateVehicleStatus(int vehicleId, String newStatus)
        public boolean updateVehicleStatus(String plateID, String newStatus) {
        // Validate status
        if (!isValidStatus(newStatus)) {
            System.err.println("✗ Invalid status: " + newStatus);
            System.err.println("Valid statuses: Available, In Use, Maintenance");
            return false;
        }
        
        String sql = "UPDATE vehicles SET status = ? WHERE plateID = ?";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, newStatus);
            stmt.setString(2, plateID);
            
            int rowsAffected = stmt.executeUpdate();
            
            if (rowsAffected > 0) {
                System.out.println("✓ Vehicle " + plateID + " status updated to: " + newStatus);
                // TODO: Log status change to audit table if needed
                return true;
            } else {
                System.err.println("✗ Vehicle not found: " + plateID);
            }
            
        } catch (SQLException e) {
            System.err.println("Error updating vehicle status: " + e.getMessage());
            e.printStackTrace();
        }
        
        return false;
    }
    // TODO: Implement getVehiclesByStatus(String status)
    public List<Vehicle> getVehiclesByStatus(String status) {
        List<Vehicle> vehicles = new ArrayList<>();
        String sql = "SELECT * FROM vehicles WHERE status = ? ORDER BY vehicleType, vehicleModel";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, status);
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                vehicles.add(extractVehicleFromResultSet(rs));
            }
            
            System.out.println("✓ Found " + vehicles.size() + " vehicle(s) with status: " + status);
            
        } catch (SQLException e) {
            System.err.println("Error getting vehicles by status: " + e.getMessage());
            e.printStackTrace();
        }
        
        return vehicles;
    }

    public List<Vehicle> getVehiclesByType(String vehicleType) {
        List<Vehicle> vehicles = new ArrayList<>();
        String sql = "SELECT * FROM vehicles WHERE vehicleType = ? ORDER BY vehicleModel";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, vehicleType);
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                vehicles.add(extractVehicleFromResultSet(rs));
            }
            
        } catch (SQLException e) {
            System.err.println("Error getting vehicles by type: " + e.getMessage());
            e.printStackTrace();
        }
        
        return vehicles;
    }
    // ==== HELPER FUNCTIONS ====

    /**
     * Helper method to extract Vehicle object from ResultSet.
     * Eliminates code duplication across all query methods.
     */
    private Vehicle extractVehicleFromResultSet(ResultSet rs) throws SQLException {
        Vehicle vehicle = new Vehicle();
        vehicle.setPlateID(rs.getString("plateID"));
        vehicle.setVehicleType(rs.getString("vehicleType"));
        vehicle.setVehicleModel(rs.getString("vehicleModel"));
        vehicle.setStatus(rs.getString("status"));
        vehicle.setRentalPrice(rs.getDouble("rentalPrice"));
        return vehicle;
    }
    
    /**
     * Validates if the given status is one of the allowed values.
     */
    private boolean isValidStatus(String status) {
        return STATUS_AVAILABLE.equalsIgnoreCase(status) ||
               STATUS_IN_USE.equalsIgnoreCase(status) ||
               STATUS_MAINTENANCE.equalsIgnoreCase(status);
    }
    
}
