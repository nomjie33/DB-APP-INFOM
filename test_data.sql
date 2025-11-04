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

-- Disable foreign key checks temporarily for clean slate
SET FOREIGN_KEY_CHECKS = 0;

-- =====================================================
-- CLEAR ALL EXISTING DATA
-- =====================================================

DELETE FROM penalty;
DELETE FROM payments;
DELETE FROM maintenance_cheque;
DELETE FROM maintenance;
-- DELETE FROM deployments;  -- Commented out - not ready yet
-- DELETE FROM rentals;        -- Commented out - not ready yet
DELETE FROM customers;
DELETE FROM vehicles;
DELETE FROM technicians;
DELETE FROM parts;
DELETE FROM locations;

-- Re-enable foreign key checks
SET FOREIGN_KEY_CHECKS = 1;

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
('LOC-008', 'Alabang Town Center');

SELECT * FROM locations;

-- =====================================================
-- 2. VEHICLES TABLE
-- =====================================================
-- E-scooters, e-bikes, and e-trikes for rent
-- E-SCOOTERS (Most common, ₱45-60/hour)
-- E-BIKES (₱70-90/hour)
-- E-TRIKES (₱95-120/hour, for couples/families)

INSERT INTO vehicles (plateID, vehicleType, vehicleModel, status, rentalPrice) VALUES
('ES-001', 'E-Scooter', 'Xiaomi M365 Pro', 'Available', 50.00),
('ES-002', 'E-Scooter', 'Segway Ninebot Max', 'Available', 55.00),
('ES-003', 'E-Scooter', 'Xiaomi Pro 2', 'In Use', 50.00),
('ES-004', 'E-Scooter', 'Segway ES2', 'Available', 45.00),
('ES-005', 'E-Scooter', 'Xiaomi M365', 'Maintenance', 48.00),
('ES-006', 'E-Scooter', 'Ninebot ES4', 'Available', 52.00),
('ES-007', 'E-Scooter', 'Xiaomi Essential', 'Available', 45.00),
('ES-008', 'E-Scooter', 'Segway Max G30', 'In Use', 60.00),
('ES-009', 'E-Scooter', 'Xiaomi Pro 3', 'Available', 58.00),
('ES-010', 'E-Scooter', 'Ninebot F40', 'Available', 50.00),
('EB-001', 'E-Bike', 'Fiido D11', 'Available', 80.00),
('EB-002', 'E-Bike', 'Xiaomi Himo C20', 'Available', 75.00),
('EB-003', 'E-Bike', 'Fiido D4S', 'In Use', 70.00),
('EB-004', 'E-Bike', 'Xiaomi Qicycle', 'Available', 78.00),
('EB-005', 'E-Bike', 'Fiido M1', 'Available', 85.00),
('EB-006', 'E-Bike', 'Himo Z20', 'Maintenance', 82.00),
('EB-007', 'E-Bike', 'Fiido D3S', 'Available', 72.00),
('EB-008', 'E-Bike', 'Xiaomi C26', 'Available', 90.00),
('ET-001', 'E-Trike', 'Passenger Trike Standard', 'Available', 100.00),
('ET-002', 'E-Trike', 'Cargo E-Trike', 'Available', 95.00),
('ET-003', 'E-Trike', 'Family Trike', 'In Use', 105.00),
('ET-004', 'E-Trike', 'Deluxe Passenger Trike', 'Available', 120.00),
('ET-005', 'E-Trike', 'Eco Trike', 'Available', 98.00);

-- =====================================================
-- 3. CUSTOMERS TABLE
-- =====================================================
-- Registered customers with contact information

