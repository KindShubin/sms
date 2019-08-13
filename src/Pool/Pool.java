package Pool;

import DB.DBconnectNEW;
import DB.GetVal;

import java.sql.SQLException;
import java.util.AbstractList;
import java.util.HashMap;

public class Pool {

    //corp, description, tarif, timeout, limit_month_mts, limit_month_ks, limit_month_life, limit_month_other, limit_day_mts, limit_day_ks, limit_day_life, limit_day_other, limit_hour_mts, limit_hour_ks, limit_hour_life, limit_hour_other, limit_minut_mts, limit_minut_ks, limit_minut_life, limit_minut_other, corp, id
    String corp;
    String description;
    String tarif;
    int timeout;


    int limit_month;
    int limit_day;
    int limit_hour;
    int limit_minut;
    int limit_month_mts;
    int limit_month_ks;
    int limit_month_life;
    int limit_month_other;
    int limit_day_mts;
    int limit_day_ks;
    int limit_day_life;
    int limit_day_other;
    int limit_hour_mts;
    int limit_hour_ks;
    int limit_hour_life;
    int limit_hour_other;
    int limit_minut_mts;
    int limit_minut_ks;
    int limit_minut_life;
    int limit_minut_other;

    public String getCorp() { return corp; }
    public String getDescription() { return description; }
    public String getTarif() { return tarif; }
    public int getTimeout() { return timeout; }
    public int getLimit_month() { return limit_month; }
    public int getLimit_day() { return limit_day; }
    public int getLimit_hour() { return limit_hour; }
    public int getLimit_minut() { return limit_minut; }
    public int getLimit_month_mts() { return limit_month_mts; }
    public int getLimit_month_ks() { return limit_month_ks; }
    public int getLimit_month_life() { return limit_month_life; }
    public int getLimit_month_other() { return limit_month_other; }
    public int getLimit_day_mts() { return limit_day_mts; }
    public int getLimit_day_ks() { return limit_day_ks; }
    public int getLimit_day_life() { return limit_day_life; }
    public int getLimit_day_other() { return limit_day_other; }
    public int getLimit_hour_mts() { return limit_hour_mts; }
    public int getLimit_hour_ks() { return limit_hour_ks; }
    public int getLimit_hour_life() { return limit_hour_life; }
    public int getLimit_hour_other() { return limit_hour_other; }
    public int getLimit_minut_mts() { return limit_minut_mts; }
    public int getLimit_minut_ks() { return limit_minut_ks; }
    public int getLimit_minut_life() { return limit_minut_life; }
    public int getLimit_minut_other() { return limit_minut_other; }

    public Pool(String corp) throws SQLException {
        this.corp=corp;
        String query = new StringBuilder(200).append("select * from smssystem.pools where corp = '").append(corp).append("'").toString();
        AbstractList<HashMap> result = DBconnectNEW.getResultSet(query);
        for (HashMap rs : result){
            this.description=GetVal.getStr(rs, "description");
            this.tarif=GetVal.getStr(rs, "tarif");
            this.timeout=GetVal.getInt(rs, "timeout");
            this.limit_month=GetVal.getInt(rs, "limit_month");
            this.limit_day=GetVal.getInt(rs, "limit_day");
            this.limit_hour=GetVal.getInt(rs, "limit_hour");
            this.limit_minut=GetVal.getInt(rs, "limit_minut");
            this.limit_month_mts=GetVal.getInt(rs, "limit_month_mts");
            this.limit_month_ks=GetVal.getInt(rs, "limit_month_ks");
            this.limit_month_life=GetVal.getInt(rs, "limit_month_life");
            this.limit_month_other=GetVal.getInt(rs, "limit_month_other");
            this.limit_day_mts=GetVal.getInt(rs, "limit_day_mts");
            this.limit_day_ks=GetVal.getInt(rs, "limit_day_ks");
            this.limit_day_life=GetVal.getInt(rs, "limit_day_life");
            this.limit_day_other=GetVal.getInt(rs, "limit_day_other");
            this.limit_hour_mts=GetVal.getInt(rs, "limit_hour_mts");
            this.limit_hour_ks=GetVal.getInt(rs, "limit_hour_ks");
            this.limit_hour_life=GetVal.getInt(rs, "limit_hour_life");
            this.limit_hour_other=GetVal.getInt(rs, "limit_hour_other");
            this.limit_minut_mts=GetVal.getInt(rs, "limit_minut_mts");
            this.limit_minut_ks=GetVal.getInt(rs, "limit_minut_ks");
            this.limit_minut_life=GetVal.getInt(rs, "limit_minut_life");
            this.limit_minut_other=GetVal.getInt(rs, "limit_minut_other");
/*        DBconnectSelect select = new DBconnectSelect(query);
        ResultSet rs = select.getRs();
        while (rs.next()){
            this.description=rs.getString("description");
            this.tarif=rs.getString("tarif");
            this.timeout=rs.getInt("timeout");
            this.limit_month=rs.getInt("limit_month");
            this.limit_day=rs.getInt("limit_day");
            this.limit_hour=rs.getInt("limit_hour");
            this.limit_minut=rs.getInt("limit_minut");
            this.limit_month_mts=rs.getInt("limit_month_mts");
            this.limit_month_ks=rs.getInt("limit_month_ks");
            this.limit_month_life=rs.getInt("limit_month_life");
            this.limit_month_other=rs.getInt("limit_month_other");
            this.limit_day_mts=rs.getInt("limit_day_mts");
            this.limit_day_ks=rs.getInt("limit_day_ks");
            this.limit_day_life=rs.getInt("limit_day_life");
            this.limit_day_other=rs.getInt("limit_day_other");
            this.limit_hour_mts=rs.getInt("limit_hour_mts");
            this.limit_hour_ks=rs.getInt("limit_hour_ks");
            this.limit_hour_life=rs.getInt("limit_hour_life");
            this.limit_hour_other=rs.getInt("limit_hour_other");
            this.limit_minut_mts=rs.getInt("limit_minut_mts");
            this.limit_minut_ks=rs.getInt("limit_minut_ks");
            this.limit_minut_life=rs.getInt("limit_minut_life");
            this.limit_minut_other=rs.getInt("limit_minut_other");*/
        }
    }

}
