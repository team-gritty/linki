package com.Gritty.Linki.domain.user.common.home.service;

import com.Gritty.Linki.domain.user.common.home.dto.RecommendedInfluencerDTO;
import com.Gritty.Linki.domain.user.common.home.repository.LinkiScoreRepository;
import com.Gritty.Linki.domain.user.advertiser.review.repository.jpa.InfluencerReviewRepository;
import com.Gritty.Linki.entity.Channel;
import com.Gritty.Linki.entity.Influencer;
import com.Gritty.Linki.entity.InfluencerReview;
import com.Gritty.Linki.entity.LinkiScore;
import com.Gritty.Linki.entity.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Slf4j
public class HomeRecommendationServiceImpl implements HomeRecommendationService {

    private final LinkiScoreRepository linkiScoreRepository;
    private final InfluencerReviewRepository influencerReviewRepository;

    @Override
    public List<RecommendedInfluencerDTO> getRecommendedInfluencers(int limit) {
        log.info("추천 인플루언서 조회 시작 - limit: {}", limit);

        try {
            // 먼저 유효한 점수가 있는지 확인
            long validScoreCount = linkiScoreRepository.countValidScores();
            log.info("유효한 LinkiScore 데이터 수: {}", validScoreCount);

            if (validScoreCount == 0) {
                log.warn("LinkiScore 데이터가 없습니다. 빈 목록을 반환합니다.");
                return new ArrayList<>();
            }

            Pageable pageable = PageRequest.of(0, limit);
            List<Object[]> results = linkiScoreRepository.findTopInfluencersByScore(pageable);

            if (results.isEmpty()) {
                log.warn("조회된 추천 인플루언서가 없습니다.");
                return new ArrayList<>();
            }

            List<RecommendedInfluencerDTO> recommendedInfluencers = results.stream()
                    .map(this::mapToRecommendedInfluencerDTO)
                    .filter(dto -> dto != null) // null 값 필터링
                    .collect(Collectors.toList());

            log.info("추천 인플루언서 조회 완료 - 조회된 수: {}", recommendedInfluencers.size());
            return recommendedInfluencers;

        } catch (Exception e) {
            log.error("추천 인플루언서 조회 중 오류 발생", e);
            return new ArrayList<>();
        }
    }

    /**
     * Object[] 결과를 RecommendedInfluencerDTO로 매핑
     * Object[] 순서: [LinkiScore, Influencer, User, Channel]
     */
    private RecommendedInfluencerDTO mapToRecommendedInfluencerDTO(Object[] result) {
        try {
            if (result == null || result.length < 3) {
                log.warn("유효하지 않은 결과 데이터입니다.");
                return null;
            }

            LinkiScore linkiScore = (LinkiScore) result[0];
            Influencer influencer = (Influencer) result[1];
            User user = (User) result[2];
            Channel channel = (Channel) (result.length > 3 ? result[3] : null); // null일 수 있음

            if (linkiScore == null || influencer == null || user == null) {
                log.warn("필수 데이터가 null입니다. influencer: {}, user: {}",
                        influencer != null ? influencer.getInfluencerId() : "null",
                        user != null ? user.getUserId() : "null");
                return null;
            }

            // 리뷰 정보 조회
            List<InfluencerReview> reviews = influencerReviewRepository
                    .findByInfluencerId(influencer.getInfluencerId());

            // 평점과 리뷰 개수 계산
            BigDecimal displayReviewScore = BigDecimal.ZERO;
            Long reviewCount = 0L;

            if (!reviews.isEmpty()) {
                reviewCount = (long) reviews.size();
                BigDecimal totalScore = reviews.stream()
                        .map(InfluencerReview::getInfluencerReviewScore)
                        .reduce(BigDecimal.ZERO, BigDecimal::add);
                displayReviewScore = totalScore.divide(BigDecimal.valueOf(reviewCount), 1, RoundingMode.HALF_UP);
            }

            return RecommendedInfluencerDTO.builder()
                    .influencerId(influencer.getInfluencerId())
                    .channelId(channel != null ? channel.getChannelId() : null)
                    .influencerName(user.getUserName())
                    .userEmail(user.getUserEmail())
                    .channelName(channel != null ? channel.getChannelName() : "채널 정보 없음")
                    .channelUrl(channel != null ? channel.getChannelUrl() : null)
                    .channelThumbnailUrl(channel != null ? channel.getChannelThumbnailUrl() : null)
                    .subscriberCount(channel != null ? channel.getSubscriberCount() : 0L)
                    .viewCount(channel != null ? channel.getViewCount() : 0L)
                    .channelCategory(channel != null ? channel.getChannelCategory() : "일반")
                    .channelDescription(channel != null ? channel.getChannelDescription() : null)
                    .totalScore(linkiScore.calculateTotalScore())
                    .costPerClickScore(
                            linkiScore.getCostPerClick() != null ? linkiScore.getCostPerClick() : BigDecimal.ZERO)
                    .dailyTrafficScore(
                            linkiScore.getDailyTraffic() != null ? linkiScore.getDailyTraffic() : BigDecimal.ZERO)
                    .averageReviewScore(linkiScore.getAverageReviewScore() != null ? linkiScore.getAverageReviewScore()
                            : BigDecimal.ZERO)
                    .contractCountScore(
                            linkiScore.getContractCount() != null ? linkiScore.getContractCount() : BigDecimal.ZERO)
                    // UI 표시용 리뷰 정보 추가
                    .displayReviewScore(displayReviewScore)
                    .reviewCount(reviewCount)
                    .build();

        } catch (Exception e) {
            log.error("DTO 매핑 중 오류 발생", e);
            return null;
        }
    }
}