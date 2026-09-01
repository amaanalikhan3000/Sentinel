package com.sentinelai.backend.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.Instant;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "incidents")
@Data
@NoArgsConstructor
public class Incident {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(name = "incident_type", nullable = false, length = 64)
	private String incidentType;

	@Column(name = "severity", nullable = false, length = 32)
	private String severity;

	@Column(name = "status", nullable = false, length = 32)
	private String status;

	@Column(name = "started_at", nullable = false)
	private Instant startedAt;

	@Column(name = "resolved_at")
	private Instant resolvedAt;

	@Column(name = "root_cause", columnDefinition = "TEXT")
	private String rootCause;

	@Column(name = "impact", columnDefinition = "TEXT")
	private String impact;

	@Column(name = "recommendation", columnDefinition = "TEXT")
	private String recommendation;

}
