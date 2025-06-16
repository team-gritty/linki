package com.Gritty.Linki.repository;

import com.Gritty.Linki.entity.SubscriberHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 구독자 히스토리 리포지토리
 */
@Repository
public interface SubscriberHistoryRepository extends JpaRepository<SubscriberHistory, String> {

        // 특정 채널의 구독자 히스토리 조회 (최신순)
        List<SubscriberHistory> findByCollectedChannelCollectedChannelIdOrderBySnapshotDateDesc(
                        String collectedChannelId);

        // 특정 채널의 구독자 히스토리 조회 (기간 내)
        @Query("SELECT h FROM SubscriberHistory h WHERE h.collectedChannel.collectedChannelId = :channelId " +
                        "AND h.snapshotDate BETWEEN :startDate AND :endDate ORDER BY h.snapshotDate DESC")
        List<SubscriberHistory> findByChannelAndDateRange(
                        @Param("channelId") String channelId,
                        @Param("startDate") LocalDateTime startDate,
                        @Param("endDate") LocalDateTime endDate);

        // 특정 채널의 최근 히스토리 조회 (개수 제한)
        @Query("SELECT h FROM SubscriberHistory h WHERE h.collectedChannel.collectedChannelId = :channelId " +
                        "ORDER BY h.snapshotDate DESC LIMIT :limit")
        List<SubscriberHistory> findRecentHistoryByChannel(@Param("channelId") String channelId,
                        @Param("limit") int limit);
}