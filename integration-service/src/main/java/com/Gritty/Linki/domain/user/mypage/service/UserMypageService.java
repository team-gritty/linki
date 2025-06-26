package com.Gritty.Linki.domain.user.mypage.service;

import com.Gritty.Linki.domain.user.mypage.dto.UserMypageRequestDto;
import com.Gritty.Linki.domain.user.mypage.dto.UserMypageResponseDto;
import com.Gritty.Linki.domain.user.mypage.dto.UserPasswordChangeRequestDto;
import com.Gritty.Linki.entity.User;
 
public interface UserMypageService {
    UserMypageResponseDto getUserMypage(String userId);
    void updateUserMypage(String userId, UserMypageRequestDto request);
    void changePassword(String userId, UserPasswordChangeRequestDto request);
    User findUserById(String userId);
} 