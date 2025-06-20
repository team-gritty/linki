package com.Gritty.Linki.domain.user.advertiser.channel.service;

import com.Gritty.Linki.domain.user.advertiser.channel.dto.YouTubeChannelDto;
import com.Gritty.Linki.domain.user.advertiser.channel.repository.jpa.ChannelSearchRepository;
import com.Gritty.Linki.entity.Channel;
import com.Gritty.Linki.entity.Influencer;
import com.Gritty.Linki.exception.BusinessException;
import com.Gritty.Linki.exception.ErrorCode;
import com.Gritty.Linki.repository.InfluencerRepository;
import com.Gritty.Linki.util.IdGenerator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * YouTube 채널 수집 및 저장을 담당하는 서비스 클래스
 * 키워드 검색을 통해 채널을 찾고, 상세 정보를 수집하여 데이터베이스에 저장
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class YouTubeChannelCollectService {

    private final YouTubeApiService youTubeApiService;
    private final ChannelSearchRepository channelRepository;
    private final InfluencerRepository influencerRepository;
    private final AtomicInteger counter = new AtomicInteger(0);

    /**
     * 키워드와 카테고리 기반으로 YouTube 채널을 검색하고 테이블에 insert수집하기
     */
    public void collectChannels(String keyword, String category, Integer maxResults) {
        log.info("YouTube 채널 수집 시작: keyword={}, category={}, maxResults={}",
                keyword, category, maxResults);

        try {
            // 1. YouTube API를 통해 채널 검색
            List<String> channelIds = youTubeApiService.searchChannels(keyword, maxResults);

            if (channelIds.isEmpty()) {
                log.warn("검색 결과가 없습니다: keyword={}", keyword);
                return;
            }

            // 2. 채널 상세 정보 조회
            List<YouTubeChannelDto.ChannelItem> channelDetails = youTubeApiService.getChannelsDetails(channelIds);

            // 3. 채널 정보를 데이터베이스에 저장 (각각 별도 트랜잭션으로)
            for (YouTubeChannelDto.ChannelItem channelItem : channelDetails) {
                try {
                    saveChannelSafely(channelItem, category);
                } catch (Exception e) {
                    log.error("채널 처리 중 오류 발생: channelId={}, error={}",
                            channelItem.getId(), e.getMessage(), e);
                    // 개별 채널 처리 실패는 전체 프로세스를 중단시키지 않음
                }
            }

            log.info("YouTube 채널 수집 완료: keyword={}", keyword);

        } catch (Exception e) {
            log.error("YouTube 채널 수집 실패: keyword={}, error={}",
                    keyword, e.getMessage(), e);
            throw new BusinessException(ErrorCode.INTERNAL_SERVER_ERROR,
                    "채널 수집 중 오류가 발생했습니다: " + e.getMessage());
        }
    }

    /**
     * 개별 채널을 안전하게 저장하기 (별도 트랜잭션)
     */
    @Transactional
    protected void saveChannelSafely(YouTubeChannelDto.ChannelItem channelItem, String category) {
        try {
            if (!channelRepository.existsByYoutubeChannelId(channelItem.getId())) {
                // 새로운 채널인 경우 저장
                Channel channel = createChannelFromYouTubeData(channelItem, category);
                channelRepository.save(channel);
                log.info("새 채널 저장 완료: channelId={}, name={}",
                        channel.getChannelId(), channel.getChannelName());
            } else {
                log.debug("이미 존재하는 채널: channelId={}", channelItem.getId());
            }
        } catch (Exception e) {
            log.error("채널 저장 중 오류: channelId={}, error={}", channelItem.getId(), e.getMessage());
            throw e; // 트랜잭션 롤백을 위해 예외를 다시 던짐
        }
    }

    /**
     * YouTube 채널 데이터를 Channel 엔티티로 변환
     */
    private Channel createChannelFromYouTubeData(YouTubeChannelDto.ChannelItem channelItem, String category) {
        YouTubeChannelDto.Snippet snippet = channelItem.getSnippet();
        YouTubeChannelDto.Statistics statistics = channelItem.getStatistics();

        // 순차적으로 인플루언서 ID 할당 (INF0000부터 시작, 순환)
        // 인플루언서가 이미 플랫폼에 가입했다고 가정하고 유튜브로 부터 데이터 수집하는 것이기 때문에 더미데이터의 influencer id를 채널
        // 테이블에 넣어주기
        int currentCount = counter.getAndIncrement() % 500;
        String influencerId = String.format("INF%04d", currentCount);

        // 인플루언서 조회 시도 (인플루언서 respository 사용)
        // influencer id 찾아 더미데이터로 들어간 인플루언서의 채널을 넣기위해
        Influencer influencer = influencerRepository.findById(influencerId)
                .orElseThrow(() -> {
                    log.error("인플루언서를 찾을 수 없음: influencerId={}", influencerId);
                    return new BusinessException(ErrorCode.ENTITY_NOT_FOUND,
                            "인플루언서를 찾을 수 없습니다: " + influencerId);
                });

        try {
            return Channel.builder()
                    .channelId(generateChannelId())
                    .channelName(snippet.getTitle())
                    .youtubeChannelId(channelItem.getId())
                    .channelUrl("https://www.youtube.com/channel/" + channelItem.getId())
                    .channelCategory(category)
                    .channelCountry(snippet.getCountry() != null ? snippet.getCountry() : "Unknown")
                    .channelDescription(snippet.getDescription())
                    .channelThumbnailUrl(getThumbnailUrl(snippet.getThumbnails()))
                    .subscriberCount(parseLong(statistics.getSubscriberCount()))
                    .videoCount(parseInt(statistics.getVideoCount()))
                    .viewCount(parseLong(statistics.getViewCount()))
                    .channelCreatedAt(parseDateTime(snippet.getPublishedAt()))
                    .likeCount(0L)
                    .commentCount(0L)
                    .influencer(influencer)
                    .collectedAt(LocalDateTime.now())
                    .build();
        } catch (Exception e) {
            log.error("채널 엔티티 생성 중 오류 발생: channelId={}, error={}",
                    channelItem.getId(), e.getMessage());
            throw new BusinessException(ErrorCode.INTERNAL_SERVER_ERROR,
                    "채널 데이터 처리 중 오류가 발생했습니다: " + e.getMessage());
        }
    }

    /**
     * 채널 ID 생성
     */
    private String generateChannelId() {
        return IdGenerator.channelId();
    }

    /**
     * 썸네일 URL 추출
     */
    private String getThumbnailUrl(YouTubeChannelDto.Thumbnails thumbnails) {
        if (thumbnails == null || thumbnails.getDefaultThumbnail() == null) {
            return null;
        }
        return thumbnails.getDefaultThumbnail().getUrl();
    }

    /**
     * 문자열을 Long으로 안전하게 변환
     */
    private Long parseLong(String value) {
        try {
            return value != null ? Long.parseLong(value) : 0L;
        } catch (NumberFormatException e) {
            log.warn("숫자 변환 실패: value={}", value);
            return 0L;
        }
    }

    /**
     * 문자열을 Integer로 안전하게 변환
     */
    private Integer parseInt(String value) {
        try {
            return value != null ? Integer.parseInt(value) : 0;
        } catch (NumberFormatException e) {
            log.warn("숫자 변환 실패: value={}", value);
            return 0;
        }
    }

    /**
     * ISO 8601 날짜 문자열을 LocalDateTime으로 변환
     */
    private LocalDateTime parseDateTime(String dateTimeString) {
        try {
            if (dateTimeString == null)
                return LocalDateTime.now();

            // YouTube API는 ISO 8601 형식으로 날짜를 반환 (예: 2023-01-01T00:00:00Z)
            return LocalDateTime.parse(dateTimeString.replace("Z", ""),
                    DateTimeFormatter.ISO_LOCAL_DATE_TIME);
        } catch (Exception e) {
            log.warn("날짜 파싱 실패: dateTimeString={}, error={}", dateTimeString, e.getMessage());
            return LocalDateTime.now();
        }
    }
}
