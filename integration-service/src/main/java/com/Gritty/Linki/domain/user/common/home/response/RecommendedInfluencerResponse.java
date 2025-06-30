package com.Gritty.Linki.domain.user.common.home.response;

import com.Gritty.Linki.domain.user.common.home.dto.RecommendedInfluencerDTO;
import lombok.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RecommendedInfluencerResponse {

    // 추천 인플루언서 카드 리스트
    private List<InfluencerCard> influencerCards;
    private int totalCount; // 추천 인플루언서 카드 개수
    private String period; // 이번 주

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class InfluencerCard {
        private String influencerId;
        private String channelId;
        private String influencerName;
        private String category; // 카테고리명 (여행, 푸드/맛집, 패션, 스포츠 등)
        private BigDecimal rating; // 평점 (1-5점)
        private Long reviewCount; // 리뷰 개수
        private String subscriberDisplay; // 구독자 수 표시용 (예: "2.1만명")
        private String thumbnailUrl; // 채널 썸네일 URL
        private String channelUrl; // 채널 URL
    }

    /**
     * 개별 카드 기반으로 추천 인플루언서 카드 리스트 생성
     * @param influencers
     * 
     * @return
     */
    public static RecommendedInfluencerResponse of(List<RecommendedInfluencerDTO> influencers) {
        List<InfluencerCard> cards = influencers.stream()
                .map(RecommendedInfluencerResponse::createCard)
                .collect(Collectors.toList());

        return RecommendedInfluencerResponse.builder()
                .influencerCards(cards)
                .totalCount(influencers.size())
                .period("이번 주")
                .build();
    }

    /**
     * 추천 인플루언서 카드를 dto 기반으로 생성
     * @param dto
     * 
     * @return
     */
    private static InfluencerCard createCard(RecommendedInfluencerDTO dto) {
        return InfluencerCard.builder()
                .influencerId(dto.getInfluencerId())
                .channelId(dto.getChannelId())
                .influencerName(dto.getInfluencerName())
                .category(dto.getChannelCategory() != null ? dto.getChannelCategory() : "일반")
                .rating(dto.getDisplayReviewScore() != null ? dto.getDisplayReviewScore() : BigDecimal.valueOf(0.0))
                .reviewCount(dto.getReviewCount() != null ? dto.getReviewCount() : 0L)
                .subscriberDisplay(formatSubscriberCount(dto.getSubscriberCount()))
                .thumbnailUrl(dto.getChannelThumbnailUrl())
                .channelUrl(dto.getChannelUrl())
                .build();
    }

    /**
     * 구독자 수 읽기 편하게 포맷팅
     * @param subscriberCount
     * 
     * @return
     */
    private static String formatSubscriberCount(Long subscriberCount) {
        if (subscriberCount == null || subscriberCount == 0) {
            return "0명";
        }

        if (subscriberCount >= 10000) {
            double formatted = subscriberCount / 10000.0;
            return String.format("%.1f만명", formatted);
        } else if (subscriberCount >= 1000) {
            double formatted = subscriberCount / 1000.0;
            return String.format("%.1f천명", formatted);
        } else {
            return subscriberCount + "명";
        }
    }
}