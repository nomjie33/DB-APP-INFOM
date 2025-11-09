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
 * - status    : VARCHAR(15) - 'Active' or 'Inactive' (soft delete flag)
 * 
 * SOFT DELETE:
 * - Records are never physically deleted from database
 * - Instead, status is set to 'Inactive' to mark as deleted
 * - Default status is 'Active' for new records
 * 
 * COLLABORATOR NOTES:
 * - Used in maintenance transactions to track parts usage
 * - PartDAO handles inventory updates (increment/decrement quantity)
 * - PartDAO filters out inactive records in queries
 */ 
public class Part {
    private String partID;
    private String partName;
    private int quantity;
    private BigDecimal price;
    private String status;
    
    // Default constructor
    public Part() {
        this.status = "Active"; // Default to Active
    }
    
    // Parameterized constructor 
    public Part(String partID, String partName, int quantity) {
        this.partID = partID;
        this.partName = partName;
        this.quantity = quantity;
        this.price = BigDecimal.ZERO;
        this.status = "Active"; // Default to Active
    }
    
    // Parameterized constructor 
    public Part(String partID, String partName, int quantity, BigDecimal price) {
        this.partID = partID;
        this.partName = partName;
        this.quantity = quantity;
        this.price = price;
        this.status = "Active"; // Default to Active
    }
    
    // Full parameterized constructor (includes status)
    public Part(String partID, String partName, int quantity, BigDecimal price, String status) {
        this.partID = partID;
        this.partName = partName;
        this.quantity = quantity;
        this.price = price;
        this.status = status;
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
    
    public String getStatus() {
        return status;
    }
    
    public void setStatus(String status) {
        this.status = status;
    }
    
    /**
     * Check if part is active (not soft-deleted)
     * @return true if status is "Active"
     */
    public boolean isActive() {
        return "Active".equalsIgnoreCase(status);
    }
    
    /**
     * Check if part is inactive (soft-deleted)
     * @return true if status is "Inactive"
     */
    public boolean isInactive() {
        return "Inactive".equalsIgnoreCase(status);
    }
    
    @Override
    public String toString() {
        return "Part{" +
                "partId='" + partID + '\'' +
                ", partName='" + partName + '\'' +
                ", quantity=" + quantity +
                ", price=" + price +
                ", status='" + status + '\'' +
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
