package com.sentinelai.backend.metrics;

import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.simple.SimpleMeterRegistry;
import io.micrometer.core.instrument.Timer;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class PaymentMetricsTest {

    @Test
    public void testCountersAndTimer() {
        MeterRegistry registry = new SimpleMeterRegistry();
        PaymentMetrics metrics = new PaymentMetrics(registry);

        metrics.incrementPaymentRequestsTotal();
        Assertions.assertEquals(1.0, registry.get("payment_requests_total").counter().count());

        metrics.recordProcessing(() -> {
            // no-op successful processing
        });

        Assertions.assertEquals(1.0, registry.get("payment_success_total").counter().count());
        Timer t = registry.find("payment_processing_duration_seconds").timer();
        Assertions.assertNotNull(t);
    }
}
