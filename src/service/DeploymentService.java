package service;

import dao.*;
import model.*;
import java.sql.Date;
import java.time.LocalDate;
import java.util.List;

/**
 * Business Logic Service for DEPLOYMENT operations.
 * 
 * PURPOSE: Manages vehicle movements between locations/branches.
 * Handles fleet rebalancing and vehicle transfers.
 * 
 * DEPENDENCIES:
 * - DeploymentDAO (deployment records)
 * - VehicleDAO (update vehicle location, check status)
 * - LocationDAO (validate locations)
 * 
 * METHODS TO IMPLEMENT:
 * 
 * 1. deployVehicle(int vehicleId, int fromLocationId, int toLocationId, String reason, String driverName)
 *    WORKFLOW:
 *    - Verify vehicle exists and is available (VehicleDAO)
 *    - Verify both locations exist (LocationDAO)
 *    - Create deployment record with status "In Transit" (DeploymentDAO)
 *    - Optionally update vehicle status to "In Transit"
 *    - Return deployment ID
 * 
 * 2. completeDeployment(int deploymentId, Timestamp arrivalDate)
 *    WORKFLOW:
 *    - Get deployment record (DeploymentDAO)
 *    - Update deployment with arrival date and status "Completed"
 *    - Update vehicle's locationId to destination (VehicleDAO)
 *    - Update vehicle status back to "Available"
 * 
 * 3. cancelDeployment(int deploymentId)
 *    - Mark deployment as "Cancelled"
 *    - Ensure vehicle location is correct
 * 
 * 4. getVehicleDeploymentHistory(int vehicleId)
 *    - Get all deployments for a vehicle
 *    - Track vehicle movement history
 * 
 * 5. getActiveDeployments()
 *    - Get all deployments currently in transit
 *    - For fleet tracking
 * 
 * 6. getDeploymentsByLocation(int locationId)
 *    - Get deployments to/from a specific location
 *    - For location management
 * 
 * 7. suggestRebalancing()
 *    - Analyze vehicle distribution across locations
 *    - Suggest which vehicles to move where
 *    - Based on rental frequency and availability
 * 
 * COLLABORATOR NOTES:
 * - Update vehicle location when deployment completes
 * - Ensure vehicle is available before deploying
 * - Track deployment reasons for analytics
 * - Use in location rental frequency reports
 */
public class DeploymentService {
    
    // Private DAO instances
    // TODO: Initialize DAO objects in constructor
    private DeploymentDAO deploymentDAO;
    private VehicleDAO vehicleDAO;
    private LocationDAO locationDAO;
    
    public DeploymentService(DeploymentDAO deploymentDAO, VehicleDAO vehicleDAO, LocationDAO locationDAO){
        this.deploymentDAO = new DeploymentDAO();
        this.vehicleDAO = new VehicleDAO();
        this.locationDAO = new LocationDAO();
    }

