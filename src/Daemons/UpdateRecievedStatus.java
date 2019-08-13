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

public class UpdateRecievedStatus {

    private final static long ID_DEFAULT=-999999999999L;
    private final static long UNIQID_DEFAULT=-999999999999L;
    private final static int QNTSMS_DEFAULT=-999;
    private final static int SMS_NO_DEFAULT=-999;

    public static void main(String[] args) throws SQLException, InterruptedException {

        try { daemonize(); }
        catch (Throwable e){
            System.err.println(LogsT.printDate() + "Startup failed. " + e.getMessage());
        }
        finally { System.out.println(LogsT.printDate() + "demonize started!"); }

        refreshStatus();
    }

    static private void daemonize() throws Exception{
        try { System.in.close(); }
        catch (Throwable e){
            System.err.println(LogsT.printDate() + "Fail. System in not close" + e.getMessage());
        }
        finally { System.out.println(LogsT.printDate() + "OK. System in closed"); }
        //System.out.close();
    }

    private static void refreshStatus() throws SQLException, InterruptedException {
        while (true) {
            //block SEND --> DELIVERED
            System.out.println("block SEND --> DELIVERED");
            String query_send = "select ss.id, ss.uniqid, ss.qntsms, gs.sms_no, gs.received from goip.sends as gs join smssystem.smslogs as ss on ss.goip_id_sms=gs.id " +
                    "where ss.availability = 'Y' and ss.status ='SEND' and time_entry > NOW() - INTERVAL 2 DAY";
            ArrayList<HashMap> result_send = DBconnectNEW.getResultSet(query_send);
            for (HashMap rs : result_send) {
                long id = ID_DEFAULT;
                long uniqid = UNIQID_DEFAULT;
                int qntsms= QNTSMS_DEFAULT;
                int sms_no= SMS_NO_DEFAULT;
                try{ id = GetVal.getLong(rs,"id");} catch (Exception e){ System.out.println(e); e.printStackTrace(); }
                try{ uniqid= GetVal.getLong(rs,"uniqid");} catch (Exception e){ System.out.println(e); e.printStackTrace();}
                try{ qntsms = GetVal.getInt(rs, "qntsms");} catch (Exception e){ System.out.println(e); e.printStackTrace();}
                try{ sms_no = GetVal.getInt(rs,"sms_no");} catch (Exception e){ System.out.println(e); e.printStackTrace();}
                int received=GetVal.getBool(rs,"received") ? 1 : 0;
                //System.out.println("recieved is "+received);
                if(received > 0) {
                    String statusSend = new StringBuilder().append("update smssystem.smslogs SET status='DELIVERED', time_delivered=NOW() where id=").append(id).toString();
                    String statusSendUniqid = new StringBuilder().append("update smssystem.smslogs SET status='DELIVERED', time_delivered=NOW() where uniqid=").append(uniqid).toString();
                    if (qntsms>1) DBconnectNEW.executeQuery(statusSendUniqid);
                    else DBconnectNEW.executeQuery(statusSend);
                }
            }
            //block UNDELIVERABLE --> SEND/DELIVERED
            System.out.println("block UNDELIVERABLE --> SEND/DELIVERED");
            String query_undel = "select ss.id, ss.uniqid, ss.qntsms, gs.sms_no, gs.received from goip.sends as gs join smssystem.smslogs as ss on ss.goip_id_sms=gs.id " +
                    "where ss.availability = 'Y' and ss.status = 'UNDELIVERABLE' and time_entry > NOW() - INTERVAL 2 DAY";
            ArrayList<HashMap> result_undel = DBconnectNEW.getResultSet(query_undel);
            for (HashMap rs : result_undel) {
                long id = ID_DEFAULT;
                long uniqid = UNIQID_DEFAULT;
                int qntsms= QNTSMS_DEFAULT;
                int sms_no= SMS_NO_DEFAULT;
                try{ id = GetVal.getLong(rs,"id");} catch (Exception e){ System.out.println(e); e.printStackTrace(); }
                try{ uniqid= GetVal.getLong(rs,"uniqid");} catch (Exception e){ System.out.println(e); e.printStackTrace();}
                try{ qntsms = GetVal.getInt(rs, "qntsms");} catch (Exception e){ System.out.println(e); e.printStackTrace();}
                try{ sms_no = GetVal.getInt(rs,"sms_no");} catch (Exception e){ System.out.println(e); e.printStackTrace();}
                int received=GetVal.getBool(rs,"received") ? 1 : 0;
                //System.out.println("recieved is "+received);
                if (sms_no >= 0 && received == 0) {
                    String statusSend = new StringBuilder().append("update smssystem.smslogs SET status='SEND' where id=").append(id).toString();
                    String statusSendUniqid = new StringBuilder().append("update smssystem.smslogs SET status='SEND' where uniqid=").append(uniqid).toString();
                    if (qntsms>1) DBconnectNEW.executeQuery(statusSendUniqid);
                    else DBconnectNEW.executeQuery(statusSend);
                }
                if(received > 0) {
                    String statusSend = new StringBuilder().append("update smssystem.smslogs SET status='DELIVERED', time_delivered=NOW() where id=").append(id).toString();
                    String statusSendUniqid = new StringBuilder().append("update smssystem.smslogs SET status='DELIVERED', time_delivered=NOW() where uniqid=").append(uniqid).toString();
                    if (qntsms>1) DBconnectNEW.executeQuery(statusSendUniqid);
                    else DBconnectNEW.executeQuery(statusSend);
                }
            }
            // block 'sending','WAIT','UNKNOWN' --> ...
            System.out.println("block 'sending','WAIT','UNKNOWN' --> ...");
            String query = "select ss.id, ss.uniqid, ss.qntsms, gs.sms_no, gs.received from goip.sends as gs join smssystem.smslogs as ss on ss.goip_id_sms=gs.id " +
                    "where ss.availability = 'Y' and ss.status in('sending','WAIT','UNKNOWN') and time_entry > NOW() - INTERVAL 2 DAY";
            ArrayList<HashMap> result = DBconnectNEW.getResultSet(query);
            for (HashMap rs : result) {
                long id = ID_DEFAULT;
                long uniqid = UNIQID_DEFAULT;
                int qntsms= QNTSMS_DEFAULT;
                int sms_no= SMS_NO_DEFAULT;
                try{ id = GetVal.getLong(rs,"id");} catch (Exception e){ System.out.println(e); e.printStackTrace(); }
                try{ uniqid= GetVal.getLong(rs,"uniqid");} catch (Exception e){ System.out.println(e); e.printStackTrace();}
                try{ qntsms = GetVal.getInt(rs, "qntsms");} catch (Exception e){ System.out.println(e); e.printStackTrace();}
                try{ sms_no = GetVal.getInt(rs,"sms_no");} catch (Exception e){ System.out.println(e); e.printStackTrace();}
                int received=GetVal.getBool(rs,"received") ? 1 : 0;
                //System.out.println("recieved is "+received);
                if (sms_no >= 0 && received == 0) {
                    String statusSend = new StringBuilder().append("update smssystem.smslogs SET status='SEND' where id=").append(id).toString();
                    String statusSendUniqid = new StringBuilder().append("update smssystem.smslogs SET status='SEND' where uniqid=").append(uniqid).toString();
                    if (qntsms>1) DBconnectNEW.executeQuery(statusSendUniqid);
                    else DBconnectNEW.executeQuery(statusSend);
                }
                else if(received > 0) {
                    String statusSend = new StringBuilder().append("update smssystem.smslogs SET status='DELIVERED', time_delivered=NOW() where id=").append(id).toString();
                    String statusSendUniqid = new StringBuilder().append("update smssystem.smslogs SET status='DELIVERED', time_delivered=NOW() where uniqid=").append(uniqid).toString();
                    if (qntsms>1) DBconnectNEW.executeQuery(statusSendUniqid);
                    else DBconnectNEW.executeQuery(statusSend);
                }
                else if(sms_no < 0) {
                    String statusSend = new StringBuilder().append("update smssystem.smslogs SET status='UNDELIVERABLE' where id=").append(id).toString();
                    String statusSendUniqid = new StringBuilder().append("update smssystem.smslogs SET status='UNDELIVERABLE' where uniqid=").append(uniqid).toString();
                    if (qntsms>1) DBconnectNEW.executeQuery(statusSendUniqid);
                    else DBconnectNEW.executeQuery(statusSend);
                }
                else {
                    String statusSend = new StringBuilder().append("update smssystem.smslogs SET status='UNKNOWN' where id=").append(id).toString();
                    String statusSendUniqid = new StringBuilder().append("update smssystem.smslogs SET status='UNKNOWN' where uniqid=").append(uniqid).toString();
                    if (qntsms>1) DBconnectNEW.executeQuery(statusSendUniqid);
                    else DBconnectNEW.executeQuery(statusSend);
                }
            }
            //finalReportsToBsg();
            sleep(3000);
        }
    }

    private static void finalReportsToBsg(){
        System.out.println(LogsT.printDate() + "started proceses finalReportsToBsg()");
        //System.out.println(LogsT.printDate() + "started process final_report_BSGstream_our_cards.py");
        ProcessBuilder bsg_stream_our_cards = new ProcessBuilder("python3", "final_report_BSG.py");
        bsg_stream_our_cards.directory(new File("/opt/smssystem/python/reports/"));
        try {
            Process p = bsg_stream_our_cards.start();
            //System.out.println(LogsT.printDate() + "process final_report_BSGstream_our_cards.py is run");
        } catch (IOException e) {
            e.printStackTrace();
        }
        //System.out.println(LogsT.printDate() + "started process final_report_BSGstream.py");
        ProcessBuilder bsg_stream = new ProcessBuilder("python3", "final_report_BSGstream.py");
        bsg_stream.directory(new File("/opt/smssystem/python/reports/"));
        try {
            Process p = bsg_stream.start();
            //System.out.println(LogsT.printDate() + "process final_report_BSGstream.py is run");
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println(LogsT.printDate() + "end proceses finalReportsToBsg()");
    }

}
