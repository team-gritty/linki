package com.linki.admin_integration_service.common.redirect.service;

import com.linki.admin_integration_service.entity.RedirectLinks;
import com.linki.admin_integration_service.common.redirect.repository.RedirectRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
@RequiredArgsConstructor
@Log4j2
public class RedirectServiceImpl implements RedirectService {

    private final RedirectRepository redirectRepository;

    //kwy : short value : origin Url
    private final Map<String, String> cashRedirect = new ConcurrentHashMap<>();

    @Override
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

        return originUrl;
    }
}
