
---

# JMS Project

This project demonstrates a messaging system using ActiveMQ and Java. It includes functionalities for private messaging and group chat.

## Requirements

- **JDK-22**
- **ActiveMQ**

## Setup

1. **Install JDK-22**: Ensure JDK-22 is installed on your system.
2. **Install ActiveMQ**: Download and install [ActiveMQ](https://activemq.apache.org/getting-started).

## Running the Project

### Private Message

`Client.java` handles private messaging between two users.

```java
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
                        System.out.println(friendName + ": " + textMessage.getText());
                    }
                }
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
                }
            } catch (JMSException e) {
                e.printStackTrace();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }).start();
    }
}
```

### Group Chat

`GroupChat.java` handles group messaging.

```java
package org.example;

import org.apache.activemq.ActiveMQConnection;
import org.apache.activemq.ActiveMQConnectionFactory;

import javax.jms.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class GroupChat {
    private static String url = ActiveMQConnection.DEFAULT_BROKER_URL;
    public static void main(String[] args) throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        System.out.println("Enter your name: ");
        String name = reader.readLine();

        new Thread(() -> {
            try {
                ConnectionFactory connectionFactory = new ActiveMQConnectionFactory(url);
                Connection connection = connectionFactory.createConnection();
                connection.start();
                Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
                Destination destination = session.createTopic("groupChat");
                MessageConsumer consumer = session.createConsumer(destination);
                while (true) {
                    Message message = consumer.receive();
                    if (message instanceof TextMessage textMessage) {
                        if (!textMessage.getText().startsWith(name))
                            System.out.println(textMessage.getText());
                    }
                }
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
                Destination destination = session.createTopic("groupChat");
                MessageProducer producer = session.createProducer(destination);
                while (true) {
                    TextMessage message = session.createTextMessage(name + ": " + reader.readLine());
                    producer.send(message);
                }
            } catch (JMSException e) {
                e.printStackTrace();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }).start();
    }
}
```

## Screenshots

### Private Message
![Group Chat](https://github.com/Khaderselim/JMS/blob/main/Screenshots/Screenshot%202024-12-25%20163933.png)

### Group Chat
![Private Message](https://github.com/Khaderselim/JMS/blob/main/Screenshots/Screenshot%202024-12-25%20163554.png)


---
