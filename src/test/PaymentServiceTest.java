package test;

import service.PaymentService;
import model.PaymentTransaction;
import java.math.BigDecimal;
import java.sql.Date;

/**
 * PAYMENT SERVICE TEST CLASS
 * 
 * PURPOSE: Testing of simplified PaymentService functionality:
 * - Payment processing (one payment per rental)
 * - Rental fee calculation
 * - Payment retrieval
 * 
 * BUSINESS MODEL:
 * - Each rental has exactly ONE payment transaction
 * - Payment is processed when rental ends
 * - Payment amount should match rental fee
 * 
 * PREREQUISITES:
 * 1. MySQL database 'vehicle_rental_db' must exist
 * 2. test_data.sql must be loaded (rentals RNT-001 to RNT-015)
 * 3. db.properties file configured
 * 4. Existing rentals with start/end times
 * 
 * TEST STRATEGY:
 * - Uses EXISTING rentals from test_data.sql (RNT-001 to RNT-015)
 * - test_data.sql has PAY-001 to PAY-011 for rentals RNT-005 to RNT-015
 * - Creates NEW test payment PAY-012 for testing
 * - Tests fee calculation, payment processing, and retrieval
 * 
 * CLEANUP:
 * After running tests, execute: DELETE FROM payments WHERE paymentID = 'PAY-012';
 * 
 * HOW TO RUN:
 * 1. Make sure test_data.sql is loaded
 * 2. Right-click this file → Run As → Java Application
 * 3. Check console output for :) or :( indicators
 * 4. Run cleanup SQL command after testing
 */
public class PaymentServiceTest {
    
    private static PaymentService paymentService;
    private static int passCount = 0;
    private static int failCount = 0;
    
    public static void main(String[] args) {
        System.out.println("═══════════════════════════════════════════════════");
        System.out.println("   PAYMENT SERVICE TEST SUITE");
        System.out.println("   (Simplified: One Payment Per Rental)");
        System.out.println("═══════════════════════════════════════════════════\n");
        
        paymentService = new PaymentService();
        
        // Test suite for simplified payment service
        testCalculateRentalFee_CompletedRentals();
        testCalculateRentalFee_OngoingRentals();
        testCalculateRentalFee_MultipleRentals();
        testProcessPayment_ValidPayment();
        testProcessPayment_InvalidAmount();
        testProcessPayment_InvalidRental();
        testGetPaymentByRental_ExistingPayment();
        testGetPaymentByRental_NoPayment();
        
        // Summary
        printSummary();
    }
    
    /**
     * Test rental fee calculation for completed rentals
     * Uses RNT-005: 2.75 hours at ₱50/day → ₱5.73
     */
    private static void testCalculateRentalFee_CompletedRentals() {
        System.out.println("\n─────────────────────────────────────────────────");
        System.out.println("TEST 1: Calculate Rental Fee - Completed Rentals");
        System.out.println("─────────────────────────────────────────────────");
        
        try {
            // RNT-005: ES-001 (₱50/day), 2024-10-27 09:00 to 11:45 (2h 45m = 2.75h)
            BigDecimal fee = paymentService.calculateRentalFee("RNT-005");
            
            // Expected: (2.75 hours / 24) * 50 = 5.729166... → rounds to 5.73
            BigDecimal expected = new BigDecimal("5.73");
            
            // Allow small rounding difference
            BigDecimal difference = fee.subtract(expected).abs();
            BigDecimal tolerance = new BigDecimal("0.01");
            
            if (difference.compareTo(tolerance) <= 0) {
                System.out.println(":) PASS: Fee calculated correctly for RNT-005");
                System.out.println("   Expected: ₱" + expected + ", Got: ₱" + fee);
                passCount++;
            } else {
                System.out.println(":( FAIL: Incorrect fee for RNT-005");
                System.out.println("   Expected: ₱" + expected + ", Got: ₱" + fee);
                failCount++;
            }
            
        } catch (Exception e) {
            System.out.println(":( FAIL: Exception - " + e.getMessage());
            failCount++;
        }
    }
    
