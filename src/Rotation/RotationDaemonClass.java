package Rotation;

import DB.DBconnectVPS;
import DB.GetVal;
import LogsParts.LogsT;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;

public class RotationDaemonClass {

    public static void main(String[] args) throws SQLException, InterruptedException {

        try { daemonize(); }
        catch (Throwable e){
            System.err.println(LogsT.printDate() + "Startup failed. " + e.getMessage());
        }
        finally { System.out.println(LogsT.printDate() + "demonize started!"); }

        ArrayList<Integer> allFreePorts = getAllFreePorts();
        System.out.println(LogsT.printDate() +"allFreePorts:");
        System.out.println(allFreePorts);

        //rotation();
    }

    static private void daemonize() throws Exception{
        try { System.in.close(); }
        catch (Throwable e){
            System.err.println(LogsT.printDate() + "Fail. System in not close" + e.getMessage());
        }
        finally { System.out.println(LogsT.printDate() + "OK. System in closed"); }
        //System.out.close();
    }

    static private void rotation(){

    }

    static ArrayList getAllFreePorts() throws SQLException {
        ArrayList<Integer> freePorts = new ArrayList<Integer>();
        String strRegexpGoips=getRegexpStringWithGoipsForAnySelect(getGoipsForRotation());// определяю регулярку для запроса (33318|33416....)
        String select = new StringBuilder("SELECT gg.name FROM goip.goip as gg ")
                .append("left join scheduler.sim as ss on gg.name=ss.line_name ")
                .append("left join scheduler.sim as ss1 on gg.name=ss1.plan_line_name ")
                .append("where gg.name regexp '").append(strRegexpGoips).append("' and gg.gsm_status='LOGOUT' and (ss.line_name is NULL or ss1.plan_line_name is NULL);")
                .toString();
        //System.out.println(LogsT.printDate() + );
        ArrayList<HashMap> result = DBconnectVPS.getResultSet(select);
        if (result.size()==0){
            System.out.println(LogsT.printDate() + "|RotationDaemonClass.getAllFreePorts| result 0");
        } else System.out.println(LogsT.printDate() + "|RotationDaemonClass.getAllFreePorts| qnt ports: "+ result.size());
        for(HashMap rs : result) {
            int line = GetVal.getInt(rs, "name");
            freePorts.add(line);
        }
        return freePorts;
    }

    static private void getAllFreePortsZone(int zone){
        System.out.println(LogsT.printDate() + "|RotationDaemonClass.getAllFreePorts zone");
    }

    static private void getAllFreePortsGate(int gate){
        System.out.println(LogsT.printDate() + "|RotationDaemonClass.getAllFreePorts gate");
    }

    static private ArrayList<Integer> getGoipsForRotation() throws SQLException {
        String select="SELECT goip FROM rotation.goips";
        ArrayList<HashMap> result = DBconnectVPS.getResultSet(select);
        ArrayList<Integer> goips = new ArrayList<Integer>();
        for (HashMap rs: result) {
            goips.add(GetVal.getInt(rs,"goip"));
        }
        System.out.println(LogsT.printDate() + "|getGoipsForRotation()| List goips:"+goips);
        return goips;
    }

    static private String getRegexpStringWithGoipsForAnySelect(ArrayList<Integer> listGoips){
        int lengthArray=listGoips.size();
        StringBuilder goipSB = new StringBuilder().append("(^");
        for(int i = 1; i<lengthArray; i++){
            int goip=listGoips.get(i);
            goipSB.append(goip).append("|^");
        }
        goipSB.delete(goipSB.toString().length()-2,goipSB.toString().length()).append(")");
        String strRegexpGoips=goipSB.toString();
        System.out.println(LogsT.printDate() + "|getRegexpStringWithGoipsForAnySelect()| string:"+strRegexpGoips);
        return strRegexpGoips;
    }

}
