package com.Gritty.Linki.domain.user.mypage.bizNumberCheck.service;

import com.Gritty.Linki.config.security.CustomUserDetails;

public interface AdvertiserRegisterService {

    void saveBizInfo(CustomUserDetails customUserDetails, String bizNum, String bizName, String userId);
}