    /**
     * Test rental fee calculation for ongoing rentals
     * Uses RNT-001: Ongoing rental (should calculate up to current time)
     */
    private static void testCalculateRentalFee_OngoingRentals() {
        System.out.println("\n─────────────────────────────────────────────────");
        System.out.println("TEST 2: Calculate Rental Fee - Ongoing Rentals");
        System.out.println("─────────────────────────────────────────────────");
        
        try {
            // RNT-001: ES-003 (₱50/day), started 2024-10-28 09:00, no end time
            BigDecimal fee = paymentService.calculateRentalFee("RNT-001");
            
            // Should return a positive value (ongoing rental)
            if (fee.compareTo(BigDecimal.ZERO) > 0) {
                System.out.println(":) PASS: Fee calculated for ongoing rental RNT-001");
                System.out.println("   Current fee: ₱" + fee);
                passCount++;
            } else {
                System.out.println(":( FAIL: Fee should be positive for ongoing rental");
                failCount++;
            }
            
        } catch (Exception e) {
            System.out.println(":( FAIL: Exception - " + e.getMessage());
            failCount++;
        }
    }
    
    /**
     * Test rental fee calculation for multiple different rentals
     * Verifies fees are calculated correctly for different vehicle types
     */
    private static void testCalculateRentalFee_MultipleRentals() {
        System.out.println("\n─────────────────────────────────────────────────");
        System.out.println("TEST 3: Calculate Rental Fee - Multiple Rentals");
        System.out.println("─────────────────────────────────────────────────");
        
        try {
            // Test RNT-006: EB-001 (₱80/day), 3.5 hours → ₱11.67
            BigDecimal fee1 = paymentService.calculateRentalFee("RNT-006");
            BigDecimal expected1 = new BigDecimal("11.67");
            BigDecimal diff1 = fee1.subtract(expected1).abs();
            
            // Test RNT-008: ET-001 (₱100/day), 3 hours → ₱12.50
            BigDecimal fee2 = paymentService.calculateRentalFee("RNT-008");
            BigDecimal expected2 = new BigDecimal("12.50");
            BigDecimal diff2 = fee2.subtract(expected2).abs();
            
            BigDecimal tolerance = new BigDecimal("0.01");
            
            if (diff1.compareTo(tolerance) <= 0 && diff2.compareTo(tolerance) <= 0) {
                System.out.println(":) PASS: Fees calculated correctly for multiple rentals");
                System.out.println("   RNT-006: ₱" + fee1 + " (expected ₱" + expected1 + ")");
                System.out.println("   RNT-008: ₱" + fee2 + " (expected ₱" + expected2 + ")");
                passCount++;
            } else {
                System.out.println(":( FAIL: Incorrect fees for one or more rentals");
                failCount++;
            }
            
        } catch (Exception e) {
            System.out.println(":( FAIL: Exception - " + e.getMessage());
            failCount++;
        }
    }
    
    /**
     * Test processing a valid payment for a rental
     * Creates PAY-012 for an ongoing rental (RNT-002) - simulates payment at rental end
     */
    private static void testProcessPayment_ValidPayment() {
        System.out.println("\n─────────────────────────────────────────────────");
        System.out.println("TEST 4: Process Payment - Valid Payment");
        System.out.println("─────────────────────────────────────────────────");
        
        try {
            // Use RNT-002 which is ongoing and has no payment
            // Calculate current rental fee
            BigDecimal rentalFee = paymentService.calculateRentalFee("RNT-002");
            
            // Process payment (simulates customer paying when they return vehicle)
            boolean success = paymentService.processPayment(
                "PAY-012", 
                "RNT-002", 
                rentalFee,
                Date.valueOf("2024-11-04")  // Payment made today
            );
            
            if (success) {
                // Verify payment was created
                PaymentTransaction payment = paymentService.getPaymentByRental("RNT-002");
                
                if (payment != null && "PAY-012".equals(payment.getPaymentID())) {
                    System.out.println(":) PASS: Payment processed and retrieved successfully");
                    System.out.println("   Payment ID: " + payment.getPaymentID());
                    System.out.println("   Amount: ₱" + payment.getAmount());
                    passCount++;
                } else {
                    System.out.println(":( FAIL: Payment not found after processing");
                    System.out.println("   Expected: PAY-012, Got: " + (payment != null ? payment.getPaymentID() : "null"));
                    failCount++;
                }
            } else {
                System.out.println(":( FAIL: Payment processing failed");
                failCount++;
            }
            
        } catch (Exception e) {
            System.out.println(":( FAIL: Exception - " + e.getMessage());
            failCount++;
        }
    }
    
