package Daemons;

import DB.DBconnectVPS;
import DB.GetVal;
import LogsParts.LogsId;
import LogsParts.LogsT;

import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;

import static java.lang.Thread.sleep;

public class Resend {

    public static void main(String[] args) throws SQLException, InterruptedException {

        try {
            daemonize();
        } catch (Throwable e) {
            System.err.println(LogsT.printDate() + "Startup failed. " + e.getMessage());
        } finally {
            System.out.println(LogsT.printDate() + "demonize started!");
        }

        mainResend();
    }

    static private void daemonize() throws Exception {
        try {
            System.in.close();
        } catch (Throwable e) {
            System.err.println(LogsT.printDate() + "Fail. System in not close" + e.getMessage());
        } finally {
            System.out.println(LogsT.printDate() + "OK. System in closed");
        }
        //System.out.close();
    }

    private static void mainResend() throws SQLException, InterruptedException {
        while (true) {
            System.out.println(LogsT.printDate() + "Resend.mainResend() begin method resendGuaranteedSend()");
            resendGuaranteedSend();
            System.out.println(LogsT.printDate() + "Resend.mainResend() begin method resendGuaranteedDelivered()");
            resendGuaranteedDelivered();
            System.out.println(LogsT.printDate() + "Resend.mainResend() begin method expiredStatus()");
            expiredStatus();
            sleep(60000);
        }
    }

    private static void resendGuaranteedSend() throws SQLException {
        //System.out.println(LogsT.printDate() + "start method resendGuaranteedSend()");
        //под выборку попадают клиенты с типом 3 и 5
        //убрал UNKNOWN. были проблемы
        //убрал 'sending', 'processing' были проблемы c циклической отправкой на неправильный номер и выводом из стороя всех карт BSG
        String query = "select ss.id, ss.status, ssi.imsi, ssi.attemps, ss.client_id, cl.resend_undeliv, ss.time_entry, ss.time_end, NOW() as now from smssystem.smslogs as ss  \n" +
                "left join smssystem.simcardsStatistics as ssi on ssi.idSMS=ss.id \n" +
                "join smssystem.clients as cl on cl.id=ss.client_id \n" +
                "where cl.type in (3, 5) and ss.total=1 and ss.status in ('UNDELIVERABLE') \n" +
                "and time(now()) between time_range_start and time_range_end \n" +
                "and (ss.time_entry < NOW() - INTERVAL cl.resend_undeliv MINUTE) and (ss.time_entry > NOW() - INTERVAL cl.ability_to_send MINUTE) and ((NOW()<ss.time_end) or ss.time_end is null) and (ss.time_send < NOW()- INTERVAL 30 MINUTE) \n" +
                "and (ss.time_entry>NOW() - INTERVAL 14 DAY)";//последнее условие для того чтобы не было поиска по всей базе а ограничивалосб 2мя неделями
        ArrayList<HashMap> result = DBconnectVPS.getResultSet(query);
        if (result.size()==0){
            System.out.println(LogsT.printDate() + "Resend.resendGuaranteedSend() Not available sms for resend");
        } else System.out.println(LogsT.printDate() + "Resend.resendGuaranteedSend() available "+ result.size() + "sms for resend!");
        for(HashMap rs : result) {
            long id = GetVal.getLong(rs, "id");
            String status = GetVal.getStr(rs, "status");
            long imsi = GetVal.getLong(rs, "imsi");
            int attemps = GetVal.getInt(rs, "attemps");
            int client_id = GetVal.getInt(rs, "client_id");
            int resend_undeliv = GetVal.getInt(rs, "resend_undeliv");
            Timestamp time_entry = GetVal.getTimeS(rs, "time_entry");
            Timestamp time_end = GetVal.getTimeS(rs, "time_end");
            Timestamp now = GetVal.getTimeS(rs, "now");
            System.out.println(LogsT.printDate() + LogsId.id(id) + "Resend.resendGuaranteedSend() sms_id:" + id + " time_entry:" + time_entry + " time_end:" + time_end + " now:" + now);
            //try{ System.out.println(LogsT.printDate() + "now.getTime(): "+now.getTime()+"time_end.getTime(): "+time_end.getTime()); } catch (NullPointerException n) {n.printStackTrace();}
            String description = new StringBuilder(300).append("resend sms. was imsi:").append(imsi).append(", attemps: ").append(attemps).toString();
            String update = new StringBuilder().append("update smssystem.smslogs Set status='WAIT', availability='Y', goip_id_sms=NULL, description='")
                    .append(description).append("', userfield='status was ").append(status).append("' where id=").append(id).toString();
            DBconnectVPS.executeQuery(update);
        }

    }

