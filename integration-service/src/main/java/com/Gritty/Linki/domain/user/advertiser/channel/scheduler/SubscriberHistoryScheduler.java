package com.Gritty.Linki.domain.user.advertiser.channel.scheduler;

import com.Gritty.Linki.domain.user.advertiser.channel.service.SubscriberHistoryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * 구독자 히스토리 업데이트 스케줄러
 * 매일 새벽3시에 모든 채널의 구독자 수를 업데이트
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class SubscriberHistoryScheduler {

    private final SubscriberHistoryService subscriberHistoryService;
    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    /**
     * 매일 자정에 모든 채널의 구독자 수 업데이트
     * cron: 초 분 시 일 월 요일
     * 0 0 3 * * *: 매일 새벽 3시
     */
    // @Scheduled(cron = "0 0 3 * * *") // 운영용 - 운영 시 주석 해제
    public void updateSubscriberCount() {
        log.info("=== 운영용 구독자 수 업데이트 스케줄러 시작 ===");

        try {
            subscriberHistoryService.updateAllChannelsSubscriberCount();
            log.info("=== 운영용 구독자 수 업데이트 스케줄러 완료 ===");
        } catch (Exception e) {
            log.error("운영용 구독자 수 업데이트 스케줄러 실행 중 오류 발생", e);
        }
    }

    /**
     * 테스트용 - 매 90초마다 실행 (운영 시 주석 처리)
     */
     //@Scheduled(fixedRate = 90000) // 테스트용 - 운영 시 주석 처리
    public void updateSubscriberCountForTest() {
        String currentTime = LocalDateTime.now().format(formatter);

        log.info("----------[{}] 테스트용 구독자 수 업데이트 스케줄러 시작", currentTime);
        log.info("=== 스케줄러 실행 확인용 로그 ===");

        try {
            subscriberHistoryService.updateAllChannelsSubscriberCount();

            String completedTime = LocalDateTime.now().format(formatter);
            log.info(" [{}] 테스트용 구독자 수 업데이트 스케줄러 완료", completedTime);

        } catch (Exception e) {
            String errorTime = LocalDateTime.now().format(formatter);
            log.error(" [{}] 테스트용 구독자 수 업데이트 스케줄러 실행 중 오류 발생", errorTime, e);
        }
    }

    /**
     * 스케줄러 상태 확인용 - 운영 시에도 유지 (주기는 조정 가능)
     */
    // @Scheduled(fixedRate = 300000) // 5분마다 실행 - 운영용 모니터링
    public void heartbeat() {
        String currentTime = LocalDateTime.now().format(formatter);
        log.debug("[{}] 스케줄러 정상----------- 스케줄러가 정상 작동 중입니다", currentTime);
    }
}