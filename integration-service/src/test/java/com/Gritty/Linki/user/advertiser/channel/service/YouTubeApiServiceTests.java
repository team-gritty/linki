package com.Gritty.Linki.user.advertiser.channel.service;

import com.Gritty.Linki.domain.user.advertiser.channel.dto.YouTubeChannelDto;
import com.Gritty.Linki.domain.user.advertiser.channel.service.YouTubeApiService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * YouTubeApiService 테스트
 */
@SpringBootTest
@Transactional
public class YouTubeApiServiceTests {

    @Autowired
    private YouTubeApiService youTubeApiService;

    String testChannelId = "CH0001";
    String testChannelId2 = "CH0002";
    String testYouTubeChannelId = "UCX6OQ3DkcsbYNE6H8uQQuVA"; // 실제 유튜브 채널 ID 예시
    String testYouTubeChannelId2 = "UC_x5XG1OV2P6uZZ5FSM9Ttw"; // 구글 개발자 채널
    String testKeyword = "개발";
    String testKeyword2 = "음악";

    @Test
    @DisplayName("키워드로 유튜브 채널 검색 Service Test")
    public void searchChannels() {
        // given & when
        List<String> searchResults1 = youTubeApiService.searchChannels(testKeyword, 5);
        List<String> searchResults2 = youTubeApiService.searchChannels(testKeyword2, 3);

        // then
        assertThat(searchResults1).isNotNull();
        assertThat(searchResults2).isNotNull();
        assertThat(searchResults1.size()).isLessThanOrEqualTo(5);
        assertThat(searchResults2.size()).isLessThanOrEqualTo(3);
    }

    @Test
    @DisplayName("채널 ID 목록으로 상세 정보 조회 Service Test")
    public void getChannelsDetails() {
        // given
        List<String> channelIds = youTubeApiService.searchChannels(testKeyword, 3);

        // when
        List<YouTubeChannelDto.ChannelItem> channelDetails = youTubeApiService.getChannelsDetails(channelIds);

        // then
        assertThat(channelDetails).isNotNull();
        if (!channelIds.isEmpty()) {
            assertThat(channelDetails.size()).isLessThanOrEqualTo(channelIds.size());
        }
    }

    @Test
    @DisplayName("빈 채널 ID 목록으로 상세 정보 조회 Service Test")
    public void getChannelsDetailsWithEmptyList() {
        // given & when
        List<YouTubeChannelDto.ChannelItem> emptyResult = youTubeApiService.getChannelsDetails(Arrays.asList());
        List<YouTubeChannelDto.ChannelItem> nullResult = youTubeApiService.getChannelsDetails(null);

        // then
        assertThat(emptyResult).isNotNull().isEmpty();
        assertThat(nullResult).isNotNull().isEmpty();
    }

    @Test
    @DisplayName("유튜브 채널 ID로 구독자 수 조회 Service Test")
    public void getSubscriberCount() {
        // given & when
        Long subscriberCount1 = youTubeApiService.getSubscriberCount(testYouTubeChannelId);
        Long subscriberCount2 = youTubeApiService.getSubscriberCount(testYouTubeChannelId2);

        // then
        assertThat(subscriberCount1).isNotNull().isGreaterThanOrEqualTo(0L);
        assertThat(subscriberCount2).isNotNull().isGreaterThanOrEqualTo(0L);
    }

    @Test
    @DisplayName("존재하지 않는 유튜브 채널 ID로 구독자 수 조회 Service Test")
    public void getSubscriberCountForNonExistentChannel() {
        // given
        String invalidChannelId = "UCinvalidchannelid12345";

        // when
        Long subscriberCount = youTubeApiService.getSubscriberCount(invalidChannelId);

        // then
        assertThat(subscriberCount).isNotNull().isEqualTo(0L);
    }

    @Test
    @DisplayName("내부 채널 ID를 유튜브 채널 ID로 변환 Service Test")
    public void getYouTubeChannelId() {
        // given & when
        String youtubeChannelId1 = youTubeApiService.getYouTubeChannelId(testChannelId);
        String youtubeChannelId2 = youTubeApiService.getYouTubeChannelId(testChannelId2);
        String youtubeChannelId3 = youTubeApiService.getYouTubeChannelId("CH9999");

        // then
        // 결과는 null이거나 유효한 채널 ID여야 함
        if (youtubeChannelId1 != null) {
            assertThat(youtubeChannelId1).isNotEmpty();
        }
        if (youtubeChannelId2 != null) {
            assertThat(youtubeChannelId2).isNotEmpty();
        }
        // 존재하지 않는 채널 ID는 null이어야 함
        assertThat(youtubeChannelId3).isNull();
    }
}