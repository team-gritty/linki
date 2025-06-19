package com.Gritty.Linki.user.advertiser.channel.service;

import com.Gritty.Linki.domain.user.advertiser.channel.dto.SubscriberHistoryDto;
import com.Gritty.Linki.domain.user.advertiser.channel.service.SubscriberHistoryService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * SubscriberHistoryService 테스트
 */
@SpringBootTest
@Transactional
public class SubscriberHistoryServiceTests {

    @Autowired
    private SubscriberHistoryService subscriberHistoryService;

    // 디비에 저장된 IdGenerator로 생성된 채널 데이터
    String testChannelId = "CH11c80f9bafd64c4ea9e44b2";
    String testChannelId2 = "CH0a681f104be04e1bbbd0e67";
    Long testSubscriberCount = 50000L;

    @Test
    @DisplayName("특정 채널의 구독자 히스토리 조회 Service Test")
    public void getSubscriberHistory() {
        // given & when
        List<SubscriberHistoryDto> history30Days = subscriberHistoryService.getSubscriberHistory(testChannelId, 30);
        List<SubscriberHistoryDto> history15Days = subscriberHistoryService.getSubscriberHistory(testChannelId, 15);

        // then
        assertThat(history30Days).isNotNull();
        assertThat(history15Days).isNotNull();
        assertThat(history15Days.size()).isLessThanOrEqualTo(history30Days.size());
    }

    @Test
    @DisplayName("기본값(30일) 구독자 히스토리 조회 Service Test")
    public void getSubscriberHistoryWithDefaults() {
        // given & when
        List<SubscriberHistoryDto> historyDefault = subscriberHistoryService.getSubscriberHistory(testChannelId2, null);
        List<SubscriberHistoryDto> historyZero = subscriberHistoryService.getSubscriberHistory(testChannelId2, 0);

        // then
        assertThat(historyDefault).isNotNull();
        assertThat(historyZero).isNotNull();
        assertThat(historyDefault.size()).isEqualTo(historyZero.size());
    }

    @Test
    @DisplayName("구독자 히스토리 저장 Service Test")
    public void saveSubscriberHistory() {
        // given & when
        String savedId = subscriberHistoryService.saveSubscriberHistory(testChannelId, testSubscriberCount);
        String savedId2 = subscriberHistoryService.saveSubscriberHistory(testChannelId2, 75000L);

        // then
        assertThat(savedId).isNotNull();
        assertThat(savedId2).isNotNull();
        assertThat(savedId).isNotEqualTo(savedId2);
    }

    @Test
    @DisplayName("모든 채널 구독자 수 업데이트 Service Test")
    public void updateAllChannelsSubscriberCount() {
        // given & when & then
        subscriberHistoryService.updateAllChannelsSubscriberCount();
        // 예외가 발생하지 않으면 성공
    }
}