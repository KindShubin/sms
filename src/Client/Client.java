package Client;

import DB.DBconnectVPS;
import DB.GetVal;
import LogsParts.LogsT;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;

public class Client {

    public long getId() {
        return id;
    }

    public int getType() { return type; }

    public String getName() { return name; }

    public String getIp() { return ip; }

    public int getPort() {
        return port;
    }

    public String getSip() {
        return sip;
    }

    public String getCurrency() { return currency; }

    public int getBlock() {
        return block;
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

    public int getOther_cost() { return other_cost; }

    //private ResultSet rs;
    private long id;
    private int type;
    private String name;
    private String ip;
    private int port;
    private String sip;
    private String currency;
    private int block;
    private String mts;
    private String ks;
    private String life;
    private String other;
    private int mts_cost;
    private int ks_cost;
    private int life_cost;
    private int other_cost;

    public Client(ResultSet rs){
        //this.rs = rs;
        getInfo(rs);
    }

    public Client(int id) throws SQLException {
        this.id=id;
        String query_client = new StringBuilder().append("SELECT * FROM smssystem.clients as sc where sc.id=").append(this.id).toString();
        //getInfo(new DBconnectSelect(query_client).makeRS());
        getInfo(DBconnectVPS.getResultSet(query_client).get(0));
    }

    public Client(HashMap hm){
        getInfo(hm);
    }

    private void getInfo(HashMap rs){
        try {
            id = GetVal.getLong(rs, "id");
            type = GetVal.getInt(rs, "type");
            name = GetVal.getStr(rs, "name");
            ip = GetVal.getStr(rs,"ip");
            port = GetVal.getInt(rs, "port");
            sip = GetVal.getStr(rs, "sip");
            currency = GetVal.getStr(rs,"currency");
            block = GetVal.getInt(rs, "block");
            mts = GetVal.getStr(rs,"mts");
            ks = GetVal.getStr(rs,"ks");
            life = GetVal.getStr(rs, "life");
            other = GetVal.getStr(rs, "other");
            mts_cost = GetVal.getInt(rs,"mts_cost");
            ks_cost = GetVal.getInt(rs,"ks_cost");
            life_cost = GetVal.getInt(rs,"life_cost");
            other_cost = GetVal.getInt(rs,"other_cost");
//                String date = String.valueOf(rs.getDate(10));
//                //int count = rs.getInt(1);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    private void getInfo(ResultSet rs){
        try {
            while (rs.next()) {
                id = rs.getLong("id");
                type = rs.getInt("type");
                name = rs.getString("name");
                ip = rs.getString("ip");
                port = rs.getInt("port");
                sip = rs.getString("sip");
                currency = rs.getString("currency");
                block = rs.getInt("block");
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

        public void printClient(){
        System.out.printf(LogsT.printDate() + " id: %d,%n name: %s,%n ip: %s,%n sip: %s,%n currency: %s,%n mts: %s,%n ks: %s,%n life: %s,%n other: %s,%n mts_cost: %d,%n ks_cost: %d,%n life_cost: %d,%n other_cost: %d%n",
                id, name, ip, sip, currency, mts, ks, life, other, mts_cost, ks_cost, life_cost, other_cost);
    }


}
