package com.Gritty.Linki.domain.user.advertiser.channel.repository;

import com.Gritty.Linki.domain.user.advertiser.channel.dto.SubscriberHistoryDto;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 구독자 히스토리 데이터 접근 인터페이스
 */
public interface SubscriberHistoryRepository {

    /**
     * 특정 채널의 구독자 히스토리들을 조회
     * 날짜와 구독자수 정보로 그래프 그리기 위한 것임
     *
     * @param channelId 채널 ID (String 형식)
     * @param startDate 조회 시작일
     * @return 구독자 히스토리 목록
     */
    List<SubscriberHistoryDto> findByChannelIdAndDateRange(String channelId, LocalDateTime startDate);

    /**
     * 구독자 히스토리 저장
     * 
     * @param subscriberHistoryDto 구독자 히스토리 정보
     * @return 저장된 구독자 히스토리 ID
     */
    String save(SubscriberHistoryDto subscriberHistoryDto);

    /**
     * 특정 번호 이상의 모든 채널 ID 조회 (1007번부터 - 실제 유튜브 데이터 수집한 채널 부터임, 변경 우너하면 application.properties에서 변경하기 )
     * 
     * @param startNumber 시작 번호
     * @return 채널 ID 목록
     */
    List<String> findChannelIdsFromNumber(Integer startNumber);

    /**
     * 모든 채널 ID 조회
     * 
     * @return 채널 ID 목록
     */
    List<String> findAllChannelIds();
}