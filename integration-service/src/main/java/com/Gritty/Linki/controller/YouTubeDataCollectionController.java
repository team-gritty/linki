package com.Gritty.Linki.controller;

import com.Gritty.Linki.dto.response.ChannelResponse;
import com.Gritty.Linki.entity.CollectedChannel;
import com.Gritty.Linki.entity.SubscriberHistory;
import com.Gritty.Linki.repository.CollectedChannelRepository;
import com.Gritty.Linki.repository.SubscriberHistoryRepository;
import com.Gritty.Linki.service.YouTubeChannelCollectionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * YouTube 채널 데이터 수집 컨트롤러
 */
@RestController
@RequestMapping("/api/v1/youtube")
@RequiredArgsConstructor
@Slf4j
public class YouTubeDataCollectionController {

    private final YouTubeChannelCollectionService youtubeChannelCollectionService;
    private final CollectedChannelRepository collectedChannelRepository;
    private final SubscriberHistoryRepository subscriberHistoryRepository;

    /**
     * 키워드로 채널 검색하고 저장
     * POST /api/v1/youtube/channels/search?keyword=뷰티&category=BEAUTY&maxResults=15
     */
    @PostMapping("/channels/search")
    public ResponseEntity<List<ChannelResponse>> searchAndSaveChannels(
            @RequestParam String keyword,
            @RequestParam String category,
            @RequestParam(defaultValue = "15") Integer maxResults) {

        log.info("채널 검색 및 저장 요청: keyword={}, category={}, maxResults={}", keyword, category, maxResults);

        List<CollectedChannel> channels = youtubeChannelCollectionService.searchAndSaveChannels(keyword, category,
                maxResults);
        List<ChannelResponse> response = channels.stream()
                .map(ChannelResponse::from)
                .collect(Collectors.toList());

        return ResponseEntity.ok(response);
    }

    /**
     * 수집된 모든 채널 조회
     * GET /api/youtube/collected-channels
     */
    @GetMapping("/collected-channels")
    public ResponseEntity<List<CollectedChannel>> getAllCollectedChannels() {
        List<CollectedChannel> channels = collectedChannelRepository.findRecentlyCollected();
        return ResponseEntity.ok(channels);
    }

    /**
     * 카테고리별 채널 조회
     * GET /api/youtube/collected-channels/category/{category}
     */
    @GetMapping("/collected-channels/category/{category}")
    public ResponseEntity<List<CollectedChannel>> getChannelsByCategory(@PathVariable String category) {
        List<CollectedChannel> channels = collectedChannelRepository
                .findByChannelCategoryOrderByCollectedAtDesc(category);
        return ResponseEntity.ok(channels);
    }

    /**
     * 특정 채널의 구독자 히스토리 조회
     * GET /api/youtube/subscriber-history/{channelId}
     */
    @GetMapping("/subscriber-history/{channelId}")
    public ResponseEntity<List<SubscriberHistory>> getSubscriberHistory(@PathVariable String channelId) {
        List<SubscriberHistory> history = subscriberHistoryRepository
                .findByCollectedChannelCollectedChannelIdOrderBySnapshotDateDesc(channelId);
        return ResponseEntity.ok(history);
    }

    /**
     * 구독자 수 업데이트 (기존 채널들)
     * POST /api/v1/youtube/channels/update
     */
    @PostMapping("/channels/update")
    public ResponseEntity<Void> updateSubscriberCounts() {
        log.info("채널 구독자 수 업데이트 요청");
        youtubeChannelCollectionService.updateSubscriberCounts();
        return ResponseEntity.ok().build();
    }

    /**
     * 검색 키워드별 채널 조회
     * GET /api/youtube/collected-channels/keyword/{keyword}
     */
    @GetMapping("/collected-channels/keyword/{keyword}")
    public ResponseEntity<List<CollectedChannel>> getChannelsByKeyword(@PathVariable String keyword) {
        List<CollectedChannel> channels = collectedChannelRepository
                .findBySearchKeywordOrderByCollectedAtDesc(keyword);
        return ResponseEntity.ok(channels);
    }

    /**
     * 구독자 수 범위로 채널 조회
     * GET /api/youtube/collected-channels/subscribers?min=1000&max=100000
     */
    @GetMapping("/collected-channels/subscribers")
    public ResponseEntity<List<CollectedChannel>> getChannelsBySubscriberRange(
            @RequestParam(defaultValue = "0") Long min,
            @RequestParam(defaultValue = "10000000") Long max) {

        List<CollectedChannel> channels = collectedChannelRepository
                .findBySubscriberCountRange(min, max);
        return ResponseEntity.ok(channels);
    }

    /**
     * 수집 통계 조회
     * GET /api/youtube/collection-stats
     */
    @GetMapping("/collection-stats")
    public ResponseEntity<Map<String, Object>> getCollectionStats() {
        long totalChannels = collectedChannelRepository.count();
        long totalHistory = subscriberHistoryRepository.count();

        Map<String, Object> stats = new HashMap<>();
        stats.put("totalChannels", totalChannels);
        stats.put("totalHistoryRecords", totalHistory);

        return ResponseEntity.ok(stats);
    }
}