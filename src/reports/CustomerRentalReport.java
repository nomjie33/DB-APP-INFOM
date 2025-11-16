/**
 * CUSTOMER RENTAL + DEMOGRAPHICS + PENALTY RISK REPORT
 *
 * PURPOSE:
 * Comprehensive analysis of customer rental behavior, geographic distribution,
 * and penalty risk assessment. Supports business decisions for branch locations,
 * targeted marketing, and customer risk management.
 *
 * SECTION 1: CUSTOMER RENTAL SUMMARY
 * - Customer rental activity and spending patterns
 * - Filtered by selected month/year
 *
 * SECTION 2: CUSTOMER DEMOGRAPHICS
 * - Geographic distribution by City and Barangay
 * - NOT FILTERED by month (shows all customers)
 * - Helps determine potential branch locations
 *
 * SECTION 3: CUSTOMER PENALTY / RISK ANALYSIS
 * - Penalty payment history and behavioral risk scoring
 * - Filtered by selected month/year
 * - Risk Score = 0.6 * (Penalty $ / Rental $) + 0.4 * (Penalties / Rentals)
 * - Color-coded risk levels: Low (0-50%), Medium (51-200%), High (201%+)
 *
 * SECTION 4: SUMMARY / KEY METRICS
 * - Overall business performance snapshot
 * - Filtered by selected month/year
 */
package reports;

import dao.*;
import model.*;
import java.math.BigDecimal;
import java.sql.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.HashMap;
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
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;

/**
 * CUSTOMER RENTAL REPORT
 * Analyze customer rental behavior, demographics, and penalty risk.
 */
public class CustomerRentalReport {

    private RentalDAO rentalDAO;
    private CustomerDAO customerDAO;
    private PaymentDAO paymentDAO;
    private PenaltyDAO penaltyDAO;

