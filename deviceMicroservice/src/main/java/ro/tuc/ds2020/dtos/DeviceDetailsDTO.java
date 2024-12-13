package ro.tuc.ds2020.dtos;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.RepresentationModel;

import javax.validation.constraints.NotNull;
import java.util.Objects;
import java.util.UUID;

@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
public class DeviceDetailsDTO  extends RepresentationModel<DeviceDetailsDTO> {

    private UUID id;
    @NotNull
    private String description;
    @NotNull
    private String address;
    private int maxHourlyEnergyConsumption;
    private String person;

    public DeviceDetailsDTO(String description, String address, int maxHourlyEnergyConsumption, String person) {
        this.description = description;
        this.address = address;
        this.maxHourlyEnergyConsumption = maxHourlyEnergyConsumption;
        this.person = person;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DeviceDetailsDTO that = (DeviceDetailsDTO) o;
        return maxHourlyEnergyConsumption == that.maxHourlyEnergyConsumption &&
                Objects.equals(description, that.description) &&
                Objects.equals(address, that.address) &&
                Objects.equals(person, that.person);
    }

    @Override
    public int hashCode() {
        return Objects.hash(description, address, maxHourlyEnergyConsumption, person);
    }


}
