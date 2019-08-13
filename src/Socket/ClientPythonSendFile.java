package Socket;

import java.io.*;
import java.net.Socket;
import java.nio.file.Path;
import java.nio.file.Paths;

public class ClientPythonSendFile {

    public final static int SOCKET_PORT = 34444;      // you may change this
    public final static String SERVER = "91.222.137.187";  // localhost
    public final static String FILE_TO_SEND = "C:/javaProjects/TestSMS/src/xml/3tests.xml";  // you may change this, I give a
    // different name because i don't want to
    // overwrite the one used by server...

    public final static int FILE_SIZE = 6022386; // file size temporary hard coded
    // should bigger than the file to be downloaded

    public final static Path path = Paths.get(FILE_TO_SEND);


        public static void main(String[] args) throws IOException {
            Socket socket = null;
            //String host = SERVER;

            socket = new Socket(SERVER, SOCKET_PORT);

            File file = new File(FILE_TO_SEND);
            System.out.println(FILE_TO_SEND);
            System.out.println(file.toString());
            // Get the size of the file
            long length = file.length();
            System.out.println("length "+length);
            byte[] bytes = new byte[16 * 1024];
            InputStream in = new FileInputStream(file);
            OutputStream out = socket.getOutputStream();

            int count;
            while ((count = in.read(bytes)) > 0) {
                out.write(bytes, 0, count);
                System.out.println("something write");
            }

            out.close();
            in.close();
            socket.close();

    }
}
