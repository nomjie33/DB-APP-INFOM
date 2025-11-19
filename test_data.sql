-- =====================================================
-- VEHICLE RENTAL SYSTEM - TEST DATA
-- =====================================================
-- Complete test dataset for development and testing
-- Run this after creating all tables
--
-- USAGE:
-- 1. Make sure all tables are created first
-- 2. In MySQL Workbench: File → Run SQL Script → select test_data.sql
-- 3. Or in MySQL command line: SOURCE test_data.sql;
-- 4. Or copy-paste sections into MySQL Workbench and execute
--
-- NOTES:
-- - Clears existing data before inserting
-- - Maintains referential integrity
-- - Realistic data for e-vehicle rental system
-- - Rentals and Deployments commented out (not ready yet)
-- =====================================================

USE vehicle_rental_db;

-- =====================================================
-- DROP AND RECREATE ALL TABLES FOR CLEAN START
-- =====================================================
-- This ensures a completely fresh start every time
SET FOREIGN_KEY_CHECKS = 0;
SET FOREIGN_KEY_CHECKS = 1;

-- =====================================================
-- Note: Now run database_schema.sql to recreate tables
-- =====================================================
-- This file only contains test data.
-- Make sure to run database_schema.sql FIRST to create the table structure.
-- =====================================================

-- =====================================================
-- 1. LOCATIONS TABLE
-- =====================================================
-- Rental branch locations across Metro Manila

INSERT INTO locations (locationID, name) VALUES
('LOC-001', 'BGC Central Hub'),
('LOC-002', 'Makati Business District'),
('LOC-003', 'Ortigas Center'),
('LOC-004', 'Eastwood City'),
('LOC-005', 'UP Town Center'),
('LOC-006', 'SM Mall of Asia'),
('LOC-007', 'Trinoma'),
('LOC-008', 'Alabang Town Center'),
('LOC-009', 'Ayala Malls Manila Bay'),
('LOC-010', 'Greenhills Shopping Center');

SELECT * FROM locations;

-- =====================================================
-- 2. VEHICLES TABLE
-- =====================================================
-- E-scooters, e-bikes, and e-trikes for rent
-- E-SCOOTERS (Most common, ₱45-60/hour)
-- E-BIKES (₱70-90/hour)
-- E-TRIKES (₱95-120/hour, for couples/families)

INSERT INTO vehicles (plateID, vehicleType, status, rentalPrice) VALUES
-- ACTIVE E-SCOOTERS
('ES-001', 'E-Scooter', 'Available', 50.00),
('ES-002', 'E-Scooter', 'Available', 55.00),
('ES-003', 'E-Scooter', 'In Use', 50.00),
('ES-004', 'E-Scooter', 'Available', 45.00),
('ES-005', 'E-Scooter', 'Maintenance', 48.00),
('ES-006', 'E-Scooter', 'Available', 52.00),
('ES-007', 'E-Scooter', 'Available', 45.00),
('ES-008', 'E-Scooter', 'In Use', 60.00),
('ES-009', 'E-Scooter', 'Available', 58.00),
('ES-010', 'E-Scooter', 'Available', 50.00),
('ES-011', 'E-Scooter', 'Inactive', 45.00),  -- Retired: Old model, battery issues
('ES-012', 'E-Scooter', 'Inactive', 50.00),  -- Retired: Frame damage, beyond repair

-- ACTIVE E-BIKES
('EB-001', 'E-Bike', 'Available', 80.00),
('EB-002', 'E-Bike', 'Available', 75.00),
('EB-003', 'E-Bike', 'In Use', 70.00),
('EB-004', 'E-Bike', 'Available', 78.00),
('EB-005', 'E-Bike', 'Available', 85.00),
('EB-006', 'E-Bike', 'Maintenance', 82.00),
('EB-007', 'E-Bike', 'Available', 72.00),
('EB-008', 'E-Bike', 'Available', 90.00),
('EB-009', 'E-Bike', 'Inactive', 75.00),  -- Retired: Motor failure, not cost-effective to repair
('EB-010', 'E-Bike', 'Inactive', 70.00),  -- Retired: Outdated model, being phased out

-- ACTIVE E-TRIKES
('ET-001', 'E-Trike', 'Available', 100.00),
('ET-002', 'E-Trike', 'Available', 95.00),
('ET-003', 'E-Trike', 'In Use', 105.00),
('ET-004', 'E-Trike', 'Available', 120.00),
('ET-005', 'E-Trike', 'Available', 98.00),
('ET-006', 'E-Trike', 'Inactive', 95.00), -- Retired: Involved in accident, retired from service
('ET-007', 'E-Trike', 'In Use', 105.00),
('ET-008', 'E-Trike', 'Available', 120.00),
('ET-009', 'E-Trike', 'Available', 98.00),
('ET-010', 'E-Trike', 'Inactive', 95.00); -- Retired: Motor failure, not cost-effective to repair

-- =====================================================
-- 3. CITIES TABLE
-- =====================================================
-- Cities in Metro Manila where customers reside

INSERT INTO cities (name) VALUES
('Taguig'),
('Makati'),
('Pasig'),
('Quezon City'),
('Pasay'),
('Muntinlupa'),
('Manila'),
('Mandaluyong'),
('Navotas'),
('Malabon');

SELECT * FROM cities;

-- =====================================================
-- 4. BARANGAYS TABLE
-- =====================================================
-- Barangays in each city

INSERT INTO barangays (cityID, name) VALUES
-- Taguig barangays
(1, 'Bonifacio Global City'),
(1, 'Western Bicutan'),
(1, 'Fort Bonifacio'),
-- Makati barangays
(2, 'Bel-Air'),
(2, 'Poblacion'),
(2, 'San Lorenzo'),
-- Pasig barangays
(3, 'Ortigas Center'),
(3, 'Kapitolyo'),
(3, 'Ugong'),
-- Quezon City barangays
(4, 'Bagumbayan'),
(4, 'Diliman'),
(4, 'Bagong Pag-asa'),
-- Pasay barangays
(5, 'Bay City'),
(5, 'Malibay'),
-- Muntinlupa barangays
(6, 'Alabang'),
(6, 'Tunasan'),
-- Manila barangays
(7, 'Ermita'),
(7, 'Malate'),
-- Mandaluyong barangays
(8, 'Plainview'),
(8, 'Highway Hills'),
-- Navotas barangays
(9, 'San Jose'),
(9, 'San Roque'),
-- Malabon barangays
(10, 'Potrero'),
(10, 'Tonsuya');

