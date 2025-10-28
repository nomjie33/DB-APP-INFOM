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
-- DROP TABLE locations;
-- DROP TABLE customers;


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
    startTime VARCHAR(25) NOT NULL,
    endTime VARCHAR(25),
    customerID VARCHAR(11) NOT NULL,
    plateID VARCHAR(11) NOT NULL,
    locationID VARCHAR(11) NOT NULL,
    rentalDate DATE NOT NULL,
    
    -- Prevent deletion
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
        ON UPDATE CASCADE,
    
    -- BR: endTime must be after startTime (if set)
    CONSTRAINT chk_rental_time 
        CHECK (endTime IS NULL OR endTime > startTime)
);

CREATE INDEX idx_rental_customer ON rentals(customerID);
CREATE INDEX idx_rental_vehicle ON rentals(plateID);
CREATE INDEX idx_rental_date ON rentals(rentalDate);
CREATE INDEX idx_rental_status ON rentals(endTime);

-- =====================================================
-- 7. PAYMENTS TABLE
-- =====================================================
-- Stores payment transaction records

-- =====================================================
-- 8. MAINTENANCE TABLE
-- =====================================================
-- Stores vehicle maintenance/repair records
CREATE TABLE maintenance (
    maintenance_id VARCHAR(11) PRIMARY KEY,
    vehicle_id VARCHAR(11) NOT NULL,
    technician_id VARCHAR(11) NOT NULL,
    part_id VARCHAR(11),
    report_date TIMESTAMP NOT NULL,
    repair_date TIMESTAMP,
    notes TEXT,
    vehicle_status VARCHAR(15),
    
    -- Foreign key constraints
    CONSTRAINT fk_maintenance_vehicle 
        FOREIGN KEY (vehicle_id) REFERENCES vehicles(plateID)
        ON DELETE RESTRICT
        ON UPDATE CASCADE,
    
    CONSTRAINT fk_maintenance_technician 
        FOREIGN KEY (technician_id) REFERENCES technicians(technician_id)
        ON DELETE RESTRICT
        ON UPDATE CASCADE,
    
    CONSTRAINT fk_maintenance_part 
        FOREIGN KEY (part_id) REFERENCES parts(part_id)
        ON DELETE RESTRICT
        ON UPDATE CASCADE,
    
    -- Indexes for performance
    INDEX idx_maintenance_vehicle (vehicle_id),
    INDEX idx_maintenance_technician (technician_id),
    INDEX idx_maintenance_report_date (report_date)
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
    rentalID VARCHAR(25) NOT NULL,
    deploymentDate DATE NOT NULL,
    
    -- Foreign key constraint
    CONSTRAINT fk_deployment_rental 
        FOREIGN KEY (rentalID) REFERENCES rentals(rentalID)
        ON DELETE RESTRICT
        ON UPDATE CASCADE
);

CREATE INDEX idx_rental_customer ON rentals(customerID);
CREATE INDEX idx_rental_vehicle ON rentals(plateID);
CREATE INDEX idx_rental_date ON rentals(rentalDate);
CREATE INDEX idx_rental_status ON rentals(endTime);

-- =====================================================
-- SAMPLE DATA (Optional - for testing)
-- =====================================================
INSERT INTO locations VALUES ('LOC-001', 'Test Location');
SELECT * FROM locations;



COMMIT;
