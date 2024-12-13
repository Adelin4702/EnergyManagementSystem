package ro.tuc.ds2020.entities;

import lombok.*;

import java.util.Date;
import java.util.UUID;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor

public class LogMessage {
    private Date date;
    private UUID deviceId;
    private double newIndex;

    @Override
    public String toString() {
        return "LogMessage{" +
                "date=" + date +
                ", deviceId=" + deviceId +
                ", value=" + newIndex +
                '}';
    }
}
