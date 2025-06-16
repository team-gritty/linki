package com.Gritty.Linki.domain.user.advertiser.repository;

import com.Gritty.Linki.entity.Channel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * 채널 데이터 접근을 위한 Repository 인터페이스
 */
@Repository
public interface ChannelRepository extends JpaRepository<Channel, String> {

    /**
     * YouTube 채널 ID로 채널 존재 여부 확인
     * 중복 채널이 테이블에 들어가지 않기 위해 사용함
     */
    boolean existsByYoutubeChannelId(String youtubeChannelId);
}