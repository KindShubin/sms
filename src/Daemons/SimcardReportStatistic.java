package Daemons;

import DB.DBconnectNEW;
import DB.GetVal;
import LogsParts.LogsId;
import LogsParts.LogsT;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;

import static java.lang.Math.toIntExact;
import static java.lang.Thread.sleep;

public class SimcardReportStatistic {

    static int countForUpdateStatitic=0;

    public static void main(String[] args) throws SQLException, InterruptedException {

        System.out.println(LogsT.printDate() + "Start SimcardReportStatistic");
        try { daemonize(); }
        catch (Throwable e){
            System.err.println(LogsT.printDate() + "Startup failed. " + e.getMessage());
        }
        finally { System.out.println(LogsT.printDate() + "demonize started!"); }

        run();
    }

    static private void daemonize() throws Exception{
        try { System.in.close(); }
        catch (Throwable e){
            System.err.println(LogsT.printDate() + "Fail. System in not close" + e.getMessage());
        }
        finally { System.out.println(LogsT.printDate() + "OK. System in closed"); }
        //System.out.close();
    }
    // refrash value report on 1 in smssystem.simcardsStatistics and update values in simcard statistics
    // if report was 3, then report_d_hour--, report_d_day-- and report_unknown_hour++, report_unknown_day++
    // if report was 2, then report_sent_hour--, report_sent_day-- and report_unknown_hour++, report_unknown_day++

//    id from smssystem.smslogs -- imsi -- prefix -- 0-3
//    0 - (not in statistic)
//    1 - undelivered and  -- default
//    2 - sent
//    3 - delivered
    private static void run() throws SQLException, InterruptedException {
        while (true){
            System.out.println(LogsT.printDate() + "refresh data to simcardsStatistics ...");
//            //2016.08.18 добавил таблицу smssystem.smsImsi
//            //2016.09.01 убрал весь блок inserta в smssystem.simcardsStatistics и удалил таблицу smssystem.smsImsi. Перенес все в HttpUrl
// 2018-10-04 убираю следующий раздел т.к. теперь по дефолту report=1
/*            String findNotSentSms = new StringBuilder("update smssystem.simcardsStatistics Set report=1, time=NOW() where report!=1 and idSMS in ")
                    .append("(select ss.id from smssystem.smslogs as ss join goip.sends as gs on gs.id=ss.goip_id_sms ")
                    .append("where datediff(now(),ss.time_entry)<2 and gs.time>=ss.time_entry and gs.over=0 or gs.sms_no<0)").toString();
            try{
                DBconnectNEW.executeQuery(findNotSentSms);
            } catch (Exception e){
                System.out.println(LogsT.printDate() + "Error update simcardsStatistics Set report=1 // non sent");
                e.printStackTrace();
            }
            */
            //System.out.println(LogsT.printDate() + "update simcardsStatistics Set report=2 // sent");
            String findSentSms = new StringBuilder().append("update smssystem.simcardsStatistics Set report=2, time=NOW() where report<2 and idSMS in ")
                    .append("(select ss.id from smssystem.smslogs as ss join goip.sends as gs on gs.id=ss.goip_id_sms ")
                    .append("where datediff(now(),ss.time_entry)<2 and gs.time>=ss.time_entry and gs.received=0 and gs.sms_no>=0)").toString();
            try{
                DBconnectNEW.executeQuery(findSentSms);
            } catch (Exception e){
                System.out.println(LogsT.printDate() + "Error update simcardsStatistics Set report=2 // sent");
                e.printStackTrace();
            }
            //System.out.println(LogsT.printDate() + "update simcardsStatistics Set report=3 // delivered");
            String findDeliveredSms = new StringBuilder().append("update smssystem.simcardsStatistics Set report=3, time=NOW() where report!=3 and idSMS in ")
                    .append("(select ss.id from smssystem.smslogs as ss join goip.sends as gs on gs.id=ss.goip_id_sms ")
                    .append("where datediff(now(),ss.time_entry)<2 and gs.time>=ss.time_entry and gs.received=1 and gs.sms_no>=0)").toString();
            try{
                DBconnectNEW.executeQuery(findDeliveredSms);
            } catch (Exception e){
                System.out.println(LogsT.printDate() + "Error update simcardsStatistics Set report=3 // delivered");
                e.printStackTrace();
            }
            System.out.println(LogsT.printDate() + "|Daemons.SimcardReportStatistic| refresh data to simcardsStatistics is done!");
//////////////////////////////////////////////////////////////
            if (countForUpdateStatitic>5){
                countForUpdateStatitic=0;
                // select works simcards per 24hours:
                String currentImsis24h = "SELECT imsi FROM smssystem.simcardsStatistics where time>date_sub(now(), INTERVAL 1 DAY) group by imsi";
                ArrayList<HashMap> result24h = DBconnectNEW.getResultSet(currentImsis24h);
                for (HashMap rs : result24h){
                    long imsi = GetVal.getLong(rs,"imsi");
                    //System.out.println(LogsT.printDate() + "update statistic for imsi "+imsi);
                    updateStatisticsPer24h(imsi);
                }
                // select works simcards per day:
                String currentImsisDay = "SELECT imsi FROM smssystem.simcardsStatistics where TO_DAYS(NOW())-TO_DAYS(time)=0 group by imsi";
                ArrayList<HashMap> resultDay = DBconnectNEW.getResultSet(currentImsisDay);
                for (HashMap rs : resultDay){
                    long imsi = GetVal.getLong(rs,"imsi");
                    //System.out.println(LogsT.printDate() + "update statistic for imsi "+imsi);
                    updateStatisticsPerDay(imsi);
                }
                // select works simcards per 1hours:
                String currentImsis1h = "SELECT imsi FROM smssystem.simcardsStatistics where time>date_sub(now(), INTERVAL 1 HOUR) group by imsi";
                ArrayList<HashMap> result1h = DBconnectNEW.getResultSet(currentImsis1h);
                for (HashMap rs : result1h){
                    long imsi = GetVal.getLong(rs,"imsi");
                    //System.out.println(LogsT.printDate() + "update statistic for imsi "+imsi);
                    updateStatisticsPerHour(imsi);
                }
                // select and update works simcards in 00:00
                String steSelectDiffSec0000="select UNIX_TIMESTAMP(now()) - UNIX_TIMESTAMP((select time_entry from smssystem.smslogs where status='WAIT time period' order by id desc limit 1)) as sec";
                try{
                    ArrayList<HashMap> res = DBconnectNEW.getResultSet(steSelectDiffSec0000);
                    int resSec=GetVal.getInt(res.get(0), "sec");
                    System.out.println(LogsT.printDate() + "resSec="+resSec+" //sec between 00:00 and now(). Need <60");
                    if (resSec<310){
                        String strUpdateReportsDay="update smssystem.simcards as ssim Set ssim.report_unknown_day=0, ssim.report_sent_day=0, ssim.report_d_day=0";
                        try{
                            DBconnectNEW.executeQuery(strUpdateReportsDay);
                            System.out.println(LogsT.printDate() + "DONE update all simcards report_unknown_day=0, report_sent_day=0, report_d_day=0");
                        } catch (Exception e){
                            System.out.println(LogsT.printDate() + "FAIL update all simcards report_unknown_day=0, report_sent_day=0, report_d_day=0");
                            e.printStackTrace();
                        }
                    }
                } catch (Exception u){
                    System.out.println(LogsT.printDate()+" Error select resSec, seconds between 00:00 and now()");
                    u.printStackTrace();
                }
                // select and update statistics to zero for other non work simcards per day
                //System.out.println(LogsT.printDate() + "reset statistics non worked simcards per day and per hour");
                String updateNonWorkImsis = new StringBuilder(1000).append("update smssystem.simcards Set report_unknown_hour='0', report_sent_hour='0', report_d_hour='0', ")
                        .append("report_unknown_day='0', report_sent_day='0', report_d_day='0' where imsi in (SELECT preSelect.imsi from (")
                        .append("SELECT ss.imsi FROM smssystem.simcards as ss left join smssystem.simcardsStatistics as sss on ss.imsi=sss.imsi and sss.time>date_sub(now(), INTERVAL 1 DAY) ")
                        .append("where sss.imsi is null) as preSelect)").toString();
                DBconnectNEW.executeQuery(updateNonWorkImsis);
                System.out.println(LogsT.printDate() + "|Daemons.SimcardReportStatistic| refresh data undeliv/send/deliv for simcard Done!");
            }
            countForUpdateStatitic+=1;
            System.out.println(LogsT.printDate() + "|Daemons.SimcardReportStatistic| countForUpdateStatitic:" + countForUpdateStatitic+" --sleep 30sec");
            sleep(30000);
        }
    }

