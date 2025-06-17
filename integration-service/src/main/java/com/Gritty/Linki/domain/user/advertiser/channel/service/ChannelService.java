package com.Gritty.Linki.domain.user.advertiser.channel.service;

import com.Gritty.Linki.domain.user.advertiser.channel.request.ChannelSearchRequest;
import com.Gritty.Linki.domain.user.advertiser.channel.response.ChannelListResponse;
import com.Gritty.Linki.entity.Channel;
import com.Gritty.Linki.domain.user.advertiser.channel.repository.jpa.ChannelJpaRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 채널 검색 서비스
 * 검색 필터를 통한 채널 조회 기능 제공
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ChannelService {

    private final ChannelJpaRepository channelRepository;

    /**
     * 채널 검색
     * 검색 조건에 따라 채널을 필터링하여 반환
     * 
     * @param request 검색 조건
     * @return 필터링된 채널 목록
     */
    public List<ChannelListResponse> searchChannels(ChannelSearchRequest request) {
        log.info("채널 검색 서비스 호출: keyword={}, category={}, page={}, limit={}",
                request.getKeyword(), request.getCategory(), request.getPage(), request.getLimit());

        // JPA를 사용하여 데이터베이스에서 채널 검색
        Page<Channel> channelPage = channelRepository.searchChannels(
                request.getCategory(),
                request.getKeyword(),
                request.getMinSubscribers(),
                request.getMaxSubscribers(),
                // minAvgViewCount, maxAvgViewCount는 viewCount로 매핑
                request.getMinAvgViewCount(),
                request.getMaxAvgViewCount(),
                PageRequest.of(request.getPage(), request.getLimit()));

        // 엔티티를 DTO로 변환
        return channelPage.getContent().stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    /**
     * Channel 엔티티를 ChannelListResponse DTO로 변환
     */
    private ChannelListResponse convertToDto(Channel channel) {
        long avgViewCount = channel.getVideoCount() != null && channel.getVideoCount() > 0
                && channel.getViewCount() != null
                        ? channel.getViewCount() / channel.getVideoCount()
                        : 0;

        return ChannelListResponse.builder()
                .channelId(channel.getChannelId())
                .channelName(channel.getChannelName())
                .thumbnailUrl(channel.getChannelThumbnailUrl())
                .subscriberCount(channel.getSubscriberCount() != null ? channel.getSubscriberCount() : 0)
                .avgViewCount(avgViewCount)
                .category(channel.getChannelCategory())
                .description(channel.getChannelDescription())
                .build();
    }
}