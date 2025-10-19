package dao;

import model.Part;
import java.sql.*;
import java.util.List;

/**
 * Data Access Object for PART table operations.
 * 
 * PURPOSE: Handles all database CRUD operations for parts/inventory table.
 * 
 * METHODS TO IMPLEMENT:
 * 
 * 1. insertPart(Part part)
 *    - INSERT new part into inventory
 * 
 * 2. updatePart(Part part)
 *    - UPDATE existing part record
 * 
 * 3. deletePart(int partId)
 *    - DELETE part by ID
 * 
 * 4. getPartById(int partId)
 *    - SELECT part by ID
 * 
 * 5. getAllParts()
 *    - SELECT all parts in inventory
 * 
 * 6. getPartsByCategory(String category)
 *    - SELECT parts by category
 * 
 * 7. getLowStockParts()
 *    - SELECT parts where quantityInStock <= reorderLevel
 *    - For inventory management alerts
 * 
 * 8. updatePartQuantity(int partId, int newQuantity)
 *    - UPDATE quantity after usage or restocking
 *    - Called when parts are used in maintenance
 * 
 * 9. decrementPartQuantity(int partId, int usedQuantity)
 *    - Reduce quantity when part is used
 *    - Called during maintenance operations
 * 
 * COLLABORATOR NOTES:
 * - Track inventory carefully
 * - Validate quantities before decrementing (prevent negative)
 * - Generate alerts for low stock items
 */
public class PartDAO {
    
    // TODO: Implement insertPart(Part part)
    
    // TODO: Implement updatePart(Part part)
    
    // TODO: Implement deletePart(int partId)
    
    // TODO: Implement getPartById(int partId)
    
    // TODO: Implement getAllParts()
    
    // TODO: Implement getPartsByCategory(String category)
    
    // TODO: Implement getLowStockParts()
    
    // TODO: Implement updatePartQuantity(int partId, int newQuantity)
    
    // TODO: Implement decrementPartQuantity(int partId, int usedQuantity)
}
