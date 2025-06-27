package com.linki.admin_integration_service.dashboard.service;

import com.linki.admin_integration_service.domain.dashboard.dto.DashboardResponseDTO;
import com.linki.admin_integration_service.domain.dashboard.service.DashboardService;
import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.TestPropertySource;

@SpringBootTest
@Log4j2
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@TestPropertySource(properties = {
    "spring.cache.type=none",
    "spring.data.redis.host=localhost",
    "spring.data.redis.port=6379"
})
public class DashBoardTests {

    @Autowired
    private DashboardService dashboardService;


    @Test
    public void testDashboard() {
        DashboardResponseDTO dashboard = dashboardService.dashboard();
        log.info("dashboard: {}", dashboard);
    }
}
