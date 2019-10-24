package Daemons;

import DB.DBconnectNEW;
import DB.GetVal;
import LogsParts.LogsId;
import LogsParts.LogsT;
import Run.ClassRunAsyncBonding;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;

import static java.lang.Thread.sleep;

public class Binder {

    public static void main(String[] args) throws SQLException, InterruptedException {

        try { daemonize(); }
        catch (Throwable e){
            System.err.println(LogsT.printDate() + "Startup failed. " + e.getMessage());
        }
        finally { System.out.println(LogsT.printDate() + "demonize started!"); }

        binder();
    }

    static private void daemonize() throws Exception{
        try { System.in.close(); }
        catch (Throwable e){
            System.err.println(LogsT.printDate() + "Fail. System in not close" + e.getMessage());
        }
        finally { System.out.println(LogsT.printDate() + "OK. System in closed"); }
        //System.out.close();
    }

    private static void binder() throws SQLException, InterruptedException {
        while (true) {
            //String query = "select uniqid from smssystem.smslogs where total>1 and status='ENROUTE' and datediff(now(),time_entry)<2 group by uniqid order by rand() limit 1";
            String query = "select ss.uniqid from smssystem.smslogs as ss left join smssystem.clients as sc on sc.id=ss.client_id where total>1 and status='ENROUTE' and datediff(now(),time_entry)<2 group by ss.uniqid, sc.prioritet order by sc.prioritet asc, ss.uniqid asc limit 10";
//            DBconnectSelect db = new DBconnectSelect(query);
//            ResultSet rs = db.getRs();
            int qntResults=DBconnectNEW.qntRowsInSelect(query);
            if (qntResults>0){
                ArrayList<HashMap> result = DBconnectNEW.getResultSet(query);
                //rs.first();
                int i=0;
                while (i<qntResults) {
                    long uniqid = GetVal.getLong(result.get(i), "uniqid");
                    System.out.println(LogsT.printDate() + "|binder| qntResults="+qntResults+", i="+i+", select uniqid fo bonding: " + uniqid);
                    Thread ct = Thread.currentThread();
                    ct.setPriority(9);
                    System.out.println(LogsT.printDate() + "|binder| run main Thread: " + ct.getName());
                    if (setStatusBonding(uniqid)){
                        Runnable r = new ClassRunAsyncBonding(uniqid);
                        Thread t = new Thread(r);
                        //t.setDaemon(true);
                        t.setPriority(5);
                        //System.out.println(LogsT.printDate() + "start t.start() with id:" + id);
                        //////////////setStatusBonding(uniqid);
                        t.start();
                    }
                    /////////setStatusBonding(uniqid);
                    //new Thread(new ClassRunAsyncSend(id)).start();
                    //System.out.println(LogsT.printDate() + "end t.start() with id:"+id);
                    sleep(50);
                    //rs.first();
                    //long uniqid = rs.getLong("uniqid");
                    //Bonding.bonding(uniqid);
                    //sleep(500);
                    i++;
                }
            }
            else sleep(500);
        }
    }

    private static boolean setStatusBonding(Number uniqid){
        boolean res = false;
        System.out.println(LogsT.printDate() + LogsId.id(uniqid) + "|Daemons.Binder.setStatusBonding()| begin update sms status to bonding for uniqid="+uniqid);
        String update = new StringBuilder().append("update smssystem.smslogs Set availability='N', status = 'bonding' WHERE total>1 and datediff(now(),time_entry)<2 and uniqid=").append(uniqid).toString();
        try {
            DBconnectNEW.executeQuery(update);
            System.out.println(LogsT.printDate() + LogsId.id(uniqid) + "|Daemons.Binder.setStatusBonding()| was set status bonding for uniqid=" + uniqid+", res=true");
            res=true;
        } catch (SQLException e) {
            System.out.println(LogsT.printDate() + LogsId.id(uniqid) + "|Daemons.Binder.setStatusBonding()| ERROR! not update status sms ENROUTE-->bonding for uniqid="+uniqid+", res=false");
            e.printStackTrace();
        }
        return res;
    }

}
