package Rotation;

import DB.DBconnectVPS;
import DB.GetVal;
import LogsParts.LogsT;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;

public class RotationClass {

    public static void main(String[] args) throws SQLException, InterruptedException {

        rotation();
    }


    static public void rotation() {
        ArrayList<HashMap> dataSmssystemLines = dataSmssystemLines();
        ArrayList<HashMap> dataSmssystemSim = dataSmssystemSim();
        ArrayList<HashMap> dataSmssystemPools = dataSmssystemPools();
        ArrayList<HashMap> dataSmssystemRotation = dataSmssystemRotation();

        // test
        System.out.printf("dataSmssystemLines:\n"+dataSmssystemLines);
        System.out.printf("dataSmssystemSim:\n"+dataSmssystemSim);
        System.out.printf("dataSmssystemPools:\n"+dataSmssystemPools);
        System.out.printf("dataSmssystemRotation:\n"+dataSmssystemRotation);
        //System.out.printf(":\n"+);

    }

    static private ArrayList<HashMap> dataSmssystemLines(){
        ArrayList<HashMap> data = new ArrayList<>();
        String select = new StringBuilder("SELECT id, pool_lines, zone, mode, description FROM smssystem.lines;").toString();
        try {
            data = DBconnectVPS.getResultSet(select);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return data;
    }

    static private ArrayList<HashMap> dataSmssystemSim(){
        ArrayList<HashMap> data = new ArrayList<>();
        String select = "SELECT id, status, goip, corp, prefix, duration_pause, next_status, counts_temporary, level, lock_work, lock_work_description, count_min, count_hour, count_day, count_month, count_min_ks, count_min_mts, count_min_life, count_min_other, count_hour_ks, count_hour_mts, count_hour_life, count_hour_other, count_day_ks, count_day_mts, count_day_life, count_day_other, count_month_ks, count_month_mts, count_month_life, count_month_other, balance_ussd, balance_ussd_datecheck, balance, ostatok_sms_ussd, ostatok_sms_datecheck, ostatok_sms, monthly_fee_ussd, monthly_fee_datecheck, monthly_fee FROM smssystem.sim;";
        try {
            data = DBconnectVPS.getResultSet(select);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return data;
    }

    static private ArrayList<HashMap> dataSmssystemPools(){
        ArrayList<HashMap> data = new ArrayList<>();
        String select = new StringBuilder("SELECT corp, description, tarif, timeout, package_sms, allowsms_ks, allowsms_mts, allowsms_life, allowsms_other, package_sms_type, limit_month, limit_day, limit_hour, limit_minut, limit_month_ks, limit_month_mts, limit_month_life, limit_month_other, limit_day_ks, limit_day_mts, limit_day_life, limit_day_other, limit_hour_ks, limit_hour_mts, limit_hour_life, limit_hour_other, limit_minut_ks, limit_minut_mts, limit_minut_life, limit_minut_other FROM smssystem.pools;").toString();
        try {
            data = DBconnectVPS.getResultSet(select);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return data;
    }

    static private ArrayList<HashMap> dataSmssystemRotation(){
        ArrayList<HashMap> data = new ArrayList<>();
        String select = new StringBuilder("SELECT id, poolSim, quantity_sim, quantity_ports, prioritet, perc_of_all_ports, include_zone, exclude_zone, include_gates, exclude_gates, description, poolPorts FROM smssystem.rotation;").toString();
        try {
            data = DBconnectVPS.getResultSet(select);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return data;
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
            System.out.println(LogsT.printDate() + "|RotationClass.getAllFreePorts| result 0");
        } else System.out.println(LogsT.printDate() + "|RotationClass.getAllFreePorts| qnt ports: "+ result.size());
        for(HashMap rs : result) {
            int line = GetVal.getInt(rs, "name");
            freePorts.add(line);
        }
        return freePorts;
    }

    static private void getAllFreePortsZone(int zone){
        System.out.println(LogsT.printDate() + "|RotationClass.getAllFreePorts zone");
    }

    static private void getAllFreePortsGate(int gate){
        System.out.println(LogsT.printDate() + "|RotationClass.getAllFreePorts gate");
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
