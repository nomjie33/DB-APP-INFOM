-- =====================================================
-- RENTAL DATA DIAGNOSTIC
-- Find out why reports show no data
-- =====================================================

USE vehicle_rental_db;

-- 1. Check ALL rentals (regardless of status)
SELECT
    COUNT(*) AS total_rentals,
    status,
    MIN(startDateTime) AS earliest,
    MAX(startDateTime) AS latest
FROM rentals
GROUP BY status;

-- 2. Check rentals for October 2024 specifically
SELECT * FROM rentals
WHERE YEAR(startDateTime) = 2024
  AND MONTH(startDateTime) = 10
    LIMIT 10;

-- 3. Check if you have ANY completed rentals
SELECT COUNT(*) AS completed_rentals
FROM rentals
WHERE status = 'Completed';

-- 4. Check rental statuses (what values do you have?)
SELECT DISTINCT status FROM rentals;

-- 5. Check customers who have rentals
SELECT
    c.customerID,
    c.firstName,
    c.lastName,
    COUNT(r.rentalID) AS rental_count,
    r.status
FROM customers c
         JOIN rentals r ON c.customerID = r.customerID
WHERE c.status = 'Active'
GROUP BY c.customerID, c.firstName, c.lastName, r.status;

-- 6. Check if you have payments
SELECT
    COUNT(*) AS payment_count,
    SUM(amount) AS total_amount
FROM payments
WHERE status = 'Active';

-- 7. Full rental + payment check
SELECT
    r.rentalID,
    r.startDateTime,
    r.endDateTime,
    r.status AS rental_status,
    c.firstName,
    c.lastName,
    p.amount,
    p.status AS payment_status
FROM rentals r
         JOIN customers c ON r.customerID = c.customerID
         LEFT JOIN payments p ON r.rentalID = p.rentalID
    LIMIT 20;