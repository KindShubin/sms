package Send;

import DB.DBconnectVPS;
import DB.GetVal;
import GoipProvider.OurProvider;
import LogsParts.LogsId;
import LogsParts.LogsT;
import Sims.Simcard;
import Web.HttpUrl1;
import sms.Operators;
import sms.Sms;

import java.sql.SQLException;
import java.util.AbstractList;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

import static java.lang.Math.min;
import static java.lang.Thread.sleep;

public class SendGoip implements iSend{

    OurProvider op;
    Number logId;
    Sms sms;
    Operators operator;

    SendGoip(Sms sms, OurProvider op) throws Exception {
        this.sms=sms;
        this.op=op;
        this.logId=sms.getId();
        this.operator=sms.getOperator();
        System.out.println(LogsT.printDate() + LogsId.id(logId) + "SendGoip. send with goip!!!");
        op.printOurProvider();
        System.out.println(LogsT.printDate() + LogsId.id(logId) + "SendGoip. sum_percents: " + op.getGeneralSumPercents());
        if (op.getId_providers()==20 && operator!=Operators.noData){
            sendAllCorp(operator);
        } else sendCorp();
    }

    @Override
    public void sendSms(Sms sms, int id_provider) throws SQLException {
    }

    @Override
    public String giveStatus(Sms sms) {
        return null;
    }

    @Override
    public void updateStatus(Sms sms) throws SQLException {
        String update = new StringBuilder("update smssystem.smslogs as ss SET ss.status='").append(sms.getStatus()).append("' WHERE id=").append(sms.getId()).toString();
        DBconnectVPS.executeQuery(update);
    }

