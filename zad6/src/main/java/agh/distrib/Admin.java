package agh.distrib;

import com.rabbitmq.client.*;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

public class Admin {
    public static void main(String[] args) throws Exception {
        ConnectionFactory cf = new ConnectionFactory();
        cf.setHost("localhost");
        String exchangeName = "exchange";

        Connection connection = cf.newConnection();
        Channel channel = connection.createChannel();
        channel.exchangeDeclare(
                exchangeName,
                BuiltinExchangeType.TOPIC
        );

        String queueName = channel.queueDeclare(
                "copyQueue",
                false,
                false,
                false,
                null
        ).getQueue();
        channel.queueBind(
                queueName,
                exchangeName,
                // wszystkie wiadomości
                "#"
                // manualne kopie
                // "copy"
        );
        channel.basicConsume(
                queueName,
                false,
                new DefaultConsumer(channel) {
                    @Override
                    public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) throws IOException {
//                        synchronized (System.out) {
                            System.out.println();
                            System.out.println("Got message '%s': '%s'".formatted(
                                    envelope.getRoutingKey(),
                                    new String(body, StandardCharsets.UTF_8)
                            ));
//                        }
                        channel.basicAck(envelope.getDeliveryTag(), false);
                    }
                }
        );

        for(;;Thread.sleep(10)) {
//            synchronized (System.out) {
                System.out.print("exit/where?: ");
                BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
                String where = in.readLine();
                if (where.equals("exit")) {
                    break;
                }
                if (!where.equals("suppliers") && !where.equals("teams") && !where.equals("all")) {
                    continue;
                }

                System.out.print("what?: ");
                String what = in.readLine();

                if (where.equals("suppliers") || where.equals("all")) {
                    channel.basicPublish(
                            exchangeName,
                            "to.supplier.all",
                            new AMQP.BasicProperties(),
                            what.getBytes(StandardCharsets.UTF_8)
                    );
                }
                if (where.equals("teams") || where.equals("all")) {
                    channel.basicPublish(
                            exchangeName,
                            "to.team.all",
                            new AMQP.BasicProperties(),
                            what.getBytes(StandardCharsets.UTF_8)
                    );
                }
//            }
        }

        channel.close();
        connection.close();
    }
}