SELECT * FROM barangays;

-- =====================================================
-- 5. ADDRESSES TABLE
-- =====================================================
-- Complete addresses for customers

INSERT INTO addresses (barangayID, street) VALUES
-- Taguig addresses
(1, '123 Bonifacio St'),        -- addressID 1
(1, '369 BGC High St'),          -- addressID 2
(3, '555 McKinley Rd'),          -- addressID 3
-- Makati addresses
(4, '456 Ayala Ave'),            -- addressID 4
(4, '741 Makati Ave'),           -- addressID 5
(5, '789 P. Burgos St'),         -- addressID 6
-- Pasig addresses
(7, '789 EDSA'),                 -- addressID 7
(8, '321 Plaza Dr'),             -- addressID 8
-- Quezon City addresses
(10, '321 Eastwood Dr'),         -- addressID 9
(11, '654 UP Campus'),           -- addressID 10
(11, '147 North Ave'),           -- addressID 11
-- Pasay addresses
(13, '987 MOA Complex'),         -- addressID 12
-- Muntinlupa addresses
(15, '258 Alabang Town');        -- addressID 13

SELECT * FROM addresses;

-- =====================================================
-- 6. CUSTOMERS TABLE
-- =====================================================
-- Registered customers with contact information and address references

INSERT INTO customers (customerID, lastName, firstName, contactNumber, addressID, emailAddress) VALUES
('CUST-001', 'Reyes', 'Juan', '09171234567', 1, 'juan.reyes@email.com'),
('CUST-002', 'Santos', 'Maria', '09281234567', 4, 'maria.santos@email.com'),
('CUST-003', 'Cruz', 'Pedro', '09391234567', 7, 'pedro.cruz@email.com'),
('CUST-004', 'Garcia', 'Ana', '09171234568', 9, 'ana.garcia@email.com'),
('CUST-005', 'Mendoza', 'Jose', '09281234568', 10, 'jose.mendoza@email.com'),
('CUST-006', 'Villanueva', 'Sofia', '09391234568', 12, 'sofia.v@email.com'),
('CUST-007', 'Torres', 'Miguel', '09171234569', 11, 'miguel.torres@email.com'),
('CUST-008', 'Ramos', 'Isabel', '09281234569', 13, 'isabel.ramos@email.com'),
('CUST-009', 'Fernandez', 'Carlos', '09391234569', 2, 'carlos.f@email.com'),
('CUST-010', 'Lopez', 'Elena', '09171234570', 5, 'elena.lopez@email.com');

SELECT * FROM customers;

-- =====================================================
-- 7. TECHNICIANS TABLE
-- =====================================================
-- Maintenance staff for vehicle repairs
-- Specializations: ELECTRICAL, MECHANICAL, BATTERY
-- Status: 'Active' for all test technicians (soft delete support)

INSERT INTO technicians (technician_id, last_name, first_name, specialization_id, rate, contact_number, status) VALUES
('TECH-001', 'Santos', 'Mario', 'ELECTRICAL', 350.00, '09171234567', 'Active'),
('TECH-002', 'Garcia', 'Rosa', 'MECHANICAL', 320.00, '09281234567', 'Active'),
('TECH-003', 'Reyes', 'Pedro', 'BODYWORK', 380.00, '09391234567', 'Active'),
('TECH-004', 'Dela Cruz', 'Ana', 'ELECTRICAL', 340.00, '09171234568', 'Active'),
('TECH-005', 'Mendoza', 'Carlos', 'MECHANICAL', 330.00, '09281234568', 'Active'),
('TECH-006', 'Villanueva', 'Sofia', 'BODYWORK', 370.00, '09391234568', 'Active'),
('TECH-007', 'Torres', 'Miguel', 'ELECTRICAL', 360.00, '09171234569', 'Active'),
('TECH-008', 'Ramos', 'Elena', 'MECHANICAL', 315.00, '09281234569', 'Active'),
('TECH-009', 'Gomez', 'Richmond', 'ELECTRICAL', 350.00, '09170559847', 'Active'),
('TECH-010', 'Evangelista', 'Will', 'BODYWORK', 380.00, '09289875563', 'Active');

SELECT * FROM technicians;

-- =====================================================
-- 8. PARTS TABLE
-- =====================================================
-- Spare parts inventory for maintenance
-- Common parts for e-scooters, e-bikes, and e-trikes
-- Status: 'Active' for all test parts (soft delete support)

INSERT INTO parts (part_id, part_name, quantity, price, status) VALUES
('PART-001', 'Lithium Battery Pack', 50, 2500.00, 'Active'),
('PART-002', 'Motor Controller', 30, 1800.00, 'Active'),
('PART-003', 'Brake Pads', 100, 150.00, 'Active'),
('PART-004', 'LED Headlight', 75, 350.00, 'Active'),
('PART-005', 'Tire (Front)', 40, 450.00, 'Active'),
('PART-006', 'Tire (Rear)', 40, 450.00, 'Active'),
('PART-007', 'Throttle Assembly', 25, 280.00, 'Active'),
('PART-008', 'Handlebar Grip', 60, 80.00, 'Active'),
('PART-009', 'Kickstand', 35, 120.00, 'Active'),
('PART-010', 'Chain', 20, 200.00, 'Active'),
('PART-011', 'Brake Cable', 80, 95.00, 'Active'),
('PART-012', 'Display Screen', 15, 1200.00, 'Active'),
('PART-013', 'Seat', 30, 350.00, 'Active'),
('PART-014', 'Pedal Set', 25, 180.00, 'Active'),
('PART-015', 'Rear Light', 70, 150.00, 'Active');

SELECT * FROM rentals;


