package com.linki.admin_integration_service.domain.linkiscore.service;

public interface CPCService {

    // 전체 평균 CPC 계산
    double getAverageCPC();

    // 개인 CPC 계산하여 점수
    // TODO : 개인 CPC Score 계산
    double getCPCScore(String influencerId);

}
