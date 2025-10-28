package main;

import ui.*;
import util.DBConnection;

/**
 * MAIN APPLICATION CLASS
 * Entry point for the Vehicle Rental Database Application.
 * 
 * PURPOSE: Initialize application and launch main UI.
 * 
 * COLLABORATOR NOTES:
 * - Test database connection before launching UI
 * - Choose which UI to launch (or create a main menu)
 * - Initialize all services if needed
 * 
 * STARTUP SEQUENCE:
 * 1. Test database connection
 * 2. Load configuration
 * 3. Launch main UI or menu
 * 
 * TODO: Decide on main UI approach:
 * Option A: Single unified UI with tabs for each module
 * Option B: Main menu that opens different UI windows
 * Option C: Command-line interface for testing
 */
public class VehicleRentalApp {
    
    /**
     * Main entry point for the application.
     * 
     * @param args Command line arguments
     */
    public static void main(String[] args) {
        System.out.println("===========================================");
        System.out.println("  VEHICLE RENTAL MANAGEMENT SYSTEM");
        System.out.println("===========================================");
        System.out.println();
        
        // Step 1: Test database connection
        System.out.println("Step 1: Testing database connection...");
        if (!DBConnection.testConnection()) {
            System.err.println("FATAL ERROR: Cannot connect to database!");
            System.err.println("Please check your database configuration in util/DBConnection.java");
            System.err.println("Make sure MySQL is running and database exists.");
            return; // Exit if no database connection
        }
        System.out.println();
        
        // Step 2: Initialize services (if needed)
        System.out.println("Step 2: Initializing services...");
        // TODO: Initialize any singleton services or configuration
        System.out.println("Services initialized.");
        System.out.println();
        
        // Step 3: Launch UI
        System.out.println("Step 3: Launching application UI...");
        
        // TODO: Choose which UI to launch
        // Option 1: Launch TransactionUI (main operational interface)
        // TransactionUI.launch(TransactionUI.class, args);
        
        // Option 2: Launch a main menu UI
        // MainMenuUI.launch(MainMenuUI.class, args);
        
        // Option 3: For testing - run specific UI
        System.out.println("\nCurrent Status: UI files created but not yet implemented.");
        System.out.println("Next steps for collaborators:");
        System.out.println("1. Implement entity classes in model/ package");
        System.out.println("2. Implement DAO classes with SQL queries");
        System.out.println("3. Implement Service classes with business logic");
        System.out.println("4. Build UI components using JavaFX");
        System.out.println("5. Implement report generation");
        System.out.println("\nSee README.md for detailed instructions.");
    }
}
