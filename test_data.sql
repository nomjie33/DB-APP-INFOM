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

DELETE FROM penalties;
DELETE FROM payments;
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



-- =====================================================
-- 5. PARTS TABLE
-- =====================================================
-- Spare parts inventory for maintenance


/*
-- =====================================================
-- 6. RENTALS TABLE (NOT READY YET - COMMENTED OUT)
-- =====================================================
-- Active and completed rental transactions

INSERT INTO rentals (rentalID, customerID, plateID, locationID, startDate, expectedReturnDate, actualReturnDate, rentalStatus) VALUES

-- ACTIVE RENTALS (In Use)
('RNT-001', 'CUST-003', 'ES-003', 'LOC-001', '2024-10-28 09:00:00', '2024-10-28 12:00:00', NULL, 'Active'),
('RNT-002', 'CUST-009', 'ES-008', 'LOC-001', '2024-10-28 10:30:00', '2024-10-28 13:30:00', NULL, 'Active'),
('RNT-003', 'CUST-005', 'EB-003', 'LOC-005', '2024-10-28 08:00:00', '2024-10-28 11:00:00', NULL, 'Active'),
('RNT-004', 'CUST-007', 'ET-003', 'LOC-001', '2024-10-28 11:00:00', '2024-10-28 14:00:00', NULL, 'Active'),

-- COMPLETED RENTALS (Returned)
('RNT-005', 'CUST-001', 'ES-001', 'LOC-001', '2024-10-27 09:00:00', '2024-10-27 12:00:00', '2024-10-27 11:45:00', 'Completed'),
('RNT-006', 'CUST-002', 'EB-001', 'LOC-002', '2024-10-27 14:00:00', '2024-10-27 17:00:00', '2024-10-27 17:30:00', 'Completed'),
('RNT-007', 'CUST-004', 'ES-002', 'LOC-004', '2024-10-26 10:00:00', '2024-10-26 13:00:00', '2024-10-26 12:50:00', 'Completed'),
('RNT-008', 'CUST-006', 'ET-001', 'LOC-006', '2024-10-26 15:00:00', '2024-10-26 18:00:00', '2024-10-26 18:00:00', 'Completed'),
('RNT-009', 'CUST-008', 'EB-002', 'LOC-008', '2024-10-25 09:00:00', '2024-10-25 12:00:00', '2024-10-25 11:30:00', 'Completed'),
('RNT-010', 'CUST-010', 'ES-004', 'LOC-002', '2024-10-25 13:00:00', '2024-10-25 16:00:00', '2024-10-25 16:15:00', 'Completed');
*/

/*
-- =====================================================
-- 7. PAYMENTS TABLE (NOT READY YET - COMMENTED OUT)
-- =====================================================
-- Payment records for rentals

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
-- 8. MAINTENANCE TABLE (NOT READY YET - COMMENTED OUT)
-- =====================================================
-- Maintenance and repair records



-- =====================================================
-- 9. PENALTIES TABLE (NOT READY YET - COMMENTED OUT)
-- =====================================================
-- Late return penalties



-- =====================================================
-- 10. DEPLOYMENTS TABLE (NOT READY YET - COMMENTED OUT)
-- =====================================================
-- Vehicle location history and movements

INSERT INTO deployments (deploymentID, plateID, locationID, startDate, endDate) VALUES

-- CURRENT DEPLOYMENTS (endDate is NULL = currently at location)
/*
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

-- HISTORICAL DEPLOYMENTS (endDate exists = moved from this location)
('DEP-022', 'ES-005', 'LOC-001', '2024-10-01', '2024-10-26'),
('DEP-023', 'ES-005', 'LOC-002', '2024-10-26', NULL),
('DEP-024', 'EB-006', 'LOC-007', '2024-10-01', '2024-10-25'),
('DEP-025', 'EB-006', 'LOC-002', '2024-10-25', NULL),
('DEP-026', 'ES-008', 'LOC-003', '2024-10-01', '2024-10-15'),
('DEP-027', 'ET-003', 'LOC-005', '2024-10-01', '2024-10-20');
*/

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
SELECT 'Parts', COUNT(*) FROM parts;
-- UNION ALL
-- SELECT 'Rentals', COUNT(*) FROM rentals           -- Uncomment when ready
-- UNION ALL
-- SELECT 'Payments', COUNT(*) FROM payments         -- Uncomment when ready
-- UNION ALL
-- SELECT 'Maintenance', COUNT(*) FROM maintenance   -- Uncomment when ready
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

SELECT '✓ Test data loaded successfully!' as Status;
SELECT 'Note: Rentals, Payments, Maintenance, Penalties, and Deployments are commented out' as Note;