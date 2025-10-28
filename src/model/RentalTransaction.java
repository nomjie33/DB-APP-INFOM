package model;

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
    
    // TODO: Add constructors (default and parameterized)
    
    // TODO: Add getters and setters
    
    // TODO: Add toString(), equals(), hashCode()
}
