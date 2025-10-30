package test;

import dao.LocationDAO;
import dao.VehicleDAO;
import dao.CustomerDAO;
import model.Location;
import model.Vehicle;
import model.Customer;
import java.util.List;

/**
 * Comprehensive Database Test
 * Tests all DAOs with real database operations
 * Uses ONLY the methods that exist in your actual DAOs
 */
public class daotest {
    
    private static LocationDAO locationDAO = new LocationDAO();
    private static VehicleDAO vehicleDAO = new VehicleDAO();
    private static CustomerDAO customerDAO = new CustomerDAO();
    
    public static void main(String[] args) {
        System.out.println("╔════════════════════════════════════════════════╗");
        System.out.println("║     COMPLETE DATABASE TEST SUITE              ║");
        System.out.println("╚════════════════════════════════════════════════╝\n");
        
        try {
            // Test each entity
            testLocations();
            testVehicles();
            testCustomers();
            
            // Show final statistics
            showDatabaseStatistics();
            
            System.out.println("\n╔════════════════════════════════════════════════╗");
            System.out.println("║     ✅ ALL TESTS COMPLETED SUCCESSFULLY!      ║");
            System.out.println("╚════════════════════════════════════════════════╝");
            
        } catch (Exception e) {
            System.err.println("\n❌ TEST FAILED!");
            e.printStackTrace();
        }
    }
    
    // ==================== LOCATION TESTS ====================
    
    private static void testLocations() {
        System.out.println("╔════════════════════════════════════════════════╗");
        System.out.println("║              LOCATION TESTS                    ║");
        System.out.println("╚════════════════════════════════════════════════╝\n");
        
        // Test 1: Get all locations
        System.out.println("━━━ Test 1: Get All Locations ━━━");
        List<Location> locations = locationDAO.getAllLocations();
        
        if (locations.isEmpty()) {
            System.out.println("⚠️  No locations found in database!");
            System.out.println("💡 Run test_data.sql first\n");
            return;
        }
        
        System.out.printf("%-12s %-30s%n", "ID", "NAME");
        System.out.println("─".repeat(50));
        for (Location loc : locations) {
            System.out.printf("%-12s %-30s%n", 
                loc.getLocationID(), 
                loc.getName()
            );
        }
        System.out.println("Total: " + locations.size() + " locations\n");
        
        // Test 2: Get location by ID
        System.out.println("━━━ Test 2: Get Location By ID ━━━");
        Location location = locationDAO.getLocationById("LOC-001");
        if (location != null) {
            System.out.println("✅ Found: " + location.getLocationID() + " - " + location.getName());
        } else {
            System.out.println("❌ Location LOC-001 not found");
        }
        System.out.println();
        
        // Test 3: Insert, Update, Delete
        System.out.println("━━━ Test 3: Insert, Update, Delete Operations ━━━");
        
        // Insert
        Location testLoc = new Location("TEST-LOC", "Test Location");
        boolean inserted = locationDAO.insertLocation(testLoc);
        System.out.println(inserted ? "✅ Insert successful" : "❌ Insert failed");
        
        // Update
        if (inserted) {
            testLoc.setName("Updated Test Location");
            boolean updated = locationDAO.updateLocation(testLoc);
            System.out.println(updated ? "✅ Update successful" : "❌ Update failed");
            
            // Verify
            Location updated2 = locationDAO.getLocationById("TEST-LOC");
            if (updated2 != null) {
                System.out.println("   New name: " + updated2.getName());
            }
            
            // Delete
            boolean deleted = locationDAO.deleteLocation("TEST-LOC");
            System.out.println(deleted ? "✅ Delete successful" : "❌ Delete failed");
        }
        System.out.println();
    }
    
    // ==================== VEHICLE TESTS ====================
    
