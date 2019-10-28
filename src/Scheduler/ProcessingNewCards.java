package Scheduler;

import DB.DBconnectVPS;
import DB.GetVal;
import LogsParts.LogsT;

import javax.net.ssl.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.X509Certificate;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;

import static java.lang.Thread.sleep;

public class ProcessingNewCards {

    private static final String USERGOIP="admin";
    private static final String PASSGOIP="itlekth2017";


    final class IccidAndImsi{

        private long iccid;
        private long imsi;

        public IccidAndImsi(long iccid, long imsi){
            this.iccid=iccid;
            this.imsi=imsi;
        }

        public long getIccid(){
            return this.iccid;
        }

        public long getImsi(){
            return this.imsi;
        }
    }


    public void definedSimsOnSlots(){
        // сравнивает новые карты из smssystem.my_scheduler c портами в банках где стоят карточки и переводит статус карты card_status=11(new card)
        boolean check = true;
        String update = "UPDATE smssystem.my_scheduler as sms join scheduler.sim as ss on sms.sim_id=ss.id Set sms.card_status=11 where sms.card_status=10 and ss.sim_login>0";
        System.out.println(LogsT.printDate() + "|definedSimsOnSlots()| update: "+ update);
        try {
            DBconnectVPS.executeQuery(update);
            System.out.println(LogsT.printDate() + "|definedSimsOnSlots()| update DONE");
        } catch (SQLException e) {
            check=false;
            e.printStackTrace();
        }

    }