    public CustomerRentalReport() {
        this.rentalDAO = new RentalDAO();
        this.customerDAO = new CustomerDAO();
        this.paymentDAO = new PaymentDAO();
        this.penaltyDAO = new PenaltyDAO();
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
     * Inner class: Customer Rental Summary Data
     */
    public static class CustomerRentalData {
        private String customerID;
        private String firstName;
        private String lastName;
        private int numberOfRentals;
        private double totalRentalCost;
        private double averageRentalCost;
        private double totalRentalDuration;
        private double averageRentalDuration;
        private Timestamp mostRecentRentalDate;

        public CustomerRentalData() {}

        public CustomerRentalData(String customerID, String firstName, String lastName,
                                  int numberOfRentals, double totalRentalCost, double averageRentalCost,
                                  double totalRentalDuration, double averageRentalDuration,
                                  Timestamp mostRecentRentalDate) {
            this.customerID = customerID;
            this.firstName = firstName;
            this.lastName = lastName;
            this.numberOfRentals = numberOfRentals;
            this.totalRentalCost = totalRentalCost;
            this.averageRentalCost = averageRentalCost;
            this.totalRentalDuration = totalRentalDuration;
            this.averageRentalDuration = averageRentalDuration;
            this.mostRecentRentalDate = mostRecentRentalDate;
        }

        // Getters and Setters
        public String getCustomerID() { return customerID; }
        public void setCustomerID(String customerID) { this.customerID = customerID; }
        public String getFirstName() { return firstName; }
        public void setFirstName(String firstName) { this.firstName = firstName; }
        public String getLastName() { return lastName; }
        public void setLastName(String lastName) { this.lastName = lastName; }
        public String getFullName() { return firstName + " " + lastName; }
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
    }

    /**
     * Inner class: Customer Demographics Data
     */
    public static class CustomerDemographicsData {
        private String cityName;
        private String barangayName;
        private int customerCount;
        private List<String> customerNames;

        public CustomerDemographicsData() {
            this.customerNames = new ArrayList<>();
        }

        public CustomerDemographicsData(String cityName, String barangayName, int customerCount) {
            this.cityName = cityName;
            this.barangayName = barangayName;
            this.customerCount = customerCount;
            this.customerNames = new ArrayList<>();
        }

        // Getters and Setters
        public String getCityName() { return cityName; }
        public void setCityName(String cityName) { this.cityName = cityName; }
        public String getBarangayName() { return barangayName; }
        public void setBarangayName(String barangayName) { this.barangayName = barangayName; }
        public int getCustomerCount() { return customerCount; }
        public void setCustomerCount(int customerCount) { this.customerCount = customerCount; }
        public List<String> getCustomerNames() { return customerNames; }
        public void setCustomerNames(List<String> customerNames) { this.customerNames = customerNames; }
        public void addCustomerName(String name) { this.customerNames.add(name); }
    }

    /**
     * Inner class: Customer Penalty Risk Data
     */
    public static class CustomerPenaltyRiskData {
        private String customerID;
        private String firstName;
        private String lastName;
        private int numberOfRentals;
        private double totalRentalPayments;
        private double totalPenaltyPayments;
        private int numberOfPenalties;
        private double penaltyRiskScore;
        private String riskLevel;

        public CustomerPenaltyRiskData() {}

        public CustomerPenaltyRiskData(String customerID, String firstName, String lastName,
                                       int numberOfRentals, double totalRentalPayments,
                                       double totalPenaltyPayments, int numberOfPenalties) {
            this.customerID = customerID;
            this.firstName = firstName;
            this.lastName = lastName;
            this.numberOfRentals = numberOfRentals;
            this.totalRentalPayments = totalRentalPayments;
            this.totalPenaltyPayments = totalPenaltyPayments;
            this.numberOfPenalties = numberOfPenalties;
            calculateRiskScore();
        }

        private void calculateRiskScore() {
            double financialRisk = 0;
            double behavioralRisk = 0;

            if (totalRentalPayments > 0) {
                financialRisk = (totalPenaltyPayments / totalRentalPayments) * 100;
            }

            if (numberOfRentals > 0) {
                behavioralRisk = ((double) numberOfPenalties / numberOfRentals) * 100;
            }

            this.penaltyRiskScore = (0.6 * financialRisk) + (0.4 * behavioralRisk);

            // Determine risk level
            if (penaltyRiskScore >= 201) {
                this.riskLevel = "High Risk";
            } else if (penaltyRiskScore >= 51) {
                this.riskLevel = "Medium Risk";
            } else {
                this.riskLevel = "Low Risk";
            }
        }

        // Getters and Setters
        public String getCustomerID() { return customerID; }
        public void setCustomerID(String customerID) { this.customerID = customerID; }
        public String getFirstName() { return firstName; }
        public void setFirstName(String firstName) { this.firstName = firstName; }
        public String getLastName() { return lastName; }
        public void setLastName(String lastName) { this.lastName = lastName; }
        public String getFullName() { return firstName + " " + lastName; }
        public int getNumberOfRentals() { return numberOfRentals; }
        public void setNumberOfRentals(int numberOfRentals) { this.numberOfRentals = numberOfRentals; }
        public double getTotalRentalPayments() { return totalRentalPayments; }
        public void setTotalRentalPayments(double totalRentalPayments) { this.totalRentalPayments = totalRentalPayments; }
        public double getTotalPenaltyPayments() { return totalPenaltyPayments; }
        public void setTotalPenaltyPayments(double totalPenaltyPayments) { this.totalPenaltyPayments = totalPenaltyPayments; }
        public int getNumberOfPenalties() { return numberOfPenalties; }
        public void setNumberOfPenalties(int numberOfPenalties) { this.numberOfPenalties = numberOfPenalties; }
        public double getPenaltyRiskScore() { return penaltyRiskScore; }
        public void setPenaltyRiskScore(double penaltyRiskScore) { this.penaltyRiskScore = penaltyRiskScore; }
        public String getRiskLevel() { return riskLevel; }
        public void setRiskLevel(String riskLevel) { this.riskLevel = riskLevel; }
    }

    /**
     * Inner class: Summary Statistics
     */
    public static class SummaryStatistics {
        private int totalCustomers;
        private int totalRentals;
        private double totalRentalRevenue;
        private double totalPenaltyPayments;
        private int customersWithPenalties;
        private int highRiskCustomers;

        // Getters and Setters
        public int getTotalCustomers() { return totalCustomers; }
        public void setTotalCustomers(int totalCustomers) { this.totalCustomers = totalCustomers; }
        public int getTotalRentals() { return totalRentals; }
        public void setTotalRentals(int totalRentals) { this.totalRentals = totalRentals; }
        public double getTotalRentalRevenue() { return totalRentalRevenue; }
        public void setTotalRentalRevenue(double totalRentalRevenue) { this.totalRentalRevenue = totalRentalRevenue; }
        public double getTotalPenaltyPayments() { return totalPenaltyPayments; }
        public void setTotalPenaltyPayments(double totalPenaltyPayments) { this.totalPenaltyPayments = totalPenaltyPayments; }
        public int getCustomersWithPenalties() { return customersWithPenalties; }
        public void setCustomersWithPenalties(int customersWithPenalties) { this.customersWithPenalties = customersWithPenalties; }
        public int getHighRiskCustomers() { return highRiskCustomers; }
        public void setHighRiskCustomers(int highRiskCustomers) { this.highRiskCustomers = highRiskCustomers; }
        public double getPercentageWithPenalties() {
            return totalCustomers > 0 ? ((double) customersWithPenalties / totalCustomers * 100) : 0;
        }
    }

    /**
     * Generate SECTION 1: Customer Rental Summary (filtered by month)
     */
    public List<CustomerRentalData> generateRentalSummary(int year, int month, String sortBy) {
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
                        "    COUNT(DISTINCT r.rentalID) AS number_of_rentals, " +
                        "    COALESCE(SUM(p.amount), 0) AS total_cost, " +
                        "    COALESCE(AVG(p.amount), 0) AS avg_cost, " +
                        "    COALESCE(SUM(TIMESTAMPDIFF(HOUR, r.startDateTime, r.endDateTime)), 0) AS total_duration, " +
                        "    COALESCE(AVG(TIMESTAMPDIFF(HOUR, r.startDateTime, r.endDateTime)), 0) AS avg_duration, " +
                        "    MAX(r.startDateTime) AS most_recent_rental " +
                        "FROM customers c " +
                        "LEFT JOIN rentals r ON c.customerID = r.customerID " +
                        "    AND r.status = 'Completed' " +
                        "    AND YEAR(r.startDateTime) = ? " +
                        "    AND MONTH(r.startDateTime) = ? " +
                        "LEFT JOIN payments p ON r.rentalID = p.rentalID " +
                        "    AND p.status = 'Active' " +
                        "WHERE c.status = 'Active' " +
                        "GROUP BY c.customerID, c.firstName, c.lastName " +
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
                data.setNumberOfRentals(rs.getInt("number_of_rentals"));
                data.setTotalRentalCost(rs.getDouble("total_cost"));
                data.setAverageRentalCost(rs.getDouble("avg_cost"));
                data.setTotalRentalDuration(rs.getDouble("total_duration"));
                data.setAverageRentalDuration(rs.getDouble("avg_duration"));
                data.setMostRecentRentalDate(rs.getTimestamp("most_recent_rental"));

                reportData.add(data);
            }

        } catch (SQLException e) {
            System.err.println("Error generating rental summary: " + e.getMessage());
            e.printStackTrace();
        }

