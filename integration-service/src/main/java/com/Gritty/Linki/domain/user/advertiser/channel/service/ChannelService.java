package com.Gritty.Linki.domain.user.advertiser.channel.service;

import com.Gritty.Linki.domain.user.advertiser.channel.request.ChannelSearchRequest;
import com.Gritty.Linki.domain.user.advertiser.channel.response.ChannelListResponse;
import com.Gritty.Linki.domain.user.advertiser.channel.response.ChannelPageResponse;
import com.Gritty.Linki.entity.Channel;
import com.Gritty.Linki.domain.user.advertiser.channel.repository.jpa.ChannelJpaRepository;
import com.Gritty.Linki.domain.user.advertiser.channel.response.ChannelDetailResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

import com.Gritty.Linki.exception.BusinessException;
import com.Gritty.Linki.exception.ErrorCode;

/**
 * 채널 검색 서비스
 * 검색 필터를 통한 채널 조회 기능 제공
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ChannelService {

        private final ChannelJpaRepository channelRepository;
        private final YouTubeApiService youTubeApiService;

        /**
         * 채널 검색
         * 검색 조건에 따라 채널을 필터링하여 반환
         * 
         * @param request 검색 조건
         * @return 필터링된 채널 목록과 페이지네이션 정보
         */
        public ChannelPageResponse searchChannels(ChannelSearchRequest request) {
                log.info("채널 검색 서비스 호출: keyword={}, category={}, page={}, limit={}",
                                request.getKeyword(), request.getCategory(), request.getPage(), request.getLimit());

                // JPA를 사용하여 데이터베이스에서 채널 검색
                Page<Channel> channelPage = channelRepository.searchChannels(
                                request.getCategory(),
                                request.getKeyword(),
                                request.getMinSubscribers(),
                                request.getMaxSubscribers(),
                                // minAvgViewCount, maxAvgViewCount는 viewCount로 매핑
                                request.getMinAvgViewCount(),
                                request.getMaxAvgViewCount(),
                                PageRequest.of(request.getPage(), request.getLimit()));

                // 엔티티를 DTO로 변환
                List<ChannelListResponse> channels = channelPage.getContent().stream()
                                .map(this::convertToDto)
                                .collect(Collectors.toList());

                // 페이지네이션 정보 생성
                ChannelPageResponse.PageInfo pageInfo = ChannelPageResponse.PageInfo.builder()
                                .currentPage(channelPage.getNumber())
                                .pageSize(channelPage.getSize())
                                .totalElements(channelPage.getTotalElements())
                                .totalPages(channelPage.getTotalPages())
                                .first(channelPage.isFirst())
                                .last(channelPage.isLast())
                                .hasNext(channelPage.hasNext())
                                .hasPrevious(channelPage.hasPrevious())
                                .build();

                log.info("채널 검색 완료: 총 {}개 중 {}페이지 ({}-{}) 반환",
                                channelPage.getTotalElements(),
                                channelPage.getNumber() + 1,
                                channelPage.getNumber() * channelPage.getSize() + 1,
                                Math.min((channelPage.getNumber() + 1) * channelPage.getSize(),
                                                channelPage.getTotalElements()));

                return ChannelPageResponse.builder()
                                .channels(channels)
                                .pageInfo(pageInfo)
                                .build();
        }

        /**
         * LinkiScore 기반으로 정렬된 채널 검색
         * HomeRecommendationController와 동일한 로직으로 LinkiScore를 기준으로 정렬하여 반환
         * 
         * @param request 검색 조건
         * @return LinkiScore 기준으로 정렬된 채널 목록과 페이지네이션 정보
         */
        public ChannelPageResponse searchChannelsByLinkiScore(ChannelSearchRequest request) {
                log.info("LinkiScore 기반 채널 검색 서비스 호출: keyword={}, category={}, sortBy={}, sortDirection={}, page={}, limit={}",
                                request.getKeyword(), request.getCategory(), request.getSortBy(),
                                request.getSortDirection(), request.getPage(), request.getLimit());

                // 정렬 파라미터가 있는 경우 정렬 전용 메서드 호출
                if (request.getSortBy() != null && !request.getSortBy().isEmpty()) {
                        return searchChannelsWithSort(request);
                }

                // JPA를 사용하여 데이터베이스에서 LinkiScore 기반으로 채널 검색
                Page<Channel> channelPage = channelRepository.searchChannelsByLinkiScore(
                                request.getCategory(),
                                request.getKeyword(),
                                request.getMinSubscribers(),
                                request.getMaxSubscribers(),
                                // minAvgViewCount, maxAvgViewCount는 viewCount로 매핑
                                request.getMinAvgViewCount(),
                                request.getMaxAvgViewCount(),
                                PageRequest.of(request.getPage(), request.getLimit()));

                // 엔티티를 DTO로 변환
                List<ChannelListResponse> channels = channelPage.getContent().stream()
                                .map(this::convertToDto)
                                .collect(Collectors.toList());

                // 페이지네이션 정보 생성
                ChannelPageResponse.PageInfo pageInfo = ChannelPageResponse.PageInfo.builder()
                                .currentPage(channelPage.getNumber())
                                .pageSize(channelPage.getSize())
                                .totalElements(channelPage.getTotalElements())
                                .totalPages(channelPage.getTotalPages())
                                .first(channelPage.isFirst())
                                .last(channelPage.isLast())
                                .hasNext(channelPage.hasNext())
                                .hasPrevious(channelPage.hasPrevious())
                                .build();

                log.info("LinkiScore 기반 채널 검색 완료: 총 {}개 중 {}페이지 ({}-{}) 반환",
                                channelPage.getTotalElements(),
                                channelPage.getNumber() + 1,
                                channelPage.getNumber() * channelPage.getSize() + 1,
                                Math.min((channelPage.getNumber() + 1) * channelPage.getSize(),
                                                channelPage.getTotalElements()));

                return ChannelPageResponse.builder()
                                .channels(channels)
                                .pageInfo(pageInfo)
                                .build();
        }

        /**
         * 정렬 기능이 있는 채널 검색
         * 구독자 수, 평균 조회수 등으로 정렬하여 반환
         * 
         * @param request 검색 조건 (정렬 파라미터 포함)
         * @return 정렬된 채널 목록과 페이지네이션 정보
         */
        public ChannelPageResponse searchChannelsWithSort(ChannelSearchRequest request) {
                log.info("정렬 기능이 있는 채널 검색 서비스 호출: keyword={}, category={}, sortBy={}, sortDirection={}, page={}, limit={}",
                                request.getKeyword(), request.getCategory(), request.getSortBy(),
                                request.getSortDirection(), request.getPage(), request.getLimit());

                // JPA를 사용하여 데이터베이스에서 정렬 기능을 통한 채널 검색
                Page<Channel> channelPage = channelRepository.searchChannelsWithSort(
                                request.getCategory(),
                                request.getKeyword(),
                                request.getMinSubscribers(),
                                request.getMaxSubscribers(),
                                // minAvgViewCount, maxAvgViewCount는 viewCount로 매핑
                                request.getMinAvgViewCount(),
                                request.getMaxAvgViewCount(),
                                request.getSortBy(),
                                request.getSortDirection(),
                                PageRequest.of(request.getPage(), request.getLimit()));

                // 엔티티를 DTO로 변환
                List<ChannelListResponse> channels = channelPage.getContent().stream()
                                .map(this::convertToDto)
                                .collect(Collectors.toList());

                // 페이지네이션 정보 생성
                ChannelPageResponse.PageInfo pageInfo = ChannelPageResponse.PageInfo.builder()
                                .currentPage(channelPage.getNumber())
                                .pageSize(channelPage.getSize())
                                .totalElements(channelPage.getTotalElements())
                                .totalPages(channelPage.getTotalPages())
                                .first(channelPage.isFirst())
                                .last(channelPage.isLast())
                                .hasNext(channelPage.hasNext())
                                .hasPrevious(channelPage.hasPrevious())
                                .build();

                log.info("정렬 기능이 있는 채널 검색 완료: 총 {}개 중 {}페이지 ({}-{}) 반환",
                                channelPage.getTotalElements(),
                                channelPage.getNumber() + 1,
                                channelPage.getNumber() * channelPage.getSize() + 1,
                                Math.min((channelPage.getNumber() + 1) * channelPage.getSize(),
                                                channelPage.getTotalElements()));

                return ChannelPageResponse.builder()
                                .channels(channels)
                                .pageInfo(pageInfo)
                                .build();
        }

        /**
         * Channel 엔티티를 ChannelListResponse DTO로 변환
         */
        private ChannelListResponse convertToDto(Channel channel) {

                long avgViewCount = channel.getVideoCount() != null && channel.getVideoCount() > 0
                                && channel.getViewCount() != null
                                                ? channel.getViewCount() / channel.getVideoCount()
                                                : 0;

                // 평균 좋아요 수 계산
                long avgLikeCount = channel.getVideoCount() != null && channel.getVideoCount() > 0
                                && channel.getLikeCount() != null && channel.getLikeCount() > 0
                                                ? channel.getLikeCount() / channel.getVideoCount()
                                                : 0;

                // 평균 댓글 수 계산
                long avgCommentCount = channel.getVideoCount() != null && channel.getVideoCount() > 0
                                && channel.getCommentCount() != null && channel.getCommentCount() > 0
                                                ? channel.getCommentCount() / channel.getVideoCount()
                                                : 0;

                // 데이터가 없는 경우 경고 로그만 남기고 계속 진행
                if (avgLikeCount == 0 && avgViewCount > 0) {
                        log.warn("좋아요 데이터가 없습니다. 채널 ID: {} (0으로 처리)", channel.getChannelId());
                }

                if (avgCommentCount == 0 && avgViewCount > 0) {
                        log.warn("댓글 데이터가 없습니다. 채널 ID: {} (0으로 처리)", channel.getChannelId());
                }

                log.debug("실제 데이터 계산 완료: channelId={}, avgViewCount={}, avgLikeCount={}, avgCommentCount={}",
                                channel.getChannelId(), avgViewCount, avgLikeCount, avgCommentCount);

                return ChannelListResponse.builder()
                                .channelId(channel.getChannelId())
                                .channelName(channel.getChannelName())
                                .thumbnailUrl(channel.getChannelThumbnailUrl())
                                .subscriberCount(
                                                channel.getSubscriberCount() != null ? channel.getSubscriberCount() : 0)
                                .avgViewCount(avgViewCount)
                                .avgLikeCount(avgLikeCount)
                                .avgCommentCount(avgCommentCount)
                                .category(channel.getChannelCategory())
                                .description(channel.getChannelDescription())
                                .build();
        }

        /**
         * 채널의 좋아요, 댓글 수 평균 통계를 계산하고 데이터베이스에 업데이트 (Youtube API 호출)
         * 채널 수집 후 필요에 따라 통계를 재계산할 때 사용하는 유틸리티 메서드
         * 
         * @param channelId  채널 ID
         * @param maxResults 조회할 최근 영상 수 (기본: 30개)
         */
        @Transactional
        public void updateChannelStatistics(String channelId, int maxResults) {
                log.info("채널 통계 업데이트 시작 - channelId: {}", channelId);

                // 1. 데이터베이스에서 채널 정보 조회
                Channel channel = channelRepository.findById(channelId)
                                .orElseThrow(() -> new BusinessException(ErrorCode.ENTITY_NOT_FOUND, "채널을 찾을 수 없습니다"));

                if (channel.getYoutubeChannelId() == null) {
                        log.warn("YouTube 채널 ID가 없어 통계 업데이트를 건너뜁니다 - channelId: {}", channelId);
                        return;
                }

                try {
                        // 2. YouTube API를 통해 평균 좋아요/댓글 수 계산
                        long[] averages = youTubeApiService.getChannelVideoAverages(channel.getYoutubeChannelId(),
                                        maxResults);

                        long avgLikeCount = averages[0];
                        long avgCommentCount = averages[1];

                        log.info("YouTube API 통계 조회 완료 - channelId: {}, avgLikes: {}, avgComments: {}",
                                        channelId, avgLikeCount, avgCommentCount);

                        // 3. 총 좋아요/댓글 수 계산 (평균 * 영상 수)
                        long totalLikeCount = channel.getVideoCount() != null && channel.getVideoCount() > 0
                                        ? avgLikeCount * channel.getVideoCount()
                                        : avgLikeCount;

                        long totalCommentCount = channel.getVideoCount() != null && channel.getVideoCount() > 0
                                        ? avgCommentCount * channel.getVideoCount()
                                        : avgCommentCount;

                        // 4. 채널 엔티티 업데이트
                        channel.setLikeCount(totalLikeCount);
                        channel.setCommentCount(totalCommentCount);

                        // 5. 데이터베이스 저장
                        channelRepository.save(channel);

                        log.info("채널 통계 업데이트 완료 - channelId: {}, totalLikes: {}, totalComments: {}",
                                        channelId, totalLikeCount, totalCommentCount);

                } catch (Exception e) {
                        log.error("채널 통계 업데이트 실패 - channelId: {}, error: {}", channelId, e.getMessage());
                        throw new BusinessException(ErrorCode.INTERNAL_SERVER_ERROR,
                                        "채널 통계 업데이트에 실패했습니다: " + e.getMessage());
                }
        }

        /**
         * 채널의 YouTube API 평균 통계를 계산하고 데이터베이스에 업데이트 (기본 30개 영상)
         * 채널 수집 후 필요에 따라 통계를 재계산할 때 사용하는 유틸리티 메서드
         * 
         * @param channelId 채널 ID
         */
        @Transactional
        public void updateChannelStatistics(String channelId) {
                updateChannelStatistics(channelId, 30);
        }

        /**
         * 여러 채널의 통계를 한 번에 업데이트
         * 채널 수집 후 필요에 따라 통계를 재계산할 때 사용하는 유틸리티 메서드
         *
         * @param channelIds 채널 ID 목록
         */
        @Transactional
        public void updateChannelStatisticsBatch(List<String> channelIds) {
                log.info("채널 통계 일괄 업데이트 시작 - 대상 채널 수: {}", channelIds.size());

                for (String channelId : channelIds) {
                        try {
                                updateChannelStatistics(channelId);
                        } catch (Exception e) {
                                log.error("채널 통계 업데이트 실패 (건너뛰기) - channelId: {}, error: {}", channelId, e.getMessage());
                                // 다른 채널 처리를 위해 계속 진행
                        }
                }

                log.info("채널 통계 일괄 업데이트 완료 - 대상 채널 수: {}", channelIds.size());
        }

        /**
         * 채널 상세 정보 조회
         * 데이터베이스에 저장된 통계 정보를 사용하여 반환
         * 
         * @param channelId 채널 ID
         * @return 채널 상세 정보
         */
        @Transactional(readOnly = true)
        public ChannelDetailResponse getChannelDetail(String channelId) {

                // 1. 데이터베이스에서 채널 기본 정보 조회
                Channel channel = channelRepository.findById(channelId)
                                .orElseThrow(() -> new BusinessException(ErrorCode.ENTITY_NOT_FOUND, "채널을 찾을 수 없습니다"));

                // 2. 저장된 데이터를 사용하여 평균값 계산
                long avgLikeCount = 0;
                long avgCommentCount = 0;
                long avgViewCount = 0;

                if (channel.getVideoCount() != null && channel.getVideoCount() > 0) {
                        // 평균 조회수 계산
                        if (channel.getViewCount() != null) {
                                avgViewCount = channel.getViewCount() / channel.getVideoCount();
                        }

                        // 평균 좋아요 수 계산 (데이터베이스에 저장된 총값 사용)
                        if (channel.getLikeCount() != null) {
                                avgLikeCount = channel.getLikeCount() / channel.getVideoCount();
                        }

                        // 평균 댓글 수 계산 (데이터베이스에 저장된 총값 사용)
                        if (channel.getCommentCount() != null) {
                                avgCommentCount = channel.getCommentCount() / channel.getVideoCount();
                        }
                }

                // 3. 배너 URL 조회 (필요시에만)
                String bannerUrl = null;
                if (channel.getYoutubeChannelId() != null) {
                        try {
                                bannerUrl = youTubeApiService.getChannelBannerUrl(channel.getYoutubeChannelId());
                        } catch (Exception e) {
                                log.warn("배너 URL 조회 실패, 기본값 사용 - channelId: {}, error: {}", channelId, e.getMessage());
                        }
                }

                // 데이터가 없는 경우 경고 로그만 남기고 계속 진행
                if (avgLikeCount == 0 && avgViewCount > 0) {
                        log.warn("좋아요 데이터가 없습니다. 채널 ID: {} (0으로 처리)", channelId);
                }

                if (avgCommentCount == 0 && avgViewCount > 0) {
                        log.warn("댓글 데이터가 없습니다. 채널 ID: {} (0으로 처리)", channelId);
                }

                // 4. Response DTO 생성
                ChannelDetailResponse response = ChannelDetailResponse.builder()
                                .channelId(channel.getChannelId())
                                .name(channel.getChannelName())
                                .category(channel.getChannelCategory())
                                .subscribers(channel.getSubscriberCount() != null ? channel.getSubscriberCount() : 0)
                                .createdAt(channel.getChannelCreatedAt())
                                .videoCount(channel.getVideoCount() != null ? channel.getVideoCount() : 0)
                                .avgViewCount(avgViewCount)
                                .avgCommentCount(avgCommentCount)
                                .avgLikeCount(avgLikeCount)
                                .country(channel.getChannelCountry())
                                .description(channel.getChannelDescription())
                                .thumbnailUrl(channel.getChannelThumbnailUrl())
                                .youtubeUrl(channel.getChannelUrl())
                                .bannerUrl(bannerUrl)
                                .build();

                log.info("채널 상세 정보 조회 완료 - channelId: {}", channelId);
                return response;
        }

        /**
         * 모든 채널의 ID 목록을 반환하는 메서드
         * 
         * @return 채널 ID 목록
         */
        @Transactional(readOnly = true)
        public List<String> getAllChannelIds() {
                log.info("모든 채널 ID 조회 시작");

                List<String> channelIds = channelRepository.findAll()
                                .stream()
                                .map(Channel::getChannelId)
                                .collect(Collectors.toList());

                log.info("모든 채널 ID 조회 완료 - 총 {}개 채널", channelIds.size());

                return channelIds;
        }
}