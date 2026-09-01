package com.sentinelai.backend.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.time.Instant;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "investigations")
@Data
@NoArgsConstructor
public class Investigation {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(name = "incident_id", nullable = false)
	private Long incidentId;

	@Column(name = "started_at", nullable = false)
	private Instant startedAt;

	@Column(name = "completed_at")
	private Instant completedAt;

	@Column(name = "summary", columnDefinition = "TEXT")
	private String summary;

	@Column(name = "root_cause", columnDefinition = "TEXT")
	private String rootCause;

	@Column(name = "confidence", precision = 5, scale = 4)
	private BigDecimal confidence;

}
