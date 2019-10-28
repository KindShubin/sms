package Sims;

import DB.DBconnectVPS;
import DB.GetVal;
import LogsParts.LogsId;
import LogsParts.LogsT;

import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.*;

import static java.lang.Thread.sleep;

public class Simcard {

    long imsi;
    String iccid;
    int id_simbank;
    int id_port_simbank;
    String onair;
    int prefix;
    String server;
    String ip;
    int port;
    String corp;
    String number;
    String status_info;
    String description;
    String block;
    String availability;
    int timeout;
    String allowsms;
    String allowsms_mts;
    String allowsms_ks;
    String allowsms_life;
    String allowsms_other;
    int count_permin;
    int count_perhour;
    int count_perday;
    int count_permonth;
    int count_permin_mts;
    int count_permin_ks;
    int count_permin_life;
    int count_permin_other;
    int count_perhour_mts;
    int count_perhour_ks;
    int count_perhour_life;
    int count_perhour_other;
    int count_perday_mts;
    int count_perday_ks;
    int count_perday_life;
    int count_perday_other;
    int count_permonth_mts;
    int count_permonth_ks;
    int count_permonth_life;
    int count_permonth_other;
    int check_sms;
    int check_sms_mts;
    int check_sms_ks;
    int check_sms_life;
    int check_sms_other;
    double balance;
    Timestamp balance_time_check;
    int package_sms;
    Timestamp package_sms_time_check;
    int report_unknown_hour;
    int report_unknown_day;
    int report_sent_hour;
    int report_sent_day;
    int report_d_hour;
    int report_d_day;

    TimeZone tz = TimeZone.getTimeZone("Europe/Kiev");
    Calendar balance_time_check_gc = GregorianCalendar.getInstance(tz);
    Calendar package_sms_time_check_gc = GregorianCalendar.getInstance(tz);

    public long getImsi() { return imsi; }
    public String getIccid() { return iccid; }
    public int getId_simbank() { return id_simbank; }
    public int getId_port_simbank() { return id_port_simbank; }
    public String getOnair() { return onair; }
    public int getPrefix() { return prefix; }
    public String getServer() { return server; }
    public String getIp() { return ip; }
    public int getPort() { return port; }
    public String getCorp() { return corp; }
    public String getNumber() { return number; }
    public String getStatus_info() { return status_info; }
    public String getDescription() { return description; }
    public String getBlock() { return block; }
    public String getAvailability() { return availability; }
    public int getTimeout() { return timeout; }
    public String getAllowsms() { return allowsms; }
    public String getAllowsms_mts() { return allowsms_mts; }
    public String getAllowsms_ks() { return allowsms_ks; }
    public String getAllowsms_life() { return allowsms_life; }
    public String getAllowsms_other() { return allowsms_other; }
    public int getCount_permin() { return count_permin; }
    public int getCount_perhour() { return count_perhour; }
    public int getCount_perday() { return count_perday; }
    public int getCount_permonth() { return count_permonth; }
    public int getCount_permin_mts() { return count_permin_mts; }
    public int getCount_permin_ks() { return count_permin_ks; }
    public int getCount_permin_life() { return count_permin_life; }
    public int getCount_permin_other() { return count_permin_other; }
    public int getCount_perhour_mts() { return count_perhour_mts; }
    public int getCount_perhour_ks() { return count_perhour_ks; }
    public int getCount_perhour_life() { return count_perhour_life; }
    public int getCount_perhour_other() { return count_perhour_other; }
    public int getCount_perday_mts() { return count_perday_mts; }
    public int getCount_perday_ks() { return count_perday_ks; }
    public int getCount_perday_life() { return count_perday_life; }
    public int getCount_perday_other() { return count_perday_other; }
    public int getCount_permonth_mts() { return count_permonth_mts; }
    public int getCount_permonth_ks() { return count_permonth_ks; }
    public int getCount_permonth_life() { return count_permonth_life; }
    public int getCount_permonth_other() { return count_permonth_other; }
    public int getCheck_sms() { return check_sms; }
    public int getCheck_sms_mts() { return check_sms_mts; }
    public int getCheck_sms_ks() { return check_sms_ks; }
    public int getCheck_sms_life() { return check_sms_life; }
    public int getCheck_sms_other() { return check_sms_other; }
    public double getBalance() { return balance; }
    public Timestamp getBalance_time_check() { return balance_time_check;}
    public int getPackage_sms() { return package_sms;}
    public Timestamp getPackage_sms_time_check() { return package_sms_time_check;}
    public int getReport_unknown_hour() { return report_unknown_hour; }
    public int getReport_unknown_day() { return report_unknown_day; }
    public int getReport_sent_hour() { return report_sent_hour; }
    public int getReport_sent_day() { return report_sent_day; }
    public int getReport_d_hour() { return report_d_hour; }
    public int getReport_d_day() { return report_d_day; }

