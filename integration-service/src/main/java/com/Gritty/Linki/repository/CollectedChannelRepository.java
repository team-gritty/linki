package com.Gritty.Linki.repository;

import com.Gritty.Linki.entity.CollectedChannel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * 수집된 채널 리포지토리
 */
@Repository
public interface CollectedChannelRepository extends JpaRepository<CollectedChannel, String> {

    // YouTube 채널 ID로 중복 검사
    boolean existsByYoutubeChannelId(String youtubeChannelId);

    // YouTube 채널 ID로 조회
    Optional<CollectedChannel> findByYoutubeChannelId(String youtubeChannelId);

    // 카테고리별 조회
    List<CollectedChannel> findByChannelCategoryOrderByCollectedAtDesc(String channelCategory);

    // 검색 키워드로 조회
    List<CollectedChannel> findBySearchKeywordOrderByCollectedAtDesc(String searchKeyword);

    // 구독자 수 범위로 조회
    @Query("SELECT c FROM CollectedChannel c WHERE c.subscriberCount BETWEEN :minSubs AND :maxSubs ORDER BY c.subscriberCount DESC")
    List<CollectedChannel> findBySubscriberCountRange(@Param("minSubs") Long minSubs, @Param("maxSubs") Long maxSubs);

    // 최근 수집된 채널들
    @Query("SELECT c FROM CollectedChannel c ORDER BY c.collectedAt DESC")
    List<CollectedChannel> findRecentlyCollected();
}