package service;

import dao.*;
import model.*;
import java.sql.Date;
import java.time.LocalDate;
import java.util.List;

/**
 * Business Logic Service for DEPLOYMENT operations.
 */
public class DeploymentService {
    
    private DeploymentDAO deploymentDAO;
    private VehicleDAO vehicleDAO;
    private LocationDAO locationDAO;
    
    public DeploymentService(DeploymentDAO deploymentDAO, VehicleDAO vehicleDAO, LocationDAO locationDAO){
        this.deploymentDAO = deploymentDAO;
        this.vehicleDAO = vehicleDAO;
        this.locationDAO = locationDAO;
    }

    /**
     * Deploy a vehicle to a location
     * 
     * @param plateID Vehicle to deploy
     * @param locationID Destination location
     * @return Deployment ID if successful, null otherwise
     */
    public String deployVehicle(String plateID, String locationID) {
        System.out.println("\n=== Deploying Vehicle ===");
        
        // VALIDATE VEHICLE
        Vehicle vehicle = vehicleDAO.getVehicleById(plateID);

        if (vehicle == null) { 
            System.err.println("Err: Vehicle " + plateID + " not found!"); 
            return null; 
        }
        
        // Check if vehicle is active (not retired)
        if (!vehicle.isActive()) {
            System.err.println("Err: Vehicle is retired/inactive!");
            return null;
        }

        System.out.println("✓ Vehicle Found: " + vehicle.getVehicleType());
        System.out.println("   Plate ID: " + vehicle.getPlateID());
        System.out.println("   Status: " + vehicle.getStatus());

        // VALIDATE LOCATION
        Location location = locationDAO.getLocationById(locationID); 
        if (location == null) { 
            System.err.println("Err: Location " + locationID + " not found!"); 
            return null; 
        }
        
        // Check if location is active
        if (!location.isActive()) {
            System.err.println("Err: Location is closed/inactive!");
            return null;
        }

        System.out.println("✓ Location Found: " + location.getName());

        // CHECK CURRENT DEPLOYMENT
        DeploymentTransaction currDeployment = deploymentDAO.getCurrentDeploymentByVehicle(plateID);
        if (currDeployment != null) {
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
            
            System.out.println("✓ Current deployment ended");
        } else {
            System.out.println("Vehicle has no current deployment (new vehicle or first deployment)");
        }
        
        // GENERATE DEPLOYMENT ID 
        String deploymentID = generateDeploymentID();
        System.out.println("Generated deployment ID: " + deploymentID);
        
        // CREATE NEW DEPLOYMENT
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
        
        System.out.println("✓ Deployment created");
        
        // SUCCESS!
        System.out.println("\n┌────────────────────────────────┐");
        System.out.println("│   🎉 DEPLOYMENT SUCCESSFUL!   │");
        System.out.println("└────────────────────────────────┘");
        System.out.println("📋 Deployment Details:");
        System.out.println("   Deployment ID: " + deploymentID);
        System.out.println("   Vehicle: " + vehicle.getVehicleType() + " (" + vehicle.getPlateID() + ")");
        System.out.println("   Location: " + location.getName());
        System.out.println("   Start Date: " + startDate);
        System.out.println("────────────────────────────────\n");
        
        return deploymentID;
    }

    /**
     * Complete a deployment (vehicle has arrived at destination)
     * 
     * @param deploymentID Deployment to complete
     * @return true if successful, false otherwise
     */
    public boolean completeDeployment(String deploymentID) {
        System.out.println("\n=== Completing Deployment ===");
        
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
        
        if (ended) { 
            System.out.println("✓ Deployment completed");
            System.out.println("   Deployment ID: " + deploymentID);
            System.out.println("   End Date: " + endDate);
        }
        
        return ended;
    }

    /**
     * Cancel a deployment (SOFT DELETE)
     * Marks deployment as Cancelled, does not delete from database
     * 
     * @param deploymentID Deployment to cancel
     * @return true if successful, false otherwise
     */
    public boolean cancelDeployment(String deploymentID) {
        System.out.println("\n=== Cancelling Deployment ===");
        
        DeploymentTransaction deployment = deploymentDAO.getDeploymentById(deploymentID);
        if (deployment == null) {
            System.err.println("Err: Deployment not found");
            return false;
        }
        
        // UPDATED: Use cancelDeployment (soft delete) instead of deleteDeployment
        boolean cancelled = deploymentDAO.cancelDeployment(deploymentID);
        
        if (cancelled) { 
            System.out.println("✓ Deployment cancelled (marked as Cancelled)");
            System.out.println("   Deployment ID: " + deploymentID);
            System.out.println("   Note: Deployment data preserved for reporting");
        }
        
        return cancelled;
    }

    /**
     * Get deployment history for a vehicle
     * 
     * @param plateID Vehicle to get history for
     * @return List of deployments
     */
    public List<DeploymentTransaction> getVehicleDeploymentHistory(String plateID) {
        System.out.println("\n=== Getting Deployment History ===");
        
        Vehicle vehicle = vehicleDAO.getVehicleById(plateID);
        if (vehicle == null) {
            System.err.println("Err: Vehicle not found");
            return null;
        }
        
        List<DeploymentTransaction> history = deploymentDAO.getDeploymentsByVehicle(plateID);
        System.out.println("Found " + history.size() + " deployment(s) for vehicle: " + vehicle.getPlateID());
        
        return history;
    }
    
    /**
     * Get all active deployments
     * 
     * @return List of active deployments
     */
    public List<DeploymentTransaction> getActiveDeployments() {
        System.out.println("\n=== Getting Active Deployments ===");
        
        List<DeploymentTransaction> active = deploymentDAO.getCurrentDeployments();
        System.out.println("Found " + active.size() + " active deployment(s)");
        
        return active;
    }
    
    /**
     * Get deployments at a specific location
     * 
     * @param locationID Location to get deployments for
     * @return List of deployments
     */
    public List<DeploymentTransaction> getDeploymentsByLocation(String locationID) {
        System.out.println("\n=== Getting Deployments By Location ===");
        
        Location location = locationDAO.getLocationById(locationID);
        if (location == null) {
            System.err.println("Err: Location not found");
            return null;
        }
        
        List<DeploymentTransaction> deployments = deploymentDAO.getDeploymentsByLocation(locationID);
        System.out.println("Found " + deployments.size() + " deployment(s) at " + location.getName());
        
        return deployments;
    }
    
    /**
     * Get current location of a vehicle
     * 
     * @param plateID Vehicle to check
     * @return Location object if deployed, null if not deployed
     */
    public Location getCurrentVehicleLocation(String plateID) {
        DeploymentTransaction deployment = deploymentDAO.getCurrentDeploymentByVehicle(plateID);
        
        if (deployment == null) {
            return null;
        }
        
        return locationDAO.getLocationById(deployment.getLocationID());
    }

    // HELPER METHODS
    
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