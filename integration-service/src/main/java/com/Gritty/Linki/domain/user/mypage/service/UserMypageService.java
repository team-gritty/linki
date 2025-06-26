package com.Gritty.Linki.domain.user.mypage.service;

import com.Gritty.Linki.domain.user.mypage.dto.UserMypageRequestDto;
import com.Gritty.Linki.domain.user.mypage.dto.UserMypageResponseDto;
import com.Gritty.Linki.domain.user.mypage.dto.UserPasswordChangeRequestDto;
 
public interface UserMypageService {
    UserMypageResponseDto getUserMypage(String userId);
    void updateUserMypage(String userId, UserMypageRequestDto request);
    void changePassword(String userId, UserPasswordChangeRequestDto request);
} 