package model;

/**
 * Entity class representing a MAINTENANCE TRANSACTION in the database.
 *
 * PURPOSE: Maps to the 'maintenance' table in MySQL database.
 * This represents the main maintenance record without part details.
 * Parts used are tracked separately in MaintenanceCheque table.
 *
 * SCHEMA:
 * - maintenanceID   : String (primary key, VARCHAR(11))
 * - dateReported    : java.sql.Date (when the issue was reported)
 * - dateRepaired    : java.sql.Date (when repair was completed)
 * - notes           : String (notes about repair, VARCHAR(125))
 * - technicianID    : String (foreign key to Technician, VARCHAR(11))
 * - plateID         : String (foreign key to Vehicle, VARCHAR(11))
 *
 * RELATIONSHIP:
 * - One maintenance record can have many parts (one-to-many with MaintenanceCheque)
 */
import java.sql.Date;

public class MaintenanceTransaction {
    private String maintenanceID;
    private Date dateReported;
    private Date dateRepaired;
    private String notes;
    private String technicianID;
    private String plateID;

    // Default constructor
    public MaintenanceTransaction() {
    }

    // Parameterized constructor
    public MaintenanceTransaction(String maintenanceID, Date dateReported, Date dateRepaired,
                                  String notes, String technicianID, String plateID) {
        this.maintenanceID = maintenanceID;
        this.dateReported = dateReported;
        this.dateRepaired = dateRepaired;
        this.notes = notes;
        this.technicianID = technicianID;
        this.plateID = plateID;
    }

    // Getters and setters
    public String getMaintenanceID() {
        return maintenanceID;
    }

    public void setMaintenanceID(String maintenanceID) {
        this.maintenanceID = maintenanceID;
    }

    public Date getDateReported() {
        return dateReported;
    }

    public void setDateReported(Date dateReported) {
        this.dateReported = dateReported;
    }

    public Date getDateRepaired() {
        return dateRepaired;
    }

    public void setDateRepaired(Date dateRepaired) {
        this.dateRepaired = dateRepaired;
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

    @Override
    public String toString() {
        return "MaintenanceTransaction{" +
                "maintenanceID='" + maintenanceID + '\'' +
                ", dateReported=" + dateReported +
                ", dateRepaired=" + dateRepaired +
                ", notes='" + notes + '\'' +
                ", technicianID='" + technicianID + '\'' +
                ", plateID='" + plateID + '\'' +
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
