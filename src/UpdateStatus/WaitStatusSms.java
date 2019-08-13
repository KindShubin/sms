//package UpdateStatus;
//
//import DB.DBconnect;
//
//import java.sql.SQLException;
//
//public class WaitStatusSms extends DBconnect{
//
//
//    public int checkQntAvailabilitySMS() throws SQLException {
//        String queryCheck = "select * from smslogs as ss where ss.availability = 'N' and ss.status = 'WAIT time period' and now() between ss.time_begin and ss.time_end";
//        int s = 0;
//        super.executeQuery(queryCheck);
//        //rs = makeRS(queryCheck);
//        // блок для отображения типов данных столбцов БД а также манипуляция с ними
//        while (rs.next()) {
//            s++;
//        }
//        //super.query=null;
//        return s;
//    }
//
//}
