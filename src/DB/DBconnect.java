package DB;

import LogsParts.LogsT;

import java.sql.*;
import java.util.Properties;

public class DBconnect {

    // JDBC URL, username and password of MySQL server
//    private static final String url = "jdbc:mysql://10.22.0.1:3306/smssystem";
//    private static final String user = "root";
//    private static final String password = "FEwuV32u6una";

// logicPower
    private static final String url = "jdbc:mysql://10.88.0.1:3306/smssystem";
    private static final String user = "root";
    private static final String password = "N9DW44dq";

//    private static final String url = "jdbc:mysql://localhost:3306/smssystem";
//    private static final String user = "root";
//    private static final String password = "N9DW44dq";

    public void setStmt(Statement stmt) {
        this.stmt = stmt;
    }

    public void setCon(Connection con) {
        this.con = con;
    }

    public void setRs(ResultSet rs) {
        this.rs = rs;
    }

    public Connection getCon() {
        return con;
    }

    public Statement getStmt() {
        return stmt;
    }

    public ResultSet getRs() {
        return rs;
    }

    // JDBC variables for opening and managing connection
    protected Connection con;
    protected Statement stmt;
    protected ResultSet rs;

    public DBconnect() throws SQLException {
        try {

            Properties p=new Properties();
            p.setProperty("user",user);
            p.setProperty("password",password);
            p.setProperty("useUnicode","true");
            //p.setProperty("characterEncoding","cp1251");
            p.setProperty("characterEncoding","utf-8");
            p.setProperty("useSSL","false");

            // opening database connection to MySQL server
            con = DriverManager.getConnection(url, p);
            // getting Statement object to execute query
            con.setAutoCommit(true);
            stmt = con.createStatement();
            // блок для отображения типов данных столбцов БД а также манипуляция с ними
        } catch (SQLException sqlEx) {
            sqlEx.printStackTrace();
            System.out.println(LogsT.printDate() + "FAIL connection and statement in constructor DBconnect()");
            System.out.println(LogsT.printDate() + sqlEx);
            sqlEx.printStackTrace();
        } finally {
            if(con==null) con.close();
            //System.out.println("connection and statement is created in constructor DBconnect()");
        }
    }

    public DBconnect(String query) throws SQLException {
        this();
        executeQuery(query);
    }

    public void executeQuery(String query){
// executing SELECT query
        try {
            this.rs = this.stmt.executeQuery(query);
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println(LogsT.printDate() + "execute SELECT query in executeQuery is failed");
        }
        finally {
//            System.out.println(LogsT.printDate() + "execute SELECT query in executeQuery is OK");
        }
    }

    public int qntRowsInSelect(ResultSet rs) throws SQLException {
        rs.beforeFirst();
        int s = 0;
        while(rs.next()){ s++; }
        rs.beforeFirst();
        return s;
    }

    public void closeConnection(){
        try { con.close(); } catch(SQLException se) { /*can't do anything */ }
        try { stmt.close(); } catch(SQLException se) { /*can't do anything */ }
        finally { System.err.println(LogsT.printDate() + "Connection is closed"); }
    }

    public void closeConnectionWithRs(){
        try { con.close(); } catch(SQLException se) { /*can't do anything */ }
        try { stmt.close(); } catch(SQLException se) { /*can't do anything */ }
        try { rs.close(); } catch(SQLException se) { /*can't do anything */ }
        finally { System.err.println(LogsT.printDate() + "Connection with RS is closed"); }
    }
}
