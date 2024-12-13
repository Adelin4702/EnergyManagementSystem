package ro.tuc.ds2020.dtos;

import lombok.Getter;

import java.util.UUID;

@Getter
public class DeviceMessage {
    private String action;
    private UUID id;
    private UUID personId;
    private int maxHourlyEnergyConsumption;
}