INSERT INTO customers (customerID, lastName, firstName, contactNumber, address, emailAddress) VALUES
('CUST-001', 'Reyes', 'Juan', '09171234567', '123 Bonifacio St, BGC, Taguig', 'juan.reyes@email.com'),
('CUST-002', 'Santos', 'Maria', '09281234567', '456 Ayala Ave, Makati', 'maria.santos@email.com'),
('CUST-003', 'Cruz', 'Pedro', '09391234567', '789 EDSA, Ortigas, Pasig', 'pedro.cruz@email.com'),
('CUST-004', 'Garcia', 'Ana', '09171234568', '321 Eastwood Dr, Quezon City', 'ana.garcia@email.com'),
('CUST-005', 'Mendoza', 'Jose', '09281234568', '654 UP Campus, Diliman', 'jose.mendoza@email.com'),
('CUST-006', 'Villanueva', 'Sofia', '09391234568', '987 MOA Complex, Pasay', 'sofia.v@email.com'),
('CUST-007', 'Torres', 'Miguel', '09171234569', '147 North Ave, Quezon City', 'miguel.torres@email.com'),
('CUST-008', 'Ramos', 'Isabel', '09281234569', '258 Alabang Town, Muntinlupa', 'isabel.ramos@email.com'),
('CUST-009', 'Fernandez', 'Carlos', '09391234569', '369 BGC High St, Taguig', 'carlos.f@email.com'),
('CUST-010', 'Lopez', 'Elena', '09171234570', '741 Makati Ave, Makati', 'elena.lopez@email.com');

-- =====================================================
-- 4. TECHNICIANS TABLE
-- =====================================================
-- Maintenance staff for vehicle repairs
-- Specializations: ELECTRICAL, MECHANICAL, BATTERY

INSERT INTO technicians (technician_id, last_name, first_name, specialization_id, rate, contact_number) VALUES
('TECH-001', 'Santos', 'Mario', 'ELECTRICAL', 350.00, '09171234567'),
('TECH-002', 'Garcia', 'Rosa', 'MECHANICAL', 320.00, '09281234567'),
('TECH-003', 'Reyes', 'Pedro', 'BATTERY', 380.00, '09391234567'),
('TECH-004', 'Dela Cruz', 'Ana', 'ELECTRICAL', 340.00, '09171234568'),
('TECH-005', 'Mendoza', 'Carlos', 'MECHANICAL', 330.00, '09281234568'),
('TECH-006', 'Villanueva', 'Sofia', 'BATTERY', 370.00, '09391234568'),
('TECH-007', 'Torres', 'Miguel', 'ELECTRICAL', 360.00, '09171234569'),
('TECH-008', 'Ramos', 'Elena', 'MECHANICAL', 315.00, '09281234569');

SELECT * FROM technicians;

-- =====================================================
-- 5. PARTS TABLE
-- =====================================================
-- Spare parts inventory for maintenance
-- Common parts for e-scooters, e-bikes, and e-trikes

INSERT INTO parts (part_id, part_name, quantity, price) VALUES
('PART-001', 'Lithium Battery Pack', 50, 2500.00),
('PART-002', 'Motor Controller', 30, 1800.00),
('PART-003', 'Brake Pads', 100, 150.00),
('PART-004', 'LED Headlight', 75, 350.00),
('PART-005', 'Tire (Front)', 40, 450.00),
('PART-006', 'Tire (Rear)', 40, 450.00),
('PART-007', 'Throttle Assembly', 25, 280.00),
('PART-008', 'Handlebar Grip', 60, 80.00),
('PART-009', 'Kickstand', 35, 120.00),
('PART-010', 'Chain', 20, 200.00),
('PART-011', 'Brake Cable', 80, 95.00),
('PART-012', 'Display Screen', 15, 1200.00),
('PART-013', 'Seat', 30, 350.00),
('PART-014', 'Pedal Set', 25, 180.00),
('PART-015', 'Rear Light', 70, 150.00);

SELECT * FROM parts;


-- =====================================================
-- 6. RENTALS TABLE (NOT READY YET - COMMENTED OUT)
-- =====================================================
-- Active and completed rental transactions

