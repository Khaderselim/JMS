package org.example;

import org.apache.activemq.ActiveMQConnection;
import org.apache.activemq.ActiveMQConnectionFactory;

import javax.jms.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class Client {
    private static String url = ActiveMQConnection.DEFAULT_BROKER_URL;
    public static void main(String[] args) throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        System.out.println("Enter your name: ");
        String name = reader.readLine();
        System.out.println("Enter your friend's name: ");
        String friendName = reader.readLine();


        new Thread(() -> {
            try {

                ConnectionFactory connectionFactory = new ActiveMQConnectionFactory(url);
                Connection connection = connectionFactory.createConnection();
                connection.start();
                Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
                Destination destination = session.createQueue(name);
                MessageConsumer consumer = session.createConsumer(destination);
                while (true) {
                    Message message = consumer.receive();
                    if (message instanceof TextMessage textMessage) {
                        System.out.println(friendName+": " + textMessage.getText() );
                    }
                }
//                connection.close();
            } catch (JMSException e) {
                e.printStackTrace();
            }
        }).start();
        new Thread(() -> {
            try {
                ConnectionFactory connectionFactory = new ActiveMQConnectionFactory(url);
                Connection connection = connectionFactory.createConnection();
                connection.start();
                Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
                Destination destination = session.createQueue(friendName);
                MessageProducer producer = session.createProducer(destination);
                while (true) {
                    TextMessage message = session.createTextMessage(reader.readLine());
                    producer.send(message);
                    //System.out.println("JMS Message Sent successfully: " + message.getText());
                }
//                connection.close();
            } catch (JMSException e) {
                e.printStackTrace();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }).start();
    }
}