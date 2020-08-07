package tests;

import DB.DBconnectSMB;
import DB.DBconnectVPS;
import LogsParts.LogsT;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class test {
    //DBconnectSelect dBconnectSelect;

    public static void main(String args[]) throws SQLException, IOException {
        testDBsmb();
        //String s = "proverka";
        //int a = 0;
        //System.out.println("a: "+method(a));
        //getPrefixForSim();
    }

    public static String method(int a) {
        switch (a) {
            case 0:
                return "null";
            default:
                return String.valueOf(a);
        }
    }

    public static void testDBsmb() throws SQLException {
        String query_scheduler = "SELECT * FROM scheduler.sim limit 10";
        ArrayList<HashMap> result_scheduler = DBconnectSMB.getResultSet(query_scheduler);
        System.out.println("result scheduler:");
        System.out.println(result_scheduler.toString());
        String query_goip = "SELECT * FROM goip.goip limit 10";
        ArrayList<HashMap> result_goip = DBconnectSMB.getResultSet(query_goip);
        System.out.println("result goip:");
        System.out.println(result_goip.toString());
    }

    private static void getPrefixForSim() throws IOException {
        //String strUrl = new StringBuilder().append("localhost/smb_scheduler/api.php?username=root&password=7pSeq6&get=bind&sim=").append(sim).append("daf Rahunku 10 hrn qdqw").toString();
        String strError = "ERROR";
        String strUrl1 = "(4603204 +380) <br>WAIT SEND 102383 (4603204 +380 +380666126402)<br><font color='#FF0000'>ERROR 39214892 102383 errorstatus:50";
        String strUrl2 = "(4603204 +380 +380666126402 SEND)</font><br>All sendings done! Failure:1<br><br><a href=sendinfo.php?id=89900 target=main><font size=2'>Click me to check details.</font></a></body>";
        String strUrl3 = "(3362010 +380) <br>WAIT SEND 102838 (3362010 +380 +380666126402)<br><font color='#00FF00'>102838  +380666126402 (3362010 +380) ok</font><br>All sendings done! Failure:0<br><br><a href=sendinfo.php?id=90236 target=main><font size=2'>Click me to check details.</font></a></body>";
        Pattern pattern_prefix = Pattern.compile("[0-9]{7}");
        final Pattern PATTERN_ERROR = Pattern.compile("ERROR");
        final Pattern PATTERN_ERRORSTATUS = Pattern.compile("errorstatus\\:[0-9]+");
        final Pattern PATTERN_FAILURE_0 = Pattern.compile("Failure\\:0");
        final Pattern PATTERN_FAILURE_1 = Pattern.compile("Failure\\:[1-9]");
        Pattern PATTERN_OPERATOR_KS = Pattern.compile("([Uu][Aa][- ]?)?(KYIVSTAR)|(Kyivstar)|(kyivstar)|(KS)|(ks)|(KYEVSTAR)|(Kyevstar)|(kyevstar)");
        Pattern PATTERN_OPERATOR_MTS = Pattern.compile("([Mm][Tt][Ss][_ ]?[Uu][Kk][Rr])|(MTS)|(Mts)|(mts)|(VODAFONE)|(Vodafone)|(vodafone)|(UMC)|(umc)");
        Pattern PATTERN_OPERATOR_LIFE = Pattern.compile("(life\\:\\))|(LIFE)|(Life)|(life)|(LIFE\\:\\))|(Life\\:\\))|(LIFECELL)|(Lifecell)|(lifecell)|(LIFECELL:\\))|(Lifecell\\:\\))|(lifecell\\:\\))");
        Pattern pFirst = Pattern.compile("([Bb]alan[sc]e?|[Ss]chete?|[Rr]ahunku|[Rr]ahunok) ?-?[0-9]+[.,]?[0-9]* ?");
        Pattern pSecond = Pattern.compile("-?[0-9]+[.,]?[0-9]*");
        //Matcher matcherPrefix = pattern_prefix.matcher(strUrl);
        //Matcher matcherFirst = pFirst.matcher(strUrl2);
        //Matcher matcherKS = PATTERN_OPERATOR_KS.matcher(strUrl1);
        //Matcher matcherMTS = PATTERN_OPERATOR_MTS.matcher(strUrl1);
        Matcher matcherError_url1 = PATTERN_ERROR.matcher(strUrl1);
        Matcher matcherErrorStatus_url1 = PATTERN_ERRORSTATUS.matcher(strUrl1);
        Matcher matcherError_url3 = PATTERN_ERROR.matcher(strUrl3);
        Matcher matcherErrorStatus_url3 = PATTERN_ERRORSTATUS.matcher(strUrl3);
        Matcher matcherFailure0_url3 = PATTERN_FAILURE_0.matcher(strUrl3);
        Matcher matcherFailure0_url2 = PATTERN_FAILURE_0.matcher(strUrl2);
        Matcher matcherFailure1_url3 = PATTERN_FAILURE_1.matcher(strUrl3);
        Matcher matcherFailure1_url2 = PATTERN_FAILURE_1.matcher(strUrl2);
        System.out.println("strUrl1: " + strUrl1);
        System.out.println("strUrl2: " + strUrl2);
        System.out.println("strUrl3: " + strUrl3);
        System.out.println("matcherFailure0_url3.matches():" + matcherFailure0_url3.matches());
        System.out.println("matcherFailure0_url3.find(): " + matcherFailure0_url3.find());
        System.out.println("matcherFailure0_url3.group():" + matcherFailure0_url3.group());
        System.out.println("matcherFailure0_url3.find(): " + matcherFailure0_url3.find());
        //System.out.println("matcherFailure0_url3.group():" + matcherFailure0_url3.group());
        System.out.println("matcherFailure0_url3.matches():" + matcherFailure0_url3.matches());
        System.out.println("matcherFailure0_url3.find(): " + matcherFailure0_url3.reset().find());
        System.out.println("matcherFailure0_url3.group():" + matcherFailure0_url3.group());
        System.out.println("matcherFailure0_url3.matches():" + matcherFailure0_url3.matches());
        System.out.println("matcherFailure0_url3.toMatchResult():"+matcherFailure0_url3.toMatchResult());
        matcherFailure0_url3.reset();
        System.out.println("matcherFailure0_url3.find(): " + matcherFailure0_url3.find());
        System.out.println("matcherFailure0_url3.toMatchResult()" + matcherFailure0_url3.toMatchResult());
        System.out.println("/////////////////////////////////////////////////");
        System.out.println("matcherFailure0_url2.matches()" + matcherFailure0_url2.matches());
        System.out.println("matcherFailure0_url2.find(): " + matcherFailure0_url2.find());
        //System.out.println("matcherFailure0_url2.group()" + matcherFailure0_url2.group());
        System.out.println("matcherFailure0_url2.find(): " + matcherFailure0_url2.find());
        //System.out.println("matcherFailure0_url2.group()" + matcherFailure0_url2.group());
        System.out.println("matcherFailure0_url2.matches()" + matcherFailure0_url2.matches());
        System.out.println("matcherFailure0_url2.find(): " + matcherFailure0_url2.reset().find());
        //System.out.println("matcherFailure0_url2.group()" + matcherFailure0_url2.group());
        System.out.println("matcherFailure0_url2.matches()" + matcherFailure0_url2.matches());
        System.out.println("matcherFailure0_url2.toMatchResult()"+matcherFailure0_url2.toMatchResult());
        matcherFailure0_url2.reset();
        System.out.println("matcherFailure0_url2.toMatchResult()" + matcherFailure0_url2.toMatchResult());
        System.out.println("/////////////////////////////////////////////////");
        System.out.println("matcherFailure1_url3.matches()" + matcherFailure1_url3.matches());
        System.out.println("matcherFailure1_url3.find(): " + matcherFailure1_url3.find());
        //System.out.println("matcherFailure1_url3.group()" + matcherFailure1_url3.group());
        System.out.println("matcherFailure1_url3.find(): " + matcherFailure1_url3.find());
        //System.out.println("matcherFailure1_url3.group()" + matcherFailure1_url3.group());
        System.out.println("matcherFailure1_url3.matches()" + matcherFailure1_url3.matches());
        System.out.println("matcherFailure1_url3.find(): " + matcherFailure1_url3.reset().find());
        //System.out.println("matcherFailure1_url3.group()" + matcherFailure1_url3.group());
        System.out.println("matcherFailure1_url3.matches()"+matcherFailure1_url3.matches());
        System.out.println("matcherFailure1_url3.toMatchResult()" + matcherFailure1_url3.toMatchResult());
        matcherFailure1_url3.reset();
        System.out.println("matcherFailure1_url3.toMatchResult()" + matcherFailure1_url3.toMatchResult());
        System.out.println("/////////////////////////////////////////////////");
        System.out.println("matcherFailure1_url2.matches()" + matcherFailure1_url2.matches());
        System.out.println("matcherFailure1_url2.find(): " + matcherFailure1_url2.find());
        System.out.println("matcherFailure1_url2.group()" + matcherFailure1_url2.group());
        System.out.println("matcherFailure1_url2.find(): " + matcherFailure1_url2.find());
        //System.out.println("matcherFailure1_url2.group()" + matcherFailure1_url2.group());
        System.out.println("matcherFailure1_url2.matches()" + matcherFailure1_url2.matches());
        System.out.println("matcherFailure1_url2.find(): " + matcherFailure1_url2.reset().find());
        System.out.println("matcherFailure1_url2.group()" + matcherFailure1_url2.group());
        System.out.println("matcherFailure1_url2.matches()"+matcherFailure1_url2.matches());
        System.out.println("matcherFailure1_url2.toMatchResult()"+matcherFailure1_url2.toMatchResult());
        matcherFailure1_url2.reset();
        System.out.println("matcherFailure1_url2.find(): " + matcherFailure1_url2.find());
        System.out.println("matcherFailure1_url2.toMatchResult()" + matcherFailure1_url2.toMatchResult());
//        System.out.println("matcherError_url3.group()"+matcherError_url3.group());
//        System.out.println(".find(): "+.find());
//
//        matcherPrefix.find();
//        System.out.println(matcherPrefix.group());
//        matcherPrefix.reset();
//        Pattern pattern = PATTERN_OPERATOR_LIFE;
//        Matcher matcher = matcherLIFE;
        if (matcherError_url1.reset().find()){
            System.out.println(LogsT.printDate() + "matcherError_url1.matches() is "+matcherError_url1.matches());
            //System.out.println(LogsT.printDate() + matcherError_url1.toString());
            matcherError_url1.reset().find();
            System.out.println(LogsT.printDate() + matcherError_url1.group());
            String strFirst = matcherError_url1.group();
            System.out.println("strFirst: "+strFirst);
            //
            System.out.println(LogsT.printDate() + "matcherErrorStatus_url1.matches() is " + matcherErrorStatus_url1.matches());
            //System.out.println(LogsT.printDate() + matcherErrorStatus_url1.toString());
            System.out.println("matcherErrorStatus_url1.reset().find(): " + matcherErrorStatus_url1.reset().find());
            System.out.println(LogsT.printDate() + matcherErrorStatus_url1.group());
            String strFirst1 = matcherErrorStatus_url1.group();
            System.out.println("strFirst: "+strFirst1);
//            Matcher matcherSecond = pattern.matcher(strFirst);
//            System.out.println(LogsT.printDate() + matcherSecond.toString());
//            if (matcherSecond.find()) {
//                System.out.println(LogsT.printDate() + "matcher.group(): " + matcherSecond.group());
//            }
//            System.out.println(LogsT.printDate() + matcher.toMatchResult());
            //return 1;
        }
        //return 0;
    }

}
