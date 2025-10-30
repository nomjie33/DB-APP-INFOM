package model;
import java.sql.Timestamp;
import java.sql.Date;
/**
 * Entity class representing a RENTAL TRANSACTION in the database.
 * 
 * PURPOSE: Maps to the 'rentals' table in MySQL database.
 * 
 * FIELDS TO IMPLEMENT:
 * - rentalId (int/String) - Primary key
 * - customerId (int/String) - Foreign key to Customer
 * - vehicleId (int/String) - Foreign key to Vehicle
 * - startDate (Date/Timestamp) - When rental begins
 * - endDate (Date/Timestamp) - When rental is scheduled to end
 * - actualReturnDate (Date/Timestamp) - Actual return date (null if ongoing)
 * - totalCost (double) - Calculated rental cost
 * - status (String) - "Active", "Completed", "Overdue"
 * - pickupLocationId (int/String) - Where vehicle was picked up
 * - returnLocationId (int/String) - Where vehicle should be/was returned
 * 
 * METHODS TO IMPLEMENT:
 * - Constructor(s)
 * - Getters and Setters for all fields
 * - toString() for debugging
 * - equals() and hashCode()
 * 
 * COLLABORATOR NOTES:
 * - Core transaction for the rental business
 * - Links Customer and Vehicle entities
 * - actualReturnDate null = rental still active
 */
public class RentalTransaction {
    // TODO: Add private fields for rental transaction attributes
    private String rentalID;
    private String customerID;
    private String plateID;
    private String locationID;
    private Timestamp startTime;
    private Timestamp endTime;      // NULL if rental is still active
    private Date rentalDate;
    // TODO: Add constructors (default and parameterized)
    public RentalTransaction() {
    }

    public RentalTransaction(String rentalID, String customerID, String plateID, 
                             String locationID, Timestamp startTime, Timestamp endTime, 
                             Date rentalDate) {
        this.rentalID = rentalID;
        this.customerID = customerID;
        this.plateID = plateID;
        this.locationID = locationID;
        this.startTime = startTime;
        this.endTime = endTime;
        this.rentalDate = rentalDate;
    }
    
    // TODO: Add getters and setters
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
    
    public Timestamp getStartTime() {
        return startTime;
    }
    
    public void setStartTime(Timestamp startTime) {
        this.startTime = startTime;
    }
    
    public Timestamp getEndTime() {
        return endTime;
    }
    
    public void setEndTime(Timestamp endTime) {
        this.endTime = endTime;
    }
    
    public Date getRentalDate() {
        return rentalDate;
    }
    
    public void setRentalDate(Date rentalDate) {
        this.rentalDate = rentalDate;
    }
    // TODO: Add toString(), equals(), hashCode()
        /**
     * Check if rental is still active (vehicle not returned yet)
     * @return true if active, false if completed
     */
    public boolean isActive() {
        return endTime == null;
    }
    
    /**
     * Check if rental is completed (vehicle returned)
     * @return true if completed, false if active
     */
    public boolean isCompleted() {
        return endTime != null;
    }
    
    /**
     * Calculate rental duration in hours
     * @return hours (0 if still active)
     */
    public long getRentalHours() {
        if (endTime == null || startTime == null) {
            return 0;
        }
        long milliseconds = endTime.getTime() - startTime.getTime();
        return milliseconds / (1000 * 60 * 60); // Convert to hours
    }
    
    /**
     * Calculate current duration (even if still active)
     * @return hours from start until now or until endTime
     */
    public long getCurrentDurationHours() {
        if (startTime == null) {
            return 0;
        }
        
        Timestamp end = endTime;
        if (end == null) {
            // Still active - calculate until now
            end = new Timestamp(System.currentTimeMillis());
        }
        
        long milliseconds = end.getTime() - startTime.getTime();
        return milliseconds / (1000 * 60 * 60);
    }
    
    /**
     * Get rental status as string
     * @return "Active" or "Completed"
     */
    public String getStatus() {
        return isActive() ? "Active" : "Completed";
    }
    
    // ===== STANDARD METHODS =====
    
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
                ", startTime=" + startTime +
                ", endTime=" + endTime +
                ", rentalDate=" + rentalDate +
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

