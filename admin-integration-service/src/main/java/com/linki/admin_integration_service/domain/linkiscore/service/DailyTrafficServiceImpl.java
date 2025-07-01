package com.linki.admin_integration_service.domain.linkiscore.service;


import com.linki.admin_integration_service.common.redirect.repository.RedirectClickRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;


@Service
@RequiredArgsConstructor
public class DailyTrafficServiceImpl implements DailyTrafficService {

    private final RedirectClickRepository redirectClickRepository;

    @PersistenceContext
    private EntityManager entityManager;

    private Double cachedAverageTraffic = null;


    @Override
    public double averageTrafficScore() {
        if (cachedAverageTraffic != null) {
            return cachedAverageTraffic;
        }

        Double average = redirectClickRepository.getAverageDailyClickCount();
        cachedAverageTraffic = average != null ? average : 0.0;
        return cachedAverageTraffic;
    }

    @Scheduled(cron = "0 0 1 * * *") // 매일 새벽 1시
    public void resetCache() {
        cachedAverageTraffic = null;
    }

    @Override
    public double getTrafficScore(String influencerId) {

        String sql = """
            SELECT COUNT(*) / COUNT(DISTINCT DATE(rc.click_time))
            FROM redirect_click rc
            JOIN redirect_links rl ON rc.redirect_id = rl.redirect_id
            WHERE rl.advertiser_id = :influencerId
        """;

        Object result = entityManager.createNativeQuery(sql)
            .setParameter("influencerId", influencerId)
            .getSingleResult();

        double dailyAverage = result != null ? ((Number) result).doubleValue() : 0.0;

        double overallAverage = averageTrafficScore();

        if (overallAverage == 0 || dailyAverage == 0) return (60.0 * 0.25);

        double ratio = dailyAverage / overallAverage;

        double calculateResult = 0;

        if (ratio >= 2.0) calculateResult = 100;
        else if (ratio >= 1.5) calculateResult =  90;
        else if (ratio >= 1.2) calculateResult =  80;
        else if (ratio >= 1.0) calculateResult =  70;
        else calculateResult =  60;

        return calculateResult * 0.25;
    }
}
