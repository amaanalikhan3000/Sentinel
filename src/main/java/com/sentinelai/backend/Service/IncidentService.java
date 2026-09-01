package com.sentinelai.backend.Service;

import com.sentinelai.backend.entity.Incident;
import com.sentinelai.backend.entity.Investigation;
import com.sentinelai.backend.repository.IncidentRepository;
import com.sentinelai.backend.repository.InvestigationRepository;
import java.time.Instant;
import java.util.Optional;
import org.springframework.stereotype.Service;

@Service
public class IncidentService {

    private final IncidentRepository incidentRepository;
    private final InvestigationRepository investigationRepository;

    public IncidentService(IncidentRepository incidentRepository,
                           InvestigationRepository investigationRepository) {
        this.incidentRepository = incidentRepository;
        this.investigationRepository = investigationRepository;
    }

    public Optional<Incident> getActiveIncident() {
        return incidentRepository.findByStatus("OPEN");
    }

    public Optional<Incident> getIncidentById(Long id) {
        return incidentRepository.findById(id);
    }

    public Investigation investigateIncident(Long incidentId) {
        Optional<Incident> maybe = incidentRepository.findById(incidentId);
        if (maybe.isEmpty()) {
            throw new IllegalArgumentException("Incident not found: " + incidentId);
        }

        Incident incident = maybe.get();
        incident.setStatus("INVESTIGATING");
        incidentRepository.save(incident);

        Investigation investigation = new Investigation();
        investigation.setIncidentId(incidentId);
        investigation.setStartedAt(Instant.now());
        return investigationRepository.save(investigation);
    }
}
