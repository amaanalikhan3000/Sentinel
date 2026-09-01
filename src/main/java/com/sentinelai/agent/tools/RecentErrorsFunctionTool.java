package com.sentinelai.agent.tools;

import java.io.IOException;
import com.google.adk.tools.Annotations.Schema;

/**
 * Function Tool wrapper for Recent Errors investigation.
 * Connects to the existing Spring Boot backend's RecentErrorsTool.
 */
public class RecentErrorsFunctionTool {

    private final BackendToolClient client;

    public RecentErrorsFunctionTool(BackendToolClient client) {
        this.client = client;
    }

    public String getName() {
        return "getRecentErrors";
    }

    public String getDescription() {
        return "Get recent errors for a specific service within a time window";
    }

    /**
     * Execute the tool with parameters and return JSON response
     */
    public String execute(
            @Schema(name = "service", description = "Service name")
            String service,
            @Schema(name = "timeWindowSeconds", description = "Time window in seconds")
            long timeWindowSeconds)  {
        try {
            var response = client.getRecentErrors(service, timeWindowSeconds);
            return formatResponse(response);
        } catch (IOException | InterruptedException e) {
            return formatError("Failed to get recent errors", e);
        }
    }

    private String formatResponse(BackendToolClient.RecentErrorsResponse response) {
        StringBuilder json = new StringBuilder();
        json.append("{\"service\":\"").append(response.service()).append("\",\"errors\":[");

        boolean first = true;
        for (var error : response.errors()) {
            if (!first) json.append(",");
            json.append("{\"message\":\"").append(escapeJson(error.message())).append("\"")
               .append(",\"timestamp\":\"").append(error.timestamp()).append("\"}");
            first = false;
        }

        json.append("]}");
        return json.toString();
    }

    private String escapeJson(String str) {
        return str.replace("\\", "\\\\")
                  .replace("\"", "\\\"")
                  .replace("\n", "\\n")
                  .replace("\r", "\\r")
                  .replace("\t", "\\t");
    }

    private String formatError(String message, Exception e) {
        return String.format("{\"error\":\"%s: %s\"}", message, e.getMessage());
    }
}
