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
         * 
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
         * 특정 인플루언서가 받은 리뷰 조회 (contract -> proposal -> influencer 경로로 조회)
         * 
         * @param influencerId 인플루언서 ID
         * @return 해당 인플루언서가 받은 리뷰 목록
         */
        @Query("SELECT ir FROM InfluencerReview ir " +
                        "JOIN ir.contract c " +
                        "JOIN c.proposal p " +
                        "JOIN p.influencer i " +
                        "WHERE i.influencerId = :influencerId " +
                        "AND ir.visibility = true " +
                        "ORDER BY ir.influencerReviewCreatedAt DESC")
        List<InfluencerReview> findByInfluencerId(@Param("influencerId") String influencerId);

        /**
         * 특정 인플루언서가 받은 리뷰 조회 (FETCH JOIN 사용)
         * 
         * @param influencerId 인플루언서 ID
         * @return 해당 인플루언서가 받은 리뷰 목록
         */
        @Query("SELECT ir FROM InfluencerReview ir " +
                        "JOIN FETCH ir.contract c " +
                        "JOIN FETCH c.proposal p " +
                        "JOIN FETCH p.influencer i " +
                        "WHERE i.influencerId = :influencerId " +
                        "AND ir.visibility = true " +
                        "ORDER BY ir.influencerReviewCreatedAt DESC")
        List<InfluencerReview> findByInfluencerIdWithFetch(@Param("influencerId") String influencerId);

        /**
         * 채널 ID로 인플루언서가 받은 리뷰 조회
         * Channel -> Influencer 관계를 이용
         * 
         * @param channelId 채널 ID
         * @return 해당 인플루언서가 받은 리뷰 목록
         */
        @Query("SELECT ir FROM InfluencerReview ir " +
                        "JOIN ir.contract c " +
                        "JOIN c.proposal p " +
                        "JOIN p.influencer i " +
                        "JOIN Channel ch ON ch.influencer.influencerId = i.influencerId " +
                        "WHERE ch.channelId = :channelId " +
                        "AND ir.visibility = true " +
                        "ORDER BY ir.influencerReviewCreatedAt DESC")
        List<InfluencerReview> findByChannelId(@Param("channelId") String channelId);
}