package com.sentinelai.backend.Service;

import com.sentinelai.backend.dto.KafkaConsumerLagResponse;
import org.apache.kafka.clients.admin.AdminClient;
import org.apache.kafka.clients.admin.AdminClientConfig;
import org.apache.kafka.clients.admin.OffsetSpec;
import org.apache.kafka.clients.consumer.OffsetAndMetadata;
import org.apache.kafka.common.TopicPartition;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

@Service
public class KafkaConsumerLagTool {

    private final String bootstrapServers;

    public KafkaConsumerLagTool(
            @Value("${spring.kafka.bootstrap-servers}") String bootstrapServers) {
        this.bootstrapServers = bootstrapServers;
    }

    public KafkaConsumerLagResponse getKafkaConsumerLag(
            String consumerGroup,
            String topic) {

        Properties properties = new Properties();
        properties.put(
                AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG,
                bootstrapServers
        );

        try (AdminClient adminClient = AdminClient.create(properties)) {

            Map<TopicPartition, OffsetSpec> offsetRequests =
                    new HashMap<>();

            var topicDescription = adminClient
                    .describeTopics(java.util.List.of(topic))
                    .allTopicNames()
                    .get()
                    .get(topic);

            for (var partitionInfo : topicDescription.partitions()) {
                TopicPartition partition =
                        new TopicPartition(topic, partitionInfo.partition());

                offsetRequests.put(
                        partition,
                        OffsetSpec.latest()
                );
            }

            Map<TopicPartition, Long> latestOffsets =
                    new HashMap<>();

            var offsetResults =
                    adminClient.listOffsets(offsetRequests)
                            .all()
                            .get();

            offsetResults.forEach((partition, result) ->
                    latestOffsets.put(partition, result.offset())
            );

            Map<TopicPartition, OffsetAndMetadata> committedOffsets =
                    adminClient.listConsumerGroupOffsets(consumerGroup)
                            .partitionsToOffsetAndMetadata()
                            .get();

            long totalLag = 0;

            for (Map.Entry<TopicPartition, Long> entry :
                    latestOffsets.entrySet()) {

                TopicPartition partition = entry.getKey();
                long latestOffset = entry.getValue();

                OffsetAndMetadata committed =
                        committedOffsets.get(partition);

                if (committed != null) {
                    totalLag += Math.max(
                            0,
                            latestOffset - committed.offset()
                    );
                }
            }

            return new KafkaConsumerLagResponse(
                    consumerGroup,
                    topic,
                    totalLag
            );

        } catch (Exception e) {
            throw new IllegalStateException(
                    "Unable to retrieve Kafka consumer lag",
                    e
            );
        }
    }
}