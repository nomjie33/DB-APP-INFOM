package reports;

import dao.*;
import model.*;
import java.sql.*;
import java.util.*;

/**
 * Report Generation Service - Main Entry Point
 * 
 * PURPOSE: 
 * Centralized service for generating various business reports.
 * Acts as a facade/coordinator for individual report classes.
 * 
 * AVAILABLE REPORTS:
 * 1. Rental Revenue Report - Revenue by vehicle type (daily/monthly/yearly)
 * 2. Defective Vehicles Report - Vehicles marked defective with rental history
 * 3. Location Rental Frequency Report - Rental patterns by location
 * 4. Customer Rental Report - Customer behavior and spending analysis
 * 
 * USAGE:
 * ReportService reportService = new ReportService();
 * reportService.showReportMenu(); // Interactive menu for report selection
 * 
 * ARCHITECTURE:
 * This class delegates to specialized report classes:
 * - RentalRevenueReport.java
 * - DefectiveVehiclesReport.java
 * - LocationRentalFrequencyReport.java
 * - CustomerRentalReport.java
 * 
 * Each report class handles its own:
 * - Data retrieval from DAOs
 * - Business logic and calculations
 * - Formatting and display
 * 
 * FUTURE ENHANCEMENTS:
 * - Export reports to PDF, Excel, CSV
 * - Schedule automated report generation
 * - Email reports to management
 * - Create dashboard with multiple reports
 * - Add data visualization (charts/graphs)
 */
public class ReportService {
    
    private RentalRevenueReport revenueReport;
    private DefectiveVehiclesReport defectiveReport;
    private LocationRentalFrequencyReport locationReport;
    private CustomerRentalReport customerReport;
    
    /**
     * Constructor - Initialize all report generators
     */
    public ReportService() {
        this.revenueReport = new RentalRevenueReport();
        this.defectiveReport = new DefectiveVehiclesReport();
        this.locationReport = new LocationRentalFrequencyReport();
        this.customerReport = new CustomerRentalReport();
    }
    
    // TODO: Implement showReportMenu() - Interactive menu for report selection
    // TODO: Add methods to call each specific report
    // TODO: Implement date range validation
}
