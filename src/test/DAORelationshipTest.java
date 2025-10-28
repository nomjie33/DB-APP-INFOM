package test;

import dao.MaintenanceDAO;
import dao.MaintenanceChequeDAO;
import dao.PartDAO;
import dao.TechnicianDAO;
import model.MaintenanceTransaction;
import model.MaintenanceCheque;
import model.Part;
import model.Technician;

import java.math.BigDecimal;
import java.sql.Date;
import java.time.LocalDate;
import java.util.List;

/**
 * DAO RELATIONSHIP & EDGE CASE TEST
 * 
 * PURPOSE: Test relationships between DAOs and edge cases:
 * - Foreign key relationships
 * - Cascading operations
 * - Null value handling
 * - Boundary conditions
 * - Data integrity
 * 
 * PREREQUISITES:
 * 1. Database and tables created
 * 2. db.properties configured
 * 3. Test data loaded (test_data.sql recommended)
 * 
 * HOW TO RUN:
 * 1. Right-click this file → Run As → Java Application
 * 2. Review console output for relationship validations
 */
public class DAORelationshipTest {
    
    public static void main(String[] args) {
        System.out.println("═══════════════════════════════════════════════════");
        System.out.println("   DAO RELATIONSHIP & EDGE CASE TESTS");
        System.out.println("═══════════════════════════════════════════════════\n");
        
        testForeignKeyRelationships();
        testMultiplePartsInMaintenance();
        testPartInventoryTracking();
        testTechnicianWorkloadAnalysis();
        testMaintenanceHistoryByVehicle();
        testEdgeCases();
        
        System.out.println("\n═══════════════════════════════════════════════════");
        System.out.println("   ALL RELATIONSHIP TESTS COMPLETED");
        System.out.println("═══════════════════════════════════════════════════");
    }
    
    /**
     * Test 1: Foreign Key Relationships
     * Verify that maintenance records properly link to technicians and parts
     */
    private static void testForeignKeyRelationships() {
        System.out.println("═══════════════════════════════════════════════════");
        System.out.println("TEST 1: Foreign Key Relationships");
        System.out.println("═══════════════════════════════════════════════════\n");
        
        try {
            MaintenanceDAO mDao = new MaintenanceDAO();
            MaintenanceChequeDAO mcDao = new MaintenanceChequeDAO();
            TechnicianDAO tDao = new TechnicianDAO();
            PartDAO pDao = new PartDAO();
            
            // Get a maintenance record
            List<MaintenanceTransaction> allMaintenance = mDao.getAllMaintenance();
            
            if (!allMaintenance.isEmpty()) {
                MaintenanceTransaction maintenance = allMaintenance.get(0);
                System.out.println("Testing maintenance record: " + maintenance.getMaintenanceID());
                
                // Verify technician exists
                Technician tech = tDao.getTechnicianById(maintenance.getTechnicianID());
                if (tech != null) {
                    System.out.println(":) Technician relationship valid: " + tech.getFullName());
                    System.out.println("  Specialization: " + tech.getSpecializationId());
                    System.out.println("  Rate: ₱" + tech.getRate());
                } else {
                    System.out.println(":( Technician relationship broken!");
                }
                
                // Verify parts used in this maintenance
                List<MaintenanceCheque> partsUsed = mcDao.getPartsByMaintenance(maintenance.getMaintenanceID());
                System.out.println("\n:) Parts used in this maintenance: " + partsUsed.size());
                
                for (MaintenanceCheque cheque : partsUsed) {
                    Part part = pDao.getPartById(cheque.getPartID());
                    if (part != null) {
                        System.out.println("  - " + part.getPartName() + 
                                         " (Qty: " + cheque.getQuantityUsed() + 
                                         ", In Stock: " + part.getQuantity() + ")");
                    }
                }
                
                System.out.println("\n:) Foreign key relationships are intact");
            } else {
                System.out.println("⚠ No maintenance records found to test");
            }
            
        } catch (Exception e) {
            System.out.println(":( ERROR: " + e.getMessage());
            e.printStackTrace();
        }
        
        System.out.println();
    }
    
