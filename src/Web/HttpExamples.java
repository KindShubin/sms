package Web;

import javax.net.ssl.*;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.security.cert.X509Certificate;

public class HttpExamples {

    private final String USER_AGENT = "Mozilla/5.0";

    public static void main(String[] args) throws Exception {

//        HttpUrlOld http = new HttpUrlOld();
//
//        System.out.println("Testing 1 - Send Http GET request");
//        http.sendGet();
//
        System.out.println("Testing 2 - Send Http POST request");
        HttpExamples http = new HttpExamples();
        http.sendPost();
    }

    public void sendPost() throws Exception {

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
}
