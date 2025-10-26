package model;

/**
 * Entity class representing a PART (inventory item) in the database.
 * 
 * PURPOSE: Maps to the 'parts' table in MySQL database.
 * 
 * SIMPLIFIED SCHEMA:
 * Table columns:
 * - partId    : VARCHAR(11) - Primary key (e.g., "P01", "P02")
 * - partName  : VARCHAR(25) - Name/description of the part
 * - quantity  : INT(3)      - Available inventory quantity
 * 
 * Example rows:
 * P01, "Brake Pads",    50
 * P02, "Engine Oil",    120
 * P03, "Air Filter",    75
 * 
 * IMPLEMENTATION NOTES:
 * - Keep this class as a POJO (no business logic).
 * - Use `partId` as the identity in equals() / hashCode().
 * - Quantity represents available stock; decrement during maintenance operations.
 * 
 * COLLABORATOR NOTES:
 * - Used in maintenance transactions to track parts usage
 * - PartDAO handles inventory updates (increment/decrement quantity)
 */
public class Part {
    // Fields matching the simplified parts table
    private String partId;
    private String partName;
    private int quantity;
    
    // Default constructor
    public Part() {
    }
    
    // Parameterized constructor
    public Part(String partId, String partName, int quantity) {
        this.partId = partId;
        this.partName = partName;
        this.quantity = quantity;
    }
    
    // Getters and setters
    public String getPartId() {
        return partId;
    }
    
    public void setPartId(String partId) {
        this.partId = partId;
    }
    
    public String getPartName() {
        return partName;
    }
    
    public void setPartName(String partName) {
        this.partName = partName;
    }
    
    public int getQuantity() {
        return quantity;
    }
    
    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }
    
    @Override
    public String toString() {
        return "Part{" +
                "partId='" + partId + '\'' +
                ", partName='" + partName + '\'' +
                ", quantity=" + quantity +
                '}';
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Part part = (Part) o;
        return partId != null && partId.equals(part.partId);
    }
    
    @Override
    public int hashCode() {
        return partId != null ? partId.hashCode() : 0;
    }
}
