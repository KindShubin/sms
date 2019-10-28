package Provider;

import DB.DBconnectVPS;
import DB.GetVal;
import LogsParts.LogsT;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.AbstractList;
import java.util.HashMap;

public class Provider {

    //private ResultSet rs;
    private long id;
    private String name;
    private String ip;
    private String sip;
    private String currency;
    private String mts;
    private String ks;
    private String life;
    private String other;
    private int mts_cost;
    private int ks_cost;
    private int life_cost;
    private int other_cost;

//    public Provider(ResultSet rs){
//        //this.rs = rs;
//        getInfo(rs);
//    }

    public Provider(int id) throws SQLException {
        this.id=id;
        String query_provider = new StringBuilder().append("SELECT * FROM smssystem.providers as sc where sc.id=").append(this.id).toString();
        //getInfo(new DBconnectSelect(query_provider).makeRS());
        AbstractList<HashMap> result = DBconnectVPS.getResultSet(query_provider);
        getInfo(result.get(0));
    }

    public long getId() { return id; }

    public String getName() {
        return name;
    }

    public String getIp() {
        return ip;
    }

    public String getSip() {
        return sip;
    }

    public String getCurrency() {
        return currency;
    }

    public String getMts() {
        return mts;
    }

    public String getKs() {
        return ks;
    }

    public String getLife() {
        return life;
    }

    public String getOther() {
        return other;
    }

    public int getMts_cost() {
        return mts_cost;
    }

    public int getKs_cost() {
        return ks_cost;
    }

    public int getLife_cost() {
        return life_cost;
    }

    public int getOther_cost() {
        return other_cost;
    }

    private void getInfo(ResultSet rs){
        try {
            while (rs.next()) {
                id = rs.getLong("id");
                name = rs.getString("name");
                ip = rs.getString("ip");
                sip = rs.getString("sip");
                currency = rs.getString("currency");
                mts = rs.getString("mts");
                ks = rs.getString("ks");
                life = rs.getString("life");
                other = rs.getString("other");
                mts_cost = rs.getInt("mts_cost");
                ks_cost = rs.getInt("ks_cost");
                life_cost = rs.getInt("life_cost");
                other_cost = rs.getInt("other_cost");
//                String date = String.valueOf(rs.getDate(10));
//                //int count = rs.getInt(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    private void getInfo(HashMap hm){
        try {
            id = GetVal.getLong(hm, "id");
            name = GetVal.getStr(hm, "name");
            ip = GetVal.getStr(hm, "ip");
            sip = GetVal.getStr(hm, "sip");
            currency = GetVal.getStr(hm, "currency");
            mts = GetVal.getStr(hm, "mts");
            ks = GetVal.getStr(hm, "ks");
            life = GetVal.getStr(hm, "life");
            other = GetVal.getStr(hm, "other");
            mts_cost = GetVal.getInt(hm, "mts_cost");
            ks_cost = GetVal.getInt(hm, "ks_cost");
            life_cost = GetVal.getInt(hm, "life_cost");
            other_cost = GetVal.getInt(hm, "other_cost");
//                String date = String.valueOf(rs.getDate(10));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void printProvider(){
        System.out.printf(LogsT.printDate() + "id: %d,%n name: %s,%n ip: %s,%n sip: %s,%n currency: %s,%n mts: %s,%n ks: %s,%n life: %s,%n other: %s,%n mts_cost: %d,%n ks_cost: %d,%n life_cost: %d,%n other_cost: %d%n",
                id, name, ip, sip, currency, mts, ks, life, other, mts_cost, ks_cost, life_cost, other_cost);
    }


}
