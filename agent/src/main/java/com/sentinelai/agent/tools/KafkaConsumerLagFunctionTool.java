package com.sentinelai.agent.tools;

import java.io.IOException;
import com.google.adk.tools.Annotations.Schema;

/**
 * Function Tool wrapper for Kafka Consumer Lag investigation.
 * Connects to the existing Spring Boot backend's KafkaConsumerLagTool.
 */
public class KafkaConsumerLagFunctionTool {

    private final BackendToolClient client;

    public KafkaConsumerLagFunctionTool(BackendToolClient client) {
        this.client = client;
    }

    public String getName() {
        return "getKafkaConsumerLag";
    }

    public String getDescription() {
        return "Get Kafka consumer lag for a specific consumer group and topic";
    }

    /**
     * Execute the tool with parameters and return JSON response
     */
    public String getKafkaConsumerLag(
            @Schema(name = "consumerGroup", description = "Kafka consumer group name")
            String consumerGroup,
            @Schema(name = "topic", description = "Kafka topic name")
            String topic)  {
        try {
            var response = client.getKafkaConsumerLag(consumerGroup, topic);
            return formatResponse(response);
        } catch (IOException | InterruptedException e) {
            return formatError("Failed to get Kafka consumer lag", e);
        }
    }

    private String formatResponse(BackendToolClient.KafkaConsumerLagResponse response) {
        return String.format(
                "{\"consumerGroup\":\"%s\",\"topic\":\"%s\",\"totalLag\":%d}",
                response.consumerGroup(),
                response.topic(),
                response.totalLag()
        );
    }

    private String formatError(String message, Exception e) {
        return String.format("{\"error\":\"%s: %s\"}", message, e.getMessage());
    }
}
