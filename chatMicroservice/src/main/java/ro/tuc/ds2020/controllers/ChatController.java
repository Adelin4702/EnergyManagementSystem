package ro.tuc.ds2020.controllers;


import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.Link;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ro.tuc.ds2020.controllers.handlers.exceptions.model.ResourceNotFoundException;
import ro.tuc.ds2020.dtos.ChatDTO;
import ro.tuc.ds2020.dtos.ChatDetailsDTO;
import ro.tuc.ds2020.services.ChatService;

import javax.validation.Valid;
import java.net.URISyntaxException;
import java.util.List;
import java.util.UUID;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@RestController
@RequestMapping(value = "/chat")
public class ChatController {

    private final ChatService chatService;

    @Autowired
    public ChatController(ChatService chatService) {
        this.chatService = chatService;
    }



    @GetMapping()
    public ResponseEntity<List<ChatDTO>> getChats() {
        List<ChatDTO> dtos = chatService.findChats();
        for (ChatDTO dto : dtos) {
            Link chatLink = linkTo(methodOn(ChatController.class)
                    .getChat(dto.getId())).withRel("chatDetails");
            dto.add(chatLink);
        }
        return new ResponseEntity<>(dtos, HttpStatus.OK);
    }

    @PostMapping()
    public ResponseEntity<ChatDetailsDTO> insertProsumer(@Valid @RequestBody ChatDetailsDTO chatDTO) throws URISyntaxException {
        UUID chatID = chatService.insertChat(chatDTO);
        if(chatID == null){
            return new ResponseEntity<>(HttpStatus.PRECONDITION_FAILED);
        }
        return new ResponseEntity<>(chatDTO, HttpStatus.CREATED);
    }

    @GetMapping(value = "/{id}")
    public ResponseEntity<ChatDetailsDTO> getChat(@PathVariable("id") UUID chatId) {
        ChatDetailsDTO dto = chatService.findChatById(chatId);
        return new ResponseEntity<>(dto, HttpStatus.OK);
    }

    @DeleteMapping(value = "/{id}")
    public ResponseEntity<UUID> deleteChat(@PathVariable("id") UUID chatId) {
        try {
            ChatDetailsDTO chat = chatService.findChatById(chatId);
            UUID id = chatService.deleteChat(chatId);
            return new ResponseEntity<>(id, HttpStatus.OK);

        } catch (ResourceNotFoundException e){
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @PutMapping(value = "/{id}")
    public ResponseEntity<ChatDetailsDTO> updateChat(@PathVariable("id") UUID chatId, @RequestBody ChatDetailsDTO chatDTO) {
        try {
            ChatDetailsDTO chat = chatService.findChatById(chatId);
        } catch (ResourceNotFoundException e){
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        chatService.updateChat(chatId, chatDTO);
        return new ResponseEntity<>(chatDTO, HttpStatus.OK);
    }
    //TODO: UPDATE, DELETE per resource

}