INSERT INTO rentals (rentalID, customerID, plateID, locationID, startTime, endTime, rentalDate) VALUES
('RNT-001', 'CUST-003', 'ES-003', 'LOC-001', '2024-10-28 09:00:00', NULL, '2024-10-28'),
('RNT-002', 'CUST-009', 'ES-008', 'LOC-001', '2024-10-28 10:30:00', NULL, '2024-10-28'),
('RNT-003', 'CUST-005', 'EB-003', 'LOC-005', '2024-10-28 08:00:00', NULL, '2024-10-28'),
('RNT-004', 'CUST-007', 'ET-003', 'LOC-001', '2024-10-28 11:00:00', NULL, '2024-10-28'),
('RNT-005', 'CUST-001', 'ES-001', 'LOC-001', '2024-10-27 09:00:00', '2024-10-27 11:45:00', '2024-10-27'),
('RNT-006', 'CUST-002', 'EB-001', 'LOC-002', '2024-10-27 14:00:00', '2024-10-27 17:30:00', '2024-10-27'),
('RNT-007', 'CUST-004', 'ES-002', 'LOC-004', '2024-10-26 10:00:00', '2024-10-26 12:50:00', '2024-10-26'),
('RNT-008', 'CUST-006', 'ET-001', 'LOC-006', '2024-10-26 15:00:00', '2024-10-26 18:00:00', '2024-10-26'),
('RNT-009', 'CUST-008', 'EB-002', 'LOC-008', '2024-10-25 09:00:00', '2024-10-25 11:30:00', '2024-10-25'),
('RNT-010', 'CUST-010', 'ES-004', 'LOC-002', '2024-10-25 13:00:00', '2024-10-25 16:15:00', '2024-10-25'),
('RNT-011', 'CUST-001', 'ES-006', 'LOC-001', '2024-10-24 10:00:00', '2024-10-24 12:00:00', '2024-10-24'),
('RNT-012', 'CUST-003', 'EB-004', 'LOC-003', '2024-10-24 14:00:00', '2024-10-24 16:30:00', '2024-10-24'),
('RNT-013', 'CUST-002', 'ES-007', 'LOC-002', '2024-10-23 09:00:00', '2024-10-23 11:00:00', '2024-10-23'),
('RNT-014', 'CUST-004', 'ET-002', 'LOC-004', '2024-10-23 15:00:00', '2024-10-23 18:00:00', '2024-10-23'),
('RNT-015', 'CUST-006', 'ES-009', 'LOC-006', '2024-10-22 10:00:00', '2024-10-22 13:00:00', '2024-10-22');


-- =====================================================
-- 7. PAYMENTS TABLE (NOT READY YET - COMMENTED OUT)
-- =====================================================
-- Payment records for rentals
/*
INSERT INTO payments (paymentID, rentalID, amount, paymentMethod, paymentDate) VALUES

-- Payments for completed rentals
('PAY-001', 'RNT-005', 150.00, 'GCash', '2024-10-27 11:50:00'),
('PAY-002', 'RNT-006', 280.00, 'Credit Card', '2024-10-27 17:35:00'),
('PAY-003', 'RNT-007', 165.00, 'Cash', '2024-10-26 13:00:00'),
('PAY-004', 'RNT-008', 300.00, 'PayMaya', '2024-10-26 18:05:00'),
('PAY-005', 'RNT-009', 225.00, 'GCash', '2024-10-25 11:35:00'),
('PAY-006', 'RNT-010', 145.00, 'Cash', '2024-10-25 16:20:00'),

-- Payments for active rentals (advance payment)
('PAY-007', 'RNT-001', 150.00, 'GCash', '2024-10-28 09:00:00'),
('PAY-008', 'RNT-002', 180.00, 'Credit Card', '2024-10-28 10:30:00'),
('PAY-009', 'RNT-003', 210.00, 'PayMaya', '2024-10-28 08:00:00'),
('PAY-010', 'RNT-004', 315.00, 'Cash', '2024-10-28 11:00:00');
*/


-- =====================================================
-- 8. MAINTENANCE TABLE (Refactored)
-- =====================================================
-- Maintenance records without part information
-- Parts tracked separately in maintenance_cheque table

INSERT INTO maintenance (maintenanceID, dateReported, dateRepaired, notes, technicianID, plateID, hoursWorked) VALUES
-- Recent maintenance - E-Scooters
('MAINT-001', '2024-10-20', '2024-10-21', 'Battery replacement - capacity degraded to 65%', 'TECH-001', 'ES-001', 3.50),
('MAINT-002', '2024-10-22', '2024-10-22', 'Routine brake maintenance - 75% worn', 'TECH-002', 'ES-002', 1.00),
('MAINT-003', '2024-10-25', '2024-10-26', 'Motor controller malfunction - replaced unit', 'TECH-005', 'ES-005', 2.50),
('MAINT-004', '2024-10-23', '2024-10-23', 'LED headlight and throttle replacement', 'TECH-001', 'ES-006', 1.50),
('MAINT-005', '2024-10-24', '2024-10-24', 'Front tire puncture - replaced', 'TECH-002', 'ES-007', 0.75),
('MAINT-006', '2024-10-21', '2024-10-21', 'Throttle assembly loose - replaced', 'TECH-004', 'ES-009', 0.50),
('MAINT-007', '2024-10-19', '2024-10-19', 'Brake cable fraying - preventive', 'TECH-002', 'ES-010', 0.50),