-- =====================================================
-- 9. RENTALS TABLE
-- =====================================================
-- Active and completed rental transactions
-- startDateTime/endDateTime use DATETIME format (removed redundant rentalDate column)

INSERT INTO rentals (rentalID, customerID, plateID, locationID, pickUpDateTime, startDateTime, endDateTime, status) VALUES
-- Active rentals (ongoing, picked up - startDateTime is set by admin)
('RNT-001', 'CUST-003', 'ES-003', 'LOC-001', '2024-10-28 09:00:00', '2024-10-28 09:05:00', NULL, 'Active'),
('RNT-002', 'CUST-009', 'ES-008', 'LOC-001', '2024-10-28 10:30:00', '2024-10-28 10:35:00', NULL, 'Active'),
('RNT-003', 'CUST-005', 'EB-003', 'LOC-005', '2024-10-28 08:00:00', '2024-10-28 08:10:00', NULL, 'Active'),
('RNT-004', 'CUST-007', 'ET-003', 'LOC-001', '2024-10-28 11:00:00', '2024-10-28 11:05:00', NULL, 'Active'),
('RNT-042', 'CUST-005', 'ES-010', 'LOC-005', '2024-11-10 09:00:00', '2024-11-10 09:05:00', NULL, 'Active'),
('RNT-043', 'CUST-006', 'EB-007', 'LOC-006', '2024-11-19 10:00:00', '2024-11-19 10:05:00', NULL, 'Active'),
('RNT-044', 'CUST-007', 'ET-004', 'LOC-001', '2024-11-21 11:00:00', '2024-11-21 11:05:00', NULL, 'Active'),
('RNT-045', 'CUST-008', 'ES-001', 'LOC-008', '2024-12-15 12:00:00', '2024-12-15 12:05:00', NULL, 'Active'),
('RNT-046', 'CUST-009', 'EB-008', 'LOC-007', '2024-12-29 13:00:00', '2024-12-29 13:05:00', NULL, 'Active'),
('RNT-047', 'CUST-010', 'ET-005', 'LOC-006', '2024-12-29 14:00:00', '2024-12-29 14:05:00', NULL, 'Active'),
-- Completed rentals with precise start and end times (pickUpDateTime set at booking, startDateTime when customer arrived)
('RNT-005', 'CUST-001', 'ES-001', 'LOC-001', '2024-10-27 09:00:00', '2024-10-27 09:05:00', '2024-10-27 11:45:00', 'Completed'),
('RNT-006', 'CUST-002', 'EB-001', 'LOC-002', '2024-10-27 14:00:00', '2024-10-27 14:10:00', '2024-10-27 17:30:00', 'Completed'),
('RNT-007', 'CUST-004', 'ES-002', 'LOC-004', '2024-10-26 10:00:00', '2024-10-26 10:05:00', '2024-10-26 12:50:00', 'Completed'),
('RNT-008', 'CUST-006', 'ET-001', 'LOC-006', '2024-10-26 15:00:00', '2024-10-26 15:10:00', '2024-10-26 18:00:00', 'Completed'),
('RNT-009', 'CUST-008', 'EB-002', 'LOC-008', '2024-10-25 09:00:00', '2024-10-25 09:05:00', '2024-10-25 11:30:00', 'Completed'),
('RNT-010', 'CUST-010', 'ES-004', 'LOC-002', '2024-10-25 13:00:00', '2024-10-25 13:05:00', '2024-10-25 16:15:00', 'Completed'),
('RNT-011', 'CUST-001', 'ES-006', 'LOC-001', '2024-10-24 10:00:00', '2024-10-24 10:05:00', '2024-10-24 12:00:00', 'Completed'),
('RNT-012', 'CUST-003', 'EB-004', 'LOC-003', '2024-10-24 14:00:00', '2024-10-24 14:05:00', '2024-10-24 16:30:00', 'Completed'),
('RNT-013', 'CUST-002', 'ES-007', 'LOC-002', '2024-10-23 09:00:00', '2024-10-23 09:10:00', '2024-10-23 11:00:00', 'Completed'),
('RNT-014', 'CUST-004', 'ET-002', 'LOC-004', '2024-10-23 15:00:00', '2024-10-23 15:05:00', '2024-10-23 18:00:00', 'Completed'),
('RNT-015', 'CUST-006', 'ES-009', 'LOC-006', '2024-10-22 10:00:00', '2024-10-22 10:10:00', '2024-10-22 13:00:00', 'Completed'),
('RNT-029', 'CUST-001', 'ES-005', 'LOC-001', '2024-02-15 08:00:00', '2024-02-15 08:05:00', '2024-02-15 12:00:00', 'Completed'),
('RNT-030', 'CUST-002', 'ET-002', 'LOC-004', '2023-07-16 09:00:00', '2023-07-16 09:05:00', '2023-07-16 13:30:00', 'Completed'),
('RNT-031', 'CUST-003', 'ET-002', 'LOC-004', '2023-03-17 10:00:00', '2023-03-17 10:05:00', '2023-03-17 14:00:00', 'Completed'),
('RNT-032', 'CUST-004', 'ET-002', 'LOC-004', '2022-08-18 11:00:00', '2022-08-18 11:05:00', '2022-08-18 15:30:00', 'Completed'),
('RNT-033', 'CUST-005', 'ET-002', 'LOC-004', '2022-04-19 09:00:00', '2022-04-19 09:05:00', '2022-04-19 12:00:00', 'Completed'),
('RNT-034', 'CUST-006', 'ET-001', 'LOC-006', '2021-11-20 10:00:00', '2021-11-20 10:05:00', '2021-11-20 13:30:00', 'Completed'),
('RNT-035', 'CUST-007', 'ES-005', 'LOC-001', '2021-05-21 08:30:00', '2021-05-21 08:35:00', '2021-05-21 11:00:00', 'Completed'),
('RNT-036', 'CUST-008', 'ES-005', 'LOC-001', '2020-09-22 14:00:00', '2020-09-22 14:05:00', '2020-09-22 17:00:00', 'Completed'),
('RNT-037', 'CUST-009', 'EB-006', 'LOC-007', '2024-06-10 09:00:00', '2024-06-10 09:05:00', '2024-06-10 13:00:00', 'Completed'),
('RNT-038', 'CUST-010', 'ET-001', 'LOC-006', '2023-12-12 10:00:00', '2023-12-12 10:05:00', '2023-12-12 14:30:00', 'Completed'),
('RNT-039', 'CUST-001', 'EB-006', 'LOC-007', '2022-03-14 08:00:00', '2022-03-14 08:05:00', '2022-03-14 12:00:00', 'Completed'),
('RNT-040', 'CUST-002', 'EB-006', 'LOC-007', '2021-07-16 11:00:00', '2021-07-16 11:05:00', '2021-07-16 15:00:00', 'Completed'),
('RNT-041', 'CUST-003', 'EB-006', 'LOC-007', '2020-10-18 09:00:00', '2020-10-18 09:05:00', '2020-10-18 13:30:00', 'Completed'),
-- Additional rentals for ES-005 (now in Maintenance) - heavy usage before defect
('RNT-016', 'CUST-001', 'ES-005', 'LOC-001', '2024-10-15 08:00:00', '2024-10-15 08:05:00', '2024-10-15 12:00:00', 'Completed'),
('RNT-017', 'CUST-002', 'ES-005', 'LOC-001', '2024-10-16 09:00:00', '2024-10-16 09:05:00', '2024-10-16 13:30:00', 'Completed'),
('RNT-018', 'CUST-003', 'ES-005', 'LOC-001', '2024-10-17 10:00:00', '2024-10-17 10:05:00', '2024-10-17 14:00:00', 'Completed'),
('RNT-019', 'CUST-004', 'ES-005', 'LOC-001', '2024-10-18 11:00:00', '2024-10-18 11:05:00', '2024-10-18 15:30:00', 'Completed'),
('RNT-020', 'CUST-005', 'ES-005', 'LOC-001', '2024-10-19 09:00:00', '2024-10-19 09:05:00', '2024-10-19 12:00:00', 'Completed'),
('RNT-021', 'CUST-006', 'ES-005', 'LOC-001', '2024-10-20 10:00:00', '2024-10-20 10:05:00', '2024-10-20 13:30:00', 'Completed'),
('RNT-022', 'CUST-007', 'ES-005', 'LOC-001', '2024-10-21 08:30:00', '2024-10-21 08:35:00', '2024-10-21 11:00:00', 'Completed'),
('RNT-023', 'CUST-008', 'ES-005', 'LOC-001', '2024-10-22 14:00:00', '2024-10-22 14:05:00', '2024-10-22 17:00:00', 'Completed'),
-- Additional rentals for EB-006 (now in Maintenance) - moderate usage
('RNT-024', 'CUST-009', 'EB-006', 'LOC-007', '2024-10-10 09:00:00', '2024-10-10 09:05:00', '2024-10-10 13:00:00', 'Completed'),
('RNT-025', 'CUST-010', 'EB-006', 'LOC-007', '2024-10-12 10:00:00', '2024-10-12 10:05:00', '2024-10-12 14:30:00', 'Completed'),
('RNT-026', 'CUST-001', 'EB-006', 'LOC-007', '2024-10-14 08:00:00', '2024-10-14 08:05:00', '2024-10-14 12:00:00', 'Completed'),
('RNT-027', 'CUST-002', 'EB-006', 'LOC-007', '2024-10-16 11:00:00', '2024-10-16 11:05:00', '2024-10-16 15:00:00', 'Completed'),
('RNT-028', 'CUST-003', 'EB-006', 'LOC-007', '2024-10-18 09:00:00', '2024-10-18 09:05:00', '2024-10-18 13:30:00', 'Completed');

