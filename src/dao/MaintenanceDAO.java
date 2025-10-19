package dao;

import model.MaintenanceTransaction;
import java.sql.*;
import java.util.List;

/**
 * Data Access Object for MAINTENANCE TRANSACTION table operations.
 * 
 * PURPOSE: Handles all database CRUD operations for maintenance table.
 * 
 * METHODS TO IMPLEMENT:
 * 
 * 1. insertMaintenance(MaintenanceTransaction maintenance)
 *    - INSERT new maintenance record
 * 
 * 2. updateMaintenance(MaintenanceTransaction maintenance)
 *    - UPDATE maintenance record
 * 
 * 3. deleteMaintenance(int maintenanceId)
 *    - DELETE maintenance record
 * 
 * 4. getMaintenanceById(int maintenanceId)
 *    - SELECT maintenance by ID
 * 
 * 5. getAllMaintenance()
 *    - SELECT all maintenance records
 * 
 * 6. getMaintenanceByVehicle(int vehicleId)
 *    - SELECT maintenance history for a vehicle
 *    - Track vehicle repair history
 * 
 * 7. getMaintenanceByTechnician(int technicianId)
 *    - SELECT work assigned to a technician
 * 
 * 8. getMaintenanceByStatus(String status)
 *    - SELECT maintenance by status
 *    - Find ongoing or scheduled work
 * 
 * 9. getMaintenanceCostByVehicle(int vehicleId)
 *    - SUM total maintenance cost for a vehicle
 *    - For vehicle profitability analysis
 * 
 * 10. completeMaintenance(int maintenanceId)
 *     - UPDATE status to "Completed"
 *     - May also update vehicle status to "Available"
 * 
 * COLLABORATOR NOTES:
 * - Calculate totalCost = laborCost + partsCost
 * - Link to VehicleDAO to update vehicle status
 * - Used in defective vehicle reports
 */
public class MaintenanceDAO {
    
    // TODO: Implement insertMaintenance(MaintenanceTransaction maintenance)
    
    // TODO: Implement updateMaintenance(MaintenanceTransaction maintenance)
    
    // TODO: Implement deleteMaintenance(int maintenanceId)
    
    // TODO: Implement getMaintenanceById(int maintenanceId)
    
    // TODO: Implement getAllMaintenance()
    
    // TODO: Implement getMaintenanceByVehicle(int vehicleId)
    
    // TODO: Implement getMaintenanceByTechnician(int technicianId)
    
    // TODO: Implement getMaintenanceByStatus(String status)
    
    // TODO: Implement getMaintenanceCostByVehicle(int vehicleId)
    
    // TODO: Implement completeMaintenance(int maintenanceId)
}
