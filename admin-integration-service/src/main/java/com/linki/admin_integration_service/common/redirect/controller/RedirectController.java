package com.linki.admin_integration_service.common.redirect.controller;

import com.linki.admin_integration_service.common.redirect.service.RedirectService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;

@RestController
@RequiredArgsConstructor
@Log4j2
public class RedirectController {

    private final RedirectService redirectService;

    @GetMapping("/v1/admin/api/redirect/{shortUrl}")
    public ResponseEntity<Void> redirect(@PathVariable String shortUrl) {
        log.info("Redirecting to {}", shortUrl);
        URI uri = URI.create(redirectService.searchRedirectUrl(shortUrl));
        log.info("Redirected to {}", uri);
        return ResponseEntity.status(HttpStatus.FOUND).location(uri).build();
    }
}
