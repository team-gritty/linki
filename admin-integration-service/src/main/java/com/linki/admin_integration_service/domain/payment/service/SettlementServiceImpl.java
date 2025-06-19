package com.linki.admin_integration_service.domain.payment.service;

import com.linki.admin_integration_service.domain.payment.dto.SettlementDTO;
import com.linki.admin_integration_service.domain.payment.dto.SettlementRequestDTO;
import com.linki.admin_integration_service.domain.payment.dto.SettlementSearchDTO;
import com.linki.admin_integration_service.domain.payment.repository.myBatis.SettlementMapper;
import com.linki.admin_integration_service.util.excel.ExcelUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;

import java.util.*;

// TODO : 정산상태(isSettle) 추가 해야함
@Service
@RequiredArgsConstructor
@Log4j2
public class SettlementServiceImpl implements SettlementService {


    private final SettlementMapper settlementMapper;
    private final ExcelUtil excelUtil;


    @Override
    public List<SettlementDTO> getAllSettlements() {
        List<SettlementDTO> settlementDTOList = settlementMapper.getAllSettlements();
        if (settlementDTOList.isEmpty()) {
            return Collections.emptyList();
        }
        List<SettlementDTO> result = settlementDTOList.stream()
                .map(dto -> dto.toBuilder().isSettled("PENDING").build())
                .toList();

        return result;
    }

    @Override
    public List<SettlementDTO> searchSettlement(SettlementSearchDTO settlementSearchDTO) {
        String keyword = settlementSearchDTO.getKeyword();
        String searchType = settlementSearchDTO.getSearchType();
        log.info("keyword:{} searchType:{}", keyword, searchType);
        // 1. 둘 다 비었을 경우 → 전체 조회
        if ((keyword == null || keyword.isBlank()) && (searchType == null || searchType.isBlank())) {
            return settlementMapper.getAllSettlements();
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
        settlementSearchDTO.setSearchType(Objects.requireNonNull(searchType).trim().toLowerCase(Locale.ROOT));
        settlementSearchDTO.setKeyword(keyword.trim().toLowerCase(Locale.ROOT));
        List<SettlementDTO> searchResult = settlementMapper.searchSettlement(settlementSearchDTO);
        List<SettlementDTO> streamResult = new ArrayList<>();

        if (!searchResult.isEmpty()) {
            String findSearchType = settlementSearchDTO.getSearchType();
            String findFirstValue = switch (findSearchType) {
                case "advertisername" -> searchResult.get(0).getAdvertiserName();
                case "influencername" -> searchResult.get(0).getInfluencerName();
                case "contractid" -> searchResult.get(0).getContractId();
                default -> null;
            };
            if (findFirstValue != null) {
                streamResult = searchResult.stream()
                        .filter(dto -> {
                            return switch (findSearchType) {
                                case "advertisername" -> dto.getAdvertiserName().equals(findFirstValue);
                                case "influencername" -> dto.getInfluencerName().equals(findFirstValue);
                                case "contractid" -> dto.getContractId().equals(findFirstValue);
                                default -> false;
                            };
                        })
                        .toList();
            }
        }

        log.info("서비스 최종 검색어 searchType : {} 검색어 : {}",settlementSearchDTO.getSearchType(),settlementSearchDTO.getKeyword());
        log.info("result:{}", streamResult);



        return streamResult.isEmpty() ? Collections.emptyList() : streamResult;
    }


    // TODO : 정산 처리 메소드(구현 필요)
    @Override
    public Boolean approveSettlement(SettlementRequestDTO settlementRequestDTO) {
        return true;
    }

    @Override
    public String exportExcel() {
        List<SettlementDTO> result = settlementMapper.getAllSettlements();
        return excelUtil.exportExcel(result,SettlementDTO.class,"SettlementList",null);
    }
}
