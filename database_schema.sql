-- =====================================================
-- VEHICLE RENTAL MANAGEMENT SYSTEM
-- Database Schema SQL Script
-- =====================================================
-- 
-- INSTRUCTIONS FOR COLLABORATORS:
-- 1. Create a new database first: CREATE DATABASE vehicle_rental_db;
-- 2. Run this script to create all tables
-- 3. Adjust data types and constraints as needed for your requirements
-- 4. Add indexes for frequently queried columns
-- 5. Consider adding triggers for automatic updates (optional)
--
-- =====================================================
CREATE DATABASE IF NOT EXISTS vehicle_rental_db;
USE vehicle_rental_db;
-- DROP TABLE IF EXISTS locations;
-- DROP TABLE IF EXISTS vehicles;
-- DROP TABLE IF EXISTS customers;
DROP TABLE IF EXISTS deployments;
DROP TABLE IF EXISTS rentals;

-- =====================================================
-- 1. LOCATIONS TABLE
-- =====================================================
-- Stores rental branch/location information
-- Create this first as it's referenced by vehicles and technicians
CREATE TABLE locations (
    locationID VARCHAR(11) PRIMARY KEY,
    name VARCHAR(50)
);


-- =====================================================
-- 2. CUSTOMERS TABLE
-- =====================================================
-- Stores customer information
CREATE TABLE customers (
    customerID VARCHAR(11) PRIMARY KEY,
    lastName VARCHAR(25) NOT NULL,
    firstName VARCHAR(25) NOT NULL,
    contactNumber VARCHAR(11),
    address VARCHAR(80),
    emailAddress VARCHAR(80)
);


-- =====================================================
-- 3. VEHICLES TABLE
-- =====================================================
-- Stores vehicle inventory
CREATE TABLE vehicles (
    plateID VARCHAR(11) PRIMARY KEY,
    vehicleType VARCHAR(25) NOT NULL, 
    vehicleModel VARCHAR(30) NOT NULL, 
    status VARCHAR(15) NOT NULL DEFAULT 'Available',
    rentalPrice DECIMAL (10, 2) NOT NULL
);

-- =====================================================
-- 4. TECHNICIANS TABLE
-- =====================================================
-- Stores technician/mechanic information
CREATE TABLE technicians (
    technician_id VARCHAR(11) PRIMARY KEY,
    last_name VARCHAR(25) NOT NULL,
    first_name VARCHAR(25) NOT NULL,
    specialization_id VARCHAR(15),
    rate DECIMAL(10, 2) NOT NULL,
    contact_number VARCHAR(15),
    
    INDEX idx_technician_specialization (specialization_id)
);

-- =====================================================
-- 5. PARTS TABLE
-- =====================================================
-- Stores parts inventory
CREATE TABLE parts (
    part_id VARCHAR(11) PRIMARY KEY,
    part_name VARCHAR(25) NOT NULL,
    quantity INT(3) NOT NULL DEFAULT 0,
    
    CONSTRAINT chk_part_quantity 
        CHECK (quantity >= 0)
);


-- =====================================================
-- 6. RENTALS TABLE
-- =====================================================
-- Stores rental transaction records
-- Links customers, vehicles, and locations
-- Tracks rental period and pricing

CREATE TABLE rentals (
    rentalID VARCHAR(11) PRIMARY KEY,
    customerID VARCHAR(11) NOT NULL,
    plateID VARCHAR(11) NOT NULL,
    locationID VARCHAR(11) NOT NULL,
    startTime TIMESTAMP NOT NULL,
    endTime TIMESTAMP NULL,
    rentalDate DATE NOT NULL,
    
    CONSTRAINT fk_rental_customer 
        FOREIGN KEY (customerID) REFERENCES customers(customerID)
        ON DELETE RESTRICT
        ON UPDATE CASCADE,
    
    CONSTRAINT fk_rental_vehicle 
        FOREIGN KEY (plateID) REFERENCES vehicles(plateID)
        ON DELETE RESTRICT
        ON UPDATE CASCADE,
    
    CONSTRAINT fk_rental_location 
        FOREIGN KEY (locationID) REFERENCES locations(locationID)
        ON DELETE RESTRICT
        ON UPDATE CASCADE
);

