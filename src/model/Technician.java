package model;

import java.math.BigDecimal;

/**
 * Entity class representing a TECHNICIAN in the database.
 * 
 * PURPOSE: Maps to the 'technicians' table in MySQL database.
 * 
 * SCHEMA:
 * Table columns:
 * - technicianId      : VARCHAR(11) - Primary key (e.g., "T01", "T02")
 * - lastName          : VARCHAR(25) - Technician last name
 * - firstName         : VARCHAR(25) - Technician first name
 * - specializationID  : VARCHAR(15) - Specialization reference (e.g., "SPEC01")
 * - rate              : DECIMAL(10,2) - Hourly or service rate
 * - contactNumber     : VARCHAR(15) - Contact phone number (stored as String for formatting)
 *
 * COLLABORATOR NOTES:
 * - Used in maintenance transactions to assign repairs
 * - Rate is used to calculate maintenance labor costs
 * - Specialization links to maintenance requirements
 */
public class Technician {
    private String technicianID;
    private String lastName;
    private String firstName;
    private String specializationID;
    private BigDecimal rate;
    private String contactNumber;
    
    // Default constructor
    public Technician() {
    }
    
    // Parameterized constructor
    public Technician(String technicianId, String lastName, String firstName, 
                     String specializationID, BigDecimal rate, String contactNumber) {
        this.technicianID = technicianId;
        this.lastName = lastName;
        this.firstName = firstName;
        this.specializationID = specializationID;
        this.rate = rate;
        this.contactNumber = contactNumber;
    }
    
    // Getters and setters
    public String getTechnicianId() {
        return technicianID;
    }
    
    public void setTechnicianId(String technicianID) {
        this.technicianID = technicianID;
    }
    
    public String getLastName() {
        return lastName;
    }
    
    public void setLastName(String lastName) {
        this.lastName = lastName;
    }
    
    public String getFirstName() {
        return firstName;
    }
    
    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }
    
    public String getSpecializationId() {
        return specializationID;
    }
    
    public void setSpecializationId(String specializationID) {
        this.specializationID = specializationID;
    }
    
    public BigDecimal getRate() {
        return rate;
    }
    
    public void setRate(BigDecimal rate) {
        this.rate = rate;
    }
    
    public String getContactNumber() {
        return contactNumber;
    }
    
    public void setContactNumber(String contactNumber) {
        this.contactNumber = contactNumber;
    }
    
    // Utility method to get full name
    public String getFullName() {
        return firstName + " " + lastName;
    }
    
    @Override
    public String toString() {
        return "Technician{" +
                "technicianID='" + technicianID + '\'' +
                ", name='" + getFullName() + '\'' +
                ", specializationID='" + specializationID + '\'' +
                ", rate=" + rate +
                ", contactNumber='" + contactNumber + '\'' +
                '}';
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Technician that = (Technician) o;
        return technicianID != null && technicianID.equals(that.technicianID);
    }
    
    @Override
    public int hashCode() {
        return technicianID != null ? technicianID.hashCode() : 0;
    }
}
