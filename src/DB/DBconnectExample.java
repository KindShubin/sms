package DB;

import LogsParts.LogsT;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class DBconnectExample {

    // JDBC URL, username and password of MySQL server
    private static final String url = "jdbc:mysql://10.22.0.1:3306/smssystem";
    private static final String user = "root";
    private static final String password = "FEwuV32u6una";

    // JDBC variables for opening and managing connection
    private static Connection con;
    private static Statement stmt;
    private static ResultSet rs;

    public static void main(String args[]) {
        String query = "select * from smslogs";

        try {
            // opening database connection to MySQL server
            con = DriverManager.getConnection(url, user, password);

            // getting Statement object to execute query
            stmt = con.createStatement();

            // executing SELECT query
            rs = stmt.executeQuery(query);

            while (rs.next()) {
                long id = rs.getLong(1);
                String text = rs.getString(6);
                String status = rs.getString(9);
                String date = String.valueOf(rs.getDate(10));
                //int count = rs.getInt(1);
                System.out.printf(LogsT.printDate() + "id: %d, text: %s, status %s, date is %s", id, text, status, date);
                System.out.println();
            }

        } catch (SQLException sqlEx) {
            sqlEx.printStackTrace();
        } finally {
            //close connection ,stmt and resultset here
            try { con.close(); } catch(SQLException se) { /*can't do anything */ }
            try { stmt.close(); } catch(SQLException se) { /*can't do anything */ }
            try { rs.close(); } catch(SQLException se) { /*can't do anything */ }
        }
    }

}
