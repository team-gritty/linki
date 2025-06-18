package com.linki.admin_integration_service.domain.contract.service;

import com.linki.admin_integration_service.domain.contract.dto.ContractDTO;
import com.linki.admin_integration_service.domain.contract.dto.ContractSearchDTO;
import com.linki.admin_integration_service.domain.contract.repository.myBatis.ContractMapper;
import com.linki.admin_integration_service.domain.user.dto.InfluencerUserDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.lang.Contract;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@RequiredArgsConstructor
@Log4j2
public class ContractServiceImpl implements ContractService {

    private final ContractMapper contractMapper;

    @Override
    public List<ContractDTO> getContracts() {
        List<ContractDTO> contractDTOList = contractMapper.getContracts();
        if(contractDTOList.isEmpty()){
            return Collections.emptyList();
        }
        return contractDTOList;
    }

    @Override
    public List<ContractDTO> searchContract(ContractSearchDTO contractSearchDTO) {
        String keyword = contractSearchDTO.getKeyword();
        String searchType = contractSearchDTO.getSearchType();
        log.info("keyword:{} searchType:{}", keyword, searchType);
        // 1. 둘 다 비었을 경우 → 전체 조회
        if ((keyword == null || keyword.isBlank()) && (searchType == null || searchType.isBlank())) {
            return contractMapper.getContracts();
        }

        // 2. keyword는 있는데 searchType이 없으면 → 의미 없으므로 빈 리스트
        if ((searchType == null || searchType.isBlank()) && keyword != null && !keyword.isBlank()) {
            return Collections.emptyList();
        }

        // 3. keyword 없는데 searchType만 있으면 → 의미 없으므로 빈 리스트
        if ((keyword == null || keyword.isBlank())) {
            return Collections.emptyList();
        }

        // 4. 정상 검색
        contractSearchDTO.setSearchType(Objects.requireNonNull(searchType).trim().toLowerCase(Locale.ROOT));
        contractSearchDTO.setKeyword(keyword.trim().toLowerCase(Locale.ROOT));
        List<ContractDTO> searchResult = contractMapper.searchContract(contractSearchDTO);
        List<ContractDTO> result = new ArrayList<>();

        if(!searchResult.isEmpty() && contractSearchDTO.getSearchType().equals("influencername")){
            String findFirst = searchResult.get(0).getInfluencerName();
            result = searchResult.stream()
                    .filter(dto -> dto.getInfluencerName().equals(findFirst))
                    .toList();
        }

        if(!searchResult.isEmpty() && contractSearchDTO.getSearchType().equals("advertisername")){
            String findFirst = searchResult.get(0).getAdvertiserName();
            result = searchResult.stream()
                    .filter(dto -> dto.getAdvertiserName().equals(findFirst))
                    .toList();
        }

        if(!searchResult.isEmpty() && contractSearchDTO.getSearchType().equals("contractid")){
            String findFirst = searchResult.get(0).getContractId();
            result = searchResult.stream()
                    .filter(dto -> dto.getContractId().equals(findFirst))
                    .toList();
        }
        log.info("서비스 최종 검색어 searchType : {} 검색어 : {}",contractSearchDTO.getSearchType(),contractSearchDTO.getKeyword());
        log.info("result:{}", result);

        return result.isEmpty() ? Collections.emptyList() : result;
    }
}
