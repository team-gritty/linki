package com.Gritty.Linki.domain.account.account.controller;


import com.Gritty.Linki.config.security.JwtUtil;
import com.Gritty.Linki.domain.account.dto.JoinDTO;
import com.Gritty.Linki.domain.account.dto.RequestJoinDto;
import com.Gritty.Linki.domain.account.account.repository.RefreshTokenRepository;
import com.Gritty.Linki.domain.account.account.service.AccountService;
import com.Gritty.Linki.util.HttpUtil;


import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.core.Authentication;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("v1/api/nonuser")

public class AccountController {

    private final AccountService accountService;
    private final RefreshTokenRepository refreshTokenRepository;
    private final JwtUtil jwtUtil;
    private static final Logger log = LoggerFactory.getLogger(AccountController.class);



    @GetMapping("/check")
    public ResponseEntity<?> check() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        if (auth == null || !auth.isAuthenticated()
                || auth.getPrincipal().equals("anonymousUser")) {
            return ResponseEntity.status(HttpServletResponse.SC_UNAUTHORIZED).body("로그인 안됨");
        }

        String username = auth.getName();
        String authorities = auth.getAuthorities().toString();
        String principalType = auth.getPrincipal().getClass().getSimpleName();
        
        log.info("인증 체크 - username: {}, authorities: {}, principalType: {}", 
                username, authorities, principalType);
        
        return ResponseEntity.ok(Map.of(
            "message", "로그인된 사용자: " + username,
            "authorities", authorities,
            "principalType", principalType,
            "principal", auth.getPrincipal().toString()
        ));
    }


    @PostMapping("signup")
    public ResponseEntity<?> signup(@RequestBody RequestJoinDto requestJoinDto) {
        //dto 변환
        JoinDTO joinDTO = new ModelMapper().map(requestJoinDto, JoinDTO.class);
        // 입력 값이 비어 있다면
        if (!StringUtils.hasLength(joinDTO.getUserLoginId()) || !StringUtils.hasLength(joinDTO.getUserLoginPw()) || !StringUtils.hasLength(joinDTO.getUserName())
                || !StringUtils.hasLength(joinDTO.getUserPhone()) || !StringUtils.hasLength(joinDTO.getUserEmail())) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        String loginId = requestJoinDto.getUserLoginId();
        if (StringUtils.hasLength(loginId) && accountService.find(loginId) != null) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(Map.of("message", "아이디 중복"));
        }

        accountService.save(joinDTO);
        return ResponseEntity.ok(Map.of("message", "회원가입이 완료되었습니다."));
    }

    @PostMapping("logout")
    @Transactional
    public ResponseEntity<?> logout(@CookieValue(value = "refresh_token", required = false) String refreshToken, HttpServletResponse response) {
        if (refreshToken != null) {
            // DB에서 삭제
//            refreshTokenRepository.deleteByRefreshToken(refreshToken);

            // 클라이언트 쿠키 삭제
            Cookie deleteCookie = new Cookie("refresh_token", null);
            deleteCookie.setHttpOnly(true);
            deleteCookie.setMaxAge(0);
            deleteCookie.setPath("/");
            response.addCookie(deleteCookie);
        }

        return ResponseEntity.ok("로그아웃 완료");
    }

    @GetMapping("token")
    public ResponseEntity<?> regenerate(HttpServletRequest req) {
        String refreshToken = HttpUtil.getCookieValue(req, "refresh_token");

        if (!StringUtils.hasLength(refreshToken)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("message", "리프레시 토큰이 없습니다."));
        }

        // 리프레시 토큰이 만료되지 않았는지 확인 (만료되지 않았을 때 true 반환)
        if (jwtUtil.isTokenExpired(refreshToken)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("message", "리프레시 토큰이 만료되었습니다."));
        }

        try {
            Map<String, Object> tokenBody = jwtUtil.getBody(refreshToken);
            String userId = (String) tokenBody.get("userId");
            String role = (String) tokenBody.get("userRole");

            if (userId == null || role == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(Map.of("message", "유효하지 않은 토큰입니다."));
            }

            String accessToken = jwtUtil.createJwtToken(userId, role, 60*60*10L);

            return ResponseEntity.ok(Map.of("accessToken", accessToken));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("message", "토큰 처리 중 오류가 발생했습니다."));
        }
    }


}

//    @GetMapping("/token")
//    public ResponseEntity<?> regenerate(HttpServletRequest req) {
//        String refreshToken = HttpUtil.getCookieValue(req, "refresh_token");
//
//        if (!StringUtils.hasLength(refreshToken)) {
//            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("리프레시 토큰 없음");
//        }
//
//        if (!TokenUtil.isValid(refreshToken)) {
//            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("만료되었거나 위조된 토큰");
//        }
//
//        Map<String, Object> tokenBody = TokenUtil.getBody(refreshToken);
//        Integer userLoginId = (Integer) tokenBody.get("userLoginId");
//        String role = (String) tokenBody.get("role"); // 있으면 활용
//
//        String newAccessToken = TokenUtil.generate("access_Token", "userLoginId", userLoginId, 60); // 60초
//
//        Map<String, String> responseBody = Map.of("accessToken", newAccessToken);
//
//        return ResponseEntity.ok(responseBody);
//}


