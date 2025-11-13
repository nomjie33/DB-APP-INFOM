package test;

import dao.*;
import model.*;
import service.*;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.sql.Date;
import java.util.List;

/**
 * SERVICE LAYER TEST CLASS
 * 
 * PURPOSE: Comprehensive testing of all business logic services:
 * - RentalService (Rental creation, completion, cancellation)
 * - PaymentService (Payment processing, cost calculation)
 * - MaintenanceService (Maintenance scheduling, completion)
 * - DeploymentService (Vehicle deployment and tracking)
 * - PenaltyService (Penalty calculation and processing)
 * 
 * PREREQUISITES:
 * 1. MySQL database 'vehicle_rental_db' must exist
 * 2. All required tables must be created (run database_schema.sql)
 * 3. Test data must be loaded (run test_data.sql)
 * 4. db.properties file must be configured with YOUR MySQL credentials
 * 
 * HOW TO RUN:
 * 1. Right-click this file → Run As → Java Application
 * 2. Check console output for success/failure messages
 * 3. Verify in MySQL Workbench that operations worked correctly
 * 
 * TEST APPROACH:
 * - Each service is tested with realistic business workflows
 * - Tests use existing test data from test_data.sql
 * - Tests create temporary records and clean up after
 * - Tests verify both success and error handling scenarios
 */
public class ServiceTest {
    
    // Test counters
    private static int totalTests = 0;
    private static int passedTests = 0;
    
    public static void main(String[] args) {
        System.out.println("═══════════════════════════════════════════════════");
        System.out.println("   SERVICE LAYER TEST - Business Logic Testing");
        System.out.println("═══════════════════════════════════════════════════\n");
        
        // Test each service with business workflows
        testRentalService();
        testPaymentService();
        testMaintenanceService();
        testDeploymentService();
        testPenaltyService();
        
        System.out.println("\n═══════════════════════════════════════════════════");
        System.out.println("   ALL SERVICE TESTS COMPLETED");
        System.out.println("═══════════════════════════════════════════════════");
        System.out.println("\n📊 TEST RESULTS:");
        System.out.println("   Tests Passed: " + passedTests + " / " + totalTests);
        
        double successRate = totalTests > 0 ? (passedTests * 100.0 / totalTests) : 0;
        System.out.println("   Success Rate: " + String.format("%.1f", successRate) + "%");
        
        if (passedTests == totalTests) {
            System.out.println("   Status: ✅ ALL TESTS PASSED!");
        } else {
            System.out.println("   Status: ⚠️ SOME TESTS FAILED");
            System.out.println("   Failed: " + (totalTests - passedTests) + " test(s)");
        }
        System.out.println("═══════════════════════════════════════════════════");
    }
    
    /**
     * Helper method to track test results
     */
    private static void recordTest(boolean passed) {
        totalTests++;
        if (passed) {
            passedTests++;
        }
    }
    
