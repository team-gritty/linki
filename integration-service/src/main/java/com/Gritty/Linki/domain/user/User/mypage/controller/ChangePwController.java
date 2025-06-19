package com.Gritty.Linki.domain.user.nonUser.mypage.controller;

import com.Gritty.Linki.domain.oAuth.signUp.repository.AccountRepository;
import com.Gritty.Linki.domain.user.nonUser.dto.ChangePwDTO;
import com.Gritty.Linki.entity.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;

@RestController
@RequestMapping("/v1/api/advertiser/mypage/campaigns")
@RequiredArgsConstructor
@Slf4j
public class ChangePwController {

    private final AccountRepository accountRepository;
    private final PasswordEncoder passwordEncoder;

    @PostMapping
    public ResponseEntity<?> changePw(@RequestBody ChangePwDTO changePwDTO, Principal principal){
        String userLoginId = principal.getName();
        User user = accountRepository.findByUserLoginId(userLoginId);
            if(user == null){
                throw new RuntimeException("사용자 없음");
            }

        if(!passwordEncoder.matches(changePwDTO.getUserLoginPw(), user.getUserLoginPw())){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("현재 비밀번호가 틀렸습니다.");
        }

        user.setUserLoginPw(passwordEncoder.encode(changePwDTO.getChangeLoginPw()));
        accountRepository.save(user);

        return ResponseEntity.ok("비밀번호 변경 완료");

    }

}