SELECT * FROM rentals;


-- =====================================================
-- 10. PAYMENTS TABLE
-- =====================================================
-- Payment records for rentals
-- Note: Only includes payments for completed rentals (RNT-005 to RNT-015)
-- Active rentals (RNT-001 to RNT-004) have no payments yet
-- Status: 'Active' for all test records (soft delete support)

INSERT INTO payments (paymentID, amount, rentalID, paymentDate, status) VALUES
-- Payments for completed rentals (full payments based on rental fees)
('PAY-001', 5.73, 'RNT-005', '2024-10-27', 'Active'),   -- ES-001: 2.75h × ₱50/day
('PAY-002', 10.40, 'RNT-006', '2024-10-27', 'Active'),  -- EB-001: 3.5h × ₱80/day
('PAY-003', 6.42, 'RNT-007', '2024-10-26', 'Active'),   -- ES-002: 2.83h × ₱55/day
('PAY-004', 12.50, 'RNT-008', '2024-10-26', 'Active'),  -- ET-001: 3h × ₱100/day
('PAY-005', 7.81, 'RNT-009', '2024-10-25', 'Active'),   -- EB-002: 2.5h × ₱75/day
('PAY-006', 6.09, 'RNT-010', '2024-10-25', 'Active'),   -- ES-004: 3.25h × ₱45/day
('PAY-007', 4.33, 'RNT-011', '2024-10-24', 'Active'),   -- ES-006: 2h × ₱52/day
('PAY-008', 8.13, 'RNT-012', '2024-10-24', 'Active'),   -- EB-004: 2.5h × ₱78/day
('PAY-009', 3.75, 'RNT-013', '2024-10-23', 'Active'),   -- ES-007: 2h × ₱45/day
('PAY-010', 11.88, 'RNT-014', '2024-10-23', 'Active'),  -- ET-002: 3h × ₱95/day
('PAY-011', 7.25, 'RNT-015', '2024-10-22', 'Active'),   -- ES-009: 3h × ₱58/day
('PAY-029', 8.00, 'RNT-029', '2024-02-15', 'Active'),   -- ES-005: 4h × ₱48/day
('PAY-030', 9.50, 'RNT-030', '2023-07-16', 'Active'),   -- ET-002: 4.5h × ₱95/day
('PAY-031', 9.50, 'RNT-031', '2023-03-17', 'Active'),   -- ET-002: 4.5h × ₱95/day
('PAY-032', 9.50, 'RNT-032', '2022-08-18', 'Active'),   -- ET-002: 4.5h × ₱95/day
('PAY-033', 9.50, 'RNT-033', '2022-04-19', 'Active'),   -- ET-002: 4.5h × ₱95/day
('PAY-034', 12.50, 'RNT-034', '2021-11-20', 'Active'),  -- ET-001: 3h × ₱100/day
('PAY-035', 8.00, 'RNT-035', '2021-05-21', 'Active'),   -- ES-005: 4h × ₱48/day
('PAY-036', 8.00, 'RNT-036', '2020-09-22', 'Active'),   -- ES-005: 4h × ₱48/day
('PAY-037', 13.67, 'RNT-037', '2024-06-10', 'Active'),  -- EB-006: 4h × ₱82/day
('PAY-038', 12.50, 'RNT-038', '2023-12-12', 'Active'),  -- ET-001: 3h × ₱100/day
('PAY-039', 13.67, 'RNT-039', '2022-03-14', 'Active'),  -- EB-006: 4h × ₱82/day
('PAY-040', 13.67, 'RNT-040', '2021-07-16', 'Active'),  -- EB-006: 4h × ₱82/day
('PAY-041', 13.67, 'RNT-041', '2020-10-18', 'Active'),  -- EB-006: 4h × ₱82/day
-- Additional payments for ES-005 rentals (before maintenance)
('PAY-016', 8.00, 'RNT-016', '2024-10-15', 'Active'),   -- ES-005: 4h × ₱48/day
('PAY-017', 9.00, 'RNT-017', '2024-10-16', 'Active'),   -- ES-005: 4.5h × ₱48/day
('PAY-018', 8.00, 'RNT-018', '2024-10-17', 'Active'),   -- ES-005: 4h × ₱48/day
('PAY-019', 9.00, 'RNT-019', '2024-10-18', 'Active'),   -- ES-005: 4.5h × ₱48/day
('PAY-020', 6.00, 'RNT-020', '2024-10-19', 'Active'),   -- ES-005: 3h × ₱48/day
('PAY-021', 7.00, 'RNT-021', '2024-10-20', 'Active'),   -- ES-005: 3.5h × ₱48/day
('PAY-022', 5.00, 'RNT-022', '2024-10-21', 'Active'),   -- ES-005: 2.5h × ₱48/day
('PAY-023', 6.00, 'RNT-023', '2024-10-22', 'Active'),   -- ES-005: 3h × ₱48/day
-- Additional payments for EB-006 rentals (before maintenance)
('PAY-024', 13.67, 'RNT-024', '2024-10-10', 'Active'),  -- EB-006: 4h × ₱82/day
('PAY-025', 15.42, 'RNT-025', '2024-10-12', 'Active'),  -- EB-006: 4.5h × ₱82/day
('PAY-026', 13.67, 'RNT-026', '2024-10-14', 'Active'),  -- EB-006: 4h × ₱82/day
('PAY-027', 13.67, 'RNT-027', '2024-10-16', 'Active'),  -- EB-006: 4h × ₱82/day
('PAY-028', 15.42, 'RNT-028', '2024-10-18', 'Active');  -- EB-006: 4.5h × ₱82/day

