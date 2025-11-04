package test;

import service.*;
import model.*;
import java.math.BigDecimal;
import java.sql.Date;
import java.util.List;
import java.util.ArrayList;

/**
 * Test Suite for MaintenanceService and PenaltyService
 * 
 * PURPOSE: Validate business logic and cost calculation functionality
 * 
 * TEST DATA REQUIREMENTS:
 * - Execute test_data.sql before running these tests
 * - Ensure database has parts with prices
 * - Ensure maintenance records have hoursWorked
 * 
 * SUCCESS INDICATORS:
 * - :) appears in console for successful operations
 * - :( appears for expected failures or errors
 * 
 * TEST CATEGORIES:
 * A. MaintenanceService Tests (Operations)
 * B. PenaltyService Tests (Cost Calculations)
 * C. Integration Tests (Maintenance → Penalty Workflow)
 */
public class ServicesTest {
    
    private static MaintenanceService maintenanceService;
    private static PenaltyService penaltyService;
    
    public static void main(String[] args) {
        System.out.println("========================================");
        System.out.println("SERVICES TEST SUITE");
        System.out.println("========================================\n");
        
        // Initialize services
        maintenanceService = new MaintenanceService();
        penaltyService = new PenaltyService();
        
        // Run test categories
        testMaintenanceService();
        testPenaltyService();
        testIntegration();
        
        System.out.println("\n========================================");
        System.out.println("TEST SUITE COMPLETED");
        System.out.println("========================================");
    }
    
    // ====================================================================
    // A. MAINTENANCE SERVICE TESTS
    // ====================================================================
    
    private static void testMaintenanceService() {
        System.out.println("\n╔════════════════════════════════════════╗");
        System.out.println("║  MAINTENANCE SERVICE TESTS             ║");
        System.out.println("╚════════════════════════════════════════╝\n");
        
        testScheduleMaintenance();
        testCompleteMaintenance();
        testFlagVehicleAsDefective();
        testGetMaintenanceHistory();
        testReassignTechnician();
        testGetTechnicianWorkload();
        testGetPartsUsedInMaintenance();
    }
    
    /**
     * Test 1: Schedule Maintenance
     * Tests scheduling a new maintenance job
     */
    private static void testScheduleMaintenance() {
        System.out.println("\n--- Test 1: Schedule Maintenance ---");
        
        boolean result = maintenanceService.scheduleMaintenance(
            "MAINT-TEST1",
            "ES-004",      // Available e-scooter
            "TECH-001",    // Electrical technician
            "Routine inspection - 1000km service",
            Date.valueOf("2024-10-28")
        );
        
        if (result) {
            System.out.println(":) Test 1 PASSED - Maintenance scheduled successfully");
        } else {
            System.out.println(":( Test 1 FAILED - Could not schedule maintenance");
        }
    }
    
    /**
     * Test 2: Complete Maintenance with Hours and Parts
     * Tests completing a maintenance job and logging hours worked
     */
    private static void testCompleteMaintenance() {
        System.out.println("\n--- Test 2: Complete Maintenance ---");
        
        // Create list of parts used
        List<MaintenanceService.PartUsage> parts = new ArrayList<>();
        parts.add(new MaintenanceService.PartUsage("PART-003", new BigDecimal("2.00"))); // 2 brake pads
        parts.add(new MaintenanceService.PartUsage("PART-011", new BigDecimal("1.00"))); // 1 brake cable
        
        boolean result = maintenanceService.completeMaintenance(
            "MAINT-TEST1",
            Date.valueOf("2024-10-28"),
            new BigDecimal("1.5"),  // 1.5 hours worked
            parts
        );
        
        if (result) {
            System.out.println(":) Test 2 PASSED - Maintenance completed with hours logged");
        } else {
            System.out.println(":( Test 2 FAILED - Could not complete maintenance");
        }
    }
    
    /**
     * Test 3: Flag Vehicle as Defective
     * Tests creating a maintenance record for a defective vehicle
     */
    private static void testFlagVehicleAsDefective() {
        System.out.println("\n--- Test 3: Flag Vehicle as Defective ---");
        
        String maintenanceID = maintenanceService.flagVehicleAsDefective(
            "ES-001",
            "Battery not charging properly - requires diagnostic",
            "TECH-003",  // Battery specialist
            Date.valueOf("2024-10-28")
        );
        
        if (maintenanceID != null) {
            System.out.println(":) Test 3 PASSED - Vehicle flagged, ID: " + maintenanceID);
        } else {
            System.out.println(":( Test 3 FAILED - Could not flag vehicle");
        }
    }
    
