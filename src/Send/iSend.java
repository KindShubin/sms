package Send;

import sms.Sms;

import java.sql.SQLException;

public interface iSend {

    void sendSms(Sms sms, int id_provider) throws SQLException;

    String giveStatus(Sms sms);

    void updateStatus(Sms sms) throws SQLException;

}
