package com.Gritty.Linki.domain.oAuth.signUp.service;

import com.Gritty.Linki.domain.oAuth.dto.JoinDTO;
import com.Gritty.Linki.domain.oAuth.signUp.repository.AccountRepository;
import com.Gritty.Linki.entity.User;
import com.Gritty.Linki.util.AesUtil;
import com.Gritty.Linki.util.IdGenerator;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import java.util.Optional;
import java.time.LocalDate;
import java.util.UUID;

@Service
@RequiredArgsConstructor

public class AccountServiceImpl implements AccountService {

    private final AccountRepository accountRepository;
    private final PasswordEncoder passwordEncoder;
    @Override
    public void save(JoinDTO joinDTO) {

        User user = User.builder()
                .userLoginId(joinDTO.getUserLoginId())
                .userLoginPw(passwordEncoder.encode(joinDTO.getUserLoginPw()))//aes 암호화
                .userName(joinDTO.getUserName())
                .userPhone(joinDTO.getUserPhone())
                .userEmail(joinDTO.getUserEmail())
                .userId(IdGenerator.userId())//id제네레이터 이용
                .userPayStatus(0)
                .userStatus(1)
                .userEnterDay(LocalDate.now())
                .userRole("ROLE_USER")
                .build();

        accountRepository.save(user);

    }

    @Override
    public User find(String userLoginId, String userLoginPw) {
        return null;
    }

    @Override
    public User find(String userLoginId) {
        return accountRepository.findByUserLoginId(userLoginId);
    }
}
