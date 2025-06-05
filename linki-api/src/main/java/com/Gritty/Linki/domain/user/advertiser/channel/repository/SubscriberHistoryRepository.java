package com.Gritty.Linki.domain.user.advertiser.channel.repository;

import com.Gritty.Linki.domain.user.advertiser.channel.entity.SubscriberHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 구독자 수 조회 이력 리포지토리
 */
@Repository
public interface SubscriberHistoryRepository extends JpaRepository<SubscriberHistory, Long> {
    /**
     * ApexCharts로 구독자 수 변화 그래프를 그리기 위한 데이터 조회 메서드
     * - x축: collectedAt (수집 시간)
     * - y축: subscriberCount (구독자 수)
     * - 최신 데이터부터 정렬하여 그래프에 표시
     * 
     * @param channelId 그래프를 그릴 채널의 ID
     * @return 구독자 수 변화 데이터 목록
     */
    List<SubscriberHistory> findByChannelIdOrderByCollectedAtDesc(Long channelId);

    /**
     * ApexCharts로 특정 기간의 구독자 수 변화 그래프를 그리기 위한 데이터 조회 메서드
     * - x축: collectedAt (수집 시간)
     * - y축: subscriberCount (구독자 수)
     * - 예: 최근 1개월, 3개월, 1년 등의 기간별 데이터 조회
     * 
     * @param channelId 그래프를 그릴 채널의 ID
     * @param startDate 조회 시작 날짜
     * @param endDate   조회 종료 날짜
     * @return 해당 기간의 구독자 수 변화 데이터 목록
     */
    List<SubscriberHistory> findByChannelIdAndCollectedAtBetweenOrderByCollectedAtDesc(
            Long channelId, LocalDateTime startDate, LocalDateTime endDate);
}