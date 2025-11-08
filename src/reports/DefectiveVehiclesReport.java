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
    
    // TODO: Implement report generation methods
    // TODO: Create DefectiveVehicleData inner class to hold report results
    // TODO: Determine "defect date" from maintenance records or status change log
    // TODO: Count rentals that occurred BEFORE the defect date
    // TODO: Handle vehicles with multiple maintenance periods
    // TODO: Add filtering by vehicle type
}