-- E-Bikes maintenance
('MAINT-008', '2024-10-18', '2024-10-19', 'Battery and display replacement', 'TECH-003', 'EB-001', 4.00),
('MAINT-009', '2024-10-20', '2024-10-21', 'Chain and brake maintenance', 'TECH-002', 'EB-002', 1.25),
('MAINT-010', '2024-10-22', '2024-10-22', 'Brake pads and cables replaced', 'TECH-005', 'EB-004', 1.00),
('MAINT-011', '2024-10-23', '2024-10-23', 'Display screen malfunction', 'TECH-001', 'EB-005', 2.00),
('MAINT-012', '2024-10-26', '2024-10-27', 'Battery capacity critical - emergency', 'TECH-006', 'EB-006', 3.00),
('MAINT-013', '2024-10-24', '2024-10-24', 'Pedal and seat replacement', 'TECH-002', 'EB-007', 1.50),
('MAINT-014', '2024-10-25', '2024-10-25', 'Rear light and brake maintenance', 'TECH-004', 'EB-008', 1.00),

-- E-Trikes maintenance
('MAINT-015', '2024-10-21', '2024-10-22', 'Tire and brake inspection', 'TECH-002', 'ET-001', 1.25),
('MAINT-016', '2024-10-19', '2024-10-20', 'Battery and controller check', 'TECH-003', 'ET-002', 2.50),
('MAINT-017', '2024-10-23', '2024-10-23', 'Complete brake system service', 'TECH-005', 'ET-004', 2.00),
('MAINT-018', '2024-10-24', '2024-10-25', 'Motor controller replacement', 'TECH-007', 'ET-005', 2.50),

-- Ongoing maintenance (not yet completed - no hours logged yet)
('MAINT-019', '2024-10-27', NULL, 'Brake inspection in progress', 'TECH-002', 'ES-005', 0.00),
('MAINT-020', '2024-10-27', NULL, 'Seat replacement - awaiting parts', 'TECH-006', 'EB-006', 0.00);

SELECT * FROM maintenance;

-- =====================================================
-- 8B. MAINTENANCE_CHEQUE TABLE (New)
-- =====================================================
-- Parts used in each maintenance record with quantities

INSERT INTO maintenance_cheque (maintenanceID, partID, quantityUsed) VALUES
-- MAINT-001: Battery replacement
('MAINT-001', 'PART-001', 1.00),

-- MAINT-002: Brake maintenance
('MAINT-002', 'PART-003', 2.00),
('MAINT-002', 'PART-011', 1.00),

-- MAINT-003: Motor controller
('MAINT-003', 'PART-002', 1.00),

-- MAINT-004: Headlight and throttle
('MAINT-004', 'PART-004', 1.00),
('MAINT-004', 'PART-007', 1.00),

-- MAINT-005: Tire replacement
('MAINT-005', 'PART-005', 1.00),

-- MAINT-006: Throttle only
('MAINT-006', 'PART-007', 1.00),

-- MAINT-007: Brake cable
('MAINT-007', 'PART-011', 1.00),

-- MAINT-008: Battery and display
('MAINT-008', 'PART-001', 1.00),
('MAINT-008', 'PART-012', 1.00),

-- MAINT-009: Chain and brakes
('MAINT-009', 'PART-010', 1.00),
('MAINT-009', 'PART-003', 2.00),

-- MAINT-010: Brake system
('MAINT-010', 'PART-003', 2.00),
('MAINT-010', 'PART-011', 2.00),

-- MAINT-011: Display only
('MAINT-011', 'PART-012', 1.00),

-- MAINT-012: Battery replacement
('MAINT-012', 'PART-001', 1.00),