    /**
     * Test 4: Get Maintenance History
     * Tests retrieving all maintenance records for a vehicle
     */
    private static void testGetMaintenanceHistory() {
        System.out.println("\n--- Test 4: Get Maintenance History ---");
        
        List<MaintenanceTransaction> history = maintenanceService.getMaintenanceHistory("ES-001");
        
        System.out.println("Found " + history.size() + " maintenance record(s) for ES-001:");
        for (MaintenanceTransaction mt : history) {
            System.out.println("  - " + mt.getMaintenanceID() + ": " + mt.getNotes());
        }
        
        if (history.size() > 0) {
            System.out.println(":) Test 4 PASSED - History retrieved");
        } else {
            System.out.println(":( Test 4 FAILED - No history found");
        }
    }
    
    /**
     * Test 5: Reassign Technician
     * Tests changing the technician assigned to a maintenance job
     */
    private static void testReassignTechnician() {
        System.out.println("\n--- Test 5: Reassign Technician ---");
        
        boolean result = maintenanceService.assignTechnician(
            "MAINT-001",
            "TECH-005"  // Reassign to different technician
        );
        
        if (result) {
            System.out.println(":) Test 5 PASSED - Technician reassigned");
        } else {
            System.out.println(":( Test 5 FAILED - Could not reassign technician");
        }
    }
    
    /**
     * Test 6: Get Technician Workload
     * Tests retrieving all maintenance jobs for a technician
     */
    private static void testGetTechnicianWorkload() {
        System.out.println("\n--- Test 6: Get Technician Workload ---");
        
        List<MaintenanceTransaction> workload = maintenanceService.getTechnicianWorkload("TECH-001");
        
        System.out.println("TECH-001 has " + workload.size() + " maintenance job(s):");
        for (MaintenanceTransaction mt : workload) {
            String status = (mt.getDateRepaired() == null) ? "In Progress" : "Completed";
            System.out.println("  - " + mt.getMaintenanceID() + " [" + status + "]");
        }
        
        if (workload.size() > 0) {
            System.out.println(":) Test 6 PASSED - Workload retrieved");
        } else {
            System.out.println(":( Test 6 FAILED - No workload found");
        }
    }
    
    /**
     * Test 7: Get Parts Used in Maintenance
     * Tests retrieving all parts used in a maintenance job
     */
    private static void testGetPartsUsedInMaintenance() {
        System.out.println("\n--- Test 7: Get Parts Used in Maintenance ---");
        
        List<MaintenanceCheque> parts = maintenanceService.getPartsUsedInMaintenance("MAINT-002");
        
        System.out.println("MAINT-002 used " + parts.size() + " part type(s):");
        for (MaintenanceCheque mc : parts) {
            System.out.println("  - " + mc.getPartID() + ": " + mc.getQuantityUsed() + " unit(s)");
        }
        
        if (parts.size() > 0) {
            System.out.println(":) Test 7 PASSED - Parts retrieved");
        } else {
            System.out.println(":( Test 7 FAILED - No parts found");
        }
    }
    
    // ====================================================================
    // B. PENALTY SERVICE TESTS
    // ====================================================================
    
    private static void testPenaltyService() {
        System.out.println("\n╔════════════════════════════════════════╗");
        System.out.println("║  PENALTY SERVICE TESTS                 ║");
        System.out.println("╚════════════════════════════════════════╝\n");
        
        testCalculateLaborCost();
        testCalculatePartsCost();
        testCalculateMaintenanceCost();
        testGetMaintenanceCostBreakdown();
        testCreatePenaltyFromMaintenance();
        testGetPenaltiesByRental();
        testUpdatePenaltyPayment();
    }
    
