package com.sentinelai.backend.repository;

import com.sentinelai.backend.entity.Investigation;
import org.springframework.data.jpa.repository.JpaRepository;

public interface InvestigationRepository
        extends JpaRepository<Investigation, Long> {
}