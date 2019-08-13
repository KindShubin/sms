package Run;

import BeforeSend.Bonding;
import DB.DBconnectNEW;
import LogsParts.LogsId;
import LogsParts.LogsT;

import java.sql.SQLException;

import static java.lang.Thread.sleep;

public class ClassRunAsyncBonding implements Runnable{

    Bonding objBonding = new Bonding();
    long uniqid;

    public ClassRunAsyncBonding(long uniqid){
        this.uniqid=uniqid;
    }

    @Override
    public void run() {
        try {
            Thread ct = Thread.currentThread();
            //setStatusBonding();
            //ct.setPriority(5);
            //System.out.println(LogsT.printDate() + "run Thread: "+ct.getName());
            //System.out.println(LogsT.printDate() + "statusSending(" + this.id + ")");
            // убрал 27,08,2016 из-за массового перевода смс в статус sending и неотрпаки в дальнейшем смс
            //this.objSendSms.statusSending(this.id);
            System.out.println(LogsT.printDate()  + LogsId.id(this.uniqid) +"Start bonding. Thread: " + ct.getName() + ". uniqid: " + this.uniqid);
            boolean resultInsert=this.objBonding.bonding(this.uniqid);
            if (resultInsert){
                System.out.println(LogsT.printDate() + LogsId.id(this.uniqid) +"resultInsert:"+resultInsert+" -- Bonding is done. exit and close thread");
            }
            else {
                System.out.println(LogsT.printDate() + LogsId.id(this.uniqid) +"resultInsert:"+resultInsert+" -- sleep 5 sec and setStatusEnroute()");
                sleep(5000);
                setStatusEnroute();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setStatusBonding(){
        System.out.println(LogsT.printDate() + "|Run.ClassRunAsyncBonding.setStatusBonding()| begin update sms status to bonding for uniqid="+uniqid);
        //String update = new StringBuilder().append("update smssystem.smslogs Set availability='N', status = 'bonding' WHERE uniqid=").append(uniqid).toString();
        String update = new StringBuilder().append("update smssystem.smslogs Set availability='N', status = 'bonding' WHERE total>1 and datediff(now(),time_entry)<2 and uniqid=").append(uniqid).toString();
        try {
            DBconnectNEW.executeQuery(update);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        System.out.println(LogsT.printDate() + "|Run.ClassRunAsyncBonding.setStatusBonding()| set status bonding for uniqid="+uniqid);
    }

    private void setStatusEnroute(){
        String update = new StringBuilder().append("update smssystem.smslogs Set availability='N', status = 'ENROUTE' WHERE total>1 and uniqid=").append(uniqid).toString();
        try {
            DBconnectNEW.executeQuery(update);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        System.out.println(LogsT.printDate() + LogsId.id(this.uniqid) +"|Run.ClassRunAsyncBonding.setStatusEnroute()| set status ENROUTE for uniqid="+uniqid);
    }
}
