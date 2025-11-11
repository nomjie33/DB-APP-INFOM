package model;

/**
 * Entity class representing a MAINTENANCE CHEQUE (parts usage record) in the database.
 *
 * PURPOSE: Maps to the 'maintenance_cheque' table in MySQL database.
 * This tracks which parts were used in a maintenance record and their quantities.
 *
 * SCHEMA:
 * - maintenanceID  : String (primary key, foreign key to Maintenance, VARCHAR(11))
 * - partID         : String (primary key, foreign key to Part, VARCHAR(11))
 * - quantityUsed   : java.math.BigDecimal (quantity of part used, DECIMAL(10,2))
 *
 * RELATIONSHIP:
 * - Many-to-one with MaintenanceTransaction
 * - Many-to-one with Part
 * - Composite primary key (maintenanceID, partID)
 */
import java.math.BigDecimal;
import java.util.Objects;

public class MaintenanceCheque {
    private String maintenanceID;
    private String partID;
    private BigDecimal quantityUsed;
    private String status;  // 'Active' or 'Inactive' for soft delete

    // Default constructor
    public MaintenanceCheque() {
        this.status = "Active";  // Default to Active
    }

    // Parameterized constructor (without status - defaults to Active)
    public MaintenanceCheque(String maintenanceID, String partID, BigDecimal quantityUsed) {
        this.maintenanceID = maintenanceID;
        this.partID = partID;
        this.quantityUsed = quantityUsed;
        this.status = "Active";  // Default to Active
    }
    
    // Full constructor (with status)
    public MaintenanceCheque(String maintenanceID, String partID, BigDecimal quantityUsed, String status) {
        this.maintenanceID = maintenanceID;
        this.partID = partID;
        this.quantityUsed = quantityUsed;
        this.status = status;
    }

    // Getters and setters
    public String getMaintenanceID() {
        return maintenanceID;
    }

    public void setMaintenanceID(String maintenanceID) {
        this.maintenanceID = maintenanceID;
    }

    public String getPartID() {
        return partID;
    }

    public void setPartID(String partID) {
        this.partID = partID;
    }

    public BigDecimal getQuantityUsed() {
        return quantityUsed;
    }

    public void setQuantityUsed(BigDecimal quantityUsed) {
        this.quantityUsed = quantityUsed;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
    
    /**
     * Check if record is active (not soft deleted).
     * @return true if active, false if inactive
     */
    public boolean isActive() {
        return "Active".equals(status);
    }
    
    /**
     * Check if record is inactive (soft deleted).
     * @return true if inactive, false if active
     */
    public boolean isInactive() {
        return "Inactive".equals(status);
    }

    @Override
    public String toString() {
        return "MaintenanceCheque{" +
                "maintenanceID='" + maintenanceID + '\'' +
                ", partID='" + partID + '\'' +
                ", quantityUsed=" + quantityUsed +
                ", status='" + status + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MaintenanceCheque that = (MaintenanceCheque) o;
        return Objects.equals(maintenanceID, that.maintenanceID) &&
                Objects.equals(partID, that.partID);
    }

    @Override
    public int hashCode() {
        return Objects.hash(maintenanceID, partID);
    }
}
