package com.Gritty.Linki.domain.user.advertiser.channel.service;

import com.Gritty.Linki.domain.user.advertiser.channel.dto.YouTubeChannelDto;
import com.Gritty.Linki.domain.user.advertiser.channel.dto.YouTubeSearchDto;
import com.Gritty.Linki.exception.BusinessException;
import com.Gritty.Linki.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.List;
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

    @Value("${youtube.api.key}")
    private String apiKey;

    @Value("${youtube.api.base-url}")
    private String baseUrl;

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
}