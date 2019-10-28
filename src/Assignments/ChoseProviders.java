package Assignments;

import DB.DBconnectVPS;
import DB.DBconnectUpdate;
import DB.GetVal;
import LogsParts.LogsId;
import LogsParts.LogsT;
import sms.Sms;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

public class ChoseProviders {

    private static final String PART_OF_QUERY_ASSIGNMENTS = "SELECT * FROM smssystem.assignments where idClient=";
    private static final String PART_OF_QUERY_SMSLOGS = "SELECT * FROM smssystem.smslogs where id=";
    private long sms_id;
    private int client_id;
    private int provider_id;
    private int qntProviders = 0;
    private ArrayList<ArrayList<String>> result;
    private int[] arrayPercents;
    private int[] arrayProvidersId;

    public int getProvider_id() {
        return provider_id;
    }

    public ChoseProviders(Sms sms) throws SQLException {
        sms_id = sms.getId();
        client_id = sms.getClient_id();
        try{
            provider_id = sms.getProvider_id();
        } catch (NullPointerException e) {
            e.printStackTrace();
            System.out.println(LogsT.printDate() + LogsId.id(sms_id) + "Provider is not specified in sms id " + sms_id);
        }
        System.out.println(LogsT.printDate() + LogsId.id(sms_id) + "provider_id: "+provider_id);
        if(provider_id<1){              // ==1 27.06.2016
            choseProvider_id(sms_id);
            sms.setProvider_id(provider_id);
        }
    }

    public void choseProvider_id(long sms_id) throws SQLException {
        String query = new StringBuilder().append(PART_OF_QUERY_ASSIGNMENTS).append(client_id).toString();
        int qntColumns = DBconnectVPS.qntRowsInSelect(query);
        System.out.println(LogsT.printDate() + LogsId.id(sms_id) + "choseProvider_id() || qntColumns:" + qntColumns);
        ArrayList<HashMap> resultQuery = DBconnectVPS.getResultSet(query);
        System.out.println(LogsT.printDate() + LogsId.id(sms_id) + "resultQuery:");
        System.out.println(LogsT.printDate() + LogsId.id(sms_id) + resultQuery);
        result = new ArrayList<ArrayList<String>>();  // List of list, one per row
        for (HashMap rs : resultQuery) {
            System.out.println(LogsT.printDate() + LogsId.id(sms_id) + "HashMap rs:");
            System.out.println(LogsT.printDate() + LogsId.id(sms_id) + rs);
            ArrayList<String> row = new ArrayList<String>(rs.values());
            result.add(row); // add it to the result
        }
                            System.out.println(LogsT.printDate() + LogsId.id(sms_id) + "result:");
                            System.out.println(LogsT.printDate() + LogsId.id(sms_id) + result);
        qntProviders(result);
                            System.out.println(LogsT.printDate() + LogsId.id(sms_id) + "qntProviders: " + qntProviders);

        arrayPercents = new int[qntProviders];
        arrayProvidersId = new int[qntProviders];
//        System.out.println(LogsT.printDate() + LogsId.id(sms_id) + "arrayPercents:");
//        System.out.println(LogsT.printDate() + LogsId.id(sms_id) + arrayPercents);
//        System.out.println(LogsT.printDate() + LogsId.id(sms_id) + "arrayProvidersId:");
//        System.out.println(LogsT.printDate() + LogsId.id(sms_id) + arrayProvidersId);
//        System.out.println(LogsT.printDate() + LogsId.id(sms_id));
        System.out.println(LogsT.printDate() + LogsId.id(sms_id) + "result.size():"+result.size());
        System.out.println(LogsT.printDate() + LogsId.id(sms_id) + "resultQuery.size():" + resultQuery.size());
        //for (int i = 0; i < result.size(); i++) {
        for (int i = 0; i < resultQuery.size(); i++) {
//            System.out.println(LogsT.printDate() + LogsId.id(sms_id) + "resultQuery.get(i).get(\"idClient\"):"+resultQuery.get(i).get("idClient"));
//            System.out.println(LogsT.printDate() + LogsId.id(sms_id) + "resultQuery.get(i).get(\"idProv\"):"+resultQuery.get(i).get("idProv"));
//            System.out.println(LogsT.printDate() + LogsId.id(sms_id) + "resultQuery.get(i).get(\"prioritet\"):"+resultQuery.get(i).get("prioritet"));
//            System.out.println(LogsT.printDate() + LogsId.id(sms_id) + "resultQuery.get(i).get(\"percents\"):"+resultQuery.get(i).get("percents"));
            //List<String> strings = result.get(i);
            arrayPercents[i] = GetVal.getInt(resultQuery.get(i),"percents");
            arrayProvidersId[i] = GetVal.getInt(resultQuery.get(i),"idProv");
//            arrayPercents[i] = Integer.parseInt(result.get(i).get(3));
//            arrayProvidersId[i] = Integer.parseInt(result.get(i).get(1));
                            System.out.print(LogsT.printDate() + LogsId.id(sms_id) + "Provider:"+arrayProvidersId[i]+" percent: " + arrayPercents[i] + "; ");
                            System.out.println();
        }
        int randSumPercent=getRandom(sumPercents(arrayPercents))+1;
        this.provider_id=generateIdProvider(arrayPercents, arrayProvidersId, randSumPercent);

        //DBconnectUpdate providerUpdate = new DBconnectUpdate(new StringBuilder(200).append(PART_OF_QUERY_SMSLOGS).append(sms_id).toString());
        DBconnectUpdate.updateProviderIdForSms(sms_id, provider_id);
    }

    public int generateIdProvider(int[] arrayPer, int[]arrayProv, int rand){
        System.out.println(LogsT.printDate() + LogsId.id(sms_id) + "rand "+rand);
        int idProv=9999;
        int a = 0;
        for(int i=0; i<arrayPer.length; i++){
            a = a+arrayPer[i];
            //System.out.println("arrayPer[i] "+ a);
            if (rand<=a){
                idProv = arrayProv[i];
                break;
            }
        }
        return idProv;
    }

    public int sumPercents(int[] arr){
        int summ = 0;
        for(int i=0; i<arr.length; i++){
            summ = summ + arr[i];
        }
        return summ;
    }

    public int getRandom(int summ){
        Random random = new Random();
        return random.nextInt(summ);
    }

    public void qntProviders(ArrayList<ArrayList<String>> ll){
        for (int i=0; i<ll.size();i++) {
            qntProviders++;
        }
    }

    public void printQuery(){
        System.out.print(LogsT.printDate() + LogsId.id(sms_id));
        for (int i = 0; i < result.size(); i++) {
            ArrayList<String> strings = result.get(i);
            for (int j = 0; j < strings.size(); j++) {
                System.out.print(strings.get(j) + " ");
            }
            System.out.println(LogsT.printDate() + LogsId.id(sms_id));
        }
    }

}
