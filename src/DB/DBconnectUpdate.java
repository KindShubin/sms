package DB;

import LogsParts.LogsT;

import java.sql.SQLException;

public class DBconnectUpdate extends DBconnect{

    public DBconnectUpdate() throws SQLException {
        super();
    }

    public DBconnectUpdate(String query) throws SQLException {
        super(query);
    }

    public void updateAvailabilitySMS(){
        // блок для отображения типов данных столбцов БД
        try {
            while (this.getRs().next()) {
                System.out.println(LogsT.printDate() + "DBconnectUpdate.updateAvailabilitySMS()");
                long id = this.getRs().getLong(1);
                String update = new StringBuilder(200).append("update smssystem.smslogs as ss SET ss.availability='Y', ss.status = 'WAIT' WHERE ss.id=").append(id).toString();
                System.out.println(LogsT.printDate() + update);
                this.getStmt().execute(update);
                System.out.println(LogsT.printDate() + "id:"+id+" DONE");
            }
        } catch (SQLException e) {
            System.out.println(LogsT.printDate() + "что-то не так с update'ом в DBU.updateAvailabilitySMS()");
            e.printStackTrace();
        }
    }


    public static void updateProviderIdForSms(long smsId, int provId) throws SQLException {
        // блок для отображения типов данных столбцов БД
 //       try {
//            while (rsu.next()) {
                String update = new StringBuilder(200).append("update smssystem.smslogs as ss SET ss.provider_id=").append(provId).append(" WHERE ss.id=").append(smsId).toString();
                System.out.println(update);
                DBconnect dbConnectExecuteUpdate = new DBconnect();
        try {
            dbConnectExecuteUpdate.getStmt().executeUpdate(update);
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println(LogsT.printDate() + "что-то не так с update'ом в DBU.updateProviderIdForSms");
        }
//            }
//        } catch (SQLException e) {

//            e.printStackTrace();
 //       }
    }

/*    public int qntRowsInSelect(ResultSet rs) throws SQLException {
        rs.beforeFirst();
        int s = 0;
        while(rs.next()){ s++; }
        rs.beforeFirst();
        return s;
    }*/
}
