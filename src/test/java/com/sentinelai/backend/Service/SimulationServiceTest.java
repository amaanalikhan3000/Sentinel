package com.sentinelai.backend.Service;

import com.sentinelai.backend.simulator.IncidentSimulator;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class SimulationServiceTest {

    @Test
    public void testEnableDisableAndDelay() {
        IncidentSimulator sim = new IncidentSimulator();
        SimulationService svc = new SimulationService(sim);

        svc.configureConsumerSlowdown(true, 500);
        Assertions.assertTrue(sim.isEnabled());
        Assertions.assertEquals(500, sim.getDelayMs());

        svc.configureConsumerSlowdown(false, 0);
        Assertions.assertFalse(sim.isEnabled());
        Assertions.assertEquals(0, sim.getDelayMs());
    }

    @Test
    public void testNegativeDelayRejected() {
        IncidentSimulator sim = new IncidentSimulator();
        SimulationService svc = new SimulationService(sim);
        Assertions.assertThrows(IllegalArgumentException.class, () -> svc.configureConsumerSlowdown(true, -1));
    }
}
