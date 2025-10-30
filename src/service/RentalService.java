package service;

import dao.*;
import model.*;
import java.sql.Timestamp;
import java.sql.Date;
import java.time.LocalDate;
import java.util.List;

/**
 * Business Logic Service for RENTAL operations.
 * 
 * PURPOSE: Implements business rules and workflows for vehicle rentals.
 * Acts as intermediary between UI and DAO layers.
 * 
 * DEPENDENCIES:
 * - CustomerDAO (verify customer exists)
 * - VehicleDAO (check availability, update status)
 * - RentalDAO (create rental records)
 * - LocationDAO (validate locations)
 * 
 * METHODS TO IMPLEMENT:
 * 
 * 1. createRental(int customerId, int vehicleId, Timestamp startDate, Timestamp endDate, int pickupLocationId, int returnLocationId)
 *    WORKFLOW:
 *    - Verify customer exists (CustomerDAO)
 *    - Check vehicle availability (VehicleDAO)
 *    - Validate dates (endDate > startDate)
 *    - Calculate total cost (vehicle daily rate × rental days)
 *    - Create rental record (RentalDAO)
 *    - Update vehicle status to "In Use" (VehicleDAO)
 *    - Return rental ID or confirmation
 * 
 * 2. completeRental(int rentalId, Timestamp actualReturnDate)
 *    WORKFLOW:
 *    - Get rental record (RentalDAO)
 *    - Update rental with return date (RentalDAO)
 *    - Update vehicle status to "Available" (VehicleDAO)
 *    - Calculate final cost (adjust for early/late return)
 *    - Return updated rental
 * 
 * 3. cancelRental(int rentalId)
 *    WORKFLOW:
 *    - Get rental record
 *    - Verify rental can be cancelled (not already completed)
 *    - Update vehicle status back to "Available"
 *    - Delete or mark rental as cancelled
 * 
 * 4. getRentalHistory(int customerId)
 *    - Retrieve all rentals for a customer
 *    - For customer rental reports
 * 
 * 5. checkVehicleAvailability(int vehicleId, Timestamp startDate, Timestamp endDate)
 *    - Check if vehicle is available for the requested period
 *    - Check vehicle status
 *    - Check no overlapping rentals
 * 
 * COLLABORATOR NOTES:
 * - Always use database transactions for rental creation
 * - Validate all inputs before calling DAOs
 * - Handle exceptions gracefully
 * - Log important operations
 */
public class RentalService {
    
    // Private DAO instances
    private CustomerDAO customerDAO;
    private VehicleDAO vehicleDAO;
    private LocationDAO locationDAO; 
    private RentalDAO rentalDAO;

    // TODO: Initialize DAO objects in constructor
    public RentalService(CustomerDAO customerDAO, VehicleDAO vehicleDAO, LocationDAO locationDAO, RentalDAO rentalDAO){
        this.customerDAO = customerDAO;
        this.vehicleDAO = vehicleDAO;
        this.locationDAO = locationDAO;
        this.rentalDAO = rentalDAO;
    }
    
