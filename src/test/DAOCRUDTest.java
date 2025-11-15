package test;

import dao.AddressDAO;
import dao.BarangayDAO;
import dao.CityDAO;
import dao.CustomerDAO;
import dao.MaintenanceDAO;
import dao.MaintenanceChequeDAO;
import dao.PartDAO;
import dao.PaymentDAO;
import dao.PenaltyDAO;
import dao.TechnicianDAO;
import model.Address;
import model.Barangay;
import model.City;
import model.Customer;
import model.MaintenanceTransaction;
import model.MaintenanceCheque;
import model.Part;
import model.PaymentTransaction;
import model.PenaltyTransaction;
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
        testCityDAO();
        testBarangayDAO();
        testAddressDAO();
        testCustomerDAO();
        testTechnicianDAO();
        testPartDAO();
        testMaintenanceDAO();
        testMaintenanceChequeDAO();
        testPaymentDAO();
        testPenaltyDAO();
        
        System.out.println("\n═══════════════════════════════════════════════════");
        System.out.println("   ALL TESTS COMPLETED");
        System.out.println("═══════════════════════════════════════════════════");
    }
    
    /**
     * Test 1: Complete CRUD testing for CityDAO
     */
    private static void testCityDAO() {
        System.out.println("═══════════════════════════════════════════════════");
        System.out.println("TEST 1: CityDAO - CRUD Operations");
        System.out.println("═══════════════════════════════════════════════════\n");
        
        CityDAO dao = new CityDAO();
        
        try {
            // === CREATE (Insert) ===
            System.out.println("─── 1.1 CREATE: Inserting City ───");
            City city = new City(null, "Test City");
            
            boolean insertSuccess = dao.insertCity(city);
            System.out.println(insertSuccess ? 
                ":) INSERT successful: City ID = " + city.getCityID() : 
                ":( INSERT failed");
            
            Integer testCityId = city.getCityID();
            
            // === READ (Select by ID) ===
            System.out.println("\n─── 1.2 READ: Retrieving City by ID ───");
            City retrieved = dao.getCityById(testCityId);
            
            if (retrieved != null) {
                System.out.println(":) SELECT by ID successful:");
                System.out.println("  City ID: " + retrieved.getCityID());
                System.out.println("  Name: " + retrieved.getName());
            } else {
                System.out.println(":( SELECT by ID failed - Record not found");
            }
            
            // === READ (Select All) ===
            System.out.println("\n─── 1.3 READ: Retrieving All Cities ───");
            List<City> allCities = dao.getAllCities();
            System.out.println(":) SELECT ALL successful: Found " + allCities.size() + " cities");
            System.out.println("  First 5 cities:");
            for (int i = 0; i < Math.min(5, allCities.size()); i++) {
                City c = allCities.get(i);
                System.out.println("  - " + c.getCityID() + ": " + c.getName());
            }
            
            // === UPDATE ===
            System.out.println("\n─── 1.4 UPDATE: Modifying City ───");
            if (retrieved != null) {
                retrieved.setName("Updated Test City");
                
                boolean updateSuccess = dao.updateCity(retrieved);
                System.out.println(updateSuccess ? 
                    ":) UPDATE successful" : 
                    ":( UPDATE failed");
                
                // Verify update
                City updated = dao.getCityById(testCityId);
                if (updated != null) {
                    System.out.println("  New Name: " + updated.getName());
                }
            }
            
            // === SEARCH ===
            System.out.println("\n─── 1.5 SEARCH: Finding Cities by Name ───");
            List<City> searchResults = dao.searchCitiesByName("Test");
            System.out.println(":) SEARCH successful: Found " + searchResults.size() + " matching cities");
            
            // === DELETE ===
            System.out.println("\n─── 1.6 DELETE: Removing City ───");
            boolean deleteSuccess = dao.deleteCity(testCityId);
            System.out.println(deleteSuccess ? 
                ":) DELETE successful" : 
                ":( DELETE failed");
            
            // Verify deletion
            City deleted = dao.getCityById(testCityId);
            System.out.println(deleted == null ? 
                ":) Verified: Record no longer exists" : 
                ":( Warning: Record still exists");
            
            System.out.println("\n> CityDAO Tests Complete\n");
            
        } catch (Exception e) {
            System.out.println(":( ERROR in CityDAO test: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * Test 2: Complete CRUD testing for BarangayDAO
     */
    private static void testBarangayDAO() {
        System.out.println("═══════════════════════════════════════════════════");
        System.out.println("TEST 2: BarangayDAO - CRUD Operations");
        System.out.println("═══════════════════════════════════════════════════\n");
        
        BarangayDAO bDao = new BarangayDAO();
        CityDAO cDao = new CityDAO();
        
        try {
            // Setup: Create supporting city
            System.out.println("─── 2.0 SETUP: Creating supporting city ───");
            City city = new City(null, "Support City for Barangay");
            cDao.insertCity(city);
            Integer testCityId = city.getCityID();
            System.out.println(":) Support city created: ID = " + testCityId);
            
            // === CREATE (Insert) ===
            System.out.println("\n─── 2.1 CREATE: Inserting Barangay ───");
            Barangay barangay = new Barangay(null, testCityId, "Test Barangay");
            
            boolean insertSuccess = bDao.insertBarangay(barangay);
            System.out.println(insertSuccess ? 
                ":) INSERT successful: Barangay ID = " + barangay.getBarangayID() : 
                ":( INSERT failed");
            
            Integer testBarangayId = barangay.getBarangayID();
            
            // === READ (Select by ID) ===
            System.out.println("\n─── 2.2 READ: Retrieving Barangay by ID ───");
            Barangay retrieved = bDao.getBarangayById(testBarangayId);
            
            if (retrieved != null) {
                System.out.println(":) SELECT by ID successful:");
                System.out.println("  Barangay ID: " + retrieved.getBarangayID());
                System.out.println("  Name: " + retrieved.getName());
                System.out.println("  City ID: " + retrieved.getCityID());
                if (retrieved.getCity() != null) {
                    System.out.println("  City Name: " + retrieved.getCity().getName());
                }
            } else {
                System.out.println(":( SELECT by ID failed - Record not found");
            }
            
            // === READ (Select by City) ===
            System.out.println("\n─── 2.3 READ: Retrieving Barangays by City ───");
            List<Barangay> cityBarangays = bDao.getBarangaysByCity(testCityId);
            System.out.println(":) SELECT by City successful: Found " + cityBarangays.size() + " barangays");
            
            // === UPDATE ===
            System.out.println("\n─── 2.4 UPDATE: Modifying Barangay ───");
            if (retrieved != null) {
                retrieved.setName("Updated Test Barangay");
                
                boolean updateSuccess = bDao.updateBarangay(retrieved);
                System.out.println(updateSuccess ? 
                    ":) UPDATE successful" : 
                    ":( UPDATE failed");
                
                // Verify update
                Barangay updated = bDao.getBarangayById(testBarangayId);
                if (updated != null) {
                    System.out.println("  New Name: " + updated.getName());
                }
            }
            
            // === DELETE ===
            System.out.println("\n─── 2.5 DELETE: Removing Barangay ───");
            boolean deleteSuccess = bDao.deleteBarangay(testBarangayId);
            System.out.println(deleteSuccess ? 
                ":) DELETE successful" : 
                ":( DELETE failed");
            
            // Cleanup: Remove supporting city
            System.out.println("\n─── 2.6 CLEANUP: Removing supporting city ───");
            cDao.deleteCity(testCityId);
            System.out.println(":) Cleanup complete");
            
            System.out.println("\n> BarangayDAO Tests Complete\n");
            
        } catch (Exception e) {
            System.out.println(":( ERROR in BarangayDAO test: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * Test 3: Complete CRUD testing for AddressDAO
     */
    private static void testAddressDAO() {
        System.out.println("═══════════════════════════════════════════════════");
        System.out.println("TEST 3: AddressDAO - CRUD Operations");
        System.out.println("═══════════════════════════════════════════════════\n");
        
        AddressDAO aDao = new AddressDAO();
        BarangayDAO bDao = new BarangayDAO();
        CityDAO cDao = new CityDAO();
        
        try {
            // Setup: Create supporting records
            System.out.println("─── 3.0 SETUP: Creating supporting records ───");
            City city = new City(null, "Support City for Address");
            cDao.insertCity(city);
            Integer testCityId = city.getCityID();
            
            Barangay barangay = new Barangay(null, testCityId, "Support Barangay");
            bDao.insertBarangay(barangay);
            Integer testBarangayId = barangay.getBarangayID();
            
            System.out.println(":) Setup complete: City & Barangay created");
            
            // === CREATE (Insert) ===
            System.out.println("\n─── 3.1 CREATE: Inserting Address ───");
            Address address = new Address(null, testBarangayId, "123 Test Street");
            
            boolean insertSuccess = aDao.insertAddress(address);
            System.out.println(insertSuccess ? 
                ":) INSERT successful: Address ID = " + address.getAddressID() : 
                ":( INSERT failed");
            
            Integer testAddressId = address.getAddressID();
            
            // === READ (Select by ID) ===
            System.out.println("\n─── 3.2 READ: Retrieving Address by ID ───");
            Address retrieved = aDao.getAddressById(testAddressId);
            
            if (retrieved != null) {
                System.out.println(":) SELECT by ID successful:");
                System.out.println("  Address ID: " + retrieved.getAddressID());
                System.out.println("  Street: " + retrieved.getStreet());
                System.out.println("  Barangay ID: " + retrieved.getBarangayID());
                if (retrieved.getBarangay() != null) {
                    System.out.println("  Full Address: " + retrieved.getFullAddress());
                }
            } else {
                System.out.println(":( SELECT by ID failed - Record not found");
            }
            
            // === READ (Select with Full Details) ===
            System.out.println("\n─── 3.3 READ: Retrieving Address with Full Hierarchy ───");
            Address fullDetails = aDao.getAddressWithFullDetails(testAddressId);
            
            if (fullDetails != null) {
                System.out.println(":) SELECT with full details successful:");
                System.out.println("  Full Address: " + fullDetails.getFullAddress());
            }
            
            // === UPDATE ===
            System.out.println("\n─── 3.4 UPDATE: Modifying Address ───");
            if (retrieved != null) {
                retrieved.setStreet("456 Updated Test Street");
                
                boolean updateSuccess = aDao.updateAddress(retrieved);
                System.out.println(updateSuccess ? 
                    ":) UPDATE successful" : 
                    ":( UPDATE failed");
                
                // Verify update
                Address updated = aDao.getAddressById(testAddressId);
                if (updated != null) {
                    System.out.println("  New Street: " + updated.getStreet());
                }
            }
            
            // === DELETE ===
            System.out.println("\n─── 3.5 DELETE: Removing Address ───");
            boolean deleteSuccess = aDao.deleteAddress(testAddressId);
            System.out.println(deleteSuccess ? 
                ":) DELETE successful" : 
                ":( DELETE failed");
            
            // Cleanup: Remove supporting records
            System.out.println("\n─── 3.6 CLEANUP: Removing supporting records ───");
            bDao.deleteBarangay(testBarangayId);
            cDao.deleteCity(testCityId);
            System.out.println(":) Cleanup complete");
            
            System.out.println("\n> AddressDAO Tests Complete\n");
            
        } catch (Exception e) {
            System.out.println(":( ERROR in AddressDAO test: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * Test 4: Complete CRUD testing for CustomerDAO with new address structure
     */
    private static void testCustomerDAO() {
        System.out.println("═══════════════════════════════════════════════════");
        System.out.println("TEST 4: CustomerDAO - CRUD Operations (New Address Structure)");
        System.out.println("═══════════════════════════════════════════════════\n");
        
        CustomerDAO custDao = new CustomerDAO();
        AddressDAO aDao = new AddressDAO();
        BarangayDAO bDao = new BarangayDAO();
        CityDAO cDao = new CityDAO();
        
        String testCustomerId = "CRUD-C001";
        
        try {
            // Setup: Create supporting address hierarchy
            System.out.println("─── 4.0 SETUP: Creating supporting address hierarchy ───");
            City city = new City(null, "Test Customer City");
            cDao.insertCity(city);
            Integer testCityId = city.getCityID();
            
            Barangay barangay = new Barangay(null, testCityId, "Test Customer Barangay");
            bDao.insertBarangay(barangay);
            Integer testBarangayId = barangay.getBarangayID();
            
            Address address = new Address(null, testBarangayId, "789 Customer Test St");
            aDao.insertAddress(address);
            Integer testAddressId = address.getAddressID();
            
            System.out.println(":) Setup complete: Address hierarchy created (ID: " + testAddressId + ")");
            
            // === CREATE (Insert) ===
            System.out.println("\n─── 4.1 CREATE: Inserting Customer with addressID ───");
            Customer customer = new Customer(
                testCustomerId,
                "Test",
                "Customer",
                "09999999999",
                testAddressId,  // Using addressID instead of address string
                "test.customer@email.com",
                "Active"
            );
            
            boolean insertSuccess = custDao.insertCustomer(customer);
            System.out.println(insertSuccess ? 
                ":) INSERT successful: " + testCustomerId : 
                ":( INSERT failed");
            
            // === READ (Select by ID) ===
            System.out.println("\n─── 4.2 READ: Retrieving Customer by ID ───");
            Customer retrieved = custDao.getCustomerById(testCustomerId);
            
            if (retrieved != null) {
                System.out.println(":) SELECT by ID successful:");
                System.out.println("  Customer ID: " + retrieved.getCustomerID());
                System.out.println("  Name: " + retrieved.getFullName());
                System.out.println("  Contact: " + retrieved.getContactNumber());
                System.out.println("  Address ID: " + retrieved.getAddressID());
                System.out.println("  Email: " + retrieved.getEmailAddress());
                System.out.println("  Status: " + retrieved.getStatus());
            } else {
                System.out.println(":( SELECT by ID failed - Record not found");
            }
            
            // === READ (Select with Address Details) ===
            System.out.println("\n─── 4.3 READ: Retrieving Customer with Full Address ───");
            Customer withAddress = custDao.getCustomerWithAddress(testCustomerId);
            
            if (withAddress != null && withAddress.getAddress() != null) {
                System.out.println(":) SELECT with address successful:");
                System.out.println("  Customer: " + withAddress.getFullName());
                System.out.println("  Full Address: " + withAddress.getAddress().getFullAddress());
            }
            
            // === READ (Select All) ===
            System.out.println("\n─── 4.4 READ: Retrieving All Active Customers ───");
            List<Customer> allCustomers = custDao.getAllCustomers();
            System.out.println(":) SELECT ALL successful: Found " + allCustomers.size() + " active customers");
            System.out.println("  First 5 customers:");
            for (int i = 0; i < Math.min(5, allCustomers.size()); i++) {
                Customer c = allCustomers.get(i);
                System.out.println("  - " + c.getCustomerID() + ": " + c.getFullName() + 
                                 " (AddressID: " + c.getAddressID() + ")");
            }
            
            // === UPDATE ===
            System.out.println("\n─── 4.5 UPDATE: Modifying Customer ───");
            if (retrieved != null) {
                retrieved.setFirstName("Updated");
                retrieved.setContactNumber("09888888888");
                
                boolean updateSuccess = custDao.updateCustomer(retrieved);
                System.out.println(updateSuccess ? 
                    ":) UPDATE successful" : 
                    ":( UPDATE failed");
                
                // Verify update
                Customer updated = custDao.getCustomerById(testCustomerId);
                if (updated != null) {
                    System.out.println("  New Name: " + updated.getFullName());
                    System.out.println("  New Contact: " + updated.getContactNumber());
                }
            }
            
            // === SOFT DELETE (Deactivate) ===
            System.out.println("\n─── 4.6 SOFT DELETE: Deactivating Customer ───");
            boolean deactivateSuccess = custDao.deactivateCustomer(testCustomerId);
            System.out.println(deactivateSuccess ? 
                ":) DEACTIVATE successful (status set to 'Inactive')" : 
                ":( DEACTIVATE failed");
            
            // Verify deactivation
            Customer deactivated = custDao.getCustomerById(testCustomerId);
            System.out.println(deactivated == null ? 
                ":) Verified: Record is now inactive (not returned by active-only query)" : 
                ":( Warning: Record still active");
            
            // Cleanup: Remove supporting records
            System.out.println("\n─── 4.7 CLEANUP: Removing supporting records ───");
            aDao.deleteAddress(testAddressId);
            bDao.deleteBarangay(testBarangayId);
            cDao.deleteCity(testCityId);
            System.out.println(":) Cleanup complete");
            
            System.out.println("\n> CustomerDAO Tests Complete\n");
            
        } catch (Exception e) {
            System.out.println(":( ERROR in CustomerDAO test: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * Test 5: Complete CRUD testing for TechnicianDAO
     */
    private static void testTechnicianDAO() {
        System.out.println("═══════════════════════════════════════════════════");
        System.out.println("TEST 5: TechnicianDAO - CRUD Operations");
        System.out.println("═══════════════════════════════════════════════════\n");
        
        TechnicianDAO dao = new TechnicianDAO();
        String testId = "CRUD-T001";
        
        try {
            // === CREATE (Insert) ===
            System.out.println("─── 5.1 CREATE: Inserting Technician ───");
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
            boolean deleteSuccess = dao.deactivateTechnician(testId);
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
     * Test 6: Complete CRUD testing for PartDAO
     */
    private static void testPartDAO() {
        System.out.println("═══════════════════════════════════════════════════");
        System.out.println("TEST 6: PartDAO - CRUD Operations");
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
            boolean deleteSuccess = dao.deactivatePart(testId);
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
     * Test 7: Complete CRUD testing for MaintenanceDAO
     */
    private static void testMaintenanceDAO() {
        System.out.println("═══════════════════════════════════════════════════");
        System.out.println("TEST 7: MaintenanceDAO - CRUD Operations");
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
                "ES-001"  // Must exist in vehicles table (vehicleType: E-Scooter)
            );
            // Note: totalCost is calculated automatically (defaults to 0, updated on completion)
            
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
                System.out.println("  Total Cost: Php" + retrieved.getTotalCost());
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
                retrieved.setTotalCost(new BigDecimal("3500.50"));  // Update total cost
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
                    System.out.println("  New Total Cost: Php" + updated.getTotalCost());
                }
            }
            
            // === SOFT DELETE (Deactivate) ===
            System.out.println("\n─── 3.7 SOFT DELETE: Deactivating Maintenance Record ───");
            boolean deactivateSuccess = mDao.deactivateMaintenance(testMaintenanceId);
            System.out.println(deactivateSuccess ? 
                ":) DEACTIVATE successful (status set to 'Inactive')" : 
                ":( DEACTIVATE failed");
            
            // Verify deactivation
            MaintenanceTransaction deactivated = mDao.getMaintenanceById(testMaintenanceId);
            System.out.println(deactivated == null ? 
                ":) Verified: Record is now inactive (not returned by active-only query)" : 
                ":( Warning: Record still active");
            
            // Verify can still retrieve if needed for historical purposes
            MaintenanceTransaction historical = mDao.getMaintenanceByIdIncludingInactive(testMaintenanceId);
            System.out.println(historical != null && "Inactive".equals(historical.getStatus()) ? 
                ":) Verified: Record exists with status='Inactive'" : 
                ":( Warning: Historical record not found or status incorrect");
            
            // Cleanup: Remove supporting records
            System.out.println("\n─── 3.8 CLEANUP: Removing supporting records ───");
            tDao.deactivateTechnician(testTechnicianId);
            System.out.println(":) Cleanup complete");
            
            System.out.println("\n> MaintenanceDAO Tests Complete\n");
            
        } catch (Exception e) {
            System.out.println(":( ERROR in MaintenanceDAO test: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * Test 8: Complete CRUD testing for MaintenanceChequeDAO
     */
    private static void testMaintenanceChequeDAO() {
        System.out.println("═══════════════════════════════════════════════════");
        System.out.println("TEST 8: MaintenanceChequeDAO - CRUD Operations");
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
            
            // === SOFT DELETE (Single Part) ===
            System.out.println("\n─── 4.6 SOFT DELETE: Deactivating Single Part from Maintenance ───");
            boolean deactivateSingle = mcDao.deactivateMaintenanceCheque(testMaintenanceId, testPart2Id);
            System.out.println(deactivateSingle ? 
                ":) DEACTIVATE single part successful" : 
                ":( DEACTIVATE failed");
            
            List<MaintenanceCheque> afterDeactivate = mcDao.getPartsByMaintenance(testMaintenanceId);
            System.out.println("  Active parts remaining: " + afterDeactivate.size());
            
            // === SOFT DELETE (All Parts for Maintenance) ===
            System.out.println("\n─── 4.7 SOFT DELETE: Deactivating All Parts from Maintenance ───");
            boolean deactivateAll = mcDao.deactivateAllByMaintenance(testMaintenanceId);
            System.out.println(deactivateAll ? 
                ":) DEACTIVATE all parts successful" : 
                ":( DEACTIVATE failed");
            
            List<MaintenanceCheque> afterDeactivateAll = mcDao.getPartsByMaintenance(testMaintenanceId);
            System.out.println(afterDeactivateAll.isEmpty() ? 
                ":) Verified: No active parts remain" : 
                ":( Warning: Active parts still exist");
            
            // Verify historical access still works
            List<MaintenanceCheque> historicalParts = mcDao.getPartsByMaintenanceIncludingInactive(testMaintenanceId);
            System.out.println("  Historical parts (including inactive): " + historicalParts.size());
            
            // Cleanup (use soft delete for consistency)
            System.out.println("\n─── 4.8 CLEANUP: Deactivating supporting records ───");
            mDao.deactivateMaintenance(testMaintenanceId);
            tDao.deactivateTechnician(testTechnicianId);
            pDao.deactivatePart(testPart1Id);
            pDao.deactivatePart(testPart2Id);
            System.out.println(":) Cleanup complete (all records deactivated)");
            
            System.out.println("\n> MaintenanceChequeDAO Tests Complete\n");
            
        } catch (Exception e) {
            System.out.println(":( ERROR in MaintenanceChequeDAO test: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * Test 9: Complete CRUD testing for PaymentDAO
     */
    private static void testPaymentDAO() {
        System.out.println("═══════════════════════════════════════════════════");
        System.out.println("TEST 9: PaymentDAO - CRUD Operations");
        System.out.println("═══════════════════════════════════════════════════\n");
        
        PaymentDAO pDao = new PaymentDAO();
        
        String testPaymentId = "CRUD-PAY001";
        String testRentalId = "RNT-005";  // Using existing rental from test data
        
        try {
            // === CREATE (Insert) ===
            System.out.println("─── 5.1 CREATE: Inserting Payment ───");
            PaymentTransaction payment = new PaymentTransaction(
                testPaymentId,
                new BigDecimal("150.00"),
                testRentalId,
                new java.sql.Date(System.currentTimeMillis())
            );
            
            boolean insertSuccess = pDao.insertPayment(payment);
            System.out.println(insertSuccess ? 
                ":) INSERT successful: " + testPaymentId : 
                ":( INSERT failed");
            
            // === READ (Select by ID) ===
            System.out.println("\n─── 5.2 READ: Retrieving Payment by ID ───");
            PaymentTransaction retrieved = pDao.getPaymentById(testPaymentId);
            
            if (retrieved != null) {
                System.out.println(":) SELECT by ID successful:");
                System.out.println("  Payment ID: " + retrieved.getPaymentID());
                System.out.println("  Amount: Php" + retrieved.getAmount());
                System.out.println("  Rental ID: " + retrieved.getRentalID());
                System.out.println("  Payment Date: " + retrieved.getPaymentDate());
                System.out.println("  Status: " + retrieved.getStatus());
            } else {
                System.out.println(":( SELECT by ID failed - Record not found");
            }
            
            // === READ (Select All) ===
            System.out.println("\n─── 5.3 READ: Retrieving All Payments ───");
            List<PaymentTransaction> allPayments = pDao.getAllPayments();
            System.out.println(":) SELECT ALL successful: Found " + allPayments.size() + " active payments");
            System.out.println("  First 5 payments:");
            for (int i = 0; i < Math.min(5, allPayments.size()); i++) {
                PaymentTransaction p = allPayments.get(i);
                System.out.println("  - " + p.getPaymentID() + ": Php" + p.getAmount() + 
                                 " for " + p.getRentalID() + " (" + p.getStatus() + ")");
            }
            
            // === READ (Select by Rental) ===
            System.out.println("\n─── 5.4 READ: Retrieving Payments by Rental ───");
            List<PaymentTransaction> rentalPayments = pDao.getPaymentsByRental(testRentalId);
            System.out.println(":) SELECT by Rental successful: Found " + rentalPayments.size() + 
                             " payment(s) for rental " + testRentalId);
            for (PaymentTransaction p : rentalPayments) {
                System.out.println("  - " + p.getPaymentID() + ": Php" + p.getAmount() + 
                                 " on " + p.getPaymentDate());
            }
            
            // === READ (Select by Date Range) ===
            System.out.println("\n─── 5.5 READ: Retrieving Payments by Date Range ───");
            java.sql.Date startDate = java.sql.Date.valueOf("2024-10-22");
            java.sql.Date endDate = java.sql.Date.valueOf("2024-10-27");
            
            List<PaymentTransaction> dateRangePayments = pDao.getPaymentsByDateRange(startDate, endDate);
            System.out.println(":) SELECT by Date Range successful: Found " + dateRangePayments.size() + 
                             " payments between " + startDate + " and " + endDate);
            
            // === SPECIAL: Revenue Calculation ===
            System.out.println("\n─── 5.6 SPECIAL: Total Revenue Calculation ───");
            BigDecimal totalRevenue = pDao.getTotalRevenueByDateRange(startDate, endDate);
            System.out.println(":) Total Revenue: Php" + totalRevenue + 
                             " (from " + startDate + " to " + endDate + ")");
            
            // === UPDATE ===
            System.out.println("\n─── 5.7 UPDATE: Modifying Payment ───");
            if (retrieved != null) {
                retrieved.setAmount(new BigDecimal("175.50"));
                retrieved.setPaymentDate(new java.sql.Date(System.currentTimeMillis()));
                
                boolean updateSuccess = pDao.updatePayment(retrieved);
                System.out.println(updateSuccess ? 
                    ":) UPDATE successful" : 
                    ":( UPDATE failed");
                
                // Verify update
                PaymentTransaction updated = pDao.getPaymentById(testPaymentId);
                if (updated != null) {
                    System.out.println("  New Amount: Php" + updated.getAmount());
                    System.out.println("  New Payment Date: " + updated.getPaymentDate());
                }
            }
            
            // === SOFT DELETE (Deactivate) ===
            System.out.println("\n─── 5.8 SOFT DELETE: Deactivating Payment ───");
            boolean deactivateSuccess = pDao.deactivatePayment(testPaymentId);
            System.out.println(deactivateSuccess ? 
                ":) DEACTIVATE successful (status set to 'Inactive')" : 
                ":( DEACTIVATE failed");
            
            // Verify deactivation
            PaymentTransaction deactivated = pDao.getPaymentById(testPaymentId);
            System.out.println(deactivated == null ? 
                ":) Verified: Record is now inactive (not returned by active-only query)" : 
                ":( Warning: Record still active");
            
            // Verify can still retrieve if needed for historical purposes
            PaymentTransaction historical = pDao.getPaymentByIdIncludingInactive(testPaymentId);
            System.out.println(historical != null && "Inactive".equals(historical.getStatus()) ? 
                ":) Verified: Record exists with status='Inactive'" : 
                ":( Warning: Historical record not found or status incorrect");
            
            // === REACTIVATE ===
            System.out.println("\n─── 5.9 REACTIVATE: Reactivating Payment ───");
            boolean reactivateSuccess = pDao.reactivatePayment(testPaymentId);
            System.out.println(reactivateSuccess ? 
                ":) REACTIVATE successful (status set back to 'Active')" : 
                ":( REACTIVATE failed");
            
            // Verify reactivation
            PaymentTransaction reactivated = pDao.getPaymentById(testPaymentId);
            System.out.println(reactivated != null && "Active".equals(reactivated.getStatus()) ? 
                ":) Verified: Record is active again" : 
                ":( Warning: Reactivation failed");
            
            // Final cleanup - deactivate again
            System.out.println("\n─── 5.10 CLEANUP: Final deactivation ───");
            pDao.deactivatePayment(testPaymentId);
            System.out.println(":) Cleanup complete");
            
            System.out.println("\n> PaymentDAO Tests Complete\n");
            
        } catch (Exception e) {
            System.out.println(":( ERROR in PaymentDAO test: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * Test 10: Complete CRUD testing for PenaltyDAO
     */
    private static void testPenaltyDAO() {
        System.out.println("═══════════════════════════════════════════════════");
        System.out.println("TEST 10: PenaltyDAO - CRUD Operations");
        System.out.println("═══════════════════════════════════════════════════\n");
        
        PenaltyDAO penDao = new PenaltyDAO();
        
        String testPenaltyId = "CRUD-PEN01";
        String testRentalId = "RNT-005";  // Using existing rental from test data
        String testMaintenanceId = "MAINT-001";  // Using existing maintenance from test data
        
        try {
            // === CREATE (Insert) ===
            System.out.println("─── 6.1 CREATE: Inserting Penalty ───");
            PenaltyTransaction penalty = new PenaltyTransaction(
                testPenaltyId,
                testRentalId,
                new BigDecimal("2500.00"),
                "UNPAID",
                testMaintenanceId,
                new java.sql.Date(System.currentTimeMillis()),
                "Active"
            );
            
            boolean insertSuccess = penDao.insertPenalty(penalty);
            System.out.println(insertSuccess ? 
                ":) INSERT successful: " + testPenaltyId : 
                ":( INSERT failed");
            
            // === READ (Select by ID) ===
            System.out.println("\n─── 6.2 READ: Retrieving Penalty by ID ───");
            PenaltyTransaction retrieved = penDao.getPenaltyById(testPenaltyId);
            
            if (retrieved != null) {
                System.out.println(":) SELECT by ID successful:");
                System.out.println("  Penalty ID: " + retrieved.getPenaltyID());
                System.out.println("  Rental ID: " + retrieved.getRentalID());
                System.out.println("  Total Penalty: Php" + retrieved.getTotalPenalty());
                System.out.println("  Payment Status: " + retrieved.getPenaltyStatus());
                System.out.println("  Maintenance ID: " + retrieved.getMaintenanceID());
                System.out.println("  Date Issued: " + retrieved.getDateIssued());
                System.out.println("  Record Status: " + retrieved.getStatus());
            } else {
                System.out.println(":( SELECT by ID failed - Record not found");
            }
            
            // === READ (Select All) ===
            System.out.println("\n─── 6.3 READ: Retrieving All Active Penalties ───");
            List<PenaltyTransaction> allPenalties = penDao.getAllPenalties();
            System.out.println(":) SELECT ALL successful: Found " + allPenalties.size() + " active penalties");
            System.out.println("  First 5 penalties:");
            for (int i = 0; i < Math.min(5, allPenalties.size()); i++) {
                PenaltyTransaction p = allPenalties.get(i);
                System.out.println("  - " + p.getPenaltyID() + ": Php" + p.getTotalPenalty() + 
                                 " (" + p.getPenaltyStatus() + ") - Rental: " + p.getRentalID());
            }
            
            // === READ (Select by Rental) ===
            System.out.println("\n─── 6.4 READ: Retrieving Penalties by Rental ───");
            List<PenaltyTransaction> rentalPenalties = penDao.getPenaltiesByRental(testRentalId);
            System.out.println(":) SELECT by Rental successful: Found " + rentalPenalties.size() + 
                             " penalty(ies) for rental " + testRentalId);
            for (PenaltyTransaction p : rentalPenalties) {
                System.out.println("  - " + p.getPenaltyID() + ": Php" + p.getTotalPenalty() + 
                                 " (" + p.getPenaltyStatus() + ") issued on " + p.getDateIssued());
            }
            
            // === READ (Select by Payment Status) ===
            System.out.println("\n─── 6.5 READ: Retrieving Penalties by Payment Status ───");
            List<PenaltyTransaction> unpaidPenalties = penDao.getPenaltiesByPaymentStatus("UNPAID");
            System.out.println(":) SELECT by Payment Status successful: Found " + unpaidPenalties.size() + 
                             " UNPAID penalties");
            
            List<PenaltyTransaction> paidPenalties = penDao.getPenaltiesByPaymentStatus("PAID");
            System.out.println(":) Found " + paidPenalties.size() + " PAID penalties");
            
            // === READ (Select by Maintenance) ===
            System.out.println("\n─── 6.6 READ: Retrieving Penalties by Maintenance ───");
            List<PenaltyTransaction> maintenancePenalties = penDao.getPenaltiesByMaintenance(testMaintenanceId);
            System.out.println(":) SELECT by Maintenance successful: Found " + maintenancePenalties.size() + 
                             " penalty(ies) linked to maintenance " + testMaintenanceId);
            
            // === SPECIAL: Total Penalties Calculation ===
            System.out.println("\n─── 6.7 SPECIAL: Total Penalties by Rental ───");
            BigDecimal totalPenalties = penDao.getTotalPenaltiesByRental(testRentalId);
            System.out.println(":) Total Active Penalties for Rental " + testRentalId + ": Php" + totalPenalties);
            
            // === READ (Select by Date Range) ===
            System.out.println("\n─── 6.8 READ: Retrieving Penalties by Date Range ───");
            java.sql.Date startDate = java.sql.Date.valueOf("2024-10-19");
            java.sql.Date endDate = java.sql.Date.valueOf("2024-10-24");
            
            List<PenaltyTransaction> dateRangePenalties = penDao.getPenaltiesByDateRange(startDate, endDate);
            System.out.println(":) SELECT by Date Range successful: Found " + dateRangePenalties.size() + 
                             " penalties between " + startDate + " and " + endDate);
            
            // === UPDATE ===
            System.out.println("\n─── 6.9 UPDATE: Modifying Penalty (Payment Status) ───");
            if (retrieved != null) {
                retrieved.setPenaltyStatus("PAID");
                retrieved.setTotalPenalty(new BigDecimal("2750.00"));  // Adjusted amount
                
                boolean updateSuccess = penDao.updatePenalty(retrieved);
                System.out.println(updateSuccess ? 
                    ":) UPDATE successful" : 
                    ":( UPDATE failed");
                
                // Verify update
                PenaltyTransaction updated = penDao.getPenaltyById(testPenaltyId);
                if (updated != null) {
                    System.out.println("  New Payment Status: " + updated.getPenaltyStatus());
                    System.out.println("  New Amount: Php" + updated.getTotalPenalty());
                }
            }
            
            // === SOFT DELETE (Deactivate) ===
            System.out.println("\n─── 6.10 SOFT DELETE: Deactivating Penalty ───");
            boolean deactivateSuccess = penDao.deactivatePenalty(testPenaltyId);
            System.out.println(deactivateSuccess ? 
                ":) DEACTIVATE successful (status set to 'Inactive')" : 
                ":( DEACTIVATE failed");
            
            // Verify deactivation
            PenaltyTransaction deactivated = penDao.getPenaltyById(testPenaltyId);
            System.out.println(deactivated == null ? 
                ":) Verified: Record is now inactive (not returned by active-only query)" : 
                ":( Warning: Record still active");
            
            // Verify can still retrieve if needed for historical purposes
            PenaltyTransaction historical = penDao.getPenaltyByIdIncludingInactive(testPenaltyId);
            System.out.println(historical != null && "Inactive".equals(historical.getStatus()) ? 
                ":) Verified: Record exists with status='Inactive'" : 
                ":( Warning: Historical record not found or status incorrect");
            
            // === REACTIVATE ===
            System.out.println("\n─── 6.11 REACTIVATE: Reactivating Penalty ───");
            boolean reactivateSuccess = penDao.reactivatePenalty(testPenaltyId);
            System.out.println(reactivateSuccess ? 
                ":) REACTIVATE successful (status set back to 'Active')" : 
                ":( REACTIVATE failed");
            
            // Verify reactivation
            PenaltyTransaction reactivated = penDao.getPenaltyById(testPenaltyId);
            System.out.println(reactivated != null && reactivated.isActive() ? 
                ":) Verified: Record is active again" : 
                ":( Warning: Reactivation failed");
            
            // Check helper methods
            if (reactivated != null) {
                System.out.println("  Helper method isActive(): " + reactivated.isActive());
                System.out.println("  Helper method isInactive(): " + reactivated.isInactive());
            }
            
            // Final cleanup - deactivate again
            System.out.println("\n─── 6.12 CLEANUP: Final deactivation ───");
            penDao.deactivatePenalty(testPenaltyId);
            System.out.println(":) Cleanup complete");
            
            System.out.println("\n> PenaltyDAO Tests Complete\n");
            
        } catch (Exception e) {
            System.out.println(":( ERROR in PenaltyDAO test: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
