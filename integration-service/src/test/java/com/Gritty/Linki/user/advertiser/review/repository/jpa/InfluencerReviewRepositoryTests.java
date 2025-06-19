package com.Gritty.Linki.user.advertiser.review.repository.jpa;

import com.Gritty.Linki.domain.user.advertiser.review.repository.jpa.InfluencerReviewRepository;
import com.Gritty.Linki.entity.InfluencerReview;
import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@SpringBootTest
@Log4j2
@Transactional
public class InfluencerReviewRepositoryTests {

    String testAdvertiserId = "ADV0001";
    String testInfluencerId = "INF0001";

    @Autowired
    private InfluencerReviewRepository influencerReviewRepository;

    @Test
    @DisplayName("광고주가 작성한 리뷰 조회 Repository Test")
    public void findGivenReviewsByAdvertiserId() {
        log.info("광고주가 작성한 리뷰 조회 테스트 시작");

        List<InfluencerReview> reviews = influencerReviewRepository.findGivenReviewsByAdvertiserId(testAdvertiserId);
        log.info("조회된 리뷰 수: {}", reviews.size());

        for (InfluencerReview review : reviews) {
            log.info("조회된 리뷰: ID={}, 점수={}, 내용={}, 작성일={}",
                    review.getInfluencerReviewId(),
                    review.getInfluencerReviewScore(),
                    review.getInfluencerReviewComment(),
                    review.getInfluencerReviewCreatedAt());
        }
    }

    @Test
    @DisplayName("인플루언서가 받은 리뷰 조회 Repository Test")
    public void findByInfluencerId() {
        log.info("인플루언서가 받은 리뷰 조회 테스트 시작");

        List<InfluencerReview> reviews = influencerReviewRepository.findByInfluencerId(testInfluencerId);
        log.info("조회된 리뷰 수: {}", reviews.size());

        for (InfluencerReview review : reviews) {
            log.info("조회된 리뷰: ID={}, 점수={}, 내용={}, 작성일={}",
                    review.getInfluencerReviewId(),
                    review.getInfluencerReviewScore(),
                    review.getInfluencerReviewComment(),
                    review.getInfluencerReviewCreatedAt());
        }
    }
}