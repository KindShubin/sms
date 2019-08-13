package Daemons;

import DB.DBconnectNEW;
import DB.GetVal;
import LogsParts.LogsId;
import LogsParts.LogsT;
import Scheduler.Sim;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static java.lang.Thread.sleep;

public class Scheduler {

    private static final String[] ARR_SCHEDULER_GROUPS = {"groupKS1", "groupMTS1", "groupLIFE1"};
    private static final String[] ARR_SIMCARDS_GROUPS = {"Kpp1","Mpp1","Lpp1"};
    private static String SCHEDULER_GROUPS="";// = "'groupLIFE1', 'groupKS1', 'groupMTS1'";
    private static String SIMCARDS_GROUPS="";// = "'Kpp1','Mpp1','Lpp1'";
    private static Map<String, String> MAP_GROUP_CORPS = new HashMap<>();

    private static void buildMapGroupsCorps(){
        for (int i=0; i<ARR_SCHEDULER_GROUPS.length; i++){
            SCHEDULER_GROUPS+="'";
            SCHEDULER_GROUPS += ARR_SCHEDULER_GROUPS[i];
            SCHEDULER_GROUPS+="'";
            if(i<ARR_SCHEDULER_GROUPS.length-1){ SCHEDULER_GROUPS+=", ";}
            SIMCARDS_GROUPS+="'";
            SIMCARDS_GROUPS += ARR_SIMCARDS_GROUPS[i];
            SIMCARDS_GROUPS+="'";
            if(i<ARR_SCHEDULER_GROUPS.length-1){ SIMCARDS_GROUPS+=", ";}
            MAP_GROUP_CORPS.put(ARR_SCHEDULER_GROUPS[i],ARR_SIMCARDS_GROUPS[i]);
        }
    }

    public static void main(String[] args) throws SQLException, InterruptedException {

        try { daemonize(); }
        catch (Throwable e){
            System.err.println(LogsT.printDate() + "Startup failed. " + e.getMessage());
        }
        finally { System.out.println(LogsT.printDate() + "demonize started!"); }
        buildMapGroupsCorps();
        System.out.println(LogsT.printDate() + "SCHEDULER_GROUPS: " + SCHEDULER_GROUPS);
        System.out.println(LogsT.printDate() + "SIMCARDS_GROUPS: "+SIMCARDS_GROUPS);
        System.out.println(LogsT.printDate() + "MAP_GROUP_CORPS: "+MAP_GROUP_CORPS);
        nullCheckActionInMyScheduler();
        nullOnairInSimcards();
        myScheduler();
    }

    static private void daemonize() throws Exception{
        try { System.in.close(); }
        catch (Throwable e){
            System.err.println(LogsT.printDate() + "Fail. System in not close" + e.getMessage());
        }
        finally { System.out.println(LogsT.printDate() + "OK. System in closed"); }
        //System.out.close();
    }

