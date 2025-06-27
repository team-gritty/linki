package com.linki.admin_integration_service.domain.payment.repository.jpa;

import com.linki.admin_integration_service.entity.Settlement;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SettlementRepository extends JpaRepository<Settlement, String> {
    Settlement findByContract_ContractId(String contractId);
}
