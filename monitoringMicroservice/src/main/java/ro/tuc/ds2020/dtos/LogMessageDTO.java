package ro.tuc.ds2020.dtos;

import lombok.*;

import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
public class LogMessageDTO {
    private long timestamp;
    private UUID device_id;
    private double measurement_value;
}
