package com.Gritty.Linki.domain.user.advertiser.channel.repository.jpa;

import com.Gritty.Linki.entity.Channel;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

/**
 * 채널 데이터 접근을 위한 JPA Repository 인터페이스
 */
public interface ChannelJpaRepository extends JpaRepository<Channel, String> {

    /**
     * YouTube 채널 ID로 채널 존재 여부 확인
     * 중복 채널이 테이블에 들어가지 않기 위해 사용함
     */
    boolean existsByYoutubeChannelId(String youtubeChannelId);

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
}