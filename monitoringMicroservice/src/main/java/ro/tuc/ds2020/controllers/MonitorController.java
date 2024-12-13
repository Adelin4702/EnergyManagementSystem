package ro.tuc.ds2020.controllers;


import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import ro.tuc.ds2020.services.MonitorService;
import org.json.JSONObject;

import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;


@Controller
@CrossOrigin("http://localhost")
public class MonitorController {
//    private final MonitorService monitorService;
    private final SimpMessagingTemplate simpMessageTemplate;
    private final MonitorService monitorService;

    @Autowired
    public MonitorController(SimpMessagingTemplate simpMessageTemplate, MonitorService monitorService) {
//        this.monitorService = monitorService;
        this.simpMessageTemplate = simpMessageTemplate;
        this.monitorService = monitorService;
    }

//    @MessageMapping("/sendConsumption")  // Map the incoming message to "/app/sendConsumption"
//    @SendTo("/topic/consumption")  // Broadcast the message to all clients subscribing to "/topic/consumption"
//    public ArrayList<Map.Entry<Date, Double>> sendEnergyData(HourlyConsumptionRequest request) {
//        // Handle the request
//        UUID deviceId = request.getDeviceId();
//        Date date = request.getDate();
//
//        // Simulate getting the data for the device and date from a database
//        ArrayList<Map.Entry<Date, Double>> hourlyConsumption = monitorService.findMonitorHistory(deviceId, date);
//
//        // Return the data to be sent back to the frontend
//        return hourlyConsumption;
//    }
//@MessageMapping("/sendConsumption")  // Map the incoming message to "/app/sendConsumption"
//@SendTo("/topic/consumption")  // Broadcast the message to all clients subscribing to "/topic/consumption"
//public void sendAlert(@Payload String message) {
//    System.out.println("----------Message sent: " + message );
//    simpMessageTemplate.convertAndSend("/topic/consumption", message);
//
//}
    @MessageMapping("/sendAlert")
    public void sendAlert(@Payload String message, @Header("simpSessionAttributes") Map<String, Object> sessionAttributes) {
        String personId = sessionAttributes.get("personId").toString(); // or extract personId from the message payload if available

        System.out.println("----------Sending message to person ID " + personId + ": " + message);

        // Send to the unique topic for this person
        simpMessageTemplate.convertAndSend("/topic/alert/" + personId, message);
    }

    // New WebSocket handler for device and date selection
    @MessageMapping("/sendConsumption")
    public void handleDeviceAndDateSelection(@Payload String message, @Header("simpSessionAttributes") Map<String, Object> sessionAttributes) {
        // Assuming 'message' contains the deviceId and date in a suitable format (e.g., JSON)
        System.out.println("Received device and date selection: " + message);

        // Parse the JSON message
        JSONObject jsonMessage = new JSONObject(message);

        // Extract the values
        String deviceId = jsonMessage.getString("deviceId");
        String dateS = jsonMessage.getString("date");
        String personId = jsonMessage.getString("personId");
        DateTimeFormatter formatter = DateTimeFormatter.ISO_DATE_TIME;
        ZonedDateTime zonedDateTime = ZonedDateTime.parse(dateS, formatter);
        System.out.println("\n****** date: " + Date.from(zonedDateTime.toInstant()) + "\na");
        if(deviceId != null){
        ObjectMapper objectMapper = new ObjectMapper();
        ArrayList<Map.Entry<Date, Double>> map = monitorService.findMonitorHistory(UUID.fromString(deviceId), Date.from(zonedDateTime.toInstant()));
        try {
            message = objectMapper.writeValueAsString(map);
        } catch (Exception e) {
            e.printStackTrace();
            message = "Error converting history data to JSON";
        }

        // Extract the personId from session attributes (or you could extract it from the payload as well)
//        String personId = sessionAttributes.get("personId").toString();

        // Here we can send the selected device and date to a specific topic, based on the personId
        simpMessageTemplate.convertAndSend("/topic/sendConsumption/" + personId, message);
        }
    }
}
