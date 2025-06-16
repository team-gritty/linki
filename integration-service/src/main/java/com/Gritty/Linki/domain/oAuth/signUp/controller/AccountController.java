package com.Gritty.Linki.domain.oAuth.signUp.controller;


import com.Gritty.Linki.domain.oAuth.dto.JoinDTO;
import com.Gritty.Linki.domain.oAuth.signUp.service.AccountService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("v1")

public class AccountController {

    private final AccountService accountService;

    @PostMapping("signup")
    public ResponseEntity<?> signup(@RequestBody JoinDTO joinDTO) {
        // 입력 값이 비어 있다면
        if (!StringUtils.hasLength(joinDTO.getUserLoginId()) || !StringUtils.hasLength(joinDTO.getUserLoginPw()) || !StringUtils.hasLength(joinDTO.getUserName())
                || !StringUtils.hasLength(joinDTO.getUserPhone()) || !StringUtils.hasLength(joinDTO.getUserEmail())) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        accountService.save(joinDTO);
        return new ResponseEntity<>(HttpStatus.OK);
    }

}


