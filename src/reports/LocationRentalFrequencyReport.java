
/**
 * LOCATION RENTAL FREQUENCY REPORT
 *
 * PURPOSE:
 * Analyze rental patterns by location to identify high-traffic pickup points
 * and optimize vehicle deployment strategy. Shows rental volume and average
 * duration per location.
 *
 * DATA SOURCES:
 * - Rental Records (rentalID, locationID, startDateTime, endDateTime)
 * - Deployment Transaction Records (vehicleID, locationID, deploymentDate)
 * - Vehicle Records (plateID, vehicleType)
 * - Location Records (locationID, locationName, address)
 *
 * REPORT OUTPUT:
 * - Location ID
 * - Location Name
 * - Address
 * - Number of Rentals (count of rentals originating from this location)
 * - Average Rental Duration (in hours)
 * - Total Revenue Generated (sum of payments for rentals from this location)
 * - Most Rented Vehicle Type (E-Scooter or E-Bike)
 * - Vehicle Deployment Count (number of vehicles deployed to this location)
 *
 * USER INPUTS:
 * 1. Report Period Type (Day/Month/Year)
 * 2. Year (required)
 * 3. Month (required if period is Day or Month)
 * 4. Day (required if period is Day)
 *
 * EXPECTED METHODS:
 * - generateDailyReport(int year, int month, int day)
 * - generateMonthlyReport(int year, int month)
 * - generateYearlyReport(int year)
 * - printReport(List<LocationFrequencyData> data) - Format and display results
 * - getRentalDuration(Timestamp start, Timestamp end) - Calculate duration in hours
 *
 * SQL LOGIC:
 * - JOIN rentals with locations on locationID
 * - Filter by date range (rental startDateTime within specified period)
 * - GROUP BY locationID, locationName
 * - Calculate:
 *   * COUNT(rentalID) - number of rentals
 *   * AVG(TIMESTAMPDIFF(HOUR, startDateTime, endDateTime)) - avg duration
 *   * COUNT(DISTINCT plateID) - number of unique vehicles
 * - JOIN with deployments to get deployment counts
 * - Sort by number of rentals (descending) to show busiest locations first
 *
 * BUSINESS INSIGHTS:
 * - High rental frequency = popular location, may need more vehicles
 * - Long average duration = customers prefer this location for long trips
 * - Low rental frequency = consider removing vehicles or marketing efforts
 * - Helps optimize vehicle distribution across locations
 *
 * EXAMPLE OUTPUT:
 * ================================================================
 * LOCATION RENTAL FREQUENCY REPORT - October 2024
 * ================================================================
 * Location              | Rentals | Avg Duration | Revenue    | Top Type  | Deployed
 * -------------------------------------------------------------------------------------
 * LOC-001: Ayala Mall   | 150     | 2.5 hrs      | P 18,750   | E-Scooter | 25
 * LOC-002: BGC Central  | 120     | 3.2 hrs      | P 19,200   | E-Bike    | 20
 * LOC-003: Makati Park  | 80      | 1.8 hrs      | P 7,200    | E-Scooter | 15
 * -------------------------------------------------------------------------------------
 * Total: 3 Locations    | 350     | 2.5 hrs      | P 45,150   |           | 60
 * ================================================================
 */
package reports;

import dao.*;
import model.*;
import java.sql.*;
import java.util.ArrayList;
import java.util.Comparator;
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
 * LOCATION RENTAL FREQUENCY REPORT
 * Analyze rental patterns by location to identify high-traffic pickup points
 */
public class LocationRentalFrequencyReport {

    private RentalDAO rentalDAO;
    private DeploymentDAO deploymentDAO;
    private VehicleDAO vehicleDAO;
    private LocationDAO locationDAO;