    private static void resendGuaranteedDelivered() throws SQLException {
        //System.out.println(LogsT.printDate() + "start method resendGuaranteedDelivered()");
        //под выборку попадают клиенты с типом 4 и 5
        String query = "select ss.id, ss.status, ssi.imsi, ssi.attemps, ss.client_id, cl.resend_sent, ss.time_entry, ss.time_end, NOW() as now from smssystem.smslogs as ss \n" +
                "left join smssystem.simcardsStatistics as ssi on ssi.idSMS=ss.id \n" +
                "join smssystem.clients as cl on cl.id=ss.client_id \n" +
                "where cl.type in (4, 5) and ss.total=1 and ss.status in ('SEND') \n" +
                "and time(now()) between time_range_start and time_range_end \n" +
                "and (ss.time_entry < NOW() - INTERVAL cl.resend_sent MINUTE) and (ss.time_entry > NOW() - INTERVAL cl.ability_to_send MINUTE) and ((NOW()<ss.time_end) or ss.time_end is null) and (ss.time_send < NOW()- INTERVAL 30 MINUTE) \n" +
                "and (ss.time_entry>NOW() - INTERVAL 14 DAY)";//последнее условие для того чтобы не было поиска по всей базе а ограничивалосб 2мя неделями
        ArrayList<HashMap> result = DBconnectVPS.getResultSet(query);
        if (result.size()==0){
            System.out.println(LogsT.printDate() + "Resend.resendGuaranteedDelivered() Not available sms for resend");
        } else System.out.println(LogsT.printDate() + "Resend.resendGuaranteedDelivered() available "+ result.size() + "sms for resend!");
        for (HashMap rs : result) {
            long id = GetVal.getLong(rs, "id");
            String status = GetVal.getStr(rs, "status");
            long imsi = GetVal.getLong(rs, "imsi");
            int attemps = GetVal.getInt(rs, "attemps");
            int client_id = GetVal.getInt(rs, "client_id");
            int resend_sent = GetVal.getInt(rs, "resend_sent");
            Timestamp time_entry = GetVal.getTimeS(rs, "time_entry");
            Timestamp time_end = GetVal.getTimeS(rs, "time_end");
            Timestamp now = GetVal.getTimeS(rs, "now");
            System.out.println(LogsT.printDate() + LogsId.id(id) + "Resend.resendGuaranteedDelivered():" + id + " time_entry:" + time_entry + " time_end:" + time_end + " now:" + now);
            //try{ System.out.println(LogsT.printDate() + "now.getTime(): "+now.getTime()+"time_end.getTime(): "+time_end.getTime()); } catch (NullPointerException n) {n.printStackTrace();}
            String description = new StringBuilder(300).append("resend sms. was imsi:").append(imsi).append(", attemps: ").append(attemps).toString();
            String update = new StringBuilder().append("update smssystem.smslogs Set status='WAIT', availability='Y', goip_id_sms=NULL, description='")
                    .append(description).append("', userfield='status was ").append(status).append("' where id=").append(id).toString();
            DBconnectVPS.executeQuery(update);
        }
    }

