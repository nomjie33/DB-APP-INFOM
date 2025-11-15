package model;

/**
 * Entity class representing a MAINTENANCE TRANSACTION in the database.
 *
 * PURPOSE: Maps to the 'maintenance' table in MySQL database.
 * This represents the main maintenance record without part details.
 * Parts used are tracked separately in MaintenanceCheque table.
 *
 * SCHEMA ALIGNMENT:
 * - maintenanceID   : String (primary key, VARCHAR(11))
 * - startDateTime   : java.sql.Timestamp (when maintenance/repair starts, DATETIME)
 * - endDateTime     : java.sql.Timestamp (when maintenance/repair completes, DATETIME, NULL if in progress)
 * - notes           : String (notes about repair, VARCHAR(125))
 * - technicianID    : String (foreign key to Technician, VARCHAR(11))
 * - plateID         : String (foreign key to Vehicle, VARCHAR(11))
 *
 * DURATION CALCULATION:
 * - Labor hours calculated dynamically from (endDateTime - startDateTime)
 * - No need to store hoursWorked separately
 * - Cost = hoursWorked × technician rate + parts cost
 *
 * RELATIONSHIP:
 * - One maintenance record can have many parts (one-to-many with MaintenanceCheque)
 */
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Timestamp;

public class MaintenanceTransaction {
    private String maintenanceID;
    private Timestamp startDateTime;
    private Timestamp endDateTime;
    private BigDecimal totalCost;  // Total maintenance cost (labor + parts)
    private String notes;
    private String technicianID;
    private String plateID;
    private String status;  // 'Active' or 'Inactive' for soft delete

    // Default constructor
    public MaintenanceTransaction() {
        this.status = "Active";  // Default to Active
    }

    // Parameterized constructor (without status - defaults to Active)
    public MaintenanceTransaction(String maintenanceID, Timestamp startDateTime, Timestamp endDateTime,
                                  String notes, String technicianID, String plateID) {
        this.maintenanceID = maintenanceID;
        this.startDateTime = startDateTime;
        this.endDateTime = endDateTime;
        this.totalCost = BigDecimal.ZERO;  // Default to 0
        this.notes = notes;
        this.technicianID = technicianID;
        this.plateID = plateID;
        this.status = "Active";  // Default to Active
    }
    
    // Full constructor (with status and totalCost)
    public MaintenanceTransaction(String maintenanceID, Timestamp startDateTime, Timestamp endDateTime,
                                  BigDecimal totalCost, String notes, String technicianID, String plateID, String status) {
        this.maintenanceID = maintenanceID;
        this.startDateTime = startDateTime;
        this.endDateTime = endDateTime;
        this.totalCost = totalCost != null ? totalCost : BigDecimal.ZERO;
        this.notes = notes;
        this.technicianID = technicianID;
        this.plateID = plateID;
        this.status = status;
    }

    // Getters and setters
    public String getMaintenanceID() {
        return maintenanceID;
    }

    public void setMaintenanceID(String maintenanceID) {
        this.maintenanceID = maintenanceID;
    }

    public Timestamp getStartDateTime() {
        return startDateTime;
    }

    public void setStartDateTime(Timestamp startDateTime) {
        this.startDateTime = startDateTime;
    }

    public Timestamp getEndDateTime() {
        return endDateTime;
    }

    public void setEndDateTime(Timestamp endDateTime) {
        this.endDateTime = endDateTime;
    }

    public BigDecimal getTotalCost() {
        return totalCost;
    }

    public void setTotalCost(BigDecimal totalCost) {
        this.totalCost = totalCost;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public String getTechnicianID() {
        return technicianID;
    }

    public void setTechnicianID(String technicianID) {
        this.technicianID = technicianID;
    }

    public String getPlateID() {
        return plateID;
    }

    public void setPlateID(String plateID) {
        this.plateID = plateID;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
    
    /**
     * Check if maintenance record is active (not soft deleted).
     * @return true if active, false if inactive
     */
    public boolean isActive() {
        return "Active".equals(status);
    }
    
    /**
     * Check if maintenance record is inactive (soft deleted).
     * @return true if inactive, false if active
     */
    public boolean isInactive() {
        return "Inactive".equals(status);
    }

    /**
     * Calculate hours worked on maintenance.
     * Returns hours between startDateTime and endDateTime.
     * @return Hours worked as BigDecimal, or ZERO if not completed
     */
    public BigDecimal getHoursWorked() {
        if (startDateTime == null || endDateTime == null) {
            return BigDecimal.ZERO;
        }
        
        long durationMillis = endDateTime.getTime() - startDateTime.getTime();
        double hours = durationMillis / (1000.0 * 60.0 * 60.0);
        
        return BigDecimal.valueOf(hours).setScale(2, RoundingMode.HALF_UP);
    }
    
    /**
     * Check if maintenance is completed
     * @return true if completed, false if in progress
     */
    public boolean isCompleted() {
        return endDateTime != null;
    }
    
    /**
     * Check if maintenance is in progress
     * @return true if in progress, false if completed
     */
    public boolean isInProgress() {
        return endDateTime == null;
    }

    @Override
    public String toString() {
        return "MaintenanceTransaction{" +
                "maintenanceID='" + maintenanceID + '\'' +
                ", startDateTime=" + startDateTime +
                ", endDateTime=" + endDateTime +
                ", notes='" + notes + '\'' +
                ", technicianID='" + technicianID + '\'' +
                ", plateID='" + plateID + '\'' +
                ", hoursWorked=" + getHoursWorked() +
                ", completionStatus='" + (isCompleted() ? "Completed" : "In Progress") + '\'' +
                ", status='" + status + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MaintenanceTransaction that = (MaintenanceTransaction) o;
        return maintenanceID != null && maintenanceID.equals(that.maintenanceID);
    }

    @Override
    public int hashCode() {
        return maintenanceID != null ? maintenanceID.hashCode() : 0;
    }
}