    /**
     * Test payment validation - invalid amount
     */
    private static void testProcessPayment_InvalidAmount() {
        System.out.println("\n─────────────────────────────────────────────────");
        System.out.println("TEST 5: Process Payment - Invalid Amount");
        System.out.println("─────────────────────────────────────────────────");
        
        try {
            // Try to process negative amount
            boolean success = paymentService.processPayment(
                "PAY-999", 
                "RNT-013", 
                new BigDecimal("-100.00"),
                Date.valueOf("2024-10-27")
            );
            
            if (!success) {
                System.out.println(":) PASS: Negative amount rejected correctly");
                passCount++;
            } else {
                System.out.println(":( FAIL: Should reject negative amounts");
                failCount++;
            }
            
        } catch (Exception e) {
            System.out.println(":( FAIL: Exception - " + e.getMessage());
            failCount++;
        }
    }
    
    /**
     * Test payment validation - invalid rental ID
     */
    private static void testProcessPayment_InvalidRental() {
        System.out.println("\n─────────────────────────────────────────────────");
        System.out.println("TEST 6: Process Payment - Invalid Rental ID");
        System.out.println("─────────────────────────────────────────────────");
        
        try {
            boolean success = paymentService.processPayment(
                "PAY-998", 
                "RNT-999", // Non-existent rental
                new BigDecimal("100.00"),
                Date.valueOf("2024-10-27")
            );
            
            if (!success) {
                System.out.println(":) PASS: Invalid rental ID rejected correctly");
                passCount++;
            } else {
                System.out.println(":( FAIL: Should reject invalid rental IDs");
                failCount++;
            }
            
        } catch (Exception e) {
            System.out.println(":( FAIL: Exception - " + e.getMessage());
            failCount++;
        }
    }
    
    /**
     * Test retrieving payment for a rental with existing payment
     * Uses RNT-005 which has PAY-001 in test_data.sql
     */
    private static void testGetPaymentByRental_ExistingPayment() {
        System.out.println("\n─────────────────────────────────────────────────");
        System.out.println("TEST 7: Get Payment by Rental - Existing Payment");
        System.out.println("─────────────────────────────────────────────────");
        
        try {
            // RNT-005 has PAY-001 in test_data.sql
            PaymentTransaction payment = paymentService.getPaymentByRental("RNT-005");
            
            if (payment != null) {
                System.out.println(":) PASS: Payment retrieved successfully");
                System.out.println("   Payment ID: " + payment.getPaymentID());
                System.out.println("   Amount: ₱" + payment.getAmount());
                System.out.println("   Rental ID: " + payment.getRentalID());
                passCount++;
            } else {
                System.out.println(":( FAIL: Expected to find payment for RNT-005");
                failCount++;
            }
            
        } catch (Exception e) {
            System.out.println(":( FAIL: Exception - " + e.getMessage());
            failCount++;
        }
    }
    
    /**
     * Test retrieving payment for a rental with no payment
     * Uses RNT-001 which is ongoing and should not have payment yet
     */
    private static void testGetPaymentByRental_NoPayment() {
        System.out.println("\n─────────────────────────────────────────────────");
        System.out.println("TEST 8: Get Payment by Rental - No Payment");
        System.out.println("─────────────────────────────────────────────────");
        
        try {
            // RNT-001 is ongoing and should not have payment
            PaymentTransaction payment = paymentService.getPaymentByRental("RNT-001");
            
            if (payment == null) {
                System.out.println(":) PASS: No payment found for ongoing rental (expected)");
                passCount++;
            } else {
                System.out.println(":( FAIL: Should not have payment for ongoing rental");
                failCount++;
            }
            
        } catch (Exception e) {
            System.out.println(":( FAIL: Exception - " + e.getMessage());
            failCount++;
        }
    }
    
    /**
     * Print test summary
     */
    private static void printSummary() {
        System.out.println("\n═══════════════════════════════════════════════════");
        System.out.println("   TEST SUMMARY");
        System.out.println("═══════════════════════════════════════════════════");
        System.out.println("Total Tests: " + (passCount + failCount));
        System.out.println("Passed:      " + passCount + " :)");
        System.out.println("Failed:      " + failCount + " :(");
        
        double successRate = (passCount * 100.0) / (passCount + failCount);
        System.out.println("Success Rate: " + String.format("%.1f%%", successRate));
        System.out.println("═══════════════════════════════════════════════════");
        
        if (failCount == 0) {
            System.out.println("\n🎉 ALL TESTS PASSED! 🎉");
        } else {
            System.out.println("\n⚠️ SOME TESTS FAILED - Review output above");
        }
        
        System.out.println("\n📝 CLEANUP INSTRUCTIONS:");
        System.out.println("Run the following SQL commands to clean up test payments:");
        System.out.println("See cleanup_payment_test.sql or run commands manually");
        System.out.println("═══════════════════════════════════════════════════\n");
    }
}