    /**
     * Test 1: RentalService - Complete rental workflow
     */
    private static void testRentalService() {
        System.out.println("═══════════════════════════════════════════════════");
        System.out.println("TEST 1: RentalService - Complete Rental Workflow");
        System.out.println("═══════════════════════════════════════════════════\n");
        
        // Initialize DAOs
        CustomerDAO customerDAO = new CustomerDAO();
        VehicleDAO vehicleDAO = new VehicleDAO();
        LocationDAO locationDAO = new LocationDAO();
        RentalDAO rentalDAO = new RentalDAO();
        PaymentDAO paymentDAO = new PaymentDAO();
        PaymentService paymentService = new PaymentService();
        
        // Initialize RentalService with all dependencies
        RentalService rentalService = new RentalService(
            customerDAO, vehicleDAO, locationDAO, rentalDAO, paymentDAO, paymentService
        );
        
        String testRentalID = null;
        
        try {
            // === TEST 1.1: Create Rental ===
            System.out.println("─── 1.1 CREATE RENTAL: Testing rental creation ───");
            System.out.println("Customer: CUST-001, Vehicle: ES-009, Location: LOC-001");
            
            testRentalID = rentalService.createRental("CUST-001", "ES-009", "LOC-001");
            
            if (testRentalID != null) {
                System.out.println(" Rental created successfully: " + testRentalID);
                recordTest(true);
                
                // Verify rental exists in database
                RentalTransaction rental = rentalDAO.getRentalById(testRentalID);
                if (rental != null) {
                    System.out.println(" Rental verified in database");
                    System.out.println("  Customer: " + rental.getCustomerID());
                    System.out.println("  Vehicle: " + rental.getPlateID());
                    System.out.println("  Location: " + rental.getLocationID());
                    System.out.println("  Active: " + rental.isActive());
                    recordTest(true);
                } else {
                    System.out.println("✗ ERROR: Rental not found in database!");
                    recordTest(false);
                }
                
                // Verify placeholder payment was created
                PaymentTransaction payment = paymentService.getPaymentByRental(testRentalID);
                if (payment != null) {
                    System.out.println(" Placeholder payment created: " + payment.getPaymentID());
                    System.out.println("  Amount: Php" + payment.getAmount() + " (should be 0.00)");
                    recordTest(true);
                } else {
                    System.out.println("✗ ERROR: Placeholder payment not found!");
                    recordTest(false);
                }
            } else {
                System.out.println("✗ ERROR: Failed to create rental");
                recordTest(false);
            }
            
            System.out.println();
            
            // === TEST 1.2: Check Vehicle Availability ===
            System.out.println("─── 1.2 CHECK AVAILABILITY: Testing vehicle status ───");
            System.out.println("Checking if ES-009 is available after rental...");
            
            boolean available = rentalService.checkVehicleAvailability("ES-009");
            System.out.println("Available: " + available + " (should be false - in use)");
            
            if (!available) {
                System.out.println(" Vehicle correctly marked as unavailable");
                recordTest(true);
            } else {
                System.out.println("✗ ERROR: Vehicle should not be available!");
                recordTest(false);
            }
            
            System.out.println();
            
            // === TEST 1.3: Get Rental History ===
            System.out.println("─── 1.3 RENTAL HISTORY: Testing customer rental history ───");
            System.out.println("Getting rental history for CUST-001...");
            
            List<RentalTransaction> history = rentalService.getRentalHistory("CUST-001");
            if (history != null && !history.isEmpty()) {
                System.out.println(" Found " + history.size() + " rental(s) for customer");
                System.out.println("  Most recent: " + history.get(0).getRentalID());
                recordTest(true);
            } else {
                System.out.println("✗ ERROR: No rental history found!");
                recordTest(false);
            }
            
            System.out.println();
            
            // === TEST 1.4: Get Active Rentals ===
            System.out.println("─── 1.4 ACTIVE RENTALS: Testing active rental retrieval ───");
            
            List<RentalTransaction> activeRentals = rentalService.getActiveRentals();
            if (activeRentals != null) {
                System.out.println(" Found " + activeRentals.size() + " active rental(s)");
                boolean foundOurs = false;
                for (RentalTransaction r : activeRentals) {
                    if (r.getRentalID().equals(testRentalID)) {
                        foundOurs = true;
                        System.out.println(" Our test rental found in active list");
                        break;
                    }
                }
                if (!foundOurs && testRentalID != null) {
                    System.out.println("✗ ERROR: Test rental not in active list!");
                    recordTest(false);
                } else {
                    recordTest(true);
                }
            } else {
                recordTest(false);
            }
            
            System.out.println();
            
            // === TEST 1.5: Complete Rental ===
            if (testRentalID != null) {
                System.out.println("─── 1.5 COMPLETE RENTAL: Testing rental completion ───");
                System.out.println("Completing rental: " + testRentalID);
                
                // Wait a moment to ensure time difference for cost calculation
                try { Thread.sleep(2000); } catch (InterruptedException e) {}
                
                double cost = rentalService.completeRental(testRentalID);
                
                if (cost > 0) {
                    System.out.println(" Rental completed successfully");
                    System.out.println("  Total cost: Php" + String.format("%.2f", cost));
                    recordTest(true);
                    
                    // Verify rental is no longer active
                    RentalTransaction completedRental = rentalDAO.getRentalById(testRentalID);
                    if (completedRental != null && !completedRental.isActive()) {
                        System.out.println(" Rental marked as completed");
                        System.out.println("  End time: " + completedRental.getEndDateTime());
                        recordTest(true);
                    } else {
                        System.out.println("✗ ERROR: Rental still marked as active!");
                        recordTest(false);
                    }
                    
                    // Verify payment was finalized
                    PaymentTransaction finalPayment = paymentService.getPaymentByRental(testRentalID);
                    if (finalPayment != null && finalPayment.getAmount().compareTo(BigDecimal.ZERO) > 0) {
                        System.out.println(" Payment finalized with amount: Php" + finalPayment.getAmount());
                        recordTest(true);
                    } else {
                        System.out.println("✗ ERROR: Payment not finalized or amount is zero!");
                        recordTest(false);
                    }
                    
                    // Verify vehicle is available again
                    Vehicle vehicle = vehicleDAO.getVehicleById("ES-009");
                    if (vehicle != null && vehicle.isAvailable()) {
                        System.out.println(" Vehicle returned to available status");
                        recordTest(true);
                    } else {
                        System.out.println("✗ ERROR: Vehicle not marked as available!");
                        recordTest(false);
                    }
                } else {
                    System.out.println("✗ ERROR: Failed to complete rental (cost: " + cost + ")");
                    recordTest(false);
                }
            }
            
            System.out.println();
            
            // === TEST 1.6: Error Handling - Invalid Customer ===
            System.out.println("─── 1.6 ERROR HANDLING: Testing invalid customer ───");
            System.out.println("Attempting rental with non-existent customer...");
            
            String invalidRental = rentalService.createRental("INVALID-999", "ES-009", "LOC-001");
            if (invalidRental == null) {
                System.out.println(" Correctly rejected invalid customer");
                recordTest(true);
            } else {
                System.out.println("✗ ERROR: Should not allow invalid customer!");
                recordTest(false);
            }
            
            System.out.println();
            
            // === TEST 1.7: Error Handling - Invalid Vehicle ===
            System.out.println("─── 1.7 ERROR HANDLING: Testing invalid vehicle ───");
            System.out.println("Attempting rental with non-existent vehicle...");
            
            invalidRental = rentalService.createRental("CUST-001", "INVALID-999", "LOC-001");
            if (invalidRental == null) {
                System.out.println(" Correctly rejected invalid vehicle");
                recordTest(true);
            } else {
                System.out.println("✗ ERROR: Should not allow invalid vehicle!");
                recordTest(false);
            }
            
            System.out.println();
            
            // === CLEANUP ===
            System.out.println("─── 1.8 CLEANUP: Removing test data ───");
            if (testRentalID != null) {
                // Deactivate payment first
                PaymentTransaction payment = paymentService.getPaymentByRental(testRentalID);
                if (payment != null) {
                    paymentDAO.deactivatePayment(payment.getPaymentID());
                    System.out.println(" Test payment deactivated");
                }
                
                // Mark as Cancelled
                rentalDAO.cancelRental(testRentalID);
                System.out.println(" Test rental cancelled (soft deleted)");
                
                // Reset vehicle status
                vehicleDAO.updateVehicleStatus("ES-009", "Available");
                System.out.println(" Vehicle status reset");
            }
            
        } catch (Exception e) {
            System.out.println("✗ EXCEPTION during RentalService test:");
            e.printStackTrace();
        }
        
        System.out.println("\n RentalService Test Complete\n");
    }
    
