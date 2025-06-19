package com.Gritty.Linki.domain.user.advertiser.channel.service;

import com.Gritty.Linki.domain.user.advertiser.channel.dto.YouTubeChannelDto;
import com.Gritty.Linki.domain.user.advertiser.channel.dto.YouTubeSearchDto;
import com.Gritty.Linki.exception.BusinessException;
import com.Gritty.Linki.exception.ErrorCode;
import com.Gritty.Linki.domain.user.advertiser.channel.config.YouTubeApiConfig;
import com.Gritty.Linki.domain.user.advertiser.channel.repository.jpa.ChannelJpaRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * YouTube Data API와 통신하는 서비스 클래스
 * 채널 검색, 채널 정보 조회 등의 기능을 제공
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class YouTubeApiService {

    private final RestTemplate restTemplate;
    private final YouTubeApiConfig youTubeApiConfig;
    private final ObjectMapper objectMapper;
    private final ChannelJpaRepository channelJpaRepository;

    @Value("${youtube.api.key}")
    private String apiKey;

    @Value("${youtube.api.base-url}")
    private String baseUrl;

    private static final String YOUTUBE_API_BASE_URL = "https://www.googleapis.com/youtube/v3";

    /**
     * 키워드로 YouTube 채널을 검색
     */
    public List<String> searchChannels(String keyword, Integer maxResults) {
        try {
            // YouTube Search API 호출 URL 구성
            String searchUrl = UriComponentsBuilder.fromHttpUrl(baseUrl + "/search")
                    .queryParam("part", "snippet")
                    .queryParam("type", "channel")
                    .queryParam("q", keyword)
                    .queryParam("maxResults", maxResults)
                    .queryParam("order", "relevance")
                    .queryParam("key", apiKey)
                    .build()
                    .toUriString();

            log.info("YouTube Search API 요청: keyword={}, maxResults={}", keyword, maxResults);
            log.debug("Search URL: {}", searchUrl.replaceAll("key=[^&]*", "key=***"));

            // API 호출 및 응답 처리
            YouTubeSearchDto searchResponse = restTemplate.getForObject(searchUrl, YouTubeSearchDto.class);

            if (searchResponse == null || searchResponse.getItems() == null) {
                log.warn("YouTube Search API 응답이 비어있습니다. keyword: {}", keyword);
                return List.of();
            }

            // 채널 ID 목록 추출
            List<String> channelIds = searchResponse.getItems().stream()
                    .filter(item -> item.getId() != null && item.getId().getChannelId() != null)
                    .map(item -> item.getId().getChannelId())
                    .distinct()
                    .collect(Collectors.toList());

            log.info("검색 완료: keyword={}, 발견된 채널 수={}", keyword, channelIds.size());
            return channelIds;

        } catch (Exception e) {
            log.error("YouTube 채널 검색 실패: keyword={}, error={}", keyword, e.getMessage(), e);
            throw new BusinessException(ErrorCode.INTERNAL_SERVER_ERROR,
                    "YouTube 채널 검색 중 오류가 발생했습니다: " + e.getMessage());
        }
    }

    /**
     * 채널 ID 목록으로 상세 채널 정보를 조회
     */
    public List<YouTubeChannelDto.ChannelItem> getChannelsDetails(List<String> channelIds) {
        if (channelIds == null || channelIds.isEmpty()) {
            return List.of();
        }

        try {
            String channelIdsParam = String.join(",", channelIds);

            String channelsUrl = UriComponentsBuilder.fromHttpUrl(baseUrl + "/channels")
                    .queryParam("part", "snippet,statistics")
                    .queryParam("id", channelIdsParam)
                    .queryParam("key", apiKey)
                    .build()
                    .toUriString();

            log.info("YouTube Channels API 요청: 채널 수={}", channelIds.size());
            log.debug("Channels URL: {}", channelsUrl.replaceAll("key=[^&]*", "key=***"));

            YouTubeChannelDto channelResponse = restTemplate.getForObject(channelsUrl, YouTubeChannelDto.class);

            if (channelResponse == null || channelResponse.getItems() == null) {
                log.warn("YouTube Channels API 응답이 비어있습니다. channelIds: {}", channelIds);
                return List.of();
            }

            log.info("채널 상세 정보 조회 완료: 요청 채널 수={}, 응답 채널 수={}",
                    channelIds.size(), channelResponse.getItems().size());

            return channelResponse.getItems();

        } catch (Exception e) {
            log.error("YouTube 채널 상세 정보 조회 실패: channelIds={}, error={}", channelIds, e.getMessage(), e);
            throw new BusinessException(ErrorCode.INTERNAL_SERVER_ERROR,
                    "YouTube 채널 상세 정보 조회 중 오류가 발생했습니다: " + e.getMessage());
        }
    }

    /**
     * YouTube 채널 ID로 구독자 수 조회
     * 
     * @param youtubeChannelId YouTube 채널 ID (UC로 시작하는 ID)
     * @return 구독자 수
     */
    public Long getSubscriberCount(String youtubeChannelId) {
        log.info("----------YouTube API 구독자 수 조회 시작 - channelId: {}", youtubeChannelId);

        try {
            String url = UriComponentsBuilder.fromHttpUrl(YOUTUBE_API_BASE_URL + "/channels")
                    .queryParam("part", "statistics")
                    .queryParam("id", youtubeChannelId)
                    .queryParam("key", youTubeApiConfig.getYoutubeApiKey())
                    .toUriString();

            log.info("YouTube API 호출 URL: {}", url.replace(youTubeApiConfig.getYoutubeApiKey(), "***"));

            // restTemplate: Spring에서 제공하는 HTTP 클라이언트
            // getForObject(): HTTP GET 요청을 보냄
            // url: 요청할 API 주소 (예: "https://api.example.com/data")
            // String.class: 응답을 문자열(String)로 받겠다
            String response = restTemplate.getForObject(url, String.class);

            if (response == null) {
                log.warn("YouTube API 응답이 null----- channelId: {}", youtubeChannelId);
                return 0L;
            }

            // response: "{"items": [...]}" 같은 JSON 문자열
            // objectMapper.readTree(...): JSON 문자열을 JsonNode로 파싱
            // rootNode: JSON의 최상위 노드
            JsonNode rootNode = objectMapper.readTree(response);
            // rootNode.get("items"): JSON에서 "items" 키에 해당하는 값을 꺼냄
            //예: {"items": [1, 2, 3]} → itemsNode는 [1, 2, 3]
            JsonNode itemsNode = rootNode.get("items");

            if (itemsNode == null || itemsNode.size() == 0) {
                log.warn("YouTube API 응답에 채널 정보가 없음 - channelId: {}", youtubeChannelId);
                return 0L;
            }

            JsonNode statisticsNode = itemsNode.get(0).get("statistics");
            if (statisticsNode == null) {
                log.warn("YouTube API 응답에 statistics 정보 없음 - channelId: {}", youtubeChannelId);
                return 0L;
            }

            log.info("statistics 정보 -----------{}", statisticsNode);

            JsonNode subscriberCountNode = statisticsNode.get("subscriberCount");
            if (subscriberCountNode == null) {
                log.warn("YouTube API 응답에 구독자 수 정보가 없습니다 - channelId: {}", youtubeChannelId);
                return 0L;
            }

            Long subscriberCount = subscriberCountNode.asLong();
            log.info("YouTube API 구독자 수 조회 완료 - channelId: {}, subscriberCount: {}", youtubeChannelId, subscriberCount);

            return subscriberCount;

        } catch (Exception e) {
            log.error("YouTube API 호출 중 오류 발생 - channelId: {}", youtubeChannelId, e);
            return 0L;
        }
    }

    /**
     * 채널 ID를 YouTube 채널 ID로 매핑 (Channel 테이블에서 조회)
     * 
     * @param channelId 내부 채널 ID
     * @return YouTube 채널 ID
     */
    public String getYouTubeChannelId(String channelId) {
        log.info("----------YouTube 채널 ID 조회 시작 - 내부 채널 ID: {}", channelId);

        try {
            Optional<String> youtubeChannelId = channelJpaRepository.findYoutubeChannelIdByChannelId(channelId);

            if (youtubeChannelId.isPresent()) {
                log.info("YouTube 채널 ID 조회 완료 - 내부 채널 ID: {}, YouTube 채널 ID: {}",
                        channelId, youtubeChannelId.get());
                return youtubeChannelId.get();
            } else {
                log.warn("YouTube 채널 ID를 찾을 수 없습니다 - 내부 채널 ID: {}", channelId);
                return null;
            }
        } catch (Exception e) {
            log.error("YouTube 채널 ID 조회 중 오류 발생 - 내부 채널 ID: {}", channelId, e);
            return null;
        }
    }
}