package com.sentinelai.backend.dto;

import jakarta.validation.constraints.Min;

public record SimulationRequest(boolean enabled, @Min(0) long delayMs) {
}