    private static void myScheduler() throws SQLException, InterruptedException {
        //int count = 0;
        while (true) {
            //System.out.println(LogsT.printDate()+"begin insert new sim in smssystem.my_scheduler");
            String queryNewSim = new StringBuilder().append("SELECT RIGHT(LEFT(ss.imsi,16), 15) as imsi, ss.sim_name, ss.line_name, sst.sim_team_name FROM scheduler.sim as ss left join smssystem.my_scheduler as sms on ss.sim_name=sms.sim_name join scheduler.sim_team as sst on sst.sim_team_id=ss.sim_team_id ")
                    .append("where ss.imsi!='' and ss.line_name>0 and sms.sim_name is null and sst.sim_team_name in (")
                    .append(SCHEDULER_GROUPS).append(")").toString();
            //System.out.println("queryNewSim: "+queryNewSim);
            boolean checkNewSimcard=false;
            ArrayList<HashMap> resultInsert = DBconnectNEW.getResultSet(queryNewSim);
            for (HashMap rs : resultInsert) {
                long imsi = GetVal.getLong(rs, "imsi");
                int sim_name = GetVal.getInt(rs, "sim_name");
                int line_nsme = GetVal.getInt(rs, "line_name");
                String group_name = GetVal.getStr(rs, "sim_team_name");
                System.out.println(LogsT.printDate() + "sim_name: " + sim_name + " line_nsme: " + line_nsme);
                String strInsertSimInMyScheduler = new StringBuilder().append("insert into smssystem.my_scheduler(sim_name) values (").append(sim_name).append(")").toString();
                System.out.println(LogsT.printDate() + "strInsertSimInMyScheduler: " + strInsertSimInMyScheduler);
                try {
                    DBconnectNEW.executeQuery(strInsertSimInMyScheduler);
                } catch (Exception e) {
                    System.out.println(LogsT.printDate() + "Fail insert to smssystem.my_scheduler. Maybe imsi is not new");
                    e.printStackTrace();
                }
                String strInsertSimInSimcards = new StringBuilder().append("insert into smssystem.simcards(imsi, corp) values (").append(imsi).append(", '").append(MAP_GROUP_CORPS.get(group_name)).append("')").toString();
                System.out.println(LogsT.printDate() + "strInsertSimInSimcards: " + strInsertSimInSimcards);
                System.out.println(LogsT.printDate() + "checkNewSimcard: "+checkNewSimcard);
                try {
                    DBconnectNEW.executeQuery(strInsertSimInSimcards);
                    checkNewSimcard=true;
                } catch (Exception e1) {
                    System.out.println(LogsT.printDate() + "Fail insert to smssystem.simcards. Maybe imsi is not new");
                    e1.printStackTrace();
                }
                if(checkNewSimcard){
                    try {
                        updateCountersInSimcards(imsi);
                    } catch (Exception e4){
                        System.out.println(LogsT.printDate()+LogsId.id(imsi)+"Fail Scheduler.updateCountersInSimcards("+imsi+")");
                        e4.printStackTrace();
                    }
                }
            }
            //System.out.println(LogsT.printDate()+"begin update sim in smssystem.my_scheduler");
//            String queryUpdateSim = new StringBuilder().append("SELECT ss.sim_name, RIGHT(LEFT(ss.imsi,16), 15) as imsi, ss.iccid, ss.line_name, case when sst.sim_team_name regexp '[Kk][Ss]' then 'Kpp1' when sst.sim_team_name regexp '[Mm][Tt][Ss]' then 'Mpp1' when sst.sim_team_name regexp '[Ll][Ii][Ff][Ee]' then 'Lpp1' end as corp,\n" +
//                    "ss.bank_name as id_simbank, gg.host, substring(gg.name,6,2) as port\n" +
//                    "FROM scheduler.sim as ss \n" +
//                    "join smssystem.my_scheduler as sms on ss.sim_name=sms.sim_name \n" +
//                    "join scheduler.sim_team as sst on sst.sim_team_id=ss.sim_team_id\n" +
//                    "join goip.goip as gg on ss.line_name=gg.name\n" +
//                    "where ss.imsi!='' and ss.line_name>0 and sms.check_action='N' and sst.sim_team_name in (")
//                    .append(SCHEDULER_GROUPS).append(")").toString();
            String queryUpdateSim = new StringBuilder().append("SELECT ss.sim_name, RIGHT(LEFT(ss.imsi,16), 15) as imsi, ss.iccid, ss.line_name, sst.sim_team_name, \n" +
                    "ss.bank_name as id_simbank, gg.host, substring(gg.name,6,2) as port\n" +
                    "FROM scheduler.sim as ss \n" +
                    "join smssystem.my_scheduler as sms on ss.sim_name=sms.sim_name \n" +
                    "join scheduler.sim_team as sst on sst.sim_team_id=ss.sim_team_id\n" +
                    "join goip.goip as gg on ss.line_name=gg.name\n" +
                    "where ss.imsi!='' and ss.line_name>0 and sms.check_action='N' and sst.sim_team_name in (")
                    .append(SCHEDULER_GROUPS).append(")").toString();
            //System.out.println("queryUpdateSim: "+queryUpdateSim);
            ArrayList<HashMap> resultUpdate = DBconnectNEW.getResultSet(queryUpdateSim);
            for (HashMap rs : resultUpdate) {
                int sim_name = GetVal.getInt(rs, "sim_name");
                long imsi = GetVal.getLong(rs, "imsi");
                String iccid = GetVal.getStr(rs, "iccid");
                int line_name = GetVal.getInt(rs, "line_name");
                String group = GetVal.getStr(rs, "sim_team_name");
                //String corp = GetVal.getStr(rs, "corp");
                String corp = MAP_GROUP_CORPS.get(group);
                int id_simbank = GetVal.getInt(rs, "id_simbank");
                String host = GetVal.getStr(rs, "host");
                int port = GetVal.getInt(rs, "port");
                System.out.println(LogsT.printDate() + "sim_name: " + sim_name + " line_name: " + line_name);
                String strDeleteInfoSimInMyScheduler = new StringBuilder().append("UPDATE smssystem.my_scheduler SET imsi=NULL, line_name=NULL, check_action='N', time_bind_begin=NULL, time_define_oper=NULL, time_begin_work=NULL, time_begin_pause=NULL, time_end_pause=NULL, time_end_work=NULL, package_sms_ussd_begin=NULL, package_sms_begin=NULL, package_sms_time_begin=NULL, package_sms_ussd_last=NULL, package_sms_last=NULL, package_sms_time_last=NULL, balance_ussd_begin=NULL, balance_begin=NULL, balance_time_begin=NULL, balance_ussd_last=NULL, balance_last=NULL, balance_time_last=NULL, description=NULL WHERE sim_name=").append(sim_name).toString();
                String strUpdateSimInMyScheduler = new StringBuilder().append("UPDATE smssystem.my_scheduler SET line_name=")
                        .append(line_name).append(", imsi=").append(imsi).append(", time_bind_begin=now(), check_action='Y', attempt=attempt+1 where sim_name=").append(sim_name).toString();
                String strUpdateSimInSimcards = new StringBuilder().append("UPDATE smssystem.simcards SET iccid='")
                        .append(iccid).append("', id_simbank=")
                        .append(id_simbank).append(", id_port_simbank=")
                        .append(sim_name).append(", prefix=")
                        .append(line_name).append(", ip='")
                        .append(host).append("', port=")
                        .append(port).append(", corp='")
                        .append(corp).append("', onair='N', block=1, availability='Y', server='logicpower', description='")
                        .append("Scheduler.myScheduler() update sim " + LogsT.printDate()).append("' where imsi=").append(imsi).toString();
                System.out.println(LogsT.printDate() + "strUpdateSimInMyScheduler: " + strUpdateSimInMyScheduler);
                try {
                    DBconnectNEW.executeQuery(strDeleteInfoSimInMyScheduler);
                } catch (Exception e1){
                    System.out.println(LogsT.printDate()+LogsId.id(imsi)+"Fail Scheduler.myScheduler() DBconnectNEW.executeQuery(strDeleteInfoSimInMyScheduler)");
                    e1.printStackTrace();
                }
                try {
                    DBconnectNEW.executeQuery(strUpdateSimInMyScheduler);
                } catch (Exception e2){
                    System.out.println(LogsT.printDate()+LogsId.id(imsi)+"Fail Scheduler.myScheduler() DBconnectNEW.executeQuery(strUpdateSimInMyScheduler)");
                    e2.printStackTrace();
                }
                try {
                    DBconnectNEW.executeQuery(strUpdateSimInSimcards);
                } catch (Exception e3){
                    System.out.println(LogsT.printDate()+LogsId.id(imsi)+"Fail Scheduler.myScheduler() DBconnectNEW.executeQuery(strUpdateSimInSimcards);");
                    e3.printStackTrace();
                }
                try {
                    Runnable r = new Sim(sim_name, imsi, line_name);
                    //Thread t = new Thread(r, "daemon");
                    Thread t = new Thread(r);
                    t.start();
                } catch (Exception e) {
                    System.out.println(LogsT.printDate() + "thread with imsi " + imsi + " don't start. Info for imsi no update");
                    e.printStackTrace();
                }
            }

            //count++;
            sleep(10000);
        }
    }

