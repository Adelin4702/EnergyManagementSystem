package ro.tuc.ds2020.entities;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.Type;

import javax.persistence.*;
import javax.persistence.Entity;
import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
public class Monitor implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @Type(type = "uuid-binary")
    private UUID deviceId;

    @Column(name = "personId")
    @Type(type = "uuid-binary")
    private UUID personId;

    @Column(name = "maxHourlyEnergyConsumption", nullable = false)
    private int maxHourlyEnergyConsumption;

    @Column(name = "lastIndex")
    private double lastIndex;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "monitor_values", joinColumns = @JoinColumn(name = "device_id"))
    @MapKeyColumn(name = "date")
    @Column(name = "value")
    private Map<Date, Double> values = new HashMap<>();

    public Monitor(UUID id, UUID personId, int maxHourlyEnergyConsumption) {
        this.deviceId = id;
        this.personId = personId;
        this.maxHourlyEnergyConsumption = maxHourlyEnergyConsumption;
        this.values = new HashMap<Date, Double>();
        this.lastIndex = -1.0;
    }
}
