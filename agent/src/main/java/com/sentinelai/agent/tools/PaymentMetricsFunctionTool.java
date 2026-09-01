package com.sentinelai.agent.tools;

import java.io.IOException;

/**
 * Function Tool wrapper for Payment Metrics investigation.
 * Connects to the existing Spring Boot backend's PaymentMetricsTool.
 */
public class PaymentMetricsFunctionTool {

    private final BackendToolClient client;

    public PaymentMetricsFunctionTool(BackendToolClient client) {
        this.client = client;
    }

    public String getName() {
        return "getPaymentMetrics";
    }

    public String getDescription() {
        return "Get payment system metrics including throughput, success rate, error rate, and average latency";
    }

    /**
     * Execute the tool and return JSON response
     */
    public String getPaymentMetrics() {
        try {
            var response = client.getPaymentMetrics();
            return formatResponse(response);
        } catch (IOException | InterruptedException e) {
            return formatError("Failed to get payment metrics", e);
        }
    }

    private String formatResponse(BackendToolClient.PaymentMetricsResponse response) {
        return String.format(
                "{\"throughput\":%s,\"successRate\":%s,\"errorRate\":%s,\"avgLatencyMillis\":%s}",
                response.throughput(),
                response.successRate(),
                response.errorRate(),
                response.avgLatencyMillis()
        );
    }

    private String formatError(String message, Exception e) {
        return String.format("{\"error\":\"%s: %s\"}", message, e.getMessage());
    }
}
