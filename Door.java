import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.DeliverCallback;

import java.util.StringTokenizer;

public class Door {
    private static final String EXCHANGE_NAME = "Exchange_Value";
    public static void main(String[] args) {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("51.83.68.66");
        factory.setPort(5672);

        try {
            Connection connection = factory.newConnection();
            Channel channel = connection.createChannel();

            channel.exchangeDeclare(EXCHANGE_NAME, "topic");

            String queue = channel.queueDeclare().getQueue();
            channel.queueBind(queue, EXCHANGE_NAME, "teacher");
            channel.queueBind(queue, EXCHANGE_NAME, "student");
            channel.queueBind(queue, EXCHANGE_NAME, "student_in_office");

            DeliverCallback deliverCallback = (consumerTag, delivery) -> {
                String message = new String(delivery.getBody(), "UTF-8");
                StringTokenizer stringTokenizer = new StringTokenizer(message, "#");
                String name = stringTokenizer.nextToken();
                String userType = stringTokenizer.nextToken();
                String roomType = stringTokenizer.nextToken();
                int roomNumber = Integer.parseInt(stringTokenizer.nextToken());

                System.out.println("[x] Log - room unlocked by");
                System.out.println("\tName: " + name);
                System.out.println("\tUser Type: " + userType);
                System.out.println("\tRoom Type: " + roomType);
                System.out.println("\tRoom Number: " + roomNumber);
            };
            channel.basicConsume(queue, true, deliverCallback, consumerTag -> { });
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
