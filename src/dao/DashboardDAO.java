package dao;

import util.DBConnection;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

public class DashboardDAO {

    public Map<String, Integer> getRecordCounts(){
        Map<String, Integer> counts = new HashMap<>();

        String sql =
                "SELECT 'Customers' as TableName, " + "COUNT(*) as RecordCount FROM customers " +
                "UNION ALL " +
                "SELECT 'Vehicles', COUNT(*) FROM vehicles " +
                "UNION ALL " +
                "SELECT 'Locations', COUNT(*) FROM locations " +
                "UNION ALL " +
                "SELECT 'Technicians', COUNT(*) FROM technicians " +
                "UNION ALL " +
                "SELECT 'Parts', COUNT(*) FROM parts " +
                "UNION ALL " +
                "SELECT 'Rentals', COUNT(*) FROM rentals " +
                "UNION ALL " +
                "SELECT 'Payments', COUNT(*) FROM payments " +
                "UNION ALL " +
                "SELECT 'Maintenance', COUNT(*) FROM maintenance " +
                "UNION ALL " +
                "SELECT 'Maintenance_Cheque', COUNT(*) FROM maintenance_cheque " +
                "UNION ALL " +
                "SELECT 'Penalties', COUNT(*) FROM penalty";

        try (Connection conn = DBConnection.getConnection();
        PreparedStatement stmt = conn.prepareStatement(sql);
        ResultSet rs = stmt.executeQuery()) {

            while (rs.next()){
                String tableName = rs.getString("TableName");
                int recordCount = rs.getInt("RecordCount");
                counts.put(tableName, recordCount);
            }
        } catch (SQLException e){
            System.err.println("Error getting record counts: " + e.getMessage());
            e.printStackTrace();
        }

        return counts;
    }
}
