package com.sentinelai.agent.tools;

import java.io.IOException;

/**
 * Function Tool wrapper for Dependency Health investigation.
 * Connects to the existing Spring Boot backend's DependencyHealthTool.
 */
public class DependencyHealthFunctionTool {

    private final BackendToolClient client;

    public DependencyHealthFunctionTool(BackendToolClient client) {
        this.client = client;
    }

    public String getName() {
        return "getDependencyHealth";
    }

    public String getDescription() {
        return "Get the health status of external dependencies including Kafka and database";
    }

    /**
     * Execute the tool and return JSON response
     */
    public String getDependencyHealth() {
        try {
            var response = client.getDependencyHealth();
            return formatResponse(response);
        } catch (IOException | InterruptedException e) {
            return formatError("Failed to get dependency health", e);
        }
    }

    private String formatResponse(BackendToolClient.DependencyHealthResponse response) {
        return String.format(
                "{\"kafka\":\"%s\",\"database\":\"%s\"}",
                response.kafka(),
                response.database()
        );
    }

    private String formatError(String message, Exception e) {
        return String.format("{\"error\":\"%s: %s\"}", message, e.getMessage());
    }
}