    public LocationRentalFrequencyReport() {
        this.rentalDAO = new RentalDAO();
        this.deploymentDAO = new DeploymentDAO();
        this.vehicleDAO = new VehicleDAO();
        this.locationDAO = new LocationDAO();
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
     * Inner class to hold location frequency report data
     */
    public static class LocationFrequencyData {
        private String locationID;
        private String locationName;
        private String address;
        private int numberOfRentals;
        private double averageRentalDuration;
        private double totalRevenue;
        private String mostRentedVehicleType;
        private int vehicleDeploymentCount;

        // Constructors
        public LocationFrequencyData() {}

        public LocationFrequencyData(String locationID, String locationName, String address,
                                     int numberOfRentals, double averageRentalDuration,
                                     double totalRevenue, String mostRentedVehicleType,
                                     int vehicleDeploymentCount) {
            this.locationID = locationID;
            this.locationName = locationName;
            this.address = address;
            this.numberOfRentals = numberOfRentals;
            this.averageRentalDuration = averageRentalDuration;
            this.totalRevenue = totalRevenue;
            this.mostRentedVehicleType = mostRentedVehicleType;
            this.vehicleDeploymentCount = vehicleDeploymentCount;
        }

        // Getters and Setters
        public String getLocationID() { return locationID; }
        public void setLocationID(String locationID) { this.locationID = locationID; }

        public String getLocationName() { return locationName; }
        public void setLocationName(String locationName) { this.locationName = locationName; }

        public String getAddress() { return address; }
        public void setAddress(String address) { this.address = address; }

        public int getNumberOfRentals() { return numberOfRentals; }
        public void setNumberOfRentals(int numberOfRentals) { this.numberOfRentals = numberOfRentals; }

        public double getAverageRentalDuration() { return averageRentalDuration; }
        public void setAverageRentalDuration(double averageRentalDuration) {
            this.averageRentalDuration = averageRentalDuration;
        }

        public double getTotalRevenue() { return totalRevenue; }
        public void setTotalRevenue(double totalRevenue) { this.totalRevenue = totalRevenue; }

        public String getMostRentedVehicleType() { return mostRentedVehicleType; }
        public void setMostRentedVehicleType(String mostRentedVehicleType) {
            this.mostRentedVehicleType = mostRentedVehicleType;
        }

        public int getVehicleDeploymentCount() { return vehicleDeploymentCount; }
        public void setVehicleDeploymentCount(int vehicleDeploymentCount) {
            this.vehicleDeploymentCount = vehicleDeploymentCount;
        }
    }

    /**
     * Generate monthly location rental frequency report
     */
    public List<LocationFrequencyData> generateMonthlyReport(int year, int month) {
        List<LocationFrequencyData> reportData = new ArrayList<>();

        String sql =
                "SELECT " +
                        "    l.locationID, " +
                        "    l.name, " +
                        "    COUNT(DISTINCT r.rentalID) AS number_of_rentals, " +
                        "    COALESCE(AVG(TIMESTAMPDIFF(DAY, r.startDateTime, r.endDateTime)), 0) AS avg_duration_days, " +
                        "    COALESCE(SUM(p.amount), 0) AS total_revenue, " +
                        "    (SELECT v2.vehicleType " +
                        "     FROM rentals r2 " +
                        "     JOIN vehicles v2 ON r2.plateID = v2.plateID " +
                        "     WHERE r2.locationID = l.locationID " +
                        "     AND r2.status = 'Active' " +
                        "     AND YEAR(r2.startDateTime) = ? " +
                        "     AND MONTH(r2.startDateTime) = ? " +
                        "     GROUP BY v2.vehicleType " +
                        "     ORDER BY COUNT(*) DESC " +
                        "     LIMIT 1) AS most_rented_type, " +
                        "    (SELECT COUNT(DISTINCT d.plateID) " +
                        "     FROM deployments d " +
                        "     WHERE d.locationID = l.locationID " +
                        "     AND d.status = 'Active' " +
                        "     AND YEAR(d.startDate) = ? " +
                        "     AND MONTH(d.startDate) = ?) AS deployment_count " +
                        "FROM locations l " +
                        "LEFT JOIN rentals r ON l.locationID = r.locationID " +
                        "    AND r.status = 'Completed' " +
                        "    AND YEAR(r.startDateTime) = ? " +
                        "    AND MONTH(r.startDateTime) = ? " +
                        "LEFT JOIN payments p ON r.rentalID = p.rentalID " +
                        "    AND p.status = 'Active' " +
                        "WHERE l.status = 'Active' " +
                        "GROUP BY l.locationID, l.name " +
                        "HAVING number_of_rentals > 0 " +
                        "ORDER BY number_of_rentals DESC, total_revenue DESC";

        try (Connection conn = util.DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, year);  // most_rented_type year
            stmt.setInt(2, month); // most_rented_type month
            stmt.setInt(3, year);  // deployment_count year
            stmt.setInt(4, month); // deployment_count month
            stmt.setInt(5, year);  // main query year
            stmt.setInt(6, month); // main query month

            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                LocationFrequencyData data = new LocationFrequencyData();
                data.setLocationID(rs.getString("locationID"));
                data.setLocationName(rs.getString("name"));

                data.setNumberOfRentals(rs.getInt("number_of_rentals"));
                data.setAverageRentalDuration(rs.getDouble("avg_duration_days"));
                data.setTotalRevenue(rs.getDouble("total_revenue"));
                data.setMostRentedVehicleType(rs.getString("most_rented_type"));
                data.setVehicleDeploymentCount(rs.getInt("deployment_count"));

                reportData.add(data);
            }

        } catch (SQLException e) {
            System.err.println("Error generating monthly location frequency report: " + e.getMessage());
            e.printStackTrace();
        }

