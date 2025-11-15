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
package reports;

import dao.*;
import model.*;
import java.math.BigDecimal;
import java.sql.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.io.*;
import java.text.SimpleDateFormat;

// iText 5 imports - avoid wildcard to prevent List conflict
import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;

/**
 * CUSTOMER RENTAL REPORT
 * Analyze customer rental behavior and spending patterns.
 */
public class CustomerRentalReport {

    private RentalDAO rentalDAO;
    private CustomerDAO customerDAO;
    private PaymentDAO paymentDAO;

    public CustomerRentalReport() {
        this.rentalDAO = new RentalDAO();
        this.customerDAO = new CustomerDAO();
        this.paymentDAO = new PaymentDAO();
    }

    /**
     * Helper method to repeat a character (Java 8 compatible)
     */
    private static String repeatChar(String ch, int count) {
        StringBuilder sb = new StringBuilder(count);
        for (int i = 0; i < count; i++) {
            sb.append(ch);
        }
        return sb.toString();
    }

    /**
     * Inner class to hold customer rental report data
     */
    public static class CustomerRentalData {
        private String customerID;
        private String firstName;
        private String lastName;
        private String email;
        private String phoneNumber;
        private int numberOfRentals;
        private double totalRentalCost;
        private double averageRentalCost;
        private double totalRentalDuration;
        private double averageRentalDuration;
        private Timestamp mostRecentRentalDate;
        private String preferredPaymentMethod;

        // Constructors
        public CustomerRentalData() {}

        public CustomerRentalData(String customerID, String firstName, String lastName,
                                  String email, String phoneNumber, int numberOfRentals,
                                  double totalRentalCost, double averageRentalCost,
                                  double totalRentalDuration, double averageRentalDuration,
                                  Timestamp mostRecentRentalDate, String preferredPaymentMethod) {
            this.customerID = customerID;
            this.firstName = firstName;
            this.lastName = lastName;
            this.email = email;
            this.phoneNumber = phoneNumber;
            this.numberOfRentals = numberOfRentals;
            this.totalRentalCost = totalRentalCost;
            this.averageRentalCost = averageRentalCost;
            this.totalRentalDuration = totalRentalDuration;
            this.averageRentalDuration = averageRentalDuration;
            this.mostRecentRentalDate = mostRecentRentalDate;
            this.preferredPaymentMethod = preferredPaymentMethod;
        }

        // Getters and Setters
        public String getCustomerID() { return customerID; }
        public void setCustomerID(String customerID) { this.customerID = customerID; }

        public String getFirstName() { return firstName; }
        public void setFirstName(String firstName) { this.firstName = firstName; }

        public String getLastName() { return lastName; }
        public void setLastName(String lastName) { this.lastName = lastName; }

        public String getFullName() { return firstName + " " + lastName; }

        public String getEmail() { return email; }
        public void setEmail(String email) { this.email = email; }

        public String getPhoneNumber() { return phoneNumber; }
        public void setPhoneNumber(String phoneNumber) { this.phoneNumber = phoneNumber; }

        public int getNumberOfRentals() { return numberOfRentals; }
        public void setNumberOfRentals(int numberOfRentals) { this.numberOfRentals = numberOfRentals; }

        public double getTotalRentalCost() { return totalRentalCost; }
        public void setTotalRentalCost(double totalRentalCost) { this.totalRentalCost = totalRentalCost; }

        public double getAverageRentalCost() { return averageRentalCost; }
        public void setAverageRentalCost(double averageRentalCost) { this.averageRentalCost = averageRentalCost; }

        public double getTotalRentalDuration() { return totalRentalDuration; }
        public void setTotalRentalDuration(double totalRentalDuration) { this.totalRentalDuration = totalRentalDuration; }

        public double getAverageRentalDuration() { return averageRentalDuration; }
        public void setAverageRentalDuration(double averageRentalDuration) { this.averageRentalDuration = averageRentalDuration; }

