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
 * CONFIGURATION:
 * - Credentials are loaded from db.properties (NOT committed to Git)
 * - Each team member creates their own db.properties file
 * - Copy db.properties.example and edit with YOUR credentials
 * 
 * SETUP (IMPORTANT):
 * 1. Copy db.properties.example to db.properties
 * 2. Edit db.properties with your MySQL username/password
 * 3. Create database: CREATE DATABASE vehicle_rental_db;
 * 4. Test connection: java util.DBConnection
 * 
 * COLLABORATOR NOTES:
 * - IMPORTANT: Add MySQL Connector/J library to project
 * - Download: https://dev.mysql.com/downloads/connector/j/
 * - db.properties is in .gitignore (passwords never committed!)
 */
public class DBConnection {
    
    // ===== DATABASE CONFIGURATION =====
    private static String DB_URL;
    private static String DB_USER;
    private static String DB_PASSWORD;
    private static final String DB_DRIVER = "com.mysql.cj.jdbc.Driver";

    // Load configuration when class is first used
    static {
        loadDatabaseConfig();
    }
    
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
    * Load database configuration from db.properties file.
    * Each team member has their own db.properties with their credentials.
    * This file is NOT committed to Git (.gitignore protects it).
    */
    private static void loadDatabaseConfig() {
    java.util.Properties props = new java.util.Properties();
    
    try (java.io.FileInputStream fis = new java.io.FileInputStream("db.properties")) {
        props.load(fis);
        
        DB_URL = props.getProperty("db.url");
        DB_USER = props.getProperty("db.username");
        DB_PASSWORD = props.getProperty("db.password");
        
        System.out.println("✓ Database configuration loaded from db.properties");
        
    } catch (java.io.IOException e) {
        System.err.println("════════════════════════════════════════════");
        System.err.println("ERROR: Cannot find db.properties file!");
        System.err.println("════════════════════════════════════════════");
        System.err.println("\nSetup Instructions:");
        System.err.println("1. Copy 'db.properties.example' to 'db.properties'");
        System.err.println("2. Edit db.properties with YOUR MySQL credentials");
        System.err.println("3. Make sure db.properties is in project root");
        System.err.println("\nFile is in .gitignore - your password is safe!");
        System.err.println("════════════════════════════════════════════\n");
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
