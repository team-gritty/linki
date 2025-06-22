package com.linki.admin_integration_service.dashboard.repository;

import com.linki.admin_integration_service.config.MyBatisConfig;
import com.linki.admin_integration_service.domain.dashboard.dto.CampaignDTO;
import com.linki.admin_integration_service.domain.dashboard.dto.ContractStatusMapperDTO;
import com.linki.admin_integration_service.domain.dashboard.dto.MonthlyRevenueDTO;
import com.linki.admin_integration_service.domain.dashboard.dto.TrendMapperDTO;
import com.linki.admin_integration_service.domain.dashboard.repository.myBatis.ContractStatusMapper;
import com.linki.admin_integration_service.domain.dashboard.repository.myBatis.DashBoardSummaryMapper;
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
public class ContractStatusMapperTests {

    @Autowired
    private ContractStatusMapper contractStatusMapper;

    @Test
    @DisplayName("ContractStatus Mapper Test")
    public void testContractStatusMapper() {
        List<ContractStatusMapperDTO> dto = contractStatusMapper.getContractStatusMapper();
        log.info("ContractStatus Mapper Test");
        log.info("ContractStatus Mapper Test size: " + dto.size());


    }
}
