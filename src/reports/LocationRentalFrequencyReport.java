
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
                        "    AND r.status = 'Active' " +
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
                        "    AND r.status = 'Active' " +
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
     * Export report to PDF
     */
    public void exportToPDF(List<LocationFrequencyData> data, String filename, int year, int month) {
        Document document = new Document(PageSize.A4.rotate()); // Landscape

        try {
            PdfWriter.getInstance(document, new FileOutputStream(filename));
            document.open();

            // Add title
            Font titleFont = new Font(Font.FontFamily.HELVETICA, 18, Font.BOLD);
            String[] months = {"", "January", "February", "March", "April", "May", "June",
                    "July", "August", "September", "October", "November", "December"};

            String title = "LOCATION RENTAL FREQUENCY REPORT";
            if (month > 0) {
                title += " - " + months[month] + " " + year;
            } else {
                title += " - Year " + year;
            }

            Paragraph titlePara = new Paragraph(title, titleFont);
            titlePara.setAlignment(Element.ALIGN_CENTER);
            titlePara.setSpacingAfter(20);
            document.add(titlePara);

            if (data.isEmpty()) {
                Paragraph noData = new Paragraph("No location rental data found for the specified period.");
                noData.setAlignment(Element.ALIGN_CENTER);
                document.add(noData);
                document.close();
                return;
            }

            // Create table
            PdfPTable table = new PdfPTable(7); // 7 columns
            table.setWidthPercentage(100);
            table.setWidths(new float[]{1.5f, 3f, 1f, 1.5f, 1.8f, 1.5f, 1f});

            // Header font
            Font headerFont = new Font(Font.FontFamily.HELVETICA, 9, Font.BOLD);

            // Add headers
            String[] headers = {"Location ID", "Location Name", "Rentals", "Avg Days",
                    "Revenue (PHP)", "Top Type", "Deployed"};

            for (String header : headers) {
                PdfPCell cell = new PdfPCell(new Phrase(header, headerFont));
                cell.setBackgroundColor(BaseColor.LIGHT_GRAY);
                cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                cell.setPadding(5);
                table.addCell(cell);
            }

            // Add data rows
            Font dataFont = new Font(Font.FontFamily.HELVETICA, 8);

            int totalRentals = 0;
            double totalRevenue = 0;
            double totalDuration = 0;
            int totalDeployed = 0;

            for (LocationFrequencyData location : data) {
                table.addCell(new Phrase(location.getLocationID(), dataFont));
                table.addCell(new Phrase(location.getLocationName(), dataFont));
                table.addCell(new Phrase(String.valueOf(location.getNumberOfRentals()), dataFont));
                table.addCell(new Phrase(String.format("%.1f", location.getAverageRentalDuration()), dataFont));
                table.addCell(new Phrase(String.format("%,.2f", location.getTotalRevenue()), dataFont));

                String topType = location.getMostRentedVehicleType() != null
                        ? location.getMostRentedVehicleType() : "N/A";
                table.addCell(new Phrase(topType, dataFont));

                table.addCell(new Phrase(String.valueOf(location.getVehicleDeploymentCount()), dataFont));

                totalRentals += location.getNumberOfRentals();
                totalRevenue += location.getTotalRevenue();
                totalDuration += location.getAverageRentalDuration() * location.getNumberOfRentals();
                totalDeployed += location.getVehicleDeploymentCount();
            }

            // Add total row
            Font totalFont = new Font(Font.FontFamily.HELVETICA, 9, Font.BOLD);
            PdfPCell totalLabel = new PdfPCell(new Phrase("TOTAL (" + data.size() + " Locations)", totalFont));
            totalLabel.setColspan(2);
            totalLabel.setBackgroundColor(BaseColor.LIGHT_GRAY);
            table.addCell(totalLabel);

            table.addCell(new Phrase(String.valueOf(totalRentals), totalFont));

            double avgDuration = totalRentals > 0 ? totalDuration / totalRentals : 0;
            table.addCell(new Phrase(String.format("%.1f", avgDuration), totalFont));

            table.addCell(new Phrase(String.format("%,.2f", totalRevenue), totalFont));
            table.addCell(new Phrase("", totalFont));
            table.addCell(new Phrase(String.valueOf(totalDeployed), totalFont));

            document.add(table);

            // Add business insights
            if (!data.isEmpty()) {
                Paragraph insights = new Paragraph("\n\nBUSINESS INSIGHTS:",
                        new Font(Font.FontFamily.HELVETICA, 12, Font.BOLD));
                document.add(insights);

                Font insightFont = new Font(Font.FontFamily.HELVETICA, 10);

                LocationFrequencyData topLocation = data.get(0);
                Paragraph insight1 = new Paragraph(
                        String.format("• Busiest Location: %s with %d rentals",
                                topLocation.getLocationName(), topLocation.getNumberOfRentals()), insightFont);
                document.add(insight1);

                LocationFrequencyData bestRevenue = data.stream()
                        .max(Comparator.comparingDouble(l -> l.getTotalRevenue() / l.getNumberOfRentals()))
                        .orElse(null);
                if (bestRevenue != null) {
                    Paragraph insight2 = new Paragraph(
                            String.format("• Highest Revenue per Rental: %s (PHP %.2f per rental)",
                                    bestRevenue.getLocationName(),
                                    bestRevenue.getTotalRevenue() / bestRevenue.getNumberOfRentals()), insightFont);
                    document.add(insight2);
                }
            }

            // Add footer
            Paragraph footer = new Paragraph("\nGenerated on: " +
                    new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()),
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