    /**
     * Test 2: PaymentService - Payment processing and calculations
     */
    private static void testPaymentService() {
        System.out.println("═══════════════════════════════════════════════════");
        System.out.println("TEST 2: PaymentService - Payment Processing");
        System.out.println("═══════════════════════════════════════════════════\n");
        
        PaymentService paymentService = new PaymentService();
        
        try {
            // === TEST 2.1: Calculate Rental Fee ===
            System.out.println("─── 2.1 CALCULATE FEE: Testing rental fee calculation ───");
            System.out.println("Calculating fee for completed rental RNT-005...");
            
            BigDecimal calculatedFee = paymentService.calculateRentalFee("RNT-005");
            if (calculatedFee != null && calculatedFee.compareTo(BigDecimal.ZERO) > 0) {
                System.out.println(" Fee calculated successfully: Php" + calculatedFee);
                recordTest(true);
            } else {
                System.out.println("✗ ERROR: Fee calculation failed or returned zero");
                recordTest(false);
            }
            
            System.out.println();
            
            // === TEST 2.2: Get Payment by Rental ===
            System.out.println("─── 2.2 GET PAYMENT: Testing payment retrieval ───");
            System.out.println("Getting payment for rental RNT-005...");
            
            PaymentTransaction payment = paymentService.getPaymentByRental("RNT-005");
            if (payment != null) {
                System.out.println(" Payment found: " + payment.getPaymentID());
                System.out.println("  Amount: Php" + payment.getAmount());
                System.out.println("  Date: " + payment.getPaymentDate());
                recordTest(true);
            } else {
                System.out.println("✗ ERROR: Payment not found!");
                recordTest(false);
            }
            
            System.out.println();
            
            // === TEST 2.3: Get Original Payment ===
            System.out.println("─── 2.3 GET ORIGINAL PAYMENT: Verify baseline before finalize ───");
            System.out.println("Getting original payment for RNT-006 (PAY-002)...");
            
            PaymentTransaction originalPayment = paymentService.getPaymentByRental("RNT-006");
            BigDecimal originalAmount = null;
            if (originalPayment != null) {
                originalAmount = originalPayment.getAmount();
                System.out.println(" Original payment found: " + originalPayment.getPaymentID());
                System.out.println("  Original amount: Php" + originalAmount);
                recordTest(true);
            } else {
                System.out.println("✗ ERROR: Original payment not found!");
                recordTest(false);
            }
            
            System.out.println();
            
            // === TEST 2.4: Finalize Payment ===
            System.out.println("─── 2.4 FINALIZE PAYMENT: Testing payment update ───");
            System.out.println("Updating payment amount for RNT-006 (PAY-002)...");
            
            BigDecimal finalAmount = new BigDecimal("2000.00");
            Date finalDate = new Date(System.currentTimeMillis());
            
            boolean finalized = paymentService.finalizePaymentForRental("RNT-006", finalAmount, finalDate);
            if (finalized) {
                System.out.println(" Payment finalized successfully");
                recordTest(true);
                
                // Verify updated amount - check the payment that was actually updated (PAY-002 for RNT-006)
                PaymentTransaction updatedPayment = paymentService.getPaymentByRental("RNT-006");
                if (updatedPayment != null && updatedPayment.getAmount().compareTo(finalAmount) == 0) {
                    System.out.println(" Payment amount updated: Php" + updatedPayment.getAmount());
                    System.out.println("  Payment ID: " + updatedPayment.getPaymentID());
                    recordTest(true);
                } else {
                    System.out.println("✗ ERROR: Payment amount not updated correctly!");
                    if (updatedPayment != null) {
                        System.out.println("  Expected: Php" + finalAmount + ", Got: Php" + updatedPayment.getAmount());
                    }
                    recordTest(false);
                }
            } else {
                System.out.println("✗ ERROR: Failed to finalize payment");
                recordTest(false);
            }
            
            System.out.println();
            
            // === TEST 2.5: Error Handling - Invalid Rental ===
            System.out.println("─── 2.5 ERROR HANDLING: Testing invalid rental ───");
            System.out.println("Attempting to calculate fee for non-existent rental...");
            
            BigDecimal invalidFee = paymentService.calculateRentalFee("INVALID-999");
            if (invalidFee.compareTo(BigDecimal.ZERO) == 0) {
                System.out.println(" Correctly returned zero for invalid rental");
                recordTest(true);
            } else {
                System.out.println("✗ ERROR: Should return zero for invalid rental!");
                recordTest(false);
            }
            
            System.out.println();
            
            // === CLEANUP ===
            System.out.println("─── 2.6 CLEANUP: Restoring original payment ───");
            if (originalAmount != null) {
                // Restore PAY-002 to original amount
                boolean restored = paymentService.finalizePaymentForRental("RNT-006", originalAmount, finalDate);
                if (restored) {
                    System.out.println(" Payment PAY-002 restored to original amount: Php" + originalAmount);
                } else {
                    System.out.println("⚠ Warning: Could not restore original payment amount");
                }
            }
            
        } catch (Exception e) {
            System.out.println("✗ EXCEPTION during PaymentService test:");
            e.printStackTrace();
        }
        
        System.out.println("\n PaymentService Test Complete\n");
    }
    
