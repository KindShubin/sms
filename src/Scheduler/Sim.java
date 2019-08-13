package Scheduler;

import DB.DBconnectNEW;
import DB.GetVal;
import LogsParts.LogsId;
import LogsParts.LogsT;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static java.lang.Thread.sleep;

public class Sim implements Runnable{

    private static final Pattern PATTERN_OK = Pattern.compile("OK");
    private static final Pattern PATTERN_ERROR = Pattern.compile("ERROR");
    private static final Pattern PATTERN_PREFIX = Pattern.compile("[0-9]{7}");
    private static final Pattern PATTERN_OPERATOR_KS = Pattern.compile("([Uu][Aa][- ]?)?(KYIVSTAR)|(Kyivstar)|(kyivstar)|(KS)|(ks)|(KYEVSTAR)|(Kyevstar)|(kyevstar)");
    private static final Pattern PATTERN_OPERATOR_MTS = Pattern.compile("([Mm][Tt][Ss][\\_\\- ]?[Uu][Kk][Rr])|(MTS)|(Mts)|(mts)|(VODAFONE)|(Vodafone)|(vodafone)|(UMC)|(umc)");
    private static final Pattern PATTERN_OPERATOR_LIFE = Pattern.compile("(life\\:\\))|(LIFE)|(Life)|(life)|(LIFE\\:\\))|(Life\\:\\))|(LIFECELL)|(Lifecell)|(lifecell)|(LIFECELL:\\))|(Lifecell\\:\\))|(lifecell\\:\\))");

    private final int sim_name;
    private final long IMSI;
    private final int line_name;
    private String operator = "";
    private String number_ussd;
    private int number;
    private String package_sms_ussd_begin;
    private int package_sms_begin;
    private String package_sms_ussd_last;
    private int package_sms_last;
    private String balance_ussd_begin;
    private double balance_begin;
    private String balance_ussd_last;
    private double balance_last;
    private int dayLimitAtNow;
    private int dailyLimit;
    private int attempt;

    private String operatorCounter;


    public Sim(int sim_name, long imsi, int line_name){
        this.sim_name=sim_name;
        this.IMSI = imsi;
        this.line_name=line_name;
    }

