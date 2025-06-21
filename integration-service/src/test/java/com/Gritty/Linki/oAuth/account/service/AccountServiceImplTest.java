package com.Gritty.Linki.oAuth.account.service;

import com.Gritty.Linki.domain.oAuth.dto.JoinDTO;
import com.Gritty.Linki.domain.oAuth.account.repository.AccountRepository;
import com.Gritty.Linki.domain.oAuth.account.service.AccountService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
public class AccountServiceImplTest {

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private AccountService accountService;


    @Test
    void signUp() {

        JoinDTO joinDTO = new JoinDTO();
        joinDTO.setUserLoginId("testId");
        joinDTO.setUserLoginPw("testPw");
        joinDTO.setUserName("홍길동");
        joinDTO.setUserPhone("010-1234-5678");
        joinDTO.setUserEmail("hong@example.com");

        accountService.save(joinDTO);

    }
}
