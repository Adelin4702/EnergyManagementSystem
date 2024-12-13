package ro.tuc.ds2020.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ro.tuc.ds2020.config.RabbitMQConfig;
import ro.tuc.ds2020.controllers.handlers.exceptions.model.ResourceNotFoundException;
import ro.tuc.ds2020.dtos.DeviceDTO;
import ro.tuc.ds2020.dtos.DeviceDetailsDTO;
import ro.tuc.ds2020.dtos.DeviceMessage;
import ro.tuc.ds2020.dtos.builders.DeviceBuilder;
import ro.tuc.ds2020.entities.Device;
import ro.tuc.ds2020.entities.Person;
import ro.tuc.ds2020.repositories.DeviceRepository;
import ro.tuc.ds2020.repositories.PersonRepository;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class DeviceService {
    private static final Logger LOGGER = LoggerFactory.getLogger(DeviceService.class);
    private final DeviceRepository deviceRepository;
    private final PersonRepository personRepository;
    private final RabbitMQSender rabbitMQSender;
    @Autowired
    public DeviceService(DeviceRepository deviceRepository, PersonRepository personRepository, RabbitMQSender rabbitMQSender) {
        this.deviceRepository = deviceRepository;
        this.personRepository = personRepository;
        this.rabbitMQSender = rabbitMQSender;
    }

    public List<DeviceDetailsDTO> findDevices() {
        List<Device> deviceList = deviceRepository.findAll();
        return deviceList.stream()
                .map(DeviceBuilder::toDeviceDetailsDTO)
                .collect(Collectors.toList());
    }

    public DeviceDetailsDTO findDeviceById(UUID id) {
        Optional<Device> prosumerOptional = deviceRepository.findById(id);
        if (!prosumerOptional.isPresent()) {
            LOGGER.error("Device with id {} was not found in db", id);
            throw new ResourceNotFoundException(Device.class.getSimpleName() + " with id: " + id);
        }
        return DeviceBuilder.toDeviceDetailsDTO(prosumerOptional.get());
    }

    public DeviceDetailsDTO findDeviceByPersonId(UUID id) {
        Optional<Device> prosumerOptional = deviceRepository.findByPersonId(id);
        if (!prosumerOptional.isPresent()) {
            LOGGER.error("Device with person id {} was not found in db", id);
            throw new ResourceNotFoundException(Device.class.getSimpleName() + " with person id: " + id);
        }
        return DeviceBuilder.toDeviceDetailsDTO(prosumerOptional.get());
    }

    public List<DeviceDetailsDTO> findDevicesByPersonId(UUID id) {
        List<Device> devices = deviceRepository.findDevicesByPersonPersonId(id);
        if (devices == null) {
            LOGGER.error("Devices with person id {} were not found in db", id);
            throw new ResourceNotFoundException(Device.class.getSimpleName() + " with person id: " + id);
        }
        return devices.stream().map(DeviceBuilder::toDeviceDetailsDTO).collect(Collectors.toList());
    }


    public UUID insert(DeviceDetailsDTO deviceDTO) {
        Device device = DeviceBuilder.toEntity(deviceDTO);
        if(!deviceDTO.getPerson().isEmpty()){
            Person person = personRepository.findByPersonId(UUID.fromString(deviceDTO.getPerson()));
            device.setPerson(person);
        }
        device = deviceRepository.save(device);

        DeviceMessage deviceMessage = new DeviceMessage("insert", device.getId(), device.getPerson() != null ? device.getPerson().getPersonId() : null, device.getMaxHourlyEnergyConsumption());
        ObjectMapper objectMapper = new ObjectMapper();
        String jsonString;
        try {
            jsonString = objectMapper.writeValueAsString(deviceMessage);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
        rabbitMQSender.sendMessageToQueue(RabbitMQConfig.TOPIC_EXCHANGE_NAME, RabbitMQConfig.ROUTING_KEY_DEVICE, jsonString);

        LOGGER.debug("Device with id {} was inserted in db", device.getId());
        return device.getId();
    }

    public UUID delete(UUID deviceId) {
        Optional<Device> device = deviceRepository.findById(deviceId);
        if (!device.isPresent()) {
            LOGGER.error("Device with id {} was not found in db", deviceId);
            throw new ResourceNotFoundException(Device.class.getSimpleName() + " with id: " + deviceId);
        }
        deviceRepository.delete(device.get());


        DeviceMessage deviceMessage = new DeviceMessage("delete", deviceId, null,-1);
        ObjectMapper objectMapper = new ObjectMapper();
        String jsonString;
        try {
            jsonString = objectMapper.writeValueAsString(deviceMessage);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
        rabbitMQSender.sendMessageToQueue(RabbitMQConfig.TOPIC_EXCHANGE_NAME, RabbitMQConfig.ROUTING_KEY_DEVICE, jsonString);

        LOGGER.debug("Device with id {} was deleted from db", device.get().getId());
        return device.get().getId();
    }

    public UUID update(UUID deviceId, DeviceDetailsDTO deviceDetailsDTO) {
        Optional<Device> device = deviceRepository.findById(deviceId);
        if (!device.isPresent()) {
            LOGGER.error("Device with id {} was not found in db", deviceId);
            throw new ResourceNotFoundException(Device.class.getSimpleName() + " with id: " + deviceDetailsDTO.getId());
        }
        Device deviceToUpdate = DeviceBuilder.toEntity(deviceDetailsDTO);
        deviceToUpdate.setId(deviceId);
        Person person;
        if(!Objects.equals(deviceDetailsDTO.getPerson(), "")){
            person = personRepository.findByPersonId(UUID.fromString(deviceDetailsDTO.getPerson()));
        } else {
            person = null;
        }
        deviceToUpdate.setPerson(person);
        deviceRepository.save(deviceToUpdate);

        DeviceMessage deviceMessage = new DeviceMessage("update", deviceToUpdate.getId(), deviceToUpdate.getPerson() != null ? deviceToUpdate.getPerson().getPersonId() : null, deviceToUpdate.getMaxHourlyEnergyConsumption());
        ObjectMapper objectMapper = new ObjectMapper();
        String jsonString;
        try {
            jsonString = objectMapper.writeValueAsString(deviceMessage);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
        rabbitMQSender.sendMessageToQueue(RabbitMQConfig.TOPIC_EXCHANGE_NAME, RabbitMQConfig.ROUTING_KEY_DEVICE, jsonString);


        LOGGER.debug("Device with id {} was updated from db", device.get().getId());
        return device.get().getId();
    }

}