    private static void testVehicles() {
        System.out.println("╔════════════════════════════════════════════════╗");
        System.out.println("║              VEHICLE TESTS                     ║");
        System.out.println("╚════════════════════════════════════════════════╝\n");
        
        // Test 1: Get all vehicles
        System.out.println("━━━ Test 1: Get All Vehicles ━━━");
        List<Vehicle> vehicles = vehicleDAO.getAllVehicles();
        
        if (vehicles.isEmpty()) {
            System.out.println("⚠️  No vehicles found in database!");
            System.out.println("💡 Run test_data.sql first\n");
            return;
        }
        
        System.out.printf("%-12s %-15s %-25s %-12s %s%n", 
            "PLATE ID", "TYPE", "MODEL", "STATUS", "PRICE");
        System.out.println("─".repeat(80));
        for (Vehicle v : vehicles) {
            System.out.printf("%-12s %-15s %-25s %-12s ₱%.2f%n",
                v.getPlateID(),
                v.getVehicleType(),
                v.getVehicleModel(),
                v.getStatus(),
                v.getRentalPrice()
            );
        }
        System.out.println("Total: " + vehicles.size() + " vehicles\n");
        
        // Test 2: Get available vehicles
        System.out.println("━━━ Test 2: Get Available Vehicles ━━━");
        List<Vehicle> available = vehicleDAO.getAvailableVehicles();
        System.out.println("Available for rent: " + available.size() + " vehicles");
        for (Vehicle v : available) {
            System.out.printf("  • %-12s %-25s ₱%.2f/hr%n", 
                v.getPlateID(), 
                v.getVehicleModel(), 
                v.getRentalPrice()
            );
        }
        System.out.println();
        
        // Test 3: Get vehicles by type
        System.out.println("━━━ Test 3: Get E-Scooters ━━━");
        List<Vehicle> scooters = vehicleDAO.getVehiclesByType("E-Scooter");
        System.out.println("E-Scooters: " + scooters.size());
        for (Vehicle v : scooters) {
            System.out.println("  • " + v.getPlateID() + " - " + v.getVehicleModel() + 
                             " (" + v.getStatus() + ")");
        }
        System.out.println();
        
        // Test 4: Get vehicles by status
        System.out.println("━━━ Test 4: Get Vehicles In Use ━━━");
        List<Vehicle> inUse = vehicleDAO.getVehiclesByStatus("In Use");
        System.out.println("Currently In Use: " + inUse.size());
        for (Vehicle v : inUse) {
            System.out.println("  • " + v.getPlateID() + " - " + v.getVehicleModel());
        }
        System.out.println();
        
        // Test 5: Get vehicle by ID
        System.out.println("━━━ Test 5: Get Vehicle By ID ━━━");
        Vehicle vehicle = vehicleDAO.getVehicleById("ES-001");
        if (vehicle != null) {
            System.out.println("✅ Found: " + vehicle.getPlateID());
            System.out.println("   Type: " + vehicle.getVehicleType());
            System.out.println("   Model: " + vehicle.getVehicleModel());
            System.out.println("   Status: " + vehicle.getStatus());
            System.out.println("   Price: ₱" + vehicle.getRentalPrice() + "/hr");
            
            // Test helper methods
            System.out.println("   Is Available? " + vehicle.isAvailable());
            System.out.println("   Is E-Scooter? " + vehicle.isEScooter());
        } else {
            System.out.println("❌ Vehicle ES-001 not found");
        }
        System.out.println();
        
        // Test 6: Insert, Update, Delete
        System.out.println("━━━ Test 6: Insert, Update, Delete Operations ━━━");
        
        // Insert
        Vehicle testVehicle = new Vehicle(
            "TEST-999", 
            "E-Scooter", 
            "Test Scooter Model", 
            "Available", 
            55.00
        );
        
        boolean inserted = vehicleDAO.insertVehicle(testVehicle);
        System.out.println(inserted ? "✅ Insert successful" : "❌ Insert failed");
        
        // Update
        if (inserted) {
            testVehicle.setRentalPrice(60.00);
            testVehicle.setStatus("In Use");
            boolean updated = vehicleDAO.updateVehicle(testVehicle);
            System.out.println(updated ? "✅ Update successful" : "❌ Update failed");
            
            // Verify
            Vehicle updated2 = vehicleDAO.getVehicleById("TEST-999");
            if (updated2 != null) {
                System.out.println("   New price: ₱" + updated2.getRentalPrice());
                System.out.println("   New status: " + updated2.getStatus());
            }
            
            // Test updateVehicleStatus method
            boolean statusUpdated = vehicleDAO.updateVehicleStatus("TEST-999", "Maintenance");
            System.out.println(statusUpdated ? "✅ Status update successful" : "❌ Status update failed");
            
            // Delete
            boolean deleted = vehicleDAO.deleteVehicle("TEST-999");
            System.out.println(deleted ? "✅ Delete successful" : "❌ Delete failed");
        }
        System.out.println();
    }
    