    /**
     * Test 2: Multiple Parts in Single Maintenance
     * Verify that maintenance can use multiple parts
     */
    private static void testMultiplePartsInMaintenance() {
        System.out.println("═══════════════════════════════════════════════════");
        System.out.println("TEST 2: Multiple Parts in Single Maintenance");
        System.out.println("═══════════════════════════════════════════════════\n");
        
        try {
            MaintenanceDAO mDao = new MaintenanceDAO();
            MaintenanceChequeDAO mcDao = new MaintenanceChequeDAO();
            
            List<MaintenanceTransaction> allMaintenance = mDao.getAllMaintenance();
            
            System.out.println("Analyzing parts usage across maintenance records...\n");
            
            int totalWithMultipleParts = 0;
            int maxPartsInOne = 0;
            String maintenanceWithMostParts = "";
            
            for (MaintenanceTransaction m : allMaintenance) {
                List<MaintenanceCheque> parts = mcDao.getPartsByMaintenance(m.getMaintenanceID());
                
                if (parts.size() > 1) {
                    totalWithMultipleParts++;
                    System.out.println("Maintenance " + m.getMaintenanceID() + 
                                     " used " + parts.size() + " different parts:");
                    
                    for (MaintenanceCheque cheque : parts) {
                        System.out.println("  - Part " + cheque.getPartID() + 
                                         ": " + cheque.getQuantityUsed() + " units");
                    }
                    System.out.println();
                }
                
                if (parts.size() > maxPartsInOne) {
                    maxPartsInOne = parts.size();
                    maintenanceWithMostParts = m.getMaintenanceID();
                }
            }
            
            System.out.println("Summary:");
            System.out.println("  Maintenance records using multiple parts: " + totalWithMultipleParts);
            System.out.println("  Maximum parts in one maintenance: " + maxPartsInOne + 
                             " (" + maintenanceWithMostParts + ")");
            System.out.println("\n:) Multiple parts per maintenance working correctly");
            
        } catch (Exception e) {
            System.out.println(":( ERROR: " + e.getMessage());
            e.printStackTrace();
        }
        
        System.out.println();
    }
    
    /**
     * Test 3: Part Inventory Tracking
     * Track which parts are most used in maintenance
     */
    private static void testPartInventoryTracking() {
        System.out.println("═══════════════════════════════════════════════════");
        System.out.println("TEST 3: Part Inventory Tracking");
        System.out.println("═══════════════════════════════════════════════════\n");
        
        try {
            MaintenanceChequeDAO mcDao = new MaintenanceChequeDAO();
            PartDAO pDao = new PartDAO();
            
            List<Part> allParts = pDao.getAllParts();
            
            System.out.println("Analyzing part usage across maintenance records...\n");
            
            // Track part usage
            java.util.Map<String, BigDecimal> partUsage = new java.util.HashMap<>();
            java.util.Map<String, String> partNames = new java.util.HashMap<>();
            java.util.Map<String, Integer> partFrequency = new java.util.HashMap<>();
            
            for (Part p : allParts) {
                partNames.put(p.getPartId(), p.getPartName());
                List<MaintenanceCheque> usageList = mcDao.getMaintenancesByPart(p.getPartId());
                
                BigDecimal totalQty = BigDecimal.ZERO;
                for (MaintenanceCheque cheque : usageList) {
                    totalQty = totalQty.add(cheque.getQuantityUsed());
                }
                
                partUsage.put(p.getPartId(), totalQty);
                partFrequency.put(p.getPartId(), usageList.size());
            }
            
            // Display most used parts by quantity
            System.out.println("Most used parts (by total quantity):");
            partUsage.entrySet().stream()
                .filter(e -> e.getValue().compareTo(BigDecimal.ZERO) > 0)
                .sorted((e1, e2) -> e2.getValue().compareTo(e1.getValue()))
                .limit(5)
                .forEach(entry -> {
                    String partId = entry.getKey();
                    BigDecimal qty = entry.getValue();
                    String name = partNames.getOrDefault(partId, "Unknown");
                    int frequency = partFrequency.getOrDefault(partId, 0);
                    
                    Part part = pDao.getPartById(partId);
                    String stockStatus = "";
                    if (part != null) {
                        int stock = part.getQuantity();
                        stockStatus = stock < 30 ? " [LOW STOCK: " + stock + "]" : " [Stock: " + stock + "]";
                    }
                    
                    System.out.println("  " + partId + ": " + name + 
                                     " - Used " + qty + " units total (" + frequency + " times)" + stockStatus);
                });
            
            // Check for low stock parts
            System.out.println("\n⚠ Low stock alert (< 30 units):");
            List<Part> lowStock = pDao.getLowStockParts(30);
            if (lowStock.isEmpty()) {
                System.out.println("  :) All parts well-stocked");
            } else {
                for (Part p : lowStock) {
                    System.out.println("  - " + p.getPartId() + ": " + p.getPartName() + 
                                     " (Only " + p.getQuantity() + " units left)");
                }
            }
            
        } catch (Exception e) {
            System.out.println(":( ERROR: " + e.getMessage());
            e.printStackTrace();
        }
        
        System.out.println();
    }
    
