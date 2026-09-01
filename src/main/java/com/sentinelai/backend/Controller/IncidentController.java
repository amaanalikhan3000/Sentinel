package com.sentinelai.backend.Controller;

import com.sentinelai.backend.Service.IncidentService;
import com.sentinelai.backend.dto.IncidentResponse;
import com.sentinelai.backend.dto.InvestigationResponse;
import com.sentinelai.backend.entity.Incident;
import com.sentinelai.backend.entity.Investigation;
import java.util.Optional;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/incidents")
public class IncidentController {

    private final IncidentService incidentService;

    public IncidentController(IncidentService incidentService) {
        this.incidentService = incidentService;
    }

    @GetMapping("/active")
    public ResponseEntity<IncidentResponse> getActiveIncident() {
        Optional<Incident> maybe = incidentService.getActiveIncident();
        if (maybe.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        Incident incident = maybe.get();
        IncidentResponse response = toIncidentResponse(incident);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<IncidentResponse> getIncidentById(@PathVariable Long id) {
        Optional<Incident> maybe = incidentService.getIncidentById(id);
        if (maybe.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        Incident incident = maybe.get();
        IncidentResponse response = toIncidentResponse(incident);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/{id}/investigate")
    public ResponseEntity<InvestigationResponse> investigateIncident(@PathVariable Long id) {
        try {
            Investigation investigation = incidentService.investigateIncident(id);
            InvestigationResponse response = toInvestigationResponse(investigation);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }

    private IncidentResponse toIncidentResponse(Incident incident) {
        return new IncidentResponse(
            incident.getId(),
            incident.getIncidentType(),
            incident.getSeverity(),
            incident.getStatus(),
            incident.getStartedAt(),
            incident.getResolvedAt(),
            incident.getRootCause(),
            incident.getImpact(),
            incident.getRecommendation()
        );
    }

    private InvestigationResponse toInvestigationResponse(Investigation investigation) {
        return new InvestigationResponse(
            investigation.getId(),
            investigation.getIncidentId(),
            investigation.getStartedAt(),
            investigation.getCompletedAt(),
            investigation.getSummary(),
            investigation.getRootCause(),
            investigation.getConfidence()
        );
    }
}
