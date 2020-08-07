package tests;

public class StaticV {
    public static void main(String[] args) {
        System.out.println("Class1.val="+Class1.val);
        System.out.println("Class2.val="+Class2.val);
        System.out.println("Class3.val="+Class3.val);
        System.out.println("Class4.val="+Class4.val);
        Class2.val=2;
        System.out.println("Class1.val="+Class1.val);
        System.out.println("Class2.val="+Class2.val);
        System.out.println("Class3.val="+Class3.val);
        System.out.println("Class4.val="+Class4.val);
        Class3.val=3;
        System.out.println("Class1.val="+Class1.val);
        System.out.println("Class2.val="+Class2.val);
        System.out.println("Class3.val="+Class3.val);
        System.out.println("Class4.val="+Class4.val);
    }
}

class Class1{
    public static int val=1;
}

class Class2 extends Class1{
}

class Class3 extends Class2{
    public static int val;
}

class Class4 extends Class3{
}