    @Override
    public void run() {
        try {
            Thread ct = Thread.currentThread();
            //ct.setPriority(5);
                System.out.println(LogsT.printDate()+LogsId.id(IMSI)+"run Thread: "+ct.getName());
                System.out.println(LogsT.printDate()+LogsId.id(IMSI)+"This thread. sim_name:" + sim_name + " IMSI:" + IMSI + " line_name:" + line_name);
                System.out.println(LogsT.printDate() + LogsId.id(IMSI) + "Operator(): " + this.operator);
            getLimitPerDayInPools();
            this.attempt = getValueAttempts();
//            while(!checkOperator()){
//                    System.out.println(LogsT.printDate() + LogsId.id(IMSI) + "operator:" + this.operator + " checkOperator() is false --> sleep 5sec");
//                sleep(5000);
//            }
            waitDefineOperator(ct);//ждем что в течении 10минут определится оператор, если нет off
            setTimeDefineOperator();
            dial();
            if (PATTERN_OPERATOR_KS.matcher(this.operator).find()){ this.operatorCounter="increment"; }
            if (PATTERN_OPERATOR_LIFE.matcher(this.operator).find()){ this.operatorCounter="decrement"; }
            if (PATTERN_OPERATOR_MTS.matcher(this.operator).find()){ this.operatorCounter="decrement"; }
            sleep(60000);
            methodeDefineNumber();
            sleep(120000);
            this.dayLimitAtNow=getLimitPerDayAtNow();
                System.out.println(LogsT.printDate() + LogsId.id(IMSI) + "dayLimitAtNow: "+ dayLimitAtNow);
            this.balance_ussd_begin=getBalance();
                System.out.println(LogsT.printDate() + LogsId.id(IMSI) + "balance_ussd_begin: "+balance_ussd_begin);
            this.balance_begin=doubleBalanceSIM(this.balance_ussd_begin);
                System.out.println(LogsT.printDate() + LogsId.id(IMSI) + "balance_begin: " + balance_begin);
            if(nullBalance(this.balance_begin)){
                checkBadSim("0 balance");
                disableSimAndEndThread(ct);
            }
            sleep(60000);
            this.package_sms_ussd_begin=getOstatok();
                System.out.println(LogsT.printDate() + LogsId.id(IMSI) + "package_sms_ussd_begin: "+package_sms_ussd_begin);
            this.package_sms_begin=(intOstatokSMS(this.package_sms_ussd_begin) == -9999) ? this.dayLimitAtNow : intOstatokSMS(this.package_sms_ussd_begin);
                System.out.println(LogsT.printDate() + LogsId.id(IMSI) + "package_sms_begin: " + package_sms_begin);
            String strQueryBegin = new StringBuilder().append("UPDATE smssystem.my_scheduler SET package_sms_ussd_begin='")
                .append(this.package_sms_ussd_begin).append("', package_sms_begin=")
                .append(this.package_sms_begin).append(", package_sms_time_begin=now(), balance_ussd_begin='")
                .append(this.balance_ussd_begin).append("', balance_begin=")
                .append(this.balance_begin).append(", balance_time_begin=now() where sim_name=").append(this.sim_name).toString();
                System.out.println(LogsT.printDate() + LogsId.id(IMSI) + "strQueryBegin: " + strQueryBegin);
            String strQueryBeginToSimcards="";
            DBconnectNEW.executeQuery(strQueryBegin);
                System.out.println(LogsT.printDate() + LogsId.id(IMSI) + "setYonairInSimcards()...");
            if (this.attempt >1 && intOstatokSMS(this.package_sms_ussd_begin) == -9999){
                this.package_sms_begin=0;
                try{
                    System.out.println(LogsT.printDate()+LogsId.id(IMSI)+" attempt:"+this.attempt+" dayLimitAtNow:"+this.dayLimitAtNow+" package_sms_begin:"+this.package_sms_begin+" --> count_perday=0");
                    String strQuery = new StringBuilder().append("update smssystem.simcards SET count_perday=0 where imsi=").append(this.IMSI).toString();
                    DBconnectNEW.executeQuery(strQuery);
                } catch (Exception ea1) {
                    System.out.println(LogsT.printDate()+LogsId.id(IMSI)+"FAIL update. attempt:"+this.attempt+" dayLimitAtNow:"+this.dayLimitAtNow+" package_sms_begin:"+this.package_sms_begin+" --> count_perday=0");
                    ea1.printStackTrace();
                }
            }
            setYonairInSimcards();
            setTimeBeginWork();
            while(getLimitPerDayAtNow()>0){
                    System.out.println(LogsT.printDate() + LogsId.id(IMSI) + "getLimitPerDayAtNow(): "+getLimitPerDayAtNow()+" --> sleep 30sec");
                sleep(30000);
            }
                System.out.println(LogsT.printDate() + LogsId.id(IMSI) + "getLimitPerDayAtNow(): " + getLimitPerDayAtNow());
            setNonairInSimcards();
            setTimeBeginPause();
            /////////
            checkSimGoipBindOperator();
            dial();
            methodeDefinePackageSmsLast();
            checkSimGoipBindOperator();
            dial();
            methodeDefineBalanceLast();
            //////////
            int raznizaOstatkov=getRaznitsaOstatcov();
                System.out.println(LogsT.printDate() + LogsId.id(IMSI) + "raznizaOstatkov: "+raznizaOstatkov);
            if(raznizaOstatkov<1 || raznizaOstatkov>=dailyLimit+10){
                System.out.println(LogsT.printDate() + LogsId.id(IMSI) + "dailyLimit=" + this.dailyLimit + " package_sms_last=" + this.package_sms_last + " raznizaOstatkov=" + raznizaOstatkov + " --> setNonairInSimcards() --> disableSim()");
                disableSimAndEndThread(ct);
            }
            System.out.println(LogsT.printDate() + LogsId.id(IMSI) + "setLimitPerDayInSimcards() = " + (raznizaOstatkov));
            setLimitPerDayInSimcards(raznizaOstatkov);
            setTimeEndPause();
            setYonairInSimcards();
            while(getLimitPerDayAtNow()>0){
                System.out.println(LogsT.printDate() + LogsId.id(IMSI) + "getLimitPerDayAtNow(): "+getLimitPerDayAtNow()+" --> sleep 30000 msec");
                sleep(30000);
            }
            setNonairInSimcards();
            setTimeEndWork();
            /////
            checkSimGoipBindOperator();
            dial();
            methodeDefinePackageSmsLast();
            checkSimGoipBindOperator();
            dial();
            methodeDefineBalanceLast();
            /////
            raznizaOstatkov=getRaznitsaOstatcov();
            System.out.println(LogsT.printDate() + LogsId.id(IMSI) + "raznizaOstatkov: " + raznizaOstatkov);
            //System.out.println(LogsT.printDate()+LogsId.id(IMSI)+"getPrefixForSim("+sim_name+"): "+getPrefixForSim(sim_name));
            //System.out.println(LogsT.printDate()+LogsId.id(IMSI)+"getSimForImsi(): "+getSimForImsi());
            System.out.println(LogsT.printDate()+LogsId.id(IMSI)+"sleep 120 sec");
            sleep(120000);
            disableSimAndEndThread(ct);
        } catch (InterruptedException ie){
            System.out.println(LogsT.printDate()+LogsId.id(IMSI)+"run() InterruptedException. setNonairInSimcards() --> disableSim() --> .interrupt()");
            setNonairInSimcards();
            try { disableSim(); }
            catch (SQLException e1) { e1.printStackTrace(); }
            ie.printStackTrace();
            Thread.currentThread().interrupt();
        }
        catch (Exception e) {
            System.out.println(LogsT.printDate()+LogsId.id(IMSI)+"run() Exception. setNonairInSimcards() --> disableSim() --> .interrupt()");
            setNonairInSimcards();
            try { disableSim(); }
            catch (SQLException e1) { e1.printStackTrace(); }
            e.printStackTrace();
            Thread.currentThread().interrupt();
        }
    }

    private int getValueAttempts() throws SQLException {
        String strQuery = new StringBuilder().append("SELECT attempt FROM smssystem.my_scheduler where sim_name=").append(this.sim_name).toString();
        int res=0;
        try{
            ArrayList <HashMap> rs = DBconnectNEW.getResultSet(strQuery);
            res = GetVal.getInt(rs.get(0),"attempt");
        } catch (Exception ae){ ae.printStackTrace(); }
        return res;
    }

