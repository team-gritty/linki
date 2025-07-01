package com.linki.admin_integration_service.config;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.linki.admin_integration_service.domain.account.repository.AccountRepository;
import com.linki.admin_integration_service.entity.RefreshToken;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.stereotype.Component;
import com.linki.admin_integration_service.config.token.*;
import java.io.IOException;
import java.time.LocalDateTime;


@Slf4j
//form 로그인 비활성화 하여 직접 커스텀]
public class LoginFilter extends UsernamePasswordAuthenticationFilter {
    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;
    private final AccountRepository accountRepository;
    private final RefreshTokenService refreshTokenService;

    public LoginFilter(AuthenticationManager authenticationManager, JwtUtil jwtUtil, AccountRepository accountRepository, RefreshTokenService refreshTokenService) {
        this.authenticationManager = authenticationManager;
        this.jwtUtil = jwtUtil;
        this.accountRepository = accountRepository;
        this.refreshTokenService = refreshTokenService;
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {

        //디스패처 서블렛 필터는 컨트롤러보다 앞단이기 떄문에 직접 json 파싱
        ObjectMapper objectMapper = new ObjectMapper();
        String username;
        String password;
        try {
            LoginRequestDto loginRequest = objectMapper.readValue(request.getInputStream(), LoginRequestDto.class);
            username = loginRequest.getAdminLoginId();
            password = loginRequest.getAdminLoginPw();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        log.info("Attempting to authenticate user: " + username);

        //매니저한테 넘겨줄 객체
        UsernamePasswordAuthenticationToken authRequest = new UsernamePasswordAuthenticationToken(username, password);

        //인증!
        return authenticationManager.authenticate(authRequest);
    }

    //인증 성공 시
    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain, Authentication authResult) throws IOException, ServletException {
        CustomUserDetails customUserDetails = (CustomUserDetails) authResult.getPrincipal();

        //유저 로그인 아이디로 유저아이디 뽑아옴
        String userId = accountRepository.findAdminIdByAdminLoginId(customUserDetails.getUsername()).orElseThrow(
                () -> new UsernameNotFoundException("User " + customUserDetails.getUsername() + "가 존재하지 않습니다.")
        );

        // 데이터베이스에서 직접 userRole 가져오기 (JWT 토큰에 정확한 role 포함하기 위해)
        String userStatus = accountRepository.findByAdminLoginId(customUserDetails.getUsername())
                .map(user -> user.getAdminStatus())
                .orElseThrow(() -> new UsernameNotFoundException("User role not found"));

        log.info("JWT 토큰 생성 - userId: {}, userRole: {}", userId, userStatus);

        String accesstoken = jwtUtil.createJwtToken(userId, userStatus, 60*60*10L);
        String refreshToken = jwtUtil.createJwtToken(userId, userStatus, 7*24*60*60*1000L);

        // Save refresh token to database
        RefreshToken tokenEntity = new RefreshToken();
        tokenEntity.setRefreshToken(refreshToken);
        tokenEntity.setCreatedAt(LocalDateTime.now());
        refreshTokenService.save(tokenEntity);

        //토큰 응답 RFC 7235 정의
        response.addHeader("Authorization", "Bearer " + accesstoken);

        //        // 리프레시 토큰을 HttpOnly 쿠키로 설정
        Cookie refreshCookie = new Cookie("refresh_token", refreshToken);
        refreshCookie.setHttpOnly(true);
        refreshCookie.setPath("/");
        refreshCookie.setMaxAge(7 * 24 * 60 * 60); // 7일
        response.addCookie(refreshCookie);

        // ✅ JSON 응답 바디 작성
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        String json = String.format("{\"accessToken\":\"%s\", \"userId\":\"%s\",  \"status\":\"%s\"}", accesstoken, userId, userStatus);
        response.getWriter().write(json);
    }

    //실패시
    @Override
    protected void unsuccessfulAuthentication(HttpServletRequest request, HttpServletResponse response, AuthenticationException failed) throws IOException, ServletException {
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
    }
}
