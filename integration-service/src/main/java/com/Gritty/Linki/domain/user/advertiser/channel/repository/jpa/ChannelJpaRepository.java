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
                        "AND (:minViewCount IS NULL OR c.viewCount >= :minViewCount) " +
                        "AND (:maxViewCount IS NULL OR c.viewCount <= :maxViewCount)")
        Page<Channel> searchChannels(
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
         * 인플루언서 ID로 첫 번째 채널 조회
         * 동일한 인플루언서가 여러 채널을 가질 수 있으므로 첫 번째 결과만 반환
         * 
         * @param influencerId 인플루언서 ID
         * @return 첫 번째 채널 (없으면 null)
         */
        Channel findFirstByInfluencerInfluencerId(String influencerId);
}