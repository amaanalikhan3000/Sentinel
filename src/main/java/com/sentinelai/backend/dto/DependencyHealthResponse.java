package com.sentinelai.backend.dto;

public record DependencyHealthResponse(
        String kafka,
        String database
) {
}