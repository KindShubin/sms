package Exceptions;

public class MyNullPointerEx extends NullPointerException {
    public MyNullPointerEx() {
        super();
    }
    public MyNullPointerEx(String message) {
        super(message);
    }
//    public MyNullPointerEx(String message, Throwable cause) {
//        super(message, cause);
//    }
//    public MyNullPointerEx(Throwable cause) {
//        super(cause);
//    }
}
