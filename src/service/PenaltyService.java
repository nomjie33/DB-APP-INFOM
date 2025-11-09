package service;

import dao.*;
import model.*;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Date;
import java.util.List;
import java.util.ArrayList;

/**
 * Business Logic Service for PENALTY operations.
 * 
 * PURPOSE: Calculates and manages customer penalties based on maintenance costs.
 * 
 * IMPORTANT: PenaltyService is the ONLY service that calculates costs.
 * MaintenanceService only tracks maintenance work (parts used, hours worked).
 * A penalty must be created to charge maintenance costs to customers.
 * 
 * COST FORMULAS:
 * - Labor Cost = hoursWorked × technician rate
 * - Parts Cost = Σ(part price × quantity used)
 * - Total Maintenance Cost = Labor Cost + Parts Cost
 * - Penalty Amount = Total Maintenance Cost
 * 
 * WORKFLOW:
 * 1. MaintenanceService completes maintenance and logs hours/parts
 * 2. PenaltyService calculates total cost from maintenance record
 * 3. PenaltyService creates penalty transaction to charge customer
 */
public class PenaltyService {
    
    private PenaltyDAO penaltyDAO;
    private MaintenanceDAO maintenanceDAO;
    private TechnicianDAO technicianDAO;
    private dao.MaintenanceChequeDAO maintenanceChequeDAO;
    private PartDAO partDAO;
    
    public PenaltyService() {
        this.penaltyDAO = new PenaltyDAO();
        this.maintenanceDAO = new MaintenanceDAO();
        this.technicianDAO = new TechnicianDAO();
        this.maintenanceChequeDAO = new dao.MaintenanceChequeDAO();
        this.partDAO = new PartDAO();
    }
    
    /**
     * Calculate labor cost for a maintenance job.
     * Formula: hoursWorked × technician rate
     */
    public BigDecimal calculateLaborCost(String maintenanceID) {
        System.out.println("\n=== CALCULATING LABOR COST ===");
        System.out.println("Maintenance ID: " + maintenanceID);
        
        try {
            // Get maintenance record
            MaintenanceTransaction maintenance = maintenanceDAO.getMaintenanceById(maintenanceID);
            if (maintenance == null) {
                System.out.println(":( Maintenance record not found");
                return BigDecimal.ZERO;
            }
            
            BigDecimal hoursWorked = maintenance.getHoursWorked();
            if (hoursWorked == null || hoursWorked.compareTo(BigDecimal.ZERO) <= 0) {
                System.out.println(":( Hours worked not recorded for this maintenance");
                return BigDecimal.ZERO;
            }
            
            // Get technician rate (including inactive for historical records)
            Technician technician = technicianDAO.getTechnicianByIdIncludingInactive(maintenance.getTechnicianID());
            if (technician == null) {
                System.out.println(":( Technician not found");
                return BigDecimal.ZERO;
            }
            
            BigDecimal rate = technician.getRate();
            BigDecimal laborCost = hoursWorked.multiply(rate).setScale(2, RoundingMode.HALF_UP);
            
            System.out.println("Hours Worked: " + hoursWorked);
            System.out.println("Technician Rate: Php" + rate);
            System.out.println("Labor Cost: Php" + laborCost);
            System.out.println(":) Labor cost calculated successfully");
            
            return laborCost;
            
        } catch (Exception e) {
            System.out.println(":( Error calculating labor cost: " + e.getMessage());
            e.printStackTrace();
            return BigDecimal.ZERO;
        }
    }
    
    /**
     * Calculate parts cost for a maintenance job.
     * Formula: Σ(part price × quantity used)
     */
    public BigDecimal calculatePartsCost(String maintenanceID) {
        System.out.println("\n=== CALCULATING PARTS COST ===");
        System.out.println("Maintenance ID: " + maintenanceID);
        
        try {
            // Get all parts used in this maintenance
            List<model.MaintenanceCheque> cheques = maintenanceChequeDAO.getPartsByMaintenance(maintenanceID);
            
            if (cheques == null || cheques.isEmpty()) {
                System.out.println("No parts used in this maintenance");
                return BigDecimal.ZERO;
            }
            
            BigDecimal totalPartsCost = BigDecimal.ZERO;
            System.out.println("\nParts breakdown:");
            
            for (model.MaintenanceCheque cheque : cheques) {
                String partID = cheque.getPartID();
                BigDecimal quantityUsed = cheque.getQuantityUsed();
                
                // Get part details including price (including inactive parts for historical accuracy)
                Part part = partDAO.getPartByIdIncludingInactive(partID);
                if (part == null) {
                    System.out.println(":( Part not found: " + partID);
                    continue;
                }
                
                BigDecimal partPrice = part.getPrice();
                if (partPrice == null || partPrice.compareTo(BigDecimal.ZERO) <= 0) {
                    System.out.println(":( Price not set for part: " + part.getPartName());
                    continue;
                }
                
                BigDecimal partCost = partPrice.multiply(quantityUsed).setScale(2, RoundingMode.HALF_UP);
                totalPartsCost = totalPartsCost.add(partCost);
                
                System.out.println("- " + part.getPartName() + ": " + quantityUsed + " × Php" + partPrice + " = Php" + partCost);
            }
            
            System.out.println("\nTotal Parts Cost: Php" + totalPartsCost);
            System.out.println(":) Parts cost calculated successfully");
            
            return totalPartsCost;
            
        } catch (Exception e) {
            System.out.println(":( Error calculating parts cost: " + e.getMessage());
            e.printStackTrace();
            return BigDecimal.ZERO;
        }
    }
    
