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
import java.sql.Timestamp;
import java.util.List;

/**
 * DAO CRUD TEST CLASS
 * 
 * PURPOSE: Comprehensive testing of all CRUD operations for:
 * - TechnicianDAO (Create, Read, Update, Delete)
 * - PartDAO (Create, Read, Update, Delete)
 * - MaintenanceDAO (Create, Read, Update, Delete)
 * 
 * PREREQUISITES:
 * 1. MySQL database 'vehicle_rental_db' must exist
 * 2. All required tables must be created (run database_schema.sql)
 * 3. db.properties file must be configured with YOUR MySQL credentials
 * 4. At least one vehicle must exist (e.g., ES-001)
 * 
 * HOW TO RUN:
 * 1. Right-click this file → Run As → Java Application
 * 2. Check console output for success/failure messages
 * 3. Verify in MySQL Workbench that operations worked correctly
 * 
 * WHAT TO EXPECT:
 * - CREATE operations (Insert)
 * - READ operations (Select by ID, Select All)
 * - UPDATE operations (Modify existing records)
 * - DELETE operations (Remove records)
 */
public class DAOCRUDTest {
    
    public static void main(String[] args) {
        System.out.println("═══════════════════════════════════════════════════");
        System.out.println("   DAO CRUD TEST - Comprehensive Testing");
        System.out.println("═══════════════════════════════════════════════════\n");
        
        // Test each DAO with all CRUD operations
        testTechnicianDAO();
        testPartDAO();
        testMaintenanceDAO();
        testMaintenanceChequeDAO();
        
        System.out.println("\n═══════════════════════════════════════════════════");
        System.out.println("   ALL TESTS COMPLETED");
        System.out.println("═══════════════════════════════════════════════════");
    }
    
