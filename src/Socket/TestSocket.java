package Socket;

import java.net.*;
import java.io.*;

public class TestSocket {

    public static void main(String args[]) throws IOException {

    String addr = "192.168.119.10";
    InetAddress ipAddress = InetAddress.getByName(addr);
    Socket socket = new Socket(ipAddress, 9993);
    InputStream sin = socket.getInputStream();
    OutputStream sout = socket.getOutputStream();
    DataInputStream in = new DataInputStream(sin);
    DataOutputStream out = new DataOutputStream(sout);
        String send = "svr_reboot_module 3 admin123\n";
        System.out.println("I'm sending it:" + send);
        out.writeUTF(send); // отсылаем клиенту обратно ту самую строку текста.
        out.flush(); // заставляем поток закончить передачу данных.
        System.out.println("Waiting for the next line...");
        System.out.println();
}
}
