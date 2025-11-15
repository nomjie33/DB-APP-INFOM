package model;

/**
 * Entity class representing a VEHICLE in the database.
 * 
 * PURPOSE: Maps to the 'vehicles' table in MySQL database.
 * 
 * FIELDS TO IMPLEMENT:
 * - vehicleId (int/String) - Primary key
 * - make (String) - Vehicle manufacturer (e.g., Toyota, Honda)
 * - year (int) - Year of manufacture
 * - licensePlate (String) - Unique plate number
 * - status (String) - Current status: "Available", "In Use", "Under Maintenance", "Defective"
 * - dailyRate (double) - Rental rate per day
 * - locationId (int/String) - Foreign key to Location (current branch)
 * - mileage (int) - Current odometer reading
 * 
 * METHODS TO IMPLEMENT:
 * - Constructor(s)
 * - Getters and Setters for all fields
 * - toString() for debugging
 * - equals() and hashCode()
 * 
 * COLLABORATOR NOTES:
 * - Status field is critical for rental logic
 * - LocationId links this to the Location entity
 */
public class Vehicle {
    // TODO: Add private fields for vehicle attributes
    private String plateID;
    private String vehicleType;
    private String status;
    private double rentalPrice;

    
    // TODO: Add constructors (default and parameterized)
    public Vehicle(){
        this.status ="Available";
    }

    public Vehicle(String plateID, String vehicleType, String status, double rentalPrice)
    {
        this.plateID = plateID;
        this.vehicleType = vehicleType;
        this.status = status;
        this.rentalPrice = rentalPrice;
    }
    // TODO: Add getters and setters
        public String getPlateID() {
        return plateID;
    }
    
    public void setPlateID(String plateID) {
        this.plateID = plateID;
    }
    
    public String getVehicleType() {
        return vehicleType;
    }
    
    public void setVehicleType(String vehicleType) {
        this.vehicleType = vehicleType;
    }
    
    public String getStatus() {
        return status;
    }
    
    public void setStatus(String status) {
        this.status = status;
    }
    
    public double getRentalPrice() {
        return rentalPrice;
    }
    
    public void setRentalPrice(double rentalPrice) {
        this.rentalPrice = rentalPrice;
    }

    // TODO: Add toString(), equals(), hashCode()
    public boolean isAvailable() {
        return "Available".equalsIgnoreCase(status);
    }
    
    public boolean isInUse() {
        return "In Use".equalsIgnoreCase(status);
    }
    
    public boolean isInMaintenance() {
        return "Maintenance".equalsIgnoreCase(status);
    }
    
    public boolean isActive() {
        return !"Inactive".equalsIgnoreCase(status);
    }
    
    public String getDisplayName() {
        return vehicleType + " (" + plateID + ")";
    }
    
    /**
     * Check if vehicle is an e-scooter
     * @return true if vehicleType contains "Scooter"
     */
    public boolean isEScooter() {
        return vehicleType != null && vehicleType.toLowerCase().contains("scooter");
    }
    
    /**
     * Check if vehicle is an e-bike
     * @return true if vehicleType contains "Bike"
     */
    public boolean isEBike() {
        return vehicleType != null && vehicleType.toLowerCase().contains("bike");
    }
    
    /**
     * Check if vehicle is an e-trike
     * @return true if vehicleType contains "Trike"
     */
    public boolean isETrike() {
        return vehicleType != null && vehicleType.toLowerCase().contains("trike");
    }
    
    
    // ========== UTILITY METHODS ==========
    
    /**
     * String representation for debugging
     */
    @Override
    public String toString() {
        return "Vehicle{" +
                "plateID='" + plateID + '\'' +
                ", type='" + vehicleType + '\'' +
                ", status='" + status + '\'' +
                ", price=" + rentalPrice +
                '}';
    }
    
    /**
     * Compare vehicles by plate ID
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        
        Vehicle vehicle = (Vehicle) o;
        return plateID != null && plateID.equals(vehicle.plateID);
    }
    
    /**
     * Hash code based on plate ID
     */
    @Override
    public int hashCode() {
        return plateID != null ? plateID.hashCode() : 0;
    }
}
