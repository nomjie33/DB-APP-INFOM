package dao;

import model.Vehicle;
import java.sql.*;
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
    
    // TODO: Implement insertVehicle(Vehicle vehicle)
    
    // TODO: Implement updateVehicle(Vehicle vehicle)
    
    // TODO: Implement deleteVehicle(int vehicleId)
    
    // TODO: Implement getVehicleById(int vehicleId)
    
    // TODO: Implement getAllVehicles()
    
    // TODO: Implement getAvailableVehicles()
    
    // TODO: Implement getVehiclesByLocation(int locationId)
    
    // TODO: Implement updateVehicleStatus(int vehicleId, String newStatus)
    
    // TODO: Implement getVehiclesByStatus(String status)
}
