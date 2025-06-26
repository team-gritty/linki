package com.Gritty.Linki.domain.user.mypage.bizNumberCheck.service;

import com.Gritty.Linki.config.security.CustomUserDetails;
import com.Gritty.Linki.domain.account.account.repository.AccountRepository;
import com.Gritty.Linki.domain.user.mypage.bizNumberCheck.repository.AdvertiserRegisterRepository;
import com.Gritty.Linki.entity.Advertiser;
import com.Gritty.Linki.entity.User;
import com.Gritty.Linki.util.IdGenerator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class AdvertiserRegisterServiceImpl implements AdvertiserRegisterService{
    private final AdvertiserRegisterRepository advertiserRegisterRepository;
    private final AccountRepository accountRepository;

    @Override
    public void saveBizInfo(CustomUserDetails customUserDetails,
            String bizNum, String bizName, String userId) {
        Advertiser advertiser = Advertiser.builder()
                .advertiserId(IdGenerator.advertiserId())
                .businessNumber(bizNum)
                .companyName(bizName)
                .userId(userId)
                .build();
        log.info("광고주등록서비스");
        log.info(advertiser.getUserId());
        log.info(advertiser.getAdvertiserId());
        log.info(advertiser.getBusinessNumber());
        log.info(advertiser.getCompanyName());


        advertiserRegisterRepository.save(advertiser);
        User user = accountRepository.findById(customUserDetails.getUserId()).orElseThrow();
        user.setUserRole("ROLE_INFLUENCER");



    }
}
