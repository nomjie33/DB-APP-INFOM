package util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.sql.Timestamp;
import java.util.Date;

/**
 * Utility Helper Class for common operations.
 * 
 * PURPOSE: Provides reusable helper methods for:
 * - Date/time formatting and conversion
 * - Input validation
 * - String formatting
 * - Calculations
 * 
 * COLLABORATOR NOTES:
 * - Add methods here that are used across multiple classes
 * - Keep methods static for easy access
 * - Document each method clearly
 */
public class Helpers {
    
    // ===== DATE/TIME UTILITIES =====
    
    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd");
    private static final SimpleDateFormat DATETIME_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    
    /**
     * Convert String to java.sql.Timestamp
     * @param dateString Date string in format "yyyy-MM-dd HH:mm:ss"
     * @return Timestamp object or null if invalid
     */
    public static Timestamp stringToTimestamp(String dateString) {
        try {
            Date date = DATETIME_FORMAT.parse(dateString);
            return new Timestamp(date.getTime());
        } catch (ParseException e) {
            System.err.println("Invalid date format: " + dateString);
            return null;
        }
    }
    
    /**
     * Convert Timestamp to formatted String
     * @param timestamp Timestamp to format
     * @return Formatted date string
     */
    public static String timestampToString(Timestamp timestamp) {
        if (timestamp == null) return "";
        return DATETIME_FORMAT.format(timestamp);
    }
    
    /**
     * Calculate days between two dates
     * @param startDate Start date
     * @param endDate End date
     * @return Number of days (rounded up)
     */
    public static int calculateDaysBetween(Timestamp startDate, Timestamp endDate) {
        long diffInMillis = endDate.getTime() - startDate.getTime();
        long diffInDays = diffInMillis / (1000 * 60 * 60 * 24);
        return (int) Math.ceil(diffInDays);
    }
    
    /**
     * Check if a date is in the past
     * @param date Date to check
     * @return true if date is before now
     */
    public static boolean isDatePast(Timestamp date) {
        return date.before(new Timestamp(System.currentTimeMillis()));
    }
    
    // ===== VALIDATION UTILITIES =====
    
    /**
     * Validate email format
     * @param email Email to validate
     * @return true if valid format
     */
    public static boolean isValidEmail(String email) {
        if (email == null || email.isEmpty()) return false;
        String emailRegex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$";
        return email.matches(emailRegex);
    }
    
    /**
     * Validate phone number format
     * @param phone Phone number to validate
     * @return true if valid format
     */
    public static boolean isValidPhone(String phone) {
        if (phone == null || phone.isEmpty()) return false;
        String phoneRegex = "^[0-9]{10,15}$"; // 10-15 digits
        return phone.replaceAll("[^0-9]", "").matches(phoneRegex);
    }
    
    /**
     * Validate string is not null or empty
     * @param str String to validate
     * @return true if not null and not empty
     */
    public static boolean isNotEmpty(String str) {
        return str != null && !str.trim().isEmpty();
    }
    
    // ===== FORMATTING UTILITIES =====
    
    /**
     * Format currency amount
     * @param amount Amount to format
     * @return Formatted string (e.g., "$1,234.56")
     */
    public static String formatCurrency(double amount) {
        return String.format("$%.2f", amount);
    }
    
    /**
     * Format percentage
     * @param value Decimal value (e.g., 0.75 for 75%)
     * @return Formatted percentage string
     */
    public static String formatPercentage(double value) {
        return String.format("%.2f%%", value * 100);
    }
    
    // ===== CALCULATION UTILITIES =====
    
    /**
     * Calculate rental cost
     * @param dailyRate Vehicle daily rate
     * @param days Number of rental days
     * @return Total rental cost
     */
    public static double calculateRentalCost(double dailyRate, int days) {
        return dailyRate * days;
    }
    
    /**
     * Calculate late fee
     * @param dailyRate Vehicle daily rate
     * @param daysLate Number of days late
     * @param multiplier Late fee multiplier (e.g., 1.5 for 150% of daily rate)
     * @return Late fee amount
     */
    public static double calculateLateFee(double dailyRate, int daysLate, double multiplier) {
        return dailyRate * daysLate * multiplier;
    }
    
    /**
     * Generate unique ID string (for receipts, etc.)
     * @param prefix Prefix for ID (e.g., "RCP", "RNT")
     * @return Unique ID string
     */
    public static String generateUniqueId(String prefix) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
        String timestamp = sdf.format(new Date());
        int random = (int) (Math.random() * 1000);
        return String.format("%s-%s-%03d", prefix, timestamp, random);
    }
    
    // ===== STRING UTILITIES =====
    
    /**
     * Capitalize first letter of each word
     * @param str String to capitalize
     * @return Capitalized string
     */
    public static String toTitleCase(String str) {
        if (str == null || str.isEmpty()) return str;
        String[] words = str.split(" ");
        StringBuilder result = new StringBuilder();
        for (String word : words) {
            if (word.length() > 0) {
                result.append(Character.toUpperCase(word.charAt(0)))
                      .append(word.substring(1).toLowerCase())
                      .append(" ");
            }
        }
        return result.toString().trim();
    }
}
