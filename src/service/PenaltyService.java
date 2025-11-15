package service;

import dao.*;
import model.*;
import java.math.BigDecimal;
import java.sql.Date;
import java.util.List;
import java.util.ArrayList;

/**
 * Business Logic Service for PENALTY operations.
 * 
 * PURPOSE: Manages customer penalties based on maintenance costs.
 * 
 * IMPORTANT: MaintenanceService is responsible for calculating ALL maintenance costs.
 * PenaltyService retrieves the pre-calculated cost from MaintenanceTransaction.
 * A penalty must be created to charge maintenance costs to customers.
 * 
 * WORKFLOW:
 * 1. MaintenanceService completes maintenance, calculates and stores total cost
 * 2. Admin decides if customer is at fault
 * 3. PenaltyService creates penalty transaction using stored maintenance cost
 */
public class PenaltyService {
    
    private PenaltyDAO penaltyDAO;
    private MaintenanceDAO maintenanceDAO;
    private MaintenanceService maintenanceService;
    
    public PenaltyService() {
        this.penaltyDAO = new PenaltyDAO();
        this.maintenanceDAO = new MaintenanceDAO();
        this.maintenanceService = new MaintenanceService();
    }
    
    /**
     * Get maintenance cost from MaintenanceService.
     * Retrieves the pre-calculated totalCost from the maintenance record.
     * If totalCost is not set (NULL or 0), calculates it on-demand.
     * 
     * @param maintenanceID Maintenance record ID
     * @return Total maintenance cost
     */
    public BigDecimal getMaintenanceCost(String maintenanceID) {
        try {
            MaintenanceTransaction maintenance = maintenanceDAO.getMaintenanceByIdIncludingInactive(maintenanceID);
            if (maintenance == null) {
                System.out.println(":( Maintenance record not found: " + maintenanceID);
                return BigDecimal.ZERO;
            }
            
            // Try to get stored totalCost first
            BigDecimal storedCost = maintenance.getTotalCost();
            if (storedCost != null && storedCost.compareTo(BigDecimal.ZERO) > 0) {
                return storedCost;
            }
            
            // If not stored, calculate on-demand using MaintenanceService
            System.out.println("Cost not stored, calculating on-demand for maintenance " + maintenanceID);
            return maintenanceService.calculateMaintenanceCost(maintenanceID);
            
        } catch (Exception e) {
            System.out.println(":( Error retrieving maintenance cost: " + e.getMessage());
            e.printStackTrace();
            return BigDecimal.ZERO;
        }
    }
    
