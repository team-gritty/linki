package com.linki.admin_integration_service.dashboard.repository;

import com.linki.admin_integration_service.config.MyBatisConfig;
import com.linki.admin_integration_service.domain.dashboard.dto.CampaignDTO;
import com.linki.admin_integration_service.domain.dashboard.dto.MonthlyRevenueDTO;
import com.linki.admin_integration_service.domain.dashboard.dto.TrendMapperDTO;
import com.linki.admin_integration_service.domain.dashboard.repository.myBatis.DashBoardSummaryMapper;
import com.linki.admin_integration_service.util.GptClient;
import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mybatis.spring.boot.test.autoconfigure.MybatisTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;

import org.springframework.context.annotation.Import;

import java.util.List;

@MybatisTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE) // 실제 DB 그대로 써라
@Import(MyBatisConfig.class)
@Log4j2
public class DashBoardSummaryMapperTests {

    @Autowired
    private DashBoardSummaryMapper dashBoardSummaryMapper;

    @Test
    @DisplayName("대시보드 Summary Mapper Test")
    public void testSummaryMapper() {
        int getAdvertiserCount = dashBoardSummaryMapper.getAdvertiserCount();
        int getInfluencerCount = dashBoardSummaryMapper.getInfluencerCount();
        int getActiveCampaignCount = dashBoardSummaryMapper.getActiveCampaignCount();
        int getOngoingContracts = dashBoardSummaryMapper.getOngoingContracts();
        int getCurrentSubscribers= dashBoardSummaryMapper.getCurrentSubscribers();
        List<MonthlyRevenueDTO> getMonthlyRevenue =  dashBoardSummaryMapper.getMonthlyRevenue();
        List<TrendMapperDTO> getEnterDay =  dashBoardSummaryMapper.getEnterDay();
        List<CampaignDTO> getCampaigns = dashBoardSummaryMapper.getCampaigns();

        log.info("getMonthlyRevenue : " + getMonthlyRevenue.size());
        log.info("getEnterDay : " + getEnterDay.size());
        log.info("getActiveCampaignCount : " + getActiveCampaignCount);
        log.info("getOngoingContracts : " + getOngoingContracts);
        log.info("getCurrentSubscribers : " + getCurrentSubscribers);
        log.info("getAdvertiserCount : " + getAdvertiserCount);
        log.info("getOngoingContracts : " + getOngoingContracts);
        log.info("getCampaigns : " + getCampaigns.size());
        log.info("getInfluencerCount : " + getInfluencerCount);

    }
}
