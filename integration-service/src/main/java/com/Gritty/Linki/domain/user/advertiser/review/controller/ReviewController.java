package com.Gritty.Linki.domain.user.advertiser.review.controller;

import com.Gritty.Linki.config.security.CustomUserDetails;
import com.Gritty.Linki.domain.user.advertiser.review.dto.AdvertiserReviewDto;
import com.Gritty.Linki.domain.user.advertiser.review.dto.InfluencerReviewDto;
import com.Gritty.Linki.domain.user.advertiser.review.request.ReviewWriteRequest;
import com.Gritty.Linki.domain.user.advertiser.review.response.InfluencerReviewRes;
import com.Gritty.Linki.domain.user.advertiser.review.response.ReceivedReviewResponse;
import com.Gritty.Linki.domain.user.advertiser.review.response.GivenReviewResponse;
import com.Gritty.Linki.domain.user.advertiser.review.service.ReviewService;
import com.Gritty.Linki.entity.InfluencerReview;
import com.Gritty.Linki.util.AuthenticationUtil;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/v1/api")
public class ReviewController {

    private final ReviewService reviewService;
    private final ModelMapper modelMapper;
    private final AuthenticationUtil authenticationUtil;

    /**
     * 광고주가 받은 리뷰 조회
     */
    @GetMapping("/advertiser/mypage/reviews/received")
    public ResponseEntity<List<ReceivedReviewResponse>> getReceivedReview(
            @AuthenticationPrincipal CustomUserDetails user) {
        log.info("광고주가 받은 리뷰 조회 요청: user={}", user.getUserId());

        List<AdvertiserReviewDto> dtos = reviewService.getReceivedReviews(user);
        List<ReceivedReviewResponse> response = dtos.stream()
                .map(dto -> modelMapper.map(dto, ReceivedReviewResponse.class))
                .collect(Collectors.toList());

        return ResponseEntity.ok(response);
    }

    /**
     * 광고주가 작성한 리뷰 조회
     */
    @GetMapping("/advertiser/mypage/reviews/given")
    public ResponseEntity<List<GivenReviewResponse>> getGivenReview(
            @AuthenticationPrincipal CustomUserDetails user) {
        log.info("광고주가 작성한 리뷰 조회 요청: user={}", user.getUserId());

        List<InfluencerReviewDto> dtos = reviewService.getGivenReviews(user);
        List<GivenReviewResponse> response = dtos.stream()
                .map(dto -> modelMapper.map(dto, GivenReviewResponse.class))
                .collect(Collectors.toList());

        return ResponseEntity.ok(response);
    }

    /**
     * 계약서 에 대한 리뷰 작성
     * 명세서에 따라 POST /v1/api/advertiser/mypage/reviews/ 엔드포인트 사용
     * contractId는 request parameter로 받음
     */
    @PostMapping("/advertiser/mypage/reviews/{contractId}")
    public ResponseEntity<InfluencerReviewRes> writeInfluencerReview(
            @Valid @RequestBody ReviewWriteRequest request,
            @PathVariable String contractId,
            @AuthenticationPrincipal CustomUserDetails user) {

        log.info("인플루언서 리뷰 작성 요청됨: user={}, contractId={}", user.getUserId(), contractId);



        InfluencerReview review = reviewService.writeInfluencerReview(user, contractId, request);

        // ModelMapper를 사용해서 엔티티를 Response로 변환
        InfluencerReviewRes response = modelMapper.map(review, InfluencerReviewRes.class);
        // 필드 매핑 조정 (엔티티와 Response 필드명이 다른 경우)
        response.setReviewId(review.getInfluencerReviewId());
        response.setReviewScore(review.getInfluencerReviewScore());
        response.setReviewComment(review.getInfluencerReviewComment());
        response.setReviewCreatedAt(review.getInfluencerReviewCreatedAt());
        response.setContractTitle(review.getContract().getContractTitle());
        response.setContractStartDate(review.getContract().getContractStartDate());
        response.setContractEndDate(review.getContract().getContractEndDate());

        return ResponseEntity.ok(response);
    }

    /**
     * 특정 인플루언서가 받은 리뷰 조회 (일반회원도 조회 가능)
     * 연관관계: InfluencerReview -> Contract -> Proposal -> Influencer
     */
    @GetMapping("/user/influencers/{influencerId}/reviews")
    public ResponseEntity<List<InfluencerReviewRes>> getInfluencerReviews(
            @PathVariable String influencerId) {
        log.info("특정 인플루언서가 받은 리뷰 조회 요청: influencerId={}", influencerId);

        try {
            List<InfluencerReviewDto> dtos = reviewService.getInfluencerReviews(influencerId);
            List<InfluencerReviewRes> response = dtos.stream()
                    .map(dto -> {
                        InfluencerReviewRes res = modelMapper.map(dto, InfluencerReviewRes.class);
                        // 필드 매핑 확인
                        res.setReviewId(dto.getReviewId());
                        res.setReviewScore(dto.getReviewScore() != null ? BigDecimal.valueOf(dto.getReviewScore())
                                : BigDecimal.ZERO);
                        res.setReviewComment(dto.getReviewComment());
                        res.setReviewCreatedAt(dto.getReviewCreatedAt());
                        res.setVisibility(dto.getVisibility());
                        res.setContractTitle(dto.getContractTitle());
                        res.setContractStartDate(dto.getContractStartDate());
                        res.setContractEndDate(dto.getContractEndDate());
                        return res;
                    })
                    .collect(Collectors.toList());

            log.info("특정 인플루언서 리뷰 조회 완료: influencerId={}, reviewCount={}", influencerId, response.size());
            return ResponseEntity.ok(response);

        } catch (IllegalArgumentException e) {
            log.error("잘못된 요청: influencerId={}, error={}", influencerId, e.getMessage(), e);
            return ResponseEntity.badRequest().build();

        } catch (Exception e) {
            log.error("인플루언서 리뷰 조회 중 서버 오류: influencerId={}, error={}", influencerId, e.getMessage(), e);
            return ResponseEntity.internalServerError().build();
        }
    }

}