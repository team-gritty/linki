package com.Gritty.Linki.domain.user.advertiser.channel.service;

import com.Gritty.Linki.domain.user.advertiser.channel.entity.Channel;
import com.Gritty.Linki.domain.user.advertiser.channel.entity.SubscriberHistory;
import com.Gritty.Linki.domain.user.advertiser.channel.repository.ChannelRepository;
import com.Gritty.Linki.domain.user.advertiser.channel.repository.SubscriberHistoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ChannelService {
    private final ChannelRepository channelRepository;
    private final SubscriberHistoryRepository subscriberHistoryRepository;

    // YouTube Data API Key from application.properties
    @Value("${youtube.api.key}")
    private String youtubeApiKey;

    private final RestTemplate restTemplate = new RestTemplate();

    /**
     * 키워드로 유튜브 채널 10개 랜덤 검색 (YouTube Data API 사용)
     * 
     * @param keyword 검색 키워드
     * @return 채널 리스트
     */
    public List<Channel> searchChannelsByKeyword(String keyword) {
        String url = "https://www.googleapis.com/youtube/v3/search?part=snippet&type=channel&maxResults=20&q="
                + keyword + "&key=" + youtubeApiKey;
        Map<String, Object> response = restTemplate.getForObject(url, Map.class);
        List<Map<String, Object>> items = (List<Map<String, Object>>) response.get("items");
        if (items == null)
            return Collections.emptyList();
        // 10개 랜덤 추출
        Collections.shuffle(items);
        List<Map<String, Object>> selected = items.stream().limit(10).collect(Collectors.toList());
        List<Channel> channels = new ArrayList<>();
        for (Map<String, Object> item : selected) {
            Map<String, Object> id = (Map<String, Object>) item.get("id");
            Map<String, Object> snippet = (Map<String, Object>) item.get("snippet");
            String channelId = (String) id.get("channelId");
            String title = (String) snippet.get("title");
            String description = (String) snippet.get("description");
            String thumbnailUrl = ((Map<String, Object>) ((Map<String, Object>) snippet.get("thumbnails"))
                    .get("default")).get("url").toString();
            Channel channel = Channel.builder()
                    .channelId(channelId)
                    .title(title)
                    .description(description)
                    .thumbnailUrl(thumbnailUrl)
                    .createdAt(LocalDateTime.now())
                    .build();
            channels.add(channel);
        }
        return channels;
    }

    /**
     * 채널의 구독자 수를 YouTube Data API로 가져와서 저장
     * 
     * @param channel 채널 엔티티
     */
    @Transactional
    public void collectAndSaveSubscriberCount(Channel channel) {
        String url = "https://www.googleapis.com/youtube/v3/channels?part=statistics&id="
                + channel.getChannelId() + "&key=" + youtubeApiKey;
        Map<String, Object> response = restTemplate.getForObject(url, Map.class);
        List<Map<String, Object>> items = (List<Map<String, Object>>) response.get("items");
        if (items == null || items.isEmpty())
            return;
        Map<String, Object> statistics = (Map<String, Object>) items.get(0).get("statistics");
        Long subscriberCount = Long.valueOf((String) statistics.get("subscriberCount"));
        // 구독자 수 이력 저장
        SubscriberHistory history = SubscriberHistory.builder()
                .channel(channel)
                .subscriberCount(subscriberCount)
                .collectedAt(LocalDateTime.now())
                .build();
        subscriberHistoryRepository.save(history);
    }

    /**
     * 매일 새벽 3시에 모든 채널의 구독자 수를 수집 (스케줄러)
     * 
     * @Scheduled(cron = "0 0 3 * * *") // 매일 새벽 3시 실행
     */
    @Scheduled(cron = "0 0 3 * * *") // 매일 새벽 3시 실행
    @Transactional
    public void collectAllChannelsSubscriberCount() {
        List<Channel> channels = channelRepository.findAll();
        for (Channel channel : channels) {
            collectAndSaveSubscriberCount(channel);
        }
    }

    /**
     * 채널 ID로 존재 여부 확인
     */
    public boolean existsByChannelId(String channelId) {
        return channelRepository.existsByChannelId(channelId);
    }

    /**
     * 채널 저장
     */
    public Channel saveChannel(Channel channel) {
        return channelRepository.save(channel);
    }
}