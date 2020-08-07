import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class DBconnect {

    // JDBC URL, username and password of MySQL server
    private static final String url = "jdbc:mysql://localhost:3306/smssystem";
    private static final String user = "root";
    private static final String password = "FEwuV32u6una";

    // JDBC variables for opening and managing connection
    private static Connection con;
    private static Statement stmt;
    private static ResultSet rs;

    public void checkAvailabilitySMS() {
        String query = "makeRS ss.id from smslogs as sms where sms.availability = 'N' and sms.status = 'WAIT time period' and now() between sms.time_begin and sms.time_end";

        try {
            // opening database connection to MySQL server
            con = DriverManager.getConnection(url, user, password);

            // getting Statement object to execute query
            stmt = con.createStatement();

            // executing SELECT query
            rs = stmt.executeQuery(query);

            while (rs.next()) {
                int id = rs.getInt(1);
                //int count = rs.getInt(1);
                System.out.printf("id %d will change status on WAIT", id);
                String insert = new StringBuilder(100).append("update smslogs as sms SET sms.availability='Y', sms.status = 'WAIT' WHERE sms.id=").append(id).append('"').toString();
                Connection con1 = DriverManager.getConnection(url, user, password);
                Statement stmt1 = con1.createStatement();
                ResultSet rs1 = stmt1.executeQuery(insert);
                con1.close();
                stmt1.close();
                rs1.close();
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