    /**
     * Test 8: Calculate Labor Cost
     * Tests labor cost calculation: hoursWorked × technician rate
     */
    private static void testCalculateLaborCost() {
        System.out.println("\n--- Test 8: Calculate Labor Cost ---");
        
        BigDecimal laborCost = penaltyService.calculateLaborCost("MAINT-001");
        
        // Expected: 3.5 hours × ₱350/hour = ₱1,225.00
        BigDecimal expected = new BigDecimal("1225.00");
        
        if (laborCost.compareTo(expected) == 0) {
            System.out.println(":) Test 8 PASSED - Labor cost correct: ₱" + laborCost);
        } else {
            System.out.println(":( Test 8 FAILED - Expected ₱" + expected + ", got ₱" + laborCost);
        }
    }
    
    /**
     * Test 9: Calculate Parts Cost
     * Tests parts cost calculation: Σ(price × quantity)
     */
    private static void testCalculatePartsCost() {
        System.out.println("\n--- Test 9: Calculate Parts Cost ---");
        
        BigDecimal partsCost = penaltyService.calculatePartsCost("MAINT-002");
        
        // Expected: (2 × ₱150) + (1 × ₱95) = ₱395.00
        BigDecimal expected = new BigDecimal("395.00");
        
        if (partsCost.compareTo(expected) == 0) {
            System.out.println(":) Test 9 PASSED - Parts cost correct: ₱" + partsCost);
        } else {
            System.out.println(":( Test 9 FAILED - Expected ₱" + expected + ", got ₱" + partsCost);
        }
    }
    
    /**
     * Test 10: Calculate Total Maintenance Cost
     * Tests total cost: labor + parts
     */
    private static void testCalculateMaintenanceCost() {
        System.out.println("\n--- Test 10: Calculate Total Maintenance Cost ---");
        
        BigDecimal totalCost = penaltyService.calculateMaintenanceCost("MAINT-001");
        
        // Expected: ₱1,225 (labor) + ₱2,500 (battery) = ₱3,725.00
        BigDecimal expected = new BigDecimal("3725.00");
        
        if (totalCost.compareTo(expected) == 0) {
            System.out.println(":) Test 10 PASSED - Total cost correct: ₱" + totalCost);
        } else {
            System.out.println(":( Test 10 FAILED - Expected ₱" + expected + ", got ₱" + totalCost);
        }
    }
    
    /**
     * Test 11: Get Maintenance Cost Breakdown
     * Tests detailed cost report generation
     */
    private static void testGetMaintenanceCostBreakdown() {
        System.out.println("\n--- Test 11: Get Maintenance Cost Breakdown ---");
        
        String breakdown = penaltyService.getMaintenanceCostBreakdown("MAINT-008");
        
        System.out.println("\nBreakdown Report:");
        System.out.println(breakdown);
        
        if (breakdown.contains("TOTAL MAINTENANCE COST")) {
            System.out.println(":) Test 11 PASSED - Breakdown generated");
        } else {
            System.out.println(":( Test 11 FAILED - Breakdown incomplete");
        }
    }
    
    /**
     * Test 12: Create Penalty from Maintenance
     * Tests creating a penalty transaction based on maintenance costs
     */
    private static void testCreatePenaltyFromMaintenance() {
        System.out.println("\n--- Test 12: Create Penalty from Maintenance ---");
        
        boolean result = penaltyService.createPenaltyFromMaintenance(
            "PEN-TEST1",
            "RNT-001",
            "MAINT-001",
            Date.valueOf("2024-10-28")
        );
        
        if (result) {
            System.out.println(":) Test 12 PASSED - Penalty created successfully");
        } else {
            System.out.println(":( Test 12 FAILED - Could not create penalty");
        }
    }
    
    /**
     * Test 13: Get Penalties by Rental
     * Tests retrieving all penalties for a rental
     */
    private static void testGetPenaltiesByRental() {
        System.out.println("\n--- Test 13: Get Penalties by Rental ---");
        
        List<PenaltyTransaction> penalties = penaltyService.getPenaltiesByRental("RNT-005");
        
        System.out.println("Found " + penalties.size() + " penalty/penalties for RNT-005");
        
        if (penalties.size() > 0) {
            System.out.println(":) Test 13 PASSED - Penalties retrieved");
        } else {
            System.out.println(":( Test 13 FAILED - No penalties found");
        }
    }
    
