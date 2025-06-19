package com.Gritty.Linki.domain.user.influencer.pay.service;

import com.Gritty.Linki.domain.user.influencer.dto.PayUserDto;
import com.Gritty.Linki.domain.user.influencer.pay.repository.InfluencerPayRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import static org.springframework.http.HttpStatus.NOT_FOUND;

@Service
@RequiredArgsConstructor
public class InfluencerPayServiceImpl {
    private final InfluencerPayRepository influencerPayRepository;

    //유저 디티오로 가져옴
    public PayUserDto getUserDtoUserId(String userId) {
        return influencerPayRepository.findByUserId(userId)
                .orElseThrow(() ->
                        new ResponseStatusException(
                                NOT_FOUND, "사용자를 찾을 수 없습니다. id=" + userId));
    }
}
