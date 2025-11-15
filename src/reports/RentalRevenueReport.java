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
package reports;

import dao.*;
import model.*;
import java.math.BigDecimal;
import java.sql.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
import com.itextpdf.text.Image;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;

/**
 * RENTAL REVENUE REPORT
 * Generate revenue analytics for vehicles by type
 */
public class RentalRevenueReport {

    private RentalDAO rentalDAO;
    private VehicleDAO vehicleDAO;
    private PaymentDAO paymentDAO;

    public RentalRevenueReport() {
        this.rentalDAO = new RentalDAO();
        this.vehicleDAO = new VehicleDAO();
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
     * Create output directory and return full path for PDF
     */
    private static String prepareOutputPath(String filename) {
        String outputDir = "reports_output";
        File dir = new File(outputDir);
        if (!dir.exists()) {
            dir.mkdirs();
        }
        return outputDir + File.separator + filename;
    }

    /**
     * Inner class to hold revenue report data
     */
    public static class RevenueData {
        private String vehicleType;
        private String timePeriod;
        private double totalRevenue;
        private double averageRevenue;
        private int numberOfRentals;

        // Constructors
        public RevenueData() {}

        public RevenueData(String vehicleType, String timePeriod, double totalRevenue,
                           double averageRevenue, int numberOfRentals) {
            this.vehicleType = vehicleType;
            this.timePeriod = timePeriod;
            this.totalRevenue = totalRevenue;
            this.averageRevenue = averageRevenue;
            this.numberOfRentals = numberOfRentals;
        }

        // Getters and Setters
        public String getVehicleType() { return vehicleType; }
        public void setVehicleType(String vehicleType) { this.vehicleType = vehicleType; }

        public String getTimePeriod() { return timePeriod; }
        public void setTimePeriod(String timePeriod) { this.timePeriod = timePeriod; }

        public double getTotalRevenue() { return totalRevenue; }
        public void setTotalRevenue(double totalRevenue) { this.totalRevenue = totalRevenue; }

        public double getAverageRevenue() { return averageRevenue; }
        public void setAverageRevenue(double averageRevenue) { this.averageRevenue = averageRevenue; }

        public int getNumberOfRentals() { return numberOfRentals; }
        public void setNumberOfRentals(int numberOfRentals) { this.numberOfRentals = numberOfRentals; }
    }

    /**
     * Generate daily revenue report
     * @param vehicleType Vehicle type filter ("E-Scooter", "E-Bike", or "All")
     * @param year Year
     * @param month Month (1-12)
     * @param day Day
     * @return List of RevenueData
     */
    public List<RevenueData> generateDailyReport(String vehicleType, int year, int month, int day) {
        List<RevenueData> reportData = new ArrayList<>();

        String vehicleFilter = "";
        if (vehicleType != null && !vehicleType.equalsIgnoreCase("All")) {
            vehicleFilter = "AND v.vehicleType = ?";
        }

        String sql =
                "SELECT " +
                        "    v.vehicleType, " +
                        "    DATE(r.startDateTime) AS rental_date, " +
                        "    COALESCE(SUM(p.amount), 0) AS total_revenue, " +
                        "    COALESCE(AVG(p.amount), 0) AS avg_revenue, " +
                        "    COUNT(DISTINCT r.rentalID) AS number_of_rentals " +
                        "FROM rentals r " +
                        "JOIN vehicles v ON r.plateID = v.plateID " +
                        "LEFT JOIN payments p ON r.rentalID = p.rentalID " +
                        "    AND p.status = 'Active' " +
                        "WHERE r.status = 'Completed' " +
                        "    AND YEAR(r.startDateTime) = ? " +
                        "    AND MONTH(r.startDateTime) = ? " +
                        "    AND DAY(r.startDateTime) = ? " +
                        vehicleFilter +
                        " GROUP BY v.vehicleType, DATE(r.startDateTime) " +
                        "ORDER BY v.vehicleType, rental_date";

        try (Connection conn = util.DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            int paramIndex = 1;
            stmt.setInt(paramIndex++, year);
            stmt.setInt(paramIndex++, month);
            stmt.setInt(paramIndex++, day);

            if (vehicleType != null && !vehicleType.equalsIgnoreCase("All")) {
                stmt.setString(paramIndex++, vehicleType);
            }

            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                RevenueData data = new RevenueData();
                data.setVehicleType(rs.getString("vehicleType"));
                data.setTimePeriod(rs.getString("rental_date"));
                data.setTotalRevenue(rs.getDouble("total_revenue"));
                data.setAverageRevenue(rs.getDouble("avg_revenue"));
                data.setNumberOfRentals(rs.getInt("number_of_rentals"));

                reportData.add(data);
            }

        } catch (SQLException e) {
            System.err.println("Error generating daily revenue report: " + e.getMessage());
            e.printStackTrace();
        }

        return reportData;
    }

