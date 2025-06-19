package com.Gritty.Linki.domain.user.User.mypage.service;

import com.Gritty.Linki.domain.user.User.mypage.dto.UserMypageRequestDto;
import com.Gritty.Linki.domain.user.User.mypage.dto.UserMypageResponseDto;
import com.Gritty.Linki.domain.user.User.mypage.dto.UserPasswordChangeRequestDto;
import com.Gritty.Linki.domain.user.User.mypage.repository.UserMypageRepository;
import com.Gritty.Linki.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class UserMypageServiceImpl implements UserMypageService {

    private final UserMypageRepository userMypageRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional(readOnly = true)
    public UserMypageResponseDto getUserMypage(String userId) {
        User user = userMypageRepository.findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다."));

        UserMypageResponseDto response = new UserMypageResponseDto();
        response.setUserId(user.getUserId());
        response.setUserName(user.getUserName());
        response.setUserPhone(user.getUserPhone());
        response.setUserEmail(user.getUserEmail());
        response.setUserEnterDay(user.getUserEnterDay());

        return response;
    }

    @Override
    public void updateUserMypage(String userId, UserMypageRequestDto request) {
        User user = userMypageRepository.findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다."));

        user.setUserName(request.getUserName());
        user.setUserPhone(request.getUserPhone());
        user.setUserEmail(request.getUserEmail());

        userMypageRepository.save(user);
    }

    @Override
    public void changePassword(String userId, UserPasswordChangeRequestDto request) {
        User user = userMypageRepository.findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다."));

        // 현재 비밀번호 확인
        if (!passwordEncoder.matches(request.getCurrentPassword(), user.getUserLoginPw())) {
            throw new RuntimeException("현재 비밀번호가 올바르지 않습니다.");
        }

        // 새 비밀번호가 현재 비밀번호와 같은지 확인
        if (passwordEncoder.matches(request.getNewPassword(), user.getUserLoginPw())) {
            throw new RuntimeException("새 비밀번호는 현재 비밀번호와 달라야 합니다.");
        }

        // 새 비밀번호로 변경
        user.setUserLoginPw(passwordEncoder.encode(request.getNewPassword()));
        userMypageRepository.save(user);
    }
} 