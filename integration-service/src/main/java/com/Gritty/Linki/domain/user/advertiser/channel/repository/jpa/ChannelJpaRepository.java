package com.Gritty.Linki.domain.user.advertiser.channel.repository.jpa;

import com.Gritty.Linki.entity.Channel;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

/**
 * 채널 데이터 접근을 위한 JPA Repository 인터페이스
 */
public interface ChannelJpaRepository extends JpaRepository<Channel, String> {

        /**
         * 채널 검색 쿼리
         * 카테고리, 키워드, 구독자 수, 조회수 등의 조건으로 채널을 검색
         */
        @Query("SELECT c FROM Channel c " +
                        "WHERE (:category IS NULL OR c.channelCategory = :category) " +
                        "AND (:keyword IS NULL OR (LOWER(c.channelName) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
                        "     OR LOWER(c.channelDescription) LIKE LOWER(CONCAT('%', :keyword, '%')))) " +
                        "AND (:minSubscribers IS NULL OR c.subscriberCount >= :minSubscribers) " +
                        "AND (:maxSubscribers IS NULL OR c.subscriberCount <= :maxSubscribers) " +
                        "AND (:minViewCount IS NULL OR " +
                        "     (CASE WHEN c.videoCount > 0 THEN c.viewCount / c.videoCount ELSE 0 END) >= :minViewCount) "
                        +
                        "AND (:maxViewCount IS NULL OR " +
                        "     (CASE WHEN c.videoCount > 0 THEN c.viewCount / c.videoCount ELSE 0 END) <= :maxViewCount)")
        Page<Channel> searchChannels(
                        @Param("category") String category,
                        @Param("keyword") String keyword,
                        @Param("minSubscribers") Long minSubscribers,
                        @Param("maxSubscribers") Long maxSubscribers,
                        @Param("minViewCount") Long minViewCount,
                        @Param("maxViewCount") Long maxViewCount,
                        Pageable pageable);

        /**
         * LinkiScore 기반으로 정렬된 채널 검색 쿼리
         * HomeRecommendationController와 동일한 로직으로 LinkiScore를 기준으로 정렬
         */
        @Query("SELECT c FROM Channel c " +
                        "LEFT JOIN c.influencer i " +
                        "LEFT JOIN LinkiScore ls ON i.influencerId = ls.influencerId " +
                        "WHERE (:category IS NULL OR c.channelCategory = :category) " +
                        "AND (:keyword IS NULL OR (LOWER(c.channelName) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
                        "     OR LOWER(c.channelDescription) LIKE LOWER(CONCAT('%', :keyword, '%')))) " +
                        "AND (:minSubscribers IS NULL OR c.subscriberCount >= :minSubscribers) " +
                        "AND (:maxSubscribers IS NULL OR c.subscriberCount <= :maxSubscribers) " +
                        "AND (:minViewCount IS NULL OR " +
                        "     (CASE WHEN c.videoCount > 0 THEN c.viewCount / c.videoCount ELSE 0 END) >= :minViewCount) "
                        +
                        "AND (:maxViewCount IS NULL OR " +
                        "     (CASE WHEN c.videoCount > 0 THEN c.viewCount / c.videoCount ELSE 0 END) <= :maxViewCount) "
                        +
                        "ORDER BY " +
                        "CASE WHEN ls.costPerClick IS NOT NULL AND ls.dailyTraffic IS NOT NULL " +
                        "     AND ls.averageReviewScore IS NOT NULL AND ls.contractCount IS NOT NULL " +
                        "THEN (" +
                        "  (COALESCE(ls.costPerClick, 0) * 0.3) + " +
                        "  (COALESCE(ls.dailyTraffic, 0) * 0.25) + " +
                        "  (COALESCE(ls.averageReviewScore, 0) * 0.25) + " +
                        "  (COALESCE(ls.contractCount, 0) * 0.2)" +
                        ") ELSE 0 END DESC, " +
                        "c.subscriberCount DESC")
        Page<Channel> searchChannelsByLinkiScore(
                        @Param("category") String category,
                        @Param("keyword") String keyword,
                        @Param("minSubscribers") Long minSubscribers,
                        @Param("maxSubscribers") Long maxSubscribers,
                        @Param("minViewCount") Long minViewCount,
                        @Param("maxViewCount") Long maxViewCount,
                        Pageable pageable);

