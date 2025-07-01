package com.Gritty.Linki.domain.user.mypage.controller;

import com.Gritty.Linki.config.security.CustomUserDetails;
import com.Gritty.Linki.domain.user.advertiser.channel.service.ChannelService;
import com.Gritty.Linki.domain.user.mypage.dto.YoutubeChannelInfo;
import com.Gritty.Linki.domain.user.mypage.service.YoutubeService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("v1/api/user/youtube")
@Slf4j
public class YoutubeController {
    private final YoutubeService youtubeService;
    private final ChannelService channelService;

    @GetMapping("/callback")
    public ResponseEntity<?> getYoutubeChannelInfo(@RequestParam("code") String code) {
        log.info("유튜브인증 컨트롤러 시작");
        try {
            String accessToken = youtubeService.exchangeCodeForAccessToken(code);
            log.info("발급된 구글 토큰: {}", accessToken);

            YoutubeChannelInfo info = youtubeService.getMyChannel(accessToken);
            log.info("가져온 채널 정보: {}", info);
            log.info("채널 ID: {}", info.getId());
            log.info("채널 제목: {}", info.getTitle());

            Map<String, String> response = new HashMap<>();
            if (info.getId() != null)
                response.put("channelId", info.getId());
            if (info.getTitle() != null)
                response.put("title", info.getTitle());
            if (info.getThumbnailUrl() != null)
                response.put("thumbnail", info.getThumbnailUrl());
            if (info.getDescription() != null)
                response.put("description", info.getDescription());
            if (info.getPublishedAt() != null)
                response.put("publishedAt", info.getPublishedAt());
            if (info.getCustomUrl() != null)
                response.put("customUrl", info.getCustomUrl());
            if (info.getCountry() != null)
                response.put("country", info.getCountry());

            // 전체 응답
            Map<String, Object> result = new HashMap<>();
            result.put("success", true); // ✅ boolean 값
            result.put("data", response); // ✅ 내부 Map<String, String> 포함
            log.info(result.toString());

            return ResponseEntity.ok(result);

        } catch (Exception e) {
            log.error("유튜브 인증 중 에러 발생", e);
            return ResponseEntity.internalServerError().body("유튜브 인증 중 오류가 발생했습니다.");
        }
    }

    @PostMapping("/register")
    public ResponseEntity<?> registerChannel(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @RequestBody YoutubeChannelInfo channelInfo) {

        try {
            // 1. 채널 등록
            String channelId = youtubeService.registerChannel(userDetails, channelInfo);

            // 2. 등록된 채널의 모든 통계 정보 업데이트 (YouTube API를 통해)
            try {
                log.info("채널 통계 업데이트 시작: channelId={}", channelId);
                channelService.updateChannelStatistics(channelId, 30);
                log.info("채널 통계 업데이트 완료: channelId={}", channelId);
            } catch (Exception e) {
                log.warn("채널 통계 업데이트 실패 (채널 등록은 성공): channelId={}, error={}",
                        channelId, e.getMessage());
                // 통계 업데이트 실패해도 등록은 성공으로 처리
            }

            return ResponseEntity.ok("채널 등록 및 통계 업데이트 완료");
        } catch (Exception e) {
            log.error("채널 등록 실패", e);
            return ResponseEntity.internalServerError().body("채널 등록 중 오류 발생");
        }
    }

}
