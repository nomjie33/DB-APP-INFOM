package dao;

import model.Customer;
import model.Address;
import util.DBConnection;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object for CUSTOMER table operations.
 */
public class CustomerDAO {
    
    private AddressDAO addressDAO = new AddressDAO();
    
    public boolean insertCustomer(Customer customer) {
        String sql = "INSERT INTO customers (customerID, lastName, firstName, " +
                    "contactNumber, addressID, emailAddress, status) VALUES (?, ?, ?, ?, ?, ?, ?)";
        
        try (Connection conn = DBConnection.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, customer.getCustomerID());
            stmt.setString(2, customer.getLastName());
            stmt.setString(3, customer.getFirstName());
            stmt.setString(4, customer.getContactNumber());
            if (customer.getAddressID() != null) {
                stmt.setInt(5, customer.getAddressID());
            } else {
                stmt.setNull(5, Types.INTEGER);
            }
            stmt.setString(6, customer.getEmailAddress());
            stmt.setString(7, "Active"); // Default to Active
            
            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
            
        } catch (SQLException e) {
            System.err.println("Error creating customer: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
    
    public boolean updateCustomer(Customer customer) {
        String sql = "UPDATE customers SET lastName = ?, firstName = ?, " +
                     "contactNumber = ?, addressID = ?, emailAddress = ?, status = ? WHERE customerID = ?";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, customer.getLastName());
            stmt.setString(2, customer.getFirstName());
            stmt.setString(3, customer.getContactNumber());
            if (customer.getAddressID() != null) {
                stmt.setInt(4, customer.getAddressID());
            } else {
                stmt.setNull(4, Types.INTEGER);
            }
            stmt.setString(5, customer.getEmailAddress());
            stmt.setString(6, customer.getStatus());
            stmt.setString(7, customer.getCustomerID());
            
            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
            
        } catch (SQLException e) {
            System.err.println("Error updating customer: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
    
    /**
     * SOFT DELETE: Mark a customer as inactive instead of physically deleting.
     * This preserves historical data and maintains referential integrity.
     * Deactivate a customer (mark as Inactive).
     * 
     * @param customerID Customer ID to deactivate
     * @return true if deactivation successful, false otherwise
     */
    public boolean deactivateCustomer(String customerID) {
        String sql = "UPDATE customers SET status = 'Inactive' WHERE customerID = ?";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, customerID);
            int rowsAffected = stmt.executeUpdate();
            
            if (rowsAffected > 0) {
                System.out.println("Customer " + customerID + " has been marked as Inactive (soft deleted)");
                return true;
            }
            
        } catch (SQLException e) {
            System.err.println("Error deactivating customer: " + e.getMessage());
            e.printStackTrace();
        }
        
        return false;
    }
    
    /**
     * Reactivate a previously deactivated customer.
     * 
     * @param customerID Customer ID to reactivate
     * @return true if reactivation successful, false otherwise
     */
    public boolean reactivateCustomer(String customerID) {
        String sql = "UPDATE customers SET status = 'Active' WHERE customerID = ?";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, customerID);
            int rowsAffected = stmt.executeUpdate();
            
            if (rowsAffected > 0) {
                System.out.println("Customer " + customerID + " has been reactivated");
                return true;
            }
            
        } catch (SQLException e) {
            System.err.println("Error reactivating customer: " + e.getMessage());
            e.printStackTrace();
        }
        
