package ro.tuc.ds2020.dtos;

import lombok.*;

import javax.validation.constraints.NotNull;
import java.util.Date;
import java.util.Objects;
import java.util.UUID;

@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
@ToString
public class ChatDetailsDTO {

    private UUID id;
    @NotNull
    private String sender;
    @NotNull
    private String receiver;
    @NotNull
    private Date date;
    @NotNull
    private String message;
    @NotNull
    private String type;

//    public ChatDetailsDTO() {
//    }

    public ChatDetailsDTO(String sender, String receiver, Date date, String message, String type) {
        this.sender = sender;
        this.receiver = receiver;
        this.date = date;
        this.message = message;
        this.type = type;
    }


//    public ChatDetailsDTO(UUID id, String name, String address, int age) {
//        this.id = id;
//        this.name = name;
//        this.address = address;
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
//    public String getAddress() {
//        return address;
//    }
//
//    public void setAddress(String address) {
//        this.address = address;
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
        ChatDetailsDTO that = (ChatDetailsDTO) o;
        return Objects.equals(sender, that.sender) &&
                    Objects.equals(receiver, that.receiver) &&
                Objects.equals(message, that.message) &&
                date == that.date &&
                Objects.equals(type, that.type);
    }

    @Override
    public int hashCode() {
        return Objects.hash(sender, receiver, date, message, type);
    }
}
