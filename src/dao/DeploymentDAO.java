package dao;

import model.DeploymentTransaction;
import java.sql.*;
import java.util.List;

/**
 * Data Access Object for DEPLOYMENT TRANSACTION table operations.
 * 
 * PURPOSE: Handles all database CRUD operations for deployments table.
 * 
 * METHODS TO IMPLEMENT:
 * 
 * 1. insertDeployment(DeploymentTransaction deployment)
 *    - INSERT new deployment record
 * 
 * 2. updateDeployment(DeploymentTransaction deployment)
 *    - UPDATE deployment (e.g., mark as completed)
 * 
 * 3. deleteDeployment(int deploymentId)
 *    - DELETE deployment record
 * 
 * 4. getDeploymentById(int deploymentId)
 *    - SELECT deployment by ID
 * 
 * 5. getAllDeployments()
 *    - SELECT all deployments
 * 
 * 6. getDeploymentsByVehicle(int vehicleId)
 *    - SELECT deployment history for a vehicle
 * 
 * 7. getActiveDeployments()
 *    - SELECT deployments with status = "In Transit"
 * 
 * 8. getDeploymentsByLocation(int locationId)
 *    - SELECT deployments to/from a location
 * 
 * 9. completeDeployment(int deploymentId, Timestamp arrivalDate)
 *    - UPDATE deployment with arrival date
 *    - Change status to "Completed"
 *    - Update vehicle's locationId
 * 
 * COLLABORATOR NOTES:
 * - Must update Vehicle.locationId when deployment completes
 * - Track vehicle movements for fleet rebalancing
 * - Use in location rental frequency reports
 */
public class DeploymentDAO {
    
    // TODO: Implement insertDeployment(DeploymentTransaction deployment)
    
    // TODO: Implement updateDeployment(DeploymentTransaction deployment)
    
    // TODO: Implement deleteDeployment(int deploymentId)
    
    // TODO: Implement getDeploymentById(int deploymentId)
    
    // TODO: Implement getAllDeployments()
    
    // TODO: Implement getDeploymentsByVehicle(int vehicleId)
    
    // TODO: Implement getActiveDeployments()
    
    // TODO: Implement getDeploymentsByLocation(int locationId)
    
    // TODO: Implement completeDeployment(int deploymentId, Timestamp arrivalDate)
}
