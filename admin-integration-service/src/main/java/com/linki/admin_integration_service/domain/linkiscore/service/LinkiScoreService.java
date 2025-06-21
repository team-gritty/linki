package com.linki.admin_integration_service.domain.linkiscore.service;

public interface LinkiScoreService {

    void saveLinkiScore(String influencerId);
    void scheduleLinkiScore();

    void newLinkiScore(String influencerId);
}
