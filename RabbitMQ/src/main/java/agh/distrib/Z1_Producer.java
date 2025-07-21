package agh.distrib;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.text.ParseException;

public class Z1_Producer {

    public static void main(String[] argv) throws Exception {

        // info
        System.out.println("Z1 PRODUCER");

        // connection & channel
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("localhost");
        Connection connection = factory.newConnection();
        Channel channel = connection.createChannel();

        // queue
        String QUEUE_NAME = "queue1";
        channel.queueDeclare(QUEUE_NAME, false, false, false, null);

        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));

        // producer (publish msg)
//        String message = br.readLine();
//        while (true){
//            try{
//                int i = Integer.parseInt(message);
//                break;
//            }
//            catch (NumberFormatException ignored){
//                System.out.println("Invalid input");
//                message = br.readLine();
//            }
//        }

        for(int i = 0; i != 10; ++i){
            String message = i % 2 == 0 ? "1" : "5";
            channel.basicPublish("", QUEUE_NAME, null, message.getBytes());
        }

//        channel.basicPublish("", QUEUE_NAME, null, message.getBytes());
//        System.out.println("Sent: " + message);

        // close
        channel.close();
        connection.close();
    }
}