-- =====================================================
-- Placeholder payments for ongoing rentals (one record per rental)
-- Business rule: one payment row exists for every rental. For ongoing
-- rentals the payment amount is set to 0.00 and paymentDate set to the
-- rentalDate as a placeholder; the real amount and paymentDate should be
-- updated when the vehicle is returned and payment is processed.
-- These correspond to RNT-001..RNT-004 (active/ongoing rentals)

INSERT INTO payments (paymentID, amount, rentalID, paymentDate, status) VALUES
('PAY-012', 0.00, 'RNT-001', '2024-10-28', 'Active'),
('PAY-013', 0.00, 'RNT-002', '2024-10-28', 'Active'),
('PAY-014', 0.00, 'RNT-003', '2024-10-28', 'Active'),
('PAY-015', 0.00, 'RNT-004', '2024-10-28', 'Active'),
('PAY-042', 0.00, 'RNT-042', '2024-11-10', 'Active'),
('PAY-043', 0.00, 'RNT-043', '2024-11-19', 'Active'),
('PAY-044', 0.00, 'RNT-044', '2024-11-21', 'Active'),
('PAY-045', 0.00, 'RNT-045', '2024-12-15', 'Active'),
('PAY-046', 0.00, 'RNT-046', '2024-12-29', 'Active'),
('PAY-047', 0.00, 'RNT-047', '2024-12-29', 'Active');

SELECT * FROM payments;


-- =====================================================
-- 11. MAINTENANCE TABLE (Refactored)
-- =====================================================
-- Maintenance records without part information
-- Parts tracked separately in maintenance_cheque table
-- startDateTime/endDateTime use DATETIME format, hoursWorked removed (calculated dynamically)

INSERT INTO maintenance (maintenanceID, startDateTime, endDateTime, totalCost, notes, technicianID, plateID, status) VALUES
-- Recent maintenance - E-Scooters (completed) - All Active
('MAINT-001', '2024-10-20 08:00:00', '2024-10-21 11:30:00', 22125.00, 'Battery replacement - capacity degraded to 65%', 'TECH-001', 'ES-001', 'Active'),
('MAINT-002', '2024-10-22 09:00:00', '2024-10-22 10:00:00', 770.00, 'Routine brake maintenance - 75% worn', 'TECH-002', 'ES-002', 'Active'),
('MAINT-003', '2024-10-25 10:00:00', '2024-10-26 12:30:00', 17540.00, 'Motor controller malfunction - replaced unit', 'TECH-005', 'ES-005', 'Active'),
('MAINT-004', '2024-10-23 13:00:00', '2024-10-23 14:30:00', 1475.00, 'LED headlight and throttle replacement', 'TECH-001', 'ES-006', 'Active'),
('MAINT-005', '2024-10-24 09:00:00', '2024-10-24 09:45:00', 540.00, 'Front tire puncture - replaced', 'TECH-002', 'ES-007', 'Active'),
('MAINT-006', '2024-10-21 14:00:00', '2024-10-21 14:30:00', 515.00, 'Throttle assembly loose - replaced', 'TECH-004', 'ES-009', 'Active'),
('MAINT-007', '2024-10-19 08:00:00', '2024-10-19 08:30:00', 310.00, 'Brake cable fraying - preventive', 'TECH-002', 'ES-010', 'Active'),

