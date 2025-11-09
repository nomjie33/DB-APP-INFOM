package reports;

import dao.*;
import model.*;
import java.math.BigDecimal;
import java.sql.*;
import java.util.*;

/**
 * CUSTOMER RENTAL REPORT
 * 
 * PURPOSE:
 * Analyze customer rental behavior and spending patterns. Identify top customers,
 * rental frequency, and total revenue per customer for loyalty programs and
 * targeted marketing.
 * 
 * DATA SOURCES:
 * - Rental Records (rentalID, customerID, startDateTime, endDateTime)
 * - Customer Records (customerID, firstName, lastName, email, phoneNumber)
 * - Payment Transaction Records (rentalID, amount, paymentDate, paymentMethod)
 * 
 * REPORT OUTPUT:
 * - Customer ID
 * - Customer Name (firstName + lastName)
 * - Contact Information (email, phone)
 * - Number of Rentals (count of rentals in period)
 * - Total Rental Cost (sum of all payment amounts)
 * - Average Rental Cost (average payment per rental)
 * - Total Rental Duration (sum of all rental hours)
 * - Average Rental Duration (average hours per rental)
 * - Most Recent Rental Date
 * - Preferred Payment Method (most frequently used)
 * 
 * USER INPUTS:
 * 1. Report Period Type (Day/Month/Year)
 * 2. Year (required)
 * 3. Month (required if period is Day or Month)
 * 4. Day (required if period is Day)
 * 5. Sort By ("Rentals", "Revenue", "Duration") - Optional, defaults to Revenue
 * 6. Top N Customers - Optional, show only top N customers
 * 
 * EXPECTED METHODS:
 * - generateDailyReport(int year, int month, int day, String sortBy, int topN)
 * - generateMonthlyReport(int year, int month, String sortBy, int topN)
 * - generateYearlyReport(int year, String sortBy, int topN)
 * - printReport(List<CustomerRentalData> data) - Format and display results
 * - calculateCustomerLifetimeValue(String customerID) - Total revenue from customer
 * 
 * SQL LOGIC:
 * - JOIN rentals with customers on customerID
 * - JOIN rentals with payments on rentalID
 * - Filter by date range (rental startDateTime within specified period)
 * - GROUP BY customerID, firstName, lastName
 * - Calculate:
 *   * COUNT(rentalID) - number of rentals
 *   * SUM(amount) - total revenue
 *   * AVG(amount) - average payment
 *   * SUM(TIMESTAMPDIFF(HOUR, startDateTime, endDateTime)) - total hours
 *   * AVG(TIMESTAMPDIFF(HOUR, startDateTime, endDateTime)) - avg hours
 *   * MAX(startDateTime) - most recent rental
 * - Sort by specified column (revenue, rentals, or duration)
 * - LIMIT to topN if specified
 * 
 * BUSINESS INSIGHTS:
 * - High revenue customers = VIP treatment, loyalty rewards
 * - Frequent renters = subscription model opportunity
 * - Long duration renters = may prefer ownership, offer purchase options
 * - Inactive customers = re-engagement marketing campaigns
 * - Payment method preferences = optimize payment options
 * 
 * EXAMPLE OUTPUT:
 * ================================================================
 * CUSTOMER RENTAL REPORT - October 2024 (Top 5 by Revenue)
 * ================================================================
 * Customer ID | Name              | Rentals | Total Cost | Avg Cost | Total Hrs | Avg Hrs | Last Rental
 * ---------------------------------------------------------------------------------------------------------
 * CUST-001    | Juan Dela Cruz    | 25      | P 3,125.00 | P 125.00 | 62.5      | 2.5     | 2024-10-28
 * CUST-002    | Maria Santos      | 20      | P 2,500.00 | P 125.00 | 50.0      | 2.5     | 2024-10-27
 * CUST-003    | Jose Rizal        | 18      | P 2,250.00 | P 125.00 | 45.0      | 2.5     | 2024-10-26
 * CUST-004    | Pedro Penduko     | 15      | P 1,875.00 | P 125.00 | 37.5      | 2.5     | 2024-10-25
 * CUST-005    | Ana Reyes         | 12      | P 1,500.00 | P 125.00 | 30.0      | 2.5     | 2024-10-24
 * ---------------------------------------------------------------------------------------------------------
 * Total (Top 5 Customers)        | 90      | P 11,250.00| P 125.00 | 225.0     | 2.5     |
 * ================================================================
 * 
 * RECOMMENDATIONS:
 * - Consider loyalty program for customers with 10+ monthly rentals
 * - Offer subscription packages for frequent renters
 * - Send re-engagement emails to customers inactive for 30+ days
 */
public class CustomerRentalReport {
    
    private RentalDAO rentalDAO;
    private CustomerDAO customerDAO;
    private PaymentDAO paymentDAO;
    
    /**
     * Constructor - Initialize required DAOs
     */
    public CustomerRentalReport() {
        this.rentalDAO = new RentalDAO();
        this.customerDAO = new CustomerDAO();
        this.paymentDAO = new PaymentDAO();
    }
    
    // TODO: Implement report generation methods
    // TODO: Create CustomerRentalData inner class to hold report results
    // TODO: Add sort options (by rentals, revenue, or duration)
    // TODO: Implement Top N filtering
    // TODO: Calculate rental duration using TIMESTAMPDIFF
    // TODO: Determine preferred payment method (MODE of paymentMethod)
    // TODO: Add customer segmentation (VIP, Regular, Occasional, Inactive)
}
