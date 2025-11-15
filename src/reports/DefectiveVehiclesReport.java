package reports;

import dao.*;
import model.*;
import java.sql.*;
import java.util.*;

/**
 * DEFECTIVE VEHICLES REPORT
 * 
 * PURPOSE:
 * Identify vehicles marked as defective/unavailable and track their rental 
 * history before being marked defective. Helps management assess vehicle 
 * reliability and maintenance patterns.
 * 
 * DATA SOURCES:
 * - Vehicle Records (plateID, vehicleType, status, model)
 * - Rental Records (plateID, startDateTime, endDateTime)
 * - Maintenance Transaction Records (plateID, startDateTime, endDateTime, notes)
 * 
 * REPORT OUTPUT:
 * - Plate ID
 * - Vehicle Type
 * - Model
 * - Current Status (Defective/Under Maintenance)
 * - Date Marked Defective (first maintenance record or status change)
 * - Number of Rentals Before Defect (count of rentals before that date)
 * - Last Rental Date (most recent rental before being marked defective)
 * - Total Revenue Generated (sum of payments before defect)
 * 
 * USER INPUTS:
 * 1. Report Period Type (Day/Month/Year)
 * 2. Year (required)
 * 3. Month (required if period is Day or Month)
 * 4. Day (required if period is Day)
 * 5. Status Filter ("Defective", "Under Maintenance", or "Both")
 * 
 * EXPECTED METHODS:
 * - generateDailyReport(int year, int month, int day, String statusFilter)
 * - generateMonthlyReport(int year, int month, String statusFilter)
 * - generateYearlyReport(int year, String statusFilter)
 * - printReport(List<DefectiveVehicleData> data) - Format and display results
 * 
 * SQL LOGIC:
 * - SELECT vehicles WHERE status IN ('Defective', 'Under Maintenance')
 * - For each vehicle, COUNT rentals WHERE endDateTime < defect_date
 * - JOIN with maintenance records to find when vehicle became defective
 * - Filter by year/month based on when vehicle was marked defective
 * - Sort by number of rentals (descending) to show most-used vehicles first
 * 
 * BUSINESS INSIGHTS:
 * - High rental count before defect = heavy usage wear and tear
 * - Low rental count before defect = potential manufacturing defect
 * - Helps identify which vehicle models are most reliable
 * - Informs purchasing decisions for future fleet expansion
 * 
 * EXAMPLE OUTPUT:
 * ================================================================
 * DEFECTIVE VEHICLES REPORT - October 2024
 * ================================================================
 * Plate ID | Type      | Model      | Status      | Marked Date | Rentals | Last Rental
 * ---------------------------------------------------------------------------------
 * ES-004   | E-Scooter | Ninebot S  | Defective   | 2024-10-15  | 87      | 2024-10-14
 * EB-003   | E-Bike    | Xiaomi Pro | Maintenance | 2024-10-20  | 45      | 2024-10-19
 * ---------------------------------------------------------------------------------
 * Total Defective Vehicles: 2
 * Average Rentals Before Defect: 66
 * ================================================================
 */
public class DefectiveVehiclesReport {
    
    private VehicleDAO vehicleDAO;
    private RentalDAO rentalDAO;
    private MaintenanceDAO maintenanceDAO;
    
    /**
     * Constructor - Initialize required DAOs
     */
    public DefectiveVehiclesReport() {
        this.vehicleDAO = new VehicleDAO();
        this.rentalDAO = new RentalDAO();
        this.maintenanceDAO = new MaintenanceDAO();
    }
    
    /**
     * Inner class to hold defective vehicle report data
     */
    public static class DefectiveVehicleData {
        private String plateID;
        private String vehicleType;
        private String vehicleModel;
        private int timesMaintained;
        private double totalMaintenanceCost;
        private double totalDaysInMaintenance;
        private Timestamp lastMaintenanceDate;
        private int rentalsInPeriod;
        private int totalRentalsLifetime;
        private double totalRevenue;
        private double costToRevenueRatio;
        private double avgMaintenanceCost;
        
        // Default constructor
        public DefectiveVehicleData() {}
        