-- E-Bikes maintenance (completed)
('MAINT-008', '2024-10-18 09:00:00', '2024-10-19 13:00:00', 22820.00, 'Battery and display replacement', 'TECH-003', 'EB-001', 'Active'),
('MAINT-009', '2024-10-20 10:00:00', '2024-10-21 11:15:00', 8780.00, 'Chain and brake maintenance', 'TECH-002', 'EB-002', 'Active'),
('MAINT-010', '2024-10-22 14:00:00', '2024-10-22 15:00:00', 910.00, 'Brake pads and cables replaced', 'TECH-005', 'EB-004', 'Active'),
('MAINT-011', '2024-10-23 09:00:00', '2024-10-23 11:00:00', 1500.00, 'Display screen malfunction', 'TECH-001', 'EB-005', 'Active'),
('MAINT-012', '2024-10-26 08:00:00', '2024-10-27 11:00:00', 22585.00, 'Battery capacity critical - emergency', 'TECH-006', 'EB-006', 'Active'),
('MAINT-013', '2024-10-24 13:00:00', '2024-10-24 14:30:00', 1130.00, 'Pedal and seat replacement', 'TECH-002', 'EB-007', 'Active'),
('MAINT-014', '2024-10-25 09:00:00', '2024-10-25 10:00:00', 710.00, 'Rear light and brake maintenance', 'TECH-004', 'EB-008', 'Active'),

-- E-Trikes maintenance (completed)
('MAINT-015', '2024-10-21 10:00:00', '2024-10-22 11:15:00', 9330.00, 'Tire and brake inspection', 'TECH-002', 'ET-001', 'Active'),
('MAINT-016', '2024-10-19 09:00:00', '2024-10-20 11:30:00', 22510.00, 'Battery and controller check', 'TECH-003', 'ET-002', 'Active'),
('MAINT-017', '2024-10-23 13:00:00', '2024-10-23 15:00:00', 1620.00, 'Complete brake system service', 'TECH-005', 'ET-004', 'Active'),
('MAINT-018', '2024-10-24 08:00:00', '2024-10-25 10:30:00', 18172.50, 'Motor controller replacement', 'TECH-007', 'ET-005', 'Active'),

-- Additional maintenance for ES-005 (multiple incidents in October 2024)
('MAINT-021', '2024-10-12 08:00:00', '2024-10-12 10:30:00', 1000.00, 'Brake cable replacement', 'TECH-002', 'ES-005', 'Active'),
('MAINT-022', '2024-10-16 14:00:00', '2024-10-17 09:00:00', 7550.00, 'Display screen glitching', 'TECH-001', 'ES-005', 'Active'),
-- Additional maintenance for EB-006 (multiple incidents in October 2024)
('MAINT-023', '2024-10-08 09:00:00', '2024-10-09 11:00:00', 8570.00, 'Chain replacement due to wear', 'TECH-002', 'EB-006', 'Active'),
('MAINT-024', '2024-10-15 08:00:00', '2024-10-15 10:00:00', 1070.00, 'Rear tire puncture', 'TECH-005', 'EB-006', 'Active'),

-- Ongoing maintenance (not yet completed - endDateTime is NULL)
('MAINT-019', '2024-10-27 09:00:00', NULL, 0.00, 'Brake inspection in progress', 'TECH-002', 'ES-005', 'Active'),
('MAINT-020', '2024-10-27 10:00:00', NULL, 0.00, 'Seat replacement - awaiting parts', 'TECH-006', 'EB-006', 'Active');

SELECT * FROM maintenance;

SELECT * FROM maintenance;

-- =====================================================
-- 8B. MAINTENANCE_CHEQUE TABLE (New)
-- =====================================================
-- Parts used in each maintenance record with quantities
-- Status: 'Active' for all test records (soft delete support)

INSERT INTO maintenance_cheque (maintenanceID, partID, quantityUsed, status) VALUES
-- MAINT-001: Battery replacement
('MAINT-001', 'PART-001', 1.00, 'Active'),

-- MAINT-002: Brake maintenance
('MAINT-002', 'PART-003', 2.00, 'Active'),
('MAINT-002', 'PART-011', 1.00, 'Active'),

-- MAINT-003: Motor controller
('MAINT-003', 'PART-002', 1.00, 'Active'),

-- MAINT-004: Headlight and throttle
('MAINT-004', 'PART-004', 1.00, 'Active'),
('MAINT-004', 'PART-007', 1.00, 'Active'),

-- MAINT-005: Tire replacement
('MAINT-005', 'PART-005', 1.00, 'Active'),

-- MAINT-006: Throttle only
('MAINT-006', 'PART-007', 1.00, 'Active'),

-- MAINT-007: Brake cable
('MAINT-007', 'PART-011', 1.00, 'Active'),

-- MAINT-008: Battery and display
('MAINT-008', 'PART-001', 1.00, 'Active'),
('MAINT-008', 'PART-012', 1.00, 'Active'),

-- MAINT-009: Chain and brakes
('MAINT-009', 'PART-010', 1.00, 'Active'),
('MAINT-009', 'PART-003', 2.00, 'Active'),

-- MAINT-010: Brake system
('MAINT-010', 'PART-003', 2.00, 'Active'),
('MAINT-010', 'PART-011', 2.00, 'Active'),

-- MAINT-011: Display only
('MAINT-011', 'PART-012', 1.00, 'Active'),

-- MAINT-012: Battery replacement
('MAINT-012', 'PART-001', 1.00, 'Active'),

-- MAINT-013: Pedals and seat
('MAINT-013', 'PART-014', 1.00, 'Active'),
('MAINT-013', 'PART-013', 1.00, 'Active'),

-- MAINT-014: Light and brakes
('MAINT-014', 'PART-015', 1.00, 'Active'),
('MAINT-014', 'PART-003', 2.00, 'Active'),