    // TODO: Implement deployVehicle()
    public String deployVehicle(String plateID, String locationID) {
        // Validate Vehicle
        Vehicle vehicle = vehicleDAO.getVehicleById(plateID);

        if (vehicle == null) { System.err.println("Err: Vehicle " + plateID + " not found!"); return null; }

        System.out.println("Vehicle Found: " + vehicle.getVehicleModel());
        System.out.println("Type: " + vehicle.getVehicleType());
        System.out.println("Status:  " + vehicle.getStatus());

        // Validate Location
        Location location = locationDAO.getLocationById(locationID); 
        if (location == null) { System.err.println("Err: Location " + locationID + " not found!"); return null; }

        System.out.println("Location Found: " + location.getName());

        // Check Current Deployment
        DeploymentTransaction currDeployment = deploymentDAO.getCurrentDeploymentByVehicle(plateID);
       if (currDeployment != null) {
            // Vehicle is already deployed somewhere
            System.out.println("Vehicle is currently deployed:");
            
            Location currentLocation = locationDAO.getLocationById(currDeployment.getLocationID());
            System.out.println("   Current Location: " + 
                (currentLocation != null ? currentLocation.getName() : currDeployment.getLocationID()));
            System.out.println("   Since: " + currDeployment.getStartDate());
            
            // Check if already at this location
            if (currDeployment.getLocationID().equals(locationID)) {
                System.err.println("Err: Vehicle is already at this location!");
                return null;
            }
            
            // End current deployment
            Date today = Date.valueOf(LocalDate.now());
            boolean ended = deploymentDAO.endDeployment(currDeployment.getDeploymentID(), today);
            
            if (!ended) {
                System.err.println("Err: Failed to end current deployment!");
                return null;
            }
            
            System.out.println("Current deployment ended");
        } else {
            System.out.println("Vehicle has no current deployment (new vehicle or first deployment)");
        }
        
        // Generate Deployment ID 
        String deploymentID = generateDeploymentID();
        System.out.println("Generated deployment ID: " + deploymentID);
        
        // Create New Deployment
        
        Date startDate = Date.valueOf(LocalDate.now());
        
        DeploymentTransaction newDeployment = new DeploymentTransaction(
            deploymentID,
            plateID,
            locationID,
            startDate,
            null  // endDate is null (current deployment)
        );
        
        boolean created = deploymentDAO.insertDeployment(newDeployment);
        
        if (!created) {
            System.err.println("Err: Failed to create deployment!");
            return null;
        }
        
        System.out.println("Deployment created");
        System.out.println("\n━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        System.out.println("🎉 DEPLOYMENT SUCCESSFUL!");
        System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        System.out.println("📋 Deployment Details:");
        System.out.println("   Deployment ID: " + deploymentID);
        System.out.println("   Vehicle: " + vehicle.getPlateID() + " - " + vehicle.getVehicleModel());
        System.out.println("   Location: " + location.getName());
        System.out.println("   Start Date: " + startDate);
        System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━\n");
        
        return deploymentID;
    }
    

    
    // TODO: Implement completeDeployment()
    public boolean completeDeployment(String deploymentID) {
        
        DeploymentTransaction deployment = deploymentDAO.getDeploymentById(deploymentID);
        if (deployment == null) {
            System.err.println("Err: Deployment not found");
            return false;
        }
        
        if (!deployment.isCurrent()) {
            System.err.println("Err: Deployment already completed");
            return false;
        }
        
        Date endDate = Date.valueOf(LocalDate.now());
        boolean ended = deploymentDAO.endDeployment(deploymentID, endDate);
        
        if (ended) { System.out.println("Deployment completed"); }
        
        return ended;
    }

    // TODO: Implement cancelDeployment()
        public boolean cancelDeployment(String deploymentID) {
        DeploymentTransaction deployment = deploymentDAO.getDeploymentById(deploymentID);
        if (deployment == null) {
            System.err.println("Err: Deployment not found");
            return false;
        }
        
        boolean deleted = deploymentDAO.deleteDeployment(deploymentID);
        
        if (deleted) { System.out.println("Deployment cancelled");}
        
        return deleted;
    }

    // TODO: Implement getVehicleDeploymentHistory()
        public List<DeploymentTransaction> getVehicleDeploymentHistory(String plateID) {
        System.out.println("\n━━━ Getting Deployment History ━━━");
        
        Vehicle vehicle = vehicleDAO.getVehicleById(plateID);
        if (vehicle == null) {
            System.err.println("Err: Vehicle not found");
            return null;
        }
        
        List<DeploymentTransaction> history = deploymentDAO.getDeploymentsByVehicle(plateID);
        System.out.println("Found " + history.size() + " deployment(s)");
        
        return history;
    }
    
    // TODO: Implement getActiveDeployments()
    public List<DeploymentTransaction> getActiveDeployments() {
        System.out.println("\nGetting Active Deployments");
        
        List<DeploymentTransaction> active = deploymentDAO.getCurrentDeployments();
        System.out.println("Found " + active.size() + " active deployment(s)");
        
        return active;
    }
    
    
    // TODO: Implement getDeploymentsByLocation()
        public List<DeploymentTransaction> getDeploymentsByLocation(String locationID) {
        System.out.println("\nGetting Deployments By Location");
        
        Location location = locationDAO.getLocationById(locationID);
        if (location == null) {
            System.err.println("Err: Location not found");
            return null;
        }
        
        List<DeploymentTransaction> deployments = deploymentDAO.getDeploymentsByLocation(locationID);
        System.out.println("Found " + deployments.size() + " deployment(s) at " + location.getName());
        
        return deployments;
    }
    
    // TODO: Implement suggestRebalancing()



        // ==================== HELPER METHODS ====================
    
    /**
     * Generate unique deployment ID
     * Format: DEP-XXXXXX
     * 
     * @return Generated deployment ID
     */
    private String generateDeploymentID() {
        long timestamp = System.currentTimeMillis();
        return "DEP-" + (timestamp % 1000000);
    }
}
