package com.Gritty.Linki.domain.user.advertiser.channel.response;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

import java.time.LocalDateTime;

/**
 * 구독자 히스토리 응답 객체
 */
@Getter
@Builder
@ToString
public class SubscriberHistoryResponse {

    private String id; // 구독자 히스토리 아이디
    private String channelId; // 구독자 수집한 채널 아이디
    private Long subscriberCount; // 구독자 수
    private LocalDateTime collectedAt; // 구독자 데이터 수집 날짜
}