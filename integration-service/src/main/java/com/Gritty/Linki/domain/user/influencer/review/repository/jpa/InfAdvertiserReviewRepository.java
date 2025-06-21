package com.Gritty.Linki.domain.user.influencer.review.repository.jpa;

import com.Gritty.Linki.entity.AdvertiserReview;
import org.springframework.data.jpa.repository.JpaRepository;

public interface InfAdvertiserReviewRepository extends JpaRepository<AdvertiserReview, String> {
    // 계약 ID로 리뷰가 존재하는지 확인
    boolean existsByContract_ContractId(String contractId);

}
