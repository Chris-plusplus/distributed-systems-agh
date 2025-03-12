package org.example.zad3;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.util.Arrays;

public class JavaUdpServer {

    public static void main(String args[])
    {
        System.out.println("JAVA UDP SERVER");
        DatagramSocket socket = null;
        int portNumber = 9008;

        try{
            socket = new DatagramSocket(portNumber);
            byte[] receiveBuffer = new byte[1024];

            while(true) {
                Arrays.fill(receiveBuffer, (byte)0);
                DatagramPacket receivePacket = new DatagramPacket(receiveBuffer, receiveBuffer.length);
                socket.receive(receivePacket);
                int recvd = ByteBuffer.wrap(receiveBuffer).getInt();
                int recvdProper = 0;
                recvdProper |= recvd & 0xff;
                recvdProper <<= 8;
                recvdProper |= (recvd >> 8) & 0xff;
                recvdProper <<= 8;
                recvdProper |= (recvd >> 16) & 0xff;
                recvdProper <<= 8;
                recvdProper |= (recvd >> 24) & 0xff;
                System.out.println("Received: " + recvdProper);
                ++recvdProper;

                recvd = 0;
                recvd |= recvdProper & 0xff;
                recvd <<= 8;
                recvd |= (recvdProper >> 8) & 0xff;
                recvd <<= 8;
                recvd |= (recvdProper >> 16) & 0xff;
                recvd <<= 8;
                recvd |= (recvdProper >> 24) & 0xff;

                var buff = ByteBuffer.allocate(4).putInt(recvd).array();

                DatagramPacket sendPacket = new DatagramPacket(buff, buff.length, receivePacket.getAddress(), portNumber + 1);
                socket.send(sendPacket);
                System.out.println("Sent");
            }
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