    /**
     * Create a penalty from a maintenance transaction.
     * The penalty amount is retrieved from the maintenance record's totalCost.
     */
    public boolean createPenaltyFromMaintenance(String penaltyID, String rentalID, String maintenanceID, Date dateIssued) {
        System.out.println("\n=== CREATING PENALTY FROM MAINTENANCE ===");
        System.out.println("Penalty ID: " + penaltyID);
        System.out.println("Rental ID: " + rentalID);
        System.out.println("Maintenance ID: " + maintenanceID);
        
        try {
            // Get maintenance cost from maintenance record
            BigDecimal amount = getMaintenanceCost(maintenanceID);
            
            if (amount.compareTo(BigDecimal.ZERO) <= 0) {
                System.out.println(":( Cannot create penalty with zero or negative amount");
                return false;
            }
            
            // Create penalty transaction with maintenance link
            PenaltyTransaction penalty = new PenaltyTransaction(
                penaltyID,
                rentalID,
                amount,
                "UNPAID",
                maintenanceID,  // Link penalty to maintenance record
                dateIssued
            );
            
            boolean success = penaltyDAO.insertPenalty(penalty);
            
            if (success) {
                System.out.println(":) Penalty created successfully!");
                System.out.println("Penalty Amount: Php" + amount);
                System.out.println("Status: UNPAID");
            } else {
                System.out.println(":( Failed to create penalty");
            }
            
            return success;
            
        } catch (Exception e) {
            System.out.println(":( Error creating penalty: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
    
    /**
     * Get all penalties for a specific rental.
     */
    public List<PenaltyTransaction> getPenaltiesByRental(String rentalID) {
        System.out.println("\n=== GETTING PENALTIES FOR RENTAL ===");
        System.out.println("Rental ID: " + rentalID);
        try {
            List<PenaltyTransaction> penalties = penaltyDAO.getPenaltiesByRental(rentalID);
            
            if (penalties == null || penalties.isEmpty()) {
                System.out.println("No penalties found for this rental");
                return new ArrayList<>();
            }
            
            System.out.println(":) Found " + penalties.size() + " penalty/penalties");
            for (PenaltyTransaction penalty : penalties) {
                System.out.println("- " + penalty.getPenaltyID() + ": Php" + penalty.getTotalPenalty() + " (" + penalty.getPenaltyStatus() + ")");
            }
            
            return penalties;
            
        } catch (Exception e) {
            System.out.println(":( Error retrieving penalties: " + e.getMessage());
            e.printStackTrace();
            return new ArrayList<>();
        }
    }
    
    /**
     * Update penalty payment status.
     */
    public boolean updatePenaltyPayment(String penaltyID, String newStatus) {
        System.out.println("\n=== UPDATING PENALTY PAYMENT ===");
        System.out.println("Penalty ID: " + penaltyID);
        System.out.println("New Status: " + newStatus);
        try {
            // Get existing penalty
            PenaltyTransaction penalty = penaltyDAO.getPenaltyById(penaltyID);
            if (penalty == null) {
                System.out.println(":( Penalty not found");
                return false;
            }
            
            // Update status
            penalty.setPenaltyStatus(newStatus);
            boolean success = penaltyDAO.updatePenalty(penalty);
            
            if (success) {
                System.out.println(":) Penalty payment status updated successfully");
            } else {
                System.out.println(":( Failed to update penalty payment status");
            }
            
            return success;
            
        } catch (Exception e) {
            System.out.println(":( Error updating penalty: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
    
    /**
     * Cancel/void a penalty (SOFT DELETE).
     * Sets the penalty status to 'Inactive' without removing from database.
     * This preserves penalty history for reporting and auditing.
     * 
     * @param penaltyID Penalty ID to cancel
     * @return true if successful, false otherwise
     */
    public boolean cancelPenalty(String penaltyID) {
        System.out.println("\n=== CANCELLING PENALTY ===");
        System.out.println("Penalty ID: " + penaltyID);
        
        try {
            // Verify penalty exists
            PenaltyTransaction penalty = penaltyDAO.getPenaltyById(penaltyID);
            if (penalty == null) {
                System.out.println(":( Penalty not found or already inactive");
                return false;
            }
            
            // Deactivate the penalty
            boolean success = penaltyDAO.deactivatePenalty(penaltyID);
            
            if (success) {
                System.out.println(":) Penalty cancelled successfully");
                System.out.println("   Penalty ID: " + penaltyID);
                System.out.println("   Amount: Php" + penalty.getTotalPenalty());
                System.out.println("   Note: Penalty data preserved for reporting");
            } else {
                System.out.println(":( Failed to cancel penalty");
            }
            
            return success;
            
        } catch (Exception e) {
            System.out.println(":( Error cancelling penalty: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
    
    /**
     * Get detailed cost breakdown for a maintenance job.
     * Retrieves breakdown information from the maintenance record.
     * 
     * @param maintenanceID Maintenance record ID
     * @return Formatted cost breakdown string
     */
    public String getMaintenanceCostBreakdown(String maintenanceID) {
        StringBuilder breakdown = new StringBuilder();
        breakdown.append("Maintenance ID: ").append(maintenanceID).append("\n");
        breakdown.append("========================================\n");
        
        try {
            // Get maintenance record
            MaintenanceTransaction maintenance = maintenanceDAO.getMaintenanceByIdIncludingInactive(maintenanceID);
            if (maintenance == null) {
                return "Maintenance record not found";
            }
            
            // Get costs using MaintenanceService
            BigDecimal laborCost = maintenanceService.calculateLaborCost(maintenanceID);
            BigDecimal partsCost = maintenanceService.calculatePartsCost(maintenanceID);
            BigDecimal totalCost = maintenance.getTotalCost();
            
            // If totalCost not stored, calculate it
            if (totalCost == null || totalCost.compareTo(BigDecimal.ZERO) == 0) {
                totalCost = laborCost.add(partsCost);
            }
            
            breakdown.append("\nCOST BREAKDOWN:\n");
            breakdown.append("Labor Cost:  Php").append(laborCost).append("\n");
            breakdown.append("Parts Cost:  Php").append(partsCost).append("\n");
            breakdown.append("-------------------\n");
            breakdown.append("TOTAL COST:  Php").append(totalCost).append("\n");
            breakdown.append("========================================\n");
            
            return breakdown.toString();
            
        } catch (Exception e) {
            return "Error generating breakdown: " + e.getMessage();
        }
    }
}
