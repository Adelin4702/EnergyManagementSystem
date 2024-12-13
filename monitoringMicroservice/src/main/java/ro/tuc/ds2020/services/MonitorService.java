package ro.tuc.ds2020.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.FanoutExchange;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import ro.tuc.ds2020.config.RabbitMQConfig;
import ro.tuc.ds2020.controllers.MonitorController;
import ro.tuc.ds2020.controllers.handlers.exceptions.model.ResourceNotFoundException;
import ro.tuc.ds2020.dtos.LogMessageDTO;
import ro.tuc.ds2020.dtos.MonitorDetailsDTO;
import ro.tuc.ds2020.dtos.builders.LogMessageBuilder;
import ro.tuc.ds2020.dtos.builders.MonitorBuilder;
import ro.tuc.ds2020.entities.LogMessage;
import ro.tuc.ds2020.entities.Monitor;
import ro.tuc.ds2020.repositories.MonitorRepository;

import java.text.SimpleDateFormat;
import java.util.*;
import java.time.Month;

@Service
public class MonitorService {
    private static final Logger LOGGER = LoggerFactory.getLogger(MonitorService.class);
    private final MonitorRepository monitorRepository;
    private final SimpMessagingTemplate simpMessageTemplate;
    private final RabbitTemplate rabbitTemplate;
    @Autowired
    private final FanoutExchange fanoutExchange;

    @Autowired
    public MonitorService(MonitorRepository monitorRepository, SimpMessagingTemplate simpMessageTemplate, RabbitTemplate rabbitTemplate, FanoutExchange fanoutExchange, FanoutExchange fanoutExchange1) {
        this.monitorRepository = monitorRepository;
        this.simpMessageTemplate = simpMessageTemplate;
        this.rabbitTemplate = rabbitTemplate;
        this.fanoutExchange = fanoutExchange1;
    }

    public MonitorDetailsDTO findMonitorByDeviceId(UUID id) {
        Monitor monitor = monitorRepository.findByDeviceId(id);
        if (monitor == null) {
            LOGGER.error("Monitor with id {} was not found in db", id);
            throw new ResourceNotFoundException(Monitor.class.getSimpleName() + " with device_id: " + id);
        }
        return MonitorBuilder.toMonitorDetailsDTO(monitor);
    }

    public ArrayList<Map.Entry<Date, Double>> findMonitorHistory(UUID id, Date date) {
        Monitor monitor = monitorRepository.findByDeviceId(id);
        if (monitor == null) {
            LOGGER.error("Monitor with id {} was not found in db", id);
            throw new ResourceNotFoundException(Monitor.class.getSimpleName() + " with device_id: " + id);
        }
        Map<Date, Double> history = monitor.getValues();
        Map<Date, Double> sortedHistory = new TreeMap<>(monitor.getValues());

        ArrayList<Map.Entry<Date, Double>> hourlyConsumption = new ArrayList<>();
        for(Map.Entry<Date, Double> entry : sortedHistory.entrySet()){
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");

            if(format.format(entry.getKey()).equals(format.format(date))){

                hourlyConsumption.add(entry);

            }
        }

        return hourlyConsumption;
    }

    public UUID insert(MonitorDetailsDTO personDTO) {
        Monitor monitor = MonitorBuilder.toEntity(personDTO);
        monitor = monitorRepository.save(monitor);
        LOGGER.debug("Monitor with id {} was inserted in db", monitor.getDeviceId());
        return monitor.getDeviceId();
    }

    public UUID delete(MonitorDetailsDTO monitorDetailsDTO) {
        Monitor monitor = monitorRepository.findByDeviceId(monitorDetailsDTO.getDeviceId());
        monitorRepository.delete(monitor);
        LOGGER.debug("Monitor with id {} was deleted from db", monitor.getDeviceId());
        return monitor.getDeviceId();
    }

    public Monitor updateConsumption(LogMessageDTO logMessageDTO) {
        //Monitor monitor = MonitorBuilder.toEntity(personDTO);
        logMessageDTO.setTimestamp(logMessageDTO.getTimestamp() / 3600000 * 3600000);
        LogMessage message = LogMessageBuilder.toEntity(logMessageDTO);
//        message.getDate().setMinutes(0);
//        message.getDate().setSeconds(0);
        Monitor monitor = monitorRepository.findByDeviceId(message.getDeviceId());
        Double value = monitor.getValues().get(message.getDate());
        Double addedValue = message.getNewIndex() - monitor.getLastIndex();
        if(monitor.getLastIndex() == -1.0){
            addedValue = 0.0;
        }
        if(value == null){
            value = 0.0;
        }
        monitor.getValues().put(message.getDate(), value + addedValue);
        monitor.setLastIndex(message.getNewIndex());

        if(value + addedValue > monitor.getMaxHourlyEnergyConsumption()){
            LOGGER.error("Consumption exceeded max energy consumption for device: {} at {}", monitor.getDeviceId(), logMessageDTO.getTimestamp());
//            Map<String, Object> map = new HashMap<>();
//            map.put("personId", monitor.getPersonId());
//            monitorController.sendAlert("Consumption exceeded max energy consumption for device: "+monitor.getDeviceId()+" at " + new Date(logMessageDTO.getTimestamp()), map);
            String notification = monitor.getPersonId() +  ",Consumption exceeded max energy consumption for device: "+monitor.getDeviceId()+" at " + new Date(logMessageDTO.getTimestamp());
            System.out.println("-----------------FANOUT: " + fanoutExchange.getName());
            rabbitTemplate.convertAndSend(fanoutExchange.getName(), "", notification);

//            simpMessageTemplate.convertAndSend("/topic/notification", monitor.getPersonId() +  "Consumption exceeded max energy consumption for device: "+monitor.getDeviceId()+" at " + new Date(logMessageDTO.getTimestamp()));
        }

        monitor = monitorRepository.save(monitor);
//        monitor = monitorRepository.save(monitor);
        LOGGER.debug("Monitor with id {} was updated in db at ", monitor.getDeviceId());
        return monitor;
    }

    public Monitor updateMHEC(MonitorDetailsDTO monitorDetailsDTO) {
        //Monitor monitor = MonitorBuilder.toEntity(personDTO);

        Monitor monitor = monitorRepository.findByDeviceId(monitorDetailsDTO.getDeviceId());
        monitor.setMaxHourlyEnergyConsumption(monitorDetailsDTO.getMaxHourlyEnergyConsumption());
        monitor.setPersonId(monitorDetailsDTO.getPersonId());
        monitor = monitorRepository.save(monitor);
//        monitor = monitorRepository.save(monitor);
        LOGGER.debug("Monitor with id {} was updated in db", monitor.getDeviceId());
        return monitor;
    }

}
