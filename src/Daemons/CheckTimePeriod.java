package Daemons;

import DB.DBconnectNEW;
import DB.GetVal;
import LogsParts.LogsT;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;

import static java.lang.Thread.sleep;

public class CheckTimePeriod {

    public static void main(String[] args) throws SQLException, InterruptedException {

        try { daemonize(); }
        catch (Throwable e){
            System.err.println(LogsT.printDate() + "Startup failed. " + e.getMessage());
        }
        finally { System.out.println(LogsT.printDate() + "demonize started!"); }

        refreshAvailabilityMessages();
    }

    static private void daemonize() throws Exception{
        try { System.in.close(); }
        catch (Throwable e){
            System.err.println(LogsT.printDate() + "Fail. System in not close" + e.getMessage());
        }
        finally { System.out.println(LogsT.printDate() + "OK. System in closed"); }
        //System.out.close();
    }

    private static void refreshAvailabilityMessages() throws SQLException, InterruptedException {
        System.out.println(LogsT.printDate() + "start CheckTimePeriod.refreshAvailabilityMessages()");
        //int i=0;
        while (true) {
        //while (i<2) {
            String queryWaitTP = "select * from smssystem.smslogs as ss where ss.availability = 'N' and ss.status = 'ACCEPTED' and ss.total=1 and (ss.userfield <> 'update id' or ss.userfield is null) and ((now() between ss.time_begin and ss.time_end) or (ss.time_begin is null and ss.time_end is null) or (ss.time_begin is null and now() < ss.time_end) or (ss.time_end is null and now() > ss.time_begin))";
            if (DBconnectNEW.qntRowsInSelect(queryWaitTP)>0){
                ArrayList<HashMap> result = DBconnectNEW.getResultSet(queryWaitTP);
                for (HashMap rs : result){
                    try{
                        long id = GetVal.getLong(rs, "id");
                        System.out.println(LogsT.printDate() + "SMS with id:"+id+"is may be available");
                        String update = new StringBuilder(200).append("update smssystem.smslogs as ss SET ss.availability='Y', ss.status = 'WAIT' WHERE ss.id=").append(id).toString();
                        System.out.println(LogsT.printDate() + update);
                        DBconnectNEW.executeQuery(update);
                        System.out.println(LogsT.printDate() + "id:"+id+" now is available and status WAIT");
                    } catch (Exception e) {
                        System.out.println(LogsT.printDate() + "что-то не так с блоком смены статуса смс с ACCEPTED на WAIT");
                        e.printStackTrace();
                    }
                }
                //break;
                sleep(500);
                //i++;
            } else {
                //System.out.println(LogsT.printDate() + "NOUP Not Available message for send");
                sleep(1000);
                //i++;
            }
        }
    }

}
