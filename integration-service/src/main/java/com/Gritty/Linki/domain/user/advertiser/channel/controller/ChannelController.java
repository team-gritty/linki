package com.Gritty.Linki.domain.user.advertiser.channel.controller;

import com.Gritty.Linki.domain.user.advertiser.channel.request.ChannelSearchRequest;
import com.Gritty.Linki.domain.user.advertiser.channel.response.ChannelListResponse;
import com.Gritty.Linki.domain.user.advertiser.channel.response.ChannelPageResponse;
import com.Gritty.Linki.domain.user.advertiser.channel.response.SubscriberHistoryResponse;
import com.Gritty.Linki.domain.user.advertiser.channel.service.ChannelService;
import com.Gritty.Linki.domain.user.advertiser.channel.service.SubscriberHistoryService;
import com.Gritty.Linki.domain.user.advertiser.channel.dto.SubscriberHistoryDto;
import com.Gritty.Linki.domain.user.advertiser.channel.response.ChannelDetailResponse;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 채널 검색 관련 API 컨트롤러
 * 검색 필터를 통한 채널 조회 기능 제공
 */
@Slf4j
@RestController
@RequestMapping("/v1/api")
@RequiredArgsConstructor
public class ChannelController {

        private final ChannelService channelService;
        private final SubscriberHistoryService subscriberHistoryService;

        /**
         * 채널 조회 (검색 필터 포함) - LinkiScore 기준 정렬
         * 키워드, 카테고리, 구독자 수, 평균 조회수 등의 필터를 적용하여 채널을 검색
         * HomeRecommendationController와 동일한 LinkiScore 로직을 사용하여 정렬
         * 
         * @param keyword         검색 키워드 (선택)
         * @param category        카테고리 (선택)
         * @param minSubscribers  최소 구독자 수 (기본값: 0)
         * @param maxSubscribers  최대 구독자 수 (기본값: Long.MAX_VALUE)
         * @param minAvgViewCount 최소 평균 조회수 (기본값: 0)
         * @param maxAvgViewCount 최대 평균 조회수 (기본값: Long.MAX_VALUE)
         * @param sortBy          정렬 기준 (subscriberCount, avgViewCount 등, 선택)
         * @param sortDirection   정렬 방향 (desc, asc, 기본값: desc)
         * @param page            페이지 번호 (기본값: 0)
         * @param limit           페이지 크기 (기본값: 10)
         * @return LinkiScore 기준으로 정렬된 채널 목록과 페이지네이션 정보
         */
        @GetMapping("/nonuser/channels")
        public ResponseEntity<ChannelPageResponse> getChannels(
                        @RequestParam(required = false) String keyword,
                        @RequestParam(required = false) String category,
                        @RequestParam(defaultValue = "0") long minSubscribers,
                        @RequestParam(defaultValue = "9223372036854775807") long maxSubscribers,
                        @RequestParam(defaultValue = "0") long minAvgViewCount,
                        @RequestParam(defaultValue = "9223372036854775807") long maxAvgViewCount,
                        @RequestParam(required = false) String sortBy,
                        @RequestParam(defaultValue = "desc") String sortDirection,
                        @RequestParam(defaultValue = "0") int page,
                        @RequestParam(defaultValue = "10") int limit) {

                log.info("채널 검색 요청 (LinkiScore 기준): keyword={}, category={}, minSubscribers={}, maxSubscribers={}, " +
                                "minAvgViewCount={}, maxAvgViewCount={}, sortBy={}, sortDirection={}, page={}, limit={}",
                                keyword, category, minSubscribers, maxSubscribers,
                                minAvgViewCount, maxAvgViewCount, sortBy, sortDirection, page, limit);

                // Request DTO 생성
                ChannelSearchRequest request = ChannelSearchRequest.builder()
                                .keyword(keyword)
                                .category(category)
                                .minSubscribers(minSubscribers)
                                .maxSubscribers(maxSubscribers)
                                .minAvgViewCount(minAvgViewCount)
                                .maxAvgViewCount(maxAvgViewCount)
                                .sortBy(sortBy)
                                .sortDirection(sortDirection)
                                .page(page)
                                .limit(limit)
                                .build();

                // Service 호출하여 LinkiScore 기반 채널 검색 (기본 정렬 방식 변경)
                ChannelPageResponse channelPageResponse = channelService.searchChannelsByLinkiScore(request);

                return ResponseEntity.ok(channelPageResponse);
        }

