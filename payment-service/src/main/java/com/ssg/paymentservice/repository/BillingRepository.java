package com.ssg.paymentservice.repository;

import com.ssg.paymentservice.common.entity.BillingEntity;
import org.springframework.data.domain.Limit;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface BillingRepository extends JpaRepository<BillingEntity, String> {
    //userid 로 billing 객체 얻어옴
    Optional<BillingEntity> findByUserId(String userId);

    //마지막 결제일이 지난 billing 객체 리스트 가져옴
    List<BillingEntity> findByAutoBillingActivatedIsTrueAndNextBillingAtBefore(LocalDateTime now);
}
