package Send;

import LogsParts.LogsId;
import LogsParts.LogsT;
import sms.Sms;

public class SendProvider implements iSend{

    SendProvider(Sms sms, int id_provider){
        System.out.println(LogsT.printDate() + LogsId.id(sms.getId()) + "send with provides");
    }

    @Override
    public void sendSms(Sms sms, int id_provider) {

    }

    @Override
    public String giveStatus(Sms sms) {
        return null;
    }

    @Override
    public void updateStatus(Sms sms) {

    }
}
