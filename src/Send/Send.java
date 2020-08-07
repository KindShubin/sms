package Send;

import Assignments.ChoseProviders;
import LogsParts.LogsId;
import LogsParts.LogsT;
import GoipProvider.OurProvider;
import sms.Sms;

public class Send {

    private static final String PART_OF_QUERY_OURPROVIDERS = "select * from smssystem.ourProviders where id_providers=";
    private int provId;

    public Send(Sms sms) throws Exception {
        long logId = sms.getId();
        System.out.println(LogsT.printDate() + LogsId.id(logId) + "provId: "+ provId);
        //provId = sms.getProvider_id();
        try{ new ChoseProviders(sms); }
        catch (Exception e){
            System.out.println(LogsT.printDate() + LogsId.id(logId) + "Send(sms) -- new ChoseProviders(sms) Error");
            e.printStackTrace();
        }
        provId = sms.getProvider_id();
        if (provId == 0){
            new ChoseProviders(sms);
        }
        provId = sms.getProvider_id();
        OurProvider ourProvider = new OurProvider(provId, logId);
        if(ourProvider.getQntRowsInSelect()<1){
            //SendProvider sendProvider = new SendProvider(sms, provId);
            new SendProvider(sms, provId);
        }
        else {
            //SendGoip sendGoip = new SendGoip(sms, ourProvider);
            new SendGoip(sms, ourProvider);
        }
    }
}