    public Simcard(long imsi) throws SQLException {
        String query = new StringBuilder(200).append("select * from smssystem.simcards where imsi=").append(imsi).toString();
        ArrayList<HashMap> result = DBconnectVPS.getResultSet(query);
        getinfo(result.get(0));

    }

    private void getinfo(HashMap hm){
        try{
            this.imsi=GetVal.getLong(hm, "imsi");
        } catch (Exception a){
            System.out.println(LogsT.printDate()+"ERROR in getinfo. Don't get imsi from Hashmap");
            System.out.println(a.getStackTrace());
        }
        System.out.println(LogsT.printDate()+"try getinfo in Simcards from imsi:"+getImsi());
        try{
            //this.imsi=GetVal.getLong(hm, "imsi");
            this.iccid=GetVal.getStr(hm, "iccid");
            this.id_simbank=GetVal.getInt(hm, "id_simbank");
            this.id_port_simbank=GetVal.getInt(hm, "id_port_simbank");
            this.onair=GetVal.getStr(hm, "onair");
            this.prefix=GetVal.getInt(hm, "prefix");
            this.server=GetVal.getStr(hm, "server");
            this.ip=GetVal.getStr(hm, "ip");
            this.port=GetVal.getInt(hm, "port");
            this.corp=GetVal.getStr(hm, "corp");
            this.number=GetVal.getStr(hm, "number");
            this.status_info=GetVal.getStr(hm, "status_info");
            this.description=GetVal.getStr(hm, "description");
            this.block=GetVal.getStr(hm, "block");
            this.availability=GetVal.getStr(hm, "availability");
            this.timeout=GetVal.getInt(hm, "timeout");
            this.allowsms=GetVal.getStr(hm, "allowsms");
            this.allowsms_mts=GetVal.getStr(hm, "allowsms_mts");
            this.allowsms_ks=GetVal.getStr(hm, "allowsms_ks");
            this.allowsms_life=GetVal.getStr(hm, "allowsms_life");
            this.allowsms_other=GetVal.getStr(hm, "allowsms_other");
            this.count_permin=GetVal.getInt(hm, "count_permin");
            this.count_perhour=GetVal.getInt(hm, "count_perhour");
            this.count_perday=GetVal.getInt(hm, "count_perday");
            this.count_permonth=GetVal.getInt(hm, "count_permonth");
            this.count_permin_mts=GetVal.getInt(hm, "count_permin_mts");
            this.count_permin_ks=GetVal.getInt(hm, "count_permin_ks");
            this.count_permin_life=GetVal.getInt(hm, "count_permin_life");
            this.count_permin_other=GetVal.getInt(hm, "count_permin_other");
            this.count_perhour_mts=GetVal.getInt(hm, "count_perhour_mts");
            this.count_perhour_ks=GetVal.getInt(hm, "count_perhour_ks");
            this.count_perhour_life=GetVal.getInt(hm, "count_perhour_life");
            this.count_perhour_other=GetVal.getInt(hm, "count_perhour_other");
            this.count_perday_mts=GetVal.getInt(hm, "count_perday_mts");
            this.count_perday_ks=GetVal.getInt(hm, "count_perday_ks");
            this.count_perday_life=GetVal.getInt(hm, "count_perday_life");
            this.count_perday_other=GetVal.getInt(hm, "count_perday_other");
            this.count_permonth_mts=GetVal.getInt(hm, "count_permonth_mts");
            this.count_permonth_ks=GetVal.getInt(hm, "count_permonth_ks");
            this.count_permonth_life=GetVal.getInt(hm, "count_permonth_life");
            this.count_permonth_other=GetVal.getInt(hm, "count_permonth_other");
            this.check_sms=GetVal.getInt(hm, "check_sms");
            this.check_sms_mts=GetVal.getInt(hm, "check_sms_mts");
            this.check_sms_ks=GetVal.getInt(hm, "check_sms_ks");
            this.check_sms_life=GetVal.getInt(hm, "check_sms_life");
            this.check_sms_other=GetVal.getInt(hm, "check_sms_other");
            this.balance=GetVal.getDouble(hm, "balance");
            this.balance_time_check=GetVal.getTimeS(hm, "balance_time_check");
            this.package_sms=GetVal.getInt(hm, "package_sms");
            this.package_sms_time_check=GetVal.getTimeS(hm, "package_sms_time_check");
            this.report_unknown_hour=GetVal.getInt(hm, "report_unknown_hour");
            this.report_unknown_day=GetVal.getInt(hm, "report_unknown_day");
            this.report_sent_hour=GetVal.getInt(hm, "report_sent_hour");
            this.report_sent_day=GetVal.getInt(hm, "report_sent_day");
            this.report_d_hour=GetVal.getInt(hm, "report_d_hour");
            this.report_d_day=GetVal.getInt(hm, "report_d_day");
            try{
                System.out.println(LogsT.printDate() + "getinfo simcards: Timestamp --> Calendar. IMSI:"+getImsi());
                balance_time_check_gc.setLenient(false);
                if ( balance_time_check != null){
                    try { balance_time_check_gc.setTime(balance_time_check); } catch (NullPointerException n) {n.printStackTrace();}
                } else balance_time_check_gc=null;
                package_sms_time_check_gc.setLenient(false);
                if ( package_sms_time_check != null){
                    try { package_sms_time_check_gc.setTime(package_sms_time_check); } catch (NullPointerException n) {n.printStackTrace();}
                } else package_sms_time_check_gc=null;
            } catch (Exception b){
                System.out.println(LogsT.printDate()+"ERROR in block getinfo simcards Timestamp --> Calendar. IMSI:"+getImsi());
                System.out.println(b.getStackTrace());
            }
            System.out.println(LogsT.printDate()+"getinfo from simcards DONE. IMSI:"+getImsi());
        } catch (Exception c){
            System.out.println(LogsT.printDate()+"ERROR in block getinfo simcards Timestamp --> Calendar. IMSI:"+getImsi());
            System.out.println(c.getStackTrace());
        }
    }

/*    private void getinfo(String query) throws SQLException {
        DBconnectSelect db = new DBconnectSelect(query);
        ResultSet rs = db.getRs();
        while (rs.next()){
            this.imsi=rs.getLong("imsi");
            this.iccid=rs.getString("iccid");
            this.id_simbank=rs.getInt("id_simbank");
            this.id_port_simbank = rs.getInt("id_port_simbank");
            this.onair=rs.getString("onair");
            this.prefix=rs.getInt("prefix");
            this.server=rs.getString("server");
            this.ip=rs.getString("ip");
            this.port=rs.getInt("port");
            this.corp = rs.getString("corp");
            this.balance=rs.getString("balance");
            this.number=rs.getString("number");
            this.status_info=rs.getString("status_info");
            this.description=rs.getString("description");
            this.block=rs.getString("block");
            this.availability=rs.getString("availability");
            this.timeout = rs.getInt("timeout");
            this.allowsms = rs.getString("allowsms");
            this.allowsms_mts=rs.getString("allowsms_mts");
            this.allowsms_ks=rs.getString("allowsms_ks");
            this.allowsms_life=rs.getString("allowsms_life");
            this.allowsms_other=rs.getString("allowsms_other");
            this.count_permin = rs.getInt("count_permin");
            this.count_perhour=rs.getInt("count_perhour");
            this.count_perday=rs.getInt("count_perday");
            this.count_permonth=rs.getInt("count_permonth");
            this.count_permin_mts=rs.getInt("count_permin_mts");
            this.count_permin_ks=rs.getInt("count_permin_ks");
            this.count_permin_life=rs.getInt("count_permin_life");
            this.count_permin_other=rs.getInt("count_permin_other");
            this.count_perhour_mts=rs.getInt("count_perhour_mts");
            this.count_perhour_ks=rs.getInt("count_perhour_ks");
            this.count_perhour_life=rs.getInt("count_perhour_life");
            this.count_perhour_other=rs.getInt("count_perhour_other");
            this.count_perday_mts=rs.getInt("count_perday_mts");
            this.count_perday_ks=rs.getInt("count_perday_ks");
            this.count_perday_life=rs.getInt("count_perday_life");
            this.count_perday_other=rs.getInt("count_perday_other");
            this.count_permonth_mts=rs.getInt("count_permonth_mts");
            this.count_permonth_ks=rs.getInt("count_permonth_ks");
            this.count_permonth_life=rs.getInt("count_permonth_life");
            this.count_permonth_other=rs.getInt("count_permonth_other");
            this.check_sms_mts=rs.getInt("check_sms_mts");
            this.check_sms_ks=rs.getInt("check_sms_ks");
            this.check_sms_life=rs.getInt("check_sms_life");
            this.check_sms_other=rs.getInt("check_sms_other");
            this.report_unknown_hour=rs.getInt("report_unknown_hour");
            this.report_unknown_day=rs.getInt("report_unknown_day");
            this.report_sent_hour=rs.getInt("report_sent_hour");
            this.report_sent_day=rs.getInt("report_sent_day");
            this.report_d_hour=rs.getInt("report_d_hour");
            this.report_d_day=rs.getInt("report_d_day");
            db.closeConnectionWithRs();
        }
    }*/

