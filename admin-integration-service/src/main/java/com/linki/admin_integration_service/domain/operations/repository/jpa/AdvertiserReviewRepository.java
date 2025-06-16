package com.linki.admin_integration_service.domain.operations.repository.jpa;

import com.linki.admin_integration_service.entity.AdvertiserReview;
import com.linki.admin_integration_service.entity.InfluencerReview;
import org.springframework.data.jpa.repository.JpaRepository;


public interface AdvertiserReviewRepository extends JpaRepository<AdvertiserReview, String> {
}