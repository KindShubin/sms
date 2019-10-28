package Web;

import DB.*;
import LogsParts.LogsId;
import LogsParts.LogsT;
import Run.ClassRunAsyncTimeout;
import Sims.Simcard;
import sms.Sms;

import javax.net.ssl.*;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.security.cert.X509Certificate;
import java.sql.SQLException;
import java.util.AbstractList;
import java.util.ArrayList;
import java.util.HashMap;

import static java.lang.Thread.sleep;

public class HttpUrl {

    private int timeout=5;
    private final String USER_AGENT = "Mozilla/5.0";
    private Number logId;
    private int idSimcard = 0;
    private int prefix;
    private Sms sms;
    private Simcard simcard;

    public HttpUrl(Sms sms, Simcard simcard) {
        this.sms = sms;
        this.simcard = simcard;
    }

    public boolean run() throws SQLException {
        this.logId=this.sms.getId();
        this.prefix = this.simcard.getPrefix();
        String querySimcardGoip = new StringBuilder(200)
                .append("select gg.id from goip.goip as gg join smssystem.simcards as ss on gg.name=")
                .append(this.simcard.getPrefix())
                .toString();
        AbstractList<HashMap> result = DBconnectVPS.getResultSet(querySimcardGoip);
        try {
            this.idSimcard = GetVal.getInt(result.get(0), "id");
            //this.prefix = GetVal.getInt(result.get(0), "name");
        } catch (Exception e){
            e.toString();
        }
        System.out.println(LogsT.printDate() + LogsId.id(this.logId) + "idSimcard: "+ this.idSimcard);
        if(this.idSimcard != 0) {
//            String url = new StringBuilder(500)
//                    .append("https://dima:19910107@10.22.0.1/goip/en/dosend.php?USERNAME=root&PASSWORD=SW0iyMG5om&smsgoip=")
//                    .append(idSimcard)
//                    .append("&smsprovider=1&smsnum=")
//                    .append(sms.getDst_num())
//                    .append("&method=2&Memo='")
//                    .append(sms.getText())
//                    .append("'")
//                    .toString();
//            System.out.println(url);
            //////defineTimeout(simcard);
            int simcardTimeout = this.simcard.defineTimeout();
            this.timeout = (simcardTimeout>0)? simcardTimeout : this.timeout;
            System.out.println(LogsT.printDate() + LogsId.id(this.logId) + "run simcard.runAsynchronouslyDisableSimcard(simcard):");
            //simcard.runAsynchronouslyDisableSimcard(simcard, logId);
            this.simcard.disableSimcardBeforeSend(this.simcard, this.logId);
            System.out.println(LogsT.printDate() + LogsId.id(this.logId) + "simcard IMSI " + this.simcard.getImsi() + " now is NOT available");
            System.out.println(LogsT.printDate() + LogsId.id(this.logId) + "starting sendPost......");
            this.sms.statusSending();
            try{
                sendPost(this.sms, this.idSimcard, this.simcard.getImsi(), this.simcard.getPrefix());
            }catch (SQLException e){
                e.toString();
                System.out.println(LogsT.printDate() + LogsId.id(this.logId) + "error in method sendPost(sms, idSimcard, simcard.getImsi())");
            } catch (Exception e){e.printStackTrace();}
            try{
                insertSimcardReportStatistics(this.sms, this.simcard.getImsi(), this.simcard.getPrefix());//add info in SimcardReportStatistics
            }catch (SQLException e){
                e.toString();
                System.out.println(LogsT.printDate() + LogsId.id(this.logId) + "error in method insertSimcardReportStatistics(sms, simcard.getImsi(), simcard.getPrefix());");
            } catch (Exception e){e.printStackTrace();}
            try{
                setCounterForSimcard(this.sms, this.simcard);// counters - sms.qntSms
            }catch (SQLException e){
                e.toString();
                System.out.println(LogsT.printDate() + LogsId.id(this.logId) + "error in method setCounterForSimcard(sms, simcard);");
            } catch (Exception e){e.printStackTrace();}
            //System.out.println(LogsT.printDate() + LogsId.id(logId) + "run simcard.runAsynchronouslyPause(simcard,this.timeout):");
            System.out.println(LogsT.printDate() + LogsId.id(this.logId) + "run ClassRunAsyncTimeout");
            Runnable r = new ClassRunAsyncTimeout(this.simcard, this.timeout, this.logId);
            Thread t = new Thread(r);
            t.start();
            //simcard.runAsynchronouslyPause(simcard, this.timeout, logId);
            System.out.println(LogsT.printDate() + LogsId.id(this.logId) + "запустил асинхронное выполнение паузы");
//            sleep(timeout * 1000);
//            enableSimcardAfterSend(dbSimcard, simcard.getPrefix());
//            System.out.println("simcard " + simcard.getPrefix() + " now is available");
            return true;
        }
        else {
            String stringError = new StringBuilder(300)
                    .append("Error! idSimcard was not choose. id_sms:")
                    .append(this.sms.getId())
                    .append(" corp:")
                    .append(this.simcard.getCorp())
                    .append(" imsi:")
                    .append(this.simcard.getImsi())
                    .append(" prefix:")
                    .append(this.simcard.getPrefix())
                    .toString();
            System.out.println(LogsT.printDate() + LogsId.id(this.logId) + stringError);
            //System.err.println(LogsT.printDate() + stringError);
            return false;
        }
    }

