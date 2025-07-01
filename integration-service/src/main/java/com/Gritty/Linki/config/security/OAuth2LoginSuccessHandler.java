package com.Gritty.Linki.config.security;

import com.Gritty.Linki.domain.account.account.repository.AccountRepository;
import com.Gritty.Linki.domain.account.account.service.RefreshTokenService;
import com.Gritty.Linki.entity.RefreshToken;
import com.Gritty.Linki.entity.User;
import com.Gritty.Linki.util.IdGenerator;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class OAuth2LoginSuccessHandler implements AuthenticationSuccessHandler {

    private final AccountRepository accountRepository;
    private final JwtUtil jwtUtil;
    private final RefreshTokenService refreshTokenService;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws IOException {

        OAuth2User oauthUser = (OAuth2User) authentication.getPrincipal();
        String oauthId = oauthUser.getAttribute("sub");
        String email = oauthUser.getAttribute("email");
        String name = oauthUser.getAttribute("name");

        // 기존 유저 확인 or 신규 유저 생성
        User user = accountRepository.findByOauthProviderAndOauthId("google", oauthId)
                .orElseGet(() -> {
                    Optional<User> existingUser = accountRepository.findByUserEmail(email);
                    if (existingUser.isPresent()) {
                        User u = existingUser.get();
                        if (!Boolean.TRUE.equals(u.getIsOauthUser())) {
                            u.setOauthId(oauthId);
                            u.setOauthProvider("google");
                            u.setIsOauthUser(true);
                            u = accountRepository.save(u);
                        }
                        return u;
                    }
                    User newUser = new User();
                    newUser.setUserId(IdGenerator.userId());
                    newUser.setUserEmail(email);
                    newUser.setUserName(name);
                    newUser.setOauthProvider("google");
                    newUser.setOauthId(oauthId);
                    newUser.setUserRole("ROLE_USER");
                    newUser.setIsOauthUser(true);
                    newUser.setUserStatus(1);
                    newUser.setUserEnterDay(LocalDate.now());
                    return accountRepository.save(newUser);
                });

        // JWT 발급
        String accessToken = jwtUtil.createJwtToken(user.getUserId(), user.getUserRole(), 60 * 60 * 10L);
        String refreshToken = jwtUtil.createJwtToken(user.getUserId(), user.getUserRole(), 7 * 24 * 60 * 60L);

        RefreshToken tokenEntity = new RefreshToken();
        tokenEntity.setRefreshToken(refreshToken);
        tokenEntity.setCreatedAt(LocalDateTime.now());
        refreshTokenService.save(tokenEntity);

        // 쿠키에 refreshToken 저장
        Cookie refreshCookie = new Cookie("refresh_token", refreshToken);
        refreshCookie.setHttpOnly(true);
        refreshCookie.setPath("/");
        refreshCookie.setMaxAge(7 * 24 * 60 * 60);
        response.addCookie(refreshCookie);

        // accessToken을 헤더에 추가하고 클라이언트로 전달
        response.setHeader("Authorization", "Bearer " + accessToken);
        String redirectUrl = String.format(
            "http://175.45.205.251:3002/oauth/google/success?accessToken=%s&userId=%s&role=%s",
            accessToken, user.getUserId(), user.getUserRole()
        );
        response.sendRedirect(redirectUrl); // ✅ 프론트 페이지로 리디렉션
    }
}
