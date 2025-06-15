package com.Gritty.Linki.service;

import com.Gritty.Linki.entity.CollectedChannel;
import com.Gritty.Linki.entity.SubscriberHistory;
import com.Gritty.Linki.repository.CollectedChannelRepository;
import com.Gritty.Linki.repository.SubscriberHistoryRepository;
import com.Gritty.Linki.util.IdGenerator;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

/**
 * YouTube 채널 수집 서비스
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class YouTubeChannelCollectionService {

    private final CollectedChannelRepository collectedChannelRepository;
    private final SubscriberHistoryRepository subscriberHistoryRepository;
    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;

    @Value("${youtube.api.key:AIzaSyA0czx00g45-GYiuduXTynVBlLxkM50zIU}")
    private String youtubeApiKey;

    private static final String YOUTUBE_API_BASE_URL = "https://www.googleapis.com/youtube/v3";

    /**
     * 키워드로 채널 검색하고 저장
     */
    @Transactional
    public List<CollectedChannel> searchAndSaveChannels(String keyword, String category, Integer maxResults) {
        log.info("키워드 '{}', 카테고리 '{}' 채널 검색 시작 (최대 {}개)", keyword, category, maxResults);

        List<CollectedChannel> savedChannels = new ArrayList<>();

        try {
            // 1. YouTube Search API로 채널 검색
            List<String> channelIds = searchChannelIds(keyword, maxResults);

            if (channelIds.isEmpty()) {
                log.info("키워드 '{}'에 해당하는 채널을 찾을 수 없습니다.", keyword);
                return savedChannels;
            }

            // 2. 채널 상세 정보 조회
            List<CollectedChannel> channels = getChannelDetails(channelIds, category, keyword);

            // 3. 중복 제거하고 저장
            for (CollectedChannel channel : channels) {
                if (!collectedChannelRepository.existsByYoutubeChannelId(channel.getYoutubeChannelId())) {
                    CollectedChannel savedChannel = collectedChannelRepository.save(channel);

                    // 4. 첫 번째 구독자 히스토리 기록
                    SubscriberHistory initialHistory = channel.createCurrentSnapshot();
                    initialHistory.setHistoryId(IdGenerator.subscriberHistoryId());
                    subscriberHistoryRepository.save(initialHistory);

                    savedChannels.add(savedChannel);
                    log.info("새 채널 저장: {} (구독자: {})", channel.getChannelName(), channel.getSubscriberCount());
                } else {
                    log.info("이미 수집된 채널: {}", channel.getChannelName());
                }
            }

        } catch (Exception e) {
            log.error("채널 검색 및 저장 중 오류 발생: {}", e.getMessage(), e);
            throw new RuntimeException("채널 수집 중 오류가 발생했습니다: " + e.getMessage());
        }

        log.info("채널 수집 완료: {}개 저장", savedChannels.size());
        return savedChannels;
    }

    /**
     * YouTube Search API로 채널 ID 검색
     */
    private List<String> searchChannelIds(String keyword, Integer maxResults) {
        List<String> channelIds = new ArrayList<>();

        try {
            String searchUrl = String.format(
                    "%s/search?part=snippet&type=channel&q=%s&regionCode=KR&maxResults=%d&key=%s",
                    YOUTUBE_API_BASE_URL, keyword, maxResults, youtubeApiKey);

            String response = restTemplate.getForObject(searchUrl, String.class);
            JsonNode jsonResponse = objectMapper.readTree(response);
            JsonNode items = jsonResponse.get("items");

            if (items != null && items.isArray()) {
                for (JsonNode item : items) {
                    JsonNode snippet = item.get("snippet");
                    if (snippet != null && snippet.has("channelId")) {
                        String channelId = snippet.get("channelId").asText();
                        channelIds.add(channelId);
                    }
                }
            }

        } catch (Exception e) {
            log.error("YouTube 검색 API 호출 중 오류: {}", e.getMessage(), e);
        }

        return channelIds;
    }

    /**
     * 채널 상세 정보 조회
     */
    private List<CollectedChannel> getChannelDetails(List<String> channelIds, String category, String keyword) {
        List<CollectedChannel> channels = new ArrayList<>();

        try {
            String channelIdsParam = String.join(",", channelIds);
            String channelUrl = String.format(
                    "%s/channels?part=snippet,statistics&id=%s&key=%s",
                    YOUTUBE_API_BASE_URL, channelIdsParam, youtubeApiKey);

            String response = restTemplate.getForObject(channelUrl, String.class);
            JsonNode jsonResponse = objectMapper.readTree(response);
            JsonNode items = jsonResponse.get("items");

            if (items != null && items.isArray()) {
                for (JsonNode item : items) {
                    CollectedChannel channel = parseChannelFromJson(item, category, keyword);
                    if (channel != null) {
                        channels.add(channel);
                    }
                }
            }

        } catch (Exception e) {
            log.error("채널 상세 정보 조회 중 오류: {}", e.getMessage(), e);
        }

        return channels;
    }

    /**
     * JSON에서 CollectedChannel 엔티티 생성
     */
    private CollectedChannel parseChannelFromJson(JsonNode item, String category, String keyword) {
        try {
            String youtubeChannelId = item.get("id").asText();
            JsonNode snippet = item.get("snippet");
            JsonNode statistics = item.get("statistics");

            String channelName = snippet.get("title").asText();
            String channelUrl = "https://www.youtube.com/channel/" + youtubeChannelId;
            String description = snippet.has("description") ? snippet.get("description").asText() : "";
            String country = snippet.has("country") ? snippet.get("country").asText() : "KR";
            String publishedAt = snippet.get("publishedAt").asText();

            // 썸네일 URL 추출
            String thumbnailUrl = null;
            if (snippet.has("thumbnails")) {
                JsonNode thumbnails = snippet.get("thumbnails");
                if (thumbnails.has("high")) {
                    thumbnailUrl = thumbnails.get("high").get("url").asText();
                } else if (thumbnails.has("medium")) {
                    thumbnailUrl = thumbnails.get("medium").get("url").asText();
                } else if (thumbnails.has("default")) {
                    thumbnailUrl = thumbnails.get("default").get("url").asText();
                }
            }

            // 통계 정보
            Long subscriberCount = 0L;
            Integer videoCount = 0;
            Long viewCount = 0L;

            if (statistics != null) {
                if (statistics.has("subscriberCount")) {
                    subscriberCount = statistics.get("subscriberCount").asLong();
                }
                if (statistics.has("videoCount")) {
                    videoCount = statistics.get("videoCount").asInt();
                }
                if (statistics.has("viewCount")) {
                    viewCount = statistics.get("viewCount").asLong();
                }
            }

            // 채널 생성 시간 파싱
            LocalDateTime createdAt = LocalDateTime.parse(publishedAt, DateTimeFormatter.ISO_OFFSET_DATE_TIME);

            // CollectedChannel 엔티티 생성
            CollectedChannel channel = new CollectedChannel();
            channel.setCollectedChannelId(IdGenerator.collectedChannelId());
            channel.setYoutubeChannelId(youtubeChannelId);
            channel.setChannelName(channelName);
            channel.setChannelUrl(channelUrl);
            channel.setChannelDescription(description);
            channel.setChannelThumbnailUrl(thumbnailUrl);
            channel.setChannelCategory(category);
            channel.setChannelCountry(country);
            channel.setSubscriberCount(subscriberCount);
            channel.setVideoCount(videoCount);
            channel.setViewCount(viewCount);
            channel.setChannelCreatedAt(createdAt);
            channel.setSearchKeyword(keyword);

            return channel;

        } catch (Exception e) {
            log.error("채널 파싱 중 오류: {}", e.getMessage(), e);
            return null;
        }
    }

    /**
     * 기존 채널들의 구독자 수 업데이트
     */
    @Transactional
    public void updateSubscriberCounts() {
        log.info("기존 채널들의 구독자 수 업데이트 시작");

        List<CollectedChannel> channels = collectedChannelRepository.findAll();

        for (CollectedChannel channel : channels) {
            try {
                updateSingleChannelStats(channel);
                Thread.sleep(100); // API 호출 제한
            } catch (Exception e) {
                log.error("채널 {} 업데이트 중 오류: {}", channel.getChannelName(), e.getMessage());
            }
        }

        log.info("구독자 수 업데이트 완료");
    }

    /**
     * 단일 채널 통계 업데이트
     */
    private void updateSingleChannelStats(CollectedChannel channel) {
        try {
            String channelUrl = String.format(
                    "%s/channels?part=statistics&id=%s&key=%s",
                    YOUTUBE_API_BASE_URL, channel.getYoutubeChannelId(), youtubeApiKey);

            String response = restTemplate.getForObject(channelUrl, String.class);
            JsonNode jsonResponse = objectMapper.readTree(response);
            JsonNode items = jsonResponse.get("items");

            if (items != null && items.isArray() && items.size() > 0) {
                JsonNode statistics = items.get(0).get("statistics");

                if (statistics != null) {
                    Long newSubscriberCount = statistics.has("subscriberCount")
                            ? statistics.get("subscriberCount").asLong()
                            : channel.getSubscriberCount();
                    Integer newVideoCount = statistics.has("videoCount") ? statistics.get("videoCount").asInt()
                            : channel.getVideoCount();
                    Long newViewCount = statistics.has("viewCount") ? statistics.get("viewCount").asLong()
                            : channel.getViewCount();

                    // 기존 데이터와 비교해서 변경된 경우에만 히스토리 기록
                    if (!newSubscriberCount.equals(channel.getSubscriberCount()) ||
                            !newVideoCount.equals(channel.getVideoCount()) ||
                            !newViewCount.equals(channel.getViewCount())) {

                        // 채널 정보 업데이트
                        channel.setSubscriberCount(newSubscriberCount);
                        channel.setVideoCount(newVideoCount);
                        channel.setViewCount(newViewCount);
                        collectedChannelRepository.save(channel);

                        // 히스토리 기록
                        SubscriberHistory history = channel.createCurrentSnapshot();
                        history.setHistoryId(IdGenerator.subscriberHistoryId());
                        subscriberHistoryRepository.save(history);

                        log.info("채널 {} 업데이트: 구독자 {}", channel.getChannelName(), newSubscriberCount);
                    }
                }
            }

        } catch (Exception e) {
            log.error("채널 통계 업데이트 중 오류: {}", e.getMessage());
        }
    }
}