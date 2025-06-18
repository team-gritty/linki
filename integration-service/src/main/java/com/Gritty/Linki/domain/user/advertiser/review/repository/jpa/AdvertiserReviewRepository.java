package com.Gritty.Linki.domain.user.advertiser.review.repository.jpa;

import com.Gritty.Linki.entity.AdvertiserReview;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AdvertiserReviewRepository extends JpaRepository<AdvertiserReview, String> {

        /**
         * 특정 광고주가 자신이 받은 리뷰 조회 (광고주의 계약을 통해)
         * 
         * @param advertiserId
         * @return
         */
        @Query("SELECT ar FROM AdvertiserReview ar " +
                        "JOIN ar.contract c " +
                        "JOIN c.proposal p " +
                        "JOIN p.campaign cam " +
                        "WHERE cam.advertiser.advertiserId = :advertiserId " +
                        "AND ar.visibility = true")
        List<AdvertiserReview> findReceivedReviewsByAdvertiserId(@Param("advertiserId") String advertiserId);

}