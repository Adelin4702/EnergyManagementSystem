package ro.tuc.ds2020.dtos;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.hateoas.RepresentationModel;

import java.util.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class MonitorDTO extends RepresentationModel<MonitorDTO> {
    private UUID deviceId;
    private UUID personId;
    private double maxHourlyEnergyConsumption;
    private double lastIndex;
    private Map<Date, Double> values;

    public MonitorDTO(UUID deviceId, UUID personId, int maxHourlyEnergyConsumption) {
        this.deviceId = deviceId;
        this.personId = personId;
        this.maxHourlyEnergyConsumption = maxHourlyEnergyConsumption;
        this.values = new HashMap<>();
        this.lastIndex = -1.0;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MonitorDTO monitorDTO = (MonitorDTO) o;
        return deviceId == monitorDTO.deviceId &&
                maxHourlyEnergyConsumption == monitorDTO.maxHourlyEnergyConsumption &&
                Objects.equals(values, monitorDTO.values) &&
                Objects.equals(lastIndex, monitorDTO.lastIndex) &&
                Objects.equals(personId, monitorDTO.personId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(deviceId, personId, maxHourlyEnergyConsumption, lastIndex, values);
    }
}
