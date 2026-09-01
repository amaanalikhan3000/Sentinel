package com.sentinelai.backend.Service;

import com.sentinelai.backend.dto.RecentErrorsResponse;
import org.springframework.stereotype.Service;

@Service
public class RecentErrorsTool {

    private final RecentErrorCollector errorCollector;

    public RecentErrorsTool(RecentErrorCollector errorCollector) {
        this.errorCollector = errorCollector;
    }

    public RecentErrorsResponse getRecentErrors(
            String service,
            long timeWindowSeconds) {

        var errors = errorCollector
                .getRecentErrors(service, timeWindowSeconds)
                .stream()
                .map(error -> new RecentErrorsResponse.ErrorItem(
                        error.message(),
                        error.timestamp()
                ))
                .toList();

        return new RecentErrorsResponse(service, errors);
    }
}