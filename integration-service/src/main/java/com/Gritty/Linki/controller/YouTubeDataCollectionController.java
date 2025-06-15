package com.Gritty.Linki.controller;

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

/**
 * YouTube 채널 데이터 수집 컨트롤러
 */
@RestController
@RequestMapping("/api/youtube")
@RequiredArgsConstructor
@Slf4j
public class YouTubeDataCollectionController {

    private final YouTubeChannelCollectionService youTubeChannelCollectionService;
    private final CollectedChannelRepository collectedChannelRepository;
    private final SubscriberHistoryRepository subscriberHistoryRepository;

    /**
     * 키워드로 채널 검색하고 저장
     * POST /api/youtube/search-and-save?keyword=뷰티&category=BEAUTY&maxResults=10
     */
    @PostMapping("/search-and-save")
    public ResponseEntity<?> searchAndSaveChannels(
            @RequestParam String keyword,
            @RequestParam String category,
            @RequestParam(defaultValue = "10") Integer maxResults) {

        log.info("채널 검색 및 저장 요청: keyword={}, category={}, maxResults={}", keyword, category, maxResults);

        try {
            List<CollectedChannel> savedChannels = youTubeChannelCollectionService
                    .searchAndSaveChannels(keyword, category, maxResults);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "채널 수집이 완료되었습니다.");
            response.put("savedCount", savedChannels.size());
            response.put("channels", savedChannels);

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            log.error("채널 검색 및 저장 중 오류: {}", e.getMessage(), e);

            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", "채널 수집 중 오류가 발생했습니다: " + e.getMessage());

            return ResponseEntity.badRequest().body(errorResponse);
        }
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
                .findByCollectedChannelCollectedChannelIdOrderByCollectedAtDesc(channelId);
        return ResponseEntity.ok(history);
    }

    /**
     * 구독자 수 업데이트 (기존 채널들)
     * POST /api/youtube/update-subscriber-counts
     */
    @PostMapping("/update-subscriber-counts")
    public ResponseEntity<?> updateSubscriberCounts() {
        try {
            youTubeChannelCollectionService.updateSubscriberCounts();

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "구독자 수 업데이트가 완료되었습니다.");

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            log.error("구독자 수 업데이트 중 오류: {}", e.getMessage(), e);

            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", "구독자 수 업데이트 중 오류가 발생했습니다: " + e.getMessage());

            return ResponseEntity.badRequest().body(errorResponse);
        }
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