package ro.tuc.ds2020.services;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import com.fasterxml.jackson.databind.ObjectMapper;
import ro.tuc.ds2020.config.RabbitMQConfig;
import ro.tuc.ds2020.dtos.DeviceMessage;
import ro.tuc.ds2020.dtos.LogMessageDTO;
import ro.tuc.ds2020.dtos.MonitorDetailsDTO;

import java.util.Date;

@Service
public class MessageReceiver {

    private final ObjectMapper objectMapper;
    private final MonitorService monitorService;
    private final SimpMessagingTemplate simpMessageTemplate;

    public MessageReceiver(ObjectMapper objectMapper, MonitorService monitorService, SimpMessagingTemplate simpMessageTemplate) {
        this.objectMapper = objectMapper;
        this.monitorService = monitorService;
        this.simpMessageTemplate = simpMessageTemplate;
    }

    @RabbitListener(queues = RabbitMQConfig.QUEUE_SIMULATOR)
    public void receiveMessageFromSimulator(String message) {
        try {
            // Deserialize JSON message to Monitor object
            LogMessageDTO logMessageDTO = objectMapper.readValue(message, LogMessageDTO.class);
            System.out.println("Received Monitor object: " + logMessageDTO.toString());
            monitorService.updateConsumption(logMessageDTO);


        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @RabbitListener(queues = RabbitMQConfig.QUEUE_DEVICE)
    public void receiveMessageFromDevice(String message) {
        try {
            // Deserialize JSON message to Monitor object

            System.out.println("Received Monitor object: " + message);
            DeviceMessage deviceMessage = objectMapper.readValue(message, DeviceMessage.class);
//            System.out.println("Received Monitor object: " + deviceMessage);

            MonitorDetailsDTO monitorDetailsDTO = new MonitorDetailsDTO(deviceMessage.getId(), deviceMessage.getPersonId(), deviceMessage.getMaxHourlyEnergyConsumption());

            if(deviceMessage.getAction().equals("insert")){
                monitorService.insert(monitorDetailsDTO);
            } else if (deviceMessage.getAction().equals("delete")) {
                monitorService.delete(monitorDetailsDTO);
            } else if (deviceMessage.getAction().equals("update")) {
                monitorService.updateMHEC(monitorDetailsDTO);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // New method to listen for alerts
    @RabbitListener(queues = "#{alertQueue.name}")  // Listen to the alertQueue
    public void receiveAlertMessage(String message) {
        try {
            // Assuming the message is a JSON representation of an alert
            System.out.println("Received Alert: " + message);
            // Here you can process the alert message as needed
            // For example, you can parse the message into an Alert object and take action
            String[] messages = message.split(",");
            simpMessageTemplate.convertAndSend("/topic/alert/" + messages[0], messages[1]);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
