package com.Gritty.Linki.user.advertiser.channel.service;

import com.Gritty.Linki.domain.user.advertiser.channel.dto.SubscriberHistoryDto;
import com.Gritty.Linki.domain.user.advertiser.channel.service.SubscriberHistoryService;
import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * SubscriberHistoryService 테스트
 */
@SpringBootTest
@Log4j2
@Transactional
public class SubscriberHistoryServiceTests {

    @Autowired
    private SubscriberHistoryService subscriberHistoryService;

    // 디비에 저장된 IdGenerator로 생성된 채널 데이터
    String testChannelId = "CH0aa04642885f46c897cfab51";
    String testChannelId2 = "CH0a681f104be04e1bbbd0e67";
    Long testSubscriberCount = 50000L;

    @Test
    @DisplayName("특정 채널의 구독자 히스토리 조회 Service Test")
    public void getSubscriberHistory() {
        log.info("구독자 히스토리 조회 테스트 시작");

        try {
            // 30일 히스토리 조회
            List<SubscriberHistoryDto> history30Days = subscriberHistoryService.getSubscriberHistory(testChannelId, 30);
            log.info("30일 구독자 히스토리 조회 완료 - 채널: {}, 데이터 수: {}", testChannelId, history30Days.size());

            for (SubscriberHistoryDto history : history30Days) {
                log.info("히스토리: ID={}, 채널={}, 구독자수={}, 수집시간={}",
                        history.getId(), history.getChannelId(), history.getSubscriberCount(),
                        history.getCollectedAt());
            }

            // 15일 히스토리 조회
            List<SubscriberHistoryDto> history15Days = subscriberHistoryService.getSubscriberHistory(testChannelId, 15);
            log.info("15일 구독자 히스토리 조회 완료 - 채널: {}, 데이터 수: {}", testChannelId, history15Days.size());

        } catch (Exception e) {
            log.info("구독자 히스토리 조회 실패: {}", e.getMessage());
        }
    }

    @Test
    @DisplayName("기본값(30일) 구독자 히스토리 조회 Service Test")
    public void getSubscriberHistoryWithDefaults() {
        log.info("기본값 구독자 히스토리 조회 테스트 시작");

        try {
            // null 값 전달 시 기본값 30일 적용 테스트
            List<SubscriberHistoryDto> historyDefault = subscriberHistoryService.getSubscriberHistory(testChannelId2,
                    null);
            log.info("기본값 구독자 히스토리 조회 완료 - 채널: {}, 데이터 수: {}", testChannelId2, historyDefault.size());

            // 0일 전달 시 기본값 30일 적용 테스트
            List<SubscriberHistoryDto> historyZero = subscriberHistoryService.getSubscriberHistory(testChannelId2, 0);
            log.info("0일 전달 시 구독자 히스토리 조회 완료 - 채널: {}, 데이터 수: {}", testChannelId2, historyZero.size());

        } catch (Exception e) {
            log.info("기본값 구독자 히스토리 조회 실패: {}", e.getMessage());
        }
    }

    @Test
    @DisplayName("구독자 히스토리 저장 Service Test")
    public void saveSubscriberHistory() {
        log.info("구독자 히스토리 저장 테스트 시작");

        try {
            String savedId = subscriberHistoryService.saveSubscriberHistory(testChannelId, testSubscriberCount);
            log.info("구독자 히스토리 저장 완료 - 저장된 ID: {}, 채널: {}, 구독자수: {}",
                    savedId, testChannelId, testSubscriberCount);

            // 다른 채널에도 저장 테스트
            Long testSubscriberCount2 = 75000L;
            String savedId2 = subscriberHistoryService.saveSubscriberHistory(testChannelId2, testSubscriberCount2);
            log.info("구독자 히스토리 저장 완료 - 저장된 ID: {}, 채널: {}, 구독자수: {}",
                    savedId2, testChannelId2, testSubscriberCount2);

        } catch (Exception e) {
            log.info("구독자 히스토리 저장 실패: {}", e.getMessage());
        }
    }

    @Test
    @DisplayName("모든 채널 구독자 수 업데이트 Service Test")
    public void updateAllChannelsSubscriberCount() {
        log.info("모든 채널 구독자 수 업데이트 테스트 시작");

        try {
            subscriberHistoryService.updateAllChannelsSubscriberCount();
            log.info("모든 채널 구독자 수 업데이트 완료");

        } catch (Exception e) {
            log.info("모든 채널 구독자 수 업데이트 실패: {}", e.getMessage());
        }
    }
}