package com.linki.admin_integration_service.domain.linkiscore.repository;

import com.linki.admin_integration_service.entity.Proposal;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProposalRepository extends JpaRepository<Proposal, Long> {
}
