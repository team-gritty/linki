package com.Gritty.Linki.domain.user.advertiser.channel.controller;

import com.Gritty.Linki.domain.user.advertiser.channel.service.ChannelService;
import com.Gritty.Linki.domain.user.advertiser.channel.service.YouTubeChannelCollectService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * YouTube 채널 수집 관련 API 컨트롤러
 * 키워드 기반 채널 검색 및 테이블로 데이터 수집함
 */
@RestController
@RequestMapping("/v1/api/youtube")
@RequiredArgsConstructor
@Slf4j
public class YouTubeChannelController {

    private final ChannelService channelService;
    private final YouTubeChannelCollectService youTubeChannelCollectService;

    /**
     * YouTube 채널 검색 및 수집 API (개발전 필요한 api)
     * 키워드와 카테고리를 기반으로 YouTube 채널을 검색하고 데이터베이스에 저장
     * 요청 예시:
     * POST /v1/api/youtube/collect?keyword=메이크업&category=BEAUTY&maxResults=15
     * 
     * @param keyword    검색 키워드 (필수)
     * @param category   채널 카테고리 (필수)
     * @param maxResults 최대 검색 결과 수 (선택, 기본값: 15, 최대: 50)
     * @return 성공 여부
     */
    @PostMapping("/collect")
    public ResponseEntity<Void> collectChannels(
            @RequestParam String keyword,
            @RequestParam String category,
            @RequestParam(defaultValue = "15") Integer maxResults) {

        log.info("YouTube 채널 수집 요청됨: keyword={}, category={}, maxResults={}",
                keyword, category, maxResults);

        youTubeChannelCollectService.collectChannels(keyword, category, maxResults);

        return ResponseEntity.ok().build();
    }

    /**
     * 모든 채널의 YouTube API 통계를 업데이트 (스케줄러로 하루에 한번씩 혹은 특정 시간에만 업데이트)
     *
     * @param maxResults 조회할 최근 영상 수 (기본값: 30개)
     * @return 업데이트 결과
     */
    @PostMapping("/youtube-api/channels/update-all-statistics")
    public ResponseEntity<Map<String, Object>> updateAllChannelStatistics(
            @RequestParam(defaultValue = "30") int maxResults) {

        log.info("모든 채널 통계 업데이트 요청 - maxResults: {}", maxResults);

        try {
            // 모든 채널 ID 조회
            List<String> allChannelIds = channelService.getAllChannelIds();

            // 일괄 업데이트 실행
            channelService.updateChannelStatisticsBatch(allChannelIds);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "모든 채널의 통계 업데이트가 완료되었습니다.");
            response.put("totalChannels", allChannelIds.size());

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("모든 채널 통계 업데이트 실패 - error: {}", e.getMessage());

            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", "모든 채널 통계 업데이트에 실패했습니다: " + e.getMessage());

            return ResponseEntity.badRequest().body(errorResponse);
        }
    }
}