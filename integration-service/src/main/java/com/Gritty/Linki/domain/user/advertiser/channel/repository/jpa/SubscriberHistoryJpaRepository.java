package com.Gritty.Linki.domain.user.advertiser.channel.repository.jpa;

import com.Gritty.Linki.domain.user.advertiser.channel.entity.SubscriberHistoryEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 구독자 히스토리 JPA Repository
 */
@Repository
public interface SubscriberHistoryJpaRepository extends JpaRepository<SubscriberHistoryEntity, String> {

    /**
     * 특정 채널의 구독자 히스토리 조회 (날짜 범위)
     * 
     * @param channelId 채널 ID
     * @param startDate 조회 시작일
     * @return 구독자 히스토리 목록
     */
    @Query("SELECT sh FROM SubscriberHistoryEntity sh " +
            "WHERE sh.channel.channelId = :channelId " +
            "AND sh.collectedAt >= :startDate " +
            "ORDER BY sh.collectedAt ASC")
    List<SubscriberHistoryEntity> findHistoryByChannelAndDateRange(
            @Param("channelId") String channelId,
            @Param("startDate") LocalDateTime startDate);

}