    private void sendCorp() throws SQLException {
        boolean checkSend = false;
        try{ this.sms.statusProcessing(); }
        catch (Exception s){
            System.out.println(LogsT.printDate() + LogsId.id(logId) + "SendGoip FAIL this.sms.statusProcessing()");
            s.printStackTrace();
        }
        int qntCorpsInProv = op.qntCorpsInProvider();
        for(int i=0; i<qntCorpsInProv; i++) {
            System.out.println(LogsT.printDate() + LogsId.id(logId) + "SendGoip.sendCorp() define corp in provider "+op.getId_providers()+" i="+i+" from "+qntCorpsInProv);
            String corp = chooseCorp(op);
            if (corp == "N/A") {
                System.out.println(LogsT.printDate() + LogsId.id(logId) + "!!! current thread is "+Thread.currentThread().getName());
                //throw new IllegalArgumentException(LogsT.printDate() + LogsId.id(logId) + "corp is N/A. Undefined corp in BD");
                System.out.println(LogsT.printDate() + LogsId.id(logId) + "SendGoip. corp is N/A. Undefined corp in BD");
                break;
            }
            System.out.println(LogsT.printDate() + LogsId.id(logId) + "SendGoip. corp: " + corp);
            System.out.println(LogsT.printDate() + LogsId.id(logId) + "SendGoip. dst Operator: " + this.sms.getOperator());
            Simcard simcard =null;
            int qntAllSimsInCorp=qntAllFreeSimcard(this.sms,corp);
            System.out.println(LogsT.printDate() + LogsId.id(logId) + "|SendGoip.sendCorp()| qntAllSimsInCorp = "+qntAllSimsInCorp+" min(2, qntAllSimsInCorp):"+min(2, qntAllSimsInCorp));
            //поменял j<qntAllSimsInCorp; на min(2, qntAllSimsInCorp) чтобы переотправок было не больше 2
            for(int j=0; j<min(2, qntAllSimsInCorp); j++) {
                System.out.println(LogsT.printDate() + LogsId.id(logId) + "перебор доступных карт в corp="+corp+" j="+j+" из "+min(2, qntAllSimsInCorp));
                if (simcard == null)
                    simcard = chooseFreeSimcard(this.sms, corp, 0);
                else simcard = chooseFreeSimcard(this.sms, corp, simcard.getImsi());//выбор следующей карты без учета предыдущей
//                if (simcard != null && checkAvailabaleSimcard(simcard)) {
//                    System.out.println(LogsT.printDate() + LogsId.id(logId) + "SendGoip.sendCorp() simcard is not null and simcard past check on available");
//                    try {
//                        /////////////////
//                        //HttpUrl httpUrl = new HttpUrl(this.sms, simcard);
//                        HttpUrl1 httpUrl = new HttpUrl1(this.sms, simcard);
//                        //////////////////////
//                        System.out.println(LogsT.printDate() + LogsId.id(logId) + "SendGoip. chekSend: "+checkSend);
//                        //System.out.println(LogsT.printDate() + LogsId.id(logId) + "!!! current thread is "+Thread.currentThread().getName());
//                        checkSend = httpUrl.run();
//                        break;
//                    }catch(Exception e){
//                        System.out.println(LogsT.printDate() + LogsId.id(logId) + "SendGoip. httpUrl is failed");
//                        e.printStackTrace();
//                        break;
//                    }
//                    // вызов python scripts  с входными параметрами: prefix simcard.getPrefix(), номер доставки sms.getDst_num(), text sms.getText();
//                } else {
//                    //System.out.println(LogsT.printDate() + LogsId.id(logId) + "!!! current thread is "+Thread.currentThread().getName());
//                    System.out.printf(LogsT.printDate() + LogsId.id(logId) + "SendGoip. all simcards is busy. Id sms: %d, corp: %s, dst_num: %d%n", this.sms.getId(), corp, this.sms.getDst_num());
//                }
                checkSend=finalSend(simcard,corp);
                if(checkSend) break;
            }
            //System.out.println(LogsT.printDate() + LogsId.id(logId) + "!!! current thread is "+Thread.currentThread().getName());
            System.out.println(LogsT.printDate() + LogsId.id(logId) + "SendGoip. chekSend: "+checkSend);
            if (checkSend) break;
            else {
                System.out.println(LogsT.printDate() + LogsId.id(logId) + "SendGoip. sms.statusProcessing()");
                try{
                    this.sms.statusProcessing();
                    System.out.println(LogsT.printDate() + LogsId.id(logId) + "SendGoip. sms.statusProcessing() is done");
                } catch (Exception s){
                    System.out.println(LogsT.printDate() + LogsId.id(logId) + "SendGoip FAIL this.sms.statusProcessing()");
                    s.printStackTrace();
                }
                System.out.printf(LogsT.printDate() + LogsId.id(logId) + "SendGoip. all simcards is busy. Id sms: %d, corp: %s, dst_num: %d%n", this.sms.getId(), corp, this.sms.getDst_num());
                //break;
            }
            System.out.println(LogsT.printDate() + LogsId.id(logId) + "SendGoip proceed to next corp...");
    }
        System.out.println(LogsT.printDate() + LogsId.id(logId) + "SendGoip final process");
        //System.out.println(LogsT.printDate() + LogsId.id(logId) + "!!! current thread is "+Thread.currentThread().getName());
        System.out.println(LogsT.printDate() + LogsId.id(logId) + "SendGoip. chekSend: "+checkSend);
        if (checkSend == false){
            System.out.println(LogsT.printDate() + LogsId.id(logId) + "SendGoip. sms.statusWait()");
            try{ this.sms.statusWait(); }
            catch (Exception s){
                System.out.println(LogsT.printDate() + LogsId.id(logId) + "SendGoip FAIL this.sms.statusWait()");
                s.printStackTrace();
            }
            System.out.println(LogsT.printDate() + LogsId.id(logId) + "SendGoip. sms.delProvider()");
            try{ this.sms.delProvider(); }
            catch (Exception s){
                System.out.println(LogsT.printDate() + LogsId.id(logId) + "SendGoip FAIL this.sms.delProvider()");
                s.printStackTrace();
            }
        }
        //System.out.println(LogsT.printDate() + LogsId.id(logId) + "!!! current thread in SendGoip end is "+Thread.currentThread().getName());
    }

