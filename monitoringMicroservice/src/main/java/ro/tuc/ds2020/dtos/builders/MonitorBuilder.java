package ro.tuc.ds2020.dtos.builders;

import ro.tuc.ds2020.dtos.MonitorDTO;
import ro.tuc.ds2020.dtos.MonitorDetailsDTO;
import ro.tuc.ds2020.entities.Monitor;

public class MonitorBuilder {

    private MonitorBuilder() {
    }

    public static MonitorDTO toMonitorDTO(Monitor monitor) {
        return new MonitorDTO(monitor.getDeviceId(), monitor.getPersonId(), monitor.getMaxHourlyEnergyConsumption(), monitor.getLastIndex(), monitor.getValues());
    }

    public static MonitorDetailsDTO toMonitorDetailsDTO(Monitor monitor) {
        return new MonitorDetailsDTO(monitor.getDeviceId(), monitor.getPersonId(), monitor.getMaxHourlyEnergyConsumption(), monitor.getLastIndex(), monitor.getValues());
    }

    public static Monitor toEntity(MonitorDetailsDTO monitorDetailsDTO) {
        return new Monitor(monitorDetailsDTO.getDeviceId(),
                monitorDetailsDTO.getPersonId(),
                monitorDetailsDTO.getMaxHourlyEnergyConsumption(),
                monitorDetailsDTO.getLastIndex(),
                monitorDetailsDTO.getValues());
    }
}
