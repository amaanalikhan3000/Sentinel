package com.sentinelai.backend.dto;

import java.math.BigDecimal;
import java.time.Instant;

public record InvestigationResponse(Long id, Long incidentId, Instant startedAt,
                                     Instant completedAt, String summary, String rootCause,
                                     BigDecimal confidence) {
}