    private static void updateStatisticsPerHour(long imsi) throws SQLException {
        int qntNonSent=0;
        int qntSent=0;
        int qntDelivered=0;
        String dataStatImsiPerHour = new StringBuilder(400).append("SELECT report, count(report) as qnt FROM smssystem.simcardsStatistics where time>date_sub(now(), INTERVAL 1 HOUR) and imsi='")
                .append(imsi).append("' group by report").toString();
        ArrayList<HashMap> result = DBconnectNEW.getResultSet(dataStatImsiPerHour);
        for (HashMap rs : result){
            int report = GetVal.getInt(rs,"report");
            int qnt = toIntExact(GetVal.getLong(rs, "qnt"));
            //System.out.println(LogsT.printDate() + "updateStatisticsPerHour IMSI:"+imsi+", report:"+report+", qnt:"+qnt);
            switch (report){
                case 0:
                    qntNonSent+=qnt;
                    break;
                case 1:
                    qntNonSent+=qnt;
                    break;
                case 2:
                    qntSent+=qnt;
                    break;
                case 3:
                    qntDelivered+=qnt;
                    break;
                default:
                    System.out.println(LogsT.printDate() + "Error in SimcardReportStatistic. For IMSI:"+imsi+" not valid report:"+report+" Qnt for this report:"+qnt);
            }
        }
        //resetStatisticPerHour(imsi);
        //System.out.println(LogsT.printDate() + LogsId.idImsi(imsi)+"qntNonSent:"+qntNonSent+" qntSent:" + qntSent + " qntDelivered:" + qntDelivered);
        updateSimcardPerHour(imsi, qntNonSent, qntSent, qntDelivered);
    }

