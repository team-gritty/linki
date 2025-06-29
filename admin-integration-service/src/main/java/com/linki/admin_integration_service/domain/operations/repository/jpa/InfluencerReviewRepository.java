package com.linki.admin_integration_service.domain.operations.repository.jpa;

import com.linki.admin_integration_service.entity.InfluencerReview;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;


public interface InfluencerReviewRepository extends JpaRepository<InfluencerReview, Long> {
    @Query("SELECT ir FROM InfluencerReview ir " +
            "JOIN FETCH ir.contract c " +
            "JOIN FETCH c.proposal p " +
            "JOIN FETCH p.influencer i")
    List<InfluencerReview> findAllWithJoin();
}