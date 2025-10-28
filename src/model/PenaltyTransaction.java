package model;

/**
 * Entity class representing a PENALTY TRANSACTION in the database.
 * 
 * PURPOSE: Maps to the 'penalties' table in MySQL database.
 * 
 * FIELDS TO IMPLEMENT:
 * - penaltyId (int/String) - Primary key
 * - rentalId (int/String) - Foreign key to RentalTransaction
 * - customerId (int/String) - Foreign key to Customer
 * - penaltyDate (Date/Timestamp) - When penalty was assessed
 * - reason (String) - "Late Return", "Damage", "Traffic Violation", etc.
 * - amount (double) - Penalty amount charged
 * - isPaid (boolean) - Payment status
 * - description (String) - Detailed description of the penalty
 * 
 * METHODS TO IMPLEMENT:
 * - Constructor(s)
 * - Getters and Setters for all fields
 * - toString() for debugging
 * - equals() and hashCode()
 * 
 * COLLABORATOR NOTES:
 * - Penalties calculated as: parts cost + technician fee (for damage)
 * - Can be late fees or damage charges
 * - Links to both Rental and Customer
 */
public class PenaltyTransaction {
    // TODO: Add private fields for penalty transaction attributes
    
    // TODO: Add constructors (default and parameterized)
    
    // TODO: Add getters and setters
    
    // TODO: Add toString(), equals(), hashCode()
}
