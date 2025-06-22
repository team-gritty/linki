package com.Gritty.Linki.domain.user.advertiser.url.service;

import com.Gritty.Linki.domain.user.advertiser.repository.jpa.AdvertiserRepository;
import com.Gritty.Linki.domain.user.advertiser.url.repository.UrlRepository;
import com.Gritty.Linki.entity.Advertiser;
import com.Gritty.Linki.entity.Contract;
import com.Gritty.Linki.entity.RedirectLinks;
import com.Gritty.Linki.entity.User;
import com.Gritty.Linki.util.IdGenerator;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import com.aventrix.jnanoid.jnanoid.NanoIdUtils;

import java.security.SecureRandom;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Log4j2
public class UrlChangeServiceImpl implements UrlChangeService {

@PersistenceContext
private EntityManager em;

private final AdvertiserRepository advertiserRepository;
private final UrlRepository urlRepository;

private static final char[] URL_CHAR_SET = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789".toCharArray();
private static final SecureRandom random = new SecureRandom();

    /**
     * 광고주의 origin URL을 전달받아, 고유한 redirect URL을 생성하여 저장합니다.
     * 이후 외부 API에서 사용할 수 있도록 redirect URL을 반환합니다.
     *
     * @param userId redirect URL을 생성할 대상 유저 ID
     * @param originUrl 원본 URL
     * @return 생성된 redirect URL
     */
    @Override
    @Transactional
    public String changeUrl(String userId,String originUrl) {

        RedirectLinks urlRedirect = new RedirectLinks();

        // 사용자 ID로 User 엔티티 조회
        User user = em.find(User.class, userId);

        // User와 연관된 Advertiser 조회
        Optional<Advertiser> advertiser = advertiserRepository.findByUser_UserId(user.getUserId());

        // 광고주가 없으면 예외 발생
        if (advertiser.isEmpty()) {
            throw new IllegalArgumentException("존재하지 않는 광고주 입니다.");
        }

        Advertiser advertiserEntity = advertiser.get();
        
        String redirectId = IdGenerator.redirectId();
        while(urlRepository.existsById(redirectId)) {
            redirectId = IdGenerator.redirectId();
        }

        // 리다이렉트 링크 엔티티 생성 및 값 설정
        urlRedirect.setRedirectId(redirectId);
        urlRedirect.setOriginUrl(originUrl);
        urlRedirect.setRedirectUrl(createUrl());
        urlRedirect.setAdvertiser(advertiserEntity);

        // DB에 저장
        urlRepository.save(urlRedirect);

        return urlRedirect.getRedirectUrl();
    }

    /**
     * 주어진 redirect URL에 해당하는 RedirectLinks 엔티티에 계약서 ID를 연결합니다.
     *
     * @param contractId 연결할 계약서의 ID
     * @param url 계약서 ID를 연결할 대상 redirect URL
     */
    @Override
    public void saveContractId(String contractId, String url) {
        // redirectUrl로 기존 RedirectLinks 엔티티 조회
        RedirectLinks urlRedirect = urlRepository.findByRedirectUrl(url);
        // 계약서 ID로 Contract 엔티티 조회
        Contract contract = em.find(Contract.class, contractId);
        // 해당 RedirectLinks에 계약서 정보 연동
        urlRedirect.setContract(contract);
        // 수정된 RedirectLinks 저장
        urlRepository.save(urlRedirect);
    }

    /**
     * 고유한 redirect URL을 생성합니다. 이미 존재하는 경우 재생성합니다.
     *
     * @return 중복되지 않는 숏 URL
     */
    private String createUrl() {
        String newUrl = createShortUrl();
        // 중복되지 않는 숏 URL이 생성될 때까지 반복
        while(urlRepository.existsByRedirectUrl(newUrl)){
            newUrl = createShortUrl();
        }
        return newUrl;
    }


    /**
     * 6자리 난수 기반의 숏 URL을 생성합니다.
     *
     * @return 생성된 6자리 난수 URL
     */
    private String createShortUrl() {
        // 6자리 난수로 구성된 URL 생성
        return NanoIdUtils.randomNanoId(random, URL_CHAR_SET, 6);
    }

}