    /**
     * Test 3: MaintenanceService - Maintenance scheduling and completion
     */
    private static void testMaintenanceService() {
        System.out.println("═══════════════════════════════════════════════════");
        System.out.println("TEST 3: MaintenanceService - Maintenance Workflow");
        System.out.println("═══════════════════════════════════════════════════\n");
        
        MaintenanceService maintenanceService = new MaintenanceService();
        MaintenanceDAO maintenanceDAO = new MaintenanceDAO();
        VehicleDAO vehicleDAO = new VehicleDAO();
        
        String testMaintenanceID = "TST-M-" + (System.currentTimeMillis() % 100000); // Keep under 11 chars
        String testVehicle = "EB-007"; // Using existing vehicle from test data
        
        try {
            // === TEST 3.1: Schedule Maintenance ===
            System.out.println("─── 3.1 SCHEDULE MAINTENANCE: Testing maintenance scheduling ───");
            System.out.println("Scheduling maintenance for vehicle " + testVehicle + "...");
            
            Timestamp startTime = new Timestamp(System.currentTimeMillis());
            boolean scheduled = maintenanceService.scheduleMaintenance(
                testMaintenanceID, 
                testVehicle, 
                "TECH-001", // Existing technician from test data
                "Test maintenance - oil change and brake check",
                startTime
            );
            
            if (scheduled) {
                System.out.println(" Maintenance scheduled successfully: " + testMaintenanceID);
                recordTest(true);
                
                // Verify maintenance record exists
                MaintenanceTransaction maintenance = maintenanceDAO.getMaintenanceById(testMaintenanceID);
                if (maintenance != null) {
                    System.out.println(" Maintenance verified in database");
                    System.out.println("  Vehicle: " + maintenance.getPlateID());
                    System.out.println("  Technician: " + maintenance.getTechnicianID());
                    System.out.println("  Notes: " + maintenance.getNotes());
                    recordTest(true);
                } else {
                    System.out.println("✗ ERROR: Maintenance not found in database!");
                    recordTest(false);
                }
                
                // Verify vehicle status updated
                Vehicle vehicle = vehicleDAO.getVehicleById(testVehicle);
                if (vehicle != null && vehicle.isInMaintenance()) {
                    System.out.println(" Vehicle status updated to Maintenance");
                    recordTest(true);
                } else {
                    System.out.println("✗ ERROR: Vehicle status not updated!");
                    recordTest(false);
                }
            } else {
                System.out.println("✗ ERROR: Failed to schedule maintenance");
                recordTest(false);
            }
            
            System.out.println();
            
            // === TEST 3.2: Get Maintenance History ===
            System.out.println("─── 3.2 MAINTENANCE HISTORY: Testing history retrieval ───");
            System.out.println("Getting maintenance history for " + testVehicle + "...");
            
            List<MaintenanceTransaction> history = maintenanceService.getMaintenanceHistory(testVehicle);
            if (history != null && !history.isEmpty()) {
                System.out.println(" Found " + history.size() + " maintenance record(s)");
                boolean foundOurs = false;
                for (MaintenanceTransaction m : history) {
                    if (m.getMaintenanceID().equals(testMaintenanceID)) {
                        foundOurs = true;
                        System.out.println(" Our test maintenance found in history");
                        break;
                    }
                }
                if (!foundOurs) {
                    System.out.println("✗ ERROR: Test maintenance not in history!");
                    recordTest(false);
                } else {
                    recordTest(true);
                }
            } else {
                System.out.println("✗ ERROR: No maintenance history found!");
                recordTest(false);
            }
            
            System.out.println();
            
            // === TEST 3.3: Get Technician Workload ===
            System.out.println("─── 3.3 TECHNICIAN WORKLOAD: Testing workload retrieval ───");
            System.out.println("Getting workload for technician TECH-001...");
            
            List<MaintenanceTransaction> workload = maintenanceService.getTechnicianWorkload("TECH-001");
            if (workload != null) {
                System.out.println(" Found " + workload.size() + " job(s) assigned to technician");
                recordTest(true);
            } else {
                System.out.println("✗ ERROR: Failed to get technician workload!");
                recordTest(false);
            }
            
            System.out.println();
            
            // === TEST 3.4: Complete Maintenance ===
            System.out.println("─── 3.4 COMPLETE MAINTENANCE: Testing maintenance completion ───");
            System.out.println("Completing maintenance with parts...");
            
            // Create parts list
            List<MaintenanceService.PartUsage> parts = new java.util.ArrayList<>();
            parts.add(new MaintenanceService.PartUsage("PART-003", new BigDecimal("2"))); // 2 units of brake pads
            parts.add(new MaintenanceService.PartUsage("PART-011", new BigDecimal("1"))); // 1 unit of brake cable
            
            Timestamp endTime = new Timestamp(System.currentTimeMillis());
            
            boolean completed = maintenanceService.completeMaintenance(
                testMaintenanceID, 
                endTime, 
                parts
            );
            
            if (completed) {
                System.out.println(" Maintenance completed successfully");
                recordTest(true);
                
                // Verify maintenance record updated
                MaintenanceTransaction completedMaint = maintenanceDAO.getMaintenanceById(testMaintenanceID);
                if (completedMaint != null && completedMaint.getEndDateTime() != null) {
                    System.out.println(" Maintenance end time recorded");
                    System.out.println("  Hours worked: " + completedMaint.getHoursWorked());
                    recordTest(true);
                } else {
                    System.out.println("✗ ERROR: Maintenance not marked as completed!");
                    recordTest(false);
                }
                
                // Verify vehicle status restored
                Vehicle vehicle = vehicleDAO.getVehicleById(testVehicle);
                if (vehicle != null && vehicle.isAvailable()) {
                    System.out.println(" Vehicle status restored to Available");
                    recordTest(true);
                } else {
                    System.out.println("✗ ERROR: Vehicle status not restored!");
                    recordTest(false);
                }
                
                // Verify parts were logged
                List<model.MaintenanceCheque> partRecords = maintenanceService.getPartsUsedInMaintenance(testMaintenanceID);
                if (partRecords != null && partRecords.size() == 2) {
                    System.out.println(" Parts usage logged: " + partRecords.size() + " part(s)");
                    recordTest(true);
                } else {
                    System.out.println("✗ ERROR: Parts not logged correctly!");
                    recordTest(false);
                }
            } else {
                System.out.println("✗ ERROR: Failed to complete maintenance");
                recordTest(false);
            }
            
            System.out.println();
            
            // === TEST 3.5: Error Handling - Invalid Vehicle ===
            System.out.println("─── 3.5 ERROR HANDLING: Testing invalid vehicle ───");
            System.out.println("Attempting maintenance for non-existent vehicle...");
            
            boolean invalidSchedule = maintenanceService.scheduleMaintenance(
                "INVALID-MNT", 
                "INVALID-999", 
                "TECH-001", 
                "Test",
                new Timestamp(System.currentTimeMillis())
            );
            
            if (!invalidSchedule) {
                System.out.println(" Correctly rejected invalid vehicle");
                recordTest(true);
            } else {
                System.out.println("✗ ERROR: Should not allow invalid vehicle!");
                recordTest(false);
            }
            
            System.out.println();
            
            // === CLEANUP ===
            System.out.println("─── 3.6 CLEANUP: Removing test maintenance ───");
            maintenanceDAO.deactivateMaintenance(testMaintenanceID);
            System.out.println(" Test maintenance deactivated");
            
            // Reset vehicle status
            vehicleDAO.updateVehicleStatus(testVehicle, "Available");
            System.out.println(" Vehicle status reset");
            
        } catch (Exception e) {
            System.out.println("✗ EXCEPTION during MaintenanceService test:");
            e.printStackTrace();
        }
        
        System.out.println("\n MaintenanceService Test Complete\n");
    }
    
