package Daemons;

import DB.DBconnectNEW;
import DB.GetVal;
import LogsParts.LogsId;
import LogsParts.LogsT;
import Run.ClassRunAsyncSend;

import java.sql.SQLException;
import java.util.AbstractList;
import java.util.ArrayList;
import java.util.HashMap;

import static java.lang.Thread.sleep;

public class SendSms {

    //private static long id;

    public static void main(String[] args) throws SQLException, InterruptedException {

        try { daemonize(); }
        catch (Throwable e){
            System.err.println(LogsT.printDate() + "Startup failed. " + e.getMessage());
        }
        finally { System.out.println(LogsT.printDate() + "demonize started!"); }
        findReadySms();
    }

    static private void daemonize() throws Exception{
        try { System.in.close(); }
        catch (Throwable e){
            System.err.println(LogsT.printDate() + "Fail. System in not close" + e.getMessage());
        }
        finally { System.out.println(LogsT.printDate() + "OK. System in closed"); }
        //System.out.close();
    }

    private static void findReadySms() throws SQLException, InterruptedException{
        System.out.println(LogsT.printDate() + "findReadySms() start");
        while(true){
            System.out.println(LogsT.printDate() + "cicle");
            //String query = "select * from smssystem.smslogs as ss where ss.availability = 'Y' and ss.status = 'WAIT' and time_entry > NOW() - INTERVAL 2 DAY order by id asc limit 50";
            //String query = preparedQueryForSearchSms(30);
            String query = preparedQueryForWaitSms(15);
            try{
                AbstractList<HashMap> result = DBconnectNEW.getResultSet(query);
                for (HashMap rs : result) {
                    long id = GetVal.getLong(rs,"id");
                    System.out.println(LogsT.printDate() + LogsId.id(id) + "id: " + id);
                    Thread ct = Thread.currentThread();
                    ct.setPriority(9);
                    System.out.println(LogsT.printDate() + LogsId.id(id) + "run Thread: " + ct.getName());
                    Runnable r = new ClassRunAsyncSend(id);
                    //Thread t = new Thread(r, "daemon");
                    Thread t = new Thread(r);
                    //t.setDaemon(true);
                    t.setPriority(5);
                    System.out.println(LogsT.printDate() + LogsId.id(id) + "start t.start() with id:" + id);
                    t.start();
                    //new Thread(new ClassRunAsyncSend(id)).start();
                    System.out.println(LogsT.printDate() + LogsId.id(id) + "end t.start() with id:"+id);
//                try {
//                    sleep(200);
//                    System.out.println(LogsT.printDate() + LogsId.id(id) + "sleep(200)");
//                } catch (InterruptedException e) {
//                    e.printStackTrace();
//                }
                }
            } catch (Exception e){
                e.printStackTrace();
                System.out.println(e);
            }
            //System.out.println(LogsT.printDate() + "NO available sms for send. No sms with status WAIT");
            try {
                sleep(3000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        //System.out.println(LogsT.printDate() + "NO available sms for send. No sms with status WAIT");
    }

    // на случай запуска отправки сразу после добавления смс в систему, пока не вклюал. Могут быть коллизии
    private static void findReadySmsNotDaemon() throws SQLException{
            String query = "select * from smssystem.smslogs as ss where ss.availability = 'Y' and ss.status = 'WAIT' and time_entry > NOW() - INTERVAL 2 DAY order by id asc";
            ArrayList<HashMap> result = DBconnectNEW.getResultSet(query);
            for (HashMap rs : result) {
                long id = GetVal.getLong(rs,"id");
                System.out.println(LogsT.printDate() + LogsId.id(id) + "id: " + id);
                Thread ct = Thread.currentThread();
                ct.setPriority(9);
                System.out.println(LogsT.printDate() + LogsId.id(id) + "run Thread: " + ct.getName());
                Runnable r = new ClassRunAsyncSend(id);
                Thread t = new Thread(r);
                t.setPriority(5);
                System.out.println(LogsT.printDate() + LogsId.id(id) + "start t.start() with id:" + id);
                t.start();
                System.out.println(LogsT.printDate() + LogsId.id(id) + "end t.start() with id:"+id);
            }
            //dbS.closeConnectionWithRs();
            //System.out.println(LogsT.printDate() + "NO available sms for send. No sms with status WAIT");
            try {
                sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
    }

    private static String preparedQueryForSearchSms(int sizeBlock){
        String strReturn = "";
        boolean check=false;
        ArrayList<Integer> prior = new ArrayList<>();
        ArrayList<Double> perc = new ArrayList<>();
        ArrayList<String> querys = new ArrayList<>();
        String strQueryDefinePriority = "SELECT prioritet, (prioritet/(select sum(subq.prioritet) from (select prioritet from smssystem.clients group by prioritet) as subq)) as perc FROM smssystem.clients where prioritet>1 group by prioritet order by prioritet desc";
        //System.out.println(LogsT.printDate() + "SendSms.preparedQueryForSearchSms("+sizeBlock+") strQueryDefinePriority: "+strQueryDefinePriority);
        //for(int i=0; i<3; i++){
        try{
            ArrayList<HashMap> res = DBconnectNEW.getResultSet(strQueryDefinePriority);
            for(HashMap hm : res){
                prior.add(GetVal.getInt(hm, "prioritet"));
                perc.add(GetVal.getDouble(hm, "perc"));
            }
            check=true;
        } catch (Exception e){
            System.out.println(LogsT.printDate() + "SendSms.preparedQueryForSearchSms("+sizeBlock+") strQueryDefinePriority: "+strQueryDefinePriority);
            System.out.println(LogsT.printDate() + "SendSms.preparedQueryForSearchSms("+sizeBlock+") check: "+check);
            System.out.println(LogsT.printDate() + "ERROR SendSms.preparedQueryForSearchSms() -- strQueryDefinePriority");
            e.printStackTrace();
        }
        //if(check){break;}
        //}
        //System.out.println(LogsT.printDate() + "SendSms.preparedQueryForSearchSms("+sizeBlock+") check: "+check);
        if (!check){return strReturn;}
        for (int i=0; i<prior.size(); i++){
            String subStr = new StringBuilder().append("select ss.*, @s").append(i+1).append(":=@s").append(i+1).append("+1 as qnt from (SELECT @s").append(i+1).append(":= 0) AS s, ")
                    .append("(select * from smssystem.smslogs where availability = 'Y' and status = 'WAIT' and time_entry > NOW() - INTERVAL 2 DAY and client_id in (SELECT id FROM smssystem.clients where time(now()) between time_range_start and time_range_end and prioritet=")
                    .append(prior.get(i)).append(") order by id asc) as ss having @s").append(i+1).append("<").append(sizeBlock*perc.get(i))
                    .toString();
            //System.out.println(LogsT.printDate() + "SendSms.preparedQueryForSearchSms("+sizeBlock+") subStr: "+subStr);
            querys.add(subStr);
        }
        System.out.println(LogsT.printDate() + "SendSms.preparedQueryForSearchSms("+sizeBlock+") begin bind block with union");
        for(String s : querys){
            strReturn+=s;
            if (s.length()>0){
                strReturn+=" union all ";
            }
        }
//        for (int i=0; i<querys.size(); i++){
//            strReturn+=querys.get(i);
//            if (i<(querys.size()-1)){
//                strReturn+=" union all ";
//            }
//        }
        String strQueryPrior1="select ss.*, @s101:=@s101+1 as qnt from (SELECT @s101:= 0) AS s, (select * from smssystem.smslogs where availability = 'Y' and status = 'WAIT' and time_entry > NOW() - INTERVAL 2 DAY and client_id in (SELECT id FROM smssystem.clients where time(now()) between time_range_start and time_range_end and prioritet=1) order by id asc) as ss ";
        strReturn+=strQueryPrior1;
        strReturn+="limit "+sizeBlock;
        //System.out.println(LogsT.printDate() + "|SendSms.preparedQueryForSearchSms|("+sizeBlock+") strReturn: "+strReturn);
        System.out.println(LogsT.printDate() + "|SendSms.preparedQueryForSearchSms|("+sizeBlock+") union iterations: "+ querys.size());
        return strReturn;
    }

    private static String preparedQueryForWaitSms(int sizeBlock){
        String strReturn = "";
        boolean check=false;
        ArrayList<Integer> prior = new ArrayList<>();
        ArrayList<String> querys = new ArrayList<>();
        String strQueryDefinePriority = "SELECT prioritet from smssystem.clients where prioritet>0 group by prioritet order by prioritet";
        try{
            ArrayList<HashMap> res = DBconnectNEW.getResultSet(strQueryDefinePriority);
            for(HashMap hm : res){
                prior.add(GetVal.getInt(hm, "prioritet"));
            }
            check=true;
        } catch (Exception e){
            System.out.println(LogsT.printDate() + "|SendSms.preparedQueryForWaitSms|("+sizeBlock+") strQueryDefinePriority: "+strQueryDefinePriority);
            System.out.println(LogsT.printDate() + "|SendSms.preparedQueryForWaitSms|("+sizeBlock+") check: "+check);
            System.out.println(LogsT.printDate() + "|SendSms.preparedQueryForWaitSms|ERROR SendSms.preparedQueryForSearchSms() -- strQueryDefinePriority");
            e.printStackTrace();
        }
        //if(check){break;}
        //}
        //System.out.println(LogsT.printDate() + "SendSms.preparedQueryForSearchSms("+sizeBlock+") check: "+check);
        if (!check){return strReturn;}
        for (int i=0; i<prior.size(); i++){
            String subStr = new StringBuilder().append("select subs.id from (select id from smssystem.smslogs where availability = 'Y' and status = 'WAIT' and time_entry > NOW() - INTERVAL 2 DAY and " +
                    "client_id in (SELECT id FROM smssystem.clients where time(now()) between time_range_start and time_range_end and prioritet=")
                    .append(prior.get(i))
                    .append(") order by id asc limit ").append(sizeBlock).append(") as subs").toString();
            //System.out.println(LogsT.printDate() + "SendSms.preparedQueryForSearchSms("+sizeBlock+") subStr: "+subStr);
            querys.add(subStr);
        }
        System.out.println(LogsT.printDate() + "|SendSms.preparedQueryForWaitSms|("+sizeBlock+") begin bind block with union");
        for(String s : querys){
            strReturn+=s;
            if (s.length()>0){
                strReturn+=" union all ";
            }
        }
        if (strReturn.length()>0){
            strReturn = strReturn.substring(0, strReturn.length() - 11);
            strReturn+=" limit "+sizeBlock;
        }
        //strReturn+=" limit "+sizeBlock;
        System.out.println(LogsT.printDate() + "|SendSms.preparedQueryForWaitSms|("+sizeBlock+") strReturn: "+strReturn);
        //System.out.println(LogsT.printDate() + "|SendSms.preparedQueryForWaitSms|("+sizeBlock+") union iterations: "+ querys.size());
        return strReturn;
    }


}
