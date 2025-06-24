package com.Gritty.Linki.domain.user.mypage.service;

import com.Gritty.Linki.domain.user.mypage.dto.UserMypageRequestDto;
import com.Gritty.Linki.domain.user.mypage.dto.UserMypageResponseDto;
import com.Gritty.Linki.domain.user.mypage.dto.UserPasswordChangeRequestDto;
import com.Gritty.Linki.domain.user.mypage.repository.InfluencerMypageRepository;
import com.Gritty.Linki.entity.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class InfluencerMypageServiceImpl implements InfluencerMypageService {

    private final InfluencerMypageRepository influencerMypageRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional(readOnly = true)
    public UserMypageResponseDto getInfluencerMypage(String userId) {
        log.info("인플루언서 마이페이지 조회 시작 - userId: {}", userId);
        
        User user = influencerMypageRepository.findByUserId(userId)
                .orElseThrow(() -> {
                    log.error("인플루언서를 찾을 수 없습니다. - userId: {}", userId);
                    return new RuntimeException("인플루언서를 찾을 수 없습니다.");
                });

        log.info("인플루언서 조회 성공 - userName: {}, userEmail: {}", user.getUserName(), user.getUserEmail());

        UserMypageResponseDto response = new UserMypageResponseDto();
        response.setUserId(user.getUserId());
        response.setUserName(user.getUserName());
        response.setUserPhone(user.getUserPhone());
        response.setUserEmail(user.getUserEmail());
        response.setUserEnterDay(user.getUserEnterDay());

        log.info("인플루언서 마이페이지 응답 생성 완료 - userName: {}", response.getUserName());
        return response;
    }

    @Override
    public void updateInfluencerMypage(String userId, UserMypageRequestDto request) {
        log.info("인플루언서 마이페이지 업데이트 시작 - userId: {}", userId);
        
        User user = influencerMypageRepository.findByUserId(userId)
                .orElseThrow(() -> {
                    log.error("인플루언서를 찾을 수 없습니다. - userId: {}", userId);
                    return new RuntimeException("인플루언서를 찾을 수 없습니다.");
                });

        user.setUserName(request.getUserName());
        user.setUserPhone(request.getUserPhone());
        user.setUserEmail(request.getUserEmail());

        influencerMypageRepository.save(user);
        log.info("인플루언서 마이페이지 업데이트 완료 - userId: {}", userId);
    }

    @Override
    public void changeInfluencerPassword(String userId, UserPasswordChangeRequestDto request) {
        log.info("인플루언서 비밀번호 변경 시작 - userId: {}", userId);
        
        User user = influencerMypageRepository.findByUserId(userId)
                .orElseThrow(() -> {
                    log.error("인플루언서를 찾을 수 없습니다. - userId: {}", userId);
                    return new RuntimeException("인플루언서를 찾을 수 없습니다.");
                });

        // 현재 비밀번호 확인
        if (!passwordEncoder.matches(request.getCurrentPassword(), user.getUserLoginPw())) {
            log.error("현재 비밀번호가 올바르지 않습니다. - userId: {}", userId);
            throw new RuntimeException("현재 비밀번호가 올바르지 않습니다.");
        }

        // 새 비밀번호가 현재 비밀번호와 같은지 확인
        if (passwordEncoder.matches(request.getNewPassword(), user.getUserLoginPw())) {
            log.error("새 비밀번호는 현재 비밀번호와 달라야 합니다. - userId: {}", userId);
            throw new RuntimeException("새 비밀번호는 현재 비밀번호와 달라야 합니다.");
        }

        // 새 비밀번호로 변경
        user.setUserLoginPw(passwordEncoder.encode(request.getNewPassword()));
        influencerMypageRepository.save(user);
        log.info("인플루언서 비밀번호 변경 완료 - userId: {}", userId);
    }
} 