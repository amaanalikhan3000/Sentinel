package com.sentinelai.backend.Service;

import com.sentinelai.backend.dto.PaymentMetricsResponse;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Instant;
import java.util.concurrent.TimeUnit;

@Service
public class PaymentMetricsTool {

    private final MeterRegistry meterRegistry;

    public PaymentMetricsTool(MeterRegistry meterRegistry) {
        this.meterRegistry = meterRegistry;
    }

    public PaymentMetricsResponse getPaymentMetrics(Instant startTime, Instant endTime) {
        Counter requestsCounter = meterRegistry.find("payment_requests_total").counter();
        Counter successCounter = meterRegistry.find("payment_success_total").counter();
        Counter failureCounter = meterRegistry.find("payment_failures_total").counter();
        Timer processingTimer = meterRegistry.find("payment_processing_duration_seconds").timer();

        double totalRequests = requestsCounter != null ? requestsCounter.count() : 0.0;
        double successCount = successCounter != null ? successCounter.count() : 0.0;
        double failureCount = failureCounter != null ? failureCounter.count() : 0.0;
        double avgLatencyNanos = processingTimer != null ? processingTimer.mean(TimeUnit.NANOSECONDS) : 0.0;

        long windowSeconds = (endTime.getEpochSecond() - startTime.getEpochSecond());
        if (windowSeconds <= 0) {
            windowSeconds = 1;
        }

        BigDecimal throughput = totalRequests > 0 
            ? BigDecimal.valueOf(totalRequests).divide(BigDecimal.valueOf(windowSeconds), 2, RoundingMode.HALF_UP)
            : BigDecimal.ZERO;

        BigDecimal successRate = totalRequests > 0
            ? BigDecimal.valueOf(successCount).divide(BigDecimal.valueOf(totalRequests), 4, RoundingMode.HALF_UP)
            : BigDecimal.ZERO;

        BigDecimal errorRate = totalRequests > 0
            ? BigDecimal.valueOf(failureCount).divide(BigDecimal.valueOf(totalRequests), 4, RoundingMode.HALF_UP)
            : BigDecimal.ZERO;

        BigDecimal avgLatencyMillis = BigDecimal.valueOf(avgLatencyNanos).divide(BigDecimal.valueOf(1_000_000), 2, RoundingMode.HALF_UP);

        return new PaymentMetricsResponse(throughput, successRate, errorRate, avgLatencyMillis);
    }
}
