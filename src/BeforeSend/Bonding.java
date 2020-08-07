package BeforeSend;

import DB.DBconnectVPS;
import DB.GetVal;
import LogsParts.LogsId;
import LogsParts.LogsT;
import sms.Sms;

import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.*;

public class Bonding {

    static long l = 1451599200000L;//eq data 2016 01 01 00 00 00
    static Date date2016 = new Date(l);
    static Timestamp time_2016 = new Timestamp(date2016.getTime());
    static TimeZone tz = TimeZone.getTimeZone("Europe/Kiev");

public static boolean bonding(long uniqid) throws SQLException {
    boolean boolRes=false;
    System.out.println(LogsT.printDate() + LogsId.id(uniqid) +"--- start bonding ---");
    Calendar time_sms;
    Calendar nowdate = GregorianCalendar.getInstance();
    time_sms = nowdate;
    //Timestamp timenow = new Timestamp(nowdate.getTime());
    //Calendar time_entry_gc = GregorianCalendar.getInstance(tz);
    String selectBonding = new StringBuilder().append("select * from smssystem.smslogs as ss where ss.total>1 and ss.uniqid=").append(uniqid).toString();
    System.out.println(LogsT.printDate() + LogsId.id(uniqid) + selectBonding);
//    DBconnectSelect dbSelectBonding = new DBconnectSelect(selectBonding);
//    ResultSet rs = dbSelectBonding.getRs();
    int qntResults = DBconnectVPS.qntRowsInSelect(selectBonding);
    ArrayList<HashMap> result = DBconnectVPS.getResultSet(selectBonding);
    //int qntResults = dbSelectBonding.qntRowsInSelect(rs);
    int qntStatusOk = 0;
    //rs.first();
    int totalFirst = GetVal.getInt(result.get(0),"total");
    //int totalFirst = rs.getInt("total");
    boolean checkTotals = true;
    //rs.beforeFirst();
    //while (rs.next()){
    for (HashMap rs : result){
        //Timestamp time_entry = rs.getTimestamp("time_entry");
        Timestamp time_entry = GetVal.getTimeS(rs, "time_entry");
        Calendar time_entry_gc = GregorianCalendar.getInstance(tz);
        time_entry_gc.setLenient(false);
        time_entry_gc.setTime(time_entry);
        if (time_entry_gc.before(nowdate) && time_entry_gc.before(time_sms)){
            time_sms = time_entry_gc;
        }
        String statusVal = GetVal.getStr(rs, "status");
        //System.out.println(LogsT.printDate() + "rs.getString(\"status\"): "+rs.getString("status"));
        System.out.println(LogsT.printDate() + LogsId.id(uniqid) +"status: "+statusVal);
        //if (rs.getString("status").equals("ENROUTE")){ qntStatusOk++; }
        if (statusVal.equals("ENROUTE")||statusVal.equals("bonding")){ qntStatusOk++; }
        //if (rs.getInt("total") != totalFirst){
        if (GetVal.getInt(rs,"total") != totalFirst){
            String wrongTotalValue = new StringBuilder(500).append("update smssystem.smslogs as ss SET ss.status='REJECTED', ss.description='Wrong total value in same uniqid' where ss.uniqid=").append(uniqid).toString();
            DBconnectVPS.executeQuery(wrongTotalValue);
            checkTotals = false;
            break;
        }
    }
    System.out.println(LogsT.printDate() + LogsId.id(uniqid) + "qntStatusOk: " + qntStatusOk + " qntResults: " + qntResults);
    if((qntStatusOk == qntResults) && ( qntStatusOk == totalFirst) && checkTotals){
        StringBuilder bondingTextSB = new StringBuilder(500);
        for (int i=1; i<=qntResults; i++) {
            //rs.beforeFirst();
            //while (rs.next()) {
            for (HashMap rs : result) {
                //if (rs.getInt("part") == i)
                if (GetVal.getInt(rs,"part") == i)
                    //bondingTextSB.append(rs.getString("text"));
                    bondingTextSB.append(GetVal.getStr(rs,"text"));
            }
        }
        String bondingText = bondingTextSB.toString().replace ("\"", "\\\"");
        System.out.println(LogsT.printDate() + LogsId.id(uniqid) + "bondingText: " + bondingText);
        //dbSelectBonding.getRs().first();
        //Sms smsFirstPart = new Sms(dbSelectBonding.getRs().getLong("id"));
        Sms smsFirstPart = new Sms(GetVal.getLong(result.get(0),"id"));
        String insert = new StringBuilder(1000).append("insert INTO smssystem.smslogs (uniqid, availability, part, total, qntsms, client_id, provider_id, prioritet, text, dst_num, src_num, status, time_begin, time_end, timeout_set, time_counter, userfield, description, uniqid_sms_client) VALUES ('")
                .append(uniqid).append("', 'N', '1', '1', ")
                .append(qntResults).append(", '")
                .append(smsFirstPart.getClient_id()).append("', ")
                .append(switchIfNull(smsFirstPart.getProvider_id())).append(", '")
                .append(smsFirstPart.getPrioritet()).append("', \"")
                .append(bondingText).append("\", '")
                .append(smsFirstPart.getDst_num()).append("', '")
                .append(smsFirstPart.getSrc_num()).append("', 'ACCEPTED', ")
                .append(switchTimestampIfNull(smsFirstPart.getTime_begin())).append(", ")
                .append(switchTimestampIfNull(smsFirstPart.getTime_end())).append(", '")
                .append(smsFirstPart.getTimeout_set()).append("', '")
                .append(smsFirstPart.getTime_counter()).append("', 'составное сообщение', 'WAIT time period', '")
                .append(smsFirstPart.getUniqid_sms_client()).append("')")
                .toString();
//        String insert = new StringBuilder(1000).append("insert INTO smssystem.smslogs (uniqid, availability, part, total, qntsms, client_id, provider_id, prioritet, text, dst_num, src_num, status, time_begin, time_end, timeout_set, time_counter, userfield, description) SELECT ")
//                .append(unigid).append(", 'N', 1, 1, ")
//                .append(qntResults).append(", ")
//                .append("client_id").append(", ")
//                .append("provider_id").append(", ")
//                .append("prioritet").append(", \"")
//                .append(bondingText).append("\", ")
//                .append("dst_num").append(", ")
//                .append("src_num").append(", 'WAIT time period', ")
//                .append("time_begin").append(", ")
//                .append("time_end").append(", ")
//                .append("timeout_set").append(", ")
//                .append("time_counter").append(", ")
//                .append("userfield").append(", 'составное сообщение' from smssystem.smslogs where id=").append(smsFirstPart.getId())
//                .toString();
        System.out.println(LogsT.printDate() + LogsId.id(uniqid) + "Insert составную смс: ");
        System.out.println(LogsT.printDate() + LogsId.id(uniqid) + insert);
        try{
            DBconnectVPS.executeQuery(insert);
            //statusToAccepted(uniqid);
            System.out.println(LogsT.printDate() + LogsId.id(uniqid) + "status parts sms uniqid=" + uniqid + " make ACCEPTED because build bonded sms and insert to db correctly");
            boolRes=true;
        } catch (Exception e){
            System.out.println(LogsT.printDate() + LogsId.id(uniqid) + "Error. Fail insert bonded sms");
            e.toString();
            boolRes=false;
        }
//        String updatePartsSms = new StringBuilder(400).append("update smssystem.smslogs Set availability='N', status = 'ACCEPTED' WHERE uniqid=").append(unigid).toString();
//        DBconnectVPS.executeQuery(updatePartsSms);
    }
    else {
        System.out.println(LogsT.printDate() + LogsId.id(uniqid) + "time entry most early part of sms is: " + time_sms.getTime());
        long diff = nowdate.getTimeInMillis()-time_sms.getTimeInMillis();
        int diff_days = (int) (diff/1000/60/60/24);
        System.out.println(LogsT.printDate() + LogsId.id(uniqid) + "now day - day of sms(uniqid "+uniqid+"): "+diff_days);
        if (diff_days>=2) {
            statusToRejected(uniqid);
        }
    }
    return boolRes;
}
//    INSERT INTO `smssystem`.`smslogs` (`uniqid`, `availability`, `part`, `total`, `client_id`, `provider_id`, `prioritet`, `text`, `dst_num`, `src_num`, `status`, `time_begin`, `time_end`,
//    `timeout_set`, `time_counter`, `userfield`, `description`)
//    VALUES ('111', 'N', '1', '1', '2', '6', '1', '555', '123456789', '321654987', 'WAIT time period', '2016-05-09 15:16:02', '2016-05-10 15:16:02', '1', '1', '12121212', '121212121');

