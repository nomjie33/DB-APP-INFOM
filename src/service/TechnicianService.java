package service;

import dao.TechnicianDAO;
import model.Technician;
import java.util.List;

/**
 * Service layer for Technician operations.
 * Handles business logic and ID generation for technician records.
 */
public class TechnicianService {

    private final TechnicianDAO technicianDAO;

    public TechnicianService() {
        this.technicianDAO = new TechnicianDAO();
    }

    /**
     * Generate the next sequential technician ID.
     * Format: TECH-XXX where XXX is a 3-digit number (001, 002, 003, etc.)
     * Finds the highest existing ID (including inactive) and increments by 1.
     * 
     * @return Next technician ID (e.g., "TECH-021")
     */
    public String generateNextTechnicianID() {
        try {
            // Get all technicians including inactive to ensure no ID collisions
            List<Technician> allTechnicians = technicianDAO.getAllTechniciansIncludingInactive();
            
            int maxNumber = 0;
            
            // Find the highest number from existing IDs
            for (Technician tech : allTechnicians) {
                String id = tech.getTechnicianId();
                // Extract number from format "TECH-XXX"
                if (id != null && id.startsWith("TECH-") && id.length() >= 8) {
                    try {
                        String numberPart = id.substring(5); // Get part after "TECH-"
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
            String nextID = String.format("TECH-%03d", nextNumber);
            System.out.println("TechnicianService: Generated next Technician ID: " + nextID);
            return nextID;
            
        } catch (Exception e) {
            // Fallback: use timestamp-based ID if something goes wrong
            System.err.println("Error generating technician ID, using fallback: " + e.getMessage());
            long timestamp = System.currentTimeMillis();
            return String.format("TECH-%03d", (int)(timestamp % 1000));
        }
    }

    public boolean addTechnician(Technician technician) {
        System.out.println("TechnicianService: Adding technician with ID: " + technician.getTechnicianId());
        boolean result = technicianDAO.insertTechnician(technician);
        System.out.println("TechnicianService: Insert result: " + result);
        return result;
    }

    public boolean updateTechnician(Technician technician) {
        System.out.println("TechnicianService: Updating technician with ID: " + technician.getTechnicianId());
        boolean result = technicianDAO.updateTechnician(technician);
        System.out.println("TechnicianService: Update result: " + result);
        return result;
    }
}
