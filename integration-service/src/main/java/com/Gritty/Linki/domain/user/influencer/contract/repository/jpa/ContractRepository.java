package com.Gritty.Linki.domain.user.influencer.contract.repository.jpa;

import com.Gritty.Linki.entity.Contract;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ContractRepository extends JpaRepository<Contract,String> {
    Optional<Contract> findByProposalId(String proposalId);
}
