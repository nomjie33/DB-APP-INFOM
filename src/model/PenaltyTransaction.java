package model;

/**
 * Entity class representing a PENALTY TRANSACTION in the database.
 * 
 * PURPOSE: Maps to the 'penalty' table in MySQL database.
 * 
 * SCHEMA:
 * - penaltyID      : String (primary key, VARCHAR(11))
 * - rentalID       : String (foreign key to Rental, VARCHAR(25))
 * - totalPenalty   : java.math.BigDecimal (penalty amount, DECIMAL(10,2))
 * - penaltyStatus  : String (status of penalty, VARCHAR(15))
 * - maintenanceID  : String (foreign key to Maintenance, VARCHAR(11))
 * - dateIssued     : java.sql.Date (when penalty was issued)
 * 
 * RELATIONSHIP:
 * - Many-to-one with RentalTransaction
 * - Many-to-one with MaintenanceTransaction
 */
import java.math.BigDecimal;
import java.sql.Date;

public class PenaltyTransaction {
    private String penaltyID;
    private String rentalID;
    private BigDecimal totalPenalty;
    private String penaltyStatus;
    private String maintenanceID;
    private Date dateIssued;

    // Default constructor
    public PenaltyTransaction() {
    }

    // Parameterized constructor
    public PenaltyTransaction(String penaltyID, String rentalID, BigDecimal totalPenalty,
                             String penaltyStatus, String maintenanceID, Date dateIssued) {
        this.penaltyID = penaltyID;
        this.rentalID = rentalID;
        this.totalPenalty = totalPenalty;
        this.penaltyStatus = penaltyStatus;
        this.maintenanceID = maintenanceID;
        this.dateIssued = dateIssued;
    }

    // Getters and setters
    public String getPenaltyID() {
        return penaltyID;
    }

    public void setPenaltyID(String penaltyID) {
        this.penaltyID = penaltyID;
    }

    public String getRentalID() {
        return rentalID;
    }

    public void setRentalID(String rentalID) {
        this.rentalID = rentalID;
    }

    public BigDecimal getTotalPenalty() {
        return totalPenalty;
    }

    public void setTotalPenalty(BigDecimal totalPenalty) {
        this.totalPenalty = totalPenalty;
    }

    public String getPenaltyStatus() {
        return penaltyStatus;
    }

    public void setPenaltyStatus(String penaltyStatus) {
        this.penaltyStatus = penaltyStatus;
    }

    public String getMaintenanceID() {
        return maintenanceID;
    }

    public void setMaintenanceID(String maintenanceID) {
        this.maintenanceID = maintenanceID;
    }

    public Date getDateIssued() {
        return dateIssued;
    }

    public void setDateIssued(Date dateIssued) {
        this.dateIssued = dateIssued;
    }

    @Override
    public String toString() {
        return "PenaltyTransaction{" +
                "penaltyID='" + penaltyID + '\'' +
                ", rentalID='" + rentalID + '\'' +
                ", totalPenalty=" + totalPenalty +
                ", penaltyStatus='" + penaltyStatus + '\'' +
                ", maintenanceID='" + maintenanceID + '\'' +
                ", dateIssued=" + dateIssued +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PenaltyTransaction that = (PenaltyTransaction) o;
        return penaltyID != null && penaltyID.equals(that.penaltyID);
    }

    @Override
    public int hashCode() {
        return penaltyID != null ? penaltyID.hashCode() : 0;
    }
}
