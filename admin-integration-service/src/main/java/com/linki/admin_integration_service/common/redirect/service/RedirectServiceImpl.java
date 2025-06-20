package com.linki.admin_integration_service.common.redirect.service;

import com.linki.admin_integration_service.entity.RedirectLinks;
import com.linki.admin_integration_service.common.redirect.repository.RedirectRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RedirectServiceImpl implements RedirectService {

    private final RedirectRepository redirectRepository;

    @Override
    public String searchRedirectUrl(String shortUrl) {
        RedirectLinks redirectLinks = redirectRepository.findByRedirectUrl(shortUrl);
        return redirectLinks.getOriginUrl();
    }
}
