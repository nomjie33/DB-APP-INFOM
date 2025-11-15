package model;

/**
 * Entity class representing a CUSTOMER in the database.
 * 
 * PURPOSE: Maps to the 'customers' table in MySQL database.
 * 
 * FIELDS TO IMPLEMENT:
 * - customerId (int/String) - Primary key
 * - name (String) - Customer full name
 * - email (String) - Contact email
 * - phone (String) - Contact phone number
 * - address (String) - Customer address
 * - licenseNumber (String) - Driver's license number
 * - registrationDate (Date) - When customer registered
 * 
 * METHODS TO IMPLEMENT:
 * - Constructor(s)
 * - Getters and Setters for all fields
 * - toString() for debugging
 * - equals() and hashCode() for object comparison
 * 
 * COLLABORATOR NOTES:
 * - Keep this class simple - NO business logic here
 * - Only data fields and basic accessor methods
 * - This is a POJO (Plain Old Java Object)
 */
public class Customer {

    // TODO: Add private fields for customer attributes
    private String customerID;
    private String lastName;
    private String firstName;
    private String contactNumber;
    private Integer addressID;
    private String emailAddress;
    private String status;
    
    // Optional: reference to Address object for convenience
    private Address address;
    
    // TODO: Add constructors (default and parameterized)
    public Customer() {
        this.status = "Active";
    }
    
    public Customer(String customerID, String lastName, String firstName, 
                   String contactNumber, Integer addressID, String emailAddress, String status) {
        this.customerID = customerID;
        this.lastName = lastName;
        this.firstName = firstName;
        this.contactNumber = contactNumber;
        this.addressID = addressID;
        this.emailAddress = emailAddress;
        this.status = "Active";

    }
    
    // TODO: Add getters and setters
    public String getCustomerID() {
        return customerID;
    }
    
    public void setCustomerID(String customerID) {
        this.customerID = customerID;
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
    
    public String getContactNumber() {
        return contactNumber;
    }
    
    public void setContactNumber(String contactNumber) {
        this.contactNumber = contactNumber;
    }
    
    public Integer getAddressID() {
        return addressID;
    }
    
    public void setAddressID(Integer addressID) {
        this.addressID = addressID;
    }
    
    public Address getAddress() {
        return address;
    }
    
    public void setAddress(Address address) {
        this.address = address;
        if (address != null) {
            this.addressID = address.getAddressID();
        }
    }
    
    public String getEmailAddress() {
        return emailAddress;
    }
    
    public void setEmailAddress(String emailAddress) {
        this.emailAddress = emailAddress;
    }
    
    // Full name getter
    public String getFullName() {
        return firstName + " " + lastName;
    }

        public String getStatus() {
        return status;
    }
    
    public void setStatus(String status) {
        this.status = status;
    }
    
    public boolean isActive() {
        return "Active".equalsIgnoreCase(status);
    }

    // TODO: Add toString(), equals(), hashCode()

    @Override
    public String toString() {
        return "Customer{" +
                "customerID='" + customerID + '\'' +
                ", name='" + getFullName() + '\'' +
                ", contactNumber='" + contactNumber + '\'' +
                ", addressID=" + addressID +
                (address != null ? ", address='" + address.getFullAddress() + '\'' : "") +
                ", emailAddress='" + emailAddress + '\'' +
                '}';
    }
    
    // equals and hashCode - for comparing customers
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Customer customer = (Customer) o;
        return customerID != null && customerID.equals(customer.customerID);
    }
    
    @Override
    public int hashCode() {
        return customerID != null ? customerID.hashCode() : 0;
    }
}
