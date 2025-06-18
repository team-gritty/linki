package com.Gritty.Linki.domain.user.advertiser.review.service;

import com.Gritty.Linki.config.security.CustomUserDetails;
import com.Gritty.Linki.domain.user.advertiser.review.repository.jpa.AdvertiserReviewRepository;
import com.Gritty.Linki.domain.user.advertiser.review.repository.jpa.InfluencerReviewRepository;
import com.Gritty.Linki.domain.user.advertiser.review.request.ReviewWriteRequest;
import com.Gritty.Linki.domain.user.advertiser.review.response.InfluencerReviewRes;
import com.Gritty.Linki.domain.user.advertiser.review.response.ReceivedReviewResponse;
import com.Gritty.Linki.domain.user.advertiser.review.response.GivenReviewResponse;
import com.Gritty.Linki.entity.*;
import com.Gritty.Linki.util.AuthenticationUtil;
import com.Gritty.Linki.vo.enums.ContractStatus;
import com.Gritty.Linki.vo.enums.SettlementStatus;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class ReviewServiceImpl implements ReviewService {

    private final AdvertiserReviewRepository advertiserReviewRepository;
    private final InfluencerReviewRepository influencerReviewRepository;
    private final AuthenticationUtil authenticationUtil;

    @PersistenceContext
    private EntityManager entityManager;

    /**
     * 광고주 자신이 받은 리뷰 목록 조회
     * 
     * @param user 인증된 사용자 정보
     * @return
     */
    @Override
    public List<ReceivedReviewResponse> getReceivedReviews(CustomUserDetails user) {
        // JWT에서 광고주 ID 추출
        String advertiserId = authenticationUtil.getAdvertiserIdFromUserDetails(user);
        log.info("광고주가 받은 리뷰 조회 요청: advertiserId={}", advertiserId);

        // 뽑은 광고주 ID로 받은 리뷰 조회
        List<AdvertiserReview> reviews = advertiserReviewRepository.findReceivedReviewsByAdvertiserId(advertiserId);
        return convertAdvertiserReviewsToResponse(reviews);
    }

    /**
     * 광고주가 작성한 리뷰 목록 조회
     * @param user 인증된 사용자 정보
     * @return
     */
    @Override
    public List<GivenReviewResponse> getGivenReviews(CustomUserDetails user) {
        // JWT에서 광고주 ID 추출
        String advertiserId = authenticationUtil.getAdvertiserIdFromUserDetails(user);
        log.info("광고주가 작성한 리뷰 조회 요청: advertiserId={}", advertiserId);

        List<InfluencerReview> reviews = influencerReviewRepository.findGivenReviewsByAdvertiserId(advertiserId);
        return convertInfluencerReviewsToResponse(reviews);
    }

    /**
     * 계약이 끝나고 계약에 대한 리뷰 작성 - 인플에 대한 리뷰
     * 
     * @param user       인증된 사용자 정보
     * @param contractId 계약 ID
     * @param request    리뷰 작성 요청
     */
    @Override
    @Transactional
    public void writeInfluencerReview(CustomUserDetails user, String contractId, ReviewWriteRequest request) {
        // JWT에서 광고주 ID 추출
        String advertiserId = authenticationUtil.getAdvertiserIdFromUserDetails(user);
        log.info("인플루언서 리뷰 작성 요청: advertiserId={}, contractId={}", advertiserId, contractId);

        // 계약 조회
        Contract contract = entityManager.find(Contract.class, contractId);
        if (contract == null) {
            throw new IllegalArgumentException("존재하지 않는 계약입니다.");
        }

        // 광고주 권한 확인 (계약의 proposal을 통해 확인)
        if (!contract.getProposal().getCampaign().getAdvertiser().getAdvertiserId().equals(advertiserId)) {
            throw new IllegalArgumentException("해당 계약에 대한 권한이 없습니다.");
        }

        // 계약이 완료된 상태인지 확인 (리뷰는 완료된 계약에 대해서만 작성 가능)
        if (contract.getContractStatus() != ContractStatus.CONFIRMED) {
            throw new IllegalArgumentException("완료된 계약에 대해서만 리뷰를 작성할 수 있습니다.");
        }

        // 중복 리뷰 확인
        List<InfluencerReview> existingReviews = influencerReviewRepository
                .findGivenReviewsByAdvertiserId(advertiserId);
        boolean alreadyReviewed = existingReviews.stream()
                .anyMatch(review -> review.getContract().getContractId().equals(contractId));

        if (alreadyReviewed) {
            throw new IllegalArgumentException("이미 해당 계약에 대한 리뷰를 작성하셨습니다.");
        }

        // 리뷰 생성
        InfluencerReview review = InfluencerReview.builder()
                .influencerReviewId(UUID.randomUUID().toString())
                .influencerReviewScore(request.getReviewScore())
                .influencerReviewComment(request.getReviewComment())
                .influencerReviewCreatedAt(LocalDateTime.now())
                .visibility(true)
                .contract(contract)
                .build();

        influencerReviewRepository.save(review);
        log.info("인플루언서 리뷰 작성 완료: contractId={}, score={}", contractId, request.getReviewScore());
    }

    @Override
    public List<InfluencerReviewRes> getInfluencerReviews(String influencerId) {
        // 인플루언서 정보 조회
        Influencer influencer = entityManager.find(Influencer.class, influencerId);
        if (influencer == null) {
            throw new IllegalArgumentException("존재하지 않는 인플루언서입니다.");
        }

        // 인플루언서가 받은 리뷰 조회
        List<InfluencerReview> reviews = influencerReviewRepository.findByInfluencerId(influencerId);
        return convertInfluencerReviewsToInfluencerReviewRes(reviews);
    }

    private List<ReceivedReviewResponse> convertAdvertiserReviewsToResponse(List<AdvertiserReview> reviews) {
        return reviews.stream()
                .map(review -> ReceivedReviewResponse.builder()
                        .reviewId(review.getAdvertiserReviewId())
                        .reviewScore(review.getAdvertiserReviewScore())
                        .reviewComment(review.getAdvertiserReviewComment())
                        .reviewCreatedAt(review.getAdvertiserReviewCreatedAt())
                        .visibility(review.getVisibility())
                        .contractTitle(review.getContract().getContractTitle())
                        .contractStartDate(review.getContract().getContractStartDate())
                        .contractEndDate(review.getContract().getContractEndDate())
                        .build())
                .collect(Collectors.toList());
    }

    private List<GivenReviewResponse> convertInfluencerReviewsToResponse(List<InfluencerReview> reviews) {
        return reviews.stream()
                .map(review -> GivenReviewResponse.builder()
                        .reviewId(review.getInfluencerReviewId())
                        .reviewScore(review.getInfluencerReviewScore())
                        .reviewComment(review.getInfluencerReviewComment())
                        .reviewCreatedAt(review.getInfluencerReviewCreatedAt())
                        .visibility(review.getVisibility())
                        .contractTitle(review.getContract().getContractTitle())
                        .contractStartDate(review.getContract().getContractStartDate())
                        .contractEndDate(review.getContract().getContractEndDate())
                        .build())
                .collect(Collectors.toList());
    }

    private List<InfluencerReviewRes> convertInfluencerReviewsToInfluencerReviewRes(List<InfluencerReview> reviews) {
        return reviews.stream()
                .map(review -> InfluencerReviewRes.builder()
                        .reviewId(review.getInfluencerReviewId())
                        .reviewScore(review.getInfluencerReviewScore())
                        .reviewComment(review.getInfluencerReviewComment())
                        .reviewCreatedAt(review.getInfluencerReviewCreatedAt())
                        .visibility(review.getVisibility())
                        .contractTitle(review.getContract().getContractTitle())
                        .contractStartDate(review.getContract().getContractStartDate())
                        .contractEndDate(review.getContract().getContractEndDate())
                        .build())
                .collect(Collectors.toList());
    }

    private String getSettlementStatus(Contract contract) {
        // Settlement 엔티티에서 해당 계약의 정산 상태 조회
        try {
            Settlement settlement = entityManager.createQuery(
                    "SELECT s FROM Settlement s WHERE s.contract.contractId = :contractId", Settlement.class)
                    .setParameter("contractId", contract.getContractId())
                    .getSingleResult();
            return settlement.getSettlementStatus().name();
        } catch (Exception e) {
            return SettlementStatus.PENDING.name(); // 기본값
        }
    }
}