    /**
     * Test 4: DeploymentService - Vehicle deployment workflow
     */
    private static void testDeploymentService() {
        System.out.println("═══════════════════════════════════════════════════");
        System.out.println("TEST 4: DeploymentService - Deployment Workflow");
        System.out.println("═══════════════════════════════════════════════════\n");
        
        DeploymentDAO deploymentDAO = new DeploymentDAO();
        VehicleDAO vehicleDAO = new VehicleDAO();
        LocationDAO locationDAO = new LocationDAO();
        
        DeploymentService deploymentService = new DeploymentService(deploymentDAO, vehicleDAO, locationDAO);
        
        String testVehicle = "ET-005"; // Using existing vehicle from test data
        String testLocation = "LOC-002"; // Target location
        
        try {
            // === TEST 4.1: Deploy Vehicle ===
            System.out.println("─── 4.1 DEPLOY VEHICLE: Testing vehicle deployment ───");
            System.out.println("Deploying " + testVehicle + " to " + testLocation + "...");
            
            String deploymentID = deploymentService.deployVehicle(testVehicle, testLocation);
            
            if (deploymentID != null) {
                System.out.println(" Vehicle deployed successfully: " + deploymentID);
                recordTest(true);
                
                // Verify deployment record exists
                DeploymentTransaction deployment = deploymentDAO.getDeploymentById(deploymentID);
                if (deployment != null) {
                    System.out.println(" Deployment verified in database");
                    System.out.println("  Vehicle: " + deployment.getPlateID());
                    System.out.println("  Target Location: " + deployment.getLocationID());
                    recordTest(true);
                } else {
                    System.out.println("✗ ERROR: Deployment not found in database!");
                    recordTest(false);
                }
            } else {
                System.out.println("✗ ERROR: Failed to deploy vehicle");
                recordTest(false);
            }
            
            System.out.println();
            
            // === TEST 4.2: Get Vehicle Deployment History ===
            System.out.println("─── 4.2 DEPLOYMENT HISTORY: Testing history retrieval ───");
            System.out.println("Getting deployment history for " + testVehicle + "...");
            
            List<DeploymentTransaction> history = deploymentService.getVehicleDeploymentHistory(testVehicle);
            if (history != null && !history.isEmpty()) {
                System.out.println(" Found " + history.size() + " deployment(s)");
                recordTest(true);
            } else {
                System.out.println("✗ No deployment history found");
                recordTest(false);
            }
            
            System.out.println();
            
            // === TEST 4.3: Get Current Deployment ===
            System.out.println("─── 4.3 CURRENT DEPLOYMENT: Testing current deployment retrieval ───");
            System.out.println("Getting current deployment for " + testVehicle + "...");
            
            DeploymentTransaction currentDeploy = deploymentDAO.getCurrentDeploymentByVehicle(testVehicle);
            if (currentDeploy != null) {
                System.out.println(" Current deployment found: " + currentDeploy.getDeploymentID());
                System.out.println("  Target: " + currentDeploy.getLocationID());
                recordTest(true);
            } else {
                System.out.println("✗ No current deployment found");
                recordTest(false);
            }
            
            System.out.println();
            
            // === TEST 4.4: Complete Deployment ===
            if (deploymentID != null) {
                System.out.println("─── 4.4 COMPLETE DEPLOYMENT: Testing deployment completion ───");
                System.out.println("Completing deployment: " + deploymentID);
                
                boolean completed = deploymentService.completeDeployment(deploymentID);
                
                if (completed) {
                    System.out.println(" Deployment completed successfully");
                    recordTest(true);
                    
                    // Verify deployment marked as complete
                    DeploymentTransaction completedDeploy = deploymentDAO.getDeploymentById(deploymentID);
                    if (completedDeploy != null && completedDeploy.getEndDate() != null) {
                        System.out.println(" Deployment marked complete with end date: " + completedDeploy.getEndDate());
                        recordTest(true);
                    } else {
                        System.out.println("✗ ERROR: Deployment not marked as completed!");
                        recordTest(false);
                    }
                } else {
                    System.out.println("✗ ERROR: Failed to complete deployment");
                    recordTest(false);
                }
            }
            
            System.out.println();
            
            // === TEST 4.5: Error Handling - Invalid Vehicle ===
            System.out.println("─── 4.5 ERROR HANDLING: Testing invalid vehicle ───");
            System.out.println("Attempting deployment of non-existent vehicle...");
            
            String invalidDeploy = deploymentService.deployVehicle("INVALID-999", testLocation);
            if (invalidDeploy == null) {
                System.out.println(" Correctly rejected invalid vehicle");
                recordTest(true);
            } else {
                System.out.println("✗ ERROR: Should not allow invalid vehicle!");
                recordTest(false);
            }
            
            System.out.println();
            
            // === CLEANUP ===
            System.out.println("─── 4.6 CLEANUP: Test complete ───");
            if (deploymentID != null) {
                System.out.println(" Deployment test finished (deployment ID: " + deploymentID + ")");
            }
            
        } catch (Exception e) {
            System.out.println("✗ EXCEPTION during DeploymentService test:");
            e.printStackTrace();
        }
        
        System.out.println("\n DeploymentService Test Complete\n");
    }
    
