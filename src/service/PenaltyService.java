package service;

import dao.*;
import model.*;

/**
 * Business Logic Service for PENALTY operations.
 * 
 * PURPOSE: Calculates and manages customer penalties for damages and violations.
 * 
 * DEPENDENCIES:
 * - PenaltyDAO (penalty records)
 * - RentalDAO (get rental details)
 * - MaintenanceDAO (get repair costs for damage penalties)
 * - CustomerDAO (customer information)
 * 
 * METHODS TO IMPLEMENT:
 * 
 * 1. assessDamagePenalty(int rentalId, int customerId, int maintenanceId, String description)
 *    WORKFLOW:
 *    - Get maintenance record to get repair cost (MaintenanceDAO)
 *    - Calculate penalty = parts cost + technician fee
 *    - Create penalty record (PenaltyDAO)
 *    - Link to rental and customer
 *    - Return penalty ID
 * 
 * 2. assessLatePenalty(int rentalId, int customerId, int daysLate)
 *    WORKFLOW:
 *    - Get rental details (RentalDAO)
 *    - Get vehicle daily rate (from rental)
 *    - Calculate late fee = daysLate × daily rate × late fee multiplier (e.g., 1.5)
 *    - Create penalty record with reason "Late Return"
 *    - Return penalty ID
 * 
 * 3. assessCustomPenalty(int rentalId, int customerId, double amount, String reason, String description)
 *    - For miscellaneous penalties (traffic tickets, cleaning fees, etc.)
 *    - Create penalty record with custom amount
 * 
 * 4. markPenaltyAsPaid(int penaltyId)
 *    - Update penalty payment status
 *    - Record payment date
 * 
 * 5. getCustomerPenalties(int customerId)
 *    - Get all penalties for a customer
 *    - For customer penalty history
 * 
 * 6. getUnpaidPenaltiesForCustomer(int customerId)
 *    - Get outstanding penalties
 *    - For collections
 * 
 * 7. getTotalPenaltyAmount(int customerId)
 *    - Calculate total penalty debt
 * 
 * COLLABORATOR NOTES:
 * - Damage penalty formula: maintenance total cost (parts + labor)
 * - Late penalty formula: days × rate × multiplier
 * - Always link penalties to rentals and customers
 * - Track payment status carefully
 */
public class PenaltyService {
    
    // Private DAO instances
    // TODO: Initialize DAO objects in constructor
    
    // TODO: Implement assessDamagePenalty()
    
    // TODO: Implement assessLatePenalty()
    
    // TODO: Implement assessCustomPenalty()
    
    // TODO: Implement markPenaltyAsPaid()
    
    // TODO: Implement getCustomerPenalties()
    
    // TODO: Implement getUnpaidPenaltiesForCustomer()
    
    // TODO: Implement getTotalPenaltyAmount()
}
