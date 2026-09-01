package com.sentinelai.backend.kakfa;

import com.sentinelai.backend.Service.PaymentService;
import com.sentinelai.backend.Service.RecentErrorCollector;
import com.sentinelai.backend.simulator.IncidentSimulator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class PaymentConsumer {

    private static final Logger log = LoggerFactory.getLogger(PaymentConsumer.class);

    private final PaymentService paymentService;
    private final IncidentSimulator incidentSimulator;
    private final RecentErrorCollector errorCollector;

    public PaymentConsumer(
            PaymentService paymentService,
            IncidentSimulator incidentSimulator,
            RecentErrorCollector errorCollector) {

        this.paymentService = paymentService;
        this.incidentSimulator = incidentSimulator;
        this.errorCollector = errorCollector;
    }

    @KafkaListener(topics = "payments", groupId = "payment-processor")
    public void listen(PaymentEvent event) {
        try {
            if (incidentSimulator != null && incidentSimulator.isEnabled()) {
                long d = incidentSimulator.getDelayMs();

                if (d > 0) {
                    try {
                        Thread.sleep(d);
                    } catch (InterruptedException ie) {
                        Thread.currentThread().interrupt();
                    }
                }
            }

            paymentService.process(event);
            log.info("Processed payment event: {}", event.transactionId());

        } catch (Exception e) {
            log.error("Failed to process payment event", e);

            errorCollector.record(
                    "payment-service",
                    "Failed to process payment event"
            );
        }
    }
}