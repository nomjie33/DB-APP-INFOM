package model;
import java.sql.Timestamp;
import java.sql.Date;
/**
 * Entity class representing a RENTAL TRANSACTION in the database.
 * 
 * PURPOSE: Maps to the 'rentals' table in MySQL database.
 * 
 * SCHEMA ALIGNMENT:
 * - rentalID        : String (primary key, VARCHAR(11))
 * - customerID      : String (foreign key to Customer, VARCHAR(11))
 * - plateID         : String (foreign key to Vehicle, VARCHAR(11))
 * - locationID      : String (foreign key to Location, VARCHAR(11))
 * - startDateTime   : java.sql.Timestamp (rental start date + time, DATETIME)
 * - endDateTime     : java.sql.Timestamp (rental end date + time, DATETIME, NULL if ongoing)
 * 
 * DURATION CALCULATION:
 * - Rental duration calculated dynamically from (endDateTime - startDateTime)
 * - No need to store rentalDate separately (extract from startDateTime)
 * 
 * STATUS:
 * - Active: endDateTime is NULL (vehicle not returned)
 * - Completed: endDateTime is set (vehicle returned)
 */
public class RentalTransaction {
    private String rentalID;
    private String customerID;
    private String plateID;
    private String locationID;
    private Timestamp startDateTime;
    private Timestamp endDateTime;      // NULL if rental is still active
    
    // Default constructor
    public RentalTransaction() {
    }

    // Parameterized constructor
    public RentalTransaction(String rentalID, String customerID, String plateID, 
                             String locationID, Timestamp startDateTime, Timestamp endDateTime) {
        this.rentalID = rentalID;
        this.customerID = customerID;
        this.plateID = plateID;
        this.locationID = locationID;
        this.startDateTime = startDateTime;
        this.endDateTime = endDateTime;
    }
    
    // Getters and setters
    public String getRentalID() {
        return rentalID;
    }
    
    public void setRentalID(String rentalID) {
        this.rentalID = rentalID;
    }
    
    public String getCustomerID() {
        return customerID;
    }
    
    public void setCustomerID(String customerID) {
        this.customerID = customerID;
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
    
    /**
     * Extract rental date from startDateTime
     * @return Date portion of startDateTime
     */
    public Date getRentalDate() {
        if (startDateTime == null) {
            return null;
        }
        return new Date(startDateTime.getTime());
    }
    
    /**
     * Check if rental is still active (vehicle not returned yet)
     * @return true if active, false if completed
     */
    public boolean isActive() {
        return endDateTime == null;
    }
    
    /**
     * Check if rental is completed (vehicle returned)
     * @return true if completed, false if active
     */
    public boolean isCompleted() {
        return endDateTime != null;
    }
    
    /**
     * Calculate rental duration in hours
     * @return hours (0 if still active)
     */
    public long getRentalHours() {
        if (endDateTime == null || startDateTime == null) {
            return 0;
        }
        long milliseconds = endDateTime.getTime() - startDateTime.getTime();
        return milliseconds / (1000 * 60 * 60); // Convert to hours
    }
    
    /**
     * Calculate current duration (even if still active)
     * @return hours from start until now or until endDateTime
     */
    public long getCurrentDurationHours() {
        if (startDateTime == null) {
            return 0;
        }
        
        Timestamp end = endDateTime;
        if (end == null) {
            // Still active - calculate until now
            end = new Timestamp(System.currentTimeMillis());
        }
        
        long milliseconds = end.getTime() - startDateTime.getTime();
        return milliseconds / (1000 * 60 * 60);
    }
    
    /**
     * Get rental status as string
     * @return "Active" or "Completed"
     */
    public String getStatus() {
        return isActive() ? "Active" : "Completed";
    }
    
    /**
     * String representation for debugging
     */
    @Override
    public String toString() {
        return "RentalTransaction{" +
                "rentalID='" + rentalID + '\'' +
                ", customerID='" + customerID + '\'' +
                ", plateID='" + plateID + '\'' +
                ", locationID='" + locationID + '\'' +
                ", startDateTime=" + startDateTime +
                ", endDateTime=" + endDateTime +
                ", status='" + getStatus() + '\'' +
                '}';
    }
    
    /**
     * Check if two rentals are the same
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        
        RentalTransaction that = (RentalTransaction) o;
        return rentalID != null && rentalID.equals(that.rentalID);
    }
    
    /**
     * Generate hash code based on rentalID
     */
    @Override
    public int hashCode() {
        return rentalID != null ? rentalID.hashCode() : 0;
    }
}

