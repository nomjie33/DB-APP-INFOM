package service;

import dao.*;
import model.*;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.List;
import java.util.ArrayList;

/**
 * Business Logic Service for MAINTENANCE operations.
 * 
 * PURPOSE: Manages vehicle maintenance, repairs, and defect tracking.
 * Coordinates between MaintenanceTransaction (main record) and MaintenanceCheque (parts usage).
 * 
 * DEPENDENCIES:
 * - MaintenanceDAO (maintenance transaction records)
 * - MaintenanceChequeDAO (parts usage tracking)
 * - VehicleDAO (update vehicle status)
 * - TechnicianDAO (assign technicians)
 * - PartDAO (track parts inventory)
 * 
 * ARCHITECTURE NOTES:
 * - MaintenanceTransaction: Main maintenance record (date, technician, vehicle, notes)
 * - MaintenanceCheque: Junction table tracking parts used with quantities
 * - This service coordinates atomic operations across both tables
 * 
 * METHODS IMPLEMENTED:
 * 
 * 1. scheduleMaintenance(String maintenanceID, String plateID, String technicianID, String notes, Date dateReported)
 *    - Verify vehicle exists (VehicleDAO)
 *    - Verify technician exists (TechnicianDAO)
 *    - Create MaintenanceTransaction record (MaintenanceDAO)
 *    - Update vehicle status to "Maintenance" (VehicleDAO)
 *    - Return success boolean
 * 
 * 2. completeMaintenance(String maintenanceID, Date dateRepaired, List<PartUsage> partsUsed)
 *    - Get maintenance record (MaintenanceDAO)
 *    - Validate parts availability (PartDAO)
 *    - Create MaintenanceCheque records for each part used (MaintenanceChequeDAO)
 *    - Decrement parts inventory (PartDAO.decrementPartQuantity)
 *    - Update maintenance record with dateRepaired (MaintenanceDAO)
 *    - Update vehicle status to "Available" (VehicleDAO)
 * 
 * 3. flagVehicleAsDefective(String plateID, String defectDescription, String technicianID, Date dateReported)
 *    - Update vehicle status to "Maintenance" (VehicleDAO)
 *    - Return generated maintenanceID
 * 
 * 4. getMaintenanceHistory(String plateID)
 *    - Return all MaintenanceTransaction records for a vehicle
 * 
 * 5. getPartsUsedInMaintenance(String maintenanceID)
 *    - Return all MaintenanceCheque records for a maintenance job
 * 
 * 6. assignTechnician(String maintenanceID, String newTechnicianID)
 *    - Reassign technician to existing maintenance job
 * 
 * 7. getTechnicianWorkload(String technicianID)
 *    - Return all maintenance records assigned to a technician
 * 
 * NOTE: Cost calculations are handled by PenaltyService.
 * A penalty transaction must be created to charge maintenance costs to customers.
 */
public class MaintenanceService {
    
    // DAO instances
    private MaintenanceDAO maintenanceDAO;
    private MaintenanceChequeDAO maintenanceChequeDAO;
    private VehicleDAO vehicleDAO;
    private TechnicianDAO technicianDAO;
    private PartDAO partDAO;
    
    /**
     * Constructor - Initialize all required DAOs
     */
    public MaintenanceService() {
        this.maintenanceDAO = new MaintenanceDAO();
        this.maintenanceChequeDAO = new MaintenanceChequeDAO();
        this.vehicleDAO = new VehicleDAO();
        this.technicianDAO = new TechnicianDAO();
        this.partDAO = new PartDAO();
    }
    