    private static void updateStatisticsPerDay(long imsi) throws SQLException {
        int qntNonSent=0;
        int qntSent=0;
        int qntDelivered=0;
        String dataStatImsiPer24h = new StringBuilder(400).append("SELECT report, count(report) as qnt FROM smssystem.simcardsStatistics where TO_DAYS(NOW())-TO_DAYS(time)=0 and imsi='")
                .append(imsi).append("' group by report").toString();
        ArrayList<HashMap> result = DBconnectNEW.getResultSet(dataStatImsiPer24h);
        for (HashMap rs : result){
            int report = GetVal.getInt(rs, "report");
            int qnt = toIntExact(GetVal.getLong(rs, "qnt"));
            //System.out.println(LogsT.printDate() + "updateStatisticsPerDay IMSI:"+imsi+", report:"+report+", qnt:"+qnt);
            switch (report){
                case 0 :
                    qntNonSent+=qnt;
                    break;
                case 1:
                    qntNonSent+=qnt;
                    break;
                case 2:
                    qntSent+=qnt;
                    break;
                case 3:
                    qntDelivered+=qnt;
                    break;
                default:
                    System.out.println(LogsT.printDate() + "Error in SimcardReportStatistic. For IMSI:"+imsi+" not valid report:"+report+" Qnt for this report:"+qnt);
            }
        }
        //resetStatisticPerDay(imsi);
        //System.out.println(LogsT.printDate() + LogsId.idImsi(imsi)+"qntNonSent:"+qntNonSent+" qntSent:" + qntSent + " qntDelivered:" + qntDelivered);
        updateSimcardPerDay(imsi, qntNonSent, qntSent, qntDelivered);
    }

    private static void updateStatisticsPer24h(long imsi) throws SQLException {
        int qntNonSent=0;
        int qntSent=0;
        int qntDelivered=0;
        String dataStatImsiPer24h = new StringBuilder(400).append("SELECT report, count(report) as qnt FROM smssystem.simcardsStatistics where time>date_sub(now(), INTERVAL 1 DAY) and imsi='")
                .append(imsi).append("' group by report").toString();
        ArrayList<HashMap> result = DBconnectNEW.getResultSet(dataStatImsiPer24h);
        for (HashMap rs : result){
            int report = GetVal.getInt(rs, "report");
            int qnt = toIntExact(GetVal.getLong(rs, "qnt"));
            //System.out.println(LogsT.printDate() + "updateStatisticsPerDay IMSI:"+imsi+", report:"+report+", qnt:"+qnt);
            switch (report){
                case 0 :
                    qntNonSent+=qnt;
                    break;
                case 1:
                    qntNonSent+=qnt;
                    break;
                case 2:
                    qntSent+=qnt;
                    break;
                case 3:
                    qntDelivered+=qnt;
                    break;
                default:
                    System.out.println(LogsT.printDate() + "Error in SimcardReportStatistic. For IMSI:"+imsi+" not valid report:"+report+" Qnt for this report:"+qnt);
            }
        }
        //resetStatisticPer24h(imsi);
        //System.out.println(LogsT.printDate() + LogsId.idImsi(imsi)+"qntNonSent:"+qntNonSent+" qntSent:" + qntSent + " qntDelivered:" + qntDelivered);
        updateSimcardPer24h(imsi, qntNonSent, qntSent, qntDelivered);
    }

