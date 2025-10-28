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
    
    // TODO: Add constructors (default and parameterized)
    
    // TODO: Add getters and setters
    
    // TODO: Add toString(), equals(), hashCode()
}
