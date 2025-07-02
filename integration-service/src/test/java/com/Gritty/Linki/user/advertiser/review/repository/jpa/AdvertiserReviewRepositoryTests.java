package com.Gritty.Linki.user.advertiser.review.repository.jpa;

import com.Gritty.Linki.domain.user.advertiser.review.repository.jpa.AdvertiserReviewRepository;
import com.Gritty.Linki.entity.AdvertiserReview;
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
public class AdvertiserReviewRepositoryTests {

    String testAdvertiserId = "ADV-0000000000000000";

    @Autowired
    private AdvertiserReviewRepository advertiserReviewRepository;

    @Test
    @DisplayName("광고주가 받은 리뷰 조회 Repository Test")
    public void findReceivedReviewsByAdvertiserId() {
        log.info("광고주가 받은 리뷰 조회 테스트 시작");

        List<AdvertiserReview> reviews = advertiserReviewRepository.findReceivedReviewsByAdvertiserId(testAdvertiserId);
        log.info("조회된 리뷰 수: {}", reviews.size());

        for (AdvertiserReview review : reviews) {
            log.info("조회된 리뷰: ID={}, 점수={}, 내용={}, 작성일={}",
                    review.getAdvertiserReviewId(),
                    review.getAdvertiserReviewScore(),
                    review.getAdvertiserReviewComment(),
                    review.getAdvertiserReviewCreatedAt());
        }
    }
}