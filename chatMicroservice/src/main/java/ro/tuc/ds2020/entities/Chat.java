package ro.tuc.ds2020.entities;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Type;

import javax.persistence.*;
import javax.persistence.Entity;
import java.io.Serializable;
import java.util.Date;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@Entity
public class Chat  implements Serializable{

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(generator = "uuid2")
    @GenericGenerator(name = "uuid2", strategy = "uuid2")
    @Type(type = "uuid-binary")
    private UUID id;

    @Column(name = "sender", nullable = false)
    private UUID sender;

    @Column(name = "receiver", nullable = false)
    private UUID receiver;

    @Column(name = "date", nullable = false)
    private Date date;

    @Column(name = "message", nullable = false)
    private String message;

    @Column(name = "type", nullable = false)
    private String type;


    public Chat(UUID sender, UUID receiver, Date date, String message, String type) {
        this.message = message;
        this.date = date;
        this.receiver = receiver;
        this.sender = sender;
        this.type = type;
    }
}
