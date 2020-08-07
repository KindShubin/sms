package tests;

import LogsParts.LogsId;
import LogsParts.LogsT;
import Run.ClassRunAsyncSend;

import java.sql.ResultSet;
import java.sql.SQLException;

public class ClassRunCicleRs implements Runnable{

    ResultSet rs;

    public ClassRunCicleRs(ResultSet rs){
        this.rs=rs;
    }

    @Override
    public void run(){
        try {
            while (this.rs.next()) {
                long id = rs.getLong("id");
                System.out.println(LogsT.printDate() + LogsId.id(id) + "id: " + id);
                Runnable r = new ClassRunAsyncSend(id);
                //Thread t = new Thread(r, "daemon");
                Thread t = new Thread(r);
                t.setDaemon(true);
                //t.setPriority(1);
                System.out.println(LogsT.printDate() + "start t.start() with id:" + id);
                t.start();
                //new Thread(new ClassRunAsyncSend(id)).start();
                System.out.println(LogsT.printDate() + "end t.start() with id:"+id);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
