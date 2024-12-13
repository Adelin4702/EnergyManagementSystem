package ro.tuc.ds2020.dtos.builders;

import ro.tuc.ds2020.dtos.LogMessageDTO;
import ro.tuc.ds2020.entities.LogMessage;

import java.util.Date;

public class LogMessageBuilder {
    public static LogMessage toEntity(LogMessageDTO logMessageDTO) {
        return new LogMessage(
                new Date(logMessageDTO.getTimestamp()),
                logMessageDTO.getDevice_id(),
                logMessageDTO.getMeasurement_value());
    }

    public static LogMessageDTO toDTO(LogMessage logMessage) {
        return new LogMessageDTO(
                logMessage.getDate().getTime(),
                logMessage.getDeviceId(),
                logMessage.getNewIndex());
    }
}
