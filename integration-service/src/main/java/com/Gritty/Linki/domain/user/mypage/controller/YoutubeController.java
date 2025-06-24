package com.Gritty.Linki.domain.user.mypage.controller;

import com.Gritty.Linki.config.security.CustomUserDetails;
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

    @GetMapping("/callback")
    public ResponseEntity<?> getYoutubeChannelInfo(@RequestParam("code") String code) {
        log.info("유튜브인증 컨트롤러 시작");
        try {
            String accessToken = youtubeService.exchangeCodeForAccessToken(code);
            log.info("발급된 구글 토큰: {}", accessToken);

            YoutubeChannelInfo info = youtubeService.getMyChannel(accessToken);
            log.info("가져온 채널 정보: {}", info);

            Map<String, String> response = new HashMap<>();
            if (info.getChannelId() != null) response.put("channelId", info.getChannelId());
            if (info.getTitle() != null) response.put("title", info.getTitle());
            if (info.getThumbnailUrl() != null) response.put("thumbnail", info.getThumbnailUrl());
            if (info.getDescription() != null) response.put("description", info.getDescription());
            if (info.getPublishedAt() != null) response.put("publishedAt", info.getPublishedAt());
            if (info.getCustomUrl() != null) response.put("customUrl", info.getCustomUrl());
            if (info.getCountry() != null) response.put("country", info.getCountry());

            // 전체 응답
            Map<String, Object> result = new HashMap<>();
            result.put("success", true);         // ✅ boolean 값
            result.put("data", response);        // ✅ 내부 Map<String, String> 포함

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
            youtubeService.registerChannel(userDetails, channelInfo);
            return ResponseEntity.ok("채널 등록 성공");
        } catch (Exception e) {
            log.error("채널 등록 실패", e);
            return ResponseEntity.internalServerError().body("채널 등록 중 오류 발생");
        }
    }



}
