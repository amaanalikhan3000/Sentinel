package com.sentinelai.backend.Service;

import com.sentinelai.backend.dto.PaymentRequest;
import com.sentinelai.backend.dto.PaymentResponse;
import com.sentinelai.backend.entity.Payment;
import com.sentinelai.backend.kakfa.PaymentEvent;
import com.sentinelai.backend.kakfa.PaymentProducer;
import com.sentinelai.backend.metrics.PaymentMetrics;
import com.sentinelai.backend.repository.PaymentRepository;
import java.time.Instant;
import java.util.UUID;
import org.springframework.stereotype.Service;
import java.math.BigDecimal;
import java.util.Optional;

@Service
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final PaymentProducer paymentProducer;
    private final PaymentMetrics paymentMetrics;

    public PaymentService(PaymentRepository paymentRepository,
                          PaymentProducer paymentProducer,
                          PaymentMetrics paymentMetrics) {
        this.paymentRepository = paymentRepository;
        this.paymentProducer = paymentProducer;
        this.paymentMetrics = paymentMetrics;
    }

    public PaymentResponse createPayment(PaymentRequest request) {
        String transactionId = "TXN-" + UUID.randomUUID();

        Payment payment = new Payment();
        payment.setTransactionId(transactionId);
        payment.setCustomerId(request.customerId());
        payment.setAmount(request.amount());
        payment.setPaymentType(request.paymentType());
        payment.setStatus("PROCESSING");
        Instant createdAt = Instant.now();
        payment.setCreatedAt(createdAt);

        paymentRepository.save(payment);

        PaymentEvent event = new PaymentEvent(
            transactionId,
            request.customerId(),
            request.amount(),
            request.paymentType(),
            "PAYMENT_CREATED",
            createdAt
        );
        paymentProducer.publish(event);

        paymentMetrics.incrementPaymentRequestsTotal();

        return new PaymentResponse(transactionId, "PROCESSING");
    }

    public void process(com.sentinelai.backend.kakfa.PaymentEvent event) {
        if (event == null) return;

        paymentMetrics.recordProcessing(() -> {
            String transactionId = event.transactionId();
            Optional<Payment> maybe = paymentRepository.findByTransactionId(transactionId);
            if (maybe.isEmpty()) {
                return;
            }

            Payment payment = maybe.get();
            payment.setStatus("SUCCESS");
            payment.setProcessedAt(Instant.now());
            paymentRepository.save(payment);
        });
    }
}
