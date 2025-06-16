package com.Gritty.Linki.domain.oAuth.signUp.service;

import com.Gritty.Linki.domain.oAuth.dto.JoinDTO;
import com.Gritty.Linki.domain.oAuth.signUp.repository.AccountRepository;
import com.Gritty.Linki.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.UUID;

@Service
@RequiredArgsConstructor

public class AccountServiceImpl implements AccountService {

    private final AccountRepository accountRepository;
    @Override
    public void save(JoinDTO joinDTO) {

        User user = User.builder()
                .userLoginId(joinDTO.getUserLoginId())
                .userLoginPw(joinDTO.getUserLoginPw())
                .userName(joinDTO.getUserName())
                .userPhone(joinDTO.getUserPhone())
                .userEmail(joinDTO.getUserEmail())
                .userId(UUID.randomUUID().toString().replace("-", "").substring(0, 20))
                .userPayStatus(0)
                .userStatus(0)
                .userEnterDay(LocalDate.now())
                .userRole("일반유저")
                .build();

        accountRepository.save(user);

    }
}