    // TODO: Implement createRental()
    public String createRental(String customerID, String plateID, String locationID)
    {
        System.out.println("==== Creating New Rental ====");

        // VALIDATE CUSTOMER
        System.out.println("Validating Customer 🔎...");
        // Check if customer exists
        Customer customer = customerDAO.getCustomerById(customerID);

        if(customer == null) {
            System.err.println("Err: Customer " + customerID + " not found!");
            return null;
        }

        System.out.println("Customer found: " + customer.getFullName());
        System.out.println("Customer Contact: " + customer.getContactNumber());

        // VALIDATE VEHICLE 
        System.out.println("Validating Vehicle 🚗...");
        // Check if vehicle exists
        Vehicle vehicle = vehicleDAO.getVehicleById(plateID);

        if(vehicle == null) {
            System.err.println("Err: Vehicle " + plateID + " not found!");
            return null;
        }
        System.out.println("Vehicle found: " + vehicle.getVehicleModel());
        System.out.println("   Type: " + vehicle.getVehicleType());
        System.out.println("   Price: ₱" + vehicle.getRentalPrice() + "/hour");
        System.out.println("   Current Status: " + vehicle.getStatus());

        // CHECK VEHICLE AVAILABILITY
        if(!vehicle.isAvailable())
        {
            System.err.println("Err: Vehicle is not available!");
            System.out.println("Current Status: "+ vehicle.getStatus());

            if ("In Use".equalsIgnoreCase(vehicle.getStatus())) {
                System.err.println("This vehicle is currently rented");
            } else if (vehicle.isInMaintenance()) {
                System.err.println("This vehicle is in maintenance");
            }
            return null;
        }
        System.out.println("Vehicle is available for rent ");

        // VALIDATE LOCATION
        System.out.println("Validating location...");
        Location location = locationDAO.getLocationById(locationID);
        
        if (location == null) {
            System.err.println("Err: Location " + locationID + " not found!");
            return null;
        }
        
        System.out.println("Location found: " + location.getName());

        // GENERATE RENTAL ID
        String rentalID = generateRentalID();
        System.out.println("Generated Rental ID: " + rentalID);


        // CREATE RENTAL RECORD
        Timestamp startTime = new Timestamp(System.currentTimeMillis());
        Date rentalDate = Date.valueOf(LocalDate.now());
        
        RentalTransaction rental = new RentalTransaction(
            rentalID,
            customerID,
            plateID,
            locationID,
            startTime,
            null,  // endTime is null (rental is active)
            rentalDate
        );
        
        boolean rentalCreated = rentalDAO.insertRental(rental);
        
        if (!rentalCreated) {
            System.err.println("Err: Failed to create rental record!");
            return null;
        }
        
        System.out.println("Rental record created!");


        // Update Vehicle Status
        System.out.println("Updating vehicle status...");
        
        boolean statusUpdated = vehicleDAO.updateVehicleStatus(plateID, "In Use");
        
        if (!statusUpdated) {
            System.err.println("Err: Failed to update vehicle status!");
            System.err.println("WARNING: Rental was created but vehicle status not updated");
            System.err.println("Attempting rollback...");
            
            // ROLLBACK: Delete the rental since we failed to update vehicle
            rentalDAO.deleteRental(rentalID);
            System.err.println("Rental rolled back");
            
            return null;
        }
        
        System.out.println("Vehicle status updated to 'In Use'");
        
        // ===== SUCCESS! =====
        System.out.println("\n━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        System.out.println("RENTAL CREATED SUCCESSFULLY!");
        System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        System.out.println("📋 Rental Details:");
        System.out.println("   Rental ID: " + vehicle);
        System.out.println("   Customer: " + customer.getFullName());
        System.out.println("   Vehicle: " + vehicle.getVehicleModel());
        System.out.println("   Location: " + location.getName());
        System.out.println("   Start Time: " + startTime);
        System.out.println("   Rate: ₱" + vehicle.getRentalPrice() + "/hour");
        System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━\n");
        
        return rentalID;
    }


    // TODO: Implement completeRental()
    public double completeRental(String rentalID){
        // FETCH RENTAL RECORD
        RentalTransaction rental = rentalDAO.getRentalById(null);

        if (rental == null){
            System.err.println("Err: Rental " + rentalID + "not found!");
            return -1;
        }
        System.out.println("Rental found!");
        System.out.println("   Customer: " + rental.getCustomerID());
        System.out.println("   Vehicle: " + rental.getPlateID());
        System.out.println("   Start Time: " + rental.getStartTime());

        // VALIDATE IF RENTAL IS STILL ACTIVE
        if (!rental.isActive()) {
            System.err.println("Err: Rental is already completed!");
            System.err.println("   End Time: " + rental.getEndTime());
            return -1;
        }
        
        System.out.println("Rental is active");

        // SET END TIME 
        Timestamp endTime = new Timestamp(System.currentTimeMillis());
        System.out.println("Return time: " + endTime);

        // CALCULATE COST
        Vehicle vehicle = vehicleDAO.getVehicleById(rental.getPlateID());
        
        if (vehicle == null) {
            System.err.println("Err: Vehicle not found!");
            return -1;
        }

        // get the duration
        long milliseconds = endTime.getTime() - rental.getStartTime().getTime();
        double hours = milliseconds / (1000.0 * 60 * 60);

        // round up to nearest hour
        hours = Math.ceil(hours);

        double totalCost = hours * vehicle.getRentalPrice();

        System.out.println("Calculated Cost:");
        System.out.println("   Duration: " + String.format("%.2f", hours) + " hours");
        System.out.println("   Rate: ₱" + vehicle.getRentalPrice() + "/hour");
        System.out.println("   Total: ₱" + String.format("%.2f", totalCost));

        // UPDATE RENTAL RECORD
        boolean rentalCompleted = rentalDAO.completeRental(rentalID, endTime);

        if(!rentalCompleted) { System.err.println("Err: Failed to update rental record!"); return -1; }
        
        System.out.println("Rental record updated successfully!");

        // UPDATE VEHICLE STATUS
        boolean statusUpdated = vehicleDAO.updateVehicleStatus(rental.getPlateID(), "Available");
        if (!statusUpdated) {
            System.err.println("WARNING: Failed to update vehicle status");
            System.err.println("   Rental was completed but vehicle still shows 'In Use'");
            System.err.println("   Manual intervention may be required");
        } else {
            System.out.println("Vehicle status updated to 'Available'");
        }
        // SUMMARY

                System.out.println("\n━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        System.out.println("RENTAL COMPLETED SUCCESSFULLY!");
        System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        System.out.println("📋 Final Details:");
        System.out.println("   Rental ID: " + rentalID);
        System.out.println("   Duration: " + String.format("%.2f", hours) + " hours");
        System.out.println("   Total Cost: ₱" + String.format("%.2f", totalCost));
        System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━\n");
        
        return totalCost;
    }

    // TODO: Implement cancelRental()
        public boolean cancelRental(String rentalID) {
        System.out.println("\n━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        System.out.println("🚫 CANCELLING RENTAL");
        System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        
        // Get rental record
        RentalTransaction rental = rentalDAO.getRentalById(rentalID);
        
        if (rental == null) {
            System.err.println("Err: Rental " + rentalID + " not found!");
            return false;
        }
        
        // Check if rental is active
        if (!rental.isActive()) {
            System.err.println("Err: Cannot cancel completed rental");
            System.err.println("   Rental ended: " + rental.getEndTime());
            return false;
        }
        
        // Update vehicle status back to "Available"
        boolean statusUpdated = vehicleDAO.updateVehicleStatus(rental.getPlateID(), "Available");
        
        if (!statusUpdated) {
            System.err.println("WARNING: Failed to update vehicle status");
        }
        
        // Delete rental record
        boolean deleted = rentalDAO.deleteRental(rentalID);
        
        if (deleted) {
            System.out.println("Rental cancelled successfully");
        }
        
        return deleted;
    }
    
    
    // TODO: Implement getRentalHistory()
    public List<RentalTransaction> getRentalHistory(String customerID) {        
        // Validate customer exists
        Customer customer = customerDAO.getCustomerById(customerID);
        
        if (customer == null) {
            System.err.println("Err: Customer not found");
            return null;
        }
        
        List<RentalTransaction> rentals = rentalDAO.getRentalsByCustomer(customerID);
        
        System.out.println("Found" + rentals.size() + " rental(s)");
        
        return rentals;
    }

    // TODO: Implement checkVehicleAvailability()
        public boolean checkVehicleAvailability(String plateID) {
        Vehicle vehicle = vehicleDAO.getVehicleById(plateID);
        
        if (vehicle == null) {
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


    // ==== HELPERS =====
    private String generateRentalID() {
        long timestamp = System.currentTimeMillis();
        return "RNT-" + (timestamp % 1000000);
    }
}