    /**
     * Generate monthly revenue report
     */
    public List<RevenueData> generateMonthlyReport(String vehicleType, int year, int startMonth, int endMonth) {
        List<RevenueData> reportData = new ArrayList<>();

        String vehicleFilter = "";
        if (vehicleType != null && !vehicleType.equalsIgnoreCase("All")) {
            vehicleFilter = "AND v.vehicleType = ?";
        }

        String sql =
                "SELECT " +
                        "    v.vehicleType, " +
                        "    YEAR(r.startDateTime) AS rental_year, " +
                        "    MONTH(r.startDateTime) AS rental_month, " +
                        "    COALESCE(SUM(p.amount), 0) AS total_revenue, " +
                        "    COALESCE(AVG(p.amount), 0) AS avg_revenue, " +
                        "    COUNT(DISTINCT r.rentalID) AS number_of_rentals " +
                        "FROM rentals r " +
                        "JOIN vehicles v ON r.plateID = v.plateID " +
                        "LEFT JOIN payments p ON r.rentalID = p.rentalID " +
                        "    AND p.status = 'Active' " +
                        "WHERE r.status = 'Completed' " +
                        "    AND YEAR(r.startDateTime) = ? " +
                        "    AND MONTH(r.startDateTime) BETWEEN ? AND ? " +
                        vehicleFilter +
                        " GROUP BY v.vehicleType, YEAR(r.startDateTime), MONTH(r.startDateTime) " +
                        "ORDER BY YEAR(r.startDateTime), MONTH(r.startDateTime), v.vehicleType";

        try (Connection conn = util.DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            int paramIndex = 1;
            stmt.setInt(paramIndex++, year);
            stmt.setInt(paramIndex++, startMonth);
            stmt.setInt(paramIndex++, endMonth);

            if (vehicleType != null && !vehicleType.equalsIgnoreCase("All")) {
                stmt.setString(paramIndex++, vehicleType);
            }

            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                RevenueData data = new RevenueData();
                data.setVehicleType(rs.getString("vehicleType"));

                // Build period string from year and month
                int rentalYear = rs.getInt("rental_year");
                int rentalMonth = rs.getInt("rental_month");
                String period = String.format("%d-%02d", rentalYear, rentalMonth);
                data.setTimePeriod(period);

                data.setTotalRevenue(rs.getDouble("total_revenue"));
                data.setAverageRevenue(rs.getDouble("avg_revenue"));
                data.setNumberOfRentals(rs.getInt("number_of_rentals"));

                reportData.add(data);
            }

        } catch (SQLException e) {
            System.err.println("Error generating monthly revenue report: " + e.getMessage());
            e.printStackTrace();
        }

