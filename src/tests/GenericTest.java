package tests;

import java.util.ArrayList;
import java.util.HashMap;

public class GenericTest<T> {

    public T getObjectT() {
        return objectT;
    }

    public void setObjectT(T objectT) {
        this.objectT = objectT;
    }

    T objectT;
    String s;

    GenericTest(T object){
        this.objectT=object;
        //this.s=s;
    }

    @Override
    public String toString(){
        return new StringBuilder().append("toString-objectT-->").append(objectT).append("<--objectT-toString").append("---s:").append(s).toString();
    }

    public static void main(String[] args) {
        ArrayList al = new ArrayList(50);
        System.out.println(al);
        System.out.println(al.size());
        al.add("one");
        al.add("two");
        al.add(null);
        al.add("tree");
        System.out.println(al);
        System.out.println(al.size());
        System.out.println(al.get(0));
        HashMap<String,String> hm = new HashMap<String,String>();
        hm.put("1","one");
        hm.put("2","twoo");
        hm.put("3", "tree");
        System.out.println(hm.keySet());
        System.out.println(hm.values());
        ArrayList<String> als = null;
        System.out.println("als: "+ als);
        als = new ArrayList<String>(hm.values());
        System.out.println("als: "+ als);

    }

}