    public void definedEmptySlots(){
        // Переводит card_status=10, если аналогичный порт в банке оказывается пустым
        boolean check = true;
        String selectNameColumns = "select COLUMN_NAME from information_schema.COLUMNS where TABLE_SCHEMA='smssystem' and TABLE_NAME='my_scheduler' and COLUMN_NAME not in ('id', 'sim_id')";
        String selectIdFromMyScheduler = "select sms.id from smssystem.my_scheduler as sms join scheduler.sim as ss on sms.sim_id=ss.id where ss.sim_login=0";
        ArrayList<HashMap> resNameColumns = null;
        try {
            resNameColumns= DBconnectVPS.getResultSet(selectNameColumns);
        } catch (SQLException e) {
            check = false;
            e.printStackTrace();
        }
        ArrayList<HashMap> resIdFromMyScheduler = null;
        try {
            resIdFromMyScheduler= DBconnectVPS.getResultSet(selectIdFromMyScheduler);
        } catch (SQLException e) {
            check = false;
            e.printStackTrace();
        }
        if (!check) {
            System.out.println(LogsT.printDate() + "|definedEmptySlots()| ERROR check=false -- return");
            return;
        }
        StringBuilder strBuildSetUpdate = new StringBuilder();
        for (HashMap hm : resNameColumns){
            String columnName = GetVal.getStr(hm, "COLUMN_NAME");
            strBuildSetUpdate.append("`").append(columnName).append("`=default, ");
        }
        String strSetUpdate = strBuildSetUpdate.toString();
        if (strSetUpdate.length()>3){
            strSetUpdate=strSetUpdate.substring(0, strSetUpdate.length()-2);
        }
        for (HashMap hm : resIdFromMyScheduler){
            int id = GetVal.getInt(hm, "id");
            String update = new StringBuilder().append("UPDATE smssystem.my_scheduler Set ").append(strSetUpdate).append(" where id=").append(id).toString();
            //System.out.println(LogsT.printDate() + "|definedEmptySlots()| update: " + update);
            try {
                DBconnectVPS.executeQuery(update);
                System.out.println(LogsT.printDate() + "|definedEmptySlots()| id update" + id + " DONE");
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    public void processingNewSim(int id){
        int sim;
        int line;

    }

    // возвращает ip гейта симки по входящему id из my_scheduler
    public String getIpGoip(int id) throws SQLException {
        String ip;
        int lineName=0;
        try {
            lineName = getLineName(id);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        String select = "SELECT host FROM goip.goip where name="+lineName;
        System.out.println(LogsT.printDate() + "|getIpGoip()| select: " + select);
        ArrayList<HashMap> res = DBconnectVPS.getResultSet(select);
        ip=GetVal.getStr(res.get(0), "host");
        System.out.println(LogsT.printDate() + "|getIpGoip()| host: " + ip);
        if (ip.length()>5) return ip;
        else return "";
    }

    // возвращает порт симки в гейте по входящему id из my_scheduler
    public int getPortGoip(int id){
        int port = 0;

        return  port;
    }

    // возвращает line_name из goip.goip или scheduler.device_line по входящему id из my_scheduler // 2220502
    public int getLineName(int id) throws SQLException {
        int lineName = 0;
        String select = "select case when sdl.line_name>0 then sdl.line_name else 0 end as line_name from smssystem.my_scheduler as sms left join scheduler.device_line as sdl on sms.line_id=sdl.id where sms.id = "+id;
        System.out.println(LogsT.printDate() + "|getLineName()| select: " + select);
        ArrayList<HashMap> res = DBconnectVPS.getResultSet(select);
        lineName = GetVal.getInt(res.get(0), "line_name");
        System.out.println(LogsT.printDate()+"|getLineName()| lineName:"+lineName);
        return lineName;
    }

    // возвращает imsi если все условия совпадают, а именно gsm_status=LOGIN, carrier!=null, iccid!=null. Если что-то не совпадает, возвращает объект IccidAndImsi с нулями
    public IccidAndImsi getIccidAndImsiFromDB (int line) throws SQLException {
        long imsi = 0L;
        long iccid = 0L;
        String select = "SELECT `iccid`, case when `iccid`>'' and `gsm_status`='LOGIN' and `carrier`>'' then cast(`imsi` as signed) else 0 end as imsi_long FROM goip.goip where name = " + line;
        System.out.println(LogsT.printDate()+"|getIccidAndImsiFromDB| select:"+select);
        ArrayList<HashMap> res = DBconnectVPS.getResultSet(select);
        iccid = GetVal.getLong(res.get(0), "iccid");
        imsi = GetVal.getLong(res.get(0), "imsi");
        System.out.println(LogsT.printDate()+"|getIccidAndImsiFromDB| iccid: "+iccid+" imsi:"+imsi);
        IccidAndImsi iai = new IccidAndImsi(iccid, imsi);
        return iai;
    }

    public IccidAndImsi getIccidAndImsi(int id) throws NoSuchAlgorithmException, KeyManagementException, IOException, InterruptedException, SQLException {
        final String USER_AGENT = "Mozilla/5.0";
        long iccid=0;
        long imsi=0;
        String ip = getIpGoip(id);
        int port = getPortGoip(id);
        String strUrl = new StringBuilder().append(USERGOIP).append(":").append(PASSGOIP).append("@").append(ip).append("/default/en_US/status.xml?type=gsm").toString();
        System.out.println(LogsT.printDate() + "|getIccidAndImsi()| strUrl:"+strUrl);
        //admin:itlekth2017@192.168.131.32/default/en_US/status.xml?type=gsm
        ///////////////////////////////////
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
        ////////////////////////////////////
        ////////////////////////
        URL url = new URL(strUrl);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("POST");
        conn.setRequestProperty("User-Agent", USER_AGENT);
        conn.setRequestProperty("Accept-Language", "en-US,en;q=0.5");
        int responseCode = 8888;
        System.out.println("\n"+LogsT.printDate() + "Sending 'POST' request to URL : " + url);
        System.out.println("Response Code : " + responseCode);
//-->отправка
        //сервер еще не знает о наших намерениях отправить запрос
        BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
        //запрос отправлен и можно читать ответ
        try{ responseCode = conn.getResponseCode(); } catch (Exception e){ System.out.println(e); }
        System.out.println(LogsT.printDate() + "status: "+responseCode);
        String outputLine;
        String outputData="";

        int i = 0;
        while (i<10) {
            try{ responseCode = conn.getResponseCode(); }
            catch (Exception e){ System.out.println(e); }
            if ((outputLine = in.readLine()) != null){
                outputData=outputData+outputLine;
                System.out.println(LogsT.printDate() + "outputLine = "+outputLine );
            }
            else{
                System.out.println(LogsT.printDate() + "No outputLine : "+outputLine );
                i++;
                sleep(10);
            }
        }
        System.out.println(LogsT.printDate() + "status: "+responseCode);


        return new IccidAndImsi(iccid, imsi);
    }

}
