package com.ssg.paymentservice.repository;

import com.ssg.paymentservice.common.entity.PaymentHistoryEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PaymentHistoryRepository extends JpaRepository<PaymentHistoryEntity, String> {
}
