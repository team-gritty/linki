package com.Gritty.Linki.domain.user.advertiser.channel.response;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Setter;

import java.util.List;

/**
 * 채널 목록 페이지네이션 응답 DTO
 * 검색된 채널 목록과 페이지네이션 정보를 포함
 */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChannelPageResponse {

    /**
     * 채널 목록
     */
    private List<ChannelListResponse> channels;

    /**
     * 페이지네이션 정보
     */
    private PageInfo pageInfo;

    @Getter
    @Setter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PageInfo {
        /**
         * 현재 페이지 번호 (0부터 시작)
         */
        private int currentPage;

        /**
         * 페이지 크기
         */
        private int pageSize;

        /**
         * 총 요소 수
         */
        private long totalElements;

        /**
         * 총 페이지 수
         */
        private int totalPages;

        /**
         * 첫 번째 페이지 여부
         */
        private boolean first;

        /**
         * 마지막 페이지 여부
         */
        private boolean last;

        /**
         * 다음 페이지 존재 여부
         */
        private boolean hasNext;

        /**
         * 이전 페이지 존재 여부
         */
        private boolean hasPrevious;
    }
}