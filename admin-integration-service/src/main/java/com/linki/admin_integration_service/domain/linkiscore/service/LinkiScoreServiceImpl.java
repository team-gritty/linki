package com.linki.admin_integration_service.domain.linkiscore.service;

import com.linki.admin_integration_service.domain.linkiscore.repository.InfluencerRepository;
import com.linki.admin_integration_service.domain.linkiscore.repository.LinkiScoreRepository;
import com.linki.admin_integration_service.entity.Influencer;
import com.linki.admin_integration_service.entity.LinkiScore;
import com.linki.admin_integration_service.util.IdGenerator;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.context.ApplicationContext;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
@Log4j2
public class LinkiScoreServiceImpl implements LinkiScoreService {

    private final CPCService cpcService;
    private final AverageReviewScoreService averageReviewScoreService;
    private final ContractCountService contractCountService;
    private final DailyTrafficService dailyTrafficService;


    private final LinkiScoreRepository linkiScoreRepository;
    private final InfluencerRepository influencerRepository;
    private final ApplicationContext context;

//    @PostConstruct
    public void init(){
        new Thread(() -> {
            try {
                Thread.sleep(10_000);
                context .getBean(LinkiScoreService.class)
                        .scheduleLinkiScore();
            } catch (Exception e) {
                log.error("점수 계산 실패", e);
            }
        }).start();

    }


    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void saveLinkiScore(String influencerId) {

        LinkiScore linkiScore = linkiScoreRepository.findByInfluencerId(influencerId);

        // CPC(클릭당 광고단가)
        BigDecimal CPCScore = BigDecimal.valueOf(cpcService.getCPCScore(influencerId));
        linkiScore.setCostPerClick(CPCScore);

        // Daily 유입수
        BigDecimal dailyTraffic = BigDecimal.valueOf(dailyTrafficService.getTrafficScore(influencerId));
        linkiScore.setDailyTraffic(dailyTraffic);

        // 리뷰 평균 점수
        BigDecimal averageReviewScore = BigDecimal.valueOf(averageReviewScoreService.getReviewScore(influencerId));
        linkiScore.setAverageReviewScore(averageReviewScore);

        // 계약 건수 점수
        BigDecimal contractCount = BigDecimal.valueOf(contractCountService.getContractScore(influencerId));
        linkiScore.setContractCount(contractCount);


        linkiScoreRepository.save(linkiScore);
    }

    @Override
    @Transactional
    @Scheduled(cron = "0 0 2 * * *") //
    public void scheduleLinkiScore() {
        List<Influencer> influencers = influencerRepository.findAll();

        influencers.parallelStream().forEach(influencer -> {
            try {
                String influencerId = influencer.getInfluencerId();
                synchronized (this) {
                    if (!linkiScoreRepository.existsByInfluencerId(influencerId)) {
                        newLinkiScore(influencerId);
                    } else {
                        saveLinkiScore(influencerId);
                    }
                }
            } catch (Exception e) {
                log.error("점수 계산 중 오류: {}", influencer.getInfluencerId(), e);
            }
        });
    }


    @Override
    @Retryable(
            value = { DataIntegrityViolationException.class },
            maxAttempts = 100,
            backoff = @Backoff(delay = 1000)
    )
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void newLinkiScore(String influencerId) {

        LinkiScore linkiScore = new LinkiScore();

        //PK
        linkiScore.setId(IdGenerator.linkiScoreId());

        // CPC(클릭당 광고단가)
        BigDecimal CPCScore = BigDecimal.valueOf(cpcService.getCPCScore(influencerId));
        linkiScore.setCostPerClick(CPCScore);

        // Daily 유입수
        BigDecimal dailyTraffic = BigDecimal.valueOf(dailyTrafficService.getTrafficScore(influencerId));
        linkiScore.setDailyTraffic(dailyTraffic);

        // 리뷰 평균 점수
        BigDecimal averageReviewScore = BigDecimal.valueOf(averageReviewScoreService.getReviewScore(influencerId));
        linkiScore.setAverageReviewScore(averageReviewScore);

        // 계약 건수 점수
        BigDecimal contractCount = BigDecimal.valueOf(contractCountService.getContractScore(influencerId));
        linkiScore.setContractCount(contractCount);

        // 인플루언서 ID
        linkiScore.setInfluencerId(influencerId);

        linkiScoreRepository.save(linkiScore);
    }
}
