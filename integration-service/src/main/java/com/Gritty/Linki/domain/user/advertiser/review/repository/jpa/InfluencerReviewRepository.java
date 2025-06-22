package com.Gritty.Linki.domain.user.advertiser.review.repository.jpa;

import com.Gritty.Linki.entity.InfluencerReview;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface InfluencerReviewRepository extends JpaRepository<InfluencerReview, String> {

        /**
         * 특정 광고주가 인플루언서에 대해 작성한 리뷰 조회
         * @param advertiserId
         * @return
         */
        @Query("SELECT ir FROM InfluencerReview ir " +
                        "JOIN ir.contract c " +
                        "JOIN c.proposal p " +
                        "JOIN p.campaign cam " +
                        "WHERE cam.advertiser.advertiserId = :advertiserId " +
                        "AND ir.visibility = true")
        List<InfluencerReview> findGivenReviewsByAdvertiserId(@Param("advertiserId") String advertiserId);

        /**
         * 특정 인플루언서가 작성한 리뷰 조회
         * @param influencerId
         * @return
         */
        @Query("SELECT ir FROM InfluencerReview ir " +
                        "JOIN ir.contract c " +
                        "JOIN c.proposal p " +
                        "WHERE p.influencer.influencerId = :influencerId " +
                        "AND ir.visibility = true")
        List<InfluencerReview> findByInfluencerId(@Param("influencerId") String influencerId);
}