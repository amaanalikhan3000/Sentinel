package com.sentinelai.backend.simulator;

import org.springframework.stereotype.Component;

@Component
public class IncidentSimulator {

    private volatile boolean enabled = false;
    private volatile long delayMs = 0L;

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public long getDelayMs() {
        return delayMs;
    }

    public void setDelayMs(long delayMs) {
        this.delayMs = delayMs;
    }
}