        /**
         * 채널 ID로 YouTube 채널 ID 조회
         * 
         * @param channelId 내부 채널 ID
         * @return YouTube 채널 ID
         */
        @Query("SELECT c.youtubeChannelId FROM Channel c WHERE c.channelId = :channelId")
        Optional<String> findYoutubeChannelIdByChannelId(@Param("channelId") String channelId);

        /**
         * 특정 번호 이상의 모든 채널 ID 조회 (구독자 수집용)
         * 더미 데이터(CH0001 형태)와 실제 수집 데이터(CHN- 형태) 모두 처리
         * 
         * @param startNumber 시작 번호 (예: 1007)
         * @return 채널 ID 목록
         */
        @Query(value = "SELECT channel_id FROM (" +
                        "  SELECT channel_id, " +
                        "         @row_number := @row_number + 1 as row_num " +
                        "  FROM channel, (SELECT @row_number := 0) r " +
                        "  ORDER BY " +
                        "    CASE " +
                        "      WHEN channel_id REGEXP '^CH[0-9]{4}$' THEN CAST(SUBSTRING(channel_id, 3) AS UNSIGNED) " +
                        "      WHEN channel_id LIKE 'CHN-%' THEN 50000 + UNIX_TIMESTAMP(collected_at) " +
                        "      ELSE 99999 + UNIX_TIMESTAMP(collected_at) " +
                        "    END, " +
                        "    channel_id " +
                        ") numbered " +
                        "WHERE row_num >= :startNumber " +
                        "ORDER BY row_num", nativeQuery = true)
        List<String> findChannelIdsFromNumber(@Param("startNumber") Integer startNumber);

        /**
         * 모든 채널 ID 조회 (구독자 수집용)
         * 
         * @return 채널 ID 목록
         */
        @Query("SELECT c.channelId FROM Channel c ORDER BY c.channelId")
        List<String> findAllChannelIds();

        /**
         * 정렬 기능이 있는 채널 검색 쿼리 (네이티브 쿼리 사용)
         * 구독자 수, 평균 조회수 등으로 동적 정렬 지원
         */
        @Query(value = "SELECT * FROM channel c " +
                        "WHERE (:category IS NULL OR c.channel_category = :category) " +
                        "AND (:keyword IS NULL OR (LOWER(c.channel_name) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
                        "     OR LOWER(c.channel_description) LIKE LOWER(CONCAT('%', :keyword, '%')))) " +
                        "AND (:minSubscribers IS NULL OR c.subscriber_count >= :minSubscribers) " +
                        "AND (:maxSubscribers IS NULL OR c.subscriber_count <= :maxSubscribers) " +
                        "AND (:minViewCount IS NULL OR " +
                        "     (CASE WHEN c.video_count > 0 THEN c.view_count / c.video_count ELSE 0 END) >= :minViewCount) "
                        +
                        "AND (:maxViewCount IS NULL OR " +
                        "     (CASE WHEN c.video_count > 0 THEN c.view_count / c.video_count ELSE 0 END) <= :maxViewCount) "
                        +
                        "ORDER BY " +
                        "CASE " +
                        "  WHEN :sortBy = 'subscriberCount' AND :sortDirection = 'desc' THEN c.subscriber_count " +
                        "  WHEN :sortBy = 'subscriberCount' AND :sortDirection = 'asc' THEN -c.subscriber_count " +
                        "  WHEN :sortBy = 'avgViewCount' AND :sortDirection = 'desc' THEN " +
                        "    (CASE WHEN c.video_count > 0 THEN c.view_count / c.video_count ELSE 0 END) " +
                        "  WHEN :sortBy = 'avgViewCount' AND :sortDirection = 'asc' THEN " +
                        "    -(CASE WHEN c.video_count > 0 THEN c.view_count / c.video_count ELSE 0 END) " +
                        "  ELSE c.subscriber_count " +
                        "END DESC", nativeQuery = true)
        Page<Channel> searchChannelsWithSort(
                        @Param("category") String category,
                        @Param("keyword") String keyword,
                        @Param("minSubscribers") Long minSubscribers,
                        @Param("maxSubscribers") Long maxSubscribers,
                        @Param("minViewCount") Long minViewCount,
                        @Param("maxViewCount") Long maxViewCount,
                        @Param("sortBy") String sortBy,
                        @Param("sortDirection") String sortDirection,
                        Pageable pageable);
}