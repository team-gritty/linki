package com.Gritty.Linki.domain.user.advertiser.channel.service;

import com.Gritty.Linki.domain.user.advertiser.channel.config.YouTubeApiConfig;
import com.Gritty.Linki.domain.user.advertiser.channel.dto.YouTubeChannelDto;
import com.Gritty.Linki.domain.user.advertiser.channel.dto.YouTubeSearchDto;
import com.Gritty.Linki.domain.user.advertiser.channel.dto.YouTubeVideoDto;
import com.Gritty.Linki.domain.user.advertiser.channel.repository.jpa.ChannelJpaRepository;
import com.Gritty.Linki.exception.BusinessException;
import com.Gritty.Linki.exception.ErrorCode;
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

            // API 호출 및 응답 처리
            YouTubeSearchDto searchResponse = restTemplate.getForObject(searchUrl, YouTubeSearchDto.class);

            if (searchResponse == null || searchResponse.getItems() == null) {
                log.warn("YouTube Search API 응답이 비어있습니다. keyword: {}", keyword);
                throw new BusinessException(ErrorCode.INTERNAL_SERVER_ERROR, "YouTube API 응답이 비어있습니다");
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
            throw new BusinessException(ErrorCode.INVALID_INPUT_VALUE, "채널 ID 목록이 비어있습니다");
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
                throw new BusinessException(ErrorCode.INTERNAL_SERVER_ERROR, "YouTube Channels API 응답이 비어있습니다");
            }

            log.info("채널 상세 정보 조회 완료: 요청 채널 수={}",
                    channelIds.size());

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
                throw new BusinessException(ErrorCode.INTERNAL_SERVER_ERROR, "YouTube API 응답이 null입니다");
            }

            // response: "{"items": [...]}" 같은 JSON 문자열
            // objectMapper.readTree(...): JSON 문자열을 JsonNode로 파싱
            // rootNode: JSON의 최상위 노드
            JsonNode rootNode = objectMapper.readTree(response);
            // rootNode.get("items"): JSON에서 "items" 키에 해당하는 값을 꺼냄
            // 예: {"items": [1, 2, 3]} → itemsNode는 [1, 2, 3]
            JsonNode itemsNode = rootNode.get("items");

            if (itemsNode == null || itemsNode.size() == 0) {
                log.warn("YouTube API 응답에 채널 정보가 없음 - channelId: {}", youtubeChannelId);
                throw new BusinessException(ErrorCode.ENTITY_NOT_FOUND, "채널 정보를 찾을 수 없습니다");
            }

            JsonNode statisticsNode = itemsNode.get(0).get("statistics");
            if (statisticsNode == null) {
                log.warn("YouTube API 응답에 statistics 정보 없음 - channelId: {}", youtubeChannelId);
                throw new BusinessException(ErrorCode.ENTITY_NOT_FOUND, "채널 통계 정보를 찾을 수 없습니다");
            }

            log.info("statistics 정보 -----------{}", statisticsNode);

            JsonNode subscriberCountNode = statisticsNode.get("subscriberCount");
            if (subscriberCountNode == null) {
                log.warn("YouTube API 응답에 구독자 수 정보가 없습니다 - channelId: {}", youtubeChannelId);
                throw new BusinessException(ErrorCode.ENTITY_NOT_FOUND, "구독자 수 정보를 찾을 수 없습니다");
            }

            Long subscriberCount = subscriberCountNode.asLong();
            log.info("YouTube API 구독자 수 조회 완료 - channelId: {}, subscriberCount: {}", youtubeChannelId, subscriberCount);

            return subscriberCount;

        } catch (Exception e) {
            log.error("YouTube API 호출 중 오류 발생 - channelId: {}", youtubeChannelId, e);
            throw new BusinessException(ErrorCode.INTERNAL_SERVER_ERROR,
                    "YouTube API 호출 중 오류가 발생했습니다: " + e.getMessage());
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
                throw new BusinessException(ErrorCode.ENTITY_NOT_FOUND, "YouTube 채널 ID를 찾을 수 없습니다");
            }
        } catch (Exception e) {
            log.error("YouTube 채널 ID 조회 중 오류 발생 - 내부 채널 ID: {}", channelId, e);
            throw new BusinessException(ErrorCode.INTERNAL_SERVER_ERROR,
                    "YouTube 채널 ID 조회 중 오류가 발생했습니다: " + e.getMessage());
        }
    }

    /**
     * YouTube 채널의 최근 영상 목록을 가져와서 평균 좋아요/댓글 수를 계산
     * 
     * @param youtubeChannelId YouTube 채널 ID
     * @param maxResults       가져올 영상 수 (최대 50개)
     * @return {avgLikeCount, avgCommentCount} 배열
     */
    public long[] getChannelVideoAverages(String youtubeChannelId, int maxResults) {
        try {
            // 1. 채널의 업로드 플레이리스트 ID 가져오기
            String uploadsPlaylistId = getUploadsPlaylistId(youtubeChannelId);
            if (uploadsPlaylistId == null) {
                throw new BusinessException(ErrorCode.ENTITY_NOT_FOUND, "업로드 플레이리스트를 찾을 수 없습니다");
            }

            // 2. 플레이리스트에서 영상 ID 목록 가져오기
            List<String> videoIds = getVideoIdsFromPlaylist(uploadsPlaylistId, maxResults);
            if (videoIds.isEmpty()) {
                throw new BusinessException(ErrorCode.ENTITY_NOT_FOUND, "영상 목록을 찾을 수 없습니다");
            }

            // 3. 영상들의 통계 정보 가져오기
            List<YouTubeVideoDto.VideoItem> videos = getVideosStatistics(videoIds);

            // 4. 평균 계산
            long totalLikes = 0;
            long totalComments = 0;
            int validVideos = 0;

            for (YouTubeVideoDto.VideoItem video : videos) {
                if (video.getStatistics() != null) {
                    try {
                        long likes = video.getStatistics().getLikeCount() != null
                                ? Long.parseLong(video.getStatistics().getLikeCount())
                                : 0;
                        long comments = video.getStatistics().getCommentCount() != null
                                ? Long.parseLong(video.getStatistics().getCommentCount())
                                : 0;

                        totalLikes += likes;
                        totalComments += comments;
                        validVideos++;
                    } catch (NumberFormatException e) {
                        log.warn("통계 데이터 파싱 실패 - videoId: {}", video.getId());
                    }
                }
            }

            long avgLikeCount = validVideos > 0 ? totalLikes / validVideos : 0;
            long avgCommentCount = validVideos > 0 ? totalComments / validVideos : 0;

            log.info("YouTube 채널 영상 평균 통계 조회 완료 - channelId: {}, validVideos: {}, avgLikes: {}, avgComments: {}",
                    youtubeChannelId, validVideos, avgLikeCount, avgCommentCount);

            return new long[] { avgLikeCount, avgCommentCount };

        } catch (Exception e) {
            log.error("YouTube 채널 영상 평균 통계 조회 실패 - channelId: {}", youtubeChannelId, e);
            throw new BusinessException(ErrorCode.INTERNAL_SERVER_ERROR,
                    "YouTube 채널 영상 평균 통계 조회에 실패했습니다: " + e.getMessage());
        }
    }

    /**
     * 채널의 업로드 플레이리스트 ID 가져오기
     */
    private String getUploadsPlaylistId(String youtubeChannelId) {
        try {
            String url = UriComponentsBuilder.fromHttpUrl(YOUTUBE_API_BASE_URL + "/channels")
                    .queryParam("part", "contentDetails")
                    .queryParam("id", youtubeChannelId)
                    .queryParam("key", youTubeApiConfig.getYoutubeApiKey())
                    .toUriString();

            String response = restTemplate.getForObject(url, String.class);
            if (response == null)
                throw new BusinessException(ErrorCode.INTERNAL_SERVER_ERROR, "YouTube API 응답이 null입니다");

            JsonNode rootNode = objectMapper.readTree(response);
            JsonNode itemsNode = rootNode.get("items");

            if (itemsNode != null && itemsNode.size() > 0) {
                JsonNode contentDetails = itemsNode.get(0).get("contentDetails");
                if (contentDetails != null) {
                    JsonNode relatedPlaylists = contentDetails.get("relatedPlaylists");
                    if (relatedPlaylists != null) {
                        JsonNode uploads = relatedPlaylists.get("uploads");
                        return uploads != null ? uploads.asText() : null;
                    }
                }
            }
            throw new BusinessException(ErrorCode.ENTITY_NOT_FOUND, "업로드 플레이리스트를 찾을 수 없습니다");
        } catch (Exception e) {
            log.error("업로드 플레이리스트 ID 조회 실패", e);
            throw new BusinessException(ErrorCode.INTERNAL_SERVER_ERROR, "업로드 플레이리스트 ID 조회에 실패했습니다: " + e.getMessage());
        }
    }

    /**
     * 플레이리스트에서 영상 ID 목록 가져오기
     */
    private List<String> getVideoIdsFromPlaylist(String playlistId, int maxResults) {
        try {
            String url = UriComponentsBuilder.fromHttpUrl(YOUTUBE_API_BASE_URL + "/playlistItems")
                    .queryParam("part", "contentDetails")
                    .queryParam("playlistId", playlistId)
                    .queryParam("maxResults", Math.min(maxResults, 50))
                    .queryParam("order", "date")
                    .queryParam("key", youTubeApiConfig.getYoutubeApiKey())
                    .toUriString();

            String response = restTemplate.getForObject(url, String.class);
            if (response == null)
                throw new BusinessException(ErrorCode.INTERNAL_SERVER_ERROR, "YouTube API 응답이 null입니다");

            JsonNode rootNode = objectMapper.readTree(response);
            JsonNode itemsNode = rootNode.get("items");

            List<String> videoIds = new java.util.ArrayList<>();
            if (itemsNode != null) {
                for (JsonNode item : itemsNode) {
                    JsonNode contentDetails = item.get("contentDetails");
                    if (contentDetails != null) {
                        JsonNode videoId = contentDetails.get("videoId");
                        if (videoId != null) {
                            videoIds.add(videoId.asText());
                        }
                    }
                }
            }
            return videoIds;
        } catch (Exception e) {
            log.error("플레이리스트 영상 ID 조회 실패", e);
            throw new BusinessException(ErrorCode.INTERNAL_SERVER_ERROR, "플레이리스트 영상 ID 조회에 실패했습니다: " + e.getMessage());
        }
    }

    /**
     * 영상 ID 목록으로 영상 통계 정보 가져오기
     */
    private List<YouTubeVideoDto.VideoItem> getVideosStatistics(List<String> videoIds) {
        try {
            String videoIdsParam = String.join(",", videoIds);

            String url = UriComponentsBuilder.fromHttpUrl(YOUTUBE_API_BASE_URL + "/videos")
                    .queryParam("part", "statistics")
                    .queryParam("id", videoIdsParam)
                    .queryParam("key", youTubeApiConfig.getYoutubeApiKey())
                    .toUriString();

            YouTubeVideoDto response = restTemplate.getForObject(url, YouTubeVideoDto.class);
            return response != null && response.getItems() != null ? response.getItems() : List.of();
        } catch (Exception e) {
            log.error("영상 통계 정보 조회 실패", e);
            throw new BusinessException(ErrorCode.INTERNAL_SERVER_ERROR, "영상 통계 정보 조회에 실패했습니다: " + e.getMessage());
        }
    }

    /**
     * YouTube 채널의 배너 URL 가져오기
     * 
     * @param youtubeChannelId YouTube 채널 ID
     * @return 배너 URL (없으면 null)
     */
    public String getChannelBannerUrl(String youtubeChannelId) {
        log.info("YouTube 채널 배너 URL 조회 시작 - channelId: {}", youtubeChannelId);

        try {
            String url = UriComponentsBuilder.fromHttpUrl(YOUTUBE_API_BASE_URL + "/channels")
                    .queryParam("part", "brandingSettings")
                    .queryParam("id", youtubeChannelId)
                    .queryParam("key", youTubeApiConfig.getYoutubeApiKey())
                    .toUriString();

            YouTubeChannelDto response = restTemplate.getForObject(url, YouTubeChannelDto.class);

            if (response != null && response.getItems() != null && !response.getItems().isEmpty()) {
                YouTubeChannelDto.ChannelItem channel = response.getItems().get(0);
                if (channel.getBrandingSettings() != null &&
                        channel.getBrandingSettings().getImage() != null) {
                    String bannerUrl = channel.getBrandingSettings().getImage().getBannerExternalUrl();

                    return bannerUrl;
                }
            }

            return null;
        } catch (Exception e) {
            log.error("채널 배너 정보 가져오는 중 에러 발생----{}", e);
            return null;
        }
    }
}