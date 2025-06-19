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
         * 더미 데이터의 채널 ID 패턴이 CH0001, CH0002... 형식임
         * 
         * @param startNumber 시작 번호 (예: 1007)
         * @return 채널 ID 목록
         */
        @Query("SELECT c.channelId FROM Channel c WHERE " +
                        "CAST(SUBSTRING(c.channelId, 3) AS INTEGER) >= :startNumber " +
                        "ORDER BY CAST(SUBSTRING(c.channelId, 3) AS INTEGER)")
        List<String> findChannelIdsFromNumber(@Param("startNumber") Integer startNumber);

        /**
         * 모든 채널 ID 조회 (구독자 수집용)
         * 
         * @return 채널 ID 목록
         */
        @Query("SELECT c.channelId FROM Channel c ORDER BY c.channelId")
        List<String> findAllChannelIds();
}