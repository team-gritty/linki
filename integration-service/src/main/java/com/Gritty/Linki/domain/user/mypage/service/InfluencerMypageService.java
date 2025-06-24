package com.Gritty.Linki.domain.user.mypage.service;

import com.Gritty.Linki.domain.user.mypage.dto.UserMypageRequestDto;
import com.Gritty.Linki.domain.user.mypage.dto.UserMypageResponseDto;
import com.Gritty.Linki.domain.user.mypage.dto.UserPasswordChangeRequestDto;

public interface InfluencerMypageService {
    UserMypageResponseDto getInfluencerMypage(String userId);
    void updateInfluencerMypage(String userId, UserMypageRequestDto request);
    void changeInfluencerPassword(String userId, UserPasswordChangeRequestDto request);
} 