        /**
         * 원본 테이블 순서로 채널 조회 (검색 필터 포함)
         * LinkiScore 정렬이 아닌 원본 데이터베이스 테이블 순서로 조회
         * 
         * @param keyword         검색 키워드 (선택)
         * @param category        카테고리 (선택)
         * @param minSubscribers  최소 구독자 수 (기본값: 0)
         * @param maxSubscribers  최대 구독자 수 (기본값: Long.MAX_VALUE)
         * @param minAvgViewCount 최소 평균 조회수 (기본값: 0)
         * @param maxAvgViewCount 최대 평균 조회수 (기본값: Long.MAX_VALUE)
         * @param page            페이지 번호 (기본값: 0)
         * @param limit           페이지 크기 (기본값: 10)
         * @return 원본 테이블 순서의 채널 목록과 페이지네이션 정보
         */
        @GetMapping("/nonuser/channels/original")
        public ResponseEntity<ChannelPageResponse> getChannelsOriginal(
                        @RequestParam(required = false) String keyword,
                        @RequestParam(required = false) String category,
                        @RequestParam(defaultValue = "0") long minSubscribers,
                        @RequestParam(defaultValue = "9223372036854775807") long maxSubscribers,
                        @RequestParam(defaultValue = "0") long minAvgViewCount,
                        @RequestParam(defaultValue = "9223372036854775807") long maxAvgViewCount,
                        @RequestParam(defaultValue = "0") int page,
                        @RequestParam(defaultValue = "10") int limit) {

                log.info("채널 검색 요청 (원본 순서): keyword={}, category={}, minSubscribers={}, maxSubscribers={}, " +
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

                // Service 호출하여 원본 순서 채널 검색
                ChannelPageResponse channelPageResponse = channelService.searchChannels(request);

                return ResponseEntity.ok(channelPageResponse);
        }

        /**
         * 특정 채널의 상세(통계 정보 포함)정보 조회
         * YouTube API를 통해 평균 좋아요/댓글 수를 실시간으로 계산하여 반환
         *
         * @param channelId 채널 ID
         * @return 채널 상세 정보
         */
        @GetMapping("/user/channels/{channelId}")
        public ResponseEntity<ChannelDetailResponse> getChannelDetail(@PathVariable String channelId) {
                log.info("채널 상세 정보 조회 요청 - channelId: {}", channelId);

                ChannelDetailResponse channelDetail = channelService.getChannelDetail(channelId);
                return ResponseEntity.ok(channelDetail);
        }

        /**
         * 특정 채널의 구독자 히스토리 조회
         * 
         * @param channelId 채널 ID (String 형식)
         * @param days      조회할 일수 (기본값: 30일)
         * @return 구독자 히스토리 목록
         */
        @GetMapping("/user/channels/{channelId}/subscriber-history")
        public ResponseEntity<List<SubscriberHistoryResponse>> getSubscriberHistory(
                        @PathVariable String channelId,
                        @RequestParam(defaultValue = "30") int days) {

                log.info("구독자 히스토리 조회 요청 - channelId: {}, days: {}", channelId, days);

                // 서비스에 요청!
                List<SubscriberHistoryDto> historyDtoList = subscriberHistoryService
                                .getSubscriberHistory(channelId, days);

                // DTO를 Response로 변환
                List<SubscriberHistoryResponse> historyResponseList = historyDtoList.stream()
                                .map(dto -> SubscriberHistoryResponse.builder()
                                                .id(dto.getId())
                                                .channelId(dto.getChannelId())
                                                .subscriberCount(dto.getSubscriberCount())
                                                .collectedAt(dto.getCollectedAt())
                                                .build())
                                .collect(Collectors.toList());

                return ResponseEntity.ok(historyResponseList);
        }

        /**
         * 수동 구독자 수 업데이트 테스트 (스케줄러 테스트용)
         * 
         * @return 실행 결과
         */
        @PostMapping("/test/scheduler/run-update")
        public ResponseEntity<Map<String, Object>> testSchedulerUpdate() {
                log.info("---------수동 스케줄러 테스트 실행 요청");

                Map<String, Object> result = new HashMap<>();

                try {
                        subscriberHistoryService.updateAllChannelsSubscriberCount();

                        result.put("success", true);
                        result.put("message", "스케줄러 테스트 실행 완료");
                        result.put("timestamp", System.currentTimeMillis());

                        log.info(" 수동 스케줄러 테스트 실행 완료");

                        return ResponseEntity.ok(result);

                } catch (Exception e) {
                        log.error(" 수동 스케줄러 테스트 실행 중 오류 발생", e);

                        result.put("success", false);
                        result.put("message", "스케줄러 테스트 실행 실패: " + e.getMessage());
                        result.put("timestamp", System.currentTimeMillis());

                        return ResponseEntity.status(500).body(result);
                }
        }

        /**
         * 스케줄러 상태 확인
         * 
         * @return 상태 정보
         */
        @GetMapping("/test/scheduler/status")
        public ResponseEntity<Map<String, Object>> getSchedulerStatus() {
                log.info("---------- 스케줄러 상태 확인 요청");

                Map<String, Object> status = new HashMap<>();
                status.put("schedulerEnabled", true);
                status.put("testMode", true);
                status.put("message", "스케줄러가 정상 작동 중입니다");
                status.put("timestamp", System.currentTimeMillis());

                return ResponseEntity.ok(status);
        }
}