    private void setLimitPerDayInSimcards(int value){
        String strQuery = new StringBuilder().append("update smssystem.simcards Set count_perday=").append(value)
                .append(", count_perday_mts=").append(value)
                .append(", count_perday_ks=").append(value)
                .append(", count_perday_life=").append(value)
                .append(", count_perday_other=").append(value)
                .append(", count_perhour=").append(value)
                .append(", count_perhour_mts=").append(value)
                .append(", count_perhour_ks=").append(value)
                .append(", count_perhour_life=").append(value)
                .append(", count_perhour_other=").append(value).append(" where imsi=").append(this.IMSI).toString();
        System.out.println(LogsT.printDate()+LogsId.id(IMSI)+"setLimitPerDayInSimcards("+value+"): "+strQuery);
        try {
        DBconnectNEW.executeQuery(strQuery);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private void checkBadSim(String reason){
        String insert = new StringBuilder().append("insert into smssystem.checkSims(imsi, simLine, goipLine, balance, reason) VALUES (")
                .append(this.IMSI).append(", ").append(this.sim_name).append(", ").append(this.line_name).append(", '").append(this.balance_ussd_begin).append("', '").append(reason).append("')").toString();
        String update = new StringBuilder().append("update smssystem.checkSims Set description=concat(date,' DATA was: simLine=',COALESCE(simLine,'null'),'; goipLine=',COALESCE(goipLine,'null'),'; balance=',balance), simLine=")
                .append(this.sim_name).append(", goipLine=").append(this.line_name).append(", date=now(), balance='").append(this.balance_ussd_begin).append("', reason='").append(reason).append("' where imsi=").append(this.IMSI).toString();
        try{
            DBconnectNEW.executeQuery(insert);
        } catch (Exception e){
            System.out.println(LogsT.printDate()+LogsId.id(IMSI)+"SchedulerOld.Sim.checkBadSim() Insert is fail --> update");
            try{
                DBconnectNEW.executeQuery(update);
            } catch (Exception e1){
                System.out.println(LogsT.printDate()+LogsId.id(IMSI)+"SchedulerOld.Sim.checkBadSim() Update is fail");
                e1.printStackTrace();
            }
        }
    }

    private void methodeDefinePackageSmsLast() throws InterruptedException {
        this.package_sms_ussd_last=getOstatok();
        System.out.println(LogsT.printDate() + LogsId.id(IMSI) + "package_sms_ussd_last: " + package_sms_ussd_last);
        this.package_sms_last=intOstatokSMS(this.package_sms_ussd_last);
        System.out.println(LogsT.printDate() + LogsId.id(IMSI) + "package_sms_last: "+package_sms_last);
        String strQueryPackageSmsLast = new StringBuilder().append("UPDATE smssystem.my_scheduler SET package_sms_ussd_last='")
                .append(this.package_sms_ussd_last).append("', package_sms_last=")
                .append(this.package_sms_last).append(", package_sms_time_last=now() where sim_name=").append(this.sim_name).toString();
        System.out.println(LogsT.printDate()+LogsId.id(IMSI)+"strQueryPackageSmsLast: "+strQueryPackageSmsLast);
        try{
            DBconnectNEW.executeQuery(strQueryPackageSmsLast);
        } catch (Exception q){
            q.printStackTrace();
        }
    }

    private void methodeDefineBalanceLast() throws InterruptedException {
        this.balance_ussd_last=getBalance();
        System.out.println(LogsT.printDate() + LogsId.id(IMSI) + "balance_ussd_last: "+this.balance_ussd_last);
        this.balance_last=doubleBalanceSIM(this.balance_ussd_last);
        System.out.println(LogsT.printDate() + LogsId.id(IMSI) + "balance_last: "+this.balance_last);
        String strQueryBalanceLast = new StringBuilder().append("UPDATE smssystem.my_scheduler SET balance_ussd_last='")
                .append(this.balance_ussd_last).append("', balance_last=")
                .append(this.balance_last).append(", balance_time_last=now() where sim_name=").append(this.sim_name).toString();
        System.out.println(LogsT.printDate()+LogsId.id(IMSI)+"strQueryBalanceLast: "+strQueryBalanceLast);
        try{
            DBconnectNEW.executeQuery(strQueryBalanceLast);
        } catch (Exception q){
            q.printStackTrace();
        }
    }

    private void methodeDefineNumber() throws InterruptedException {
        System.out.println(LogsT.printDate() + LogsId.id(IMSI) + "SchedulerOld.Sim.methodeDefineNumber() start");
        this.number_ussd=getNumber();
        System.out.println(LogsT.printDate() + LogsId.id(IMSI) + "SchedulerOld.Sim.methodeDefineNumber() number_ussd: "+this.number_ussd);
        this.number=intNumber(this.number_ussd);
        System.out.println(LogsT.printDate() + LogsId.id(IMSI) + "SchedulerOld.Sim.methodeDefineNumber() number: " + this.number);
        String strQueryNumberSimcards = new StringBuilder().append("UPDATE smssystem.simcards SET number='").append(this.number).append("' WHERE imsi=").append(this.IMSI).toString();
        System.out.println(LogsT.printDate()+LogsId.id(IMSI)+"SchedulerOld.Sim.methodeDefineNumber() strQueryBalanceLast: "+strQueryNumberSimcards);
        String strQueryNumberMySheduler = new StringBuilder().append("UPDATE smssystem.my_scheduler SET number=").append(this.number).append(", number_time=now() WHERE sim_name=").append(this.sim_name).toString();
        System.out.println(LogsT.printDate()+LogsId.id(IMSI)+"SchedulerOld.Sim.methodeDefineNumber() strQueryNumberMySheduler: "+strQueryNumberMySheduler);
        try{
            DBconnectNEW.executeQuery(strQueryNumberSimcards);
        } catch (Exception q){
            System.out.println(LogsT.printDate()+LogsId.id(IMSI)+"SchedulerOld.Sim.methodeDefineNumber() Error update strQueryNumberSimcards");
            q.printStackTrace();
        }
        try{
            DBconnectNEW.executeQuery(strQueryNumberMySheduler);
        } catch (Exception q){
            System.out.println(LogsT.printDate()+LogsId.id(IMSI)+"SchedulerOld.Sim.methodeDefineNumber() Error update strQueryNumberMyScheduler");
            q.printStackTrace();
        }

    }

    private void setNonairInSimcards(){
        System.out.println(LogsT.printDate() + LogsId.id(IMSI) + "SchedulerOld.Sim.setNonairInSimcards() start...");
        String strQuery = new StringBuilder().append("update smssystem.simcards Set onair='N', prefix=0 where imsi=").append(this.IMSI).toString();
        System.out.println(LogsT.printDate() + LogsId.id(IMSI) + "SchedulerOld.Sim.setNonairInSimcards() strQuery: "+strQuery);
        try{ DBconnectNEW.executeQuery(strQuery); }
        catch (Exception e){
            System.out.println(LogsT.printDate() + LogsId.id(IMSI) + "SchedulerOld.Sim.setNonairInSimcards() Error update");
            e.printStackTrace();
        }
    }

    private void setYonairInSimcards(){
        System.out.println(LogsT.printDate() + LogsId.id(IMSI) + "SchedulerOld.Sim.setYonairInSimcards() start...");
        String strQuery = new StringBuilder().append("update smssystem.simcards Set onair='Y', prefix=").append(this.line_name).append(" where imsi=").append(this.IMSI).toString();
        System.out.println(LogsT.printDate() + LogsId.id(IMSI) + "SchedulerOld.Sim.setYonairInSimcards() strQuery: " + strQuery);
        try{ DBconnectNEW.executeQuery(strQuery); }
        catch (Exception e){
            System.out.println(LogsT.printDate() + LogsId.id(IMSI) + "SchedulerOld.Sim.setYonairInSimcards() Error update");
            e.printStackTrace();
        }
    }

    private int getLimitPerDayAtNow() throws SQLException {
        int res;
        String strQuery = new StringBuilder().append("select count_perday from smssystem.simcards where imsi=").append(this.IMSI).toString();
        ArrayList<HashMap> result = DBconnectNEW.getResultSet(strQuery);
        res=GetVal.getInt(result.get(0),"count_perday");
        return res;
    }

    private void getLimitPerDayInPools() throws SQLException {
        String strQuery = new StringBuilder().append("select limit_day from smssystem.pools where corp=(select corp from smssystem.simcards where imsi=").append(this.IMSI).append(")").toString();
        ArrayList<HashMap> result = DBconnectNEW.getResultSet(strQuery);
        this.dailyLimit=GetVal.getInt(result.get(0),"limit_day");
    }

    private int getRaznitsaOstatcov(){
        int razOst=0;
        if (Objects.equals(this.operatorCounter, "increment")){
            //raznizaOstatkov=Math.abs(this.dailyLimit-this.package_sms_last-2);
            razOst=this.dailyLimit-this.package_sms_last-2;
        }
        if (this.operatorCounter=="decrement"){
            //raznizaOstatkov=Math.abs(this.package_sms_last-2);
            razOst=this.package_sms_last-2;
        }
        return razOst;
    }

    private String getBalance() throws InterruptedException {
        String balance = "";
        String strUrl = new StringBuilder(110).append("http://localhost/goip/en/ussd.php?USERNAME=root&PASSWORD=gf9e44s2&TERMID=").append(line_name).append("&USSDMSG=").append(getUssdForBalance()).toString();
        System.out.println(LogsT.printDate()+LogsId.id(IMSI)+"strUrl for getBalance():"+strUrl);
        boolean check = false;
        for(int i=0; i<3; i++){
            try{
                URL url = new URL(strUrl);
                balance = "";
                try (BufferedReader reader = new BufferedReader(new InputStreamReader(url.openStream(), "UTF-8"))) {
                    for (String line; (line = reader.readLine()) != null;) {
                        balance=balance+line;
                        System.out.println(LogsT.printDate()+LogsId.id(IMSI)+balance);
                        Matcher matcherError = PATTERN_ERROR.matcher(line);
                        check=!matcherError.find();//если вывод line неудачный, содержит ERROR то check false
                        System.out.println(LogsT.printDate()+LogsId.id(IMSI)+"check: "+check);
                    }
                }
                if (!check){
                    System.out.println(LogsT.printDate()+LogsId.id(IMSI)+"Sim "+sim_name+" whith IMSI:"+this.IMSI+" maybe don't gave balance");
                }
            } catch (Exception e){
                System.out.println(LogsT.printDate()+LogsId.id(IMSI) + "some error in SchedulerOld.Sim.getBalance()");
                e.printStackTrace();
            }
            if(check) break;
            sleep(30000);
        }
        return balance;
    }

    private String getOstatok() throws InterruptedException {
        String ostatok = "";
        String strUrl = new StringBuilder(110).append("http://localhost/goip/en/ussd.php?USERNAME=root&PASSWORD=gf9e44s2&TERMID=").append(line_name).append("&USSDMSG=").append(getUssdForOstatok()).toString();
        System.out.println(LogsT.printDate()+LogsId.id(IMSI)+"strUrl for getOstatok():"+strUrl);
        boolean check = false;
        for(int i=0; i<3; i++){
            try{
                URL url = new URL(strUrl);
                ostatok = "";
                try (BufferedReader reader = new BufferedReader(new InputStreamReader(url.openStream(), "UTF-8"))) {
                    for (String line; (line = reader.readLine()) != null;) {
                        ostatok=ostatok+line;
                        System.out.println(LogsT.printDate()+LogsId.id(IMSI)+ostatok);
                        Matcher matcherError = PATTERN_ERROR.matcher(line);
                        check=!matcherError.find();//если вывод line неудачный, содержит ERROR то check false
                        System.out.println(LogsT.printDate()+LogsId.id(IMSI)+"check: "+check);
                    }
                }
                if (!check){
                    System.out.println(LogsT.printDate()+LogsId.id(IMSI)+"Sim "+sim_name+" whith IMSI:"+this.IMSI+" maybe don't gave residue sms");
                }
            } catch (Exception e){
                System.out.println(LogsT.printDate()+LogsId.id(IMSI) + "some error in SchedulerOld.Sim.getOstatok()");
                e.printStackTrace();
            }
            if(check) break;
            sleep(30000);
        }
        return ostatok;
    }

    private String getNumber() throws InterruptedException {
        String ussdNumber="";
        String strUrl = new StringBuilder().append("http://localhost/goip/en/ussd.php?USERNAME=root&PASSWORD=gf9e44s2&TERMID=").append(this.line_name).append("&USSDMSG=*161%23").toString();
        System.out.println(LogsT.printDate()+LogsId.id(IMSI)+"Sim.getNumber() strUrl for getNumber():"+strUrl);
        boolean check = false;
        for(int i=0; i<5; i++){
            System.out.println(LogsT.printDate()+LogsId.id(IMSI)+"Sim.getNumber() attampt "+(i+1)+" from 5");
            try{
                URL url = new URL(strUrl);
                ussdNumber = "";
                try (BufferedReader reader = new BufferedReader(new InputStreamReader(url.openStream(), "UTF-8"))) {
                    for (String line; (line = reader.readLine()) != null;) {
                        ussdNumber+=line;
                        System.out.println(LogsT.printDate()+LogsId.id(IMSI)+"Sim.getNumber() ussdNumber: "+ussdNumber);
                        Matcher matcherError = PATTERN_ERROR.matcher(line);
                        check=!matcherError.find();//если вывод line неудачный, содержит ERROR то check false
                        System.out.println(LogsT.printDate()+LogsId.id(IMSI)+"Sim.getNumber() check: "+check);
                    }
                }
                if (!check){
                    System.out.println(LogsT.printDate()+LogsId.id(IMSI)+"Sim.getNumber() Sim:"+sim_name+" whith IMSI:"+this.IMSI+" maybe don't gave number sms");
                }
            } catch (Exception e){
                System.out.println(LogsT.printDate()+LogsId.id(IMSI) + "Sim.getNumber() some error in SchedulerOld.Sim.getNumber()");
                e.printStackTrace();
            }
            if(check) break;
            sleep(30000);
        }
        return ussdNumber;
    }

    private String getUssdForBalance(){
        String ussd="*111%23";
        Matcher matcherKS = PATTERN_OPERATOR_KS.matcher(this.operator);
        Matcher matcherMTS = PATTERN_OPERATOR_MTS.matcher(this.operator);
        Matcher matcherLIFE = PATTERN_OPERATOR_LIFE.matcher(this.operator);
        if(matcherKS.find()){ ussd="*111%23"; }
        if(matcherMTS.find()){ ussd="*101%23"; }
        if(matcherLIFE.find()){ ussd="*111%23"; }
        System.out.println(LogsT.printDate()+LogsId.id(IMSI)+"Operator:"+this.operator+" USSD for balance: "+ussd);
        return ussd;
    }

    private String getUssdForOstatok(){
        String ussd="*121%23";
        Matcher matcherKS = PATTERN_OPERATOR_KS.matcher(this.operator);
        Matcher matcherMTS = PATTERN_OPERATOR_MTS.matcher(this.operator);
        Matcher matcherLIFE = PATTERN_OPERATOR_LIFE.matcher(this.operator);
        if(matcherKS.find()){ ussd="*112%23"; }
        if(matcherMTS.find()){ ussd="*101*4%23"; }
        if(matcherLIFE.find()){ ussd="*121%23"; }
        System.out.println(LogsT.printDate()+LogsId.id(IMSI)+"Operator:"+this.operator+" USSD for ostatok sms: "+ussd);
        return ussd;
    }

//    private void defineOperator(){
//
//    }

    private void getOperator() throws SQLException {
        String query = new StringBuilder(100).append("SELECT oper FROM scheduler.device_line where line_name=").append(this.line_name).toString();
        ArrayList<HashMap> result = DBconnectNEW.getResultSet(query);
        this.operator = GetVal.getStr(result.get(0), "oper");
        System.out.println(LogsT.printDate()+LogsId.id(IMSI)+"now operator:"+this.operator);
        //return this.operator;
    }

    private boolean checkOperator() throws SQLException {
        getOperator();
        Matcher mKS = PATTERN_OPERATOR_KS.matcher(this.operator);
        Matcher mMTS = PATTERN_OPERATOR_MTS.matcher(this.operator);
        Matcher mLIFE = PATTERN_OPERATOR_LIFE.matcher(this.operator);
        if(mKS.find() || mMTS.find() || mLIFE.find()) return true;
        else return false;
    }

    private void waitDefineOperator(Thread ct) throws InterruptedException, SQLException {
        for(int i=0; i<120; i++){ //ожидаем 10 минут пока поднимится симка
            if(checkOperator()) break;
            System.out.println(LogsT.printDate() + LogsId.id(IMSI) + "operator:" + this.operator + " checkOperator() is false --> sleep 5sec");
            if (i==119){
                checkBadSim("operator is not up");
                disableSimAndEndThread(ct);
            }
            else sleep(5000);
        }
    }

    private int intOstatokSMS(String strOstatok){
        int res = -9999;
        System.out.println(LogsT.printDate() + LogsId.id(IMSI) + "begin SchedulerOld.Sim.intOstatokSMS()...");
        Pattern pFirst = Pattern.compile("-?[0-9]+ ?[SsСс][MmМм][SsСс]");
        Pattern pSecond = Pattern.compile("-?[0-9]+");
        Matcher mFirst = pFirst.matcher(strOstatok);
        if (mFirst.find()){
            String strSecond = mFirst.group();
            System.out.println(LogsT.printDate()+LogsId.id(IMSI)+"first result: "+strSecond);
            Matcher mSecond = pSecond.matcher(strSecond);
            if (mSecond.find()){
                String result = mSecond.group();
                System.out.println(LogsT.printDate()+LogsId.id(IMSI)+"second result: "+result);
                res = Integer.valueOf(result);
                System.out.println(LogsT.printDate()+LogsId.id(IMSI)+"final res: "+res);
            }
        }
        return res;
    }

    private double doubleBalanceSIM(String strBalance){
        System.out.println(LogsT.printDate()+LogsId.id(IMSI)+"SchedulerOld.Sim.intBalanceSIM() start...");
        double res = -9999;
        Pattern pFirst = Pattern.compile("([Bb]alan[sc]e?|[Ss]chet[ue]?|[Rr]ahun(ku|ok)) ?-?[0-9]+[.,]?[0-9]* ?");
        Pattern pSecond = Pattern.compile("-?[0-9]+[.,]?[0-9]*");
        Matcher mFirst = pFirst.matcher(strBalance);
        if (mFirst.find()){
            String strSecond = mFirst.group();
            System.out.println(LogsT.printDate()+LogsId.id(IMSI)+"SchedulerOld.Sim.intBalanceSIM() first result: "+strSecond);
            Matcher mSecond = pSecond.matcher(strSecond);
            if (mSecond.find()){
                String result = mSecond.group();
                System.out.println(LogsT.printDate()+LogsId.id(IMSI)+"SchedulerOld.Sim.intBalanceSIM() second result: "+result);
                res = Double.valueOf(result);
                System.out.println(LogsT.printDate()+LogsId.id(IMSI)+"SchedulerOld.Sim.intBalanceSIM() final res: "+res+" --> return res");
            }
        }
        return res;
    }

    private int intNumber(String strUssdNumber){//int в формате 501234567
        System.out.println(LogsT.printDate() + LogsId.id(IMSI) + "SchedulerOld.Sim.intNumber() start...");
        int res = -9999;
        Pattern pattern = Pattern.compile(".*[0-9]{9,}.*");
        Pattern pattern2 = Pattern.compile("(39|50|6[3678]|73|9[1-9])[0-9]{7}");
        Matcher mFirst = pattern.matcher(strUssdNumber);
        if (mFirst.find()){
            String strSecond = mFirst.group();
            System.out.println(LogsT.printDate()+LogsId.id(IMSI)+"SchedulerOld.Sim.intNumber() first result: "+strSecond);
            Matcher mSecond = pattern2.matcher(strSecond);
            if (mSecond.find()){
                String result = mSecond.group();
                System.out.println(LogsT.printDate()+LogsId.id(IMSI)+"SchedulerOld.Sim.intNumber() second result: "+result);
                res = Integer.valueOf(result);
                System.out.println(LogsT.printDate()+LogsId.id(IMSI)+"SchedulerOld.Sim.intNumber() final res: "+res+" --> return res");
            }
        }
        return res;
    }

    private boolean nullBalance(double doubleBalance){
        if(doubleBalance<1.5) return true;
        else return false;
    }

    //решаем что делать с симкой, выводить в работу, выводить обратно в работу после выработки некоторого кол-ва смс или выводить из гейта
    private boolean resultSim(int intOstatokBegin, int intOstatokEnd, int dailyAllLimit, int statUnknownStatusPerHour){
        return false;
    }

    private int getPrefixForSim(int sim) throws IOException {
        int prefix = 0;
        String strUrl = new StringBuilder().append("http://localhost/smb_scheduler/api.php?username=root&password=gf9e44s2=bind&sim=").append(sim).toString();
        URL url = new URL(strUrl);
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(url.openStream(), "UTF-8"))) {
            for (String line; (line = reader.readLine()) != null;) {
                System.out.println(LogsId.id(IMSI)+line);
                Matcher matcherPrefix = PATTERN_PREFIX.matcher(line);
                if (matcherPrefix.find()){
                    System.out.println(LogsT.printDate()+LogsId.id(IMSI)+"matcherPrefix.matches() is TRUE");
                    System.out.println(LogsT.printDate()+LogsId.id(IMSI)+matcherPrefix.toString());
                    System.out.println(LogsT.printDate()+LogsId.id(IMSI)+matcherPrefix.group());
                    System.out.println(LogsT.printDate()+LogsId.id(IMSI)+matcherPrefix.toMatchResult());
                    prefix=Integer.parseInt(matcherPrefix.group());
                }
            }
        }
        return prefix;
    }