    // HTTP POST request
    private void sendPost(Sms sms, int idSimcard, long imsi, int prefix) throws Exception {

        TrustManager[] trustAllCerts = new TrustManager[] {new X509TrustManager() {
            public X509Certificate[] getAcceptedIssuers() {
                return null;
            }
            public void checkClientTrusted(X509Certificate[] certs, String authType) {
            }
            public void checkServerTrusted(X509Certificate[] certs, String authType) {
            }
        }
        };
        // Install the all-trusting trust manager
        SSLContext sc = SSLContext.getInstance("SSL");
        sc.init(null, trustAllCerts, new java.security.SecureRandom());
        HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
        // Create all-trusting host name verifier
        HostnameVerifier allHostsValid = new HostnameVerifier() {
            public boolean verify(String hostname, SSLSession session) {
                return true;
            }
        };
        // Install the all-trusting host verifier
        HttpsURLConnection.setDefaultHostnameVerifier(allHostsValid);
//////////////////////////////////////////////////////////////////

        System.out.println(LogsT.printDate() + LogsId.id(this.logId) + "исходный текст составного сообщения:" + sms.getText());
        String strText = sms.getText().toString().replace("\"", "\\\"");
        System.out.println(LogsT.printDate() + LogsId.id(this.logId) + "текст после преобразования:" + strText);
        String insertGoipMessage = new StringBuilder(300)
                .append("insert into goip.message(userid, msg, type, tel, prov, goipid, total, card_id, card, smsSystemId) values(1, \"")
                .append(strText)
                .append("\", 4, ")
                .append(sms.getDst_num())
                .append(", 1, ")
                .append(idSimcard)
                .append(", 1, 0, \"\", ")
                .append(sms.getId())
                .append(")")
                .toString();
        System.out.println(LogsT.printDate() + LogsId.id(this.logId) + "insertGoipMessage: " + insertGoipMessage);
        //insertIntoGoipMessage.getStmt().execute(insertGoipMessage);
        //insertIntoGoipMessage.executeQuery("SELECT LAST_INSERT_ID()");
        //db.getStmt().execute(insertGoipMessage);
        DBconnectVPS.executeQuery(insertGoipMessage);
        //ArrayList<HashMap> resultID = DBconnectVPS.getResultSet("SELECT LAST_INSERT_ID() as lastid");
        String strLastId = new StringBuilder().append("select id from goip.message where smsSystemId=").append(sms.getId()).append(" order by id desc").toString();
        ArrayList<HashMap> resultID = DBconnectVPS.getResultSet(strLastId);
        //db.executeQuery("SELECT LAST_INSERT_ID()");
        int idSms = GetVal.getInt(resultID.get(0),"id");
        String insertGoipSends = new StringBuilder(300)
                .append("insert into goip.sends(userid, messageid, goipid, provider, telnum, msg, received, total) values (1, ")
                .append(idSms)
                .append(", ")
                .append(idSimcard)
                .append(", 1, '+380")
                .append(sms.getDst_num())
                .append("', \"\", 0, 1)")
                .toString();
        System.out.println(LogsT.printDate() + LogsId.id(this.logId) + "idSMS: " + idSms + " insertGoipSends: " + insertGoipSends);
//        DBconnect insertIntoGoipSends = new DBconnect();
//        insertIntoGoipSends.getStmt().execute(insertGoipSends);
        //db.getStmt().execute(insertGoipSends);
        DBconnectVPS.executeQuery(insertGoipSends);
        String updateGoipIdSmsToSMSLOGSid = new StringBuilder().append("update smssystem.smslogs Set goip_id_sms=(select id from goip.sends where messageid=")
                .append(idSms).append(" order by id desc limit 1) where id=").append(sms.getId()).toString();
        String updateGoipIdSmsToSMSLOGSuniqid = new StringBuilder().append("update smssystem.smslogs Set goip_id_sms=(select id from goip.sends where messageid=")
                .append(idSms).append(" order by id desc limit 1) where uniqid=").append(sms.getUniqid()).toString();
        System.out.println(LogsT.printDate() + LogsId.id(this.logId) + "updateGoipIdSmsToSMSLOGS:");
        if (sms.getQntsms()>1) {
            System.out.println(LogsT.printDate() + LogsId.id(this.logId) + "Sms is bonding --> updateGoipIdSmsToSMSLOGSuniqid:");
            System.out.println(LogsT.printDate() + LogsId.id(this.logId) + updateGoipIdSmsToSMSLOGSuniqid);
            //db.getStmt().execute(updateGoipIdSmsToSMSLOGSuniqid);
            DBconnectVPS.executeQuery(updateGoipIdSmsToSMSLOGSuniqid);
        }
        else {
            System.out.println(LogsT.printDate() + LogsId.id(this.logId) + "qnt Sms = 1 --> updateGoipIdSmsToSMSLOGSid:");
            System.out.println(LogsT.printDate() + LogsId.id(this.logId) + updateGoipIdSmsToSMSLOGSid);
            //db.getStmt().execute(updateGoipIdSmsToSMSLOGSid);
            DBconnectVPS.executeQuery(updateGoipIdSmsToSMSLOGSid);
        }
        //db.getRs().beforeFirst();
        String urlBase = "https://dima:19910107@localhost/goip/en/resend.php";
        //String urlBase = "https://dima:19910107@10.22.0.1/goip/en/resend.php";
        String urlParameters = new StringBuilder(200)
                .append("messageid=")
                .append(idSms)
                .append("&USERNAME=root&PASSWORD=gf9e44s2")
                //.append("&USERNAME=root&PASSWORD=gf9e44s2&useSSL=false")
                //.append("&USERNAME=root&PASSWORD=SW0iyMG5om")
                .toString();
        String urlFull = new StringBuilder(200).append(urlBase).append("?").append(urlParameters).toString();
        System.out.println(LogsT.printDate() + LogsId.id(this.logId) + "urlFull: "+urlFull);
        URL url = new URL(urlFull);
        //URLConnection conn = url.openConnection();
        HttpsURLConnection conn = (HttpsURLConnection) url.openConnection();
        conn.setRequestMethod("POST");
        conn.setRequestProperty("User-Agent", USER_AGENT);
        conn.setRequestProperty("Accept-Language", "en-US,en;q=0.5");

        // Send post request
//        conn.setDoOutput(true);
//        DataOutputStream wr = new DataOutputStream(conn.getOutputStream());
//        wr.writeBytes(urlParameters);
//        wr.flush();
//        wr.close();

//        int responseCode = conn.getResponseCode();
        int responseCode = 8888;
//        int status = responseCode;
        System.out.println("\n"+LogsT.printDate() + LogsId.id(this.logId) + "Sending 'POST' request to URL : " + url);
//        System.out.println("Post parameters : " + urlParameters);
//        System.out.println("Response Code : " + responseCode);
//-->отправка
        //сервер еще не знает о наших намерениях отправить запрос
        BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
        //запрос отправлен и можно читать ответ
        try{ responseCode = conn.getResponseCode(); } catch (Exception e){ System.out.println(e); }
        System.out.println(LogsT.printDate() + LogsId.id(this.logId) + "status: "+responseCode);
        //Pattern resend = Pattern.compile("resend\\.php\\?messageid=[0-9]+");//.USERNAME=root.PASSWORD=SW0iyMG5om");
//        Pattern resend = Pattern.compile("resend");
        String inputLine;
        int i = 0;
        while (true && i<3) {
                if ((inputLine = in.readLine()) != null){
                    System.out.println(LogsT.printDate() + LogsId.id(this.logId) + "inputLine = "+inputLine );
                }
                else{
                    System.out.println(LogsT.printDate() + LogsId.id(this.logId) + "No inputLine : "+inputLine );
                    i++;
                    sleep(150);
                }
        }
        String updateSendDateSMSLOGS = new StringBuilder().append("update smssystem.smslogs Set time_send=NOW() where id=").append(sms.getId()).toString();
        String updateSendDateSMSLOGSuniqid = new StringBuilder().append("update smssystem.smslogs Set time_send=NOW() where uniqid=").append(sms.getUniqid()).toString();
        if (sms.getQntsms()>1){
            System.out.println(LogsT.printDate() + LogsId.id(this.logId) + "updateSendDateSMSLOGSuniqid: " + updateSendDateSMSLOGSuniqid);
            DBconnectVPS.executeQuery(updateSendDateSMSLOGSuniqid);
        }
        else {
            System.out.println(LogsT.printDate() + LogsId.id(this.logId) + "updateSendDateSMSLOGS: " + updateSendDateSMSLOGS);
            DBconnectVPS.executeQuery(updateSendDateSMSLOGS);
        }
//        // добавление информации в базу smsImsi: idSMS--imsi
//        String insertSmsImsi=new StringBuilder(300).append("insert smssystem.smsImsi(idSms, imsi) values(")
//                .append(sms.getId()).append(", ")
//                .append(imsi).append(")").toString();
//        String updateSmsImsi=new StringBuilder(300).append("update smssystem.smsImsi Set imsi=")
//                .append(imsi).append(" where idSms=")
//                .append(sms.getId()).toString();
//        try{
//            System.out.println(LogsT.printDate() + LogsId.id(logId) + "query insertSmsImsi: "+insertSmsImsi);
//            db.getStmt().execute(insertSmsImsi);
//        } catch (Exception e){
//            System.out.println(LogsT.printDate() + LogsId.id(logId) + "query insertSmsImsi with idSms:"+sms.getId()+" IMSI:"+imsi+" is FAIL");
//            db.getStmt().execute(updateSmsImsi);
//            System.out.println(LogsT.printDate() + LogsId.id(logId) + "query updateImsi: " + updateSmsImsi);
//        }
// заменил на simcardsStatistics
    }