    public int defineTimeout() throws SQLException {
        //String corp = simcard.getCorp();
        System.out.println(LogsT.printDate() + "simcard.getCorp(): " + this.corp);
        String getTimeout = new StringBuilder(300).append("select sp.timeout from smssystem.pools as sp where sp.corp='")
                .append(corp).append("'")
                .toString();
        System.out.println(LogsT.printDate() + "query getTimeout: " + getTimeout);
        //DBconnectSelect db = new DBconnectSelect(getTimeout);
        //db.getRs().first();
        ArrayList<HashMap> result = DBconnectVPS.getResultSet(getTimeout);
        int timeoutCheck = 0;
        try {
            timeoutCheck = GetVal.getInt(result.get(0), "timeout");
        } catch (Exception e){
            e.toString();
        }
        //db.closeConnectionWithRs();
        return timeoutCheck;
    }

    public int defineTimeout(Number idSms) throws SQLException {
        //String corp = simcard.getCorp();
        System.out.println(LogsT.printDate() + LogsId.id(idSms) + "simcard.getCorp(): " + this.corp);
        String getTimeout = new StringBuilder(300).append("select sp.timeout from smssystem.pools as sp where sp.corp='")
                .append(corp).append("'")
                .toString();
        System.out.println(LogsT.printDate() + LogsId.id(idSms) + "query getTimeout: " + getTimeout);
        //DBconnectSelect db = new DBconnectSelect(getTimeout);
        //db.getRs().first();
        ArrayList<HashMap> result = DBconnectVPS.getResultSet(getTimeout);
        int timeoutCheck = 0;
        try {
            timeoutCheck = GetVal.getInt(result.get(0), "timeout");
        } catch (Exception e){
            e.toString();
        }
        //db.closeConnectionWithRs();
        return timeoutCheck;
    }

