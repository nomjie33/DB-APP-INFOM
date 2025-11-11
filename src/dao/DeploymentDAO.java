package dao;

import model.DeploymentTransaction;
import util.DBConnection;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object for DEPLOYMENT TRANSACTION table operations.
 */
public class DeploymentDAO {
    
    // ==================== CREATE ====================

    public boolean insertDeployment(DeploymentTransaction deployment) {
        String sql = "INSERT INTO deployments (deploymentID, plateID, locationID, " +
                     "startDate, endDate, status) VALUES (?, ?, ?, ?, ?, ?)";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, deployment.getDeploymentID());
            stmt.setString(2, deployment.getPlateID());
            stmt.setString(3, deployment.getLocationID());
            stmt.setDate(4, deployment.getStartDate());
            stmt.setDate(5, deployment.getEndDate());
            stmt.setString(6, "Active");
            
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

    /**
     * Get all ACTIVE deployments (excludes cancelled)
     */
    public List<DeploymentTransaction> getAllDeployments() {
        List<DeploymentTransaction> deployments = new ArrayList<>();
        String sql = "SELECT * FROM deployments WHERE status != 'Cancelled' ORDER BY startDate DESC";
        
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
     * Get ALL deployments including cancelled ones.
     * For reporting purposes.
     */
    public List<DeploymentTransaction> getAllDeploymentsIncludingCancelled() {
        List<DeploymentTransaction> deployments = new ArrayList<>();
        String sql = "SELECT * FROM deployments ORDER BY startDate DESC";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            
            while (rs.next()) {
                deployments.add(extractDeploymentFromResultSet(rs));
            }
            
        } catch (SQLException e) {
            System.err.println("Error getting all deployments: " + e.getMessage());
            e.printStackTrace();
        }
        
        return deployments;
    }
    
    public List<DeploymentTransaction> getCurrentDeployments() {
        List<DeploymentTransaction> deployments = new ArrayList<>();
        String sql = "SELECT * FROM deployments WHERE endDate IS NULL AND status = 'Active' ORDER BY startDate DESC";
        
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
    
    public List<DeploymentTransaction> getHistoricalDeployments() {
        List<DeploymentTransaction> deployments = new ArrayList<>();
        String sql = "SELECT * FROM deployments WHERE endDate IS NOT NULL AND status = 'Active' ORDER BY endDate DESC";
        
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
    
    public List<DeploymentTransaction> getDeploymentsByVehicle(String plateID) {
        List<DeploymentTransaction> deployments = new ArrayList<>();
        String sql = "SELECT * FROM deployments WHERE plateID = ? AND status != 'Cancelled' ORDER BY startDate DESC";
        
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
    
    public List<DeploymentTransaction> getDeploymentsByLocation(String locationID) {
        List<DeploymentTransaction> deployments = new ArrayList<>();
        String sql = "SELECT * FROM deployments WHERE locationID = ? AND status != 'Cancelled' ORDER BY startDate DESC";
        
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
    
    public DeploymentTransaction getCurrentDeploymentByVehicle(String plateID) {
        String sql = "SELECT * FROM deployments WHERE plateID = ? AND endDate IS NULL AND status = 'Active'";
        
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
    
    public List<DeploymentTransaction> getCurrentDeploymentsByLocation(String locationID) {
        List<DeploymentTransaction> deployments = new ArrayList<>();
        String sql = "SELECT * FROM deployments WHERE locationID = ? AND endDate IS NULL AND status = 'Active'";
        
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
    
    public boolean updateDeployment(DeploymentTransaction deployment) {
        String sql = "UPDATE deployments SET plateID = ?, locationID = ?, " +
                     "startDate = ?, endDate = ?, status = ? WHERE deploymentID = ?";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, deployment.getPlateID());
            stmt.setString(2, deployment.getLocationID());
            stmt.setDate(3, deployment.getStartDate());
            stmt.setDate(4, deployment.getEndDate());
            stmt.setString(5, deployment.getStatus());
            stmt.setString(6, deployment.getDeploymentID());
            
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
     * SOFT DELETE: Mark a deployment as cancelled instead of physically deleting.
     * This preserves historical data and maintains referential integrity.
     * Cancel a deployment (mark as Cancelled).
     * 
     * @param deploymentID Deployment ID to cancel
     * @return true if cancellation successful, false otherwise
     */
    public boolean cancelDeployment(String deploymentID) {
        String sql = "UPDATE deployments SET status = 'Cancelled' WHERE deploymentID = ?";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, deploymentID);
            int rowsAffected = stmt.executeUpdate();
            
            if (rowsAffected > 0) {
                System.out.println("Deployment " + deploymentID + " has been marked as Cancelled (soft deleted)");
                return true;
            }
            
        } catch (SQLException e) {
            System.err.println("Error cancelling deployment: " + e.getMessage());
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
        deployment.setStatus(rs.getString("status"));
        return deployment;
    }
}