package model;

import java.math.BigDecimal;

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
 * - price     : DECIMAL(10,2) - Price per unit
 * 
 * COLLABORATOR NOTES:
 * - Used in maintenance transactions to track parts usage
 * - PartDAO handles inventory updates (increment/decrement quantity)
 */ 
public class Part {
    private String partID;
    private String partName;
    private int quantity;
    private BigDecimal price;
    
    // Default constructor
    public Part() {
    }
    
    // Parameterized constructor 
    public Part(String partID, String partName, int quantity) {
        this.partID = partID;
        this.partName = partName;
        this.quantity = quantity;
        this.price = BigDecimal.ZERO;
    }
    
    // Parameterized constructor 
    public Part(String partID, String partName, int quantity, BigDecimal price) {
        this.partID = partID;
        this.partName = partName;
        this.quantity = quantity;
        this.price = price;
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
    
    public BigDecimal getPrice() {
        return price;
    }
    
    public void setPrice(BigDecimal price) {
        this.price = price;
    }
    
    @Override
    public String toString() {
        return "Part{" +
                "partId='" + partID + '\'' +
                ", partName='" + partName + '\'' +
                ", quantity=" + quantity +
                ", price=" + price +
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