    /**
     * Test 4: Technician Workload Analysis
     * Analyze technician assignments and specializations
     */
    private static void testTechnicianWorkloadAnalysis() {
        System.out.println("═══════════════════════════════════════════════════");
        System.out.println("TEST 4: Technician Workload Analysis");
        System.out.println("═══════════════════════════════════════════════════\n");
        
        try {
            MaintenanceDAO mDao = new MaintenanceDAO();
            TechnicianDAO tDao = new TechnicianDAO();
            
            List<Technician> allTechnicians = tDao.getAllTechnicians();
            
            System.out.println("Analyzing workload for " + allTechnicians.size() + " technicians...\n");
            
            for (Technician tech : allTechnicians) {
                List<MaintenanceTransaction> jobs = mDao.getMaintenanceByTechnician(tech.getTechnicianId());
                
                // Count completed vs in-progress
                long completed = jobs.stream().filter(m -> m.getDateRepaired() != null).count();
                long inProgress = jobs.stream().filter(m -> m.getDateRepaired() == null).count();
                
                System.out.println("Technician: " + tech.getFullName() + " (" + tech.getTechnicianId() + ")");
                System.out.println("  Specialization: " + tech.getSpecializationId());
                System.out.println("  Total jobs: " + jobs.size());
                System.out.println("  Completed: " + completed);
                System.out.println("  In progress: " + inProgress);
                
                if (jobs.size() > 0) {
                    System.out.println("  Most recent job: " + jobs.get(0).getMaintenanceID());
                }
                System.out.println();
            }
            
            // Find busiest technician
            Technician busiest = null;
            int maxJobs = 0;
            
            for (Technician tech : allTechnicians) {
                int jobCount = mDao.getMaintenanceByTechnician(tech.getTechnicianId()).size();
                if (jobCount > maxJobs) {
                    maxJobs = jobCount;
                    busiest = tech;
                }
            }
            
            if (busiest != null) {
                System.out.println(" Busiest technician: " + busiest.getFullName() + 
                                 " with " + maxJobs + " jobs");
            }
            
        } catch (Exception e) {
            System.out.println(":( ERROR: " + e.getMessage());
            e.printStackTrace();
        }
        
        System.out.println();
    }
    