    /**
     * Calculate total maintenance cost.
     * Formula: Labor Cost + Parts Cost
     */
    public BigDecimal calculateMaintenanceCost(String maintenanceID) {
        System.out.println("\n========================================");
        System.out.println("CALCULATING TOTAL MAINTENANCE COST");
        System.out.println("========================================");
        System.out.println("Maintenance ID: " + maintenanceID);
        
        BigDecimal laborCost = calculateLaborCost(maintenanceID);
        BigDecimal partsCost = calculatePartsCost(maintenanceID);
        BigDecimal totalCost = laborCost.add(partsCost).setScale(2, RoundingMode.HALF_UP);
        
        System.out.println("\n--- COST SUMMARY ---");
        System.out.println("Labor Cost:  Php" + laborCost);
        System.out.println("Parts Cost:  Php" + partsCost);
        System.out.println("-------------------");
        System.out.println("TOTAL COST:  Php" + totalCost);
        System.out.println("========================================");
        
        return totalCost;
    }
    
    /**
     * Create a penalty from a maintenance transaction.
     * The penalty amount is the total maintenance cost.
     */
    public boolean createPenaltyFromMaintenance(String penaltyID, String rentalID, String maintenanceID, Date dateIssued) {
        System.out.println("\n=== CREATING PENALTY FROM MAINTENANCE ===");
        System.out.println("Penalty ID: " + penaltyID);
        System.out.println("Rental ID: " + rentalID);
        System.out.println("Maintenance ID: " + maintenanceID);
        
        try {
            // Calculate total maintenance cost
            BigDecimal amount = calculateMaintenanceCost(maintenanceID);
            
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
     * Get detailed cost breakdown for a maintenance job.
     */
    public String getMaintenanceCostBreakdown(String maintenanceID) {
        System.out.println("\n=== MAINTENANCE COST BREAKDOWN ===");
        
        StringBuilder breakdown = new StringBuilder();
        breakdown.append("Maintenance ID: ").append(maintenanceID).append("\n");
        breakdown.append("========================================\n");
        
        try {
            MaintenanceTransaction maintenance = maintenanceDAO.getMaintenanceById(maintenanceID);
            if (maintenance == null) {
                return "Maintenance record not found";
            }
            
            // Labor details (including inactive for historical records)
            BigDecimal laborCost = calculateLaborCost(maintenanceID);
            Technician tech = technicianDAO.getTechnicianByIdIncludingInactive(maintenance.getTechnicianID());
            
            breakdown.append("\nLABOR:\n");
            if (tech != null && maintenance.getHoursWorked() != null) {
                breakdown.append("Technician: ").append(tech.getFullName()).append("\n");
                breakdown.append("Hours Worked: ").append(maintenance.getHoursWorked()).append(" hours\n");
                breakdown.append("Rate: Php").append(tech.getRate()).append("/hour\n");
                breakdown.append("Labor Cost: Php").append(laborCost).append("\n");
            } else {
                breakdown.append("Labor details not available\n");
            }
            
            // Parts details
            BigDecimal partsCost = calculatePartsCost(maintenanceID);
            List<model.MaintenanceCheque> cheques = maintenanceChequeDAO.getPartsByMaintenance(maintenanceID);
            
            breakdown.append("\nPARTS:\n");
            if (cheques != null && !cheques.isEmpty()) {
                for (model.MaintenanceCheque cheque : cheques) {
                    Part part = partDAO.getPartById(cheque.getPartID());
                    if (part != null && part.getPrice() != null) {
                        BigDecimal itemCost = part.getPrice().multiply(cheque.getQuantityUsed());
                        breakdown.append("- ").append(part.getPartName())
                                 .append(": ").append(cheque.getQuantityUsed())
                                 .append(" × Php").append(part.getPrice())
                                 .append(" = Php").append(itemCost).append("\n");
                    }
                }
                breakdown.append("Parts Cost: Php").append(partsCost).append("\n");
            } else {
                breakdown.append("No parts used\n");
            }
            
            // Total
            BigDecimal totalCost = laborCost.add(partsCost);
            breakdown.append("\n========================================\n");
            breakdown.append("TOTAL MAINTENANCE COST: Php").append(totalCost).append("\n");
            breakdown.append("========================================\n");
            
            return breakdown.toString();
            
        } catch (Exception e) {
            return "Error generating breakdown: " + e.getMessage();
        }
    }
}
