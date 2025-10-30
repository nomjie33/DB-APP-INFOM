package dao;

import model.DeploymentTransaction;
import util.DBConnection;
import java.sql.*;
import java.util.ArrayList;
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
    
    // ==================== CREATE ====================

    public boolean insertDeployment(DeploymentTransaction deployment) {
        String sql = "INSERT INTO deployments (deploymentID, plateID, locationID, " +
                     "startDate, endDate) VALUES (?, ?, ?, ?, ?)";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, deployment.getDeploymentID());
            stmt.setString(2, deployment.getPlateID());
            stmt.setString(3, deployment.getLocationID());
            stmt.setDate(4, deployment.getStartDate());
            stmt.setDate(5, deployment.getEndDate());
            
            int rowsAffected = stmt.executeUpdate();
            
            if (rowsAffected > 0) {
                System.out.println("Deployment inserted: " + deployment.getDeploymentID());
                return true;
            }
            
        } catch (SQLException e) {
            System.err.println("Error inserting deployment: " + e.getMessage());
            e.printStackTrace();
        }
        
        return false;
    }
    
    // ==================== READ ====================

    public DeploymentTransaction getDeploymentById(String deploymentID) {
        String sql = "SELECT * FROM deployments WHERE deploymentID = ?";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, deploymentID);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                return extractDeploymentFromResultSet(rs);
            }
            
        } catch (SQLException e) {
            System.err.println("Error getting deployment: " + e.getMessage());
            e.printStackTrace();
        }
        
        return null;
    }

    public List<DeploymentTransaction> getAllDeployments() {
        List<DeploymentTransaction> deployments = new ArrayList<>();
        String sql = "SELECT * FROM deployments ORDER BY startDate DESC";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            
            while (rs.next()) {
                deployments.add(extractDeploymentFromResultSet(rs));
            }
            
            System.out.println("Retrieved " + deployments.size() + " deployment(s)");
            
        } catch (SQLException e) {
            System.err.println("Error getting all deployments: " + e.getMessage());
            e.printStackTrace();
        }
        
        return deployments;
    }
    
    /**
     * Get current deployments (endDate is NULL)
     * These represent where vehicles currently are
     * 
     * @return List of current deployments
     */
    public List<DeploymentTransaction> getCurrentDeployments() {
        List<DeploymentTransaction> deployments = new ArrayList<>();
        String sql = "SELECT * FROM deployments WHERE endDate IS NULL ORDER BY startDate DESC";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            
            while (rs.next()) {
                deployments.add(extractDeploymentFromResultSet(rs));
            }
            
            System.out.println("Found " + deployments.size() + " current deployment(s)");
            
        } catch (SQLException e) {
            System.err.println("Error getting current deployments: " + e.getMessage());
            e.printStackTrace();
        }
        
        return deployments;
    }
    
    /**
     * Get historical deployments (endDate is NOT NULL)
     * These show past vehicle movements
     * 
     * @return List of historical deployments
     */
    public List<DeploymentTransaction> getHistoricalDeployments() {
        List<DeploymentTransaction> deployments = new ArrayList<>();
        String sql = "SELECT * FROM deployments WHERE endDate IS NOT NULL ORDER BY endDate DESC";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            
            while (rs.next()) {
                deployments.add(extractDeploymentFromResultSet(rs));
            }
            
        } catch (SQLException e) {
            System.err.println("Error getting historical deployments: " + e.getMessage());
            e.printStackTrace();
        }
        
        return deployments;
    }
    
    /**
     * Get deployment history for a vehicle
     * 
     * @param plateID The vehicle's plate ID
     * @return List of vehicle's deployments
     */
    public List<DeploymentTransaction> getDeploymentsByVehicle(String plateID) {
        List<DeploymentTransaction> deployments = new ArrayList<>();
        String sql = "SELECT * FROM deployments WHERE plateID = ? ORDER BY startDate DESC";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, plateID);
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                deployments.add(extractDeploymentFromResultSet(rs));
            }
            
        } catch (SQLException e) {
            System.err.println("Error getting vehicle deployments: " + e.getMessage());
            e.printStackTrace();
        }
        
        return deployments;
    }
    
    /**
     * Get deployments at a specific location
     * 
     * @param locationID The location ID
     * @return List of deployments at this location
     */
    public List<DeploymentTransaction> getDeploymentsByLocation(String locationID) {
        List<DeploymentTransaction> deployments = new ArrayList<>();
        String sql = "SELECT * FROM deployments WHERE locationID = ? ORDER BY startDate DESC";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, locationID);
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                deployments.add(extractDeploymentFromResultSet(rs));
            }
            
        } catch (SQLException e) {
            System.err.println("Error getting location deployments: " + e.getMessage());
            e.printStackTrace();
        }
        
        return deployments;
    }
    
    /**
     * Get current deployment for a vehicle
     * (Where is this vehicle right now?)
     * 
     * @param plateID The vehicle's plate ID
     * @return Current deployment or null if none
     */
    public DeploymentTransaction getCurrentDeploymentByVehicle(String plateID) {
        String sql = "SELECT * FROM deployments WHERE plateID = ? AND endDate IS NULL";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, plateID);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                return extractDeploymentFromResultSet(rs);
            }
            
        } catch (SQLException e) {
            System.err.println("Error getting current deployment: " + e.getMessage());
            e.printStackTrace();
        }
        
        return null;
    }
    
    /**
     * Get vehicles currently at a location
     * 
     * @param locationID The location ID
     * @return List of deployments (current vehicles at location)
     */
    public List<DeploymentTransaction> getCurrentDeploymentsByLocation(String locationID) {
        List<DeploymentTransaction> deployments = new ArrayList<>();
        String sql = "SELECT * FROM deployments WHERE locationID = ? AND endDate IS NULL";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, locationID);
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                deployments.add(extractDeploymentFromResultSet(rs));
            }
            
        } catch (SQLException e) {
            System.err.println("Error getting current deployments at location: " + e.getMessage());
            e.printStackTrace();
        }
        
        return deployments;
    }
    
    // ==================== UPDATE ====================
    
    /**
     * Update deployment record
     * 
     * @param deployment The deployment object with updated data
     * @return true if successful, false otherwise
     */
    public boolean updateDeployment(DeploymentTransaction deployment) {
        String sql = "UPDATE deployments SET plateID = ?, locationID = ?, " +
                     "startDate = ?, endDate = ? WHERE deploymentID = ?";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, deployment.getPlateID());
            stmt.setString(2, deployment.getLocationID());
            stmt.setDate(3, deployment.getStartDate());
            stmt.setDate(4, deployment.getEndDate());
            stmt.setString(5, deployment.getDeploymentID());
            
            int rowsAffected = stmt.executeUpdate();
            
            if (rowsAffected > 0) {
                System.out.println("Deployment updated: " + deployment.getDeploymentID());
                return true;
            }
            
        } catch (SQLException e) {
            System.err.println("Error updating deployment: " + e.getMessage());
            e.printStackTrace();
        }
        
        return false;
    }
    
    /**
     * End a deployment (vehicle moved from this location)
     * 
     * @param deploymentID The deployment ID
     * @param endDate The date vehicle left
     * @return true if successful, false otherwise
     */
    public boolean endDeployment(String deploymentID, Date endDate) {
        String sql = "UPDATE deployments SET endDate = ? WHERE deploymentID = ?";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setDate(1, endDate);
            stmt.setString(2, deploymentID);
            
            int rowsAffected = stmt.executeUpdate();
            
            if (rowsAffected > 0) {
                System.out.println("Deployment ended: " + deploymentID);
                return true;
            }
            
        } catch (SQLException e) {
            System.err.println("Error ending deployment: " + e.getMessage());
            e.printStackTrace();
        }
        
        return false;
    }
    
    // ==================== DELETE ====================
    
    /**
     * Delete deployment record
     * Use carefully - this removes historical data
     * 
     * @param deploymentID The deployment ID to delete
     * @return true if successful, false otherwise
     */
    public boolean deleteDeployment(String deploymentID) {
        String sql = "DELETE FROM deployments WHERE deploymentID = ?";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, deploymentID);
            
            int rowsAffected = stmt.executeUpdate();
            
            if (rowsAffected > 0) {
                System.out.println("Deployment deleted: " + deploymentID);
                return true;
            } else {
                System.err.println("Deployment not found: " + deploymentID);
            }
            
        } catch (SQLException e) {
            System.err.println("Error deleting deployment: " + e.getMessage());
            e.printStackTrace();
        }
        
        return false;
    }
    
    // ==================== HELPER ====================
    
    /**
     * Helper method to convert database row to Java object
     */
    private DeploymentTransaction extractDeploymentFromResultSet(ResultSet rs) throws SQLException {
        DeploymentTransaction deployment = new DeploymentTransaction();
        deployment.setDeploymentID(rs.getString("deploymentID"));
        deployment.setPlateID(rs.getString("plateID"));
        deployment.setLocationID(rs.getString("locationID"));
        deployment.setStartDate(rs.getDate("startDate"));
        deployment.setEndDate(rs.getDate("endDate"));
        return deployment;
    }
}