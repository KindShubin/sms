package DB;

import LogsParts.LogsT;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Properties;

public class DBconnectSMB {

    // JDBC URL, username and password of MySQL server
//    private static final String url = "jdbc:mysql://10.22.0.1:3306/smssystem";
//    private static final String user = "root";
//    private static final String password = "FEwuV32u6una";

// logicPower
    private static final String url = "jdbc:mysql://10.55.0.2:3306/scheduler?zeroDateTimeBehavior=convertToNull";
    private static final String user = "mysql";
    private static final String password = "cvOHBdjBPi";


    private static Properties p;
    // JDBC variables for opening and managing connection
//    protected static Connection con;
//    protected static Statement stmt;
//    protected static ResultSet rs;

    public DBconnectSMB() throws SQLException {
        try {

//            p=new Properties();
//            p.setProperty("user",user);
//            p.setProperty("password",password);
//            p.setProperty("useUnicode","true");
//            //p.setProperty("characterEncoding","cp1251");
//            p.setProperty("characterEncoding","utf-8");
//            p.setProperty("useSSL","false");

//            // opening database connection to MySQL server
//            con = DriverManager.getConnection(url, p);
//            // getting Statement object to execute query
//            con.setAutoCommit(true);
//            stmt = con.createStatement();
//            // блок для отображения типов данных столбцов БД а также манипуляция с ними
        } catch (Exception ex) {
            ex.printStackTrace();
            System.out.println(LogsT.printDate() + "FAIL connection and statement in constructor DBconnect()");
            System.out.println(LogsT.printDate() + ex);
            ex.printStackTrace();
        } finally {
            //if(con==null) con.close();
            //System.out.println("connection and statement is created in constructor DBconnect()");
        }
    }

//    public static ArrayList<HashMap> executeQuery(String query) throws SQLException {
public static Exception executeQuery(String query) throws SQLException {
// executing SELECT query
        setProperties();
        //setPropertiesLite();
        Connection con = null;
        Statement stmt = null;
        SQLException sqlException = null;
        boolean boolException = false;
        try {
            // opening database connection to MySQL server
            con = DriverManager.getConnection(url, p);
            // getting Statement object to execute query
            con.setAutoCommit(true);
            stmt = con.createStatement();
            // блок для отображения типов данных столбцов БД а также манипуляция с ними
            stmt.executeUpdate(query);
        } catch (SQLException e) {
            System.out.println(LogsT.printDate() + "execute SELECT query in executeQuery is failed:");
            e.printStackTrace();
            boolException=true;
            sqlException=e;
        }
        finally {
            //////closeConnection(stmt, con);
//            System.out.println(LogsT.printDate() + "execute SELECT query in executeQuery is OK");
            try {
                stmt.close();
            } catch(SQLException se) {
                System.out.println(LogsT.printDate() + "closeConnectionWithRs -- STMT is NOT closed");
                se.printStackTrace();
            }
            try {
                con.close();
            } catch(SQLException se) {
                System.out.println(LogsT.printDate() + "closeConnectionWithRs -- CON is NOT closed");
                se.printStackTrace();
            }
        }
        if (boolException){
            System.out.println(LogsT.printDate() +"executeQuery() get Exception -- return this exception");
            return sqlException;
                //throw new SQLException(sqlException);
        } else {
            return null;
        }
    }

