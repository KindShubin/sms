package tests;

public class GenericTestRun {
    public static void main(String[] args) {
        int a = 33;
//        GenericTest<Integer> gtInteger = new GenericTest<>("lol");
//        gtInteger.setObjectT(new Integer(55));
//        System.out.println(gtInteger);
//        System.out.println("output inner value:" + gtInteger.getObjectT());
//        System.out.println("-------------");
        GenericTest<? extends Number> gtInteger1 = new GenericTest<Integer>(new Integer(44));
        System.out.println(gtInteger1);
        System.out.println("output inner value:"+gtInteger1.getObjectT());
        System.out.println("-------------");
//        GenericTest<Character> gtCharacter = new GenericTest<Character>('B');
//        System.out.println(gtCharacter);
//        System.out.println("output inner value:"+gtCharacter.getObjectT());
//        System.out.println("-------------");
//        GenericTest<String> gtString = new GenericTest<>("ololo");
//        System.out.println(gtString);
//        System.out.println("output inner value:"+gtString.getObjectT());
//        System.out.println("-------------");
//        GenericTest<Boolean> gtBoolean = new GenericTest<>(false);
//        System.out.println(gtBoolean);
//        System.out.println("output inner value:"+gtBoolean.getObjectT());
//        System.out.println("-------------");
    }
}
