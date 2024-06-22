package Controller;
import java.sql.*;


public class Supplierdb {

    private Connection con;
    PreparedStatement pst;

    public Connection connect() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            con = DriverManager.getConnection("jdbc:mysql://localhost/project", "root", "");
            System.out.println("Connection Success");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            throw new RuntimeException("MySQL JDBC Driver not found: " + e.getMessage());
        } catch (SQLException e1) {
            e1.printStackTrace();
            throw new RuntimeException("Database connection failed: " + e1.getMessage());
        }
        return con;
    }



}
