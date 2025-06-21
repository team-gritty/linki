package com.Gritty.Linki.domain.user.influencer.settlement.repository.jpa;


import com.Gritty.Linki.entity.Settlement;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface InfSettlementRepository extends JpaRepository<Settlement, String> {
    Optional<Settlement> findByContract_ContractId(String contractId);
}
