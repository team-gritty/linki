package com.Gritty.Linki.user.advertiser.url.service;

import com.Gritty.Linki.domain.user.advertiser.url.repository.UrlRepository;
import com.Gritty.Linki.domain.user.advertiser.url.service.UrlChangeService;
import com.Gritty.Linki.entity.RedirectLinks;
import jakarta.transaction.Transactional;
import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;


@Log4j2
@SpringBootTest
public class urlChangeTests {


    @Autowired
    private UrlChangeService urlChangeService;

    @Autowired
    private UrlRepository urlRepository;


    @Test
    @DisplayName("url 변경 Test")
    @Transactional
    public void urlChangeTest(){
        String userId ="USR-0000000000000502";
        String originUrl = "https://www.erdcloud.com";

        String changeUrl = urlChangeService.changeUrl(userId, originUrl);

        log.info("Change URL: " + changeUrl);
    }

    @Test
    @DisplayName("url - 계약서 연결 Test")
    @Transactional
    public void saveTest(){
        urlChangeService.saveContractId("CTR-0000000000000003", "https://short.link/3");
        RedirectLinks redirectLinks = urlRepository.findByRedirectUrl("https://short.link/3");
        log.info("ID : {}",redirectLinks.getRedirectId());
        log.info("Redirect URL: " + redirectLinks.getRedirectUrl());
        log.info("ContractId : {}",redirectLinks.getContract().getContractId());

    }
}
