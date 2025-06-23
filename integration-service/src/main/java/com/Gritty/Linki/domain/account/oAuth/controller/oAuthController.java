package com.Gritty.Linki.domain.account.oAuth.controller;

import com.Gritty.Linki.config.security.JwtUtil;
import com.Gritty.Linki.domain.account.account.repository.AccountRepository;
import com.Gritty.Linki.domain.account.account.service.RefreshTokenService;
import com.Gritty.Linki.entity.RefreshToken;
import com.Gritty.Linki.entity.User;
import com.Gritty.Linki.util.IdGenerator;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/api/user/auth")
@Component

public class oAuthController {

    private final AccountRepository accountRepository;
    private final JwtUtil jwtUtil;
    private final RefreshTokenService refreshTokenService;
    private static final Logger logger = LoggerFactory.getLogger(oAuthController.class);

//    @GetMapping("/google")
//    public ResponseEntity<?> getGoogleAuthUrl() {
//        String redirectUri = "http://localhost:3002/google/callback"; // Vue 앱 콜백 경로
//        String clientId = System.getenv("GOOGLE_CLIENT_ID");
//        String scope = "email profile";
//        String responseType = "token id_token";
//        String state = "secure_random_state"; // 필요 시 동적으로 생성
//        String nonce = "secure_random_nonce"; // 필요 시 동적으로 생성
//
//        String authUrl = String.format(
//            "https://accounts.google.com/o/oauth2/v2/auth?client_id=%s&redirect_uri=%s&response_type=%s&scope=%s&state=%s&nonce=%s&prompt=select_account",
//            clientId, redirectUri, responseType, scope, state, nonce
//        );
//
//        return ResponseEntity.ok(Map.of(
//            "success", true,
//            "authUrl", authUrl
//        ));
//    }

//    @PostMapping("/google")
//    public ResponseEntity<?> googleLogin(@RequestBody GoogleLoginDTO googleLoginDTO) {
//
//        // 구글 ID와 이메일 정보
//        String oauthId = googleLoginDTO.getSub();
//        String email = googleLoginDTO.getEmail();
//        String name = googleLoginDTO.getName();
//
//        // 이미 등록된 사용자면 가져오기
//        User user = accountRepository.findByOauthProviderAndOauthId("google", oauthId)
//                .orElseGet(() -> {
//                    // 이메일 기준 기존 회원이 있으면 연결
//                    Optional<User> existingUser = accountRepository.findByUserEmail(email);
//                    if (existingUser.isPresent()) {
//                        User u = existingUser.get();
//                        // If not already linked with OAuth, link it
//                        if (!Boolean.TRUE.equals(u.getIsOauthUser())) {
//                            u.setOauthId(oauthId);
//                            u.setOauthProvider("google");
//                            u.setIsOauthUser(true);
//                            u = accountRepository.save(u);
//                            logger.info("Existing user linked with Google OAuth: userId={}", u.getUserId());
//                        } else {
//                            logger.info("Existing user already linked with OAuth: userId={}", u.getUserId());
//                        }
//                        return u;
//                    }
//
//                    // 신규 사용자 생성
//                    User newUser = new User();
//                    newUser.setUserId(IdGenerator.userId());
//                    newUser.setUserEmail(email);
//                    newUser.setUserName(name);
//                    newUser.setOauthProvider("google");
//                    newUser.setOauthId(oauthId);
//                    newUser.setUserRole("ROLE_USER");
//                    newUser.setIsOauthUser(true);
//                    newUser.setUserStatus(1);
//                    newUser.setUserEnterDay(LocalDate.now());
//                    User savedUser = accountRepository.save(newUser);
//                    logger.info("New user created with Google OAuth: userId={}", savedUser.getUserId());
//                    return savedUser;
//                });
//
//        // JWT 토큰 생성
//        String accessToken = jwtUtil.createJwtToken(user.getUserId(), user.getUserRole(), 60 * 60 * 10L); // 10시간
//        String refreshToken = jwtUtil.createJwtToken(user.getUserId(), user.getUserRole(), 7 * 24 * 60 * 60L); // 7일
//
//        // 리프레시 토큰 DB 저장
//        RefreshToken tokenEntity = new RefreshToken();
//        tokenEntity.setRefreshToken(refreshToken);
//        tokenEntity.setCreatedAt(LocalDateTime.now());
//        refreshTokenService.save(tokenEntity);
//
//        // 리프레시 토큰을 HttpOnly 쿠키로 설정
//        Cookie refreshCookie = new Cookie("refresh_token", refreshToken);
//        refreshCookie.setHttpOnly(true);
//        refreshCookie.setPath("/");
//        refreshCookie.setMaxAge(7 * 24 * 60 * 60); // 7일
//
//        // 응답에 쿠키 추가
//        HttpServletResponse rawResponse = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getResponse();
//        if (rawResponse != null) {
//            rawResponse.addCookie(refreshCookie);
//            rawResponse.addHeader("Authorization", "Bearer " + accessToken);
//        }
//
//        return ResponseEntity.ok(Map.of(
//                "accessToken", accessToken,
//                "userId", user.getUserId(),
//                "role", user.getUserRole()
//        ));
//
//
//    }

//    @GetMapping("/login-success")
//    public void loginSuccess(HttpServletResponse response) throws java.io.IOException {
//        response.sendRedirect("http://localhost:3002/oauth/google/callback");
//    }

//    @GetMapping("/login-success")
//    public ResponseEntity<?> loginSuccess() {
//        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
//
//        if (authentication == null || !authentication.isAuthenticated() || authentication.getPrincipal().equals("anonymousUser")) {
//            return ResponseEntity.status(401).body("Unauthorized");
//        }
//
//        // OAuth 로그인 후 인증 객체에서 사용자 정보 추출
//        org.springframework.security.oauth2.core.user.OAuth2User oauthUser =
//                (org.springframework.security.oauth2.core.user.OAuth2User) authentication.getPrincipal();
//
//        String oauthId = oauthUser.getAttribute("sub");
//        String email = oauthUser.getAttribute("email");
//        String name = oauthUser.getAttribute("name");
//
//        // 사용자 저장 또는 조회 로직 재사용
//        User user = accountRepository.findByOauthProviderAndOauthId("google", oauthId)
//                .orElseGet(() -> {
//                    Optional<User> existingUser = accountRepository.findByUserEmail(email);
//                    if (existingUser.isPresent()) {
//                        User u = existingUser.get();
//                        if (!Boolean.TRUE.equals(u.getIsOauthUser())) {
//                            u.setOauthId(oauthId);
//                            u.setOauthProvider("google");
//                            u.setIsOauthUser(true);
//                            u = accountRepository.save(u);
//                        }
//                        return u;
//                    }
//
//                    User newUser = new User();
//                    newUser.setUserId(IdGenerator.userId());
//                    newUser.setUserEmail(email);
//                    newUser.setUserName(name);
//                    newUser.setOauthProvider("google");
//                    newUser.setOauthId(oauthId);
//                    newUser.setUserRole("ROLE_USER");
//                    newUser.setIsOauthUser(true);
//                    newUser.setUserStatus(1);
//                    newUser.setUserEnterDay(LocalDate.now());
//                    return accountRepository.save(newUser);
//                });
//
//        // JWT 토큰 발급
//        String accessToken = jwtUtil.createJwtToken(user.getUserId(), user.getUserRole(), 60 * 60 * 10L);
//        String refreshToken = jwtUtil.createJwtToken(user.getUserId(), user.getUserRole(), 7 * 24 * 60 * 60L);
//
//        RefreshToken tokenEntity = new RefreshToken();
//        tokenEntity.setRefreshToken(refreshToken);
//        tokenEntity.setCreatedAt(LocalDateTime.now());
//        refreshTokenService.save(tokenEntity);
//
//        Cookie refreshCookie = new Cookie("refresh_token", refreshToken);
//        refreshCookie.setHttpOnly(true);
//        refreshCookie.setPath("/");
//        refreshCookie.setMaxAge(7 * 24 * 60 * 60);
//
//        HttpServletResponse rawResponse = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getResponse();
//        if (rawResponse != null) {
//            rawResponse.addCookie(refreshCookie);
//            rawResponse.addHeader("Authorization", "Bearer " + accessToken);
//        }
//
//        return ResponseEntity.ok(Map.of(
//                "accessToken", accessToken,
//                "userId", user.getUserId(),
//                "role", user.getUserRole()
//        ));
//    }


    @GetMapping("/login-success")
    public ResponseEntity<?> loginSuccess(Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return ResponseEntity.status(401).body("Unauthorized");
        }

        OAuth2User oauthUser = (OAuth2User) authentication.getPrincipal();
        String email = oauthUser.getAttribute("email");
        String sub = oauthUser.getAttribute("sub");
        String name = oauthUser.getAttribute("name");

        System.out.println("✅ 로그인 성공: " + email + ", " + sub + ", " + name);

        return ResponseEntity.ok("OAuth2 로그인 성공!");
    }
}