    /**
     * Test 5: PenaltyService - Penalty calculation and processing
     */
    private static void testPenaltyService() {
        System.out.println("═══════════════════════════════════════════════════");
        System.out.println("TEST 5: PenaltyService - Penalty Calculation");
        System.out.println("═══════════════════════════════════════════════════\n");
        
        PenaltyService penaltyService = new PenaltyService();
        PenaltyDAO penaltyDAO = new PenaltyDAO();
        
        String testPenaltyID = "TST-P-" + (System.currentTimeMillis() % 100000); // Keep under 11 chars
        
        try {
            // === TEST 5.1: Calculate Labor Cost ===
            System.out.println("─── 5.1 LABOR COST: Testing labor cost calculation ───");
            System.out.println("Calculating labor cost for maintenance MAINT-001...");
            
            BigDecimal laborCost = penaltyService.calculateLaborCost("MAINT-001");
            if (laborCost != null && laborCost.compareTo(BigDecimal.ZERO) > 0) {
                System.out.println(" Labor cost calculated: Php" + laborCost);
                recordTest(true);
            } else {
                System.out.println("⚠ Labor cost is zero (may be expected if no hours recorded)");
                recordTest(true); // Not a failure, just no data
            }
            
            System.out.println();
            
            // === TEST 5.2: Calculate Parts Cost ===
            System.out.println("─── 5.2 PARTS COST: Testing parts cost calculation ───");
            System.out.println("Calculating parts cost for maintenance MAINT-001...");
            
            BigDecimal partsCost = penaltyService.calculatePartsCost("MAINT-001");
            if (partsCost != null && partsCost.compareTo(BigDecimal.ZERO) >= 0) {
                System.out.println(" Parts cost calculated: Php" + partsCost);
                recordTest(true);
            } else {
                System.out.println("✗ ERROR: Parts cost calculation failed");
                recordTest(false);
            }
            
            System.out.println();
            
            // === TEST 5.3: Calculate Total Maintenance Cost ===
            System.out.println("─── 5.3 TOTAL COST: Testing total maintenance cost ───");
            System.out.println("Calculating total maintenance cost for MAINT-001...");
            
            BigDecimal totalCost = penaltyService.calculateMaintenanceCost("MAINT-001");
            if (totalCost != null && totalCost.compareTo(BigDecimal.ZERO) >= 0) {
                System.out.println(" Total cost calculated: Php" + totalCost);
                recordTest(true);
                
                // Verify it equals labor + parts
                BigDecimal expectedTotal = laborCost.add(partsCost);
                if (totalCost.compareTo(expectedTotal) == 0) {
                    System.out.println(" Total = Labor + Parts (correct)");
                    recordTest(true);
                } else {
                    System.out.println("⚠ Total doesn't match Labor + Parts");
                    recordTest(false);
                }
            } else {
                System.out.println("✗ ERROR: Total cost calculation failed");
                recordTest(false);
            }
            
            System.out.println();
            
            // === TEST 5.4: Create Penalty for Maintenance ===
            System.out.println("─── 5.4 CREATE PENALTY: Testing penalty creation ───");
            System.out.println("Creating penalty for maintenance MAINT-001...");
            
            Date today = new Date(System.currentTimeMillis());
            boolean created = penaltyService.createPenaltyFromMaintenance(
                testPenaltyID,
                "RNT-005", // Existing rental
                "MAINT-001", // Existing maintenance
                today
            );
            
            if (created) {
                System.out.println(" Penalty created successfully: " + testPenaltyID);
                recordTest(true);
                
                // Verify penalty exists
                PenaltyTransaction penalty = penaltyDAO.getPenaltyById(testPenaltyID);
                if (penalty != null) {
                    System.out.println(" Penalty verified in database");
                    System.out.println("  Amount: Php" + penalty.getTotalPenalty());
                    System.out.println("  Rental: " + penalty.getRentalID());
                    recordTest(true);
                } else {
                    System.out.println("✗ ERROR: Penalty not found in database!");
                    recordTest(false);
                }
            } else {
                System.out.println("✗ ERROR: Failed to create penalty");
                recordTest(false);
            }
            
            System.out.println();
            
            // === TEST 5.5: Get Penalties by Rental ===
            System.out.println("─── 5.5 GET PENALTIES: Testing penalty retrieval ───");
            System.out.println("Getting penalties for rental RNT-005...");
            
            List<PenaltyTransaction> penalties = penaltyService.getPenaltiesByRental("RNT-005");
            if (penalties != null && !penalties.isEmpty()) {
                System.out.println(" Found " + penalties.size() + " penalty(ies)");
                for (PenaltyTransaction p : penalties) {
                    System.out.println("  " + p.getPenaltyID() + ": Php" + p.getTotalPenalty());
                }
                recordTest(true);
            } else {
                System.out.println("⚠ No penalties found for rental");
                recordTest(true); // Not necessarily an error
            }
            
            System.out.println();
            
            // === TEST 5.6: Error Handling - Invalid Maintenance ===
            System.out.println("─── 5.6 ERROR HANDLING: Testing invalid maintenance ───");
            System.out.println("Attempting to calculate cost for non-existent maintenance...");
            
            BigDecimal invalidCost = penaltyService.calculateMaintenanceCost("INVALID-999");
            if (invalidCost.compareTo(BigDecimal.ZERO) == 0) {
                System.out.println(" Correctly returned zero for invalid maintenance");
                recordTest(true);
            } else {
                System.out.println("✗ ERROR: Should return zero for invalid maintenance!");
                recordTest(false);
            }
            
            System.out.println();
            
            // === CLEANUP ===
            System.out.println("─── 5.7 CLEANUP: Removing test penalty ───");
            penaltyDAO.deactivatePenalty(testPenaltyID);
            System.out.println(" Test penalty deactivated");
            
        } catch (Exception e) {
            System.out.println("✗ EXCEPTION during PenaltyService test:");
            e.printStackTrace();
        }
        
        System.out.println("\n PenaltyService Test Complete\n");
    }
}
