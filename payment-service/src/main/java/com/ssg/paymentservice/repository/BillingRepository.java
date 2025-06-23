package com.ssg.paymentservice.repository;

import com.ssg.paymentservice.common.entity.BillingEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BillingRepository extends JpaRepository<BillingEntity, String> {
}