    public void pause(int time, Number id) throws InterruptedException, SQLException {
        System.out.println(LogsT.printDate() + LogsId.id(id) + "start method pause for imsi:"+this.imsi+" corp:+"+this.corp+" prefix:"+this.prefix);
        sleep(time * 1000);
        enableSimcardAfterSend(this.getImsi());
        System.out.println(LogsT.printDate() + LogsId.id(id) + "simcard imsi:"+this.imsi+" corp:+"+this.corp+" prefix:"+this.prefix+ " now is available");
        System.out.println(LogsT.printDate() + LogsId.id(id) + "close method pause for imsi:"+this.imsi+" corp:+"+this.corp+" prefix:"+this.prefix);
    }

    public void disableSimcardBeforeSend(Simcard simcard, Number idSms) throws SQLException {
        System.out.println(LogsT.printDate() + LogsId.id(idSms) + "start method disableSimcardBeforeSend for imsi:"+simcard.imsi+" corp:+"+simcard.corp+" prefix:"+simcard.prefix);
        //DBconnect db = new DBconnect();
        String setUnavailableSimcard = new StringBuilder(400)
                //.append("update smssystem.simcards as ss Set ss.availability='N', ss.description='WAIT' where ss.imsi=")
                .append("update smssystem.simcards as ss Set ss.availability='N', ss.status_info='sending' where ss.imsi=")
                .append(simcard.imsi).toString();//off simcard on timeout peiod
        System.out.println(LogsT.printDate() + LogsId.id(idSms) + "query: "+setUnavailableSimcard);
        try{
            //db.getStmt().execute(setUnavailableSimcard);
            DBconnectVPS.executeQuery(setUnavailableSimcard);
        } catch (Exception e) {e.toString();}
        //db.closeConnection();
        System.out.println(LogsT.printDate() + LogsId.id(idSms) + "close disableSimcardBeforeSend for imsi:"+simcard.imsi+" corp:+"+simcard.corp+" prefix:"+simcard.prefix);
    }

