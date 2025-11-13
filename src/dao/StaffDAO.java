package dao;

import model.Staff;
import util.DBConnection;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class StaffDAO {

    public boolean insertStaff(Staff staff){

        String sql = "INSERT INTO staff (staffID, username, staffEmail, password) " +
                "VALUES (?, ?, ?, ?)";

        try(Connection conn = DBConnection.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql)){

            stmt.setString(1, staff.getStaffID());
            stmt.setString(2, staff.getUsername());
            stmt.setString(3, staff.getStaffEmail());
            stmt.setString(4, staff.getPassword());

            int rowsAffected = stmt.executeUpdate();

            if (rowsAffected > 0){
                System.out.println("Staff inserted: " + staff.getUsername());
                return true;
            }

        } catch(SQLException e){
            System.err.println("Error inserting staff: " + e.getMessage());
            e.printStackTrace();
        }

        return false;
    }

    public Staff getStaffByUsername(String username){

        String sql = "SELECT * FROM staff WHERE username = ?";

        try (Connection conn = DBConnection.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql)){

            stmt.setString(1, username);

            try (ResultSet rs = stmt.executeQuery()){
                if (rs.next()){
                    Staff staff = new Staff();
                    staff.setStaffID(rs.getString("staffID"));
                    staff.setUsername(rs.getString("username"));
                    staff.setStaffEmail(rs.getString("staffEmail"));
                    staff.setPassword(rs.getString("password"));
                    return staff;
                }
            }

        } catch (SQLException e){
            System.err.println("Error getting staff by username: " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }
}
