package GoipProvider;

import DB.DBconnectVPS;
import DB.GetVal;
import LogsParts.LogsId;
import LogsParts.LogsT;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class OurProvider {

    //id_providers, corp1, corp1_percents, corp2, corp2_percents, corp3, corp3_percents, corp4, corp4_percents, corp5, corp5_percents, corp6, corp6_percents, corp7, corp7_percents, corp8, corp8_percents, corp9, corp9_percents, corp10, corp10_percents, corp11, corp11_percents, description, id_providers, id
    private int id_providers;
    private String description;
    private List<Integer> listPercents = new ArrayList<>();
    private List<String> listCorp = new ArrayList<>();
    private int generalSumPercents;
    //private DBconnectSelect db;
    //private ResultSet rs;
    private Number logId;

    public int getId_providers() { return id_providers; }

    public int getQntRowsInSelect() { return qntRowsInSelect; }

    private int qntRowsInSelect;

    //public DBconnectSelect getDb() { return db; }

    //public ResultSet getRs() { return rs; }

    public String getCorp(int numCorp){ return listCorp.get(numCorp-1); }

    public int getPercent(int numCorp){ return listPercents.get(numCorp-1); }

    public List<Integer> getListPercents() {
        return listPercents;
    }

    public List<String> getListCorp() {
        return listCorp;
    }

    public int getGeneralSumPercents(){ return this.generalSumPercents; }


    public OurProvider(int id_providers, Number logId) throws SQLException {
        this.logId=logId;
        this.id_providers=id_providers;
        String query = new StringBuilder(200).append("select * from smssystem.ourProviders where id_providers=").append(this.id_providers).toString();
        System.out.println(LogsT.printDate() + LogsId.id(logId) + "query: "+query);
        //this.db = new DBconnectSelect(query);
        //this.rs = db.getRs();
        int qntRows = DBconnectVPS.qntRowsInSelect(query);
        ArrayList<HashMap> result = DBconnectVPS.getResultSet(query);
        writeData(result);
        //this.rs.beforeFirst();
        if(qntRows<1){
            //SendProvider sendProvider = new SendProvider(sms, provId);
            this.qntRowsInSelect=0;
        }
        else {
            //SendGoip sendGoip = new SendGoip(sms, ourProvider);
            this.qntRowsInSelect=1;
        }
        //this.db.closeConnectionWithRs();
    }

    public void printOurProvider(){
        System.out.println(LogsT.printDate() + LogsId.id(logId) + "id OurProvider: "+getId_providers());
        System.out.println(LogsT.printDate() + LogsId.id(logId) + "qntCorpsInProvider: "+qntCorpsInProvider());
        for(int i=0; i< qntCorpsInProvider(); i++){
            System.out.print(LogsT.printDate() + LogsId.id(logId)+"corp" + i + ": " + this.listCorp.get(i) + " --> " + this.listPercents.get(i) + "%;\n");
        }
        //System.out.println();
    }

    // записываю названия корпораций и проценты из запроса в listarray для назыаний listCorp и для процентов listPercents
    private void writeData(ResultSet rs) throws SQLException {
        while (rs.next()){
            this.id_providers=rs.getInt("id_providers");
            this.description=rs.getString("description");
//            System.out.println("(rs.getMetaData().getColumnCount()-2)/2 "+((rs.getMetaData().getColumnCount()-2)/2));
            for(int i=0; i<((rs.getMetaData().getColumnCount()-2)/2); i++){
                String columnNameCorp = new StringBuilder().append("corp").append(i + 1).toString();
                String columnPercentCorp = new StringBuilder().append("corp").append(i + 1).append("_percents").toString();
                String nameCorp = rs.getString(columnNameCorp);
                int percentCorp = rs.getInt(columnPercentCorp);
                //if (nameCorp != null && nameCorp!="null" && nameCorp != "N/A" && percentCorp>0) {
                if (nameCorp != null && nameCorp!="null" && nameCorp != "N/A") {
                    this.listCorp.add(i, nameCorp);
                    this.listPercents.add(i, percentCorp);
                    System.out.println(LogsT.printDate() + LogsId.id(logId) + "corp: " + this.listCorp.get(i) + " percent: " + listPercents.get(i));
                }
            }
        }
        GeneralSumPercents();
    }
    private void writeData(ArrayList<HashMap> result) throws SQLException {
        for (HashMap rs : result){
            this.id_providers= GetVal.getInt(rs,"id_providers");
            this.description=GetVal.getStr(rs,"description");
//            System.out.println("(rs.getMetaData().getColumnCount()-2)/2 "+((rs.getMetaData().getColumnCount()-2)/2));
            for(int i=0; i<((rs.size()-2)/2); i++){
                String columnNameCorp = new StringBuilder().append("corp").append(i + 1).toString();
                String columnPercentCorp = new StringBuilder().append("corp").append(i + 1).append("_percents").toString();
                String nameCorp = GetVal.getStr(rs, columnNameCorp);
                int percentCorp = GetVal.getInt(rs, columnPercentCorp);
                //if (nameCorp != null && nameCorp!="null" && nameCorp != "N/A" && percentCorp>0) {
                if (nameCorp != null && nameCorp!="null" && nameCorp != "N/A") {
                    this.listCorp.add(i, nameCorp);
                    this.listPercents.add(i, percentCorp);
                    System.out.println(LogsT.printDate() + LogsId.id(logId) + "corp: " + this.listCorp.get(i) + " percent: " + listPercents.get(i));
                }
            }
        }
        GeneralSumPercents();
    }


    public void GeneralSumPercents(){
        int sum = 0;
        for(int i:listPercents){
            //String name = new StringBuilder(50).append("corp").append(i).append("_percents").toString();
            sum += i;
        }
        this.generalSumPercents = sum;
        System.out.println(LogsT.printDate() + LogsId.id(logId) + "GeneralSumPercents: "+this.generalSumPercents);
    }

    public int qntCorpsInProvider(){
        int count = 0;
        for (int i=0; i<listCorp.size(); i++){
            if (listCorp.get(i) != "null" && listCorp.get(i) != null && listCorp.get(i) != "N/A" && listPercents.get(i)>0) count++;
        }
        return count;
    }
}
