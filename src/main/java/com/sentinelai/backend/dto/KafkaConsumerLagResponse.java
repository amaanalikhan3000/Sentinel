package com.sentinelai.backend.dto;

public record KafkaConsumerLagResponse(
        String consumerGroup,
        String topic,
        long lag
) {
}