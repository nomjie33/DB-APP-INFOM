package dao;

import model.RentalTransaction;
import java.sql.*;
import java.util.List;

/**
 * Data Access Object for RENTAL TRANSACTION table operations.
 * 
 * PURPOSE: Handles all database CRUD operations for rentals table.
 * 
 * METHODS TO IMPLEMENT:
 * 
 * 1. insertRental(RentalTransaction rental)
 *    - INSERT new rental record
 *    - Return generated rental ID
 * 
 * 2. updateRental(RentalTransaction rental)
 *    - UPDATE existing rental (e.g., when returned)
 * 
 * 3. deleteRental(int rentalId)
 *    - DELETE rental record (rarely used)
 * 
 * 4. getRentalById(int rentalId)
 *    - SELECT rental by ID
 * 
 * 5. getAllRentals()
 *    - SELECT all rental records
 * 
 * 6. getActiveRentals()
 *    - SELECT rentals with status = "Active"
 *    - Currently ongoing rentals
 * 
 * 7. getRentalsByCustomer(int customerId)
 *    - SELECT all rentals for a customer
 *    - Rental history
 * 
 * 8. getRentalsByVehicle(int vehicleId)
 *    - SELECT all rentals for a vehicle
 *    - Vehicle usage history
 * 
 * 9. getOverdueRentals()
 *    - SELECT rentals where actualReturnDate is null AND endDate < NOW()
 *    - For penalty processing
 * 
 * 10. completeRental(int rentalId, Timestamp returnDate)
 *     - UPDATE rental with return date and change status to "Completed"
 * 
 * COLLABORATOR NOTES:
 * - Core transaction table - handle with care
 * - Use transactions for atomicity when creating rentals
 * - Link with VehicleDAO to update vehicle status
 */
public class RentalDAO {
    
    // TODO: Implement insertRental(RentalTransaction rental)
    
    // TODO: Implement updateRental(RentalTransaction rental)
    
    // TODO: Implement deleteRental(int rentalId)
    
    // TODO: Implement getRentalById(int rentalId)
    
    // TODO: Implement getAllRentals()
    
    // TODO: Implement getActiveRentals()
    
    // TODO: Implement getRentalsByCustomer(int customerId)
    
    // TODO: Implement getRentalsByVehicle(int vehicleId)
    
    // TODO: Implement getOverdueRentals()
    
    // TODO: Implement completeRental(int rentalId, Timestamp returnDate)
}