        // Full constructor
        public DefectiveVehicleData(String plateID, String vehicleType, String vehicleModel,
                                   int timesMaintained, double totalMaintenanceCost,
                                   int totalDaysInMaintenance, Timestamp lastMaintenanceDate,
                                   int rentalsInPeriod, int totalRentalsLifetime, double totalRevenue,
                                   double costToRevenueRatio, double avgMaintenanceCost) {
            this.plateID = plateID;
            this.vehicleType = vehicleType;
            this.vehicleModel = vehicleModel;
            this.timesMaintained = timesMaintained;
            this.totalMaintenanceCost = totalMaintenanceCost;
            this.totalDaysInMaintenance = totalDaysInMaintenance;
            this.lastMaintenanceDate = lastMaintenanceDate;
            this.rentalsInPeriod = rentalsInPeriod;
            this.totalRentalsLifetime = totalRentalsLifetime;
            this.totalRevenue = totalRevenue;
            this.costToRevenueRatio = costToRevenueRatio;
            this.avgMaintenanceCost = avgMaintenanceCost;
        }
        
        // Getters and setters
        public String getPlateID() { return plateID; }
        public void setPlateID(String plateID) { this.plateID = plateID; }
        
        public String getVehicleType() { return vehicleType; }
        public void setVehicleType(String vehicleType) { this.vehicleType = vehicleType; }
        
        public String getVehicleModel() { return vehicleModel; }
        public void setVehicleModel(String vehicleModel) { this.vehicleModel = vehicleModel; }
        
        public int getTimesMaintained() { return timesMaintained; }
        public void setTimesMaintained(int timesMaintained) { this.timesMaintained = timesMaintained; }
        
        public double getTotalMaintenanceCost() { return totalMaintenanceCost; }
        public void setTotalMaintenanceCost(double totalMaintenanceCost) { 
            this.totalMaintenanceCost = totalMaintenanceCost; 
        }
        
        public double getTotalDaysInMaintenance() { return totalDaysInMaintenance; }
        public void setTotalDaysInMaintenance(double totalDaysInMaintenance) { 
            this.totalDaysInMaintenance = totalDaysInMaintenance; 
        }
        
        public Timestamp getLastMaintenanceDate() { return lastMaintenanceDate; }
        public void setLastMaintenanceDate(Timestamp lastMaintenanceDate) { 
            this.lastMaintenanceDate = lastMaintenanceDate; 
        }
        
        public int getRentalsInPeriod() { return rentalsInPeriod; }
        public void setRentalsInPeriod(int rentalsInPeriod) { 
            this.rentalsInPeriod = rentalsInPeriod; 
        }
        
        public int getTotalRentalsLifetime() { return totalRentalsLifetime; }
        public void setTotalRentalsLifetime(int totalRentalsLifetime) { 
            this.totalRentalsLifetime = totalRentalsLifetime; 
        }
        
        public double getTotalRevenue() { return totalRevenue; }
        public void setTotalRevenue(double totalRevenue) { this.totalRevenue = totalRevenue; }
        
        public double getCostToRevenueRatio() { return costToRevenueRatio; }
        public void setCostToRevenueRatio(double costToRevenueRatio) { 
            this.costToRevenueRatio = costToRevenueRatio; 
        }
        
        public double getAvgMaintenanceCost() { return avgMaintenanceCost; }
        public void setAvgMaintenanceCost(double avgMaintenanceCost) { 
            this.avgMaintenanceCost = avgMaintenanceCost; 
        }
        
        @Override
        public String toString() {
            return String.format("DefectiveVehicleData{plateID='%s', type='%s', model='%s', " +
                    "timesMaintained=%d, totalCost=%.2f, days=%d, lastMaintenance=%s, " +
                    "rentalsInPeriod=%d, totalRentals=%d, revenue=%.2f, ratio=%.3f, avgCost=%.2f}",
                    plateID, vehicleType, vehicleModel, timesMaintained, totalMaintenanceCost,
                    totalDaysInMaintenance, lastMaintenanceDate, rentalsInPeriod,
                    totalRentalsLifetime, totalRevenue, costToRevenueRatio, avgMaintenanceCost);
        }
    }
    
