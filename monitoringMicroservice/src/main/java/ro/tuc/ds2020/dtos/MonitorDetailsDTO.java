package ro.tuc.ds2020.dtos;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ro.tuc.ds2020.dtos.validators.annotation.AgeLimit;

import javax.validation.constraints.NotNull;
import java.util.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class MonitorDetailsDTO {

    private UUID deviceId;
    private UUID personId;
    @NotNull
    private int maxHourlyEnergyConsumption;
    private double lastIndex;
    private Map<Date, Double> values;

    public MonitorDetailsDTO(UUID deviceId, UUID perosnId, int maxHourlyEnergyConsumption) {
        this.deviceId = deviceId;
        this.personId = perosnId;
        this.maxHourlyEnergyConsumption = maxHourlyEnergyConsumption;
        this.values = new HashMap<>();
        this.lastIndex = -1.0;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MonitorDetailsDTO that = (MonitorDetailsDTO) o;
        return deviceId == that.deviceId &&
                maxHourlyEnergyConsumption == that.maxHourlyEnergyConsumption &&
                Objects.equals(values, that.values )&&
                lastIndex == that.lastIndex &&
                Objects.equals(personId, that.personId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(deviceId, personId, maxHourlyEnergyConsumption, lastIndex, values);
    }
}
