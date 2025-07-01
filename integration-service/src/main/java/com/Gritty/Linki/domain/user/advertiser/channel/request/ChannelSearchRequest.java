package com.Gritty.Linki.domain.user.advertiser.channel.request;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Setter;

/**
 * 채널 검색 요청 DTO
 * 검색 필터링을 위한 다양한 조건들을 포함
 */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChannelSearchRequest {

    /**
     * 검색 키워드 (선택)
     */
    private String keyword;

    /**
     * 카테고리 (선택)
     */
    private String category;

    /**
     * 최소 구독자 수 (기본값: 0)
     */
    private long minSubscribers;

    /**
     * 최대 구독자 수 (기본값: Long.MAX_VALUE)
     */
    private long maxSubscribers;

    /**
     * 최소 평균 조회수 (기본값: 0)
     */
    private long minAvgViewCount;

    /**
     * 최대 평균 조회수 (기본값: Long.MAX_VALUE)
     */
    private long maxAvgViewCount;

    /**
     * 페이지 번호 (기본값: 0)
     */
    private int page;

    /**
     * 페이지 크기 (기본값: 10)
     */
    private int limit;

    /**
     * 정렬 기준 (subscriberCount, avgViewCount 등)
     */
    private String sortBy;

    /**
     * 정렬 방향 (desc, asc)
     */
    private String sortDirection;
}