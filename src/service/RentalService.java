package service;

import dao.*;
import model.*;
import java.sql.Timestamp;
import java.util.List;

/**
 * Business Logic Service for RENTAL operations.
 * UPDATED: Two-phase rental process:
 *   1. bookRental() - Customer selects pickUpDateTime, vehicle stays Available
 *   2. startRental() - Admin sets startDateTime when customer picks up, vehicle becomes In Use
 * Cost calculation handled by PaymentService
 */
public class RentalService {
    
    private CustomerDAO customerDAO;
    private VehicleDAO vehicleDAO;
    private LocationDAO locationDAO; 
    private RentalDAO rentalDAO;
    private PaymentDAO paymentDAO;
    private PaymentService paymentService;

    // Constructor with all dependencies
    public RentalService(CustomerDAO customerDAO, VehicleDAO vehicleDAO, LocationDAO locationDAO, RentalDAO rentalDAO, PaymentDAO paymentDAO, PaymentService paymentService){
        this.customerDAO = customerDAO;
        this.vehicleDAO = vehicleDAO;
        this.locationDAO = locationDAO;
        this.rentalDAO = rentalDAO;
        this.paymentDAO = paymentDAO;
        this.paymentService = paymentService;
    }
    
    /**
     * Book a rental (Phase 1: Customer Booking)
     * Creates a rental record with pickUpDateTime, but startDateTime remains NULL.
     * Vehicle status stays "Available" until admin confirms pickup.
     * 
     * @param customerID Customer booking the vehicle
     * @param plateID Vehicle being booked
     * @param locationID Rental location
     * @param pickUpDateTime Customer's selected pickup date and time
     * @return Rental ID if successful, null otherwise
     */
    public String bookRental(String customerID, String plateID, String locationID, Timestamp pickUpDateTime)
    {
        // VALIDATE CUSTOMER
        System.out.println("Validating Customer 🔎...");
        Customer customer = customerDAO.getCustomerById(customerID);

        if(customer == null) {
            System.err.println("Err: Customer " + customerID + " not found!");
            return null;
        }

        // Check if customer is active
        if (!customer.isActive()) {
            System.err.println("Err: Customer account is inactive!");
            return null;
        }

        System.out.println("Customer found: " + customer.getFullName());
        System.out.println("   Contact: " + customer.getContactNumber());

        // VALIDATE VEHICLE 
        System.out.println("Validating Vehicle 🚗...");
        Vehicle vehicle = vehicleDAO.getVehicleById(plateID);

        if(vehicle == null) {
            System.err.println("Err: Vehicle " + plateID + " not found!");
            return null;
        }
        
        // Check if vehicle is active (not retired)
        if (!vehicle.isActive()) {
            System.err.println("Err: Vehicle is retired/inactive!");
            return null;
        }
        
        System.out.println("Vehicle found: " + vehicle.getVehicleType());
        System.out.println("   Plate ID: " + vehicle.getPlateID());
        System.out.println("   Price: ₱" + vehicle.getRentalPrice() + "/hour");
        System.out.println("   Current Status: " + vehicle.getStatus());

        // CHECK VEHICLE AVAILABILITY
        if(!vehicle.isAvailable())
        {
            System.err.println("Err: Vehicle is not available!");
            System.out.println("   Current Status: "+ vehicle.getStatus());

            if ("In Use".equalsIgnoreCase(vehicle.getStatus())) {
                System.err.println("   This vehicle is currently rented");
            } else if (vehicle.isInMaintenance()) {
                System.err.println("   This vehicle is in maintenance");
            }
            return null;
        }
        System.out.println("✓ Vehicle is available for booking");

        // VALIDATE LOCATION
        System.out.println("Validating location...");
        Location location = locationDAO.getLocationById(locationID);
        
        if (location == null) {
            System.err.println("Err: Location " + locationID + " not found!");
            return null;
        }
        
        // Check if location is active
        if (!location.isActive()) {
            System.err.println("Err: Location is closed/inactive!");
            return null;
        }
        
        System.out.println("✓ Location found: " + location.getName());

        // GENERATE RENTAL ID
        String rentalID = generateRentalID();
        System.out.println("Generated Rental ID: " + rentalID);

        // CREATE RENTAL RECORD (startDateTime is NULL - not picked up yet)
        RentalTransaction rental = new RentalTransaction(
            rentalID,
            customerID,
            plateID,
            locationID,
            pickUpDateTime,  // Customer's chosen pickup time
            null,            // startDateTime is NULL (vehicle not picked up)
            null             // endDateTime is null (rental not completed)
        );
        
        boolean rentalCreated = rentalDAO.insertRental(rental);
        
        if (!rentalCreated) {
            System.err.println("Err: Failed to create rental booking!");
            return null;
        }
        
        System.out.println("✓ Rental booking created!");
        System.out.println("   Note: Vehicle status remains 'Available' until pickup");
        
        // CREATE PLACEHOLDER PAYMENT
        System.out.println("Creating placeholder payment...");
        String paymentID = generatePaymentID();
        java.sql.Date paymentDate = new java.sql.Date(System.currentTimeMillis());
        
        PaymentTransaction placeholderPayment = new PaymentTransaction(
            paymentID,
            java.math.BigDecimal.ZERO,  // Placeholder amount
            rentalID,
            paymentDate
        );
        
        boolean paymentCreated = paymentDAO.insertPayment(placeholderPayment);
        
        if (!paymentCreated) {
            System.err.println("WARNING: Failed to create placeholder payment!");
            System.err.println("Booking created but payment record is missing");
        } else {
            System.out.println("Placeholder payment created: " + paymentID);
        }
        
        // ===== SUCCESS! =====
        System.out.println("\n━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        System.out.println("RENTAL BOOKED!");
        System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        System.out.println("   Rental ID: " + rentalID);
        System.out.println("   Payment ID: " + paymentID);
        System.out.println("   Customer: " + customer.getFullName());
        System.out.println("   Vehicle: " + vehicle.getVehicleType() + " (" + vehicle.getPlateID() + ")");
        System.out.println("   Location: " + location.getName());
        System.out.println("   Pickup Time: " + pickUpDateTime);
        System.out.println("   Rate: ₱" + vehicle.getRentalPrice() + "/hour");
        System.out.println("   Status: Awaiting pickup (can be cancelled)");
        System.out.println("────────────────────────────────\n");
        
        return rentalID;
    }
    
    /**
     * Start a rental (Phase 2: Admin Action on Physical Pickup)
     * Sets startDateTime when customer physically picks up the vehicle.
     * Updates vehicle status to "In Use".
     * 
     * @param rentalID Rental to start
     * @return true if successful, false otherwise
     */
    public boolean startRental(String rentalID) {
        System.out.println("\n=== Starting Rental (Physical Pickup) ===");
        
        // FETCH RENTAL RECORD
        RentalTransaction rental = rentalDAO.getRentalById(rentalID);
        
        if (rental == null) {
            System.err.println("Err: Rental " + rentalID + " not found!");
            return false;
        }
        
        System.out.println("✓ Rental found!");
        System.out.println("   Customer: " + rental.getCustomerID());
        System.out.println("   Vehicle: " + rental.getPlateID());
        System.out.println("   Scheduled Pickup: " + rental.getPickUpDateTime());
        
        // VALIDATE RENTAL STATUS
        if (rental.isPickedUp()) {
            System.err.println("Err: Rental already started!");
            System.err.println("   Start Time: " + rental.getStartDateTime());
            return false;
        }
        
        if (rental.isCancelled()) {
            System.err.println("Err: Rental was cancelled!");
            return false;
        }
        
        if (rental.isCompleted()) {
            System.err.println("Err: Rental is already completed!");
            return false;
        }
        
        System.out.println("✓ Rental is awaiting pickup");
        
        // VALIDATE VEHICLE AVAILABILITY
        Vehicle vehicle = vehicleDAO.getVehicleById(rental.getPlateID());
        
        if (vehicle == null) {
            System.err.println("Err: Vehicle not found!");
            return false;
        }
        
        if (!vehicle.isAvailable()) {
            System.err.println("Err: Vehicle is not available!");
            System.err.println("   Current Status: " + vehicle.getStatus());
            return false;
        }
        
        // SET START TIME
        Timestamp startDateTime = new Timestamp(System.currentTimeMillis());
        rental.setStartDateTime(startDateTime);
        
        // UPDATE RENTAL RECORD
        boolean rentalUpdated = rentalDAO.updateRental(rental);
        
        if (!rentalUpdated) {
            System.err.println("Err: Failed to update rental record!");
            return false;
        }
        
        System.out.println("✓ Rental start time set: " + startDateTime);
        
        // UPDATE VEHICLE STATUS TO "IN USE"
        System.out.println("Updating vehicle status to 'In Use'...");
        
        boolean statusUpdated = vehicleDAO.updateVehicleStatus(rental.getPlateID(), "In Use");
        
        if (!statusUpdated) {
            System.err.println("Err: Failed to update vehicle status!");
            System.err.println("WARNING: Rental started but vehicle status not updated");
            System.err.println("Attempting rollback...");
            
            // ROLLBACK: Reset startDateTime to NULL
            rental.setStartDateTime(null);
            rentalDAO.updateRental(rental);
            System.err.println("Rental start rolled back");
            
            return false;
        }
        
        System.out.println("✓ Vehicle status updated to 'In Use'");
        
        // ===== SUCCESS! =====
        System.out.println("\n━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        System.out.println("RENTAL STARTED!");
        System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        System.out.println("   Rental ID: " + rentalID);
        System.out.println("   Vehicle: " + vehicle.getVehicleType() + " (" + rental.getPlateID() + ")");
        System.out.println("   Scheduled: " + rental.getPickUpDateTime());
        System.out.println("   Actual Start: " + startDateTime);
        System.out.println("   Status: In progress (cannot be cancelled)");
        System.out.println("────────────────────────────────\n");
        
        return true;
    }

    /**
     * Complete a rental (vehicle returned)
     * NOTE: Cost calculation handled by PaymentService
     * 
     * @param rentalID Rental to complete
     * @return Total rental cost if successful, 0.0 otherwise
     */
    public double completeRental(String rentalID){
        System.out.println("\n=== Completing Rental ===");
        
        // FETCH RENTAL RECORD
        RentalTransaction rental = rentalDAO.getRentalById(rentalID);

        if (rental == null){
            System.err.println("Err: Rental " + rentalID + " not found!");
            return 0.0;
        }
        
        System.out.println("✓ Rental found!");
        System.out.println("   Customer: " + rental.getCustomerID());
        System.out.println("   Vehicle: " + rental.getPlateID());
        System.out.println("   Start Time: " + rental.getStartDateTime());

        // VALIDATE IF RENTAL IS STILL ACTIVE
        if (rental.isCompleted()) {
            System.err.println("Err: Rental is already completed!");
            System.err.println("   End Time: " + rental.getEndDateTime());
            return 0.0;
        }
        
        if (rental.isCancelled()) {
            System.err.println("Err: Rental was cancelled!");
            return 0.0;
        }
        
        System.out.println("✓ Rental is active");

        // SET END TIME 
        Timestamp endTime = new Timestamp(System.currentTimeMillis());
        System.out.println("Return time: " + endTime);

        // UPDATE RENTAL RECORD
        boolean rentalCompleted = rentalDAO.completeRental(rentalID, endTime);

        if(!rentalCompleted) { 
            
            System.err.println("Err: Failed to update rental record!"); 
            
            return 0.0; 
        
        }
        
        System.out.println("Rental record updated successfully!");

        // CALCULATE COST (delegated to PaymentService)
        System.out.println("\nCalculating rental cost...");
        java.math.BigDecimal totalCost = paymentService.calculateRentalFee(rentalID);
        
        if (totalCost == null || totalCost.compareTo(java.math.BigDecimal.ZERO) <= 0) {
            System.err.println("WARNING: Cost calculation failed or returned zero");
            System.err.println("   Payment may need manual adjustment");
        }
        
        // FINALIZE PAYMENT RECORD
        System.out.println("\nFinalizing payment...");
        java.sql.Date paymentDate = new java.sql.Date(System.currentTimeMillis());
        boolean paymentFinalized = paymentService.finalizePaymentForRental(rentalID, totalCost, paymentDate);
        
        if (!paymentFinalized) {
            System.err.println("WARNING: Failed to finalize payment record");
            System.err.println("   Payment amount: ₱" + totalCost);
            System.err.println("   Manual payment update may be required");
        }
        System.out.println("✓ Rental record updated successfully!");

        // UPDATE VEHICLE STATUS
        boolean statusUpdated = vehicleDAO.updateVehicleStatus(rental.getPlateID(), "Available");
        if (!statusUpdated) {
            System.err.println("WARNING: Failed to update vehicle status");
            System.err.println("   Rental was completed but vehicle still shows 'In Use'");
            System.err.println("   Manual intervention may be required");
        } else {
            System.out.println("✓ Vehicle status updated to 'Available'");
        }
        
        // SUMMARY
        System.out.println("\n━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        System.out.println("RENTAL COMPLETED!");
        System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        System.out.println("   Rental ID: " + rentalID);
        System.out.println("   Total Cost: ₱" + totalCost);
        System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━\n");
        
        return totalCost.doubleValue();
    }

    /**
     * Cancel a rental (SOFT DELETE)
     * Only allowed if vehicle has NOT been picked up (startDateTime is NULL).
     * Marks rental as Cancelled, does not delete from database.
     * Also deactivates the corresponding payment record.
     * 
     * @param rentalID Rental to cancel
     * @return true if successful, false otherwise
     */
    public boolean cancelRental(String rentalID) {
        System.out.println("\n=== Cancelling Rental ===");
        
        RentalTransaction rental = rentalDAO.getRentalById(rentalID);
        if (rental == null) {
            System.err.println("Err: Rental not found");
            return false;
        }
        
        // CHECK IF ALREADY CANCELLED OR COMPLETED
        if (rental.isCancelled()) {
            System.err.println("Err: Rental is already cancelled");
            return false;
        }
        
        if (rental.isCompleted()) {
            System.err.println("Err: Cannot cancel completed rental");
            return false;
        }
        
        // CHECK IF VEHICLE HAS BEEN PICKED UP
        if (rental.isPickedUp()) {
            System.err.println("Err: Cannot cancel rental - vehicle has been picked up!");
            System.err.println("   Pickup Time: " + rental.getStartDateTime());
            System.err.println("   Cancellation is only allowed before physical pickup");
            return false;
        }
        
        System.out.println("✓ Rental is awaiting pickup - cancellation allowed");
        System.out.println("   Scheduled Pickup: " + rental.getPickUpDateTime());
        
        // NOTE: Vehicle status should still be "Available" since pickup hasn't happened
        // But we'll check and update just in case
        Vehicle vehicle = vehicleDAO.getVehicleById(rental.getPlateID());
        if (vehicle != null && !vehicle.isAvailable()) {
            System.out.println("   Resetting vehicle status to 'Available'...");
            boolean statusUpdated = vehicleDAO.updateVehicleStatus(rental.getPlateID(), "Available");
            if (!statusUpdated) {
                System.err.println("WARNING: Failed to update vehicle status");
            }
        }
        
        // Deactivate the associated payment record
        PaymentTransaction payment = paymentService.getPaymentByRental(rentalID);
        if (payment != null) {
            boolean paymentDeactivated = paymentDAO.deactivatePayment(payment.getPaymentID());
            if (paymentDeactivated) {
                System.out.println("✓ Payment record deactivated: " + payment.getPaymentID());
            } else {
                System.err.println("WARNING: Failed to deactivate payment record");
            }
        } else {
            System.out.println("Note: No payment record found for this rental");
        }
        
        // Mark rental as Cancelled (soft delete)
        boolean cancelled = rentalDAO.cancelRental(rentalID);
        
        if (cancelled) {
            System.out.println("✓ Rental cancelled (marked as Cancelled)");
            System.out.println("   Rental ID: " + rentalID);
            System.out.println("   Note: Rental data preserved for reporting");
        }
        
        return cancelled;
    }
    
    /**
     * Get rental history for a customer
     * 
     * @param customerID Customer to get history for
     * @return List of rentals
     */
    public List<RentalTransaction> getRentalHistory(String customerID) {        
        Customer customer = customerDAO.getCustomerById(customerID);
        
        if (customer == null) {
            System.err.println("Err: Customer not found");
            return null;
        }
        
        List<RentalTransaction> rentals = rentalDAO.getRentalsByCustomer(customerID);
        
        System.out.println("Found " + rentals.size() + " rental(s) for customer: " + customer.getFullName());
        
        return rentals;
    }

    /**
     * Check if vehicle is available for rental
     * 
     * @param plateID Vehicle to check
     * @return true if available, false otherwise
     */
    public boolean checkVehicleAvailability(String plateID) {
        Vehicle vehicle = vehicleDAO.getVehicleById(plateID);
        
        if (vehicle == null) {
            return false;
        }
        
        // Check if vehicle is active (not retired)
        if (!vehicle.isActive()) {
            return false;
        }
        
        // Check status
        if (!vehicle.isAvailable()) {
            return false;
        }
        
        // Check no active rentals
        return !rentalDAO.hasActiveRental(plateID);
    }

    // EXTRA QUERY METHODS
    
    public List<RentalTransaction> getActiveRentals() {
        return rentalDAO.getActiveRentals();
    }
    
    public List<RentalTransaction> getCompletedRentals() {
        return rentalDAO.getCompletedRentals();
    }
    
    public List<RentalTransaction> getAllRentals() {
        return rentalDAO.getAllRentals();
    }

    // HELPER METHODS
    
    /**
     * Generate unique rental ID
     * Format: RNT-XXXXXX
     */
    private String generateRentalID() {
        long timestamp = System.currentTimeMillis();
        return "RNT-" + (timestamp % 1000000);
    }
    
    private String generatePaymentID() {
        long timestamp = System.currentTimeMillis();
        return "PAY-" + (timestamp % 1000000);
    }
}