-- MAINT-015: Tire and brakes
('MAINT-015', 'PART-006', 1.00, 'Active'),
('MAINT-015', 'PART-003', 2.00, 'Active'),

-- MAINT-016: Battery and controller
('MAINT-016', 'PART-001', 1.00, 'Active'),
('MAINT-016', 'PART-002', 1.00, 'Active'),

-- MAINT-017: Complete brake system (uses more parts)
('MAINT-017', 'PART-003', 4.00, 'Active'),
('MAINT-017', 'PART-011', 2.00, 'Active'),

-- MAINT-018: Motor controller
('MAINT-018', 'PART-002', 1.00, 'Active'),

-- MAINT-019: Brake maintenance in progress
('MAINT-019', 'PART-003', 2.00, 'Active'),

-- MAINT-020: Seat replacement (awaiting delivery - no parts used yet)
('MAINT-020', 'PART-013', 1.00, 'Active'),

-- MAINT-021: Brake cable (ES-005)
('MAINT-021', 'PART-011', 1.00, 'Active'),

-- MAINT-022: Display screen (ES-005)
('MAINT-022', 'PART-012', 1.00, 'Active'),

-- MAINT-023: Chain replacement (EB-006)
('MAINT-023', 'PART-010', 1.00, 'Active'),

-- MAINT-024: Rear tire (EB-006)
('MAINT-024', 'PART-006', 1.00, 'Active');

SELECT * FROM maintenance_cheque;



-- =====================================================
-- 12. PENALTIES TABLE
-- =====================================================
-- Penalty records for damage/repair costs charged to customers
-- Linked to maintenance records via maintenanceID

INSERT INTO penalty (penaltyID, rentalID, totalPenalty, penaltyStatus, maintenanceID, dateIssued, status) VALUES
-- Penalties for completed maintenance (customer charged for damages)
-- status field: 'Active' = penalty is active, 'Inactive' = soft deleted (cancelled/voided)
('PEN-001', 'RNT-005', 3725.00, 'UNPAID', 'MAINT-001', '2024-10-21', 'Active'),  -- Battery replacement: 3.5h × ₱350 + ₱2500 = ₱3,725
('PEN-002', 'RNT-007', 745.00, 'PAID', 'MAINT-002', '2024-10-22', 'Active'),     -- Brake maintenance: 1.0h × ₱330 + (2×₱150 + ₱95) = ₱725
('PEN-003', 'RNT-006', 3535.00, 'UNPAID', 'MAINT-008', '2024-10-19', 'Active'),  -- Battery + display: 4.0h × ₱380 + (₱2500 + ₱1200) = ₱5,220
('PEN-004', 'RNT-009', 565.00, 'PAID', 'MAINT-009', '2024-10-21', 'Active'),     -- Chain + brakes: 1.25h × ₱320 + (₱200 + 2×₱150) = ₱900
('PEN-005', 'RNT-012', 1275.00, 'UNPAID', 'MAINT-011', '2024-10-23', 'Active'),  -- Display screen: 2.0h × ₱350 + ₱1200 = ₱1,900
('PEN-006', 'RNT-013', 562.50, 'PAID', 'MAINT-004', '2024-10-23', 'Active'),     -- Headlight + throttle: 1.5h × ₱350 + (₱350 + ₱280) = ₱1,155
('PEN-007', 'RNT-014', 1470.00, 'UNPAID', 'MAINT-016', '2024-10-20', 'Active'),  -- Battery + controller: 2.5h × ₱380 + (₱2500 + ₱1800) = ₱5,250
('PEN-008', 'RNT-015', 600.00, 'PAID', 'MAINT-005', '2024-10-24', 'Active'),     -- Tire replacement: 0.75h × ₱320 + ₱450 = ₱690
('PEN-009', 'RNT-011', 1130.00, 'UNPAID', 'MAINT-013', '2024-10-24', 'Active'),  -- Pedal + seat: 1.5h × ₱320 + (₱180 + ₱350) = ₱1,010
('PEN-010', 'RNT-010', 925.00, 'PAID', 'MAINT-017', '2024-10-23', 'Active');     -- Complete brake system: 2.0h × ₱330 + (4×₱150 + 2×₱95) = ₱1,450

SELECT * FROM penalty;

-- =====================================================
-- 13. DEPLOYMENTS TABLE (NOT READY YET - COMMENTED OUT)
-- =====================================================
-- Vehicle location history and movements

INSERT INTO deployments (deploymentID, plateID, locationID, startDate, endDate, status) VALUES
('DEP-001', 'ES-001', 'LOC-001', '2024-10-01', NULL, 'Active'),
('DEP-002', 'ES-002', 'LOC-001', '2024-10-01', NULL, 'Active'),
('DEP-003', 'ES-003', 'LOC-001', '2024-10-01', NULL, 'Active'),
('DEP-004', 'ES-004', 'LOC-002', '2024-10-01', NULL, 'Active'),
('DEP-005', 'ES-006', 'LOC-003', '2024-10-01', NULL, 'Active'),
('DEP-006', 'ES-007', 'LOC-004', '2024-10-01', NULL, 'Active'),
('DEP-007', 'ES-008', 'LOC-001', '2024-10-15', NULL, 'Active'),
('DEP-008', 'ES-009', 'LOC-005', '2024-10-01', NULL, 'Active'),
('DEP-009', 'ES-010', 'LOC-006', '2024-10-01', NULL, 'Active'),
('DEP-010', 'EB-001', 'LOC-002', '2024-10-01', NULL, 'Active'),
('DEP-011', 'EB-002', 'LOC-002', '2024-10-01', NULL, 'Active'),
('DEP-012', 'EB-003', 'LOC-005', '2024-10-01', NULL, 'Active'),
('DEP-013', 'EB-004', 'LOC-001', '2024-10-01', NULL, 'Active'),
('DEP-014', 'EB-005', 'LOC-007', '2024-10-01', NULL, 'Active'),
('DEP-015', 'EB-007', 'LOC-003', '2024-10-01', NULL, 'Active'),
('DEP-016', 'EB-008', 'LOC-008', '2024-10-01', NULL, 'Active'),
('DEP-017', 'ET-001', 'LOC-006', '2024-10-01', NULL, 'Active'),
('DEP-018', 'ET-002', 'LOC-001', '2024-10-01', NULL, 'Active'),
('DEP-019', 'ET-003', 'LOC-001', '2024-10-20', NULL, 'Active'),
('DEP-020', 'ET-004', 'LOC-002', '2024-10-01', NULL, 'Active'),
('DEP-021', 'ET-005', 'LOC-004', '2024-10-01', NULL, 'Active'),
('DEP-022', 'ES-005', 'LOC-001', '2024-10-01', '2024-10-26', 'Completed'),
('DEP-023', 'ES-005', 'LOC-002', '2024-10-26', NULL, 'Active'),
('DEP-024', 'EB-006', 'LOC-007', '2024-10-01', '2024-10-25', 'Completed'),
('DEP-025', 'EB-006', 'LOC-002', '2024-10-25', NULL, 'Active'),
('DEP-026', 'ES-008', 'LOC-003', '2024-10-01', '2024-10-15', 'Completed'),
('DEP-027', 'ET-003', 'LOC-005', '2024-10-01', '2024-10-20', 'Completed');
-- =====================================================
-- STAFF TABLE
-- =====================================================
INSERT INTO staff (staffID, username, staffEmail, password) VALUES
('STAFF-001', 'nomjie33', 'naomi_reyes@uvr.com', 'nomsUVR124'),
('STAFF-002', 'cj1ayi', 'nate_tan@uvr.com', 'nateUVR124'),
('STAFF-003', 'catDespair', 'airon_bantillo@uvr.com', 'AironUVR124'),
('STAFF-004', 'alexxxxxxxxx', 'alexandra_gonzales@uvr.com', 'AlexUVR124');

