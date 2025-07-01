package com.Gritty.Linki.user.advertiser.channel.service;

import com.Gritty.Linki.domain.user.advertiser.channel.request.ChannelSearchRequest;
import com.Gritty.Linki.domain.user.advertiser.channel.response.ChannelListResponse;
import com.Gritty.Linki.domain.user.advertiser.channel.service.ChannelService;
import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;
//
//@SpringBootTest
//@Log4j2
//public class ChannelServiceTests {
//
//    @Autowired
//    private ChannelService channelService;
//
//    @Test
//    @DisplayName("채널 검색 Service Test")
//    public void searchChannels() {
//        // keyword만 있는 경우
//        log.info("keyword만 있는 경우");
//        ChannelSearchRequest request1 = ChannelSearchRequest.builder()
//                .keyword("channel")
//                .page(0)
//                .limit(10)
//                .build();
//        List<ChannelListResponse> result1 = channelService.searchChannels(request1);
//        log.info("List : {}", result1.size());
//
//        // category만 있는 경우
//        log.info("category만 있는 경우");
//        ChannelSearchRequest request2 = ChannelSearchRequest.builder()
//                .category("게임")
//                .page(0)
//                .limit(10)
//                .build();
//        List<ChannelListResponse> result2 = channelService.searchChannels(request2);
//        log.info("List : {}", result2.size());
//
//        // keyword, category 둘 다 없는 경우
//        log.info("둘 다 없는 경우");
//        ChannelSearchRequest request3 = ChannelSearchRequest.builder()
//                .page(0)
//                .limit(10)
//                .build();
//        List<ChannelListResponse> result3 = channelService.searchChannels(request3);
//        log.info("List : {}", result3.size());
//
//        // category = 게임, keyword = 채널1 검색
//        log.info("게임 카테고리에서 채널1 검색");
//        ChannelSearchRequest request4 = ChannelSearchRequest.builder()
//                .keyword("채널1")
//                .category("게임")
//                .page(0)
//                .limit(10)
//                .build();
//        List<ChannelListResponse> result4 = channelService.searchChannels(request4);
//        log.info("List : {}", result4.size());
//
//        // 구독자 수 범위 검색
//        log.info("구독자 수 범위 검색");
//        ChannelSearchRequest request5 = ChannelSearchRequest.builder()
//                .minSubscribers(1000L)
//                .maxSubscribers(100000L)
//                .page(0)
//                .limit(10)
//                .build();
//        List<ChannelListResponse> result5 = channelService.searchChannels(request5);
//        log.info("List : {}", result5.size());
//
//        // 평균 조회수 범위 검색
//        log.info("평균 조회수 범위 검색");
//        ChannelSearchRequest request6 = ChannelSearchRequest.builder()
//                .minAvgViewCount(10000L)
//                .maxAvgViewCount(1000000L)
//                .page(0)
//                .limit(10)
//                .build();
//        List<ChannelListResponse> result6 = channelService.searchChannels(request6);
//        log.info("List : {}", result6.size());
//
//        // 복합 검색 (키워드 + 카테고리 + 구독자 수)
//        log.info("복합 검색");
//        ChannelSearchRequest request7 = ChannelSearchRequest.builder()
//                .keyword("메이크업")
//                .category("뷰티")
//                .minSubscribers(5000L)
//                .maxSubscribers(500000L)
//                .page(0)
//                .limit(10)
//                .build();
//        List<ChannelListResponse> result7 = channelService.searchChannels(request7);
//        log.info("List : {}", result7.size());
//    }
//}