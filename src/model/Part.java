package model;

/**
 * Entity class representing a PART (inventory item) in the database.
 * 
 * PURPOSE: Maps to the 'parts' table in MySQL database.
 * 
 * SCHEMA:
 * Table columns:
 * - partID    : VARCHAR(11) - Primary key (e.g., "P01", "P02")
 * - partName  : VARCHAR(25) - Name/description of the part
 * - quantity  : INT(3)      - Available inventory quantity
 * 
 * COLLABORATOR NOTES:
 * - Used in maintenance transactions to track parts usage
 * - PartDAO handles inventory updates (increment/decrement quantity)
 */ 
public class Part {
    // Fields matching the simplified parts table
    private String partID;
    private String partName;
    private int quantity;
    
    // Default constructor
    public Part() {
    }
    
    // Parameterized constructor
    public Part(String partID, String partName, int quantity) {
        this.partID = partID;
        this.partName = partName;
        this.quantity = quantity;
    }
    
    // Getters and setters
    public String getPartId() {
        return partID;
    }
    
    public void setPartId(String partID) {
        this.partID = partID;
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
                "partId='" + partID + '\'' +
                ", partName='" + partName + '\'' +
                ", quantity=" + quantity +
                '}';
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Part part = (Part) o;
        return partID != null && partID.equals(part.partID);
    }
    
    @Override
    public int hashCode() {
        return partID != null ? partID.hashCode() : 0;
    }
}