    public void enableSimcardAfterSend(long imsi) throws SQLException {
        System.out.println(LogsT.printDate() + "start method enableSimcardAfterSend for imsi:"+imsi);
        //DBconnect db = new DBconnect();
        String setAvailableSimcard = new StringBuilder(400)
                .append("update smssystem.simcards as ss Set ss.availability='Y', ss.status_info='' where ss.imsi=")
                .append(imsi).toString();//on simcard
        System.out.println(LogsT.printDate() + "query: " + setAvailableSimcard);
        try {
            //db.getStmt().execute(setAvailableSimcard);
            DBconnectVPS.executeQuery(setAvailableSimcard);
        } catch (Exception e) {e.toString();}
        //db.closeConnection();
        System.out.println(LogsT.printDate() + "close enableSimcardAfterSend for imsi:"+imsi);
    }

    // повторение методов disableSimcardBeforeSend но на основании данныйх Simcards из класса
    public void disableSimcardBeforeSend(Number id) throws SQLException {
        System.out.println(LogsT.printDate() + LogsId.id(id) + "start method disableSimcardBeforeSend for imsi:"+getImsi()+" corp:+"+getCorp()+" prefix:"+getPrefix());
        String setUnavailableSimcard = new StringBuilder(400)
                //.append("update smssystem.simcards as ss Set ss.availability='N', ss.description='WAIT' where ss.imsi=")
                .append("update smssystem.simcards as ss Set ss.availability='N', ss.status_info='sending' where ss.imsi=")
                .append(getImsi()).toString();//off simcard on timeout peiod
        System.out.println(LogsT.printDate() + LogsId.id(id) + "query: "+setUnavailableSimcard);
        try{
            DBconnectVPS.executeQuery(setUnavailableSimcard);
        } catch (Exception e) {e.toString();}
        System.out.println(LogsT.printDate() + LogsId.id(id) + "close disableSimcardBeforeSend for imsi:"+getImsi()+" corp:+"+getCorp()+" prefix:"+getPrefix());
    }

// 2016.08.18 переход на imsi. Были неактивны
//    public void runAsynchronouslyDisableSimcard(final Simcard obj, Number id) {
//        System.out.println(LogsT.printDate() + LogsId.id(id) + "start runAsynchronouslyDisableSimcard. Prefix simcard:" + obj.getPrefix());
//        new Thread(new Runnable() {
//            @Override
//            public void run() {
//                try {
//                    obj.disableSimcardBeforeSend(obj.getPrefix(), id);
//                } catch (SQLException e) {
//                    e.printStackTrace();
//                }
//            }
//        }).start();
//        System.out.println(LogsT.printDate() + LogsId.id(id) + "close runAsynchronouslyDisableSimcard. Prefix simcard:" + obj.getPrefix());
//    }
//
//    public void runAsynchronouslyEnableSimcard(final Simcard obj) {
//        System.out.println(LogsT.printDate() + "start runAsynchronouslyEnableSimcard. Prefix simcard:" + obj.getPrefix());
//        new Thread(new Runnable() {
//            @Override
//            public void run() {
//                try {
//                    enableSimcardAfterSend(obj.getPrefix());
//                } catch (SQLException e) {
//                    e.printStackTrace();
//                }
//            }
//        }).start();
//        System.out.println(LogsT.printDate() + "close runAsynchronouslyEnableSimcard. Prefix simcard:" + obj.getPrefix());
//    }
//
//    public void runAsynchronouslyPause(final Simcard obj, int time, Number id) {
//        System.out.println(LogsT.printDate() + LogsId.id(id) + "start runAsynchronouslyPause. IMSI simcard:"+obj.getImsi()+" Corp:"+obj.getCorp()+" Prefix: "+obj.getPrefix()+" Time:"+time);
//        new Thread(new Runnable() {
//            @Override
//            public void run() {
//                try {
//                    pause(obj, time, id);
//                } catch (InterruptedException e) {
//                    e.printStackTrace();
//                } catch (SQLException e) {
//                    e.printStackTrace();
//                }
//            }
//        }).start();
//        System.out.println(LogsT.printDate() + LogsId.id(id) + "close runAsynchronouslyPause. IMSI simcard:"+obj.getImsi()+" Corp:"+obj.getCorp()+" Prefix: "+obj.getPrefix()+" Time:"+time);
//}

