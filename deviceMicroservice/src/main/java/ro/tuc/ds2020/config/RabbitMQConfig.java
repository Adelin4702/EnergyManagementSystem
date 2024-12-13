package ro.tuc.ds2020.config;

import org.springframework.amqp.core.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    public static final String QUEUE_DEVICE = "deviceQueue";
    public static final String QUEUE_SIMULATOR = "simulatorQueue";
    public static final String EXCHANGE_NAME = "monitorExchange";
    public static final String ROUTING_KEY_DEVICE = "monitor.device";
    public static final String ROUTING_KEY_SIMULATOR = "monitor.simulator";

    public static final String TOPIC_EXCHANGE_NAME = "device_topic";

    @Bean
    public TopicExchange topicExchange() {
        return new TopicExchange(TOPIC_EXCHANGE_NAME);
    }
    // Define the Direct Exchange
    @Bean
    public DirectExchange exchange() {
        return new DirectExchange(EXCHANGE_NAME);
    }

    // Define Queue for Device
    @Bean
    public Queue deviceQueue() {
        return new Queue(QUEUE_DEVICE, true);
    }

    // Define Queue for Simulator
    @Bean
    public Queue simulatorQueue() {
        return new Queue(QUEUE_SIMULATOR, true);
    }

    // Binding for Device Queue
    @Bean
    public Binding bindingDeviceQueue(Queue deviceQueue, DirectExchange exchange) {
        return BindingBuilder.bind(deviceQueue).to(topicExchange()).with(ROUTING_KEY_DEVICE);
    }

    // Binding for Simulator Queue
    @Bean
    public Binding bindingSimulatorQueue(Queue simulatorQueue, DirectExchange exchange) {
        return BindingBuilder.bind(simulatorQueue).to(exchange).with(ROUTING_KEY_SIMULATOR);
    }
}
