package com.sentinelai.backend.dto;

import java.time.Instant;

public record IncidentResponse(Long id, String incidentType, String severity, String status,
                               Instant startedAt, Instant resolvedAt, String rootCause,
                               String impact, String recommendation) {
}
