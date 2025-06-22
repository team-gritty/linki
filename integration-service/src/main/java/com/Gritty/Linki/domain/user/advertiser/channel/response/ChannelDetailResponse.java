package com.Gritty.Linki.domain.user.advertiser.channel.response;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

/**
 * 채널 상세 정보 응답 DTO
 * 특정 채널의 상세 정보와 통계를 포함
 */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChannelDetailResponse {

    /**
     * 채널 ID
     */
    private String channelId;

    /**
     * 채널명
     */
    private String name;

    /**
     * 채널 카테고리
     */
    private String category;

    /**
     * 구독자 수
     */
    private long subscribers;

    /**
     * 채널 생성일
     */
    private LocalDateTime createdAt;

    /**
     * 총 영상 수
     */
    private int videoCount;

    /**
     * 영상별 평균 조회수
     */
    private long avgViewCount;

    /**
     * 영상별 평균 댓글 수
     */
    private long avgCommentCount;

    /**
     * 영상별 평균 좋아요 수
     */
    private long avgLikeCount;

    /**
     * 국가
     */
    private String country;

    /**
     * 채널 설명
     */
    private String description;

    /**
     * 채널 썸네일 URL
     */
    private String thumbnailUrl;

    /**
     * YouTube 채널 URL
     */
    private String youtubeUrl;

    /**
     * 채널 배너 URL
     */
    private String bannerUrl;
}