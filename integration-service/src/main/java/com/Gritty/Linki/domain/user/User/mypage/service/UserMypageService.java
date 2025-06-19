package com.Gritty.Linki.domain.user.User.mypage.service;

import com.Gritty.Linki.domain.user.User.mypage.dto.UserMypageRequestDto;
import com.Gritty.Linki.domain.user.User.mypage.dto.UserMypageResponseDto;

public interface UserMypageService {
    UserMypageResponseDto getUserMypage(String userId);
    void updateUserMypage(String userId, UserMypageRequestDto request);
} 