    private void setCounterForSimcard(Sms sms, Simcard simcard) throws SQLException {
        String singleSms = new StringBuilder(400)
                .append("update smssystem.simcards as ss SET")
                .append(" ss.count_permin=ss.count_permin - ").append(sms.getQntsms())
                .append(", ss.count_perhour=ss.count_perhour - ").append(sms.getQntsms())
                .append(", ss.count_perday = ss.count_perday - ").append(sms.getQntsms())
                .append(", ss.count_permonth = ss.count_permonth - ").append(sms.getQntsms())
                .append(", ss.count_permin_").append(sms.getOperator()).append(" = ss.count_permin_").append(sms.getOperator()).append(" - ").append(sms.getQntsms())
                .append(", ss.count_perhour_").append(sms.getOperator()).append(" = ss.count_perhour_").append(sms.getOperator()).append(" - ").append(sms.getQntsms())
                .append(", ss.count_perday_").append(sms.getOperator()).append(" = ss.count_perday_").append(sms.getOperator()).append(" - ").append(sms.getQntsms())
                .append(", ss.count_permonth_").append(sms.getOperator()).append(" = ss.count_permonth_").append(sms.getOperator()).append(" - ").append(sms.getQntsms())
                .append(" where ss.imsi=").append(simcard.getImsi()).toString();
        System.out.println(LogsT.printDate() + LogsId.id(logId) + "String minuser counter:");
        System.out.println(LogsT.printDate() + LogsId.id(logId) + singleSms);
        DBconnectVPS.executeQuery(singleSms);
    }

