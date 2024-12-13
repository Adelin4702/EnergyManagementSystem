package ro.tuc.ds2020.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ro.tuc.ds2020.entities.Device;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface DeviceRepository extends JpaRepository<Device, UUID> {

    /**
     * Example: JPA generate Query by Field
     */
    List<Device> findByDescription(String description);
    List<Device> findDevicesByPersonPersonId(UUID id);
    Optional<Device> findById(UUID deviceId);
    Optional<Device> findByPersonId(UUID personId);

    /**
     * Example: Write Custom Query
     */
//    @Query(value = "SELECT p " +
//            "FROM Device p " +
//            "WHERE p.description = :description " +
//            "AND p.maxHourlyEnergyConsumption >= 60  ")
//    Optional<Device> findSeniorsByName(@Param("description") String description);

}
