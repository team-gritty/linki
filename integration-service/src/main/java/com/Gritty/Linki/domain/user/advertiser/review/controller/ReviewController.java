package com.Gritty.Linki.domain.user.advertiser.review.controller;

import com.Gritty.Linki.config.security.CustomUserDetails;
import com.Gritty.Linki.domain.user.advertiser.review.request.ReviewWriteRequest;
import com.Gritty.Linki.domain.user.advertiser.review.response.InfluencerReviewRes;
import com.Gritty.Linki.domain.user.advertiser.review.response.ReceivedReviewResponse;
import com.Gritty.Linki.domain.user.advertiser.review.response.GivenReviewResponse;
import com.Gritty.Linki.domain.user.advertiser.review.service.ReviewService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal; // Spring Security dependency 필요
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/v1/api")
public class ReviewController {

    private final ReviewService reviewService;

    /**
     * 광고주가 받은 리뷰 조회
     */
    @GetMapping("/advertiser/mypage/reviews/received")
    public ResponseEntity<List<ReceivedReviewResponse>> getReceivedReview(
            @AuthenticationPrincipal CustomUserDetails user) {
        log.info("광고주가 받은 리뷰 조회 요청: user={}", user.getUserId());

        List<ReceivedReviewResponse> response = reviewService.getReceivedReviews(user);
        return ResponseEntity.ok(response);
    }

    /**
     * 광고주가 작성한 리뷰 조회
     */
    @GetMapping("/advertiser/mypage/reviews/given")
    public ResponseEntity<List<GivenReviewResponse>> getGivenReview(
            @AuthenticationPrincipal  CustomUserDetails user) {
        log.info("광고주가 작성한 리뷰 조회 요청: user={}", user.getUserId());

        List<GivenReviewResponse> response = reviewService.getGivenReviews(user);
        return ResponseEntity.ok(response);
    }

    /**
     * 인플루언서에 대한 리뷰 작성
     * 명세서에 따라 POST /v1/api/advertiser/mypage/reviews/ 엔드포인트 사용
     * contractId는 request parameter로 받음
     */
    @PostMapping("/advertiser/mypage/reviews")
    public ResponseEntity<Void> writeInfluencerReview(
            @AuthenticationPrincipal CustomUserDetails user,
            @Valid @RequestBody ReviewWriteRequest request,
            @RequestParam String contractId) {
        log.info("인플루언서 리뷰 작성 요청: user={}, contractId={}",
                user.getUserId(), contractId);

        reviewService.writeInfluencerReview(user, contractId, request);
        return ResponseEntity.ok().build();
    }

    /**
     * 특정 인플루언서의 리뷰 조회 (일반회원도 조회 가능)
     */
    @GetMapping("/user/influencers/{influencerId}/reviews")
    public ResponseEntity<List<InfluencerReviewRes>> getInfluencerReviews(
            @PathVariable String influencerId) {
        log.info("특정 인플루언서 리뷰 조회 요청: influencerId={}", influencerId);

        List<InfluencerReviewRes> response = reviewService.getInfluencerReviews(influencerId);
        return ResponseEntity.ok(response);
    }
}