    private static String switchIfNull(Number a) {
        if (a.longValue() == 0){ return "null"; }
        else return String.valueOf(a);
    }
    private static String switchTimestampIfNull(Timestamp t) {
        if (t == null){ return "null"; }
        else {
            String withQuonted = new StringBuilder().append("\'").append(String.valueOf(t)).append("\'").toString();
            System.out.println(LogsT.printDate() + "withQuonted: "+withQuonted);
            return withQuonted;
        }
    }

    private static void statusToRejected(long uniqid) throws SQLException {
        String update = new StringBuilder().append("update smssystem.smslogs Set availability='N', status = 'REJECTED', description='not all parts was ENROUTE' WHERE uniqid=").append(uniqid).toString();
        DBconnectVPS.executeQuery(update);
        System.out.println(LogsT.printDate() + "status pasts sms uniqid="+uniqid+" make REJECTED because not all parts was ENROUTE");
    }

    private static void statusToAccepted(long uniqid) throws SQLException {
        String updatePartsSms = new StringBuilder(400).append("update smssystem.smslogs Set availability='N', status = 'ACCEPTED' WHERE uniqid=").append(uniqid).toString();
        DBconnectVPS.executeQuery(updatePartsSms);
        System.out.println(LogsT.printDate() + "status parts sms uniqid=" + uniqid + " make ACCEPTED because build bonded sms and insert to db correctly");
    }

}