    private static void updateSimcardPerHour(long imsi, int qntNonSent, int qntSent, int qntDelivered) throws SQLException {
        //System.out.println(LogsT.printDate() + "updateSimcardsPerHour start. value:"+value);
        String update = new StringBuilder(600).append("update smssystem.simcards Set report_unknown_hour='")
                .append(qntNonSent).append("', report_sent_hour='").append(qntSent).append("', report_d_hour='")
                .append(qntDelivered).append("' where imsi='").append(imsi).append("'").toString();
        try{
            DBconnectNEW.executeQuery(update);
            System.out.println(LogsT.printDate() + "DONE updateSimcardsPerHour(imsi "+imsi+")");
        } catch (Exception e){
            System.out.println(LogsT.printDate() + "FAIL updateSimcardsPerHour() update:"+update);
            e.printStackTrace();
        }
    }

    private static void updateSimcardPerDay(long imsi, int qntNonSent, int qntSent, int qntDelivered) throws SQLException {
        //System.out.println(LogsT.printDate() + "updateSimcardsPerDay start. value:"+value);
        String update = new StringBuilder(600).append("update smssystem.simcards Set report_unknown_day='")
                .append(qntNonSent).append("', report_sent_day='").append(qntSent).append("', report_d_day='")
                .append(qntDelivered).append("' where imsi='").append(imsi).append("'").toString();
        try{
            DBconnectNEW.executeQuery(update);
            System.out.println(LogsT.printDate() + "DONE updateSimcardsPerDay(imsi "+imsi+")");
        } catch (Exception e){
            System.out.println(LogsT.printDate() + "FAIL updateSimcardsPerDay() update:"+update);
            e.printStackTrace();
        }
    }

    private static void updateSimcardPer24h(long imsi, int qntNonSent, int qntSent, int qntDelivered) throws SQLException {
        //System.out.println(LogsT.printDate() + "updateSimcardsPer24h start. value:"+value);
        String update = new StringBuilder(600).append("update smssystem.simcards Set report_unknown_24='")
                .append(qntNonSent).append("', report_sent_24='").append(qntSent).append("', report_d_24='")
                .append(qntDelivered).append("' where imsi='").append(imsi).append("'").toString();
        try{
            DBconnectNEW.executeQuery(update);
            System.out.println(LogsT.printDate() + "DONE updateSimcardsPer24h(imsi "+imsi+")");
        } catch (Exception e){
            System.out.println(LogsT.printDate() + "FAIL updateSimcardsPer24h() update:"+update);
            e.printStackTrace();
        }
    }

//////////////////////////////////////////
///// изменения для всех imsi разои. Хз нахер оно нужно...
    private static void updateSimcardsNotSentPerDay(int value) throws SQLException {
        //System.out.println(LogsT.printDate() + "updateSimcardsNotSentPerDay start. value:" + value);
        String update = new StringBuilder(300).append("update smssystem.simcards Set report_unknown_day='")
                .append(value).append("'").toString();
        DBconnectNEW.executeQuery(update);
    }

    private static void updateSimcardsNotSentPer24h(int value) throws SQLException {
        //System.out.println(LogsT.printDate() + "updateSimcardsNotSentPerDay start. value:" + value);
        String update = new StringBuilder(300).append("update smssystem.simcards Set report_unknown_24='")
                .append(value).append("'").toString();
        DBconnectNEW.executeQuery(update);
    }

    private static void updateSimcardsSentPerHour(int value) throws SQLException {
        //System.out.println(LogsT.printDate() + "updateSimcardsSentPerHour start. value:" + value);
        String update = new StringBuilder(300).append("update smssystem.simcards Set report_sent_hour='")
                .append(value).append("'").toString();
        DBconnectNEW.executeQuery(update);
    }

    private static void updateSimcardsSentPerDay(int value) throws SQLException {
        //System.out.println(LogsT.printDate() + "updateSimcardsSentPerDay start. value:" + value);
        String update = new StringBuilder(300).append("update smssystem.simcards Set report_sent_day='")
                .append(value).append("'").toString();
        DBconnectNEW.executeQuery(update);
    }

