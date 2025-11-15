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
}