    private static void updateCountersInSimcards(long imsi){
        File path = new File("/opt/smssystem/python/update_data/");
        ProcessBuilder countersMin = new ProcessBuilder("python3", "refresh_counters_min.py", String.valueOf(imsi));
        ProcessBuilder countersHour = new ProcessBuilder("python3", "refresh_counters_hour.py", String.valueOf(imsi));
        ProcessBuilder countersDay = new ProcessBuilder("python3", "refresh_counters_day.py", String.valueOf(imsi));
        ProcessBuilder countersMonth = new ProcessBuilder("python3", "refresh_counters_month.py", String.valueOf(imsi));
        countersMin.directory(path);
        countersHour.directory(path);
        countersDay.directory(path);
        countersMonth.directory(path);
        System.out.println(LogsT.printDate() + LogsId.id(imsi) + "countersMin.toString(): " + countersMin.toString());
        System.out.println(LogsT.printDate() + LogsId.id(imsi) + "countersHour.toString(): " + countersHour.toString());
        System.out.println(LogsT.printDate() + LogsId.id(imsi) + "countersDay.toString(): " + countersDay.toString());
        System.out.println(LogsT.printDate() + LogsId.id(imsi) + "countersMonth.toString(): " + countersMonth.toString());
        try {
            Process pCountersMin = countersMin.start();
            System.out.println(LogsT.printDate() + "countersMin.command(): " + countersMin.command());
            System.out.println(LogsT.printDate() + "pCountersMin.toString(): " + pCountersMin.toString());
            System.out.println(LogsT.printDate() + "process refresh_counters_min.py is run");
        } catch (IOException ep) { ep.printStackTrace(); }
        try {
            Process pCountersHour = countersHour.start();
            System.out.println(LogsT.printDate() + "process refresh_counters_hour.py is run");
        } catch (IOException ep) { ep.printStackTrace(); }
        try {
            Process pCountersDay = countersDay.start();
            System.out.println(LogsT.printDate() + "process refresh_counters_day.py is run");
        } catch (IOException ep) { ep.printStackTrace(); }
        try {
            Process pCountersMonth = countersMonth.start();
            System.out.println(LogsT.printDate() + "process refresh_counters_month.py is run");
        } catch (IOException ep) { ep.printStackTrace(); }
    }

    private static void nullCheckActionInMyScheduler(){
        String query = "update smssystem.my_scheduler Set check_action='N'";
        try{
            DBconnectNEW.executeQuery(query);
            System.out.println(LogsT.printDate() + "OK. Scheduler.nullCheckActionInMyScheduler() done. Start rotation sims...");
        } catch (Exception e){
            System.out.println(LogsT.printDate()+"FAIL. Scheduler.nullCheckActionInMyScheduler()");
            e.printStackTrace();
        }
    }

    private static void nullOnairInSimcards(){
        String query = new StringBuilder().append("update smssystem.simcards Set onair='N' where corp in (").append(SIMCARDS_GROUPS).append(")").toString();
        try{
            DBconnectNEW.executeQuery(query);
            System.out.println(LogsT.printDate() + "OK. Scheduler.nullOnairInSimcards() done. Start rotation sims...");
        } catch (Exception e){
            System.out.println(LogsT.printDate()+"FAIL. Scheduler.nullOnairInSimcards()");
            e.printStackTrace();
        }
    }

}

