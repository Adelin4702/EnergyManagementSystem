package org.example;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.config.ConfigLoader;
import org.example.config.RabbitMQConfig;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.UUID;

public class Main {
    private static BufferedReader bufferedReader;
    private static RabbitMQSender rabbitMQSender;
    private static final int TIME_BETWEEN_MESSAGES = 30000;
    private static UUID device_id;

    public static void start(String[] args) {
        // Ensure args are being passed correctly
        if (args.length < 1) {
            System.out.println("No configFile path provided, using default path.");
            return; // You can decide to handle this better
        }

        // Initialize RabbitMQSender and load configuration
        rabbitMQSender = new RabbitMQSender(); // Initialize RabbitMQSender
        loadConfiguration(args[0]);  // Pass config file path
        readAndSend();
    }

    private static void loadConfiguration(String configFilePath) {
        if (configFilePath == null || configFilePath.isEmpty()) {
            throw new IllegalArgumentException("Config file path cannot be null or empty");
        }

        ConfigLoader config = new ConfigLoader(configFilePath);
        device_id = UUID.fromString(config.getProperty("device_id"));
        String filePath = config.getProperty("file_path");

        try {
            bufferedReader = new BufferedReader(new FileReader(filePath));
        } catch (FileNotFoundException e) {
            throw new RuntimeException("File not found: " + filePath, e);
        }
    }

    public static void readAndSend() {
        String line;
        String csvSplitBy = ",";  // Specify your delimiter here

        try {
            while ((line = bufferedReader.readLine()) != null) {
                String[] values = line.split(csvSplitBy);
                LogMessageDTO logMessage = new LogMessageDTO(System.currentTimeMillis() + 2*3600000, device_id, values[0]);

                ObjectMapper objectMapper = new ObjectMapper();
                String jsonString = objectMapper.writeValueAsString(logMessage);

                rabbitMQSender.sendMessageToQueue(RabbitMQConfig.QUEUE_SIMULATOR, jsonString);
                System.out.println("Message sent: " + jsonString);

                Thread.sleep(TIME_BETWEEN_MESSAGES);
            }
        } catch (IOException e) {
            System.err.println("Error reading file: " + e.getMessage());
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            System.err.println("Thread interrupted: " + e.getMessage());
        } finally {
            try {
                if (bufferedReader != null) {
                    bufferedReader.close();
                }
            } catch (IOException e) {
                System.err.println("Error closing BufferedReader: " + e.getMessage());
            }
        }
    }

    public static void main(String[] args) {
        System.out.println("Starting application...");
        start(args);
    }
}
