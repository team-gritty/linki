package com.Gritty.Linki.domain.user.influencer.contract.repository.jpa;

import com.Gritty.Linki.entity.Signature;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface SignatureRepository extends JpaRepository<Signature, String> {

    Signature findByContract_ContractId(String contractId);
}
