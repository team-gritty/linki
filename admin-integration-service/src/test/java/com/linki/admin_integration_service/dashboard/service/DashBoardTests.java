package com.linki.admin_integration_service.dashboard.service;

import com.linki.admin_integration_service.domain.dashboard.dto.DashboardResponseDTO;
import com.linki.admin_integration_service.domain.dashboard.service.DashboardService;
import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

@SpringBootTest
@Log4j2
public class DashBoardTests {

    @Autowired
    private DashboardService dashboardService;

    @Test
    public void testDashboard() {
        DashboardResponseDTO dto
                = dashboardService.dashboard();

        log.info("Dashboard Test");
        log.info(dto.toString());
    }
}
