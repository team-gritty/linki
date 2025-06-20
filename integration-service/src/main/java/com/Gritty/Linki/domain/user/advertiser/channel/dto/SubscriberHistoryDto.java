package com.Gritty.Linki.domain.user.advertiser.channel.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

import java.time.LocalDateTime;

/**
 * 구독자 히스토리 데이터 전송 객체
 */
@Getter
@Builder
@ToString
public class SubscriberHistoryDto {

    private String id; // 구독자 히스토리 아이디
    private String channelId; // 채널 ID (String 형식)
    private Long subscriberCount;
    private LocalDateTime collectedAt;
}