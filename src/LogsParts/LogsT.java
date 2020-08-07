package LogsParts;

import java.text.SimpleDateFormat;
import java.util.Date;

public  class LogsT {

    //static  LogsParts d = new LogsParts();
    public static String printDate(){
        return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS ").format(new Date());
    }

}
