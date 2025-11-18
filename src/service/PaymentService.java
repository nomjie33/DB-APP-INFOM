package service;

import dao.*;
import model.*;

/**
 * Business Logic Service for PAYMENT operations.
 * 
 * PURPOSE: Handles payment processing and rental fee calculations.
 * 
 * PAYMENT WORKFLOW:
 * 1. Customer rents vehicle → rental created
 * 2. Calculate rental fee based on time and vehicle rate
 * 3. Process one-time payment for the rental
 * 
 * BUSINESS MODEL ASSUMPTION:
 * - Each rental requires ONE payment transaction
 * - Payment is processed when rental ends
 * - No partial payments or installments
 * 
 * FEE CALCULATIONS:
 * - Base Fee = (hours rented / 24) × daily rate
 * - Minimum charge = 1 hour
 * - Hourly rate = daily rate / 24
 */
public class PaymentService {
    
    private PaymentDAO paymentDAO;
    private RentalDAO rentalDAO;
    private VehicleDAO vehicleDAO;
    
    public PaymentService() {
        this.paymentDAO = new PaymentDAO();
        this.rentalDAO = new RentalDAO();
        this.vehicleDAO = new VehicleDAO();
    }
    
    /**
     * Process a payment for a rental.
     * Creates a payment record in the database.
     * 
     * BUSINESS RULE: One payment per rental transaction
     * 
     * @param paymentID Unique payment identifier (e.g., "PAY-001")
     * @param rentalID Rental this payment is for
     * @param amount Payment amount
     * @param paymentDate Date of payment
     * @return true if payment processed successfully
     */
    public boolean processPayment(String paymentID, String rentalID, java.math.BigDecimal amount, java.sql.Date paymentDate) {
        System.out.println("\n=== PROCESSING PAYMENT ===");
        System.out.println("Payment ID: " + paymentID);
        System.out.println("Rental ID: " + rentalID);
        System.out.println("Amount: ₱" + amount);
        
        try {
            // Validate amount
            if (amount == null || amount.compareTo(java.math.BigDecimal.ZERO) <= 0) {
                System.out.println(":( Invalid payment amount");
                return false;
            }
            
            // Validate rental exists
            RentalTransaction rental = rentalDAO.getRentalById(rentalID);
            if (rental == null) {
                System.out.println(":( Rental not found: " + rentalID);
                return false;
            }
            
            // Create payment record
            PaymentTransaction payment = new PaymentTransaction(paymentID, amount, rentalID, paymentDate);
            boolean success = paymentDAO.insertPayment(payment);
            
            if (success) {
                System.out.println(":) Payment processed successfully");
                System.out.println("Receipt: " + paymentID);
            } else {
                System.out.println(":( Failed to process payment");
            }
            
            return success;
            
        } catch (Exception e) {
            System.out.println(":( Error processing payment: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
    
    /**
     * Calculate rental fee based on time rented and vehicle rate.
     * Formula: (hours rented ÷ 24) × daily rate
     * 
     * @param rentalID The rental to calculate fee for
     * @return Total rental fee
     */
    public java.math.BigDecimal calculateRentalFee(String rentalID) {
        System.out.println("\n=== CALCULATING RENTAL FEE ===");
        System.out.println("Rental ID: " + rentalID);
        
        try {
            // Get rental details
            RentalTransaction rental = rentalDAO.getRentalById(rentalID);
            if (rental == null) {
                System.out.println(":( Rental not found");
                return java.math.BigDecimal.ZERO;
            }
            
            // Get vehicle rate
            Vehicle vehicle = vehicleDAO.getVehicleById(rental.getPlateID());
            if (vehicle == null) {
                System.out.println(":( Vehicle not found");
                return java.math.BigDecimal.ZERO;
            }
            
            java.math.BigDecimal dailyRate = java.math.BigDecimal.valueOf(vehicle.getRentalPrice());
            
            // Calculate rental duration
            java.sql.Timestamp startDateTime = rental.getStartDateTime();
            java.sql.Timestamp endDateTime = rental.getEndDateTime();
            
            if (startDateTime == null) {
                System.out.println(":( Start time not set");
                return java.math.BigDecimal.ZERO;
            }
            
            // If rental not ended, use current time
            if (endDateTime == null) {
                endDateTime = new java.sql.Timestamp(System.currentTimeMillis());
                System.out.println("(Rental ongoing, calculating up to current time)");
            }
            
            // Calculate hours
            long durationMillis = endDateTime.getTime() - startDateTime.getTime();
            
            // Calculate fractional hours (not rounded)
            double hoursDecimal = durationMillis / (1000.0 * 60.0 * 60.0);
            
            // Minimum 1 hour charge
            if (hoursDecimal < 1.0) {
                hoursDecimal = 1.0;
            }
            
            // Calculate fee: (hours / 24) * daily rate
            // Use high precision for calculation, then round final result
            java.math.BigDecimal hours = java.math.BigDecimal.valueOf(hoursDecimal);
            java.math.BigDecimal hoursPerDay = java.math.BigDecimal.valueOf(24);
            java.math.BigDecimal rentalFee = hours.divide(hoursPerDay, 10, java.math.RoundingMode.HALF_UP)
                                                   .multiply(dailyRate)
                                                   .setScale(2, java.math.RoundingMode.HALF_UP);
            
            java.math.BigDecimal days = hours.divide(hoursPerDay, 2, java.math.RoundingMode.HALF_UP);
            System.out.println("Duration: " + String.format("%.2f", hoursDecimal) + " hours (" + days + " days)");
            System.out.println("Daily Rate: ₱" + dailyRate);
            System.out.println("Rental Fee: ₱" + rentalFee);
            System.out.println(":) Rental fee calculated");
            
            return rentalFee;
            
        } catch (Exception e) {
            System.out.println(":( Error calculating rental fee: " + e.getMessage());
            e.printStackTrace();
            return java.math.BigDecimal.ZERO;
        }
    }
    
    /**
     * Get payment record for a specific rental.
     * 
     * @param rentalID The rental ID
     * @return PaymentTransaction object or null if no payment found
     */
    public PaymentTransaction getPaymentByRental(String rentalID) {
        System.out.println("\n=== GETTING PAYMENT FOR RENTAL ===");
        System.out.println("Rental ID: " + rentalID);
        
        try {
            java.util.List<PaymentTransaction> payments = paymentDAO.getPaymentsByRental(rentalID);
            
            if (payments == null || payments.isEmpty()) {
                System.out.println("No payment found for this rental");
                return null;
            }
            
            // Since we assume one payment per rental, return the first one
            PaymentTransaction payment = payments.get(0);
            System.out.println(":) Payment found: " + payment.getPaymentID() + " - ₱" + payment.getAmount());
            
            return payment;
            
        } catch (Exception e) {
            System.out.println(":( Error retrieving payment: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }
    
    /**
     * Update an existing payment record with final amount and date.
     * Used to finalize placeholder payments when rental is completed.
     * 
     * @param rentalID The rental ID to find and update payment for
     * @param finalAmount The calculated rental amount
     * @param paymentDate The actual payment date
     * @return true if payment updated successfully
     */
    public boolean finalizePaymentForRental(String rentalID, java.math.BigDecimal finalAmount, java.sql.Date paymentDate) {
        System.out.println("\n=== FINALIZING PAYMENT ===");
        System.out.println("Rental ID: " + rentalID);
        System.out.println("Final Amount: ₱" + finalAmount);

        try {

            PaymentTransaction payment = getPaymentByRental(rentalID);

            if (payment == null) {

                System.out.println("Note: No placeholder payment found. Creating new payment record.");
                String newPaymentID = "PAY-" + rentalID;

                PaymentTransaction newPayment = new PaymentTransaction(
                        newPaymentID,
                        finalAmount,
                        rentalID,
                        paymentDate,
                        "Active"
                );

                boolean success = paymentDAO.insertPayment(newPayment);

                if (success) {
                    System.out.println(":) Payment (new) finalized successfully: " + newPaymentID);
                } else {
                    System.out.println(":( Failed to create new payment record");
                }
                return success;


            } else {
                System.out.println("✓ Placeholder payment found: " + payment.getPaymentID());
                payment.setAmount(finalAmount);
                payment.setPaymentDate(paymentDate);

                boolean success = paymentDAO.updatePayment(payment);

                if (success) {
                    System.out.println(":) Payment (update) finalized successfully");
                } else {
                    System.out.println(":( Failed to finalize/update payment");
                }
                return success;
            }

        } catch (Exception e) {
            System.out.println(":( Error finalizing payment: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
}
