import com.rabbitmq.client.BuiltinExchangeType;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

import java.util.Scanner;

public class Person {
    private static final String EXCHANGE_NAME = "Exchange_Value";

    public static void main(String[] args) {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("51.83.68.66");
        factory.setPort(5672);

        Scanner scanner = new Scanner(System.in);
        System.out.println("Reading user name from card:");
        String name = scanner.nextLine();
        System.out.println("Reading user type from card (\"teacher\" or \"student\"):");
        String userType = scanner.nextLine();
        System.out.println("Room type (\"classroom\", \"laboratory\", or \"office\"):");
        String roomType = scanner.nextLine();
        System.out.println("Room number:");
        int roomNumber = scanner.nextInt();

        try(Connection connection = factory.newConnection()) {
            Channel channel = connection.createChannel();
            channel.exchangeDeclare(EXCHANGE_NAME, BuiltinExchangeType.TOPIC);
            String routingKey = "";

            if (userType.equals("student")) {
                if (roomType.equals("office")) {
                    routingKey = "student_in_office";
                } else {
                    routingKey = "student";
                }
            } else if (userType.equals("teacher")) {
                routingKey = "teacher";
            } else {
                System.out.println("Invalid data ...");
                return;
            }

            var message = name + "#" + userType + "#" + roomType + "#" + roomNumber;

            channel.basicPublish(EXCHANGE_NAME, routingKey,null, message.getBytes("UTF-8"));
            System.out.println("[x] Sent '" + routingKey + "':'" + message + "'");

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