    public static ArrayList<HashMap> getResultSet(String query) throws SQLException {
// executing SELECT query
//        Properties p=new Properties();
//        p.setProperty("user",user);
//        p.setProperty("password",password);
//        p.setProperty("useUnicode","true");
//        //p.setProperty("characterEncoding","cp1251");
//        p.setProperty("characterEncoding","utf-8");
//        p.setProperty("useSSL", "false");
        setProperties();
        //setPropertiesLite();
        Connection con = null;
        Statement stmt = null;
        ResultSet rs = null;
        ResultSetMetaData md;
        int columns;
        ArrayList<HashMap> list = new ArrayList(50);
        try {
            // opening database connection to MySQL server
            con = DriverManager.getConnection(url, p);
            // getting Statement object to execute query
            con.setAutoCommit(true);
            stmt = con.createStatement();
            // блок для отображения типов данных столбцов БД а также манипуляция с ними
            rs=stmt.executeQuery(query);
            //
            md = rs.getMetaData();
//            System.out.println("@@@@@@@ md.toString(): "+md.toString());
            columns = md.getColumnCount();
            //System.out.println("@@@@@@@ columns: "+columns);
            rs.beforeFirst();
            while (rs.next()){
                HashMap map = new HashMap();
                for(int i=1; i<=columns; i++) {
//                    System.out.println("@@@@@@@ rs.next: md.getCatalogName(i): " + md.getCatalogName(i));
//                    System.out.println("@@@@@@@ rs.next: md.getColumnClassName(i): "+md.getColumnClassName(i));
//                    System.out.println("@@@@@@@ rs.next: md.getColumnLabel(i): " + md.getColumnLabel(i));
//                    System.out.println("@@@@@@@ rs.next: md.getColumnName(i): " + md.getColumnName(i));
//                    System.out.println("@@@@@@@ rs.next: md.getColumnTypeName(i): "+md.getColumnTypeName(i));
//                    System.out.println("@@@@@@@ rs.next: md.getSchemaName(i): " + md.getSchemaName(i));
//                    System.out.println("@@@@@@@ rs.next: md.getTableName(i): " +md.getTableName(i));
//                    System.out.println("@@@@@@@ rs.next: md.getColumnDisplaySize(i): "+md.getColumnDisplaySize(i));
//                    System.out.println("@@@@@@@ rs.next: md.getPrecision(i): "+md.getPrecision(i));
//                    System.out.println("@@@@@@@ rs.next: md.getScale(i): "+md.getScale(i));
                    map.put(md.getColumnLabel(i), rs.getObject(i));
                }
                list.add(map);
            }
            return list;
        } catch (SQLException e) {
            System.out.println(LogsT.printDate() + "execute SELECT query in getResultSet is failed:");
            e.printStackTrace();
        } finally {
            try {
                rs.close();
            } catch(Exception se) {
                System.out.println(LogsT.printDate() + "closeConnectionWithRs -- RS is NOT closed");
                se.printStackTrace();
            }
            try {
                stmt.close();
            } catch(SQLException se) {
                System.out.println(LogsT.printDate() + "closeConnectionWithRs -- STMT is NOT closed");
                se.printStackTrace();
            }
            try {
                con.close();
            } catch(SQLException se) {
                System.out.println(LogsT.printDate() + "closeConnectionWithRs -- CON is NOT closed");
                se.printStackTrace();
            }
        }
        return list;
    }

    public static int qntRowsInSelect(String query) throws SQLException {
        setProperties();
        //setPropertiesLite();
        Connection con = null;
        Statement stmt = null;
        ResultSet rs = null;
        int s = 0;
        try {
            // opening database connection to MySQL server
            con = DriverManager.getConnection(url, p);
            // getting Statement object to execute query
            con.setAutoCommit(true);
            stmt = con.createStatement();
            // блок для отображения типов данных столбцов БД а также манипуляция с ними
            rs=stmt.executeQuery(query);
            rs.beforeFirst();
            while(rs.next()){ s++; }
        } catch (SQLException e) {
            System.out.println(LogsT.printDate() + "execute SELECT query in qntRowsInSelect is failed:");
            e.printStackTrace();
        } finally {
            closeConnectionWithRs(rs, stmt, con);
            //System.out.println(LogsT.printDate() + "execute SELECT query in executeQuery is OK");
        }
        return s;
    }

    public static void closeConnection(Statement stmt, Connection con){
        try {
            stmt.close();
            //System.out.println(LogsT.printDate() + "stmt is closed");
        } catch(SQLException se) {
        /*can't do anything */
            System.out.println(LogsT.printDate() + "closeConnection -- STMT is NOT closed");
            se.printStackTrace();
        }
        try {
            con.close();
            //System.out.println(LogsT.printDate() + "con is closed");
        } catch(SQLException se) {
        /*can't do anything */
            System.out.println(LogsT.printDate() + "closeConnection -- CON is NOT closed");
            se.printStackTrace();
        }
        //finally { System.out.println(LogsT.printDate() + "Connection is closed"); }
    }

    public static void closeConnectionWithRs(ResultSet rs, Statement stmt, Connection con){
        try {
            rs.close();
            //System.out.println(LogsT.printDate() + "rs is closed");
        } catch(Exception se) {
        /*can't do anything */
            System.out.println(LogsT.printDate() + "closeConnectionWithRs -- RS is NOT closed");
            se.printStackTrace();
        }
        try {
            stmt.close();
            //System.out.println(LogsT.printDate() + "stmt is closed");
        } catch(SQLException se) {
        /*can't do anything */
            System.out.println(LogsT.printDate() + "closeConnectionWithRs -- STMT is NOT closed");
            se.printStackTrace();
        }
        try {
            con.close();
            //System.out.println(LogsT.printDate() + "con is closed");
        } catch(SQLException se) {
        /*can't do anything */
            System.out.println(LogsT.printDate() + "closeConnectionWithRs -- CON is NOT closed");
            se.printStackTrace();
        }
        //finally { System.out.println(LogsT.printDate() + "Connection with RS is closed"); }
    }


    private static void setProperties(){
        p=new Properties();
        p.setProperty("user",user);
        p.setProperty("password",password);
        p.setProperty("useUnicode","true");
            //p.setProperty("characterEncoding","cp1251");
        p.setProperty("characterEncoding","utf-8");
        p.setProperty("useSSL","false");
    }

    private static void setPropertiesLite(){
        p=new Properties();
        p.setProperty("user",user);
        p.setProperty("password",password);
        p.setProperty("useSSL","false");
    }
}