        return reportData;
    }

    /**
     * Generate yearly location rental frequency report
     */
    public List<LocationFrequencyData> generateYearlyReport(int year) {
        List<LocationFrequencyData> reportData = new ArrayList<>();

        String sql =
                "SELECT " +
                        "    l.locationID, " +
                        "    l.name, " +
                        "    COUNT(DISTINCT r.rentalID) AS number_of_rentals, " +
                        "    COALESCE(AVG(TIMESTAMPDIFF(DAY, r.startDateTime, r.endDateTime)), 0) AS avg_duration_days, " +
                        "    COALESCE(SUM(p.amount), 0) AS total_revenue, " +
                        "    (SELECT v2.vehicleType " +
                        "     FROM rentals r2 " +
                        "     JOIN vehicles v2 ON r2.plateID = v2.plateID " +
                        "     WHERE r2.locationID = l.locationID " +
                        "     AND r2.status = 'Active' " +
                        "     AND YEAR(r2.startDateTime) = ? " +
                        "     GROUP BY v2.vehicleType " +
                        "     ORDER BY COUNT(*) DESC " +
                        "     LIMIT 1) AS most_rented_type, " +
                        "    (SELECT COUNT(DISTINCT d.plateID) " +
                        "     FROM deployments d " +
                        "     WHERE d.locationID = l.locationID " +
                        "     AND d.status = 'Active' " +
                        "     AND YEAR(d.startDate) = ?) AS deployment_count " +
                        "FROM locations l " +
                        "LEFT JOIN rentals r ON l.locationID = r.locationID " +
                        "    AND r.status = 'Completed' " +
                        "    AND YEAR(r.startDateTime) = ? " +
                        "LEFT JOIN payments p ON r.rentalID = p.rentalID " +
                        "    AND p.status = 'Active' " +
                        "WHERE l.status = 'Active' " +
                        "GROUP BY l.locationID, l.name " +
                        "HAVING number_of_rentals > 0 " +
                        "ORDER BY number_of_rentals DESC, total_revenue DESC";

        try (Connection conn = util.DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, year);  // most_rented_type year
            stmt.setInt(2, year);  // deployment_count year
            stmt.setInt(3, year);  // main query year

            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                LocationFrequencyData data = new LocationFrequencyData();
                data.setLocationID(rs.getString("locationID"));
                data.setLocationName(rs.getString("name"));

                data.setNumberOfRentals(rs.getInt("number_of_rentals"));
                data.setAverageRentalDuration(rs.getDouble("avg_duration_days"));
                data.setTotalRevenue(rs.getDouble("total_revenue"));
                data.setMostRentedVehicleType(rs.getString("most_rented_type"));
                data.setVehicleDeploymentCount(rs.getInt("deployment_count"));

                reportData.add(data);
            }

        } catch (SQLException e) {
            System.err.println("Error generating yearly location frequency report: " + e.getMessage());
            e.printStackTrace();
        }

        return reportData;
    }

    /**
     * Print formatted report to console
     */
    public void printReport(List<LocationFrequencyData> data, int year, int month) {
        System.out.println("\n" + repeatChar("=", 140));

        String[] months = {"", "January", "February", "March", "April", "May", "June",
                "July", "August", "September", "October", "November", "December"};

        if (month > 0) {
            System.out.printf("LOCATION RENTAL FREQUENCY REPORT - %s %d\n", months[month], year);
        } else {
            System.out.printf("LOCATION RENTAL FREQUENCY REPORT - Year %d\n", year);
        }
        System.out.println(repeatChar("=", 140));

        if (data.isEmpty()) {
            System.out.println("No location rental data found for the specified period.");
            System.out.println(repeatChar("=", 140) + "\n");
            return;
        }

        // Table header
        System.out.printf("%-15s %-30s %-8s %-12s %-15s %-15s %-10s\n",
                "Location ID", "Location Name", "Rentals", "Avg Days", "Revenue (PHP)",
                "Top Type", "Deployed");
        System.out.println(repeatChar("-", 140));

        // Calculate totals
        int totalRentals = 0;
        double totalRevenue = 0;
        double totalDuration = 0;
        int totalDeployed = 0;

        // Display each location
        for (LocationFrequencyData location : data) {
            System.out.printf("%-15s %-30s %-8d %,12.1f %,15.2f %-15s %-10d\n",
                    location.getLocationID(),
                    location.getLocationName(),
                    location.getNumberOfRentals(),
                    location.getAverageRentalDuration(),
                    location.getTotalRevenue(),
                    location.getMostRentedVehicleType() != null ? location.getMostRentedVehicleType() : "N/A",
                    location.getVehicleDeploymentCount());

            totalRentals += location.getNumberOfRentals();
            totalRevenue += location.getTotalRevenue();
            totalDuration += location.getAverageRentalDuration() * location.getNumberOfRentals();
            totalDeployed += location.getVehicleDeploymentCount();
        }

        System.out.println(repeatChar("-", 140));

        // Summary
        double avgDuration = totalRentals > 0 ? totalDuration / totalRentals : 0;

        System.out.printf("Total: %d Locations%10s %-8d %,12.1f %,15.2f %15s %-10d\n",
                data.size(), "", totalRentals, avgDuration, totalRevenue, "", totalDeployed);

        System.out.println(repeatChar("=", 140));

        // Business insights
        System.out.println("\nBUSINESS INSIGHTS:");
        if (!data.isEmpty()) {
            LocationFrequencyData topLocation = data.get(0);
            System.out.printf("- Busiest Location: %s with %d rentals\n",
                    topLocation.getLocationName(), topLocation.getNumberOfRentals());

            // Find location with highest revenue per rental
            LocationFrequencyData bestRevenue = data.stream()
                    .max(Comparator.comparingDouble(l -> l.getTotalRevenue() / l.getNumberOfRentals()))
                    .orElse(null);
            if (bestRevenue != null) {
                System.out.printf("- Highest Revenue per Rental: %s (PHP %.2f per rental)\n",
                        bestRevenue.getLocationName(),
                        bestRevenue.getTotalRevenue() / bestRevenue.getNumberOfRentals());
            }

            // Find location with longest average duration
            LocationFrequencyData longestDuration = data.stream()
                    .max(Comparator.comparingDouble(LocationFrequencyData::getAverageRentalDuration))
                    .orElse(null);
            if (longestDuration != null) {
                System.out.printf("- Longest Average Duration: %s (%.1f days)\n",
                        longestDuration.getLocationName(), longestDuration.getAverageRentalDuration());
            }
        }
        System.out.println(repeatChar("=", 140) + "\n");
    }

    /**
     * Export report to branded PDF
     */
    public void exportToPDF(List<LocationFrequencyData> data, String filename, int year, int month, String chartImagePath) {
        Document document = new Document(PageSize.A4.rotate());

        try {
            String fullPath = prepareOutputPath(filename);
            PdfWriter.getInstance(document, new FileOutputStream(fullPath));
            document.open();

            // Title
            String[] months = {"", "January", "February", "March", "April", "May", "June",
                    "July", "August", "September", "October", "November", "December"};
            String title = "Location Rental Frequency Report";
            if (month > 0) {
                title += " - " + months[month] + " " + year;
            } else {
                title += " - Year " + year;
            }

            PDFBrandingHelper.addHeaderSection(document, title, null);

            if (chartImagePath != null) {
                try {
                    com.itextpdf.text.Image chartImage = com.itextpdf.text.Image.getInstance(chartImagePath);
                    // Scale image to fit width, preserve aspect ratio
                    float maxWidth = document.getPageSize().getWidth() - document.leftMargin() - document.rightMargin();
                    chartImage.scaleToFit(maxWidth, 350); // Max height 350
                    chartImage.setAlignment(Element.ALIGN_CENTER);
                    chartImage.setSpacingBefore(10);
                    chartImage.setSpacingAfter(20);
                    document.add(chartImage);
                } catch (Exception e) {
                    System.err.println("Could not load chart image: " + e.getMessage());
                }
            }

            if (data.isEmpty()) {
                Paragraph noData = new Paragraph("No location rental data found for the specified period.",
                        new Font(Font.FontFamily.HELVETICA, 9));
                noData.setAlignment(Element.ALIGN_CENTER);
                noData.setSpacingBefore(30);
                document.add(noData);
                document.close();
                System.out.println("✓ PDF saved to: " + fullPath);
                return;
            }

            // Table - Updated to match your actual data fields
            PdfPTable table = new PdfPTable(6);
            table.setWidthPercentage(100);
            table.setWidths(new float[]{2f, 2f, 1.5f, 2f, 1.5f, 2f});

            // Headers - Updated to match your data
            String[] headers = {"Location ID", "Location Name", "Rentals",
                    "Total Revenue (PHP)", "Avg Duration (days)", "Most Rented Type"};
            for (String header : headers) {
                table.addCell(PDFBrandingHelper.createHeaderCell(header));
            }

            // Data
            int totalRentals = 0;
            double totalRevenue = 0;

            for (int i = 0; i < data.size(); i++) {
                LocationFrequencyData loc = data.get(i);

                table.addCell(PDFBrandingHelper.createDataCell(loc.getLocationID(), i));
                table.addCell(PDFBrandingHelper.createDataCell(loc.getLocationName(), i));
                table.addCell(PDFBrandingHelper.createDataCell(String.valueOf(loc.getNumberOfRentals()), i, Element.ALIGN_CENTER));
                table.addCell(PDFBrandingHelper.createDataCell(String.format("₱%,.2f", loc.getTotalRevenue()), i, Element.ALIGN_RIGHT));
                table.addCell(PDFBrandingHelper.createDataCell(String.format("%.1f", loc.getAverageRentalDuration()), i, Element.ALIGN_CENTER));
                table.addCell(PDFBrandingHelper.createDataCell(loc.getMostRentedVehicleType() != null ? loc.getMostRentedVehicleType() : "N/A", i));

                totalRentals += loc.getNumberOfRentals();
                totalRevenue += loc.getTotalRevenue();
            }

            document.add(table);

            // Summary
            Paragraph summaryTitle = new Paragraph("\nSummary Statistics",
                    new Font(Font.FontFamily.HELVETICA, 12, Font.BOLD, PDFBrandingHelper.BRAND_GREEN));
            summaryTitle.setSpacingBefore(20);
            summaryTitle.setSpacingAfter(10);
            document.add(summaryTitle);

            PdfPTable summaryTable = new PdfPTable(2);
            summaryTable.setWidthPercentage(60);
            summaryTable.setWidths(new float[]{2f, 1f});

            double avgRevenue = totalRentals > 0 ? totalRevenue / totalRentals : 0;
            LocationFrequencyData topLocation = data.stream()
                    .max((a, b) -> Integer.compare(a.getNumberOfRentals(), b.getNumberOfRentals()))
                    .orElse(null);
            String topLocationName = topLocation != null ? topLocation.getLocationName() : "N/A";

            PDFBrandingHelper.addSummaryRow(summaryTable, "Total Locations:", String.valueOf(data.size()));
            PDFBrandingHelper.addSummaryRow(summaryTable, "Total Rentals:", String.valueOf(totalRentals));
            PDFBrandingHelper.addSummaryRow(summaryTable, "Total Revenue:", String.format("₱%,.2f", totalRevenue));
            PDFBrandingHelper.addSummaryRow(summaryTable, "Avg Revenue per Rental:", String.format("₱%,.2f", avgRevenue));
            PDFBrandingHelper.addSummaryRow(summaryTable, "Most Popular Location:", topLocationName);

            document.add(summaryTable);

            // Footer
            PDFBrandingHelper.addFooter(document,
                    new SimpleDateFormat("MMMM dd, yyyy 'at' hh:mm a").format(new java.util.Date()));

            System.out.println("✓ PDF saved to: " + fullPath);

        } catch (DocumentException | IOException e) {
            System.err.println("Error generating PDF report: " + e.getMessage());
            e.printStackTrace();
        } finally {
            document.close();
        }
    }

    //Overloaded version
    public void exportToPDF(List<LocationFrequencyData> data, String filename, int year, int month) {
        exportToPDF(data, filename, year, month, null);
    }

    /**
     * Main method for testing
     */
    public static void main(String[] args) {
        LocationRentalFrequencyReport report = new LocationRentalFrequencyReport();

        System.out.println("=== LOCATION RENTAL FREQUENCY REPORT TEST ===\n");

        // Test monthly report
        System.out.println("Testing Monthly Report for October 2024...");
        List<LocationFrequencyData> monthlyData = report.generateMonthlyReport(2024, 10);
        report.printReport(monthlyData, 2024, 10);
        report.exportToPDF(monthlyData, "Location_Frequency_Report_Oct2024.pdf", 2024, 10);

        // Test yearly report
        System.out.println("\nTesting Yearly Report for 2024...");
        List<LocationFrequencyData> yearlyData = report.generateYearlyReport(2024);
        report.printReport(yearlyData, 2024, 0);
        report.exportToPDF(yearlyData, "Location_Frequency_Report_2024.pdf", 2024, 0);

        System.out.println("=== TEST COMPLETE ===");
    }
}