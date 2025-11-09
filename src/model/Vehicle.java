package model;

/**
 * Entity class representing a VEHICLE in the database.
 * 
 * PURPOSE: Maps to the 'vehicles' table in MySQL database.
 * 
 * FIELDS TO IMPLEMENT:
 * - vehicleId (int/String) - Primary key
 * - make (String) - Vehicle manufacturer (e.g., Toyota, Honda)
 * - model (String) - Vehicle model (e.g., Camry, Civic)
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
    private String vehicleModel;
    private String status;
    private double rentalPrice;

    
    // TODO: Add constructors (default and parameterized)
    public Vehicle(){

    }

    public Vehicle(String plateID, String vehicleType, String vehicleModel, String status, double rentalPrice)
    {
        this.plateID = plateID;
        this.vehicleType = vehicleType;
        this.vehicleModel = vehicleModel;
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
    
    public String getVehicleModel() {
        return vehicleModel;
    }
    
    public void setVehicleModel(String vehicleModel) {
        this.vehicleModel = vehicleModel;
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
        /**
     * Check if vehicle is available for rent
     * @return true if status is "Available"
     */
    public boolean isAvailable() {
        return "Available".equalsIgnoreCase(status);
    }
    
    /**
     * Check if vehicle is currently rented
     * @return true if status is "Rented"
     */
    public boolean isRented() {
        return "In Use".equalsIgnoreCase(status);
    }
    
    /**
     * Check if vehicle is in maintenance
     * @return true if status is "Maintenance"
     */
    public boolean isInMaintenance() {
        return "Maintenance".equalsIgnoreCase(status);
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
                ", model='" + vehicleModel + '\'' +
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
