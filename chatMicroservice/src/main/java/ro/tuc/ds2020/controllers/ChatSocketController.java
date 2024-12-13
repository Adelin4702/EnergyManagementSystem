package ro.tuc.ds2020.controllers;

import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import ro.tuc.ds2020.dtos.ChatDetailsDTO;
import ro.tuc.ds2020.services.ChatService;

@Controller
@CrossOrigin("http://localhost")
public class ChatSocketController {
    private final SimpMessagingTemplate simpMessagingTemplate;
    private final ChatService chatService;

    public ChatSocketController(SimpMessagingTemplate simpMessagingTemplate, ChatService chatService) {
        this.simpMessagingTemplate = simpMessagingTemplate;
        this.chatService = chatService;
    }

    @MessageMapping("/sendMessage/{personId}")
    public void sendMessage(@DestinationVariable String personId, @Payload ChatDetailsDTO chatDetailsDTO) {
        chatService.sendMessage(chatDetailsDTO);
        System.out.println("Message sent to chat: " + chatDetailsDTO.toString());
    }

    @MessageMapping("/sendTypingNotification/{personId}")
    public void sendTypingNotification(@DestinationVariable String personId, @Payload ChatDetailsDTO chatDetailsDTO) {
        simpMessagingTemplate.convertAndSend("/topic/typing/" + chatDetailsDTO.getReceiver(), chatDetailsDTO);
        System.out.println("Message sent to typing: " + chatDetailsDTO.toString());
    }

    @MessageMapping("/sendSeenNotification/{personId}")
    public void sendSeenNotification(@DestinationVariable String personId, @Payload ChatDetailsDTO chatDetailsDTO) {
        simpMessagingTemplate.convertAndSend("/topic/seen/" + chatDetailsDTO.getReceiver(), chatDetailsDTO);
        System.out.println("Message sent to seen: " + chatDetailsDTO.toString());
    }
}
