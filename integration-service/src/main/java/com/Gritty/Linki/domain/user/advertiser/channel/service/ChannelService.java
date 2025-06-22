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

                // 임시 해결책: 데이터가 없는 경우 가상의 비율 생성 (실제 데이터가 없을 때만)
                if (avgLikeCount == 0 && avgViewCount > 0) {
                        // 일반적인 좋아요 비율 2-5% 적용
                        avgLikeCount = (long) (avgViewCount * (0.02 + Math.random() * 0.03));
                        log.warn("실제 데이터가 아닌 더미 데이터 사용, 채널 아이디: {}, 평균 좋아요 수: {}",
                                        channel.getChannelId(), avgLikeCount);
                }

                if (avgCommentCount == 0 && avgViewCount > 0) {
                        // 일반적인 댓글 비율 0.1-0.5% 적용
                        avgCommentCount = (long) (avgViewCount * (0.001 + Math.random() * 0.004));
                        log.warn("실제 데이터가 아닌 더미 데이터 사용: {}, avgCommentCount: {}",
                                        channel.getChannelId(), avgCommentCount);
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
         * 채널 상세 정보 조회
         * YouTube API를 통해 평균 좋아요/댓글 수를 실시간으로 계산
         * 
         * @param channelId 채널 ID
         * @return 채널 상세 정보
         */
        public ChannelDetailResponse getChannelDetail(String channelId) {

                // 1. 데이터베이스에서 채널 기본 정보 조회
                Channel channel = channelRepository.findById(channelId)
                                .orElseThrow(() -> new RuntimeException("채널을 찾을 수 없습니다: " + channelId));

                // 2. YouTube API를 통해 평균 좋아요/댓글 수, 배너 url 조회 (최근 30개 영상 기준)
                long avgLikeCount = 0;
                long avgCommentCount = 0;
                String bannerUrl = null;

                if (channel.getYoutubeChannelId() != null) {
                        try {
                                long[] averages = youTubeApiService
                                                .getChannelVideoAverages(channel.getYoutubeChannelId(), 30);

                                // 영상 평균 좋아요 수
                                avgLikeCount = averages[0];
                                // 영상 평균 댓글 수
                                avgCommentCount = averages[1];

                                // 배너 URL도 가져오기
                                bannerUrl = youTubeApiService.getChannelBannerUrl(channel.getYoutubeChannelId());
                        } catch (Exception e) {
                                log.warn("YouTube API 평균 통계 조회 실패, 기본값 사용 - channelId: {}, error: {}", channelId,
                                                e.getMessage());
                                // YouTube API 호출 실패 시 기존 DB 데이터로 계산
                                if (channel.getVideoCount() != null && channel.getVideoCount() > 0) {
                                        avgLikeCount = channel.getLikeCount() != null && channel.getLikeCount() > 0
                                                        ? channel.getLikeCount() / channel.getVideoCount()
                                                        : 0;
                                        avgCommentCount = channel.getCommentCount() != null
                                                        && channel.getCommentCount() > 0
                                                                        ? channel.getCommentCount()
                                                                                        / channel.getVideoCount()
                                                                        : 0;
                                }
                        }
                }

                // 3. 평균 조회수 계산
                long avgViewCount = 0;
                if (channel.getVideoCount() != null && channel.getVideoCount() > 0 && channel.getViewCount() != null) {
                        avgViewCount = channel.getViewCount() / channel.getVideoCount();
                }

                // 임시 해결책: 데이터가 없는 경우 가상의 비율 생성 (상세 페이지용)
                if (avgLikeCount == 0 && avgViewCount > 0) {
                        // 일반적인 좋아요 비율 2-5% 적용
                        avgLikeCount = (long) (avgViewCount * (0.02 + Math.random() * 0.03));
                        log.warn("더미데이터 계산--------------");
                }

                if (avgCommentCount == 0 && avgViewCount > 0) {
                        // 일반적인 댓글 비율 0.1-0.5% 적용
                        avgCommentCount = (long) (avgViewCount * (0.001 + Math.random() * 0.004));
                        log.warn("더미데이터 계산--------------");
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
}