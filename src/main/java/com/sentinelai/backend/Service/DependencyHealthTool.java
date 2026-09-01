package com.sentinelai.backend.Service;

import com.sentinelai.backend.dto.DependencyHealthResponse;
import org.springframework.kafka.core.KafkaAdmin;
import org.springframework.stereotype.Service;

import javax.sql.DataSource;

@Service
public class DependencyHealthTool {

    private final DataSource dataSource;
    private final KafkaAdmin kafkaAdmin;

    public DependencyHealthTool(
            DataSource dataSource,
            KafkaAdmin kafkaAdmin) {
        this.dataSource = dataSource;
        this.kafkaAdmin = kafkaAdmin;
    }

    public DependencyHealthResponse getDependencyHealth() {
        return new DependencyHealthResponse(
                checkKafkaHealth(),
                checkDatabaseHealth()
        );
    }

    private String checkDatabaseHealth() {
        try {
            var connection = dataSource.getConnection();
            connection.close();
            return "UP";
        } catch (Exception e) {
            return "DOWN";
        }
    }

    private String checkKafkaHealth() {
        try {
            if (kafkaAdmin != null) {
                return "UP";
            }
            return "DOWN";
        } catch (Exception e) {
            return "DOWN";
        }
    }
}