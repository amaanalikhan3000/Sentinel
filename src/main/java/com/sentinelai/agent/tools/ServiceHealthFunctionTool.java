package com.sentinelai.agent.tools;

import java.io.IOException;

/**
 * Function Tool wrapper for Service Health investigation.
 * Connects to the existing Spring Boot backend's ServiceHealthTool.
 */
public class ServiceHealthFunctionTool {

    private final BackendToolClient client;

    public ServiceHealthFunctionTool(BackendToolClient client) {
        this.client = client;
    }

    public String getName() {
        return "getServiceHealth";
    }

    public String getDescription() {
        return "Get the current health status of the payment service, Kafka, and database";
    }

    /**
     * Execute the tool and return JSON response
     */
    public String execute() {
        try {
            var response = client.getServiceHealth();
            return formatResponse(response);
        } catch (IOException | InterruptedException e) {
            return formatError("Failed to get service health", e);
        }
    }

    private String formatResponse(BackendToolClient.ServiceHealthResponse response) {
        return String.format(
                "{\"paymentService\":\"%s\",\"kafka\":\"%s\",\"database\":\"%s\"}",
                response.paymentService(),
                response.kafka(),
                response.database()
        );
    }

    private String formatError(String message, Exception e) {
        return String.format("{\"error\":\"%s: %s\"}", message, e.getMessage());
    }
}
