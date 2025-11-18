-- =====================================================
-- RENTAL DATA DIAGNOSTIC
-- Find out why reports show no data
-- =====================================================

USE vehicle_rental_db;

SELECT rentalID, status FROM rentals;
SELECT plateID, status FROM vehicles ;


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
    LIMIT 20;-- =====================================================
-- RENTAL DATA DIAGNOSTIC
-- Find out why reports show no data
-- =====================================================

USE vehicle_rental_db;


SHOW COLUMNS FROM maintenance;

SELECT YEAR(startDateTime) as year, MONTH(startDateTime) as month, COUNT(*) as count
FROM rentals
WHERE status = 'Active'
GROUP BY YEAR(startDateTime), MONTH(startDateTime)
ORDER BY year, month;

SELECT status, COUNT(*) 
FROM rentals 
GROUP BY status;
-- 1. Check ALL rentals (regardless of status)
SELECT
    COUNT(*) AS total_rentals,
    status,
    MIN(startDateTime) AS earliest,
    MAX(startDateTime) AS latest
FROM rentals
GROUP BY status;

SELECT COUNT(*) FROM payments WHERE status = 'Active';
SELECT * FROM payments LIMIT 5;


SELECT rentalID FROM rentals LIMIT 5;

SELECT DISTINCT rentalID FROM payments LIMIT 5;
SELECT 
    r.rentalID,
    r.customerID,
    r.status as rental_status,
    DATE(r.startDateTime) as rental_date,
    p.paymentID,
    p.amount
FROM rentals r
LEFT JOIN payments p ON r.rentalID = p.rentalID AND p.status = 'Active'
WHERE YEAR(r.startDateTime) = 2024 AND MONTH(r.startDateTime) = 10
ORDER BY r.rentalID
LIMIT 10;

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