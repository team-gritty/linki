package com.Gritty.Linki.domain.oAuth.signUp.controller;


import com.Gritty.Linki.config.security.JwtUtil;
import com.Gritty.Linki.domain.oAuth.dto.JoinDTO;
import com.Gritty.Linki.domain.oAuth.dto.RequestJoinDto;
import com.Gritty.Linki.domain.oAuth.signUp.repository.RefreshTokenRepository;
import com.Gritty.Linki.domain.oAuth.signUp.service.AccountService;
import com.Gritty.Linki.domain.oAuth.signUp.service.RefreshTokenService;
import com.Gritty.Linki.util.HttpUtil;

import com.Gritty.Linki.util.TokenUtil;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import software.amazon.awssdk.services.s3.endpoints.internal.Value;

import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("v1/api/nonuser")

public class AccountController {

    private final AccountService accountService;
    private final RefreshTokenRepository refreshTokenRepository;
    private final JwtUtil jwtUtil;


    @PostMapping("signup")
    public ResponseEntity<?> signup(@RequestBody RequestJoinDto requestJoinDto) {
        //dto 변환
        JoinDTO joinDTO = new ModelMapper().map(requestJoinDto, JoinDTO.class);
        // 입력 값이 비어 있다면
        if (!StringUtils.hasLength(joinDTO.getUserLoginId()) || !StringUtils.hasLength(joinDTO.getUserLoginPw()) || !StringUtils.hasLength(joinDTO.getUserName())
                || !StringUtils.hasLength(joinDTO.getUserPhone()) || !StringUtils.hasLength(joinDTO.getUserEmail())) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        accountService.save(joinDTO);
        return new ResponseEntity<>(HttpStatus.OK);
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
        String accessToken = "";
        String refreshToken = HttpUtil.getCookieValue(req, "refresh_Token");

        if (StringUtils.hasLength(refreshToken) && jwtUtil.isTokenExpired(refreshToken) ) {
            Map<String, Object> tokenBody = jwtUtil.getBody(refreshToken);

            Integer userLoginId = (Integer) tokenBody.get("userLoginId");

            accessToken = TokenUtil.generate("access_Token", "userLoginId", userLoginId, 60);



        }
        return new ResponseEntity<>(accessToken, HttpStatus.OK);
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


