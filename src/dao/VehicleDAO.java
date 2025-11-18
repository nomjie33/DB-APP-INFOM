package dao;

import model.Vehicle;
import util.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object for VEHICLE table operations.
 * 
 * SOFT DELETE IMPLEMENTATION:
 * This DAO implements soft delete using the 'status' field with dual purposes:
 * 
 * 1. ACTIVE/INACTIVE (Record-level):
 *    - 'Inactive': Vehicle is soft-deleted/retired
 *    - All other statuses: Vehicle is active
 * 
 * 2. OPERATIONAL STATUS (Active vehicles only):
 *    - 'Available': Ready for rental
 *    - 'In Use': Currently rented out
 *    - 'Maintenance': Under repair
 * 
 * QUERY BEHAVIOR:
 * - getAllVehicles(): Returns only active vehicles (status != 'Inactive')
 * - getAllVehiclesIncludingInactive(): Returns ALL vehicles for reporting
 * - getAvailableVehicles(): Returns only vehicles with status = 'Available'
 * - getVehiclesByLocation(): Automatically excludes inactive vehicles
 * - getVehiclesByType(): Automatically excludes inactive vehicles
 * - getVehiclesByStatus(): Can query any status including 'Inactive'
 * - getVehicleById(): Returns vehicle regardless of status (for lookups)
 * 
 * MODIFICATION METHODS:
 * - deactivateVehicle(): Soft delete (sets status = 'Inactive')
 * - reactivateVehicle(): Restore vehicle (sets status = 'Available')
 * - updateVehicleStatus(): Change operational status (validates allowed values)
 */
public class VehicleDAO {
    private static final String STATUS_AVAILABLE = "Available";
    private static final String STATUS_IN_USE = "In Use";
    private static final String STATUS_MAINTENANCE = "Maintenance";
    private static final String STATUS_INACTIVE = "Inactive";
    
    public boolean insertVehicle(Vehicle vehicle) {
        String sql = "INSERT INTO vehicles (plateID, vehicleType, status, rentalPrice) " +
                "VALUES (?, ?, ?, ?)";
        
        try (Connection conn = DBConnection.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, vehicle.getPlateID());
            stmt.setString(2, vehicle.getVehicleType());
            stmt.setString(3, vehicle.getStatus());
            stmt.setDouble(4, vehicle.getRentalPrice());
            
            int rowsAffected = stmt.executeUpdate();
            
            if (rowsAffected > 0) {
                System.out.println("Vehicle inserted: " + vehicle.getPlateID());
                return true;
            }
            
        } catch (SQLException e) {
            System.err.println("Error inserting vehicle: " + e.getMessage());
            e.printStackTrace();
        }
        
