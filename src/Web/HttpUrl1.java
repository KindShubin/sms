package Web;

import DB.DBconnectVPS;
import DB.GetVal;
import LogsParts.LogsId;
import LogsParts.LogsT;
import Run.ClassRunAsyncTimeout;
import Sims.Simcard;
import sms.Sms;

import javax.net.ssl.*;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.security.cert.X509Certificate;
import java.sql.SQLException;
import java.util.AbstractList;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static java.lang.Thread.sleep;

public class HttpUrl1 {

    private int timeout=5;
    private final String USER_AGENT = "Mozilla/5.0";
    private Number logId;
    private int idSimcard = 0;
    private int prefix;
    private Sms sms;
    private Simcard simcard;
    private boolean checkSend=false;
    private static final Pattern PATTERN_ERROR = Pattern.compile("ERROR");
    private static final Pattern PATTERN_ERRORSTATUS = Pattern.compile("errorstatus\\:[0-9]+");
    private static final Pattern PATTERN_FAILURE_0 = Pattern.compile("Failure\\:0");
    private static final Pattern PATTERN_FAILURE_1 = Pattern.compile("Failure\\:[1-9]");

    public HttpUrl1(Sms sms, Simcard simcard) {
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
        System.out.println(LogsT.printDate() + LogsId.id(this.logId) + "HttpUrl1.run() idSimcard: "+ this.idSimcard);
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
            int simcardTimeout = this.simcard.defineTimeout(this.logId);
            this.timeout = (simcardTimeout>0)? simcardTimeout : this.timeout;
            System.out.println(LogsT.printDate() + LogsId.id(this.logId) + "run simcard.runAsynchronouslyDisableSimcard(simcard):");
            //simcard.runAsynchronouslyDisableSimcard(simcard, logId);
            this.simcard.disableSimcardBeforeSend(this.simcard, this.logId);
            System.out.println(LogsT.printDate() + LogsId.id(this.logId) + "simcard IMSI " + this.simcard.getImsi() + " now is NOT available");
            System.out.println(LogsT.printDate() + LogsId.id(this.logId) + "starting sendPost......");
            this.sms.statusSending();
            try{
                sendPost(this.sms, this.idSimcard, this.simcard.getImsi(), this.simcard.getPrefix());
                System.out.println(LogsT.printDate() + LogsId.id(this.logId) + "sendPost() is Over");
            }catch (SQLException e){
                e.toString();
                System.out.println(LogsT.printDate() + LogsId.id(this.logId) + "error in method sendPost(sms, idSimcard, simcard.getImsi())");
            } catch (Exception e){e.printStackTrace();}
            if (this.checkSend) {
                try {
                    setCounterForSimcard(this.sms, this.simcard);// counters - sms.qntSms
                    System.out.println(LogsT.printDate() + LogsId.id(this.logId) + "setCounterForSimcard() is Done");
                } catch (SQLException e) {
                    e.toString();
                    System.out.println(LogsT.printDate() + LogsId.id(this.logId) + "error in method setCounterForSimcard(sms, simcard);");
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            System.out.println(LogsT.printDate() + LogsId.id(this.logId) + "HttpUrl setCounterForSimcard() is past --> insertSimcardReportStatistics() sms.status:"+this.sms.getStatus());
            try{
                insertSimcardReportStatistics(this.sms, this.simcard.getImsi(), this.simcard.getPrefix());//add info in SimcardReportStatistics
                System.out.println(LogsT.printDate() + LogsId.id(this.logId) + "insertSimcardReportStatistics() is Done");
            }catch (SQLException e){
                e.toString();
                System.out.println(LogsT.printDate() + LogsId.id(this.logId) + "error in method insertSimcardReportStatistics(sms, simcard.getImsi(), simcard.getPrefix());");
            } catch (Exception e){e.printStackTrace();}
                        //if (this.sms.getStatus()!="WAIT"){
                        //System.out.println(LogsT.printDate() + LogsId.id(logId) + "run simcard.runAsynchronouslyPause(simcard,this.timeout):");
            System.out.println(LogsT.printDate() + LogsId.id(this.logId) + "run ClassRunAsyncTimeout");
            Runnable r = new ClassRunAsyncTimeout(this.simcard, this.timeout, this.logId);
            Thread t = new Thread(r);
            t.start();
            //simcard.runAsynchronouslyPause(simcard, this.timeout, logId);
            System.out.println(LogsT.printDate() + LogsId.id(this.logId) + "запустил асинхронное выполнение паузы");
            //}
            return this.checkSend;
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
        String strText = sms.getText().toString().replaceAll("\"", "\\\"");
        System.out.println(LogsT.printDate() + LogsId.id(this.logId) + "текст после преобразования:" + strText);
        /*String insertGoipMessage = new StringBuilder(300)
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
        //db.getRs().beforeFirst();*/
        String strIdGoip = new StringBuilder().append("select id from goip.goip where name=").append(simcard.getPrefix()).toString();
        ArrayList<HashMap> resultID = DBconnectVPS.getResultSet(strIdGoip);
        //HashMap res = resultID.get(0);
        int idGoip = GetVal.getInt(resultID.get(0), "id");
        String urlBase = "http://localhost/goip/en/dosend.php";
        String urlParameters = new StringBuilder().append("USERNAME=root&PASSWORD=gf9e44s2&smsgoip=")
                .append(idGoip).append("&smsprovider=1&smsnum=")
                //.append(sms.getDst_num()).append("&method=2&Memo='")
                //.append(strText).append("'").toString();
                .append(sms.getDst_num()).append("&method=2&Memo=")
                .append(URLEncoder.encode(strText, "UTF-8")).toString();
        String urlFull = new StringBuilder(200).append(urlBase).append("?").append(urlParameters).toString();
        System.out.println(LogsT.printDate() + LogsId.id(this.logId) + "urlFull: "+urlFull);
        ////////////////////////
        URL url = new URL(urlFull);
        //URLConnection conn = url.openConnection();
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
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
        String outputLine;
        boolean unknownError = false;
        boolean error = false;
        boolean failure0 = false;
        boolean failure1 = false;
        String errorstatus = null;
        int i = 0;
        while (i<3) {
            try{ responseCode = conn.getResponseCode(); }
            catch (Exception e){ System.out.println(e); }
            if ((outputLine = in.readLine()) != null){
                System.out.println(LogsT.printDate() + LogsId.id(this.logId) + "outputLine = "+outputLine );
                Matcher matcherError = PATTERN_ERROR.matcher(outputLine);
                Matcher matcherFailure0 = PATTERN_FAILURE_0.matcher(outputLine);
                Matcher matcherFailure1 = PATTERN_FAILURE_1.matcher(outputLine);
                if (matcherError.find()){
                    unknownError = true;
                    System.out.println(LogsT.printDate() + LogsId.id(this.logId) + "Error send:"+matcherError.group());
                    try{
                        Matcher matcherErrorStatus = PATTERN_ERRORSTATUS.matcher(outputLine);
                        matcherErrorStatus.find();
                        error = true;
                        errorstatus = matcherErrorStatus.group();
                        System.out.println(LogsT.printDate() + LogsId.id(this.logId) + "ERROR send. bool error:"+error+" Errorstatus:"+errorstatus);}
                    catch (Exception e){System.out.println(e);}
                }
                if (matcherFailure0.find()) {
                    failure0=true;
                    System.out.println(LogsT.printDate() + LogsId.id(this.logId) + "failure0 is " +failure0+" group(): "+matcherFailure0.group());
                }
                if (matcherFailure1.find()) {
                    failure1=true;
                    System.out.println(LogsT.printDate() + LogsId.id(this.logId) + "failure1 is " +failure1+" group(): "+matcherFailure1.group());
                }
            }
            else{
                System.out.println(LogsT.printDate() + LogsId.id(this.logId) + "No outputLine : "+outputLine );
                i++;
                sleep(150);
            }
        }
        System.out.println(LogsT.printDate() + LogsId.id(this.logId) + "status: "+responseCode);
        // определяю id в goip.message и id в goip.sends
        String strQueryFindIds = new StringBuilder().append("select gm.id as idM, gs.id as idS from goip.message as gm join goip.sends as gs on gs.messageid=gm.id where gm.tel=")
            .append(sms.getDst_num()).append(" and gm.goipid=")
            .append(idGoip).append(" order by gm.id desc limit 1").toString();
        System.out.println(LogsT.printDate() + LogsId.id(this.logId) + "strQueryFindIds: " + strQueryFindIds);
                ArrayList<HashMap> result = DBconnectVPS.getResultSet(strQueryFindIds);
        System.out.println(LogsT.printDate() + LogsId.id(this.logId) + "result.get(0): "+result.get(0));
        //System.out.println(LogsT.printDate() + LogsId.id(this.logId) + "result: "+result.toString());
        int idSmsInMessages=GetVal.getInt(result.get(0), "idM");
        int idSmsInSends=GetVal.getInt(result.get(0),"idS");
        System.out.println(LogsT.printDate() + LogsId.id(this.logId) + "idSmsInMessages: "+idSmsInMessages+" idSmsInSends:"+idSmsInSends);
        // добавляю в таблицу message id смс для дальнейшего обновления статуса смс
//        String strUpdateIdsmsInMessage = new StringBuilder().append("update goip.message Set smsSystemId=")
//            .append(sms.getId()).append(" where id=(select subSelect.id from (select id from goip.message where tel=")
//            .append(sms.getDst_num()).append(" and goipid=")
//            .append(idGoip).append(" order by id desc limit 1) as subSelect)").toString();
        String strUpdateIdsmsInMessage = new StringBuilder().append("update goip.message Set smsSystemId=")
                .append(sms.getId()).append(" where id=").append(idSmsInMessages).toString();
        try{
            DBconnectVPS.executeQuery(strUpdateIdsmsInMessage);
        } catch (Exception u){
            System.out.println(LogsT.printDate() + LogsId.id(this.logId) + "FAIL! HttpUrl1.sendPost add smsId in goip.message after send sms");
            u.printStackTrace();
        }
        //Далее мониторим таблицу goip.sends поле over. Если over=1, смс отправлена. Выходим по таймауту bMAX*5сек=...сек
        // bMax - колличество итераций по 5 сек для конкртеной смс. Основано на колличестве смс внутри одной составной смс и типа клеинта.
        int bMax=getQnt5SecIterations(this.sms);
        boolean over=false;//значение из базы goip означающее отправку смс
        for(int b=0; b<bMax; b++){
            over=getOverInGoipSend(idSmsInSends);
            if (over) break;
            System.out.println(LogsT.printDate() + LogsId.id(this.logId) +"HttpUrl1.sendPost define over. b="+b+", over="+over+" --> sleep 5sec");
            sleep(5000);
        }
        System.out.printf(LogsT.printDate() + LogsId.id(this.logId) + "check error data: unknownError:%s, error:%s, errorstatus:%s, failure0:%s, failure1:%s, over:%s\n", unknownError, error, errorstatus, failure0, failure1, over);
        //проверяем условия пр неотправке смс
        String description="";
        if (failure0) {
            description = "ошибок нет. error:"+error+" errorstatus:"+errorstatus+" failure0:"+failure0+" failure1:"+failure1+" over:"+over;
        }
        if (failure1){
            description = "ошибки есть. error:"+error+" errorstatus:"+errorstatus+" failure0:"+failure0+" failure1:"+failure1+" over:"+over;
        }
        if (unknownError){
            description = "unknownError:"+unknownError+" error:"+error+" errorstatus:"+errorstatus+" failure0:"+failure0+" failure1:"+failure1+" over:"+over;
        }
        //String description="error:"+error+" failure1:"+failure1+" failure0:"+failure0+" over:"+over+" errorstatus:" + errorstatus;
        System.out.println(LogsT.printDate() + LogsId.id(this.logId) + "description:" + description);
        System.out.println(LogsT.printDate() + LogsId.id(this.logId) + "HttpUrl1.sendPost is unknown. checkSend->true; status->UNDELIVERABLE");
        this.sms.setStatus("UNDELIVERABLE");
        this.checkSend=true;
        if (error && failure1 && !over) {
            System.out.println(LogsT.printDate() + LogsId.id(this.logId) + "HttpUrl1.sendPost is FAIL. checkSend->false; status->UNDELIVERABLE(local status, not write to DB); not counting limits");
            // 07.03.2018 убрал изменения статуса на WAIT, таким образом остаетс UNDELIVERABLE т.к. смс с ошибками обновлялись со статусом WAIT
            this.checkSend=false;
        }
        if (failure0 && over){
            System.out.println(LogsT.printDate() + LogsId.id(this.logId) + "HttpUrl1.sendPost is Done. checkSend->true; status->SEND");
            this.sms.setStatus("SEND");
            this.checkSend=true;
        }
        if (unknownError && !error){
            System.out.printf(LogsT.printDate() + LogsId.id(this.logId) + "HttpUrl1.sendPost is unknownError\n");
            this.sms.setStatus("UNKNOWN");
            this.checkSend=false;
        }
        // добавляю id из goip.sends в поле goip_id_sms из smssystem.smslogs а также добавляю время отправки
        this.sms.setGoip_id_sms(idSmsInSends);
        this.sms.setTime_send_toNow();
        this.sms.setDescription(description);
        String updateGoipIdSmsToSMSLOGSid = new StringBuilder().append("update smssystem.smslogs Set time_send=NOW(), goip_id_sms=")
                .append(idSmsInSends).append(", status='").append(this.sms.getStatus()).append("', description='").append(description).append("' where id=").append(sms.getId()).toString();
        String updateGoipIdSmsToSMSLOGSuniqid = new StringBuilder().append("update smssystem.smslogs Set time_send=NOW(), goip_id_sms=")
                .append(idSmsInSends).append(", status='").append(this.sms.getStatus()).append("', description='").append(description).append("' where uniqid=").append(sms.getUniqid()).toString();
        System.out.println(LogsT.printDate() + LogsId.id(this.logId) + "updateGoipIdSmsToSMSLOGS:");
        if (sms.getQntsms()>1) {
            System.out.println(LogsT.printDate() + LogsId.id(this.logId) + "|HttpUrl.sendpost| Sms is bonding(qntsms>1) --> updateGoipIdSmsToSMSLOGSuniqid:");
            try{
                System.out.println(LogsT.printDate() + LogsId.id(this.logId) + "|HttpUrl.sendpost| updateGoipIdSmsToSMSLOGSuniqid: "+updateGoipIdSmsToSMSLOGSuniqid);
                DBconnectVPS.executeQuery(updateGoipIdSmsToSMSLOGSuniqid);
                System.out.println(LogsT.printDate() + LogsId.id(this.logId) + "|HttpUrl.sendpost| updateGoipIdSmsToSMSLOGSuniqid DONE");
            } catch (Exception e){
                System.out.println(LogsT.printDate() + LogsId.id(this.logId) + "|HttpUrl.sendpost| ERROR! updateGoipIdSmsToSMSLOGSuniqid is not done! e:");
                e.printStackTrace();
            }
        }
        else {
            System.out.println(LogsT.printDate() + LogsId.id(this.logId) + "qnt Sms = 1 --> updateGoipIdSmsToSMSLOGSid:");
            try{
                System.out.println(LogsT.printDate() + LogsId.id(this.logId) + "|HttpUrl.sendpost| updateGoipIdSmsToSMSLOGSid: " + updateGoipIdSmsToSMSLOGSid);
                DBconnectVPS.executeQuery(updateGoipIdSmsToSMSLOGSid);
                System.out.println(LogsT.printDate() + LogsId.id(this.logId) + "|HttpUrl.sendpost| updateGoipIdSmsToSMSLOGSid DONE");
            } catch (Exception e){
                System.out.println(LogsT.printDate() + LogsId.id(this.logId) + "|HttpUrl.sendpost| ERROR! updateGoipIdSmsToSMSLOGSid is not done! e:");
                e.printStackTrace();
            }
        }
//        String updateSendDateSMSLOGS = new StringBuilder().append("update smssystem.smslogs Set time_send=NOW() where id=").append(sms.getId()).toString();
//        String updateSendDateSMSLOGSuniqid = new StringBuilder().append("update smssystem.smslogs Set time_send=NOW() where uniqid=").append(sms.getUniqid()).toString();
//        if (sms.getQntsms()>1){
//            System.out.println(LogsT.printDate() + LogsId.id(this.logId) + "updateSendDateSMSLOGSuniqid: " + updateSendDateSMSLOGSuniqid);
//            DBconnectVPS.executeQuery(updateSendDateSMSLOGSuniqid);
//        }
//        else {
//            System.out.println(LogsT.printDate() + LogsId.id(this.logId) + "updateSendDateSMSLOGS: " + updateSendDateSMSLOGS);
//            DBconnectVPS.executeQuery(updateSendDateSMSLOGS);
//        }


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
        int qntSms = sms.getQntsms();
        String singleSms = new StringBuilder(400)
                .append("update smssystem.simcards as ss SET")
                .append(" ss.count_permin=ss.count_permin - ").append(qntSms)
                .append(", ss.count_perhour=ss.count_perhour - ").append(qntSms)
                .append(", ss.count_perday = ss.count_perday - ").append(qntSms)
                .append(", ss.count_permonth = ss.count_permonth - ").append(qntSms)
                .append(", ss.count_permin_").append(sms.getOperator()).append(" = ss.count_permin_").append(sms.getOperator()).append(" - ").append(qntSms)
                .append(", ss.count_perhour_").append(sms.getOperator()).append(" = ss.count_perhour_").append(sms.getOperator()).append(" - ").append(qntSms)
                .append(", ss.count_perday_").append(sms.getOperator()).append(" = ss.count_perday_").append(sms.getOperator()).append(" - ").append(qntSms)
                .append(", ss.count_permonth_").append(sms.getOperator()).append(" = ss.count_permonth_").append(sms.getOperator()).append(" - ").append(qntSms)
                .append(" where ss.imsi=").append(simcard.getImsi()).toString();
        System.out.println(LogsT.printDate() + LogsId.id(logId) + "String minuser counter:");
        System.out.println(LogsT.printDate() + LogsId.id(logId) + singleSms);
        DBconnectVPS.executeQuery(singleSms);
    }

    //2018-10-04
    // добавляю total=1 чтобы в статистику заносилось только сообщения целиком.
    // изменяю логику работы с полем report. Теперь по дефолту будет не 0 а 1
    private void insertSimcardReportStatistics(Sms sms, long imsi, int prefix) throws SQLException {
        System.out.println(LogsT.printDate() + LogsId.id(logId) + "insert data to simcardsStatistics");
        //DBconnectUpdate dbu = new DBconnectUpdate();
        String strInsertToSimcardStatistics;
        String strUpdateToSimcardStatistics;
        strInsertToSimcardStatistics = new StringBuilder().append("INSERT into smssystem.simcardsStatistics (idSMS, imsi, prefix, time, report, attemps) values (")
                .append(sms.getId()).append(", ").append(imsi).append(", ").append(prefix).append(", NOW(), 1, +1)").toString();
        strUpdateToSimcardStatistics = new StringBuilder().append("update smssystem.simcardsStatistics SET imsi=").
                append(imsi).append(", prefix=").append(prefix).append(", time=NOW(), report=1, attemps=attemps+1 where idSMS=").append(sms.getId()).toString();
        //2018-10-04
        /*System.out.println(LogsT.printDate() + LogsId.id(logId) + "sms.getQntsms()="+sms.getQntsms());
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
        }*/
        System.out.println(LogsT.printDate() + LogsId.id(logId) + "strInsertToSimcardStatistics: " + strInsertToSimcardStatistics);
        System.out.println(LogsT.printDate() + LogsId.id(logId) + "strUpdateToSimcardStatistics: " + strUpdateToSimcardStatistics);
        Exception checkEx = DBconnectVPS.executeQuery(strInsertToSimcardStatistics);
        if (checkEx==null){
            System.out.println(LogsT.printDate() + LogsId.id(logId) + "HttpUrl1.insertSimcardReportStatistics() Insert to SimcardStatistics is DONE. checkEx=null");
        } else{
            System.out.println(LogsT.printDate() + LogsId.id(logId) + "HttpUrl1.insertSimcardReportStatistics() checkEx="+checkEx.toString());
            System.out.println(LogsT.printDate() + LogsId.id(logId) +"HttpUrl1.insertSimcardReportStatistics() Insert data to simcardsStatistics FAIL --> UPDATE");
            Exception checkUpdateEx= DBconnectVPS.executeQuery(strUpdateToSimcardStatistics);
            if (checkUpdateEx == null){
                System.out.println(LogsT.printDate() + LogsId.id(logId) + "HttpUrl1.insertSimcardReportStatistics() Update to SimcardStatistics is DONE. checkUpdateEx=null");
            } else {
                System.out.println(LogsT.printDate() + LogsId.id(logId) + "HttpUrl1.insertSimcardReportStatistics()"+ checkUpdateEx.toString());
                System.out.println(LogsT.printDate() + LogsId.id(logId) + "HttpUrl1.insertSimcardReportStatistics() Update to SimcardStatistics FAIL");
            }
        }
    }

    private boolean getOverInGoipSend(Number idSend) throws SQLException {
        String query = new StringBuilder(50).append("SELECT over FROM goip.sends where id=").append(idSend).toString();
        ArrayList<HashMap> result = DBconnectVPS.getResultSet(query);
        System.out.println(LogsT.printDate() + LogsId.id(logId) + "result: "+result);
        //int over = 0;
        boolean boolOver=false;
//        try{
//            over = GetVal.getInt(result.get(0), "over");
//            System.out.println(LogsT.printDate() + LogsId.id(logId) + "HttpUrl1.getOverInGoipSend idSend:"+idSend+" over:"+over);
//        }
//        catch (Exception e){
//            System.out.println(LogsT.printDate() + LogsId.id(logId) + "Over int fail");
//            e.printStackTrace();
//        }
        try{
            boolOver = GetVal.getBool(result.get(0),"over");
            System.out.println(LogsT.printDate() + LogsId.id(logId) + "HttpUrl1.getOverInGoipSend idSend:"+idSend+" boolOver:"+boolOver);
        }
        catch (Exception e){
            System.out.println(LogsT.printDate() + LogsId.id(logId) + "Over bool fail");
            e.printStackTrace();
        }
        return boolOver;
    }

    private int getTypeOfClient(Sms sms) throws SQLException {
        int client = sms.getClient_id();
        int typyOfClient = 1; // default value is 1 - stream!
        String query = new StringBuilder().append("SELECT type FROM smssystem.clients where id=").append(client).toString();
        try {
            ArrayList<HashMap> result = DBconnectVPS.getResultSet(query);
            typyOfClient = GetVal.getInt(result.get(0), "type");
            System.out.println(LogsT.printDate() + LogsId.id(logId) + "|getTypeOfClient()| client: "+client+" typyOfClient:"+typyOfClient);
        } catch (Exception e){
            System.out.println(LogsT.printDate() + LogsId.id(logId) + "error in getTypeOfClient(Sms sms)");
            e.printStackTrace();
        }
        return typyOfClient;
    }

    private int getQnt5SecIterations(Sms sms) throws SQLException {
        int qntIterations; // qntIterations * 5sec = ...
        int typeOfClient = getTypeOfClient(sms);
        int qntSms = sms.getQntsms();
        if (typeOfClient == 1)
            qntIterations = qntSms * 6;
        else if(typeOfClient == 3 || typeOfClient == 5)
            qntIterations = qntSms * 9;
        else qntIterations = qntSms * 3;
        int qntIterationsFinal=Math.min(18, qntIterations); // огранияение в 1,5минуты , т.к. при больших составных смс кол-во итерация  может быть больше 50 ~ >5 минут
        System.out.println(LogsT.printDate() + LogsId.id(logId) + "|getQnt5SecIterations()| typeOfClient:"+typeOfClient+" qntSms:"+qntSms+" qntIterations:"+qntIterations+" qntIterationsFinal(min(18,...)):"+qntIterationsFinal);
        return qntIterationsFinal;


    }

    private int getQntResends(Sms sms){
        // пока зашито жестко 2 попытки переотправить. Решудировать буду временем ожидания статуса в goip.sends
        return 2;
    }
}
