package Web;

import DB.DBconnectSelect;
import Sims.Simcard;
import sms.Sms;

import javax.net.ssl.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.security.cert.X509Certificate;
import java.sql.SQLException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static java.lang.Thread.sleep;

public class HttpUrlOld {

    private final String USER_AGENT = "Mozilla/5.0";

    public HttpUrlOld(Sms sms, Simcard simcard) throws Exception {
        DBconnectSelect db = new DBconnectSelect(new StringBuilder(200)
                .append("select gg.id from goip.goip as gg join smssystem.simcards as ss on gg.name=")
                .append(simcard.getPrefix())
                .toString());
        int idSimcard = 0;
        try {
            while (db.getRs().next()){
                idSimcard = db.getRs().getInt("id");
                break;
            }
        } catch (SQLException e){
            e.toString();
        }

        if(idSimcard != 0) {
            String url = new StringBuilder(500)
                    .append("https://dima:19910107@10.22.0.1/goip/en/dosend.php?USERNAME=root&PASSWORD=SW0iyMG5om&smsgoip=").append(idSimcard)
                    .append("&smsprovider=1&smsnum=")
                    .append(sms.getDst_num())
                    .append("&method=2&Memo='")
                    .append(sms.getText())
                    .append("'")
                    .toString();
            System.out.println(url);
            sendPost(url);
        }
        else {
            String stringError = new StringBuilder(300)
                    .append("Error! idSimcard was not choose. id_sms:")
                    .append(sms.getId())
                    .append(" corp:")
                    .append(simcard.getCorp())
                    .append(" prefix:")
                    .append(simcard.getPrefix())
                    .toString();
            System.out.println(stringError);
            System.err.println(stringError);
        }
    }


    // HTTP GET request
    public void sendGet(String url) throws Exception {
//https://10.22.0.1/goip/en/dosend.php?USERNAME=root&PASSWORD=SW0iyMG5om&smsgoip=185&smsprovider=1&smsnum=730421302&method=2&Memo=hello test1
        //String url = "https://10.22.0.1/goip/en/dosend.php?USERNAME=root&PASSWORD=SW0iyMG5om&smsgoip=185&smsprovider=1&smsnum=730421302&method=2&Memo=hello test from java class HttpUrlOld";

        URL obj = new URL(url);
        HttpURLConnection con = (HttpURLConnection) obj.openConnection();

        // optional default is GET
        con.setRequestMethod("GET");

        //add request header
        con.setRequestProperty("User-Agent", USER_AGENT);

        String responseCode = con.getResponseMessage();
//        OutputStream os = con.getOutputStream();

        System.out.println("Sending 'GET' request to URL : " + url);
        System.out.println("con.toString() "+con.toString());
//        System.out.println("OutputStream" + os.toString());
        //System.out.println("Response Code : " + responseCode);

        BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
        String inputLine;
        StringBuffer response = new StringBuffer();

        while ((inputLine = in.readLine()) != null) {
            response.append(inputLine);
        }
        in.close();

        //print result
        System.out.println(response.toString());

    }

    // HTTP POST request
    public void sendPost(String StringUrl) throws Exception {

        TrustManager[] trustAllCerts = new TrustManager[] {new X509TrustManager() {
            public java.security.cert.X509Certificate[] getAcceptedIssuers() {
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

        int status;
  //      try{
            URL url = new URL(StringUrl);
            URLConnection conn = url.openConnection();

            //сервер еще не знает о наших намерениях отправить запрос
            BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            //запрос отправлен и можно читать ответ
                            //status = conn.getResponseCode();
            //Pattern resend = Pattern.compile("resend\\.php\\?messageid=[0-9]+");//.USERNAME=root.PASSWORD=SW0iyMG5om");
            Pattern resend = Pattern.compile("resend");
            String inputLine;
            int i = 0;
            while (true && i<3) {
                if ((inputLine = in.readLine()) != null){
                    System.out.println("inputLine = "+inputLine );
                    Matcher matcher = resend.matcher(inputLine);
                    if(matcher.matches()){
                        String s = matcher.group();
                        String s1 = matcher.group(1);
                        String s2 = matcher.group(2);
                        String s3 = matcher.replaceAll("resend");
                        String s4 = matcher.replaceFirst("resend");
                        System.out.println(s);
                        System.out.println(s1);
                        System.out.println(s2);
                        System.out.println(s3);
                        System.out.println(s4);
                    }
                }
                else{
                    sleep(3000);
                    System.out.println(111111111);
                    i++;
                }
            }
            try {
                in.close();
            } catch (IOException ex) {
                ex.printStackTrace();
            }

    //    } finally {}

        /*
        //String url = "https://10.22.0.1/goip/en/dosend.php?USERNAME=root&PASSWORD=SW0iyMG5om&smsgoip=185&smsprovider=1&smsnum=730421302&method=2&Memo=hello test from java class HttpUrlOld getPost";
        String url = "https://10.22.0.1/goip/en/dosend.php";
        URL obj = new URL(url);
        HttpsURLConnection con = (HttpsURLConnection) obj.openConnection();

        //add reuqest header
        con.setRequestMethod("POST");
        con.setRequestProperty("User-Agent", USER_AGENT);
        con.setRequestProperty("Accept-Language", "en-US,en;q=0.5");

        String urlParameters = "USERNAME=root&PASSWORD=SW0iyMG5om&smsgoip=185&smsprovider=1&smsnum=730421302&method=2&Memo=hello test from java class HttpUrlOld getPost";
        //String urlParameters = "sn=C02G8416DRJM&cn=&locale=&caller=&num=12345";

        // Send post request
        con.setDoOutput(true);
        DataOutputStream wr = new DataOutputStream(con.getOutputStream());
        wr.writeBytes(urlParameters);
        wr.flush();
        wr.close();

        int responseCode = con.getResponseCode();
        System.out.println("\nSending 'POST' request to URL : " + url);
        System.out.println("Post parameters : " + urlParameters);
        System.out.println("Response Code : " + responseCode);

        BufferedReader in = new BufferedReader(
                new InputStreamReader(con.getInputStream()));
        String inputLine;
        StringBuffer response = new StringBuffer();

        while ((inputLine = in.readLine()) != null) {
            response.append(inputLine);
        }
        in.close();

        //print result
        System.out.println(response.toString());
*/
    }

}
