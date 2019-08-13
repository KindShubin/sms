package tests;

import Assignments.ChoseProviders;
import Exceptions.FooException;
import sms.Sms;

import java.sql.SQLException;

public class testAssignments {

    public static void main(String[] args) throws SQLException, FooException {
        long id = 160415000008L;
        Sms sms = new Sms(id);
        ChoseProviders choseProviders = new ChoseProviders(sms);
        choseProviders.printQuery();
        for (int i=0; i<10; i++) {
            //choseProviders.choseProvider_id();
            System.out.println("provider id: " + choseProviders.getProvider_id());
        }
    }
}
