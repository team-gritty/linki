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

    // 인플루언서 ID 카운터를 초기화할 때 현재 채널 개수를 기준으로 인플러언서 ID를 설정하도록
    private AtomicInteger influencerIdCounter;

    /**
     * 키워드와 카테고리 기반으로 YouTube 채널을 검색하고 테이블에 insert수집하기
     */
    public void collectChannels(String keyword, String category, Integer maxResults) {
        log.info("YouTube 채널 수집 시작: keyword={}, category={}, maxResults={}",
                keyword, category, maxResults);

        try {
            // 인플루언서 ID 카운터 초기화 (현재 채널 개수를 기준으로)
            initializeInfluencerIdCounter();

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
     * 인플루언서 ID 카운터 초기화
     * 현재 채널 테이블의 개수를 조회하여 다음 인플루언서 ID를 설정
     */
    private void initializeInfluencerIdCounter() {
        if (influencerIdCounter == null) {
            // 현재 채널 테이블의 총 개수 조회
            long currentChannelCount = channelRepository.count();

            // 인플루언서 ID 범위는 0-499 (500개)
            // 현재 채널 개수가 500을 넘으면 0부터 다시 시작하되, 이미 사용된 ID는 건너뛰기
            int startingId = (int) (currentChannelCount % 500);

            influencerIdCounter = new AtomicInteger(startingId);

            log.info("인플루언서 ID 카운터 초기화 완료: currentChannelCount={}, startingId={}",
                    currentChannelCount, startingId);
        }
    }

    /**
     * 다음 사용 가능한 인플루언서 ID 생성
     * 이미 채널이 할당된 인플루언서 ID는 건너뛰기
     */
    private String getNextAvailableInfluencerId() {
        int maxAttempts = 500; // 무한루프 방지
        int attempts = 0;

        while (attempts < maxAttempts) {
            int currentId = influencerIdCounter.getAndIncrement() % 500;
            String influencerId = String.format("INF-%016d", currentId);

            // 해당 인플루언서 ID가 이미 채널을 가지고 있는지 확인
            boolean hasChannel = channelRepository.existsByInfluencerInfluencerId(influencerId);

            if (!hasChannel) {
                // 인플루언서가 존재하는지 확인
                if (influencerRepository.existsById(influencerId)) {
                    log.debug("사용 가능한 인플루언서 ID 찾음: {}", influencerId);
                    return influencerId;
                } else {
                    log.warn("인플루언서가 존재하지 않음: {}", influencerId);
                }
            }

            attempts++;
        }

        throw new BusinessException(ErrorCode.INTERNAL_SERVER_ERROR,
                "사용 가능한 인플루언서 ID를 찾을 수 없습니다. 모든 인플루언서가 이미 채널을 가지고 있습니다.");
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

        // 사용 가능한 인플루언서 ID 할당 (중복 방지)
        // 각 인플루언서는 하나의 채널만 가질 수 있도록 제한
        // 인플루언서 ID 범위: INF-0000000000000000 ~ INF-0000000000000499
        String influencerId = getNextAvailableInfluencerId();

        // 인플루언서 조회 시도 (인플루언서 respository 사용)
        // influencer id 찾아 더미데이터로 들어간 인플루언서의 채널을 넣기위해
        Influencer influencer = influencerRepository.findById(influencerId)
                .orElseThrow(() -> {
                    log.error("인플루언서를 찾을 수 없음: influencerId={}", influencerId);
                    return new BusinessException(ErrorCode.ENTITY_NOT_FOUND,
                            "인플루언서를 찾을 수 없습니다: " + influencerId);
                });

        // 좋아요/댓글 통계 변수 초기화
        long avgLikeCount = 0L;
        long avgCommentCount = 0L;

        try {
            log.info("채널 수집 시점에 좋아요/댓글 통계 계산 시작 - channelId: {}", channelItem.getId());

            // YouTube API를 통해 평균 좋아요/댓글 수 계산 (최근 30개 영상 기준)
            long[] averages = youTubeApiService.getChannelVideoAverages(channelItem.getId(), 30);

            avgLikeCount = averages[0];
            avgCommentCount = averages[1];

            log.info("채널 통계 계산 완료 - channelId: {}, avgLikes: {}, avgComments: {}",
                    channelItem.getId(), avgLikeCount, avgCommentCount);

        } catch (Exception e) {
            log.warn("채널 수집 시 통계 계산 실패, 기본값 사용 - channelId: {}, error: {}", channelItem.getId(), e.getMessage());
            // 통계 계산 실패 시 기본값 0 사용
            avgLikeCount = 0L;
            avgCommentCount = 0L;
        }

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
                    .likeCount(avgLikeCount)
                    .commentCount(avgCommentCount)
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
