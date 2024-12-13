package ro.tuc.ds2020.config;

import org.springframework.amqp.core.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.UUID;

@Configuration
public class RabbitMQConfig {

    public static final String QUEUE_DEVICE = "deviceQueue";
    public static final String QUEUE_SIMULATOR = "simulatorQueue";
    public static final String EXCHANGE_NAME = "monitorExchange";
    public static final String ROUTING_KEY_DEVICE = "monitor.device";
    public static final String ROUTING_KEY_SIMULATOR = "monitor.simulator";
    public static final String TOPIC_EXCHANGE_NAME = "device_topic";
    public static final String TOPIC_ALERT = "alert_topic";  // Defined as alert topic
    public static final String ALERT_ROUTING_KEY = "alert.notification";  // You can choose more specific keys, like `alert.*`

    // Define Topic Exchange for Devices
    @Bean
    public TopicExchange topicExchange() {
        return new TopicExchange(TOPIC_EXCHANGE_NAME); // This is for device-related topics
    }

    // Define Topic Exchange for Alerts
    @Bean
    public FanoutExchange topicExchangeAlert() {
        return new FanoutExchange("alertFanoutExchange"); // This is for alert-related topics
    }


    // Define the Direct Exchange (unchanged)
    @Bean
    public DirectExchange exchange() {
        return new DirectExchange(EXCHANGE_NAME);
    }

    // Define Queue for Alerts
    @Bean
    public Queue alertQueue() {
        String queueName = "alertQueue-" + UUID.randomUUID();
        return new Queue(queueName, true);  // Declare a durable queue for alerts
    }

    // Define Queue for Device (unchanged)
    @Bean
    public Queue deviceQueue() {
        return new Queue(QUEUE_DEVICE, true);
    }

    // Define Queue for Simulator (unchanged)
    @Bean
    public Queue simulatorQueue() {
        return new Queue(QUEUE_SIMULATOR, true);
    }

    // Binding for Device Queue to Device Exchange
    @Bean
    public Binding bindingDeviceQueue(Queue deviceQueue, TopicExchange topicExchange) {
        return BindingBuilder.bind(deviceQueue).to(topicExchange()).with(ROUTING_KEY_DEVICE);
    }

    // Binding for Simulator Queue to Direct Exchange
    @Bean
    public Binding bindingSimulatorQueue(Queue simulatorQueue, DirectExchange exchange) {
        return BindingBuilder.bind(simulatorQueue).to(exchange).with(ROUTING_KEY_SIMULATOR);
    }

    // Binding for Alert Queue to the Alert Exchange with Routing Key
    @Bean
    public Binding bindingAlertQueue(Queue alertQueue, FanoutExchange fanoutExchange) {
        return BindingBuilder.bind(alertQueue).to(fanoutExchange);
    }

}