    private void sendAllCorp(Operators operator) throws SQLException {
        boolean checkSend=false;
        for (int i=1; i<=5; i++){
            System.out.println(LogsT.printDate() + LogsId.id(logId) + "SendGoip.sendAllCorp attempt "+i);
            String queryFineSims = new StringBuilder().append("select imsi from smssystem.simcards where corp is not null and prefix>0 and block=1 and availability='Y' and onair='Y' and count_permonth>0 and count_perday>0 and count_perhour>0 and count_permin>0 ")
                .append("and corp regexp 'l' and corp not like '%bsg%' and corp not like '%pp_%' and corp not like '%Ldm%' ")
                .append("and count_permonth_").append(operator).append(">0 and count_perday_").append(operator).append(">0 and count_perhour_").append(operator).append(">0 and count_permin_").append(operator).append(">0 ")
                .append("order by rand() limit 1").toString();
            System.out.println(LogsT.printDate() + LogsId.id(logId) + "SendGoip.sendAllCorp queryFineSims:" + queryFineSims);
            Simcard simcard = null;
            try {
                ArrayList<HashMap> result = DBconnectVPS.getResultSet(queryFineSims);
                long imsi = GetVal.getLong(result.get(0), "imsi");
                System.out.println(LogsT.printDate() + LogsId.id(logId) + "SendGoip.sendAllCorp IMSI:" + imsi);
                simcard = new Simcard(imsi);
            } catch (Exception e){
                System.out.println(LogsT.printDate() + LogsId.id(logId) + "SendGoip.sendAllCorp FAIL find imsi. Simcard not created");
                e.printStackTrace();
            }
            String corp;
            try{ corp = simcard.getCorp();}
            catch (Exception e){
                corp = "";
                System.out.println(LogsT.printDate() + LogsId.id(logId) + "SendGoip.sendAllCorp FAIL find corp="+corp);
                e.printStackTrace();
            }
//            if (simcard != null && checkAvailabaleSimcard(simcard)) {
//                //System.out.println(LogsT.printDate() + LogsId.id(logId) + "!!! current thread is "+Thread.currentThread().getName());
//                try {
//                    /////////////////
//                    //HttpUrl httpUrl = new HttpUrl(this.sms, simcard);
//                    HttpUrl1 httpUrl = new HttpUrl1(this.sms, simcard);
//                    //////////////////////
//                    System.out.println(LogsT.printDate() + LogsId.id(logId) + "SendGoip. chekSend: "+checkSend);
//                    //System.out.println(LogsT.printDate() + LogsId.id(logId) + "!!! current thread is "+Thread.currentThread().getName());
//                    checkSend = httpUrl.run();
//                    break;
//                }catch(Exception e){
//                    System.out.println(LogsT.printDate() + LogsId.id(logId) + "SendGoip. httpUrl is failed");
//                    e.printStackTrace();
//                    break;
//                }
//                // вызов python scripts  с входными параметрами: prefix simcard.getPrefix(), номер доставки sms.getDst_num(), text sms.getText();
//            } else {
//                //System.out.println(LogsT.printDate() + LogsId.id(logId) + "!!! current thread is "+Thread.currentThread().getName());
//                System.out.printf(LogsT.printDate() + LogsId.id(logId) + "SendGoip. simcards is busy. Id sms: %d, corp: %s, dst_num: %d%n", this.sms.getId(), simcard.getCorp(), this.sms.getDst_num());
//            }
            checkSend = finalSend(simcard, corp);
            System.out.println(LogsT.printDate() + LogsId.id(logId) + "SendGoip. chekSend: "+checkSend);
            if (checkSend) break;
            else {
                System.out.println(LogsT.printDate() + LogsId.id(logId) + "SendGoip. sms.statusProcessing()");
                try{ this.sms.statusProcessing(); }
                catch (Exception s){
                    System.out.println(LogsT.printDate() + LogsId.id(logId) + "SendGoip FAIL this.sms.statusProcessing()");
                    s.printStackTrace();
                }
                System.out.printf(LogsT.printDate() + LogsId.id(logId) + "SendGoip. all simcards is busy. Id sms: %d, corp: %s, dst_num: %d%n", this.sms.getId(), corp, this.sms.getDst_num());
                //break;
            }
        }
        System.out.println(LogsT.printDate() + LogsId.id(logId) + "SendGoip. chekSend: "+checkSend);
        if (checkSend == false){
            System.out.println(LogsT.printDate() + LogsId.id(logId) + "SendGoip. sms.statusWait()");
            try{
                try {
                    sleep(30000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                this.sms.statusWait();
                //временно убрал 07.03.2108
                // после неудачной отправки смс опять переходит в статус WAIT...
                //
                // добавил обратно 06.04.2018
                // нихера не работает из-за этого.
            }
            catch (Exception s){
                System.out.println(LogsT.printDate() + LogsId.id(logId) + "SendGoip FAIL this.sms.statusWait()");
                s.printStackTrace();
            }
            System.out.println(LogsT.printDate() + LogsId.id(logId) + "SendGoip. sms.delProvider()");
            try{ this.sms.delProvider(); }
            catch (Exception s){
                System.out.println(LogsT.printDate() + LogsId.id(logId) + "SendGoip FAIL this.sms.delProvider()");
                s.printStackTrace();
            }
        }
    }

    public boolean finalSend(Simcard simcard, String corp) throws SQLException {
        long imsi;
        try{imsi=simcard.getImsi();}
        catch (Exception e){
            imsi=0L;
            System.out.println(LogsT.printDate() + LogsId.id(logId) + "SendGoip.finalSend() Fail define imsi:"+imsi);
            e.printStackTrace();
        }
        if (simcard != null && checkAvailabaleSimcard(simcard)) {
            System.out.println(LogsT.printDate() + LogsId.id(logId) + "SendGoip.finalSend() simcard is not null and simcard past check on available");
            try {
                /////////////////
                //HttpUrl httpUrl = new HttpUrl(this.sms, simcard);
                HttpUrl1 httpUrl = new HttpUrl1(this.sms, simcard);
                //////////////////////
                //System.out.println(LogsT.printDate() + LogsId.id(logId) + "SendGoip. chekSend: "+checkSend);
                //System.out.println(LogsT.printDate() + LogsId.id(logId) + "!!! current thread is "+Thread.currentThread().getName());
                //boolean resultSend = httpUrl.run();
                return httpUrl.run();
            }catch(Exception e){
                System.out.println(LogsT.printDate() + LogsId.id(logId) + "SendGoip.finalSend() httpUrl is failed");
                e.printStackTrace();
                return false;
            }
            // вызов python scripts  с входными параметрами: prefix simcard.getPrefix(), номер доставки sms.getDst_num(), text sms.getText();
        } else {
            //System.out.println(LogsT.printDate() + LogsId.id(logId) + "!!! current thread is "+Thread.currentThread().getName());
            System.out.printf(LogsT.printDate() + LogsId.id(logId) + "SendGoip.finalSend() Simcards is busy. imsi: %d, Id sms: %d, corp: %s, dst_num: %d%n", imsi, this.sms.getId(), corp, this.sms.getDst_num());
        }
        return false;
    }

    public String chooseCorp(OurProvider op){
        String corp = "N/A";
        int generalSum=op.getGeneralSumPercents();
        int rand = getRandom(generalSum)+1;
        int a = 0;
        int index = 0;
        for(int i : op.getListPercents()){
            a += i;
            if(rand<a){
                return op.getListCorp().get(index);
            }
            index++;
        }
        //System.out.println(LogsT.printDate() + LogsId.id(logId) + "!!! current thread in chooseCorp end is "+Thread.currentThread().getName());
        return corp;
    }

    private int getRandom(int summ){
        Random random = new Random();
        //System.out.println(LogsT.printDate() + LogsId.id(logId) + "!!! current thread in getRandom is "+Thread.currentThread().getName());
        return random.nextInt(summ);
    }


    public Simcard chooseFreeSimcard(Sms sms, String corp, long previousImsi){
        //System.out.println(LogsT.printDate() + LogsId.id(logId) + "!!! current thread in chooseFreeSimcard begin is "+Thread.currentThread().getName());
    //private void chooseFreeSimcard(Sms sms, String corp) throws SQLException {
        Operators op=sms.getOperator();
        String query = new StringBuilder(700).append("select ss.imsi from smssystem.simcards as ss where ss.corp='").
                append(corp).
                append("' and ss.onair='Y' and ss.availability='Y' and ss.block between 1 and 100 and ss.allowsms='Y' and ss.allowsms_").
                append(op).append("='Y' and ss.count_permonth_").
                append(op).append(">0 and ss.count_perday_").
                append(op).append(">0 and ss.count_perhour_").
                append(op).append(">0 and ss.count_permin_").
                append(op).append(">0 and ss.count_permonth>0 and ss.count_perday>0 and ss.count_perhour>0 and ss.count_permin>0 ").
                append("and ss.imsi <> ").append(previousImsi).
                append(" order by rand() limit 1").
                toString();
            System.out.println(LogsT.printDate() + LogsId.id(logId) + "SendGoip.chooseFreeSimcard. query for choose simcard:");
            System.out.println(LogsT.printDate() + LogsId.id(logId) + query);
            try {
                ArrayList<HashMap> result = DBconnectVPS.getResultSet(query);
                long imsi = GetVal.getLong(result.get(0),"imsi");
                //long imsi = selectFreeSimcards1.getRs().getLong("imsi");
                System.out.println(LogsT.printDate() + LogsId.id(logId) + "SendGoip.chooseFreeSimcard. IMSI: " + imsi);
                //selectFreeSimcards1.closeConnectionWithRs();
                return new Simcard(imsi);
            } catch (SQLException e) {
                e.printStackTrace();
                System.out.println(LogsT.printDate() + LogsId.id(logId) + "SendGoip.chooseFreeSimcard. no one simcard was choose in corp:" + corp);
            } catch (Exception e){e.printStackTrace();}
            //selectFreeSimcards1.closeConnectionWithRs();
        //System.out.println(LogsT.printDate() + LogsId.id(logId) + "!!! current thread in chooseFreeSimcard end is "+Thread.currentThread().getName());
        return null;
    }

    public int qntAllFreeSimcard(Sms sms, String corp){
        //System.out.println(LogsT.printDate() + LogsId.id(logId) + "!!! current thread in qntAllFreeSimcard begin is "+Thread.currentThread().getName());
        Operators op=sms.getOperator();
        String query = new StringBuilder(700).append("select count(ss.prefix) as qnt from smssystem.simcards as ss where ss.corp='").
                append(corp).
                append("' and ss.onair='Y' and ss.availability='Y' and ss.block between 1 and 100 and ss.allowsms='Y' and ss.allowsms_").
                append(op).append("='Y' and ss.count_permonth_").
                append(op).append(">0 and ss.count_perday_").
                append(op).append(">0 and ss.count_perhour_").
                append(op).append(">0 and ss.count_permin_").
                append(op).append(">0 and ss.count_permonth>0 and ss.count_perday>0 and ss.count_perhour>0 and ss.count_permin>0").
                toString();
        System.out.println(LogsT.printDate() + LogsId.id(logId) + "SendGoip.qntAllFreeSimcard. query for define quantaty all free simcards in corp "+corp+":");
        System.out.println(LogsT.printDate() + LogsId.id(logId) + query);
        //DBconnectSelect selectAllFreeSimcards = null;
        int qnt = 0;
        try {
            AbstractList<HashMap> result = DBconnectVPS.getResultSet(query);
            qnt = GetVal.getInt(result.get(0), "qnt");
            System.out.println(LogsT.printDate() + LogsId.id(logId) + "SendGoip.qntAllFreeSimcard. qnt: " + qnt);
        } catch (SQLException e) {
            System.err.println(LogsT.printDate() + LogsId.id(logId) + "SendGoip.qntAllFreeSimcard. error in define qnt all free simcards in corp:"+corp);
            e.printStackTrace();
        } catch (Exception e){e.printStackTrace();}
        //System.out.println(LogsT.printDate() + LogsId.id(logId) + "!!! current thread in qntAllFreeSimcard end is "+Thread.currentThread().getName());
        return qnt;
    }

    public boolean checkAvailabaleSimcard(Simcard simcard) throws SQLException {
        //System.out.println(LogsT.printDate() + LogsId.id(logId) + "!!! current thread in checkAvailabaleSimcard begin is "+Thread.currentThread().getName());
        System.out.println(LogsT.printDate() + LogsId.id(logId) + "SendGoip.checkAvailabaleSimcard. IMSI:" + simcard.getImsi());
        String query = new StringBuilder(300).append("select gsm_status from goip.goip where name=").append(simcard.getPrefix()).toString();
        String gsmStatus = "LOGOUT";
        //DBconnectSelect db = new DBconnectSelect(query);
        try {
            //db.getRs().first();
            //gsmStatus = db.getRs().getString("gsm_status");
            AbstractList<HashMap> result = DBconnectVPS.getResultSet(query);
            gsmStatus = GetVal.getStr(result.get(0), "gsm_status");
        } catch (Exception e){
            e.printStackTrace();
        }
        System.out.println(LogsT.printDate() + LogsId.id(logId) + "SendGoip.checkAvailabaleSimcard. IMSI:"+simcard.getImsi()+" status: "+gsmStatus);
        if (gsmStatus.equals("LOGIN")) {
            System.out.println(LogsT.printDate() + LogsId.id(logId) + "SendGoip.checkAvailabaleSimcard. IMSI: "+simcard.getImsi()+" return true");
            //db.closeConnectionWithRs();
            return true;
        } else {
            System.out.println(LogsT.printDate() + LogsId.id(logId) + "SendGoip.checkAvailabaleSimcard. IMSI"+simcard.getImsi()+" return false");
            //db.closeConnectionWithRs();
            //System.out.println(LogsT.printDate() + LogsId.id(logId) + "!!! current thread in checkAvailabaleSimcard end is "+Thread.currentThread().getName());
            return false;
        }
    }
}