    /**
     * Test 1: Complete CRUD testing for TechnicianDAO
     */
    private static void testTechnicianDAO() {
        System.out.println("═══════════════════════════════════════════════════");
        System.out.println("TEST 1: TechnicianDAO - CRUD Operations");
        System.out.println("═══════════════════════════════════════════════════\n");
        
        TechnicianDAO dao = new TechnicianDAO();
        String testId = "CRUD-T001";
        
        try {
            // === CREATE (Insert) ===
            System.out.println("─── 1.1 CREATE: Inserting Technician ───");
            Technician technician = new Technician(
                testId,
                "Test",
                "Technician",
                "ELECTRICAL",
                new BigDecimal("400.00"),
                "09999999999"
            );
            
            boolean insertSuccess = dao.insertTechnician(technician);
            System.out.println(insertSuccess ? 
                ":) INSERT successful: " + testId : 
                ":( INSERT failed");
            
            // === READ (Select by ID) ===
            System.out.println("\n─── 1.2 READ: Retrieving Technician by ID ───");
            Technician retrieved = dao.getTechnicianById(testId);
            
            if (retrieved != null) {
                System.out.println(":) SELECT by ID successful:");
                System.out.println("  Name: " + retrieved.getFullName());
                System.out.println("  Specialization: " + retrieved.getSpecializationId());
                System.out.println("  Rate: Php" + retrieved.getRate());
                System.out.println("  Contact: " + retrieved.getContactNumber());
            } else {
                System.out.println(":( SELECT by ID failed - Record not found");
            }
            
            // === READ (Select All) ===
            System.out.println("\n─── 1.3 READ: Retrieving All Technicians ───");
            List<Technician> allTechnicians = dao.getAllTechnicians();
            System.out.println(":) SELECT ALL successful: Found " + allTechnicians.size() + " technicians");
            System.out.println("  First 3 technicians:");
            for (int i = 0; i < Math.min(3, allTechnicians.size()); i++) {
                Technician t = allTechnicians.get(i);
                System.out.println("  - " + t.getTechnicianId() + ": " + t.getFullName() + 
                                 " (" + t.getSpecializationId() + ")");
            }
            
            // === UPDATE ===
            System.out.println("\n─── 1.4 UPDATE: Modifying Technician ───");
            if (retrieved != null) {
                retrieved.setRate(new BigDecimal("450.00"));
                retrieved.setSpecializationId("MECHANICAL");
                retrieved.setContactNumber("09888888888");
                
                boolean updateSuccess = dao.updateTechnician(retrieved);
                System.out.println(updateSuccess ? 
                    ":) UPDATE successful" : 
                    ":( UPDATE failed");
                
                // Verify update
                Technician updated = dao.getTechnicianById(testId);
                if (updated != null) {
                    System.out.println("  New Rate: Php" + updated.getRate());
                    System.out.println("  New Specialization: " + updated.getSpecializationId());
                    System.out.println("  New Contact: " + updated.getContactNumber());
                }
            }
            
            // === DELETE ===
            System.out.println("\n─── 1.5 DELETE: Removing Technician ───");
            boolean deleteSuccess = dao.deleteTechnician(testId);
            System.out.println(deleteSuccess ? 
                ":) DELETE successful" : 
                ":( DELETE failed");
            
            // Verify deletion
            Technician deleted = dao.getTechnicianById(testId);
            System.out.println(deleted == null ? 
                ":) Verified: Record no longer exists" : 
                ":( Warning: Record still exists");
            
            System.out.println("\n> TechnicianDAO Tests Complete\n");
            
        } catch (Exception e) {
            System.out.println(":( ERROR in TechnicianDAO test: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * Test 2: Complete CRUD testing for PartDAO
     */
    private static void testPartDAO() {
        System.out.println("═══════════════════════════════════════════════════");
        System.out.println("TEST 2: PartDAO - CRUD Operations");
        System.out.println("═══════════════════════════════════════════════════\n");
        
        PartDAO dao = new PartDAO();
        String testId = "CRUD-P001";
        
        try {
            // === CREATE (Insert) ===
            System.out.println("─── 2.1 CREATE: Inserting Part ───");
            Part part = new Part(testId, "Test Motor", 100);
            
            boolean insertSuccess = dao.insertPart(part);
            System.out.println(insertSuccess ? 
                ":) INSERT successful: " + testId : 
                ":( INSERT failed");
            
            // === READ (Select by ID) ===
            System.out.println("\n─── 2.2 READ: Retrieving Part by ID ───");
            Part retrieved = dao.getPartById(testId);
            
            if (retrieved != null) {
                System.out.println(":) SELECT by ID successful:");
                System.out.println("  Part Name: " + retrieved.getPartName());
                System.out.println("  Quantity: " + retrieved.getQuantity() + " units");
            } else {
                System.out.println(":( SELECT by ID failed - Record not found");
            }
            
            // === READ (Select All) ===
            System.out.println("\n─── 2.3 READ: Retrieving All Parts ───");
            List<Part> allParts = dao.getAllParts();
            System.out.println(":) SELECT ALL successful: Found " + allParts.size() + " parts");
            System.out.println("  First 3 parts:");
            for (int i = 0; i < Math.min(3, allParts.size()); i++) {
                Part p = allParts.get(i);
                System.out.println("  - " + p.getPartId() + ": " + p.getPartName() + 
                                 " (Qty: " + p.getQuantity() + ")");
            }
            
            // === UPDATE ===
            System.out.println("\n─── 2.4 UPDATE: Modifying Part ───");
            if (retrieved != null) {
                retrieved.setPartName("Updated Test Motor");
                retrieved.setQuantity(150);
                
                boolean updateSuccess = dao.updatePart(retrieved);
                System.out.println(updateSuccess ? 
                    ":) UPDATE successful" : 
                    ":( UPDATE failed");
                
                // Verify update
                Part updated = dao.getPartById(testId);
                if (updated != null) {
                    System.out.println("  New Name: " + updated.getPartName());
                    System.out.println("  New Quantity: " + updated.getQuantity());
                }
            }
            
            // === SPECIAL: Quantity Operations ===
            System.out.println("\n─── 2.5 SPECIAL: Quantity Management ───");
            
            // Decrement quantity (simulating part usage)
            boolean decrementSuccess = dao.decrementPartQuantity(testId, 10);
            System.out.println(decrementSuccess ? 
                ":) DECREMENT successful: -10 units" : 
                ":( DECREMENT failed");
            
            Part afterDecrement = dao.getPartById(testId);
            if (afterDecrement != null) {
                System.out.println("  Quantity after decrement: " + afterDecrement.getQuantity());
            }
            
            // Increment quantity (simulating restocking)
            boolean incrementSuccess = dao.incrementPartQuantity(testId, 25);
            System.out.println(incrementSuccess ? 
                ":) INCREMENT successful: +25 units" : 
                ":( INCREMENT failed");
            
            Part afterIncrement = dao.getPartById(testId);
            if (afterIncrement != null) {
                System.out.println("  Quantity after increment: " + afterIncrement.getQuantity());
            }
            
            // === DELETE ===
            System.out.println("\n─── 2.6 DELETE: Removing Part ───");
            boolean deleteSuccess = dao.deletePart(testId);
            System.out.println(deleteSuccess ? 
                ":) DELETE successful" : 
                ":( DELETE failed");
            
            // Verify deletion
            Part deleted = dao.getPartById(testId);
            System.out.println(deleted == null ? 
                ":) Verified: Record no longer exists" : 
                ":( Warning: Record still exists");
            
            System.out.println("\n> PartDAO Tests Complete\n");
            
        } catch (Exception e) {
            System.out.println(":( ERROR in PartDAO test: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * Test 3: Complete CRUD testing for MaintenanceDAO
     */
    private static void testMaintenanceDAO() {
        System.out.println("═══════════════════════════════════════════════════");
        System.out.println("TEST 3: MaintenanceDAO - CRUD Operations");
        System.out.println("═══════════════════════════════════════════════════\n");
        
        MaintenanceDAO mDao = new MaintenanceDAO();
        TechnicianDAO tDao = new TechnicianDAO();
        
        String testMaintenanceId = "CRUD-M001";
        String testTechnicianId = "CRUD-T002";
        
        try {
            // Setup: Create supporting records
            System.out.println("─── 3.0 SETUP: Creating supporting records ───");
            
            Technician tech = new Technician(testTechnicianId, "Support", "Tech", 
                                            "BATTERY", new BigDecimal("350.00"), "09111111111");
            tDao.insertTechnician(tech);
            System.out.println(":) Support technician created: " + testTechnicianId);
            
            // === CREATE (Insert) ===
            System.out.println("\n─── 3.1 CREATE: Inserting Maintenance Record ───");
            MaintenanceTransaction maintenance = new MaintenanceTransaction(
                testMaintenanceId,
                new Timestamp(System.currentTimeMillis() - 172800000L),  // 2 days ago
                new Timestamp(System.currentTimeMillis() - 86400000L),   // 1 day ago
                "Test maintenance - battery replacement",
                testTechnicianId,
                "ES-001"  // Must exist in vehicles table
            );
            
            boolean insertSuccess = mDao.insertMaintenance(maintenance);
            System.out.println(insertSuccess ? 
                ":) INSERT successful: " + testMaintenanceId : 
                ":( INSERT failed");
            
            // === READ (Select by ID) ===
            System.out.println("\n─── 3.2 READ: Retrieving Maintenance by ID ───");
            MaintenanceTransaction retrieved = mDao.getMaintenanceById(testMaintenanceId);
            
            if (retrieved != null) {
                System.out.println(":) SELECT by ID successful:");
                System.out.println("  Vehicle: " + retrieved.getPlateID());
                System.out.println("  Technician: " + retrieved.getTechnicianID());
                System.out.println("  Start DateTime: " + retrieved.getStartDateTime());
                System.out.println("  End DateTime: " + retrieved.getEndDateTime());
                System.out.println("  Notes: " + retrieved.getNotes());
            } else {
                System.out.println(":( SELECT by ID failed - Record not found");
            }
            
            // === READ (Select All) ===
            System.out.println("\n─── 3.3 READ: Retrieving All Maintenance Records ───");
            List<MaintenanceTransaction> allMaintenance = mDao.getAllMaintenance();
            System.out.println(":) SELECT ALL successful: Found " + allMaintenance.size() + " maintenance records");
            System.out.println("  First 3 records:");
            for (int i = 0; i < Math.min(3, allMaintenance.size()); i++) {
                MaintenanceTransaction m = allMaintenance.get(i);
                System.out.println("  - " + m.getMaintenanceID() + ": Vehicle " + 
                                 m.getPlateID() + " by " + m.getTechnicianID());
            }
            
            // === READ (Select by Vehicle) ===
            System.out.println("\n─── 3.4 READ: Retrieving Maintenance by Vehicle ───");
            List<MaintenanceTransaction> byVehicle = mDao.getMaintenanceByVehicle("ES-001");
            System.out.println(":) SELECT by Vehicle successful: Found " + byVehicle.size() + 
                             " records for ES-001");
            
            // === READ (Select by Technician) ===
            System.out.println("\n─── 3.5 READ: Retrieving Maintenance by Technician ───");
            List<MaintenanceTransaction> byTechnician = mDao.getMaintenanceByTechnician(testTechnicianId);
            System.out.println(":) SELECT by Technician successful: Found " + byTechnician.size() + 
                             " records for " + testTechnicianId);
            
            // === UPDATE ===
            System.out.println("\n─── 3.6 UPDATE: Modifying Maintenance Record ───");
            if (retrieved != null) {
                retrieved.setEndDateTime(new Timestamp(System.currentTimeMillis()));
                retrieved.setNotes("Updated test maintenance - completed successfully");
                
                boolean updateSuccess = mDao.updateMaintenance(retrieved);
                System.out.println(updateSuccess ? 
                    ":) UPDATE successful" : 
                    ":( UPDATE failed");
                
                // Verify update
                MaintenanceTransaction updated = mDao.getMaintenanceById(testMaintenanceId);
                if (updated != null) {
                    System.out.println("  New Notes: " + updated.getNotes());
                    System.out.println("  New End DateTime: " + updated.getEndDateTime());
                }
            }
            
            // === DELETE ===
            System.out.println("\n─── 3.7 DELETE: Removing Maintenance Record ───");
            boolean deleteSuccess = mDao.deleteMaintenance(testMaintenanceId);
            System.out.println(deleteSuccess ? 
                ":) DELETE successful (also cascades to maintenance_cheque)" : 
                ":( DELETE failed");
            
            // Verify deletion
            MaintenanceTransaction deleted = mDao.getMaintenanceById(testMaintenanceId);
            System.out.println(deleted == null ? 
                ":) Verified: Record no longer exists" : 
                ":( Warning: Record still exists");
            
            // Cleanup: Remove supporting records
            System.out.println("\n─── 3.8 CLEANUP: Removing supporting records ───");
            tDao.deleteTechnician(testTechnicianId);
            System.out.println(":) Cleanup complete");
            
            System.out.println("\n> MaintenanceDAO Tests Complete\n");
            
        } catch (Exception e) {
            System.out.println(":( ERROR in MaintenanceDAO test: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * Test 4: Complete CRUD testing for MaintenanceChequeDAO
     */
    private static void testMaintenanceChequeDAO() {
        System.out.println("═══════════════════════════════════════════════════");
        System.out.println("TEST 4: MaintenanceChequeDAO - CRUD Operations");
        System.out.println("═══════════════════════════════════════════════════\n");
        
        MaintenanceDAO mDao = new MaintenanceDAO();
        MaintenanceChequeDAO mcDao = new MaintenanceChequeDAO();
        TechnicianDAO tDao = new TechnicianDAO();
        PartDAO pDao = new PartDAO();
        
        String testMaintenanceId = "CRUD-M002";
        String testTechnicianId = "CRUD-T003";
        String testPart1Id = "CRUD-P003";
        String testPart2Id = "CRUD-P004";
        
        try {
            // Setup: Create supporting records
            System.out.println("─── 4.0 SETUP: Creating supporting records ───");
            
            Technician tech = new Technician(testTechnicianId, "Test", "Mechanic", 
                                            "MECHANICAL", new BigDecimal("320.00"), "09222222222");
            tDao.insertTechnician(tech);
            
            Part part1 = new Part(testPart1Id, "Test Brake Pads", 100);
            pDao.insertPart(part1);
            
            Part part2 = new Part(testPart2Id, "Test Brake Cable", 80);
            pDao.insertPart(part2);
            
            MaintenanceTransaction maintenance = new MaintenanceTransaction(
                testMaintenanceId,
                new Timestamp(System.currentTimeMillis() - 86400000L),  // 1 day ago
                new Timestamp(System.currentTimeMillis()),               // now
                "Test brake system maintenance",
                testTechnicianId,
                "ES-002"
            );
            mDao.insertMaintenance(maintenance);
            
            System.out.println(":) Setup complete: Maintenance and parts created");
            
            // === CREATE (Insert Multiple Parts) ===
            System.out.println("\n─── 4.1 CREATE: Adding Parts to Maintenance ───");
            
            MaintenanceCheque cheque1 = new MaintenanceCheque(
                testMaintenanceId,
                testPart1Id,
                new BigDecimal("2.00")  // 2 brake pads
            );
            
            MaintenanceCheque cheque2 = new MaintenanceCheque(
                testMaintenanceId,
                testPart2Id,
                new BigDecimal("1.00")  // 1 brake cable
            );
            
            boolean insert1 = mcDao.insertMaintenanceCheque(cheque1);
            boolean insert2 = mcDao.insertMaintenanceCheque(cheque2);
            
            System.out.println(insert1 && insert2 ? 
                ":) INSERT successful: 2 parts added to maintenance" : 
                ":( INSERT failed");
            
            // === READ (Select by Maintenance) ===
            System.out.println("\n─── 4.2 READ: Retrieving Parts by Maintenance ───");
            List<MaintenanceCheque> partsUsed = mcDao.getPartsByMaintenance(testMaintenanceId);
            
            System.out.println(":) SELECT by Maintenance successful: Found " + partsUsed.size() + " parts");
            for (MaintenanceCheque mc : partsUsed) {
                System.out.println("  - Part " + mc.getPartID() + ": " + 
                                 mc.getQuantityUsed() + " units");
            }
            
            // === READ (Select by Part) ===
            System.out.println("\n─── 4.3 READ: Retrieving Maintenance by Part ───");
            List<MaintenanceCheque> maintenanceForPart = mcDao.getMaintenancesByPart(testPart1Id);
            System.out.println(":) SELECT by Part successful: Found " + maintenanceForPart.size() + 
                             " maintenance records using part " + testPart1Id);
            
            // === READ (Select Specific Record) ===
            System.out.println("\n─── 4.4 READ: Retrieving Specific Part Usage ───");
            MaintenanceCheque specific = mcDao.getMaintenanceChequeById(testMaintenanceId, testPart1Id);
            
            if (specific != null) {
                System.out.println(":) SELECT by ID successful:");
                System.out.println("  Maintenance: " + specific.getMaintenanceID());
                System.out.println("  Part: " + specific.getPartID());
                System.out.println("  Quantity Used: " + specific.getQuantityUsed());
            }
            
            // === UPDATE ===
            System.out.println("\n─── 4.5 UPDATE: Modifying Quantity Used ───");
            if (specific != null) {
                specific.setQuantityUsed(new BigDecimal("4.00"));  // Changed from 2 to 4
                
                boolean updateSuccess = mcDao.updateMaintenanceCheque(specific);
                System.out.println(updateSuccess ? 
                    ":) UPDATE successful" : 
                    ":( UPDATE failed");
                
                // Verify update
                MaintenanceCheque updated = mcDao.getMaintenanceChequeById(testMaintenanceId, testPart1Id);
                if (updated != null) {
                    System.out.println("  New Quantity: " + updated.getQuantityUsed());
                }
            }
            
            // === DELETE (Single Part) ===
            System.out.println("\n─── 4.6 DELETE: Removing Single Part from Maintenance ───");
            boolean deleteSingle = mcDao.deleteMaintenanceCheque(testMaintenanceId, testPart2Id);
            System.out.println(deleteSingle ? 
                ":) DELETE single part successful" : 
                ":( DELETE failed");
            
            List<MaintenanceCheque> afterDelete = mcDao.getPartsByMaintenance(testMaintenanceId);
            System.out.println("  Parts remaining: " + afterDelete.size());
            
            // === DELETE (All Parts for Maintenance) ===
            System.out.println("\n─── 4.7 DELETE: Removing All Parts from Maintenance ───");
            boolean deleteAll = mcDao.deleteAllByMaintenance(testMaintenanceId);
            System.out.println(deleteAll ? 
                ":) DELETE all parts successful" : 
                ":( DELETE failed");
            
            List<MaintenanceCheque> afterDeleteAll = mcDao.getPartsByMaintenance(testMaintenanceId);
            System.out.println(afterDeleteAll.isEmpty() ? 
                ":) Verified: No parts remain" : 
                ":( Warning: Parts still exist");
            
            // Cleanup
            System.out.println("\n─── 4.8 CLEANUP: Removing supporting records ───");
            mDao.deleteMaintenance(testMaintenanceId);
            tDao.deleteTechnician(testTechnicianId);
            pDao.deletePart(testPart1Id);
            pDao.deletePart(testPart2Id);
            System.out.println(":) Cleanup complete");
            
            System.out.println("\n> MaintenanceChequeDAO Tests Complete\n");
            
        } catch (Exception e) {
            System.out.println(":( ERROR in MaintenanceChequeDAO test: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
