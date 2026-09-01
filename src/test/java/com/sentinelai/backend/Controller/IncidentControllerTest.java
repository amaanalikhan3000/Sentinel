package com.sentinelai.backend.Controller;

import com.sentinelai.backend.Service.IncidentService;
import com.sentinelai.backend.dto.IncidentResponse;
import com.sentinelai.backend.dto.InvestigationResponse;
import com.sentinelai.backend.entity.Incident;
import com.sentinelai.backend.entity.Investigation;
import java.time.Instant;
import java.util.Optional;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

public class IncidentControllerTest {

    private IncidentService incidentService;
    private IncidentController controller;

    private void setup() {
        incidentService = Mockito.mock(IncidentService.class);
        controller = new IncidentController(incidentService);
    }

    @Test
    public void testGetActiveIncident() {
        setup();
        Incident incident = new Incident();
        incident.setId(1L);
        incident.setIncidentType("PAYMENT_ANOMALY");
        incident.setSeverity("HIGH");
        incident.setStatus("OPEN");
        incident.setStartedAt(Instant.now());

        Mockito.when(incidentService.getActiveIncident()).thenReturn(Optional.of(incident));

        var response = controller.getActiveIncident();
        Assertions.assertEquals(200, response.getStatusCode().value());
        Assertions.assertNotNull(response.getBody());
        Assertions.assertEquals("OPEN", response.getBody().status());
    }

    @Test
    public void testGetActiveIncidentNotFound() {
        setup();
        Mockito.when(incidentService.getActiveIncident()).thenReturn(Optional.empty());

        var response = controller.getActiveIncident();
        Assertions.assertEquals(204, response.getStatusCode().value());
    }

    @Test
    public void testGetIncidentById() {
        setup();
        Incident incident = new Incident();
        incident.setId(1L);
        incident.setIncidentType("PAYMENT_ANOMALY");
        incident.setStatus("OPEN");

        Mockito.when(incidentService.getIncidentById(1L)).thenReturn(Optional.of(incident));

        var response = controller.getIncidentById(1L);
        Assertions.assertEquals(200, response.getStatusCode().value());
        Assertions.assertNotNull(response.getBody());
        Assertions.assertEquals(1L, response.getBody().id());
    }

    @Test
    public void testGetIncidentByIdNotFound() {
        setup();
        Mockito.when(incidentService.getIncidentById(999L)).thenReturn(Optional.empty());

        var response = controller.getIncidentById(999L);
        Assertions.assertEquals(404, response.getStatusCode().value());
    }

    @Test
    public void testInvestigateIncident() {
        setup();
        Investigation investigation = new Investigation();
        investigation.setId(1L);
        investigation.setIncidentId(1L);
        investigation.setStartedAt(Instant.now());

        Mockito.when(incidentService.investigateIncident(1L)).thenReturn(investigation);

        var response = controller.investigateIncident(1L);
        Assertions.assertEquals(201, response.getStatusCode().value());
        Assertions.assertNotNull(response.getBody());
        Assertions.assertEquals(1L, response.getBody().incidentId());
    }

    @Test
    public void testInvestigateIncidentNotFound() {
        setup();
        Mockito.when(incidentService.investigateIncident(999L))
            .thenThrow(new IllegalArgumentException("Incident not found"));

        var response = controller.investigateIncident(999L);
        Assertions.assertEquals(404, response.getStatusCode().value());
    }
}
