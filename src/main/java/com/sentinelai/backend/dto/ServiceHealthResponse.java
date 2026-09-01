package com.sentinelai.backend.dto;

public record ServiceHealthResponse(String paymentService, String kafka, String database) {
}