    private void insertSimcardReportStatistics(Sms sms, long imsi, int prefix) throws SQLException {
        System.out.println(LogsT.printDate() + LogsId.id(logId) + "insert data to simcardsStatistics");
        //DBconnectUpdate dbu = new DBconnectUpdate();
        String strInsertToSimcardStatistics;
        String strUpdateToSimcardStatistics;
        System.out.println(LogsT.printDate() + LogsId.id(logId) + "sms.getQntsms()="+sms.getQntsms());
        if (sms.getQntsms()==1) {
            strInsertToSimcardStatistics = new StringBuilder().append("INSERT into smssystem.simcardsStatistics (idSMS, imsi, prefix, time, report, attemps) values (")
                    .append(sms.getId()).append(", ").append(imsi).append(", ").append(prefix).append(", NOW(), 0, +1)").toString();
            strUpdateToSimcardStatistics = new StringBuilder().append("update smssystem.simcardsStatistics SET imsi=").
                    append(imsi).append(", prefix=").append(prefix).append(", time=NOW(), report=0, attemps=attemps+1 where idSMS=").append(sms.getId()).toString();
        }
        else {
            //убрал в where qntsms=1 чтобы в базу заносились как реальные единичные смски, так и результирующая склееная смска
            strInsertToSimcardStatistics = new StringBuilder().append("INSERT into smssystem.simcardsStatistics (idSMS, imsi, prefix, time, report, attemps) select id, ")
                    .append(imsi).append(", ").append(prefix).append(", NOW(), 0, +1 from smssystem.smslogs where uniqid=").append(sms.getUniqid()).toString();
            strUpdateToSimcardStatistics = new StringBuilder().append("update smssystem.simcardsStatistics SET imsi=")
                    .append(imsi).append(", prefix=").append(prefix).append(", time=NOW(), report=0, attemps=attemps+1 where idSMS in (select id from smssystem.smslogs where uniqid=")
                    .append(sms.getUniqid()).append(")").toString();
        }
        System.out.println(LogsT.printDate() + LogsId.id(logId) + "strInsertToSimcardStatistics: "+strInsertToSimcardStatistics);
        System.out.println(LogsT.printDate() + LogsId.id(logId) + "strUpdateToSimcardStatistics: " + strUpdateToSimcardStatistics);
        try {
            //dbu.getStmt().execute(strInsertToSimcardStatistics);
            DBconnectVPS.executeQuery(strInsertToSimcardStatistics);
            System.out.println(LogsT.printDate() + LogsId.id(logId) + "");
        }
        catch (Exception e){
            e.toString();
            try{ DBconnectVPS.executeQuery(strUpdateToSimcardStatistics); } catch (Exception e1){ System.out.println(e1); }
        }
//        try {
//            DBconnectVPS.executeQuery(strUpdateToSimcardStatistics);
//        }
//        catch (Exception e){ e.toString(); }
    }

}