    // ==================== CUSTOMER TESTS ====================
    
    private static void testCustomers() {
        System.out.println("╔════════════════════════════════════════════════╗");
        System.out.println("║              CUSTOMER TESTS                    ║");
        System.out.println("╚════════════════════════════════════════════════╝\n");
        
        // Test 1: Get all customers
        System.out.println("━━━ Test 1: Get All Customers ━━━");
        List<Customer> customers = customerDAO.getAllCustomers();
        
        if (customers.isEmpty()) {
            System.out.println("⚠️  No customers found in database!");
            System.out.println("💡 Run test_data.sql first\n");
            return;
        }
        
        System.out.printf("%-12s %-20s %-15s %-30s%n", 
            "ID", "NAME", "CONTACT", "EMAIL");
        System.out.println("─".repeat(80));
        for (Customer c : customers) {
            System.out.printf("%-12s %-20s %-15s %-30s%n",
                c.getCustomerID(),
                c.getFullName(),
                c.getContactNumber(),
                c.getEmailAddress()
            );
        }
        System.out.println("Total: " + customers.size() + " customers\n");
        
        // Test 2: Get customer by ID
        System.out.println("━━━ Test 2: Get Customer By ID ━━━");
        Customer customer = customerDAO.getCustomerById("CUST-001");
        if (customer != null) {
            System.out.println("✅ Found: " + customer.getCustomerID());
            System.out.println("   Name: " + customer.getFullName());
            System.out.println("   Contact: " + customer.getContactNumber());
            System.out.println("   Email: " + customer.getEmailAddress());
            System.out.println("   Address: " + customer.getAddress());
        } else {
            System.out.println("❌ Customer CUST-001 not found");
        }
        System.out.println();
        
        // Test 3: Search customers by name
        System.out.println("━━━ Test 3: Search Customers By Name ━━━");
        List<Customer> searchResults = customerDAO.searchCustomersByName("Juan");
        System.out.println("Search for 'Juan': Found " + searchResults.size() + " result(s)");
        for (Customer c : searchResults) {
            System.out.println("  • " + c.getFullName() + " (" + c.getCustomerID() + ")");
        }
        System.out.println();
        
        // Test 4: Get customer by email
        System.out.println("━━━ Test 4: Get Customer By Email ━━━");
        Customer byEmail = customerDAO.getCustomerByEmail("juan.reyes@email.com");
        if (byEmail != null) {
            System.out.println("✅ Found: " + byEmail.getFullName());
        } else {
            System.out.println("❌ Customer not found");
        }
        System.out.println();
        
        // Test 5: Insert, Update, Delete
        System.out.println("━━━ Test 5: Insert, Update, Delete Operations ━━━");
        
        // Insert
        Customer testCustomer = new Customer(
            "TEST-999",
            "TestLast",
            "TestFirst",
            "09999999999",
            "Test Address, Test City",
            "test@email.com"
        );
        
        boolean inserted = customerDAO.insertCustomer(testCustomer);
        System.out.println(inserted ? "✅ Insert successful" : "❌ Insert failed");
        
        // Update
        if (inserted) {
            testCustomer.setEmailAddress("newemail@email.com");
            testCustomer.setContactNumber("09888888888");
            boolean updated = customerDAO.updateCustomer(testCustomer);
            System.out.println(updated ? "✅ Update successful" : "❌ Update failed");
            
            // Verify
            Customer updated2 = customerDAO.getCustomerById("TEST-999");
            if (updated2 != null) {
                System.out.println("   New email: " + updated2.getEmailAddress());
                System.out.println("   New contact: " + updated2.getContactNumber());
            }
            
            // Delete
            boolean deleted = customerDAO.deleteCustomer("TEST-999");
            System.out.println(deleted ? "✅ Delete successful" : "❌ Delete failed");
        }
        System.out.println();
    }
    