        return false;
    }
    
    public Customer getCustomerById(String customerID) {
        String sql = "SELECT * FROM customers WHERE customerID = ?";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, customerID);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return extractCustomerFromResultSet(rs);
                }
            }
            
        } catch (SQLException e) {
            System.err.println("Error getting customer by ID: " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }
    
    /**
     * Get all ACTIVE customers (default behavior)
     * Use getAllCustomersIncludingInactive() for reports
     */
    public List<Customer> getAllCustomers() {
        String sql = "SELECT * FROM customers WHERE status = 'Active'";
        List<Customer> customers = new ArrayList<>();
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            
            while (rs.next()) {
                customers.add(extractCustomerFromResultSet(rs));
            }
            
        } catch (SQLException e) {
            System.err.println("Error getting all customers: " + e.getMessage());
            e.printStackTrace();
        }
        return customers;
    }
    
    /**
     * Get ALL customers including inactive ones
     * USE THIS FOR REPORTS that need historical data
     */
    public List<Customer> getAllCustomersIncludingInactive() {
        String sql = "SELECT * FROM customers";
        List<Customer> customers = new ArrayList<>();
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            
            while (rs.next()) {
                customers.add(extractCustomerFromResultSet(rs));
            }
            
        } catch (SQLException e) {
            System.err.println("Error getting all customers: " + e.getMessage());
            e.printStackTrace();
        }
        return customers;
    }
    
    /**
     * Get customers by status
     * 
     * @param status "Active" or "Inactive"
     * @return List of customers with that status
     */
    public List<Customer> getCustomersByStatus(String status) {
        String sql = "SELECT * FROM customers WHERE status = ?";
        List<Customer> customers = new ArrayList<>();
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, status);
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    customers.add(extractCustomerFromResultSet(rs));
                }
            }
            
        } catch (SQLException e) {
            System.err.println("Error getting customers by status: " + e.getMessage());
            e.printStackTrace();
        }
        return customers;
    }
    
    public List<Customer> searchCustomersByName(String name) {
        String sql = "SELECT * FROM customers WHERE (lastName LIKE ? OR firstName LIKE ?) AND status = 'Active'";
        List<Customer> customers = new ArrayList<>();
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            String searchPattern = "%" + name + "%";
            stmt.setString(1, searchPattern);
            stmt.setString(2, searchPattern);
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    customers.add(extractCustomerFromResultSet(rs));
                }
            }
            
        } catch (SQLException e) {
            System.err.println("Error searching customers by name: " + e.getMessage());
            e.printStackTrace();
        }
        return customers;
    }
    
    public Customer getCustomerByEmail(String email) {
        String sql = "SELECT * FROM customers WHERE emailAddress = ?";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, email);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return extractCustomerFromResultSet(rs);
                }
            }
            
        } catch (SQLException e) {
            System.err.println("Error getting customer by email: " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }
    
    /**
     * Helper method to extract Customer object from ResultSet.
     * Reduces code duplication across query methods.
     */
    private Customer extractCustomerFromResultSet(ResultSet rs) throws SQLException {
        Customer customer = new Customer();
        customer.setCustomerID(rs.getString("customerID"));
        customer.setLastName(rs.getString("lastName"));
        customer.setFirstName(rs.getString("firstName"));
        customer.setContactNumber(rs.getString("contactNumber"));
        
        // Handle addressID (can be NULL)
        int addressID = rs.getInt("addressID");
        if (!rs.wasNull()) {
            customer.setAddressID(addressID);
        }
        
        customer.setEmailAddress(rs.getString("emailAddress"));
        customer.setStatus(rs.getString("status"));
        return customer;
    }
    
    /**
     * Get customer with full address details (including barangay and city).
     * Use this method when you need complete address information.
     * 
     * @param customerID Customer ID to retrieve
     * @return Customer object with Address information, null if not found
     */
    public Customer getCustomerWithAddress(String customerID) {
        Customer customer = getCustomerById(customerID);
        if (customer != null && customer.getAddressID() != null) {
            Address address = addressDAO.getAddressWithFullDetails(customer.getAddressID());
            customer.setAddress(address);
        }
        return customer;
    }
    
    /**
     * Get all customers with full address details.
     * Use this method when you need complete address information for reporting.
     * 
     * @return List of customers with Address information
     */
    public List<Customer> getAllCustomersWithAddress() {
        List<Customer> customers = getAllCustomers();
        for (Customer customer : customers) {
            if (customer.getAddressID() != null) {
                Address address = addressDAO.getAddressWithFullDetails(customer.getAddressID());
                customer.setAddress(address);
            }
        }
        return customers;
    }

    public int getNextCustomerNumber() {
        String sql = "SELECT customerID FROM customers ORDER BY customerID DESC LIMIT 1";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            if (rs.next()) {
                String lastID = rs.getString("customerID");
                String numericPart = lastID.substring(5);
                return Integer.parseInt(numericPart) + 1;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 1;
    }

    public String generateCustomerID() {
        int nextNum = getNextCustomerNumber();
        return String.format("CUST-%03d", nextNum);
    }

    /**
     * Generate the next sequential customer ID.
     * Format: CUST-XXX where XXX is a 3-digit number (001, 002, 003, etc.)
     * Finds the highest existing ID (including inactive) and increments by 1.
     *
     * @return Next customer ID (e.g., "CUST-021")
     */
    public String generateNextCustomerID() {
        try {
            // Get all customers including inactive to ensure no ID collisions
            List<Customer> allCustomers = getAllCustomersIncludingInactive();

            int maxNumber = 0;

            // Find the highest number from existing IDs
            for (Customer customer : allCustomers) {
                String id = customer.getCustomerID();
                // Extract number from format "CUST-XXX"
                if (id != null && id.startsWith("CUST-") && id.length() >= 8) {
                    try {
                        String numberPart = id.substring(5); // Get part after "CUST-"
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
            String nextID = String.format("CUST-%03d", nextNumber);
            System.out.println("CustomerDAO: Generated next Customer ID: " + nextID);
            return nextID;

        } catch (Exception e) {
            // Fallback: use timestamp-based ID if something goes wrong
            System.err.println("Error generating customer ID, using fallback: " + e.getMessage());
            long timestamp = System.currentTimeMillis();
            return String.format("CUST-%03d", (int)(timestamp % 1000));
        }
    }
}