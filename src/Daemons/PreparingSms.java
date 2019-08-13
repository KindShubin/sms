package Daemons;

import BeforeSend.CheckSymbolsSms;
import DB.DBconnectNEW;
import DB.GetVal;
import LogsParts.LogsT;
import sms.Sms;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;

import static java.lang.Thread.sleep;

public class PreparingSms {

    private static int port;
    private static long idSmsStart;
    private static long idSmsEnd;
    private static boolean checkParam = false;

    public static void main(String[] args) throws SQLException, InterruptedException {
        System.out.println(LogsT.printDate() + "PreparingSms started!");
        try { daemonize(); }
        catch (Throwable e){
            System.err.println(LogsT.printDate() + "Startup failed. " + e.getMessage());
        }
        finally { }//System.out.println(LogsT.printDate() + "demonize started!"); }
        try {
            System.out.print(LogsT.printDate() + "input parametrs: ");
            for (int i = 0; i < args.length; i++) {
                System.out.print(args[i] + " ");
            }
            System.out.println();
            port = Integer.valueOf(args[0]);
            idSmsStart = Long.valueOf(args[1]);
            idSmsEnd = Long.valueOf(args[2]);
            checkParam=true;
        } catch (Throwable e){
            System.out.println(LogsT.printDate() + "ERROR. Input parameters is missing");
            e.toString();
        }
        printInputData();
        if (checkParam) {preparingMessages();}
        else {preparingMessagesAll();}
    }

    static private void printInputData(){
        try{
            System.out.printf(LogsT.printDate() + "port:%d idSmsStart:%d idSmsEnd:%d %n", port, idSmsStart, idSmsEnd);
            System.out.printf(LogsT.printDate() + "checkParam: %s%n", checkParam);
        } catch (Throwable e){
            System.out.println(LogsT.printDate() + "Error. printInputData()");
            e.toString();
        }
    }

    static private void daemonize() throws Exception{
        try { System.in.close(); }
        catch (Throwable e){
            System.err.println(LogsT.printDate() + "Fail. System in not close" + e.getMessage());
        }
        finally {
            //System.out.println(LogsT.printDate() + "OK. System in closed");
        }
        //System.out.close();
    }

    private static void preparingMessages() throws SQLException, InterruptedException {
        System.out.println(LogsT.printDate() + "preparingMessages() with parametrs start");
        int count = 0;
        while(true && count<1) {
//        while(true){
            String queryRecieved = new StringBuilder().append("select id from smssystem.smslogs as ss where ss.availability = 'N' and ss.status = 'recieved'")
                    .append(" and time_entry>DATE_SUB(now(),INTERVAL 24 hour) and (ss.userfield <> 'update id' or ss.userfield is null) and qntsms=1")
                    .append(" and port=").append(port).append(" and id between ").append(idSmsStart).append(" and ").append(idSmsEnd)
                    .toString();
            System.out.println(LogsT.printDate() + "queryRecieved: "+queryRecieved);
            int qntPreparedSms=DBconnectNEW.qntRowsInSelect(queryRecieved);
            System.out.println(LogsT.printDate() + "QNT SMS FOR PREPARED: " + qntPreparedSms + "!");
            if(qntPreparedSms<1){
                //System.out.println(LogsT.printDate() + "no select in preparingMessages() -- sleep(1000)");
                count ++;
                sleep(1000);
            }
            else {
                ArrayList<HashMap> result = DBconnectNEW.getResultSet(queryRecieved);
                for(HashMap rs : result) {
                    try {
                        long id = GetVal.getLong(rs,"id");
                        System.out.println(LogsT.printDate() + "***** id: " + id + " *****");
                        Sms sms = new Sms(id);
                        if (checkClientId(sms)){
                            CheckSymbolsSms.check(sms);
                            System.out.println(LogsT.printDate() + "id: " + id + " is checked");
                        }
                    } catch (Error e) {
                        e.toString();
                        System.out.println(LogsT.printDate() + "error in method preparingMessages()");
                    }
                }
                System.out.println(LogsT.printDate() + "sleep(500) after processing some sms");
                //count=2;
                //db.closeConnectionWithRs();
                count++;
                sleep(500);
            }
        }
    }

    private static void preparingMessagesAll() throws SQLException, InterruptedException {
        System.out.println(LogsT.printDate() + "preparingMessagesAll() without parametrs start");
        int count = 0;
        while(true && count<1) {
//        while(true){
            String queryRecieved = "select id from smssystem.smslogs as ss where ss.availability = 'N' and ss.status = 'recieved' and time_entry>DATE_SUB(now(),INTERVAL 24 hour) and (ss.userfield <> 'update id' or ss.userfield is null)";
            int qntPreparedSms=DBconnectNEW.qntRowsInSelect(queryRecieved);
            System.out.println(LogsT.printDate() + "QNT SMS FOR PREPARED: " + qntPreparedSms + "!");
            if(qntPreparedSms<1){
            //System.out.println(LogsT.printDate() + "no select in preparingMessages() -- sleep(1000)");
            count ++;
            sleep(1000);
            }
            else {
                ArrayList<HashMap> result = DBconnectNEW.getResultSet(queryRecieved);
                for(HashMap rs : result) {
                    try {
                        long id = GetVal.getLong(rs,"id");
                        System.out.println(LogsT.printDate() + "***** id: " + id + " *****");
                        Sms sms = new Sms(id);
                        if (checkClientId(sms)){
                            CheckSymbolsSms.check(sms);
                            System.out.println(LogsT.printDate() + "id: " + id + " is checked");
                        }
                    } catch (Error e) {
                        e.toString();
                        System.out.println(LogsT.printDate() + "error in method preparingMessages()");
                    }
                }
                System.out.println(LogsT.printDate() + "sleep(500) after processing some sms");
                //count=2;
                //db.closeConnectionWithRs();
                count++;
                sleep(500);
            }
        }
    }

    public static boolean checkClientId(Sms sms) throws SQLException {
        String query = new StringBuilder().append("select * from smssystem.clients where id =").append(sms.getClient_id()).toString();
        ///ResultSet rs = DBconnectNEW.getResultSet(query);
        if(DBconnectNEW.qntRowsInSelect(query)==0){
            incorrectClientIdInMessage(sms);
            sms.updateSmsToDB();
            return false;
        }
        return true;
    }

    private static void incorrectClientIdInMessage(Sms sms){
        System.out.println(LogsT.printDate() + "incorrectClientIdInMessage");
        sms.setAvailability("N");
        sms.setStatus("REJECTED");
        sms.setDescription(new StringBuilder().append("dst num is not identified. ").append(sms.getDst_num()).toString());
        sms.setUserfield(new StringBuilder().append("clientId is not defined").append(sms.getClient_id()).toString());
    }

    }
