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
        String originUrl = redirectService.searchRedirectUrl(shortUrl);

        if (originUrl == null){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }

        URI uri = URI.create(originUrl);
        return ResponseEntity.status(HttpStatus.FOUND).location(uri).build();
    }
}