    /**
     * Schedule a new maintenance job for a vehicle.
     * Creates a MaintenanceTransaction record and updates vehicle status.
     * 
     * @param maintenanceID Unique identifier for maintenance record
     * @param plateID Vehicle plate ID
     * @param technicianID Technician assigned to the job
     * @param notes Description of the maintenance/issue
     * @param startDateTime Timestamp when maintenance started
     * @return true if scheduling successful, false otherwise
     */
    public boolean scheduleMaintenance(String maintenanceID, String plateID, 
                                      String technicianID, String notes, Timestamp startDateTime) {
        try {
            // Verify vehicle exists
            Vehicle vehicle = vehicleDAO.getVehicleById(plateID);
            if (vehicle == null) {
                System.out.println("Error: Vehicle with ID " + plateID + " not found.");
                return false;
            }
            
            // Check if vehicle is active (not retired/inactive)
            if (!vehicle.isActive()) {
                System.out.println("Error: Cannot schedule maintenance for inactive/retired vehicle: " + plateID);
                return false;
            }
            
            // Verify technician exists
            Technician technician = technicianDAO.getTechnicianById(technicianID);
            if (technician == null) {
                System.out.println("Error: Technician with ID " + technicianID + " not found.");
                return false;
            }
            
            // Create maintenance transaction (endDateTime is null until completed)
            MaintenanceTransaction maintenance = new MaintenanceTransaction(
                maintenanceID, startDateTime, null, notes, technicianID, plateID
            );
            
            boolean insertSuccess = maintenanceDAO.insertMaintenance(maintenance);
            if (!insertSuccess) {
                System.out.println("Error: Failed to create maintenance record.");
                return false;
            }
            
            // Update vehicle status to "Maintenance"
            boolean statusUpdate = vehicleDAO.updateVehicleStatus(plateID, "Maintenance");
            if (!statusUpdate) {
                System.out.println("Warning: Maintenance created but vehicle status update failed.");
            }
            
            System.out.println("Maintenance scheduled successfully: " + maintenanceID);
            return true;
            
        } catch (Exception e) {
            System.out.println("Error scheduling maintenance: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
    
    /**
     * Calculate labor cost for a maintenance job.
     * Formula: hoursWorked × technician rate
     * 
     * @param maintenanceID Maintenance record ID
     * @return Labor cost as BigDecimal, or ZERO if calculation fails
     */
    public BigDecimal calculateLaborCost(String maintenanceID) {
        try {
            // Get maintenance record (including inactive for historical calculations)
            MaintenanceTransaction maintenance = maintenanceDAO.getMaintenanceByIdIncludingInactive(maintenanceID);
            if (maintenance == null) {
                System.out.println("Maintenance record not found: " + maintenanceID);
                return BigDecimal.ZERO;
            }
            
            BigDecimal hoursWorked = maintenance.getHoursWorked();
            if (hoursWorked == null || hoursWorked.compareTo(BigDecimal.ZERO) <= 0) {
                System.out.println("Hours worked not recorded for maintenance " + maintenanceID);
                return BigDecimal.ZERO;
            }
            
            // Get technician rate (including inactive for historical records)
            Technician technician = technicianDAO.getTechnicianByIdIncludingInactive(maintenance.getTechnicianID());
            if (technician == null) {
                System.out.println("Technician not found for maintenance " + maintenanceID);
                return BigDecimal.ZERO;
            }
            
            BigDecimal rate = technician.getRate();
            BigDecimal laborCost = hoursWorked.multiply(rate).setScale(2, java.math.RoundingMode.HALF_UP);
            
            return laborCost;
            
        } catch (Exception e) {
            System.out.println("Error calculating labor cost: " + e.getMessage());
            e.printStackTrace();
            return BigDecimal.ZERO;
        }
    }
    
    /**
     * Calculate parts cost for a maintenance job.
     * Formula: Σ(part price × quantity used)
     * 
     * @param maintenanceID Maintenance record ID
     * @return Parts cost as BigDecimal, or ZERO if calculation fails
     */
    public BigDecimal calculatePartsCost(String maintenanceID) {
        try {
            // Get all parts used in this maintenance (including inactive for historical accuracy)
            List<model.MaintenanceCheque> cheques = maintenanceChequeDAO.getPartsByMaintenanceIncludingInactive(maintenanceID);
            
            if (cheques == null || cheques.isEmpty()) {
                return BigDecimal.ZERO;
            }
            
            BigDecimal totalPartsCost = BigDecimal.ZERO;
            
            for (model.MaintenanceCheque cheque : cheques) {
                String partID = cheque.getPartID();
                BigDecimal quantityUsed = cheque.getQuantityUsed();
                
                // Get part details including price (including inactive parts for historical accuracy)
                Part part = partDAO.getPartByIdIncludingInactive(partID);
                if (part == null) {
                    System.out.println("Part not found: " + partID);
                    continue;
                }
                
                BigDecimal partPrice = part.getPrice();
                if (partPrice == null || partPrice.compareTo(BigDecimal.ZERO) <= 0) {
                    System.out.println("Price not set for part: " + part.getPartName());
                    continue;
                }
                
                BigDecimal partCost = partPrice.multiply(quantityUsed).setScale(2, java.math.RoundingMode.HALF_UP);
                totalPartsCost = totalPartsCost.add(partCost);
            }
            
            return totalPartsCost;
            
        } catch (Exception e) {
            System.out.println("Error calculating parts cost: " + e.getMessage());
            e.printStackTrace();
            return BigDecimal.ZERO;
        }
    }
    
    /**
     * Calculate total maintenance cost (labor + parts).
     * This is the single source of truth for maintenance costs.
     * Used for both business-shouldered and customer-shouldered (penalty) costs.
     * 
     * @param maintenanceID Maintenance record ID
     * @return Total maintenance cost as BigDecimal
     */
    public BigDecimal calculateMaintenanceCost(String maintenanceID) {
        BigDecimal laborCost = calculateLaborCost(maintenanceID);
        BigDecimal partsCost = calculatePartsCost(maintenanceID);
        BigDecimal totalCost = laborCost.add(partsCost).setScale(2, java.math.RoundingMode.HALF_UP);
        
        return totalCost;
    }
    
    /**
     * Complete a maintenance job by recording repair end time (no new parts).
     * Used when parts were already added separately via MaintenanceCheque.
     * Updates vehicle status to "Available" and calculates total cost.
     * 
     * @param maintenanceID Maintenance record to complete
     * @param endDateTime Timestamp when repair was completed
     * @return true if completion successful, false otherwise
     */
    public boolean completeMaintenance(String maintenanceID, Timestamp endDateTime) {
        return completeMaintenance(maintenanceID, endDateTime, null);
    }
    
    /**
     * Complete a maintenance job by recording repair end time and parts used.
     * Updates MaintenanceTransaction with endDateTime and creates MaintenanceCheque records for parts.
     * Automatically calculates and stores total maintenance cost.
     * 
     * @param maintenanceID Maintenance record to complete
     * @param endDateTime Timestamp when repair was completed
     * @param partsUsed List of PartUsage objects (partID and quantity)
     * @return true if completion successful, false otherwise
     */
    public boolean completeMaintenance(String maintenanceID, Timestamp endDateTime, 
                                      List<PartUsage> partsUsed) {
        try {
            // Get maintenance record
            MaintenanceTransaction maintenance = maintenanceDAO.getMaintenanceById(maintenanceID);
            if (maintenance == null) {
                System.out.println("Error: Maintenance record " + maintenanceID + " not found.");
                return false;
            }
            
            // Process parts used
            if (partsUsed != null && !partsUsed.isEmpty()) {
                for (PartUsage usage : partsUsed) {
                    // Verify part exists
                    Part part = partDAO.getPartById(usage.getPartID());
                    if (part == null) {
                        System.out.println("Error: Part " + usage.getPartID() + " not found.");
                        return false;
                    }
                    
                    // Check if sufficient inventory
                    if (part.getQuantity() < usage.getQuantity().intValue()) {
                        System.out.println("Error: Insufficient inventory for part " + 
                                         usage.getPartID() + ". Available: " + part.getQuantity() + 
                                         ", Required: " + usage.getQuantity());
                        return false;
                    }
                    
                    // Create maintenance cheque record
                    MaintenanceCheque cheque = new MaintenanceCheque(
                        maintenanceID, usage.getPartID(), usage.getQuantity()
                    );
                    boolean chequeInsert = maintenanceChequeDAO.insertMaintenanceCheque(cheque);
                    if (!chequeInsert) {
                        System.out.println("Error: Failed to record part usage for " + usage.getPartID());
                        return false;
                    }
                    
                    // Decrement part inventory
                    boolean inventoryUpdate = partDAO.decrementPartQuantity(
                        usage.getPartID(), usage.getQuantity().intValue()
                    );
                    if (!inventoryUpdate) {
                        System.out.println("Warning: Part usage recorded but inventory update failed.");
                    }
                }
            }
            
            // Update maintenance record with endDateTime
            maintenance.setEndDateTime(endDateTime);
            
            // Calculate hours worked
            BigDecimal hoursWorked = maintenance.getHoursWorked();
            System.out.println("Hours worked calculated: " + hoursWorked + " hours");
            
            // Calculate total maintenance cost (labor + parts)
            BigDecimal totalCost = calculateMaintenanceCost(maintenanceID);
            maintenance.setTotalCost(totalCost);
            System.out.println("Total maintenance cost calculated: Php" + totalCost);
            
            boolean updateSuccess = maintenanceDAO.updateMaintenance(maintenance);
            if (!updateSuccess) {
                System.out.println("Error: Failed to update maintenance record.");
                return false;
            }
            
            // Update vehicle status back to "Available"
            boolean statusUpdate = vehicleDAO.updateVehicleStatus(
                maintenance.getPlateID(), "Available"
            );
            if (!statusUpdate) {
                System.out.println("Warning: Maintenance completed but vehicle status update failed.");
            }
            
            System.out.println("Maintenance completed successfully: " + maintenanceID);
            return true;
            
        } catch (Exception e) {
            System.out.println("Error completing maintenance: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
    
    /**
     * Flag a vehicle as defective and create a maintenance record.
     * 
     * @param plateID Vehicle to flag
     * @param defectDescription Description of the defect
     * @param technicianID Technician to assign (optional, can be null)
     * @param startDateTime Timestamp when defect was detected
     * @return maintenanceID if successful, null otherwise
     */
    public String flagVehicleAsDefective(String plateID, String defectDescription, 
                                        String technicianID, Timestamp startDateTime) {
        try {
            // Verify vehicle exists
            Vehicle vehicle = vehicleDAO.getVehicleById(plateID);
            if (vehicle == null) {
                System.out.println("Error: Vehicle with ID " + plateID + " not found.");
                return null;
            }
            
            // Check if vehicle is active (not retired/inactive)
            if (!vehicle.isActive()) {
                System.out.println("Error: Cannot flag inactive/retired vehicle as defective: " + plateID);
                return null;
            }
            
            // Update vehicle status to "Maintenance"
            boolean statusUpdate = vehicleDAO.updateVehicleStatus(plateID, "Maintenance");
            if (!statusUpdate) {
                System.out.println("Error: Failed to update vehicle status.");
                return null;
            }
            
            // Generate next sequential maintenance ID in format: MAINT-XXX
            String maintenanceID = generateNextMaintenanceID();
            
            // Create maintenance record
            MaintenanceTransaction maintenance = new MaintenanceTransaction(
                maintenanceID, startDateTime, null, 
                defectDescription, technicianID, plateID
            );
            
            boolean insertSuccess = maintenanceDAO.insertMaintenance(maintenance);
            if (!insertSuccess) {
                System.out.println("Error: Failed to create maintenance record.");
                return null;
            }
            
            System.out.println("Vehicle flagged as under maintenance. Maintenance ID: " + maintenanceID);
            return maintenanceID;
            
        } catch (Exception e) {
            System.out.println("Error flagging vehicle: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }
    
    /**
     * Get complete maintenance history for a vehicle.
     * Returns list of MaintenanceTransaction records.
     * 
     * @param plateID Vehicle plate ID
     * @return List of maintenance records
     */
    public List<MaintenanceTransaction> getMaintenanceHistory(String plateID) {
        try {
            return maintenanceDAO.getMaintenanceByVehicle(plateID);
        } catch (Exception e) {
            System.out.println("Error retrieving maintenance history: " + e.getMessage());
            e.printStackTrace();
            return new ArrayList<>();
        }
    }
    
    /**
     * Get all parts used in a specific maintenance job.
     * 
     * @param maintenanceID Maintenance record ID
     * @return List of MaintenanceCheque records showing parts and quantities
     */
    public List<MaintenanceCheque> getPartsUsedInMaintenance(String maintenanceID) {
        try {
            return maintenanceChequeDAO.getPartsByMaintenance(maintenanceID);
        } catch (Exception e) {
            System.out.println("Error retrieving parts used: " + e.getMessage());
            e.printStackTrace();
            return new ArrayList<>();
        }
    }
    
    /**
     * Reassign a technician to an existing maintenance job.
     * 
     * @param maintenanceID Maintenance record to update
     * @param newTechnicianID New technician to assign
     * @return true if reassignment successful, false otherwise
     */
    public boolean assignTechnician(String maintenanceID, String newTechnicianID) {
        try {
            // Verify maintenance record exists
            MaintenanceTransaction maintenance = maintenanceDAO.getMaintenanceById(maintenanceID);
            if (maintenance == null) {
                System.out.println("Error: Maintenance record " + maintenanceID + " not found.");
                return false;
            }
            
            // Verify new technician exists
            Technician technician = technicianDAO.getTechnicianById(newTechnicianID);
            if (technician == null) {
                System.out.println("Error: Technician with ID " + newTechnicianID + " not found.");
                return false;
            }
            
            // Update maintenance record
            maintenance.setTechnicianID(newTechnicianID);
            boolean updateSuccess = maintenanceDAO.updateMaintenance(maintenance);
            
            if (updateSuccess) {
                System.out.println("Technician reassigned successfully to maintenance " + maintenanceID);
            }
            
            return updateSuccess;
            
        } catch (Exception e) {
            System.out.println("Error assigning technician: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
    
    /**
     * Get all maintenance records assigned to a technician.
     * 
     * @param technicianID Technician ID
     * @return List of maintenance records
     */
    public List<MaintenanceTransaction> getTechnicianWorkload(String technicianID) {
        try {
            return maintenanceDAO.getMaintenanceByTechnician(technicianID);
        } catch (Exception e) {
            System.out.println("Error retrieving technician workload: " + e.getMessage());
            e.printStackTrace();
            return new ArrayList<>();
        }
    }
    
    /**
     * Generate the next sequential maintenance ID.
     * Format: MAINT-XXX where XXX is a 3-digit number (001, 002, 003, etc.)
     * Finds the highest existing ID and increments by 1.
     * 
     * @return Next maintenance ID (e.g., "MAINT-021")
     */
    private String generateNextMaintenanceID() {
        try {
            // Get all maintenance records
            List<MaintenanceTransaction> allMaintenance = maintenanceDAO.getAllMaintenance();
            
            int maxNumber = 0;
            
            // Find the highest number from existing IDs
            for (MaintenanceTransaction mt : allMaintenance) {
                String id = mt.getMaintenanceID();
                // Extract number from format "MAINT-XXX"
                if (id != null && id.startsWith("MAINT-") && id.length() >= 9) {
                    try {
                        String numberPart = id.substring(6); // Get part after "MAINT-"
                        int number = Integer.parseInt(numberPart);
                        if (number > maxNumber) {
                            maxNumber = number;
                        }
                    } catch (NumberFormatException e) {
                        // Skip IDs that don't have numeric suffix
                        continue;
                    }
                }
            }
            
            // Generate next ID
            int nextNumber = maxNumber + 1;
            return String.format("MAINT-%03d", nextNumber);
            
        } catch (Exception e) {
            // Fallback: use timestamp-based ID if something goes wrong
            System.err.println("Error generating maintenance ID, using fallback: " + e.getMessage());
            long timestamp = System.currentTimeMillis();
            return String.format("MAINT-%03d", (int)(timestamp % 1000));
        }
    }
    
    /**
     * Inner class to represent part usage in maintenance completion.
     * Used to pass part ID and quantity together.
     */
    public static class PartUsage {
        private String partID;
        private BigDecimal quantity;
        
        public PartUsage(String partID, BigDecimal quantity) {
            this.partID = partID;
            this.quantity = quantity;
        }
        
        public String getPartID() {
            return partID;
        }
        
        public BigDecimal getQuantity() {
            return quantity;
        }
    }
}