    /**
     * Generate monthly defective vehicles report.
     * 
     * Retrieves all vehicles that underwent maintenance in the specified month/year,
     * regardless of current status. Shows maintenance costs for the period and lifetime
     * revenue for cost analysis.
     * 
     * @param year Year for report (e.g., 2024)
     * @param month Month for report (1-12)
     * @return List of DefectiveVehicleData sorted by cost-to-revenue ratio DESC
     */
    public List<DefectiveVehicleData> generateMonthlyReport(int year, int month) {
        List<DefectiveVehicleData> reportData = new ArrayList<>();
        
        String sql = 
            "SELECT " +
            "    v.plateID, " +
            "    v.vehicleType, " +
            "    v.vehicleModel, " +
            "    v.status AS current_status, " +
            "    COUNT(DISTINCT m.maintenanceID) AS times_maintained, " +
            "    COALESCE(SUM(m.totalCost), 0) AS total_maintenance_cost, " +
            "    ROUND(COALESCE(SUM( " +
            "        CASE WHEN m.endDateTime IS NOT NULL " +
            "             THEN TIMESTAMPDIFF(HOUR, m.startDateTime, m.endDateTime) " +
            "             ELSE 0 " +
            "        END " +
            "    ), 0) / 24.0, 1) AS total_days_in_maintenance, " +
            "    MAX(m.startDateTime) AS last_maintenance_date, " +
            "    (SELECT COUNT(*) FROM rentals r " +
            "     WHERE r.plateID = v.plateID " +
            "     AND r.status = 'Completed' " +
            "     AND YEAR(r.endDateTime) = ? " +
            "     AND MONTH(r.endDateTime) = ?) AS rentals_in_period, " +
            "    (SELECT COUNT(*) FROM rentals r " +
            "     WHERE r.plateID = v.plateID " +
            "     AND r.status = 'Completed') AS total_rentals_lifetime, " +
            "    COALESCE((SELECT SUM(p.amount) " +
            "              FROM payments p " +
            "              JOIN rentals r ON p.rentalID = r.rentalID " +
            "              WHERE r.plateID = v.plateID " +
            "              AND r.status = 'Completed' " +
            "              AND p.status = 'Active'), 0) AS total_revenue " +
            "FROM maintenance m " +
            "INNER JOIN vehicles v ON m.plateID = v.plateID " +
            "WHERE m.status = 'Active' " +
            "    AND YEAR(m.startDateTime) = ? " +
            "    AND MONTH(m.startDateTime) = ? " +
            "GROUP BY v.plateID, v.vehicleType, v.vehicleModel, v.status " +
            "HAVING times_maintained > 0 " +
            "ORDER BY " +
            "    CASE WHEN total_revenue > 0 " +
            "         THEN total_maintenance_cost / total_revenue " +
            "         ELSE 999.999 " +
            "    END DESC, " +
            "    total_maintenance_cost DESC";
        
        try (Connection conn = util.DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            // Set parameters for subqueries and main query
            stmt.setInt(1, year);  // rentals_in_period year
            stmt.setInt(2, month); // rentals_in_period month
            stmt.setInt(3, year);  // main query year
            stmt.setInt(4, month); // main query month
            
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                DefectiveVehicleData data = new DefectiveVehicleData();
                data.setPlateID(rs.getString("plateID"));
                data.setVehicleType(rs.getString("vehicleType"));
                data.setVehicleModel(rs.getString("vehicleModel"));
                data.setTimesMaintained(rs.getInt("times_maintained"));
                data.setTotalMaintenanceCost(rs.getDouble("total_maintenance_cost"));
                data.setTotalDaysInMaintenance(rs.getInt("total_days_in_maintenance"));
                data.setLastMaintenanceDate(rs.getTimestamp("last_maintenance_date"));
                data.setRentalsInPeriod(rs.getInt("rentals_in_period"));
                data.setTotalRentalsLifetime(rs.getInt("total_rentals_lifetime"));
                data.setTotalRevenue(rs.getDouble("total_revenue"));
                
                // Calculate cost-to-revenue ratio
                double revenue = data.getTotalRevenue();
                double cost = data.getTotalMaintenanceCost();
                double ratio = (revenue > 0) ? (cost / revenue) : 999.999;
                data.setCostToRevenueRatio(ratio);
                
                // Calculate average maintenance cost
                double avgCost = (data.getTimesMaintained() > 0) 
                    ? (cost / data.getTimesMaintained()) 
                    : 0.0;
                data.setAvgMaintenanceCost(avgCost);
                
                reportData.add(data);
            }
            
        } catch (SQLException e) {
            System.err.println("Error generating defective vehicles report: " + e.getMessage());
            e.printStackTrace();
        }
        
