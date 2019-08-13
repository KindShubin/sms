package tests;

import sms.Sms;

import java.sql.SQLException;

public class testSms {
    //DBconnectSelect dBconnectSelect;

    public static void main(String args[]) throws SQLException {
        long id = 160509000005L;
        Sms sms = new Sms(id);
        sms.printSms();
        sms.setProvider_id(5);
        sms.setAvailability("N");
        sms.setDescription("111description111");
        sms.setPart(5);
        sms.setPrioritet(11);
        sms.setStatus("111Status111");
        sms.setTime_counter(5);
        //Timestamp t = new Timestamp(2020, 12, 12, 12, 12, 12, 000);
        //sms.setTime_send(t);
        sms.setTotal(5);
        sms.setUniqid(111);
        sms.setUserfield("11u1serfield111");
        sms.setTimeout_set(222);
        sms.printSms();
        sms.updateSmsToDB();
        sms.printSms();
//        int i = 10;
//        System.out.printf("string s is %s, int i is %d%n", s, i);
//        String query_sms = "SELECT * FROM smssystem.smslogs where id = 160412000004";
//        DBconnectSelect dBconnectSelectSms = null;
//        try {
//            dBconnectSelectSms = new DBconnectSelect(query_sms);
//        } catch (SQLException e) {
//            e.printStackTrace();
//            System.out.println("trouble in dBconnectSelectSms = new DBconnectSelect(query_sms)");
//        } finally {
//            System.out.println("dBconnectSelect for client is created");
//        }
//        Sms sms = new Sms(dBconnectSelectSms.getRs());
//        sms.printSms();

    }

}
