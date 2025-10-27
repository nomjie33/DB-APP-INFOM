package dao;

import model.Customer;
import util.DBConnection;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object for CUSTOMER table operations.
 * 
 * PURPOSE: Handles all database CRUD operations for customers table.
 * 
 * METHODS TO IMPLEMENT:
 * 
 * 1. insertCustomer(Customer customer)
 *    - INSERT new customer into database
 * 
 * 2. updateCustomer(Customer customer)
 *    - UPDATE existing customer record
 *    - Match by customer ID
 * 
 * 3. deleteCustomer(int customerId)
 *    - DELETE customer by ID
 *    - Consider cascade rules for rentals
 * 
 * 4. getCustomerById(int customerId)
 *    - SELECT customer by ID
 *    - Return Customer object or null
 * 
 * 5. getAllCustomers()
 *    - SELECT all customers
 *    - Return List<Customer>
 * 
 * 6. searchCustomersByName(String name)
 *    - SELECT customers matching name pattern
 *    - Use LIKE for partial matches
 * 
 * 7. getCustomerByEmail(String email)
 *    - SELECT customer by email
 *    - For login/validation
 * 
 * COLLABORATOR NOTES:
 * - Use PreparedStatement to prevent SQL injection
 * - Handle SQLException appropriately
 * - Use DBConnection utility class for connections
 * - Close resources in finally block or use try-with-resources
 * - Return null when record not found
 */
public class CustomerDAO {
    
    public boolean insertCustomer(Customer customer) {
        String sql = "INSERT INTO customers (customerID, lastName, firstName, " +
                    "contactNumber, address, emailAddress) VALUES (?, ?, ?, ?, ?, ?)";
        
        try (Connection conn = DBConnection.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, customer.getCustomerID());
            stmt.setString(2, customer.getLastName());
            stmt.setString(3, customer.getFirstName());
            stmt.setString(4, customer.getContactNumber());
            stmt.setString(5, customer.getAddress());
            stmt.setString(6, customer.getEmailAddress());
            
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
                     "contactNumber = ?, address = ?, emailAddress = ? WHERE customerID = ?";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, customer.getLastName());
            stmt.setString(2, customer.getFirstName());
            stmt.setString(3, customer.getContactNumber());
            stmt.setString(4, customer.getAddress());
            stmt.setString(5, customer.getEmailAddress());
            stmt.setString(6, customer.getCustomerID());
            
            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
            
        } catch (SQLException e) {
            System.err.println("Error updating customer: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
    
    public boolean deleteCustomer(String customerID) {
        String sql = "DELETE FROM customers WHERE customerID = ?";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, customerID);
            
            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
            
        } catch (SQLException e) {
            System.err.println("Error deleting customer: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
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
    
    public List<Customer> getAllCustomers() {
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
    
    public List<Customer> searchCustomersByName(String name) {
        String sql = "SELECT * FROM customers WHERE lastName LIKE ? OR firstName LIKE ?";
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
        customer.setAddress(rs.getString("address"));
        customer.setEmailAddress(rs.getString("emailAddress"));
        return customer;
    }
}
