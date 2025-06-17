package com.Gritty.Linki.domain.user.advertiser.channel.controller;

import com.Gritty.Linki.domain.user.advertiser.channel.request.ChannelSearchRequest;
import com.Gritty.Linki.domain.user.advertiser.channel.response.ChannelListResponse;
import com.Gritty.Linki.domain.user.advertiser.channel.service.ChannelService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 채널 검색 관련 API 컨트롤러
 * 검색 필터를 통한 채널 조회 기능 제공
 */
@Slf4j
@Controller
@RequestMapping("/v1/api")
@RequiredArgsConstructor
public class ChannelController {

    private final ChannelService channelService;

    /**
     * 채널 조회 (검색 필터 포함)
     * 키워드, 카테고리, 구독자 수, 평균 조회수 등의 필터를 적용하여 채널을 검색
     * 
     * @param keyword         검색 키워드 (선택)
     * @param category        카테고리 (선택)
     * @param minSubscribers  최소 구독자 수 (기본값: 0)
     * @param maxSubscribers  최대 구독자 수 (기본값: Long.MAX_VALUE)
     * @param minAvgViewCount 최소 평균 조회수 (기본값: 0)
     * @param maxAvgViewCount 최대 평균 조회수 (기본값: Long.MAX_VALUE)
     * @param page            페이지 번호 (기본값: 0)
     * @param limit           페이지 크기 (기본값: 10)
     * @return 필터링된 채널 목록
     */
    @GetMapping("/nonuser/channels")
    @ResponseBody
    public ResponseEntity<List<ChannelListResponse>> getChannels(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String category,
            @RequestParam(defaultValue = "0") long minSubscribers,
            @RequestParam(defaultValue = "9223372036854775807") long maxSubscribers,
            @RequestParam(defaultValue = "0") long minAvgViewCount,
            @RequestParam(defaultValue = "9223372036854775807") long maxAvgViewCount,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int limit) {

        log.info("채널 검색 요청: keyword={}, category={}, minSubscribers={}, maxSubscribers={}, " +
                "minAvgViewCount={}, maxAvgViewCount={}, page={}, limit={}",
                keyword, category, minSubscribers, maxSubscribers,
                minAvgViewCount, maxAvgViewCount, page, limit);

        // Request DTO 생성
        ChannelSearchRequest request = ChannelSearchRequest.builder()
                .keyword(keyword)
                .category(category)
                .minSubscribers(minSubscribers)
                .maxSubscribers(maxSubscribers)
                .minAvgViewCount(minAvgViewCount)
                .maxAvgViewCount(maxAvgViewCount)
                .page(page)
                .limit(limit)
                .build();

        // Service 호출하여 채널 검색
        List<ChannelListResponse> channels = channelService.searchChannels(request);

        return ResponseEntity.ok(channels);
    }
}