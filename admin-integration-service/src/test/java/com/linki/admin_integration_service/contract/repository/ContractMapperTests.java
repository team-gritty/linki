package com.linki.admin_integration_service.contract.repository;

import com.linki.admin_integration_service.config.MyBatisConfig;
import com.linki.admin_integration_service.domain.contract.dto.ContractDTO;
import com.linki.admin_integration_service.domain.contract.dto.ContractSearchDTO;
import com.linki.admin_integration_service.domain.contract.repository.myBatis.ContractMapper;
import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;

import java.util.List;

@SpringBootTest
@Import(MyBatisConfig.class)
@Log4j2
public class ContractMapperTests {

    @Autowired
    private ContractMapper contractMapper;

    @Test
    @DisplayName("계약 정보 가져오기 Mapper Test")
    public void getAllContractsTest(){
        List<ContractDTO> contractDTOList = contractMapper.getContracts();
        log.info("테스트 결과 : {}",contractDTOList.size());
    }

    @Test
    @DisplayName("계약 정보 검색 Mapper Test")
    public void searchContractsTest(){
        // 1. keyword = null / searchType = Notnull
        ContractSearchDTO contractSearchDTO1 = new ContractSearchDTO();
        contractSearchDTO1.setSearchType("contractId");
        contractSearchDTO1.setKeyword("");
        List<ContractDTO> contractDTOS1 = contractMapper.searchContract(contractSearchDTO1);
        log.info("테스트 1 : keyword=null, searchType=contractId : {}", contractDTOS1.size());

        // 2. keyword = notNull / searchType = null
        ContractSearchDTO contractSearchDTO2 = new ContractSearchDTO();
        contractSearchDTO2.setSearchType("");
        contractSearchDTO2.setKeyword("사용자500");
        List<ContractDTO> contractDTOS2 = contractMapper.searchContract(contractSearchDTO2);
        log.info("테스트 2 : keyword=사용자500, searchType=null : {}", contractDTOS2.size());

        // 3. keyword = null / searchType = null
        ContractSearchDTO contractSearchDTO3 = new ContractSearchDTO();
        contractSearchDTO3.setSearchType("");
        contractSearchDTO3.setKeyword("");
        List<ContractDTO> contractDTOS3 = contractMapper.searchContract(contractSearchDTO3);
        log.info("테스트 3 : keyword=null, searchType=null : {}", contractDTOS3.size());

        // 4. searchType = influencerName
        ContractSearchDTO contractSearchDTO4 = new ContractSearchDTO();
        contractSearchDTO4.setSearchType("influencerName");
        contractSearchDTO4.setKeyword("사용자1");
        List<ContractDTO> contractDTOS4 = contractMapper.searchContract(contractSearchDTO4);
        log.info("테스트 4 : searchType=influencerName, keyword=사용자1 : {}", contractDTOS4.size());

        // 5. searchType = advertiserName
        ContractSearchDTO contractSearchDTO5 = new ContractSearchDTO();
        contractSearchDTO5.setSearchType("advertiserName");
        contractSearchDTO5.setKeyword("사용자500");
        List<ContractDTO> contractDTOS5 = contractMapper.searchContract(contractSearchDTO5);
        log.info("테스트 5 : searchType=advertiserName, keyword=사용자500 : {}", contractDTOS5.size());

        // 6. searchType = contractId
        ContractSearchDTO contractSearchDTO6 = new ContractSearchDTO();
        contractSearchDTO6.setSearchType("contractId");
        contractSearchDTO6.setKeyword("1");
        List<ContractDTO> contractDTOS6 = contractMapper.searchContract(contractSearchDTO6);
        log.info("테스트 6 : searchType=contractId, keyword=1 : {}", contractDTOS6.size());

    }
}