    public void updateSimcard() throws SQLException {
        //DBconnectUpdate dbu = new DBconnectUpdate();
        String update = new StringBuilder(300).append("update smssystem.simcards SET iccid='")
                .append(iccid).append("', id_simbank='")
                .append(id_simbank).append("', id_port_simbank='")
                .append(id_port_simbank).append("', onair='")
                .append(onair).append("', prefix='")
                .append(prefix).append("', server='")
                .append(server).append("', ip='")
                .append(ip).append("', port='")
                .append(port).append("', corp='")
                .append(corp).append("', description='")
                .append(description).append("', number='")
                .append(number).append("', block='")
                .append(block).append("', availability='")
                .append(availability).append("', timeout='")
                .append(timeout).append("', allowsms='")
                .append(allowsms).append("', allowsms_mts='")
                .append(allowsms_mts).append("', allowsms_ks='")
                .append(allowsms_ks).append("', allowsms_life='")
                .append(allowsms_life).append("', allowsms_other='")
                .append(allowsms_other).append("', count_permin='")
                .append(count_permin).append("', count_perhour='")
                .append(count_perhour).append("', count_perday='")
                .append(count_perday).append("', count_permonth='")
                .append(count_permonth).append("', count_permin_mts='")
                .append(count_permin_mts).append("', count_permin_ks='")
                .append(count_permin_ks).append("', count_permin_life='")
                .append(count_permin_life).append("', count_permin_other='")
                .append(count_permin_other).append("', count_perhour_mts='")
                .append(count_perhour_mts).append("', count_perhour_ks='")
                .append(count_perhour_ks).append("', count_perhour_life='")
                .append(count_perhour_life).append("', count_perhour_other='")
                .append(count_perhour_other).append("', count_perday_mts='")
                .append(count_perday_mts).append("', count_perday_ks='")
                .append(count_perday_ks).append("', count_perday_life='")
                .append(count_perday_life).append("', count_perday_other='")
                .append(count_perday_other).append("', count_permonth_mts='")
                .append(count_permonth_mts).append("', count_permonth_ks='")
                .append(count_permonth_ks).append("', count_permonth_life='")
                .append(count_permonth_life).append("', count_permonth_other='")
                .append(count_permonth_other).append("', check_sms='")
                .append(check_sms).append("', check_sms_mts='")
                .append(check_sms_mts).append("', check_sms_ks='")
                .append(check_sms_ks).append("', check_sms_life='")
                .append(check_sms_life).append("', check_sms_other='")
                .append(check_sms_other).append("', balance='")
                .append(balance).append("', package_sms='")
                .append(package_sms).append("', report_unknown_hour='")
                .append(report_unknown_hour).append("', report_unknown_day='")
                .append(report_unknown_day).append("', report_sent_hour='")
                .append(report_sent_hour).append("', report_sent_day='")
                .append(report_sent_day).append("', report_d_hour='")
                .append(report_d_hour).append("', report_d_day='")
                .append(report_d_day).append("' WHERE imsi='")
                .append(imsi).append("'").toString();
        //dbu.getStmt().execute(update);
        //dbu.closeConnection();
        try{
            DBconnectVPS.executeQuery(update);
        } catch (Exception e){
            System.out.println(LogsT.printDate() + "error update simcard: "+ e.toString());
        }
    }

    public void updateSimcardReports() throws SQLException {
        //DBconnectUpdate dbu = new DBconnectUpdate();
        String update = new StringBuilder(300).append("update smssystem.simcards SET report_unknown_hour='")
                .append(report_unknown_hour).append("', report_unknown_day='")
                .append(report_unknown_day).append("', report_sent_hour='")
                .append(report_sent_hour).append("', report_sent_day='")
                .append(report_sent_day).append("', report_d_hour='")
                .append(report_d_hour).append("', report_d_day='")
                .append(report_d_day).append("' WHERE imsi='")
                .append(imsi).append("'").toString();
        DBconnectVPS.executeQuery(update);
        //dbu.getStmt().execute(update);
        //dbu.closeConnection();
    }

}
