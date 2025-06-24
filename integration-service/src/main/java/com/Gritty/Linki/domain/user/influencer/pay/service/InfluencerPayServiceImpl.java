package com.Gritty.Linki.domain.user.influencer.pay.service;

import com.Gritty.Linki.domain.user.influencer.dto.PayUserDto;
import com.Gritty.Linki.domain.user.influencer.pay.repository.InfluencerPayRepository;
import com.Gritty.Linki.entity.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import static org.springframework.http.HttpStatus.NOT_FOUND;

@Service
@RequiredArgsConstructor
@Slf4j
public class InfluencerPayServiceImpl implements InfluencerPayService {
    private final InfluencerPayRepository influencerPayRepository;

    //유저 디티오로 가져옴
    public PayUserDto getUserDtoUserId(String userId) {
        log.info("getUserDtoUserId {}", userId);
        User user = influencerPayRepository.findByUserId(userId)
                .orElseThrow(() ->
                        new ResponseStatusException(
                                NOT_FOUND, "사용자를 찾을 수 없습니다. id=" + userId));
        return new ModelMapper().map(user, PayUserDto.class);
    }
}
