package DB;

import java.sql.Timestamp;

import java.util.HashMap;

public class GetVal {

    public static Integer getInt(HashMap hm, String name){
        //System.out.println("getInt begin");
        int val;
        try{ val = (int) hm.get(name); }
        catch (Exception e){
//            System.out.println(e);
//            e.printStackTrace();
            try{
                String substr = String.valueOf(hm.get(name));
                //System.out.println("subStr in setInt is "+substr);
                val = substr=="null" ? 0 : Integer.valueOf(substr);
            }
            catch (Exception e1){
                //System.out.println("e1.getStackTrace(): " + e1.getStackTrace() + "\ne1.printStackTrace():");
                //e1.printStackTrace();
                System.out.println(e1);
                e1.printStackTrace();
                val=0;
            }
        }
        //System.out.println("getInt end. Val="+val);
        return val;
    }

    public static Long getLong(HashMap hm, String name){
        //System.out.println("getLong start");
        long val;
        try{ val = (long) hm.get(name); }
        catch (Exception e){
//            System.out.println(e);
            try{
                String substr = String.valueOf(hm.get(name));
                //System.out.println("subStr in setLong is "+substr);
                val = substr=="null" ? 0L : Long.valueOf(substr);
            }
            catch (Exception e1){
                System.out.println(e1);
                val=0L;
            }
        }
        //System.out.println("getLong end. Val="+val);
        return val;
    }

    public static Double getDouble(HashMap hm, String name){
        //System.out.println("getLong start");
        double val;
        try{ val = (double) hm.get(name); }
        catch (Exception e){
//            System.out.println(e);
            try{
                String substr = String.valueOf(hm.get(name));
                //System.out.println("subStr in setLong is "+substr);
                val = substr=="null" ? 0L : Double.valueOf(substr);
            }
            catch (Exception e1){
                System.out.println(e1);
                val=0;
            }
        }
        //System.out.println("getLong end. Val="+val);
        return val;
    }

    public static String getStr(HashMap hm, String name){
        //System.out.println("getStr start");
        String val;
        try{ val = (String) hm.get(name); }
        catch (Exception e){
//            System.out.println(e);
            try{ val = String.valueOf(hm.get(name)); }
            catch (Exception e1){
                System.out.println(e1);
                val="";
            }
        }
        //System.out.println("getStr end. Val="+val);
        return val;
    }

    public static Character getChar(HashMap hm, String name){
        //System.out.println("getChar start");
        char val;
        try{ val = (char) hm.get(name); }
        catch (Exception e){
            System.out.println(e);
            try{ val = Character.valueOf((Character) hm.get(name)); }
            catch (Exception e1){
                System.out.println(e1);
                val='!';
            }
        }
        //System.out.println("getChar end. Val="+val);
        return val;
    }

    public static Timestamp getTimeS(HashMap hm, String name){
        //System.out.println("getTimeS start");
        Timestamp val;
        try{ val = (Timestamp) hm.get(name); }
        catch (Exception e){
//            System.out.println(e);
            try {
                String substr = String.valueOf(hm.get(name));
                val = substr=="null" ? null : Timestamp.valueOf(substr);
            }
            catch (Exception e1){
                System.out.println(e1);
                val=null;
            }
        }
        //System.out.println("getTimeS end. Val="+val);
        return val;
    }

    public static Boolean getBool(HashMap hm, String name){
        //System.out.println("getBool start");
        boolean val;
        try{ val = (Boolean) hm.get(name); }
        catch (Exception e){
//            System.out.println(e);
            try {
                String substr = String.valueOf(hm.get(name));
                //System.out.println("subString in setBool is "+substr);
                if (substr=="true" || substr=="True" || substr=="TRUE"){ val = true;}
                else val = false;
                //System.out.println("result setBool: "+val);
            }
            catch (Exception e1){
                System.out.println(e1);
                val=false;
            }
        }
        //System.out.println("getBool end. return "+val);
        return val;
    }

}
