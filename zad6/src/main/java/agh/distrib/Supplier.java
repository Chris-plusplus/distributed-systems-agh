package agh.distrib;

import com.rabbitmq.client.*;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Supplier {
    private static final List<Integer> deliveryID = new ArrayList<>();
    private static int getDeliveryID() {
        if (deliveryID.isEmpty()) {
            deliveryID.add(0);
        }
        return deliveryID.set(0, deliveryID.getFirst() + 1);
    }

    public static void main(String[] args) throws Exception {
        List<String> supplies = new ArrayList<>();
        supplies.addAll(Arrays.asList(args));
        String name = supplies.removeFirst();

        System.out.println("Supplier:\n\t%s".formatted(supplies));

        ConnectionFactory cf = new ConnectionFactory();
        cf.setHost("localhost");
        String exchangeName = "exchange";

        Connection connection = cf.newConnection();
        Channel channel = connection.createChannel();
        channel.exchangeDeclare(
                exchangeName,
                BuiltinExchangeType.TOPIC
        );
        for(String supply : supplies) {
            String queueName = channel.queueDeclare(
                    supply,
                    false,
                    false,
                    false,
                    null
            ).getQueue();
            channel.queueBind(
                    queueName,
                    exchangeName,
                    "*.supply.%s".formatted(supply)
            );
            channel.basicConsume(
                    queueName,
                    false,
                    new DefaultConsumer(channel) {
                        @Override
                        public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) throws IOException {
                            String[] key = envelope.getRoutingKey().split("\\.");
                            if(key.length != 3) {
                                channel.basicNack(envelope.getDeliveryTag(), false, false);
                                return;
                            }
                            String teamName = key[0];

                            synchronized (deliveryID) {
                                System.out.println("Got request from '%s' for '%s'".formatted(
                                        teamName, supply
                                ));
                                int currDeliveryID = getDeliveryID();
                                System.out.println("Delivery ID: %d".formatted(currDeliveryID));
                                String target = "%s.delivery.%d.%s.%s".formatted(
                                        name,
                                        currDeliveryID,
                                        teamName,
                                        supply
                                );
                                System.out.println("Sending to: '%s'".formatted(target));
                                channel.basicPublish(
                                        exchangeName,
                                        target,
                                        new AMQP.BasicProperties(),
                                        new byte[]{}
                                );

                                // manualne wysyłanie do admina
//                                channel.basicPublish(
//                                        exchangeName,
//                                        "copy",
//                                        new AMQP.BasicProperties(),
//                                        target.getBytes(StandardCharsets.UTF_8)
//                                );

                                channel.basicAck(envelope.getDeliveryTag(), false);
                            }
                        }
                    }
            );
        }
        String queueName = channel.queueDeclare(
                "to%s".formatted(name),
                false,
                false,
                false,
                null
        ).getQueue();
        channel.queueBind(
                queueName,
                exchangeName,
                "to.supplier.#"
        );
        channel.basicConsume(
                queueName,
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
    }
}
