package reports;

import dao.*;
import model.*;
import java.math.BigDecimal;
import java.sql.*;
import java.util.*;

/**
 * RENTAL REVENUE REPORT
 * 
 * PURPOSE: 
 * Generate revenue analytics for vehicles by type, showing total and average 
 * rental revenue aggregated by day, month, or year.
 * 
 * DATA SOURCES:
 * - Rental Transaction Records (startDateTime, endDateTime, rentalID)
 * - Vehicle Records (plateID, vehicleType, dailyRate)
 * - Payment Transaction Records (rentalID, amount, paymentDate)
 * 
 * REPORT OUTPUT:
 * - Vehicle Type
 * - Time Period (Day/Month/Year based on user selection)
 * - Total Revenue (sum of all payments for that vehicle type in period)
 * - Average Revenue (average payment amount per rental)
 * - Number of Rentals (count of rentals for that vehicle type)
 * 
 * USER INPUTS:
 * 1. Vehicle Type (e.g., "E-Scooter", "E-Bike", or "All")
 * 2. Report Period Type (Day/Month/Year)
 * 3. Date Range (Start Date - End Date)
 * 
 * EXPECTED METHODS:
 * - generateDailyReport(String vehicleType, Date startDate, Date endDate)
 * - generateMonthlyReport(String vehicleType, int year, int startMonth, int endMonth)
 * - generateYearlyReport(String vehicleType, int startYear, int endYear)
 * - printReport(List<RevenueData> data) - Format and display results
 * 
 * SQL LOGIC:
 * - JOIN rentals with vehicles on plateID
 * - JOIN rentals with payments on rentalID
 * - Filter by vehicleType and date range
 * - GROUP BY vehicleType and time period (DATE, YEAR-MONTH, or YEAR)
 * - Calculate SUM(amount), AVG(amount), COUNT(rentalID)
 * 
 * EXAMPLE OUTPUT:
 * ================================================================
 * RENTAL REVENUE REPORT - MONTHLY (October 2024)
 * ================================================================
 * Vehicle Type    | Total Revenue | Avg Revenue | # Rentals
 * ----------------------------------------------------------------
 * E-Scooter       | P 12,500.00   | P 125.00    | 100
 * E-Bike          | P 8,300.00    | P 83.00     | 100
 * ----------------------------------------------------------------
 * TOTAL           | P 20,800.00   | P 104.00    | 200
 * ================================================================
 */
public class RentalRevenueReport {
    
    private RentalDAO rentalDAO;
    private VehicleDAO vehicleDAO;
    private PaymentDAO paymentDAO;
    
    /**
     * Constructor - Initialize required DAOs
     */
    public RentalRevenueReport() {
        this.rentalDAO = new RentalDAO();
        this.vehicleDAO = new VehicleDAO();
        this.paymentDAO = new PaymentDAO();
    }
    
    // TODO: Implement report generation methods
    // TODO: Create RevenueData inner class to hold report results
    // TODO: Add data validation for date ranges
    // TODO: Handle "All" vehicle types option
    // TODO: Format currency output with proper peso formatting
}
