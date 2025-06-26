package com.Gritty.Linki.domain.user.influencer.contract.util;

import com.Gritty.Linki.domain.user.influencer.contract.service.AdvertiserContractService;
import com.Gritty.Linki.domain.user.influencer.contract.service.InfluencerContractService;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
@Log4j2
public class ContractStatusScheduler {
    private final AdvertiserContractService advertiserContractService;
    private final InfluencerContractService influencerContractService;

    @Scheduled(cron = "0 0 0 * * ?") // 매일 자정
    public void autoUpdateExpiredContracts() {
        int advertiserUpdated = advertiserContractService.updateContractsToCompleted();
        log.info("스케줄러: 만료된 광고주 계약 상태 {}건 자동 업데이트 완료", advertiserUpdated);

        int influencerUpdated = influencerContractService.updateContractsToCompleted();
        log.info("스케줄러: 만료된 인플루언서 계약 상태 {}건 자동 업데이트 완료", advertiserUpdated);

    }

    @EventListener(ApplicationReadyEvent.class)
    @Transactional
    public void runOnceAfterStartup() {
        int count = advertiserContractService.updateContractsToCompleted();
        log.info("앱 시작 후 한 번 실행: 광고주 계약 상태 {}건 갱신", count);

        int count2 = influencerContractService.updateContractsToCompleted();
        log.info("앱 시작 후 한 번 실행: 인플루언서 계약 상태 {}건 갱신", count2);
    }


}
