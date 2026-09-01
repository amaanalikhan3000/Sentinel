package com.sentinelai.backend.Controller;

import com.sentinelai.backend.Service.SimulationService;
import com.sentinelai.backend.dto.SimulationRequest;
import com.sentinelai.backend.simulator.IncidentSimulator;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/simulation")
public class SimulationController {

    private final SimulationService simulationService;

    public SimulationController(SimulationService simulationService) {
        this.simulationService = simulationService;
    }

    @PostMapping("/consumer-slowdown")
    public ResponseEntity<Void> configureConsumerSlowdown(@Valid @RequestBody SimulationRequest request) {
        simulationService.configureConsumerSlowdown(request.enabled(), request.delayMs());
        return ResponseEntity.ok().build();
    }

    @GetMapping("/status")
    public IncidentSimulator status() {
        return simulationService.status();
    }
}
