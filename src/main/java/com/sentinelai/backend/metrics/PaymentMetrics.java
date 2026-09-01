package com.sentinelai.backend.metrics;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

@Component
public class PaymentMetrics {

    private final Counter requestsCounter;
    private final Counter successCounter;
    private final Counter failureCounter;
    private final Timer processingTimer;

    public PaymentMetrics(MeterRegistry registry) {
        this.requestsCounter = Counter.builder("payment_requests_total").description("Total payment requests received").register(registry);
        this.successCounter = Counter.builder("payment_success_total").description("Total successful payments processed").register(registry);
        this.failureCounter = Counter.builder("payment_failures_total").description("Total failed payment processing attempts").register(registry);
        this.processingTimer = Timer.builder("payment_processing_duration_seconds").description("Payment processing duration in seconds").publishPercentiles(0.5, 0.95).register(registry);
    }

    public void incrementPaymentRequestsTotal() {
        requestsCounter.increment();
    }

    public void incrementPaymentSuccessTotal() {
        successCounter.increment();
    }

    public void incrementPaymentFailuresTotal() {
        failureCounter.increment();
    }

    public void recordProcessing(Runnable work) {
        long start = System.nanoTime();
        try {
            work.run();
            incrementPaymentSuccessTotal();
        } catch (RuntimeException | Error e) {
            incrementPaymentFailuresTotal();
            throw e;
        } finally {
            long durationNs = System.nanoTime() - start;
            processingTimer.record(durationNs, TimeUnit.NANOSECONDS);
        }
    }
}
