package Run;

import LogsParts.LogsId;
import LogsParts.LogsT;
import Sims.Simcard;

import java.sql.SQLException;

public class ClassRunAsyncTimeout implements Runnable{

    Number id;
    Simcard obj;
    int time;

    public ClassRunAsyncTimeout(final Simcard obj, int time, Number id){
        this.id= id;
        this.obj=obj;
        this.time=time;
    }

    @Override
    public void run() {
        try {
            System.out.println(LogsT.printDate() + LogsId.id(id) + "pause "+time+" for simcard IMSI:"+obj.getImsi()+" Corp:" + obj.getCorp()+" Prefix: "+obj.getPrefix()+" Time:"+time);
            obj.pause(time, id);
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        System.out.println(LogsT.printDate() + LogsId.id(id) + "close runAsynchronouslyPause. IMSI simcard:"+obj.getImsi()+" Corp:"+obj.getCorp()+" Prefix: "+obj.getPrefix()+" Time:"+time);
    }

//    private void pause(Simcard simcard, int time, Number id) throws InterruptedException, SQLException {
//        System.out.println(LogsT.printDate() + LogsId.id(id) + "start method pause for imsi:"+simcard.getImsi()+" corp:"+simcard.getCorp()+" prefix:"+simcard.getPrefix());
//        sleep(time * 1000);
//        this.obj.enableSimcardAfterSend(simcard.getImsi());
//        System.out.println(LogsT.printDate() + LogsId.id(id) + "simcard imsi:"+simcard.getImsi()+" corp:+"+simcard.getCorp()+" prefix:"+simcard.getPrefix()+ " now is available");
//        System.out.println(LogsT.printDate() + LogsId.id(id) + "close method pause for imsi:"+simcard.getImsi()+" corp:"+simcard.getCorp()+" prefix:"+simcard.getPrefix());
//    }
}
