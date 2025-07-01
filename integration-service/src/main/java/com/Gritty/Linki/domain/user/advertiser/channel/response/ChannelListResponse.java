package com.Gritty.Linki.domain.user.advertiser.channel.response;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Setter;

/**
 * 채널 목록 응답 DTO
 * 검색된 채널의 기본 정보를 포함
 */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChannelListResponse {

    /**
     * 채널 ID
     */
    private String channelId;

    /**
     * 채널명
     */
    private String channelName;

    /**
     * 채널 썸네일 URL
     */
    private String thumbnailUrl;

    /**
     * 구독자 수
     */
    private long subscriberCount;

    /**
     * 평균 조회수
     */
    private long avgViewCount;

    /**
     * 평균 좋아요 수
     */
    private long avgLikeCount;

    /**
     * 평균 댓글 수
     */
    private long avgCommentCount;

    /**
     * 카테고리
     */
    private String category;

    /**
     * 채널 설명
     */
    private String description;
}