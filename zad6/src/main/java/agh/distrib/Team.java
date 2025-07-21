package agh.distrib;

import com.rabbitmq.client.*;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class Team {
    private static final Map<String, Integer> demands = new HashMap<>();

    public static void main(String[] args) throws Exception {
        String name = args[0];

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
                name,
                false,
                false,
                false,
                null
        ).getQueue();
        channel.queueBind(
                queueName,
                exchangeName,
                "*.delivery.*.%s.*".formatted(
                        name
                )
        );
        channel.basicConsume(
                queueName,
                false,
                new DefaultConsumer(channel) {
                    @Override
                    public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) throws IOException {
                        String[] key = envelope.getRoutingKey().split("\\.");
                        if(key.length != 5) {
                            channel.basicNack(envelope.getDeliveryTag(), false, false);
                            return;
                        }

                        String supplierName = key[0];
                        String deliveryID = key[2];
                        String supply = key[4];

                        synchronized (demands) {
                            System.out.println("Got '%s' from %s, deliveryID = %s".formatted(
                                    supply, supplierName, deliveryID
                            ));

                            demands.put(supply, demands.get(supply) - 1);
                        }

                        channel.basicAck(envelope.getDeliveryTag(), false);
                    }
                }
        );

        String toTeamsQueueName = channel.queueDeclare(
                "to%s".formatted(name),
                false,
                false,
                false,
                null
        ).getQueue();
        channel.queueBind(
                toTeamsQueueName,
                exchangeName,
                "to.team.#"
        );
        channel.basicConsume(
                toTeamsQueueName,
                false,
                new DefaultConsumer(channel) {
                    @Override
                    public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) throws IOException {
                        System.out.println("\nFrom Admin: '%s'".formatted(
                                new String(body, StandardCharsets.UTF_8)
                        ));
                        channel.basicAck(envelope.getDeliveryTag(), false);
                    }
                }
        );

        for(;;) {
            System.out.print("Supplies: ");
            BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
            String[] inArgs = in.readLine().split(" ");
            if(inArgs.length == 0) {
                continue;
            }
            if(inArgs[0].equals("exit")) {
                break;
            }
            synchronized (demands) {
                for (String arg : inArgs) {
                    demands.putIfAbsent(arg, 0);
                    demands.put(arg, demands.get(arg) + 1);
                }

                for (String supply : demands.keySet()) {
                    for (int i = 0, end = demands.get(supply); i != end; ++i) {
                        String key = "%s.supply.%s".formatted(
                                name, supply
                        );
                        channel.basicPublish(
                                exchangeName,
                                key,
                                new AMQP.BasicProperties(),
                                new byte[]{}
                        );

                        // manualne wysyłanie do admina
//                        channel.basicPublish(
//                                exchangeName,
//                                "copy",
//                                new AMQP.BasicProperties(),
//                                key.getBytes(StandardCharsets.UTF_8)
//                        );
                    }
                }
            }
            for(;; Thread.sleep(10)) {
                synchronized (demands) {
                    if(demands.values().stream()
                            .reduce(Integer::sum)
                            .get() == 0
                    ) {
                        System.out.println("Demand satisfied");
                        break;
                    }
                }
            }
        }

        channel.close();
        connection.close();
    }
}
