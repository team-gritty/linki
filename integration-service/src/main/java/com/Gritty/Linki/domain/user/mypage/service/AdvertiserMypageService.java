package com.Gritty.Linki.domain.user.mypage.service;

import com.Gritty.Linki.domain.user.mypage.dto.UserMypageRequestDto;
import com.Gritty.Linki.domain.user.mypage.dto.UserMypageResponseDto;
import com.Gritty.Linki.domain.user.mypage.dto.UserPasswordChangeRequestDto;

public interface AdvertiserMypageService {
    UserMypageResponseDto getAdvertiserMypage(String userId);
    void updateAdvertiserMypage(String userId, UserMypageRequestDto request);
    void changeAdvertiserPassword(String userId, UserPasswordChangeRequestDto request);
} 