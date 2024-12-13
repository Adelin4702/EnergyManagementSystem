package ro.tuc.ds2020.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;
import ro.tuc.ds2020.controllers.handlers.exceptions.model.ResourceNotFoundException;
import ro.tuc.ds2020.dtos.ChatDTO;
import ro.tuc.ds2020.dtos.ChatDetailsDTO;
import ro.tuc.ds2020.dtos.builders.ChatBuilder;
import ro.tuc.ds2020.entities.Chat;
import ro.tuc.ds2020.repositories.ChatRepository;

import java.net.URISyntaxException;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class ChatService {
    private static final Logger LOGGER = LoggerFactory.getLogger(ChatService.class);
    private final ChatRepository chatRepository;
    SimpMessagingTemplate simpMessagingTemplate;

    @Autowired
    public ChatService(ChatRepository chatRepository, SimpMessagingTemplate simpMessagingTemplate) {
        this.chatRepository = chatRepository;
        this.simpMessagingTemplate = simpMessagingTemplate;
    }

    @Autowired
    RestTemplate restTemplate;

    public List<ChatDTO> findChats() {
        List<Chat> chatList = chatRepository.findAll();
        return chatList.stream()
                .map(ChatBuilder::toChatDTO)
                .collect(Collectors.toList());
    }

    public ChatDetailsDTO findChatById(UUID id) {
        Optional<Chat> prosumerOptional = chatRepository.findById(id);
        if (!prosumerOptional.isPresent()) {
            LOGGER.error("Chat with id {} was not found in db", id);
            throw new ResourceNotFoundException(Chat.class.getSimpleName() + " with id: " + id);
        }
        return ChatBuilder.toChatDetailsDTO(prosumerOptional.get());
    }

//    public ChatDetailsDTO findChatByEmail(String email) {
//        Chat prosumerOptional = chatRepository.findByEmail(email);
//        if (prosumerOptional == null) {
//            LOGGER.error("Chat with email {} was not found in db", email);
//            throw new ResourceNotFoundException(Chat.class.getSimpleName() + " with email: " + email);
//        }
//        return ChatBuilder.toChatDetailsDTO(prosumerOptional);
//    }



    @Transactional
    public UUID insertChat(ChatDetailsDTO chatDTO) throws URISyntaxException {
//        if (chatRepository.findByEmail(chatDTO.getEmail()) != null) {
//            return null;
//        }
        // Encode the password
      //  chatDTO.setPassword(new BCryptPasswordEncoder().encode(chatDTO.getPassword()));
        Chat chat = ChatBuilder.toEntity(chatDTO);
        chat = chatRepository.save(chat);

//        restTemplate.postForEntity("http://device-backend-container:8081/chat/" + chat.getId().toString(),
        restTemplate.postForEntity("http://backend-device:8081/api-device/chat/" + chat.getId().toString(),
                null, Void.class);

        LOGGER.debug("Chat with id {} was inserted in db", chat.getId());
        System.out.println("http://localhost:80/api-device/chat/" + chat.getId().toString());
        return chat.getId();
    }

    public UUID updateChat(UUID id, ChatDetailsDTO chatDTO){
        Chat chat = ChatBuilder.toEntity(chatDTO);
        chat.setId(id);
        //chat.setPassword(new BCryptPasswordEncoder().encode(chatDTO.getPassword()));
        chat = chatRepository.save(chat);
        LOGGER.debug("Chat with id {} was updated in db", id);
        return chat.getId();
    }

    @Transactional
    public UUID deleteChat(UUID id) {
        chatRepository.deleteById(id);
//        restTemplate.delete("http://device-backend-container:8081/chat/" + id);
        restTemplate.delete("http://backend-device:8081/api-device/chat/" + id);
        LOGGER.debug("Chat with id {} was deleted from db", id);
        return id;
    }

    public void sendMessage(ChatDetailsDTO chatDetailsDTO) {
        if(chatDetailsDTO == null){
            System.out.println("NULL MESSAGE");
            return;
        }

        if (chatDetailsDTO.getSender().equals(chatDetailsDTO.getReceiver())) {
            LOGGER.debug("Chat with id {} was sent to sender", chatDetailsDTO.getId());
        }

        Chat chat = ChatBuilder.toEntity(chatDetailsDTO);
        chat = chatRepository.save(chat);
        try {
            simpMessagingTemplate.convertAndSend("/topic/chat/" + chat.getReceiver().toString(), chat);
            System.out.println("\nMESSAGE SENT TO " + chat.getReceiver() + "\n");
        }catch (Exception e){
            e.printStackTrace();
        }
    }

}