    private static void expiredStatus() throws SQLException {
        String query;
        ArrayList<HashMap> result;
        query = "select ss.id, ss.status, ssi.imsi, ssi.attemps, ss.client_id, ss.time_entry, ss.time_end, NOW() as now from smssystem.smslogs as ss \n" +
                "left join smssystem.simcardsStatistics as ssi on ssi.idSMS=ss.id \n" +
                "where ss.status in ('WAIT', 'sending', 'processing') and ss.time_end<now() and ss.time_end is not null and (ss.time_entry>NOW() - INTERVAL 14 DAY)";
        result = DBconnectVPS.getResultSet(query);
        if (result.size()==0){
            System.out.println(LogsT.printDate() + "Resend.expiredStatus() /now()>time_end/ Not available sms for expired status");
        } else System.out.println(LogsT.printDate() + "Resend.expiredStatus() /now()>time_end/ available "+ result.size() + "sms for change status to expired!");
        for(HashMap rs : result) {
            long id = GetVal.getLong(rs, "id");
            String status = GetVal.getStr(rs, "status");
            long imsi = GetVal.getLong(rs, "imsi");
            int attemps = GetVal.getInt(rs, "attemps");
            int client_id = GetVal.getInt(rs, "client_id");
            Timestamp time_entry = GetVal.getTimeS(rs, "time_entry");
            Timestamp time_end = GetVal.getTimeS(rs, "time_end");
            Timestamp now = GetVal.getTimeS(rs, "now");
            String description = "expired status. Life time sms is empty. time_end:" + time_end + "< now:" + now+" last imsi:"+imsi+" attemps:"+attemps;
            System.out.println(LogsT.printDate() + LogsId.id(id)+"Resend.expiredStatus() "+description);
            String update = new StringBuilder().append("update smssystem.smslogs Set status='EXPIRED', description='")
                    .append(description).append("', userfield='status was ").append(status).append("' where id=").append(id).toString();
            DBconnectVPS.executeQuery(update);
        }
        //
        query = "select ss.id, ss.status, ssi.imsi, ssi.attemps, ss.client_id, cl.resend_undeliv, ss.time_entry, ss.time_end, NOW() as now from smssystem.smslogs as ss \n" +
                "left join smssystem.simcardsStatistics as ssi on ssi.idSMS=ss.id \n" +
                "join smssystem.clients as cl on cl.id=ss.client_id \n" +
                "where cl.type in (2, 3) and ss.status in ('UNDELIVERABLE', 'sending', 'processing') and (ss.time_entry < NOW() - INTERVAL cl.ability_to_send MINUTE) and (ss.time_entry>NOW()-INTERVAL 14 DAY)";
        result = DBconnectVPS.getResultSet(query);
        if (result.size()==0){
            System.out.println(LogsT.printDate() + "Resend.expiredStatus() /client_type 2-3 not send/ Not available sms for expired status");
        } else System.out.println(LogsT.printDate() + "Resend.expiredStatus() /client_type 2-3 not send/ available "+ result.size() + "sms for change status to expired!");
        for(HashMap rs : result) {
            long id = GetVal.getLong(rs, "id");
            String status = GetVal.getStr(rs, "status");
            long imsi = GetVal.getLong(rs, "imsi");
            int attemps = GetVal.getInt(rs, "attemps");
            int client_id = GetVal.getInt(rs, "client_id");
            int resend_undeliv = GetVal.getInt(rs, "resend_undeliv");
            Timestamp time_entry = GetVal.getTimeS(rs, "time_entry");
            Timestamp time_end = GetVal.getTimeS(rs, "time_end");
            Timestamp now = GetVal.getTimeS(rs, "now");
            String description = "expired status. Time for get status SEND is empty. time_entry:" + time_entry + "; now:" + now+"; resend_undeliv="+resend_undeliv+"minut; last imsi:"+imsi+" attemps:"+attemps;
            System.out.println(LogsT.printDate() + LogsId.id(id)+"Resend.expiredStatus() "+description);
            String update = new StringBuilder().append("update smssystem.smslogs Set status='EXPIRED', description='")
                    .append(description).append("', userfield='status was ").append(status).append("' where id=").append(id).toString();
            DBconnectVPS.executeQuery(update);
        }
        //
        query = "select ss.id, ss.status, ssi.imsi, ssi.attemps, ss.client_id, cl.resend_sent, ss.time_entry, ss.time_end, NOW() as now from smssystem.smslogs as ss \n" +
                "left join smssystem.simcardsStatistics as ssi on ssi.idSMS=ss.id \n" +
                "join smssystem.clients as cl on cl.id=ss.client_id \n" +
                "where cl.type in (4, 5) and ss.status in ('UNDELIVERABLE', 'SEND', 'sending', 'processing') and (ss.time_entry < NOW() - INTERVAL cl.ability_to_send MINUTE) and (ss.time_entry>NOW()-INTERVAL 14 DAY)";
        result = DBconnectVPS.getResultSet(query);
        if (result.size()==0){
            System.out.println(LogsT.printDate() + "Resend.expiredStatus() /client_type 4-5 not delivered/ Not available sms for expired status");
        } else System.out.println(LogsT.printDate() + "Resend.expiredStatus() /client_type 4-5 not delivered/ available "+ result.size() + "sms for change status to expired!");
        for(HashMap rs : result) {
            long id = GetVal.getLong(rs, "id");
            String status = GetVal.getStr(rs, "status");
            long imsi = GetVal.getLong(rs, "imsi");
            int attemps = GetVal.getInt(rs, "attemps");
            int client_id = GetVal.getInt(rs, "client_id");
            int resend_sent = GetVal.getInt(rs, "resend_sent");
            Timestamp time_entry = GetVal.getTimeS(rs, "time_entry");
            Timestamp time_end = GetVal.getTimeS(rs, "time_end");
            Timestamp now = GetVal.getTimeS(rs, "now");
            String description = "expired status. Time for get status DELIVERED is empty. time_entry:" + time_entry + "; now:" + now+"; resend_sent="+resend_sent+"minut; last imsi:"+imsi+" attemps:"+attemps;
            System.out.println(LogsT.printDate() + LogsId.id(id)+"Resend.expiredStatus() "+description);
            String update = new StringBuilder().append("update smssystem.smslogs Set status='EXPIRED', description='")
                    .append(description).append("', userfield='status was ").append(status).append("' where id=").append(id).toString();
            DBconnectVPS.executeQuery(update);
        }
    }
}