package com.Gritty.Linki.domain.user.advertiser;

import com.Gritty.Linki.config.security.CustomUserDetails;
import com.Gritty.Linki.domain.user.advertiser.repository.AdvertiserRepository;
import com.Gritty.Linki.entity.Advertiser;
import com.Gritty.Linki.exception.BusinessException;
import com.Gritty.Linki.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Optional;

/**
 * 인증 관련 유틸리티 클래스
 * JWT CustomUserDetails에서 광고주 ID를 찾는 기능을 제공
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class AuthenticationUtil {

    private final AdvertiserRepository advertiserRepository;

    /**
     * CustomUserDetails에서 광고주 ID를 가져오는 메소드
     * 
     * @param userDetails 인증된 사용자 정보
     * @return 광고주 ID
     * @throws BusinessException 광고주를 찾을 수 없는 경우
     */
    public String getAdvertiserIdFromUserDetails(CustomUserDetails userDetails) {
        if (userDetails == null) {
            log.error("UserDetails가 null입니다.");
            throw new BusinessException(ErrorCode.UNAUTHORIZED, "해당 광고주를 찾을 수 없습니다.");
        }

        // 유저 아이디 먼저 추출
        String userId = userDetails.getUserId();
        log.debug("UserDetails에서 추출한 사용자 ID: {}", userId);

        // 광고주 아이디 추출
        return getAdvertiserIdByUserId(userId);
    }

    /**
     * 사용자 ID로 광고주 ID를 조회하는 메소드
     * 
     * @param userId 사용자 ID
     * @return 광고주 ID
     * @throws BusinessException 광고주를 찾을 수 없는 경우
     */
    public String getAdvertiserIdByUserId(String userId) {
        if (userId == null || userId.trim().isEmpty()) {
            log.error("사용자 ID가 null이거나 비어있습니다.");
            throw new BusinessException(ErrorCode.INVALID_INPUT_VALUE, "사용자 ID가 유효하지 않습니다.");
        }

        try {
            Optional<Advertiser> advertiserOpt = advertiserRepository.findByUser_UserId(userId);

            if (advertiserOpt.isPresent()) {
                String advertiserId = advertiserOpt.get().getAdvertiserId();
                log.debug("사용자 ID {}에 대한 광고주 ID: {}", userId, advertiserId);
                return advertiserId;
            } else {
                log.error("사용자 ID {}에 해당하는 광고주를 찾을 수 없습니다.", userId);
                throw new BusinessException(ErrorCode.ENTITY_NOT_FOUND, "광고주 정보를 찾을 수 없습니다.");
            }

        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            log.error("사용자 ID {}로 광고주 조회 중 오류 발생", userId, e);
            throw new BusinessException(ErrorCode.INTERNAL_SERVER_ERROR, "광고주 정보 조회 중 오류가 발생했습니다.");
        }
    }

}