    /**
     * Test 14: Update Penalty Payment Status
     * Tests changing penalty status from UNPAID to PAID
     */
    private static void testUpdatePenaltyPayment() {
        System.out.println("\n--- Test 14: Update Penalty Payment Status ---");
        
        boolean result = penaltyService.updatePenaltyPayment("PEN-001", "PAID");
        
        if (result) {
            System.out.println(":) Test 14 PASSED - Payment status updated");
        } else {
            System.out.println(":( Test 14 FAILED - Could not update status");
        }
    }
    
    // ====================================================================
    // C. INTEGRATION TESTS
    // ====================================================================
    
    private static void testIntegration() {
        System.out.println("\n╔════════════════════════════════════════╗");
        System.out.println("║  INTEGRATION TESTS                     ║");
        System.out.println("╚════════════════════════════════════════╝\n");
        
        testMaintenanceToPenaltyWorkflow();
        testZeroCostHandling();
        testMissingDataHandling();
    }
    
    /**
     * Test 15: Complete Maintenance → Penalty Workflow
     * Tests the full workflow from maintenance to penalty creation
     */
    private static void testMaintenanceToPenaltyWorkflow() {
        System.out.println("\n--- Test 15: Maintenance → Penalty Workflow ---");
        
        // Step 1: Schedule maintenance
        System.out.println("\nStep 1: Scheduling maintenance...");
        boolean scheduled = maintenanceService.scheduleMaintenance(
            "MAINT-INTEG1",
            "ES-010",
            "TECH-002",
            "Customer reported brake issues",
            Date.valueOf("2024-10-28")
        );
        
        if (!scheduled) {
            System.out.println(":( Test 15 FAILED - Could not schedule maintenance");
            return;
        }
        
        // Step 2: Complete maintenance
        System.out.println("\nStep 2: Completing maintenance...");
        List<MaintenanceService.PartUsage> parts = new ArrayList<>();
        parts.add(new MaintenanceService.PartUsage("PART-003", new BigDecimal("2.00")));
        
        boolean completed = maintenanceService.completeMaintenance(
            "MAINT-INTEG1",
            Date.valueOf("2024-10-28"),
            new BigDecimal("1.0"),  // 1 hour
            parts
        );
        
        if (!completed) {
            System.out.println(":( Test 15 FAILED - Could not complete maintenance");
            return;
        }
        
        // Step 3: Create penalty
        System.out.println("\nStep 3: Creating penalty from maintenance...");
        boolean penaltyCreated = penaltyService.createPenaltyFromMaintenance(
            "PEN-INTEG1",
            "RNT-001",
            "MAINT-INTEG1",
            Date.valueOf("2024-10-28")
        );
        
        if (penaltyCreated) {
            System.out.println("\n:) Test 15 PASSED - Complete workflow successful");
        } else {
            System.out.println("\n:( Test 15 FAILED - Could not create penalty");
        }
    }
    
    /**
     * Test 16: Zero Cost Handling
     * Tests behavior when maintenance has no hours or parts
     */
    private static void testZeroCostHandling() {
        System.out.println("\n--- Test 16: Zero Cost Handling ---");
        
        // Try to create penalty for maintenance with no hours (MAINT-019)
        boolean result = penaltyService.createPenaltyFromMaintenance(
            "PEN-ZERO",
            "RNT-001",
            "MAINT-019",  // In progress, no hours logged
            Date.valueOf("2024-10-28")
        );
        
        if (!result) {
            System.out.println(":) Test 16 PASSED - Zero cost penalty rejected as expected");
        } else {
            System.out.println(":( Test 16 FAILED - Should not allow zero cost penalty");
        }
    }
    
    /**
     * Test 17: Missing Data Handling
     * Tests behavior with non-existent IDs
     */
    private static void testMissingDataHandling() {
        System.out.println("\n--- Test 17: Missing Data Handling ---");
        
        // Try to calculate cost for non-existent maintenance
        BigDecimal cost = penaltyService.calculateMaintenanceCost("MAINT-INVALID");
        
        if (cost.compareTo(BigDecimal.ZERO) == 0) {
            System.out.println(":) Test 17 PASSED - Missing data handled gracefully");
        } else {
            System.out.println(":( Test 17 FAILED - Should return zero for invalid ID");
        }
    }
}
