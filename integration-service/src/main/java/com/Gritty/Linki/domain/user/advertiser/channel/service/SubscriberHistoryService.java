package com.Gritty.Linki.domain.user.advertiser.channel.service;

import com.Gritty.Linki.domain.user.advertiser.channel.dto.SubscriberHistoryDto;
import com.Gritty.Linki.domain.user.advertiser.channel.repository.SubscriberHistoryRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import com.Gritty.Linki.exception.BusinessException;
import com.Gritty.Linki.exception.ErrorCode;

/**
 * 구독자 히스토리 비즈니스 로직 처리 서비스
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class SubscriberHistoryService {

    private final SubscriberHistoryRepository subscriberHistoryRepository;
    private final YouTubeApiService youTubeApiService;

    @Value("${subscriber.collection.start-channel:1007}")
    private Integer startChannelNumber;

    @Value("${subscriber.collection.test-mode:false}")
    private Boolean testMode;

    private final Random random = new Random();

    /**
     * 구독자 히스토리 조회
     * 
     * @param channelId 채널 ID (String 형식)
     * @param days      조회할 일수
     * @return 구독자 히스토리 목록
     */
    public List<SubscriberHistoryDto> getSubscriberHistory(String channelId, Integer days) {
        log.info("구독자 히스토리 조회 요청 - channelId: {}, days: {}", channelId, days);

        // 기본값 설정
        if (days == null || days <= 0) {
            days = 30;
        }

        // 날짜 범위 계산
        LocalDateTime startDate = LocalDateTime.now().minusDays(days);

        try {
            // 데이터베이스에서 구독자 히스토리 조회
            List<SubscriberHistoryDto> history = subscriberHistoryRepository.findByChannelIdAndDateRange(channelId,
                    startDate);

            if (history.isEmpty()) {
                log.warn("구독자 히스토리 데이터가 없습니다 - channelId: {}", channelId);
                return Collections.emptyList();
//                throw new BusinessException(ErrorCode.ENTITY_NOT_FOUND, "구독자 히스토리 데이터를 찾을 수 없습니다");
            }

            log.info("구독자 히스토리 조회 완료 - channelId: {}, 조회된 데이터 수: {}", channelId, history.size());
            return history;

        } catch (Exception e) {
            log.error("구독자 히스토리 조회 중 오류 발생 - channelId: {}", channelId, e);
            return Collections.emptyList();
//            throw new BusinessException(ErrorCode.INTERNAL_SERVER_ERROR, "구독자 히스토리 조회 중 오류가 발생했습니다: " + e.getMessage());
        }
    }

    /**
     * 구독자 히스토리 저장
     * 
     * @param channelId       채널 ID (String 형식)
     * @param subscriberCount 구독자 수
     * @return 저장된 히스토리 ID
     */
    public String saveSubscriberHistory(String channelId, Long subscriberCount) {
        log.info("구독자 히스토리 저장 - channelId: {}, subscriberCount: {}", channelId, subscriberCount);

        SubscriberHistoryDto dto = SubscriberHistoryDto.builder()
                .channelId(channelId)
                .subscriberCount(subscriberCount)
                .collectedAt(LocalDateTime.now())
                .build();

        String savedId = subscriberHistoryRepository.save(dto);

        log.info("구독자 히스토리 저장 완료 - id: {}", savedId);

        // 저장된 구독자 히스토리 pk 반환
        return savedId;
    }

    /**
     * 모든 채널의 구독자 수 업데이트 (스케줄러용)
     * 1007번 채널부터 수집 시작
     */
    public void updateAllChannelsSubscriberCount() {
        log.info("----------채널 구독자 수 업데이트 시작 - {}번 채널부터 수집 (테스트 모드: {})",
                startChannelNumber, testMode);

        // 1007번부터 채널 ID 조회
        List<String> channelIds = subscriberHistoryRepository.findChannelIdsFromNumber(startChannelNumber);

        log.info("--------- 수집 대상 채널 수: {}", channelIds.size());

        int successCount = 0;
        int failCount = 0;

        for (String channelId : channelIds) {
            try {
                log.info("-------- 채널 {} 구독자 수 수집 시작", channelId);

                // YouTube API 호출하여 실제 구독자 수 조회
                Long currentSubscriberCount = getCurrentSubscriberCount(channelId);

                if (currentSubscriberCount != null && currentSubscriberCount > 0) {
                    saveSubscriberHistory(channelId, currentSubscriberCount);
                    successCount++;
                    log.info("-------- 채널 {} 구독자 수 업데이트 성공: {}", channelId, currentSubscriberCount);
                } else {
                    failCount++;
                    log.warn("------- 채널 {} 구독자 수 조회 실패 또는 0", channelId);
                }
            } catch (Exception e) {
                failCount++;
                log.error("-------- 채널 {} 구독자 수 업데이트 실패", channelId, e);
            }
        }

        log.info("------------ 모든 채널 구독자 수 업데이트 완료 - 성공: {}, 실패: {}, 총: {}",
                successCount, failCount, channelIds.size());
    }

    /**
     * 현재 구독자 수 조회 (YouTube Data API 호출)
     * 
     * @param channelId 채널 ID (String 형식)
     * @return 현재 구독자 수
     */
    private Long getCurrentSubscriberCount(String channelId) {
        log.info("---------- 채널 {} 구독자 수 조회 시작 (테스트 모드: {})", channelId, testMode);

        try {
            // 테스트 모드로 설정했을 경우 아래 코드 실행
            if (testMode) {
                // 테스트 모드: 임시 구독자 수 생성
                Long mockSubscriberCount = generateMockSubscriberCount(channelId);
                log.info("---------- 테스트 모드 - 채널 {} 임시 구독자 수: {}", channelId, mockSubscriberCount);

                // 실제 API 호출을 시뮬레이션하기 위한 약간의 지연
                Thread.sleep(100);

                return mockSubscriberCount;
            }

            // 실제 데이터 수집 모드: 실제 YouTube API 호출하기
            String youtubeChannelId = youTubeApiService.getYouTubeChannelId(channelId);

            // 유튜브 채널 아이디가 널이라면 (디비 컬럼에 값이 없다면)
            if (youtubeChannelId == null) {
                log.warn(" 채널 {}에 대한 YouTube 채널 ID를 찾을 수 없습니다", channelId);
                throw new BusinessException(ErrorCode.ENTITY_NOT_FOUND, "YouTube 채널 ID를 찾을 수 없습니다");
            }

            // youtubeChannelId의 수독자 수 수집
            Long subscriberCount = youTubeApiService.getSubscriberCount(youtubeChannelId);
            log.info(" 채널 {} 구독자 수 조회 완료 - subscriberCount: {}", channelId, subscriberCount);

            // 구독자 수 반환
            return subscriberCount;
        } catch (Exception e) {
            log.error(" 채널 {} 구독자 수 조회 중 오류 발생", channelId, e);
            throw new BusinessException(ErrorCode.INTERNAL_SERVER_ERROR, "구독자 수 조회 중 오류가 발생했습니다: " + e.getMessage());
        }
    }

    /**
     * 테스트용 임시 구독자 수 생성
     */
    private Long generateMockSubscriberCount(String channelId) {
        // 채널 ID 기반으로 기본 구독자 수 설정
        int baseSubscribers = 10000;

        // 채널 ID의 해시값을 이용해 일관된 기본값 생성
        int channelHash = Math.abs(channelId.hashCode()) % 50000;
        baseSubscribers += channelHash;

        // ±5% 랜덤 변화 추가 (실제 구독자 수 변화 시뮬레이션)
        double variation = (random.nextDouble() - 0.5) * 0.1; // -5% ~ +5%
        long finalCount = Math.round(baseSubscribers * (1 + variation));

        return Math.max(finalCount, 1L); // 최소 1명
    }
}