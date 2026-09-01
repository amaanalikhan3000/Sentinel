package com.sentinelai.backend.Service;

import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

@Component
public class RecentErrorCollector {

    private final List<RecentError> errors = new CopyOnWriteArrayList<>();

    public void record(String service, String message) {
        errors.add(new RecentError(
                service,
                message,
                Instant.now()
        ));
    }

    public List<RecentError> getRecentErrors(
            String service,
            long timeWindowSeconds) {

        Instant cutoff = Instant.now().minusSeconds(timeWindowSeconds);

        return errors.stream()
                .filter(error -> error.service().equals(service))
                .filter(error -> error.timestamp().isAfter(cutoff))
                .toList();
    }

    public record RecentError(
            String service,
            String message,
            Instant timestamp
    ) {
    }
}