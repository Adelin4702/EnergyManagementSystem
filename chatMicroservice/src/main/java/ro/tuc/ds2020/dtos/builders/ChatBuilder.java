package ro.tuc.ds2020.dtos.builders;

import lombok.NoArgsConstructor;
import ro.tuc.ds2020.dtos.ChatDTO;
import ro.tuc.ds2020.dtos.ChatDetailsDTO;
import ro.tuc.ds2020.entities.Chat;

import java.util.UUID;

@NoArgsConstructor
public class ChatBuilder {

    public static ChatDTO toChatDTO(Chat chat) {
        return new ChatDTO(chat.getId(), chat.getSender(), chat.getReceiver(), chat.getDate(), chat.getMessage(), chat.getType());
    }

    public static ChatDetailsDTO toChatDetailsDTO(Chat chat) {
        return new ChatDetailsDTO(chat.getId(), chat.getSender().toString(), chat.getReceiver().toString(), chat.getDate(), chat.getMessage(), chat.getType());
    }

    public static Chat toEntity(ChatDetailsDTO chatDetailsDTO) {
        return new Chat(UUID.fromString(chatDetailsDTO.getSender()), UUID.fromString(chatDetailsDTO.getReceiver()), chatDetailsDTO.getDate(), chatDetailsDTO.getMessage(), chatDetailsDTO.getType());
    }
}
