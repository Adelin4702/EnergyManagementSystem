package org.example;

import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.example.config.RabbitMQConfig;

public class RabbitMQSender {

    private RabbitTemplate rabbitTemplate;

    public RabbitMQSender() {
        // Setup the ConnectionFactory
        ConnectionFactory connectionFactory = createConnectionFactory();

        // Initialize the RabbitTemplate with the connection factory
        this.rabbitTemplate = new RabbitTemplate(connectionFactory);
    }

    private ConnectionFactory createConnectionFactory() {
        // This assumes RabbitMQ is running locally. Adjust your settings as needed.
        CachingConnectionFactory connectionFactory = new CachingConnectionFactory("localhost");  // RabbitMQ host
        connectionFactory.setUsername("guest");  // default username
        connectionFactory.setPassword("guest");  // default password
        connectionFactory.setVirtualHost("/");   // default virtual host
        connectionFactory.setPort(5672);        // The default AMQP port
        return connectionFactory;
    }

    public void sendMessageToQueue(String queueName, String message) {
        rabbitTemplate.convertAndSend(queueName, message);  // Send the message to the specified queue
    }
}
