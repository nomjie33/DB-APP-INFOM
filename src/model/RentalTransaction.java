package model;

import java.sql.Timestamp;

/**
 * RentalTransaction entity representing a vehicle rental.
 */
public class RentalTransaction {
    private String rentalID;
    private String customerID;
    private String plateID;
    private String locationID;
    private Timestamp pickUpDateTime;  // Customer's chosen pickup schedule
    private Timestamp startDateTime;   // Actual rental start (set by admin)
    private Timestamp endDateTime;
    private String status;  // ADDED: Active, Completed, or Cancelled
    
    // Default constructor
    public RentalTransaction() {
        this.status = "Active";  // Default to Active
    }
    
    // Parameterized constructor
    public RentalTransaction(String rentalID, String customerID, String plateID, 
                           String locationID, Timestamp pickUpDateTime, Timestamp startDateTime, Timestamp endDateTime) {
        this.rentalID = rentalID;
        this.customerID = customerID;
        this.plateID = plateID;
        this.locationID = locationID;
        this.pickUpDateTime = pickUpDateTime;
        this.startDateTime = startDateTime;
        this.endDateTime = endDateTime;
        this.status = "Active";  // Default to Active
    }
    
    // Full constructor with status
    public RentalTransaction(String rentalID, String customerID, String plateID, 
                           String locationID, Timestamp pickUpDateTime, Timestamp startDateTime, Timestamp endDateTime, String status) {
        this.rentalID = rentalID;
        this.customerID = customerID;
        this.plateID = plateID;
        this.locationID = locationID;
        this.pickUpDateTime = pickUpDateTime;
        this.startDateTime = startDateTime;
        this.endDateTime = endDateTime;
        this.status = status;
    }
    
    // Getters and Setters
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
    
    public Timestamp getPickUpDateTime() {
        return pickUpDateTime;
    }
    
    public void setPickUpDateTime(Timestamp pickUpDateTime) {
        this.pickUpDateTime = pickUpDateTime;
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
    
    // ADDED: Status getter and setter
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
    
    public boolean isCompleted() {
        return "Completed".equalsIgnoreCase(status);
    }
    
    public boolean isCancelled() {
        return "Cancelled".equalsIgnoreCase(status);
    }
    
    public boolean isOngoing() {
        return endDateTime == null && "Active".equalsIgnoreCase(status);
    }
    
    /**
     * Check if the vehicle has been picked up (rental has started).
     * A rental is considered not picked up if startDateTime has not been set by admin.
     * 
     * @return true if startDateTime is set (vehicle picked up), false otherwise
     */
    public boolean isPickedUp() {
        return startDateTime != null;
    }
    
    public long getDurationInHours() {
        if (endDateTime != null && startDateTime != null) {
            long diffInMillis = endDateTime.getTime() - startDateTime.getTime();
            return diffInMillis / (1000 * 60 * 60);
        }
        return 0;
    }
    
    @Override
    public String toString() {
        return "RentalTransaction{" +
                "rentalID='" + rentalID + '\'' +
                ", customerID='" + customerID + '\'' +
                ", plateID='" + plateID + '\'' +
                ", locationID='" + locationID + '\'' +
                ", pickUpDateTime=" + pickUpDateTime +
                ", startDateTime=" + startDateTime +
                ", endDateTime=" + endDateTime +
                ", status='" + status + '\'' +
                '}';
    }
}