SELECT * FROM staff;

-- =====================================================
-- VERIFICATION QUERIES
-- =====================================================
-- Run these to verify data was inserted correctly

-- Check all table counts
SELECT 'Locations' as TableName, COUNT(*) as RecordCount FROM locations
UNION ALL
SELECT 'Vehicles', COUNT(*) FROM vehicles
UNION ALL
SELECT 'Customers', COUNT(*) FROM customers
UNION ALL
SELECT 'Technicians', COUNT(*) FROM technicians
UNION ALL
SELECT 'Parts', COUNT(*) FROM parts
UNION ALL
SELECT 'Rentals', COUNT(*) FROM rentals
UNION ALL
SELECT 'Payments', COUNT(*) FROM payments
UNION ALL
SELECT 'Maintenance', COUNT(*) FROM maintenance
UNION ALL
SELECT 'Maintenance_Cheque', COUNT(*) FROM maintenance_cheque
UNION ALL
SELECT 'Penalties', COUNT(*) FROM penalty
UNION ALL
SELECT 'Deployments', COUNT(*) FROM deployments
UNION ALL
SELECT 'Staff', COUNT(*) FROM staff;

-- Check vehicle distribution by status
SELECT status, COUNT(*) as count
FROM vehicles
GROUP BY status;

-- Check vehicle distribution by type
SELECT vehicleType, COUNT(*) as count
FROM vehicles
GROUP BY vehicleType;

-- Check technician distribution by specialization
SELECT specialization_id, COUNT(*) as count
FROM technicians
GROUP BY specialization_id;

-- Check maintenance records by status (completed vs in progress)
SELECT
    CASE
        WHEN endDateTime IS NULL THEN 'In Progress'
        ELSE 'Completed'
    END as MaintenanceStatus,
    COUNT(*) as count
FROM maintenance
GROUP BY MaintenanceStatus;

-- Check payment totals by rental
SELECT
    r.rentalID,
    r.customerID,
    r.plateID,
    COALESCE(SUM(p.amount), 0) as total_paid
FROM rentals r
LEFT JOIN payments p ON r.rentalID = p.rentalID
GROUP BY r.rentalID, r.customerID, r.plateID
ORDER BY r.rentalID;

-- Check most used parts in maintenance
SELECT p.part_name, SUM(mc.quantityUsed) as total_quantity_used, COUNT(mc.maintenanceID) as times_used
FROM parts p
LEFT JOIN maintenance_cheque mc ON p.part_id = mc.partID
GROUP BY p.part_id, p.part_name
ORDER BY total_quantity_used DESC
LIMIT 10;

-- Check technician workload
SELECT
    t.technician_id,
    CONCAT(t.first_name, ' ', t.last_name) as technician_name,
    t.specialization_id,
    COUNT(m.maintenanceID) as jobs_completed
FROM technicians t
LEFT JOIN maintenance m ON t.technician_id = m.technicianID
GROUP BY t.technician_id, t.first_name, t.last_name, t.specialization_id
ORDER BY jobs_completed DESC;

-- Check active vs completed rentals
SELECT
    CASE
        WHEN endDateTime IS NULL THEN 'Active'
        ELSE 'Completed'
    END as RentalStatus,
    COUNT(*) as count
FROM rentals
GROUP BY RentalStatus;

-- Check vehicles at each location (current deployment)
SELECT l.name, COUNT(d.plateID) as VehicleCount
FROM locations l
LEFT JOIN deployments d ON l.locationID = d.locationID AND d.endDate IS NULL
GROUP BY l.locationID, l.name
ORDER BY VehicleCount DESC;

-- =====================================================
-- END OF TEST DATA
-- =====================================================

SELECT 'Test data loaded successfully!' as Status;
SELECT 'Data Summary:' as Info;
SELECT '  - 10 Locations' as Summary
UNION ALL SELECT '  - 32 Vehicles'
UNION ALL SELECT '  - 10 Customers'
UNION ALL SELECT '  - 10 Technicians'
UNION ALL SELECT '  - 15 Parts'
UNION ALL SELECT '  - 47 Rentals'
UNION ALL SELECT '  - 47 Payments'
UNION ALL SELECT '  - 24 Maintenance Records'
UNION ALL SELECT '  - 34 Maintenance Parts Usage Records'
UNION ALL SELECT '  - 10 Penalties'
UNION ALL SELECT '  - 27 Deployments';

SELECT 'All core tables populated with test data!' as Note;