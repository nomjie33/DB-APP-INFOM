package dao;

import model.Customer;
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
 *    - INSERT new customer into database **/

    public boolean createCustomer(Customer customer) {
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
 
 /* 
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
    
    // TODO: Implement insertCustomer(Customer customer)
    
    // TODO: Implement updateCustomer(Customer customer)
    
    // TODO: Implement deleteCustomer(int customerId)
    
    // TODO: Implement getCustomerById(int customerId)
    
    // TODO: Implement getAllCustomers()
    
    // TODO: Implement searchCustomersByName(String name)
    
    // TODO: Implement getCustomerByEmail(String email)
}
