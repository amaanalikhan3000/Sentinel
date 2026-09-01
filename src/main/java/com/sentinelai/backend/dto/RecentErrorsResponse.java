package com.sentinelai.backend.dto;

import java.time.Instant;
import java.util.List;

public record RecentErrorsResponse(
        String service,
        List<ErrorItem> errors
) {

    public record ErrorItem(
            String message,
            Instant timestamp
    ) {
    }
}