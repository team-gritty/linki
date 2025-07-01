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
