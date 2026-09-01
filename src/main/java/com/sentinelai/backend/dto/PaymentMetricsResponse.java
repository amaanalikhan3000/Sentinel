package com.sentinelai.backend.dto;

import java.math.BigDecimal;

public record PaymentMetricsResponse(
    BigDecimal throughputPerSecond,
    BigDecimal successRate,
    BigDecimal errorRate,
    BigDecimal averageLatencyMillis
) {
}
