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

    private final Map<String, String> cashRedirect = new ConcurrentHashMap<>();

    @Override
    public String searchRedirectUrl(String shortUrl) {
        String cached = cashRedirect.get(shortUrl);
        if (cached != null) {
            return cached;
        }

        RedirectLinks redirectLinks = redirectRepository.findByRedirectUrl(shortUrl);
        if (redirectLinks == null) {
            return null;
        }
        String originUrl = redirectLinks.getOriginUrl();
        cashRedirect.put(shortUrl, originUrl);
        return originUrl;
    }
}