        public Timestamp getMostRecentRentalDate() { return mostRecentRentalDate; }
        public void setMostRecentRentalDate(Timestamp mostRecentRentalDate) { this.mostRecentRentalDate = mostRecentRentalDate; }

        public String getPreferredPaymentMethod() { return preferredPaymentMethod; }
        public void setPreferredPaymentMethod(String preferredPaymentMethod) { this.preferredPaymentMethod = preferredPaymentMethod; }
    }

    /**
     * Generate monthly customer rental report
     * @param year Year for report
     * @param month Month for report (1-12)
     * @param sortBy Sort criteria: "Rentals", "Revenue", or "Duration"
     * @return List of CustomerRentalData
     */
    public List<CustomerRentalData> generateMonthlyReport(int year, int month, String sortBy) {
        List<CustomerRentalData> reportData = new ArrayList<>();

        String orderByClause;
        switch (sortBy != null ? sortBy.toLowerCase() : "revenue") {
            case "rentals":
                orderByClause = "number_of_rentals DESC";
                break;
            case "duration":
                orderByClause = "total_duration DESC";
                break;
            default:
                orderByClause = "total_cost DESC";
        }

        String sql =
                "SELECT " +
                        "    c.customerID, " +
                        "    c.firstName, " +
                        "    c.lastName, " +
                        "    c.emailAddress, " +
                        "    c.contactNumber, " +
                        "    COUNT(DISTINCT r.rentalID) AS number_of_rentals, " +
                        "    COALESCE(SUM(p.amount), 0) AS total_cost, " +
                        "    COALESCE(AVG(p.amount), 0) AS avg_cost, " +
                        "    COALESCE(SUM(TIMESTAMPDIFF(HOUR, r.startDateTime, r.endDateTime)), 0) AS total_duration, " +
                        "    COALESCE(AVG(TIMESTAMPDIFF(HOUR, r.startDateTime, r.endDateTime)), 0) AS avg_duration, " +
                        "    MAX(r.startDateTime) AS most_recent_rental " +
                        "FROM customers c " +
                        "LEFT JOIN rentals r ON c.customerID = r.customerID " +
                        "    AND r.status = 'Active' " +
                        "    AND YEAR(r.startDateTime) = ? " +
                        "    AND MONTH(r.startDateTime) = ? " +
                        "LEFT JOIN payments p ON r.rentalID = p.rentalID " +
                        "    AND p.status = 'Active' " +
                        "WHERE c.status = 'Active' " +
                        "GROUP BY c.customerID, c.firstName, c.lastName, c.emailAddress, c.contactNumber " +
                        "HAVING number_of_rentals > 0 " +
                        "ORDER BY " + orderByClause;

        try (Connection conn = util.DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, year);
            stmt.setInt(2, month);

            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                CustomerRentalData data = new CustomerRentalData();
                data.setCustomerID(rs.getString("customerID"));
                data.setFirstName(rs.getString("firstName"));
                data.setLastName(rs.getString("lastName"));
                data.setEmail(rs.getString("emailAddress"));
                data.setPhoneNumber(rs.getString("contactNumber"));
                data.setNumberOfRentals(rs.getInt("number_of_rentals"));
                data.setTotalRentalCost(rs.getDouble("total_cost"));
                data.setAverageRentalCost(rs.getDouble("avg_cost"));
                data.setTotalRentalDuration(rs.getDouble("total_duration"));
                data.setAverageRentalDuration(rs.getDouble("avg_duration"));
                data.setMostRecentRentalDate(rs.getTimestamp("most_recent_rental"));
                data.setPreferredPaymentMethod(null); // Payment method not in schema

                reportData.add(data);
            }

        } catch (SQLException e) {
            System.err.println("Error generating monthly customer rental report: " + e.getMessage());
            e.printStackTrace();
        }

        return reportData;
    }

    /**
     * Generate yearly customer rental report
     */
    public List<CustomerRentalData> generateYearlyReport(int year, String sortBy) {
        List<CustomerRentalData> reportData = new ArrayList<>();

        String orderByClause;
        switch (sortBy != null ? sortBy.toLowerCase() : "revenue") {
            case "rentals":
                orderByClause = "number_of_rentals DESC";
                break;
            case "duration":
                orderByClause = "total_duration DESC";
                break;
            default:
                orderByClause = "total_cost DESC";
        }

        String sql =
                "SELECT " +
                        "    c.customerID, " +
                        "    c.firstName, " +
                        "    c.lastName, " +
                        "    c.emailAddress, " +
                        "    c.contactNumber, " +
                        "    COUNT(DISTINCT r.rentalID) AS number_of_rentals, " +
                        "    COALESCE(SUM(p.amount), 0) AS total_cost, " +
                        "    COALESCE(AVG(p.amount), 0) AS avg_cost, " +
                        "    COALESCE(SUM(TIMESTAMPDIFF(HOUR, r.startDateTime, r.endDateTime)), 0) AS total_duration, " +
                        "    COALESCE(AVG(TIMESTAMPDIFF(HOUR, r.startDateTime, r.endDateTime)), 0) AS avg_duration, " +
                        "    MAX(r.startDateTime) AS most_recent_rental " +
                        "FROM customers c " +
                        "LEFT JOIN rentals r ON c.customerID = r.customerID " +
                        "    AND r.status = 'Active' " +
                        "    AND YEAR(r.startDateTime) = ? " +
                        "LEFT JOIN payments p ON r.rentalID = p.rentalID " +
                        "    AND p.status = 'Active' " +
                        "WHERE c.status = 'Active' " +
                        "GROUP BY c.customerID, c.firstName, c.lastName, c.emailAddress, c.contactNumber " +
                        "HAVING number_of_rentals > 0 " +
                        "ORDER BY " + orderByClause;

        try (Connection conn = util.DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, year);

            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                CustomerRentalData data = new CustomerRentalData();
                data.setCustomerID(rs.getString("customerID"));
                data.setFirstName(rs.getString("firstName"));
                data.setLastName(rs.getString("lastName"));
                data.setEmail(rs.getString("emailAddress"));
                data.setPhoneNumber(rs.getString("contactNumber"));
                data.setNumberOfRentals(rs.getInt("number_of_rentals"));
                data.setTotalRentalCost(rs.getDouble("total_cost"));
                data.setAverageRentalCost(rs.getDouble("avg_cost"));
                data.setTotalRentalDuration(rs.getDouble("total_duration"));
                data.setAverageRentalDuration(rs.getDouble("avg_duration"));
                data.setMostRecentRentalDate(rs.getTimestamp("most_recent_rental"));
                data.setPreferredPaymentMethod(null); // Payment method not in schema

                reportData.add(data);
            }

        } catch (SQLException e) {
            System.err.println("Error generating yearly customer rental report: " + e.getMessage());
            e.printStackTrace();
        }

        return reportData;
    }

    /**
     * Print formatted report to console
     */
    public void printReport(List<CustomerRentalData> data, int year, int month, String sortBy) {
        System.out.println("\n" + repeatChar("=", 150));

        String[] months = {"", "January", "February", "March", "April", "May", "June",
                "July", "August", "September", "October", "November", "December"};

        if (month > 0) {
            System.out.printf("CUSTOMER RENTAL REPORT - %s %d (Sorted by %s)", months[month], year, sortBy);
        } else {
            System.out.printf("CUSTOMER RENTAL REPORT - Year %d (Sorted by %s)", year, sortBy);
        }
        System.out.println();
        System.out.println(repeatChar("=", 150));

        if (data.isEmpty()) {
            System.out.println("No customer rental data found for the specified period.");
            System.out.println(repeatChar("=", 150) + "\n");
            return;
        }

        // Table header
        System.out.printf("%-12s %-25s %-8s %-15s %-12s %-12s %-12s %-15s %-15s\n",
                "Customer ID", "Name", "Rentals", "Total Cost", "Avg Cost", "Total Hrs",
                "Avg Hrs", "Last Rental", "Payment Method");
        System.out.println(repeatChar("-", 150));

        // Calculate totals
        int totalRentals = 0;
        double totalRevenue = 0;
        double totalHours = 0;

        // Display each customer
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        for (CustomerRentalData customer : data) {
            String lastRentalDate = (customer.getMostRecentRentalDate() != null)
                    ? dateFormat.format(customer.getMostRecentRentalDate())
                    : "N/A";

            System.out.printf("%-12s %-25s %-8d %,15.2f %,12.2f %,12.1f %,12.1f %-15s %-15s\n",
                    customer.getCustomerID(),
                    customer.getFullName(),
                    customer.getNumberOfRentals(),
                    customer.getTotalRentalCost(),
                    customer.getAverageRentalCost(),
                    customer.getTotalRentalDuration(),
                    customer.getAverageRentalDuration(),
                    lastRentalDate,
                    customer.getPreferredPaymentMethod() != null ? customer.getPreferredPaymentMethod() : "N/A");

            totalRentals += customer.getNumberOfRentals();
            totalRevenue += customer.getTotalRentalCost();
            totalHours += customer.getTotalRentalDuration();
        }

        System.out.println(repeatChar("-", 150));

        // Summary
        double avgRevenue = data.size() > 0 ? totalRevenue / data.size() : 0;
        double avgHours = data.size() > 0 ? totalHours / data.size() : 0;

        System.out.printf("Total (%d Customers)%9s %-8d %,15.2f %,12.2f %,12.1f %,12.1f\n",
                data.size(), "", totalRentals, totalRevenue, avgRevenue, totalHours, avgHours);

        System.out.println(repeatChar("=", 150) + "\n");
    }

    /**
     * Export report to PDF
     */
    public void exportToPDF(List<CustomerRentalData> data, String filename, int year, int month,
                            String sortBy) {
        Document document = new Document(PageSize.A4.rotate()); // Landscape for wide table

        try {
            PdfWriter.getInstance(document, new FileOutputStream(filename));
            document.open();

            // Add title
            Font titleFont = new Font(Font.FontFamily.HELVETICA, 18, Font.BOLD);
            String[] months = {"", "January", "February", "March", "April", "May", "June",
                    "July", "August", "September", "October", "November", "December"};

            String title = "CUSTOMER RENTAL REPORT";
            if (month > 0) {
                title += " - " + months[month] + " " + year;
            } else {
                title += " - Year " + year;
            }
            title += " (Sorted by " + sortBy + ")";

            Paragraph titlePara = new Paragraph(title, titleFont);
            titlePara.setAlignment(Element.ALIGN_CENTER);
            titlePara.setSpacingAfter(20);
            document.add(titlePara);

            if (data.isEmpty()) {
                Paragraph noData = new Paragraph("No customer rental data found for the specified period.");
                noData.setAlignment(Element.ALIGN_CENTER);
                document.add(noData);
                document.close();
                return;
            }

            // Create table
            PdfPTable table = new PdfPTable(9); // 9 columns
            table.setWidthPercentage(100);
            table.setWidths(new float[]{1.2f, 2.5f, 0.8f, 1.5f, 1.2f, 1.2f, 1.2f, 1.5f, 1.5f});

            // Header font
            Font headerFont = new Font(Font.FontFamily.HELVETICA, 9, Font.BOLD);

            // Add headers
            String[] headers = {"Customer ID", "Name", "Rentals", "Total Cost (PHP)",
                    "Avg Cost", "Total Hrs", "Avg Hrs", "Last Rental", "Payment Method"};

            for (String header : headers) {
                PdfPCell cell = new PdfPCell(new Phrase(header, headerFont));
                cell.setBackgroundColor(BaseColor.LIGHT_GRAY);
                cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                cell.setPadding(5);
                table.addCell(cell);
            }

            // Add data rows
            Font dataFont = new Font(Font.FontFamily.HELVETICA, 8);
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

            int totalRentals = 0;
            double totalRevenue = 0;
            double totalHours = 0;

            for (CustomerRentalData customer : data) {
                table.addCell(new Phrase(customer.getCustomerID(), dataFont));
                table.addCell(new Phrase(customer.getFullName(), dataFont));
                table.addCell(new Phrase(String.valueOf(customer.getNumberOfRentals()), dataFont));
                table.addCell(new Phrase(String.format("%,.2f", customer.getTotalRentalCost()), dataFont));
                table.addCell(new Phrase(String.format("%,.2f", customer.getAverageRentalCost()), dataFont));
                table.addCell(new Phrase(String.format("%.1f", customer.getTotalRentalDuration()), dataFont));
                table.addCell(new Phrase(String.format("%.1f", customer.getAverageRentalDuration()), dataFont));

                String lastRental = (customer.getMostRecentRentalDate() != null)
                        ? dateFormat.format(customer.getMostRecentRentalDate()) : "N/A";
                table.addCell(new Phrase(lastRental, dataFont));

                String paymentMethod = customer.getPreferredPaymentMethod() != null
                        ? customer.getPreferredPaymentMethod() : "N/A";
                table.addCell(new Phrase(paymentMethod, dataFont));

                totalRentals += customer.getNumberOfRentals();
                totalRevenue += customer.getTotalRentalCost();
                totalHours += customer.getTotalRentalDuration();
            }

            // Add total row
            Font totalFont = new Font(Font.FontFamily.HELVETICA, 9, Font.BOLD);
            PdfPCell totalLabel = new PdfPCell(new Phrase("TOTAL (" + data.size() + " Customers)", totalFont));
            totalLabel.setColspan(2);
            totalLabel.setBackgroundColor(BaseColor.LIGHT_GRAY);
            table.addCell(totalLabel);

            table.addCell(new Phrase(String.valueOf(totalRentals), totalFont));
            table.addCell(new Phrase(String.format("%,.2f", totalRevenue), totalFont));

            double avgRevenue = data.size() > 0 ? totalRevenue / data.size() : 0;
            table.addCell(new Phrase(String.format("%,.2f", avgRevenue), totalFont));

            table.addCell(new Phrase(String.format("%.1f", totalHours), totalFont));

            double avgHours = data.size() > 0 ? totalHours / data.size() : 0;
            table.addCell(new Phrase(String.format("%.1f", avgHours), totalFont));

            table.addCell(new Phrase("", totalFont));
            table.addCell(new Phrase("", totalFont));

            document.add(table);

            // Add footer
            Paragraph footer = new Paragraph("\nGenerated on: " + new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()),
                    new Font(Font.FontFamily.HELVETICA, 8, Font.ITALIC));
            footer.setAlignment(Element.ALIGN_RIGHT);
            footer.setSpacingBefore(20);
            document.add(footer);

            System.out.println("PDF report generated successfully: " + filename);

        } catch (DocumentException | IOException e) {
            System.err.println("Error generating PDF report: " + e.getMessage());
            e.printStackTrace();
        } finally {
            document.close();
        }
    }

    /**
     * Main method for testing
     */
    public static void main(String[] args) {
        CustomerRentalReport report = new CustomerRentalReport();

        System.out.println("=== CUSTOMER RENTAL REPORT TEST ===\n");

        // Test monthly report
        System.out.println("Testing Monthly Report for October 2024 (Sorted by Revenue)...");
        List<CustomerRentalData> monthlyData = report.generateMonthlyReport(2024, 10, "Revenue");
        report.printReport(monthlyData, 2024, 10, "Revenue");
        report.exportToPDF(monthlyData, "Customer_Rental_Report_Oct2024.pdf", 2024, 10, "Revenue");

        // Test yearly report
        System.out.println("\nTesting Yearly Report for 2024 (Sorted by Rentals)...");
        List<CustomerRentalData> yearlyData = report.generateYearlyReport(2024, "Rentals");
        report.printReport(yearlyData, 2024, 0, "Rentals");
        report.exportToPDF(yearlyData, "Customer_Rental_Report_2024.pdf", 2024, 0, "Rentals");

        System.out.println("=== TEST COMPLETE ===");
    }
}