package com.sentinelai.backend.Service;

import com.sentinelai.backend.dto.PaymentMetricsResponse;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import io.micrometer.core.instrument.simple.SimpleMeterRegistry;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.concurrent.TimeUnit;

public class PaymentMetricsToolTest {

    @Test
    public void testGetPaymentMetricsWithValidData() {
        MeterRegistry registry = new SimpleMeterRegistry();

        Counter requestsCounter = Counter.builder("payment_requests_total").register(registry);
        Counter successCounter = Counter.builder("payment_success_total").register(registry);
        Counter failureCounter = Counter.builder("payment_failures_total").register(registry);
        Timer processingTimer = Timer.builder("payment_processing_duration_seconds").register(registry);

        // Simulate 100 requests
        requestsCounter.increment(100);
        successCounter.increment(95);
        failureCounter.increment(5);

        // Simulate processing times (in milliseconds): 100ms average
        for (int i = 0; i < 95; i++) {
            processingTimer.record(100, TimeUnit.MILLISECONDS);
        }

        PaymentMetricsTool tool = new PaymentMetricsTool(registry);
        Instant startTime = Instant.now().minusSeconds(10);
        Instant endTime = Instant.now();

        PaymentMetricsResponse response = tool.getPaymentMetrics(startTime, endTime);

        Assertions.assertNotNull(response);
        Assertions.assertTrue(response.throughputPerSecond().signum() > 0, "Throughput should be positive");
        Assertions.assertEquals(new BigDecimal("0.9500"), response.successRate(), "Success rate should be 95%");
        Assertions.assertEquals(new BigDecimal("0.0500"), response.errorRate(), "Error rate should be 5%");
        Assertions.assertTrue(response.averageLatencyMillis().signum() > 0, "Average latency should be positive");
    }

    @Test
    public void testGetPaymentMetricsZeroRequests() {
        MeterRegistry registry = new SimpleMeterRegistry();

        Counter requestsCounter = Counter.builder("payment_requests_total").register(registry);
        Counter successCounter = Counter.builder("payment_success_total").register(registry);
        Counter failureCounter = Counter.builder("payment_failures_total").register(registry);
        Timer processingTimer = Timer.builder("payment_processing_duration_seconds").register(registry);

        PaymentMetricsTool tool = new PaymentMetricsTool(registry);
        Instant startTime = Instant.now().minusSeconds(10);
        Instant endTime = Instant.now();

        PaymentMetricsResponse response = tool.getPaymentMetrics(startTime, endTime);

        Assertions.assertNotNull(response);
        Assertions.assertEquals(0, response.throughputPerSecond().signum(), "Throughput should be zero");
        Assertions.assertEquals(0, response.successRate().signum(), "Success rate should be zero");
        Assertions.assertEquals(0, response.errorRate().signum(), "Error rate should be zero");
        Assertions.assertEquals(0, response.averageLatencyMillis().signum(), "Average latency should be zero");
    }

    @Test
    public void testGetPaymentMetricsAllSuccessful() {
        MeterRegistry registry = new SimpleMeterRegistry();

        Counter requestsCounter = Counter.builder("payment_requests_total").register(registry);
        Counter successCounter = Counter.builder("payment_success_total").register(registry);
        Counter failureCounter = Counter.builder("payment_failures_total").register(registry);
        Timer processingTimer = Timer.builder("payment_processing_duration_seconds").register(registry);

        requestsCounter.increment(50);
        successCounter.increment(50);
        failureCounter.increment(0);

        for (int i = 0; i < 50; i++) {
            processingTimer.record(50, TimeUnit.MILLISECONDS);
        }

        PaymentMetricsTool tool = new PaymentMetricsTool(registry);
        Instant startTime = Instant.now().minusSeconds(5);
        Instant endTime = Instant.now();

        PaymentMetricsResponse response = tool.getPaymentMetrics(startTime, endTime);

        Assertions.assertNotNull(response);
        Assertions.assertEquals(new BigDecimal("1.0000"), response.successRate(), "Success rate should be 100%");
        Assertions.assertEquals(0, response.errorRate().signum(), "Error rate should be 0%");
    }
}
