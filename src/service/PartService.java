package service;

import dao.PartDAO;
import model.Part;

/**
 * Service layer for Part operations. Keeps business logic centralized
 * so GUI controllers call services instead of DAOs directly.
 */
public class PartService {

    private final PartDAO partDAO;

    public PartService() {
        this.partDAO = new PartDAO();
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