        return false;
    }

    public boolean updateVehicle(Vehicle vehicle) {
        String sql = "UPDATE vehicles SET vehicleType = ?, " +
                    "status = ?, rentalPrice = ? WHERE plateID = ?";
        
        try (Connection conn = DBConnection.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, vehicle.getVehicleType());
            stmt.setString(2, vehicle.getStatus());
            stmt.setDouble(3, vehicle.getRentalPrice());
            stmt.setString(4, vehicle.getPlateID());
            
            int rowsAffected = stmt.executeUpdate();
            
            if (rowsAffected > 0) {
                System.out.println("Vehicle updated: " + vehicle.getPlateID());
                return true;
            }
            
        } catch (SQLException e) {
            System.err.println("Error updating vehicle: " + e.getMessage());
            e.printStackTrace();
        }
        
        return false;
    }
    
    /**
     * SOFT DELETE: Mark a vehicle as inactive instead of physically deleting.
     * This preserves historical data and maintains referential integrity.
     * Deactivate a vehicle (mark as Inactive).
     * 
     * @param plateID Vehicle plate ID to deactivate
     * @return true if deactivation successful, false otherwise
     */
    public boolean deactivateVehicle(String plateID) {
        String sql = "UPDATE vehicles SET status = 'Inactive' WHERE plateID = ?";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, plateID);
            int rowsAffected = stmt.executeUpdate();
            
            if (rowsAffected > 0) {
                System.out.println("Vehicle " + plateID + " has been marked as Inactive (soft deleted)");
                return true;
            }
            
        } catch (SQLException e) {
            System.err.println("Error deactivating vehicle: " + e.getMessage());
            e.printStackTrace();
        }
        
        return false;
    }
    
    /**
     * Reactivate a previously deactivated vehicle.
     * Sets status back to Available.
     * 
     * @param plateID Vehicle plate ID to reactivate
     * @return true if reactivation successful, false otherwise
     */
    public boolean reactivateVehicle(String plateID) {
        String sql = "UPDATE vehicles SET status = 'Available' WHERE plateID = ?";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, plateID);
            int rowsAffected = stmt.executeUpdate();
            
            if (rowsAffected > 0) {
                System.out.println("Vehicle " + plateID + " has been reactivated");
                return true;
            }
            
        } catch (SQLException e) {
            System.err.println("Error reactivating vehicle: " + e.getMessage());
            e.printStackTrace();
        }
        
        return false;
    }

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
    
    /**
     * Get all ACTIVE vehicles (excludes Inactive/retired)
     */
    public List<Vehicle> getAllVehicles() {
        List<Vehicle> vehicles = new ArrayList<>();
        String sql = "SELECT * FROM vehicles WHERE status != 'Inactive' ORDER BY plateID";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            
            while (rs.next()) {
                vehicles.add(extractVehicleFromResultSet(rs));
            }
            
            System.out.println("Retrieved " + vehicles.size() + " vehicle(s)");
            
        } catch (SQLException e) {
            System.err.println("Error getting all vehicles: " + e.getMessage());
            e.printStackTrace();
        }
        
        return vehicles;
    }
    
    /**
     * Get ALL vehicles including inactive/retired ones.
     * For reporting purposes.
     */
    public List<Vehicle> getAllVehiclesIncludingInactive() {
        List<Vehicle> vehicles = new ArrayList<>();
        String sql = "SELECT * FROM vehicles ORDER BY plateID";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            
            while (rs.next()) {
                vehicles.add(extractVehicleFromResultSet(rs));
            }
            
        } catch (SQLException e) {
            System.err.println("Error getting all vehicles: " + e.getMessage());
            e.printStackTrace();
        }
        
        return vehicles;
    }
    
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
            
            System.out.println("Found " + vehicles.size() + " available vehicle(s)");
            
        } catch (SQLException e) {
            System.err.println("Error getting available vehicles: " + e.getMessage());
            e.printStackTrace();
        }
        
        return vehicles;
    }
    
    public List<Vehicle> getVehiclesByLocation(String locationID) {
        List<Vehicle> vehicles = new ArrayList<>();
        String sql = "SELECT v.* FROM vehicles v " +
                     "JOIN deployments d ON v.plateID = d.plateID " +
                     "WHERE d.locationID = ? AND d.endDate IS NULL AND v.status != 'Inactive'";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, locationID);
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                vehicles.add(extractVehicleFromResultSet(rs));
            }
            
        } catch (SQLException e) {
            System.err.println("Error getting vehicles by location: " + e.getMessage());
            e.printStackTrace();
        }
        
        return vehicles;
    }
    
    public boolean updateVehicleStatus(String plateID, String newStatus) {
        if (!isValidStatus(newStatus)) {
            System.err.println("Invalid status: " + newStatus);
            return false;
        }
        
        String sql = "UPDATE vehicles SET status = ? WHERE plateID = ?";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, newStatus);
            stmt.setString(2, plateID);
            
            int rowsAffected = stmt.executeUpdate();
            
            if (rowsAffected > 0) {
                System.out.println("Vehicle " + plateID + " status updated to: " + newStatus);
                return true;
            }
            
        } catch (SQLException e) {
            System.err.println("Error updating vehicle status: " + e.getMessage());
            e.printStackTrace();
        }
        
        return false;
    }
    
    public List<Vehicle> getVehiclesByStatus(String status) {
        List<Vehicle> vehicles = new ArrayList<>();
        String sql = "SELECT * FROM vehicles WHERE status = ? ORDER BY vehicleType, plateID";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, status);
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                vehicles.add(extractVehicleFromResultSet(rs));
            }
            
        } catch (SQLException e) {
            System.err.println("Error getting vehicles by status: " + e.getMessage());
            e.printStackTrace();
        }
        
        return vehicles;
    }

    public List<Vehicle> getVehiclesByType(String vehicleType) {
        List<Vehicle> vehicles = new ArrayList<>();
        String sql = "SELECT * FROM vehicles WHERE vehicleType = ? AND status != 'Inactive' ORDER BY plateID";
        
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

    /**
     * Get only INACTIVE vehicles (soft-deleted/retired).
     * Used for UI filtering and reactivation workflows.
     * 
     * @return List of inactive vehicles
     */
    public List<Vehicle> getInactiveVehicles() {
        List<Vehicle> vehicles = new ArrayList<>();
        String sql = "SELECT * FROM vehicles WHERE status = 'Inactive' ORDER BY plateID";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            
            while (rs.next()) {
                vehicles.add(extractVehicleFromResultSet(rs));
            }
            
            System.out.println("Found " + vehicles.size() + " inactive vehicle(s)");
            
        } catch (SQLException e) {
            System.err.println("Error getting inactive vehicles: " + e.getMessage());
            e.printStackTrace();
        }
        
        return vehicles;
    }
    
    /**
     * Get active vehicles by operational status.
     * Ensures only active (non-retired) vehicles are returned.
     * 
     * @param operationalStatus One of: 'Available', 'In Use', 'Maintenance'
     * @return List of active vehicles with the specified operational status
     */
    public List<Vehicle> getActiveVehiclesByOperationalStatus(String operationalStatus) {
        List<Vehicle> vehicles = new ArrayList<>();
        
        // Prevent querying for inactive vehicles through this method
        if (STATUS_INACTIVE.equalsIgnoreCase(operationalStatus)) {
            System.err.println("Use getInactiveVehicles() method instead");
            return vehicles;
        }
        
        String sql = "SELECT * FROM vehicles WHERE status = ? ORDER BY vehicleType, plateID";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, operationalStatus);
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                vehicles.add(extractVehicleFromResultSet(rs));
            }
            
            System.out.println("Found " + vehicles.size() + " " + operationalStatus + " vehicle(s)");
            
        } catch (SQLException e) {
            System.err.println("Error getting vehicles by operational status: " + e.getMessage());
            e.printStackTrace();
        }
        
        return vehicles;
    }

    /**
     * Helper method to extract Vehicle object from ResultSet.
     */
    private Vehicle extractVehicleFromResultSet(ResultSet rs) throws SQLException {
        Vehicle vehicle = new Vehicle();
        vehicle.setPlateID(rs.getString("plateID"));
        vehicle.setVehicleType(rs.getString("vehicleType"));
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
               STATUS_MAINTENANCE.equalsIgnoreCase(status) ||
               STATUS_INACTIVE.equalsIgnoreCase(status);
    }
    
}