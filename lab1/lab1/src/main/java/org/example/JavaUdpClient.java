package org.example;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

public class JavaUdpClient {

    public static void main(String args[]) throws Exception
    {
        System.out.println("JAVA UDP CLIENT");
        DatagramSocket socket = null;
        DatagramSocket socketRecv = null;
        int portNumber = 9008;

        try {
            socket = new DatagramSocket();
            InetAddress address = InetAddress.getByName("localhost");
            byte[] sendBuffer = "Ping Java Udp".getBytes();

            DatagramPacket sendPacket = new DatagramPacket(sendBuffer, sendBuffer.length, address, portNumber);
            socket.send(sendPacket);

            socketRecv = new DatagramSocket(portNumber + 1);
            DatagramPacket receivePacket = new DatagramPacket(new byte[1024], 1024);
            socketRecv.receive(receivePacket);

            System.out.println(new String(receivePacket.getData()));
            System.out.println("from: %s".formatted(receivePacket.getAddress()));
        }
        catch(Exception e){
            e.printStackTrace();
        }
        finally {
            if (socket != null) {
                socket.close();
            }
        }
    }
}
