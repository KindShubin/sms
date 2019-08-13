package Daemons;

import DB.DBconnectNEW;
import DB.GetVal;
import LogsParts.LogsT;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;

import static java.lang.Thread.sleep;

public class BsgSimcards {

    public static void main(String[] args) throws SQLException, InterruptedException {

        try { daemonize(); }
        catch (Throwable e){
            System.err.println(LogsT.printDate() + "Startup failed. " + e.getMessage());
        }
        finally { System.out.println(LogsT.printDate() + "demonize started!"); }

        orderService();
    }

    static private void daemonize() throws Exception{
        try { System.in.close(); }
        catch (Throwable e){
            System.err.println(LogsT.printDate() + "Fail. System in not close" + e.getMessage());
        }
        finally { System.out.println(LogsT.printDate() + "OK. System in closed"); }
        //System.out.close();
    }

    private static void orderService() throws SQLException, InterruptedException {
        while (true) {
            String query = "select ss.imsi, gg.name, ss.corp, ss.count_permonth from smssystem.simcards as ss join goip.goip as gg on ss.imsi=gg.imsi where ss.corp in ('L9bsgStream','L9bsg') and ss.count_permonth<1";
            int qnt = DBconnectNEW.qntRowsInSelect(query);
            System.out.printf(LogsT.printDate() + "orderService() run. qnt %s" , qnt);
            if (qnt>0){
                ArrayList<HashMap> result = DBconnectNEW.getResultSet(query);
                for (HashMap rs : result){
                    long imsi = 0L;
                    String corp="";
                    try{
                        imsi = GetVal.getLong(rs, "imsi");
                        //int prefix = GetVal.getInt(rs, "name");
                        corp = GetVal.getStr(rs, "corp");
                    } catch (Exception e){e.printStackTrace();}
                    ProcessBuilder limitsBsg = new ProcessBuilder("python3", "limitsBsg.py", corp, String.valueOf(imsi));
                    limitsBsg.directory(new File("/opt/smssystem/monitoring/"));
                    System.out.printf(LogsT.printDate() + "process limitsBsg.py %s %s", corp, String.valueOf(imsi));
                    try {
                        Process p = limitsBsg.start();
                        System.out.printf(LogsT.printDate() + "process limitsBsg.py run!");
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
            sleep(30000);
        }
    }

}
