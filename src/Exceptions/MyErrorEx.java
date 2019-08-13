package Exceptions;

public class MyErrorEx extends Error {
    public MyErrorEx() {
        super();
    }
    public MyErrorEx(String message) {
        super(message);
    }
//    public MyNullPointerEx(String message, Throwable cause) {
//        super(message, cause);
//    }
//    public MyNullPointerEx(Throwable cause) {
//        super(cause);
//    }
}
