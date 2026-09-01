package com.sentinelai.backend.checks;

import com.sentinelai.backend.Service.DependencyHealthTool;
import com.sentinelai.backend.Service.KafkaConsumerLagTool;
import com.sentinelai.backend.Service.RecentErrorsTool;
import com.sentinelai.backend.Service.ServiceHealthTool;
import com.sentinelai.backend.Service.PaymentMetricsTool;
import com.sentinelai.backend.dto.DependencyHealthResponse;
import com.sentinelai.backend.dto.KafkaConsumerLagResponse;
import com.sentinelai.backend.dto.RecentErrorsResponse;
import com.sentinelai.backend.dto.ServiceHealthResponse;
import com.sentinelai.backend.dto.PaymentMetricsResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;

@RestController
public class InvestigationToolsTestController {

    private final ServiceHealthTool serviceHealthTool;
    private final PaymentMetricsTool paymentMetricsTool;
    private final KafkaConsumerLagTool kafkaConsumerLagTool;
    private final RecentErrorsTool recentErrorsTool;
    private final DependencyHealthTool dependencyHealthTool;

    public InvestigationToolsTestController(
            ServiceHealthTool serviceHealthTool,
            PaymentMetricsTool paymentMetricsTool,
            KafkaConsumerLagTool kafkaConsumerLagTool,
            RecentErrorsTool recentErrorsTool,
            DependencyHealthTool dependencyHealthTool) {

        this.serviceHealthTool = serviceHealthTool;
        this.paymentMetricsTool = paymentMetricsTool;
        this.kafkaConsumerLagTool = kafkaConsumerLagTool;
        this.recentErrorsTool = recentErrorsTool;
        this.dependencyHealthTool = dependencyHealthTool;
    }

    @GetMapping("/api/tools/health")
    public ServiceHealthResponse health() {
        return serviceHealthTool.getServiceHealth();
    }

    @GetMapping("/api/tools/metrics")
    public PaymentMetricsResponse metrics() {
        return paymentMetricsTool.getPaymentMetrics(
                Instant.now().minusSeconds(300),
                Instant.now()
        );
    }

    @GetMapping("/api/tools/kafka-lag")
    public KafkaConsumerLagResponse kafkaLag(
            @RequestParam String consumerGroup,
            @RequestParam String topic) {

        return kafkaConsumerLagTool.getKafkaConsumerLag(
                consumerGroup,
                topic
        );
    }

    @GetMapping("/api/tools/recent-errors")
    public RecentErrorsResponse recentErrors(
            @RequestParam String service,
            @RequestParam long timeWindowSeconds) {

        return recentErrorsTool.getRecentErrors(
                service,
                timeWindowSeconds
        );
    }

    @GetMapping("/api/tools/dependencies")
    public DependencyHealthResponse dependencies() {
        return dependencyHealthTool.getDependencyHealth();
    }
}