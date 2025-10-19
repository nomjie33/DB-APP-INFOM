package dao;

import model.PenaltyTransaction;
import java.sql.*;
import java.util.List;

/**
 * Data Access Object for PENALTY TRANSACTION table operations.
 * 
 * PURPOSE: Handles all database CRUD operations for penalties table.
 * 
 * METHODS TO IMPLEMENT:
 * 
 * 1. insertPenalty(PenaltyTransaction penalty)
 *    - INSERT new penalty record
 * 
 * 2. updatePenalty(PenaltyTransaction penalty)
 *    - UPDATE penalty (e.g., mark as paid)
 * 
 * 3. deletePenalty(int penaltyId)
 *    - DELETE penalty record
 * 
 * 4. getPenaltyById(int penaltyId)
 *    - SELECT penalty by ID
 * 
 * 5. getAllPenalties()
 *    - SELECT all penalties
 * 
 * 6. getPenaltiesByRental(int rentalId)
 *    - SELECT penalties for a rental
 * 
 * 7. getPenaltiesByCustomer(int customerId)
 *    - SELECT all penalties for a customer
 *    - Customer penalty history
 * 
 * 8. getUnpaidPenalties()
 *    - SELECT penalties where isPaid = false
 *    - For collections
 * 
 * 9. markPenaltyAsPaid(int penaltyId)
 *    - UPDATE isPaid to true
 * 
 * 10. getTotalPenaltiesByCustomer(int customerId)
 *     - SUM penalty amounts for a customer
 * 
 * COLLABORATOR NOTES:
 * - Penalty amount = parts cost + technician fee (for damage)
 * - Can also be flat late return fees
 * - Track payment status carefully
 */
public class PenaltyDAO {
    
    // TODO: Implement insertPenalty(PenaltyTransaction penalty)
    
    // TODO: Implement updatePenalty(PenaltyTransaction penalty)
    
    // TODO: Implement deletePenalty(int penaltyId)
    
    // TODO: Implement getPenaltyById(int penaltyId)
    
    // TODO: Implement getAllPenalties()
    
    // TODO: Implement getPenaltiesByRental(int rentalId)
    
    // TODO: Implement getPenaltiesByCustomer(int customerId)
    
    // TODO: Implement getUnpaidPenalties()
    
    // TODO: Implement markPenaltyAsPaid(int penaltyId)
    
    // TODO: Implement getTotalPenaltiesByCustomer(int customerId)
}