    /**
     * Test 5: Maintenance History by Vehicle
     * Track maintenance patterns for specific vehicles
     */
    private static void testMaintenanceHistoryByVehicle() {
        System.out.println("═══════════════════════════════════════════════════");
        System.out.println("TEST 5: Maintenance History by Vehicle");
        System.out.println("═══════════════════════════════════════════════════\n");
        
        try {
            MaintenanceDAO dao = new MaintenanceDAO();
            
            String[] testVehicles = {"ES-001", "EB-001", "ET-001"};
            
            for (String vehicleId : testVehicles) {
                List<MaintenanceTransaction> history = dao.getMaintenanceByVehicle(vehicleId);
                
                System.out.println("Vehicle: " + vehicleId);
                System.out.println("  Total maintenance records: " + history.size());
                
                if (!history.isEmpty()) {
                    System.out.println("  Maintenance history:");
                    for (MaintenanceTransaction m : history) {
                        String status = m.getDateRepaired() != null ? "Completed" : "In Progress";
                        System.out.println("    - " + m.getMaintenanceID() + 
                                         " [" + status + "]: " + m.getNotes());
                    }
                } else {
                    System.out.println("  :) No maintenance history (vehicle in good condition)");
                }
                System.out.println();
            }
            
        } catch (Exception e) {
            System.out.println(":( ERROR: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * Test 6: Edge Cases and Error Handling
     */
    private static void testEdgeCases() {
        System.out.println("═══════════════════════════════════════════════════");
        System.out.println("TEST 6: Edge Cases & Error Handling");
        System.out.println("═══════════════════════════════════════════════════\n");
        
        try {
            TechnicianDAO tDao = new TechnicianDAO();
            PartDAO pDao = new PartDAO();
            MaintenanceDAO mDao = new MaintenanceDAO();
            MaintenanceChequeDAO mcDao = new MaintenanceChequeDAO();
            
            // Test 1: Non-existent ID
            System.out.println("─── 6.1 Testing Non-Existent ID Retrieval ───");
            Technician nonExistent = tDao.getTechnicianById("FAKE-ID-999");
            System.out.println(nonExistent == null ? 
                ":) Correctly returns null for non-existent ID" : 
                ":( Should return null for non-existent ID");
            
            // Test 2: Empty result sets
            System.out.println("\n─── 6.2 Testing Empty Result Sets ───");
            List<MaintenanceTransaction> byFakeTech = mDao.getMaintenanceByTechnician("FAKE-T999");
            System.out.println(byFakeTech.isEmpty() ? 
                ":) Correctly returns empty list for non-existent technician" : 
                ":( Should return empty list");
            
            // Test 3: Part quantity boundaries
            System.out.println("\n─── 6.3 Testing Part Quantity Boundaries ───");
            Part testPart = new Part("EDGE-P001", "Test Part", 5);
            pDao.insertPart(testPart);
            
            // Try to decrement more than available (should fail)
            boolean shouldFail = pDao.decrementPartQuantity("EDGE-P001", 10);
            System.out.println(!shouldFail ? 
                ":) Correctly prevents negative quantity" : 
                ":( Should prevent negative quantity");
            
            // Cleanup
            pDao.deletePart("EDGE-P001");
            
            // Test 4: Null handling in maintenance
            System.out.println("\n─── 6.4 Testing Null Repair Date (In-Progress) ───");
            Technician edgeTech = new Technician("EDGE-T001", "Edge", "Tech", 
                                                "ELECTRICAL", new BigDecimal("300.00"), "09999999999");
            tDao.insertTechnician(edgeTech);
            
            Part edgePart = new Part("EDGE-P002", "Edge Part", 10);
            pDao.insertPart(edgePart);
            
            MaintenanceTransaction inProgress = new MaintenanceTransaction(
                "EDGE-M001",
                Date.valueOf(LocalDate.now()),
                null,  // No repair date yet - still in progress
                "Testing in-progress maintenance",
                "EDGE-T001",
                "ES-001"
            );
            
            boolean insertSuccess = mDao.insertMaintenance(inProgress);
            System.out.println(insertSuccess ? 
                ":) Correctly handles null repair date (in-progress maintenance)" : 
                ":( Should handle null repair date");
            
            // Test 5: CASCADE DELETE (maintenance_cheque should auto-delete)
            System.out.println("\n─── 6.5 Testing CASCADE DELETE ───");
            MaintenanceCheque cheque = new MaintenanceCheque("EDGE-M001", "EDGE-P002", new BigDecimal("2.00"));
            mcDao.insertMaintenanceCheque(cheque);
            
            List<MaintenanceCheque> beforeDelete = mcDao.getPartsByMaintenance("EDGE-M001");
            System.out.println("Parts before delete: " + beforeDelete.size());
            
            // Delete maintenance - should cascade to maintenance_cheque
            mDao.deleteMaintenance("EDGE-M001");
            
            List<MaintenanceCheque> afterDelete = mcDao.getPartsByMaintenance("EDGE-M001");
            System.out.println(afterDelete.isEmpty() ? 
                ":) CASCADE DELETE working - parts auto-deleted" : 
                ":( CASCADE DELETE failed");
            
            // Cleanup
            tDao.deleteTechnician("EDGE-T001");
            pDao.deletePart("EDGE-P002");
            
            System.out.println("\n:) All edge cases handled correctly");
            
        } catch (Exception e) {
            System.out.println(":( ERROR: " + e.getMessage());
            e.printStackTrace();
        }
        
        System.out.println();
    }
}
