package com.Gritty.Linki.domain.oAuth.signUp.controller;


import com.Gritty.Linki.domain.oAuth.dto.JoinDTO;
import com.Gritty.Linki.domain.oAuth.dto.RequestJoinDto;
import com.Gritty.Linki.domain.oAuth.signUp.repository.RefreshTokenRepository;
import com.Gritty.Linki.domain.oAuth.signUp.service.AccountService;
import com.Gritty.Linki.domain.oAuth.signUp.service.RefreshTokenService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("v1/api/nonuser")

public class AccountController {

    private final AccountService accountService;
    private final RefreshTokenRepository refreshTokenRepository;

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
    public ResponseEntity<?> logout (@CookieValue(value = "refresh_token", required = false) String refreshToken, HttpServletResponse response){
        if(refreshToken != null){
            // DB에서 삭제
            refreshTokenRepository.deleteByRefreshToken(refreshToken);

            // 클라이언트 쿠키 삭제
            Cookie deleteCookie = new Cookie("refresh_token", null);
            deleteCookie.setHttpOnly(true);
            deleteCookie.setMaxAge(0);
            deleteCookie.setPath("/");
            response.addCookie(deleteCookie);
        }

        return ResponseEntity.ok("로그아웃 완료");
    }

}


