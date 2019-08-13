package Payment;

import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.net.URLConnection;
import java.security.MessageDigest;
//import org.apache.commons.codec.digest.DigestUtils;

public class Privat {

    private static final int ID=128655;
    private static final String PASSWORD="NTF28A49777CEmmYv87NeJ6GTo1o99d7";
    private static final String URL_REFILL="https://api.privatbank.ua/p24api/directfill";
    private static int num;
    //private static double cash;
    private static int cash;
    private static int wait;


    public static void main(String[] args) {
        try {
            num=Integer.parseInt(args[0]);
            //cash=Double.parseDouble(args[1]);
            cash=Integer.parseInt(args[1]);
            wait=Integer.parseInt(args[2]);
        } catch (Exception e){
            System.out.println("ERROR");
            e.printStackTrace();
        }
        Privat p = new Privat();
        String data = p.payNum(num, cash, wait);
        p.sendRefill(data);

    }

    public void sendRefill(String data){
        try
        {
            URL url = new URL( URL_REFILL );
            URLConnection con = url.openConnection();
            // specify that we will send output and accept input
            con.setDoInput(true);
            con.setDoOutput(true);
            con.setConnectTimeout( 20000 );  // long timeout, but not infinite
            con.setReadTimeout( 20000 );
            con.setUseCaches (false);
            con.setDefaultUseCaches (false);
            // tell the web server what we are sending
            con.setRequestProperty ( "Content-Type", "text/xml" );
            OutputStreamWriter writer = new OutputStreamWriter( con.getOutputStream() );
            writer.write( data );
            writer.flush();
            writer.close();
            // reading the response
            InputStreamReader reader = new InputStreamReader( con.getInputStream() );
            StringBuilder buf = new StringBuilder();
            char[] cbuf = new char[ 2048 ];
            int num;
            while ( -1 != (num=reader.read( cbuf )))
            {
                buf.append( cbuf, 0, num );
            }
            String result = buf.toString();
            System.err.println( "\nResponse from server after POST:\n" + result );
        }
        catch( Throwable t )
        {
            t.printStackTrace( System.out );
        }
    }

    //public void payNum(int num, double cash, int wait){
    public String payNum(int num, int cash, int wait){
        String dataTag = new StringBuilder().append("<oper>cmt</oper><wait>").append(wait)
                .append("</wait><test>0</test><payment id=\"\"><prop name=\"phone\" value=\"%2B380").append(num)
                .append("\" /><prop name=\"amt\" value=\"").append(cash)
                .append("\" /></payment>").toString();
        System.out.println("dataTag:"+dataTag);
        String md5Hash=getMD5(dataTag + PASSWORD);
        System.out.println("MD5:"+md5Hash);
        String sha1Hash=getSHA1(md5Hash);
        System.out.println("SHA-1:" + sha1Hash);
        String dataXML=new  StringBuilder().append("<?xml version=\"1.0\" encoding=\"UTF-8\"?><request version=\"1.0\"><merchant><id>").append(ID)
                .append("</id><signature>").append(sha1Hash).append("</signature></merchant><data>")
                .append(dataTag).append("</data></request>")
                .toString();
        System.out.println("dataXML:"+dataXML);
        return dataXML;

    }

    public String getMD5(String input){
        String output = null;
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            md.reset();
            md.update(input.getBytes("UTF-8"));
            //output=new BigInteger(1,md.digest()).toString(16);
            output=convertToHex(md.digest());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return output;
    }


    public String getSHA1(String input) {
        String output = null;
//        output=DigestUtils.sha1Hex(input);
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-1");
            md.reset();
            md.update(input.getBytes("UTF-8"));
            output = convertToHex(md.digest());
            //output=byteToHex(md.digest());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return output;
    }
    private static String convertToHex(byte[] data) {
        StringBuffer stringBuffer = new StringBuffer();
        for (int i = 0; i < data.length; i++) {
            stringBuffer.append(Integer.toString((data[i] & 0xff) + 0x100, 16)
                    .substring(1));
        }
        return stringBuffer.toString();
        }

    }
