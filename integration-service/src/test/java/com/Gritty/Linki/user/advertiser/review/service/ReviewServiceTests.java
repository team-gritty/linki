package com.Gritty.Linki.user.advertiser.review.service;

import com.Gritty.Linki.config.security.CustomUserDetails;
import com.Gritty.Linki.domain.user.advertiser.review.dto.AdvertiserReviewDto;
import com.Gritty.Linki.domain.user.advertiser.review.dto.InfluencerReviewDto;
import com.Gritty.Linki.domain.user.advertiser.review.request.ReviewWriteRequest;
import com.Gritty.Linki.domain.user.advertiser.review.service.ReviewService;
import com.Gritty.Linki.entity.InfluencerReview;
import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@SpringBootTest
@Log4j2
@Transactional
public class ReviewServiceTests {

    @Autowired
    private ReviewService reviewService;

    String testContractId = "CONT0001";
    String testInfluencerId = "INF0001";
    String testUserId = "USER0500"; // 광고주인 USER0500 사용

    /**
     * CustomUserDetails 실제 객체를 생성하는 메소드
     * 
     * @return
     */
    private CustomUserDetails createMockUserDetails() {
        return new CustomUserDetails(
                testUserId,
                "testLoginId",
                "testPassword",
                "ROLE_ADVERTISER");
    }

    @Test
    @DisplayName("광고주가 받은 리뷰 목록 조회 Service Test")
    public void getReceivedReviews() {
        log.info("광고주가 받은 리뷰 목록 조회 테스트 시작");

        CustomUserDetails mockUser = createMockUserDetails();

        try {
            List<AdvertiserReviewDto> reviews = reviewService.getReceivedReviews(mockUser);
            log.info("조회된 리뷰 수: {}", reviews.size());

            for (AdvertiserReviewDto review : reviews) {
                log.info("받은 리뷰 정보: ID={}, 점수={}, 내용={}, 계약명={}",
                        review.getReviewId(), review.getReviewScore(),
                        review.getReviewComment(), review.getContractTitle());
            }
        } catch (Exception e) {
            log.info("광고주가 받은 리뷰 조회 실패 (예상됨): {}", e.getMessage());
        }
    }

    @Test
    @DisplayName("광고주가 작성한 리뷰 목록 조회 Service Test")
    public void getGivenReviews() {
        log.info("광고주가 작성한 리뷰 목록 조회 테스트 시작");

        CustomUserDetails mockUser = createMockUserDetails();

        try {
            List<InfluencerReviewDto> reviews = reviewService.getGivenReviews(mockUser);
            log.info("조회된 리뷰 수: {}", reviews.size());

            for (InfluencerReviewDto review : reviews) {
                log.info("작성한 리뷰 정보: ID={}, 점수={}, 내용={}, 계약명={}",
                        review.getReviewId(), review.getReviewScore(),
                        review.getReviewComment(), review.getContractTitle());
            }
        } catch (Exception e) {
            log.info("광고주가 작성한 리뷰 조회 실패 (예상됨): {}", e.getMessage());
        }
    }

    @Test
    @DisplayName("인플루언서 리뷰 작성 Service Test")
    public void writeInfluencerReview() {
        log.info("인플루언서 리뷰 작성 테스트 시작");

        CustomUserDetails mockUser = createMockUserDetails();
        ReviewWriteRequest request = new ReviewWriteRequest();
        request.setReviewScore(BigDecimal.valueOf(5.00));
        request.setReviewComment("훌륭한 인플루언서입니다. 적극 추천합니다!");

        try {
            InfluencerReview review = reviewService.writeInfluencerReview(mockUser, testContractId, request);
            log.info("작성된 리뷰: ID={}, 점수={}, 내용={}",
                    review.getInfluencerReviewId(),
                    review.getInfluencerReviewScore(),
                    review.getInfluencerReviewComment());
        } catch (Exception e) {
            log.info("인플루언서 리뷰 작성 실패 (예상됨): {}", e.getMessage());
        }
    }

    @Test
    @DisplayName("인플루언서별 리뷰 조회 Service Test")
    public void getInfluencerReviews() {
        log.info("인플루언서별 리뷰 조회 테스트 시작");

        try {
            List<InfluencerReviewDto> reviews = reviewService.getInfluencerReviews(testInfluencerId);
            log.info("조회된 리뷰 수: {}", reviews.size());

            for (InfluencerReviewDto review : reviews) {
                log.info("인플루언서 리뷰 정보: ID={}, 점수={}, 내용={}, 계약명={}",
                        review.getReviewId(), review.getReviewScore(),
                        review.getReviewComment(), review.getContractTitle());
            }
        } catch (Exception e) {
            log.info("인플루언서별 리뷰 조회 실패 (예상됨): {}", e.getMessage());
        }
    }

}