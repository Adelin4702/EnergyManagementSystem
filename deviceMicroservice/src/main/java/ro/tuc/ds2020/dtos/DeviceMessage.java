package ro.tuc.ds2020.dtos;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.UUID;

@Getter
@AllArgsConstructor
public class DeviceMessage {
    private String action;
    private UUID id;
    private UUID personId;
    private int maxHourlyEnergyConsumption;

    @Override
    public String toString() {
        return "DeviceMessage{" +
                "action='" + action + '\'' +
                ", id=" + id +
                ", personId=" + personId +
                ", maxHourlyEnergyConsumption=" + maxHourlyEnergyConsumption +
                '}';
    }
}
