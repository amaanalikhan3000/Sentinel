package com.sentinelai.backend.Service;

import com.sentinelai.backend.entity.Incident;
import com.sentinelai.backend.entity.Investigation;
import com.sentinelai.backend.repository.IncidentRepository;
import com.sentinelai.backend.repository.InvestigationRepository;
import java.time.Instant;
import java.util.Optional;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

public class IncidentServiceTest {

    @Test
    public void testGetActiveIncident() {
        Incident incident = new Incident();
        incident.setId(1L);
        incident.setStatus("OPEN");

        IncidentRepository incidentRepository = Mockito.mock(IncidentRepository.class);
        InvestigationRepository investigationRepository = Mockito.mock(InvestigationRepository.class);
        Mockito.when(incidentRepository.findByStatus("OPEN")).thenReturn(Optional.of(incident));

        IncidentService service = new IncidentService(incidentRepository, investigationRepository);
        Optional<Incident> result = service.getActiveIncident();

        Assertions.assertTrue(result.isPresent());
        Assertions.assertEquals("OPEN", result.get().getStatus());
    }

    @Test
    public void testGetIncidentById() {
        Incident incident = new Incident();
        incident.setId(1L);

        IncidentRepository incidentRepository = Mockito.mock(IncidentRepository.class);
        InvestigationRepository investigationRepository = Mockito.mock(InvestigationRepository.class);
        Mockito.when(incidentRepository.findById(1L)).thenReturn(Optional.of(incident));

        IncidentService service = new IncidentService(incidentRepository, investigationRepository);
        Optional<Incident> result = service.getIncidentById(1L);

        Assertions.assertTrue(result.isPresent());
        Assertions.assertEquals(1L, result.get().getId());
    }

    @Test
    public void testInvestigateIncident() {
        Incident incident = new Incident();
        incident.setId(1L);
        incident.setStatus("OPEN");

        Investigation investigation = new Investigation();
        investigation.setId(1L);
        investigation.setIncidentId(1L);
        investigation.setStartedAt(Instant.now());

        IncidentRepository incidentRepository = Mockito.mock(IncidentRepository.class);
        InvestigationRepository investigationRepository = Mockito.mock(InvestigationRepository.class);

        Mockito.when(incidentRepository.findById(1L)).thenReturn(Optional.of(incident));
        Mockito.when(incidentRepository.save(Mockito.any(Incident.class))).thenAnswer(invocation -> invocation.getArgument(0));
        Mockito.when(investigationRepository.save(Mockito.any(Investigation.class))).thenReturn(investigation);

        IncidentService service = new IncidentService(incidentRepository, investigationRepository);
        Investigation result = service.investigateIncident(1L);

        Assertions.assertNotNull(result);
        Assertions.assertEquals(1L, result.getIncidentId());
        Mockito.verify(incidentRepository).save(Mockito.argThat(i -> "INVESTIGATING".equals(i.getStatus())));
    }

    @Test
    public void testInvestigateIncidentNotFound() {
        IncidentRepository incidentRepository = Mockito.mock(IncidentRepository.class);
        InvestigationRepository investigationRepository = Mockito.mock(InvestigationRepository.class);

        Mockito.when(incidentRepository.findById(999L)).thenReturn(Optional.empty());

        IncidentService service = new IncidentService(incidentRepository, investigationRepository);
        Assertions.assertThrows(IllegalArgumentException.class, () -> service.investigateIncident(999L));
    }
}
