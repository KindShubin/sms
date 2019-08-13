package Run;

import DB.DBconnect;
import DB.DBconnectNEW;
import LogsParts.LogsId;
import LogsParts.LogsT;
import Send.Send;
import sms.Sms;

import java.sql.SQLException;

public class RunSend {

    public void send(long id) throws SQLException {
        System.out.println(LogsT.printDate() + LogsId.id(id) + "!!! RunSend.send(id) current thread is "+Thread.currentThread().getName());
        statusSending(id);
        Sms sms = null;
        try{
            System.out.println(LogsT.printDate() + LogsId.id(id) + "RunSend.send(id) !!! current thread is "+Thread.currentThread().getName());
            sms = new Sms(id);
        }
        catch(SQLException e){
            System.err.println(LogsT.printDate() + LogsId.id(id) + e.toString());
            System.out.println(LogsT.printDate() + LogsId.id(id) + "RunSend.send(id) Error before creating object sms");
        }
        System.out.println(LogsT.printDate() + LogsId.id(id) + "!!! RunSend.send(id) current thread is "+Thread.currentThread().getName());
        if(sms.getOperator()!=null){
            try {
                System.out.println(LogsT.printDate() + LogsId.id(id) + "!!! RunSend.send(id) current thread before Send(sms)is "+Thread.currentThread().getName());
                new Send(sms);
                System.out.println(LogsT.printDate() + LogsId.id(id) + "!!! RunSend.send(id) current thread after Send(sms)is "+Thread.currentThread().getName());
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else{
            System.out.println(LogsT.printDate() + LogsId.id(id) + "RunSend.send(id) Error! Send failed. sms.getOperator() is null. Maybe dst_num is incorrect. dst_num:" + sms.getDst_num());
            //setUnknownStatusSms(id);
            sms.statusUnknown();
            //System.err.println("Error! Send failed. sms.getOperator() is null. Maybe dst_num is incorrect. dst_num:" + sms.getDst_num());
        }
        System.out.println(LogsT.printDate() + LogsId.id(id) + "!!! RunSend.send(id) current thread in RunSend.send end is "+Thread.currentThread().getName());
        Thread.currentThread().interrupt();
    }

    private void setUnknownStatusSms(long id) throws SQLException {
        System.out.println(LogsT.printDate() + LogsId.id(id) + "!!! RunSend.setUnknownStatusSms(id) curent thread is "+Thread.currentThread().getName());
        String query = new StringBuilder().append("update smssystem.smslogs Set status='UNKNOWN', description='maybe wrong dst number' where id=")
                .append(id).toString();
        DBconnectNEW.executeQuery(query);
    }

// убрал 27,08,2016 из-за массового перевода смс в статус sending и неотрпаки в дальнейшем смс
//!!!!!!!!!!!!!!!!! 2018 09 29
    public void statusSending(long id) throws SQLException {
        DBconnect db = new DBconnect();
        String query = new StringBuilder().append("update smssystem.smslogs Set status='sending' where id=").append(id).toString();
        db.getStmt().execute(query);
        db.closeConnection();
    }

}
