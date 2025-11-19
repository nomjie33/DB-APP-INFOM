package service;

import dao.PartDAO;
import model.Part;
import java.util.List;

/**
 * Service layer for Part operations. Keeps business logic centralized
 * so GUI controllers call services instead of DAOs directly.
 */
public class PartService {

    private final PartDAO partDAO;

    public PartService() {
        this.partDAO = new PartDAO();
    }

    /**
     * Generate the next sequential part ID.
     * Format: PART-XXX where XXX is a 3-digit number (001, 002, 003, etc.)
     * Finds the highest existing ID (including inactive) and increments by 1.
     * 
     * @return Next part ID (e.g., "PART-021")
     */
    public String generateNextPartID() {
        try {
            // Get all parts including inactive to ensure no ID collisions
            List<Part> allParts = partDAO.getAllPartsIncludingInactive();
            
            int maxNumber = 0;
            
            // Find the highest number from existing IDs
            for (Part part : allParts) {
                String id = part.getPartId();
                // Extract number from format "PART-XXX"
                if (id != null && id.startsWith("PART-") && id.length() >= 8) {
                    try {
                        String numberPart = id.substring(5); // Get part after "PART-"
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
            String nextID = String.format("PART-%03d", nextNumber);
            System.out.println("PartService: Generated next Part ID: " + nextID);
            return nextID;
            
        } catch (Exception e) {
            // Fallback: use timestamp-based ID if something goes wrong
            System.err.println("Error generating part ID, using fallback: " + e.getMessage());
            long timestamp = System.currentTimeMillis();
            return String.format("PART-%03d", (int)(timestamp % 1000));
        }
    }

    public boolean addPart(Part part) {
        // Additional business rules could go here (validation, logging)
        System.out.println("PartService: Adding part with ID: " + part.getPartId());
        boolean result = partDAO.insertPart(part);
        System.out.println("PartService: Insert result: " + result);
        return result;
    }

    public boolean updatePart(Part part) {
        // Additional business rules could go here (e.g., audit trail)
        System.out.println("PartService: Updating part with ID: " + part.getPartId());
        boolean result = partDAO.updatePart(part);
        System.out.println("PartService: Update result: " + result);
        return result;
    }
}
