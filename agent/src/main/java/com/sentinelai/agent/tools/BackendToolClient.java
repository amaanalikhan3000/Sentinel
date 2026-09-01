package com.sentinelai.agent.tools;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import java.io.IOException;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;

/**
 * HTTP client for connecting to the existing Spring Boot backend investigation tools.
 * Uses the existing REST API endpoints without modifying backend architecture.
 */
public class BackendToolClient {

    private final HttpClient httpClient;
    private final ObjectMapper objectMapper;
    private final String backendBaseUrl;

    public BackendToolClient(String backendBaseUrl) {
        this.backendBaseUrl = backendBaseUrl;
        this.httpClient = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(10))
                .build();
        this.objectMapper = new ObjectMapper();
        this.objectMapper.registerModule(new JavaTimeModule());
    }

    /**
     * Tool 1: Get service health status
     */
    public ServiceHealthResponse getServiceHealth() throws IOException, InterruptedException {
        String url = backendBaseUrl + "/api/tools/health";
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .timeout(Duration.ofSeconds(10))
                .GET()
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() != 200) {
            throw new IOException("Backend returned status " + response.statusCode() + ": " + response.body());
        }

        return objectMapper.readValue(response.body(), ServiceHealthResponse.class);
    }

    /**
     * Tool 2: Get payment metrics for a time window
     * Note: The backend endpoint uses a fixed 300-second window; parameters are for future extension
     */
    public PaymentMetricsResponse getPaymentMetrics() throws IOException, InterruptedException {
        String url = backendBaseUrl + "/api/tools/metrics";
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .timeout(Duration.ofSeconds(10))
                .GET()
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() != 200) {
            throw new IOException("Backend returned status " + response.statusCode() + ": " + response.body());
        }

        return objectMapper.readValue(response.body(), PaymentMetricsResponse.class);
    }

    /**
     * Tool 3: Get Kafka consumer lag
     */
    public KafkaConsumerLagResponse getKafkaConsumerLag(String consumerGroup, String topic)
            throws IOException, InterruptedException {
        String url = backendBaseUrl + "/api/tools/kafka-lag"
                + "?consumerGroup=" + URLEncoder.encode(consumerGroup, StandardCharsets.UTF_8)
                + "&topic=" + URLEncoder.encode(topic, StandardCharsets.UTF_8);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .timeout(Duration.ofSeconds(10))
                .GET()
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() != 200) {
            throw new IOException("Backend returned status " + response.statusCode() + ": " + response.body());
        }

        return objectMapper.readValue(response.body(), KafkaConsumerLagResponse.class);
    }

    /**
     * Tool 4: Get recent errors for a service
     */
    public RecentErrorsResponse getRecentErrors(String service, long timeWindowSeconds)
            throws IOException, InterruptedException {
        String url = backendBaseUrl + "/api/tools/recent-errors"
                + "?service=" + URLEncoder.encode(service, StandardCharsets.UTF_8)
                + "&timeWindowSeconds=" + timeWindowSeconds;

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .timeout(Duration.ofSeconds(10))
                .GET()
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() != 200) {
            throw new IOException("Backend returned status " + response.statusCode() + ": " + response.body());
        }

        return objectMapper.readValue(response.body(), RecentErrorsResponse.class);
    }

    /**
     * Tool 5: Get dependency health status
     */
    public DependencyHealthResponse getDependencyHealth() throws IOException, InterruptedException {
        String url = backendBaseUrl + "/api/tools/dependencies";
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .timeout(Duration.ofSeconds(10))
                .GET()
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() != 200) {
            throw new IOException("Backend returned status " + response.statusCode() + ": " + response.body());
        }

        return objectMapper.readValue(response.body(), DependencyHealthResponse.class);
    }

    // Response DTOs matching the backend contracts

    public record ServiceHealthResponse(String paymentService, String kafka, String database) {}

    public record PaymentMetricsResponse(
            java.math.BigDecimal throughput,
            java.math.BigDecimal successRate,
            java.math.BigDecimal errorRate,
            java.math.BigDecimal avgLatencyMillis) {}

    public record KafkaConsumerLagResponse(String consumerGroup, String topic, long totalLag) {}

    public record RecentErrorsResponse(String service, java.util.List<ErrorItem> errors) {
        public record ErrorItem(String message, java.time.Instant timestamp) {}
    }

    public record DependencyHealthResponse(String kafka, String database) {}
}
