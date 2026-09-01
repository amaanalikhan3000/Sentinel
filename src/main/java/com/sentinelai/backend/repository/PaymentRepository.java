package com.sentinelai.backend.repository;

import com.sentinelai.backend.entity.Payment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PaymentRepository
        extends JpaRepository<Payment, Long> {
        java.util.Optional<Payment> findByTransactionId(String transactionId);
}