CREATE INDEX idx_rental_customer ON rentals(customerID);
CREATE INDEX idx_rental_vehicle ON rentals(plateID);
CREATE INDEX idx_rental_date ON rentals(rentalDate);


-- =====================================================
-- 7. PAYMENTS TABLE
-- =====================================================
-- Stores payment transaction records

-- =====================================================
-- 8. MAINTENANCE TABLE
-- =====================================================
-- Stores vehicle maintenance/repair records
-- Parts used are tracked separately in maintenance_cheque table
CREATE TABLE maintenance (
    maintenanceID VARCHAR(11) PRIMARY KEY,
    dateReported DATE NOT NULL,
    dateRepaired DATE,
    notes VARCHAR(125),
    technicianID VARCHAR(11) NOT NULL,
    plateID VARCHAR(11) NOT NULL,
    
    -- Foreign key constraints
    CONSTRAINT fk_maintenance_technician 
        FOREIGN KEY (technicianID) REFERENCES technicians(technician_id)
        ON DELETE RESTRICT
        ON UPDATE CASCADE,
    
    CONSTRAINT fk_maintenance_vehicle 
        FOREIGN KEY (plateID) REFERENCES vehicles(plateID)
        ON DELETE RESTRICT
        ON UPDATE CASCADE,
    
    -- Indexes for performance
    INDEX idx_maintenance_vehicle (plateID),
    INDEX idx_maintenance_technician (technicianID),
    INDEX idx_maintenance_report_date (dateReported)
);

-- =====================================================
-- 8B. MAINTENANCE_CHEQUE TABLE
-- =====================================================
-- Junction table tracking parts used in maintenance
-- Supports multiple parts per maintenance with quantity tracking
CREATE TABLE maintenance_cheque (
    maintenanceID VARCHAR(11),
    partID VARCHAR(11),
    quantityUsed DECIMAL(10, 2) NOT NULL,
    
    -- Composite primary key
    PRIMARY KEY (maintenanceID, partID),
    
    -- Foreign key constraints
    CONSTRAINT fk_cheque_maintenance 
        FOREIGN KEY (maintenanceID) REFERENCES maintenance(maintenanceID)
        ON DELETE CASCADE
        ON UPDATE CASCADE,
    
    CONSTRAINT fk_cheque_part 
        FOREIGN KEY (partID) REFERENCES parts(part_id)
        ON DELETE RESTRICT
        ON UPDATE CASCADE,
    
    -- Business rule: quantity must be positive
    CONSTRAINT chk_quantity_positive 
        CHECK (quantityUsed > 0),
    
    -- Indexes for performance
    INDEX idx_cheque_maintenance (maintenanceID),
    INDEX idx_cheque_part (partID)
);

-- =====================================================
-- 9. PENALTIES TABLE
-- =====================================================
-- Stores customer penalty records

-- =====================================================
-- 10. DEPLOYMENTS TABLE
-- =====================================================
-- Stores vehicle deployment/transfer records
CREATE TABLE deployments (
    deploymentID VARCHAR(11) PRIMARY KEY,
    plateID VARCHAR(11) NOT NULL,
    locationID VARCHAR(11) NOT NULL,
    startDate DATE NOT NULL,
    endDate DATE NULL,
    
    CONSTRAINT fk_deployment_vehicle 
        FOREIGN KEY (plateID) REFERENCES vehicles(plateID)
        ON DELETE RESTRICT
        ON UPDATE CASCADE,
        
    CONSTRAINT fk_deployment_location 
        FOREIGN KEY (locationID) REFERENCES locations(locationID)
        ON DELETE RESTRICT
        ON UPDATE CASCADE
);

CREATE INDEX idx_deployment_vehicle ON deployments(plateID);
CREATE INDEX idx_deployment_location ON deployments(locationID);


-- =====================================================
-- 11. STAFF TABLE
-- =====================================================
-- Verifies admin access before entering app
CREATE TABLE staff (
    staffID VARCHAR(11) PRIMARY KEY,
    username VARCHAR(30) NOT NULL UNIQUE, 
    staffEmail VARCHAR(80) NOT NULL UNIQUE, 
    password VARCHAR(255) NOT NULL
);

-- Verify
-- SHOW TABLES;
-- DESCRIBE rentals;
-- DESCRIBE deployments;


COMMIT;
