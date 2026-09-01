package com.sentinelai.backend.kakfa;

import java.math.BigDecimal;
import java.time.Instant;

public record PaymentEvent(
	String transactionId,
	String customerId,
	BigDecimal amount,
	String paymentType,
	String eventType,
	Instant timestamp
) {
}
