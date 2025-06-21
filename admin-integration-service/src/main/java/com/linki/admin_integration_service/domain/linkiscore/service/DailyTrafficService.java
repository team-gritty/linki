package com.linki.admin_integration_service.domain.linkiscore.service;

public interface DailyTrafficService {

    double averageTrafficScore();
    double getTrafficScore(String influencerId);
}
