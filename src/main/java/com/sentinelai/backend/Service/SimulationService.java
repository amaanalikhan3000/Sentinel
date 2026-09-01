package com.sentinelai.backend.Service;

import com.sentinelai.backend.simulator.IncidentSimulator;
import org.springframework.stereotype.Service;

@Service
public class SimulationService {

    private final IncidentSimulator simulator;

    public SimulationService(IncidentSimulator simulator) {
        this.simulator = simulator;
    }

    public void configureConsumerSlowdown(boolean enabled, long delayMs) {
        if (delayMs < 0) throw new IllegalArgumentException("delayMs must be >= 0");
        simulator.setEnabled(enabled);
        simulator.setDelayMs(delayMs);
    }

    public IncidentSimulator status() {
        return simulator;
    }
}