        return reportData;
    }
    
    /**
     * Generate yearly defective vehicles report.
     * 
     * Retrieves all vehicles that underwent maintenance in the specified year,
     * regardless of current status. Shows maintenance costs for the year and lifetime
     * revenue for cost analysis.
     * 
     * @param year Year for report (e.g., 2024)
     * @return List of DefectiveVehicleData sorted by cost-to-revenue ratio DESC
     */
    public List<DefectiveVehicleData> generateYearlyReport(int year) {
        List<DefectiveVehicleData> reportData = new ArrayList<>();
        
        String sql = 
            "SELECT " +
            "    v.plateID, " +
            "    v.vehicleType, " +
            "    v.vehicleModel, " +
            "    v.status AS current_status, " +
            "    COUNT(DISTINCT m.maintenanceID) AS times_maintained, " +
            "    COALESCE(SUM(m.totalCost), 0) AS total_maintenance_cost, " +
            "    ROUND(COALESCE(SUM( " +
            "        CASE WHEN m.endDateTime IS NOT NULL " +
            "             THEN TIMESTAMPDIFF(HOUR, m.startDateTime, m.endDateTime) " +
            "             ELSE 0 " +
            "        END " +
            "    ), 0) / 24.0, 1) AS total_days_in_maintenance, " +
            "    MAX(m.startDateTime) AS last_maintenance_date, " +
            "    (SELECT COUNT(*) FROM rentals r " +
            "     WHERE r.plateID = v.plateID " +
            "     AND r.status = 'Completed' " +
            "     AND YEAR(r.endDateTime) = ?) AS rentals_in_period, " +
            "    (SELECT COUNT(*) FROM rentals r " +
            "     WHERE r.plateID = v.plateID " +
            "     AND r.status = 'Completed') AS total_rentals_lifetime, " +
            "    COALESCE((SELECT SUM(p.amount) " +
            "              FROM payments p " +
            "              JOIN rentals r ON p.rentalID = r.rentalID " +
            "              WHERE r.plateID = v.plateID " +
            "              AND r.status = 'Completed' " +
            "              AND p.status = 'Active'), 0) AS total_revenue " +
            "FROM maintenance m " +
            "INNER JOIN vehicles v ON m.plateID = v.plateID " +
            "WHERE m.status = 'Active' " +
            "    AND YEAR(m.startDateTime) = ? " +
            "GROUP BY v.plateID, v.vehicleType, v.vehicleModel, v.status " +
            "HAVING times_maintained > 0 " +
            "ORDER BY " +
            "    CASE WHEN total_revenue > 0 " +
            "         THEN total_maintenance_cost / total_revenue " +
            "         ELSE 999.999 " +
            "    END DESC, " +
            "    total_maintenance_cost DESC";
        
        try (Connection conn = util.DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, year);  // rentals_in_period year
            stmt.setInt(2, year);  // main query year
            
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                DefectiveVehicleData data = new DefectiveVehicleData();
                data.setPlateID(rs.getString("plateID"));
                data.setVehicleType(rs.getString("vehicleType"));
                data.setVehicleModel(rs.getString("vehicleModel"));
                data.setTimesMaintained(rs.getInt("times_maintained"));
                data.setTotalMaintenanceCost(rs.getDouble("total_maintenance_cost"));
                data.setTotalDaysInMaintenance(rs.getInt("total_days_in_maintenance"));
                data.setLastMaintenanceDate(rs.getTimestamp("last_maintenance_date"));
                data.setRentalsInPeriod(rs.getInt("rentals_in_period"));
                data.setTotalRentalsLifetime(rs.getInt("total_rentals_lifetime"));
                data.setTotalRevenue(rs.getDouble("total_revenue"));
                
                // Calculate cost-to-revenue ratio
                double revenue = data.getTotalRevenue();
                double cost = data.getTotalMaintenanceCost();
                double ratio = (revenue > 0) ? (cost / revenue) : 999.999;
                data.setCostToRevenueRatio(ratio);
                
                // Calculate average maintenance cost
                double avgCost = (data.getTimesMaintained() > 0) 
                    ? (cost / data.getTimesMaintained()) 
                    : 0.0;
                data.setAvgMaintenanceCost(avgCost);
                
                reportData.add(data);
            }
            
        } catch (SQLException e) {
            System.err.println("Error generating yearly defective vehicles report: " + e.getMessage());
            e.printStackTrace();
        }
        
        return reportData;
    }
    
    /**
     * Print formatted report to console.
     * 
     * Displays the defective vehicles report in a readable table format
     * with summary statistics and recommendations.
     * 
     * @param data List of DefectiveVehicleData to display
     * @param year Year of report
     * @param month Month of report (0 for yearly report)
     */
    public void printReport(List<DefectiveVehicleData> data, int year, int month) {
        System.out.println("\n" + "=".repeat(150));
        if (month > 0) {
            String[] months = {"", "January", "February", "March", "April", "May", "June", 
                             "July", "August", "September", "October", "November", "December"};
            System.out.printf("DEFECTIVE VEHICLES REPORT - %s %d\n", months[month], year);
        } else {
            System.out.printf("DEFECTIVE VEHICLES REPORT - Year %d\n", year);
        }
        System.out.println("Vehicles with Maintenance Activity in Period");
        System.out.println("=".repeat(150));
        
        if (data.isEmpty()) {
            System.out.println("No vehicles currently in maintenance for the specified period.");
            System.out.println("=".repeat(150) + "\n");
            return;
        }
        
        // Table header
        System.out.printf("%-12s %-15s %-20s %-8s %-8s %-15s %-10s %-20s %-10s %-10s %-15s %-10s %-15s\n",
            "Plate ID", "Type", "Model", "Maint.", "Rentals", "Total Cost", "Days in", "Last Maint.", 
            "Total", "Avg Revenue", "Total Revenue", "C/R Ratio", "Avg Cost");
        System.out.printf("%-12s %-15s %-20s %-8s %-8s %-15s %-10s %-20s %-10s %-10s %-15s %-10s %-15s\n",
            "", "", "", "Period", "Period", "(PHP)", "Maint.", "Date", "Rentals", "(PHP)", "(PHP)", "", "per Maint.");
        System.out.println("-".repeat(150));
        
        // Calculate summary statistics
        double totalCost = 0;
        double totalRevenue = 0;
        int totalMaintenance = 0;
        int highRiskCount = 0;  // ratio > 0.5
        int moderateRiskCount = 0;  // ratio 0.3-0.5
        
        // Display each vehicle
        for (DefectiveVehicleData vehicle : data) {
            // Format last maintenance date
            String lastMaintDate = (vehicle.getLastMaintenanceDate() != null) 
                ? new java.text.SimpleDateFormat("yyyy-MM-dd").format(vehicle.getLastMaintenanceDate())
                : "N/A";
            
            // Determine status indicator
            String riskIndicator = "";
            if (vehicle.getCostToRevenueRatio() > 0.5) {
                riskIndicator = " [HIGH RISK]";
                highRiskCount++;
            } else if (vehicle.getCostToRevenueRatio() >= 0.3) {
                riskIndicator = " [MONITOR]";
                moderateRiskCount++;
            }
            
            System.out.printf("%-12s %-15s %-20s %-8d %-8d %,15.2f %-10.1f %-20s %-10d %,10.2f %,15.2f %10.3f %,15.2f%s\n",
                vehicle.getPlateID(),
                vehicle.getVehicleType(),
                vehicle.getVehicleModel(),
                vehicle.getTimesMaintained(),
                vehicle.getRentalsInPeriod(),
                vehicle.getTotalMaintenanceCost(),
                vehicle.getTotalDaysInMaintenance(),
                lastMaintDate,
                vehicle.getTotalRentalsLifetime(),
                vehicle.getTotalRevenue() / (vehicle.getTotalRentalsLifetime() > 0 ? vehicle.getTotalRentalsLifetime() : 1),
                vehicle.getTotalRevenue(),
                vehicle.getCostToRevenueRatio(),
                vehicle.getAvgMaintenanceCost(),
                riskIndicator);
            
            totalCost += vehicle.getTotalMaintenanceCost();
            totalRevenue += vehicle.getTotalRevenue();
            totalMaintenance += vehicle.getTimesMaintained();
        }
        
        System.out.println("-".repeat(150));
        
        // Summary statistics
        System.out.println("\nSUMMARY STATISTICS:");
        System.out.printf("Total Vehicles in Maintenance: %d\n", data.size());
        System.out.printf("Total Maintenance Incidents: %d\n", totalMaintenance);
        System.out.printf("Total Maintenance Cost: PHP %,.2f\n", totalCost);
        System.out.printf("Total Revenue Generated: PHP %,.2f\n", totalRevenue);
        
        double avgCostPerVehicle = (data.size() > 0) ? (totalCost / data.size()) : 0.0;
        double avgRevenuePerVehicle = (data.size() > 0) ? (totalRevenue / data.size()) : 0.0;
        System.out.printf("Average Cost per Vehicle: PHP %,.2f\n", avgCostPerVehicle);
        System.out.printf("Average Revenue per Vehicle: PHP %,.2f\n", avgRevenuePerVehicle);
        
        double overallRatio = (totalRevenue > 0) ? (totalCost / totalRevenue) : 0.0;
        System.out.printf("Overall Cost-to-Revenue Ratio: %.3f\n", overallRatio);
        
        // Risk assessment
        System.out.println("\nRISK ASSESSMENT:");
        System.out.printf("HIGH RISK Vehicles (ratio > 0.5): %d vehicles - RECOMMEND REPLACEMENT\n", highRiskCount);
        System.out.printf("MODERATE RISK Vehicles (ratio 0.3-0.5): %d vehicles - MONITOR CLOSELY\n", moderateRiskCount);
        System.out.printf("LOW RISK Vehicles (ratio < 0.3): %d vehicles - CONTINUE MAINTENANCE\n", 
            data.size() - highRiskCount - moderateRiskCount);
        
        System.out.println("\nBUSINESS RECOMMENDATIONS:");
        if (highRiskCount > 0) {
            System.out.println("[!] URGENT: " + highRiskCount + " vehicle(s) spending more than 50% of revenue on maintenance.");
            System.out.println("   Consider replacing these vehicles to improve fleet profitability.");
        }
        if (moderateRiskCount > 0) {
            System.out.println("[!] WARNING: " + moderateRiskCount + " vehicle(s) spending 30-50% of revenue on maintenance.");
            System.out.println("   Monitor maintenance trends and plan for potential replacement.");
        }
        if (overallRatio > 0.4) {
            System.out.println("[!] Fleet maintenance costs are consuming " + 
                String.format("%.1f%%", overallRatio * 100) + " of revenue.");
            System.out.println("   Review fleet replacement strategy to improve profitability.");
        }
        
        System.out.println("=".repeat(150) + "\n");
    }
    
    /**
     * Main method for testing the report generation.
     * 
     * @param args Command line arguments (not used)
     */
    public static void main(String[] args) {
        DefectiveVehiclesReport report = new DefectiveVehiclesReport();
        
        System.out.println("=== DEFECTIVE VEHICLES REPORT TEST ===\n");
        
        // Test monthly report
        System.out.println("Testing Monthly Report for October 2024...");
        List<DefectiveVehicleData> monthlyData = report.generateMonthlyReport(2024, 10);
        report.printReport(monthlyData, 2024, 10);
        
        // Test yearly report
        System.out.println("\nTesting Yearly Report for 2024...");
        List<DefectiveVehicleData> yearlyData = report.generateYearlyReport(2024);
        report.printReport(yearlyData, 2024, 0);
        
        System.out.println("=== TEST COMPLETE ===");
    }
}
