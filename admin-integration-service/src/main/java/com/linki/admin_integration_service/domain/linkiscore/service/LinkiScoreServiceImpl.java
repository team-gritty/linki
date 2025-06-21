package com.linki.admin_integration_service.domain.linkiscore.service;

import com.linki.admin_integration_service.domain.linkiscore.repository.InfluencerRepository;
import com.linki.admin_integration_service.domain.linkiscore.repository.LinkiScoreRepository;
import com.linki.admin_integration_service.entity.Influencer;
import com.linki.admin_integration_service.entity.LinkiScore;
import com.linki.admin_integration_service.util.IdGenerator;
import jakarta.annotation.PostConstruct;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

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

    @PostConstruct
    public void init(){
        new Thread(() -> {
            try {
                Thread.sleep(10_000);
                scheduleLinkiScore();
            } catch (Exception e) {
                log.error("점수 계산 실패", e);
            }
        }).start();

    }


    @Override
    @Transactional
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
    @Scheduled(cron = "0 0 2 * * *") //
    @Transactional
    public void scheduleLinkiScore() {
        List<Influencer> influencers = influencerRepository.findAll();
        for (Influencer influencer : influencers) {
            if(!linkiScoreRepository.existsByInfluencerId(influencer.getInfluencerId())){
                newLinkiScore(influencer.getInfluencerId());
            }
            else {
                saveLinkiScore(influencer.getInfluencerId());
            }
        }
    }

    @Override
    @Transactional
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
