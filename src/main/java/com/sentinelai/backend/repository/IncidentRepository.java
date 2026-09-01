package com.sentinelai.backend.repository;

import com.sentinelai.backend.entity.Incident;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface IncidentRepository
        extends JpaRepository<Incident, Long> {

    Optional<Incident> findByStatus(String status);
}