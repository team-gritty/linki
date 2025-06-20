package com.Gritty.Linki.domain.user.influencer.contract.repository.jpa;

import com.Gritty.Linki.entity.Contract;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ContractRepository extends JpaRepository<Contract,String> {
}