    private int getSimForImsi() throws SQLException {
        int sim=0;
        String strQuery=new StringBuilder(100).append("SELECT sim_name FROM scheduler.sim where imsi regexp ").append(this.IMSI).toString();
        ArrayList<HashMap> result = DBconnectNEW.getResultSet(strQuery);
        try{
            sim = GetVal.getInt(result.get(0),"sim_name");
        } catch (Exception e){
            System.out.println(LogsT.printDate()+LogsId.id(IMSI)+"can't find №sim (sim_name) in scheduler.sim for imsi="+this.IMSI);
            e.printStackTrace();
        }
        return sim;
    }

    private void disableSim() throws SQLException {
        setNincheck_action();
        //int sim = getSimForImsi();
        String strUrl = new StringBuilder(110).append("http://localhost/smb_scheduler/api.php?username=root&password=gf9e44s2&set=disable&sim=").append(sim_name).toString();
        System.out.println(LogsT.printDate()+LogsId.id(IMSI)+"try disable sim "+sim_name+" IMSI: "+this.IMSI);
        boolean check = false;
        try{
            URL url = new URL(strUrl);
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(url.openStream(), "UTF-8"))) {
                for (String line; (line = reader.readLine()) != null;) {
                    System.out.println(LogsT.printDate()+LogsId.id(IMSI)+line);
                    Matcher matcher_ok = PATTERN_OK.matcher(line);
                    if (matcher_ok.find()){
                        System.out.println(LogsT.printDate()+LogsId.id(IMSI)+"Sim "+sim_name+" whith IMSI:"+this.IMSI+" disable correct");
                        check=true;
                    }
                }
            }
            if (!check){
                System.out.println(LogsT.printDate()+LogsId.id(IMSI)+"Sim "+sim_name+" whith IMSI:"+this.IMSI+" maybe not disable");
            }
        } catch (Exception e){
            System.out.println(LogsT.printDate()+LogsId.id(IMSI)+"some error in SchedulerOld.Sim.disableSim()");
            e.printStackTrace();
        }
    }

    private void enableSim() throws SQLException, IOException {
        int sim = getSimForImsi();
        String strUrl = new StringBuilder(110).append("http://localhost/smb_scheduler/api.php?username=root&password=gf9e44s2&set=enable&sim=").append(sim).toString();
        System.out.println(LogsT.printDate()+LogsId.id(IMSI)+"try enable sim "+sim+" IMSI: "+this.IMSI);
        boolean check = false;
        try{
            URL url = new URL(strUrl);

            try (BufferedReader reader = new BufferedReader(new InputStreamReader(url.openStream(), "UTF-8"))) {
                for (String line; (line = reader.readLine()) != null;) {
                    System.out.println(LogsT.printDate()+LogsId.id(IMSI)+line);
                    Matcher matcher_ok = PATTERN_OK.matcher(line);
                    if (matcher_ok.find()){
                        System.out.println(LogsT.printDate()+LogsId.id(IMSI)+"Sim "+sim+" whith IMSI:"+this.IMSI+" enabled correct");
                        check=true;
                    }
                }
            }
            if (!check){
                System.out.println(LogsT.printDate()+LogsId.id(IMSI)+"Sim "+sim+" whith IMSI:"+this.IMSI+" maybe not enabled");
            }
        } catch (Exception e){
            System.out.println(LogsT.printDate()+LogsId.id(IMSI)+"some error in SchedulerOld.Sim.enableSim()");
            e.printStackTrace();
        }
    }

    private void checkSimGoipBindOperator() throws SQLException, InterruptedException {
        //boolean res = false;
        for(int i=0; i<18; i++){
            String strQuery=new StringBuilder().append("SELECT line_name FROM scheduler.sim where sim_name=").append(this.sim_name).toString();
            ArrayList<HashMap> result = DBconnectNEW.getResultSet(strQuery);
            int line = GetVal.getInt(result.get(0),"line_name");
            if (this.line_name == line){
                if(checkOperator()){
                    //res=true;
                    System.out.println(LogsT.printDate()+LogsId.id(IMSI)+"checkSimGoipBinOperator() TRUE: this.line:"+this.line_name+"line:"+line+"this.operator:"+this.operator);
                    break;
                }
            }
            System.out.println(LogsT.printDate()+LogsId.id(IMSI)+"checkSimGoipBinOperator() FALSE: this.line:"+this.line_name+"line:"+line+"this.operator:"+this.operator+" -- sleep 10sec");
            sleep(10000);
        }
        //return res;
    }

    private void setNincheck_action(){
        String strQuery=new StringBuilder(100).append("UPDATE smssystem.my_scheduler SET check_action='N' where sim_name=").append(this.sim_name).toString();
        try {
            DBconnectNEW.executeQuery(strQuery);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void setTimeDefineOperator(){
        try{
            String strQuery = new StringBuilder().append("update smssystem.my_scheduler Set time_define_oper=now() where sim_name=").append(this.sim_name).toString();
            DBconnectNEW.executeQuery(strQuery);
        } catch (Exception e){
            System.out.println(LogsT.printDate()+LogsId.id(IMSI)+"FAIL setTimeDefineOperator");
            e.printStackTrace();
        }
    }

    private void setTimeBeginWork(){
        try{
            String strQuery = new StringBuilder().append("update smssystem.my_scheduler Set time_begin_work=now() where sim_name=").append(this.sim_name).toString();
            DBconnectNEW.executeQuery(strQuery);
        } catch (Exception e){
            System.out.println(LogsT.printDate()+LogsId.id(IMSI)+"FAIL setTimeBeginWork");
            e.printStackTrace();
        }
    }

    private void setTimeBeginPause(){
        try{
            String strQuery = new StringBuilder().append("update smssystem.my_scheduler Set time_begin_pause=now() where sim_name=").append(this.sim_name).toString();
            DBconnectNEW.executeQuery(strQuery);
        } catch (Exception e){
            System.out.println(LogsT.printDate()+LogsId.id(IMSI)+"FAIL setTimeBeginPause");
            e.printStackTrace();
        }
    }

    private void setTimeEndPause(){
        try{
            String strQuery = new StringBuilder().append("update smssystem.my_scheduler Set time_end_pause=now() where sim_name=").append(this.sim_name).toString();
            DBconnectNEW.executeQuery(strQuery);
        } catch (Exception e){
            System.out.println(LogsT.printDate()+LogsId.id(IMSI)+"FAIL setTimeEndPause");
            e.printStackTrace();
        }
    }

    private void setTimeEndWork(){
        try{
            String strQuery = new StringBuilder().append("update smssystem.my_scheduler Set time_end_work=now() where sim_name=").append(this.sim_name).toString();
            DBconnectNEW.executeQuery(strQuery);
        } catch (Exception e){
            System.out.println(LogsT.printDate()+LogsId.id(IMSI)+"FAIL setTimeEndWork");
            e.printStackTrace();
        }
    }

    private void dial(){
        String strDial="";
        int num=0;
        int duration=0;
        for(int i=0; i<5; i++){
            boolean check = false;
            try{
                num = findNumForCall();
                duration = getRandom(100);
                strDial = new StringBuilder().append("http://localhost/smb_scheduler/api.php?username=root&password=gf9e44s2&set=dial&line=")
                        .append(this.line_name).append("&num=0").append(num).append("&duration=").append(duration).toString();
                System.out.println(LogsT.printDate() + LogsId.id(IMSI) + "dial().strDial: "+strDial);
                check = true;
            }catch (Exception e){
                System.out.println(LogsT.printDate() + LogsId.id(IMSI) +"ERROR! SchedulerOld.Sim.dial() FAIL build strDial");
                e.printStackTrace();
            }
            if (check){
                check=false;
                try{
                    URL url = new URL(strDial);
                    try (BufferedReader reader = new BufferedReader(new InputStreamReader(url.openStream(), "UTF-8"))) {
                        for (String line; (line = reader.readLine()) != null;) {
                            System.out.println(LogsT.printDate()+LogsId.id(IMSI)+line);
                            Matcher matcher_ok = PATTERN_OK.matcher(line);
                            if (matcher_ok.find()){
                                System.out.println(LogsT.printDate()+LogsId.id(IMSI)+"OK dial! Number:"+num+"; duration:"+duration);
                                check=true;
                            }
                        }
                    }
                    if (!check){
                        System.out.println(LogsT.printDate()+LogsId.id(IMSI)+"WARNING. Dial maybe not done. Number:"+num+"; duration:"+duration);
                    }
                } catch (Exception e){
                    System.out.println(LogsT.printDate()+LogsId.id(IMSI)+"ERROR. Some error in SchedulerOld.Sim.dial()");
                    e.printStackTrace();
                }
            }
            if (check) break;
        }
    }

    private int findNumForCall() throws SQLException {
        // в базе хранятся номер в формате 501234567
        int num=0;
        try {
            String strQuery = new StringBuilder().append("select num from smssystem.auto_prozvon where operator='").append(this.operator).append("' order by rand() limit 1").toString();
            System.out.println(LogsT.printDate() + LogsId.id(IMSI) + "findNumForCall().strQuery: " + strQuery);
            ArrayList<HashMap> result = DBconnectNEW.getResultSet(strQuery);
            num=GetVal.getInt(result.get(0),"num");
        } catch (Exception e){
            System.out.println(LogsT.printDate()+LogsId.id(IMSI)+"FAIL findNumForCall()");
            e.printStackTrace();
        }
        return num;
    }

    private int getRandom(int summ){
        Random random = new Random();
        int a = random.nextInt(summ);
        if (a<26){
            a=25;
        }
        return a;
    }

    private void disableSimAndEndThread(Thread ct) throws SQLException, InterruptedException {
        System.out.println(LogsT.printDate() + LogsId.id(IMSI) + "SchedulerOld.Sim.disableSimAndEndThread() start...");
        System.out.println(LogsT.printDate() + LogsId.id(IMSI) + "SchedulerOld.Sim.disableSimAndEndThread() setNonairInSimcards():");
        setNonairInSimcards();
        System.out.println(LogsT.printDate() + LogsId.id(IMSI) + "SchedulerOld.Sim.disableSimAndEndThread() disableSim():");
        disableSim();
        System.out.println(LogsT.printDate() + LogsId.id(IMSI) + "SchedulerOld.Sim.disableSimAndEndThread() Thread:" + ct.getName() + " Thread.currentThread().interrupt():");
        System.out.println(LogsT.printDate()+LogsId.id(IMSI)+"SchedulerOld.Sim.disableSimAndEndThread() isInterrupted(): "+ct.isInterrupted());
        if(ct.isInterrupted()){
            System.out.println(LogsT.printDate()+LogsId.id(IMSI)+"SchedulerOld.Sim.disableSimAndEndThread() throw new InterruptedException()");
            throw new InterruptedException();
        }
        Thread.currentThread().interrupt();
        if(ct.isInterrupted()){
            System.out.println(LogsT.printDate() + LogsId.id(IMSI) + "SchedulerOld.Sim.disableSimAndEndThread() throw new InterruptedException()");
            throw new InterruptedException();
        }
    }


}
