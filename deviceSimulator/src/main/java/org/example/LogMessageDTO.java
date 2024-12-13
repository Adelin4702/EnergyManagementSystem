package org.example;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class LogMessageDTO {
    private long timestamp;
    private UUID device_id;
    private double measurement_value;

    public LogMessageDTO(long timestamp, UUID deviceId, String value) {
        this.timestamp = timestamp;
        this.device_id = deviceId;
        this.measurement_value = Double.parseDouble(value);
    }
}