    private static void updateSimcardsSentPer24h(int value) throws SQLException {
        //System.out.println(LogsT.printDate() + "updateSimcardsSentPerDay start. value:" + value);
        String update = new StringBuilder(300).append("update smssystem.simcards Set report_sent_24='")
                .append(value).append("'").toString();
        DBconnectNEW.executeQuery(update);
    }

    private static void updateSimcardsDeliveredPerHour(int value) throws SQLException {
        //System.out.println(LogsT.printDate() + "updateSimcardsDeliveredPerHour start. value:" + value);
        String update = new StringBuilder(300).append("update smssystem.simcards Set report_d_hour='")
                .append(value).append("'").toString();
        DBconnectNEW.executeQuery(update);
    }

    private static void updateSimcardsDeliveredPerDay(int value) throws SQLException {
        //System.out.println(LogsT.printDate() + "updateSimcardsDeliveredPerDay start. value:" + value);
        String update = new StringBuilder(300).append("update smssystem.simcards Set report_d_day='")
                .append(value).append("'").toString();
        DBconnectNEW.executeQuery(update);
    }

    private static void updateSimcardsDeliveredPer24h(int value) throws SQLException {
        //System.out.println(LogsT.printDate() + "updateSimcardsDeliveredPerDay start. value:" + value);
        String update = new StringBuilder(300).append("update smssystem.simcards Set report_d_24='")
                .append(value).append("'").toString();
        DBconnectNEW.executeQuery(update);
    }
//////////////////////////////////
//// изменения значения для определенного imsi. Ранее использовал
    private static void updateSimcardsNotSentPerHour(long imsi, int value) throws SQLException {
        //System.out.println(LogsT.printDate() + "updateSimcardsNotSentPerHour start. Prefix:"+prefix+"; value:"+value);
        String update = new StringBuilder(300).append("update smssystem.simcards Set report_unknown_hour='")
            .append(value).append("' where imsi=").append(imsi).toString();
        DBconnectNEW.executeQuery(update);
        //System.out.println(LogsT.printDate() + "updateSimcardsNotSentPerHour end. IMSI:" + imsi + "; value:" + value);
    }

    private static void updateSimcardsNotSentPerDay(long imsi, int value) throws SQLException {
        //System.out.println(LogsT.printDate() + "updateSimcardsNotSentPerDay start. Prefix:" + prefix + "; value:" + value);
        String update = new StringBuilder(300).append("update smssystem.simcards Set report_unknown_day='")
                .append(value).append("' where imsi=").append(imsi).toString();
        DBconnectNEW.executeQuery(update);
        //System.out.println(LogsT.printDate() + "updateSimcardsNotSentPerDay end. IMSI:" + imsi + "; value:" + value);
    }

    private static void updateSimcardsNotSentPer24h(long imsi, int value) throws SQLException {
        //System.out.println(LogsT.printDate() + "updateSimcardsNotSentPerDay start. Prefix:" + prefix + "; value:" + value);
        String update = new StringBuilder(300).append("update smssystem.simcards Set report_unknown_24='")
                .append(value).append("' where imsi=").append(imsi).toString();
        DBconnectNEW.executeQuery(update);
        //System.out.println(LogsT.printDate() + "updateSimcardsNotSentPerDay end. IMSI:" + imsi + "; value:" + value);
    }

    private static void updateSimcardsSentPerHour(long imsi, int value) throws SQLException {
        //System.out.println(LogsT.printDate() + "updateSimcardsSentPerHour start. Prefix:" + prefix + "; value:" + value);
        String update = new StringBuilder(300).append("update smssystem.simcards Set report_sent_hour='")
                .append(value).append("' where imsi=").append(imsi).toString();
        DBconnectNEW.executeQuery(update);
        //System.out.println(LogsT.printDate() + "updateSimcardsSentPerHour end. IMSI:" + imsi + "; value:" + value);
    }

    private static void updateSimcardsSentPerDay(long imsi, int value) throws SQLException {
        //System.out.println(LogsT.printDate() + "updateSimcardsSentPerDay start. Prefix:" + prefix + "; value:" + value);
        String update = new StringBuilder(300).append("update smssystem.simcards Set report_sent_day='")
                .append(value).append("' where imsi=").append(imsi).toString();
        DBconnectNEW.executeQuery(update);
        //System.out.println(LogsT.printDate() + "updateSimcardsSentPerDay end. IMSI:" + imsi + "; value:" + value);
    }

