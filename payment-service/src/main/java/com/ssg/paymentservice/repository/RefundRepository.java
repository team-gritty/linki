package com.ssg.paymentservice.repository;

import com.ssg.paymentservice.common.entity.RefundEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RefundRepository extends JpaRepository<RefundEntity, String> {
}
