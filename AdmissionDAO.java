import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class AdmissionDAO {
    private static final String URL = "jdbc:mysql://localhost:3306/admissions_db";
    private static final String USER = "root";
    public static final String PASS = "root123";

    public static String getPass() {
        return PASS;
    }

    private static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASS);
    }

    public static boolean saveApplication(String name, String email, String phone, double score, String course) {
        String sql = "INSERT INTO applications (student_name, email, phone, hsc_percentage, program, status) VALUES (?, ?, ?, ?, ?, 'Pending')";
        try (Connection conn = getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, name);
            ps.setString(2, email);
            ps.setString(3, phone);
            ps.setDouble(4, score);
            ps.setString(5, course);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static boolean updateStatus(int applicationId, String newStatus) {
        String sql = "UPDATE applications SET status = ? WHERE application_id = ?";
        try (Connection conn = getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, newStatus);
            ps.setInt(2, applicationId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    public static void main(String[] args) {
        boolean ok = saveApplication("Rahul Sen", "rahul@test.com", "9988776655", 88.0, "Computer Engineering");
        if (ok) {
            System.out.println("SUCCESS: Record inserted into admissions_db MySQL!");
        } else {
            System.out.println("FAILED: Check MySQL credentials or driver.");
        }
    }
}