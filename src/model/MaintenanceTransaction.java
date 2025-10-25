package model;

/**
 * Entity class representing a MAINTENANCE TRANSACTION in the database.
 *
 * PURPOSE: Maps to the 'maintenance' table in MySQL database. This class
 * follows the simplified table layout used by the team:
 *
 * Table columns (expected):
 * - maintenanceId   : String (primary key, e.g. "M001")
 * - vehicleId       : String (foreign key to Vehicle, e.g. "V103")
 * - technicianId    : String (foreign key to Technician, e.g. "T01")
 * - partId          : String (part used / reference, e.g. "P01")
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
 * - Use `maintenanceId` as the identity in equals() / hashCode().
 * - Use java.sql.Timestamp for reportDate and repairDate to match DB TIMESTAMP.
 */
import java.sql.Timestamp;
import java.util.Objects;
public class MaintenanceTransaction {
    // Fields matching the simplified maintenance table
    private String maintenanceId;
    private String vehicleId;
    private String technicianId;
    private String partId;
    private Timestamp reportDate;
    private Timestamp repairDate;
    private String notes;
    private String vehicleStatus;

    // Default constructor
    public MaintenanceTransaction() {
    }

    // Parameterized constructor
    public MaintenanceTransaction(String maintenanceId, String vehicleId, String technicianId,
                                  String partId, Timestamp reportDate, Timestamp repairDate,
                                  String notes, String vehicleStatus) {
        this.maintenanceId = maintenanceId;
        this.vehicleId = vehicleId;
        this.technicianId = technicianId;
        this.partId = partId;
        this.reportDate = reportDate;
        this.repairDate = repairDate;
        this.notes = notes;
        this.vehicleStatus = vehicleStatus;
    }

    // Getters and setters
    public String getMaintenanceId() {
        return maintenanceId;
    }

    public void setMaintenanceId(String maintenanceId) {
        this.maintenanceId = maintenanceId;
    }

    public String getVehicleId() {
        return vehicleId;
    }

    public void setVehicleId(String vehicleId) {
        this.vehicleId = vehicleId;
    }

    public String getTechnicianId() {
        return technicianId;
    }

    public void setTechnicianId(String technicianId) {
        this.technicianId = technicianId;
    }

    public String getPartId() {
        return partId;
    }

    public void setPartId(String partId) {
        this.partId = partId;
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
                "maintenanceId='" + maintenanceId + '\'' +
                ", vehicleId='" + vehicleId + '\'' +
                ", technicianId='" + technicianId + '\'' +
                ", partId='" + partId + '\'' +
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
        return maintenanceId != null && maintenanceId.equals(that.maintenanceId);
    }

    @Override
    public int hashCode() {
        return maintenanceId != null ? maintenanceId.hashCode() : 0;
    }
}
