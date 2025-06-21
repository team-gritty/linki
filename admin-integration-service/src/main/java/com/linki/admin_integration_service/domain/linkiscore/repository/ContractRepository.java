package com.linki.admin_integration_service.domain.linkiscore.repository;

import com.linki.admin_integration_service.entity.Contract;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ContractRepository extends JpaRepository<Contract, String> {
    @Query("SELECT c FROM Contract c JOIN FETCH c.proposal p JOIN FETCH p.influencer")
    List<Contract> findAllWithProposalAndInfluencer();
}