-- MAINT-013: Pedals and seat
('MAINT-013', 'PART-014', 1.00),
('MAINT-013', 'PART-013', 1.00),

-- MAINT-014: Light and brakes
('MAINT-014', 'PART-015', 1.00),
('MAINT-014', 'PART-003', 2.00),

-- MAINT-015: Tire and brakes
('MAINT-015', 'PART-006', 1.00),
('MAINT-015', 'PART-003', 2.00),

-- MAINT-016: Battery and controller
('MAINT-016', 'PART-001', 1.00),
('MAINT-016', 'PART-002', 1.00),

-- MAINT-017: Complete brake system (uses more parts)
('MAINT-017', 'PART-003', 4.00),
('MAINT-017', 'PART-011', 2.00),

-- MAINT-018: Motor controller
('MAINT-018', 'PART-002', 1.00),

-- MAINT-019: Brake maintenance in progress
('MAINT-019', 'PART-003', 2.00),

-- MAINT-020: Seat replacement (awaiting delivery - no parts used yet)
('MAINT-020', 'PART-013', 1.00);

SELECT * FROM maintenance_cheque;



-- =====================================================
-- 9. PENALTIES TABLE
-- =====================================================
-- Penalty records for damage/repair costs charged to customers
-- Linked to maintenance records via maintenanceID

INSERT INTO penalty (penaltyID, rentalID, totalPenalty, penaltyStatus, maintenanceID, dateIssued) VALUES
-- Penalties for completed maintenance (customer charged for damages)
('PEN-001', 'RNT-005', 3725.00, 'UNPAID', 'MAINT-001', '2024-10-21'),  -- Battery replacement: 3.5h × ₱350 + ₱2500 = ₱3,725
('PEN-002', 'RNT-007', 745.00, 'PAID', 'MAINT-002', '2024-10-22'),     -- Brake maintenance: 1.0h × ₱330 + (2×₱150 + ₱95) = ₱725
('PEN-003', 'RNT-006', 3535.00, 'UNPAID', 'MAINT-008', '2024-10-19'),  -- Battery + display: 4.0h × ₱380 + (₱2500 + ₱1200) = ₱5,220
('PEN-004', 'RNT-009', 565.00, 'PAID', 'MAINT-009', '2024-10-21'),     -- Chain + brakes: 1.25h × ₱320 + (₱200 + 2×₱150) = ₱900
('PEN-005', 'RNT-012', 1275.00, 'UNPAID', 'MAINT-011', '2024-10-23'),  -- Display screen: 2.0h × ₱350 + ₱1200 = ₱1,900
('PEN-006', 'RNT-013', 562.50, 'PAID', 'MAINT-004', '2024-10-23'),     -- Headlight + throttle: 1.5h × ₱350 + (₱350 + ₱280) = ₱1,155
('PEN-007', 'RNT-014', 1470.00, 'UNPAID', 'MAINT-016', '2024-10-20'),  -- Battery + controller: 2.5h × ₱380 + (₱2500 + ₱1800) = ₱5,250
('PEN-008', 'RNT-015', 600.00, 'PAID', 'MAINT-005', '2024-10-24'),     -- Tire replacement: 0.75h × ₱320 + ₱450 = ₱690
('PEN-009', 'RNT-011', 1130.00, 'UNPAID', 'MAINT-013', '2024-10-24'),  -- Pedal + seat: 1.5h × ₱320 + (₱180 + ₱350) = ₱1,010
('PEN-010', 'RNT-010', 925.00, 'PAID', 'MAINT-017', '2024-10-23');     -- Complete brake system: 2.0h × ₱330 + (4×₱150 + 2×₱95) = ₱1,450

SELECT * FROM penalty;



-- =====================================================
-- 10. DEPLOYMENTS TABLE (NOT READY YET - COMMENTED OUT)
-- =====================================================
-- Vehicle location history and movements

