package com.linki.admin_integration_service.common.redirect.service;

import com.linki.admin_integration_service.common.redirect.repository.RedirectClickRepository;
import com.linki.admin_integration_service.entity.RedirectClick;
import com.linki.admin_integration_service.entity.RedirectLinks;
import com.linki.admin_integration_service.common.redirect.repository.RedirectRepository;
import com.linki.admin_integration_service.util.IdGenerator;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

@Service
@RequiredArgsConstructor
@Log4j2
public class RedirectServiceImpl implements RedirectService {

    private final RedirectRepository redirectRepository;
    private final RedirectClickRepository redirectClickRepository;

    @PersistenceContext
    private final EntityManager em;

    //kwy : short value : origin Url
    private final Map<String, String> cashRedirect = new ConcurrentHashMap<>();

    @Override
    @Transactional
    //변환된 url
    // 캐시 있으면 반환
    public String searchRedirectUrl(String shortUrl) {
        String cached = cashRedirect.get(shortUrl);
        if (cached != null) {
            return cached;
        }
        //캐시 없으면 찾아서 반환 , 저장  db 에서 (계약서 생성될떄 만들어진 튜플)
        RedirectLinks redirectLinks = redirectRepository.findByRedirectUrl(shortUrl);
        if (redirectLinks == null) {
            return null;
        }
        String originUrl = redirectLinks.getOriginUrl();
        cashRedirect.put(shortUrl, originUrl);

        //TODO : 저장 로직 or event 발행
        saveRedirectUrl(shortUrl);

        return originUrl;
    }

    @Transactional
    protected void saveRedirectUrl(String shortUrl) {
        // shortUrl 기준 redirectLink 검색
        RedirectLinks redirectLinks = redirectRepository.findByRedirectUrl(shortUrl);
        RedirectClick redirectClickEntity = new RedirectClick();

        // redirectLink Id , LocalDate.now() 일치 하는 레코드 있나 찾기
        // 있으면 카운트 + 1
        if(redirectClickRepository.existsByRedirectLinkAndClickTime(redirectLinks,LocalDate.now())){
            List<RedirectClick> redirectClick = redirectClickRepository.findByRedirectLink(redirectLinks);
            redirectClickEntity = redirectClick.stream().
                    findFirst().orElse(null);
            // 동시성 테스트 : 고트래픽 상황에서 NPE 발생 유도
            // 만약에 redirectClickEntity가 null이면 바로 NPE 터트림
            Objects.requireNonNull(redirectClickEntity).setClickCount(redirectClickEntity.getClickCount() + 1);
            redirectClickRepository.save(redirectClickEntity);
        }
        else {
            // 없으면 엔티티 새로 만들어서 저장
            redirectClickEntity.setClickId(IdGenerator.clickId());
            redirectClickEntity.setClickTime(LocalDate.now());
            redirectClickEntity.setClickCount(1);
            redirectClickEntity.setRedirectLink(redirectLinks);
            redirectClickRepository.save(redirectClickEntity);
        }


    }
}
