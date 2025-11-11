package model;
import java.sql.Date;
/**
 * Entity class representing a DEPLOYMENT TRANSACTION in the database.
 * 
 * PURPOSE: Maps to the 'deployments' table in MySQL database.
 * Tracks vehicle movements between locations/branches.
 * 
 * FIELDS TO IMPLEMENT:
 * - deploymentId (int/String) - Primary key
 * - vehicleId (int/String) - Foreign key to Vehicle
 * - fromLocationId (int/String) - Origin location
 * - toLocationId (int/String) - Destination location
 * - deploymentDate (Date/Timestamp) - When deployment started
 * - arrivalDate (Date/Timestamp) - When vehicle arrived (null if in transit)
 * - reason (String) - "Rebalancing", "Maintenance", "Customer Request"
 * - status (String) - "In Transit", "Completed", "Cancelled"
 * - driverName (String) - Who transported the vehicle
 * 
 * METHODS TO IMPLEMENT:
 * - Constructor(s)
 * - Getters and Setters for all fields
 * - toString() for debugging
 * - equals() and hashCode()
 * 
 * COLLABORATOR NOTES:
 * - Tracks vehicle movements for fleet management
 * - Used to rebalance vehicle distribution across branches
 */
public class DeploymentTransaction {
    // TODO: Add private fields for deployment transaction attributes
    private String deploymentID;
    private String plateID;
    private String locationID;
    private Date startDate;
    private Date endDate; // NULL means vehicle currently at this location
    private String status;

    // TODO: Add constructors (default and parameterized)
    public DeploymentTransaction() {
        this.status = "Active";
    }
    
    public DeploymentTransaction(String deploymentID, String plateID, String locationID, 
                                 Date startDate, Date endDate) {
        this.deploymentID = deploymentID;
        this.plateID = plateID;
        this.locationID = locationID;
        this.startDate = startDate;
        this.endDate = endDate;
        this.status = "Active";
    }

    public DeploymentTransaction(String deploymentID, String plateID, String locationID, 
                                 Date startDate, Date endDate, String status) {
        this.deploymentID = deploymentID;
        this.plateID = plateID;
        this.locationID = locationID;
        this.startDate = startDate;
        this.endDate = endDate;
        this.status = status;
    }


    // TODO: Add getters and setters
    public String getDeploymentID() {
        return deploymentID;
    }
    
    public void setDeploymentID(String deploymentID) {
        this.deploymentID = deploymentID;
    }
    
    public String getPlateID() {
        return plateID;
    }
    
    public void setPlateID(String plateID) {
        this.plateID = plateID;
    }
    
    public String getLocationID() {
        return locationID;
    }
    
    public void setLocationID(String locationID) {
        this.locationID = locationID;
    }
    
    public Date getStartDate() {
        return startDate;
    }
    
    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }
    
    public Date getEndDate() {
        return endDate;
    }
    
    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }
    public String getStatus() {
        return status;
    }
    
    public void setStatus(String status) {
        this.status = status;
    }
    
    // Utility methods
    public boolean isActive() {
        return "Active".equalsIgnoreCase(status);
    }
    
    public boolean isCancelled() {
        return "Cancelled".equalsIgnoreCase(status);
    }
    
    public boolean isCurrent() {
        return endDate == null && "Active".equalsIgnoreCase(status);
    }
    
    public boolean isCompleted() {
        return endDate != null && "Active".equalsIgnoreCase(status);
    }
    
    public long getDurationInDays() {
        if (endDate != null && startDate != null) {
            long diffInMillis = endDate.getTime() - startDate.getTime();
            return diffInMillis / (1000 * 60 * 60 * 24);
        }
        return 0;
    }
    
    @Override
    public String toString() {
        return "DeploymentTransaction{" +
                "deploymentID='" + deploymentID + '\'' +
                ", plateID='" + plateID + '\'' +
                ", locationID='" + locationID + '\'' +
                ", startDate=" + startDate +
                ", endDate=" + endDate +
                ", status='" + status + '\'' +
                '}';
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        
        DeploymentTransaction that = (DeploymentTransaction) o;
        return deploymentID != null && deploymentID.equals(that.deploymentID);
    }
    
    @Override
    public int hashCode() {
        return deploymentID != null ? deploymentID.hashCode() : 0;
    }
}