INSERT INTO deployments (deploymentID, plateID, locationID, startDate, endDate) VALUES
('DEP-001', 'ES-001', 'LOC-001', '2024-10-01', NULL),
('DEP-002', 'ES-002', 'LOC-001', '2024-10-01', NULL),
('DEP-003', 'ES-003', 'LOC-001', '2024-10-01', NULL),
('DEP-004', 'ES-004', 'LOC-002', '2024-10-01', NULL),
('DEP-005', 'ES-006', 'LOC-003', '2024-10-01', NULL),
('DEP-006', 'ES-007', 'LOC-004', '2024-10-01', NULL),
('DEP-007', 'ES-008', 'LOC-001', '2024-10-15', NULL),
('DEP-008', 'ES-009', 'LOC-005', '2024-10-01', NULL),
('DEP-009', 'ES-010', 'LOC-006', '2024-10-01', NULL),
('DEP-010', 'EB-001', 'LOC-002', '2024-10-01', NULL),
('DEP-011', 'EB-002', 'LOC-002', '2024-10-01', NULL),
('DEP-012', 'EB-003', 'LOC-005', '2024-10-01', NULL),
('DEP-013', 'EB-004', 'LOC-001', '2024-10-01', NULL),
('DEP-014', 'EB-005', 'LOC-007', '2024-10-01', NULL),
('DEP-015', 'EB-007', 'LOC-003', '2024-10-01', NULL),
('DEP-016', 'EB-008', 'LOC-008', '2024-10-01', NULL),
('DEP-017', 'ET-001', 'LOC-006', '2024-10-01', NULL),
('DEP-018', 'ET-002', 'LOC-001', '2024-10-01', NULL),
('DEP-019', 'ET-003', 'LOC-001', '2024-10-20', NULL),
('DEP-020', 'ET-004', 'LOC-002', '2024-10-01', NULL),
('DEP-021', 'ET-005', 'LOC-004', '2024-10-01', NULL),
('DEP-022', 'ES-005', 'LOC-001', '2024-10-01', '2024-10-26'),
('DEP-023', 'ES-005', 'LOC-002', '2024-10-26', NULL),
('DEP-024', 'EB-006', 'LOC-007', '2024-10-01', '2024-10-25'),
('DEP-025', 'EB-006', 'LOC-002', '2024-10-25', NULL),
('DEP-026', 'ES-008', 'LOC-003', '2024-10-01', '2024-10-15'),
('DEP-027', 'ET-003', 'LOC-005', '2024-10-01', '2024-10-20');


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
SELECT 'Maintenance', COUNT(*) FROM maintenance
UNION ALL
SELECT 'Maintenance_Cheque', COUNT(*) FROM maintenance_cheque;
-- UNION ALL
-- SELECT 'Rentals', COUNT(*) FROM rentals           -- Uncomment when ready
-- UNION ALL
-- SELECT 'Payments', COUNT(*) FROM payments         -- Uncomment when ready
-- UNION ALL
-- SELECT 'Penalties', COUNT(*) FROM penalties       -- Uncomment when ready
-- UNION ALL
-- SELECT 'Deployments', COUNT(*) FROM deployments;  -- Uncomment when ready

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
        WHEN dateRepaired IS NULL THEN 'In Progress'
        ELSE 'Completed'
    END as MaintenanceStatus,
    COUNT(*) as count
FROM maintenance
GROUP BY (dateRepaired IS NULL);

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

/*
-- Uncomment these when rentals/deployments are ready:

-- Check active rentals
SELECT COUNT(*) as ActiveRentals 
FROM rentals 
WHERE rentalStatus = 'Active';

-- Check vehicles at each location
SELECT l.name, COUNT(d.plateID) as VehicleCount
FROM locations l
LEFT JOIN deployments d ON l.locationID = d.locationID AND d.endDate IS NULL
GROUP BY l.locationID, l.name
ORDER BY VehicleCount DESC;
*/

-- =====================================================
-- END OF TEST DATA
-- =====================================================

SELECT 'Test data loaded successfully!' as Status;
SELECT 'Data Summary:' as Info;
SELECT '  - 8 Locations' as Summary
UNION ALL SELECT '  - 23 Vehicles'
UNION ALL SELECT '  - 10 Customers'
UNION ALL SELECT '  - 8 Technicians'
UNION ALL SELECT '  - 15 Parts'
UNION ALL SELECT '  - 20 Maintenance Records'
UNION ALL SELECT '  - 37 Maintenance Parts Usage Records';

SELECT 'Note: Rentals, Payments, Penalties, and Deployments are not yet populated' as Note;