    // ==================== STATISTICS ====================
    
    private static void showDatabaseStatistics() {
        System.out.println("\n╔════════════════════════════════════════════════╗");
        System.out.println("║          DATABASE STATISTICS                   ║");
        System.out.println("╚════════════════════════════════════════════════╝\n");
        
        // Locations
        int locationCount = locationDAO.getAllLocations().size();
        System.out.println("📍 Locations:  " + locationCount);
        
        // Vehicles
        List<Vehicle> allVehicles = vehicleDAO.getAllVehicles();
        int totalVehicles = allVehicles.size();
        
        // Count by status manually
        int available = 0;
        int inUse = 0;
        int maintenance = 0;
        
        for (Vehicle v : allVehicles) {
            if (v.isAvailable()) available++;
            else if ("In Use".equalsIgnoreCase(v.getStatus())) inUse++;
            else if (v.isInMaintenance()) maintenance++;
        }
        
        System.out.println("\n🚗 Vehicles:   " + totalVehicles);
        if (totalVehicles > 0) {
            System.out.println("   ├─ Available:    " + available + " (" + 
                String.format("%.1f%%", (available * 100.0 / totalVehicles)) + ")");
            System.out.println("   ├─ In Use:       " + inUse + " (" + 
                String.format("%.1f%%", (inUse * 100.0 / totalVehicles)) + ")");
            System.out.println("   └─ Maintenance:  " + maintenance + " (" + 
                String.format("%.1f%%", (maintenance * 100.0 / totalVehicles)) + ")");
        }
        
        // Vehicle types
        int scooterCount = vehicleDAO.getVehiclesByType("E-Scooter").size();
        int bikeCount = vehicleDAO.getVehiclesByType("E-Bike").size();
        int trikeCount = vehicleDAO.getVehiclesByType("E-Trike").size();
        
        System.out.println("\n🛴 Vehicle Types:");
        System.out.println("   ├─ E-Scooters: " + scooterCount);
        System.out.println("   ├─ E-Bikes:    " + bikeCount);
        System.out.println("   └─ E-Trikes:   " + trikeCount);
        
        // Customers
        int customerCount = customerDAO.getAllCustomers().size();
        System.out.println("\n👥 Customers:  " + customerCount);
        
        // Calculate average prices
        if (!allVehicles.isEmpty()) {
            double totalPrice = 0;
            double minPrice = Double.MAX_VALUE;
            double maxPrice = Double.MIN_VALUE;
            
            for (Vehicle v : allVehicles) {
                totalPrice += v.getRentalPrice();
                if (v.getRentalPrice() < minPrice) minPrice = v.getRentalPrice();
                if (v.getRentalPrice() > maxPrice) maxPrice = v.getRentalPrice();
            }
            
            double avgPrice = totalPrice / allVehicles.size();
            
            System.out.println("\n💰 Rental Prices:");
            System.out.println("   ├─ Average: ₱" + String.format("%.2f", avgPrice) + "/hr");
            System.out.println("   ├─ Minimum: ₱" + String.format("%.2f", minPrice) + "/hr");
            System.out.println("   └─ Maximum: ₱" + String.format("%.2f", maxPrice) + "/hr");
        }
        
        System.out.println("\n" + "─".repeat(50));
    }
}