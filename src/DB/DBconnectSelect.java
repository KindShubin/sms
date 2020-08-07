package DB;

import LogsParts.LogsT;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;

public class DBconnectSelect extends DBconnect {

    private String query;

    public  DBconnectSelect(String query) throws SQLException {
        super(query);
        this.query=query;
        //setRs(getStmt().executeQuery(query));
        //setRs(makeRS(this.query));
        //System.out.println("DBconnectSelect (query) query = "+ query);
    }

    public DBconnectSelect() throws SQLException {
        super();
    }

    public ResultSet makeRS(){
        try {
            //executing SELECT query
            setRs(getStmt().executeQuery(query));
        // блок для отображения типов данных столбцов БД а также манипуляция с ними
        } catch (SQLException sqlEx) {
            sqlEx.printStackTrace();
        } finally {
            //System.out.println("makeRS is prepared");
        }
        return getRs();
    }

    public ResultSet makeRS(String s){
        this.query=s;
        makeRS();
        this.query=null;
        return rs;
    }

    public void getInfoAboutColumns() throws SQLException {
        ResultSetMetaData rsmd = rs.getMetaData();
        int columnCount = rsmd.getColumnCount();
        System.out.println(LogsT.printDate() + "counts is "+columnCount);
        for (int i = 1; i <= columnCount; i++)
        {
            String s = rsmd.getColumnTypeName(i);
            System.out.println (LogsT.printDate() + "Column " + i + " is type " + s);
        }
    }

    public void getInfoAboutColumns(ResultSet rsGetInfo) throws SQLException {
        ResultSetMetaData rsmd = rsGetInfo.getMetaData();
        int columnCount = rsmd.getColumnCount();
        System.out.println(LogsT.printDate() + "counts is "+columnCount);
        for (int i = 1; i <= columnCount; i++)
        {
            String s = rsmd.getColumnTypeName(i);
            System.out.println (LogsT.printDate() + "Column " + i + " is type " + s);
        }
    }

//    public void printSelect() throws SQLException {
//        ResultSetMetaData rsmd = rs.getMetaData();
//        System.out.println("PRINT RESULT:");
//        int columnsNumber = rsmd.getColumnCount();
//        System.out.println("columnsNumber: "+ columnsNumber);
//        while (rs.next()) {
//            for (int i = 1; i <= columnsNumber; i++) {
//                if (i > 1) System.out.print(",  ");
//                String columnValue = rs.getString(i);
//                System.out.print(columnValue + " " + rsmd.getColumnName(i));
//            }
//            System.out.println("");
//        }
//    }

    public void printSelect() throws SQLException {
        rs.beforeFirst();
        ResultSetMetaData rsmd = rs.getMetaData();
        int columnsNumber = rsmd.getColumnCount();
        System.out.println(LogsT.printDate() + "PRINT RESULT:");
        while (rs.next()) {
            int i = 1;
            while (i <= columnsNumber) {  // don't skip the last column, use <=
                System.out.print(LogsT.printDate() + rsmd.getColumnName(i)+": " + rs.getString(i++) + "; ");
            }
            System.out.println();
        }
    }

    public void closeConnectionWithRs(){
        try { con.close(); } catch(SQLException se) { /*can't do anything */ }
        try { stmt.close(); } catch(SQLException se) { /*can't do anything */ }
        try { rs.close(); } catch(SQLException se) { /*can't do anything */ }
        finally { System.err.println(LogsT.printDate() + "Connection is closed with RS"); }
    }
}
