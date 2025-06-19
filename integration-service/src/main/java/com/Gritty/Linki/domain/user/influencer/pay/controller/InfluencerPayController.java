package com.Gritty.Linki.domain.user.influencer.pay.controller;

import com.Gritty.Linki.config.security.CustomUserDetails;
import com.Gritty.Linki.domain.user.influencer.dto.PayUserDto;
import com.Gritty.Linki.domain.user.influencer.pay.service.InfluencerPayServiceImpl;
import com.Gritty.Linki.domain.user.influencer.responseDTO.PayInfluencerEmailNameResponseDto;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/v1")
@RequiredArgsConstructor
public class InfluencerPayController {

    private final InfluencerPayServiceImpl influencerPayServiceimpl;

    @GetMapping("/user/email-name")
    public ResponseEntity<PayInfluencerEmailNameResponseDto> emailName(
            @AuthenticationPrincipal CustomUserDetails customUserDetails) {
        //로그인된 사용자 아이디
        String userId = customUserDetails.getUsername();
        //사용자 아이디로 PayUserDto 객체 받아옴
        PayUserDto userDtoUserId = influencerPayServiceimpl.getUserDtoUserId(userId);
        //리스폰스 dto 객체로 바꿈
        PayInfluencerEmailNameResponseDto result = new ModelMapper()
                .map(userDtoUserId, PayInfluencerEmailNameResponseDto.class);
        return new ResponseEntity<>(result, HttpStatus.OK);
    }
}
