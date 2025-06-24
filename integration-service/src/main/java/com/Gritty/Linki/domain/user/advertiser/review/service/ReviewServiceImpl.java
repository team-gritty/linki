package com.Gritty.Linki.domain.user.advertiser.review.service;

import com.Gritty.Linki.config.security.CustomUserDetails;
import com.Gritty.Linki.domain.user.advertiser.review.dto.AdvertiserReviewDto;
import com.Gritty.Linki.domain.user.advertiser.review.dto.InfluencerReviewDto;
import com.Gritty.Linki.domain.user.advertiser.review.repository.jpa.AdvertiserReviewRepository;
import com.Gritty.Linki.domain.user.advertiser.review.repository.jpa.InfluencerReviewRepository;
import com.Gritty.Linki.domain.user.advertiser.review.request.ReviewWriteRequest;
import com.Gritty.Linki.entity.*;
import com.Gritty.Linki.util.AuthenticationUtil;
import com.Gritty.Linki.util.IdGenerator;
import com.Gritty.Linki.vo.enums.ContractStatus;
import com.Gritty.Linki.vo.enums.SettlementStatus;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import com.Gritty.Linki.exception.BusinessException;
import com.Gritty.Linki.exception.ErrorCode;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class ReviewServiceImpl implements ReviewService {

    private final AdvertiserReviewRepository advertiserReviewRepository;
    private final InfluencerReviewRepository influencerReviewRepository;
    private final AuthenticationUtil authenticationUtil;
    private final ModelMapper modelMapper;

    @PersistenceContext
    private EntityManager entityManager;

    /**
     * 광고주 자신이 받은 리뷰 목록 조회
     * 
     * @param user 인증된 사용자 정보
     * @return
     */
    @Override
    public List<AdvertiserReviewDto> getReceivedReviews(CustomUserDetails user) {
        // JWT에서 광고주 ID 추출
        String advertiserId = authenticationUtil.getAdvertiserIdFromUserDetails(user);
        log.info("광고주가 받은 리뷰 조회 요청: advertiserId={}", advertiserId);

        // 뽑은 광고주 ID로 받은 리뷰 조회
        List<AdvertiserReview> reviews = advertiserReviewRepository.findReceivedReviewsByAdvertiserId(advertiserId);
        return convertAdvertiserReviewsToDto(reviews);
    }

    /**
     * 광고주가 작성한 리뷰 목록 조회
     * 
     * @param user 인증된 사용자 정보
     * @return
     */
    @Override
    public List<InfluencerReviewDto> getGivenReviews(CustomUserDetails user) {
        // JWT에서 광고주 ID 추출
        String advertiserId = authenticationUtil.getAdvertiserIdFromUserDetails(user);
        log.info("광고주가 작성한 리뷰 조회 요청: advertiserId={}", advertiserId);

        List<InfluencerReview> reviews = influencerReviewRepository.findGivenReviewsByAdvertiserId(advertiserId);
        return convertInfluencerReviewsToDto(reviews);
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
    public InfluencerReview writeInfluencerReview(CustomUserDetails user, String contractId,
            ReviewWriteRequest request) {
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
        if (contract.getContractStatus() != ContractStatus.COMPLETED) {
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
                .influencerReviewId(IdGenerator.influencerReviewId())
                .influencerReviewScore(request.getReviewScore())
                .influencerReviewComment(request.getReviewComment())
                .influencerReviewCreatedAt(LocalDateTime.now())
                .visibility(true)
                .contract(contract)
                .build();

        influencerReviewRepository.save(review);
        log.info("인플루언서 리뷰 작성 완료: contractId={}, score={}", contractId, request.getReviewScore());

        return review;
    }

    @Override
    public List<InfluencerReviewDto> getInfluencerReviews(String influencerId) {
        log.info("특정 인플루언서가 받은 리뷰 조회 시작: influencerId={}", influencerId);

        // 입력값 검증
        if (influencerId == null || influencerId.trim().isEmpty()) {
            log.error("인플루언서 ID가 null이거나 빈 값입니다.");
            throw new IllegalArgumentException("인플루언서 ID는 필수값입니다.");
        }

        try {
            // 인플루언서 정보 조회
            log.debug("인플루언서 정보 조회 시작: influencerId={}", influencerId);

            // CHN- 접두사가 있으면 채널 ID로 처리
            boolean isChannelId = influencerId.startsWith("CHN-");

            if (isChannelId) {
                log.info("채널 ID로 인식됨: channelId={}", influencerId);

                // 채널 존재 여부 확인
                Long channelCount = entityManager.createQuery(
                        "SELECT COUNT(c) FROM Channel c WHERE c.channelId = :channelId", Long.class)
                        .setParameter("channelId", influencerId)
                        .getSingleResult();

                if (channelCount == 0) {
                    log.warn("존재하지 않는 채널입니다: channelId={}", influencerId);
                    throw new BusinessException(ErrorCode.ENTITY_NOT_FOUND, "존재하지 않는 채널입니다");
                }

                log.info("채널 정보 조회 완료: channelId={}, exists=true", influencerId);

                // 채널 ID로 리뷰 조회
                List<InfluencerReview> reviews;
                try {
                    reviews = influencerReviewRepository.findByChannelId(influencerId);
                    log.info("채널 ID로 리뷰 조회 완료: channelId={}, reviewCount={}", influencerId, reviews.size());
                } catch (Exception e) {
                    log.error("채널 ID로 리뷰 조회 실패: channelId={}, error={}", influencerId, e.getMessage());
                    throw new BusinessException(ErrorCode.INTERNAL_SERVER_ERROR, "리뷰 조회 중 오류가 발생했습니다");
                }

                return convertInfluencerReviewsToDto(reviews);

            } else {
                // 기존 인플루언서 ID 처리 로직
                // 먼저 JPQL로 인플루언서 존재 여부 확인
                Long influencerCount = entityManager.createQuery(
                        "SELECT COUNT(i) FROM Influencer i WHERE i.influencerId = :influencerId", Long.class)
                        .setParameter("influencerId", influencerId)
                        .getSingleResult();

                if (influencerCount == 0) {
                    log.warn("존재하지 않는 인플루언서입니다: influencerId={}", influencerId);
                    throw new BusinessException(ErrorCode.INFLUENCER_NOT_FOUND);
                }

                log.info("인플루언서 정보 조회 완료: influencerId={}, exists=true", influencerId);

                // 인플루언서가 받은 리뷰 조회 (먼저 간단한 쿼리로 시도)
                List<InfluencerReview> reviews;
                try {
                    reviews = influencerReviewRepository.findByInfluencerId(influencerId);
                    log.info("인플루언서가 받은 리뷰 조회 완료 (기본 쿼리): influencerId={}, reviewCount={}", influencerId,
                            reviews.size());
                } catch (Exception e) {
                    log.warn("기본 쿼리 실패, FETCH JOIN 쿼리로 재시도: influencerId={}, error={}", influencerId, e.getMessage());
                    try {
                        reviews = influencerReviewRepository.findByInfluencerIdWithFetch(influencerId);
                        log.info("인플루언서가 받은 리뷰 조회 완료 (FETCH 쿼리): influencerId={}, reviewCount={}", influencerId,
                                reviews.size());
                    } catch (Exception fetchError) {
                        log.error("FETCH JOIN 쿼리도 실패: influencerId={}, error={}", influencerId,
                                fetchError.getMessage());
                        throw new BusinessException(ErrorCode.INTERNAL_SERVER_ERROR, "리뷰 조회 중 오류가 발생했습니다");
                    }
                }

                return convertInfluencerReviewsToDto(reviews);
            }

        } catch (IllegalArgumentException e) {
            log.error("잘못된 요청: influencerId={}, error={}", influencerId, e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("인플루언서 리뷰 조회 중 오류 발생: influencerId={}, error={}", influencerId, e.getMessage(), e);
            throw new RuntimeException("인플루언서 리뷰 조회 중 오류가 발생했습니다: " + e.getMessage(), e);
        }
    }

    private List<AdvertiserReviewDto> convertAdvertiserReviewsToDto(List<AdvertiserReview> reviews) {
        return reviews.stream()
                .map(review -> {
                    AdvertiserReviewDto dto = modelMapper.map(review, AdvertiserReviewDto.class);
                    dto.setReviewId(review.getAdvertiserReviewId());
                    dto.setReviewScore(review.getAdvertiserReviewScore().intValue());
                    dto.setReviewComment(review.getAdvertiserReviewComment());
                    dto.setReviewCreatedAt(review.getAdvertiserReviewCreatedAt());
                    dto.setContractTitle(review.getContract().getContractTitle());
                    dto.setContractStartDate(review.getContract().getContractStartDate());
                    dto.setContractEndDate(review.getContract().getContractEndDate());
                    return dto;
                })
                .collect(Collectors.toList());
    }

    private List<InfluencerReviewDto> convertInfluencerReviewsToDto(List<InfluencerReview> reviews) {
        if (reviews == null || reviews.isEmpty()) {
            log.info("변환할 인플루언서 리뷰가 없습니다.");
            return List.of();
        }

        return reviews.stream()
                .map(review -> {
                    try {
                        if (review == null) {
                            log.warn("null인 리뷰가 발견되어 건너뜁니다.");
                            return null;
                        }

                        InfluencerReviewDto dto = modelMapper.map(review, InfluencerReviewDto.class);
                        dto.setReviewId(review.getInfluencerReviewId());
                        dto.setReviewScore(
                                review.getInfluencerReviewScore() != null ? review.getInfluencerReviewScore().intValue()
                                        : 0);
                        dto.setReviewComment(review.getInfluencerReviewComment());
                        dto.setReviewCreatedAt(review.getInfluencerReviewCreatedAt());
                        dto.setVisibility(review.getVisibility());

                        // Contract 정보 안전하게 처리
                        Contract contract = review.getContract();
                        if (contract != null) {
                            dto.setContractTitle(contract.getContractTitle());
                            dto.setContractStartDate(contract.getContractStartDate());
                            dto.setContractEndDate(contract.getContractEndDate());
                        } else {
                            log.warn("리뷰에 연결된 계약 정보가 없습니다: reviewId={}", review.getInfluencerReviewId());
                        }

                        return dto;
                    } catch (Exception e) {
                        log.error("리뷰 DTO 변환 중 오류 발생: reviewId={}, error={}",
                                review != null ? review.getInfluencerReviewId() : "null", e.getMessage(), e);
                        return null;
                    }
                })
                .filter(dto -> dto != null) // null인 dto 제거
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
            throw new BusinessException(ErrorCode.SETTLEMENT_LOOKUP_ERROR, "정산 상태 조회에 실패했습니다");
        }
    }
}