import com.rabbitmq.client.*;

import java.io.IOException;
import java.util.StringTokenizer;
import java.util.concurrent.TimeoutException;

public class TeacherLog {
    private static final String EXCHANGE_NAME = "Exchange_Value";
    private static final String ROUTE_KEY = "teacher";

    public static void main(String[] args) throws IOException, TimeoutException {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("51.83.68.66");
        factory.setPort(5672);
        Connection connection = factory.newConnection();
        Channel channel = connection.createChannel();
        channel.exchangeDeclare(EXCHANGE_NAME, "topic");
        String queue = channel.queueDeclare().getQueue();
        channel.queueBind(queue, EXCHANGE_NAME, ROUTE_KEY);

        DeliverCallback deliverCallback = (consumerTag, delivery) -> {
            String message = new String(delivery.getBody(), "UTF-8");

            StringTokenizer st = new StringTokenizer(message,"#");
            var name = st.nextToken();
            var userType = st.nextToken();
            var roomType = st.nextToken();
            var roomNumber = st.nextToken();

            System.out.println("[x] Log - room unlocked by");
            System.out.println("\tName: " + name);
            System.out.println("\tUser Type: " + userType);
            System.out.println("\tRoom Type: " + roomType);
            System.out.println("\tRoom Number: " + roomNumber);
        };
        channel.basicConsume(queue, true, deliverCallback, cancelCollback -> {});
    }
}
