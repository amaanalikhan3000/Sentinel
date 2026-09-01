package com.sentinelai.backend.Service;

import com.sentinelai.backend.dto.ServiceHealthResponse;
import javax.sql.DataSource;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.kafka.core.KafkaAdmin;

import java.sql.Connection;

public class ServiceHealthToolTest {

    @Test
    public void testGetServiceHealthUp() throws Exception {
        DataSource dataSource = Mockito.mock(DataSource.class);
        KafkaAdmin kafkaAdmin = Mockito.mock(KafkaAdmin.class);

        Connection connection = Mockito.mock(Connection.class);
        Mockito.when(dataSource.getConnection()).thenReturn(connection);

        ServiceHealthTool tool = new ServiceHealthTool(dataSource, kafkaAdmin);
        ServiceHealthResponse response = tool.getServiceHealth();

        Assertions.assertNotNull(response);
        Assertions.assertEquals("UP", response.paymentService());
        Assertions.assertEquals("UP", response.kafka());
        Assertions.assertEquals("UP", response.database());
    }

    @Test
    public void testGetServiceHealthDatabaseDown() throws Exception {
        DataSource dataSource = Mockito.mock(DataSource.class);
        KafkaAdmin kafkaAdmin = Mockito.mock(KafkaAdmin.class);

        Mockito.when(dataSource.getConnection()).thenThrow(new RuntimeException("DB connection failed"));

        ServiceHealthTool tool = new ServiceHealthTool(dataSource, kafkaAdmin);
        ServiceHealthResponse response = tool.getServiceHealth();

        Assertions.assertNotNull(response);
        Assertions.assertEquals("UP", response.paymentService());
        Assertions.assertEquals("UP", response.kafka());
        Assertions.assertEquals("DOWN", response.database());
    }

    @Test
    public void testGetServiceHealthKafkaNull() throws Exception {
        DataSource dataSource = Mockito.mock(DataSource.class);

        Connection connection = Mockito.mock(Connection.class);
        Mockito.when(dataSource.getConnection()).thenReturn(connection);

        ServiceHealthTool tool = new ServiceHealthTool(dataSource, null);
        ServiceHealthResponse response = tool.getServiceHealth();

        Assertions.assertNotNull(response);
        Assertions.assertEquals("UP", response.paymentService());
        Assertions.assertEquals("DOWN", response.kafka());
        Assertions.assertEquals("UP", response.database());
    }
}

