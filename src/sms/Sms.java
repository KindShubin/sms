package sms;

import DB.DBconnectVPS;
import DB.GetVal;
import Exceptions.MyNullPointerEx;
import LogsParts.LogsId;
import LogsParts.LogsT;

import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Sms {

    private static final Pattern PATTERN_MTS = Pattern.compile("^(\\+380|380|80|0?)(50|66|95|99)[0-9]{7}$");
    private static final Pattern PATTERN_KS = Pattern.compile("^(\\+380|380|80|0?)(39|67|68|9[6-8])[0-9]{7}$");
    private static final Pattern PATTERN_LIFE = Pattern.compile("^(\\+380|380|80|0?)[679]3[0-9]{7}$");
    private static final Pattern PATTERN_OTHER = Pattern.compile("^(\\+380|380|80|0?)9[124][0-9]{7}$");

    private ArrayList<HashMap> result;
    private Operators operator;
//    private ResultSet rs = null;

    long id;
    long uniqid;
    String availability;
    int part;
    int total;
    int qntsms;
    int client_id;
    int provider_id = 0;
    int prioritet;
    String text;
    int dst_num;
    int src_num;
    String status;
    Timestamp time_entry;
    Timestamp time_begin;
    Timestamp time_end;
    Timestamp time_send;
    Timestamp time_delivered;
    int timeout_set;
    int time_counter;
    String userfield;
    String description;
    long id_sms_client;
    long uniqid_sms_client;
    int goip_id_sms;
    int report;
    String client_provider_id;

    TimeZone tz = TimeZone.getTimeZone("Europe/Kiev");
    Calendar time_entry_gc = GregorianCalendar.getInstance(tz);
    Calendar time_begin_gc = GregorianCalendar.getInstance(tz);
    Calendar time_end_gc = GregorianCalendar.getInstance(tz);
    Calendar time_send_gc = GregorianCalendar.getInstance(tz);

    public Calendar getTime_entry_gc() { return time_entry_gc; }

    public Calendar getTime_begin_gc() {
        return time_begin_gc;
    }

    public Calendar getTime_end_gc() {
        return time_end_gc;
    }

    public Calendar getTime_send_gc() {
        return time_send_gc;
    }

    public long getId() { return id; }

    public long getUniqid() { return uniqid; }

    public String getAvailability() { return availability; }

    public int getPart() { return part; }

    public int getTotal() { return total; }

    public int getQntsms() { return qntsms; }

    public int getClient_id() { return client_id; }

    public int getProvider_id() { return provider_id; }

    public int getPrioritet() { return prioritet; }

    public String getText() { return text; }

    public int getDst_num() { return dst_num; }

    public int getSrc_num() { return src_num; }

    public String getStatus() { return status; }

    public Timestamp getTime_entry() { return time_entry; }

    public Timestamp getTime_begin() { return time_begin; }

    public Timestamp getTime_end() { return time_end; }

    public Timestamp getTime_send() { return time_send; }

    public Timestamp getTime_delivered() { return time_delivered;}

    public int getTimeout_set() { return timeout_set; }

    public int getTime_counter() { return time_counter; }

    public String getUserfield() { return userfield; }

    public Operators getOperator() { return operator; }

    public String getDescription() { return description; }

    public long getId_sms_client() { return id_sms_client; }

    public long getUniqid_sms_client() { return uniqid_sms_client; }

    public int getGoip_id_sms() { return goip_id_sms; }

    public int report() { return report; }

    public String client_provider_id() { return client_provider_id; }

    public void setProvider_id(int provider_id) { this.provider_id = provider_id; }

    public void setUniqid(int uniqid) { this.uniqid = uniqid; }

    public void setAvailability(String availability) { this.availability = availability; }

    public void setPart(int part) { this.part = part; }

    public void setTotal(int total) { this.total = total; }

    public void setQntsms(int qntsms) { this.qntsms = qntsms; }

    public void setPrioritet(int prioritet) { this.prioritet = prioritet; }

    public void setStatus(String status) { this.status = status; }

    public void setTime_send(Timestamp time_send) { this.time_send = time_send; }

    public void setTime_send_toNow() { this.time_send = new Timestamp(System.currentTimeMillis()); }

    public void setTime_delivered(Timestamp time_delivered) { this.time_delivered = time_delivered; }

    public void setTimeout_set(int timeout_set) { this.timeout_set = timeout_set; }

    public void setTime_counter(int time_counter) { this.time_counter = time_counter; }

    public void setUserfield(String userfield) { this.userfield = userfield; }

    public void setDescription(String description) { this.description = description; }

    public void setGoip_id_sms(int goip_id_sms) { this.goip_id_sms = goip_id_sms; }

    public void setReport(int report) { this.report = report; }

    public void setClient_provider_id(String client_provider_id) { this.client_provider_id = client_provider_id; }

    public Sms(long id) throws SQLException{
        this.id=id;
        //executeResultSet();

        String query = new StringBuilder(200).append("SELECT * FROM smssystem.smslogs where id = ").append(id).toString();
        //DBconnectSelect dBconnectSelect = new DBconnectSelect(query);
        //this.rs = dBconnectSelect.getRs();
        this.result = DBconnectVPS.getResultSet(query);
        System.out.println(LogsT.printDate() + LogsId.id(id) + "получил данные смс и закрываю соединение -- getInfo()");
        //getInfo(this.rs);
        getInfo(result.get(0));
        try{
            operator = defineOperator();
        } catch (MyNullPointerEx ex){
            String err = "Operator noData. Method sms.defineOperator() is failed. Maybe illegal dst_num: "+dst_num;
            //System.err.println(LogsT.printDate() + err);
            System.out.println(LogsT.printDate() + LogsId.id(id) + err);
            //System.err.println(LogsT.printDate() + ex.toString());
            System.out.println(LogsT.printDate() + LogsId.id(id) + ex.getMessage());
        }
        //dBconnectSelect.closeConnectionWithRs();
    }

    private void getInfo(HashMap hm){
        try {
            //while (rs.next()) {
            //rs.first();
            id = GetVal.getLong(hm, "id");
            uniqid = GetVal.getLong(hm, "uniqid");
            availability = GetVal.getStr(hm, "availability");
            part = GetVal.getInt(hm, "part");
            total = GetVal.getInt(hm, "total");
            qntsms = GetVal.getInt(hm, "qntsms");
            client_id = GetVal.getInt(hm, "client_id");
            provider_id = GetVal.getInt(hm, "provider_id");
            prioritet = GetVal.getInt(hm, "prioritet");
            text = GetVal.getStr(hm, "text");
            dst_num = GetVal.getInt(hm, "dst_num");
            src_num = GetVal.getInt(hm, "src_num");
            status = GetVal.getStr(hm, "status");
            time_entry = GetVal.getTimeS(hm, "time_entry");
            time_begin = GetVal.getTimeS(hm, "time_begin");
            time_end = GetVal.getTimeS(hm, "time_end");
            time_send = GetVal.getTimeS(hm, "time_send");
            time_delivered = GetVal.getTimeS(hm, "time_delivered");
            timeout_set = GetVal.getInt(hm, "timeout_set");
            time_counter = GetVal.getInt(hm, "time_counter");
            userfield = GetVal.getStr(hm, "userfield");
            description = GetVal.getStr(hm, "description");
            id_sms_client = GetVal.getLong(hm, "id_sms_client");
            uniqid_sms_client = GetVal.getLong(hm, "uniqid_sms_client");
            goip_id_sms = GetVal.getInt(hm, "goip_id_sms");
            report = GetVal.getInt(hm, "report");
            client_provider_id = GetVal.getStr(hm, "client_provider_id");
            System.err.println("getInfo sms done");
            //}
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println(LogsT.printDate() + LogsId.id(id) + "getInfo sms 1 part failed");
        }
        try{
            System.out.println(LogsT.printDate() + LogsId.id(id) + time_entry);
            time_entry_gc.setLenient(false);
            if (time_entry != null){
                try { time_entry_gc.setTime(time_entry); } catch (NullPointerException n) {n.printStackTrace();}
            } else time_entry_gc=null;
            time_begin_gc.setLenient(false);
            if (time_begin != null){
                try{ time_begin_gc.setTime(time_begin); } catch (NullPointerException n) {n.printStackTrace();}
            } else time_begin_gc=null;
            time_end_gc.setLenient(false);
            if (time_end != null){
                try{ time_end_gc.setTime(time_end); } catch (NullPointerException n) {n.printStackTrace();}
            } else time_end_gc = null;
            time_send_gc.setLenient(false);
            if (time_send != null){
                try{ time_send_gc.setTime(time_send); } catch (NullPointerException n) {n.printStackTrace();}
            } else time_send_gc = null;
        } catch (Exception e){
            e.printStackTrace();
            System.err.println(LogsT.printDate() + LogsId.id(id) + "getInfo 2 part failed");
        }
    }

    public void printSms(){
        DateFormat dateFormat = new SimpleDateFormat("MMM", Locale.ENGLISH);
        dateFormat.setTimeZone(tz);
        System.out.printf(LogsT.printDate() + LogsId.id(id) + "id: %d, uniqid: %d, availability: %s, part: %d, total: %d, qntsms: %d, client_id: %d, provider_id: %d,%n prioritet: %d, text: %s,%n dst_num: %d, src_num: %d, status: %s,%n time_entry: %s, time_begin: %s, time_end: %s, time_send: %s, time_delivered: %s,%n timeout_set: %d, time_counter: %d, userfield: %s,%n description: %s%n",
                id, uniqid, availability, part, total, qntsms, client_id, provider_id, prioritet, text, dst_num, src_num, status, time_entry, time_begin, time_end, time_send, time_delivered, timeout_set, time_counter, userfield, description);
        System.out.println();
       // System.out.println(new SimpleDateFormat("MMMMMM", Locale.ENGLISH).format(time_entry_gc.getTime()));
        /*System.out.printf("time_entry_gc: year: %d, month_lit: %s, month_dig: %d, day: %d, hour: %d, min: %d, sec: %d, msec: %d",
                time_entry_gc.get(Calendar.YEAR),
                new SimpleDateFormat("MMM", Locale.ENGLISH).format(time_entry),
                time_entry_gc.get(Calendar.MONTH) + 1,
                //new SimpleDateFormat("MM").format(time_entry),
                //time_entry_gc.getTime().getDay(),
                time_entry_gc.get(Calendar.DAY_OF_MONTH),
                time_entry_gc.get(Calendar.HOUR),
                time_entry_gc.get(Calendar.MINUTE),
                time_entry_gc.get(Calendar.SECOND),
                time_entry_gc.get(Calendar.MILLISECOND));
                //new SimpleDateFormat("S").format(time_entry));*/
        System.out.println();
    }

    public Operators defineOperator(){
        Matcher matcherMts = PATTERN_MTS.matcher(String.valueOf(dst_num));
        Matcher matcherKs = PATTERN_KS.matcher(String.valueOf(dst_num));
        Matcher matcherLife = PATTERN_LIFE.matcher(String.valueOf(dst_num));
        Matcher matcherOther = PATTERN_OTHER.matcher(String.valueOf(dst_num));
        if(matcherMts.matches()) return Operators.mts;
        if(matcherKs.matches()) return Operators.ks;
        if(matcherLife.matches()) return Operators.life;
        if(matcherOther.matches()) return Operators.other;
        else return null;
    }

    public void updateSmsToDB() throws SQLException {
        //DBconnectUpdate update = new DBconnectUpdate();
        String updateStr = new StringBuilder(500).append("update smssystem.smslogs as ss SET ")
                .append("ss.uniqid=").append(switchIfNull(uniqid))
                .append(", ss.availability='").append(availability)
                .append("', ss.part=").append(part)
                .append(", ss.total=").append(total)
                .append(", ss.qntsms=").append(qntsms)
                .append(", ss.provider_id=").append(switchIfNull(provider_id))
                .append(", ss.prioritet=").append(switchIfNull(prioritet))
                .append(", ss.status=\"").append(status)
                .append("\", ss.time_send=").append(time_send)
                .append(", ss.time_delivered=").append(time_delivered)
                .append(", ss.timeout_set=").append(switchIfNull(timeout_set))
                .append(", ss.time_counter=").append(switchIfNull(time_counter))
                .append(", ss.userfield=\"").append(userfield)
                .append("\", ss.description=\"").append(description)
                .append("\" where ss.id=").append(id)
                .toString();
        System.out.println(LogsT.printDate() + LogsId.id(id) + "updateSms string: "+updateStr);
        DBconnectVPS.executeQuery(updateStr);
        //update.getStmt().execute(updateStr);
        //executeResultSet();
        String query = new StringBuilder(200).append("SELECT * FROM smssystem.smslogs where id = ").append(id).toString();
        //DBconnectSelect dBconnectSelect = new DBconnectSelect(query);
        this.result= DBconnectVPS.getResultSet(query);
        //this.rs = dBconnectSelect.getRs();
        System.out.println(LogsT.printDate() + LogsId.id(id) + "повторно получил данные смс и закрываю соединение");
        //dBconnectSelect.closeConnectionWithRs();
    }

    private String switchIfNull(Number a) {
        if (a.longValue() == 0){ return "null"; }
        else return String.valueOf(a);
    }

    public void delProvider() throws SQLException {
        this.setProvider_id(0);
        String query = new StringBuilder().append("update smssystem.smslogs Set provider_id=NULL where id=").append(this.id).toString();
        DBconnectVPS.executeQuery(query);
        System.out.println(LogsT.printDate() + LogsId.id(this.id) + "provider_id --> NULL");
    }

    public void statusSending() throws SQLException {
        //System.out.println(LogsT.printDate() + LogsId.id(id) + "!!! Sms.statusSending() curent thread is " + Thread.currentThread().getName());
        this.setStatus("sending");
        String query = new StringBuilder().append("update smssystem.smslogs Set status='sending' where id=").append(id).toString();
        DBconnectVPS.executeQuery(query);
        System.out.println(LogsT.printDate() + LogsId.id(this.id) + "status --> sending");
    }

    public void statusProcessing() throws SQLException {
        //System.out.println(LogsT.printDate() + LogsId.id(id) + "!!! Sms.statusProcessing() curent thread is " + Thread.currentThread().getName());
        this.setStatus("processing");
        String query = new StringBuilder().append("update smssystem.smslogs Set status='processing' where id=").append(id).toString();
        DBconnectVPS.executeQuery(query);
        System.out.println(LogsT.printDate() + LogsId.id(this.id) + "status --> processing");
    }

    public void statusWait() throws SQLException {
        //System.out.println(LogsT.printDate() + LogsId.id(id) + "!!! Sms.statusWait() curent thread is " + Thread.currentThread().getName());
        this.setStatus("WAIT");
        String query = new StringBuilder().append("update smssystem.smslogs Set status='WAIT' where id=").append(this.id).toString();
        DBconnectVPS.executeQuery(query);
        System.out.println(LogsT.printDate() + LogsId.id(this.id) + "status --> WAIT");
    }

    public void statusUnknown() throws SQLException {
        //System.out.println(LogsT.printDate() + LogsId.id(id) + "!!! Sms.statusUnknown() curent thread is " + Thread.currentThread().getName());
        this.setStatus("UNKNOWN");
        String query = new StringBuilder().append("update smssystem.smslogs Set status='UNKNOWN', description='maybe wrong dst number' where id=").append(this.id).toString();
        DBconnectVPS.executeQuery(query);
        System.out.println(LogsT.printDate() + LogsId.id(this.id) + "status --> UNKNOWN");
    }
}
