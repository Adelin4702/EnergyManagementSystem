package ro.tuc.ds2020.entities;

import lombok.*;

import java.util.Date;
import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
@ToString
public class HourlyConsumptionRequest {
    private UUID deviceId;  // Device ID sent from frontend
    private Date date;        // Date sent from frontend
}
