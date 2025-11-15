package model;

/**
 * Entity class representing an ADDRESS in the database.
 * 
 * PURPOSE: Maps to the 'addresses' table in MySQL database.
 * Stores complete address information for customers.
 * Links to Barangay table which links to City for hierarchical address structure.
 * Used for customer demographics analysis and reporting.
 */
public class Address {
    
    private Integer addressID;
    private Integer barangayID;
    private String street;
    
    // Optional: reference to Barangay object for convenience
    private Barangay barangay;
    
    // Constructors
    public Address() {
    }
    
    public Address(Integer addressID, Integer barangayID, String street) {
        this.addressID = addressID;
        this.barangayID = barangayID;
        this.street = street;
    }
    
    // Getters and Setters
    public Integer getAddressID() {
        return addressID;
    }
    
    public void setAddressID(Integer addressID) {
        this.addressID = addressID;
    }
    
    public Integer getBarangayID() {
        return barangayID;
    }
    
    public void setBarangayID(Integer barangayID) {
        this.barangayID = barangayID;
    }
    
    public String getStreet() {
        return street;
    }
    
    public void setStreet(String street) {
        this.street = street;
    }
    
    public Barangay getBarangay() {
        return barangay;
    }
    
    public void setBarangay(Barangay barangay) {
        this.barangay = barangay;
        if (barangay != null) {
            this.barangayID = barangay.getBarangayID();
        }
    }
    
    /**
     * Get full formatted address string.
     * Format: Street, Barangay, City
     * 
     * @return Formatted address string
     */
    public String getFullAddress() {
        if (barangay != null && barangay.getCity() != null) {
            return (street != null ? street + ", " : "") + 
                   barangay.getName() + ", " + 
                   barangay.getCity().getName();
        } else if (barangay != null) {
            return (street != null ? street + ", " : "") + barangay.getName();
        } else {
            return street != null ? street : "";
        }
    }
    
    // Utility methods
    @Override
    public String toString() {
        return "Address{" +
                "addressID=" + addressID +
                ", barangayID=" + barangayID +
                ", street='" + street + '\'' +
                (barangay != null ? ", fullAddress='" + getFullAddress() + '\'' : "") +
                '}';
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Address address = (Address) o;
        return addressID != null && addressID.equals(address.addressID);
    }
    
    @Override
    public int hashCode() {
        return addressID != null ? addressID.hashCode() : 0;
    }
}
