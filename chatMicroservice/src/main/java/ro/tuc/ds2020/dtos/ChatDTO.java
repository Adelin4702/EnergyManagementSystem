package ro.tuc.ds2020.dtos;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.hateoas.RepresentationModel;

import java.util.Date;
import java.util.Objects;
import java.util.UUID;

@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
public class ChatDTO extends RepresentationModel<ChatDTO> {
    private UUID id;
    private UUID sender;
    private UUID receiver;
    private Date date;
    private String message;
    private String type;

//    public ChatDTO() {
//    }

//    public ChatDTO(UUID id, String name, int age) {
//        this.id = id;
//        this.name = name;
//        this.age = age;
//    }

//    public UUID getId() {
//        return id;
//    }
//
//    public void setId(UUID id) {
//        this.id = id;
//    }
//
//    public String getName() {
//        return name;
//    }
//
//    public void setName(String name) {
//        this.name = name;
//    }
//
//    public int getAge() {
//        return age;
//    }
//
//    public void setAge(int age) {
//        this.age = age;
//    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ChatDTO chatDTO = (ChatDTO) o;
        return  Objects.equals(sender, chatDTO.sender) &&
                Objects.equals(receiver, chatDTO.receiver) &&
                Objects.equals(message, chatDTO.message) &&
                date == chatDTO.date &&
                Objects.equals(type, chatDTO.type);
    }

    @Override
    public int hashCode() {
        return Objects.hash(sender, receiver, date, message, type);
    }
}
