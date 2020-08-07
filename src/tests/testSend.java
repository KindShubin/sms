package tests;

import Exceptions.MyErrorEx;
import Send.Send;
import sms.Sms;

public class testSend {

    public static void main(String[] args) throws Exception {
/*
        long id1 = 160415000007L;
        Sms sms1 = new Sms(id1);
//        Sms sms1 = null;
//        try {
//            sms1 = new Sms(id1);
//        } catch (MyErrorEx e){
//            System.err.println(e.toString());
//            System.out.println("Error before creating object sms");
//        }
        if(sms1.getOperator() != null){
            Send send1 = new Send(sms1);
        }
        else{
            System.out.println("Error! Send failed. sms.getOperator() is null. Maybe dst_num is incorrect. dst_num:"+sms1.getDst_num());
            System.err.println("Error! Send failed. sms.getOperator() is null. Maybe dst_num is incorrect. dst_num:"+sms1.getDst_num());
        }
        //sms1.defineOperator();

        System.out.println("--------------");

        long id3 = 160416000000L;
        Sms sms3 = new Sms(id3);
        if(sms3.getOperator() != null) {
            // throw new MyNullPointerEx("oper is null");
            Send send3 = new Send(sms3);
        }
        else{
            System.out.println("Error! Send failed. sms.getOperator() is null. Maybe dst_num is incorrect. dst_num:"+sms3.getDst_num());
            System.err.println("Error! Send failed. sms.getOperator() is null. Maybe dst_num is incorrect. dst_num:"+sms3.getDst_num());
        }
*/

        System.out.println("--------------");

        long id1 = 160513000002L;
        Sms sms1 = null;
        try{
            sms1 = new Sms(id1);
        } catch (MyErrorEx e){
            System.err.println(e.toString());
            System.out.println("Error before creating object sms");
        }
        if(sms1.getOperator() != null){
            Send send1 = new Send(sms1);
        }
        else{
            System.out.println("Error! Send failed. sms.getOperator() is null. Maybe dst_num is incorrect. dst_num:"+sms1.getDst_num());
            System.err.println("Error! Send failed. sms.getOperator() is null. Maybe dst_num is incorrect. dst_num:"+sms1.getDst_num());
        }

        System.out.println("--------------");


//        for(int i = 1; i<14; i++){
//            long id = 160504000000L+i;
//            System.out.println("id: " + id);
//            Sms sms = new Sms(id);
//            new ChoseProviders(sms);
//            System.out.println("--------------");
//        }

    }


}
