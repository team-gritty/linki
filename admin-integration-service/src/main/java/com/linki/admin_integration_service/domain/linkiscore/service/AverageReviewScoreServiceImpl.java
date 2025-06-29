package com.linki.admin_integration_service.domain.linkiscore.service;


import com.linki.admin_integration_service.domain.operations.repository.jpa.InfluencerReviewRepository;
import com.linki.admin_integration_service.entity.InfluencerReview;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

@Service
@RequiredArgsConstructor
public class AverageReviewScoreServiceImpl implements AverageReviewScoreService {

    private final InfluencerReviewRepository influencerReviewRepository;

    private static volatile List<InfluencerReview> cachedList = new CopyOnWriteArrayList<>();

    @Scheduled(cron = "0 0 1 * * *") //
    private void cached(){
        cachedList = new CopyOnWriteArrayList<>();
        cachedList = influencerReviewRepository.findAll();
    }


    @Override
    @Transactional(readOnly = true)
    public double getReviewScore(String influencerId) {

        if (cachedList.isEmpty()){
            cachedList = influencerReviewRepository.findAllWithJoin();
        }


        List<InfluencerReview> list
                = cachedList.stream()
                .filter(dto -> dto.getContract().getProposal().getInfluencer().getInfluencerId().equals(influencerId))
                .toList();

        BigDecimal total = BigDecimal.ZERO;
        BigDecimal count = BigDecimal.ZERO;
        for (InfluencerReview influencerReview : list) {
            total = total.add(influencerReview.getScore());
            count = count.add(BigDecimal.ONE);
        }

        if (total.compareTo(BigDecimal.ZERO) == 0 || count.compareTo(BigDecimal.ZERO) == 0){
            return 60.0 * 0.25;
        }

        BigDecimal average = total.divide(count, RoundingMode.HALF_UP);

        BigDecimal calculatedResult;

        if (average.compareTo(new BigDecimal("3.0")) > 0 && average.compareTo(new BigDecimal("3.5")) < 0) {
            calculatedResult = BigDecimal.valueOf(70);
        } else if (average.compareTo(new BigDecimal("3.5")) >= 0 && average.compareTo(new BigDecimal("4.0")) < 0) {
            calculatedResult = BigDecimal.valueOf(80);
        } else if (average.compareTo(new BigDecimal("4.0")) >= 0 && average.compareTo(new BigDecimal("4.5")) <= 0) {
            calculatedResult = BigDecimal.valueOf(90);
        } else if (average.compareTo(new BigDecimal("4.5")) >= 0) {
            calculatedResult = BigDecimal.valueOf(100);
        } else {
            calculatedResult = BigDecimal.valueOf(60);
        }


        BigDecimal result = calculatedResult.multiply(BigDecimal.valueOf(0.25));

        return result.setScale(0, RoundingMode.HALF_UP).doubleValue();
    }
}
