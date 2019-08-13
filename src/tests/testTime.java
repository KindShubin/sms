package tests;


import LogsParts.LogsId;
import LogsParts.LogsT;
import java.sql.Timestamp;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import static java.lang.Thread.sleep;


public class testTime {


    static long l = 1451599200000L;

    public static void main(String[] args) throws InterruptedException {
        Calendar calendar = Calendar.getInstance();
        System.out.println("Calendar calendar: " + calendar);
        System.out.println("calendar.getTime()"+calendar.getTime());
        GregorianCalendar gc = (GregorianCalendar) GregorianCalendar.getInstance();
        System.out.println("GregorianCalendar gc: " + gc);
        System.out.println("gc.getTime()"+gc.getTime());
        Date now = calendar.getTime();
        Calendar c = Calendar.getInstance();
        c.setTimeInMillis(l);
        System.out.println("Calendar.getInstance().setTimeInMillis(1451599200000L): " + c.getTime());
        Date date2016 = new Date(l);
        System.out.println("Date 2016: "+date2016);
        Timestamp timest_2016 = new Timestamp(date2016.getTime());
        //Timestamp null_time = new Timestamp(Calendar.getInstance().setTimeInMillis(1451599200000L));
        //System.out.println("Timestamp null_time: "+ null_time);
        System.out.println("Timestamp timest_2016: " + timest_2016);
        calendar.set(2016, 8, 2, 0, 0, 0);
        System.out.println("calendar.getTime(): " + calendar.getTime());
        System.out.println("calendar.getTime() in ms: "+calendar.getTimeInMillis());
        Date nowdate = Calendar.getInstance().getTime();
        Timestamp timenow = new Timestamp(nowdate.getTime());
        System.out.println("timenow: "+timenow);
        long diff = gc.getTimeInMillis()-calendar.getTimeInMillis();
        int days = (int) (diff/1000/60/60/24);
        System.out.println("days: "+days);




        System.out.println(1);
        System.out.println(new Date(System.currentTimeMillis()));
        System.out.println(new Date());
        //System.out.println(new Calendar);
        System.out.println(Calendar.getInstance().getTime());
        System.out.println(2);
        System.out.println(LogsT.printDate() + LogsId.id(486874313)+"111");
        sleep(900);
        System.out.println(new SimpleDateFormat("yyyy.MM.dd_HH:mm:s:S").format(new Date()));
        sleep(900);
        System.out.println(LogsT.printDate() + LogsId.id(486874313)+"222");
        sleep(900);
        System.out.println(LogsT.printDate() + "333");
    }

}
