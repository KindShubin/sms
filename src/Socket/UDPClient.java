package Socket;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.*;

public class UDPClient {

    public static void main(String args[]) throws Exception{
//        new UDPClient().udpC(100000001,"Send");
//        new UDPClient().udpC(100000002,"Send");
        new UDPClient().udpC(100000003,"Error");
       new UDPClient().udpC(100000004,"Send");
 //       new UDPClient().udpC();
    } // See more at: https://systembash.com/a-simple-java-udp-server-and-udp-client/#sthash.4bF6Dtyw.dpuf

    public void udpC() throws Exception {
        System.out.println("...");
        BufferedReader inFromUser = new BufferedReader(new InputStreamReader(System.in));
        DatagramSocket clientSocket = new DatagramSocket();
        InetAddress IPAddress = InetAddress.getByName("91.222.137.187");
        byte[] sendData = new byte[1024];
        byte[] receiveData = new byte[1024];
        String sentence = inFromUser.readLine();
        System.out.println("sentence: "+sentence);
        sendData = sentence.getBytes();
        //DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, IPAddress, 21333);
        DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, IPAddress, 34444);
        System.out.println("sendPacket"+sendPacket);
        clientSocket.send(sendPacket);
        System.out.println(1);
        DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
        System.out.println(2);
        clientSocket.receive(receivePacket);
        String modifiedSentence = new String(receivePacket.getData());
        System.out.println("FROM SERVER:" + modifiedSentence);
        clientSocket.close();
    }
    public void udpC(int id, String status) throws Exception {
        //BufferedReader inFromUser = new BufferedReader(new InputStreamReader(System.in));
        DatagramSocket clientSocket = new DatagramSocket();
        InetAddress IPAddress = InetAddress.getByName("91.222.137.187");
        byte[] sendData = new byte[1024];
        byte[] receiveData = new byte[1024];
        //String sentence = inFromUser.readLine();
        String sentence = new StringBuilder().append(id).append("; ").append(status).toString();
        System.out.println("Send to Server: "+sentence);
        sendData = sentence.getBytes();
//        DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, IPAddress, 21333);
        DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, IPAddress, 34444);
        clientSocket.send(sendPacket);
        DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
        clientSocket.receive(receivePacket);
        String modifiedSentence = new String(receivePacket.getData());
        System.out.println("OK. Server received: " + modifiedSentence);
        clientSocket.close();
    }
}
