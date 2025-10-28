package model;

/**
 * Entity class representing a MAINTENANCE TRANSACTION in the database.
 *
 * PURPOSE: Maps to the 'maintenance' table in MySQL database. This class
 * follows the simplified table layout used by the team:
 *
 * Table columns (expected):
 * - maintenanceID   : String (primary key, e.g. "M001")
 * - vehicleID       : String (foreign key to Vehicle, e.g. "V103")
 * - technicianID    : String (foreign key to Technician, e.g. "T01")
 * - partID          : String (part used / reference, e.g. "P01")
 * - reportDate      : java.sql.Timestamp (when the issue was reported)
 * - repairDate      : java.sql.Timestamp (when repair was completed)
 * - notes           : String (notes about repair / description)
 * - vehicleStatus   : String (vehicle status after repair, e.g. "Available")
 *
 * Example rows:
 * M001, V103, T01, P01, 2025-09-07, 2025-09-20, "Brake replacement", "Available"
 * M002, V103, T02, P02, 2025-09-08, 2025-09-21, "Battery issue",     "Available"
 *
 * IMPLEMENTATION NOTES:
 * - Keep this class as a POJO (no DB logic).
 * - Use `maintenanceID` as the identity in equals() / hashCode().
 * - Use java.sql.Timestamp for reportDate and repairDate to match DB TIMESTAMP.
 */
import java.sql.Timestamp;
import java.util.Objects;
public class MaintenanceTransaction {
    // Fields matching the simplified maintenance table
    private String maintenanceID;
    private String vehicleID;
    private String technicianID;
    private String partID;
    private Timestamp reportDate;
    private Timestamp repairDate;
    private String notes;
    private String vehicleStatus;

    // Default constructor
    public MaintenanceTransaction() {
    }

    // Parameterized constructor
    public MaintenanceTransaction(String maintenanceID, String vehicleID, String technicianID,
                                  String partID, Timestamp reportDate, Timestamp repairDate,
                                  String notes, String vehicleStatus) {
        this.maintenanceID = maintenanceID;
        this.vehicleID = vehicleID;
        this.technicianID = technicianID;
        this.partID = partID;
        this.reportDate = reportDate;
        this.repairDate = repairDate;
        this.notes = notes;
        this.vehicleStatus = vehicleStatus;
    }

    // Getters and setters
    public String getMaintenanceId() {
        return maintenanceID;
    }

    public void setMaintenanceId(String maintenanceID) {
        this.maintenanceID = maintenanceID;
    }

    public String getVehicleId() {
        return vehicleID;
    }

    public void setVehicleId(String vehicleID) {
        this.vehicleID = vehicleID;
    }

    public String getTechnicianId() {
        return technicianID;
    }

    public void setTechnicianId(String technicianID) {
        this.technicianID = technicianID;
    }

    public String getPartId() {
        return partID;
    }

    public void setPartId(String partID) {
        this.partID = partID;
    }

    public Timestamp getReportDate() {
        return reportDate;
    }

    public void setReportDate(Timestamp reportDate) {
        this.reportDate = reportDate;
    }

    public Timestamp getRepairDate() {
        return repairDate;
    }

    public void setRepairDate(Timestamp repairDate) {
        this.repairDate = repairDate;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public String getVehicleStatus() {
        return vehicleStatus;
    }

    public void setVehicleStatus(String vehicleStatus) {
        this.vehicleStatus = vehicleStatus;
    }

    @Override
    public String toString() {
        return "MaintenanceTransaction{" +
                "maintenanceID='" + maintenanceID + '\'' +
                ", vehicleID='" + vehicleID + '\'' +
                ", technicianID='" + technicianID + '\'' +
                ", partID='" + partID + '\'' +
                ", reportDate=" + reportDate +
                ", repairDate=" + repairDate +
                ", notes='" + notes + '\'' +
                ", vehicleStatus='" + vehicleStatus + '\'' +
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