        return reportData;
    }

    /**
     * Generate SECTION 1: Customer Rental Summary for entire year
     */
    public List<CustomerRentalData> generateYearlySummary(int year, String sortBy) {
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
                        "    COUNT(DISTINCT r.rentalID) AS number_of_rentals, " +
                        "    COALESCE(SUM(p.amount), 0) AS total_cost, " +
                        "    COALESCE(AVG(p.amount), 0) AS avg_cost, " +
                        "    COALESCE(SUM(TIMESTAMPDIFF(HOUR, r.startDateTime, r.endDateTime)), 0) AS total_duration, " +
                        "    COALESCE(AVG(TIMESTAMPDIFF(HOUR, r.startDateTime, r.endDateTime)), 0) AS avg_duration, " +
                        "    MAX(r.startDateTime) AS most_recent_rental " +
                        "FROM customers c " +
                        "LEFT JOIN rentals r ON c.customerID = r.customerID " +
                        "    AND r.status = 'Completed' " +
                        "    AND YEAR(r.startDateTime) = ? " +
                        "LEFT JOIN payments p ON r.rentalID = p.rentalID " +
                        "    AND p.status = 'Active' " +
                        "WHERE c.status = 'Active' " +
                        "GROUP BY c.customerID, c.firstName, c.lastName " +
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
                data.setNumberOfRentals(rs.getInt("number_of_rentals"));
                data.setTotalRentalCost(rs.getDouble("total_cost"));
                data.setAverageRentalCost(rs.getDouble("avg_cost"));
                data.setTotalRentalDuration(rs.getDouble("total_duration"));
                data.setAverageRentalDuration(rs.getDouble("avg_duration"));
                data.setMostRecentRentalDate(rs.getTimestamp("most_recent_rental"));

                reportData.add(data);
            }

        } catch (SQLException e) {
            System.err.println("Error generating yearly rental summary: " + e.getMessage());
            e.printStackTrace();
        }

        return reportData;
    }

    /**
     * Generate SECTION 2: Customer Demographics (NOT filtered by month)
     */
    public List<CustomerDemographicsData> generateDemographics() {
        List<CustomerDemographicsData> demographicsData = new ArrayList<>();

        String sql =
                "SELECT " +
                        "    ci.name AS city_name, " +
                        "    b.name AS barangay_name, " +
                        "    COUNT(c.customerID) AS customer_count, " +
                        "    GROUP_CONCAT(CONCAT(c.firstName, ' ', c.lastName) ORDER BY c.lastName SEPARATOR ', ') AS customer_names " +
                        "FROM cities ci " +
                        "JOIN barangays b ON ci.cityID = b.cityID " +
                        "JOIN addresses a ON b.barangayID = a.barangayID " +
                        "JOIN customers c ON a.addressID = c.addressID " +
                        "WHERE c.status = 'Active' " +
                        "GROUP BY ci.cityID, ci.name, b.barangayID, b.name " +
                        "ORDER BY customer_count DESC, ci.name, b.name";

        try (Connection conn = util.DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                CustomerDemographicsData data = new CustomerDemographicsData();
                data.setCityName(rs.getString("city_name"));
                data.setBarangayName(rs.getString("barangay_name"));
                data.setCustomerCount(rs.getInt("customer_count"));

                String names = rs.getString("customer_names");
                if (names != null && !names.isEmpty()) {
                    for (String name : names.split(", ")) {
                        data.addCustomerName(name);
                    }
                }

                demographicsData.add(data);
            }

        } catch (SQLException e) {
            System.err.println("Error generating demographics: " + e.getMessage());
            e.printStackTrace();
        }

        return demographicsData;
    }

    /**
     * Generate SECTION 3: Customer Penalty Risk Analysis (filtered by month)
     */
    public List<CustomerPenaltyRiskData> generatePenaltyRiskAnalysis(int year, int month) {
        List<CustomerPenaltyRiskData> riskData = new ArrayList<>();

        String sql =
                "SELECT " +
                        "    c.customerID, " +
                        "    c.firstName, " +
                        "    c.lastName, " +
                        "    COUNT(DISTINCT r.rentalID) AS number_of_rentals, " +
                        "    COALESCE(SUM(p.amount), 0) AS total_rental_payments, " +
                        "    COALESCE(SUM(pen.totalPenalty), 0) AS total_penalty_payments, " +
                        "    COUNT(DISTINCT pen.penaltyID) AS number_of_penalties " +
                        "FROM customers c " +
                        "LEFT JOIN rentals r ON c.customerID = r.customerID " +
                        "    AND r.status = 'Completed' " +
                        "    AND YEAR(r.startDateTime) = ? " +
                        "    AND MONTH(r.startDateTime) = ? " +
                        "LEFT JOIN payments p ON r.rentalID = p.rentalID " +
                        "    AND p.status = 'Active' " +
                        "LEFT JOIN penalty pen ON r.rentalID = pen.rentalID " +
                        "    AND pen.status = 'Active' " +
                        "    AND YEAR(pen.dateIssued) = ? " +
                        "    AND MONTH(pen.dateIssued) = ? " +
                        "WHERE c.status = 'Active' " +
                        "GROUP BY c.customerID, c.firstName, c.lastName " +
                        "HAVING number_of_rentals > 0 " +
                        "ORDER BY total_penalty_payments DESC, number_of_penalties DESC";

        try (Connection conn = util.DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, year);
            stmt.setInt(2, month);
            stmt.setInt(3, year);
            stmt.setInt(4, month);

            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                CustomerPenaltyRiskData data = new CustomerPenaltyRiskData(
                        rs.getString("customerID"),
                        rs.getString("firstName"),
                        rs.getString("lastName"),
                        rs.getInt("number_of_rentals"),
                        rs.getDouble("total_rental_payments"),
                        rs.getDouble("total_penalty_payments"),
                        rs.getInt("number_of_penalties")
                );

                riskData.add(data);
            }

        } catch (SQLException e) {
            System.err.println("Error generating penalty risk analysis: " + e.getMessage());
            e.printStackTrace();
        }

        return riskData;
    }

    /**
     * Generate SECTION 3: Customer Penalty Risk Analysis for entire year
     */
    public List<CustomerPenaltyRiskData> generateYearlyPenaltyRiskAnalysis(int year) {
        List<CustomerPenaltyRiskData> riskData = new ArrayList<>();

        String sql =
                "SELECT " +
                        "    c.customerID, " +
                        "    c.firstName, " +
                        "    c.lastName, " +
                        "    COUNT(DISTINCT r.rentalID) AS number_of_rentals, " +
                        "    COALESCE(SUM(p.amount), 0) AS total_rental_payments, " +
                        "    COALESCE(SUM(pen.totalPenalty), 0) AS total_penalty_payments, " +
                        "    COUNT(DISTINCT pen.penaltyID) AS number_of_penalties " +
                        "FROM customers c " +
                        "LEFT JOIN rentals r ON c.customerID = r.customerID " +
                        "    AND r.status = 'Completed' " +
                        "    AND YEAR(r.startDateTime) = ? " +
                        "LEFT JOIN payments p ON r.rentalID = p.rentalID " +
                        "    AND p.status = 'Active' " +
                        "LEFT JOIN penalty pen ON r.rentalID = pen.rentalID " +
                        "    AND pen.status = 'Active' " +
                        "    AND YEAR(pen.dateIssued) = ? " +
                        "WHERE c.status = 'Active' " +
                        "GROUP BY c.customerID, c.firstName, c.lastName " +
                        "HAVING number_of_rentals > 0 " +
                        "ORDER BY total_penalty_payments DESC, number_of_penalties DESC";

        try (Connection conn = util.DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, year);
            stmt.setInt(2, year);

            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                CustomerPenaltyRiskData data = new CustomerPenaltyRiskData(
                        rs.getString("customerID"),
                        rs.getString("firstName"),
                        rs.getString("lastName"),
                        rs.getInt("number_of_rentals"),
                        rs.getDouble("total_rental_payments"),
                        rs.getDouble("total_penalty_payments"),
                        rs.getInt("number_of_penalties")
                );

                riskData.add(data);
            }

        } catch (SQLException e) {
            System.err.println("Error generating yearly penalty risk analysis: " + e.getMessage());
            e.printStackTrace();
        }

        return riskData;
    }

    /**
     * Generate SECTION 4: Summary Statistics (filtered by month)
     */
    public SummaryStatistics generateSummaryStatistics(List<CustomerRentalData> rentalData,
                                                       List<CustomerPenaltyRiskData> riskData) {
        SummaryStatistics stats = new SummaryStatistics();

        stats.setTotalCustomers(rentalData.size());

        int totalRentals = 0;
        double totalRevenue = 0;

        for (CustomerRentalData data : rentalData) {
            totalRentals += data.getNumberOfRentals();
            totalRevenue += data.getTotalRentalCost();
        }

        stats.setTotalRentals(totalRentals);
        stats.setTotalRentalRevenue(totalRevenue);

        int customersWithPenalties = 0;
        int highRiskCustomers = 0;
        double totalPenalties = 0;

        for (CustomerPenaltyRiskData data : riskData) {
            if (data.getNumberOfPenalties() > 0) {
                customersWithPenalties++;
            }
            if ("High Risk".equals(data.getRiskLevel())) {
                highRiskCustomers++;
            }
            totalPenalties += data.getTotalPenaltyPayments();
        }

        stats.setCustomersWithPenalties(customersWithPenalties);
        stats.setHighRiskCustomers(highRiskCustomers);
        stats.setTotalPenaltyPayments(totalPenalties);

        return stats;
    }

    /**
     * Print comprehensive report to console
     */
    public void printReport(List<CustomerRentalData> rentalData,
                            List<CustomerDemographicsData> demographicsData,
                            List<CustomerPenaltyRiskData> riskData,
                            SummaryStatistics summary,
                            int year, int month, String sortBy) {

        String[] months = {"", "January", "February", "March", "April", "May", "June",
                "July", "August", "September", "October", "November", "December"};

        System.out.println("\n" + repeatChar("=", 150));
        System.out.printf("CUSTOMER RENTAL + DEMOGRAPHICS + PENALTY RISK REPORT - %s %d",
                months[month], year);
        System.out.println();
        System.out.println(repeatChar("=", 150));

        // SECTION 1: Rental Summary
        System.out.println("\n" + repeatChar("-", 150));
        System.out.println("SECTION 1: CUSTOMER RENTAL SUMMARY (Month Filtered)");
        System.out.println(repeatChar("-", 150));

        if (rentalData.isEmpty()) {
            System.out.println("No rental data found for the specified period.");
        } else {
            System.out.printf("%-12s %-25s %-8s %-15s %-12s %-12s %-12s %-15s\n",
                    "Customer ID", "Name", "Rentals", "Total Cost", "Avg Cost",
                    "Total Hrs", "Avg Hrs", "Last Rental");
            System.out.println(repeatChar("-", 150));

            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
            for (CustomerRentalData data : rentalData) {
                String lastRental = (data.getMostRecentRentalDate() != null)
                        ? dateFormat.format(data.getMostRecentRentalDate()) : "N/A";

                System.out.printf("%-12s %-25s %-8d %,15.2f %,12.2f %,12.1f %,12.1f %-15s\n",
                        data.getCustomerID(),
                        data.getFullName(),
                        data.getNumberOfRentals(),
                        data.getTotalRentalCost(),
                        data.getAverageRentalCost(),
                        data.getTotalRentalDuration(),
                        data.getAverageRentalDuration(),
                        lastRental);
            }
        }

        // SECTION 2: Demographics
        System.out.println("\n" + repeatChar("-", 150));
        System.out.println("SECTION 2: CUSTOMER DEMOGRAPHICS (All Customers - Not Month Filtered)");
        System.out.println(repeatChar("-", 150));

        if (demographicsData.isEmpty()) {
            System.out.println("No demographics data available.");
        } else {
            System.out.printf("%-25s %-30s %-15s\n", "City", "Barangay", "# Customers");
            System.out.println(repeatChar("-", 150));

            String currentCity = "";
            int cityTotal = 0;
            int grandTotal = 0;

            for (CustomerDemographicsData data : demographicsData) {
                if (!data.getCityName().equals(currentCity)) {
                    if (!currentCity.isEmpty()) {
                        System.out.printf("%-25s %-30s %-15d\n",
                                currentCity + " SUBTOTAL:", "", cityTotal);
                        System.out.println(repeatChar("-", 70));
                    }
                    currentCity = data.getCityName();
                    cityTotal = 0;
                }

                System.out.printf("%-25s %-30s %-15d\n",
                        data.getCityName(),
                        data.getBarangayName(),
                        data.getCustomerCount());

                cityTotal += data.getCustomerCount();
                grandTotal += data.getCustomerCount();
            }

            if (!currentCity.isEmpty()) {
                System.out.printf("%-25s %-30s %-15d\n",
                        currentCity + " SUBTOTAL:", "", cityTotal);
            }

            System.out.println(repeatChar("=", 70));
            System.out.printf("%-25s %-30s %-15d\n", "GRAND TOTAL:", "", grandTotal);
        }

        // SECTION 3: Penalty Risk Analysis
        System.out.println("\n" + repeatChar("-", 150));
        System.out.println("SECTION 3: CUSTOMER PENALTY / RISK ANALYSIS (Month Filtered)");
        System.out.println(repeatChar("-", 150));

        if (riskData.isEmpty()) {
            System.out.println("No penalty risk data found for the specified period.");
        } else {
            System.out.printf("%-12s %-25s %-8s %-15s %-15s %-10s %-15s %-15s\n",
                    "Customer ID", "Name", "Rentals", "Rental Pay (₱)",
                    "Penalty Pay (₱)", "Penalties", "Risk Score %", "Risk Level");
            System.out.println(repeatChar("-", 150));

            for (CustomerPenaltyRiskData data : riskData) {
                String riskIcon = "";
                if ("Low Risk".equals(data.getRiskLevel())) {
                    riskIcon = "🟢 ";
                } else if ("Medium Risk".equals(data.getRiskLevel())) {
                    riskIcon = "🟠 ";
                } else if ("High Risk".equals(data.getRiskLevel())) {
                    riskIcon = "🔴 ";
                }

                System.out.printf("%-12s %-25s %-8d %,15.2f %,15.2f %-10d %,15.2f %s%-15s\n",
                        data.getCustomerID(),
                        data.getFullName(),
                        data.getNumberOfRentals(),
                        data.getTotalRentalPayments(),
                        data.getTotalPenaltyPayments(),
                        data.getNumberOfPenalties(),
                        data.getPenaltyRiskScore(),
                        riskIcon,
                        data.getRiskLevel());
            }
        }

        // SECTION 4: Summary Statistics
        System.out.println("\n" + repeatChar("-", 150));
        System.out.println("SECTION 4: SUMMARY / KEY METRICS (Month Filtered)");
        System.out.println(repeatChar("-", 150));

        System.out.printf("Total Customers: %d\n", summary.getTotalCustomers());
        System.out.printf("Total Rentals: %d\n", summary.getTotalRentals());
        System.out.printf("Total Rental Revenue: ₱%,.2f\n", summary.getTotalRentalRevenue());
        System.out.printf("Total Penalty Payments: ₱%,.2f\n", summary.getTotalPenaltyPayments());
        System.out.printf("Customers with ≥1 Penalty: %d (%.1f%%)\n",
                summary.getCustomersWithPenalties(),
                summary.getPercentageWithPenalties());
        System.out.printf("High-Risk Customers: %d\n", summary.getHighRiskCustomers());

        System.out.println("\n" + repeatChar("=", 150) + "\n");
    }

    /**
     * Print comprehensive yearly report to console
     */
    public void printYearlyReport(List<CustomerRentalData> rentalData,
                                  List<CustomerDemographicsData> demographicsData,
                                  List<CustomerPenaltyRiskData> riskData,
                                  SummaryStatistics summary,
                                  int year, String sortBy) {

        System.out.println("\n" + repeatChar("=", 150));
        System.out.printf("CUSTOMER RENTAL + DEMOGRAPHICS + PENALTY RISK REPORT - Year %d", year);
        System.out.println();
        System.out.println(repeatChar("=", 150));

        // SECTION 1: Rental Summary
        System.out.println("\n" + repeatChar("-", 150));
        System.out.println("SECTION 1: CUSTOMER RENTAL SUMMARY (Year Filtered)");
        System.out.println(repeatChar("-", 150));

        if (rentalData.isEmpty()) {
            System.out.println("No rental data found for the specified period.");
        } else {
            System.out.printf("%-12s %-25s %-8s %-15s %-12s %-12s %-12s %-15s\n",
                    "Customer ID", "Name", "Rentals", "Total Cost", "Avg Cost",
                    "Total Hrs", "Avg Hrs", "Last Rental");
            System.out.println(repeatChar("-", 150));

            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
            for (CustomerRentalData data : rentalData) {
                String lastRental = (data.getMostRecentRentalDate() != null)
                        ? dateFormat.format(data.getMostRecentRentalDate()) : "N/A";

                System.out.printf("%-12s %-25s %-8d %,15.2f %,12.2f %,12.1f %,12.1f %-15s\n",
                        data.getCustomerID(),
                        data.getFullName(),
                        data.getNumberOfRentals(),
                        data.getTotalRentalCost(),
                        data.getAverageRentalCost(),
                        data.getTotalRentalDuration(),
                        data.getAverageRentalDuration(),
                        lastRental);
            }
        }

        // SECTION 2: Demographics
        System.out.println("\n" + repeatChar("-", 150));
        System.out.println("SECTION 2: CUSTOMER DEMOGRAPHICS (All Customers - Not Year Filtered)");
        System.out.println(repeatChar("-", 150));

        if (demographicsData.isEmpty()) {
            System.out.println("No demographics data available.");
        } else {
            System.out.printf("%-25s %-30s %-15s\n", "City", "Barangay", "# Customers");
            System.out.println(repeatChar("-", 150));

            String currentCity = "";
            int cityTotal = 0;
            int grandTotal = 0;

            for (CustomerDemographicsData data : demographicsData) {
                if (!data.getCityName().equals(currentCity)) {
                    if (!currentCity.isEmpty()) {
                        System.out.printf("%-25s %-30s %-15d\n",
                                currentCity + " SUBTOTAL:", "", cityTotal);
                        System.out.println(repeatChar("-", 70));
                    }
                    currentCity = data.getCityName();
                    cityTotal = 0;
                }

                System.out.printf("%-25s %-30s %-15d\n",
                        data.getCityName(),
                        data.getBarangayName(),
                        data.getCustomerCount());

                cityTotal += data.getCustomerCount();
                grandTotal += data.getCustomerCount();
            }

            if (!currentCity.isEmpty()) {
                System.out.printf("%-25s %-30s %-15d\n",
                        currentCity + " SUBTOTAL:", "", cityTotal);
            }

            System.out.println(repeatChar("=", 70));
            System.out.printf("%-25s %-30s %-15d\n", "GRAND TOTAL:", "", grandTotal);
        }

        // SECTION 3: Penalty Risk Analysis
        System.out.println("\n" + repeatChar("-", 150));
        System.out.println("SECTION 3: CUSTOMER PENALTY / RISK ANALYSIS (Year Filtered)");
        System.out.println(repeatChar("-", 150));

        if (riskData.isEmpty()) {
            System.out.println("No penalty risk data found for the specified period.");
        } else {
            System.out.printf("%-12s %-25s %-8s %-15s %-15s %-10s %-15s %-15s\n",
                    "Customer ID", "Name", "Rentals", "Rental Pay (₱)",
                    "Penalty Pay (₱)", "Penalties", "Risk Score %", "Risk Level");
            System.out.println(repeatChar("-", 150));

            for (CustomerPenaltyRiskData data : riskData) {
                String riskIcon = "";
                if ("Low Risk".equals(data.getRiskLevel())) {
                    riskIcon = "🟢 ";
                } else if ("Medium Risk".equals(data.getRiskLevel())) {
                    riskIcon = "🟠 ";
                } else if ("High Risk".equals(data.getRiskLevel())) {
                    riskIcon = "🔴 ";
                }

                System.out.printf("%-12s %-25s %-8d %,15.2f %,15.2f %-10d %,15.2f %s%-15s\n",
                        data.getCustomerID(),
                        data.getFullName(),
                        data.getNumberOfRentals(),
                        data.getTotalRentalPayments(),
                        data.getTotalPenaltyPayments(),
                        data.getNumberOfPenalties(),
                        data.getPenaltyRiskScore(),
                        riskIcon,
                        data.getRiskLevel());
            }
        }

        // SECTION 4: Summary Statistics
        System.out.println("\n" + repeatChar("-", 150));
        System.out.println("SECTION 4: SUMMARY / KEY METRICS (Year Filtered)");
        System.out.println(repeatChar("-", 150));

        System.out.printf("Total Customers: %d\n", summary.getTotalCustomers());
        System.out.printf("Total Rentals: %d\n", summary.getTotalRentals());
        System.out.printf("Total Rental Revenue: ₱%,.2f\n", summary.getTotalRentalRevenue());
        System.out.printf("Total Penalty Payments: ₱%,.2f\n", summary.getTotalPenaltyPayments());
        System.out.printf("Customers with ≥1 Penalty: %d (%.1f%%)\n",
                summary.getCustomersWithPenalties(),
                summary.getPercentageWithPenalties());
        System.out.printf("High-Risk Customers: %d\n", summary.getHighRiskCustomers());

        System.out.println("\n" + repeatChar("=", 150) + "\n");
    }

    /**
     * Export comprehensive report to PDF
     */
    public void exportToPDF(List<CustomerRentalData> rentalData,
                            List<CustomerDemographicsData> demographicsData,
                            List<CustomerPenaltyRiskData> riskData,
                            SummaryStatistics summary,
                            String filename, int year, int month, String sortBy) {

        Document document = new Document(PageSize.A4.rotate());

        try {
            String fullPath = prepareOutputPath(filename);
            PdfWriter.getInstance(document, new FileOutputStream(fullPath));
            document.open();

            String[] months = {"", "January", "February", "March", "April", "May", "June",
                    "July", "August", "September", "October", "November", "December"};

            String title = "Customer Rental + Demographics + Penalty Risk Report";
            String subtitle = months[month] + " " + year;

            PDFBrandingHelper.addHeaderSection(document, title, subtitle);

            // SECTION 1: Rental Summary
            addSectionTitle(document, "Section 1: Customer Rental Summary (Month Filtered)");

            if (!rentalData.isEmpty()) {
                PdfPTable table1 = new PdfPTable(8);
                table1.setWidthPercentage(100);
                table1.setWidths(new float[]{1.2f, 2.5f, 0.8f, 1.5f, 1.2f, 1.2f, 1.2f, 1.5f});

                String[] headers1 = {"Customer ID", "Name", "Rentals", "Total Cost (₱)",
                        "Avg Cost", "Total Hrs", "Avg Hrs", "Last Rental"};
                for (String header : headers1) {
                    table1.addCell(PDFBrandingHelper.createHeaderCell(header));
                }

                SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy");
                for (int i = 0; i < rentalData.size(); i++) {
                    CustomerRentalData data = rentalData.get(i);

                    table1.addCell(PDFBrandingHelper.createDataCell(data.getCustomerID(), i));
                    table1.addCell(PDFBrandingHelper.createDataCell(data.getFullName(), i));
                    table1.addCell(PDFBrandingHelper.createDataCell(String.valueOf(data.getNumberOfRentals()), i, Element.ALIGN_CENTER));
                    table1.addCell(PDFBrandingHelper.createDataCell(String.format("₱%,.2f", data.getTotalRentalCost()), i, Element.ALIGN_RIGHT));
                    table1.addCell(PDFBrandingHelper.createDataCell(String.format("₱%,.2f", data.getAverageRentalCost()), i, Element.ALIGN_RIGHT));
                    table1.addCell(PDFBrandingHelper.createDataCell(String.format("%.1f", data.getTotalRentalDuration()), i, Element.ALIGN_CENTER));
                    table1.addCell(PDFBrandingHelper.createDataCell(String.format("%.1f", data.getAverageRentalDuration()), i, Element.ALIGN_CENTER));

                    String lastRental = (data.getMostRecentRentalDate() != null)
                            ? dateFormat.format(data.getMostRecentRentalDate()) : "N/A";
                    table1.addCell(PDFBrandingHelper.createDataCell(lastRental, i, Element.ALIGN_CENTER));
                }

                document.add(table1);
            }

            // SECTION 2: Demographics
            document.newPage();
            addSectionTitle(document, "Section 2: Customer Demographics (All Customers)");

            if (!demographicsData.isEmpty()) {
                PdfPTable table2 = new PdfPTable(3);
                table2.setWidthPercentage(80);
                table2.setWidths(new float[]{2.5f, 3f, 1.5f});

                String[] headers2 = {"City", "Barangay", "# Customers"};
                for (String header : headers2) {
                    table2.addCell(PDFBrandingHelper.createHeaderCell(header));
                }

                int i = 0;
                for (CustomerDemographicsData data : demographicsData) {
                    table2.addCell(PDFBrandingHelper.createDataCell(data.getCityName(), i));
                    table2.addCell(PDFBrandingHelper.createDataCell(data.getBarangayName(), i));
                    table2.addCell(PDFBrandingHelper.createDataCell(String.valueOf(data.getCustomerCount()), i, Element.ALIGN_CENTER));
                    i++;
                }

                document.add(table2);
            }

            // SECTION 3: Penalty Risk
            document.newPage();
            addSectionTitle(document, "Section 3: Customer Penalty / Risk Analysis (Month Filtered)");

            if (!riskData.isEmpty()) {
                PdfPTable table3 = new PdfPTable(8);
                table3.setWidthPercentage(100);
                table3.setWidths(new float[]{1.2f, 2.5f, 0.8f, 1.5f, 1.5f, 1f, 1.5f, 1.5f});

                String[] headers3 = {"Customer ID", "Name", "Rentals", "Rental Pay (₱)",
                        "Penalty Pay (₱)", "#Pen", "Risk Score %", "Risk Level"};
                for (String header : headers3) {
                    table3.addCell(PDFBrandingHelper.createHeaderCell(header));
                }

                for (int i = 0; i < riskData.size(); i++) {
                    CustomerPenaltyRiskData data = riskData.get(i);

                    table3.addCell(PDFBrandingHelper.createDataCell(data.getCustomerID(), i));
                    table3.addCell(PDFBrandingHelper.createDataCell(data.getFullName(), i));
                    table3.addCell(PDFBrandingHelper.createDataCell(String.valueOf(data.getNumberOfRentals()), i, Element.ALIGN_CENTER));
                    table3.addCell(PDFBrandingHelper.createDataCell(String.format("₱%,.2f", data.getTotalRentalPayments()), i, Element.ALIGN_RIGHT));
                    table3.addCell(PDFBrandingHelper.createDataCell(String.format("₱%,.2f", data.getTotalPenaltyPayments()), i, Element.ALIGN_RIGHT));
                    table3.addCell(PDFBrandingHelper.createDataCell(String.valueOf(data.getNumberOfPenalties()), i, Element.ALIGN_CENTER));
                    table3.addCell(PDFBrandingHelper.createDataCell(String.format("%.2f", data.getPenaltyRiskScore()), i, Element.ALIGN_RIGHT));

                    // Color-coded risk level cell
                    PdfPCell riskCell = new PdfPCell(new Phrase(data.getRiskLevel(),
                            new Font(Font.FontFamily.HELVETICA, 8, Font.BOLD)));
                    riskCell.setHorizontalAlignment(Element.ALIGN_CENTER);
                    riskCell.setVerticalAlignment(Element.ALIGN_MIDDLE);
                    riskCell.setPadding(5);

                    if ("Low Risk".equals(data.getRiskLevel())) {
                        riskCell.setBackgroundColor(new BaseColor(144, 238, 144)); // Light green
                    } else if ("Medium Risk".equals(data.getRiskLevel())) {
                        riskCell.setBackgroundColor(new BaseColor(255, 200, 124)); // Light orange
                    } else if ("High Risk".equals(data.getRiskLevel())) {
                        riskCell.setBackgroundColor(new BaseColor(255, 160, 160)); // Light red
                    }

                    table3.addCell(riskCell);
                }

                document.add(table3);
            }

            // SECTION 4: Summary
            addSectionTitle(document, "Section 4: Summary / Key Metrics (Month Filtered)");

            PdfPTable summaryTable = new PdfPTable(2);
            summaryTable.setWidthPercentage(60);
            summaryTable.setWidths(new float[]{2f, 1f});

            PDFBrandingHelper.addSummaryRow(summaryTable, "Total Customers:", String.valueOf(summary.getTotalCustomers()));
            PDFBrandingHelper.addSummaryRow(summaryTable, "Total Rentals:", String.valueOf(summary.getTotalRentals()));
            PDFBrandingHelper.addSummaryRow(summaryTable, "Total Rental Revenue:", String.format("₱%,.2f", summary.getTotalRentalRevenue()));
            PDFBrandingHelper.addSummaryRow(summaryTable, "Total Penalty Payments:", String.format("₱%,.2f", summary.getTotalPenaltyPayments()));
            PDFBrandingHelper.addSummaryRow(summaryTable, "Customers with ≥1 Penalty:",
                    String.format("%d (%.1f%%)", summary.getCustomersWithPenalties(), summary.getPercentageWithPenalties()));
            PDFBrandingHelper.addSummaryRow(summaryTable, "High-Risk Customers:", String.valueOf(summary.getHighRiskCustomers()));

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

    /**
     * Export comprehensive yearly report to PDF
     */
    public void exportYearlyToPDF(List<CustomerRentalData> rentalData,
                                  List<CustomerDemographicsData> demographicsData,
                                  List<CustomerPenaltyRiskData> riskData,
                                  SummaryStatistics summary,
                                  String filename, int year, String sortBy) {

        Document document = new Document(PageSize.A4.rotate());

        try {
            String fullPath = prepareOutputPath(filename);
            PdfWriter.getInstance(document, new FileOutputStream(fullPath));
            document.open();

            String title = "Customer Rental + Demographics + Penalty Risk Report";
            String subtitle = "Year " + year;

            PDFBrandingHelper.addHeaderSection(document, title, subtitle);

            // SECTION 1: Rental Summary
            addSectionTitle(document, "Section 1: Customer Rental Summary (Year Filtered)");

            if (!rentalData.isEmpty()) {
                PdfPTable table1 = new PdfPTable(8);
                table1.setWidthPercentage(100);
                table1.setWidths(new float[]{1.2f, 2.5f, 0.8f, 1.5f, 1.2f, 1.2f, 1.2f, 1.5f});

                String[] headers1 = {"Customer ID", "Name", "Rentals", "Total Cost (₱)",
                        "Avg Cost", "Total Hrs", "Avg Hrs", "Last Rental"};
                for (String header : headers1) {
                    table1.addCell(PDFBrandingHelper.createHeaderCell(header));
                }

                SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy");
                for (int i = 0; i < rentalData.size(); i++) {
                    CustomerRentalData data = rentalData.get(i);

                    table1.addCell(PDFBrandingHelper.createDataCell(data.getCustomerID(), i));
                    table1.addCell(PDFBrandingHelper.createDataCell(data.getFullName(), i));
                    table1.addCell(PDFBrandingHelper.createDataCell(String.valueOf(data.getNumberOfRentals()), i, Element.ALIGN_CENTER));
                    table1.addCell(PDFBrandingHelper.createDataCell(String.format("₱%,.2f", data.getTotalRentalCost()), i, Element.ALIGN_RIGHT));
                    table1.addCell(PDFBrandingHelper.createDataCell(String.format("₱%,.2f", data.getAverageRentalCost()), i, Element.ALIGN_RIGHT));
                    table1.addCell(PDFBrandingHelper.createDataCell(String.format("%.1f", data.getTotalRentalDuration()), i, Element.ALIGN_CENTER));
                    table1.addCell(PDFBrandingHelper.createDataCell(String.format("%.1f", data.getAverageRentalDuration()), i, Element.ALIGN_CENTER));

                    String lastRental = (data.getMostRecentRentalDate() != null)
                            ? dateFormat.format(data.getMostRecentRentalDate()) : "N/A";
                    table1.addCell(PDFBrandingHelper.createDataCell(lastRental, i, Element.ALIGN_CENTER));
                }

                document.add(table1);
            }

            // SECTION 2: Demographics
            document.newPage();
            addSectionTitle(document, "Section 2: Customer Demographics (All Customers)");

            if (!demographicsData.isEmpty()) {
                PdfPTable table2 = new PdfPTable(3);
                table2.setWidthPercentage(80);
                table2.setWidths(new float[]{2.5f, 3f, 1.5f});

                String[] headers2 = {"City", "Barangay", "# Customers"};
                for (String header : headers2) {
                    table2.addCell(PDFBrandingHelper.createHeaderCell(header));
                }

                int i = 0;
                for (CustomerDemographicsData data : demographicsData) {
                    table2.addCell(PDFBrandingHelper.createDataCell(data.getCityName(), i));
                    table2.addCell(PDFBrandingHelper.createDataCell(data.getBarangayName(), i));
                    table2.addCell(PDFBrandingHelper.createDataCell(String.valueOf(data.getCustomerCount()), i, Element.ALIGN_CENTER));
                    i++;
                }

                document.add(table2);
            }

            // SECTION 3: Penalty Risk
            document.newPage();
            addSectionTitle(document, "Section 3: Customer Penalty / Risk Analysis (Year Filtered)");

            if (!riskData.isEmpty()) {
                PdfPTable table3 = new PdfPTable(8);
                table3.setWidthPercentage(100);
                table3.setWidths(new float[]{1.2f, 2.5f, 0.8f, 1.5f, 1.5f, 1f, 1.5f, 1.5f});

                String[] headers3 = {"Customer ID", "Name", "Rentals", "Rental Pay (₱)",
                        "Penalty Pay (₱)", "#Pen", "Risk Score %", "Risk Level"};
                for (String header : headers3) {
                    table3.addCell(PDFBrandingHelper.createHeaderCell(header));
                }

                for (int i = 0; i < riskData.size(); i++) {
                    CustomerPenaltyRiskData data = riskData.get(i);

                    table3.addCell(PDFBrandingHelper.createDataCell(data.getCustomerID(), i));
                    table3.addCell(PDFBrandingHelper.createDataCell(data.getFullName(), i));
                    table3.addCell(PDFBrandingHelper.createDataCell(String.valueOf(data.getNumberOfRentals()), i, Element.ALIGN_CENTER));
                    table3.addCell(PDFBrandingHelper.createDataCell(String.format("₱%,.2f", data.getTotalRentalPayments()), i, Element.ALIGN_RIGHT));
                    table3.addCell(PDFBrandingHelper.createDataCell(String.format("₱%,.2f", data.getTotalPenaltyPayments()), i, Element.ALIGN_RIGHT));
                    table3.addCell(PDFBrandingHelper.createDataCell(String.valueOf(data.getNumberOfPenalties()), i, Element.ALIGN_CENTER));
                    table3.addCell(PDFBrandingHelper.createDataCell(String.format("%.2f", data.getPenaltyRiskScore()), i, Element.ALIGN_RIGHT));

                    // Color-coded risk level cell
                    PdfPCell riskCell = new PdfPCell(new Phrase(data.getRiskLevel(),
                            new Font(Font.FontFamily.HELVETICA, 8, Font.BOLD)));
                    riskCell.setHorizontalAlignment(Element.ALIGN_CENTER);
                    riskCell.setVerticalAlignment(Element.ALIGN_MIDDLE);
                    riskCell.setPadding(5);

                    if ("Low Risk".equals(data.getRiskLevel())) {
                        riskCell.setBackgroundColor(new BaseColor(144, 238, 144)); // Light green
                    } else if ("Medium Risk".equals(data.getRiskLevel())) {
                        riskCell.setBackgroundColor(new BaseColor(255, 200, 124)); // Light orange
                    } else if ("High Risk".equals(data.getRiskLevel())) {
                        riskCell.setBackgroundColor(new BaseColor(255, 160, 160)); // Light red
                    }

                    table3.addCell(riskCell);
                }

                document.add(table3);
            }

            // SECTION 4: Summary
            addSectionTitle(document, "Section 4: Summary / Key Metrics (Year Filtered)");

            PdfPTable summaryTable = new PdfPTable(2);
            summaryTable.setWidthPercentage(60);
            summaryTable.setWidths(new float[]{2f, 1f});

            PDFBrandingHelper.addSummaryRow(summaryTable, "Total Customers:", String.valueOf(summary.getTotalCustomers()));
            PDFBrandingHelper.addSummaryRow(summaryTable, "Total Rentals:", String.valueOf(summary.getTotalRentals()));
            PDFBrandingHelper.addSummaryRow(summaryTable, "Total Rental Revenue:", String.format("₱%,.2f", summary.getTotalRentalRevenue()));
            PDFBrandingHelper.addSummaryRow(summaryTable, "Total Penalty Payments:", String.format("₱%,.2f", summary.getTotalPenaltyPayments()));
            PDFBrandingHelper.addSummaryRow(summaryTable, "Customers with ≥1 Penalty:",
                    String.format("%d (%.1f%%)", summary.getCustomersWithPenalties(), summary.getPercentageWithPenalties()));
            PDFBrandingHelper.addSummaryRow(summaryTable, "High-Risk Customers:", String.valueOf(summary.getHighRiskCustomers()));

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

    /**
     * Helper: Add section title to PDF
     */
    private void addSectionTitle(Document document, String title) throws DocumentException {
        Paragraph sectionTitle = new Paragraph(title,
                new Font(Font.FontFamily.HELVETICA, 12, Font.BOLD, PDFBrandingHelper.BRAND_GREEN));
        sectionTitle.setSpacingBefore(20);
        sectionTitle.setSpacingAfter(10);
        document.add(sectionTitle);
    }

    /**
     * Main method for testing
     */
    public static void main(String[] args) {
        CustomerRentalReport report = new CustomerRentalReport();

        System.out.println("=== CUSTOMER RENTAL + DEMOGRAPHICS + PENALTY RISK REPORT TEST ===\n");

        // Test comprehensive report for October 2024
        System.out.println("Testing Comprehensive Report for October 2024...");

        List<CustomerRentalData> rentalData = report.generateRentalSummary(2024, 10, "Revenue");
        List<CustomerDemographicsData> demographicsData = report.generateDemographics();
        List<CustomerPenaltyRiskData> riskData = report.generatePenaltyRiskAnalysis(2024, 10);
        SummaryStatistics summary = report.generateSummaryStatistics(rentalData, riskData);

        report.printReport(rentalData, demographicsData, riskData, summary, 2024, 10, "Revenue");
        report.exportToPDF(rentalData, demographicsData, riskData, summary,
                "Customer_Comprehensive_Report_Oct2024.pdf", 2024, 10, "Revenue");

        // Test yearly report for 2024
        System.out.println("\nTesting Yearly Report for 2024...");

        List<CustomerRentalData> yearlyRentalData = report.generateYearlySummary(2024, "Revenue");
        List<CustomerDemographicsData> yearlyDemographicsData = report.generateDemographics();
        List<CustomerPenaltyRiskData> yearlyRiskData = report.generateYearlyPenaltyRiskAnalysis(2024);
        SummaryStatistics yearlySummary = report.generateSummaryStatistics(yearlyRentalData, yearlyRiskData);

        report.printYearlyReport(yearlyRentalData, yearlyDemographicsData, yearlyRiskData, yearlySummary, 2024, "Revenue");
        report.exportYearlyToPDF(yearlyRentalData, yearlyDemographicsData, yearlyRiskData, yearlySummary,
                "Customer_Comprehensive_Report_2024.pdf", 2024, "Revenue");

        System.out.println("=== TEST COMPLETE ===");
    }
}