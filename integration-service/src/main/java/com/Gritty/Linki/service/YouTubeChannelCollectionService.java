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
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;
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
    private final ChannelStatisticsService channelStatisticsService;
    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;

    @Value("${youtube.api.key}")
    private String youtubeApiKey;

    private static final String YOUTUBE_API_BASE_URL = "https://www.googleapis.com/youtube/v3";
    private static final DateTimeFormatter YOUTUBE_DATE_FORMATTER = DateTimeFormatter.ISO_DATE_TIME;

    /**
     * 키워드로 채널 검색하고 저장
     */
    @Transactional
    public List<CollectedChannel> searchAndSaveChannels(String keyword, String category, Integer maxResults) {
        log.info("키워드 '{}', 카테고리 '{}' 채널 검색 시작 (최대 {}개)", keyword, category, maxResults);

        List<CollectedChannel> savedChannels = new ArrayList<>();

        try {
            List<String> channelIds = searchChannelIds(keyword, maxResults);

            if (channelIds.isEmpty()) {
                log.info("키워드 '{}'에 해당하는 채널을 찾을 수 없습니다.", keyword);
                return savedChannels;
            }

            List<CollectedChannel> channels = getChannelDetails(channelIds, category, keyword);

            for (CollectedChannel channel : channels) {
                if (!collectedChannelRepository.existsByYoutubeChannelId(channel.getYoutubeChannelId())) {
                    CollectedChannel savedChannel = collectedChannelRepository.save(channel);
                    channelStatisticsService.createSubscriberSnapshot(savedChannel);
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
            String encodedKeyword = URLEncoder.encode(keyword, StandardCharsets.UTF_8);

            String searchUrl = UriComponentsBuilder.fromHttpUrl(YOUTUBE_API_BASE_URL + "/search")
                    .queryParam("part", "snippet")
                    .queryParam("type", "channel")
                    .queryParam("q", encodedKeyword)
                    .queryParam("regionCode", "KR")
                    .queryParam("maxResults", maxResults)
                    .queryParam("key", youtubeApiKey)
                    .build()
                    .toString();

            log.debug("YouTube API 검색 URL: {}", searchUrl);

            String response = restTemplate.getForObject(searchUrl, String.class);
            JsonNode jsonResponse = objectMapper.readTree(response);
            JsonNode items = jsonResponse.get("items");

            if (items == null || items.size() == 0) {
                log.info("키워드 '{}'에 대한 검색 결과가 없습니다.", keyword);
                return channelIds;
            }

            for (JsonNode item : items) {
                JsonNode snippet = item.get("snippet");
                if (snippet != null && snippet.has("channelId")) {
                    String channelId = snippet.get("channelId").asText();
                    channelIds.add(channelId);
                    log.debug("채널 ID 찾음: {}", channelId);
                }
            }

            log.info("키워드 '{}'로 {}개의 채널을 찾았습니다.", keyword, channelIds.size());

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

            CollectedChannel channel = new CollectedChannel(
                    IdGenerator.collectedChannelId(),
                    youtubeChannelId,
                    snippet.get("title").asText(),
                    "https://www.youtube.com/channel/" + youtubeChannelId,
                    category);

            channel.setChannelDescription(snippet.has("description") ? snippet.get("description").asText() : "");
            channel.setChannelCountry(snippet.has("country") ? snippet.get("country").asText() : "KR");

            // YouTube API의 publishedAt 날짜 형식 처리
            String publishedAt = snippet.get("publishedAt").asText();
            try {
                // ISO 8601 형식의 날짜 문자열을 ZonedDateTime으로 파싱 후 LocalDateTime으로 변환
                ZonedDateTime zonedDateTime = ZonedDateTime.parse(publishedAt);
                channel.setChannelCreatedAt(zonedDateTime.toLocalDateTime());
            } catch (Exception e) {
                log.warn("채널 생성일 파싱 실패: {}, 현재 시간으로 설정합니다.", publishedAt);
                channel.setChannelCreatedAt(LocalDateTime.now());
            }

            channel.setSearchKeyword(keyword);

            // 썸네일 URL 설정
            if (snippet.has("thumbnails")) {
                JsonNode thumbnails = snippet.get("thumbnails");
                if (thumbnails.has("high")) {
                    channel.setChannelThumbnailUrl(thumbnails.get("high").get("url").asText());
                } else if (thumbnails.has("medium")) {
                    channel.setChannelThumbnailUrl(thumbnails.get("medium").get("url").asText());
                } else if (thumbnails.has("default")) {
                    channel.setChannelThumbnailUrl(thumbnails.get("default").get("url").asText());
                }
            }

            // 통계 정보 설정
            if (statistics != null) {
                if (statistics.has("subscriberCount")) {
                    channel.setSubscriberCount(statistics.get("subscriberCount").asLong());
                }
                if (statistics.has("videoCount")) {
                    channel.setVideoCount(statistics.get("videoCount").asInt());
                }
                if (statistics.has("viewCount")) {
                    channel.setViewCount(statistics.get("viewCount").asLong());
                }
            }

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