        return reportData;
    }

    /**
     * Generate yearly revenue report
     */
    public List<RevenueData> generateYearlyReport(String vehicleType, int startYear, int endYear) {
        List<RevenueData> reportData = new ArrayList<>();

        String vehicleFilter = "";
        if (vehicleType != null && !vehicleType.equalsIgnoreCase("All")) {
            vehicleFilter = "AND v.vehicleType = ?";
        }

        String sql =
                "SELECT " +
                        "    v.vehicleType, " +
                        "    YEAR(r.startDateTime) AS period, " +
                        "    COALESCE(SUM(p.amount), 0) AS total_revenue, " +
                        "    COALESCE(AVG(p.amount), 0) AS avg_revenue, " +
                        "    COUNT(DISTINCT r.rentalID) AS number_of_rentals " +
                        "FROM rentals r " +
                        "JOIN vehicles v ON r.plateID = v.plateID " +
                        "LEFT JOIN payments p ON r.rentalID = p.rentalID " +
                        "    AND p.status = 'Active' " +
                        "WHERE r.status = 'Completed' " +
                        "    AND YEAR(r.startDateTime) BETWEEN ? AND ? " +
                        vehicleFilter +
                        " GROUP BY v.vehicleType, YEAR(r.startDateTime) " +
                        "ORDER BY period, v.vehicleType";

        try (Connection conn = util.DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            int paramIndex = 1;
            stmt.setInt(paramIndex++, startYear);
            stmt.setInt(paramIndex++, endYear);

            if (vehicleType != null && !vehicleType.equalsIgnoreCase("All")) {
                stmt.setString(paramIndex++, vehicleType);
            }

            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                RevenueData data = new RevenueData();
                data.setVehicleType(rs.getString("vehicleType"));
                data.setTimePeriod(String.valueOf(rs.getInt("period")));
                data.setTotalRevenue(rs.getDouble("total_revenue"));
                data.setAverageRevenue(rs.getDouble("avg_revenue"));
                data.setNumberOfRentals(rs.getInt("number_of_rentals"));

                reportData.add(data);
            }

        } catch (SQLException e) {
            System.err.println("Error generating yearly revenue report: " + e.getMessage());
            e.printStackTrace();
        }

        return reportData;
    }

    /**
     * Print formatted report to console
     */
    public void printReport(List<RevenueData> data, String reportType, String vehicleType) {
        System.out.println("\n" + repeatChar("=", 100));

        String title = "RENTAL REVENUE REPORT - " + reportType.toUpperCase();
        if (vehicleType != null && !vehicleType.equalsIgnoreCase("All")) {
            title += " (" + vehicleType + ")";
        }
        System.out.println(title);
        System.out.println(repeatChar("=", 100));

        if (data.isEmpty()) {
            System.out.println("No revenue data found for the specified period.");
            System.out.println(repeatChar("=", 100) + "\n");
            return;
        }

        // Table header
        System.out.printf("%-15s %-15s %-20s %-20s %-15s\n",
                "Vehicle Type", "Period", "Total Revenue (PHP)", "Avg Revenue (PHP)", "# Rentals");
        System.out.println(repeatChar("-", 100));

        // Calculate totals
        double grandTotalRevenue = 0;
        int grandTotalRentals = 0;

        // Display each row
        for (RevenueData rev : data) {
            System.out.printf("%-15s %-15s %,20.2f %,20.2f %-15d\n",
                    rev.getVehicleType(),
                    rev.getTimePeriod(),
                    rev.getTotalRevenue(),
                    rev.getAverageRevenue(),
                    rev.getNumberOfRentals());

            grandTotalRevenue += rev.getTotalRevenue();
            grandTotalRentals += rev.getNumberOfRentals();
        }

        System.out.println(repeatChar("-", 100));

        // Grand total
        double grandAvgRevenue = grandTotalRentals > 0 ? grandTotalRevenue / grandTotalRentals : 0;
        System.out.printf("%-15s %-15s %,20.2f %,20.2f %-15d\n",
                "TOTAL", "", grandTotalRevenue, grandAvgRevenue, grandTotalRentals);

        System.out.println(repeatChar("=", 100) + "\n");
    }

    /**
     * Export report to branded PDF with orange/green theme and logo
     */
    public void exportToPDF(List<RevenueData> data, String filename, String reportType, String vehicleType) {
        Document document = new Document(PageSize.A4);

        try {
            String fullPath = prepareOutputPath(filename);
            PdfWriter.getInstance(document, new FileOutputStream(fullPath));
            document.open();

            // Use branding helper
            String title = "Rental Revenue Report";
            if (reportType != null && !reportType.isEmpty()) {
                title += " - " + reportType;
            }
            if (vehicleType != null && !vehicleType.equalsIgnoreCase("All")) {
                title += " (" + vehicleType + ")";
            }
            PDFBrandingHelper.addHeaderSection(document, title, null);

            if (data.isEmpty()) {
                Paragraph noData = new Paragraph("No revenue data found for the specified period.",
                        PDFBrandingHelper.getBodyFont());
                noData.setAlignment(Element.ALIGN_CENTER);
                noData.setSpacingBefore(30);
                document.add(noData);
                document.close();
                System.out.println("✓ PDF saved to: " + fullPath);
                return;
            }

            // Create table with branded styling
            PdfPTable table = new PdfPTable(5);
            table.setWidthPercentage(100);
            table.setWidths(new float[]{1.5f, 1.5f, 2f, 2f, 1.5f});

            // Add headers with green background
            String[] headers = {"Vehicle Type", "Period", "Total Revenue (PHP)", "Avg Revenue (PHP)", "# Rentals"};
            for (String header : headers) {
                table.addCell(PDFBrandingHelper.createHeaderCell(header));
            }

            // Add data rows with alternating colors
            double grandTotalRevenue = 0;
            int grandTotalRentals = 0;

            for (int i = 0; i < data.size(); i++) {
                RevenueData rev = data.get(i);

                table.addCell(PDFBrandingHelper.createDataCell(rev.getVehicleType(), i));
                table.addCell(PDFBrandingHelper.createDataCell(rev.getTimePeriod(), i, Element.ALIGN_CENTER));
                table.addCell(PDFBrandingHelper.createDataCell(
                        String.format("₱%,.2f", rev.getTotalRevenue()), i, Element.ALIGN_RIGHT));
                table.addCell(PDFBrandingHelper.createDataCell(
                        String.format("₱%,.2f", rev.getAverageRevenue()), i, Element.ALIGN_RIGHT));
                table.addCell(PDFBrandingHelper.createDataCell(
                        String.valueOf(rev.getNumberOfRentals()), i, Element.ALIGN_CENTER));

                grandTotalRevenue += rev.getTotalRevenue();
                grandTotalRentals += rev.getNumberOfRentals();
            }

            document.add(table);

            // Add summary section
            Paragraph summaryTitle = new Paragraph("\nSummary Statistics", PDFBrandingHelper.getSubheaderFont());
            summaryTitle.setSpacingBefore(20);
            summaryTitle.setSpacingAfter(10);
            document.add(summaryTitle);

            PdfPTable summaryTable = new PdfPTable(2);
            summaryTable.setWidthPercentage(60);
            summaryTable.setWidths(new float[]{2f, 1f});

            double grandAvgRevenue = grandTotalRentals > 0 ? grandTotalRevenue / grandTotalRentals : 0;

            // Find highest revenue vehicle type
            Map<String, Double> revenueByType = new HashMap<>();
            for (RevenueData rev : data) {
                revenueByType.merge(rev.getVehicleType(), rev.getTotalRevenue(), Double::sum);
            }
            String topVehicle = revenueByType.entrySet().stream()
                    .max(Map.Entry.comparingByValue())
                    .map(Map.Entry::getKey)
                    .orElse("N/A");

            PDFBrandingHelper.addSummaryRow(summaryTable, "Total Revenue:", String.format("₱%,.2f", grandTotalRevenue));
            PDFBrandingHelper.addSummaryRow(summaryTable, "Total Rentals:", String.valueOf(grandTotalRentals));
            PDFBrandingHelper.addSummaryRow(summaryTable, "Average per Rental:", String.format("₱%,.2f", grandAvgRevenue));
            PDFBrandingHelper.addSummaryRow(summaryTable, "Top Vehicle Type:", topVehicle);

            document.add(summaryTable);

            // Add footer
            PDFBrandingHelper.addFooter(document,
                    new SimpleDateFormat("MMMM dd, yyyy 'at' hh:mm a").format(new Date()));

            System.out.println("✓ PDF saved to: " + fullPath);

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
        RentalRevenueReport report = new RentalRevenueReport();

        System.out.println("=== RENTAL REVENUE REPORT TEST ===\n");

        // Test monthly report for all vehicles
        System.out.println("Testing Monthly Report for 2024 (All Vehicles)...");
        List<RevenueData> monthlyData = report.generateMonthlyReport("All", 2024, 1, 12);
        report.printReport(monthlyData, "Monthly", "All");
        report.exportToPDF(monthlyData, "Revenue_Report_Monthly_2024.pdf", "Monthly", "All");

        // Test yearly report for E-Scooter
        System.out.println("\nTesting Yearly Report for E-Scooter...");
        List<RevenueData> yearlyData = report.generateYearlyReport("E-Scooter", 2023, 2024);
        report.printReport(yearlyData, "Yearly", "E-Scooter");
        report.exportToPDF(yearlyData, "Revenue_Report_Yearly_EScooter.pdf", "Yearly", "E-Scooter");

        System.out.println("=== TEST COMPLETE ===");
    }
}