    private static void updateSimcardsSentPer24h(long imsi, int value) throws SQLException {
        //System.out.println(LogsT.printDate() + "updateSimcardsSentPerDay start. Prefix:" + prefix + "; value:" + value);
        String update = new StringBuilder(300).append("update smssystem.simcards Set report_sent_24='")
                .append(value).append("' where imsi=").append(imsi).toString();
        DBconnectNEW.executeQuery(update);
        //System.out.println(LogsT.printDate() + "updateSimcardsSentPerDay end. IMSI:" + imsi + "; value:" + value);
    }

    private static void updateSimcardsDeliveredPerHour(long imsi, int value) throws SQLException {
        //System.out.println(LogsT.printDate() + "updateSimcardsDeliveredPerHour start. Prefix:" + prefix + "; value:" + value);
        String update = new StringBuilder(300).append("update smssystem.simcards Set report_d_hour='")
                .append(value).append("' where imsi=").append(imsi).toString();
        DBconnectNEW.executeQuery(update);
        //System.out.println(LogsT.printDate() + "updateSimcardsDeliveredPerHour end. IMSI:" + imsi + "; value:" + value);
    }

    private static void updateSimcardsDeliveredPerDay(long imsi, int value) throws SQLException {
        //System.out.println(LogsT.printDate() + "updateSimcardsDeliveredPerDay start. Prefix:" + prefix + "; value:" + value);
        String update = new StringBuilder(300).append("update smssystem.simcards Set report_d_day='")
                .append(value).append("' where imsi=").append(imsi).toString();
        DBconnectNEW.executeQuery(update);
        //System.out.println(LogsT.printDate() + "updateSimcardsDeliveredPerDay end. IMSI:" + imsi + "; value:" + value);
    }

    private static void updateSimcardsDeliveredPer24h(long imsi, int value) throws SQLException {
        //System.out.println(LogsT.printDate() + "updateSimcardsDeliveredPerDay start. Prefix:" + prefix + "; value:" + value);
        String update = new StringBuilder(300).append("update smssystem.simcards Set report_d_24='")
                .append(value).append("' where imsi=").append(imsi).toString();
        DBconnectNEW.executeQuery(update);
        //System.out.println(LogsT.printDate() + "updateSimcardsDeliveredPerDay end. IMSI:" + imsi + "; value:" + value);
    }
/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
//// обнуление значений reports для imsi перед обработкой новых данных
    private static void resetStatisticPerHour(long imsi) throws SQLException {
        String update = new StringBuilder().append("update smssystem.simcards Set report_unknown_hour='0', report_sent_hour='0', report_d_hour='0' where imsi=").append(imsi).toString();
        try{
            DBconnectNEW.executeQuery(update);
        } catch (Exception e){
            System.out.println(LogsT.printDate() + LogsId.idImsi(imsi)+"Error resetStatisticPerHour()");
            e.printStackTrace();
        }
        //System.out.println(LogsT.printDate() + "resetStatisticPerHour end. IMSI:" + imsi);
    }

    private static void resetStatisticPerDay(long imsi) throws SQLException {
        String update = new StringBuilder().append("update smssystem.simcards Set report_unknown_day='0', report_sent_day='0', report_d_day='0' where imsi=").append(imsi).toString();
        try{
            DBconnectNEW.executeQuery(update);
        } catch (Exception e){
            System.out.println(LogsT.printDate() + LogsId.idImsi(imsi)+"Error resetStatisticPerDay()");
            e.printStackTrace();
        }
        //System.out.println(LogsT.printDate() + "resetStatisticPerDay end. IMSI:" + imsi);
    }

    private static void resetStatisticPer24h(long imsi) throws SQLException {
        String update = new StringBuilder().append("update smssystem.simcards Set report_unknown_24='0', report_sent_24='0', report_d_24='0' where imsi=").append(imsi).toString();
        try{
            DBconnectNEW.executeQuery(update);
        } catch (Exception e){
            System.out.println(LogsT.printDate() + LogsId.idImsi(imsi)+"Error resetStatisticPer24h()");
            e.printStackTrace();
        }
        //System.out.println(LogsT.printDate() + "resetStatisticPerDay end. IMSI:" + imsi);
    }

    // refrash value report on 1 in smssystem.simcardsStatistics and update values in simcard statistics
    // if report was 3, then report_d_hour--, report_d_day-- and report_unknown_hour++, report_unknown_day++
    // if report was 2, then report_sent_hour--, report_sent_day-- and report_unknown_hour++, report_unknown_day++

}
