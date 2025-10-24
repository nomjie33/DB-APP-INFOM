package util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Database Connection Utility Class.
 * 
 * PURPOSE: Centralized database connection management.
 * Provides connection to MySQL database for all DAO classes.
 * 
 * SINGLETON PATTERN: Ensures only one connection pool/manager exists.
 * 
 * CONFIGURATION:
 * Update these constants with your MySQL database details:
 * - DB_URL: Your database URL
 * - DB_USER: Your MySQL username
 * - DB_PASSWORD: Your MySQL password
 * 
 * USAGE EXAMPLE:
 * ```java
 * Connection conn = DBConnection.getConnection();
 * // Use connection for queries
 * conn.close(); // Always close when done
 * ```
 * 
 * COLLABORATOR NOTES:
 * - IMPORTANT: Add MySQL Connector/J library to project dependencies
 * - Download from: https://dev.mysql.com/downloads/connector/j/
 * - Or use Maven/Gradle dependency
 * - Never commit database credentials to version control
 * - Consider using environment variables or config file for credentials
 * - Implement connection pooling for production (e.g., HikariCP)
 */
public class DBConnection {
    
    // ===== DATABASE CONFIGURATION =====
    // TODO: Update these values for your MySQL setup
    private static final String DB_URL = "jdbc:mysql://localhost:3306/vehicle_rental_db";
    private static final String DB_USER = "root";  // Change to your MySQL username
    private static final String DB_PASSWORD = "p@ssword";  // Change to your password
    private static final String DB_DRIVER = "com.mysql.cj.jdbc.Driver";
    
    // Singleton instance (optional - can use static methods instead)
    private static DBConnection instance;
    
    /**
     * Private constructor to prevent instantiation.
     * Load JDBC driver.
     */
    private DBConnection() {
        try {
            Class.forName(DB_DRIVER);
            System.out.println("MySQL JDBC Driver loaded successfully.");
        } catch (ClassNotFoundException e) {
            System.err.println("ERROR: MySQL JDBC Driver not found!");
            System.err.println("Add mysql-connector-java JAR to your project classpath.");
            e.printStackTrace();
        }
    }
    
    /**
     * Get database connection.
     * Creates new connection each time - suitable for small applications.
     * 
     * @return Connection object to MySQL database
     * @throws SQLException if connection fails
     */
    public static Connection getConnection() throws SQLException {
        try {
            Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
            System.out.println("Database connection established.");
            return conn;
        } catch (SQLException e) {
            System.err.println("ERROR: Failed to connect to database!");
            System.err.println("Check your DB_URL, DB_USER, and DB_PASSWORD settings.");
            throw e;
        }
    }
    
    /**
     * Test database connection.
     * Run this method to verify your database configuration.
     * 
     * @return true if connection successful, false otherwise
     */
    public static boolean testConnection() {
        try (Connection conn = getConnection()) {
            if (conn != null && !conn.isClosed()) {
                System.out.println("✓ Database connection test SUCCESSFUL!");
                return true;
            }
        } catch (SQLException e) {
            System.err.println("✗ Database connection test FAILED!");
            e.printStackTrace();
        }
        return false;
    }
    
    /**
     * Close database connection safely.
     * Always call this in finally block or use try-with-resources.
     * 
     * @param conn Connection to close
     */
    public static void closeConnection(Connection conn) {
        if (conn != null) {
            try {
                conn.close();
                System.out.println("Database connection closed.");
            } catch (SQLException e) {
                System.err.println("ERROR: Failed to close connection!");
                e.printStackTrace();
            }
        }
    }
    
    /**
     * Main method for testing database connection.
     * Run this to verify your database setup.
     */
    public static void main(String[] args) {
        System.out.println("Testing database connection...");
        testConnection();
    }
}
